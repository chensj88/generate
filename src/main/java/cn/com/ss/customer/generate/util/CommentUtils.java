package cn.com.ss.customer.generate.util;

import cn.com.ss.customer.generate.domain.TableInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 注释信息获取
 *
 * @author chenshijie
 * @date 2018-05-29 13:48
 */
public class CommentUtils {

    /**
     * 获取字段说明
     *
     * @param info
     * @param colName
     * @param type
     * @return
     */
    public static String getComment(TableInfo info, String colName, int type) {
        String url = PropertiesLoader.getProperty("db.url");
        String dbType = JdbcUtils.getDbType(url, null);
        if (JdbcConstants.SQL_SERVER.equals(dbType)) {
            if (type == 0) {
                return tableCommentForSqlServer(info);
            } else {
                return tableColumnCommentForSqlServer(info, colName);
            }
        } else if (JdbcConstants.MYSQL.equals(dbType)) {
            if (type == 0) {
                return tableCommentForMySql(info) != null ? tableCommentForMySql(info) : "";
            } else {
                return tableColumnCommentForrMySql(info, colName);
            }
        } else if (JdbcConstants.ORACLE.equals(dbType)) {
            if (type == 0) {
                return tableCommentForOracle(info);
            } else {
                return tableColumnCommentForrOracle(info, colName);
            }
        } else {
            if (type == 0) {
                return info.getTableName();
            } else {
                return colName;
            }
        }
    }

    //=====================Db2===============================================================//
    //TODO DB2 待扩展
    private static String tableCommentForDb2(TableInfo info) {
//        String sql = "select  table_name,comments from user_tab_comments " +
//                "where table_name='"+info.getTableName()+"'";
        return info.getTableName();
    }

    private static String tableColumnCommentForrDb2(TableInfo info, String colName) {
        /*String sql = "select column_name,comments from user_col_comments " +
                "where table_name='"+info.getTableName()+"' \" and  column_name= '"+colName+"'" ;
        return  queryDatabaseBySql(sql);*/
        return colName;
    }

    //=====================Oracle===============================================================//
    private static String tableCommentForOracle(TableInfo info) {
        String sql = "select  table_name,comments from user_tab_comments " +
                "where table_name='" + info.getTableName() + "'";
        return queryDatabaseBySql(sql);
    }

    private static String tableColumnCommentForrOracle(TableInfo info, String colName) {
        String sql = "select column_name,comments from user_col_comments " +
                "where table_name='" + info.getTableName() + "'  and  column_name= '" + colName + "'";
        return queryDatabaseBySql(sql);
    }

    //======================MySql==========================================================//
    private static String tableCommentForMySql(TableInfo info) {
        StringBuilder sql = new StringBuilder();
        sql.append(
                "select TABLE_NAME,TABLE_COMMENT " +
                        "from INFORMATION_SCHEMA.TABLES where table_name='");
        sql.append(info.getTableName());
        sql.append("' ");
        if (info.getActualTableName() != null && info.getActualTableName().getSchema() != null) {
            sql.append("and table_schema='");
            sql.append(info.getActualTableName().getSchema());
            sql.append("'");
        }
        return queryDatabaseBySql(sql.toString());
    }

    private static String tableColumnCommentForrMySql(TableInfo info, String colName) {
        String sql = "select COLUMN_NAME,column_comment from INFORMATION_SCHEMA.Columns " +
                "where table_name='" + info.getTableName() + "'  and  COLUMN_NAME= '" + colName + "'" +
                "and table_schema='" + info.getActualTableName().getSchema() + "'";
        return queryDatabaseBySql(sql);
    }

    //=====================SqlServer==========================================================//
    private static String tableCommentForSqlServer(TableInfo info) {
        String sql = "SELECT \n" +
                " convert(varchar(100),a.name) AS TABLE_NAME, convert(varchar(100),b.value) TABLE_COMMENT\n" +
                "FROM sysobjects a LEFT JOIN sys.extended_properties b ON (a.id = b.major_id) AND (b.minor_id = 0)\n" +
                "WHERE a.xtype = 'U' and  a.id =  object_id('" + info.getTableName() + "')";
        return queryDatabaseBySql(sql);
    }

    private static String tableColumnCommentForSqlServer(TableInfo info, String colName) {
        String sql = "SELECT\n" +
                " convert(varchar(100),a.name) AS col_name , convert(varchar(100),g.value) as col_comment\n" +
                " FROM syscolumns a\n" +
                "INNER JOIN sysobjects d ON (a.id = d.id) AND (d.xtype = 'U') AND (d.name <> 'dtproperties') \n" +
                "LEFT JOIN sys.extended_properties g ON (a.id = g.major_id)  AND (a.colid = g.minor_id)\n" +
                "WHERE d.xtype = 'U' and  d.id =  object_id('" + info.getTableName() + "') AND a.name = '" + colName + "' ;";
        return queryDatabaseBySql(sql);
    }


    private static String queryDatabaseBySql(String sql) {
        String result = "";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = ConnectionUtil.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.closeAll(connection, ps, rs);
        }
        return result == null ? "" : result;
    }
}
