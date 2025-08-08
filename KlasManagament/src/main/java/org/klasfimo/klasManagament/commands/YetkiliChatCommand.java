package org.klasfimo.klasManagament.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.klasfimo.klasManagament.KlasManagament;
import org.klasfimo.klasManagament.managers.YetkiliChatManager;

public class YetkiliChatCommand implements CommandExecutor {
    
    private final KlasManagament plugin;
    
    public YetkiliChatCommand(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtils().sendMessage(sender, "&cBu komut sadece oyuncular tarafından kullanılabilir!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("klas.yetkilichat")) {
            plugin.getMessageUtils().sendMessage(player, plugin.getMessageUtils().getMessage("yetkili-chat.no-permission"));
            return true;
        }
        
        YetkiliChatManager manager = plugin.getYetkiliChatManager();
        
        if (manager.isYetkiliChatEnabled(player)) {
            manager.disableYetkiliChat(player);
            plugin.getMessageUtils().sendMessage(player, plugin.getMessageUtils().getMessage("yetkili-chat.disabled"));
        } else {
            manager.enableYetkiliChat(player);
            plugin.getMessageUtils().sendMessage(player, plugin.getMessageUtils().getMessage("yetkili-chat.enabled"));
        }
        
        return true;
    }
} 