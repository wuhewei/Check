package cn.sy.service;

import cn.sy.model.CheckRecord;
import cn.sy.model.ZkemConf;
import cn.sy.util.IocUtils;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Date;

/**
 * 考勤Service
 *
 * @author hewei
 */
public class CheckService {

    private static Log log = Logs.getLog(CheckService.class);
    private static Dao dao;

    static {
        dao = IocUtils.getConn();
    }

    public void addRecord(Integer stuNumber, Integer zkemNumber) {
        CheckRecord record = new CheckRecord();
        record.setStudentNumber(stuNumber);
        record.setZkemNumber(zkemNumber);
        record.setCheckTime(new Date());
        record = dao.insert(record);
        log.info("实时录入考勤信息" + (record == null ? "失败" : "成功") +
                "，学员编号：" + stuNumber);
    }
}
