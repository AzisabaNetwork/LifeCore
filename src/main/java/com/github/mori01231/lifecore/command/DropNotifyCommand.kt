package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.config.DropNotifyFile
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class DropNotifyCommand : PlayerTabExecutor() {
    override fun execute(player: Player, args: Array<String>): Boolean {
        if (DropNotifyFile.contains(player.uniqueId)) {
            DropNotifyFile.remove(player.uniqueId)
            player.sendMessage(Component.text("ドロップ通知をオフにしました。", NamedTextColor.GREEN))
        } else {
            DropNotifyFile.add(player.uniqueId)
            player.sendMessage(Component.text("ドロップ通知をオンにしました。", NamedTextColor.GREEN))
        }
        return true
    }
}
