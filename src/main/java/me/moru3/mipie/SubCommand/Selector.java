package me.moru3.mipie.SubCommand;

import me.moru3.marstools.Contents;
import me.moru3.marstools.ContentsList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Deprecated
public class Selector {
    Pattern PROPERTY_REGEX = Pattern.compile("\\[.*?]");
    Pattern CURLYBRASES_REGEX = Pattern.compile("\\{.*?}");
    public List<Player> build(String selector, Player executer) {
        SelectorType type = SelectorType.NEAREST_PLAYER;
        for (SelectorType selectorType : SelectorType.values()) {
            if(selector.startsWith(selectorType.toString())) { type = selectorType; break; }
        }
        ContentsList<String> property = new ContentsList<>(PROPERTY_REGEX.matcher(selector).group(1).split(","));
        ContentsList<Player> result = new ContentsList<>();
        result.addAll(Bukkit.getOnlinePlayers());
        property.forEach(prop -> {
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
                // case "scores":
                //     ContentsList<String> scores = new ContentsList<>(CURLYBRASES_REGEX.matcher(keyValue.get(1)).group(1).split(","));
            }
        });
        return result;
    }
}
