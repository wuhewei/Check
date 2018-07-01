package cn.sy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author hewei
 */
public class DateUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");

    public static Date format(String source){
        try {
            return sdf.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static String format(Date date){
        return sdf.format(date);
    }

    public static Date formatYMD(Date date){
        try {
            return sdfYMD.parse(format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date formatYMD(String source){
        try {
            return sdfYMD.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
