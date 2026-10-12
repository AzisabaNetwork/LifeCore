package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Bukkit.getServer;

public class TutorialCommand implements CommandExecutor {
    private final LifeCore plugin;

    public TutorialCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;
            String tutorial = plugin.getConfig().getString("tutorial-teleport");
            getServer().dispatchCommand(getServer().getConsoleSender(), "mvtp " + player.getName() + " " + tutorial);
            sender.sendMessage(Component.text("任意チュートリアルにテレポートしました。", NamedTextColor.DARK_AQUA));
        } else {
            sender.sendMessage(Component.text("このコマンドはコンソールから使用できません。", NamedTextColor.DARK_AQUA));
        }

        return true;
    }
}
