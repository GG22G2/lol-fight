package hsb.lol.lolfight.config;

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
    public static volatile List<String> heroNames = List.of(
            "流光镜影","卡牌大师","诡术妖姬","寒冰射手" ,"虚空之女", "深渊巨口"
            ,"远古巫灵","星籁歌姬","光辉女郎","九尾妖狐"
            , "探险家" ,"皮城女警"
            , "堕落天使", "冰晶凤凰","大发明家","虚空先知", "复仇焰魂","冰霜女巫","暗黑元首","虚空之眼","狂暴之心" , "万花通灵"
            ,"圣枪游侠", "麦林炮手"
            ,"愁云使者"
    );



}
