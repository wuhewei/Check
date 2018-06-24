import cn.sy.model.ZkemConf;
import cn.sy.util.IocUtils;
import cn.sy.zkem.ZkemSDK;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 考勤机连接
 *
 * @author hewei
 */
public class Main {

    private static Log log = Logs.getLog(Main.class);

    public static void main(String[] args) {

        Dao dao = IocUtils.getConn();

        ZkemConf zkem = dao.fetch(ZkemConf.class, Cnd.where("ip_address", "=", "192.168.0.240"));
        if (zkem == null) {
            log.error("不存在该考勤机");
            return;
        }

        ZkemSDK sdk = new ZkemSDK();
        boolean connFlag = sdk.connect(zkem.getIpAddr(), zkem.getPort());
        if (connFlag) {
            log.info("连接成功！");
            sdk.regEvent(zkem);
        } else {
            log.error("连接失败！");
        }
    }
}  