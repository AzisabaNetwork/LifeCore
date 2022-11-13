package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class DeathLoopListener implements Listener {
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof LivingEntity)) {
            return;
        }
        LivingEntity entity = (LivingEntity) e.getDamager();
        if (hasTempHealthBoost(entity)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (hasTempHealthBoost(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    private boolean hasTempHealthBoost(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr == null) {
            return false;
        }
        for (AttributeModifier modifier : attr.getModifiers()) {
            if (modifier.getName().equals("lifecore.temp_health_boost")) {
                return true;
            }
        }
        return false;
    }

    public static void checkAttribute(LivingEntity entity) {
        Bukkit.getScheduler().runTaskLater(LifeCore.getInstance(), () -> checkAttribute0(entity), 5);
    }

    private static void checkAttribute0(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr == null) {
            return;
        }
        for (AttributeModifier modifier : attr.getModifiers()) {
            if (modifier.getName().equals("lifecore.temp_health_boost")) {
                attr.removeModifier(modifier);
            }
        }
        if (attr.getValue() <= 0) {
            double amount = 0;
            for (AttributeModifier modifier : attr.getModifiers()) {
                if (modifier.getOperation() == AttributeModifier.Operation.ADD_SCALAR) {
                    if (modifier.getAmount() < 0) {
                        amount -= modifier.getAmount();
                    }
                }
            }
            attr.addModifier(new AttributeModifier("lifecore.temp_health_boost", amount, AttributeModifier.Operation.ADD_SCALAR));
            entity.sendMessage(ChatColor.RED + "不可能な装備の組み合わせです。最大体力が0以下になっています。");
        }
    }
}
