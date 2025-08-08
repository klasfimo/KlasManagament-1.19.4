package org.klasfimo.klasManagament.managers;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.klasfimo.klasManagament.KlasManagament;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IzlemeManager {
    
    private final KlasManagament plugin;
    private final Map<UUID, UUID> watchingPlayers; // watcher -> target
    private final Map<UUID, GameMode> previousGameModes;
    private final Map<UUID, Location> previousLocations;
    
    public IzlemeManager(KlasManagament plugin) {
        this.plugin = plugin;
        this.watchingPlayers = new HashMap<>();
        this.previousGameModes = new HashMap<>();
        this.previousLocations = new HashMap<>();
    }
    
    public void startWatching(Player watcher, Player target) {
        UUID watcherId = watcher.getUniqueId();
        UUID targetId = target.getUniqueId();
        
        // Önceki durumu kaydet
        previousGameModes.put(watcherId, watcher.getGameMode());
        previousLocations.put(watcherId, watcher.getLocation());
        
        // Spectator moduna geç
        watcher.setGameMode(GameMode.SPECTATOR);
        
        // Hedef oyuncuya ışınla
        watcher.teleport(target.getLocation());
        
        // İzleme durumunu kaydet
        watchingPlayers.put(watcherId, targetId);
    }
    
    public void stopWatching(Player watcher) {
        UUID watcherId = watcher.getUniqueId();
        
        // İzleme durumunu kaldır
        watchingPlayers.remove(watcherId);
        
        // Önceki game mode'a geri dön
        GameMode previousMode = previousGameModes.get(watcherId);
        if (previousMode != null) {
            watcher.setGameMode(previousMode);
            previousGameModes.remove(watcherId);
        }
        
        // Önceki konuma geri dön
        Location previousLocation = previousLocations.get(watcherId);
        if (previousLocation != null) {
            watcher.teleport(previousLocation);
            previousLocations.remove(watcherId);
        }
    }
    
    public boolean isWatching(Player watcher, Player target) {
        UUID watcherId = watcher.getUniqueId();
        UUID targetId = target.getUniqueId();
        
        UUID currentTarget = watchingPlayers.get(watcherId);
        return targetId.equals(currentTarget);
    }
    
    public boolean isWatching(Player watcher) {
        return watchingPlayers.containsKey(watcher.getUniqueId());
    }
    
    public Player getWatchedPlayer(Player watcher) {
        UUID targetId = watchingPlayers.get(watcher.getUniqueId());
        if (targetId != null) {
            return plugin.getServer().getPlayer(targetId);
        }
        return null;
    }
    
    public void checkDistance(Player watcher) {
        if (!isWatching(watcher)) return;
        
        Player target = getWatchedPlayer(watcher);
        if (target == null || !target.isOnline()) {
            stopWatching(watcher);
            return;
        }
        
        int maxDistance = plugin.getConfigManager().getInt("izleme.max-distance", 15);
        double distance = watcher.getLocation().distance(target.getLocation());
        
        if (distance > maxDistance) {
            // Oyuncudan çok uzaklaştı, geri döndür
            watcher.teleport(target.getLocation());
            plugin.getMessageUtils().sendMessage(watcher, 
                plugin.getMessageUtils().getMessage("izleme.too-far")
            );
        }
    }
    
    public void shutdown() {
        // Tüm izleme durumlarını temizle
        for (Player watcher : plugin.getServer().getOnlinePlayers()) {
            if (isWatching(watcher)) {
                stopWatching(watcher);
            }
        }
        
        watchingPlayers.clear();
        previousGameModes.clear();
        previousLocations.clear();
    }
} 