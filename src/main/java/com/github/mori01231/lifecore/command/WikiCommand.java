package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WikiCommand implements CommandExecutor {
    private final LifeCore plugin;
    
    public WikiCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("unofficial")) {
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3非公式Wikiリンク: &f&l" + plugin.getConfig().getString("unofficial-wiki-url")));
            }
            else if (args[0].equalsIgnoreCase("official")) {
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3公式Wikiリンク: &f&l" + plugin.getConfig().getString("wiki-url")));
            }
        } else {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3非公式Wikiリンク: &f" + plugin.getConfig().getString("unofficial-wiki-url")));
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3&l公式Wikiリンク: &f&l" + plugin.getConfig().getString("wiki-url")));
        }

        return true;
    }
}
