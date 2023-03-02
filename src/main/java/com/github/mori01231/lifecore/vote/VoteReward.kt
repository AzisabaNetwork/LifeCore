package com.github.mori01231.lifecore.vote

import com.charleskorn.kaml.YamlComment
import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import io.lumine.xikage.mythicmobs.MythicMobs
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Serializable
sealed interface VoteReward {
    fun execute(plugin: LifeCore, player: Player)
}

@SerialName("command")
@Serializable
data class CommandReward(
    @YamlComment(
        "先頭のスラッシュを除くコンソールから実行されるコマンド",
        "<player>はプレイヤー名に置き換えられます。"
    )
    val command: String,
) : VoteReward {
    override fun execute(plugin: LifeCore, player: Player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.name))
    }
}

@SerialName("mmid")
@Serializable
data class MythicItemReward(
    @YamlComment("MythicMobsのアイテムID")
    val id: String,
    @YamlComment("アイテムの数")
    val amount: Int = 1,
) : VoteReward {
    override fun execute(plugin: LifeCore, player: Player) {
        val item = MythicMobs.inst()
            .itemManager
            .getItemStack(id)
            ?.clone()
            ?.apply { this.amount = this@MythicItemReward.amount }
        if (item == null) {
            plugin.logger.warning("Mythic Item $id not found.")
        } else {
            player.inventory.addItem(item).values.apply {
                if (isNotEmpty() && all { ItemUtil.addToStashIfEnabled(player.uniqueId, it) }) {
                    player.sendMessage("${ChatColor.RED}インベントリがいっぱいのため、Stashに入りました。")
                    player.sendMessage("${ChatColor.AQUA}/pickupstash${ChatColor.RED}で回収できます。")
                }
            }
        }
    }
}

@SerialName("item")
@Serializable
data class ItemReward(
    @YamlComment("アイテムのID")
    val id: Material,
    @YamlComment("アイテムの数")
    val amount: Int = 1,
) : VoteReward {
    override fun execute(plugin: LifeCore, player: Player) {
        player.inventory.addItem(ItemStack(id, amount)).values.apply {
            if (isNotEmpty() && all { ItemUtil.addToStashIfEnabled(player.uniqueId, it) }) {
                player.sendMessage("${ChatColor.RED}インベントリがいっぱいのため、Stashに入りました。")
                player.sendMessage("${ChatColor.AQUA}/pickupstash${ChatColor.RED}で回収できます。")
            }
        }
    }
}

@SerialName("eco")
@Serializable
data class EconomyReward(
    @YamlComment("与える金額")
    val amount: Double,
) : VoteReward {
    constructor(amount: Int) : this(amount.toDouble())

    override fun execute(plugin: LifeCore, player: Player) {
        try {
            val eco = Bukkit.getServicesManager().getRegistration(Economy::class.java)?.provider
            if (eco == null) {
                plugin.logger.warning("Cannot give $$amount to player ${player.name} because no economy plugin is installed.")
                return
            }
            eco.depositPlayer(player, amount)
        } catch (e: NoClassDefFoundError) {
            plugin.logger.warning("Cannot give $$amount to player ${player.name} because no economy plugin is installed.")
        }
    }
}
