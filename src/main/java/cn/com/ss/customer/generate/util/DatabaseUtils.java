package cn.com.ss.customer.generate.util;

import cn.com.ss.customer.generate.Constant;
import cn.com.ss.customer.generate.domain.ActualTableName;
import cn.com.ss.customer.generate.domain.TableColumnInfo;
import cn.com.ss.customer.generate.domain.TableInfo;
import static cn.com.ss.customer.generate.util.ConnectionUtil.closeResultSet;

import java.sql.*;
import java.util.*;

/**
 * @author chenshijie
 * @title
 * @email chensj@winning.com.cm
 * @package cn.com.ss.customer.generate.domain
 * @date 2018-05-22 22:11
 */
public class DatabaseUtils {


    public static TableInfo getTableInfo(String tableName, Connection connection) throws SQLException {
        TableInfo info = new TableInfo();
        info.setTableName(tableName);
        info.setDomainName(DatabaseNameUtils.convertFromDBToJava(tableName,0));
        info.setAlias(DatabaseNameUtils.convertFromDBToJava(tableName,1));
        info.setDomainPackage(Constant.DAO_PACKAGE);
        DatabaseMetaData metaData =  connection.getMetaData();
        getPrimaryKey(metaData,info);
        getColumns(metaData,info);
        return info;
    }

    /**
     * 获取主键信息
     * @param metaData
     * @param tableInfo
     */
    public static void getPrimaryKey(DatabaseMetaData metaData,  TableInfo tableInfo)  {
        ResultSet rs = null;
        try {
            rs = metaData.getPrimaryKeys("","",tableInfo.getTableName());
        } catch (SQLException e) {
            closeResultSet(rs);
            e.printStackTrace();
            return;
        }

        try {
            Map<Short, String> keyColumns = new TreeMap<Short, String>();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME"); //$NON-NLS-1$
                short keySeq = rs.getShort("KEY_SEQ"); //$NON-NLS-1$
                keyColumns.put(keySeq, columnName);
            }

            for (String columnName : keyColumns.values()) {
                tableInfo.addPrimaryKeyColumn(columnName);
            }
        } catch (SQLException e) {
        } finally {
            closeResultSet(rs);
        }
    }

    public static void  getColumns(DatabaseMetaData databaseMetaData, TableInfo tableInfo) throws SQLException {
        Map<ActualTableName,List<TableColumnInfo>> answer = new HashMap<>();
        ResultSet rs = databaseMetaData.getColumns("", "", tableInfo.getTableName(), "%");
        boolean supportsIsAutoIncrement = false;
        boolean supportsIsGeneratedColumn = false;
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        for (int i = 1; i <= colCount; i++) {
            if ("IS_AUTOINCREMENT".equals(rsmd.getColumnName(i))) { //$NON-NLS-1$
                supportsIsAutoIncrement = true;
            }
            if ("IS_GENERATEDCOLUMN".equals(rsmd.getColumnName(i))) { //$NON-NLS-1$
                supportsIsGeneratedColumn = true;
            }
        }
        while (rs.next()) {
            TableColumnInfo columnInfo = new TableColumnInfo();
            columnInfo.setJdbcType(rs.getInt("DATA_TYPE")); //$NON-NLS-1$
            columnInfo.setLength(rs.getInt("COLUMN_SIZE")); //$NON-NLS-1$
            columnInfo.setActualColumnName(rs.getString("COLUMN_NAME")); //$NON-NLS-1$
            columnInfo.setDomainColumnName(DatabaseNameUtils.convertFromDBToJava(rs.getString("COLUMN_NAME"),1)); //$NON-NLS-1$
            columnInfo.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable); //$NON-NLS-1$
            columnInfo.setScale(rs.getInt("DECIMAL_DIGITS")); //$NON-NLS-1$
            columnInfo.setRemarks(rs.getString("REMARKS")); //$NON-NLS-1$
            columnInfo.setDefaultValue(rs.getString("COLUMN_DEF")); //$NON-NLS-1$
            if (supportsIsAutoIncrement) {
                columnInfo.setAutoIncrement(
                        "YES".equals(rs.getString("IS_AUTOINCREMENT"))); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (supportsIsGeneratedColumn) {
                columnInfo.setGeneratedColumn(
                        "YES".equals(rs.getString("IS_GENERATEDCOLUMN"))); //$NON-NLS-1$ //$NON-NLS-2$
            }
            ActualTableName atn = new ActualTableName(
                    rs.getString("TABLE_CAT"),
                    rs.getString("TABLE_SCHEM"),
                    rs.getString("TABLE_NAME"));
            tableInfo.setActualTableName(atn);
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
