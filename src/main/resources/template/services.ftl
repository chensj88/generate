package ${packageName};

<#list importData as value>
${value}
</#list>

/**
* @author ${author}
* @title ${title}
* @email ${email}
* @package ${packageName}
* @date ${date}
*/
public interface ${className} {
    /**
     * ${domainName}服务接口 数据新增
     * @param ${paramT}
     * @return int
     */
    public int create${domainName}(${domainName} ${paramT});
    /**
     * ${domainName}服务接口 数据更新
     * @param ${paramT}
     * @return int
     */
    public int modify${domainName}(${domainName} ${paramT});
    /**
     * ${domainName}服务接口 数据删除
     * @param ${paramT}
     * @return int
     */
    public int remove${domainName}(${domainName} ${paramT});
    /**
     * ${domainName}服务接口 单条数据查询
     * @param ${paramT}
     * @return int
     */
    public ${domainName} get${domainName}(${domainName} ${paramT});
    /**
     * ${domainName}服务接口 统计数据条数
     * @param ${paramT}
     * @return ${domainName}
     */
    public int get${domainName}Count(${domainName} ${paramT});
    /**
     * ${domainName}服务接口 查询List
     * @param ${paramT}
     * @return ${domainName}List
     */
    public List<${domainName}> get${domainName}List(${domainName} ${paramT});
    /**
     * ${domainName}服务接口 分页查询
     * @param ${paramT}
     * @return ${domainName}List
     */
    public List<${domainName}> get${domainName}PageList(${domainName} ${paramT});
}