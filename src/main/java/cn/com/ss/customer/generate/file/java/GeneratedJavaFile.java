package cn.com.ss.customer.generate.file.java;

import cn.com.ss.customer.generate.domain.TableInfo;
import cn.com.ss.customer.generate.file.GeneratedFile;

/**
 * java文件生成
 *
 * @author chenshijie
 * @date 2018-05-25 20:35
 */
public class GeneratedJavaFile extends GeneratedFile {

    /**
     * 文件编码.
     */
    private String fileEncoding;

    private TableInfo tableInfo;

    /**
     * 构造方法
     *
     * @param tableInfo
     */
    public GeneratedJavaFile(TableInfo tableInfo) {
        super(tableInfo.getDomainPath());
        this.tableInfo = tableInfo;
    }

    @Override
    public String getFormattedContent() {
        return null;
    }

    @Override
    public String getFileName() {
        return tableInfo.getDomainName() + ".java";
    }

    @Override
    public String getTargetPackage() {
        return tableInfo.getDomainPackage();
    }

    @Override
    public boolean isMergeable() {
        return false;
    }
}
