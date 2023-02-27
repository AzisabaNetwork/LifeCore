package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CreatureSpawnEventListener implements Listener {
    private final LifeCore plugin;
    
    public CreatureSpawnEventListener(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType().equals(EntityType.BEE)) {
            // Remove the bee
            event.getEntity().remove();
        }

        if (event.getEntityType().equals(EntityType.ENDERMAN)) {

            if (plugin.getConfig().getStringList("NonEndermanWorlds").contains(event.getLocation().getWorld().getName())) {
                // Remove the Enderman
                event.getEntity().remove();
            }
        }

    }
}
