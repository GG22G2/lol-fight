package hsb.lol.lolfight.demo;

/**
 * @author 胡帅博
 * @date 2023/9/5 11:35
 */

import hsb.lol.lolfight.ui.DragScene;
import hsb.lol.lolfight.ui.RootPanel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class CustomMinimizeButton extends Application {

    private DoubleProperty scaleXProperty;
    private DoubleProperty scaleYProperty;
    private DoubleProperty opacityProperty;


    private double showWidth = 0;
    private double showHeight = 0;
    private double showOpacity = 1;
    private double showX = 0;
    private double showY = 0;


    Pane root = new Pane();

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {


        primaryStage.initStyle(StageStyle.TRANSPARENT);

        RootPanel rootPanel = new RootPanel(primaryStage);
        rootPanel.setRootBackgroundTransparent(true);
        rootPanel.setRoot(root);

        DragScene scene = new DragScene(rootPanel, primaryStage);
        scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        scene.setCanDrag();

        root.setPrefHeight(Region.USE_COMPUTED_SIZE);
        root.setPrefWidth(Region.USE_COMPUTED_SIZE);

        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);

        // 创建最小化按钮
        Button minimizeButton = createMinimizeButton(primaryStage);


        root.getChildren().addAll(minimizeButton);
        root.setStyle("-fx-background-color: rgba(100, 0, 0, 255);");

        // 监听舞台宽度和高度的变化
        scaleXProperty = new SimpleDoubleProperty();
        scaleYProperty = new SimpleDoubleProperty();
        opacityProperty = new SimpleDoubleProperty();


        scaleXProperty.addListener((obs, oldVal, newVal) -> rootPanel.setScaleX(newVal.doubleValue()));
        scaleYProperty.addListener((obs, oldVal, newVal) -> rootPanel.setScaleY(newVal.doubleValue()));
        opacityProperty.addListener((obs, oldVal, newVal) -> primaryStage.setOpacity(newVal.doubleValue()));


        primaryStage.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    System.out.println("窗口最小化");
                } else {
                    rootPanel.setScaleX(1);
                    rootPanel.setScaleY(1);
                    primaryStage.setX(showX);
                    primaryStage.setY(showY);
                    primaryStage.setOpacity(showOpacity);
                    System.out.println("窗口恢复1");
                }
            }
        });

        primaryStage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    System.out.println("窗口最大化");
                } else {
                    rootPanel.setScaleX(1);
                    rootPanel.setScaleY(1);
                    primaryStage.setX(showX);
                    primaryStage.setY(showY);
                    primaryStage.setOpacity(showOpacity);

                    System.out.println("窗口恢复2");
                }
            }
        });

        primaryStage.showingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    System.out.println("窗口显示");
                } else {
                    System.out.println("窗口隐藏");
                }
            }
        });


        primaryStage.setWidth(800);
        primaryStage.setHeight(500);
        primaryStage.show();
    }

    private Button createMinimizeButton(final Stage primaryStage) {
        Button minimizeButton = new Button("Minimize");
        minimizeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                minimizePrimaryStage(primaryStage);
            }
        });
        minimizeButton.setLayoutX(10);
        minimizeButton.setLayoutY(10);
        return minimizeButton;
    }

    //从最小化恢复窗口时的动画
    private void iconifiedRestoreAnimation(final Stage primaryStage) {

    }


    //最小化窗口时的处理逻辑
    private void minimizePrimaryStage(final Stage primaryStage) {
        double animationTime = 200; //毫秒
        double miniScale = 0.5; //动画时间内缩小比例

        showHeight = primaryStage.getHeight();
        showWidth = primaryStage.getWidth();
        showOpacity = primaryStage.getOpacity();
        showX = primaryStage.getX();
        showY = primaryStage.getY();


        double yOffset = showY + showHeight > 1080 ? 0 : showHeight * 0.8;

        //  double yOffset = showHeight * 0.8 ;
        double xOffset = 960 - showX - showWidth * miniScale;

        DoubleProperty xProperty = new SimpleDoubleProperty();
        DoubleProperty yProperty = new SimpleDoubleProperty();
        xProperty.addListener((obs, oldVal, newVal) -> primaryStage.setX(newVal.doubleValue()));
        yProperty.addListener((obs, oldVal, newVal) -> primaryStage.setY(newVal.doubleValue()));

        //窗口大小变化
        Timeline timeline1 = new Timeline(144
                , new KeyFrame(Duration.millis(0), new KeyValue(scaleXProperty, 1), new KeyValue(scaleYProperty, 1))
                , new KeyFrame(Duration.millis(animationTime), new KeyValue(scaleXProperty, miniScale), new KeyValue(scaleYProperty, miniScale))
        );
        //窗口透明度变化
        Timeline timeline2 = new Timeline(144
                , new KeyFrame(Duration.millis(0), new KeyValue(opacityProperty, 1))
                , new KeyFrame(Duration.millis(animationTime), new KeyValue(opacityProperty, 0))
        );
        //窗口位置变化
        Timeline timeline3 = new Timeline(144
                , new KeyFrame(Duration.millis(0), new KeyValue(xProperty, showX), new KeyValue(yProperty, showY))
                , new KeyFrame(Duration.millis(animationTime), new KeyValue(xProperty, showX + xOffset), new KeyValue(yProperty, showY + yOffset))
        );
        ParallelTransition parallelTransition = new ParallelTransition(timeline1, timeline2, timeline3);

        // 动画结束后最小化舞台
        parallelTransition.setOnFinished(event -> primaryStage.setIconified(true));

        parallelTransition.play();

    }
}