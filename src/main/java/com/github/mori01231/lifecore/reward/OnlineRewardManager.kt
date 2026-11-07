package com.github.mori01231.lifecore.reward

import com.github.mori01231.lifecore.DBConnector
import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.config.OnlineReward
import com.github.mori01231.lifecore.config.OnlineRewardConfig
import com.github.mori01231.lifecore.util.YAML.yaml
import io.lumine.mythic.bukkit.MythicBukkit
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.azisaba.itemstash.ItemStash
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.UUID
import java.util.logging.Level

/**
 * Periodically gives rewards to online players via ItemStash.
 *
 * Time is divided into slots of `interval` seconds (based on UNIX time, so every server agrees on the slot
 * boundaries). When a new slot starts, each server tries to insert `(reward_id, slot, uuid)` into the database for
 * every online player, and only gives the reward if the insert succeeded. This guarantees that a player receives a
 * reward at most once per slot even if they are on (or moving between) multiple servers.
 */
class OnlineRewardManager(private val plugin: LifeCore) {
    private val file get() = plugin.dataFolder.resolve(FILE_NAME)
    private val lastSlots = mutableMapOf<String, Long>()
    private var task: BukkitTask? = null
    var config = OnlineRewardConfig()
        private set

    fun reload() {
        stop()
        if (!file.exists()) {
            file.writeText(yaml.encodeToString(OnlineRewardConfig()))
        }
        config = yaml.decodeFromString(file.readText())
        lastSlots.clear()
        config.rewards.forEach { (id, reward) ->
            require(reward.interval > 0) { "interval of online reward $id must be positive" }
            require(id.length <= 64) { "online reward id $id is too long (max 64 characters)" }
            // only give rewards at slot boundaries that occur after (re)loading
            lastSlots[id] = reward.currentSlot()
        }
        if (!config.enabled) return
        if (!isItemStashAvailable()) {
            plugin.logger.severe("ItemStash is not installed. Online rewards are disabled.")
            return
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable { cleanup() })
        task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable { tick() }, 20, 20)
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    private fun tick() {
        val now = System.currentTimeMillis() / 1000
        config.rewards.forEach { (id, reward) ->
            val slot = reward.currentSlot(now)
            val last = lastSlots[id]
            if (last != null && slot <= last) return@forEach
            lastSlots[id] = slot
            distribute(id, reward, slot)
        }
    }

    /**
     * Adds the reward items to the stash of the online players that have not received the reward in [slot] yet.
     * Must be called from the main thread.
     */
    fun distribute(id: String, reward: OnlineReward, slot: Long) {
        val targets = Bukkit.getOnlinePlayers().map { it.uniqueId }
        if (targets.isEmpty()) return
        val items = buildItems(id, reward) ?: return
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val claimed = try {
                claim(id, slot, targets)
            } catch (e: Exception) {
                plugin.logger.log(Level.SEVERE, "Failed to claim online reward $id (slot $slot)", e)
                return@Runnable
            }
            val given = claimed.filter { uuid ->
                try {
                    items.forEach { ItemStash.getInstance().addItemToStash(uuid, it.clone()) }
                    true
                } catch (e: Exception) {
                    plugin.logger.log(Level.SEVERE, "Failed to add online reward $id to the stash of $uuid", e)
                    // allow the player to receive the reward on another server or by /lifecoreutil onlineReward give
                    unclaim(id, slot, uuid)
                    false
                }
            }
            if (given.isEmpty()) return@Runnable
            Bukkit.getScheduler().runTask(plugin, Runnable {
                given.forEach { uuid ->
                    Bukkit.getPlayer(uuid)?.apply {
                        sendMessage(Component.text("オンライン報酬がStashに追加されました。", NamedTextColor.GREEN))
                        sendMessage(
                            Component.text("/pickupstash", NamedTextColor.AQUA)
                                .append(Component.text("で回収できます。", NamedTextColor.GREEN))
                        )
                    }
                }
            })
        })
    }

    /**
     * Resolves the MythicMobs items of the reward. Returns null if any of them could not be found, so that the reward
     * is not claimed partially.
     */
    private fun buildItems(id: String, reward: OnlineReward): List<ItemStack>? {
        if (!isItemStashAvailable()) {
            plugin.logger.severe("Cannot give online reward $id because ItemStash is not installed.")
            return null
        }
        return reward.items.map { item ->
            MythicBukkit.inst()
                .itemManager
                .getItemStack(item.id)
                ?.clone()
                ?.apply { amount = item.amount }
                ?: run {
                    plugin.logger.severe("Cannot give online reward $id because Mythic Item ${item.id} was not found.")
                    return null
                }
        }
    }

    /**
     * Records the reward for each player and returns the players for which the record was newly inserted (i.e. the
     * players that have not received the reward in this slot on any server).
     */
    private fun claim(id: String, slot: Long, players: List<UUID>): List<UUID> {
        val server = plugin.config.getString("lp-server-name") ?: "unknown"
        // executeUpdate is called per row instead of executeBatch because rewriteBatchedStatements may cause the
        // driver to return SUCCESS_NO_INFO instead of the actual update counts
        return DBConnector.getPrepareStatement(
            "INSERT IGNORE INTO `online_rewards` (`reward_id`, `slot`, `uuid`, `server`) VALUES (?, ?, ?, ?)"
        ) { ps ->
            players.filter { uuid ->
                ps.setString(1, id)
                ps.setLong(2, slot)
                ps.setString(3, uuid.toString())
                ps.setString(4, server)
                ps.executeUpdate() > 0
            }
        }
    }

    private fun unclaim(id: String, slot: Long, uuid: UUID) {
        try {
            DBConnector.runPrepareStatement("DELETE FROM `online_rewards` WHERE `reward_id` = ? AND `slot` = ? AND `uuid` = ?") { ps ->
                ps.setString(1, id)
                ps.setLong(2, slot)
                ps.setString(3, uuid.toString())
                ps.executeUpdate()
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to remove online reward record of $uuid ($id, slot $slot)", e)
        }
    }

    private fun cleanup() {
        try {
            DBConnector.runPrepareStatement("DELETE FROM `online_rewards` WHERE `given_at` < NOW() - INTERVAL ? DAY") { ps ->
                ps.setInt(1, config.retentionDays)
                ps.executeUpdate()
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to clean up old online reward records", e)
        }
    }

    private fun isItemStashAvailable() =
        try {
            Class.forName("net.azisaba.itemstash.ItemStash")
            true
        } catch (_: Throwable) {
            false
        }

    companion object {
        const val FILE_NAME = "config-online-reward.yml"
    }
}
