package com.github.mori01231.lifecore.util

import net.minecraft.server.v1_15_R1.BlockPosition
import net.minecraft.server.v1_15_R1.PacketPlayOutOpenSignEditor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object PromptSign {

    private val awaitingSign = ConcurrentHashMap<UUID, (List<String>) -> Unit>()

    @JvmStatic
    fun promptSign(player: Player, action: (List<String>) -> Unit) {
        val loc0 = player.location.clone().apply { y = 0.0 }
        val origBlockData = loc0.block.blockData
        player.sendBlockChange(loc0, Material.AIR.createBlockData())
        player.sendBlockChange(loc0, Material.OAK_SIGN.createBlockData())
        awaitingSign[player.uniqueId] = {
            player.sendBlockChange(loc0, origBlockData)
            action(it)
        }
        (player as CraftPlayer).handle.playerConnection
            .sendPacket(PacketPlayOutOpenSignEditor(BlockPosition(loc0.blockX, loc0.blockY, loc0.blockZ)))
    }
}