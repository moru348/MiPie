package me.moru3.mipie.SubCommand;

import me.moru3.marstools.ContentsList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.List;
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
        SelectorType type = SelectorType.NEAREST_PLAYER;
        for (SelectorType selectorType : SelectorType.values()) {
            if(selector.startsWith(selectorType.toString())) { type = selectorType; break; }
        }
        Matcher matcher = PROPERTY_REGEX.matcher(selector);
        if(!matcher.matches()) { return new ContentsList<>(); }
        ContentsList<String> property = new ContentsList<>(matcher.group(1).split(","));
        ContentsList<Player> result = new ContentsList<>();
        result.addAll(Bukkit.getOnlinePlayers());
        for(String prop : property) {
            ContentsList<String> keyValue = new ContentsList<>(prop.split("="));
            ContentsList<Player> temp = new ContentsList<>();
            boolean negation = keyValue.get(0).startsWith("!");
            temp.addAll(result);
            switch (keyValue.get(0)) {
                case "gamemode":
                    GameMode gamemode = GameMode.SURVIVAL;
                    for (GameMode gameMode : GameMode.values()) { if(keyValue.get(0).equalsIgnoreCase(negation ? "!" : "" + gameMode)) { gamemode = gameMode;break; } }
                    GameMode finalGamemode = gamemode;
                    temp.forEach(player -> {if(player.getGameMode() == finalGamemode != negation) { result.remove(player); }});
                    break;
                case "level":
                    int level = Integer.parseInt(keyValue.get(1));
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
                        if(SCORE_FIXED.matcher(boardCond.get(0)).matches()) {
                            temp.forEach(player -> { if(Integer.parseInt(boardCond.get(0))!=board.getObjective(boardCond.get(0)).getScore(player).getScore()) { result.remove(player); } } );
                        } else if (SCORE_MAX.matcher(boardCond.get(0)).matches()) {
                            int max = Integer.parseInt(boardCond.get(1).replace("..", ""));
                            temp.forEach(player -> { if(max<board.getObjective(boardCond.get(0)).getScore(player).getScore()) { result.remove(player); } });
                        } else if (SCORE_MIN.matcher(boardCond.get(0)).matches()) {
                            int min = Integer.parseInt(boardCond.get(1).replace("..", ""));
                            temp.forEach(player -> { if(min>board.getObjective(boardCond.get(0)).getScore(player).getScore()) { result.remove(player); } });
                        } else if (SCORE_RANGE.matcher(boardCond.get(0)).matches()) {
                            ContentsList<String> splitValue = new ContentsList<>(boardCond.get(1).split(".."));
                            int min = Integer.parseInt(splitValue.get(0));
                            int max = Integer.parseInt(splitValue.get(1));
                            temp.forEach(player -> { if(max<board.getObjective(boardCond.get(0)).getScore(player).getScore()||min>board.getObjective(boardCond.get(0)).getScore(player).getScore()) { result.remove(player); } });
                        }
                    });
                    break;
            }
        }
        return result;
    }
}
