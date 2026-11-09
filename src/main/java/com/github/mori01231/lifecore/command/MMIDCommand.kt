package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.util.ItemUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class MMIDCommand : PlayerTabExecutor() {
    override fun execute(player: Player, args: Array<String>): Boolean {
        val target = if (args.isNotEmpty() && player.hasPermission("lifecore.mmid.others")) {
            Bukkit.getPlayerExact(args[0])
        } else {
            player
        }
        if (target == null) {
            player.sendMessage(Component.text("プレイヤーが見つかりませんでした。", NamedTextColor.RED))
            return true
        }
        val id = ItemUtil.getMythicType(target.inventory.itemInMainHand)
        val clickableId = Component.text(id ?: "<null>", NamedTextColor.AQUA)
            .hoverEvent(HoverEvent.showText(Component.text("クリックでコピー")))
            .clickEvent(ClickEvent.copyToClipboard(id ?: "null"))
        player.sendMessage(Component.text("ID: ").append(clickableId))
        return true
    }
}
