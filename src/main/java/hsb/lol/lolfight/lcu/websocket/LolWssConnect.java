package hsb.lol.lolfight.lcu.websocket;


import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.data.Summoner;
import hsb.lol.lolfight.json.JSONArray;
import hsb.lol.lolfight.json.JSONObject;
import hsb.lol.lolfight.lcu.ApiRequest;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.WebSocket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LolWssConnect {

    // 在类的顶部添加一个静态的日志文件写入器
    public static PrintWriter logger;

    static {
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

    // 一个简单的日志方法
    public static void log(String message) {
        if (logger != null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            logger.println(timestamp + " - " + message);
        }
    }

    // 记录异常的辅助方法
    public static void log(Exception e) {
        if (logger != null) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log("ERROR: " + sw.toString());
        }
    }



    static class WebSocketListener implements WebSocket.Listener {

        Map<String, List<Consumer<String>>> eventRegister = new ConcurrentHashMap<>();
        StringBuilder text = new StringBuilder();
        Runnable onClose;

        public WebSocketListener() {
            registerAutoAccept();
            registerAutoPick();
        }

        public WebSocketListener(Runnable onClose) {
            logger.println("创建websocket消息处理");
            registerAutoAccept();
            registerAutoPick();
            this.onClose = onClose;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            WebSocket.Listener.super.onOpen(webSocket);
            //发送订阅
            String[] subscribe = {
                    //BP环节订阅
                    "OnJsonApiEvent_lol-champ-select_v1_session",
                    //游戏流程订阅
                    "OnJsonApiEvent_lol-gameflow_v1_gameflow-phase",
            };
            for (String s : subscribe) {
                webSocket.sendText("[5,\"" + s + "\"]", true);
            }

        }


        public void registerAutoAccept() {
            eventRegister("lol-gameflow_v1_gameflow-phase", new Consumer<String>() {
                @Override
                public void accept(String data) {
                    if (!Config.autoAccept)
                        return;
                    if (!Pattern.compile("\"data\":\"ReadyCheck\"").matcher(data).find())
                        return;

                    //接受对局
                    ApiRequest apiRequest = new ApiRequest();
                    apiRequest.autoAccept();
                }
            });
        }

        volatile int lastChampionId = -1;

        public void registerAutoPick() {
            eventRegister("lol-champ-select_v1_session", data -> {
                try {
                    logger.println("开始处理lol-champ-select_v1_session事件");
                    // eventType 分为 Create Update Delete
                    //获取data节点json数据
                    JSONObject dataNode = getDataNode(data);
                    //获取大乱斗模式的10个备选英雄

                    int cellId = -1;
                    int curChampionId = -1;

                    JSONArray myTeamNode = dataNode.getJSONArray("myTeam");

                    int localPlayerCellId = dataNode.getInt("localPlayerCellId");
                    //int localPlayerCellId = dataNode.getInt("localPlayerCellId");

                    for (int i = 0; i < myTeamNode.length(); i++) {
                        JSONObject oneSummoner = myTeamNode.optJSONObject(i);
                        long summonerId = oneSummoner.getLong("summonerId");
                        if (summonerId != Summoner.summonerId)
                            continue;
                        cellId = oneSummoner.getInt("cellId");
                        curChampionId = oneSummoner.getInt("championId");
                    }
                    logger.println("当前选择的英雄id为:"+curChampionId);
                    //List<String> heroNames = List.of("探险家", "深渊巨口", "涤魂圣枪", "圣枪游侠", "虚空之女", "堕落天使", "暗夜猎手", "惩戒之箭", "麦林炮手", "赏金猎人");


                    if (Config.autoPick) {
                        logger.println("开启了自动选英雄，将按照优先级选择");
                        List<String> heroNames = Config.heroNames;
                        logger.println("英雄列表：" + heroNames.toString());
                        JSONArray benchChampions = dataNode.getJSONArray("benchChampions");

                        Set<Integer> championIdSet = new HashSet<>(10);
                        for (int i = 0; i < benchChampions.length(); i++) {
                            JSONObject benchChampion = benchChampions.getJSONObject(i);
                            int championId = benchChampion.getInt("championId");
                            championIdSet.add(championId);
                        }
                        logger.println("当前候选英雄数量：" + championIdSet.size());
                        //todo 这里要考虑自己有的和周免的，避免选择不了



                        for (String heroName : heroNames) {
                            Integer heroId = Summoner.ownedChampions.get(heroName);
                            if (heroId == null) {
                                logger.println("根据英雄名称，无法好到对应id，（当前玩家没有此英雄或者数据不全）：" + heroName);
                                continue;
                            }

                            if (heroId == curChampionId) {
                                break;
                            }

                            if (championIdSet.contains(heroId)) {
                                ApiRequest apiRequest = new ApiRequest();
                                logger.println("发现优先级更高的英雄，准备选择：" + heroName);
                                apiRequest.benchSwap(heroId);
                                logger.println("已经发送更换英雄请求，");
                                curChampionId = heroId;
                                break;
                            }
                        }
                    }
                    logger.println("准备判断是否需要打开辅助网站");
                    //打开攻略网站
                    if (Config.openHelp && curChampionId > 0 && lastChampionId != curChampionId) {
                        logger.println("打开攻略");
                        try {
                            // 创建URI对象
                            URI uri = new URI("https://101.qq.com/#/hero-detail?heroid=" + curChampionId + "&datatype=fight");
                            Desktop.getDesktop().browse(uri);
                        } catch (Throwable e) {
                            logger.println("打开攻略失败");
                            logger.println(e.getMessage());
                            e.printStackTrace(logger);
                        }
                    }
                    lastChampionId = curChampionId;
                } catch (Exception e) {
                    logger.println("select_v1_session出bug");
                    logger.println(e.toString());
                    logger.println(e.getMessage());
                    e.printStackTrace();
                }
            });
        }


        public void eventRegister(String eventType, Consumer<String> consumer) {
            List<Consumer<String>> consumers = eventRegister.computeIfAbsent(eventType, k -> new ArrayList<>());
            consumers.add(consumer);
        }

        public void processCompleteTextMessage(String message) {

            logger.println(message);

            //判断是否为已订阅的事件类型
            Pattern eventPtn = Pattern.compile("(?<=OnJsonApiEvent_)[^\"]*");
            Matcher eventMch = eventPtn.matcher(message);
            if (eventMch.find()) {
                String eventType = eventMch.group();

                List<Consumer<String>> listens = eventRegister.get(eventType);

                if (listens != null) {
                    for (Consumer<String> consumer : listens) {
                        consumer.accept(message);
                    }
                }
            }
        }


        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

            text.append(data);
            if (last) {
                processCompleteTextMessage(text.toString());
                text = new StringBuilder();
            }
            webSocket.request(1);
            return null;
        }


        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            if (onClose != null) {
                onClose.run();
            }
            logger.println("连接关闭了");
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }


        public JSONObject getDataNode(String jsonStr) {
            JSONArray jsonNode = new JSONArray(jsonStr);
            return jsonNode.getJSONObject(2).getJSONObject("data");
        }

    }
}
