package cn.sy.util;

/**
 * @author hewei
 */
public class PathUtil {

    //获取根目录的上层
    public static String getUserDirParent(){
        String path = System.getProperty("user.dir");
        path = path.substring(0, path.lastIndexOf("\\"));
        return path;
    }
}
