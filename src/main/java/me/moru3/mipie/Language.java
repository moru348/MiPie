package me.moru3.mipie;

import me.moru3.marstools.ContentsList;
import org.bukkit.ChatColor;

import java.util.Map;
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
        return ChatColor.translateAlternateColorCodes('&', prefix+config.config().getString(key, config.defaultConfig().getString(key, "§cMessage not found")));
    }

    /**
     * If you pass a value list, it will be replaced with %1, %2, %3 ...
     * @param key config key
     * @param replace replace value list
     * @return string.
     */
    public String get(String key, String... replace) {
        AtomicReference<String> msg = new AtomicReference<>(config.config().getString(key, config.defaultConfig().getString(key)));
        if(msg.get()==null) { return "§cMessage not found"; }
        new ContentsList<>(replace).forEach((value, index) -> msg.updateAndGet(v -> v.replace("%" + (index+1), value)));
        return ChatColor.translateAlternateColorCodes('&', prefix+msg.get());
    }

    public String get(String key, Map<String, String> replace) {
        AtomicReference<String> msg = new AtomicReference<>(config.config().getString(key, config.defaultConfig().getString(key)));
        if(msg.get()==null) { return "§cMessage not found"; }
        replace.forEach((k, v) -> msg.updateAndGet(i -> i.replace(k, v)));
        return ChatColor.translateAlternateColorCodes('&', prefix+msg.get());
    }
}
