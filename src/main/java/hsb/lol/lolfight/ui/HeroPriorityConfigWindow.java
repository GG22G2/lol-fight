package hsb.lol.lolfight.ui;

import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.data.Summoner;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
import javafx.scene.shape.Circle;
import javafx.application.Platform;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 英雄优先级配置窗口 (Next-Gen UI Edition)
 * <p>
 * 全新特性：
 * - 修复 Color 越界报错，事件机制重构，全面恢复完美拖拽体验。
 * - 深色极简科幻质感，悬浮玻璃拟物态。
 * - 纯手写弹性网格引擎 (Elastic Grid Engine)，彻底重构拖拽。
 * - 卡片拖动时，周围卡片平滑丝滑退让（流体动画）。
 * - 释放卡片时精准吸附回位动画。
 */
public class HeroPriorityConfigWindow {

    // ==================== 顶级视觉 CSS ====================
    private static final String CSS_STYLE = """
        .window-root {
            -fx-background-color: transparent;
            -fx-font-family: 'Segoe UI', 'Microsoft YaHei UI', system-ui, sans-serif;
        }

        .main-bg {
            -fx-background-color: linear-gradient(to bottom right, #0F111A, #06070B);
            -fx-background-radius: 16px;
            -fx-border-color: rgba(255, 255, 255, 0.08);
            -fx-border-width: 1px;
            -fx-border-radius: 16px;
        }

        .title-text {
            -fx-font-size: 20px;
            -fx-font-weight: 800;
            -fx-text-fill: linear-gradient(to right, #00F0FF, #00A3FF);
            -fx-effect: dropshadow(gaussian, rgba(0, 240, 255, 0.4), 10, 0.2, 0, 0);
        }

        .subtitle-text {
            -fx-font-size: 14px;
            -fx-font-weight: 600;
            -fx-text-fill: #788299;
            -fx-padding: 0 0 10px 0;
        }

        .glass-panel {
            -fx-background-color: rgba(21, 23, 32, 0.6);
            -fx-background-radius: 12px;
            -fx-border-color: rgba(255, 255, 255, 0.04);
            -fx-border-width: 1px;
            -fx-border-radius: 12px;
            -fx-padding: 16px;
        }

        .search-field {
            -fx-background-color: rgba(0, 0, 0, 0.3);
            -fx-text-fill: #FFFFFF;
            -fx-prompt-text-fill: #4B5263;
            -fx-border-color: rgba(255, 255, 255, 0.08);
            -fx-border-width: 1px;
            -fx-border-radius: 8px;
            -fx-background-radius: 8px;
            -fx-font-size: 13px;
            -fx-padding: 10px 14px;
        }
        .search-field:focused {
            -fx-border-color: #00A3FF;
            -fx-background-color: rgba(0, 163, 255, 0.05);
        }

        .hero-card {
            -fx-background-color: rgba(255, 255, 255, 0.02);
            -fx-background-radius: 10px;
            -fx-border-color: rgba(255, 255, 255, 0.05);
            -fx-border-width: 1px;
            -fx-border-radius: 10px;
            -fx-cursor: hand;
        }
        
        .hero-name {
            -fx-font-size: 12px;
            -fx-font-weight: 600;
            -fx-text-fill: #D1D5E0;
            -fx-alignment: center;
        }

        .priority-badge {
            -fx-background-color: #00F0FF;
            -fx-background-radius: 10px;
            -fx-text-fill: #06070B;
            -fx-font-size: 11px;
            -fx-font-weight: 800;
            -fx-padding: 1px 6px;
            -fx-effect: dropshadow(gaussian, rgba(0, 240, 255, 0.6), 6, 0.4, 0, 0);
        }

        /* 极致纤细的隐藏式滚动条 */
        .custom-scroll {
            -fx-background-color: transparent;
            -fx-background: transparent;
        }
        .custom-scroll .viewport { -fx-background-color: transparent; }
        .custom-scroll .scroll-bar:vertical {
            -fx-background-color: transparent;
            -fx-pref-width: 4px;
        }
        .custom-scroll .scroll-bar:vertical .track { -fx-background-color: transparent; }
        .custom-scroll .scroll-bar:vertical .thumb {
            -fx-background-color: rgba(255, 255, 255, 0.15);
            -fx-background-radius: 4px;
        }
        .custom-scroll .scroll-bar:vertical .thumb:hover { -fx-background-color: rgba(0, 240, 255, 0.5); }
        .custom-scroll .scroll-bar:vertical .increment-button,
        .custom-scroll .scroll-bar:vertical .decrement-button { -fx-padding: 0; }
        .custom-scroll .scroll-bar .increment-arrow,
        .custom-scroll .scroll-bar .decrement-arrow { -fx-pref-height: 0; }

        .btn-modern {
            -fx-background-color: rgba(255, 255, 255, 0.05);
            -fx-text-fill: #FFFFFF;
            -fx-font-size: 13px;
            -fx-font-weight: 600;
            -fx-background-radius: 8px;
            -fx-border-radius: 8px;
            -fx-border-color: rgba(255, 255, 255, 0.1);
            -fx-border-width: 1px;
            -fx-padding: 8px 20px;
            -fx-cursor: hand;
        }
        .btn-modern:hover {
            -fx-background-color: rgba(255, 255, 255, 0.1);
        }

        .btn-primary {
            -fx-background-color: linear-gradient(to right, #00A3FF, #0057FF);
            -fx-border-color: transparent;
            -fx-text-fill: #FFFFFF;
        }
        .btn-primary:hover {
            -fx-background-color: linear-gradient(to right, #1AB0FF, #1A6CFF);
            -fx-effect: dropshadow(gaussian, rgba(0, 163, 255, 0.5), 10, 0.2, 0, 0);
        }
        
        .btn-danger {
            -fx-text-fill: #FF4565;
            -fx-background-color: rgba(255, 69, 101, 0.05);
            -fx-border-color: rgba(255, 69, 101, 0.2);
        }
        .btn-danger:hover {
            -fx-background-color: rgba(255, 69, 101, 0.15);
            -fx-border-color: #FF4565;
        }
        
        .close-btn {
            -fx-background-color: transparent;
            -fx-cursor: hand;
            -fx-padding: 5px;
        }
        .close-btn:hover {
            -fx-background-color: rgba(255, 69, 101, 0.2);
            -fx-background-radius: 6px;
        }
        """;

    private static final String SVG_AVATAR = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Cdefs%3E%3ClinearGradient id='g' x1='0%25' y1='0%25' x2='100%25' y2='100%25'%3E%3Cstop offset='0%25' stop-color='%231E2333'/%3E%3Cstop offset='100%25' stop-color='%230B0D14'/%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width='100' height='100' fill='url(%23g)'/%3E%3Ccircle cx='50' cy='40' r='18' fill='none' stroke='%2300A3FF' stroke-width='2' opacity='0.5'/%3E%3Cpath d='M 20 85 Q 50 50 80 85' fill='none' stroke='%2300A3FF' stroke-width='2' opacity='0.5'/%3E%3C/svg%3E";

    // ==================== 布局参数 ====================
    private static final double CARD_W = 80;
    private static final double CARD_H = 110;
    private static final double AVATAR_SIZE = 56;
    private static final double GRID_GAP = 12;

    // ==================== 实例组件 ====================
    private Stage window;
    private ObservableList<String> priorityHeroes;
    private ObservableList<String> availableHeroes;
    private FilteredList<String> filteredHeroes;

    // 核心面板
    private FlowPane availableFlowPane;
    private Pane priorityPane; // 纯手写计算坐标的弹性面板
    private TextField searchField;
    private ScrollPane rightScrollPane;
    private Pane overlayPane;

    // 拖拽状态系
    private boolean isDragging = false;
    private boolean isAnimatingDrop = false; // 防并发锁：防止动画吸附期间点击引发错位
    private String draggedHeroName = null;
    private VBox draggedSourceCard = null; // 原位隐身的卡片
    private VBox dragPreview = null;       // 悬浮层飞行的卡片
    private int currentPlaceholderIndex = -1; // 拖动时当前让位出来的索引
    private List<VBox> activePriorityCards = new ArrayList<>(); // 保存右侧真实存在的UI节点

    private double pressX = 0;
    private double pressY = 0;
    private PauseTransition dragTimer;

    public static void display() {
        new HeroPriorityConfigWindow().showWindow();
    }

    private void showWindow() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);

        initData();

        // 顶级外壳，预留给发光阴影
        StackPane rootLayer = new StackPane();
        rootLayer.getStyleClass().add("window-root");
        rootLayer.setPadding(new Insets(20));

        // 实际内容窗口背景
        BorderPane mainContainer = new BorderPane();
        mainContainer.getStyleClass().add("main-bg");
        DropShadow windowShadow = new DropShadow(40, Color.rgb(0, 0, 0, 0.8));
        windowShadow.setOffsetY(15);
        mainContainer.setEffect(windowShadow);

        // 顶栏 (可拖拽区域)
        HBox topBar = createTopBar();
        mainContainer.setTop(topBar);

        // 中心两栏
        VBox leftPanel = buildLeftPanel();
        VBox rightPanel = buildRightPanel();

        HBox centerContent = new HBox(20, leftPanel, rightPanel);
        centerContent.setPadding(new Insets(0, 24, 0, 24));
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        leftPanel.setPrefWidth(320);
        rightPanel.setPrefWidth(440);

        mainContainer.setCenter(centerContent);

        // 底部
        HBox bottomBar = createBottomBar();
        mainContainer.setBottom(bottomBar);

        // 拖拽悬浮层，覆盖于 mainContainer 之上，且透明无阻挡
        overlayPane = new Pane();
        overlayPane.setMouseTransparent(true);

        rootLayer.getChildren().addAll(mainContainer, overlayPane);

        Scene scene = new Scene(rootLayer, 900, 680);
        scene.setFill(Color.TRANSPARENT);
        try {
            String css = URLEncoder.encode(CSS_STYLE, StandardCharsets.UTF_8).replace("+", "%20");
            scene.getStylesheets().add("data:text/css," + css);
        } catch (Exception ignored) {}

        window.setScene(scene);
        Platform.runLater(window::showAndWait);
    }

    private void initData() {
        priorityHeroes = FXCollections.observableArrayList(Config.heroNames);
        availableHeroes = FXCollections.observableArrayList();
        if (Summoner.allChampions != null && !Summoner.allChampions.isEmpty()) {
            List<String> all = new ArrayList<>(Summoner.allChampions.keySet());
            all.removeAll(priorityHeroes);
            availableHeroes.addAll(all);
        }
        FXCollections.sort(availableHeroes);
        filteredHeroes = new FilteredList<>(availableHeroes, p -> true);
    }

    // ==================== 视图构建 ====================

    private HBox createTopBar() {
        Label title = new Label("PRIORITY ARRAY");
        title.getStyleClass().add("title-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 自定义关闭按钮 (X 矢量图标)
        SVGPath closeIcon = new SVGPath();
        closeIcon.setContent("M 5 5 L 15 15 M 15 5 L 5 15");
        closeIcon.setStroke(Color.web("#788299"));
        closeIcon.setStrokeWidth(2);
        StackPane closeBtn = new StackPane(closeIcon);
        closeBtn.getStyleClass().add("close-btn");
        closeBtn.setOnMouseClicked(e -> window.close());
        closeBtn.setOnMouseEntered(e -> closeIcon.setStroke(Color.web("#FF4565")));
        closeBtn.setOnMouseExited(e -> closeIcon.setStroke(Color.web("#788299")));

        HBox topBar = new HBox(title, spacer, closeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(24, 24, 20, 24));

        // 丝滑窗口拖拽
        final Point2D[] dragDelta = {new Point2D(0, 0)};
        topBar.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                dragDelta[0] = new Point2D(window.getX() - e.getScreenX(), window.getY() - e.getScreenY());
            }
        });
        topBar.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                window.setX(e.getScreenX() + dragDelta[0].getX());
                window.setY(e.getScreenY() + dragDelta[0].getY());
            }
        });

        return topBar;
    }

    private VBox buildLeftPanel() {
        Label header = new Label("HERO ARSENAL");
        header.getStyleClass().add("subtitle-text");

        searchField = new TextField();
        searchField.setPromptText("搜索英雄代号...");
        searchField.getStyleClass().add("search-field");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredHeroes.setPredicate(h -> newVal == null || newVal.isEmpty() || h.toLowerCase().contains(newVal.toLowerCase()));
            refreshAvailablePane();
        });

        availableFlowPane = new FlowPane(GRID_GAP, GRID_GAP);
        availableFlowPane.setPadding(new Insets(5, 5, 20, 5));

        ScrollPane scroll = new ScrollPane(availableFlowPane);
        scroll.getStyleClass().add("custom-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox panel = new VBox(14, header, searchField, scroll);
        panel.getStyleClass().add("glass-panel");
        refreshAvailablePane();
        return panel;
    }

    private VBox buildRightPanel() {
        Label header = new Label("DEPLOYMENT QUEUE");
        header.getStyleClass().add("subtitle-text");

        Button clearBtn = new Button("清空队列");
        clearBtn.getStyleClass().addAll("btn-modern", "btn-danger");
        clearBtn.setOnAction(e -> {
            if (isAnimatingDrop) return;
            availableHeroes.addAll(priorityHeroes);
            priorityHeroes.clear();
            FXCollections.sort(availableHeroes);
            refreshPriorityPane();
            refreshAvailablePane();
        });

        HBox headerBox = new HBox(header, new Region(), clearBtn);
        HBox.setHgrow(headerBox.getChildren().get(1), Priority.ALWAYS);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // 采用 Pane，因为我们要全手动接管位置，实现完美退让动画
        priorityPane = new Pane();
        priorityPane.setPadding(new Insets(5));

        rightScrollPane = new ScrollPane(priorityPane);
        rightScrollPane.getStyleClass().add("custom-scroll");
        rightScrollPane.setFitToWidth(true);
        rightScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // 监听宽度变化重新计算排版
        rightScrollPane.viewportBoundsProperty().addListener((obs, oldV, newV) -> {
            if (!isDragging && !isAnimatingDrop) {
                layoutPriorityCards(false, -1);
            } else {
                layoutPriorityCards(false, currentPlaceholderIndex);
            }
        });

        VBox panel = new VBox(14, headerBox, rightScrollPane);
        panel.getStyleClass().add("glass-panel");

        // 绑定右侧滚轮+拖拽事件 (使用 Filter 层级捕获)
        setupDragEvents(rightScrollPane);

        refreshPriorityPane();
        return panel;
    }

    private HBox createBottomBar() {
        Button saveBtn = new Button("保存并同步配置");
        saveBtn.getStyleClass().addAll("btn-modern", "btn-primary");
        saveBtn.setPrefWidth(200);
        saveBtn.setPrefHeight(40);
        saveBtn.setOnAction(e -> {
            Config.heroNames = new ArrayList<>(priorityHeroes);
            Config.save();
            window.close();
        });

        HBox bar = new HBox(saveBtn);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(20, 0, 24, 0));
        return bar;
    }

    // ==================== 逻辑更新 ====================

    private void refreshAvailablePane() {
        availableFlowPane.getChildren().clear();
        for (String hero : filteredHeroes) {
            VBox card = createCard(hero, null);
            // 左侧依然保留正常的点击事件
            card.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    if (isAnimatingDrop) return;
                    moveToPriority(hero);
                }
            });
            availableFlowPane.getChildren().add(card);

            // 淡入微动画
            FadeTransition ft = new FadeTransition(Duration.millis(300), card);
            ft.setFromValue(0); ft.setToValue(1);
            ft.play();
        }
    }

    private void refreshPriorityPane() {
        refreshPriorityPane(true);
    }

    // 重载方法：如果 animate=false，则会保留原本的元素位置直接覆盖，避免全体复位闪烁
    private void refreshPriorityPane(boolean animate) {
        List<VBox> oldCards = new ArrayList<>(activePriorityCards);
        priorityPane.getChildren().clear();
        activePriorityCards.clear();

        for (int i = 0; i < priorityHeroes.size(); i++) {
            String hero = priorityHeroes.get(i);
            VBox card = createCard(hero, i + 1);
            card.setUserData(hero);

            // 继承原有卡片坐标，避免重新渲染时判定为新加入的 (0,0) 而触发全局 fade-in 闪烁
            for (VBox oldCard : oldCards) {
                if (hero.equals(oldCard.getUserData())) {
                    card.setTranslateX(oldCard.getTranslateX());
                    card.setTranslateY(oldCard.getTranslateY());
                    break;
                }
            }

            // 右侧卡片不再使用 setOnMouseClicked，避免与拖拽拦截起冲突
            // 右侧移除逻辑统一在右侧 ScrollPane 的 MouseReleased Filter 中处理

            priorityPane.getChildren().add(card);
            activePriorityCards.add(card);
        }
        // 带动画地将卡片排好位置
        layoutPriorityCards(animate, -1);
    }

    private void moveToPriority(String heroName) {
        availableHeroes.remove(heroName);
        priorityHeroes.add(heroName);
        refreshPriorityPane();
        refreshAvailablePane();
    }

    private void moveToAvailable(String heroName) {
        priorityHeroes.remove(heroName);
        if (!availableHeroes.contains(heroName)) {
            availableHeroes.add(heroName);
            FXCollections.sort(availableHeroes);
        }
        refreshPriorityPane();
        refreshAvailablePane();
    }

    // ==================== 核心：弹性网格引擎 ====================

    /**
     * 计算并为右侧面板所有卡片排位（实现流动避让动画）
     *
     * @param animate 是否展现滑动动画
     * @param placeholderIndex 当前被拖拽物悬浮占用的位置 (-1表示无拖拽)
     */
    private void layoutPriorityCards(boolean animate, int placeholderIndex) {
        if (rightScrollPane == null || priorityPane == null) return;

        double width = rightScrollPane.getViewportBounds().getWidth();
        if (width <= 0) width = 400; // 初始化防御

        // 计算可用列数
        int cols = Math.max(1, (int)((width - GRID_GAP) / (CARD_W + GRID_GAP)));

        int logicalIndex = 0;
        for (int i = 0; i < activePriorityCards.size(); i++) {
            VBox card = activePriorityCards.get(i);

            // 遇到正在拖拽的原卡片，它本身不占常规排布位置（或者说它的位置交给了 placeholder）
            if (card == draggedSourceCard) {
                continue;
            }

            // 遇到虚拟空位，逻辑索引向后推一格，留出空档！这就是动画精髓
            if (logicalIndex == placeholderIndex) {
                logicalIndex++;
            }

            // 计算当前卡片应在的实际坐标
            double targetX = GRID_GAP + (logicalIndex % cols) * (CARD_W + GRID_GAP);
            double targetY = GRID_GAP + (logicalIndex / cols) * (CARD_H + GRID_GAP);

            if (animate) {
                // 如果是从 0,0 刚创建的，直接瞬间移动过去，避免所有卡片从左上角飞出
                if (card.getTranslateX() == 0 && card.getTranslateY() == 0) {
                    card.setTranslateX(targetX);
                    card.setTranslateY(targetY);
                    FadeTransition ft = new FadeTransition(Duration.millis(400), card);
                    ft.setFromValue(0); ft.setToValue(1); ft.play();
                } else {
                    TranslateTransition tt = new TranslateTransition(Duration.millis(250), card);
                    tt.setToX(targetX);
                    tt.setToY(targetY);
                    tt.setInterpolator(Interpolator.SPLINE(0.25, 0.1, 0.25, 1)); // 顺滑阻尼
                    tt.play();
                }
            } else {
                card.setTranslateX(targetX);
                card.setTranslateY(targetY);
            }

            logicalIndex++;
        }

        // 更新 Pane 高度以支撑正确的滚轮
        int totalItemsCount = activePriorityCards.size();
        if (placeholderIndex != -1 && draggedSourceCard == null) totalItemsCount++;
        int rows = (int) Math.ceil((double) totalItemsCount / cols);
        priorityPane.setPrefHeight(GRID_GAP + rows * (CARD_H + GRID_GAP));
    }

    // ==================== 拖拽与吸附逻辑 (Filter拦截机制) ====================

    private void setupDragEvents(Node node) {
        // 使用 EventFilter 提前拦截，防止 ScrollPane 吞噬我们的拖拽事件
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() != MouseButton.PRIMARY || isAnimatingDrop) return;

            VBox targetCard = getCardFromEvent(e);
            if (targetCard == null) return;

            pressX = e.getScreenX();
            pressY = e.getScreenY();
            draggedSourceCard = targetCard;
            draggedHeroName = (String) targetCard.getUserData();

            // 长按判定机制
            if (dragTimer != null) dragTimer.stop();
            dragTimer = new PauseTransition(Duration.millis(200));
            dragTimer.setOnFinished(ev -> {
                dragTimer = null;
                startDrag(e);
            });
            dragTimer.play();
        });

        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (isDragging) {
                updateDragPreviewPosition(e);
                int newIndex = calculatePlaceholderIndex(e);
                if (newIndex != currentPlaceholderIndex) {
                    currentPlaceholderIndex = newIndex;
                    layoutPriorityCards(true, currentPlaceholderIndex); // 实时流动退让
                }
                handleAutoScroll(e.getY());
                e.consume(); // 拦截 ScrollPane 的原生拖动
            } else if (dragTimer != null && draggedSourceCard != null) {
                // 距离超过阈值，直接转为拖拽
                double dist = Math.hypot(e.getScreenX() - pressX, e.getScreenY() - pressY);
                if (dist > 10) {
                    dragTimer.stop();
                    dragTimer = null;
                    startDrag(e);
                    e.consume();
                }
            }
        });

        node.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            if (dragTimer != null) {
                // Timer 未结束就被释放，说明是单纯的点击操作 (移回左侧)
                dragTimer.stop();
                dragTimer = null;
                if (draggedHeroName != null) {
                    moveToAvailable(draggedHeroName);
                }

                isDragging = false;
                draggedSourceCard = null;
                draggedHeroName = null;
                e.consume(); // 拦截点击
            } else if (isDragging) {
                finishDrag();
                isDragging = false;
                // 注意：这里不再清空 draggedSourceCard 变量，交由 finishDrag 在动画完毕后清理
                // 防止在 drop 动画 200 毫秒内引起 UI 状态断层报错！
                e.consume(); // 拦截释放
            }
        });
    }

    private void handleAutoScroll(double mouseY) {
        double scrollZone = 50;
        double height = rightScrollPane.getHeight();
        if (mouseY < scrollZone) {
            rightScrollPane.setVvalue(Math.max(0, rightScrollPane.getVvalue() - 0.03));
        } else if (mouseY > height - scrollZone) {
            rightScrollPane.setVvalue(Math.min(1, rightScrollPane.getVvalue() + 0.03));
        }
    }

    private void startDrag(MouseEvent e) {
        isDragging = true;

        // 隐藏原卡片（保持在树中，但不渲染）
        draggedSourceCard.setVisible(false);

        // 在顶层创建精美的物理飞行卡片
        dragPreview = createCard(draggedHeroName, null);
        dragPreview.setStyle("-fx-background-color: rgba(0, 163, 255, 0.15); -fx-border-color: #00A3FF;");
        DropShadow flightShadow = new DropShadow(30, Color.rgb(0, 0, 0, 0.9));
        flightShadow.setOffsetY(15);
        dragPreview.setEffect(flightShadow);
        dragPreview.setMouseTransparent(true);
        dragPreview.setRotate(5); // 起飞倾斜
        dragPreview.setScaleX(1.1);
        dragPreview.setScaleY(1.1);

        overlayPane.getChildren().add(dragPreview);
        updateDragPreviewPosition(e);

        currentPlaceholderIndex = priorityHeroes.indexOf(draggedHeroName);
    }

    private void updateDragPreviewPosition(MouseEvent e) {
        if (dragPreview != null) {
            // Scene 坐标体系绝对跟随
            dragPreview.setLayoutX(e.getSceneX() - CARD_W / 2.0);
            dragPreview.setLayoutY(e.getSceneY() - CARD_H / 2.0);
        }
    }

    /**
     * 根据当前鼠标所在的 Scene 坐标，推算悬停在哪一个网格的中心点最近
     */
    private int calculatePlaceholderIndex(MouseEvent e) {
        // 将鼠标坐标转为 priorityPane 的内部相对坐标
        Point2D localPoint = priorityPane.sceneToLocal(e.getSceneX(), e.getSceneY());

        double width = rightScrollPane.getViewportBounds().getWidth();
        if(width <= 0) width = 400;
        int cols = Math.max(1, (int)((width - GRID_GAP) / (CARD_W + GRID_GAP)));
        int totalSlots = activePriorityCards.size();

        int closestIndex = totalSlots - 1;
        double minDistance = Double.MAX_VALUE;

        // 遍历所有可能的坑位中心点，寻找距离鼠标最近的一个
        for (int i = 0; i < totalSlots; i++) {
            double cx = GRID_GAP + (i % cols) * (CARD_W + GRID_GAP) + CARD_W / 2.0;
            double cy = GRID_GAP + (i / cols) * (CARD_H + GRID_GAP) + CARD_H / 2.0;
            double distance = localPoint.distance(cx, cy);
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }
        return Math.max(0, closestIndex);
    }

    private void finishDrag() {
        if (dragPreview == null || currentPlaceholderIndex == -1) {
            dragPreview = null;
            draggedSourceCard = null;
            draggedHeroName = null;
            currentPlaceholderIndex = -1;
            return;
        }

        isAnimatingDrop = true; // 加防并发锁

        // 捕获局部变量，防止它们被鼠标释放等外部事件中途破坏
        Node preview = dragPreview;
        int targetIndex = currentPlaceholderIndex;
        String heroName = draggedHeroName;

        // 计算目标坑位在 priorityPane 内部的绝对坐标
        double width = rightScrollPane.getViewportBounds().getWidth();
        if(width <= 0) width = 400; // 防御
        int cols = Math.max(1, (int)((width - GRID_GAP) / (CARD_W + GRID_GAP)));
        double targetLocalX = GRID_GAP + (targetIndex % cols) * (CARD_W + GRID_GAP);
        double targetLocalY = GRID_GAP + (targetIndex / cols) * (CARD_H + GRID_GAP);

        // 转换为顶层 Scene 的全局坐标以供飞行卡片对齐
        Point2D targetScene = priorityPane.localToScene(targetLocalX, targetLocalY);

        // 创建精准坠落/吸附动画
        TranslateTransition fall = new TranslateTransition(Duration.millis(200), preview);
        fall.setToX(targetScene.getX() - preview.getLayoutX());
        fall.setToY(targetScene.getY() - preview.getLayoutY());

        RotateTransition rotate = new RotateTransition(Duration.millis(200), preview);
        rotate.setToAngle(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), preview);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition pt = new ParallelTransition(fall, rotate, scale);
        pt.setOnFinished(ev -> {
            // 动画结束后清理临时节点并刷新真实数据
            overlayPane.getChildren().remove(preview);

            int oldIndex = priorityHeroes.indexOf(heroName);
            if (oldIndex != -1 && oldIndex != targetIndex) {
                priorityHeroes.remove(oldIndex);
                priorityHeroes.add(targetIndex, heroName);
            }

            // 传入 false，代表重排时完全摒弃 FadeIn 微动画闪烁，让数据顺滑瞬间交接
            refreshPriorityPane(false);

            // 彻底清理状态
            dragPreview = null;
            draggedSourceCard = null;
            draggedHeroName = null;
            currentPlaceholderIndex = -1;
            isAnimatingDrop = false; // 释放锁
        });
        pt.play();
    }

    private VBox getCardFromEvent(MouseEvent event) {
        Node target = (Node) event.getTarget();
        while (target != null && target != rightScrollPane) {
            if (target instanceof VBox && target.getUserData() instanceof String) {
                return (VBox) target;
            }
            target = target.getParent();
        }
        return null;
    }

    // ==================== 通用卡片工厂 ====================

    private VBox createCard(String heroName, Integer priorityValue) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefSize(CARD_W, CARD_H);
        card.setMinSize(CARD_W, CARD_H);
        card.setMaxSize(CARD_W, CARD_H);
        card.getStyleClass().add("hero-card");

        StackPane avatarContainer = new StackPane();
        avatarContainer.setPrefSize(AVATAR_SIZE, AVATAR_SIZE);

        ImageView avatar = new ImageView(new Image(SVG_AVATAR));
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);

        Circle clip = new Circle(AVATAR_SIZE / 2.0);
        clip.setCenterX(AVATAR_SIZE / 2.0);
        clip.setCenterY(AVATAR_SIZE / 2.0);
        avatar.setClip(clip);
        avatarContainer.getChildren().add(avatar);

        if (priorityValue != null) {
            Label badge = new Label(String.valueOf(priorityValue));
            badge.getStyleClass().add("priority-badge");
            StackPane.setAlignment(badge, Pos.TOP_LEFT);
            StackPane.setMargin(badge, new Insets(-4, 0, 0, -4));
            avatarContainer.getChildren().add(badge);
        }

        Label name = new Label(heroName);
        name.getStyleClass().add("hero-name");
        name.setWrapText(true);
        name.setMaxWidth(CARD_W - 10);

        card.getChildren().addAll(avatarContainer, name);
        card.setPadding(new Insets(12, 5, 5, 5));

        // 丝滑悬浮动画
        DropShadow glow = new DropShadow(15, Color.rgb(0, 163, 255, 0)); // 初始透明度0
        card.setEffect(glow);

        card.setOnMouseEntered(e -> {
            if (!isDragging) {
                card.setStyle("-fx-border-color: rgba(0, 163, 255, 0.6); -fx-background-color: rgba(0, 163, 255, 0.05);");
                glow.setColor(Color.rgb(0, 163, 255, 0.4));
                ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
                st.setToX(1.05);
                st.setToY(1.05);
                st.play();
            }
        });
        card.setOnMouseExited(e -> {
            if (!isDragging) {
                card.setStyle("");
                glow.setColor(Color.rgb(0, 163, 255, 0));
                ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
        });

        return card;
    }
}