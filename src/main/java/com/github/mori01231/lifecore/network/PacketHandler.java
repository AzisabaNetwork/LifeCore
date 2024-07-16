package com.github.mori01231.lifecore.network;

import com.github.mori01231.lifecore.event.AsyncPlayerPreInteractEntityEvent;
import com.github.mori01231.lifecore.event.AsyncPlayerPreInteractEvent;
import com.github.mori01231.lifecore.event.AsyncPreSignChangeEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayInUpdateSign;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PacketHandler extends ChannelDuplexHandler {
    private final Player player;
    private final EntityPlayer entityPlayer;

    public PacketHandler(@NotNull Player player) {
        this.player = player;
        this.entityPlayer = ((CraftPlayer) player).getHandle();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) msg;
            Entity entity = packet.a(entityPlayer.world);
            if (entity != null) {
                if (new AsyncPlayerPreInteractEvent(player, packet.b(), entity).callEvent() &&
                        new AsyncPlayerPreInteractEntityEvent(player, packet.b(), entity).callEvent()) {
                    super.channelRead(ctx, msg);
                }
                return;
            }
        }
        if (msg instanceof PacketPlayInUpdateSign) {
            PacketPlayInUpdateSign packet = (PacketPlayInUpdateSign) msg;
            Location location = new Location(player.getWorld(), packet.b().getX(), packet.b().getY(), packet.b().getZ());
            List<String> lines = Arrays.asList(packet.c());
            if (new AsyncPreSignChangeEvent(player, location, lines).callEvent()) {
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
