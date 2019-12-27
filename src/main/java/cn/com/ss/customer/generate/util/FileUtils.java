package cn.com.ss.customer.generate.util;

import java.io.File;

/**
 * @author chenshijie
 * @title
 * @email chensj@winning.com.cm
 * @package cn.com.ss.customer.generate.util
 * @date 2018-05-26 3:20
 */
public class FileUtils {

    public static String createABSPath(String targetPath,String targetPackage){
        StringBuilder path = new StringBuilder();

        if(targetPath.endsWith("/")){
            targetPath = targetPath.substring(0,targetPath.lastIndexOf("/"));
        }
        path.append(targetPath);
        String[] paths = targetPackage.split("\\.");
        for(int i=0; i < paths.length ; i++){
            path.append("/"+ paths[i]);
        }
        File file = new File(path.toString());
        if(!file.exists()){
            file.mkdirs();
        }
        return path.toString();
    }

    public static void main(String[] args){
       createABSPath("d:/winning","com.cn.demo");
    }
}
