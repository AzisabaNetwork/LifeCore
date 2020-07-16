package com.github.mori01231.lifecore;


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
            int originX = event.getEntity().getOrigin().getBlock().getX();
            int originY = event.getEntity().getOrigin().getBlock().getY();
            int originZ = event.getEntity().getOrigin().getBlock().getZ();
            String originWorld = event.getEntity().getOrigin().getWorld().getName().toString();
            event.getEntity().remove();
            getLogger().info("ハチが湧いてきやがった、、、ふざけんな！" + originWorld + "ワールドの x: " + originX + " y: " + originY + " z: " + originZ + "に巣があるので消しておいてくれ");
        }

    }
}
