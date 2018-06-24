package cn.sy.model;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * 学员勤签到记录
 *
 * @author hewei
 */
@Table("check_record")
public class CheckRecord {

    @Column("student_number")
    private Integer studentNumber;

    @Column("zkem_number")
    private Integer zkemNumber;

    @Column("check_time")
    private Date checkTime;

    @Column("entry_time")
    private Date entryTime;

    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Integer getZkemNumber() {
        return zkemNumber;
    }

    public void setZkemNumber(Integer zkemNumber) {
        this.zkemNumber = zkemNumber;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    @Override
    public String toString() {
        return "CheckRecord{" +
                "studentNumber=" + studentNumber +
                ", zkemNumber=" + zkemNumber +
                ", checkTime=" + checkTime +
                ", entryTime=" + entryTime +
                '}';
    }
}
