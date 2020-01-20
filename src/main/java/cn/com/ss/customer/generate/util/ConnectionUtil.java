package cn.com.ss.customer.generate.util;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.*;
import java.util.Properties;

/**
 * @author chenshijie
 * @title 获取数据库连接信息
 * @email chensj@winning.com.cm
 * @package cn.com.ss.generate.util
 * @date 2018-05-21 21:57
 */
public class ConnectionUtil {

    /**
     * @return Connection
     */
    public static synchronized Connection getConnection() throws Exception {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(PropertiesLoader.getProperty("db.url"));
        datasource.setUsername(PropertiesLoader.getProperty("db.username"));
        datasource.setPassword(PropertiesLoader.getProperty("db.password"));
        datasource.setDriverClassName(PropertiesLoader.getProperty("db.driverClassName"));
        datasource.setInitialSize(5);
        datasource.setMinIdle(5);
        datasource.setMaxActive(20);
        datasource.setMaxWait(60000);
        datasource.setTimeBetweenEvictionRunsMillis(60000);
        datasource.setMinEvictableIdleTimeMillis(300000);
        String dbType = JdbcUtils.getDbType(PropertiesLoader.getProperty("db.url"), null);
        if(JdbcUtils.SQL_SERVER.equals(dbType)){
            datasource.setValidationQuery("SELECT GETDATE()");
        }else if(JdbcUtils.MYSQL.equals(dbType) || JdbcUtils.MARIADB.equals(dbType)){
            datasource.setValidationQuery("SELECT 1");
        }else if(JdbcUtils.ORACLE.equals(dbType)){
            datasource.setValidationQuery("SELECT dual");
        }
        datasource.setTestWhileIdle(true);
        datasource.setTestOnBorrow(false);
        datasource.setTestOnReturn(false);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
        Properties prop = new Properties();
        prop.setProperty("druid.stat.mergeSql","true");
        prop.setProperty("druid.stat.slowSqlMillis","5000");
        datasource.setConnectProperties(prop);
        datasource.setUseGlobalDataSourceStat(true);

//        // 读出配置信息
//        String driverClassName = PropertiesLoader.getProperty("db.driverClassName");
//        String url = PropertiesLoader.getProperty("db.url");
//        String username = PropertiesLoader.getProperty("db.username");
//        String password = PropertiesLoader.getProperty("db.password");
//
//        Connection conn = null;
//        try {
//            // 加载数据库驱动程序
//            Class.forName(driverClassName);
//            conn = DriverManager.getConnection(url, username, password);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new Exception(e.getMessage());
//        }
        Connection conn = datasource.getConnection();

        return conn;
    }

    public static void closeAll(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
                pstmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet( ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeConnection( Connection rs) {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private ConnectionUtil() {
    }

     public static void main(String[] args) throws Exception {
         Connection conn = null;
         try {
             // 加载数据库驱动程序
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             conn = DriverManager.getConnection("jdbc:sqlserver://172.16.0.200\\ssgj;database=CISDB", "sa", "zyc@8468");
         } catch (Exception e) {
             e.printStackTrace();
             throw new Exception(e.getMessage());
         }finally {
             assert conn != null;
             conn.close();
         }
      }

}
