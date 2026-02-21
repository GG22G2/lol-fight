package hsb.lol.lolfight;

import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.lcu.websocket.ConnectClient;
import hsb.lol.lolfight.ui.DragScene;
import hsb.lol.lolfight.ui.HeroPriorityConfigWindow;
import hsb.lol.lolfight.ui.RootPanel;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * LOL-Fight Main - Cyber Hex Edition
 * 赛博海克斯风格主页
 */
public class Main extends Application {

    private Region statusDot;
    private ScaleTransition breatheAnimation;

    @Override
    public void start(Stage stage) {
        Config.load();

        RootPanel rootPanel = new RootPanel(stage);

        VBox contentRoot = new VBox(12);
        contentRoot.setPadding(new Insets(6, 12, 12, 12));
        contentRoot.setAlignment(Pos.TOP_CENTER);

        // 主内容面板 - 玻璃拟物风格
        VBox mainPanel = new VBox(10);
        mainPanel.getStyleClass().add("main-content-panel");
        mainPanel.setPadding(new Insets(10, 10, 8, 10));

        // 自动接受对局
        CheckBox autoAcceptCheck = new CheckBox("自动接受对局");
        autoAcceptCheck.getStyleClass().add("cyber-checkbox");
        autoAcceptCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Config.autoAccept = newVal;
            Config.save();
        });
        addHoverAnimation(autoAcceptCheck);

        // 秒抢英雄 + 配置按钮
        HBox autoPickRow = new HBox(8);
        autoPickRow.setAlignment(Pos.CENTER_LEFT);

        CheckBox autoPickCheck = new CheckBox("秒抢英雄");
        autoPickCheck.getStyleClass().add("cyber-checkbox");
        autoPickCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Config.autoPick = newVal;
            Config.save();
        });
        addHoverAnimation(autoPickCheck);

        Label configBtn = new Label("配置");
        configBtn.getStyleClass().add("micro-config-btn");
        configBtn.setOnMouseClicked(e -> {
            ScaleTransition press = new ScaleTransition(Duration.millis(100), configBtn);
            press.setToX(0.9);
            press.setToY(0.9);
            press.setInterpolator(Interpolator.LINEAR);
            press.setOnFinished(ev -> {
                ScaleTransition release = new ScaleTransition(Duration.millis(150), configBtn);
                release.setToX(1.0);
                release.setToY(1.0);
                release.setInterpolator(Interpolator.SPLINE(0.25, 0.1, 0.25, 1));
                release.play();
                HeroPriorityConfigWindow.display();
            });
            press.play();
        });

        autoPickRow.getChildren().addAll(autoPickCheck, configBtn);

        // 打开英雄攻略
        CheckBox openHelpCheck = new CheckBox("打开英雄攻略");
        openHelpCheck.getStyleClass().add("cyber-checkbox");
        openHelpCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Config.openHelp = newVal;
            Config.save();
        });
        addHoverAnimation(openHelpCheck);

        mainPanel.getChildren().addAll(autoAcceptCheck, autoPickRow, openHelpCheck);

        // 状态栏 - 连接指示器
        HBox statusBar = new HBox(6);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(4, 2, 0, 2));

        statusDot = new Region();
        statusDot.getStyleClass().add("status-dot");

        Label statusLabel = new Label("等待连接...");
        statusLabel.getStyleClass().add("status-indicator");

        statusBar.getChildren().addAll(statusDot, statusLabel);

        // 呼吸动画
        setupBreatheAnimation();

        contentRoot.getChildren().addAll(mainPanel, statusBar);
        rootPanel.setRoot(contentRoot);

        // 窗口阴影
        DropShadow windowShadow = new DropShadow(24, Color.rgb(0, 0, 0, 0.7));
        windowShadow.setOffsetY(8);
        rootPanel.setEffect(windowShadow);

        DragScene scene = new DragScene(rootPanel, stage);
        scene.setCanDrag();
        scene.setFill(Color.TRANSPARENT);
        scene.setCamera(new PerspectiveCamera());
        scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("lol大乱斗工具箱");
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setWidth(180);
        stage.setHeight(170);
        stage.show();

        stage.setOnCloseRequest(event -> {
            stage.hide();
            Platform.exit();
            System.exit(0);
        });

        new ConnectClient(msg -> {
            Platform.runLater(() -> {
                if (msg.contains("connected") || msg.contains("已连接")) {
                    statusDot.getStyleClass().add("connected");
                    statusLabel.setText("已连接");
                    if (breatheAnimation != null) {
                        breatheAnimation.play();
                    }
                } else if (msg.contains("disconnected") || msg.contains("断开")) {
                    statusDot.getStyleClass().remove("connected");
                    statusLabel.setText("等待连接...");
                    if (breatheAnimation != null) {
                        breatheAnimation.stop();
                        statusDot.setScaleX(1.0);
                        statusDot.setScaleY(1.0);
                    }
                }
            });
            System.out.println(msg);
        }).connect();

        autoAcceptCheck.setSelected(Config.autoAccept);
        autoPickCheck.setSelected(Config.autoPick);
        openHelpCheck.setSelected(Config.openHelp);
    }

    private void addHoverAnimation(CheckBox checkBox) {
        checkBox.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), checkBox);
            st.setToX(1.02);
            st.setToY(1.02);
            st.setInterpolator(Interpolator.SPLINE(0.25, 0.1, 0.25, 1));
            st.play();
        });
        checkBox.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), checkBox);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.SPLINE(0.25, 0.1, 0.25, 1));
            st.play();
        });
    }

    private void setupBreatheAnimation() {
        breatheAnimation = new ScaleTransition(Duration.millis(1500), statusDot);
        breatheAnimation.setFromX(1.0);
        breatheAnimation.setFromY(1.0);
        breatheAnimation.setToX(1.3);
        breatheAnimation.setToY(1.3);
        breatheAnimation.setAutoReverse(true);
        breatheAnimation.setCycleCount(ScaleTransition.INDEFINITE);
        breatheAnimation.setInterpolator(Interpolator.SPLINE(0.4, 0, 0.6, 1));
    }

    public static void main(String[] args) {
        launch();
    }
}
