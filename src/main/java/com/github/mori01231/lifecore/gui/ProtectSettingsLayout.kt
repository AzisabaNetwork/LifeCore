package com.github.mori01231.lifecore.gui

import com.github.mori01231.lifecore.util.ItemUtil
import net.azisaba.rarity.api.RarityAPIProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

/**
 * Shared layout of the DropProtect / TrashProtect setting screens.
 */
internal object ProtectSettingsLayout {
    const val CLOSE_SLOT = 26

    private val RARITY_ICONS = listOf(
        "common" to Material.COBBLESTONE,
        "uncommon" to Material.IRON_BLOCK,
        "rare" to Material.GOLD_BLOCK,
        "epic" to Material.EMERALD_BLOCK,
        "legendary" to Material.DIAMOND_BLOCK,
        "mythic" to Material.ENCHANTED_GOLDEN_APPLE,
        "special" to Material.NETHER_STAR,
    )

    /** slot -> setting key */
    val SLOT_KEYS: Map<Int, String> = buildMap {
        put(0, "no_rarity")
        RARITY_ICONS.forEachIndexed { index, (id, _) -> put(index + 1, id) }
        put(8, "has_pve_level")
        put(9, "has_no_pve_level")
    }

    fun render(inventory: Inventory, player: Player, isEnabled: (String) -> Boolean) {
        fun status(key: String) =
            if (isEnabled(key)) Component.text("有効", NamedTextColor.GREEN) else Component.text("無効", NamedTextColor.RED)

        fun setIcon(slot: Int, material: Material, name: Component, lore: Component?) {
            inventory.setItem(slot, ItemUtil.createItemStack(material, 1) { item ->
                val meta = item.itemMeta ?: return@createItemStack
                meta.displayName(name)
                if (lore != null) meta.lore(listOf(lore))
                item.itemMeta = meta
            })
        }

        setIcon(0, Material.PAPER, Component.text("レア度が付与されていないアイテム", NamedTextColor.WHITE), status("no_rarity"))
        val rarityAPI = RarityAPIProvider.get()
        RARITY_ICONS.forEachIndexed { index, (id, material) ->
            val displayName = rarityAPI.getRarityById(id)?.getDisplayName(player) ?: id
            setIcon(index + 1, material, LegacyComponentSerializer.legacyAmpersand().deserialize(displayName), status(id))
        }
        setIcon(8, Material.MAP, Component.text("PvEレベルが付与されているアイテム", NamedTextColor.YELLOW), status("has_pve_level"))
        setIcon(9, Material.FILLED_MAP, Component.text("PvEレベルが付与されていないアイテム", NamedTextColor.GRAY), status("has_no_pve_level"))
        setIcon(CLOSE_SLOT, Material.BARRIER, Component.text("閉じる", NamedTextColor.RED), null)
    }
}
