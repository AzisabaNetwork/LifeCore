package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class VoteCommand implements CommandExecutor {
    private final LifeCore plugin;
    
    public VoteCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bJMS: &f" + plugin.getConfig().getString("vote-url-jms")));
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&aMonocraft: &f" + plugin.getConfig().getString("vote-url-monocraft")));

        return true;
    }
}
