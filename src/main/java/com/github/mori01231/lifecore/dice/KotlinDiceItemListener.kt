package com.github.mori01231.lifecore.dice

import com.destroystokyo.paper.profile.ProfileProperty
import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID
import kotlin.random.Random

/**
 * 旧 KotlinDice プラグイン。
 * カスタムタグ付きプレイヤーヘッドを右/左クリックすると、周囲のプレイヤーから当選者を抽選する。
 */
class KotlinDiceItemListener(val plugin: LifeCore) : Listener {

    private val wonPlayers = HashSet<UUID>()
    private val cooldowns = HashMap<UUID, Long>()
    private val cooldownTimeMs = 5000L

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        if (item.type != Material.PLAYER_HEAD) return
        if (ItemUtil.getStringTag(item, TAG_KEY) != "1") return

        val now = System.currentTimeMillis()
        if (player.isSneaking && (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) {
            wonPlayers.clear()
            player.sendMessage(colored("&d[KotlinDice] &aすべての当選履歴をリセットしました"))
            return
        }

        val expireTime = cooldowns[player.uniqueId]
        if (expireTime != null && expireTime > now) {
            val remaining = (expireTime - now) / 1000
            player.sendMessage(colored("&d[KotlinDice] &cクールダウン中です (残り${remaining}秒)"))
            return
        }

        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            cooldowns[player.uniqueId] = now + cooldownTimeMs
            executeInstantDice(player)
        } else if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
            cooldowns[player.uniqueId] = now + cooldownTimeMs
            executeCountdownDice(player)
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val item = event.itemInHand
        if (item.type != Material.PLAYER_HEAD) return
        if (ItemUtil.getStringTag(item, TAG_KEY) == "1") {
            event.isCancelled = true
        }
    }

    private fun setSkullTexture(meta: SkullMeta, base64: String) {
        val profile = Bukkit.createProfile(UUID.randomUUID(), null as String?)
        profile.setProperty(ProfileProperty("textures", base64))
        meta.playerProfile = profile
    }

    fun createDiceItem(): ItemStack {
        val diceItem = ItemStack(Material.PLAYER_HEAD)
        val meta = diceItem.itemMeta as? SkullMeta
        if (meta != null) {
            meta.setDisplayName(colored("&6&l抽選Dice &7(Kotlin版+)"))
            plugin.config.getString("kotlindice.skin-texture")?.let { setSkullTexture(meta, it) }
            diceItem.itemMeta = meta
        }
        return ItemUtil.setStringTag(diceItem, TAG_KEY, "1")
    }

    private fun drawLottery(caster: Player): Player? {
        val targets = caster.getNearbyEntities(50.0, 50.0, 50.0)
            .filterIsInstance<Player>()
            .filter { !wonPlayers.contains(it.uniqueId) }
        if (targets.isEmpty()) return null
        return targets[Random.nextInt(targets.size)]
    }

    private fun broadcastToRange(caster: Player, message: String) {
        val coloredMessage = colored(message)
        caster.sendMessage(coloredMessage)
        caster.getNearbyEntities(50.0, 50.0, 50.0)
            .filterIsInstance<Player>()
            .forEach { it.sendMessage(coloredMessage) }
    }

    private fun executeInstantDice(caster: Player) {
        val winner = drawLottery(caster)
        if (winner == null) {
            caster.sendMessage(colored("&d[KotlinDice] &c範囲内に抽選対象のプレイヤーがいません"))
            return
        }
        wonPlayers.add(winner.uniqueId)
        broadcastToRange(caster, "&f&l${caster.name}&6&lの抽選ダイスの結果は&f&l${winner.name}&6&lさんになりました！")
    }

    private fun executeCountdownDice(caster: Player) {
        val winner = drawLottery(caster)
        if (winner == null) {
            caster.sendMessage(colored("&d[KotlinDice] &c範囲内に抽選対象のプレイヤーがいません"))
            return
        }
        wonPlayers.add(winner.uniqueId)

        object : BukkitRunnable() {
            var step = 0
            override fun run() {
                when (step) {
                    0 -> broadcastToRange(caster, "&f&l【&6&l抽選&f&l】&6&l${caster.name}&f&lの&a&l抽&b&l選&c&lま&a&lて&b&lえ&c&lえ&a&l.&b&l.&c&l.&a&l.&b&l!&c&l!&a&l!&b&l!&c&l!&a&l!&b&l!")
                    1 -> broadcastToRange(caster, "&f&l【&6&l抽選&f&l】&a&l3!!!")
                    2 -> broadcastToRange(caster, "&f&l【&6&l抽選&f&l】&b&l2!!!")
                    3 -> broadcastToRange(caster, "&f&l【&6&l抽選&f&l】&c&l1!!!")
                    4 -> {
                        broadcastToRange(caster, "&f&l${caster.name}&6&lの抽選ダイスの結果は&f&l${winner.name}&6&lさんになりました！")
                        cancel()
                        return
                    }
                }
                step++
            }
        }.runTaskTimer(plugin, 0L, 30L)
    }

    companion object {
        const val TAG_KEY = "KotlinDice"

        private fun colored(message: String): String =
            ChatColor.translateAlternateColorCodes('&', message)
    }
}
