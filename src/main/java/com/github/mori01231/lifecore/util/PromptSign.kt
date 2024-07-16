package com.github.mori01231.lifecore.util

import com.github.mori01231.lifecore.LifeCore
import net.minecraft.server.v1_15_R1.BlockPosition
import net.minecraft.server.v1_15_R1.CancelledPacketHandleException
import net.minecraft.server.v1_15_R1.PacketPlayOutOpenSignEditor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

object PromptSign {
    internal val awaitingSign = ConcurrentHashMap<UUID, (List<String>) -> Unit>()

    @JvmStatic
    fun promptSign(player: Player, action: Consumer<List<String>>) {
        val loc0 = player.location.clone().apply { y += 3 }
        val origBlockData = loc0.block.blockData
        player.sendBlockChange(loc0, Material.AIR.createBlockData())
        player.sendBlockChange(loc0, Material.OAK_SIGN.createBlockData())
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(LifeCore::class.java), Runnable {
            awaitingSign[player.uniqueId] = {
                player.sendBlockChange(loc0, origBlockData)
                action.accept(it)
            }
            (player as CraftPlayer).handle.playerConnection
                .sendPacket(PacketPlayOutOpenSignEditor(BlockPosition(loc0.blockX, loc0.blockY, loc0.blockZ)))
        })
    }
}