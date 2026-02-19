package hsb.lol.lolfight.log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hsb
 * @date 2025/10/6 16:26
 */
public class LogHelper {

    // 在类的顶部添加一个静态的日志文件写入器
    private static PrintWriter logger;

   final public static boolean outputLog = false;

    static {
        if (outputLog){
            try {
                // 将日志文件创建在用户的主目录下，方便查找
                File logFile = new File("G:\\kaifa_environment\\code\\java\\lol-fight", "lolfight-debug.log");
                FileWriter fw = new FileWriter(logFile, true); // true 表示追加写入
                logger = new PrintWriter(fw, true); // true 表示自动刷新
                log("Logger initialized.");
            } catch (IOException e) {
                e.printStackTrace(); // 这个在IDE里还能看到，在exe里就看不到了
            }
        }

    }

    // 一个简单的日志方法
    public static void log(String message) {
        if (logger != null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            logger.println(timestamp + " - " + message);
        }
    }

    // 记录异常的辅助方法
    public static void log(Throwable e) {
        if (logger != null) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log("ERROR: " + sw.toString());
        }
    }

}
