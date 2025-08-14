package com.autopay.economy;

import org.bukkit.entity.Player;

public class DummyEconomyProvider implements EconomyProvider {
    
    @Override
    public boolean isAvailable() {
        return false; // 虛擬提供者永遠不可用
    }
    
    @Override
    public boolean depositPlayer(Player player, double amount) {
        return false;
    }
    
    @Override
    public boolean withdrawPlayer(Player player, double amount) {
        return false;
    }
    
    @Override
    public double getBalance(Player player) {
        return 0.0;
    }
    
    @Override
    public String format(double amount) {
        return String.valueOf(amount);
    }
}
