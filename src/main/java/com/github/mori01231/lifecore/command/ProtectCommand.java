package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * {@code /protect}: toggles drop/trash protection for the kind of item held in the main hand.
 * <p>
 * Works for any item, including items without a PvE level. See {@link ItemUtil#getProtectKey(ItemStack)}
 * for how the "kind" of the item is determined.
 */
public class ProtectCommand implements TabExecutor {
    private final LifeCore plugin;

    public ProtectCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be executed by players.", NamedTextColor.RED));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        String key = ItemUtil.getProtectKey(item);
        if (key == null) {
            player.sendMessage(Component.text("手にアイテムを持ってください。", NamedTextColor.RED));
            return true;
        }

        if (plugin.getMythicItemProtectConfig().toggle(player.getUniqueId(), key)) {
            player.sendMessage(Component.text("アイテム \"" + key + "\" のドロップ/ゴミ箱保護を有効にしました。", NamedTextColor.GREEN));
            if (key.startsWith("minecraft:")) {
                player.sendMessage(Component.text("※同じ種類のアイテム(" + key + ")はすべて保護されます。", NamedTextColor.YELLOW));
            }
        } else {
            player.sendMessage(Component.text("アイテム \"" + key + "\" のドロップ/ゴミ箱保護を無効にしました。", NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
