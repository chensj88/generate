package cn.com.ss.customer.generate.util;

import cn.com.ss.customer.generate.Constant;
import cn.com.ss.customer.generate.code.java.JavaFileGenerator;
import cn.com.ss.customer.generate.domain.TableInfo;
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

    private static boolean isConfiig = false;

    private static Logger logger = LoggerFactory.getLogger(GenerateFileUtils.class);

    public static void generateFile(String tableName, Connection connection) {
        TableInfo info = null;
        try {
            info = DatabaseUtils.getTableInfo(tableName, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!isConfiig){
            generateOnceFile();
            isConfiig = true;
        }
        generateFile(info);

    }

    /**
     * 生成一次性文件
     * mybatis-config、BaseDomain和Row文件
     */
    private static void generateOnceFile(){
        generateMyBatisConfigFile();
        generateBaseDomainFile();
        generateRowFile();
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

    private static void generateFile(TableInfo info ){
        JavaFileGenerator generator = new JavaFileGenerator();
        generator.setTableInfo(info);
        Map<String, Object> domainData = generator.generateDomainData();
        Map<String, Object> clientData = generator.generateJavaClientData();
        Map<String, Object> sqlMapData = generator.generateSqlMapData();
        Map<String, Object> serviceData = generator.generateJavaServiceData();
        Map<String, Object>  serviceImplData = generator.generateJavaServiceImplData();
        generateDomainFile(domainData, info);
        generateClientFile(clientData, info);
        generateSqlMapFile(sqlMapData, info);
        generateServiceFile(serviceData, info);
        generateServiceImplFile(serviceImplData,info);

    }

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
