package cn.sy.util;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Ioc工具
 *
 * @author hewei
 */
public class IocUtils {

    private static Log log = Logs.getLog(IocUtils.class);
    private static  Ioc ioc = null;
    static {
        try {
            ioc = new NutIoc(new ComboIocLoader(new String[]{"*org.nutz.ioc.loader.json.JsonLoader", "datasource.js"}));
        } catch (Exception e) {
            log.error("获取数据库连接失败", e);
        }
    }

    public static Dao getConn() {
        return ioc.get(Dao.class);
    }
}
