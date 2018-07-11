package cn.sy.service;

import cn.sy.model.CheckRecord;
import cn.sy.model.ZkemConf;
import cn.sy.util.DateUtil;
import cn.sy.util.IocUtils;
import cn.sy.zkem.ZkemSDK;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 考勤Service
 *
 * @author hewei
 */
public class CheckService {

    private static Log log = Logs.getLog(CheckService.class);
    private static Dao dao;

    public CheckService() {
        dao = IocUtils.getConn();
    }

    /**
     * 录入考勤记录
     *
     * @param stuNumber 学员编号/卡号
     * @param zkemNumber 机器号
     */
    public void addRecord(Integer stuNumber, Integer zkemNumber) {
        CheckRecord record = new CheckRecord();
        record.setStudentNumber(stuNumber);
        record.setZkemNumber(zkemNumber);
        record.setCheckTime(new Date());
        record = dao.insert(record);
        log.info("实时录入考勤信息" + (record == null ? "失败" : "成功") +
                "，学员编号：" + stuNumber);
    }

    /**
     * 录入不能实时插入到数据库的数据，如考勤机断电，故障等
     *
     * @param zkemSDK 考勤机SDK
     * @param zkem 考勤机Model
     */
    public void addCannotSensorRecord(ZkemSDK zkemSDK, ZkemConf zkem){
        zkemSDK = new ZkemSDK();
        boolean readGeneralLogData = zkemSDK.readGeneralLogData(zkem.getNumber());
        if (readGeneralLogData){
            List<Map<String, Object>> records = zkemSDK.getGeneralLogData();
            final List<CheckRecord> list = new ArrayList<>();
            final CheckRecord lastRecord = getLastRecord(zkem.getNumber());
            for (Map<String, Object> m : records) {
                CheckRecord record = getRecord(zkem.getNumber(), m);
                if (isGtLastCheckTime(lastRecord, record))
                    list.add(record);
            }
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    if (list.size() > 0){
                        dao.fastInsert(list);
                        log.info("成功恢复" + (lastRecord == null ? "" : (DateUtil.format(lastRecord.getCheckTime()) + "之后的")) + list.size() + "条考勤记录");
                    }
                }
            });
        }
    }

    private CheckRecord getRecord(Integer zkemNumberm, Map<String, Object> m){
        CheckRecord record = new CheckRecord();
        Date checkTime = DateUtil.format(m.get("Year") + "-" + m.get("Month") + "-" + m.get("Day") + " " + m.get("Hour") + ":" + m.get("Minute") + ":" + m.get("Second"));
        Integer enrollNumber = Integer.valueOf(m.get("EnrollNumber").toString());
        record.setZkemNumber(zkemNumberm);
        record.setStudentNumber(enrollNumber);
        record.setCheckTime(checkTime);
        return record;
    }

    private boolean isGtLastCheckTime(CheckRecord lastRecord, CheckRecord record){
        return lastRecord == null ? true : record.getCheckTime().after(lastRecord.getCheckTime());
    }

    private CheckRecord getLastRecord(int zkemNumber){
        Sql sql = Sqls.create("select * from check_record where zkem_number = $zkemNumber order by check_time desc limit 1");
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(CheckRecord.class));
        sql.setVar("zkemNumber", zkemNumber);
        dao.execute(sql);
        return sql.getObject(CheckRecord.class);
    }

    /**
     * 获取当天考勤记录
     *
     * @param zkemSDK
     * @param zkem
     * @return
     */
    public List<CheckRecord> getTodayRecord(ZkemSDK zkemSDK, ZkemConf zkem){
        List<CheckRecord> list = new ArrayList<>();
        boolean readGeneralLogData = zkemSDK.readGeneralLogData(zkem.getNumber());
        if (readGeneralLogData) {
            List<Map<String, Object>> records = zkemSDK.getGeneralLogData();
            for (Map<String, Object> m : records) {
                CheckRecord record = getRecord(zkem.getNumber(), m);
                if (DateUtil.formatYMD(record.getCheckTime()).compareTo(DateUtil.formatYMD(new Date())) == 0)
                    list.add(record);
            }
        }

        return list;
    }

    /**
     * 获取指定日期的考勤记录
     *
     * @param zkemSDK
     * @param zkem
     * @return
     */
    public List<CheckRecord> getFixedDayRecord(ZkemSDK zkemSDK, ZkemConf zkem, Date start, Date end){
        List<CheckRecord> list = new ArrayList<>();
        boolean readGeneralLogData = zkemSDK.readGeneralLogData(zkem.getNumber());
        if (readGeneralLogData) {
            List<Map<String, Object>> records = zkemSDK.getGeneralLogData();
            for (Map<String, Object> m : records) {
                CheckRecord record = getRecord(zkem.getNumber(), m);
                if (DateUtil.formatYMD(record.getCheckTime()).compareTo(start) > -1 && DateUtil.formatYMD(record.getCheckTime()).compareTo(end) < 1)
                    list.add(record);
            }
        }

        return list;
    }
}
