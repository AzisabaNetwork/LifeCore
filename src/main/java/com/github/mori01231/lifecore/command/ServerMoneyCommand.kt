package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.DBConnector
import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.TableKey
import com.github.mori01231.lifecore.util.NumberUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.RemoteConsoleCommandSender
import java.text.DecimalFormat
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

class ServerMoneyCommand(private val plugin: LifeCore) : CommandExecutor {
    private val map: MutableMap<UUID, Double> = ConcurrentHashMap()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val diff = mutableListOf<Pair<UUID, Double>>()
            val dbName = plugin.getDatabaseConfig().getDatabaseName(TableKey.MPDB)
            val sum = DBConnector.getPrepareStatement("SELECT (SUM(`money`) + SUM(`offline_money`)) AS `sum` FROM `$dbName`.`mpdb_economy`") { ps ->
                val rs = ps.executeQuery()
                if (rs.next()) rs.getDouble("sum") else 0.0
            }
            DBConnector.runPrepareStatement("SELECT `player_uuid`, (`money` + `offline_money`) AS `money` FROM `$dbName`.`mpdb_economy`") { ps ->
                val rs = ps.executeQuery()
                while (rs.next()) {
                    val uuid = UUID.fromString(rs.getString("player_uuid"))
                    val newMoney = rs.getDouble("money")
                    val oldMoney = map.put(uuid, newMoney)
                    if (oldMoney != null && oldMoney != newMoney) {
                        diff.add(uuid to newMoney - oldMoney)
                    }
                }
            }
            // diffの中身を最大10個出力
            val topDiff = diff.sortedByDescending { abs(it.second) }.take(10)
            val sendDiff = { target: CommandSender ->
                for ((uuid, amount) in topDiff) {
                    val name = Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
                    target.sendMessage(
                        Component.text("$name: ", NamedTextColor.GOLD)
                            .append(Component.text(FORMATTER_COMMAS.format(amount), NamedTextColor.RED))
                            .append(Component.text("円(" + NumberUtil.toFriendlyString(amount.toLong()) + ")", NamedTextColor.GREEN))
                    )
                }
            }
            val message = Component.text("現在のサーバー内のお金の合計は", NamedTextColor.GREEN)
                .append(Component.text(FORMATTER_COMMAS.format(sum), NamedTextColor.RED))
                .append(Component.text("円(" + NumberUtil.toFriendlyString(sum.toLong()) + ")です。", NamedTextColor.GREEN))
            sender.sendMessage(message)
            sendDiff(sender)
            if (sender is RemoteConsoleCommandSender) {
                Bukkit.getConsoleSender().sendMessage(message)
                sendDiff(Bukkit.getConsoleSender())
            }
        })
        return true
    }

    companion object {
        private val FORMATTER_COMMAS = DecimalFormat("#,###.00")
    }
}
