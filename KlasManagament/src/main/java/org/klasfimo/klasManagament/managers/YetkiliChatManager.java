package org.klasfimo.klasManagament.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.klasfimo.klasManagament.KlasManagament;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class YetkiliChatManager {
    
    private final KlasManagament plugin;
    private final Set<UUID> yetkiliChatEnabled;
    
    public YetkiliChatManager(KlasManagament plugin) {
        this.plugin = plugin;
        this.yetkiliChatEnabled = new HashSet<>();
    }
    
    public void enableYetkiliChat(Player player) {
        yetkiliChatEnabled.add(player.getUniqueId());
    }
    
    public void disableYetkiliChat(Player player) {
        yetkiliChatEnabled.remove(player.getUniqueId());
    }
    
    public boolean isYetkiliChatEnabled(Player player) {
        return yetkiliChatEnabled.contains(player.getUniqueId());
    }
    
    public void sendYetkiliMessage(Player sender, String message) {
        String format = plugin.getConfigManager().getString("yetkili-chat.format", "&8[&cYetkili&8] &7{player}: &f{message}");
        
        String formattedMessage = format
            .replace("{player}", sender.getName())
            .replace("{message}", message);
        
        String finalMessage = plugin.getMessageUtils().colorize(formattedMessage);
        
        // Sadece yetkili sohbeti aktif olan ve yetkisi olan oyunculara gönder
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("klas.yetkilichat")) {
                player.sendMessage(finalMessage);
            }
        }
    }
    
    public void shutdown() {
        yetkiliChatEnabled.clear();
    }
} 