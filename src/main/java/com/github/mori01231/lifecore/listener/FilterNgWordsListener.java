package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.util.NGWordsCache;
import net.azisaba.ryuzupluginchat.event.AsyncChannelMessageEvent;
import net.azisaba.ryuzupluginchat.event.AsyncGlobalMessageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FilterNgWordsListener implements Listener {
    private final LifeCore plugin;

    public FilterNgWordsListener(LifeCore plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onGlobalChat(AsyncGlobalMessageEvent e) {
        filter(e.getRecipients(), e.getMessage().format(), true);
    }

    @EventHandler
    public void onChannelChat(AsyncChannelMessageEvent e) {
        filter(e.getRecipients(), e.getMessage().format(), false);
    }

    private void filter(Set<Player> recipients, String format, boolean global) {
        List<Player> toRemove = new ArrayList<>();
        for (Player player : recipients) {
            Set<String> set = plugin.ngWordsCache.get(player.getUniqueId());
            String filtered = NGWordsCache.filter(set, format);
            if (global) {
            }
            if (!format.equals(filtered)) {
                toRemove.add(player);
                player.sendMessage(filtered);
            }
        }
        toRemove.forEach(recipients::remove);
    }
}
