package com.github.mori01231.lifecore.event;

import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AsyncPlayerPreInteractEntityEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final PacketPlayInUseEntity.EnumEntityUseAction action;
    private final Entity interactedEntity;
    private boolean cancelled = false;

    public AsyncPlayerPreInteractEntityEvent(@NotNull Player player, @NotNull PacketPlayInUseEntity.EnumEntityUseAction action, @NotNull Entity interactedEntity) {
        super(player, true);
        this.action = action;
        this.interactedEntity = interactedEntity;
    }

    @NotNull
    public PacketPlayInUseEntity.EnumEntityUseAction getAction() {
        return action;
    }

    @NotNull
    public Entity getInteractedEntity() {
        return interactedEntity;
    }

    @NotNull
    public UUID getEntityUniqueID() {
        return interactedEntity.getUniqueID();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @SuppressWarnings("unused") // required for bukkit
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
