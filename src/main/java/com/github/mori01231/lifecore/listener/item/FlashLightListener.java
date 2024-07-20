package com.github.mori01231.lifecore.listener.item;

import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class FlashLightListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
            if (Objects.equals(ItemUtil.getMythicType(e.getPlayer().getInventory().getItemInOffHand()), "flashlight")) {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 18000, 0));
            }
        }
    }
}
