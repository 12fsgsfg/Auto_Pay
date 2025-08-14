package com.autopay.managers;

import com.autopay.AutoPay;
import com.autopay.utils.MessageUtils;
import com.autopay.economy.EconomyProvider;
import com.autopay.economy.SmartEconomyProvider;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AutoPayManager {
    
    private final AutoPay plugin;
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;
    private BukkitTask autoPayTask;
    private EconomyProvider economy;
    
    public AutoPayManager(AutoPay plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.databaseManager = plugin.getDatabaseManager();
        
        // 初始化經濟提供者
        economy = new SmartEconomyProvider();
    }
    
    public void startAutoPayTask() {
        if (!configManager.isAutoPayEnabled()) {
            plugin.getLogger().info("自動付款已停用");
            return;
        }
        
        if (economy == null || !economy.isAvailable()) {
            plugin.getLogger().severe("未找到經濟插件！請安裝 Vault 和經濟插件");
            plugin.getLogger().severe("已安裝的插件: " + getInstalledPlugins());
            return;
        }
        
        plugin.getLogger().info("經濟插件已連接: " + economy.getClass().getSimpleName());
        
        int intervalTicks = configManager.getInterval() * 60 * 20; // 轉換為遊戲刻
        
        autoPayTask = new BukkitRunnable() {
            @Override
            public void run() {
                executeAutoPay();
            }
        }.runTaskTimer(plugin, intervalTicks, intervalTicks);
        
        plugin.getLogger().info("自動付款任務已啟動，間隔: " + configManager.getInterval() + " 分鐘");
    }
    
    private String getInstalledPlugins() {
        StringBuilder sb = new StringBuilder();
        for (org.bukkit.plugin.Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(plugin.getName()).append(" v").append(plugin.getDescription().getVersion());
        }
        return sb.toString();
    }
    
    public void stopAutoPayTask() {
        if (autoPayTask != null) {
            autoPayTask.cancel();
            autoPayTask = null;
            plugin.getLogger().info("自動付款任務已停止");
        }
    }
    
    private void executeAutoPay() {
        double amount = configManager.getAmount();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            // 檢查玩家是否啟用通知
            if (databaseManager.areNotificationsEnabled(player.getUniqueId())) {
                // 給玩家錢
                economy.depositPlayer(player, amount);
                
                // 發送通知
                if (configManager.isNotificationsEnabled()) {
                    String message = configManager.getNotificationMessage()
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{player}", player.getName());
                    
                    player.sendMessage(MessageUtils.colorize(message));
                }
                
                // 播放音效
                if (configManager.isSoundEnabled()) {
                    Sound sound = configManager.getSoundType();
                    float volume = configManager.getSoundVolume();
                    float pitch = configManager.getSoundPitch();
                    
                    player.playSound(player.getLocation(), sound, volume, pitch);
                }
                
                // 只在調試模式下輸出詳細日誌
                if (plugin.getConfigManager().getConfig().getBoolean("debug.enabled", false)) {
                    plugin.getLogger().info("已給玩家 " + player.getName() + " 發放 " + amount + " 金錢");
                }
            }
        }
    }
    
    public void reload() {
        stopAutoPayTask();
        startAutoPayTask();
    }
    
    public void reinitializeEconomy() {
        if (economy instanceof SmartEconomyProvider) {
            ((SmartEconomyProvider) economy).reinitialize();
        }
    }
    
    public EconomyProvider getEconomy() {
        return economy;
    }
}
