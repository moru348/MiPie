package me.moru3.mipie.SubCommand;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Selector {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getMainScoreboard();
    Pattern CURLY_BRASSES_REGEX = Pattern.compile("\\{.*?}");
    Pattern SCORE_FIXED = Pattern.compile("[0-9]+?");
    Pattern SCORE_MAX = Pattern.compile("\\.\\.[0-9]+?");
    Pattern SCORE_MIN = Pattern.compile("[0-9]+?\\.\\.");
    Pattern SCORE_RANGE = Pattern.compile("[0-9]+?\\.\\.[0-9]+?");
    public List<Player> build(@NotNull String selector, @NotNull Player executer) {
        SelectorType type = null;
        for (SelectorType selectorType : SelectorType.values()) { if(selector.startsWith(selectorType.toString())) { type = selectorType; selector = selector.replace(selectorType.toString(), ""); break; } }
        if(type==null) { return new ContentsList<>(); }
        ContentsList<Player> result = new ContentsList<>();
        result.addAll(Bukkit.getOnlinePlayers());
        AtomicInteger limit = new AtomicInteger(Integer.MAX_VALUE);
        ContentsMap<String, String> property = morphologicalAnalysis(selector);
        property.forEach((key, value) -> {
            ContentsList<Player> temp = new ContentsList<>();
            temp.addAll(result);
            boolean negation = value.startsWith("!");
            switch (key) {
                case "limit":
                    limit.set(Integer.parseInt(value));
                case "gamemode":
                    GameMode gamemode = null;
                    for (GameMode gameMode : GameMode.values()) {if(value.equalsIgnoreCase(((negation ? "!" : "") + gameMode))) { gamemode = gameMode;break; } }
                    if(gamemode==null) { return; }
                    GameMode finalGamemode = gamemode;
                    temp.forEach(player -> {if(player.getGameMode() == finalGamemode && negation) { result.remove(player); }});
                    break;
                case "level":
                    int level = Integer.parseInt(value);
                    if(level<0) { return; }
                    temp.forEach(player -> {if(player.getLevel()!=level) { result.remove(player); }});
                    break;
                case "name":
                    temp.forEach(player -> {if(player.getName().equalsIgnoreCase(value)&&negation) { result.remove(player); } } );
                    break;
                case "scores":
                    ContentsMap<String, String> val = parenthesisAnalysis(value);
                    val.forEach((k, v) -> {
                        Objective objective = board.getObjective(k);
                        if(objective==null) { return; }

                        if(SCORE_FIXED.matcher(v).matches()) {
                            temp.forEach(player -> { if(Integer.parseInt(v)!=objective.getScore(player.getName()).getScore()) { result.remove(player); } } );
                        } else if (SCORE_MAX.matcher(v).matches()) {
                            int max = Integer.parseInt(v.replace("..", ""));
                            temp.forEach(player -> { if(max< objective.getScore(player.getName()).getScore()) { result.remove(player); } });
                        } else if (SCORE_MIN.matcher(v).matches()) {
                            int min = Integer.parseInt(v.replace("..", ""));
                            temp.forEach(player -> { if(min> objective.getScore(player.getName()).getScore()) { result.remove(player); } });
                        } else if (SCORE_RANGE.matcher(v).matches()) {
                            ContentsList<String> splitValue = new ContentsList<>(v.split(".."));
                            System.out.println(splitValue);
                            int min = Integer.parseInt(splitValue.get(0));
                            int max = Integer.parseInt(splitValue.get(1));
                            temp.forEach(player -> { if(max<objective.getScore(player.getName()).getScore()||min>objective.getScore(player.getName()).getScore()) { result.remove(player); } });
                        }
                    });
                    break;
            }
        });
        if(result.size()<=0|| limit.get() <1) { return new ContentsList<>(); }
        switch (Objects.requireNonNull(type)) {
            case ALL_PLAYERS:
                return result.slice(0, limit.get() -1);
            case RANDOM_PLAYER:
                return new ContentsList<>(result.random());
            case NEAREST_PLAYER:
                double distance = Double.MAX_VALUE;
                Player player = null;
                for(Player resultPlayer: result) {
                    if(executer.getLocation().distance(resultPlayer.getLocation())<distance) {
                        distance = executer.getLocation().distance(resultPlayer.getLocation());
                        player = resultPlayer;
                    }
                }
                if(player==null) { return new ContentsList<>(); }
                return new ContentsList<>(player);
            case EXECUTING_ENTITY:
                if(result.contains(executer)) { return new ContentsList<>(executer); } else { return new ContentsList<>(); }
            default:
                throw new IllegalArgumentException("This type is not supported.");
        }
    }

    private ContentsMap<String, String> parenthesisAnalysis(String str) {
        AtomicReference<String> syntax = new AtomicReference<>(str);
        syntax.updateAndGet(v -> v.replace("{", "").replace("}", ""));
        ContentsMap<String, String> result = new ContentsMap<>();
        if(syntax.get().length()<=0) { return new ContentsMap<>(); }
        ContentsList<String> keys = new ContentsList<>(syntax.get().split(","));
        for (String s : keys) { String[] temp = s.split("=");result.put(temp[0], temp[1]); }
        return result;
    }

    private ContentsMap<String, String> morphologicalAnalysis(String str) {
        AtomicReference<String> syntax = new AtomicReference<>(str);
        syntax.updateAndGet(v -> v.replace("[", "").replace("]", ""));
        ContentsMap<String, String> result = new ContentsMap<>();
        Matcher matcher = CURLY_BRASSES_REGEX.matcher(str);
        ContentsMap<String, String> values = new ContentsMap<>();
        if(matcher.find()) {new ContentsList<>(matcher.group().split(" ")).forEach((value, index) -> { values.put("%" + index, value);syntax.updateAndGet(v -> v.replace(value, "%" + index)); }); }
        if(syntax.get().length()<=0) { return new ContentsMap<>(); }
        ContentsList<String> keys = new ContentsList<>(syntax.get().split(","));
        if(keys.size()<=0) { return new ContentsMap<>(); }
        for (String s : keys) { String[] temp = s.split("=");result.put(temp[0], temp[1]); }
        values.forEach((key, value) -> { for (String resultKey : result.getKeys()) { result.put(resultKey, result.get(resultKey).replace(key, value)); } });
        return result;
    }
}
