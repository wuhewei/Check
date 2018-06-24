package cn.sy.zkem;

import cn.sy.model.ZkemConf;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.STA;
import com.jacob.com.Variant;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 中控考勤机sdk函数调用类
 *
 * @author hewei
 */
public class ZkemSDK {

    private static Log log = Logs.getLog(ZkemSDK.class);

    private static ActiveXComponent zkem = new ActiveXComponent("zkemkeeper.ZKEM.1");

    /**
     * 链接考勤机
     *
     * @param address 考勤机地址
     * @param port    端口号
     * @return
     */
    public boolean connect(String address, int port) {
        boolean result = zkem.invoke("Connect_NET", address, port).getBoolean();
        return result;
    }

    /**
     * 断开考勤机链接
     */
    public void disConnect() {
        zkem.invoke("Disconnect");
    }

    /**
     * 监控考勤机实时事件
     */
    public void regEvent(ZkemConf zkemConf) {
        Variant v0 = new Variant(1);
        Variant eventMask = new Variant(65535);
        zkem.invoke("RegEvent", v0, eventMask).getBoolean();

        Dispatch ob = zkem.getObject();
        SensorEvents se = new SensorEvents(zkemConf.getIpAddr(), zkemConf);
        DispatchEvents de = new DispatchEvents(ob, se);

        log.info("考勤机实时监听中...");
        Dispatch.call(zkem, "RegEvent", new Variant(1l), new Variant(65535l));
        STA sta = new STA();
        sta.doMessagePump();
    }
}  