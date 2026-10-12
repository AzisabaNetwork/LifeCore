package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.config.VotesFile;
import com.github.mori01231.lifecore.util.PlayerUtil;
import com.github.mori01231.lifecore.vote.VoteReward;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class VoteListener implements Listener {
    private final LifeCore plugin;

    public VoteListener(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVote(@NotNull VotifierEvent e) {
        processVotePacket(plugin, e.getVote().getUsername(), e.getVote().getServiceName());
    }

    public static synchronized void processVotePacket(@NotNull LifeCore plugin, @NotNull String username, @NotNull String serviceName) {
        Player player = Bukkit.getPlayerExact(username);
        Bukkit.broadcast(Component.text("[", NamedTextColor.GOLD)
                .append(Component.text("Broadcast", NamedTextColor.DARK_RED))
                .append(Component.text("] ", NamedTextColor.GOLD))
                .append(Component.text("Thanks ", NamedTextColor.DARK_GREEN))
                .append(Component.text(username, NamedTextColor.RED))
                .append(Component.text(" for voting on " + serviceName, NamedTextColor.DARK_GREEN)));
        if (player == null) {
            PlayerUtil.resolveUUIDAsync(plugin, username).thenAcceptAsync(uuid -> {
                if (uuid == null) {
                    plugin.getLogger().warning("Could not resolve UUID for player " + username);
                    return;
                }
                VotesFile.increase(uuid.toString());
                plugin.getLogger().info("Queued vote from " + username + " (UUID: " + uuid + ", service name: " + serviceName + ")");
            }, plugin.asyncExecutor);
            return;
        }
        player.sendMessage(Component.text(serviceName + "で投票していただきありがとうございます！", NamedTextColor.GREEN));
        long count = VotesFile.getVotes(player.getUniqueId().toString()) + 1;
        VotesFile.setVotes(player.getUniqueId().toString(), 0);
        processVotes(plugin, player, count);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player player = Bukkit.getPlayer(e.getPlayer().getUniqueId());
            if (player == null || !player.isOnline()) {
                return;
            }
            long count = VotesFile.getVotes(player.getUniqueId().toString());
            VotesFile.setVotes(player.getUniqueId().toString(), 0);
            processVotes(plugin, player, count);
        }, 20 * 10);
    }

    private static void processVotes(@NotNull LifeCore plugin, @NotNull Player player, long count) {
        plugin.getLogger().info("Processing vote from " + player.getName() + " (count: " + count + ")");
        for (long i = 0; i < count; i++) {
            for (VoteReward reward : plugin.getVoteConfig().getRewards()) {
                reward.execute(plugin, player);
            }
        }
        player.sendMessage(Component.text(count + "回分の投票報酬を受け取りました。", NamedTextColor.GREEN));
    }
}
