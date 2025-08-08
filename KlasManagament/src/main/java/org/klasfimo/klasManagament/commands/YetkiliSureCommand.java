package org.klasfimo.klasManagament.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.klasfimo.klasManagament.KlasManagament;
import org.klasfimo.klasManagament.managers.YetkiliSureManager;

public class YetkiliSureCommand implements CommandExecutor {
    
    private final KlasManagament plugin;
    
    public YetkiliSureCommand(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtils().sendMessage(sender, "&cBu komut sadece oyuncular tarafından kullanılabilir!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("klas.yetkilisure")) {
            plugin.getMessageUtils().sendMessage(player, "&cBu komutu kullanmaya yetkiniz yok!");
            return true;
        }
        
        YetkiliSureManager manager = plugin.getYetkiliSureManager();
        manager.openYetkiliSureMenu(player);
        
        return true;
    }
} 