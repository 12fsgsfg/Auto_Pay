package com.autopay.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyProvider implements EconomyProvider {
    
    private Object economy;
    private boolean available = false;
    
    public VaultEconomyProvider() {
        initialize();
    }
    
    private void initialize() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            try {
                // 等待一下讓其他插件完全載入
                Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("AutoPay"), () -> {
                    try {
                        RegisteredServiceProvider<?> provider = Bukkit.getServicesManager()
                            .getRegistration(Class.forName("net.milkbowl.vault.economy.Economy"));
                        
                        if (provider != null) {
                            economy = provider.getProvider();
                            available = true;
                            Bukkit.getLogger().info("Vault 經濟插件已找到: " + economy.getClass().getName());
                            
                            // 測試經濟插件是否真的可用
                            if (Bukkit.getOnlinePlayers().size() > 0) {
                                Player testPlayer = Bukkit.getOnlinePlayers().iterator().next();
                                try {
                                    double currentBalance = getBalance(testPlayer);
                                    Bukkit.getLogger().info("經濟插件測試成功，玩家 " + testPlayer.getName() + " 餘額: " + currentBalance);
                                } catch (Exception e) {
                                    Bukkit.getLogger().warning("經濟插件測試失敗: " + e.getMessage());
                                    available = false;
                                }
                            }
                        } else {
                            Bukkit.getLogger().warning("Vault 已安裝但未找到經濟服務提供者");
                            Bukkit.getLogger().warning("請檢查 EssentialsX 或其他經濟插件是否正確安裝");
                        }
                    } catch (ClassNotFoundException e) {
                        Bukkit.getLogger().warning("Vault 經濟 API 類未找到: " + e.getMessage());
                    }
                }, 20L); // 等待 1 秒
                
            } catch (Exception e) {
                Bukkit.getLogger().warning("初始化經濟提供者時出錯: " + e.getMessage());
            }
        } else {
            Bukkit.getLogger().warning("Vault 插件未安裝");
        }
        
        // 檢查是否有經濟插件
        if (!available) {
            Bukkit.getLogger().warning("請確保已安裝以下插件之一:");
            Bukkit.getLogger().warning("- EssentialsX (推薦)");
            Bukkit.getLogger().warning("- iConomy");
            Bukkit.getLogger().warning("- BOSEconomy");
            Bukkit.getLogger().warning("- 或其他支持 Vault 的經濟插件");
        }
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    @Override
    public boolean depositPlayer(Player player, double amount) {
        if (!available || economy == null) {
            Bukkit.getLogger().warning("經濟插件不可用，無法執行存款操作");
            return false;
        }
        
        try {
            // 使用反射調用 Vault 經濟插件方法
            Object result = economy.getClass().getMethod("depositPlayer", Player.class, double.class)
                .invoke(economy, player, amount);
            
            // 檢查結果
            if (result instanceof Boolean) {
                boolean success = (Boolean) result;
                if (success) {
                    Bukkit.getLogger().info("成功給玩家 " + player.getName() + " 存款 " + amount);
                } else {
                    Bukkit.getLogger().warning("給玩家 " + player.getName() + " 存款 " + amount + " 失敗");
                }
                return success;
            } else {
                // 如果方法沒有返回值，假設成功
                Bukkit.getLogger().info("給玩家 " + player.getName() + " 存款 " + amount + " (無返回值)");
                return true;
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("存款操作出錯: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean withdrawPlayer(Player player, double amount) {
        if (!available || economy == null) {
            return false;
        }
        
        try {
            economy.getClass().getMethod("withdrawPlayer", Player.class, double.class)
                .invoke(economy, player, amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public double getBalance(Player player) {
        if (!available || economy == null) {
            Bukkit.getLogger().warning("經濟插件不可用，無法查詢餘額");
            return 0.0;
        }
        
        try {
            Object result = economy.getClass().getMethod("getBalance", Player.class)
                .invoke(economy, player);
            
            if (result instanceof Number) {
                double balance = ((Number) result).doubleValue();
                Bukkit.getLogger().info("玩家 " + player.getName() + " 餘額: " + balance);
                return balance;
            } else {
                Bukkit.getLogger().warning("餘額查詢返回無效結果: " + result);
                return 0.0;
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("餘額查詢出錯: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }
    
    @Override
    public String format(double amount) {
        if (!available || economy == null) {
            return String.valueOf(amount);
        }
        
        try {
            Object result = economy.getClass().getMethod("format", double.class)
                .invoke(economy, amount);
            return result != null ? result.toString() : String.valueOf(amount);
        } catch (Exception e) {
            return String.valueOf(amount);
        }
    }
    
    /**
     * 強制重新初始化經濟插件
     */
    public void reinitialize() {
        available = false;
        economy = null;
        Bukkit.getLogger().info("正在重新初始化經濟插件...");
        initialize();
    }
}
