package com.github.mori01231.lifecore.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated misspelled
 */
@Deprecated
public class AsyncPlayerPreInteractEvent extends CallableEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final int interactedEntity;
    private boolean cancelled = false;

    public AsyncPlayerPreInteractEvent(@NotNull Player player, int interactedEntity) {
        super(true);
        this.player = player;
        this.interactedEntity = interactedEntity;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public int getInteractedEntity() {
        return interactedEntity;
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
