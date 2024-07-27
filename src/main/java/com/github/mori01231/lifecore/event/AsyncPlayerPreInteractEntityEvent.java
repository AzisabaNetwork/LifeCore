package com.github.mori01231.lifecore.event;

import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class AsyncPlayerPreInteractEntityEvent extends CallableEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final int interactedEntity;
    private boolean cancelled = false;

    public AsyncPlayerPreInteractEntityEvent(@NotNull Player player, int interactedEntity) {
        super(true);
        this.player = player;
        this.interactedEntity = interactedEntity;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public int getInteractedEntityId() {
        return interactedEntity;
    }

    public @NotNull Entity getInteractedEntity() {
        return Objects.requireNonNull(((CraftWorld) player.getWorld()).getHandle().getEntity(interactedEntity), "entity no longer exists or never existed");
    }

    public @Nullable UUID getEntityUniqueID() {
        return getInteractedEntity().getBukkitEntity().getUniqueId();
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
