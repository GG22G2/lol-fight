package hsb.lol.lolfight.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * @author hsb
 * @date 2023/8/27 0:53
 */
public class RootPanel extends BorderPane {

    public RootPanel(Stage stage) {
        super();
        initHeader(stage);

    }


    public void setRootBackgroundTransparent(boolean transparent){
        if (transparent){
            setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        }else {
            setStyle("-fx-background-color: rgba(255, 255, 255, 255);");
        }
    }

    public void initHeader(Stage stage) {
        VBox header = new VBox();
        header.prefHeight(26);
        header.setAlignment(Pos.TOP_CENTER);
        header.setStyle("-fx-background-color: #282B3D;");


        HBox fillWithBox = new HBox();
        fillWithBox.prefHeight(0);
        HBox.setHgrow(fillWithBox, Priority.ALWAYS);


        HBox optBtnBox = new HBox(4);
        optBtnBox.prefHeight(26);
        optBtnBox.setAlignment(Pos.CENTER_RIGHT);
        optBtnBox.setPadding(new Insets(0,1,0,0));



        StackPane miniBtn = createButton("mini-btn","svg-btn");
        miniBtn.setOnMouseClicked(mouseEvent -> {
            stage.setIconified(true);
        });


        StackPane closeBtn = createButton("close-btn","svg-btn");
        closeBtn.setOnMouseClicked(mouseEvent -> {
           stage.hide();
            Platform.exit();
            System.exit(0);
        });
        optBtnBox.getChildren().addAll(miniBtn,closeBtn);
        header.getChildren().addAll(fillWithBox,optBtnBox);
        setTop(header);
    }


    public void setRoot(Node root){
        setCenter(root);
    }


    public StackPane createButton(String id,String styleClass) {
        StackPane stackPane = new StackPane();
        stackPane.setId(id);
        stackPane.setMaxHeight(Double.NEGATIVE_INFINITY);
        stackPane.setMaxWidth(Double.NEGATIVE_INFINITY);
        stackPane.setPrefHeight(26);
        stackPane.setPrefWidth(40);

        stackPane.getStyleClass().add(styleClass);

        Region region = new Region();
        region.setMaxHeight(Double.NEGATIVE_INFINITY);
        region.setMaxWidth(Double.NEGATIVE_INFINITY);
        region.setPrefHeight(26);
        region.setPrefWidth(40);
        stackPane.getChildren().add(region);

        return stackPane;
    }


}
