package cn.sy.view;

import cn.sy.model.ZkemConf;
import cn.sy.util.IocUtils;
import cn.sy.util.PropertiesUtil;
import cn.sy.zkem.ZkemSDK;
import javafx.application.Platform;
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
import java.util.Timer;
import java.util.TimerTask;

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
    @FXML
    private CheckBox CboxRunAtLogon;

    private ZkemSDK sdk = new ZkemSDK();

    /**
     * 连接考勤机
     * @param event
     */
    public void conn(ActionEvent event) {
        Dao dao = IocUtils.getConn();
        ZkemConf zkem = dao.fetch(ZkemConf.class, Cnd.where("ip_address", "=", tfIp.getText())
                .and("port", "=", tfPort.getText())
                .and("number", "=", tfNumber.getText()));
        if (zkem == null) {
            log.error("系统不存在该考勤机");
            return;
        }
        btnCon.setText("连接中...");
        btnCon.setDisable(true);
        boolean connFlag = sdk.connect(zkem.getIpAddr(), zkem.getPort());
        btnCon.setText("连接");
        btnCon.setDisable(false);
        if (connFlag) {
            log.info("连接成功！");
            btnCon.setVisible(false);
            btnDisCon.setVisible(true);
            PropertiesUtil.setValue("zkem.ipAddr", tfIp.getText());
            PropertiesUtil.setValue("zkem.port", tfPort.getText());
            PropertiesUtil.setValue("zkem.number", tfNumber.getText());
            if(event == null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       sdk.regEvent(zkem);
                    }
                }).start();
            }else {
                sdk.regEvent(zkem);
            }
        } else {
            log.error("连接失败！");
        }
    }

    /**
     * 断开考勤机连接
     *
     * @param event
     */
    public void disConn(ActionEvent event) {
        sdk.disConnect();
        btnDisCon.setVisible(false);
        btnCon.setVisible(true);
    }

    /**
     * 注册考勤机需要的动态库
     *
     * @param event
     */
    public void regService(ActionEvent event) {
        try {
            String path =  new File("sdk/Register_SDK.bat").getAbsolutePath();
            Runtime.getRuntime().exec(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开机自启动
     *
     * @param event
     */
    public void runAtLogon(ActionEvent event) {
        try {
            boolean isRun = ((CheckBox) event.getSource()).isSelected();
            changeAutoRunAtLogon(isRun);
            log.info((isRun ? "启动" : "取消") + "开机运行本程序");
        } catch (IOException e) {
            log.info("设置失败", e);
        }
    }

    /**
     * 初始化页面组件
     */
    public void initView(){
        try {
            String path = new File("isRunAtLogon.bat").getAbsolutePath();
            Process process = Runtime.getRuntime().exec(path);
            InputStream in = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            Boolean isRun = Boolean.valueOf(bufferedReader.readLine());
            String ipAddr = PropertiesUtil.getValue("zkem.ipAddr");
            String port = PropertiesUtil.getValue("zkem.port");
            String number = PropertiesUtil.getValue("zkem.number");
            CboxRunAtLogon.setSelected(isRun);
            tfIp.setText(ipAddr);
            tfPort.setText(port);
            tfNumber.setText(number);
            if(isRun){
                this.conn(null);
            }
            in.close();
            bufferedReader.close();
        } catch (Exception e) {
            log.info("初始化组件失败", e);
        }
    }

    /**
     * 初始化时调用
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Controller.Console console = new Controller.Console();
        PrintStream ps = new PrintStream(console, false);
        System.setOut(ps);
        System.setErr(ps);
        System.err.flush();
        System.out.flush();
        initView();
    }

    /**
     * 自定义输出流
     */
    private class Console extends OutputStream {

        @Override
        public void write(int b) throws IOException {

        }

        /**
         * 追加到TextArea日志面板
         */
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
        String exePath = System.getProperty("user.dir") + "\\check.exe";
        Runtime.getRuntime().exec("reg " + (isRun ? "add " : "delete ") + regKey + " /v " + myAppName + (isRun ? " /d " + exePath : "") + " /f");
    }

}
