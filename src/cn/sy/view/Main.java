package cn.sy.view;

import cn.sy.util.PathUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main extends Application {

    private static Log log = Logs.getLog(Application.class);

    public static void main(String[] args) {
        try {
            Main.launch(args);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "运行错误，详情请看根目录下error.log文件");
            try {
                File file = new File(PathUtil.getUserDirParent() + "\\error.log");
                if( !file.exists()){
                    file.createNewFile();
                }
                PrintWriter pw = new PrintWriter(file);
                e.printStackTrace(pw);
                pw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(new Controller());
        loader.setLocation(getClass().getResource("/cn/sy/view/view.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("考勤机实时监控程序");
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("/app.png")));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
//        System.exit(0);
    }
}
