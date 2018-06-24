package cn.sy.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("考勤机实时监控程序");
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("/icon/app.png")));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
