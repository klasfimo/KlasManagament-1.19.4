package org.klasfimo.klasManagament.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.klasfimo.klasManagament.KlasManagament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class YetkiliSureManager {
    
    private final KlasManagament plugin;
    private final Map<UUID, Long> kaliciSureler;
    private final Map<UUID, Long> haftalikSureler;
    private final Map<UUID, Long> lastResetTime;
    
    public YetkiliSureManager(KlasManagament plugin) {
        this.plugin = plugin;
        this.kaliciSureler = new HashMap<>();
        this.haftalikSureler = new HashMap<>();
        this.lastResetTime = new HashMap<>();
        
        // Verileri yükle
        loadData();
        
        // Haftalık sıfırlama görevini başlat
        startWeeklyResetTask();
    }
    
    public void openYetkiliSureMenu(Player player) {
        String title = plugin.getConfigManager().getString("yetkili-sure.title", "&8&lYetkili Süre Sıralaması");
        int size = plugin.getConfigManager().getInt("yetkili-sure.size", 54);
        
        Inventory menu = Bukkit.createInventory(null, size, plugin.getMessageUtils().colorize(title));
        
        // Kalıcı sıralama öğesi
        createMenuItem(menu, "kalici-siralama", player);
        
        // Haftalık sıralama öğesi
        createMenuItem(menu, "haftalik-siralama", player);
        
        player.openInventory(menu);
    }
    
    public void openKaliciSiralamaMenu(Player player) {
        String title = "&8&lKalıcı Süre Sıralaması";
        Inventory menu = Bukkit.createInventory(null, 54, plugin.getMessageUtils().colorize(title));
        
        // Süreleri sırala
        List<Map.Entry<UUID, Long>> sortedTimes = kaliciSureler.entrySet().stream()
            .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
            .toList();
        
        int slot = 0;
        for (Map.Entry<UUID, Long> entry : sortedTimes) {
            if (slot >= 54) break;
            
            UUID playerId = entry.getKey();
            Long time = entry.getValue();
            
            Player targetPlayer = Bukkit.getPlayer(playerId);
            if (targetPlayer != null) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                if (meta != null) {
                    meta.setOwningPlayer(targetPlayer);
                    meta.setDisplayName(plugin.getMessageUtils().colorize("&6" + targetPlayer.getName()));
                    
                    List<String> lore = new ArrayList<>();
                    lore.add(plugin.getMessageUtils().colorize("&7Süre: &f" + plugin.getMessageUtils().formatTime(time)));
                    lore.add(plugin.getMessageUtils().colorize("&7Sıra: &f#" + (slot + 1)));
                    
                    meta.setLore(lore);
                    head.setItemMeta(meta);
                }
                
                menu.setItem(slot, head);
                slot++;
            }
        }
        
        player.openInventory(menu);
    }
    
    public void openHaftalikSiralamaMenu(Player player) {
        String title = "&8&lHaftalık Süre Sıralaması";
        Inventory menu = Bukkit.createInventory(null, 54, plugin.getMessageUtils().colorize(title));
        
        // Süreleri sırala
        List<Map.Entry<UUID, Long>> sortedTimes = haftalikSureler.entrySet().stream()
            .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
            .toList();
        
        int slot = 0;
        for (Map.Entry<UUID, Long> entry : sortedTimes) {
            if (slot >= 54) break;
            
            UUID playerId = entry.getKey();
            Long time = entry.getValue();
            
            Player targetPlayer = Bukkit.getPlayer(playerId);
            if (targetPlayer != null) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                if (meta != null) {
                    meta.setOwningPlayer(targetPlayer);
                    meta.setDisplayName(plugin.getMessageUtils().colorize("&a" + targetPlayer.getName()));
                    
                    List<String> lore = new ArrayList<>();
                    lore.add(plugin.getMessageUtils().colorize("&7Süre: &f" + plugin.getMessageUtils().formatTime(time)));
                    lore.add(plugin.getMessageUtils().colorize("&7Sıra: &f#" + (slot + 1)));
                    lore.add(plugin.getMessageUtils().colorize("&7Bu hafta"));
                    
                    meta.setLore(lore);
                    head.setItemMeta(meta);
                }
                
                menu.setItem(slot, head);
                slot++;
            }
        }
        
        player.openInventory(menu);
    }
    
    private void createMenuItem(Inventory menu, String itemKey, Player player) {
        String path = "yetkili-sure.items." + itemKey;
        int slot = plugin.getConfigManager().getInt(path + ".slot", 0);
        String materialName = plugin.getConfigManager().getString(path + ".material", "PAPER");
        String name = plugin.getConfigManager().getString(path + ".name", "&6&lMenü Öğesi");
        List<String> lore = plugin.getConfigManager().getConfig().getStringList(path + ".lore");
        
        Material material = Material.valueOf(materialName);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(plugin.getMessageUtils().colorize(name));
            
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(plugin.getMessageUtils().colorize(line));
            }
            meta.setLore(coloredLore);
            
            item.setItemMeta(meta);
        }
        
        menu.setItem(slot, item);
    }
    
    public void addTime(Player player, long seconds) {
        UUID playerId = player.getUniqueId();
        
        // Kalıcı süre ekle
        kaliciSureler.put(playerId, kaliciSureler.getOrDefault(playerId, 0L) + seconds);
        
        // Haftalık süre ekle
        haftalikSureler.put(playerId, haftalikSureler.getOrDefault(playerId, 0L) + seconds);
    }
    
    public long getKaliciSure(Player player) {
        return kaliciSureler.getOrDefault(player.getUniqueId(), 0L);
    }
    
    public long getHaftalikSure(Player player) {
        return haftalikSureler.getOrDefault(player.getUniqueId(), 0L);
    }
    
    private void startWeeklyResetTask() {
        String resetDay = plugin.getConfigManager().getString("yetkili-sure.weekly-reset.day", "SUNDAY");
        int resetHour = plugin.getConfigManager().getInt("yetkili-sure.weekly-reset.hour", 23);
        int resetMinute = plugin.getConfigManager().getInt("yetkili-sure.weekly-reset.minute", 0);
        
        // Her gün kontrol et
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Haftalık sıfırlama kontrolü
            if (shouldResetWeekly()) {
                resetWeeklyTimes();
            }
        }, 20L * 60 * 60, 20L * 60 * 60); // Her saat kontrol et
    }
    
    private boolean shouldResetWeekly() {
        // Basit haftalık sıfırlama kontrolü
        long currentTime = System.currentTimeMillis();
        long lastReset = lastResetTime.getOrDefault(null, 0L);
        
        // 7 gün geçtiyse sıfırla
        return (currentTime - lastReset) >= (7 * 24 * 60 * 60 * 1000L);
    }
    
    public void resetWeeklyTimes() {
        haftalikSureler.clear();
        lastResetTime.put(null, System.currentTimeMillis());
        
        // Discord'a bildirim gönder (eğer aktifse)
        if (plugin.getConfigManager().getBoolean("yetkili-sure.discord.enabled", false)) {
            sendDiscordNotification();
        }
        
        plugin.getLogger().info("Haftalık yetkili süreleri sıfırlandı!");
    }
    
    private void sendDiscordNotification() {
        if (!plugin.getConfigManager().getBoolean("yetkili-sure.discord.enabled", false)) {
            return;
        }
        
        String webhookUrl = plugin.getConfigManager().getString("yetkili-sure.discord.webhook-url", "");
        if (webhookUrl.isEmpty()) {
            return;
        }
        
        // Discord webhook işlemini asenkron olarak çalıştır
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Discord webhook gönderimi
                String message = "🔄 **Haftalık Yetkili Süreleri Sıfırlandı!**\n\n";
                message += "📊 **Kalıcı Sıralama:**\n";
                
                // En iyi 5 yetkiliyi listele
                List<Map.Entry<UUID, Long>> topPlayers = kaliciSureler.entrySet().stream()
                    .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                    .limit(5)
                    .collect(java.util.stream.Collectors.toList());
                
                for (int i = 0; i < topPlayers.size(); i++) {
                    Map.Entry<UUID, Long> entry = topPlayers.get(i);
                    org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(entry.getKey());
                    if (player != null) {
                        message += (i + 1) + ". **" + player.getName() + "** - " + 
                                  plugin.getMessageUtils().formatTime(entry.getValue()) + "\n";
                    }
                }
                
                // Webhook gönder
                sendDiscordWebhook(webhookUrl, message);
                
            } catch (Exception e) {
                plugin.getLogger().warning("Discord bildirimi gönderilirken hata: " + e.getMessage());
            }
        });
    }
    
    private void sendDiscordWebhook(String webhookUrl, String message) {
        int maxRetries = plugin.getConfigManager().getInt("yetkili-sure.discord.retry-attempts", 3);
        int timeout = plugin.getConfigManager().getInt("yetkili-sure.discord.timeout", 5000);
        boolean debug = plugin.getConfigManager().getBoolean("yetkili-sure.discord.debug", false);
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                java.net.URL url = new java.net.URL(webhookUrl);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "KlasManagement/1.0");
                connection.setDoOutput(true);
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
                
                // JSON payload'ı güvenli şekilde oluştur
                String escapedMessage = message
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
                
                String jsonPayload = "{\"content\":\"" + escapedMessage + "\"}";
                
                if (debug) {
                    plugin.getLogger().info("Discord webhook payload: " + jsonPayload);
                }
                
                try (java.io.OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                int responseCode = connection.getResponseCode();
                if (responseCode == 204) {
                    plugin.getLogger().info("Discord webhook başarıyla gönderildi! (Deneme: " + attempt + ")");
                    return; // Başarılı, döngüden çık
                } else {
                    plugin.getLogger().warning("Discord webhook yanıt kodu: " + responseCode + " (Deneme: " + attempt + ")");
                }
                
            } catch (Exception e) {
                plugin.getLogger().warning("Discord webhook hatası (Deneme " + attempt + "/" + maxRetries + "): " + e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        // Yeniden denemeden önce bekle
                        Thread.sleep(1000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        plugin.getLogger().warning("Discord webhook " + maxRetries + " deneme sonrası başarısız oldu!");
    }
    
    public void shutdown() {
        // Verileri kaydet
        saveData();
        
        kaliciSureler.clear();
        haftalikSureler.clear();
        lastResetTime.clear();
    }
    
    private void loadData() {
        try {
            // Kalıcı süreler yükle
            java.io.File kaliciFile = new java.io.File(plugin.getDataFolder(), "kalici_sureler.yml");
            if (kaliciFile.exists()) {
                org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(kaliciFile);
                for (String uuidStr : config.getKeys(false)) {
                    UUID uuid = UUID.fromString(uuidStr);
                    long time = config.getLong(uuidStr);
                    kaliciSureler.put(uuid, time);
                }
            }
            
            // Haftalık süreler yükle
            java.io.File haftalikFile = new java.io.File(plugin.getDataFolder(), "haftalik_sureler.yml");
            if (haftalikFile.exists()) {
                org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(haftalikFile);
                for (String uuidStr : config.getKeys(false)) {
                    UUID uuid = UUID.fromString(uuidStr);
                    long time = config.getLong(uuidStr);
                    haftalikSureler.put(uuid, time);
                }
            }
            
            // Son sıfırlama zamanları yükle
            java.io.File resetFile = new java.io.File(plugin.getDataFolder(), "last_reset.yml");
            if (resetFile.exists()) {
                org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(resetFile);
                for (String uuidStr : config.getKeys(false)) {
                    UUID uuid = UUID.fromString(uuidStr);
                    long time = config.getLong(uuidStr);
                    lastResetTime.put(uuid, time);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Süre verileri yüklenirken hata oluştu: " + e.getMessage());
        }
    }
    
    private void saveData() {
        try {
            // Kalıcı süreler kaydet
            java.io.File kaliciFile = new java.io.File(plugin.getDataFolder(), "kalici_sureler.yml");
            org.bukkit.configuration.file.YamlConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();
            
            for (Map.Entry<UUID, Long> entry : kaliciSureler.entrySet()) {
                config.set(entry.getKey().toString(), entry.getValue());
            }
            
            config.save(kaliciFile);
            
            // Haftalık süreler kaydet
            java.io.File haftalikFile = new java.io.File(plugin.getDataFolder(), "haftalik_sureler.yml");
            org.bukkit.configuration.file.YamlConfiguration haftalikConfig = new org.bukkit.configuration.file.YamlConfiguration();
            
            for (Map.Entry<UUID, Long> entry : haftalikSureler.entrySet()) {
                haftalikConfig.set(entry.getKey().toString(), entry.getValue());
            }
            
            haftalikConfig.save(haftalikFile);
            
            // Son sıfırlama zamanları kaydet
            java.io.File resetFile = new java.io.File(plugin.getDataFolder(), "last_reset.yml");
            org.bukkit.configuration.file.YamlConfiguration resetConfig = new org.bukkit.configuration.file.YamlConfiguration();
            
            for (Map.Entry<UUID, Long> entry : lastResetTime.entrySet()) {
                resetConfig.set(entry.getKey().toString(), entry.getValue());
            }
            
            resetConfig.save(resetFile);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Süre verileri kaydedilirken hata oluştu: " + e.getMessage());
        }
    }
} 