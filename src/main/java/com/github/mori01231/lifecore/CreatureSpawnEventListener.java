package com.github.mori01231.lifecore;


import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.Listener;

import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class CreatureSpawnEventListener implements Listener{

    private LifeCore plugin;
    public CreatureSpawnEventListener(LifeCore plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(event.getEntityType().equals(EntityType.BEE)){
            // Remove the bee
            event.getEntity().remove();
        }

    }
}
