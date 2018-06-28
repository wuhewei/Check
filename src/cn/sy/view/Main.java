package cn.sy.view;

import cn.sy.util.PathUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            Main.launch(args);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "运行错误，详情请看根目录下error.txt文件");
            try {
                File file = new File(PathUtil.getUserDirParent() + "\\error.txt");
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
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("考勤机实时监控程序");
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("/app.png")));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
}
