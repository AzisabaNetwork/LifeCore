package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.config.DamageLogFile
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class DamageLogCommand : PlayerTabExecutor() {
    override fun execute(player: Player, args: Array<String>): Boolean {
        if (DamageLogFile.isEnabled(player.uniqueId)) {
            DamageLogFile.disable(player.uniqueId)
            player.sendMessage(Component.text("DamageLogが無効化されました", NamedTextColor.RED))
        } else {
            DamageLogFile.enable(player.uniqueId)
            player.sendMessage(Component.text("DamageLogが有効化されました", NamedTextColor.GREEN))
        }
        return true
    }
}
