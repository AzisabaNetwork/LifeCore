package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
            if (modifier.getKey().equals(new NamespacedKey("lifecore", "temp_health_boost_scalar")) ||
                    modifier.getKey().equals(new NamespacedKey("lifecore", "temp_health_boost_number"))) {
                return true;
            }
        }
        return false;
    }

    public static void checkAttribute(@NotNull Plugin plugin, LivingEntity entity) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkAttribute0(entity), 5);
    }

    private static void checkAttribute0(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr == null) {
            return;
        }
        for (AttributeModifier modifier : attr.getModifiers()) {
            if (modifier.getName().equals("lifecore.temp_health_boost_scalar") ||
                    modifier.getName().equals("lifecore.temp_health_boost_number")) {
                attr.removeModifier(modifier);
            }
        }
        if (attr.getValue() <= 0) {
            double amountScalar = 0;
            double amountNumber = 0;
            for (AttributeModifier modifier : attr.getModifiers()) {
                if (modifier.getOperation() == AttributeModifier.Operation.ADD_SCALAR) {
                    if (modifier.getAmount() < 0) {
                        amountScalar -= modifier.getAmount();
                    }
                } else if (modifier.getOperation() == AttributeModifier.Operation.ADD_NUMBER) {
                    if (modifier.getAmount() < 0) {
                        amountNumber -= modifier.getAmount();
                    }
                }
            }
            if (amountScalar != 0) {
                attr.addModifier(new AttributeModifier(new NamespacedKey("lifecore", "temp_health_boost_scalar"), amountScalar, AttributeModifier.Operation.ADD_SCALAR));
            }
            if (amountNumber != 0) {
                attr.addModifier(new AttributeModifier(new NamespacedKey("lifecore", "temp_health_boost_number"), amountNumber, AttributeModifier.Operation.ADD_NUMBER));
            }
            entity.sendMessage(ChatColor.RED + "不可能な装備の組み合わせです。最大体力が0以下になっています。");
        }
    }
}
