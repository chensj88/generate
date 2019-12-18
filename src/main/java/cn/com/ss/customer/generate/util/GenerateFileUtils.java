package cn.com.ss.customer.generate.util;

import cn.com.ss.customer.generate.Constant;
import cn.com.ss.customer.generate.code.java.JavaFileGenerator;
import cn.com.ss.customer.generate.code.java.JavaJpaFileGenerator;
import cn.com.ss.customer.generate.domain.TableInfo;
import cn.com.ss.customer.generate.test.MainTest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenshijie
 * @title
 * @email chensj@winning.com.cm
 * @package cn.com.ss.customer.generate.util
 * @date 2018-05-29 20:35
 */
public class GenerateFileUtils {

    private static boolean isConfig = false;

    private static Logger logger = LoggerFactory.getLogger(GenerateFileUtils.class);

    public static void generateFile(String tableName, Connection connection) {
        logger.info("开始解析表:{}",tableName);
        TableInfo info = null;
        try {
            info = DatabaseUtils.getTableInfo(tableName, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 判断JPA
        boolean isJpa = Boolean.parseBoolean(PropertiesLoader.getProperty("config.isJpa"));
        logger.info("是否使用JPA:{}",isJpa);
        if(isJpa){
            logger.info("生成Row 分页文件信息");
            generateJpaRowFile();
            logger.info("生成JPA文件开始,Table Name:{}",info.getTableName());
            generateJpaFile(info);
            logger.info("生成JPA文件结束");
        }else{
            logger.info("生成MyBatis文件开始,Table Name:{}",info.getTableName());
            if(!isConfig){
                generateOnceFile();
                isConfig = true;
            }
            generateFile(info);
            logger.info("生成MyBatis文件结束");
        }
    }
    //==============================================JPA 配置文件================================================================
    /**
     * jpa 文件生成
     * @param info
     */
    private static void generateJpaFile(TableInfo info) {
        JavaJpaFileGenerator generator = new JavaJpaFileGenerator();
        generator.setTableInfo(info);
        Map<String, Object> domainData = generator.generateDomainData();
        Map<String, Object> repositoryData = generator.generateRepositoryData();
        Map<String, Object> serviceData = generator.generateJavaServiceData();
        Map<String, Object> serviceImplData = generator.generateJavaServiceImplData();
        boolean isJpaUseLombok = Boolean.valueOf(PropertiesLoader.getProperty("config.isJpaUseLombok"));
        logger.info("JPA中是否使用Lombok:{}",isJpaUseLombok);
        if(isJpaUseLombok){
            logger.info("生成JPA-Lombok Domain,Table Name:{}",info.getTableName());
            generateJpaLombokDomainFile(domainData,info);
        }else{
            logger.info("生成JPA Domain,Table Name:{}",info.getTableName());
            generateJpaDomainFile(domainData,info);
        }
        logger.info("生成JPA Repository,Table Name:{}",info.getTableName());
        generateJpaRepositoryFile(repositoryData,info);
        logger.info("生成JPA Service,Table Name:{}",info.getTableName());
        generateJpaServiceFile(serviceData,info);
        logger.info("生成JPA ServiceImpl,Table Name:{}",info.getTableName());
        generateJpaServiceImplFile(serviceImplData,info);
    }

    /**
     * Row文件 jpa
     */
    private static void generateJpaRowFile() {
        Map<String, Object> data = new HashMap<>();
        data.put("domainPackage", Constant.DOMAIN_PACKAGE+".support");
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("row-jpa.ftl");
            String path = Constant.PATH;
            String pack = Constant.DOMAIN_PACKAGE+".support";
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + "Row.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 生成JPA Repository
     * @param data map data
     * @param info table
     */
    private static void generateJpaRepositoryFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("repository.ftl");
            String path = info.getDomainPath();
            String pack = Constant.DAO_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + "Repository.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成JPA Domain
     * @param data map data
     * @param info table
     */
    private static void generateJpaDomainFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("domain-jpa.ftl");
            String path = info.getDomainPath();
            String pack = info.getDomainPackage();
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + ".java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成JPA-Lombok Domain
     * @param data map data
     * @param info table
     */
    private static void generateJpaLombokDomainFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("domain-jpa-lombok.ftl");
            String path = info.getDomainPath();
            String pack = info.getDomainPackage();
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + ".java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 生成Service JPA层文件
     * @param data
     * @param info
     */
    private static void generateJpaServiceFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("services-jpa.ftl");
            String path = info.getDomainPath();
            String pack = Constant.SERVICE_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + "Service.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成Service JPA实现层文件
     * @param data
     * @param info
     */
    private static void generateJpaServiceImplFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("servicesImpl-jpa.ftl");
            String path = info.getDomainPath();
            String pack = Constant.SERVICEIMPL_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + "ServiceImpl.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //==============================================MyBatis 配置文件================================================================
    /**
     * 文件生成
     * @param info
     */
    private static void generateFile(TableInfo info ){
        JavaFileGenerator generator = new JavaFileGenerator();
        generator.setTableInfo(info);
        Map<String, Object> domainData = generator.generateDomainData();
        Map<String, Object> clientData = generator.generateJavaClientData();
        Map<String, Object> sqlMapData = generator.generateSqlMapData();
        Map<String, Object> serviceData = generator.generateJavaServiceData();
        Map<String, Object>  serviceImplData = generator.generateJavaServiceImplData();
        Map<String, Object>  controllerData = generator.generateJavaControllerData();
        // 判断是否使用Lombok
        logger.info("MyBatis中是否使用Lombok:{}",Boolean.valueOf(PropertiesLoader.getProperty("config.isUseLombok")));
        if(Boolean.valueOf(PropertiesLoader.getProperty("config.isUseLombok"))){
            logger.info("生成MyBatis-Lombok Domain,Table Name:{}",info.getTableName());
            generateDomainFileForLombok(domainData, info);
        }else{
            logger.info("生成MyBatis Domain,Table Name:{}",info.getTableName());
            generateDomainFile(domainData, info);
        }
        logger.info("MyBatis中是否使用Redis:{}",Boolean.valueOf(PropertiesLoader.getProperty("config.isUseRedis")));
        if(Boolean.valueOf(PropertiesLoader.getProperty("config.isUseRedis"))){
            logger.info("生成MyBatis-Redis Client,Table Name:{}",info.getTableName());
            generateClientFileForRedis(clientData, info);
        }else{
            logger.info("生成MyBatis Client,Table Name:{}",info.getTableName());
            generateClientFile(clientData, info);
        }
        logger.info("生成MyBatis SqlMap,Table Name:{}",info.getTableName());
        generateSqlMapFile(sqlMapData, info);
        logger.info("生成MyBatis Service,Table Name:{}",info.getTableName());
        generateServiceFile(serviceData, info);
        logger.info("生成MyBatis ServiceImpl,Table Name:{}",info.getTableName());
        generateServiceImplFile(serviceImplData,info);
        logger.info("生成MyBatis Controller,Table Name:{}",info.getTableName());
        generateControllerFile(controllerData,info);

    }
    /**
     * 生成一次性文件
     * mybatis-config、BaseDomain和Row文件
     */
    private static void generateOnceFile(){
        generateMyBatisConfigFile();
        generateBaseDomainFile();
        generateRowFile();
        generateBaseControllerFile();
        // 判断是否使用Redis
        if(Boolean.valueOf(PropertiesLoader.getProperty("config.isUseRedis"))){
            generateRedisConfiFile();
        }
    }

    /**
     * 生成BaseController
     */
    private static void generateBaseControllerFile() {
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("packageName",Constant.CONTROLLER_PACKAGE+".base;");
        dataMap.put("modelPackage",Constant.DOMAIN_PACKAGE+".*;");
        dataMap.put("facadeName",Constant.SERVICE_PACKAGE+".Facade;");
        dataMap.put("facade","Facade");
        dataMap.put("author",PropertiesLoader.getProperty("config.author"));
        dataMap.put("title","BaseController");
        dataMap.put("email", PropertiesLoader.getProperty("config.email"));
        dataMap.put("date",DateUtils.getCurrentDate());
        dataMap.put("className","BaseController");
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("baseController.ftl");
            String path = Constant.PATH;
            String pack = Constant.CONTROLLER_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + "BaseController.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(dataMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成Redis配置文件
     */
    private static void generateRedisConfiFile() {
        Map<String, Object> data = new HashMap<>();
        data.put("packageName", Constant.CONFIG_PACKAGE);
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("redisConfig.ftl");
            String path = Constant.PATH;
            String pack = Constant.CONFIG_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + "RedisConfig.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 生成BaseDomain文件
     */
    private static void generateBaseDomainFile() {
        Map<String, Object> data = new HashMap<>();
        data.put("domainPackage", Constant.DOMAIN_PACKAGE);
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("baseDomain.ftl");
            String path = Constant.PATH;
            String pack = Constant.DOMAIN_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + "BaseDomain.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Row文件
     */
    private static void generateRowFile() {
        Map<String, Object> data = new HashMap<>();
        data.put("domainPackage", Constant.DOMAIN_PACKAGE+".support");
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("row.ftl");
            String path = Constant.PATH;
            String pack = Constant.DOMAIN_PACKAGE+".support";
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + "Row.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * MyBatis-config文件
     */
    private static void generateMyBatisConfigFile() {
        Map<String, Object> data = new HashMap<>();
        data.put("domainPackage", Constant.DOMAIN_PACKAGE);
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("mybatis.ftl");
            String path = Constant.PATH;
            String pack = Constant.MAPPER_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + "mybatis-config.xml");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成实体类文件 lombok
     * @param data
     * @param info
     */
    private static void generateDomainFileForLombok(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("domain-lombok.ftl");
            String path = info.getDomainPath();
            String pack = info.getDomainPackage();
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + ".java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成实体类文件 Lombok
     */
    private static void generateDomainFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("domain.ftl");
            String path = info.getDomainPath();
            String pack = info.getDomainPackage();
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + ".java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 生成MyBatis Dao文件
     * @param data
     * @param info
     */
    private static void generateClientFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("client.ftl");
            String path = info.getDomainPath();
            String pack = Constant.DAO_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + "Dao.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 生成MyBatis Dao文件 Redis
     * @param data
     * @param info
     */
    private static void generateClientFileForRedis(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("client-redis.ftl");
            String path = info.getDomainPath();
            String pack = Constant.DAO_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + "Dao.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成MyBatis SqlMap文件
     * @param data
     * @param info
     */
    private static void generateSqlMapFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("sqlmap.ftl");
            String path = info.getDomainPath();
            String pack = Constant.MAPPER_PACKAGE + ".mapper";
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getTableName() + "_SqlMap.xml");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成Service层文件
     * @param data
     * @param info
     */
    private static void generateServiceFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("services.ftl");
            String path = info.getDomainPath();
            String pack = Constant.SERVICE_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + "Service.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成Controller文件
     * @param data
     * @param info
     */
    private static void generateControllerFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("controller.ftl");
            String path = info.getDomainPath();
            String pack = Constant.CONTROLLER_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + "Controller.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 生成Service 实现层文件
     * @param data
     * @param info
     */
    private static void generateServiceImplFile(Map<String, Object> data, TableInfo info) {
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("servicesImpl.ftl");
            String path = info.getDomainPath();
            String pack = Constant.SERVICEIMPL_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + info.getDomainName() + "ServiceImpl.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 生成Facade接口
     * @param tableList
     */
    public static void generateFacdeFile(List<String> tableList) {
        Map<String, Object> data = generateFacdeFileData(tableList);
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("facade.ftl");
            String path = PropertiesLoader.getProperty("config.path");
            String pack = Constant.SERVICE_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + "Facade.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成Facade接口
     * @param tableList
     */
    public static void generateFacdeImplFile(List<String> tableList) {
        Map<String, Object> data = generateFacdeFileImplData(tableList);
        Configuration config = new Configuration();
        Writer writer = null;
        try {
            File templateFile = new File(GenerateFileUtils.class.getClassLoader().getResource("template").getPath());
            config.setDirectoryForTemplateLoading(templateFile);
            Template template = config.getTemplate("facadeImpl.ftl");
            String path = PropertiesLoader.getProperty("config.path");
            String pack = Constant.SERVICEIMPL_PACKAGE;
            String targePath = FileUtils.createABSPath(path, pack);
            File file = new File(targePath + File.separator + "FacadeImpl.java");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            template.process(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成facade实现接口数据
     * @param tableList
     * @return
     */
    private static Map<String,Object> generateFacdeFileImplData(List<String> tableList) {
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("packageName",Constant.SERVICEIMPL_PACKAGE+";");
        dataMap.put("pPackage",Constant.SERVICE_PACKAGE);
        dataMap.put("author",PropertiesLoader.getProperty("config.author"));
        dataMap.put("title","FacadeImpl");
        dataMap.put("email", PropertiesLoader.getProperty("config.email"));
        dataMap.put("date",DateUtils.getCurrentDate());
        dataMap.put("className","FacadeImpl");
        dataMap.put("pname","Facade");
        List<String> tableNameList = new ArrayList<>();
        for (String s : tableList) {
            tableNameList.add(DatabaseNameUtils.convertFromDBToJava(s,0)+"");
        }
        dataMap.put("tableNameList",tableNameList);
        return dataMap;
    }


    /**
     * 生成facade接口数据
     * @param tableList
     * @return
     */
    private static Map<String,Object> generateFacdeFileData(List<String> tableList) {
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("packageName",Constant.SERVICE_PACKAGE+";");
        dataMap.put("author",PropertiesLoader.getProperty("config.author"));
        dataMap.put("title","Facade");
        dataMap.put("email", PropertiesLoader.getProperty("config.email"));
        dataMap.put("date",DateUtils.getCurrentDate());
        dataMap.put("className","Facade");
        List<String> tableNameList = new ArrayList<>();
        for (String s : tableList) {
            tableNameList.add(DatabaseNameUtils.convertFromDBToJava(s,0)+"");
        }
        dataMap.put("tableNameList",tableNameList);
        return dataMap;
    }

}
