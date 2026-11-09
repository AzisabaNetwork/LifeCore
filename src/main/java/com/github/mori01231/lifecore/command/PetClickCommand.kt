package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.config.PetClickFile
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class PetClickCommand : PlayerTabExecutor() {
    override fun execute(player: Player, args: Array<String>): Boolean {
        if (PetClickFile.isDisabled(player.uniqueId)) {
            PetClickFile.enable(player.uniqueId)
            player.sendMessage(Component.text("ペットに対してShift+右クリックができるようになりました。", NamedTextColor.GREEN))
        } else {
            PetClickFile.disable(player.uniqueId)
            player.sendMessage(Component.text("ペットに対してShift+右クリックができなくなりました。", NamedTextColor.GREEN))
        }
        return true
    }
}
