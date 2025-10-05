package hsb.lol.lolfight.demo;

/**
 * @author 胡帅博
 * @date 2023/9/5 12:10
 */
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WindowAnimationExample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Button minimizeButton = new Button("Minimize");



        minimizeButton.setOnAction(event -> {
            // 获取当前屏幕的大小和任务栏的高度
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();
            double taskbarHeight = screenHeight - screenBounds.getMinY();

            // 动画持续时间和缩放比例
            Duration duration = Duration.seconds(1);
            double initialScale = 1.0;
            double targetScale = 0.5;

            // 创建缩放动画
            ScaleTransition scaleTransition = new ScaleTransition(duration, primaryStage.getScene().getRoot());
            scaleTransition.setFromX(initialScale);
            scaleTransition.setFromY(initialScale);
            scaleTransition.setToX(targetScale);
            scaleTransition.setToY(targetScale);

            // 创建透明度动画
            FadeTransition fadeTransition = new FadeTransition(duration, primaryStage.getScene().getRoot());
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);

            // 创建窗口位置变化动画
//            TranslateTransition translateTransition = new TranslateTransition(duration, primaryStage.getScene().getWindow());
//            translateTransition.setFromX(0);
//            translateTransition.setFromY(0);
//            translateTransition.setToX((screenWidth - primaryStage.getWidth()) / 2);
//            translateTransition.setToY(screenHeight - primaryStage.getHeight() - taskbarHeight);

            // 组合缩放动画、透明度动画和窗口位置变化动画
            ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);

            // 播放动画
            parallelTransition.play();
        });

        StackPane root = new StackPane(minimizeButton);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}