package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.DBConnector;
import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.TableKey;
import com.github.mori01231.lifecore.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class ServerMoneyCommand implements CommandExecutor {
    private static final DecimalFormat FORMATTER_COMMAS = new DecimalFormat("#,###.00");
    private final LifeCore plugin;

    public ServerMoneyCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            double sum;
            try {
                String dbName = plugin.getDatabaseConfig().getDatabaseName(TableKey.MPDB);
                sum = DBConnector.getPrepareStatement("SELECT (SUM(`money`) + SUM(`offline_money`)) AS `sum` FROM `" + dbName + "`.`mpdb_economy`", ps -> {
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        return null;
                    }
                    return rs.getDouble("sum");
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                    String message = ChatColor.GREEN + "現在のサーバー内のお金の合計は" +
                            ChatColor.RED + FORMATTER_COMMAS.format(sum) + ChatColor.GREEN + "円(" + NumberUtil.toFriendlyString((long) sum) + ")です。";
                    sender.sendMessage(message);
                    if (sender instanceof RemoteConsoleCommandSender) {
                        Bukkit.getConsoleSender().sendMessage(message);
                    }
            });
        });
        return true;
    }
}
