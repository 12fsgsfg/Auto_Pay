package com.autopay.commands;

import com.autopay.AutoPay;
import com.autopay.utils.MessageUtils;
import com.autopay.economy.EconomyProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoPayCommand implements CommandExecutor, TabCompleter {
    
    private final AutoPay plugin;
    
    public AutoPayCommand(AutoPay plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&c此指令只能由玩家執行！"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "on":
                enableNotifications(player);
                break;
            case "off":
                disableNotifications(player);
                break;
            case "status":
                showStatus(player);
                break;
            case "reload":
                if (player.hasPermission("autopay.admin")) {
                    reloadPlugin(player);
                } else {
                    player.sendMessage(MessageUtils.colorize("&c您沒有權限執行此指令！"));
                }
                break;
            case "test":
                if (player.hasPermission("autopay.admin")) {
                    testEconomy(player);
                } else {
                    player.sendMessage(MessageUtils.colorize("&c您沒有權限執行此指令！"));
                }
                break;
            case "reinit":
                if (player.hasPermission("autopay.admin")) {
                    reinitializeEconomy(player);
                } else {
                    player.sendMessage(MessageUtils.colorize("&c您沒有權限執行此指令！"));
                }
                break;
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage(MessageUtils.colorize("&6=== AutoPay 指令幫助 ==="));
        player.sendMessage(MessageUtils.colorize("&e/autopay on &7- 啟用自動付款通知"));
        player.sendMessage(MessageUtils.colorize("&e/autopay off &7- 停用自動付款通知"));
        player.sendMessage(MessageUtils.colorize("&e/autopay status &7- 查看通知狀態"));
        if (player.hasPermission("autopay.admin")) {
            player.sendMessage(MessageUtils.colorize("&e/autopay reload &7- 重新載入插件配置"));
            player.sendMessage(MessageUtils.colorize("&e/autopay test &7- 測試經濟插件連接"));
            player.sendMessage(MessageUtils.colorize("&e/autopay reinit &7- 重新初始化經濟插件"));
        }
    }
    
    private void enableNotifications(Player player) {
        plugin.getDatabaseManager().setNotificationsEnabled(player.getUniqueId(), true);
        player.sendMessage(MessageUtils.colorize("&a已啟用自動付款通知！"));
    }
    
    private void disableNotifications(Player player) {
        plugin.getDatabaseManager().setNotificationsEnabled(player.getUniqueId(), false);
        player.sendMessage(MessageUtils.colorize("&c已停用自動付款通知！"));
    }
    
    private void showStatus(Player player) {
        boolean enabled = plugin.getDatabaseManager().areNotificationsEnabled(player.getUniqueId());
        String status = enabled ? "&a啟用" : "&c停用";
        player.sendMessage(MessageUtils.colorize("&6自動付款通知狀態: " + status));
        
        if (enabled) {
            double amount = plugin.getConfigManager().getAmount();
            int interval = plugin.getConfigManager().getInterval();
            player.sendMessage(MessageUtils.colorize("&7每次付款金額: &e" + amount));
            player.sendMessage(MessageUtils.colorize("&7付款間隔: &e" + interval + " 分鐘"));
        }
    }
    
    private void reloadPlugin(Player player) {
        plugin.getConfigManager().reloadConfig();
        plugin.getAutoPayManager().reload();
        player.sendMessage(MessageUtils.colorize("&a插件配置已重新載入！"));
    }
    
    private void testEconomy(Player player) {
        player.sendMessage(MessageUtils.colorize("&6=== 經濟插件測試 ==="));
        
        if (plugin.getAutoPayManager().getEconomy() != null) {
            EconomyProvider economy = plugin.getAutoPayManager().getEconomy();
            
            if (economy.isAvailable()) {
                player.sendMessage(MessageUtils.colorize("&a✓ 經濟插件已連接"));
                player.sendMessage(MessageUtils.colorize("&7當前餘額: &e" + economy.getBalance(player)));
                
                // 測試給錢
                double testAmount = 10.0;
                if (economy.depositPlayer(player, testAmount)) {
                    player.sendMessage(MessageUtils.colorize("&a✓ 測試付款成功: +" + testAmount));
                    player.sendMessage(MessageUtils.colorize("&7新餘額: &e" + economy.getBalance(player)));
                    
                    // 立即扣除測試金額
                    economy.withdrawPlayer(player, testAmount);
                    player.sendMessage(MessageUtils.colorize("&c測試金額已扣除"));
                } else {
                    player.sendMessage(MessageUtils.colorize("&c✗ 測試付款失敗"));
                }
            } else {
                player.sendMessage(MessageUtils.colorize("&c✗ 經濟插件未就緒"));
            }
        } else {
            player.sendMessage(MessageUtils.colorize("&c✗ 經濟插件未找到"));
        }
        
        player.sendMessage(MessageUtils.colorize("&7使用 &e/bal &7檢查餘額"));
    }
    
    private void reinitializeEconomy(Player player) {
        player.sendMessage(MessageUtils.colorize("&6正在重新初始化經濟插件..."));
        plugin.getAutoPayManager().reinitializeEconomy();
        player.sendMessage(MessageUtils.colorize("&a經濟插件已重新初始化！"));
        player.sendMessage(MessageUtils.colorize("&7請使用 &e/autopay test &7測試連接"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("on", "off", "status");
            
            if (sender.hasPermission("autopay.admin")) {
                subCommands = Arrays.asList("on", "off", "status", "reload", "test", "reinit");
            }
            
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        }
        
        return completions;
    }
}
