package cn.sy.util;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {

    private static  Properties prop = new Properties();
    private final static String file = new PropertiesUtil().getClass().getResource("zkem-info.properties").getFile();

    static {
        try {
            prop.load(new FileInputStream(file));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String getValue(String key){
        return prop.getProperty(key);
    }

    public static void setValue(String key, String value){
       prop.setProperty(key, value);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            prop.store(fos, null);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
