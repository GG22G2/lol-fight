package hsb.lol.lolfight.lcu.websocket;


import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.data.Summoner;
import hsb.lol.lolfight.json.JSONArray;
import hsb.lol.lolfight.json.JSONObject;
import hsb.lol.lolfight.lcu.ApiRequest;
import hsb.lol.lolfight.log.LogHelper;

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




    static class WebSocketListener implements WebSocket.Listener {

        Map<String, List<Consumer<String>>> eventRegister = new ConcurrentHashMap<>();
        StringBuilder text = new StringBuilder();
        Runnable onClose;

        public WebSocketListener() {
            registerAutoAccept();
            registerAutoPick();
        }

        public WebSocketListener(Runnable onClose) {
            LogHelper.log("创建websocket消息处理");
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
                    LogHelper.log("开始处理lol-champ-select_v1_session事件");
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
                    LogHelper.log("当前选择的英雄id为:"+curChampionId);
                    //List<String> heroNames = List.of("探险家", "深渊巨口", "涤魂圣枪", "圣枪游侠", "虚空之女", "堕落天使", "暗夜猎手", "惩戒之箭", "麦林炮手", "赏金猎人");


                    if (Config.autoPick) {
                        LogHelper.log("开启了自动选英雄，将按照优先级选择");
                        List<String> heroNames = Config.heroNames;
                        LogHelper.log("英雄列表：" + heroNames.toString());
                        JSONArray benchChampions = dataNode.getJSONArray("benchChampions");

                        Set<Integer> championIdSet = new HashSet<>(10);
                        for (int i = 0; i < benchChampions.length(); i++) {
                            JSONObject benchChampion = benchChampions.getJSONObject(i);
                            int championId = benchChampion.getInt("championId");
                            championIdSet.add(championId);
                        }
                        LogHelper.log("当前候选英雄数量：" + championIdSet.size());
                        //todo 这里要考虑自己有的和周免的，避免选择不了



                        for (String heroName : heroNames) {
                            Integer heroId = Summoner.ownedChampions.get(heroName);
                            if (heroId == null) {
                                LogHelper.log("根据英雄名称，无法好到对应id，（当前玩家没有此英雄或者数据不全）：" + heroName);
                                continue;
                            }

                            if (heroId == curChampionId) {
                                break;
                            }

                            if (championIdSet.contains(heroId)) {
                                ApiRequest apiRequest = new ApiRequest();
                                LogHelper.log("发现优先级更高的英雄，准备选择：" + heroName);
                                apiRequest.benchSwap(heroId);
                                LogHelper.log("已经发送更换英雄请求，");
                                curChampionId = heroId;
                                break;
                            }
                        }
                    }
                    LogHelper.log("准备判断是否需要打开辅助网站");
                    //打开攻略网站
                    if (Config.openHelp && curChampionId > 0 && lastChampionId != curChampionId) {
                        LogHelper.log("打开攻略");
                        try {
                            // 创建URI对象
                            URI uri = new URI("https://101.qq.com/#/hero-detail?heroid=" + curChampionId + "&datatype=fight");
                            Desktop.getDesktop().browse(uri);
                        } catch (Throwable e) {
                            LogHelper.log("打开攻略失败");
                            LogHelper.log(e.getMessage());
                            LogHelper.log(e);

                        }
                    }
                    lastChampionId = curChampionId;
                } catch (Exception e) {
                    LogHelper.log("select_v1_session出bug");
                    LogHelper.log(e.toString());
                    LogHelper.log(e.getMessage());
                    e.printStackTrace();
                }
            });
        }


        public void eventRegister(String eventType, Consumer<String> consumer) {
            List<Consumer<String>> consumers = eventRegister.computeIfAbsent(eventType, k -> new ArrayList<>());
            consumers.add(consumer);
        }

        public void processCompleteTextMessage(String message) {

            LogHelper.log(message);

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
            LogHelper.log("连接关闭了");
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }


        public JSONObject getDataNode(String jsonStr) {
            JSONArray jsonNode = new JSONArray(jsonStr);
            return jsonNode.getJSONObject(2).getJSONObject("data");
        }

    }
}
