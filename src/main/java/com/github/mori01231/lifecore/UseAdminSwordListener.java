package com.github.mori01231.lifecore;

import com.github.mori01231.lifecore.util.ItemUtil;
import com.github.mori01231.lifecore.util.WebhookUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UseAdminSwordListener implements Listener {
    private final Map<UUID, Long> cooldown = new HashMap<>();

    // sendDiscordWebhook with config path "adminSwordNotifyWebhookURL" if player uses an item with > 10000 damage attribute modifier
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (checkCooldown(e.getPlayer().getUniqueId())) return;
        if (ItemUtil.isProbablyAdminSword(e.getItem()) && !e.getPlayer().hasPermission("lifecore.canuseadminsword")) {
            e.setCancelled(true);
            WebhookUtil.sendDiscordWebhook(
                    "adminSwordNotifyWebhookURL",
                    LifeCore.getInstance().getConfig().getString("adminSwordNotifyWebhookUsername"),
                    e.getPlayer().getName() + " tried to use an item with >= 9999 generic.attack_damage\n" +
                            "Display name: `" + e.getItem().getItemMeta().getDisplayName() + "`\n" +
                            "Location: " + e.getPlayer().getLocation());
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (checkCooldown(e.getPlayer().getUniqueId())) return;
        if (ItemUtil.isProbablyAdminSword(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
            WebhookUtil.sendDiscordWebhook(
                    "adminSwordNotifyWebhookURL",
                    LifeCore.getInstance().getConfig().getString("adminSwordNotifyWebhookUsername"),
                    e.getPlayer().getName() + " tried to drop an item with >= 9999 generic.attack_damage\n" +
                            "Display name: `" + e.getItemDrop().getItemStack().getItemMeta().getDisplayName() + "`\n" +
                            "Location: " + e.getPlayer().getLocation());
        }
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent e) {
        if (ItemUtil.isProbablyAdminSword(e.getItem().getItemStack())) {
            e.setCancelled(true);
            WebhookUtil.sendDiscordWebhook(
                    "adminSwordNotifyWebhookURL",
                    LifeCore.getInstance().getConfig().getString("adminSwordNotifyWebhookUsername"),
                    e.getPlayer().getName() + " tried to pick up an item with >= 9999 generic.attack_damage\n" +
                            "Display name: `" + e.getItem().getItemStack().getItemMeta().getDisplayName() + "`\n" +
                            "Location: " + e.getPlayer().getLocation());
        }
    }

    /**
     * Checks for cooldown and returns false if player is not on cooldown
     * @param uuid UUID of player
     * @return true if player is on cooldown
     */
    private boolean checkCooldown(UUID uuid) {
        // check for cooldown
        if (cooldown.containsKey(uuid)) {
            // cooldown of 2 minutes
            if (System.currentTimeMillis() - cooldown.get(uuid) < 120000) {
                return true;
            }
        }
        // add cooldown
        cooldown.put(uuid, System.currentTimeMillis());
        return false;
    }
}
