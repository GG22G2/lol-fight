package hsb.lol.lolfight.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

/**
 * RootPanel - Cyber Hex Style
 * 赛博海克斯风格自定义窗口标题栏
 */
public class RootPanel extends BorderPane {

    public RootPanel(Stage stage) {
        super();
        initHeader(stage);
        getStyleClass().add("main-glass-bg");
    }

    public void setRootBackgroundTransparent(boolean transparent) {
        if (transparent) {
            setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        } else {
            setStyle("-fx-background-color: transparent;");
        }
    }

    private void initHeader(Stage stage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("main-header");
        header.setPadding(new Insets(10, 8, 8, 12));

        // 应用标题 - LOL·FIGHT
        Label titleLabel = new Label("LOL·FIGHT");
        titleLabel.getStyleClass().add("app-title");
        DropShadow titleGlow = new DropShadow(12, Color.rgb(0, 240, 255, 0.4));
        titleGlow.setOffsetY(0);
        titleLabel.setEffect(titleGlow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 最小化按钮
        StackPane miniBtn = createWindowControlButton(
            "M 4 10 L 12 10",
            "#788299",
            "#FFFFFF"
        );
        miniBtn.setOnMouseClicked(e -> stage.setIconified(true));

        // 关闭按钮
        StackPane closeBtn = createWindowControlButton(
            "M 4 4 L 12 12 M 12 4 L 4 12",
            "#788299",
            "#FF4565"
        );
        closeBtn.getStyleClass().add("close");
        closeBtn.setOnMouseClicked(e -> {
            stage.hide();
            Platform.exit();
            System.exit(0);
        });

        header.getChildren().addAll(titleLabel, spacer, miniBtn, closeBtn);
        setTop(header);
    }

    public void setRoot(Node root) {
        setCenter(root);
    }

    private StackPane createWindowControlButton(String svgPath, String normalColor, String hoverColor) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setStroke(Color.web(normalColor));
        icon.setStrokeWidth(1.5);
        icon.setFill(null);

        StackPane btn = new StackPane(icon);
        btn.getStyleClass().add("window-control-btn");
        btn.setPrefSize(28, 24);
        btn.setMinSize(28, 24);

        btn.setOnMouseEntered(e -> icon.setStroke(Color.web(hoverColor)));
        btn.setOnMouseExited(e -> icon.setStroke(Color.web(normalColor)));

        return btn;
    }
}
