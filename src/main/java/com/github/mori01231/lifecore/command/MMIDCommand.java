package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MMIDCommand extends PlayerTabExecutor {
    @Override
    public boolean execute(@NotNull Player player, @NotNull String [] args) {
        Player target = player;
        if (args.length > 0 && player.hasPermission("lifecore.mmid.others")) {
            target = Bukkit.getPlayerExact(args[0]);
        }
        if (target == null) {
            player.sendMessage(Component.text("プレイヤーが見つかりませんでした。", NamedTextColor.RED));
            return true;
        }
        ItemStack stack = target.getInventory().getItemInMainHand();
        String id = ItemUtil.getMythicType(stack);
        Component clickableId = Component.text(id == null ? "<null>" : id, NamedTextColor.AQUA)
                .hoverEvent(HoverEvent.showText(Component.text("クリックでコピー")))
                .clickEvent(ClickEvent.copyToClipboard(id == null ? "null" : id));
        Component component = Component.text("ID: ").append(clickableId);
        player.sendMessage(component);
        return true;
    }
}
