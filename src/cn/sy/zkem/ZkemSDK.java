package cn.sy.zkem;

import cn.sy.model.ZkemConf;
import cn.sy.service.CheckService;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.STA;
import com.jacob.com.Variant;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.swing.text.Document;
import java.util.*;

/**
 * 中控考勤机sdk函数调用类
 *
 * @author hewei
 */
public class ZkemSDK {

    private static Log log = Logs.getLog(ZkemSDK.class);

    private ActiveXComponent zkem = null;

    /**
     * 连接考勤机
     *
     * @param address 考勤机地址
     * @param port    端口号
     * @return
     */
    public boolean connect(String address, int port) {
        // 防止断开连接重新连接出现的多次监听，只能这样了...
        zkem = new ActiveXComponent("zkemkeeper.ZKEM.1");
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
     * 读取所有考勤记录到 PC 的内部缓冲区，配合getGeneralLogData使用。
     *
     * @param machineNumber 机器 号
     * @return
     */
    public boolean readGeneralLogData(Integer machineNumber) {
        boolean result = zkem.invoke("ReadGeneralLogData", machineNumber).getBoolean();
        return result;
    }

    /**
     * 获取缓存中的考勤数据，配合readGeneralLogData / readLastestLogData使用。
     *
     * @return 返回的map中，包含以下键值：
     * "EnrollNumber"  人员编号
     * "Time"          考勤时间串，格式: yyyy-MM-dd HH:mm:ss
     * "VerifyMode"
     * "InOutMode"
     * "Year"          考勤时间：年
     * "Month"         考勤时间：月
     * "Day"           考勤时间：日
     * "Hour"			考勤时间：时
     * "Minute"		考勤时间：分
     * "Second"		考勤时间：秒
     */
    public List<Map<String, Object>> getGeneralLogData() {

        Variant v0 = new Variant(1);
        Variant dwEnrollNumber = new Variant("", true);
        Variant dwVerifyMode = new Variant(0, true);
        Variant dwInOutMode = new Variant(0, true);
        Variant dwYear = new Variant(0, true);
        Variant dwMonth = new Variant(0, true);
        Variant dwDay = new Variant(0, true);
        Variant dwHour = new Variant(0, true);
        Variant dwMinute = new Variant(0, true);
        Variant dwSecond = new Variant(0, true);
        Variant dwWorkCode = new Variant(0, true);
        List<Map<String, Object>> strList = new ArrayList<>();
        boolean newresult = false;

        do {
            Variant vResult = Dispatch.call(zkem, "SSR_GetGeneralLogData", v0, dwEnrollNumber, dwVerifyMode, dwInOutMode, dwYear, dwMonth, dwDay, dwHour,
                    dwMinute, dwSecond, dwWorkCode);
            newresult = vResult.getBoolean();
            if (newresult) {
                String enrollNumber = dwEnrollNumber.getStringRef();
                //如果没有编号，则跳过。
                if (enrollNumber == null || enrollNumber.trim().length() == 0)
                    continue;

                Map<String, Object> m = new HashMap<>();
//                m.put("Time", dwYear.getIntRef() + "-" + dwMonth.getIntRef() + "-" + dwDay.getIntRef() + " " + dwHour.getIntRef() + ":" + dwMinute.getIntRef() + ":" + dwSecond.getIntRef());
//                m.put("VerifyMode", dwVerifyMode.getIntRef());
//                m.put("InOutMode", dwInOutMode.getIntRef());

                m.put("EnrollNumber", enrollNumber);
                m.put("Year", dwYear.getIntRef());
                m.put("Month", dwMonth.getIntRef());
                m.put("Day", dwDay.getIntRef());
                m.put("Hour", dwHour.getIntRef());
                m.put("Minute", dwMinute.getIntRef());
                m.put("Second", dwSecond.getIntRef());

                strList.add(m);
            }

        } while (newresult == true);

        return strList;

    }

    /**
     * 监控考勤机实时事件
     */
    public void regEvent(ZkemConf zkemConf, CheckService checkService) {
        Variant v0 = new Variant(zkemConf.getNumber());
        Variant eventMask = new Variant(65535);
        boolean regEvent = zkem.invoke("RegEvent", v0, eventMask).getBoolean();
        if (regEvent){
            SensorEvents se = new SensorEvents(zkemConf.getIpAddr(), zkemConf, checkService);
            DispatchEvents de = new DispatchEvents(zkem.getObject(), se);
            log.info("考勤机实时监听中...");
        }else{
            log.error("注册实时监听事件失败，请重新连接考勤机");
        }
    }

    public static void main(String[] args) {
        ZkemSDK zkemSDK = new ZkemSDK();
        boolean connect = zkemSDK.connect("192.168.0.240", 4370);
        if (connect) {
            System.out.println("Connect success!");
            boolean readAllGLogData = zkemSDK.readGeneralLogData(1);
            if (readAllGLogData) {
                System.out.println("读取数据到缓存区成功！");
            }
        }
    }


}  