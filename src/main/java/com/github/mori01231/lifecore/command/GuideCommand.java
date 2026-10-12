package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Bukkit.getServer;

public class GuideCommand implements CommandExecutor {
    private final LifeCore plugin;
    
    public GuideCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 1) {
            if (sender.hasPermission("lifecore.giveguide")) {

                String guide = plugin.getConfig().getString("guide");
                getServer().dispatchCommand(getServer().getConsoleSender(), "minecraft:give " + args[0] + " " + guide);
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&a&l" + args[0] + "&3にガイドブックを授与しました。"));
            } else {
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&4権限不足です"));
            }
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String guide = plugin.getConfig().getString("guide");
                getServer().dispatchCommand(getServer().getConsoleSender(), "minecraft:give " + player.getName() + " " + guide);
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3ガイドブックを入手しました。"));
            } else {
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3このコマンドはコンソールから使用できません。"));
            }
        }

        return true;
    }
}
