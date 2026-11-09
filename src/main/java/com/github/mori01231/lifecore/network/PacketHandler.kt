package com.github.mori01231.lifecore.network

import com.github.mori01231.lifecore.event.AsyncPlayerPreInteractEntityEvent
import com.github.mori01231.lifecore.event.AsyncPlayerPreInteractEvent
import com.github.mori01231.lifecore.event.AsyncPreSignChangeEvent
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket
import net.minecraft.server.level.ServerPlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

class PacketHandler(private val player: Player) : ChannelDuplexHandler() {
    @Suppress("unused")
    private val entityPlayer: ServerPlayer = (player as CraftPlayer).handle

    @Suppress("DEPRECATION")
    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is ServerboundInteractPacket) {
            val entityId = msg.entityId
            if (AsyncPlayerPreInteractEvent(player, entityId).callEvent() &&
                AsyncPlayerPreInteractEntityEvent(player, entityId).callEvent()
            ) {
                super.channelRead(ctx, msg)
            }
        }
        if (msg is ServerboundSignUpdatePacket) {
            val pos = msg.pos
            val location = Location(player.world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            val lines = msg.lines.toList()
            val event = AsyncPreSignChangeEvent(player, location, lines)
            Bukkit.getPluginManager().callEvent(event)
            if (!event.isCancelled) {
                super.channelRead(ctx, msg)
            }
            return
        }
        super.channelRead(ctx, msg)
    }
}
