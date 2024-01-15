package com.github.mori01231.lifecore.listener;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OverrideTabCompleteListener implements Listener {
    private static final Map<String, TabCompleteHandler> handlers = new HashMap<>();

    static {
        handlers.put("/nick ", (sender, buffer, list) -> {
            if (!(sender instanceof Player)) {
                return list;
            }
            if (!list.isEmpty()) {
                return list;
            }
            String last = buffer.substring(buffer.lastIndexOf(' ') + 1);
            if (last.equals("off") || sender.getName().equalsIgnoreCase(last)) {
                return Collections.emptyList();
            }
            last = ChatColor.translateAlternateColorCodes('&', last);
            String prefix = getChat().getPlayerPrefix((Player) sender);
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            String suffix = getChat().getPlayerSuffix((Player) sender);
            suffix = ChatColor.translateAlternateColorCodes('&', suffix);
            return Collections.singletonList(prefix + "~" + last + suffix);
        });
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        handlers.forEach((key, handler) -> {
            if (e.getBuffer().startsWith(key)) {
                e.setCompletions(handler.handle(e.getSender(), e.getBuffer(), e.getCompletions()));
            }
        });
    }

    private static @NotNull Chat getChat() {
        return Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(Chat.class)).getProvider();
    }

    public interface TabCompleteHandler {
        List<String> handle(CommandSender sender, String buffer, List<String> list);
    }
}
