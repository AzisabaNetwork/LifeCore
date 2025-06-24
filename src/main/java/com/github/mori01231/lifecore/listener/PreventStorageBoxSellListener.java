package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PreventStorageBoxSellListener implements Listener {
    private final LifeCore plugin;

    public PreventStorageBoxSellListener(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the inventory title contains "Menu" and "Sell"
        if (event.getView().getTitle().contains("Menu") && event.getView().getTitle().contains("Sell")) {
            // Check if player is moving an item TO the sell screen (clicked inventory is the top inventory)
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
                ItemStack item = null;
                
                // Handle different click types
                switch (event.getAction()) {
                    case PLACE_ALL:
                    case PLACE_ONE:
                    case PLACE_SOME:
                    case SWAP_WITH_CURSOR:
                        item = event.getCursor();
                        break;
                    case MOVE_TO_OTHER_INVENTORY:
                        item = event.getCurrentItem();
                        break;
                    default:
                        // For shift-click from player inventory
                        if (event.isShiftClick() && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
                            item = event.getCurrentItem();
                        }
                        break;
                }
                
                if (item != null && hasStorageBoxType(item)) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "StorageBoxは売却できません。");
                }
            }
            
            // Handle shift-clicking from player inventory to sell screen
            if (event.isShiftClick() && event.getClickedInventory() != null 
                && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
                ItemStack item = event.getCurrentItem();
                if (item != null && hasStorageBoxType(item)) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "ストレージボックスは売却できません。");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Check if the inventory title contains "Menu" and "Sell"
        if (event.getView().getTitle().contains("Menu") && event.getView().getTitle().contains("Sell")) {
            // Check if any of the dragged slots are in the top inventory
            boolean isDraggingToSellScreen = event.getRawSlots().stream()
                .anyMatch(slot -> slot < event.getView().getTopInventory().getSize());
                
            if (isDraggingToSellScreen) {
                ItemStack item = event.getOldCursor();
                if (item != null && hasStorageBoxType(item)) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "ストレージボックスは売却できません。");
                }
            }
        }
    }

    private boolean hasStorageBoxType(@NotNull ItemStack item) {
        String storageBoxType = ItemUtil.getStringTag(item, "storageBoxType");
        return storageBoxType != null && !storageBoxType.isEmpty();
    }
}