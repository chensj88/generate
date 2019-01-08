package ${packageName};

<#list importData as value>
${value}
</#list>
import org.springframework.stereotype.Repository;
/**
* @author ${author}
* @title ${title}
* @email ${email}
* @package ${packageName}
* @date ${date}
*/
@Repository
public interface ${className} {
    /**
     * ${domainName} 数据新增
     * @param ${paramT}
     * @return int
     * @throws DataAccessException
     */
    public int insert${domainName}(${domainName} ${paramT}) throws DataAccessException;
    /**
     * ${domainName} 数据更新
     * @param ${paramT}
     * @return int
     * @throws DataAccessException
     */
    public int update${domainName}(${domainName} ${paramT}) throws DataAccessException;
    /**
     * ${domainName} 数据删除
     * @param ${paramT}
     * @return int
     * @throws DataAccessException
     */
    public int delete${domainName}(${domainName} ${paramT}) throws DataAccessException;
    /**
     * ${domainName} 单条数据查询
     * @param ${paramT}
     * @return ${domainName}
     * @throws DataAccessException
     */
    public ${domainName} select${domainName}(${domainName} ${paramT}) throws DataAccessException;
    /**
     * ${domainName} 统计数据条数
     * @param ${paramT}
     * @return Object
     * @throws DataAccessException
     */
    public Object select${domainName}Count(${domainName} ${paramT}) throws DataAccessException;
    /**
     * ${domainName} 查询List
     * @param ${paramT}
     * @return ${domainName}List
     * @throws DataAccessException
     */
    public List<${domainName}> select${domainName}List(${domainName} ${paramT}) throws DataAccessException;
    /**
     * ${domainName} 分页查询
     * @param ${paramT}
     * @return ${domainName}List
     * @throws DataAccessException
     */
    public List<${domainName}> select${domainName}PageList(${domainName} ${paramT}) throws DataAccessException;
}