package me.moru3.mipie.SubCommand;

import me.moru3.marstools.ContentsList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class Selector {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getMainScoreboard();
    Pattern PROPERTY_REGEX = Pattern.compile("\\[.*?]");
    Pattern CURLY_BRASES_REGEX = Pattern.compile("\\{.*?}");
    Pattern SCORE_FIXED = Pattern.compile("[0-9]+?");
    Pattern SCORE_MAX = Pattern.compile("\\.\\.[0-9]+?");
    Pattern SCORE_MIN = Pattern.compile("[0-9]+?\\.\\.");
    Pattern SCORE_RANGE = Pattern.compile("[0-9]+?\\.\\.[0-9]+?");
    public List<Player> build(String selector, Player executer) {
        SelectorType type = null;
        for (SelectorType selectorType : SelectorType.values()) {
            if(selector.startsWith(selectorType.toString())) { type = selectorType; break; } else { return new ContentsList<>(); }
        }
        Matcher matcher = PROPERTY_REGEX.matcher(selector);
        if(!matcher.matches()) { return new ContentsList<>(); }
        ContentsList<String> property = new ContentsList<>(matcher.group(1).split(","));
        ContentsList<Player> result = new ContentsList<>();
        result.addAll(Bukkit.getOnlinePlayers());
        int limit = Integer.MAX_VALUE;
        for(String prop : property) {
            ContentsList<String> keyValue = new ContentsList<>(prop.split("="));
            ContentsList<Player> temp = new ContentsList<>();
            boolean negation = keyValue.get(0).startsWith("!");
            temp.addAll(result);
            if(keyValue.get(1)==null) { continue; }
            switch (keyValue.get(0)) {
                case "limit":
                    limit = Integer.parseInt(keyValue.get(1));
                case "gamemode":
                    GameMode gamemode = null;
                    for (GameMode gameMode : GameMode.values()) { if(keyValue.get(0).equalsIgnoreCase(negation ? "!" : "" + gameMode)) { gamemode = gameMode;break; } }
                    if(gamemode==null) { continue; }
                    GameMode finalGamemode = gamemode;
                    temp.forEach(player -> {if(player.getGameMode() == finalGamemode != negation) { result.remove(player); }});
                    break;
                case "level":
                    int level = Integer.parseInt(keyValue.get(1));
                    if(level<0) { continue; }
                    temp.forEach(player -> {if(player.getLevel()!=level) { result.remove(player); }});
                    break;
                case "name":
                    temp.forEach(player -> {if(player.getName().equals(keyValue.get(1))!=negation) { result.remove(player); } } );
                    break;
                case "scores":
                    Matcher matcher1 = CURLY_BRASES_REGEX.matcher(keyValue.get(1));
                    if(!matcher1.matches()) { return new ContentsList<>(); }
                    ContentsList<String> scores = new ContentsList<>(matcher1.group(1).split(","));
                    scores.forEach(score -> {
                        ContentsList<String> boardCond = new ContentsList<>(score.split("="));
                        Objective objective = board.getObjective(boardCond.get(0));
                        if(objective==null) { return; }
                        if(SCORE_FIXED.matcher(boardCond.get(0)).matches()) {
                            temp.forEach(player -> { if(Integer.parseInt(boardCond.get(0))!=objective.getScore(player).getScore()) { result.remove(player); } } );
                        } else if (SCORE_MAX.matcher(boardCond.get(0)).matches()) {
                            int max = Integer.parseInt(boardCond.get(1).replace("..", ""));
                            temp.forEach(player -> { if(max<objective.getScore(player).getScore()) { result.remove(player); } });
                        } else if (SCORE_MIN.matcher(boardCond.get(0)).matches()) {
                            int min = Integer.parseInt(boardCond.get(1).replace("..", ""));
                            temp.forEach(player -> { if(min>objective.getScore(player).getScore()) { result.remove(player); } });
                        } else if (SCORE_RANGE.matcher(boardCond.get(0)).matches()) {
                            ContentsList<String> splitValue = new ContentsList<>(boardCond.get(1).split(".."));
                            int min = Integer.parseInt(splitValue.get(0));
                            int max = Integer.parseInt(splitValue.get(1));
                            temp.forEach(player -> { if(max<objective.getScore(player).getScore()||min>objective.getScore(player).getScore()) { result.remove(player); } });
                        }
                    });
                    break;
            }
        }
        if(result.size()<=0||limit<1) { return new ContentsList<>(); }
        switch (Objects.requireNonNull(type)) {
            case ALL_PLAYERS:
                return result.slice(0, limit-1);
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
}
