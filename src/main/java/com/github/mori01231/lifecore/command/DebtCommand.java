package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.DBConnector;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

public class DebtCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String target;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must specify a player to check debt to.");
                return true;
            }
            target = ((Player) sender).getUniqueId().toString();
        } else {
            if (!sender.hasPermission("lifecore.debt.others")) {
                sender.sendMessage(ChatColor.RED + "権限がありません。");
                return true;
            }
            target = args[0];
        }
        double money;
        double offlineMoney;
        try {
            Map.Entry<Double, Double> entry = DBConnector.getPrepareStatement("SELECT `money`, `offline_money` FROM `mpdb_economy` WHERE `player_uuid` = ? OR LOWER(`player_name`) = LOWER(?)", ps -> {
                ps.setString(1, target);
                ps.setString(2, target);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                return new AbstractMap.SimpleImmutableEntry<>(
                        rs.getDouble("money"),
                        rs.getDouble("offline_money")
                );
            });
            if (entry == null) {
                sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりませんでした。");
                return true;
            } else {
                money = entry.getKey();
                offlineMoney = entry.getValue();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        double debt = Math.max(0.0, -(money + offlineMoney));
        if (debt == -0.0) {
            debt = 0.0;
        }
        String details = ChatColor.GRAY + "(" + money + " + " + offlineMoney + ")";
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "現在の借金は" + ChatColor.RED + debt + ChatColor.GREEN + "円です。" + details);
        } else {
            sender.sendMessage(ChatColor.GREEN + target + "の現在の借金は" + ChatColor.RED + debt + ChatColor.GREEN + "円です。" + details);
        }
        return true;
    }
}
