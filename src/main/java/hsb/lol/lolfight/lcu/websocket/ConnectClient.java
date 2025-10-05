package hsb.lol.lolfight.lcu.websocket;

import hsb.lol.lolfight.data.Summoner;
import hsb.lol.lolfight.lcu.Connect;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author 胡帅博
 * @date 2023/3/21 13:02
 */
public class ConnectClient {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    HttpClient client = HttpClient.newBuilder().sslContext(Connect.createUnverifiedSslContext()).build();

    Consumer<String> onMessage;

    boolean start = false;

    volatile WebSocket webSocket;
    LolWssConnect.WebSocketListener webSocketListener;

    public ConnectClient(Consumer<String> onMessage) {
        this.onMessage = onMessage;

    }

    public void connect() {
        if (!start) {
            webSocketListener = new LolWssConnect.WebSocketListener(this::reconnect);
            reconnect();
            start = true;
        }
    }


    //todo 如果网络问题，导致连接断开，客户端需要发送心跳包确认是否断链
    private void reconnect() {
        webSocket = null;
        onMessage.accept("连接英雄联盟客户端中...");
        executor.execute(() -> {
            try {
                Connect httpsConnect = Connect.reConnect();
                Summoner.init();
                webSocket = client.newWebSocketBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .header("Authorization", httpsConnect.getAuthorization())
                        .buildAsync(URI.create("wss://127.0.0.1:" + httpsConnect.getPort()), webSocketListener)
                        .join();
                onMessage.accept("连接成功");
            } catch (Exception e) {
                //e.printStackTrace();
                onMessage.accept("连接错误,5秒后重试");
                executor.schedule(this::reconnect, 5, TimeUnit.SECONDS);
            }
        });
    }

}
