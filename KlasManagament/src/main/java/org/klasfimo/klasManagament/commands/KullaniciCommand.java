package org.klasfimo.klasManagament.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.klasfimo.klasManagament.KlasManagament;
import org.klasfimo.klasManagament.managers.KullaniciManager;

public class KullaniciCommand implements CommandExecutor {
    
    private final KlasManagament plugin;
    
    public KullaniciCommand(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtils().sendMessage(sender, "&cBu komut sadece oyuncular tarafından kullanılabilir!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("klas.kullanici")) {
            plugin.getMessageUtils().sendMessage(player, "&cBu komutu kullanmaya yetkiniz yok!");
            return true;
        }
        
        if (args.length != 1) {
            plugin.getMessageUtils().sendMessage(player, "&cKullanım: /kullanici <oyuncu>");
            return true;
        }
        
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            plugin.getMessageUtils().sendMessage(player, plugin.getMessageUtils().getMessage("kullanici.player-not-found"));
            return true;
        }
        
        KullaniciManager manager = plugin.getKullaniciManager();
        manager.openKullaniciMenu(player, target);
        
        return true;
    }
} 