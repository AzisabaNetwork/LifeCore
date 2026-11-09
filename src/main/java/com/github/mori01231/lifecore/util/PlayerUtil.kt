package com.github.mori01231.lifecore.util

import com.github.mori01231.lifecore.DBConnector
import com.github.mori01231.lifecore.DatabaseConfig
import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.TableKey
import io.netty.channel.Channel
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.CompletableFuture

object PlayerUtil {
    fun resolveUUIDAsync(plugin: LifeCore, name: String): CompletableFuture<UUID> =
        CompletableFuture.supplyAsync({
            // try faster (cached) method first
            try {
                return@supplyAsync resolveUUID(name)
            } catch (_: IllegalArgumentException) {
            }
            // and then try slower (database) method
            fetchUUID(plugin.getDatabaseConfig(), name)
                ?: UUID.nameUUIDFromBytes(name.toByteArray(Charsets.UTF_8))
        }, plugin.asyncExecutor)

    @Throws(IllegalArgumentException::class)
    fun resolveUUID(name: String): UUID {
        Bukkit.getPlayerExact(name)?.let { return it.uniqueId }
        try {
            return UUID.fromString(name)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("name is not UUID or player name")
        }
    }

    fun fetchUUID(databaseConfig: DatabaseConfig, name: String): UUID? {
        val dbName = databaseConfig.getDatabaseName(TableKey.SpicyAzisaBan)
        try {
            return DBConnector.getPrepareStatement("SELECT `uuid` FROM `$dbName`.`players` WHERE LOWER(`name`) = ? LIMIT 1") { ps ->
                ps.setString(1, name.lowercase())
                val rs = ps.executeQuery()
                if (!rs.next()) {
                    null
                } else {
                    UUID.fromString(rs.getString("uuid"))
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    fun getChannel(player: Player): Channel =
        // channel field is set to non-null value after #channelActive is called
        requireNotNull((player as CraftPlayer).handle.connection.connection.channel) { "inactive channel" }
}
