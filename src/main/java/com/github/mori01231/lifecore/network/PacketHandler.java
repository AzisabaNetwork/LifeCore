package com.github.mori01231.lifecore.network;

import com.github.mori01231.lifecore.event.AsyncPlayerPreInteractEntityEvent;
import com.github.mori01231.lifecore.event.AsyncPlayerPreInteractEvent;
import com.github.mori01231.lifecore.event.AsyncPreSignChangeEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PacketHandler extends ChannelDuplexHandler {
    private final Player player;
    private final ServerPlayer entityPlayer;

    public PacketHandler(@NotNull Player player) {
        this.player = player;
        this.entityPlayer = ((CraftPlayer) player).getHandle();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ServerboundInteractPacket packet) {
            int entityId = packet.getEntityId();
            if (new AsyncPlayerPreInteractEvent(player, entityId).callEvent() &&
                    new AsyncPlayerPreInteractEntityEvent(player, entityId).callEvent()) {
                super.channelRead(ctx, msg);
            }
        }
        if (msg instanceof ServerboundSignUpdatePacket packet) {
            var pos = packet.getPos();
            var location = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
            var lines = Arrays.asList(packet.getLines());
            var event = new AsyncPreSignChangeEvent(player, location, lines);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                super.channelRead(ctx, msg);
            }
            return;
        }
        super.channelRead(ctx, msg);
    }

    //@Skip
    /*
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }
    */
}
