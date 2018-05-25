package cn.com.ss.customer.generate;

import cn.com.ss.customer.generate.util.PropertiesLoader;

/**
 * @author chenshijie
 * @title
 * @email chensj@winning.com.cm
 * @package cn.com.ss.customer.generate
 * @date 2018-05-22 22:50
 */
public class Constant {


    public static String PREFIX = PropertiesLoader.getProperty("config.package");

    public static String DOMAIN_PACKAGE = PREFIX + ".domain";

    public static String DAO_PACKAGE = PREFIX + ".dao";

    public static String DAOIMPL_PACKAGE = PREFIX + ".dao.impl";

    public static String MAPPER_PACKAGE = "config.mapper";

    public static String SERVICE_PACKAGE = PREFIX + ".service";

    public static String SERVICEIMPL_PACKAGE = PREFIX + ".service.impl";
}
