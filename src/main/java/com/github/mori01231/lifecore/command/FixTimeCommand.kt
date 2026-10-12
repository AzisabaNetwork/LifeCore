package com.github.mori01231.lifecore.command

import me.armar.plugins.autorank.Autorank
import me.armar.plugins.autorank.storage.TimeType
import me.staartvin.statz.Statz
import me.staartvin.statz.datamanager.player.PlayerStat
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import java.util.UUID

object FixTimeCommand : PlayerTabExecutor() {
    private val cooldown = mutableMapOf<UUID, Long>()

    override fun execute(player: Player, args: Array<out String>): Boolean {
        cooldown[player.uniqueId]?.let { expiresAt ->
            if (expiresAt > System.currentTimeMillis() && !player.hasPermission("lifecore.fixtime.bypass-cooldown")) {
                player.sendMessage(Component.text("このコマンドは1時間に1回のみ実行できます。", NamedTextColor.RED))
                return true
            }
        }
        cooldown[player.uniqueId] = System.currentTimeMillis() + 1000 * 60 * 60
        val statz = Statz.getPlugin(Statz::class.java)
        val playedMinutes = statz.dataManager.getPlayerInfo(player.uniqueId).getTotalValue(PlayerStat.TIME_PLAYED)
        Autorank.getInstance().playTimeManager.setGlobalPlayTime(TimeType.TOTAL_TIME, player.uniqueId, playedMinutes.toInt())
        Autorank.getInstance().playTimeManager.setLocalPlayTime(TimeType.TOTAL_TIME, player.uniqueId, playedMinutes.toInt())
        player.sendMessage(Component.text("Autorankの時間をStatzの時間に同期しました。", NamedTextColor.GREEN))
        return true
    }
}
