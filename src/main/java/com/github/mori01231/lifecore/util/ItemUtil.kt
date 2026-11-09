package com.github.mori01231.lifecore.util

import io.lumine.mythic.bukkit.MythicBukkit
import net.azisaba.itemstash.ItemStash
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.component.CustomData
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.UUID

object ItemUtil {
    private val RESTORE_BYPASS_SET = setOf("Damage")

    fun isProbablyAdminSword(stack: ItemStack?): Boolean {
        if (stack == null || stack.type.isAir || !stack.hasItemMeta()) return false
        val meta = stack.itemMeta ?: return false
        if (!meta.hasAttributeModifiers()) return false
        val modifiers = meta.getAttributeModifiers(Attribute.ATTACK_DAMAGE) ?: return false
        return modifiers.any { it.amount >= 9999 }
    }

    fun getCustomData(stack: ItemStack?): CompoundTag? {
        if (stack == null || stack.type.isAir) return null
        return CraftItemStack.asNMSCopy(stack).get(DataComponents.CUSTOM_DATA)?.copyTag()
    }

    fun getMythicType(item: ItemStack?): String? {
        if (item == null || item.type.isAir) {
            return null
        }
        return MythicBukkit.inst().itemManager.getMythicTypeFromItem(item)
    }

    fun getTag(stack: ItemStack?, key: String): Tag? = getCustomData(stack)?.get(key)

    fun getStringTag(stack: ItemStack?, key: String): String? = getCustomData(stack)?.getString(key)?.orElse(null)

    fun getIntTag(stack: ItemStack?, key: String): Int = getCustomData(stack)?.getInt(key)?.orElse(0) ?: 0

    fun getByteArrayTag(stack: ItemStack?, key: String): ByteArray? = getCustomData(stack)?.getByteArray(key)?.orElse(null)

    fun setStringTag(stack: ItemStack?, key: String, value: String): ItemStack {
        if (stack == null || stack.type.isAir) return ItemStack(Material.AIR)
        val tag = getCustomData(stack) ?: CompoundTag()
        tag.putString(key, value)
        return applyCustomData(stack, tag)
    }

    fun setTag(stack: ItemStack?, key: String?, nbt: Tag): ItemStack {
        if (stack == null || stack.type.isAir) return ItemStack(Material.AIR)
        val tag = if (key == null) {
            require(nbt is CompoundTag) { "key is null, but nbt is not CompoundTag" }
            nbt
        } else {
            (getCustomData(stack) ?: CompoundTag()).also { it.put(key, nbt) }
        }
        return applyCustomData(stack, tag)
    }

    private fun applyCustomData(stack: ItemStack, tag: CompoundTag): ItemStack {
        val nms = CraftItemStack.asNMSCopy(stack)
        CustomData.set(DataComponents.CUSTOM_DATA, nms, tag)
        return CraftItemStack.asBukkitCopy(nms)
    }

    fun setCustomModelData(meta: ItemMeta, data: Int?) {
        if (data == null) {
            meta.setCustomModelDataComponent(null)
        } else {
            val component = meta.customModelDataComponent
            component.floats = listOf(data.toFloat())
            meta.setCustomModelDataComponent(component)
        }
    }

    fun toString(stack: ItemStack): String {
        val props = StringBuilder()
        props.append("[Type: ${stack.type.name}]")
        props.append("[Amount: ${stack.amount}]")
        val meta = stack.itemMeta
        if (meta != null) {
            if (meta.hasDisplayName()) {
                props.append("[Name: " + LegacyComponentSerializer.legacySection().serialize(meta.displayName()!!) + "]")
            }
            if (meta.hasLore()) props.append("[Lore: " + meta.lore()!!.size + " entries]")
            if (meta.hasCustomModelDataComponent()) {
                meta.customModelDataComponent.floats.firstOrNull()?.let {
                    props.append("[CustomModelData: " + it.toInt() + "]")
                }
            }
            getMythicType(stack)?.let { props.append("[MMID: $it]") }
            if (meta.hasEnchants()) {
                meta.enchants.forEach { (enchant, level) -> props.append("[Enchant: " + enchant.key.key + " " + level + "]") }
            }
        }
        return props.toString()
    }

    fun createItemStack(material: Material, amount: Int, action: (ItemStack) -> Unit): ItemStack =
        ItemStack(material, amount).also(action)

    fun createItemStack(material: Material, displayName: String, lore: List<String>): ItemStack =
        createItemStack(material, 1) { item ->
            val meta = item.itemMeta ?: return@createItemStack
            val serializer = LegacyComponentSerializer.legacySection()
            meta.displayName(serializer.deserialize(displayName))
            if (lore.isNotEmpty()) {
                meta.lore(lore.map { serializer.deserialize(it) })
            }
            item.itemMeta = meta
        }

    fun addToStashIfEnabled(uuid: UUID, item: ItemStack): Boolean =
        try {
            Class.forName("net.azisaba.itemstash.ItemStash")
            ItemStash.getInstance().addItemToStash(uuid, item)
            true
        } catch (_: Throwable) {
            false
        }

    fun isEquippedInAnySlot(player: Player, stack: ItemStack): Boolean =
        anyEquippedItem(player) { it == stack }

    fun isEquippedInAnySlot(player: Player, mythicType: String): Boolean =
        anyEquippedItem(player) { mythicType == getMythicType(it) }

    private fun anyEquippedItem(player: Player, predicate: (ItemStack) -> Boolean): Boolean {
        val inventory = player.inventory
        return EquipmentSlot.entries.any { slot ->
            try {
                predicate(inventory.getItem(slot))
            } catch (_: Exception) {
                // some slots (e.g. body/saddle) are not supported by player inventory
                false
            }
        }
    }

    fun backupTag(stack: ItemStack?): ItemStack? {
        if (stack == null) return null
        val tag = getCustomData(stack)
        if (tag == null || tag.isEmpty) return stack
        if (tag.contains("backup")) return stack
        val isAdmin = tag.getCompound("PublicBukkitValues")
            .map { it.getInt("minecraft:admin_item").orElse(0) == 1 }
            .orElse(false)
        if (isAdmin) return stack
        return setTag(stack, "backup", tag)
    }

    fun restoreTag(stack: ItemStack?): ItemStack? {
        if (stack == null) return null
        val tag = getCustomData(stack)
        if (tag == null || !tag.contains("backup")) return stack
        val backup = tag.getCompound("backup").orElse(null) ?: return stack
        var newStack = setTag(stack, null, backup)
        for (bypassTag in RESTORE_BYPASS_SET) {
            if (containsTag(stack, bypassTag)) {
                getTag(stack, bypassTag)?.let { newStack = setTag(newStack, bypassTag, it) }
            }
        }
        return newStack
    }

    /**
     * Returns the key used by `/protect` to identify the "kind" of the item.
     * - MythicMobs items: the MythicMobs item type (e.g. `ontime_diamond`)
     * - Life items (items with `LifeItemId` tag): `lifeitem:<id>`
     * - Other items: the namespaced material key (e.g. `minecraft:diamond`)
     *
     * MythicMobs types are stored without a prefix so that data saved by older versions stays valid.
     */
    fun getProtectKey(item: ItemStack?): String? {
        if (item == null || item.type.isAir) {
            return null
        }
        getMythicType(item)?.let { return it }
        val lifeItemId = getStringTag(item, "LifeItemId")
        if (!lifeItemId.isNullOrEmpty()) {
            return "lifeitem:$lifeItemId"
        }
        return item.type.key.toString()
    }

    /**
     * Returns true if the item has no modified data components (equivalent to "no NBT tag" in older versions).
     */
    fun isVanillaItem(stack: ItemStack): Boolean = CraftItemStack.unwrap(stack).componentsPatch.isEmpty

    fun containsTag(stack: ItemStack?, key: String): Boolean = getCustomData(stack)?.contains(key) == true

    fun cloneWithNewMaterial(stack: ItemStack, material: Material): ItemStack {
        val newStack = stack.withType(material)
        val meta = newStack.itemMeta
        if (meta != null && meta.hasTool()) {
            meta.setTool(null)
            newStack.itemMeta = meta
        }
        return newStack
    }
}
