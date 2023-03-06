package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.config.DamageLogFile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageLogListener implements Listener {

    public void message(Player p, String type, double damage, boolean send) {

        String prefix = "§8[§cDamageLog§8] ";
        String damageType;
        String damageColor;

        if ( send ) {
            damageType = "§a与";
            damageColor = "§a";
        } else {
            damageType = "§c受";
            damageColor = "§c";
        }

        p.sendMessage(prefix + damageColor + damage + " §8(" + damageType + " §7" + type + "§8)");
    }

    public void message(Player p, String type, double damage, boolean send, Entity entity) {

        String prefix = "§8[§cDamageLog§8] ";
        String damageType;
        String damageColor;
        String entityName = "§f" + entity.getName() + " ";

        if ( send ) {
            damageType = "§a与";
            damageColor = "§a";
        } else {
            damageType = "§c受";
            damageColor = "§c";
        }

        p.sendMessage(prefix + entityName + damageColor + damage + " §8(" + damageType + " §7" + type + "§8)");
    }
    @EventHandler
    public void onDamaged(EntityDamageEvent e) {

        if ( !(e.getEntity() instanceof Player) ) return;

        Player player = (Player) e.getEntity();

        if (!DamageLogFile.isEnabled(player.getUniqueId())) return;

        if ( e.getCause() == EntityDamageEvent.DamageCause.FALL ) message(player, "落下", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.FIRE ) message(player, "炎上", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.LAVA ) message(player, "溶岩", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.POISON ) message(player, "毒", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.VOID ) message(player, "奈落", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.WITHER ) message(player, "衰弱", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL ) message(player, "激突", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.SUICIDE ) message(player, "自害", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.THORNS ) message(player, "茨の鎧", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.STARVATION ) message(player, "餓死", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ) message(player, "延焼", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.DRAGON_BREATH ) message(player, "ドラゴンの息", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR ) message(player, "マグマブロック", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.DROWNING ) message(player, "窒息", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ) message(player, "爆発", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION ) message(player, "窒息", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.CONTACT ) message(player, "棘", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ) message(player, "爆発", e.getFinalDamage(), false);
        if ( e.getCause() == EntityDamageEvent.DamageCause.CUSTOM ) message(player, "貫通", e.getFinalDamage(), false);

    }
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {

        if ( (e.getEntity() instanceof Player) ) {

            Player player = (Player) e.getEntity();

            if (!DamageLogFile.isEnabled(player.getUniqueId())) return;

            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.FALL ) message(player, "落下", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.FIRE ) message(player, "炎上", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.LAVA ) message(player, "溶岩", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.POISON ) message(player, "毒", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.VOID ) message(player, "奈落", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.WITHER ) message(player, "衰弱", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.LIGHTNING ) message(player, "雷", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.FALLING_BLOCK ) message(player, "落下中のブロック", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.FLY_INTO_WALL ) message(player, "激突", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.MAGIC ) message(player, "魔法", e.getFinalDamage(), false, e.getDamager());
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.SUICIDE ) message(player, "自害", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.THORNS ) message(player, "茨の鎧", e.getFinalDamage(), false, e.getDamager());
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.STARVATION ) message(player, "餓死", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.FIRE_TICK ) message(player, "延焼", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.DRAGON_BREATH ) message(player, "ドラゴンの息", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.HOT_FLOOR ) message(player, "マグマブロック", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.DROWNING ) message(player, "窒息", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.ENTITY_EXPLOSION ) message(player, "爆発", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.SUFFOCATION ) message(player, "窒息", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.CONTACT ) message(player, "棘", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.BLOCK_EXPLOSION ) message(player, "爆発", e.getFinalDamage(), false);
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.CUSTOM ) message(player, "貫通", e.getFinalDamage(), false, e.getDamager());
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK ) message(player, "攻撃", e.getFinalDamage(), false, e.getDamager());
            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.ENTITY_SWEEP_ATTACK ) message(player, "範囲攻撃", e.getFinalDamage(), false, e.getDamager());

        }
        if ( (e.getDamager() instanceof Player) ) {

            Player player = (Player) e.getDamager();

            if (!DamageLogFile.isEnabled(player.getUniqueId())) return;

            if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.POISON ) message(player, "毒", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.WITHER ) message(player, "衰弱", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.LIGHTNING ) message(player, "雷", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.MAGIC ) message(player, "魔法", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.SUICIDE ) message(player, "自害", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.THORNS ) message(player, "茨の鎧", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.FIRE_TICK ) message(player, "延焼", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.DRAGON_BREATH ) message(player, "ドラゴンの息", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.ENTITY_EXPLOSION ) message(player, "爆発", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.CONTACT ) message(player, "棘", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.BLOCK_EXPLOSION ) message(player, "爆発", e.getFinalDamage(), true);
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.CUSTOM ) message(player, "貫通", e.getFinalDamage(), true, e.getEntity());
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK ) message(player, "攻撃", e.getFinalDamage(), true, e.getEntity());
            else if ( e.getCause() == EntityDamageByEntityEvent.DamageCause.ENTITY_SWEEP_ATTACK ) message(player, "範囲攻撃", e.getFinalDamage(), true, e.getEntity());

        }
    }
}
