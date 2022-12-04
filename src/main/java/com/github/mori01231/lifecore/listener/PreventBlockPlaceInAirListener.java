package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PreventBlockPlaceInAirListener implements Listener {
    private static final Set<BlockFace> CHECK_FACES =
            Stream.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN).collect(Collectors.toSet());
    private final LifeCore plugin;

    public PreventBlockPlaceInAirListener(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        for (BlockFace face : CHECK_FACES) {
            Block relBlock = event.getBlockPlaced().getRelative(face);
            if (!relBlock.getType().isAir()) {
                return;
            }
        }
        plugin.getSLF4JLogger().warn("{} tried to place block in air (all block faces are air)", event.getPlayer().getName());
        event.setCancelled(true);
    }
}
