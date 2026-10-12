package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


public class HelpCommand implements CommandExecutor {
    private final LifeCore plugin;
    
    public HelpCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("menu")) {
                for (String line : plugin.getConfig().getStringList("Help.Menu")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("town")) {
                for (String line : plugin.getConfig().getStringList("Help.Town")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("money")) {
                for (String line : plugin.getConfig().getStringList("Help.Money")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("rank")) {
                for (String line : plugin.getConfig().getStringList("Help.Rank")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("dungeon")) {
                for (String line : plugin.getConfig().getStringList("Help.Dungeon")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("command")) {
                for (String line : plugin.getConfig().getStringList("Help.Command")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("pet")) {
                for (String line : plugin.getConfig().getStringList("Help.Pet")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("mcmmo")) {
                for (String line : plugin.getConfig().getStringList("Help.Mcmmo")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("recipe")) {
                for (String line : plugin.getConfig().getStringList("Help.Recipe")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("enchant")) {
                for (String line : plugin.getConfig().getStringList("Help.Enchant")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("vote")) {
                for (String line : plugin.getConfig().getStringList("Help.Vote")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("basics")) {
                for (String line : plugin.getConfig().getStringList("Help.Basics")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            } else if (args[0].equalsIgnoreCase("auction")) {
                for (String line : plugin.getConfig().getStringList("Help.Auction")) {
                    sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
            }
        } else {
            for (String line : plugin.getConfig().getStringList("Help.Menu")) {
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
            }
        }


        return true;
    }
}
