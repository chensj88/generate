package cn.com.ss.customer.generate.util;

import cn.com.ss.customer.generate.Constant;
import cn.com.ss.customer.generate.domain.ActualTableName;
import cn.com.ss.customer.generate.domain.TableColumnInfo;
import cn.com.ss.customer.generate.domain.TableInfo;
import org.mariadb.jdbc.UrlParser;

import java.sql.*;
import java.util.*;

import static cn.com.ss.customer.generate.util.ConnectionUtil.closeResultSet;

/**
 * @author chenshijie
 * @title
 * @email chensj@winning.com.cm
 * @package cn.com.ss.customer.generate.domain
 * @date 2018-05-22 22:11
 */
public class DatabaseUtils {

    /**
     * 获取数据库表信息
     *
     * @param tableName  表名
     * @param connection 数据库连接
     * @return info
     * @throws SQLException
     */
    public static TableInfo getTableInfo(String tableName, Connection connection) throws SQLException {
        TableInfo info = new TableInfo();
        info.setTableName(tableName);
        info.setDomainName(DatabaseNameUtils.convertFromDBToJava(tableName, 0));
        info.setAlias(DatabaseNameUtils.convertFromDBToJava(tableName, 1));
        info.setDomainPackage(Constant.DOMAIN_PACKAGE);
        info.setDomainPath(PropertiesLoader.getProperty("config.path"));
        info.setDbType(JdbcUtils.getDbType(PropertiesLoader.getProperty("db.url"), null));
        DatabaseMetaData metaData = connection.getMetaData();
        getPrimaryKey(metaData, info);
        getColumns(metaData, info);
        // 主键判断
        for (TableColumnInfo columnInfo : info.getTableColumnInfos()) {
            for (String key : info.getPrimaryKeys()) {
                if (columnInfo.getActualColumnName().equals(key)) {
                    columnInfo.setSequenceColumn(true);
                }
            }
        }
        return info;
    }

    /**
     * 获取主键信息
     *
     * @param metaData  数据库元数据
     * @param tableInfo 表信息domain
     */
    public static void getPrimaryKey(DatabaseMetaData metaData, TableInfo tableInfo) {
        ResultSet rs = null;
        ActualTableName atn = null;
        try {
            rs = metaData.getPrimaryKeys(tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getTableName());
            Map<Short, String> keyColumns = new TreeMap<Short, String>();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                short keySeq = rs.getShort("KEY_SEQ");
                keyColumns.put(keySeq, columnName);
                if (atn == null) {
                    atn = new ActualTableName(
                            rs.getString("TABLE_CAT"),
                            rs.getString("TABLE_SCHEM"),
                            rs.getString("TABLE_NAME"));
                }
                if (UrlParser.acceptsUrl(metaData.getURL())) {
                    UrlParser parse = UrlParser.parse(metaData.getURL());
                    atn.setSchema(parse.getDatabase());
                }
            }

            for (String columnName : keyColumns.values()) {
                tableInfo.addPrimaryKeyColumn(columnName);
            }

        } catch (SQLException e) {
            closeResultSet(rs);
            e.printStackTrace();
            return;
        }
        tableInfo.setActualTableName(atn);
        tableInfo.setRemark(CommentUtils.getComment(tableInfo, null, 0));
    }

    /**
     * 获取列信息
     *
     * @param databaseMetaData
     * @param tableInfo
     * @throws SQLException
     */
    public static void getColumns(DatabaseMetaData databaseMetaData, TableInfo tableInfo) throws SQLException {
        Map<ActualTableName, List<TableColumnInfo>> answer = new HashMap<>();
        ResultSet rs = databaseMetaData.getColumns(tableInfo.getCatalog(), tableInfo.getSchema(),
                tableInfo.getTableName(), "%");
        ActualTableName atn = tableInfo.getActualTableName();
        boolean supportsIsAutoIncrement = false;
        boolean supportsIsGeneratedColumn = false;
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        for (int i = 1; i <= colCount; i++) {
            if ("IS_AUTOINCREMENT".equals(rsmd.getColumnName(i))) {
                supportsIsAutoIncrement = true;
            }
            if ("IS_GENERATEDCOLUMN".equals(rsmd.getColumnName(i))) {
                supportsIsGeneratedColumn = true;
            }
        }
        while (rs.next()) {
            TableColumnInfo columnInfo = new TableColumnInfo();
            columnInfo.setJdbcType(rs.getInt("DATA_TYPE"));
            columnInfo.setJdbcTypeName(JdbcUtils.getTypeName(rs.getInt("DATA_TYPE")));
            columnInfo.setLength(rs.getInt("COLUMN_SIZE"));
            columnInfo.setActualColumnName(rs.getString("COLUMN_NAME"));
            columnInfo.setDomainColumnName(DatabaseNameUtils.convertFromDBToJava(rs.getString("COLUMN_NAME"), 1));
            columnInfo.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
            columnInfo.setScale(rs.getInt("DECIMAL_DIGITS"));
            columnInfo.setRemarks(CommentUtils.getComment(tableInfo, rs.getString("COLUMN_NAME"), 1));
            columnInfo.setDefaultValue(rs.getString("COLUMN_DEF"));
            if (supportsIsAutoIncrement) {
                columnInfo.setAutoIncrement(
                        "YES".equals(rs.getString("IS_AUTOINCREMENT")));  //$NON-NLS-2$
            }

            if (supportsIsGeneratedColumn) {
                columnInfo.setGeneratedColumn(
                        "YES".equals(rs.getString("IS_GENERATEDCOLUMN")));  //$NON-NLS-2$
            }

            columnInfo.setFullyQualifiedJavaType(JavaTypeResolverUtils.generateJavaType(columnInfo));
            List<TableColumnInfo> columns = answer.get(atn);
            if (columns == null) {
                columns = new ArrayList<TableColumnInfo>();
                answer.put(atn, columns);
            }
            columns.add(columnInfo);
        }
        closeResultSet(rs);
        tableInfo.setTableColumnInfos(answer.get(tableInfo.getActualTableName()));
    }

}
