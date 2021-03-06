package cn.sy.zkem;

import cn.sy.model.ZkemConf;
import cn.sy.service.CheckService;
import com.jacob.com.Variant;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.EventObject;

/**
 * 中控考勤机实时事件
 *
 * @author hewei
 */
public class SensorEvents extends EventObject {

    private static Log log = Logs.getLog(SensorEvents.class);

    private CheckService checkService;
    private ZkemConf zkem;

    public SensorEvents(Object source, ZkemConf zkem, CheckService checkService) {
        super(source);
        this.zkem = zkem;
        this.checkService = checkService;
    }

    /**
     * 当成功连接机器时触发该事件
     *
     * @param arge
     */
    public void OnConnected(Variant[] arge) {
        log.info("当成功连接机器时触发该事件：");
    }

    /**
     * 当断开机器时触发该事件
     *
     * @param arge
     */
    public void OnDisConnected(Variant[] arge) {
        log.info("当断开机器时触发该事件：");
    }

    /**
     * 当验证通过时触发该事件
     *
     * @param arge
     */
    public void OnAttTransactionEx(Variant[] arge) {
        log.info("当验证通过时触发该事件：");
        Integer stuNumber = Integer.parseInt(arge[0].toString());
        checkService.addRecord(stuNumber, zkem.getNumber());
    }

    /**
     * 当刷卡时触发该消息
     *
     * @param arge
     */
    public void OnHIDNum(Variant[] arge) {
        log.info("当刷卡时触发该消息：");
    }

    /**
     * 当成功登记新用户时触发该消息
     *
     * @param arge
     */
    public void OnNewUser(Variant[] arge) {
        log.info("当成功登记新用户时触发该消息：");
    }

    /**
     * 当用户验证时触发该消息
     *
     * @param arge
     */
    public void OnVerify(Variant[] arge) {
        log.info("当用户验证时触发该消息：");
    }

    /**
     * 当机器进行写卡操作时触发该事件
     *
     * @param arge
     */
    public void OnWriteCard(Variant[] arge) {
        log.info("当机器进行写卡操作时触发该事件：");
    }
}
