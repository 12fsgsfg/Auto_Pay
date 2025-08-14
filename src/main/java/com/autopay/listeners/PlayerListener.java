package com.autopay.listeners;

import com.autopay.AutoPay;
import com.autopay.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    private final AutoPay plugin;
    
    public PlayerListener(AutoPay plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 檢查玩家是否啟用通知
        boolean notificationsEnabled = plugin.getDatabaseManager().areNotificationsEnabled(player.getUniqueId());
        
        if (notificationsEnabled) {
            player.sendMessage(MessageUtils.colorize("&a歡迎回來！自動付款通知已啟用。"));
            player.sendMessage(MessageUtils.colorize("&7使用 &e/autopay status &7查看詳細信息"));
        } else {
            player.sendMessage(MessageUtils.colorize("&e歡迎回來！自動付款通知已停用。"));
            player.sendMessage(MessageUtils.colorize("&7使用 &e/autopay on &7啟用通知"));
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 玩家離開時可以執行一些清理操作
        // 目前不需要特殊處理
    }
}
