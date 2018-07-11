package cn.sy.view;

import cn.sy.model.CheckRecord;
import cn.sy.model.ZkemConf;
import cn.sy.service.CheckService;
import cn.sy.util.*;
import cn.sy.zkem.ZkemSDK;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

public class Controller implements Initializable {

    private static Log log = Logs.getLog(Controller.class);

    @FXML
    private Button btnCon;
    @FXML
    private Button btnDisCon;
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

    private CheckService checkService = new CheckService();

    private ZkemSDK sdk = new ZkemSDK();
    private ZkemConf zkem = null;
    private Dao dao = null;

    /**
     * 连接考勤机
     * @param event
     */
    @FXML
    public void conn(ActionEvent event) {
        zkem = dao.fetch(ZkemConf.class, Cnd.where("ip_address", "=", tfIp.getText())
                .and("port", "=", tfPort.getText())
                .and("number", "=", tfNumber.getText()));
        if (zkem == null) {
            log.error("系统不存在该考勤机");
            return;
        }
        updateButtonLater(btnCon, "连接中...", true);
        boolean connFlag = sdk.connect(zkem.getIpAddr(), zkem.getPort());
        if (connFlag) {
            log.info("连接成功！");
            btnCon.setVisible(false);
            btnDisCon.setVisible(true);
            updateButtonLater(btnCon, "连接", false);
            PropertiesUtil.setValue("zkem.ipAddr", tfIp.getText());
            PropertiesUtil.setValue("zkem.port", tfPort.getText());
            PropertiesUtil.setValue("zkem.number", tfNumber.getText());

            Task zkemTask = new Task<Void>(){
                @Override
                protected Void call() throws Exception {
                    checkService.addCannotSensorRecord(sdk, zkem);
                    return null;
                }
            };
            Task sensorTask = new Task<Void>(){
                @Override
                protected Void call() throws Exception {
                    sdk.regEvent(zkem, checkService);
                    return null;
                }
            };

            Thread th1 = new Thread(sensorTask);
            th1.setName("实时事件");
            th1.start();
            Thread th2 = new Thread(zkemTask);
            th2.setDaemon(true);
            th2.setName("考勤事件");
            th2.start();
        } else {
            updateButtonLater(btnCon, "连接", false);
            log.error("连接失败！");
        }
    }

    /**
     * 断开考勤机连接
     *
     * @param event
     */
    @FXML
    public void disConn(ActionEvent event) {
        sdk.disConnect();
        zkem = null;
        btnDisCon.setVisible(false);
        btnCon.setVisible(true);
        Runtime.getRuntime().gc();
        Runtime.getRuntime().removeShutdownHook(Thread.currentThread());
    }
    private void updateButtonLater(final Button button, final String text, final boolean disable) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                button.setGraphic(null);
                button.setDisable(disable);
                button.setText(text);
            }
        });
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
    private void initView(){
        try {
            String path = new File("files/isRunAtLogon.bat").getAbsolutePath();
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
            bufferedReader.close();
            in.close();
            if(isRun){
                this.conn(null);
            }
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
        dao = IocUtils.getConn();
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
            String info = new String(b, off, len);
            logTextArea.appendText(info);
        }
    }

    /**
     * 修改注册表，实现程序自启动
     *
     * @param isRun 是否开机启动
     * @throws IOException
     */
    private static void changeAutoRunAtLogon(boolean isRun) throws IOException {
        String regKey = "HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
        String myAppName = "Check";
        String exePath = PathUtil.getUserDirParent() + "\\check.exe";
        Runtime.getRuntime().exec("reg " + (isRun ? "add " : "delete ") + regKey + " /v " + myAppName + (isRun ? " /d " + exePath : "") + " /f");
    }

}
