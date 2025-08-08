package org.klasfimo.klasManagament.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.klasfimo.klasManagament.KlasManagament;

public class ReloadCommand implements CommandExecutor {
    
    private final KlasManagament plugin;
    
    public ReloadCommand(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("klas.reload")) {
            plugin.getMessageUtils().sendMessage(sender, "&cBu komutu kullanmaya yetkiniz yok!");
            return true;
        }
        
        try {
            // Config'i yeniden yükle
            plugin.reloadConfig();
            plugin.getConfigManager().reloadConfig();
            
            // Mesajları yeniden yükle
            plugin.getMessageUtils().sendMessage(sender, "&aConfig dosyası yeniden yüklendi!");
            
            // Manager'ları yeniden başlat
            plugin.getLogger().info("Plugin yapılandırmaları yeniden yüklendi!");
            
            // Başarı mesajı
            plugin.getMessageUtils().sendMessage(sender, "&aKlas Management başarıyla yeniden yüklendi!");
            plugin.getMessageUtils().sendMessage(sender, "&7Tüm ayarlar ve mesajlar güncellendi.");
            
        } catch (Exception e) {
            plugin.getLogger().severe("Plugin yeniden yüklenirken hata oluştu: " + e.getMessage());
            plugin.getMessageUtils().sendMessage(sender, "&cPlugin yeniden yüklenirken hata oluştu!");
            plugin.getMessageUtils().sendMessage(sender, "&7Lütfen console'u kontrol edin.");
        }
        
        return true;
    }
} 