package org.klasfimo.klasManagament.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.klasfimo.klasManagament.KlasManagament;

public class YetkiliSureSifirlaCommand implements CommandExecutor {
    
    private final KlasManagament plugin;
    
    public YetkiliSureSifirlaCommand(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("klas.yetkilisure.reset")) {
            plugin.getMessageUtils().sendMessage(sender, "&cBu komutu kullanmaya yetkiniz yok!");
            return true;
        }
        
        try {
            // Haftalık süreleri sıfırla
            plugin.getYetkiliSureManager().resetWeeklyTimes();
            
            // Başarı mesajı
            plugin.getMessageUtils().sendMessage(sender, "&aHaftalık yetkili süreleri başarıyla sıfırlandı!");
            plugin.getMessageUtils().sendMessage(sender, "&7Discord'a sıralama bildirimi gönderildi.");
            
            // Console'a log
            plugin.getLogger().info("Haftalık yetkili süreleri manuel olarak sıfırlandı! (Komut: " + sender.getName() + ")");
            
        } catch (Exception e) {
            plugin.getLogger().severe("Haftalık süreler sıfırlanırken hata oluştu: " + e.getMessage());
            plugin.getMessageUtils().sendMessage(sender, "&cHaftalık süreler sıfırlanırken hata oluştu!");
            plugin.getMessageUtils().sendMessage(sender, "&7Lütfen console'u kontrol edin.");
        }
        
        return true;
    }
} 