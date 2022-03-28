package com.github.mori01231.lifecore.util;

import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemUtil {
    public static boolean isProbablyAdminSword(ItemStack stack) {
        if (stack == null ||
                stack.getType().isAir() ||
                !stack.hasItemMeta() ||
                !stack.getItemMeta().hasAttributeModifiers()) return false;
        return Objects.requireNonNull(stack.getItemMeta().getAttributeModifiers())
                .get(Attribute.GENERIC_ATTACK_DAMAGE)
                .stream()
                .anyMatch(mod -> mod.getAmount() >= 9999);
    }
}
