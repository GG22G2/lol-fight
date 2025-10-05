package hsb.lol.lolfight.data;


import hsb.lol.lolfight.service.DataService;

import java.util.HashMap;
import java.util.Map;

public class Summoner {


    public static String name;

    public static volatile long summonerId;

    public static Map<String, Integer> ownedChampions;

    public static Map<String, Integer> allChampions;

    public static Map<Integer, String> heroIdMap = new HashMap<>();
    public static Map<String, Integer> heroNameMap = new HashMap<>();


    private Summoner() {
        name = "Not Found";

        ownedChampions = new HashMap<>();
        ownedChampions.put("无", -1);

        allChampions = new HashMap<>();
        allChampions.put("无", -1);

    }

    public static void init() {
        DataService.freshSummoner();
        DataService.getOwnedChampions();
        DataService.getAllChampions();
    }

}
