package com.autopay.economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SmartEconomyProvider implements EconomyProvider {
    
    private EconomyProvider selectedProvider;
    private String providerName;
    
    public SmartEconomyProvider() {
        selectBestProvider();
    }
    
    private void selectBestProvider() {
        // 優先選擇 Essentials
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            try {
                selectedProvider = new EssentialsEconomyProvider();
                if (selectedProvider.isAvailable()) {
                    providerName = "Essentials";
                    Bukkit.getLogger().info("已選擇 Essentials 作為經濟提供者");
                    return;
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("Essentials 經濟提供者初始化失敗: " + e.getMessage());
            }
        }
        
        // 其次選擇 Vault
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            try {
                selectedProvider = new VaultEconomyProvider();
                if (selectedProvider.isAvailable()) {
                    providerName = "Vault";
                    Bukkit.getLogger().info("已選擇 Vault 作為經濟提供者");
                    return;
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("Vault 經濟提供者初始化失敗: " + e.getMessage());
            }
        }
        
        // 如果都沒有，創建一個虛擬提供者
        selectedProvider = new DummyEconomyProvider();
        providerName = "Dummy";
        Bukkit.getLogger().warning("未找到可用的經濟插件，使用虛擬提供者");
    }
    
    @Override
    public boolean isAvailable() {
        return selectedProvider != null && selectedProvider.isAvailable();
    }
    
    @Override
    public boolean depositPlayer(org.bukkit.entity.Player player, double amount) {
        if (selectedProvider != null) {
            return selectedProvider.depositPlayer(player, amount);
        }
        return false;
    }
    
    @Override
    public boolean withdrawPlayer(org.bukkit.entity.Player player, double amount) {
        if (selectedProvider != null) {
            return selectedProvider.withdrawPlayer(player, amount);
        }
        return false;
    }
    
    @Override
    public double getBalance(org.bukkit.entity.Player player) {
        if (selectedProvider != null) {
            return selectedProvider.getBalance(player);
        }
        return 0.0;
    }
    
    @Override
    public String format(double amount) {
        if (selectedProvider != null) {
            return selectedProvider.format(amount);
        }
        return String.valueOf(amount);
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void reinitialize() {
        Bukkit.getLogger().info("正在重新初始化經濟提供者...");
        selectBestProvider();
    }
}
