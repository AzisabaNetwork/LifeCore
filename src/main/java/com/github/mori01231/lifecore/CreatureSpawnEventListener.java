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
            String origin = event.getEntity().getOrigin().toString();
            event.getEntity().remove();
            getLogger().info("ハチが湧いてきやがった、、、ふざけんな！この座標に巣があるので消しておいてくれ： " + origin);
        }

    }
}
