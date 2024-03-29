package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.DBConnector;
import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.TableKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.Map;

public class DebtCommand implements CommandExecutor {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.##");
    private final LifeCore plugin;

    public DebtCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            double money;
            double offlineMoney;
            try {
                String dbName = plugin.getDatabaseConfig().getDatabaseName(TableKey.MPDB);
                Map.Entry<Double, Double> entry = DBConnector.getPrepareStatement("SELECT `money`, `offline_money` FROM `" + dbName + "`.`mpdb_economy` WHERE `player_uuid` = ? OR LOWER(`player_name`) = LOWER(?)", ps -> {
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
                    Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりませんでした。"));
                    return;
                } else {
                    money = entry.getKey();
                    offlineMoney = entry.getValue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            double varDebt = Math.max(0.0, -(money + offlineMoney));
            if (varDebt == -0.0) {
                varDebt = 0.0;
            }
            double debt = varDebt;
            String details = ChatColor.GRAY + "(" + DECIMAL_FORMAT.format(money) + " + " + DECIMAL_FORMAT.format(offlineMoney) + ")";
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.GREEN + "現在の借金は" + ChatColor.RED + DECIMAL_FORMAT.format(debt) + ChatColor.GREEN + "円です。" + details);
                } else {
                    sender.sendMessage(ChatColor.GREEN + target + "の現在の借金は" + ChatColor.RED + DECIMAL_FORMAT.format(debt) + ChatColor.GREEN + "円です。" + details);
                }
            });
        });
        return true;
    }
}
