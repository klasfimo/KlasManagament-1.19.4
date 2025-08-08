package org.klasfimo.klasManagament.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.klasfimo.klasManagament.KlasManagament;

public class ChatListener implements Listener {
    
    private final KlasManagament plugin;
    
    public ChatListener(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Yetkili sohbeti aktif olan oyuncuların mesajlarını yakala
        if (plugin.getYetkiliChatManager().isYetkiliChatEnabled(event.getPlayer())) {
            event.setCancelled(true);
            
            // Yetkili sohbetine gönder
            plugin.getYetkiliChatManager().sendYetkiliMessage(event.getPlayer(), event.getMessage());
        }
        
        // Tüm mesajları kaydet
        plugin.getKullaniciManager().addPlayerMessage(event.getPlayer(), event.getMessage());
    }
} 