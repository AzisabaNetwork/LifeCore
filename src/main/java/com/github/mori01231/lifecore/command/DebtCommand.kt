package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.DBConnector
import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.TableKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.text.DecimalFormat
import kotlin.math.max

class DebtCommand(private val plugin: LifeCore) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val target = if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendMessage(Component.text("You must specify a player to check debt to.", NamedTextColor.RED))
                return true
            }
            sender.uniqueId.toString()
        } else {
            if (!sender.hasPermission("lifecore.debt.others")) {
                sender.sendMessage(Component.text("権限がありません。", NamedTextColor.RED))
                return true
            }
            args[0]
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val dbName = plugin.getDatabaseConfig().getDatabaseName(TableKey.MPDB)
            val entry = DBConnector.getPrepareStatement(
                "SELECT `money`, `offline_money` FROM `$dbName`.`mpdb_economy` WHERE `player_uuid` = ? OR LOWER(`player_name`) = LOWER(?)"
            ) { ps ->
                ps.setString(1, target)
                ps.setString(2, target)
                val rs = ps.executeQuery()
                if (rs.next()) rs.getDouble("money") to rs.getDouble("offline_money") else null
            }
            if (entry == null) {
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    sender.sendMessage(Component.text("プレイヤーが見つかりませんでした。", NamedTextColor.RED))
                })
                return@Runnable
            }
            val (money, offlineMoney) = entry
            // "+ 0.0" turns -0.0 into 0.0
            val debt = max(0.0, -(money + offlineMoney)) + 0.0
            val details = Component.text(
                "(" + DECIMAL_FORMAT.format(money) + " + " + DECIMAL_FORMAT.format(offlineMoney) + ")",
                NamedTextColor.GRAY
            )
            val prefix = if (args.isEmpty()) "現在の借金は" else target + "の現在の借金は"
            Bukkit.getScheduler().runTask(plugin, Runnable {
                sender.sendMessage(
                    Component.text(prefix, NamedTextColor.GREEN)
                        .append(Component.text(DECIMAL_FORMAT.format(debt), NamedTextColor.RED))
                        .append(Component.text("円です。", NamedTextColor.GREEN))
                        .append(details)
                )
            })
        })
        return true
    }

    companion object {
        private val DECIMAL_FORMAT = DecimalFormat("#,###.##")
    }
}
