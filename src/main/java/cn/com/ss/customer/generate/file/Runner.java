package cn.com.ss.customer.generate.file;

import cn.com.ss.customer.generate.test.MainTest;
import cn.com.ss.customer.generate.util.ConnectionUtil;
import cn.com.ss.customer.generate.util.GenerateFileUtils;
import cn.com.ss.customer.generate.util.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * @author chenshijie
 * @title
 * @email chensj@winning.com.cm
 * @package cn.com.ss.customer.generate.file
 * @date 2018-05-29 20:43
 */
public class Runner {
    private static Logger logger = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args){
        logger.info("开始准备导出数据");
        Connection connection = null;
        try {
            logger.info("连接数据库");
            connection = ConnectionUtil.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            logger.info("数据库连接成功");
            List<String> tableList = PropertiesLoader.getTableNameList();
            if(tableList == null || tableList.size() < 0){
                logger.error("config.properties文件中未配置config.table");
                throw  new  Exception("config.properties文件中未配置config.table");
            }else{
                for (String s : tableList) {
                    GenerateFileUtils.generateFile(s,connection);
                }
                GenerateFileUtils.generateFacdeFile(tableList);
                GenerateFileUtils.generateFacdeImplFile(tableList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("数据库连接失败，错误原因[{}]", e.getMessage());
            return;
        }finally {
            ConnectionUtil.closeConnection(connection);
        }
    }
}
