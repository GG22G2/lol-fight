package hsb.lol.lolfight.ui;


import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * @author hsb
 * @date 2023/1/29 20:13
 */
public class DragScene extends Scene {
    private double offsetX, offsetY;
    //private double stageX, stageY;

    //private double stageW, stageH;

    private double widthEndXOffset;


    private Stage stage;
    private Parent root;


    private boolean allowOpt = false;

    final private int DRAG_OPT = 1;
    final private int RESIZE_OPT = 2;

    final private int IGNORE = -1;

    private int pressedOpt = IGNORE;

    /**
     * 判定是否为调整窗口状态的范围与边界距离
     */
    private static final double RESIZE_WIDTH = 4.0D;
    /**
     * 窗口最小宽度
     */
    private static final double MIN_WIDTH = 400;
    /**
     * 窗口最小高度
     */
    private static final double MIN_HEIGHT = 400;

    private Cursor previousCursor;

    public DragScene(Parent root, Stage stage) {
        super(root);
        this.stage = stage;
        this.root = root;
    }

    public void drag(MouseEvent event) {

        double x = event.getSceneX();
        double y = event.getSceneY();
        // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
        double nextX = stage.getX();
        double nextY = stage.getY();
        double nextWidth = stage.getWidth();
        double nextHeight = stage.getHeight();


        //  System.out.println(x+","+event.getScreenX());
        //System.out.println(offsetX + "," + event.getScreenX());
        //鼠标焦点位于左侧边界，执行左右移动
        if (Cursor.NW_RESIZE.equals(root.getCursor())
                || Cursor.W_RESIZE.equals(root.getCursor())
                || Cursor.SW_RESIZE.equals(root.getCursor())) {
            nextX = event.getScreenX() + offsetX;
            nextWidth = widthEndXOffset - event.getScreenX();
            if (nextWidth < MIN_WIDTH) {
                return;
                //nextX = stage.getX();
            }
            //System.out.println(nextX + "," + nextWidth);
        }

        //鼠标焦点位于右侧边界，执行左右移动
        if (Cursor.NE_RESIZE.equals(root.getCursor())
                || Cursor.E_RESIZE.equals(root.getCursor())
                || Cursor.SE_RESIZE.equals(root.getCursor())) {
            nextWidth = Math.max(x, MIN_WIDTH);
        }

        //鼠标焦点位于顶部边界，执行上下移动
        if (Cursor.SW_RESIZE.equals(root.getCursor())
                || Cursor.SE_RESIZE.equals(root.getCursor())
                || Cursor.S_RESIZE.equals(root.getCursor())) {
            nextHeight = Math.max(y, MIN_HEIGHT);
        }

        //鼠标焦点位于底部边界，执行上下移动
        if (Cursor.NW_RESIZE.equals(root.getCursor())
                || Cursor.N_RESIZE.equals(root.getCursor())
                || Cursor.NE_RESIZE.equals(root.getCursor())) {
            double height = Math.max(nextHeight - y, MIN_HEIGHT);
            nextY = nextY + nextHeight - height;
            nextHeight = height;
        }

        // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
        stage.setX(nextX);
        stage.setY(nextY);
        stage.setWidth(nextWidth);
        stage.setHeight(nextHeight);
        //System.out.println(nextWidth);
    }

    public void setCanDrag() {


        // 鼠标按下时记录偏移量
        this.setOnMousePressed(event -> {
            offsetX = event.getSceneX();
            offsetY = event.getSceneY();

            //判断一下是拖动还是调整窗口
            if (pressedOpt == DRAG_OPT) {
                allowOpt = true;
            }
        });

        this.setOnMouseReleased(event -> {
            allowOpt = false;
            pressedOpt = IGNORE;
        });

        this.setOnMouseDragOver(event -> {
            allowOpt = false;
        });

        // 鼠标拖动时改变界面的位置
        this.setOnMouseDragged(event -> {
            if (pressedOpt == DRAG_OPT) {
                stage.setX(event.getScreenX() - offsetX);
                stage.setY(event.getScreenY() - offsetY);
            }
        });

        this.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            event.consume();

            //事件被消费，不在执行其他任务
            event.consume();
            double x = event.getSceneX();
            double y = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();
            // 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型
            Cursor cursorType = FlexibleListener.position(x, y, width, height);

            if (cursorType == Cursor.DISAPPEAR) {
                pressedOpt = DRAG_OPT;
            }

        });


    }
}
