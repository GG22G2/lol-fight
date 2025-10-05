package hsb.lol.lolfight;

import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.data.Summoner;
import hsb.lol.lolfight.lcu.websocket.ConnectClient;
import hsb.lol.lolfight.lcu.websocket.LolWssConnect;
import hsb.lol.lolfight.ui.DragScene;
import hsb.lol.lolfight.ui.RootPanel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
    @Override
    public void start(Stage stage) {

        RootPanel rootPanel = new RootPanel(stage);

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #282B3D;");
        rootPanel.setRoot(root);


        root.setBackground(new Background(new BackgroundFill(Color.WHEAT, null, null)));
        DragScene scene = new DragScene(rootPanel, stage);
        scene.setCanDrag();
        scene.setFill(Color.TRANSPARENT);
        scene.setCamera(new PerspectiveCamera());
        scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());


        root.setPadding(new Insets(10, 0, 0, 10));
        HBox autoAcceptRow = new HBox();


        CheckBox autoAcceptCheck = new CheckBox("自动接受对局");
        autoAcceptCheck.setTextFill(Paint.valueOf("#ffffff"));
        autoAcceptCheck.selectedProperty().addListener((observable, oldValue, newValue) -> Config.autoAccept = newValue);
        autoAcceptRow.getChildren().add(autoAcceptCheck);


        //秒选英雄
        HBox autoPickRow = new HBox();
        autoPickRow.setPadding(new Insets(10, 0, 10, 0));
        CheckBox autoPickCheck = new CheckBox("秒抢英雄");
        autoPickCheck.setTextFill(Paint.valueOf("#ffffff"));

        Label configChampionBtn = new Label("配置");
        configChampionBtn.setTextFill(Paint.valueOf("#ffffff"));
        HBox.setMargin(configChampionBtn, new Insets(0, 0, 0, 10));
        configChampionBtn.setPadding(new Insets(3, 5, 3, 5));
        configChampionBtn.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        configChampionBtn.setOnMouseClicked(event -> {

        });

        autoPickRow.setAlignment(Pos.CENTER_LEFT);
        autoPickRow.getChildren().addAll(autoPickCheck, configChampionBtn);



        //打开英雄攻略网页
        HBox openHelpRow = new HBox();
        autoPickRow.setPadding(new Insets(10, 0, 10, 0));
        CheckBox openHelpCheck = new CheckBox("打开英雄攻略");
        openHelpCheck.setTextFill(Paint.valueOf("#ffffff"));
        openHelpCheck.selectedProperty().addListener((observable, oldValue, newValue) -> Config.openHelp = newValue);
        openHelpRow.getChildren().addAll(openHelpCheck);


        root.getChildren().addAll(autoAcceptRow, autoPickRow, openHelpRow);
        stage.setScene(scene);
        stage.setTitle("lol大乱斗工具箱");
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setWidth(140);
        stage.setHeight(130);
        stage.show();
        stage.setOnCloseRequest(event -> {
            //因为创建了websocket连接，点击关闭按钮只能关闭界面，还需要手动关闭java程序才能结束整个程序
            stage.hide();
            Platform.exit();
            System.exit(0);
        });

        //初始化连接
        new ConnectClient(System.out::println).connect();

        //初始勾选
        autoAcceptCheck.selectedProperty().setValue(true);
        autoPickCheck.selectedProperty().setValue(true);
        openHelpCheck.selectedProperty().setValue(true);

    }

    public static void main(String[] args) {
        launch();
    }
}