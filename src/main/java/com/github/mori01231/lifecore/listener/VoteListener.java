package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.config.VotesFile;
import com.github.mori01231.lifecore.util.PlayerUtil;
import com.vexsoftware.votifier.model.VotifierEvent;
import io.lumine.xikage.mythicmobs.MythicMobs;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VoteListener implements Listener {
    private final LifeCore plugin;
    
    public VoteListener(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onVote(@NotNull VotifierEvent e) {
        processVotePacket(plugin, e.getVote().getUsername(), e.getVote().getServiceName());
    }

    public static void processVotePacket(@NotNull LifeCore plugin, @NotNull String username, @NotNull String serviceName) {
        Player player = Bukkit.getPlayerExact(username);
        Bukkit.broadcastMessage(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "Broadcast" + ChatColor.GOLD + "] " +
            ChatColor.DARK_GREEN + "Thanks " + ChatColor.RED + username +
            ChatColor.DARK_GREEN + " for voting on " + serviceName);
        if (player == null) {
            PlayerUtil.resolveUUIDAsync(plugin, username).thenAcceptAsync(uuid -> {
                if (uuid == null) {
                    plugin.getSLF4JLogger().warn("Could not resolve UUID for player " + username);
                    return;
                }
                VotesFile.increase(uuid.toString());
                plugin.getSLF4JLogger().info("Queued vote from {} (UUID: {}, service name: {})", username, uuid, serviceName);
            }, plugin.asyncExecutor);
            return;
        }
        player.sendMessage(ChatColor.GREEN + serviceName + "で投票していただきありがとうございます！");
        long count = VotesFile.getVotes(player.getUniqueId().toString()) + 1;
        VotesFile.setVotes(player.getUniqueId().toString(), 0);
        processVotes(plugin, player, count);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!e.getPlayer().isOnline()) {
                return;
            }
            long count = VotesFile.getVotes(e.getPlayer().getUniqueId().toString());
            VotesFile.setVotes(e.getPlayer().getUniqueId().toString(), 0);
            processVotes(plugin, e.getPlayer(), count);
        }, 20 * 10);
    }

    private static void processVotes(@NotNull LifeCore plugin, @NotNull Player player, long count) {
        Optional<Economy> economy = Optional.ofNullable(Bukkit.getServicesManager().getRegistration(Economy.class))
                .map(RegisteredServiceProvider::getProvider);
        plugin.getLogger().info("Processing vote from " + player.getName() + " (count: " + count + ")");
        for (long i = 0; i < count; i++) {
            ItemStack itemVoteTicket = MythicMobs.inst().getItemManager().getItemStack("vote_ticket");
            ItemStack itemDiamondBlock = new ItemStack(Material.DIAMOND_BLOCK);
            player.getInventory().addItem(itemVoteTicket, itemDiamondBlock);
            economy.ifPresent(eco -> eco.depositPlayer(player, 50000));
        }
        player.sendMessage("" + ChatColor.GREEN + count + "個の投票報酬を受け取りました。");
    }
}
