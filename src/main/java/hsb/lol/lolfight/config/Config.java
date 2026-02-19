package hsb.lol.lolfight.config;

import hsb.lol.lolfight.json.JSONArray;
import hsb.lol.lolfight.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hsb
 * @date 2023/8/26 19:04
 */
public class Config {

    public static volatile boolean autoAccept = true;
    public static volatile boolean autoPick = false;
    public static volatile boolean openHelp = false;

    //默认的英雄选择优先级
    // 注意：这里我们使用了 new ArrayList<>(Arrays.asList(...)) 来创建一个可变的列表
    public static volatile List<String> heroNames = new ArrayList<>(Arrays.asList(
            "流光镜影","卡牌大师","诡术妖姬","寒冰射手" ,"虚空之女", "深渊巨口"
            ,"远古巫灵","星籁歌姬","光辉女郎","九尾妖狐"
            , "探险家" ,"皮城女警"
            , "堕落天使", "冰晶凤凰","大发明家","虚空先知", "复仇焰魂","冰霜女巫","暗黑元首","虚空之眼","狂暴之心" , "万花通灵"
            ,"圣枪游侠", "麦林炮手"
            ,"愁云使者"
    ));

    /**
     * 从 config.json 加载配置
     * 如果文件不存在或读取失败，则保持默认值
     */
    public static void load() {
        Path configPath = PathUtil.getDefaultConfigFilePath();
        if (!Files.exists(configPath)) {
            return;
        }

        try {
            String content = Files.readString(configPath);
            JSONObject json = new JSONObject(content);

            // 读取 boolean 配置
            autoAccept = json.optBoolean("autoAccept", autoAccept);
            autoPick = json.optBoolean("autoPick", autoPick);
            openHelp = json.optBoolean("openHelp", openHelp);

            // 读取英雄列表
            JSONArray heroArray = json.optJSONArray("heroNames");
            if (heroArray != null) {
                List<String> loadedHeroes = new ArrayList<>();
                for (int i = 0; i < heroArray.length(); i++) {
                    loadedHeroes.add(heroArray.getString(i));
                }
                if (!loadedHeroes.isEmpty()) {
                    heroNames = loadedHeroes;
                }
            }
        } catch (Exception e) {
            // 加载失败时使用默认值，不抛出异常
            e.printStackTrace();
        }
    }

    /**
     * 将当前配置保存到 config.json
     */
    public static void save() {
        try {
            JSONObject json = new JSONObject();
            json.put("autoAccept", autoAccept);
            json.put("autoPick", autoPick);
            json.put("openHelp", openHelp);

            JSONArray heroArray = new JSONArray();
            for (String hero : heroNames) {
                heroArray.put(hero);
            }
            json.put("heroNames", heroArray);

            Path configPath = PathUtil.getDefaultConfigFilePath();
            Files.writeString(configPath, json.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
