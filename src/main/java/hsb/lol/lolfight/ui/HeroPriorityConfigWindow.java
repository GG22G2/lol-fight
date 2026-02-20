package hsb.lol.lolfight.ui;

import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.data.Summoner;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 英雄优先级配置窗口
 * 
 * 功能说明：
 * - 左侧显示所有可用英雄，点击可添加到右侧优先级列表
 * - 右侧显示已选英雄，支持拖拽排序，点击可移回左侧
 * - 使用自定义鼠标事件实现拖拽，避免 JavaFX DragBoard 机制的滚动限制
 */
public class HeroPriorityConfigWindow {

    // ==================== CSS 样式定义 ====================
    
    private static final String CSS_STYLE = """
        .root {
            -fx-background-color: #1D2033;
            -fx-font-family: 'Segoe UI', 'Microsoft YaHei UI', system-ui, sans-serif;
            -fx-border-color: #3E4257;
            -fx-border-width: 1px;
            -fx-border-radius: 10px;
            -fx-background-radius: 10px;
        }

        .window-title {
            -fx-font-size: 18px;
            -fx-font-weight: 600;
            -fx-text-fill: #FFFFFF;
        }

        .title-underline {
            -fx-min-height: 1px;
            -fx-max-height: 1px;
            -fx-background-color: #3E4257;
            -fx-opacity: 0.5;
            -fx-max-width: 240px;
        }

        .column-header {
            -fx-font-size: 13px;
            -fx-font-weight: 500;
            -fx-text-fill: #939292;
            -fx-padding: 2px 0 6px 0;
        }

        .panel {
            -fx-background-color: #282B3D;
            -fx-background-radius: 6px;
            -fx-border-radius: 6px;
            -fx-border-color: transparent;
            -fx-border-width: 0;
            -fx-padding: 10px;
        }

        .text-field {
            -fx-background-color: #1D2033;
            -fx-text-fill: #FFFFFF;
            -fx-prompt-text-fill: #939292;
            -fx-border-color: #3E4257;
            -fx-border-width: 1px;
            -fx-border-radius: 6px;
            -fx-background-radius: 6px;
            -fx-font-size: 13px;
            -fx-padding: 7px 10px;
            -fx-highlight-fill: #00CC74;
            -fx-cursor: text;
        }
        .text-field:focused {
            -fx-border-color: #00CC74;
        }

        .hero-grid {
            -fx-background-color: transparent;
            -fx-padding: 0;
        }

        .hero-card {
            -fx-background-color: #1D2033;
            -fx-background-radius: 6px;
            -fx-border-radius: 6px;
            -fx-border-color: transparent;
            -fx-border-width: 1px;
            -fx-padding: 8px;
            -fx-cursor: hand;
        }
        .hero-card:hover {
            -fx-background-color: #3E4257;
        }
        .hero-card:pressed {
            -fx-background-color: #32364A;
        }

        .hero-avatar {
        }

        .hero-name {
            -fx-font-size: 11px;
            -fx-font-weight: 400;
            -fx-text-fill: #FFFFFF;
            -fx-text-alignment: center;
        }

        .priority-badge {
            -fx-background-color: #00CC74;
            -fx-background-radius: 6px;
            -fx-text-fill: #1D2033;
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
            -fx-padding: 0 0 0 2px;
        }
        .scroll-pane .scroll-bar:vertical .track {
            -fx-background-color: transparent;
        }
        .scroll-pane .scroll-bar:vertical .thumb {
            -fx-background-color: #3E4257;
            -fx-background-radius: 6px;
            -fx-min-width: 6px;
            -fx-pref-width: 6px;
        }
        .scroll-pane .scroll-bar:vertical .thumb:hover {
            -fx-background-color: #939292;
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
            -fx-background-color: #282B3D;
            -fx-text-fill: #FFFFFF;
            -fx-font-size: 12px;
            -fx-font-weight: 500;
            -fx-border-color: #3E4257;
            -fx-border-width: 1px;
            -fx-background-radius: 6px;
            -fx-border-radius: 6px;
            -fx-cursor: hand;
            -fx-padding: 6px 12px;
        }
        .button:hover {
            -fx-background-color: #3E4257;
        }
        .button:pressed {
            -fx-background-color: #32364A;
        }

        .button-danger {
            -fx-text-fill: #FCA5A5;
            -fx-border-color: #7F1D1D;
        }
        .button-danger:hover {
            -fx-text-fill: #FEE2E2;
            -fx-background-color: #7F1D1D;
        }

        .button-primary {
            -fx-text-fill: #FFFFFF;
            -fx-border-color: #00CC74;
            -fx-background-color: #00CC74;
        }
        .button-primary:hover {
            -fx-background-color: #00AA62;
            -fx-border-color: #00AA62;
        }
        """;

    // 占位头像 SVG 数据（LOL 风格的占位图标）
    private static final String PLACEHOLDER_SVG = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Cdefs%3E%3ClinearGradient id='grad1' x1='0%25' y1='0%25' x2='100%25' y2='100%25'%3E%3Cstop offset='0%25' style='stop-color:%23282B3D;stop-opacity:1' /%3E%3Cstop offset='100%25' style='stop-color:%231D2033;stop-opacity:1' /%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width='100' height='100' fill='url(%23grad1)'/%3E%3Ccircle cx='50' cy='50' r='35' fill='none' stroke='%2300CC74' stroke-width='3'/%3E%3Ccircle cx='50' cy='42' r='12' fill='%2300CC74' opacity='0.6'/%3E%3Cpath d='M 25 75 Q 50 55 75 75' fill='none' stroke='%2300CC74' stroke-width='3' opacity='0.6'/%3E%3C/svg%3E";

    // ==================== 常量定义 ====================
    
    private static final int HERO_CARD_SIZE = 85;
    private static final int AVATAR_SIZE = 55;
    private static final int CARD_GAP = 8;
    private static final double DRAG_DISTANCE_THRESHOLD = 15.0;
    private static final double DRAG_DELAY_MS = 300;

    // ==================== UI 组件引用 ====================
    
    private static ObservableList<String> priorityHeroes;    // 优先级列表中的英雄
    private static ObservableList<String> availableHeroes;   // 可用英雄列表
    private static FilteredList<String> filteredHeroes;      // 过滤后的可用英雄
    private static FlowPane priorityFlowPane;                // 右侧优先级卡片容器
    private static FlowPane availableFlowPane;               // 左侧可用卡片容器
    private static TextField searchField;                    // 搜索输入框
    private static ScrollPane rightScrollPane;               // 右侧滚动容器
    private static StackPane priorityStack;                  // 右侧 StackPane（包含 FlowPane 和插入线）
    private static Rectangle insertLine;                     // 拖拽时的插入位置指示线

    // ==================== 拖拽状态变量 ====================
    
    private static boolean isDragging = false;           // 是否处于拖拽模式
    private static String draggedHeroName = null;        // 当前拖拽的英雄名称
    private static VBox draggedCard = null;              // 当前拖拽的原始卡片
    private static VBox dragPreview = null;              // 拖拽时的预览卡片
    private static int insertIndex = -1;                 // 当前插入位置索引

    // ==================== 定时器相关变量 ====================
    
    private static double pressX = 0;                    // 鼠标按下时的 X 坐标
    private static double pressY = 0;                    // 鼠标按下时的 Y 坐标
    private static PauseTransition dragTimer;            // 长按定时器
    private static boolean timerTriggered = false;       // 定时器是否已触发

    /**
     * 显示英雄优先级配置窗口
     * 
     * 这是窗口的入口方法，负责：
     * 1. 初始化数据（从配置加载已选英雄，从召唤师数据加载所有英雄）
     * 2. 构建左右两栏 UI
     * 3. 绑定事件处理器
     * 4. 显示模态窗口
     */
    public static void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);
        window.setTitle("英雄优先级配置");

        // 初始化数据
        priorityHeroes = FXCollections.observableArrayList(Config.heroNames);
        availableHeroes = FXCollections.observableArrayList();
        boolean allChampionsAvailable = (Summoner.allChampions != null && !Summoner.allChampions.isEmpty());
        if (allChampionsAvailable) {
            List<String> allHeroNames = new ArrayList<>(Summoner.allChampions.keySet());
            allHeroNames.removeAll(priorityHeroes);
            availableHeroes.addAll(allHeroNames);
        }
        FXCollections.sort(availableHeroes);

        // 构建标题区域
        Label titleLabel = new Label("英雄优先级配置");
        titleLabel.getStyleClass().add("window-title");
        Region titleUnderline = new Region();
        titleUnderline.getStyleClass().add("title-underline");
        VBox titleContainer = new VBox(6, titleLabel, titleUnderline);
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setPadding(new Insets(12, 0, 8, 0));

        // 构建左侧面板（可用英雄列表）
        Label leftHeaderLabel = new Label("全部英雄");
        leftHeaderLabel.getStyleClass().add("column-header");

        searchField = new TextField();
        searchField.setPromptText("搜索英雄名称...");
        searchField.getStyleClass().add("text-field");

        availableFlowPane = new FlowPane();
        availableFlowPane.setHgap(CARD_GAP);
        availableFlowPane.setVgap(CARD_GAP);
        availableFlowPane.setPadding(new Insets(4, 4, 4, 4));
        availableFlowPane.getStyleClass().add("hero-grid");
        refreshAvailableFlowPane(availableHeroes);

        ScrollPane leftScrollPane = new ScrollPane(availableFlowPane);
        leftScrollPane.getStyleClass().add("scroll-pane");
        leftScrollPane.setFitToWidth(true);
        leftScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScrollPane.setPrefViewportHeight(420);

        VBox leftPanel = new VBox(8, leftHeaderLabel, searchField, leftScrollPane);
        leftPanel.getStyleClass().add("panel");
        VBox.setVgrow(leftScrollPane, Priority.ALWAYS);
        leftPanel.setDisable(!allChampionsAvailable);

        // 搜索过滤功能
        filteredHeroes = new FilteredList<>(availableHeroes, p -> true);
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            filteredHeroes.setPredicate(hero ->
                newV == null || newV.isEmpty() || hero.toLowerCase().contains(newV.toLowerCase()));
            refreshAvailableFlowPane(filteredHeroes);
        });

        // 构建右侧面板（优先级列表）
        Label rightHeaderLabel = new Label("优先级列表");
        rightHeaderLabel.getStyleClass().add("column-header");

        Button clearButton = new Button("清空已选");
        clearButton.getStyleClass().addAll("button", "button-danger");
        clearButton.setPadding(new Insets(6, 12, 6, 12));

        HBox rightHeader = new HBox(8, rightHeaderLabel, clearButton);
        rightHeader.setAlignment(Pos.CENTER_LEFT);

        // 创建优先级面板容器
        priorityStack = new StackPane();
        priorityStack.setAlignment(Pos.TOP_LEFT);

        priorityFlowPane = new FlowPane();
        priorityFlowPane.setHgap(CARD_GAP);
        priorityFlowPane.setVgap(CARD_GAP);
        priorityFlowPane.setPadding(new Insets(4, 4, 4, 4));
        priorityFlowPane.getStyleClass().add("hero-grid");

        // 创建插入位置指示线
        insertLine = new Rectangle(3, 90, Color.web("#00CC74"));
        insertLine.setArcWidth(2);
        insertLine.setArcHeight(2);
        insertLine.setVisible(false);
        insertLine.setManaged(false);

        priorityStack.getChildren().addAll(priorityFlowPane, insertLine);

        refreshPriorityFlowPane();

        // 创建右侧滚动容器
        rightScrollPane = new ScrollPane(priorityStack);
        rightScrollPane.getStyleClass().add("scroll-pane");
        rightScrollPane.setFitToWidth(true);
        rightScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScrollPane.setPrefViewportHeight(400);

        // 设置右侧滚动容器的事件处理器
        setupRightScrollPaneEvents();

        VBox rightPanel = new VBox(10, rightHeader, rightScrollPane);
        rightPanel.getStyleClass().add("panel");
        VBox.setVgrow(rightScrollPane, Priority.ALWAYS);

        // 清空按钮事件
        clearButton.setOnAction(e -> {
            availableHeroes.addAll(priorityHeroes);
            priorityHeroes.clear();
            FXCollections.sort(availableHeroes);
            refreshPriorityFlowPane();
            refreshAvailableFlowPaneByFilter();
        });

        // 构建主内容区域（左右两栏）
        HBox mainContent = new HBox(14, leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // 构建底部按钮区域
        Button saveButton = new Button("保存并应用");
        saveButton.getStyleClass().addAll("button", "button-primary");
        Button cancelButton = new Button("取消");
        cancelButton.getStyleClass().add("button");

        HBox bottomBox = new HBox(10, saveButton, cancelButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10, 0, 6, 0));

        // 构建根布局
        BorderPane rootLayout = new BorderPane();
        rootLayout.setTop(titleContainer);
        rootLayout.setCenter(mainContent);
        rootLayout.setBottom(bottomBox);
        rootLayout.setPadding(new Insets(0, 16, 12, 16));
        rootLayout.getStyleClass().add("root");
        rootLayout.setEffect(new DropShadow(30, Color.BLACK));

        // 创建场景
        Scene scene = new Scene(rootLayout, 760, 560);
        scene.setFill(Color.TRANSPARENT);
        try {
            String encodedCSS = URLEncoder.encode(CSS_STYLE, StandardCharsets.UTF_8).replace("+", "%20");
            scene.getStylesheets().add("data:text/css," + encodedCSS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 保存按钮事件
        saveButton.setOnAction(e -> {
            Config.heroNames = new ArrayList<>(priorityHeroes);
            Config.save();
            window.close();
        });
        cancelButton.setOnAction(e -> window.close());

        // 窗口拖拽功能（在标题栏区域拖动可移动窗口）
        final double[] xOffset = {0}, yOffset = {0};
        final boolean[] canDragWindow = {false};
        
        titleContainer.setOnMousePressed(event -> {
            if (!isDragging && event.getButton() == MouseButton.PRIMARY) {
                canDragWindow[0] = true;
                xOffset[0] = event.getSceneX();
                yOffset[0] = event.getSceneY();
                event.consume();
            }
        });
        
        titleContainer.setOnMouseReleased(event -> {
            canDragWindow[0] = false;
        });
        
        rootLayout.setOnMouseDragged(event -> {
            if (!isDragging && canDragWindow[0] && event.getButton() == MouseButton.PRIMARY) {
                window.setX(event.getScreenX() - xOffset[0]);
                window.setY(event.getScreenY() - yOffset[0]);
                event.consume();
            }
        });
        
        rootLayout.setOnMouseReleased(event -> {
            canDragWindow[0] = false;
        });

        window.setScene(scene);
        window.showAndWait();
    }

    // ==================== 刷新方法 ====================

    /**
     * 刷新左侧可用英雄列表
     * 
     * @param heroes 要显示的英雄名称列表
     */
    private static void refreshAvailableFlowPane(List<String> heroes) {
        availableFlowPane.getChildren().clear();
        for (String heroName : heroes) {
            VBox heroCard = createAvailableHeroCard(heroName);
            availableFlowPane.getChildren().add(heroCard);
        }
    }

    /**
     * 根据当前搜索过滤条件刷新左侧可用英雄列表
     * 
     * 如果搜索框有内容，显示过滤后的结果；否则显示全部可用英雄
     */
    private static void refreshAvailableFlowPaneByFilter() {
        if (searchField != null && searchField.getText() != null && !searchField.getText().isEmpty()) {
            refreshAvailableFlowPane(filteredHeroes);
        } else {
            refreshAvailableFlowPane(availableHeroes);
        }
    }

    /**
     * 刷新右侧优先级列表
     * 
     * 重新创建所有优先级卡片，并更新优先级徽章数字
     */
    private static void refreshPriorityFlowPane() {
        priorityFlowPane.getChildren().clear();
        insertIndex = -1;
        insertLine.setVisible(false);
        for (int i = 0; i < priorityHeroes.size(); i++) {
            String heroName = priorityHeroes.get(i);
            VBox heroCard = createPriorityHeroCard(heroName, i + 1);
            priorityFlowPane.getChildren().add(heroCard);
        }
    }

    // ==================== 卡片创建方法 ====================

    /**
     * 创建英雄卡片的基础 UI 组件
     * 
     * @param heroName 英雄名称
     * @param priority 优先级数字（null 表示不显示徽章）
     * @return 卡片 VBox 组件
     */
    private static VBox createHeroCardBase(String heroName, Integer priority) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(HERO_CARD_SIZE);
        card.setMaxWidth(HERO_CARD_SIZE);
        card.getStyleClass().add("hero-card");

        // 创建头像容器
        StackPane avatarContainer = new StackPane();
        avatarContainer.setPrefSize(AVATAR_SIZE, AVATAR_SIZE);
        avatarContainer.setMaxSize(AVATAR_SIZE, AVATAR_SIZE);

        // 创建头像图片
        ImageView avatar = new ImageView(new Image(PLACEHOLDER_SVG));
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        avatar.getStyleClass().add("hero-avatar");

        // 设置圆角裁剪
        Rectangle clip = new Rectangle(AVATAR_SIZE, AVATAR_SIZE);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        avatar.setClip(clip);

        avatarContainer.getChildren().add(avatar);

        // 添加优先级徽章（如果有）
        if (priority != null && priority > 0) {
            Label priorityBadge = new Label(String.valueOf(priority));
            priorityBadge.getStyleClass().add("priority-badge");
            StackPane.setAlignment(priorityBadge, Pos.TOP_LEFT);
            StackPane.setMargin(priorityBadge, new Insets(-3, 0, 0, -3));
            avatarContainer.getChildren().add(priorityBadge);
        }

        // 创建英雄名称标签
        Label nameLabel = new Label(heroName);
        nameLabel.getStyleClass().add("hero-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(HERO_CARD_SIZE - 12);
        nameLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(avatarContainer, nameLabel);
        return card;
    }

    /**
     * 创建左侧可用英雄卡片
     * 
     * 特点：只有点击事件，点击后移动到右侧优先级列表
     * 
     * @param heroName 英雄名称
     * @return 卡片 VBox 组件
     */
    private static VBox createAvailableHeroCard(String heroName) {
        VBox card = createHeroCardBase(heroName, null);

        // 点击事件：移动到优先级列表
        card.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                System.out.println("[左侧卡片] MouseClicked - 英雄: " + heroName + " → 移动到优先级列表");
                moveToPriority(heroName);
            }
        });

        return card;
    }

    /**
     * 创建右侧优先级英雄卡片
     * 
     * 特点：
     * - 有 MousePressed 事件，用于启动长按定时器
     * - 长按 300ms 后进入拖拽模式
     * - 快速点击（300ms 内释放）则移回左侧
     * 
     * @param heroName 英雄名称
     * @param priority 优先级数字
     * @return 卡片 VBox 组件
     */
    private static VBox createPriorityHeroCard(String heroName, int priority) {
        VBox card = createHeroCardBase(heroName, priority);
        card.setUserData(heroName);
        return card;
    }

    // ==================== 右侧事件处理方法 ====================

    /**
     * 设置右侧滚动容器的事件处理器
     */
    private static void setupRightScrollPaneEvents() {

        rightScrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;

            String heroName = getHeroNameFromEvent(event);
            if (heroName == null) return;

            Node target = (Node) event.getTarget();
            VBox card = null;
            while (target != null && target != rightScrollPane) {
                if (target instanceof VBox && target.getUserData() instanceof String) {
                    card = (VBox) target;
                    break;
                }
                target = target.getParent();
            }
            if (card == null) return;

            pressX = event.getX();
            pressY = event.getY();
            timerTriggered = false;
            draggedCard = card;
            draggedHeroName = heroName;

            System.out.println("[右侧ScrollPane] MousePressed - 英雄: " + heroName + ", 启动定时器");

            if (dragTimer != null) {
                dragTimer.stop();
                dragTimer = null;
            }

            dragTimer = new PauseTransition(Duration.millis(DRAG_DELAY_MS));
            VBox finalCard = card;
            dragTimer.setOnFinished(e -> {
                dragTimer = null;
                System.out.println("[右侧ScrollPane] 定时器触发 - 进入拖拽模式");
                timerTriggered = true;
                startDragMode(finalCard, heroName, event);
            });
            dragTimer.play();
        });

        rightScrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isDragging) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                updateDragPreviewPosition(mouseX, mouseY);
                updateInsertPosition(mouseX, mouseY);
                handleAutoScroll(mouseY);
                return;
            }

            if (dragTimer != null && draggedCard != null) {
                double dx = event.getX() - pressX;
                double dy = event.getY() - pressY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance > DRAG_DISTANCE_THRESHOLD) {
                    dragTimer.stop();
                    dragTimer = null;
                    System.out.println("[右侧ScrollPane] 移动距离超过阈值 - 进入拖拽模式");
                    timerTriggered = true;
                    startDragMode(draggedCard, draggedHeroName, event);
                }
            }
        });

        rightScrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;

            System.out.println("[右侧ScrollPane] MouseReleased - timerTriggered: " + timerTriggered + ", isDragging: " + isDragging);

            if (dragTimer != null) {
                dragTimer.stop();
                dragTimer = null;
                System.out.println("[右侧ScrollPane] 定时器已停止（点击模式）");
                if (draggedHeroName != null) {
                    moveToAvailable(draggedHeroName);
                }
            } else if (isDragging) {
                System.out.println("[右侧ScrollPane] 完成拖拽模式");
                finishDragMode();
            }

            timerTriggered = false;
            draggedCard = null;
            draggedHeroName = null;
        });
    }

    // ==================== 拖拽模式方法 ====================

    /**
     * 进入拖拽模式
     * 
     * 功能：
     * 1. 设置拖拽状态标志
     * 2. 将原卡片设为半透明
     * 3. 创建拖拽预览卡片
     * 4. 显示插入位置指示线
     * 
     * @param card 被拖拽的卡片
     * @param heroName 英雄名称
     * @param event 鼠标事件
     */
    private static void startDragMode(VBox card, String heroName, javafx.scene.input.MouseEvent event) {
        isDragging = true;
        draggedHeroName = heroName;
        draggedCard = card;
        card.setOpacity(0.4);

        // 创建拖拽预览卡片
        dragPreview = createDragPreview(heroName);
        priorityStack.getChildren().add(dragPreview);

        // 更新预览位置和插入位置
        double mouseX = event.getX();
        double mouseY = event.getY();
        updateDragPreviewPosition(mouseX, mouseY);

        insertLine.setVisible(true);
        updateInsertPosition(mouseX, mouseY);
        
        System.out.println("[拖拽模式] 已启动 - 英雄: " + heroName);
    }

    /**
     * 创建拖拽预览卡片
     * 
     * @param heroName 英雄名称
     * @return 预览卡片 VBox 组件
     */
    private static VBox createDragPreview(String heroName) {
        VBox preview = createHeroCardBase(heroName, null);
        preview.setStyle("-fx-border-color: #00CC74; -fx-border-width: 2px;");
        preview.setOpacity(0.85);
        preview.setMouseTransparent(true);
        preview.setManaged(false);
        return preview;
    }

    /**
     * 更新拖拽预览卡片的位置
     * 
     * @param mouseX 鼠标 X 坐标（相对于 ScrollPane）
     * @param mouseY 鼠标 Y 坐标（相对于 ScrollPane）
     */
    private static void updateDragPreviewPosition(double mouseX, double mouseY) {
        if (dragPreview != null) {
            Bounds stackBounds = priorityStack.getBoundsInLocal();
            double scrollOffset = rightScrollPane.getVvalue() * (priorityFlowPane.getHeight() - stackBounds.getHeight());

            double previewX = mouseX - HERO_CARD_SIZE / 2;
            double previewY = mouseY - AVATAR_SIZE / 2 + scrollOffset;

            dragPreview.setLayoutX(previewX);
            dragPreview.setLayoutY(previewY);
            
            System.out.println("[拖拽预览] 位置更新 - X: " + previewX + ", Y: " + previewY);
        }
    }

    /**
     * 更新插入位置指示线
     * 
     * @param mouseX 鼠标 X 坐标（相对于 ScrollPane）
     * @param mouseY 鼠标 Y 坐标（相对于 ScrollPane）
     */
    private static void updateInsertPosition(double mouseX, double mouseY) {
        Bounds viewportBounds = rightScrollPane.getBoundsInLocal();
        double scrollOffset = rightScrollPane.getVvalue() * (priorityFlowPane.getHeight() - viewportBounds.getHeight());

        double flowY = mouseY + scrollOffset;

        int newInsertIndex = calculateInsertIndex(mouseX, flowY);
        if (newInsertIndex != insertIndex) {
            insertIndex = newInsertIndex;
            updateInsertLinePosition(newInsertIndex);
            System.out.println("[插入位置] 更新 - 索引: " + insertIndex);
        }
    }

    /**
     * 处理自动滚动
     * 
     * 当鼠标靠近 ScrollPane 边缘时自动滚动
     * 
     * @param mouseY 鼠标 Y 坐标（相对于 ScrollPane）
     */
    private static void handleAutoScroll(double mouseY) {
        Bounds viewportBounds = rightScrollPane.getBoundsInLocal();
        double viewportHeight = viewportBounds.getHeight();
        double scrollZone = 40;

        if (mouseY < scrollZone) {
            double newValue = rightScrollPane.getVvalue() - 0.02;
            rightScrollPane.setVvalue(Math.max(0, newValue));
            System.out.println("[自动滚动] 向上滚动 - vvalue: " + rightScrollPane.getVvalue());
        } else if (mouseY > viewportHeight - scrollZone) {
            double newValue = rightScrollPane.getVvalue() + 0.02;
            rightScrollPane.setVvalue(Math.min(1, newValue));
            System.out.println("[自动滚动] 向下滚动 - vvalue: " + rightScrollPane.getVvalue());
        }
    }

    /**
     * 完成拖拽模式
     * 
     * 功能：
     * 1. 执行列表重排序
     * 2. 恢复原卡片透明度
     * 3. 移除拖拽预览卡片
     * 4. 重置所有状态变量
     */
    private static void finishDragMode() {
        if (draggedHeroName != null && insertIndex >= 0) {
            int draggedIndex = priorityHeroes.indexOf(draggedHeroName);
            if (draggedIndex != -1) {
                int targetIdx = insertIndex;
                if (draggedIndex != targetIdx && targetIdx >= 0 && targetIdx < priorityHeroes.size()) {
                    System.out.println("[拖拽完成] 重排序 - 从 " + draggedIndex + " 移动到 " + targetIdx);
                    priorityHeroes.remove(draggedIndex);
                    priorityHeroes.add(targetIdx, draggedHeroName);
                    refreshPriorityFlowPane();
                } else if (targetIdx == priorityHeroes.size()) {
                    System.out.println("[拖拽完成] 重排序 - 从 " + draggedIndex + " 移动到末尾");
                    priorityHeroes.remove(draggedIndex);
                    priorityHeroes.add(draggedHeroName);
                    refreshPriorityFlowPane();
                } else {
                    System.out.println("[拖拽完成] 位置未变化 - draggedIndex: " + draggedIndex + ", targetIdx: " + targetIdx);
                }
            }
        }

        // 恢复原卡片透明度
        if (draggedCard != null) {
            draggedCard.setOpacity(1.0);
        }
        
        // 移除拖拽预览卡片
        if (dragPreview != null) {
            priorityStack.getChildren().remove(dragPreview);
        }

        // 重置状态
        insertLine.setVisible(false);
        insertIndex = -1;
        isDragging = false;
        draggedHeroName = null;
        draggedCard = null;
        dragPreview = null;
        
        System.out.println("[拖拽模式] 已退出");
    }

    // ==================== 计算方法 ====================

    /**
     * 根据鼠标事件获取当前点击位置的英雄卡片信息
     *
     * @param event 鼠标事件
     * @return 英雄名称，如果未点击到英雄卡片则返回 null
     */
    private static String getHeroNameFromEvent(MouseEvent event) {
        Node target = (Node) event.getTarget();

        while (target != null && target != rightScrollPane) {
            if (target instanceof VBox && target.getUserData() instanceof String) {
                return (String) target.getUserData();
            }
            target = target.getParent();
        }
        return null;
    }

    /**
     * 计算插入位置索引
     * 
     * 根据鼠标坐标判断应该插入到哪个位置
     * 
     * @param mouseX 鼠标 X 坐标（相对于 FlowPane）
     * @param flowY 鼠标 Y 坐标（已加上滚动偏移）
     * @return 插入位置索引
     */
    private static int calculateInsertIndex(double mouseX, double flowY) {
        List<javafx.scene.Node> children = priorityFlowPane.getChildren();
        int size = children.size();

        if (size == 0) {
            return 0;
        }

        for (int i = 0; i < size; i++) {
            javafx.scene.Node node = children.get(i);
            Bounds bounds = node.getBoundsInParent();

            if (flowY >= bounds.getMinY() && flowY <= bounds.getMaxY()) {
                if (mouseX >= bounds.getMinX() && mouseX <= bounds.getMaxX()) {
                    return i;
                }
            }
        }

        Bounds lastBounds = children.get(size - 1).getBoundsInParent();
        if (flowY > lastBounds.getMaxY()) {
            return size;
        }

        return size;
    }

    /**
     * 更新插入位置指示线的位置
     * 
     * @param index 插入位置索引
     */
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
            Bounds lastBounds = children.get(size - 1).getBoundsInParent();
            insertLine.setLayoutX(lastBounds.getMaxX() + CARD_GAP / 2 - 2);
            insertLine.setLayoutY(lastBounds.getMinY());
            insertLine.setHeight(lastBounds.getHeight());
        } else {
            // 插入到中间
            Bounds targetBounds = children.get(index).getBoundsInParent();
            insertLine.setLayoutX(targetBounds.getMinX() - CARD_GAP / 2 - 2);
            insertLine.setLayoutY(targetBounds.getMinY());
            insertLine.setHeight(targetBounds.getHeight());
        }

        insertLine.setVisible(true);
    }

    // ==================== 移动方法 ====================

    /**
     * 将英雄从左侧移动到右侧优先级列表
     * 
     * @param heroName 英雄名称
     */
    private static void moveToPriority(String heroName) {
        availableHeroes.remove(heroName);
        priorityHeroes.add(heroName);
        refreshPriorityFlowPane();
        refreshAvailableFlowPaneByFilter();
    }

    /**
     * 将英雄从右侧优先级列表移动回左侧
     * 
     * @param heroName 英雄名称
     */
    private static void moveToAvailable(String heroName) {
        priorityHeroes.remove(heroName);
        if (!availableHeroes.contains(heroName)) {
            availableHeroes.add(heroName);
            FXCollections.sort(availableHeroes);
        }
        refreshPriorityFlowPane();
        refreshAvailableFlowPaneByFilter();
    }
}
