package me.moru3.mipie;

import me.moru3.marstools.ContentsList;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Language {
    Config config;

    String prefix = "";

    public void setPrefix(String prefix) {
        this.prefix = prefix + "";
    }

    public Language(Config config) {
        this.config = config;
    }

    public String get(String key) {
        return ChatColor.translateAlternateColorCodes('&', prefix+config.config().getString(key, config.defaultConfig().getString(key, ChatColor.RED + "Message not found: " + key)));
    }

    /**
     * If you pass a value list, it will be replaced with %1, %2, %3 ...
     * @param key config key
     * @param replace replace value list
     * @return string.
     */
    public String get(String key, Object... replace) {
        AtomicReference<String> msg = new AtomicReference<>(config.config().getString(key, config.defaultConfig().getString(key)));
        if(msg.get()==null) { return ChatColor.RED + "Message not found: " + key; }
        new ContentsList<>(replace).forEach((value, index) -> msg.updateAndGet(v -> v.replace("%" + (index+1), value.toString())));
        return ChatColor.translateAlternateColorCodes('&', prefix+msg.get());
    }

    public String get(String key, Map<String, Object> replace) {
        AtomicReference<String> msg = new AtomicReference<>(config.config().getString(key, config.defaultConfig().getString(key)));
        if(msg.get()==null) { return ChatColor.RED + "Message not found: " + key; }
        replace.forEach((k, v) -> msg.updateAndGet(i -> i.replace(k, v.toString())));
        return ChatColor.translateAlternateColorCodes('&', prefix+msg.get());
    }
}
