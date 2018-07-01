package cn.sy.util;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

/**
 * 提示框工具
 *
 * @author hewei
 */
public class AlertUtil {

    public static void alertInformation(String info){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("  错误提示");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
}
