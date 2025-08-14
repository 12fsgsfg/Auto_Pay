package com.autopay.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class LogUtils {
    
    /**
     * 檢查是否啟用調試模式
     */
    public static boolean isDebugEnabled() {
        Plugin autoPay = Bukkit.getPluginManager().getPlugin("AutoPay");
        return autoPay != null && autoPay.getConfig().getBoolean("debug.enabled", false);
    }
    
    /**
     * 調試日誌（只在調試模式下輸出）
     */
    public static void debug(String message) {
        if (isDebugEnabled()) {
            Bukkit.getLogger().info("[AutoPay Debug] " + message);
        }
    }
    
    /**
     * 信息日誌（總是輸出）
     */
    public static void info(String message) {
        Bukkit.getLogger().info("[AutoPay] " + message);
    }
    
    /**
     * 警告日誌（總是輸出）
     */
    public static void warning(String message) {
        Bukkit.getLogger().warning("[AutoPay] " + message);
    }
    
    /**
     * 錯誤日誌（總是輸出）
     */
    public static void error(String message) {
        Bukkit.getLogger().severe("[AutoPay] " + message);
    }
}
