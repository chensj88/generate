<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <settings>
        <!-- 打印查询语句 -->
        <setting name="logImpl" value="STDOUT_LOGGING"/>
        <!-- 全局的映射器启用或禁用缓存。 -->
        <setting name="cacheEnabled" value="true" />
        <!-- 全局启用或禁用延迟加载 -->
        <setting name="lazyLoadingEnabled" value="true" />
        <!-- 允许或不允许多种结果集从一个单独的语句中返回 -->
        <setting name="multipleResultSetsEnabled" value="true" />
        <!-- 设置超时时间 -->
        <setting name="defaultStatementTimeout" value="25000" />
        <!-- 解决map属性返回不全,3.2版本以上可用 -->
        <setting name="callSettersOnNulls" value="true"/>
    </settings>

    <!-- 这里配置扫描整个domain目录，作为mybatis的mapper映射文件的 Aliases -->
    <typeAliases>
        <package name="${domainPackage}" />
    </typeAliases>
</configuration>