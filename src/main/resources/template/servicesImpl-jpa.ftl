package ${packageName};

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
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
@Service
@Transactional(rollbackFor = Exception.class)
public class ${className} implements  ${pClassName} {

    @Autowired
    private ${daoClassName} ${daoClassNameT};

    @Override
    public ${domainName} save${domainName}(${domainName} ${paramT}) {
        return ${daoClassNameT}.save(${paramT});
    }

   <#-- @Override
    public void delete${domainName}ById(${domainName} ${paramT}) {
        ${paramT}.setIsDel(1);
        ${daoClassNameT}.save(${paramT});
    }-->

    @Override
    public ${domainName} get${domainName}ById(${domainName} ${paramT}) {
        return ${daoClassNameT}.findById(${paramT}.getId()).orElse(null);
    }

    @Override
    public Page<${domainName}> get${domainName}PageList(${domainName} ${paramT}, Row row) {
        Pageable pageable = row.getPageable();
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<${domainName}> example = Example.of(${paramT},matcher);
        return ${daoClassNameT}.findAll(example,pageable);
    }

    @Override
    public Long countBy${domainName}(${domainName} ${paramT}){
        return ${daoClassNameT}.count(Example.of(${paramT}));
    }

    @Override
    public List<${domainName}> findAllBy${domainName}(${domainName} ${paramT}){
        if( ${paramT} == null ){
            return ${daoClassNameT}.findAll();
        }else{
            return ${daoClassNameT}.findAll(Example.of(${paramT}));
        }

    }

}
