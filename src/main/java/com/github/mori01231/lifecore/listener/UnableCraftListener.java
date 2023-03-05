package com.github.mori01231.lifecore.listener;

import com.destroystokyo.paper.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class UnableCraftListener implements Listener {

    private static final List <String> LIST = Arrays.asList( "§a", "§b","§c","§d","§e","§f", "§0","§1","§2","§3","§4","§5","§6","§7","§8", "§9", "§n", "§m", "§o", "§k", "§l");

    @EventHandler
    public void onCraft(PrepareGrindstoneEvent e) {

        if ( e.getView().getPlayer().hasPermission("lifecore.bypasscraft") ) return;
        if ( checkItemStack(e.getInventory().getLowerItem()) ) e.setResult(null);
        if ( checkItemStack(e.getInventory().getUpperItem()) ) e.setResult(null);

    }
    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {

        if ( e.getView().getPlayer().hasPermission("lifecore.bypasscraft") ) return;

        if ( checkItemStack(e.getInventory().getFirstItem()) ) e.setResult(null);
        if ( checkItemStack(e.getInventory().getSecondItem()) ) e.setResult(null);

    }

    public static boolean checkItemStack(ItemStack item) {

        if ( item == null ) return false;

        ItemMeta meta = item.getItemMeta();

        if ( item.getType().isAir() ) return false;
        if ( !meta.hasDisplayName() ) return false;
        if ( meta.hasLore() ) return true;

        for ( String c: LIST ) {
            if ( meta.getDisplayName().contains(c) ) return true;
        }

        return false;
    }
}
