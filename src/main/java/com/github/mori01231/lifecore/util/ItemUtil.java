package com.github.mori01231.lifecore.util;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
    public static String getMythicType(@Nullable ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        NBTTagCompound tag = CraftItemStack.asNMSCopy(stack).getTag();
        if (tag == null) return null;
        String type = tag.getString("MYTHIC_TYPE");
        if (type == null || type.isEmpty()) return null;
        return type;
    }
}
