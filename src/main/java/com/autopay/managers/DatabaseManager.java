package com.autopay.managers;

import com.autopay.AutoPay;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {
    
    private final AutoPay plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Boolean> playerNotifications;
    
    public DatabaseManager(AutoPay plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        this.playerNotifications = new HashMap<>();
    }
    
    public void initialize() {
        if (!dataFile.exists()) {
            plugin.saveResource("data.yml", false);
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadPlayerData();
    }
    
    private void loadPlayerData() {
        if (dataConfig.contains("players")) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    boolean notificationsEnabled = dataConfig.getBoolean("players." + uuidString + ".notifications", true);
                    playerNotifications.put(uuid, notificationsEnabled);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("無效的 UUID: " + uuidString);
                }
            }
        }
    }
    
    public void savePlayerData() {
        for (Map.Entry<UUID, Boolean> entry : playerNotifications.entrySet()) {
            dataConfig.set("players." + entry.getKey().toString() + ".notifications", entry.getValue());
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("無法保存玩家數據: " + e.getMessage());
        }
    }
    
    public boolean areNotificationsEnabled(UUID playerUUID) {
        return playerNotifications.getOrDefault(playerUUID, true);
    }
    
    public void setNotificationsEnabled(UUID playerUUID, boolean enabled) {
        playerNotifications.put(playerUUID, enabled);
        savePlayerData();
    }
    
    public void close() {
        savePlayerData();
    }
}
