package org.klasfimo.klasManagament.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.klasfimo.klasManagament.KlasManagament;

public class MenuListener implements Listener {
    
    private final KlasManagament plugin;
    
    public MenuListener(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof org.bukkit.entity.Player)) {
            return;
        }
        
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Yetkili süre menüsü kontrolü
        if (title.contains("Yetkili Süre Sıralaması")) {
            event.setCancelled(true);
            handleYetkiliSureMenuClick(player, event.getSlot());
        }
        
        // Kullanıcı bilgileri menüsü kontrolü
        if (title.contains("Bilgileri")) {
            event.setCancelled(true);
            handleKullaniciMenuClick(player, event.getSlot());
        }
        
        // Dupe IP menüsü kontrolü
        if (title.contains("Dupe IP Kullanıcıları")) {
            event.setCancelled(true);
        }
        
        // Sohbet geçmişi menüsü kontrolü
        if (title.contains("Sohbet Geçmişi")) {
            event.setCancelled(true);
            handleChatHistoryMenuClick(player, event.getSlot());
        }
        
        // Cezalar menüsü kontrolü
        if (title.contains("Cezalar")) {
            event.setCancelled(true);
        }
        
        // Yetkililer menüsü kontrolü
        if (title.contains("Yetkililer")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof org.bukkit.entity.Player)) {
            return;
        }
        
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getPlayer();
        String title = event.getView().getTitle();
        
        // Kullanıcı bilgileri menüsü kapatıldığında hedefi temizle
        if (title.contains("Bilgileri")) {
            plugin.getKullaniciManager().removeMenuTarget(player);
        }
    }
    
    private void handleYetkiliSureMenuClick(org.bukkit.entity.Player player, int slot) {
        // Kalıcı sıralama slot'u
        if (slot == plugin.getConfigManager().getInt("yetkili-sure.items.kalici-siralama.slot", 20)) {
            // Kalıcı sıralama menüsünü aç
            openKaliciSiralamaMenu(player);
        }
        
        // Haftalık sıralama slot'u
        if (slot == plugin.getConfigManager().getInt("yetkili-sure.items.haftalik-siralama.slot", 24)) {
            // Haftalık sıralama menüsünü aç
            openHaftalikSiralamaMenu(player);
        }
    }
    
    private void handleKullaniciMenuClick(org.bukkit.entity.Player player, int slot) {
        // Hedef oyuncuyu al
        org.bukkit.entity.Player target = plugin.getKullaniciManager().getMenuTarget(player);
        if (target == null) {
            plugin.getMessageUtils().sendMessage(player, "&cHedef oyuncu bulunamadı!");
            return;
        }
        
        // Dupe IP slot'u
        if (slot == plugin.getConfigManager().getInt("kullanici-menu.items.dupe-ip.slot", 12)) {
            // Dupe IP menüsünü aç
            plugin.getKullaniciManager().openDupeIPMenu(player, target);
        }
        
        // Cezalar slot'u
        if (slot == plugin.getConfigManager().getInt("kullanici-menu.items.cezalar.slot", 14)) {
            // Cezalar menüsünü aç
            plugin.getKullaniciManager().openCezalarMenu(player, target);
        }
        
        // Sohbet kayıtları slot'u
        if (slot == plugin.getConfigManager().getInt("kullanici-menu.items.sohbet-kayitlari.slot", 16)) {
            // Sohbet kayıtları menüsünü aç
            plugin.getKullaniciManager().openChatHistoryMenu(player, target);
        }
    }
    
    private void openKaliciSiralamaMenu(org.bukkit.entity.Player player) {
        // Kalıcı sıralama menüsünü aç
        plugin.getYetkiliSureManager().openKaliciSiralamaMenu(player);
    }
    
    private void openHaftalikSiralamaMenu(org.bukkit.entity.Player player) {
        // Haftalık sıralama menüsünü aç
        plugin.getYetkiliSureManager().openHaftalikSiralamaMenu(player);
    }
    
    private void handleChatHistoryMenuClick(org.bukkit.entity.Player player, int slot) {
        // Hedef oyuncuyu al
        org.bukkit.entity.Player target = plugin.getKullaniciManager().getMenuTarget(player);
        if (target == null) {
            plugin.getMessageUtils().sendMessage(player, "&cHedef oyuncu bulunamadı!");
            return;
        }
        
        // Sayfalama butonları
        if (slot == 45) { // Önceki sayfa
            // Mevcut sayfa bilgisini al ve önceki sayfaya git
            openChatHistoryMenu(player, target, getCurrentPage(player) - 1);
        } else if (slot == 53) { // Sonraki sayfa
            // Mevcut sayfa bilgisini al ve sonraki sayfaya git
            openChatHistoryMenu(player, target, getCurrentPage(player) + 1);
        }
    }
    
    private void openChatHistoryMenu(org.bukkit.entity.Player player, org.bukkit.entity.Player target, int page) {
        plugin.getKullaniciManager().openChatHistoryMenu(player, target, page);
    }
    
    private int getCurrentPage(org.bukkit.entity.Player player) {
        // KullaniciManager'dan mevcut sayfa bilgisini al
        return plugin.getKullaniciManager().getCurrentPage(player);
    }
} 