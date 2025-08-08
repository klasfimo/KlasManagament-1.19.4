package org.klasfimo.klasManagament.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.klasfimo.klasManagament.KlasManagament;

public class ConfigManager {
    
    private final KlasManagament plugin;
    private FileConfiguration config;
    
    public ConfigManager(KlasManagament plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    public String getString(String path) {
        return config.getString(path, "");
    }
    
    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }
    
    public int getInt(String path) {
        return config.getInt(path, 0);
    }
    
    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }
    
    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }
    
    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }
} 