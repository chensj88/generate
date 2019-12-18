package cn.com.ss.customer.generate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * properties文件加载
 *
 * @author chenshijie
 * @date 2018-05-21 21:50
 */
public class PropertiesLoader {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    private static Properties properties = new Properties();

    static {
        InputStream in2 = PropertiesLoader.class.getClassLoader().getResourceAsStream("config.properties");
        if (in2 == null) {
            logger.info("===config.properties not found===");
        } else {
            if (!(in2 instanceof BufferedInputStream))
                in2 = new BufferedInputStream(in2);
            try {
                properties.load(in2);
                in2.close();
                logger.info("===config.properties loaded===");
            } catch (Exception e) {
                logger.error("Error while processing config.properties");
                throw new RuntimeException("Error while processing config.properties", e);
            }
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public static List<String> getTableNameList() {
        List<String> tableNameList = new ArrayList<>();
        String tableNameString = properties.getProperty("config.table");
        for (int i = 0; i < tableNameString.split(",").length; i++) {
            tableNameList.add(tableNameString.split(",")[i].trim());
        }
        return tableNameList;
    }
}
