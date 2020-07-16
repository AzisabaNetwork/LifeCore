package com.github.mori01231.lifecore;


import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.Listener;



import static org.bukkit.Bukkit.getLogger;

public class CreatureSpawnEventListener implements Listener{

    private LifeCore plugin;
    public CreatureSpawnEventListener(LifeCore plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(event.getEntityType().equals(EntityType.BEE)){

            // Get the coordinates of the bee spawn
            Block originBlock = event.getEntity().getOrigin().getBlock();
            int originX = originBlock.getX();
            int originY = originBlock.getY();
            int originZ = originBlock.getZ();

            // Combine the coordinates into one string
            String originLocation = " x: " + originX + " y: " + originY + " z: " + originZ + " ";

            // Get the world name of the bee spawn
            String originWorld = event.getEntity().getOrigin().getWorld().getName();

            // Remove the bee
            event.getEntity().remove();

            // Log the location of the beehive
            getLogger().info("ハチが湧いてきやがった、、、ふざけんな！" + originWorld + "ワールドの" + originLocation + "に巣があるので消しておいてくれ");
        }

    }
}
