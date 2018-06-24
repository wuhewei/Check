package cn.sy.model;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

/**
 * 考勤机配置信息
 *
 * @author hewei
 */
@Table("zkem_conf")
public class ZkemConf {

    @Column("ip_address")
    private String ipAddr;

    @Column("port")
    private Integer port;

    @Column("number")
    private Integer number;

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "ZkemConf{" +
                "ipAddr='" + ipAddr + '\'' +
                ", port=" + port +
                ", number=" + number +
                '}';
    }
}
