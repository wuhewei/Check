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
     * 连接考勤机
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
        log.info("已断开考勤机连接");
    }

    /**
     * 读取考勤记录到 PC 的内部缓冲区，同 ReadAllGLogData
     * @param number 机器号
     * @return
     */
    public boolean ReadAllGLogData(int number){
        boolean result = zkem.invoke("ReadAllGLogData", number).getBoolean();
        return result;
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

    public static void main(String[] args){
        ZkemSDK zkemSDK = new ZkemSDK();
        boolean connect = zkemSDK.connect("192.168.0.240", 4370);
        if (connect){
            System.out.println("Connect success!");
            boolean readAllGLogData = zkemSDK.ReadAllGLogData(1);
            if (readAllGLogData){
                System.out.println("读取数据到缓存区成功！");
            }
        }
    }


}  