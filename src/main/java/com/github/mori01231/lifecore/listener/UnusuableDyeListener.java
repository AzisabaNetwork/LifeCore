package com.github.mori01231.lifecore.listener;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class UnusuableDyeListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {

        PlayerInventory inv = e.getPlayer().getInventory();

        if ( !inv.getItemInMainHand().getItemMeta().hasDisplayName() ) return;

        Block block = e.getClickedBlock();
        Material blockType = Objects.requireNonNull(block).getType();

        if ( blockType == Material.OAK_SIGN || blockType == Material.ACACIA_SIGN || blockType == Material.BIRCH_SIGN || blockType == Material.DARK_OAK_SIGN || blockType == Material.JUNGLE_SIGN || blockType == Material.SPRUCE_SIGN ) {

            Player player = e.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            DyeColor dyeColor = getDyeColor(item.getType());

            if (dyeColor != null) {

                e.setCancelled(true);

            }

        }
    }

    private DyeColor getDyeColor(Material material) {
        if (material == Material.WHITE_DYE) {
            return DyeColor.WHITE;
        } else if (material == Material.ORANGE_DYE) {
            return DyeColor.ORANGE;
        } else if (material == Material.MAGENTA_DYE) {
            return DyeColor.MAGENTA;
        } else if (material == Material.LIGHT_BLUE_DYE) {
            return DyeColor.LIGHT_BLUE;
        } else if (material == Material.YELLOW_DYE) {
            return DyeColor.YELLOW;
        } else if (material == Material.LIME_DYE) {
            return DyeColor.LIME;
        } else if (material == Material.PINK_DYE) {
            return DyeColor.PINK;
        } else if (material == Material.GRAY_DYE) {
            return DyeColor.GRAY;
        } else if (material == Material.LIGHT_GRAY_DYE) {
            return DyeColor.LIGHT_GRAY;
        } else if (material == Material.CYAN_DYE) {
            return DyeColor.CYAN;
        } else if (material == Material.PURPLE_DYE) {
            return DyeColor.PURPLE;
        } else if (material == Material.BLUE_DYE) {
            return DyeColor.BLUE;
        } else if (material == Material.BROWN_DYE) {
            return DyeColor.BROWN;
        } else if (material == Material.GREEN_DYE) {
            return DyeColor.GREEN;
        } else if (material == Material.RED_DYE) {
            return DyeColor.RED;
        } else if (material == Material.BLACK_DYE) {
            return DyeColor.BLACK;
        } else {
            return null;
        }
    }
}
