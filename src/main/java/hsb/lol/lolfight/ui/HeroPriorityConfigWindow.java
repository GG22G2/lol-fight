package hsb.lol.lolfight.ui;

import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.data.Summoner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HeroPriorityConfigWindow {

    // 经过完全重新设计的、具有科技感和质感的CSS样式 (v2)
    private static final String CSS_STYLE = """
        .root {
            /* 更深邃、更专业的渐变背景 */
            -fx-background-color: linear-gradient(to bottom, #1f2433, #10121b);
            -fx-background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='40' height='40' viewBox='0 0 40 40'%3E%3Cg fill-rule='evenodd'%3E%3Cg fill='%23a9b1c7' fill-opacity='0.03'%3E%3Cpath d='M0 38.59l2.83-2.83 1.41 1.41L1.41 40H0v-1.41zM0 1.4l2.83 2.83 1.41-1.41L1.41 0H0v1.41zM38.59 40l-2.83-2.83 1.41-1.41L40 38.59V40h-1.41zM40 1.41l-2.83 2.83-1.41-1.41L38.59 0H40v1.41zM20 18.6l2.83-2.83 1.41 1.41L21.41 20l2.83 2.83-1.41 1.41L20 21.41l-2.83 2.83-1.41-1.41L18.59 20l-2.83-2.83 1.41-1.41L20 18.59z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
            -fx-font-family: 'Segoe UI', 'Microsoft YaHei UI', system-ui, sans-serif;
            -fx-border-color: rgba(0, 170, 255, 0.2);
            -fx-border-width: 1px;
            -fx-border-radius: 8px;
            -fx-background-radius: 8px;
        }

        .window-title {
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            -fx-text-fill: #00aaff; /* 优化为更柔和的青色 */
            -fx-effect: dropshadow(gaussian, rgba(0, 170, 255, 0.5), 15, 0, 0, 0);
        }

        .column-header {
            -fx-font-size: 15px;
            -fx-font-weight: 600;
            -fx-text-fill: #a9b1c7;
            -fx-padding: 0 0 8px 5px;
        }
        
        /* 磨砂玻璃质感面板 */
        .panel {
            -fx-background-color: rgba(31, 36, 51, 0.7); /* 半透明背景 */
            -fx-background-radius: 6;
            -fx-border-radius: 6;
            -fx-border-color: rgba(0, 190, 255, 0.25);
            -fx-border-width: 1;
            -fx-padding: 15px;
            -fx-effect: innershadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);
             /* backdrop-filter is not a standard CSS property in JavaFX, but this sets up for future possibilities */
        }

        .text-field {
            -fx-background-color: rgba(16, 18, 27, 0.8);
            -fx-text-fill: #cdd3d6;
            -fx-prompt-text-fill: #6a7384;
            -fx-border-color: #3e4964;
            -fx-border-width: 1;
            -fx-border-radius: 4;
            -fx-font-size: 14px;
            -fx-padding: 8px;
            -fx-highlight-fill: rgba(0, 170, 255, 0.5);
            -fx-transition: -fx-border-color 0.2s ease-in-out, -fx-effect 0.2s ease-in-out;
        }
        .text-field:focused {
            -fx-border-color: #00aaff;
            -fx-effect: dropshadow(gaussian, rgba(0, 170, 255, 0.4), 8, 0, 0, 0);
        }

        .list-view { -fx-background-color: transparent; }

        .list-cell {
            -fx-background-color: transparent;
            -fx-text-fill: #a9b1c7;
            -fx-padding: 12px 10px;
            -fx-font-size: 14px;
            -fx-background-radius: 4;
            -fx-border-radius: 4;
            -fx-transition: -fx-background-color 0.2s ease-in-out, -fx-text-fill 0.2s ease-in-out;
        }

        .list-cell:filled:hover {
            -fx-background-color: rgba(0, 170, 255, 0.1);
            -fx-text-fill: #ffffff;
        }

        .list-cell:filled:selected {
            -fx-background-color: rgba(0, 170, 255, 0.2);
            -fx-text-fill: #ffffff;
            -fx-font-weight: bold;
        }
        
        .list-view:focused .list-cell:filled:selected {
             -fx-border-color: #00aaff;
             -fx-border-width: 0 0 0 3;
        }

        /* --- 滚动条样式优化 --- */
        .list-view .scroll-bar:vertical { -fx-background-color: transparent; -fx-padding: 2; }
        .list-view .scroll-bar:vertical .track { -fx-background-color: transparent; -fx-border-color: rgba(0, 190, 255, 0.15); -fx-border-width: 1 0 1 0; -fx-background-radius: 5; -fx-border-radius: 5; }
        .list-view .scroll-bar:vertical .thumb { -fx-background-color: #3e4964; -fx-background-radius: 5; }
        .list-view .scroll-bar:vertical .thumb:hover { -fx-background-color: #00aaff; }
        .list-view .scroll-bar:vertical .increment-button, .list-view .scroll-bar:vertical .decrement-button { -fx-padding: 0; -fx-background-color: transparent; }
        .list-view .scroll-bar .increment-arrow, .list-view .scroll-bar .decrement-arrow { -fx-pref-height: 0; }


        /* --- 按钮和图标 --- */
        .button {
             -fx-background-color: linear-gradient(to bottom, #394056, #242938);
             -fx-text-fill: #cdd3d6;
             -fx-font-size: 14px;
             -fx-font-weight: bold;
             -fx-border-color: #505c7c;
             -fx-border-width: 1;
             -fx-background-radius: 4; -fx-border-radius: 4;
             -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0, 1, 2);
             -fx-transition: all 0.2s ease-in-out;
             -fx-cursor: hand;
        }
        .button:hover {
            -fx-border-color: #00aaff;
            -fx-effect: dropshadow(gaussian, rgba(0, 170, 255, 0.5), 10, 0, 0, 0);
        }
        .button:armed {
            -fx-background-color: #242938;
            -fx-effect: innershadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 0);
        }
        
        .action-button { -fx-min-width: 50; -fx-min-height: 40; }
        .bottom-button { -fx-padding: 10 25; }
        
        /* 图标样式 */
        .action-button .svg-path { -fx-fill: #a9b1c7; }
        .action-button:hover .svg-path { -fx-fill: #ffffff; }

        #save-button {
             -fx-border-color: #00aaff;
             -fx-background-color: linear-gradient(to bottom, #0077aa, #005588);
             -fx-text-fill: white;
        }
        #save-button:hover {
             -fx-effect: dropshadow(gaussian, #00aaff, 15, 0, 0, 0);
        }
        """;

    // 辅助方法，用于创建SVG图标
    private static SVGPath createIcon(String path) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.getStyleClass().add("svg-path");
        return svg;
    }

    public static void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);
        window.setTitle("配置英雄优先级");

        // --- Data Initialization ---
        ObservableList<String> priorityHeroes = FXCollections.observableArrayList(Config.heroNames);
        ObservableList<String> availableHeroes = FXCollections.observableArrayList();
        boolean allChampionsAvailable = (Summoner.allChampions != null && !Summoner.allChampions.isEmpty());
        if (allChampionsAvailable) {
            List<String> allHeroNames = new ArrayList<>(Summoner.allChampions.keySet());
            allHeroNames.removeAll(priorityHeroes);
            availableHeroes.addAll(allHeroNames);
        }
        FXCollections.sort(availableHeroes);

        // --- UI Components (Refactored to create nodes directly) ---
        Label titleLabel = new Label("英雄优先级配置");
        titleLabel.getStyleClass().add("window-title");

        // Left Pane: All Heroes
        TextField searchField = new TextField();
        searchField.setPromptText("搜索英雄...");
        ListView<String> availableListView = new ListView<>();
        VBox availableContent = new VBox(15, new Label("所有英雄"), searchField, availableListView);
        availableContent.getChildren().forEach(node -> {
            if (node instanceof Label) node.getStyleClass().add("column-header");
            if (node instanceof ListView) VBox.setVgrow(node, Priority.ALWAYS);
        });
        VBox availableBox = new VBox(availableContent);
        availableBox.getStyleClass().add("panel");
        availableBox.setDisable(!allChampionsAvailable);
        VBox.setVgrow(availableContent, Priority.ALWAYS);

        // Right Pane: Priority List
        ListView<String> priorityListView = new ListView<>();
        VBox priorityContent = new VBox(15, new Label("优先级列表 (上 > 下)"), priorityListView);
        priorityContent.getChildren().forEach(node -> {
            if (node instanceof Label) node.getStyleClass().add("column-header");
            if (node instanceof ListView) VBox.setVgrow(node, Priority.ALWAYS);
        });
        VBox priorityBox = new VBox(priorityContent);
        priorityBox.getStyleClass().add("panel");
        VBox.setVgrow(priorityContent, Priority.ALWAYS);

        // Reorder Buttons for Priority List
        Button upButton = new Button("", createIcon("M7.5 4 L2.5 9 L12.5 9 Z")); // ▲
        Button downButton = new Button("", createIcon("M7.5 11 L2.5 6 L12.5 6 Z")); // ▼
        upButton.getStyleClass().add("action-button");
        downButton.getStyleClass().add("action-button");
        VBox reorderBox = new VBox(10, upButton, downButton);
        reorderBox.setAlignment(Pos.CENTER);
        reorderBox.setPadding(new Insets(0, 0, 0, 15));

        // Combined Right Section
        HBox prioritySection = new HBox(0, priorityBox, reorderBox);
        HBox.setHgrow(priorityBox, Priority.ALWAYS);


        // Center Pane: Transfer Buttons
        Button addAllButton = new Button("", createIcon("M0 5 L5 0 L5 10 Z M6 5 L11 0 L11 10 Z")); // ⏩
        Button addButton = new Button("", createIcon("M0 0 L8 5 L0 10 Z")); // ▶
        Button removeButton = new Button("", createIcon("M8 0 L0 5 L8 10 Z")); // ◀
        Button removeAllButton = new Button("", createIcon("M11 5 L6 0 L6 10 Z M5 5 L0 0 L0 10 Z")); // ⏪
        VBox transferBox = new VBox(10, addAllButton, addButton, removeButton, removeAllButton);
        transferBox.setAlignment(Pos.CENTER);
        transferBox.getChildren().forEach(node -> node.getStyleClass().add("action-button"));
        transferBox.setDisable(!allChampionsAvailable);

        // Bottom Pane: Action Buttons
        Button saveButton = new Button("保存并应用");
        saveButton.setId("save-button");
        Button cancelButton = new Button("取消");
        saveButton.getStyleClass().add("bottom-button");
        cancelButton.getStyleClass().add("bottom-button");
        HBox bottomBox = new HBox(20, saveButton, cancelButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 10, 0));

        // --- Layout Assembly ---
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(25, 0, 20, 0));

        HBox mainContent = new HBox(15, availableBox, transferBox, prioritySection);
        HBox.setHgrow(availableBox, Priority.ALWAYS);
        HBox.setHgrow(prioritySection, Priority.ALWAYS);

        BorderPane rootLayout = new BorderPane();
        rootLayout.setTop(titleBox);
        rootLayout.setCenter(mainContent);
        rootLayout.setBottom(bottomBox);
        rootLayout.setPadding(new Insets(10, 25, 20, 25));
        rootLayout.getStyleClass().add("root");
        rootLayout.setEffect(new DropShadow(30, Color.BLACK));

        // --- Scene and Stage Setup ---
        Scene scene = new Scene(rootLayout);
        scene.setFill(Color.TRANSPARENT);
        try {
            String encodedCSS = URLEncoder.encode(CSS_STYLE, StandardCharsets.UTF_8).replace("+", "%20");
            scene.getStylesheets().add("data:text/css," + encodedCSS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Data Binding & Event Handling ---
        FilteredList<String> filteredAvailableHeroes = new FilteredList<>(availableHeroes, p -> true);
        SortedList<String> sortedAvailableHeroes = new SortedList<>(filteredAvailableHeroes, Comparator.naturalOrder());
        availableListView.setItems(sortedAvailableHeroes);
        priorityListView.setItems(priorityHeroes);

        searchField.textProperty().addListener((obs, oldV, newV) -> filteredAvailableHeroes.setPredicate(hero ->
                newV == null || newV.isEmpty() || hero.toLowerCase().contains(newV.toLowerCase())));

        addButton.setOnAction(e -> { // Add
            String selected = availableListView.getSelectionModel().getSelectedItem();
            if (selected != null) { availableHeroes.remove(selected); priorityHeroes.add(selected); }
        });
        removeButton.setOnAction(e -> { // Remove
            String selected = priorityListView.getSelectionModel().getSelectedItem();
            if (selected != null) { priorityHeroes.remove(selected); availableHeroes.add(selected); FXCollections.sort(availableHeroes); }
        });
        addAllButton.setOnAction(e -> { // Add All
            List<String> toAdd = new ArrayList<>(availableListView.getItems());
            priorityHeroes.addAll(toAdd); availableHeroes.removeAll(toAdd);
        });
        removeAllButton.setOnAction(e -> { // Remove All
            availableHeroes.addAll(priorityHeroes); priorityHeroes.clear(); FXCollections.sort(availableHeroes);
        });

        upButton.setOnAction(e -> { // Move Up
            int index = priorityListView.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                String s = priorityHeroes.remove(index); priorityHeroes.add(index - 1, s);
                priorityListView.getSelectionModel().select(index - 1);
            }
        });
        downButton.setOnAction(e -> { // Move Down
            int index = priorityListView.getSelectionModel().getSelectedIndex();
            if (index != -1 && index < priorityHeroes.size() - 1) {
                String s = priorityHeroes.remove(index); priorityHeroes.add(index + 1, s);
                priorityListView.getSelectionModel().select(index + 1);
            }
        });

        saveButton.setOnAction(e -> { Config.heroNames = new ArrayList<>(priorityHeroes); window.close(); });
        cancelButton.setOnAction(e -> window.close());

        // Window Dragging
        final double[] xOffset = {0}, yOffset = {0};
        rootLayout.setOnMousePressed(event -> { xOffset[0] = event.getSceneX(); yOffset[0] = event.getSceneY(); });
        rootLayout.setOnMouseDragged(event -> { window.setX(event.getScreenX() - xOffset[0]); window.setY(event.getScreenY() - yOffset[0]); });

        window.setScene(scene);
        window.showAndWait();
    }
}