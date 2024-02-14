package com.github.mori01231.lifecore.util;

import net.azisaba.itemstash.ItemStash;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemUtil {
    @Contract("null -> false")
    public static boolean isProbablyAdminSword(@Nullable ItemStack stack) {
        if (stack == null ||
                stack.getType().isAir() ||
                !stack.hasItemMeta() ||
                !stack.getItemMeta().hasAttributeModifiers()) return false;
        return Objects.requireNonNull(stack.getItemMeta().getAttributeModifiers())
                .get(Attribute.GENERIC_ATTACK_DAMAGE)
                .stream()
                .anyMatch(mod -> mod.getAmount() >= 9999);
    }

    @Contract("null -> null")
    public static @Nullable String getMythicType(@Nullable ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        NBTTagCompound tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null) return null;
        String type = tag.getString("MYTHIC_TYPE");
        if (type == null || type.isEmpty()) return null;
        return type;
    }

    @Contract("null, _ -> null")
    public static @Nullable String getStringTag(@Nullable ItemStack stack, @NotNull String key) {
        if (stack == null || stack.getType().isAir()) return null;
        NBTTagCompound tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null) return null;
        return tag.getString(key);
    }

    public static @NotNull String toString(@NotNull ItemStack stack) {
        List<String> props = new ArrayList<>();
        props.add("[Type: " + stack.getType().name() + "]");
        props.add("[Amount: " + stack.getAmount() + "]");
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) props.add("[Name: " + meta.getDisplayName() + "]");
            if (meta.hasLore()) props.add("[Lore: " + Objects.requireNonNull(meta.getLore()).size() + " entries]");
            if (meta.hasCustomModelData()) props.add("[CustomModelData: " + meta.getCustomModelData() + "]");
            if (getMythicType(stack) != null) props.add("[MMID: " + getMythicType(stack) + "]");
            if (meta.hasEnchants()) meta.getEnchants().forEach((enchant, level) -> props.add("[Enchant: " + enchant.getKey() + " " + level + "]"));
        }
        return String.join("", props);
    }

    public static @NotNull ItemStack createItemStack(@NotNull Material material, int amount, @NotNull Consumer<ItemStack> action) {
        ItemStack stack = new ItemStack(material, amount);
        action.accept(stack);
        return stack;
    }

    public static boolean addToStashIfEnabled(@NotNull UUID uuid, @NotNull ItemStack item) {
        try {
            Class.forName("net.azisaba.itemstash.ItemStash");
            ItemStash.getInstance().addItemToStash(uuid, item);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    @SuppressWarnings("ConstantValue")
    public static boolean isEquippedInAnySlot(@NotNull Player player, @NotNull ItemStack stack) {
        PlayerInventory inventory = player.getInventory();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (inventory.getItem(slot) != null && inventory.getItem(slot).equals(stack)) return true;
        }
        return false;
    }

    public static boolean isEquippedInAnySlot(@NotNull Player player, @NotNull String mythicType) {
        PlayerInventory inventory = player.getInventory();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (mythicType.equals(getMythicType(inventory.getItem(slot)))) return true;
        }
        return false;
    }
}
