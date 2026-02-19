package hsb.lol.lolfight.service;


import hsb.lol.lolfight.data.Summoner;
import hsb.lol.lolfight.json.JSONArray;
import hsb.lol.lolfight.json.JSONObject;
import hsb.lol.lolfight.json.JSONTokener;
import hsb.lol.lolfight.lcu.ApiRequest;
import hsb.lol.lolfight.log.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataService {

    //获取全部英雄
    public static  void getAllChampions() {
        ApiRequest apiRequest = new ApiRequest();
        InputStream allChampions = apiRequest.getAllChampions();

        JSONArray allChampNode = new JSONArray(new JSONTokener(allChampions));


        //第一组数据为 "无" 这里直接略过
        for (int i = 1; i < allChampNode.length(); i++) {
            JSONObject oneChamp = allChampNode.getJSONObject(i);
            int championId = oneChamp.getInt("id");
            String championName = oneChamp.getString("name");
            Summoner.heroIdMap.put(championId, championName);
            Summoner.heroNameMap.put(championName, championId);
            //System.out.println(championName);
            LogHelper.log("英雄：" + championName);
        }
        Summoner.allChampions = Summoner.heroNameMap;
    }

    //获取自己的英雄列表
    public static void getOwnedChampions() {
        ApiRequest apiRequest = new ApiRequest();
        InputStream ownedChampions = apiRequest.getOwnedChampions();
        String s = convertStr(ownedChampions);

        Map<String, Integer> map = new HashMap<>();

        //正则表达式匹配
        Pattern namePattern = Pattern.compile("(?<=name\":\")([^\"]*)");
        Matcher nameMatcher = namePattern.matcher(s);

        Pattern titlePattern = Pattern.compile("(?<=title\":\")([^\"]*)");
        Matcher titleMatcher = titlePattern.matcher(s);

        Pattern idPattern = Pattern.compile("(?<=\"id\":)([^,]*)");
        Matcher idMatcher = idPattern.matcher(s);

        while (nameMatcher.find() && titleMatcher.find() && idMatcher.find()) {
          //  map.put(nameMatcher.group() + " - " + titleMatcher.group(), Integer.parseInt(idMatcher.group()));
            map.put(nameMatcher.group(), Integer.parseInt(idMatcher.group()));
        }

        Summoner.ownedChampions = map;
    }




    //获取召唤师头像和id
    public static void freshSummoner() {
        ApiRequest apiRequest = new ApiRequest();
        InputStream currentSummoner = apiRequest.getCurrentSummoner();
        String s = convertStr(currentSummoner);

        //进行正则表达式匹配，以得到召唤师昵称
        Pattern namePattern = Pattern.compile("(?<=displayName\":\")([^\"]*)");
        Matcher nameMatcher = namePattern.matcher(s);
        if (!nameMatcher.find())
            return;

        Summoner.name = nameMatcher.group();


        //召唤师ID
        Pattern sIdPattern = Pattern.compile("(?<=summonerId\":)([^,]*)");
        Matcher sIdMatcher = sIdPattern.matcher(s);
        if (!sIdMatcher.find())
            return;

        Summoner.summonerId = Long.parseLong(sIdMatcher.group());
    }


    //转换输入流为 String
    //方便进行正则表达式匹配
    public static String convertStr(InputStream inputStream) {
        StringBuilder result = new StringBuilder();
        // 使用 try-with-resources 语句确保流被自动关闭
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String line;
            // 正确的行读取方式
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
