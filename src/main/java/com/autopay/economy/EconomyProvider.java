package com.autopay.economy;

import org.bukkit.entity.Player;

public interface EconomyProvider {
    
    /**
     * 檢查經濟插件是否可用
     */
    boolean isAvailable();
    
    /**
     * 給玩家錢
     */
    boolean depositPlayer(Player player, double amount);
    
    /**
     * 從玩家扣除錢
     */
    boolean withdrawPlayer(Player player, double amount);
    
    /**
     * 獲取玩家餘額
     */
    double getBalance(Player player);
    
    /**
     * 格式化金額
     */
    String format(double amount);
}
