package cn.sy.view;

import cn.sy.model.ZkemConf;
import cn.sy.util.IocUtils;
import cn.sy.zkem.ZkemSDK;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static Log log = Logs.getLog(Controller.class);

    @FXML
    private Button btnCon;
    @FXML
    private Button btnDisCon;
    @FXML
    private Button btnReg;
    @FXML
    private TextField tfIp;
    @FXML
    private TextField tfPort;
    @FXML
    private TextField tfNumber;
    @FXML
    private TextArea logTextArea;

    private ZkemSDK sdk = new ZkemSDK();

    public void conn(ActionEvent event) {

        Dao dao = IocUtils.getConn();
        ZkemConf zkem = dao.fetch(ZkemConf.class, Cnd.where("ip_address", "=", tfIp.getText())
                .and("port", "=", tfPort.getText())
                .and("number", "=", tfNumber.getText()));
        if (zkem == null) {
            log.error("系统不存在该考勤机");
            return;
        }
        ((Button) event.getSource()).setText("连接中...");
        ((Button) event.getSource()).setDisable(true);
        boolean connFlag = sdk.connect(zkem.getIpAddr(), zkem.getPort());
        ((Button) event.getSource()).setText("连接");
        ((Button) event.getSource()).setDisable(false);
        if (connFlag) {
            ((Button) event.getSource()).setVisible(false);
            btnDisCon.setVisible(true);
            log.info("连接成功！");
            sdk.regEvent(zkem);
        } else {
            log.error("连接失败！");
        }

    }

    public void disConn(ActionEvent event) {
        sdk.disConnect();
        btnDisCon.setVisible(false);
        btnCon.setVisible(true);
    }

    public void regService(ActionEvent event) {
        try {
            String path = Main.class.getResource("/sdk/Register_SDK.bat").getPath();
            path = path.substring(1, path.length());
            Runtime.getRuntime().exec(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAtLogon(ActionEvent event) {
        try {
            boolean isRun = ((CheckBox) event.getSource()).isSelected();
            changeAutoRunAtLogon(isRun);
            log.info((isRun ? "启动" : "取消") + "开机运行本程序");
        } catch (IOException e) {
            log.info("设置失败", e);
        }
    }

    public void initView(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Controller.Console console = new Controller.Console();
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);
        System.setErr(ps);
        System.err.flush();
        System.out.flush();
    }

    private class Console extends OutputStream {

        @Override
        public void write(int b) throws IOException {

        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {

            String info = new String(b, off, len, "UTF-8");
            logTextArea.appendText(info);
        }

    }

    /**
     * 修改注册表，实现程序自启动
     *
     * @param isRun 是否开机启动
     * @throws IOException
     */
    public static void changeAutoRunAtLogon(boolean isRun) throws IOException {
        String regKey = "HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
        String myAppName = "Check";
        String exePath = System.getProperty("user.dir") + "Check.exe";
        Runtime.getRuntime().exec("reg " + (isRun ? "add " : "delete ") + regKey + " /v " + myAppName + (isRun ? " /d " + exePath : "") + " /f");
    }

}
