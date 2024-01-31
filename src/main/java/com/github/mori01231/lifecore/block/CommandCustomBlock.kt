package com.github.mori01231.lifecore.block

import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class CommandCustomBlock(
    override val name: String,
    override val lockFacing: Boolean,
    override val axisShift: Int,
    override val backgroundBlock: Material,
    material: Material,
    displayName: String? = null,
    lore: List<String>? = null,
    private val commands: List<String>,
    private val consoleCommands: List<String>,
    private val destroyWithoutWrench: Boolean,
) : CustomBlock(material, displayName, lore) {
    override fun onInteract(e: PlayerInteractEvent, state: CustomBlockState) {
        if (e.hand != EquipmentSlot.HAND) return
        commands.forEach { e.player.performCommand(it) }
        consoleCommands.forEach { e.player.server.dispatchCommand(e.player.server.consoleSender, it.replace("<player>", e.player.name)) }
    }

    override fun canDestroy(state: CustomBlockState, wrench: Boolean): Boolean = wrench || destroyWithoutWrench
}
