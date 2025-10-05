package hsb.lol.lolfight.lcu.websocket;


import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.data.Summoner;
import hsb.lol.lolfight.json.JSONArray;
import hsb.lol.lolfight.json.JSONObject;
import hsb.lol.lolfight.lcu.ApiRequest;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.WebSocket;
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
            System.out.println("创建websocket消息处理");
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
                    // eventType 分为 Create Update Delete
                    //获取data节点json数据
                    JSONObject dataNode = getDataNode(data);
                    //获取大乱斗模式的10个备选英雄

                    int cellId = -1;
                    int myChampionId = -1;

                    JSONArray myTeamNode = dataNode.getJSONArray("myTeam");

                    int localPlayerCellId = dataNode.getInt("localPlayerCellId");
                    //int localPlayerCellId = dataNode.getInt("localPlayerCellId");

                    for (int i = 0; i < myTeamNode.length(); i++) {
                        JSONObject oneSummoner = myTeamNode.optJSONObject(i);
                        long summonerId = oneSummoner.getLong("summonerId");
                        if (summonerId != Summoner.summonerId)
                            continue;
                        cellId = oneSummoner.getInt("cellId");
                        myChampionId = oneSummoner.getInt("championId");
                    }

                    //List<String> heroNames = List.of("探险家", "深渊巨口", "涤魂圣枪", "圣枪游侠", "虚空之女", "堕落天使", "暗夜猎手", "惩戒之箭", "麦林炮手", "赏金猎人");

                    List<String> heroNames = List.of(
                            "流光镜影","卡牌大师","诡术妖姬","寒冰射手" ,"虚空之女", "深渊巨口"
                            ,"远古巫灵","星籁歌姬","光辉女郎","九尾妖狐"
                            , "探险家" ,"皮城女警"
                            , "堕落天使", "冰晶凤凰","大发明家","虚空先知", "复仇焰魂","冰霜女巫","暗黑元首","虚空之眼","狂暴之心" , "万花通灵"
                            ,"圣枪游侠", "麦林炮手"
                            ,"愁云使者"
                    );

                    //
                    //Set<Integer> championIdMap = heroNames.stream().map(x -> Summoner.heroNameMap.get(x)).collect(Collectors.toSet());
                    //当前选中英雄已经是候选列表中的，则直接结束
                    //if (!championIdMap.contains(myChampionId)) {
                    JSONArray benchChampions = dataNode.getJSONArray("benchChampions");

                    Set<Integer> championIdSet = new HashSet<>(10);
                    for (int i = 0; i < benchChampions.length(); i++) {
                        JSONObject benchChampion = benchChampions.getJSONObject(i);
                        int championId = benchChampion.getInt("championId");
                        championIdSet.add(championId);
                    }

                    for (String heroName : heroNames) {
                        Integer heroId = Summoner.heroNameMap.get(heroName);
                        if (heroId == null) {
                            System.out.println("根据英雄名称，无法好到对于id，：" + heroName);
                            continue;
                        }
                        if (heroId == myChampionId) {
                            break;
                        }
                        if (championIdSet.contains(heroId)) {
                            ApiRequest apiRequest = new ApiRequest();
                            apiRequest.benchSwap(heroId);
                            myChampionId = heroId;
                            break;
                        }
                    }

//                        for (int i = 0; i < benchChampions.length(); i++) {
//                            JSONObject benchChampion = benchChampions.getJSONObject(i);
//                            int championId = benchChampion.getInt("championId");
////                            if (championId==myChampionId){
////                                //按照优先级， 后续的都没必要看了
////                                break;
////                            }
//                            String name = Summoner.heroIdMap.get(championId);
//                            ApiRequest apiRequest = new ApiRequest();
//                            System.out.println(name);
//                            if (championIdMap.contains(championId)) {
//                                apiRequest.benchSwap(championId);
//                                myChampionId = championId;
//                                break;
//                            }
//                        }
                    // }

                    //打开攻略网站
                    if (Config.openHelp && myChampionId > 0 && lastChampionId != myChampionId) {
                        System.out.println("打开攻略");
                        try {
                            // 创建URI对象
                            URI uri = new URI("https://101.qq.com/#/hero-detail?heroid=" + myChampionId + "&datatype=fight");
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    lastChampionId = myChampionId;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


        public void eventRegister(String eventType, Consumer<String> consumer) {
            List<Consumer<String>> consumers = eventRegister.computeIfAbsent(eventType, k -> new ArrayList<>());
            consumers.add(consumer);
        }

        public void processCompleteTextMessage(String message) {

            System.out.println(message);

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
            System.out.println("连接关闭了");
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }


        public JSONObject getDataNode(String jsonStr) {
            JSONArray jsonNode = new JSONArray(jsonStr);
            return jsonNode.getJSONObject(2).getJSONObject("data");
        }

    }
}
