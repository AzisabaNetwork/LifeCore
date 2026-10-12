package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WebsiteCommand implements CommandExecutor {
    private final LifeCore plugin;
    
    public WebsiteCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3ホームページ: &f&l" + plugin.getConfig().getString("website-url")));
        return true;
    }
}
