package com.autopay.managers;

import com.autopay.AutoPay;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final AutoPay plugin;
    private FileConfiguration config;
    
    // 配置值
    private boolean autoPayEnabled;
    private int interval;
    private double amount;
    private boolean notificationsEnabled;
    private String notificationMessage;
    private boolean soundEnabled;
    private Sound soundType;
    private float soundVolume;
    private float soundPitch;
    
    public ConfigManager(AutoPay plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // 讀取自動付款設置
        autoPayEnabled = config.getBoolean("auto-pay.enabled", true);
        interval = config.getInt("auto-pay.interval", 30);
        amount = config.getDouble("auto-pay.amount", 100.0);
        
        // 讀取通知設置
        notificationsEnabled = config.getBoolean("auto-pay.notifications.enabled", true);
        notificationMessage = config.getString("auto-pay.notifications.message", 
            "&a[自動付款] &f您收到了 &e{amount} &f金錢！");
        
        // 讀取音效設置
        soundEnabled = config.getBoolean("auto-pay.notifications.sound.enabled", true);
        try {
            soundType = Sound.valueOf(config.getString("auto-pay.notifications.sound.type", "ENTITY_PLAYER_LEVELUP"));
        } catch (IllegalArgumentException e) {
            soundType = Sound.ENTITY_PLAYER_LEVELUP;
        }
        soundVolume = (float) config.getDouble("auto-pay.notifications.sound.volume", 1.0);
        soundPitch = (float) config.getDouble("auto-pay.notifications.sound.pitch", 1.0);
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }
    
    // Getter 方法
    public boolean isAutoPayEnabled() {
        return autoPayEnabled;
    }
    
    public int getInterval() {
        return interval;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    public String getNotificationMessage() {
        return notificationMessage;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public Sound getSoundType() {
        return soundType;
    }
    
    public float getSoundVolume() {
        return soundVolume;
    }
    
    public float getSoundPitch() {
        return soundPitch;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
}
