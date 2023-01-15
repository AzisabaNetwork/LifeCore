package com.github.mori01231.lifecore.listener.item;

import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class GlassHammerItemListener implements Listener {
    private static final String ITEM_ID = "38ac6fcd-e77f-4a19-a251-bb9cd48bd367";

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK || e.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            // return if the player is not in survival mode or if the player is not left-clicking a block
            return;
        }
        if (Objects.requireNonNull(e.getClickedBlock()).getType().name().contains("GLASS")) {
            // return if the player is not left-clicking a glass block
            return;
        }
        if (!ITEM_ID.equals(ItemUtil.getStringTag(e.getItem(), "LifeItemId"))) {
            // wrong item
            return;
        }
        if (new BlockBreakEvent(e.getClickedBlock(), e.getPlayer()).callEvent()) {
            e.getClickedBlock().breakNaturally(e.getItem(), true);
        }
    }
}
