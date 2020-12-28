package me.moru3.mipie;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Config {
    private final Plugin plugin;
    private final String filename;
    private FileConfiguration config;
    private final File configFile;
    private FileConfiguration defaultConfig;
    public Config(Plugin plugin, String filename) {
        this.plugin = plugin;
        this.filename = filename;
        this.configFile = new File(plugin.getDataFolder(), this.filename);
    }

    public void saveDefaultConfig() {
        if(!configFile.exists()) { plugin.saveResource(filename, false); }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(filename), StandardCharsets.UTF_8));
        config.setDefaults(defaultConfig);
    }

    private void reloadDefaultConfig() {
        defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(filename), StandardCharsets.UTF_8));
    }

    public FileConfiguration config() {
        if(config == null) { reloadConfig(); }
        return config;
    }

    public FileConfiguration defaultConfig() {
        if(defaultConfig == null) { reloadDefaultConfig(); }
        return defaultConfig;
    }

    public void saveConfig() {
        if(config==null) { return; }
        try { config().save(configFile); } catch (IOException e) { e.printStackTrace(); }
    }
}