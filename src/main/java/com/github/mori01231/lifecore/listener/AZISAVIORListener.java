package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AZISAVIORListener implements Listener {
    private static final Set<String> OFFHAND_ITEMS = new HashSet<>(Arrays.asList("AZISAVIOR", "ZARISAVIOR"));
    private final LifeCore plugin;
    public AZISAVIORListener(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    private static final Set <UUID> coolTime = new HashSet<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        ItemStack mainHand = e.getPlayer().getInventory().getItemInMainHand();
        ItemStack offHand = e.getPlayer().getInventory().getItemInOffHand();

        if ( !OFFHAND_ITEMS.contains(ItemUtil.getMythicType(offHand)) ) return;
        if ( !"offhandactivate".equals(ItemUtil.getMythicType(mainHand)) && !mainHand.getType().isAir() ) return;

        if ( coolTime.contains(e.getPlayer().getUniqueId()) ) return;

        coolTime.add(e.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLater(plugin, ()-> coolTime.remove(e.getPlayer().getUniqueId()), 20 * 15);

        double maxHealth = Objects.requireNonNull(e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

        e.getPlayer().setHealth(Math.min(e.getPlayer().getHealth() + 50, maxHealth));
    }
    @EventHandler
    public void onDamaged(EntityDamageEvent e) {

        if ( !(e.getEntity() instanceof Player) ) return;

        Player player = (Player) e.getEntity();

        ItemStack offHand = player.getInventory().getItemInOffHand();
        if ( !"AZISAVIOR".equals(ItemUtil.getMythicType(offHand)) ) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 200, 1));

    }
}
