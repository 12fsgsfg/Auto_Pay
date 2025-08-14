package com.autopay.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EssentialsEconomyProvider implements EconomyProvider {
    
    private Object essentialsEconomy;
    private boolean available = false;
    
    public EssentialsEconomyProvider() {
        initialize();
    }
    
    private void initialize() {
        Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentials != null) {
            Bukkit.getLogger().info("檢測到 Essentials 插件: " + essentials.getDescription().getVersion());
            
            // 嘗試多種方法獲取經濟管理器
            try {
                // 方法1: 嘗試標準的 getEconomy 方法
                try {
                    Class<?> essentialsClass = Class.forName("com.earth2me.essentials.Essentials");
                    if (essentialsClass.isInstance(essentials)) {
                        Object economyManager = essentialsClass.getMethod("getEconomy").invoke(essentials);
                        if (economyManager != null) {
                            essentialsEconomy = economyManager;
                            available = true;
                            Bukkit.getLogger().info("Essentials 經濟插件已找到 (方法1): " + economyManager.getClass().getName());
                            return;
                        }
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().info("方法1 失敗: " + e.getMessage());
                }
                
                // 方法2: 嘗試通過 ServicesManager 獲取
                try {
                    if (Bukkit.getServicesManager().isProvidedFor(Class.forName("net.milkbowl.vault.economy.Economy"))) {
                        Object economy = Bukkit.getServicesManager().getRegistration(Class.forName("net.milkbowl.vault.economy.Economy")).getProvider();
                        if (economy != null) {
                            essentialsEconomy = economy;
                            available = true;
                            Bukkit.getLogger().info("Essentials 經濟插件已找到 (方法2): " + economy.getClass().getName());
                            return;
                        }
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().info("方法2 失敗: " + e.getMessage());
                }
                
                // 方法3: 直接使用指令方式（備用方案）
                Bukkit.getLogger().info("使用指令方式作為經濟提供者");
                available = true;
                essentialsEconomy = "COMMAND_MODE";
                
            } catch (Exception e) {
                Bukkit.getLogger().warning("Essentials 經濟插件初始化失敗: " + e.getMessage());
            }
        }
        
        if (!available) {
            Bukkit.getLogger().warning("Essentials 經濟插件未就緒");
        }
    }
    
    @Override
    public boolean isAvailable() {
        return available && essentialsEconomy != null;
    }
    
    @Override
    public boolean depositPlayer(Player player, double amount) {
        if (!isAvailable()) {
            Bukkit.getLogger().warning("Essentials 經濟插件不可用，無法執行存款操作");
            return false;
        }
        
        try {
            // 檢查是否使用指令模式
            if ("COMMAND_MODE".equals(essentialsEconomy)) {
                // 使用 Essentials 的經濟指令
                String command = "eco give " + player.getName() + " " + amount;
                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                
                if (success) {
                    // 只在調試模式下輸出詳細日誌
                    if (Bukkit.getPluginManager().getPlugin("AutoPay") != null && 
                        Bukkit.getPluginManager().getPlugin("AutoPay").getConfig().getBoolean("debug.enabled", false)) {
                        Bukkit.getLogger().info("成功給玩家 " + player.getName() + " 存款 " + amount + " (指令模式)");
                    }
                } else {
                    Bukkit.getLogger().warning("給玩家 " + player.getName() + " 存款 " + amount + " 失敗 (指令模式)");
                }
                
                return success;
            } else {
                // 使用反射調用方法
                try {
                    Object result = essentialsEconomy.getClass()
                        .getMethod("depositPlayer", String.class, double.class)
                        .invoke(essentialsEconomy, player.getName(), amount);
                    
                    if (result instanceof Boolean) {
                        boolean success = (Boolean) result;
                        // 只在調試模式下輸出詳細日誌
                        if (Bukkit.getPluginManager().getPlugin("AutoPay") != null && 
                            Bukkit.getPluginManager().getPlugin("AutoPay").getConfig().getBoolean("debug.enabled", false)) {
                            Bukkit.getLogger().info("成功給玩家 " + player.getName() + " 存款 " + amount + " (API 模式)");
                        }
                        return success;
                    } else {
                        // 只在調試模式下輸出詳細日誌
                        if (Bukkit.getPluginManager().getPlugin("AutoPay") != null && 
                            Bukkit.getPluginManager().getPlugin("AutoPay").getConfig().getBoolean("debug.enabled", false)) {
                            Bukkit.getLogger().info("給玩家 " + player.getName() + " 存款 " + amount + " (API 模式，無返回值)");
                        }
                        return true;
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().warning("API 模式失敗，回退到指令模式: " + e.getMessage());
                    // 回退到指令模式
                    String command = "eco give " + player.getName() + " " + amount;
                    return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("存款操作出錯: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean withdrawPlayer(Player player, double amount) {
        if (!isAvailable()) {
            return false;
        }
        
        try {
            // 檢查是否使用指令模式
            if ("COMMAND_MODE".equals(essentialsEconomy)) {
                // 使用 Essentials 的經濟指令
                String command = "eco take " + player.getName() + " " + amount;
                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                // 只在調試模式下輸出詳細日誌
                if (Bukkit.getPluginManager().getPlugin("AutoPay") != null && 
                    Bukkit.getPluginManager().getPlugin("AutoPay").getConfig().getBoolean("debug.enabled", false)) {
                    Bukkit.getLogger().info("扣款操作 " + (success ? "成功" : "失敗") + " (指令模式)");
                }
                return success;
            } else {
                // 使用反射調用方法
                try {
                    Object result = essentialsEconomy.getClass()
                        .getMethod("withdrawPlayer", String.class, double.class)
                        .invoke(essentialsEconomy, player.getName(), amount);
                    
                    if (result instanceof Boolean) {
                        boolean success = (Boolean) result;
                        // 只在調試模式下輸出詳細日誌
                        if (Bukkit.getPluginManager().getPlugin("AutoPay") != null && 
                            Bukkit.getPluginManager().getPlugin("AutoPay").getConfig().getBoolean("debug.enabled", false)) {
                            Bukkit.getLogger().info("扣款操作 " + (success ? "成功" : "失敗") + " (API 模式)");
                        }
                        return success;
                    } else {
                        // 只在調試模式下輸出詳細日誌
                        if (Bukkit.getPluginManager().getPlugin("AutoPay") != null && 
                            Bukkit.getPluginManager().getPlugin("AutoPay").getConfig().getBoolean("debug.enabled", false)) {
                            Bukkit.getLogger().info("扣款操作完成 (API 模式，無返回值)");
                        }
                        return true;
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().warning("API 模式失敗，回退到指令模式: " + e.getMessage());
                    // 回退到指令模式
                    String command = "eco take " + player.getName() + " " + amount;
                    return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("扣款操作出錯: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public double getBalance(Player player) {
        if (!isAvailable()) {
            Bukkit.getLogger().warning("Essentials 經濟插件不可用，無法查詢餘額");
            return 0.0;
        }
        
        try {
            // 檢查是否使用指令模式
            if ("COMMAND_MODE".equals(essentialsEconomy)) {
                // 指令模式下無法直接獲取餘額，使用指令查詢
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco balance " + player.getName());
                // 只在調試模式下輸出詳細日誌
                if (Bukkit.getPluginManager().getPlugin("AutoPay") != null && 
                    Bukkit.getPluginManager().getPlugin("AutoPay").getConfig().getBoolean("debug.enabled", false)) {
                    Bukkit.getLogger().info("已使用指令查詢玩家 " + player.getName() + " 的餘額");
                }
                return 0.0; // 指令模式下返回 0，實際餘額會通過指令顯示
            } else {
                // 嘗試使用反射獲取餘額
                if (essentialsEconomy != null) {
                    try {
                        // 嘗試調用 getBalance 方法
                        Object result = essentialsEconomy.getClass()
                            .getMethod("getBalance", String.class)
                            .invoke(essentialsEconomy, player.getName());
                        
                        if (result instanceof Number) {
                            double balance = ((Number) result).doubleValue();
                            // 只在調試模式下輸出詳細日誌
                            if (Bukkit.getPluginManager().getPlugin("AutoPay") != null && 
                                Bukkit.getPluginManager().getPlugin("AutoPay").getConfig().getBoolean("debug.enabled", false)) {
                                Bukkit.getLogger().info("玩家 " + player.getName() + " 餘額: " + balance);
                            }
                            return balance;
                        }
                    } catch (NoSuchMethodException e) {
                        // 如果沒有 getBalance 方法，嘗試其他方法
                        // 只在調試模式下輸出詳細日誌
                        if (Bukkit.getPluginManager().getPlugin("AutoPay") != null && 
                            Bukkit.getPluginManager().getPlugin("AutoPay").getConfig().getBoolean("debug.enabled", false)) {
                            Bukkit.getLogger().info("使用指令方式查詢餘額");
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco balance " + player.getName());
                    }
                }
            }
            
            // 如果都失敗了，返回 0
            return 0.0;
        } catch (Exception e) {
            Bukkit.getLogger().severe("餘額查詢出錯: " + e.getMessage());
            return 0.0;
        }
    }
    
    @Override
    public String format(double amount) {
        return String.valueOf(amount);
    }
    
    /**
     * 強制重新初始化經濟插件
     */
    public void reinitialize() {
        available = false;
        essentialsEconomy = null;
        Bukkit.getLogger().info("正在重新初始化 Essentials 經濟插件...");
        initialize();
    }
}
