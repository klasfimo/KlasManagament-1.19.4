package org.klasfimo.klasManagament.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.klasfimo.klasManagament.KlasManagament;

public class PlayerListener implements Listener {
    
    private final KlasManagament plugin;
    
    public PlayerListener(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Oyuncu IP'sini alt hesaplar için kaydet
        String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
        plugin.getKullaniciManager().addAltAccount(ip, event.getPlayer().getName());
        
        // Yetkili süre takibi için oyuncuyu kaydet
        // Bu kısım playtime plugin entegrasyonu ile geliştirilecek
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // İzleme durumunu temizle
        if (plugin.getIzlemeManager().isWatching(event.getPlayer())) {
            plugin.getIzlemeManager().stopWatching(event.getPlayer());
        }
        
        // İzlenen oyuncu çıkarsa, izleyenleri de durdur
        for (org.bukkit.entity.Player watcher : Bukkit.getOnlinePlayers()) {
            if (plugin.getIzlemeManager().isWatching(watcher)) {
                if (plugin.getIzlemeManager().getWatchedPlayer(watcher) == event.getPlayer()) {
                    plugin.getIzlemeManager().stopWatching(watcher);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // İzleme mesafesi kontrolü
        if (plugin.getIzlemeManager().isWatching(event.getPlayer())) {
            plugin.getIzlemeManager().checkDistance(event.getPlayer());
        }
    }
    
    // Süre takip sistemi - her saniye çalışır
    public void startTimeTracking() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("klas.yetkilisure")) {
                    // Her saniye 1 saniye ekle
                    plugin.getYetkiliSureManager().addTime(player, 1);
                }
            }
        }, 20L, 20L); // 20 tick = 1 saniye
    }
} 