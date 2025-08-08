package org.klasfimo.klasManagament.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.klasfimo.klasManagament.KlasManagament;

import java.util.ArrayList;
import java.util.List;

public class YetkililerCommand implements org.bukkit.command.CommandExecutor {
    
    private final KlasManagament plugin;
    
    public YetkililerCommand(KlasManagament plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtils().sendMessage(sender, "&cBu komut sadece oyuncular tarafından kullanılabilir!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("klas.yetkililer")) {
            plugin.getMessageUtils().sendMessage(player, plugin.getConfigManager().getString("messages.yetkili-chat.no-permission", "&cBu komutu kullanmaya yetkiniz yok!"));
            return true;
        }
        
        openYetkililerMenu(player);
        return true;
    }
    
    public void openYetkililerMenu(Player player) {
        String title = "&8&lYetkililer";
        Inventory menu = Bukkit.createInventory(null, 54, plugin.getMessageUtils().colorize(title));
        
        List<Player> yetkililer = getYetkililer();
        int slot = 0;
        
        for (Player yetkili : yetkililer) {
            if (slot >= 54) break;
            
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(yetkili);
                
                // Yetkili durumuna göre renk
                String status = yetkili.isOnline() ? "&a" : "&7";
                String onlineStatus = yetkili.isOnline() ? "&a● Online" : "&7● Offline";
                
                meta.setDisplayName(plugin.getMessageUtils().colorize(status + yetkili.getName()));
                
                List<String> lore = new ArrayList<>();
                lore.add(plugin.getMessageUtils().colorize(onlineStatus));
                lore.add(plugin.getMessageUtils().colorize("&7Yetki: &f" + getYetkiliRank(yetkili)));
                lore.add(plugin.getMessageUtils().colorize("&7Kalıcı Süre: &f" + plugin.getMessageUtils().formatTime(plugin.getYetkiliSureManager().getKaliciSure(yetkili))));
                lore.add(plugin.getMessageUtils().colorize("&7Haftalık Süre: &f" + plugin.getMessageUtils().formatTime(plugin.getYetkiliSureManager().getHaftalikSure(yetkili))));
                
                if (yetkili.isOnline()) {
                    lore.add(plugin.getMessageUtils().colorize("&7Dünya: &f" + yetkili.getWorld().getName()));
                    lore.add(plugin.getMessageUtils().colorize("&7Ping: &f" + yetkili.getPing() + "ms"));
                }
                
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            
            menu.setItem(slot, head);
            slot++;
        }
        
        // Eğer yetkili yoksa bilgi mesajı
        if (yetkililer.isEmpty()) {
            ItemStack info = new ItemStack(Material.BARRIER);
            ItemMeta meta = info.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getMessageUtils().colorize("&cHiç yetkili bulunamadı"));
                List<String> lore = new ArrayList<>();
                lore.add(plugin.getMessageUtils().colorize("&7Yetkili olan oyuncular burada"));
                lore.add(plugin.getMessageUtils().colorize("&7görünecektir."));
                meta.setLore(lore);
                info.setItemMeta(meta);
            }
            menu.setItem(22, info);
        }
        
        player.openInventory(menu);
    }
    
    private List<Player> getYetkililer() {
        List<Player> yetkililer = new ArrayList<>();
        
        // Online yetkililer
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("klas.yetkililer")) {
                yetkililer.add(player);
            }
        }
        
        // Offline yetkililer (LuckPerms entegrasyonu ile)
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                // LuckPerms API'sini kullanarak offline yetkilileri bul
                // Bu kısım LuckPerms API'si ile geliştirilecek
            } catch (Exception e) {
                plugin.getLogger().warning("LuckPerms entegrasyonu hatası: " + e.getMessage());
            }
        }
        
        return yetkililer;
    }
    
    private String getYetkiliRank(Player player) {
        // LuckPerms entegrasyonu
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                // LuckPerms'tan yetki seviyesini al
                return "Yetkili"; // Placeholder
            } catch (Exception e) {
                return "Yetkili";
            }
        }
        
        // Temel yetki kontrolü
        if (player.hasPermission("klas.admin")) {
            return "Admin";
        } else if (player.hasPermission("klas.yetkilichat")) {
            return "Moderatör";
        } else {
            return "Yetkili";
        }
    }
} 