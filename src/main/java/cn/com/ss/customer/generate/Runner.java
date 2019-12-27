package cn.com.ss.customer.generate;

import cn.com.ss.customer.generate.util.ConnectionUtil;
import cn.com.ss.customer.generate.util.GenerateFileUtils;
import cn.com.ss.customer.generate.util.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.sql.Connection;
import java.util.List;

/**
 * @author chenshijie
 * @title
 * @email chensj@winning.com.cm
 * @package cn.com.ss.customer.generate.file
 * @date 2018-05-29 20:43
 */
@Slf4j
public class Runner {

    public static void main(String[] args){
        log.info("---开始准备导出数据---");
        Connection connection = null;
        try {
            log.info("连接数据库");
            connection = ConnectionUtil.getConnection();
            log.info("数据库连接成功");
            List<String> tableList = PropertiesLoader.getTableNameList();
            if(CollectionUtils.isEmpty(tableList)){
                log.error("config.properties文件中未配置config.table");
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
            log.error("数据库连接失败，错误原因[{}]", e.getMessage());
            return;
        }finally {
            ConnectionUtil.closeConnection(connection);
        }
    }
}
