package hsb.lol.lolfight.ui;

import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.data.Summoner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HeroPriorityConfigWindow {

    // LOL暗黑电竞风格CSS
    private static final String CSS_STYLE = """
        .root {
            -fx-background-color: #0F172A;
            -fx-background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='60' height='60' viewBox='0 0 60 60'%3E%3Cg fill-rule='evenodd'%3E%3Cg fill='%234FA6FD' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
            -fx-font-family: 'Segoe UI', 'Microsoft YaHei UI', system-ui, sans-serif;
            -fx-border-color: rgba(79, 166, 253, 0.3);
            -fx-border-width: 1px;
            -fx-border-radius: 12px;
            -fx-background-radius: 12px;
        }

        .window-title {
            -fx-font-size: 20px;
            -fx-font-weight: 600;
            -fx-text-fill: linear-gradient(to bottom, #4FA6FD, #2563EB);
            -fx-effect: dropshadow(gaussian, rgba(79, 166, 253, 0.4), 15, 0, 0, 0);
        }

        .title-underline {
            -fx-min-height: 2px;
            -fx-max-height: 2px;
            -fx-background-color: linear-gradient(to right, transparent, #4FA6FD, transparent);
            -fx-opacity: 0.8;
            -fx-max-width: 240px;
        }

        .column-header {
            -fx-font-size: 14px;
            -fx-font-weight: 500;
            -fx-text-fill: #94A3B8;
            -fx-padding: 6px 0 10px 0;
        }

        .panel {
            -fx-background-color: #1E293B;
            -fx-background-radius: 10px;
            -fx-border-radius: 10px;
            -fx-border-color: rgba(79, 166, 253, 0.2);
            -fx-border-width: 1px;
            -fx-padding: 14px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 3);
        }

        .text-field {
            -fx-background-color: #2D3748;
            -fx-text-fill: #F1F5F9;
            -fx-prompt-text-fill: #64748B;
            -fx-border-color: #334155;
            -fx-border-width: 1px;
            -fx-border-radius: 6px;
            -fx-background-radius: 6px;
            -fx-font-size: 13px;
            -fx-padding: 8px 12px;
            -fx-highlight-fill: rgba(79, 166, 253, 0.4);
            -fx-cursor: text;
        }
        .text-field:focused {
            -fx-border-color: #4FA6FD;
            -fx-effect: dropshadow(gaussian, rgba(79, 166, 253, 0.2), 6, 0, 0, 0);
        }

        .hero-grid {
            -fx-background-color: transparent;
            -fx-padding: 6px;
        }

        .hero-card {
            -fx-background-color: #121826;
            -fx-background-radius: 8px;
            -fx-border-radius: 8px;
            -fx-border-color: transparent;
            -fx-border-width: 2px;
            -fx-padding: 8px;
            -fx-cursor: hand;
        }
        .hero-card:hover {
            -fx-border-color: #4FA6FD;
        }
        .hero-card:pressed {
            -fx-background-color: #1E293B;
        }

        .hero-avatar {
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 2);
        }

        .hero-name {
            -fx-font-size: 11px;
            -fx-font-weight: 400;
            -fx-text-fill: #CBD5E1;
            -fx-text-alignment: center;
        }

        .priority-badge {
            -fx-background-color: #4FA6FD;
            -fx-background-radius: 8px;
            -fx-text-fill: white;
            -fx-font-size: 10px;
            -fx-font-weight: 600;
            -fx-padding: 2px 6px;
        }

        .scroll-pane {
            -fx-background-color: transparent;
            -fx-background: transparent;
            -fx-border-color: transparent;
        }
        .scroll-pane > .viewport {
            -fx-background-color: transparent;
        }
        .scroll-pane .scroll-bar:vertical {
            -fx-background-color: transparent;
            -fx-padding: 0 0 0 4px;
        }
        .scroll-pane .scroll-bar:vertical .track {
            -fx-background-color: transparent;
        }
        .scroll-pane .scroll-bar:vertical .thumb {
            -fx-background-color: #334155;
            -fx-background-radius: 6px;
            -fx-min-width: 6px;
            -fx-pref-width: 6px;
        }
        .scroll-pane .scroll-bar:vertical .thumb:hover {
            -fx-background-color: #475569;
        }
        .scroll-pane .scroll-bar:vertical .increment-button,
        .scroll-pane .scroll-bar:vertical .decrement-button {
            -fx-padding: 0;
        }
        .scroll-pane .scroll-bar .increment-arrow,
        .scroll-pane .scroll-bar .decrement-arrow {
            -fx-pref-height: 0;
        }

        .button {
            -fx-background-color: #1E293B;
            -fx-text-fill: #E2E8F0;
            -fx-font-size: 13px;
            -fx-font-weight: 500;
            -fx-border-color: #334155;
            -fx-border-width: 1px;
            -fx-background-radius: 6px;
            -fx-border-radius: 6px;
            -fx-cursor: hand;
            -fx-padding: 8px 16px;
        }
        .button:hover {
            -fx-border-color: #4FA6FD;
            -fx-background-color: #2D3748;
        }
        .button:pressed {
            -fx-background-color: #374151;
        }

        .button-danger {
            -fx-text-fill: #FCA5A5;
            -fx-border-color: #7F1D1D;
        }
        .button-danger:hover {
            -fx-text-fill: #FEE2E2;
            -fx-border-color: #DC2626;
            -fx-background-color: #7F1D1D;
        }

        .button-primary {
            -fx-text-fill: #BFDBFE;
            -fx-border-color: #1E40AF;
        }
        .button-primary:hover {
            -fx-text-fill: #EFF6FF;
            -fx-border-color: #3B82F6;
            -fx-background-color: #1E40AF;
        }
        """;

    // 占位头像SVG数据（LOL风格的占位图标）
    private static final String PLACEHOLDER_SVG = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Cdefs%3E%3ClinearGradient id='grad1' x1='0%25' y1='0%25' x2='100%25' y2='100%25'%3E%3Cstop offset='0%25' style='stop-color:%231E293B;stop-opacity:1' /%3E%3Cstop offset='100%25' style='stop-color:%230F172A;stop-opacity:1' /%3E%3C/linearGradient%3E%3ClinearGradient id='grad2' x1='0%25' y1='0%25' x2='100%25' y2='100%25'%3E%3Cstop offset='0%25' style='stop-color:%234FA6FD;stop-opacity:0.8' /%3E%3Cstop offset='100%25' style='stop-color:%238B5CF6;stop-opacity:0.8' /%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width='100' height='100' fill='url(%23grad1)'/%3E%3Ccircle cx='50' cy='50' r='35' fill='none' stroke='url(%23grad2)' stroke-width='3'/%3E%3Ccircle cx='50' cy='42' r='12' fill='%234FA6FD' opacity='0.6'/%3E%3Cpath d='M 25 75 Q 50 55 75 75' fill='none' stroke='%234FA6FD' stroke-width='3' opacity='0.6'/%3E%3C/svg%3E";

    private static ObservableList<String> priorityHeroes;
    private static ObservableList<String> availableHeroes;
    private static FilteredList<String> filteredHeroes;
    private static FlowPane priorityFlowPane;
    private static FlowPane availableFlowPane;
    private static TextField searchField;
    private static Rectangle insertLine;
    private static int insertIndex = -1;
    private static final int HERO_CARD_SIZE = 85;
    private static final int AVATAR_SIZE = 55;
    private static final int CARD_GAP = 8;

    public static void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);
        window.setTitle("英雄优先级配置");

        // 数据初始化
        priorityHeroes = FXCollections.observableArrayList(Config.heroNames);
        availableHeroes = FXCollections.observableArrayList();
        boolean allChampionsAvailable = (Summoner.allChampions != null && !Summoner.allChampions.isEmpty());
        if (allChampionsAvailable) {
            List<String> allHeroNames = new ArrayList<>(Summoner.allChampions.keySet());
            allHeroNames.removeAll(priorityHeroes);
            availableHeroes.addAll(allHeroNames);
        }
        FXCollections.sort(availableHeroes);

        // 创建标题区域
        Label titleLabel = new Label("英雄优先级配置");
        titleLabel.getStyleClass().add("window-title");
        Region titleUnderline = new Region();
        titleUnderline.getStyleClass().add("title-underline");
        VBox titleContainer = new VBox(6, titleLabel, titleUnderline);
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setPadding(new Insets(18, 0, 12, 0));

        // 左侧面板 - 全部英雄
        Label leftHeaderLabel = new Label("全部英雄");
        leftHeaderLabel.getStyleClass().add("column-header");

        searchField = new TextField();
        searchField.setPromptText("搜索英雄名称...");
        searchField.getStyleClass().add("text-field");

        availableFlowPane = new FlowPane();
        availableFlowPane.setHgap(CARD_GAP);
        availableFlowPane.setVgap(CARD_GAP);
        availableFlowPane.setPadding(new Insets(6, 0, 6, 0));
        availableFlowPane.getStyleClass().add("hero-grid");
        refreshAvailableFlowPane(availableHeroes);

        ScrollPane leftScrollPane = new ScrollPane(availableFlowPane);
        leftScrollPane.getStyleClass().add("scroll-pane");
        leftScrollPane.setFitToWidth(true);
        leftScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScrollPane.setPrefViewportHeight(400);

        VBox leftPanel = new VBox(10, leftHeaderLabel, searchField, leftScrollPane);
        leftPanel.getStyleClass().add("panel");
        VBox.setVgrow(leftScrollPane, Priority.ALWAYS);
        leftPanel.setDisable(!allChampionsAvailable);

        // 搜索过滤
        filteredHeroes = new FilteredList<>(availableHeroes, p -> true);
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            filteredHeroes.setPredicate(hero ->
                newV == null || newV.isEmpty() || hero.toLowerCase().contains(newV.toLowerCase()));
            refreshAvailableFlowPane(filteredHeroes);
        });

        // 右侧面板 - 已选英雄
        Label rightHeaderLabel = new Label("优先级列表 (拖动排序)");
        rightHeaderLabel.getStyleClass().add("column-header");

        Button clearButton = new Button("清空已选");
        clearButton.getStyleClass().addAll("button", "button-danger");

        HBox rightHeader = new HBox();
        rightHeader.setAlignment(Pos.CENTER_LEFT);
        rightHeader.getChildren().addAll(rightHeaderLabel, new Region(), clearButton);
        HBox.setHgrow(rightHeaderLabel, Priority.ALWAYS);

        // 创建优先级面板，使用 StackPane 支持插入线
        StackPane priorityStack = new StackPane();
        priorityStack.setAlignment(Pos.TOP_LEFT);

        priorityFlowPane = new FlowPane();
        priorityFlowPane.setHgap(CARD_GAP);
        priorityFlowPane.setVgap(CARD_GAP);
        priorityFlowPane.setPadding(new Insets(6, 0, 6, 0));
        priorityFlowPane.getStyleClass().add("hero-grid");

        // 插入线
        insertLine = new Rectangle(3, 90, Color.web("#4FA6FD"));
        insertLine.setArcWidth(2);
        insertLine.setArcHeight(2);
        insertLine.setVisible(false);
        insertLine.setManaged(false);

        priorityStack.getChildren().addAll(priorityFlowPane, insertLine);

        refreshPriorityFlowPane();

        // 全局拖拽处理 - 在StackPane上处理
        setupGlobalDragHandlers();

        ScrollPane rightScrollPane = new ScrollPane(priorityStack);
        rightScrollPane.getStyleClass().add("scroll-pane");
        rightScrollPane.setFitToWidth(true);
        rightScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScrollPane.setPrefViewportHeight(400);

        VBox rightPanel = new VBox(10, rightHeader, rightScrollPane);
        rightPanel.getStyleClass().add("panel");
        VBox.setVgrow(rightScrollPane, Priority.ALWAYS);

        // 清空按钮事件
        clearButton.setOnAction(e -> {
            availableHeroes.addAll(priorityHeroes);
            priorityHeroes.clear();
            FXCollections.sort(availableHeroes);
            refreshPriorityFlowPane();
            // 刷新可用列表时保持搜索过滤
            if (searchField != null && searchField.getText() != null && !searchField.getText().isEmpty()) {
                refreshAvailableFlowPane(filteredHeroes);
            } else {
                refreshAvailableFlowPane(availableHeroes);
            }
        });

        // 主内容区域
        HBox mainContent = new HBox(14, leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // 底部按钮
        Button saveButton = new Button("保存并应用");
        saveButton.getStyleClass().addAll("button", "button-primary");
        Button cancelButton = new Button("取消");
        cancelButton.getStyleClass().add("button");

        HBox bottomBox = new HBox(12, saveButton, cancelButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(16, 0, 8, 0));

        // 根布局
        BorderPane rootLayout = new BorderPane();
        rootLayout.setTop(titleContainer);
        rootLayout.setCenter(mainContent);
        rootLayout.setBottom(bottomBox);
        rootLayout.setPadding(new Insets(0, 20, 16, 20));
        rootLayout.getStyleClass().add("root");
        rootLayout.setEffect(new DropShadow(30, Color.BLACK));

        // 创建场景
        Scene scene = new Scene(rootLayout, 780, 580);
        scene.setFill(Color.TRANSPARENT);
        try {
            String encodedCSS = URLEncoder.encode(CSS_STYLE, StandardCharsets.UTF_8).replace("+", "%20");
            scene.getStylesheets().add("data:text/css," + encodedCSS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 按钮事件
        saveButton.setOnAction(e -> {
            Config.heroNames = new ArrayList<>(priorityHeroes);
            Config.save();
            window.close();
        });
        cancelButton.setOnAction(e -> window.close());

        // 窗口拖拽
        final double[] xOffset = {0}, yOffset = {0};
        rootLayout.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });
        rootLayout.setOnMouseDragged(event -> {
            window.setX(event.getScreenX() - xOffset[0]);
            window.setY(event.getScreenY() - yOffset[0]);
        });

        window.setScene(scene);
        window.showAndWait();
    }

    // 刷新可用英雄网格
    private static void refreshAvailableFlowPane(List<String> heroes) {
        availableFlowPane.getChildren().clear();
        for (String heroName : heroes) {
            VBox heroCard = createHeroCard(heroName, false);
            availableFlowPane.getChildren().add(heroCard);
        }
    }

    // 刷新优先级英雄网格
    private static void refreshPriorityFlowPane() {
        priorityFlowPane.getChildren().clear();
        insertIndex = -1;
        insertLine.setVisible(false);
        for (int i = 0; i < priorityHeroes.size(); i++) {
            String heroName = priorityHeroes.get(i);
            VBox heroCard = createHeroCard(heroName, true, i + 1);
            priorityFlowPane.getChildren().add(heroCard);
        }
    }

    // 创建英雄卡片（不带优先级徽章）
    private static VBox createHeroCard(String heroName, boolean isPriority) {
        return createHeroCard(heroName, isPriority, -1);
    }

    // 创建英雄卡片（带优先级徽章）
    private static VBox createHeroCard(String heroName, boolean isPriority, int priority) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(HERO_CARD_SIZE);
        card.setMaxWidth(HERO_CARD_SIZE);
        card.getStyleClass().add("hero-card");

        // 头像容器
        StackPane avatarContainer = new StackPane();
        avatarContainer.setPrefSize(AVATAR_SIZE, AVATAR_SIZE);
        avatarContainer.setMaxSize(AVATAR_SIZE, AVATAR_SIZE);

        // 使用占位头像
        ImageView avatar = new ImageView(new Image(PLACEHOLDER_SVG));
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        avatar.getStyleClass().add("hero-avatar");

        // 圆角裁剪
        Rectangle clip = new Rectangle(AVATAR_SIZE, AVATAR_SIZE);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        avatar.setClip(clip);

        avatarContainer.getChildren().add(avatar);

        // 优先级徽章
        if (isPriority && priority > 0) {
            Label priorityBadge = new Label(String.valueOf(priority));
            priorityBadge.getStyleClass().add("priority-badge");
            StackPane.setAlignment(priorityBadge, Pos.TOP_LEFT);
            StackPane.setMargin(priorityBadge, new Insets(-3, 0, 0, -3));
            avatarContainer.getChildren().add(priorityBadge);
        }

        // 英雄名称
        Label nameLabel = new Label(heroName);
        nameLabel.getStyleClass().add("hero-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(HERO_CARD_SIZE - 12);
        nameLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(avatarContainer, nameLabel);

        // 点击事件 - 在双栏间移动
        card.setOnMouseClicked(event -> {
            if (isPriority) {
                // 从优先级移回可用
                priorityHeroes.remove(heroName);
                if (!availableHeroes.contains(heroName)) {
                    availableHeroes.add(heroName);
                    FXCollections.sort(availableHeroes);
                }
                refreshPriorityFlowPane();
                // 刷新可用列表时保持搜索过滤
                if (searchField != null && searchField.getText() != null && !searchField.getText().isEmpty()) {
                    refreshAvailableFlowPane(filteredHeroes);
                } else {
                    refreshAvailableFlowPane(availableHeroes);
                }
            } else {
                // 从可用移到优先级
                availableHeroes.remove(heroName);
                priorityHeroes.add(heroName);
                refreshPriorityFlowPane();
                // 刷新可用列表时保持搜索过滤
                if (searchField != null && searchField.getText() != null && !searchField.getText().isEmpty()) {
                    refreshAvailableFlowPane(filteredHeroes);
                } else {
                    refreshAvailableFlowPane(availableHeroes);
                }
            }
        });

        // 拖拽功能仅在优先级面板启用
        if (isPriority) {
            setupDragAndDrop(card, heroName);
        }

        return card;
    }

    // 设置拖拽排序
    private static void setupDragAndDrop(VBox card, String heroName) {
        // 拖拽检测开始
        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(heroName);
            db.setContent(content);
            card.setOpacity(0.5);
            event.consume();
        });

        // 拖拽完成
        card.setOnDragDone(event -> {
            card.setOpacity(1.0);
            event.consume();
        });
    }

    // 设置全局拖拽处理
    private static void setupGlobalDragHandlers() {
        // 拖拽经过
        priorityFlowPane.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);

                // 计算插入位置
                double mouseX = event.getX();
                double mouseY = event.getY();

                int newInsertIndex = calculateInsertIndex(mouseX, mouseY);

                if (newInsertIndex != insertIndex) {
                    insertIndex = newInsertIndex;
                    updateInsertLinePosition(newInsertIndex);
                }
            }
            event.consume();
        });

        // 拖拽离开
        priorityFlowPane.setOnDragExited(event -> {
            insertLine.setVisible(false);
            event.consume();
        });

        // 拖拽进入
        priorityFlowPane.setOnDragEntered(event -> {
            if (event.getDragboard().hasString()) {
                insertLine.setVisible(true);
            }
            event.consume();
        });

        // 拖拽释放
        priorityFlowPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String draggedHero = db.getString();
                int draggedIndex = priorityHeroes.indexOf(draggedHero);
                if (draggedIndex != -1 && insertIndex != -1) {
                    int targetIdx = insertIndex;
                    if (draggedIndex < targetIdx) {
                        targetIdx--;
                    }
                    if (draggedIndex != targetIdx && targetIdx >= 0) {
                        priorityHeroes.remove(draggedIndex);
                        priorityHeroes.add(targetIdx, draggedHero);
                        refreshPriorityFlowPane();
                        success = true;
                    }
                }
            }
            insertLine.setVisible(false);
            insertIndex = -1;
            event.setDropCompleted(success);
            event.consume();
        });
    }

    // 计算插入索引
    private static int calculateInsertIndex(double mouseX, double mouseY) {
        List<javafx.scene.Node> children = priorityFlowPane.getChildren();
        int size = children.size();

        if (size == 0) {
            return 0;
        }

        // 先检查是否在第一个卡片之前
        if (!children.isEmpty()) {
            Bounds firstBounds = children.get(0).getBoundsInParent();
            if (mouseX < firstBounds.getMinX() + firstBounds.getWidth() / 2 &&
                mouseY < firstBounds.getMaxY()) {
                return 0;
            }
        }

        // 检查每个卡片的中间位置
        for (int i = 0; i < size; i++) {
            javafx.scene.Node node = children.get(i);
            Bounds bounds = node.getBoundsInParent();

            // 检查是否在这个卡片的左半部分
            if (mouseX >= bounds.getMinX() && mouseX <= bounds.getMinX() + bounds.getWidth() / 2 &&
                mouseY >= bounds.getMinY() && mouseY <= bounds.getMaxY()) {
                return i;
            }

            // 检查是否在这个卡片的右半部分
            if (mouseX > bounds.getMinX() + bounds.getWidth() / 2 && mouseX <= bounds.getMaxX() &&
                mouseY >= bounds.getMinY() && mouseY <= bounds.getMaxY()) {
                return i + 1;
            }
        }

        // 检查是否在最后一行的末尾
        if (!children.isEmpty()) {
            Bounds lastBounds = children.get(size - 1).getBoundsInParent();
            if (mouseX > lastBounds.getMinX() + lastBounds.getWidth() / 2 ||
                mouseY > lastBounds.getMaxY()) {
                return size;
            }
        }

        return size;
    }

    // 更新插入线位置
    private static void updateInsertLinePosition(int index) {
        List<javafx.scene.Node> children = priorityFlowPane.getChildren();
        int size = children.size();

        if (size == 0 || index == 0) {
            // 插入到最前面
            insertLine.setLayoutX(-2);
            insertLine.setLayoutY(6);
            insertLine.setHeight(100);
        } else if (index >= size) {
            // 插入到最后面
            if (!children.isEmpty()) {
                Bounds lastBounds = children.get(size - 1).getBoundsInParent();
                insertLine.setLayoutX(lastBounds.getMaxX() + CARD_GAP / 2 - 2);
                insertLine.setLayoutY(lastBounds.getMinY());
                insertLine.setHeight(lastBounds.getHeight());
            }
        } else {
            // 插入到中间
            Bounds targetBounds = children.get(index).getBoundsInParent();
            insertLine.setLayoutX(targetBounds.getMinX() - CARD_GAP / 2 - 2);
            insertLine.setLayoutY(targetBounds.getMinY());
            insertLine.setHeight(targetBounds.getHeight());
        }

        insertLine.setVisible(true);
    }
}
