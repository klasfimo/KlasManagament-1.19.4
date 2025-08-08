package org.klasfimo.klasManagament;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.klasfimo.klasManagament.commands.*;
import org.klasfimo.klasManagament.listeners.*;
import org.klasfimo.klasManagament.managers.*;
import org.klasfimo.klasManagament.utils.ConfigManager;
import org.klasfimo.klasManagament.utils.MessageUtils;

public final class KlasManagament extends JavaPlugin {

    private static KlasManagament instance;
    private ConfigManager configManager;
    private MessageUtils messageUtils;
    private YetkiliChatManager yetkiliChatManager;
    private YetkiliSureManager yetkiliSureManager;
    private IzlemeManager izlemeManager;
    private KullaniciManager kullaniciManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Config yöneticilerini başlat
        configManager = new ConfigManager(this);
        messageUtils = new MessageUtils(this);
        
        // Manager'ları başlat
        yetkiliChatManager = new YetkiliChatManager(this);
        yetkiliSureManager = new YetkiliSureManager(this);
        izlemeManager = new IzlemeManager(this);
        kullaniciManager = new KullaniciManager(this);
        
        // Komutları kaydet
        registerCommands();
        
        // Event listener'ları kaydet
        registerListeners();
        
        // Süre takip sistemini başlat
        startTimeTracking();
        
        // PlaceholderAPI kontrolü
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI bulundu! Placeholder desteği aktif.");
        } else {
            getLogger().warning("PlaceholderAPI bulunamadı! Placeholder desteği devre dışı.");
        }
        
        // LiteBans kontrolü
        if (getServer().getPluginManager().getPlugin("LiteBans") != null) {
            getLogger().info("LiteBans bulundu! Ban sistemi entegrasyonu aktif.");
        } else {
            getLogger().warning("LiteBans bulunamadı! Ban sistemi entegrasyonu devre dışı.");
        }
        
        // LuckPerms kontrolü
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            getLogger().info("LuckPerms bulundu! İzin sistemi entegrasyonu aktif.");
        } else {
            getLogger().warning("LuckPerms bulunamadı! İzin sistemi entegrasyonu devre dışı.");
        }
        
        getLogger().info("Klas Management Plugin başarıyla yüklendi!");
    }

    @Override
    public void onDisable() {
        // Tüm manager'ları kapat
        if (yetkiliChatManager != null) {
            yetkiliChatManager.shutdown();
        }
        if (yetkiliSureManager != null) {
            yetkiliSureManager.shutdown();
        }
        if (izlemeManager != null) {
            izlemeManager.shutdown();
        }
        if (kullaniciManager != null) {
            kullaniciManager.shutdown();
        }
        
        getLogger().info("Klas Management Plugin kapatıldı!");
    }
    
    private void registerCommands() {
        getCommand("yetkilichat").setExecutor(new YetkiliChatCommand(this));
        getCommand("yetkilisure").setExecutor(new YetkiliSureCommand(this));
        getCommand("izle").setExecutor(new IzleCommand(this));
        getCommand("kullanici").setExecutor(new KullaniciCommand(this));
        getCommand("yetkililer").setExecutor(new YetkililerCommand(this));
        getCommand("klasmanagement").setExecutor(new ReloadCommand(this));
        getCommand("yetkilisuresifirla").setExecutor(new YetkiliSureSifirlaCommand(this));
    }
    
    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new MenuListener(this), this);
    }
    
    private void startTimeTracking() {
        // Süre takip sistemini başlat
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
                if (player.hasPermission("klas.yetkilisure")) {
                    // Her saniye 1 saniye ekle
                    yetkiliSureManager.addTime(player, 1);
                }
            }
        }, 20L, 20L); // 20 tick = 1 saniye
    }
    
    // Getter metodları
    public static KlasManagament getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MessageUtils getMessageUtils() {
        return messageUtils;
    }
    
    public YetkiliChatManager getYetkiliChatManager() {
        return yetkiliChatManager;
    }
    
    public YetkiliSureManager getYetkiliSureManager() {
        return yetkiliSureManager;
    }
    
    public IzlemeManager getIzlemeManager() {
        return izlemeManager;
    }
    
    public KullaniciManager getKullaniciManager() {
        return kullaniciManager;
    }
}
