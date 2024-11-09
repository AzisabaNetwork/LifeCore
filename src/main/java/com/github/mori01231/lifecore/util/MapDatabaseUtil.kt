package com.github.mori01231.lifecore.util

import com.github.mori01231.lifecore.DBConnector
import com.github.mori01231.lifecore.LifeCore
import net.minecraft.nbt.ByteArrayTag
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.plugin.java.JavaPlugin
import org.mariadb.jdbc.MariaDbBlob
import java.security.MessageDigest
import java.util.UUID

@Suppress("SqlNoDataSourceInspection")
object MapDatabaseUtil {
    @JvmStatic
    fun getAll(player: Player): List<ItemData> {
        return DBConnector.getPrepareStatement("SELECT `name`, `data`, `amount` FROM `player_maps` WHERE `owner` = ?") { ps ->
            ps.setString(1, player.uniqueId.toString())
            ps.executeQuery().use { rs ->
                val list = mutableListOf<ItemData>()
                while (rs.next()) {
                    list.add(ItemData(
                        player.uniqueId,
                        rs.getString("name"),
                        rs.getBlob("data").binaryStream.use { it.readBytes() },
                        rs.getLong("amount"),
                    ))
                }
                list
            }
        }
    }

    @JvmStatic
    fun getItemData(player: Player, item: ItemStack): ItemData? {
        val data = ItemUtil.getByteArrayTag(item, "SerializedMapData") ?: error("item does not contain SerializedMapData")
        return getItemData(player, data)
    }

    @JvmStatic
    fun getItemData(player: Player, data: ByteArray): ItemData? {
        if (data.isEmpty()) error("data is empty")
        return DBConnector.getPrepareStatement("SELECT `name`, `amount` FROM `player_maps` WHERE `owner` = ? AND `hash` = ?") { ps ->
            ps.setString(1, player.uniqueId.toString())
            ps.setString(2, hashData(data))
            ps.executeQuery().use {
                if (it.next()) {
                    ItemData(player.uniqueId, it.getString("name"), data, it.getLong("amount"))
                } else {
                    null
                }
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun save(player: Player, item: ItemStack, callback: () -> Unit = {}) {
        val existing = getItemData(player, item)
        if (existing != null) {
            save(player, item, existing.name, callback)
            return
        }
        PromptSign.promptSign(player) { lines ->
            val name = ChatColor.translateAlternateColorCodes('&', lines.joinToString(""))
            save(player, item, name, callback)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun save(player: Player, item: ItemStack, name: String, callback: () -> Unit = {}) {
        val data = ItemUtil.getByteArrayTag(item, "SerializedMapData") ?: error("item does not contain SerializedMapData")
        if (data.isEmpty()) error("data is empty")
        DBConnector.runPrepareStatement("INSERT INTO `player_maps` (`owner`, `name`, `data`, `hash`, `amount`) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `amount` = `amount` + VALUES(`amount`)") { ps ->
            ps.setString(1, player.uniqueId.toString())
            ps.setString(2, name)
            ps.setBlob(3, MariaDbBlob(data))
            ps.setString(4, hashData(data))
            ps.setInt(5, item.amount)
            ps.executeUpdate()
        }
        callback()
    }

    @JvmStatic
    fun delete(player: Player, data: ByteArray) {
        DBConnector.runPrepareStatement("DELETE FROM `player_maps` WHERE `owner` = ? AND `hash` = ?") { ps ->
            ps.setString(1, player.uniqueId.toString())
            ps.setString(2, hashData(data))
            if (ps.executeUpdate() == 0) {
                error("item not found")
            }
        }
    }

    @JvmStatic
    fun updateName(player: Player, data: ByteArray, name: String) {
        DBConnector.runPrepareStatement("UPDATE `player_maps` SET `name` = ? WHERE `owner` = ? AND `hash` = ?") { ps ->
            ps.setString(1, name)
            ps.setString(2, player.uniqueId.toString())
            ps.setString(3, hashData(data))
            if (ps.executeUpdate() == 0) {
                error("item not found")
            }
        }
    }

    @JvmStatic
    fun take(player: Player, data: ByteArray, amount: Int): ItemStack {
        if (amount <= 0) error("amount must be greater than 0")
        val itemData = getItemData(player, data) ?: error("item not found")
        DBConnector.runPrepareStatement("UPDATE `player_maps` SET `amount` = `amount` - ? WHERE `owner` = ? AND `hash` = ? AND `amount` >= ?") { ps ->
            ps.setInt(1, amount)
            ps.setString(2, player.uniqueId.toString())
            ps.setString(3, hashData(data))
            ps.setInt(4, amount)
            if (ps.executeUpdate() == 0) {
                error("not enough amount")
            }
        }
        return itemData.copy(amount = amount.toLong()).toItemStack()
    }

    @JvmStatic
    fun hashData(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-512")
        val hash = digest.digest(data)
        return hash.joinToString("") { "%02x".format(it) }
    }

    data class ItemData(
        val owner: UUID,
        val name: String,
        val data: ByteArray,
        val amount: Long,
    ) {
        fun toItemStack() =
            ItemStack(Material.FILLED_MAP, amount.toInt())
                .let { ItemUtil.setTag(it, "SerializedMapData", ByteArrayTag(data)) }
                .apply {
                    itemMeta = (itemMeta as MapMeta).apply {
                        mapView = Bukkit.createMap(Bukkit.getWorlds()[0])
                    }
                }
    }
}
