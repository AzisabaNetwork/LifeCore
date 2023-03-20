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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerMoneyCommand implements CommandExecutor {
    private static final DecimalFormat FORMATTER_COMMAS = new DecimalFormat("#,###.00");
    private final Map<UUID, Double> map = new ConcurrentHashMap<>();
    private final LifeCore plugin;

    public ServerMoneyCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Map.Entry<UUID, Double>> diff = new ArrayList<>();
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
                DBConnector.runPrepareStatement("SELECT `player_uuid`, (`money` + `offline_money`) AS `money` FROM `" + dbName + "`.`mpdb_economy`", ps -> {
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                        double newMoney = rs.getDouble("money");
                        Double oldMoney = map.put(uuid, newMoney);
                        if (oldMoney != null && oldMoney != newMoney) {
                            diff.add(new HashMap.SimpleEntry<>(uuid, newMoney - oldMoney));
                        }
                    }
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            //noinspection RedundantOperationOnEmptyContainer
            diff.sort((o1, o2) -> (int) (Math.abs(o2.getValue()) - Math.abs(o1.getValue())));
            // diffの中身を最大10個出力
            Consumer<CommandSender> sendDiff = senderIn -> {
                if (!diff.isEmpty()) {
                    for (Map.Entry<UUID, Double> entry : diff.subList(0, Math.min(10, diff.size()))) {
                        String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                        if (name == null) {
                            name = entry.getKey().toString();
                        }
                        senderIn.sendMessage(ChatColor.GOLD + name + ": " + ChatColor.RED + FORMATTER_COMMAS.format(entry.getValue()) + ChatColor.GREEN + "円(" + NumberUtil.toFriendlyString(entry.getValue().longValue()) + ")");
                    }
                }
            };
            String message = ChatColor.GREEN + "現在のサーバー内のお金の合計は" +
                    ChatColor.RED + FORMATTER_COMMAS.format(sum) + ChatColor.GREEN + "円(" + NumberUtil.toFriendlyString((long) sum) + ")です。";
            sender.sendMessage(message);
            sendDiff.accept(sender);
            if (sender instanceof RemoteConsoleCommandSender) {
                Bukkit.getConsoleSender().sendMessage(message);
                sendDiff.accept(Bukkit.getConsoleSender());
            }
        });
        return true;
    }
}
