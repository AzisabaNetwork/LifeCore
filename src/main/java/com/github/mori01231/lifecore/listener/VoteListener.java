package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.VotesFile;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import io.lumine.xikage.mythicmobs.MythicMobs;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
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
    @EventHandler
    public void onVote(VotifierEvent e) {
        Vote vote = e.getVote();
        String username = vote.getUsername();
        Player player = Bukkit.getPlayerExact(username);
        if (player == null) {
            VotesFile.increase(username);
            LifeCore.getInstance().getLogger().info("Queued vote from " + username);
            return;
        }
        long count = VotesFile.getVotes(username) + 1;
        VotesFile.setVotes(username, 0);
        processVotes(player, count);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        long count = VotesFile.getVotes(e.getPlayer().getName());
        VotesFile.setVotes(e.getPlayer().getName(), 0);
        if (count > 0) {
            Bukkit.getScheduler().runTaskLater(LifeCore.getInstance(), () -> processVotes(e.getPlayer(), count), 1);
        }
    }

    private void processVotes(@NotNull Player player, long count) {
        Optional<Economy> economy = Optional.ofNullable(Bukkit.getServicesManager().getRegistration(Economy.class))
                .map(RegisteredServiceProvider::getProvider);
        LifeCore.getInstance().getLogger().info("Processing vote from " + player.getName() + " (count: " + count + ")");
        for (long i = 0; i < count; i++) {
            ItemStack itemVoteTicket = MythicMobs.inst().getItemManager().getItemStack("vote_ticket");
            ItemStack itemDiamondBlock = new ItemStack(Material.DIAMOND_BLOCK);
            player.getInventory().addItem(itemVoteTicket, itemDiamondBlock);
            economy.ifPresent(eco -> eco.depositPlayer(player, 50000));
        }
    }
}
