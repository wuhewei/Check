package cn.sy.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static Log log = Logs.getLog(Controller.class);

    @FXML
    private Button btnCon;
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

    public void conn(ActionEvent event) {
        ((Button) event.getSource()).setText("连接中...");
        ((Button) event.getSource()).setDisable(true);
        log.info(System.getProperty("file.encoding"));
        log.info("你好");
       /* Dao dao = IocUtils.getConn();
        ZkemConf zkem = dao.fetch(ZkemConf.class, Cnd.where("ip_address", "=",  tfIp.getText())
                .and("port", "=", tfPort.getText())
                .and("number", "=", tfNumber.getText()));
        if(zkem == null){
            log.error("系统不存在该考勤机");
            return;
        }
        ZkemSDK sdk = new ZkemSDK();
        boolean connFlag = sdk.connect(zkem.getIpAddr(), zkem.getPort());
        if (connFlag) {
            ((Button)event.getSource()).setText("已连接");
            log.info("连接成功！");
            sdk.regEvent(zkem);
        } else {
            ((Button)event.getSource()).setText("连接");
            ((Button)event.getSource()).setDisable(false);
            log.error("连接失败！");
        }*/
    }

    public void regService(ActionEvent event) {
        try {
            String path = Main.class.getResource("/sdk/Register_SDK.bat").getPath();
            Runtime.getRuntime().exec("cmd /c start" + path);
            log.info("注册服务成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            logTextArea.appendText(String.valueOf((char) b));
        }

    }
}
