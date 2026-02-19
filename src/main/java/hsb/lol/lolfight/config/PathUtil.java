package hsb.lol.lolfight.config;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

/**
 * 路径工具类，兼容 JAR 运行和 Native 编译两种模式
 *
 * @author hsb
 */
public class PathUtil {

    /**
     * 获取应用程序运行目录
     * - JAR 运行时：返回 JAR 所在目录
     * - Native 编译运行时：返回可执行文件所在目录
     * - IDE 开发时：返回项目根目录
     *
     * @return 应用程序运行目录的 Path 对象
     */
    public static Path getApplicationDir() {
        try {
            // 尝试从 CodeSource 获取位置
            CodeSource codeSource = Config.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                URL location = codeSource.getLocation();
                if (location != null) {
                    Path path = Paths.get(location.toURI());
                    // 如果是 JAR 文件，返回其父目录
                    if (path.toString().endsWith(".jar")) {
                        return path.getParent();
                    }
                    // 如果是目录（IDE 开发模式），直接返回
                    return path;
                }
            }
        } catch (URISyntaxException | SecurityException e) {
            // 忽略异常，使用备用方案
        }

        // 备用方案：使用 user.dir 系统属性
        return Paths.get(System.getProperty("user.dir"));
    }

    /**
     * 获取配置文件完整路径
     *
     * @param filename 配置文件名
     * @return 配置文件的完整 Path 对象
     */
    public static Path getConfigFilePath(String filename) {
        return getApplicationDir().resolve(filename);
    }

    /**
     * 获取默认配置文件路径 (config.json)
     *
     * @return config.json 的完整 Path 对象
     */
    public static Path getDefaultConfigFilePath() {
        return getConfigFilePath("config.json");
    }
}
