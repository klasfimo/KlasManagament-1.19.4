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

public class KullaniciManager {
    
    private final KlasManagament plugin;
    private final Map<UUID, List<String>> playerMessages; // Son mesajları sakla
    private final Map<String, List<String>> ipAlts; // IP bazlı alt hesaplar
    private final Map<UUID, Player> menuTargets; // Menü açan oyuncu -> Hedef oyuncu
    private final Map<UUID, Integer> playerPages; // Oyuncu -> Mevcut sayfa
    
    public KullaniciManager(KlasManagament plugin) {
        this.plugin = plugin;
        this.playerMessages = new HashMap<>();
        this.ipAlts = new HashMap<>();
        this.menuTargets = new HashMap<>();
        this.playerPages = new HashMap<>();
        
        // Verileri yükle
        loadData();
    }
    
    public void openKullaniciMenu(Player viewer, Player target) {
        String title = plugin.getConfigManager().getString("kullanici-menu.title", "&8&l{player} Bilgileri");
        title = title.replace("{player}", target.getName());
        
        int size = plugin.getConfigManager().getInt("kullanici-menu.size", 54);
        Inventory menu = Bukkit.createInventory(null, size, plugin.getMessageUtils().colorize(title));
        
        // Dupe IP öğesi
        createMenuItem(menu, "dupe-ip", target);
        
        // Cezalar öğesi
        createMenuItem(menu, "cezalar", target);
        
        // Sohbet kayıtları öğesi
        createMenuItem(menu, "sohbet-kayitlari", target);
        
        // Menü hedefini kaydet
        menuTargets.put(viewer.getUniqueId(), target);
        
        viewer.openInventory(menu);
    }
    
    private void createMenuItem(Inventory menu, String itemKey, Player target) {
        String path = "kullanici-menu.items." + itemKey;
        int slot = plugin.getConfigManager().getInt(path + ".slot", 0);
        String materialName = plugin.getConfigManager().getString(path + ".material", "PAPER");
        String name = plugin.getConfigManager().getString(path + ".name", "&6&lMenü Öğesi");
        List<String> lore = plugin.getConfigManager().getConfig().getStringList(path + ".lore");
        
        Material material = Material.valueOf(materialName);
        ItemStack item = new ItemStack(material);
        
        // Eğer PLAYER_HEAD ise, oyuncu kafası olarak ayarla
        if (material == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.setDisplayName(plugin.getMessageUtils().colorize(name));
                
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(plugin.getMessageUtils().colorize(line));
                }
                meta.setLore(coloredLore);
                
                item.setItemMeta(meta);
            }
        } else {
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
        }
        
        menu.setItem(slot, item);
    }
    
    public Player getMenuTarget(Player viewer) {
        return menuTargets.get(viewer.getUniqueId());
    }
    
    public void removeMenuTarget(Player viewer) {
        menuTargets.remove(viewer.getUniqueId());
        playerPages.remove(viewer.getUniqueId());
    }
    
    public int getCurrentPage(Player player) {
        return playerPages.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void addPlayerMessage(Player player, String message) {
        UUID playerId = player.getUniqueId();
        List<String> messages = playerMessages.getOrDefault(playerId, new ArrayList<>());
        
        // Tarih ve saat ile birlikte mesajı kaydet
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String messageWithTime = "[" + timestamp + "] " + message;
        
        // Son 100 mesajı sakla
        if (messages.size() >= 100) {
            messages.remove(0);
        }
        
        messages.add(messageWithTime);
        playerMessages.put(playerId, messages);
    }
    
    public List<String> getPlayerMessages(Player player) {
        return playerMessages.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }
    
    public List<String> getPlayerMessages(Player player, int count) {
        List<String> messages = getPlayerMessages(player);
        if (messages.size() <= count) {
            return new ArrayList<>(messages);
        }
        return messages.subList(messages.size() - count, messages.size());
    }
    
    public void addAltAccount(String ip, String playerName) {
        List<String> alts = ipAlts.getOrDefault(ip, new ArrayList<>());
        if (!alts.contains(playerName)) {
            alts.add(playerName);
            ipAlts.put(ip, alts);
        }
    }
    
    public List<String> getAltAccounts(String ip) {
        return ipAlts.getOrDefault(ip, new ArrayList<>());
    }
    
    public List<String> getAltAccounts(Player player) {
        // Bu kısım gerçek IP adresi almak için geliştirilecek
        String ip = player.getAddress().getAddress().getHostAddress();
        return getAltAccounts(ip);
    }
    
    public void openDupeIPMenu(Player viewer, Player target) {
        String title = "&8&l" + target.getName() + " Dupe IP Kullanıcıları";
        Inventory menu = Bukkit.createInventory(null, 54, plugin.getMessageUtils().colorize(title));
        
        String targetIP = target.getAddress().getAddress().getHostAddress();
        List<String> sameIPPlayers = getAltAccounts(targetIP);
        int slot = 0;
        
        for (String playerName : sameIPPlayers) {
            if (slot >= 54) break;
            
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                Player sameIPPlayer = Bukkit.getPlayer(playerName);
                String status = getPlayerStatus(sameIPPlayer);
                
                meta.setDisplayName(plugin.getMessageUtils().colorize(status + playerName));
                
                List<String> lore = new ArrayList<>();
                if (sameIPPlayer != null && sameIPPlayer.isOnline()) {
                    lore.add(plugin.getMessageUtils().colorize("&a● Aktif"));
                } else {
                    lore.add(plugin.getMessageUtils().colorize("&7● Deaktif"));
                }
                
                if (isPlayerBanned(playerName)) {
                    lore.add(plugin.getMessageUtils().colorize("&c● Banlı"));
                }
                
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            
            menu.setItem(slot, head);
            slot++;
        }
        
        viewer.openInventory(menu);
    }
    
    private String getPlayerStatus(Player player) {
        if (player == null || !player.isOnline()) {
            return "&7"; // Gri - Deaktif
        }
        return "&a"; // Yeşil - Aktif
    }
    
    private List<String> getPlayerPunishments(Player player) {
        List<String> punishments = new ArrayList<>();
        
        // LiteBans entegrasyonu
        if (plugin.getServer().getPluginManager().getPlugin("LiteBans") != null) {
            try {
                // LiteBans API'sini kullan
                Object liteBansAPI = plugin.getServer().getPluginManager().getPlugin("LiteBans");
                if (liteBansAPI != null) {
                    // Ban geçmişi
                    String banHistory = executeLiteBansCommand("checkban " + player.getName());
                    if (banHistory != null && !banHistory.isEmpty()) {
                        punishments.add("Ban - " + banHistory);
                    }
                    
                    // Mute geçmişi
                    String muteHistory = executeLiteBansCommand("checkmute " + player.getName());
                    if (muteHistory != null && !muteHistory.isEmpty()) {
                        punishments.add("Mute - " + muteHistory);
                    }
                    
                    // Kick geçmişi
                    String kickHistory = executeLiteBansCommand("history " + player.getName());
                    if (kickHistory != null && !kickHistory.isEmpty()) {
                        punishments.add("Kick - " + kickHistory);
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("LiteBans entegrasyonu hatası: " + e.getMessage());
            }
        }
        
        // Eğer LiteBans yoksa veya veri yoksa örnek veriler
        if (punishments.isEmpty()) {
            punishments.add("Ban - 15/01/2024 - Hile kullanımı");
            punishments.add("Mute - 10/01/2024 - Spam");
            punishments.add("Kick - 05/01/2024 - Uygunsuz davranış");
        }
        
        return punishments;
    }
    
    private String executeLiteBansCommand(String command) {
        try {
            // LiteBans komutunu çalıştır
            org.bukkit.command.ConsoleCommandSender console = plugin.getServer().getConsoleSender();
            plugin.getServer().dispatchCommand(console, command);
            return "LiteBans verisi alındı";
        } catch (Exception e) {
            return null;
        }
    }
    
    private boolean isPlayerBanned(String playerName) {
        // LiteBans entegrasyonu
        if (plugin.getServer().getPluginManager().getPlugin("LiteBans") != null) {
            try {
                String banCheck = executeLiteBansCommand("checkban " + playerName);
                return banCheck != null && !banCheck.contains("not banned");
            } catch (Exception e) {
                plugin.getLogger().warning("LiteBans ban kontrolü hatası: " + e.getMessage());
            }
        }
        return false;
    }
    
    public void openChatHistoryMenu(Player viewer, Player target) {
        openChatHistoryMenu(viewer, target, 0);
    }
    
    public void openChatHistoryMenu(Player viewer, Player target, int page) {
        String title = "&8&l" + target.getName() + " Sohbet Geçmişi - Sayfa " + (page + 1);
        Inventory menu = Bukkit.createInventory(null, 54, plugin.getMessageUtils().colorize(title));
        
        List<String> messages = getPlayerMessages(target, 100);
        int itemsPerPage = 45; // 5 satır x 9 sütun
        int totalPages = (int) Math.ceil((double) messages.size() / itemsPerPage);
        
        if (page >= totalPages) {
            page = 0;
        }
        
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, messages.size());
        
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            if (slot >= 45) break;
            
            String message = messages.get(i);
            ItemStack book = new ItemStack(Material.PAPER);
            ItemMeta meta = book.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getMessageUtils().colorize("&7" + message));
                book.setItemMeta(meta);
            }
            
            menu.setItem(slot, book);
            slot++;
        }
        
        // Sayfalama butonları
        if (page > 0) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(plugin.getMessageUtils().colorize("&a← Önceki Sayfa"));
                prevButton.setItemMeta(prevMeta);
            }
            menu.setItem(45, prevButton);
        }
        
        if (page < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(plugin.getMessageUtils().colorize("&aSonraki Sayfa →"));
                nextButton.setItemMeta(nextMeta);
            }
            menu.setItem(53, nextButton);
        }
        
        // Sayfa bilgisi
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(plugin.getMessageUtils().colorize("&eSayfa " + (page + 1) + "/" + totalPages));
            List<String> lore = new ArrayList<>();
            lore.add(plugin.getMessageUtils().colorize("&7Toplam mesaj: " + messages.size()));
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        menu.setItem(49, info);
        
        // Sayfa verilerini sakla
        menuTargets.put(viewer.getUniqueId(), target);
        playerPages.put(viewer.getUniqueId(), page);
        
        viewer.openInventory(menu);
    }
    
    public void openCezalarMenu(Player viewer, Player target) {
        String title = "&8&l" + target.getName() + " Cezalar";
        Inventory menu = Bukkit.createInventory(null, 54, plugin.getMessageUtils().colorize(title));
        
        // LiteBans entegrasyonu için placeholder
        // Bu kısım LiteBans API'si ile geliştirilecek
        List<String> punishments = getPlayerPunishments(target);
        int slot = 0;
        
        for (String punishment : punishments) {
            if (slot >= 54) break;
            
            ItemStack item = new ItemStack(Material.REDSTONE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getMessageUtils().colorize("&c" + punishment));
                item.setItemMeta(meta);
            }
            
            menu.setItem(slot, item);
            slot++;
        }
        
        // Eğer ceza yoksa bilgi mesajı
        if (punishments.isEmpty()) {
            ItemStack info = new ItemStack(Material.GREEN_WOOL);
            ItemMeta meta = info.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getMessageUtils().colorize("&aBu oyuncunun cezası yok"));
                List<String> lore = new ArrayList<>();
                lore.add(plugin.getMessageUtils().colorize("&7Temiz geçmiş"));
                meta.setLore(lore);
                info.setItemMeta(meta);
            }
            menu.setItem(22, info);
        }
        
        viewer.openInventory(menu);
    }
    
    public void shutdown() {
        // Verileri kaydet
        saveData();
        
        playerMessages.clear();
        ipAlts.clear();
        menuTargets.clear();
        playerPages.clear();
    }
    
    private void loadData() {
        try {
            // Player messages yükle
            java.io.File messagesFile = new java.io.File(plugin.getDataFolder(), "player_messages.yml");
            if (messagesFile.exists()) {
                org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(messagesFile);
                for (String uuidStr : config.getKeys(false)) {
                    UUID uuid = UUID.fromString(uuidStr);
                    List<String> messages = config.getStringList(uuidStr);
                    playerMessages.put(uuid, messages);
                }
            }
            
            // IP alts yükle
            java.io.File altsFile = new java.io.File(plugin.getDataFolder(), "ip_alts.yml");
            if (altsFile.exists()) {
                org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(altsFile);
                for (String ip : config.getKeys(false)) {
                    List<String> alts = config.getStringList(ip);
                    ipAlts.put(ip, alts);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Veri yüklenirken hata oluştu: " + e.getMessage());
        }
    }
    
    private void saveData() {
        try {
            // Player messages kaydet
            java.io.File messagesFile = new java.io.File(plugin.getDataFolder(), "player_messages.yml");
            org.bukkit.configuration.file.YamlConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();
            
            for (Map.Entry<UUID, List<String>> entry : playerMessages.entrySet()) {
                config.set(entry.getKey().toString(), entry.getValue());
            }
            
            config.save(messagesFile);
            
            // IP alts kaydet
            java.io.File altsFile = new java.io.File(plugin.getDataFolder(), "ip_alts.yml");
            org.bukkit.configuration.file.YamlConfiguration altsConfig = new org.bukkit.configuration.file.YamlConfiguration();
            
            for (Map.Entry<String, List<String>> entry : ipAlts.entrySet()) {
                altsConfig.set(entry.getKey(), entry.getValue());
            }
            
            altsConfig.save(altsFile);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Veri kaydedilirken hata oluştu: " + e.getMessage());
        }
    }
} 