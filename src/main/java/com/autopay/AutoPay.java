package com.autopay;

import com.autopay.commands.AutoPayCommand;
import com.autopay.listeners.PlayerListener;
import com.autopay.managers.AutoPayManager;
import com.autopay.managers.ConfigManager;
import com.autopay.managers.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoPay extends JavaPlugin {
    
    private static AutoPay instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private AutoPayManager autoPayManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化配置管理器
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // 初始化數據庫管理器
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        
        // 初始化自動付款管理器
        autoPayManager = new AutoPayManager(this);
        
        // 註冊指令
        getCommand("autopay").setExecutor(new AutoPayCommand(this));
        
        // 註冊監聽器
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // 啟動自動付款任務
        autoPayManager.startAutoPayTask();
        
        getLogger().info("AutoPay 插件已啟用！");
    }
    
    @Override
    public void onDisable() {
        if (autoPayManager != null) {
            autoPayManager.stopAutoPayTask();
        }
        
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        getLogger().info("AutoPay 插件已停用！");
    }
    
    public static AutoPay getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public AutoPayManager getAutoPayManager() {
        return autoPayManager;
    }
}
