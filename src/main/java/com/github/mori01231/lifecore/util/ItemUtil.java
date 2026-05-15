package com.github.mori01231.lifecore.util;

import net.azisaba.itemstash.ItemStash;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class ItemUtil {

    @Contract("null -> false")
    public static boolean isProbablyAdminSword(@Nullable ItemStack stack) {
        if (stack == null || stack.getType().isAir() || !stack.hasItemMeta()) return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !meta.hasAttributeModifiers()) return false;

        var modifiers = meta.getAttributeModifiers(Attribute.ATTACK_DAMAGE);
        if (modifiers == null) return false;

        return modifiers.stream().anyMatch(mod -> mod.getAmount() >= 9999);
    }

    public static @Nullable CompoundTag getCustomData(@Nullable ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        CustomData customData = nmsStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        return customData.copyTag();
    }

    @Contract("null -> null")
    public static @Nullable String getMythicType(@Nullable ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        if (tag == null || !tag.contains("PublicBukkitValues")) return null;
        return tag.getCompound("PublicBukkitValues")
                .map(pbv -> {
                    String type = pbv.getString("mythicmobs:type").orElse("");
                    return type.isEmpty() ? null : type;
                })
                .orElse(null);
    }

    @Contract("null, _ -> null")
    public static @Nullable Tag getTag(@Nullable ItemStack stack, @NotNull String key) {
        CompoundTag tag = getCustomData(stack);
        return (tag == null) ? null : tag.get(key);
    }

    @Contract("null, _ -> null")
    public static @Nullable String getStringTag(@Nullable ItemStack stack, @NotNull String key) {
        CompoundTag tag = getCustomData(stack);
        if (tag == null) return null;
        return tag.getString(key).orElse(null);
    }

    public static int getIntTag(@Nullable ItemStack stack, @NotNull String key) {
        CompoundTag tag = getCustomData(stack);
        if (tag == null) return 0;
        return tag.getInt(key).orElse(0);
    }

    @Contract("null, _ -> null")
    public static @Nullable byte[] getByteArrayTag(@Nullable ItemStack stack, @NotNull String key) {
        CompoundTag tag = getCustomData(stack);
        if (tag == null) return null;
        return tag.getByteArray(key).orElse(null);
    }

    public static @NotNull ItemStack setStringTag(@Nullable ItemStack stack, @NotNull String key, @NotNull String value) {
        if (stack == null || stack.getType().isAir()) return new ItemStack(Material.AIR);
        CompoundTag tag = getCustomData(stack);
        if (tag == null) tag = new CompoundTag();
        tag.putString(key, value);
        return applyCustomData(stack, tag);
    }

    public static @NotNull ItemStack setTag(@Nullable ItemStack stack, @Nullable String key, @NotNull Tag nbt) {
        if (stack == null || stack.getType().isAir()) return new ItemStack(Material.AIR);
        CompoundTag tag;
        if (key == null) {
            if (!(nbt instanceof CompoundTag)) throw new IllegalArgumentException("key is null, but nbt is not CompoundTag");
            tag = (CompoundTag) nbt;
        } else {
            tag = getCustomData(stack);
            if (tag == null) tag = new CompoundTag();
            tag.put(key, nbt);
        }
        return applyCustomData(stack, tag);
    }

    private static @NotNull ItemStack applyCustomData(@NotNull ItemStack stack, @NotNull CompoundTag tag) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData.set(DataComponents.CUSTOM_DATA, nms, tag);
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static @NotNull String toString(@NotNull ItemStack stack) {
        StringJoiner props = new StringJoiner("");
        props.add("[Type: " + stack.getType().name() + "]");
        props.add("[Amount: " + stack.getAmount() + "]");
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) props.add("[Name: " + meta.getDisplayName() + "]");
            if (meta.hasLore()) props.add("[Lore: " + Objects.requireNonNull(meta.getLore()).size() + " entries]");
            if (meta.hasCustomModelData()) props.add("[CustomModelData: " + meta.getCustomModelData() + "]");
            String mmType = getMythicType(stack);
            if (mmType != null) props.add("[MMID: " + mmType + "]");
            if (meta.hasEnchants()) meta.getEnchants().forEach((enchant, level) -> props.add("[Enchant: " + enchant.getKey().getKey() + " " + level + "]"));
        }
        return props.toString();
    }

    public static @NotNull ItemStack createItemStack(@NotNull Material material, int amount, @NotNull Consumer<ItemStack> action) {
        ItemStack stack = new ItemStack(material, amount);
        action.accept(stack);
        return stack;
    }

    public static @NotNull ItemStack createItemStack(@NotNull Material material, @NotNull String displayName, @NotNull List<String> lore) {
        return createItemStack(material, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(displayName);
                if (!lore.isEmpty()) meta.setLore(lore);
                item.setItemMeta(meta);
            }
        });
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

    public static boolean isEquippedInAnySlot(@NotNull Player player, @NotNull ItemStack stack) {
        PlayerInventory inventory = player.getInventory();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            try {
                ItemStack item = inventory.getItem(slot);
                if (item != null && item.equals(stack)) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public static boolean isEquippedInAnySlot(@NotNull Player player, @NotNull String mythicType) {
        PlayerInventory inventory = player.getInventory();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            try {
                ItemStack item = inventory.getItem(slot);
                if (item != null && mythicType.equals(getMythicType(item))) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    @Contract("null -> null")
    public static ItemStack backupTag(@Nullable ItemStack stack) {
        if (stack == null) return null;
        CompoundTag tag = getCustomData(stack);
        if (tag == null || tag.isEmpty()) return stack;
        if (tag.contains("backup")) return stack;

        boolean isAdmin = tag.getCompound("PublicBukkitValues")
                .map(pbv -> pbv.getInt("minecraft:admin_item").orElse(0) == 1)
                .orElse(false);

        if (isAdmin) return stack;

        return setTag(stack, "backup", tag);
    }

    private static final Set<String> RESTORE_BYPASS_SET = Set.of("Damage");

    @Contract("null -> null")
    public static ItemStack restoreTag(@Nullable ItemStack stack) {
        if (stack == null) return null;
        CompoundTag tag = getCustomData(stack);
        if (tag == null || !tag.contains("backup")) return stack;

        return tag.getCompound("backup").map(backup -> {
            ItemStack newStack = setTag(stack, null, backup);
            for (String bypassTag : RESTORE_BYPASS_SET) {
                if (containsTag(stack, bypassTag)) {
                    Tag value = getTag(stack, bypassTag);
                    if (value != null) {
                        newStack = setTag(newStack, bypassTag, value);
                    }
                }
            }
            return newStack;
        }).orElse(stack);
    }

    public static boolean containsTag(@Nullable ItemStack stack, @NotNull String key) {
        CompoundTag tag = getCustomData(stack);
        return tag != null && tag.contains(key);
    }

    public static @NotNull ItemStack cloneWithNewMaterial(@NotNull ItemStack stack, @NotNull Material material) {
        ItemStack newStack = new ItemStack(material, stack.getAmount());
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) newStack.setItemMeta(meta);
        return newStack;
    }
}
