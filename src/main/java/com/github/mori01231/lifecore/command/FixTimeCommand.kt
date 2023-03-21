package com.github.mori01231.lifecore.command

import me.armar.plugins.autorank.Autorank
import me.armar.plugins.autorank.storage.TimeType
import me.staartvin.statz.Statz
import me.staartvin.statz.datamanager.player.PlayerStat
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.UUID

object FixTimeCommand : PlayerTabExecutor() {
    private val cooldown = mutableMapOf<UUID, Long>()

    override fun execute(player: Player, args: Array<out String>): Boolean {
        cooldown[player.uniqueId]?.let { expiresAt ->
            if (expiresAt > System.currentTimeMillis() && !player.hasPermission("lifecore.fixtime.bypass-cooldown")) {
                player.sendMessage("${ChatColor.RED}このコマンドは1時間に1回のみ実行できます。")
                return true
            }
        }
        cooldown[player.uniqueId] = System.currentTimeMillis() + 1000 * 60 * 60
        val statz = Statz.getPlugin(Statz::class.java)
        val playedMinutes = statz.dataManager.getPlayerInfo(player.uniqueId).getTotalValue(PlayerStat.TIME_PLAYED)
        Autorank.getInstance().playTimeManager.setGlobalPlayTime(TimeType.TOTAL_TIME, player.uniqueId, playedMinutes.toInt())
        Autorank.getInstance().playTimeManager.setLocalPlayTime(TimeType.TOTAL_TIME, player.uniqueId, playedMinutes.toInt())
        player.sendMessage("${ChatColor.GREEN}Autorankの時間をStatzの時間に同期しました。")
        return true
    }
}
