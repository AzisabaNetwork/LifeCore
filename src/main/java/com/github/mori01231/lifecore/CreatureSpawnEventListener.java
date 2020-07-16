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

            // Combine the coordinates into one string
            String originLocation = " x: " + (int)Math.round(Objects.requireNonNull(event.getEntity().getOrigin()).getX()) + " y: " + (int)Math.round(Objects.requireNonNull(event.getEntity().getOrigin()).getY()) + " z: " + (int)Math.round(Objects.requireNonNull(event.getEntity().getOrigin()).getZ()) + " ";

            // Get the world name of the bee spawn
            String originWorld = event.getEntity().getOrigin().getWorld().getName();

            // Remove the bee
            event.getEntity().remove();

            // Log the location of the beehive
            getLogger().info("ハチが湧いてきやがった、、、ふざけんな！" + originWorld + "ワールドの" + originLocation + "に巣があるので消しておいてくれ");
        }

    }
}
