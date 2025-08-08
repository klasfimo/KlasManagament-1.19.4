package org.klasfimo.klasManagament.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.klasfimo.klasManagament.KlasManagament;

public class MessageUtils {
    
    private final KlasManagament plugin;
    
    public MessageUtils(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    public String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }
    
    public void sendMessage(Player player, String message) {
        player.sendMessage(colorize(message));
    }
    
    public String getMessage(String path) {
        return colorize(plugin.getConfigManager().getString("messages." + path, "&cMesaj bulunamadı: " + path));
    }
    
    public String getMessage(String path, String defaultValue) {
        return colorize(plugin.getConfigManager().getString("messages." + path, defaultValue));
    }
    
    public String replacePlaceholders(String text, org.bukkit.entity.Player player) {
        if (text == null) return "";
        
        // Temel placeholder'ları değiştir
        text = text.replace("{player}", player.getName());
        text = text.replace("{uuid}", player.getUniqueId().toString());
        text = text.replace("{world}", player.getWorld().getName());
        text = text.replace("{online}", String.valueOf(org.bukkit.Bukkit.getOnlinePlayers().size()));
        text = text.replace("{max_online}", String.valueOf(org.bukkit.Bukkit.getMaxPlayers()));
        
        // PlaceholderAPI entegrasyonu
        if (org.bukkit.Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                // PlaceholderAPI kullanılabilir durumda ise
                // text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
            } catch (Exception e) {
                // PlaceholderAPI hatası durumunda temel placeholder'ları kullan
            }
        }
        
        return colorize(text);
    }
    
    public String replacePlaceholders(String text, org.bukkit.entity.Player player, String targetName) {
        if (text == null) return "";
        
        // Hedef oyuncu placeholder'ları
        text = text.replace("{target}", targetName);
        text = text.replace("{target_player}", targetName);
        
        // Normal placeholder'ları değiştir
        return replacePlaceholders(text, player);
    }
    
    public String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }
} 