package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.util.ItemUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MMIDCommand implements PlayerTabExecutor {
    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        Player target;
        if (args.length == 0) {
            target = player;
        } else {
            target = Bukkit.getPlayerExact(args[0]);
        }
        if (target == null) {
            player.sendMessage(ChatColor.RED + "プレイヤーが見つかりませんでした。");
            return true;
        }
        ItemStack stack = target.getInventory().getItemInMainHand();
        String id = ItemUtil.getMythicType(stack);
        TextComponent component = new TextComponent("ID: ");
        TextComponent clickableId = new TextComponent(id == null ? "<null>" : id);
        clickableId.setColor(ChatColor.AQUA.asBungee());
        clickableId.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("クリックでコピー")}));
        clickableId.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id == null ? "null" : id));
        component.addExtra(clickableId);
        player.sendMessage(component);
        return true;
    }
}
