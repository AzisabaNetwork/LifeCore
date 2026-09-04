package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.config.DamageLogFile;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class DamageLogListener implements Listener {

    private final Map<UUID, ArrayDeque<MythicDamage>> pendingMythicDamage = new HashMap<>();
    private final Map<EntityDamageEvent, Boolean> mythicArmorPiercing = new WeakHashMap<>();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMythicDamage(MythicDamageEvent e) {
        UUID targetId = e.getTarget().getUniqueId();
        MythicDamage damage = new MythicDamage(Boolean.TRUE.equals(e.getDamageMetadata().getIgnoresArmor()));
        pendingMythicDamage.computeIfAbsent(targetId, ignored -> new ArrayDeque<>()).addLast(damage);

        // MythicDamageEvent normally produces the Bukkit damage event synchronously. Remove the
        // marker on the next tick if another plugin cancels it before that happens.
        Bukkit.getScheduler().runTask(LifeCore.getPlugin(LifeCore.class), () -> {
            ArrayDeque<MythicDamage> pending = pendingMythicDamage.get(targetId);
            if (pending == null) return;
            pending.remove(damage);
            if (pending.isEmpty()) pendingMythicDamage.remove(targetId);
        });
    }

    private boolean isMythicArmorPiercing(EntityDamageEvent e) {
        Boolean cached = mythicArmorPiercing.get(e);
        if (cached != null) return cached;

        ArrayDeque<MythicDamage> pending = pendingMythicDamage.get(e.getEntity().getUniqueId());
        MythicDamage damage = pending == null ? null : pending.pollFirst();
        if (pending != null && pending.isEmpty()) pendingMythicDamage.remove(e.getEntity().getUniqueId());

        boolean armorPiercing = damage != null && damage.ignoresArmor();
        mythicArmorPiercing.put(e, armorPiercing);
        return armorPiercing;
    }

    private record MythicDamage(boolean ignoresArmor) {}

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
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamaged(EntityDamageEvent e) {

        if ( !(e.getEntity() instanceof Player) || e.isCancelled()) return;

        Player player = (Player) e.getEntity();

        if (!DamageLogFile.isEnabled(player.getUniqueId())) return;

        if (isMythicArmorPiercing(e)) {
            message(player, "貫通", e.getFinalDamage(), false);
            return;
        }

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
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent e) {

        if ( (e.getEntity() instanceof Player) ) {

            Player player = (Player) e.getEntity();

            if (!DamageLogFile.isEnabled(player.getUniqueId())) return;

            if (isMythicArmorPiercing(e)) {
                message(player, "貫通", e.getFinalDamage(), false, e.getDamager());
            } else {
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

        }
        if ( (e.getDamager() instanceof Player) ) {

            Player player = (Player) e.getDamager();

            if (!DamageLogFile.isEnabled(player.getUniqueId())) return;

            if (isMythicArmorPiercing(e)) {
                message(player, "貫通", e.getFinalDamage(), true, e.getEntity());
                return;
            }

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
