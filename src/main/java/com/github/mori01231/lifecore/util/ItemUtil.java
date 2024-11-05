package com.github.mori01231.lifecore.util;

import net.azisaba.itemstash.ItemStash;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
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
        CompoundTag tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null) return null;
        String type = tag.getString("MYTHIC_TYPE");
        if (type.isEmpty()) return null;
        return type;
    }

    @Contract("null, _ -> null")
    public static @Nullable NBTBase getTag(@Nullable ItemStack stack, @NotNull String key) {
        if (stack == null || stack.getType().isAir()) return null;
        NBTTagCompound tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null) return null;
        return tag.get(key);
    }

    @Contract("null, _ -> null")
    public static @Nullable String getStringTag(@Nullable ItemStack stack, @NotNull String key) {
        if (stack == null || stack.getType().isAir()) return null;
        CompoundTag tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null) return null;
        return tag.getString(key);
    }

    public static int getIntTag(@Nullable ItemStack stack, @NotNull String key) {
        if (stack == null || stack.getType().isAir()) return 0;
        NBTTagCompound tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null) return 0;
        return tag.getInt(key);
    }

    @Contract("null, _ -> null")
    public static @Nullable byte[] getByteArrayTag(@Nullable ItemStack stack, @NotNull String key) {
        if (stack == null || stack.getType().isAir()) return null;
        CompoundTag tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null) return null;
        return tag.getByteArray(key);
    }

    public static @NotNull ItemStack setStringTag(@Nullable ItemStack stack, @NotNull String key, @NotNull String value) {
        if (stack == null || stack.getType().isAir()) return new ItemStack(Material.AIR);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CompoundTag tag = nms.getOrCreateTag();
        tag.putString(key, value);
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static @NotNull ItemStack setTag(@Nullable ItemStack stack, @Nullable String key, @NotNull Tag nbt) {
        if (stack == null || stack.getType().isAir()) return new ItemStack(Material.AIR);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        if (key == null) {
            if (!(nbt instanceof CompoundTag)) {
                throw new IllegalArgumentException("key is null, but nbt is not NBTTagCompound");
            }
            nms.setTag((CompoundTag) nbt);
        } else {
            CompoundTag tag = nms.getOrCreateTag();
            tag.put(key, nbt);
        }
        return CraftItemStack.asBukkitCopy(nms);
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

    public static @NotNull ItemStack createItemStack(@NotNull Material material, @NotNull String displayName, @NotNull List<String> lore) {
        return createItemStack(material, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
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

    @Contract("null -> null")
    public static ItemStack backupTag(@Nullable ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        CompoundTag tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null || tag.isEmpty()) return stack;
        if (!tag.getCompound("backup").isEmpty()) return stack;
        // exclude some items
        if (tag.getCompound("PublicBukkitValues").getInt("minecraft:admin_item") == 1) {
            return stack;
        }
        return setTag(stack, "backup", tag);
    }

    private static final Set<String> RESTORE_BYPASS_SET = new HashSet<>(Collections.singletonList("Damage"));

    @Contract("null -> null")
    public static ItemStack restoreTag(@Nullable ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        CompoundTag tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null) return stack;
        CompoundTag backup = tag.getCompound("backup");
        if (backup.isEmpty()) return stack;
        ItemStack newStack = setTag(stack, null, backup);
        for (String bypassTag : RESTORE_BYPASS_SET) {
            if (containsTag(stack, bypassTag)) {
                NBTBase value = getTag(stack, bypassTag);
                assert value != null;
                newStack = setTag(newStack, bypassTag, value);
            }
        }
        return newStack;
    }

    public static boolean containsTag(@Nullable ItemStack stack, @NotNull String key) {
        if (stack == null || stack.getType().isAir()) return false;
        CompoundTag tag = CraftItemStack.asNMSCopy(stack).getTag();
        return tag != null && tag.contains(key);
    }
}
