package com.github.mori01231.lifecore.resource

import com.github.mori01231.lifecore.LifeCore
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.random.Random

/**
 * 旧 LifeResource プラグインの資源ボックス処理。
 * コマンドは引き続き `mlg` / `give` 経由で実行する。
 */
object ResourceCommandUtil {
    private val logger get() = LifeCore.instance.logger

    @JvmStatic
    fun sendWeaponCommand(player: Player, item: String, num: Int) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mlg ${player.name} $item$num 1 1")
        logger.info("[ResourceDrop] ${player.name}に$item${num}を1個付与しました")
    }

    @JvmStatic
    fun sendMlgCommand(player: Player, item: String, amount: Int) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mlg ${player.name} $item $amount 1")
        logger.info("[ResourceDrop] ${player.name}に${item}を${amount}個付与しました")
    }

    @JvmStatic
    fun sendGiveCommand(player: Player, material: Material, amount: Int) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give ${player.name} $material $amount")
        logger.info("[ResourceDrop] ${player.name}に${material.name}を${amount}個付与しました")
    }
}

object XRandomBox {
    fun get(player: Player) {
        when (Random.nextInt(17) + 1) {
            in 1..4 -> {
                ResourceCommandUtil.sendMlgCommand(player, "kenmazai1", 30)
                ResourceCommandUtil.sendGiveCommand(player, Material.ENDER_PEARL, 32)
            }
            in 5..8 -> ResourceCommandUtil.sendMlgCommand(player, "endevidence", 1)
            in 9..12 -> {
                ResourceCommandUtil.sendGiveCommand(player, Material.DIAMOND, 30)
                ResourceCommandUtil.sendGiveCommand(player, Material.ENDER_PEARL, 32)
            }
            in 13..16 -> {
                ResourceCommandUtil.sendMlgCommand(player, "unknownbox", 1)
                ResourceCommandUtil.sendGiveCommand(player, Material.ENDER_PEARL, 32)
            }
            else -> ResourceCommandUtil.sendGiveCommand(player, Material.SHULKER_SHELL, 1)
        }
    }

    fun get16(player: Player) {
        when (Random.nextInt(17) + 1) {
            in 1..4 -> {
                ResourceCommandUtil.sendMlgCommand(player, "kenmazai1_15setTicket", 2)
                ResourceCommandUtil.sendMlgCommand(player, "enderpearl_16setTicket", 2)
            }
            in 5..8 -> ResourceCommandUtil.sendMlgCommand(player, "endevidence", 1)
            in 9..12 -> {
                ResourceCommandUtil.sendMlgCommand(player, "diamond_30setTicket", 1)
                ResourceCommandUtil.sendMlgCommand(player, "enderpearl_16setTicket", 2)
            }
            in 13..16 -> {
                ResourceCommandUtil.sendMlgCommand(player, "unknownbox", 1)
                ResourceCommandUtil.sendMlgCommand(player, "enderpearl_16setTicket", 2)
            }
            else -> ResourceCommandUtil.sendGiveCommand(player, Material.SHULKER_SHELL, 1)
        }
    }
}

object YRandomBox {
    fun get(player: Player) {
        when (Random.nextInt(17) + 1) {
            in 1..4 -> ResourceCommandUtil.sendMlgCommand(player, "netherkaihou", 1)
            in 5..8 -> ResourceCommandUtil.sendGiveCommand(player, Material.IRON_INGOT, 60)
            in 9..12 -> {
                ResourceCommandUtil.sendMlgCommand(player, "sigenticket", 15)
                ResourceCommandUtil.sendMlgCommand(player, "kenmazai1", 15)
            }
            in 13..16 -> ResourceCommandUtil.sendMlgCommand(player, "Yunknownbox", 1)
            else -> ResourceCommandUtil.sendGiveCommand(player, Material.SHULKER_SHELL, 1)
        }
    }

    fun get16(player: Player) {
        when (Random.nextInt(17) + 1) {
            in 1..4 -> ResourceCommandUtil.sendMlgCommand(player, "netherkaihou", 1)
            in 5..8 -> ResourceCommandUtil.sendMlgCommand(player, "iron_60setTicket", 1)
            in 9..12 -> {
                ResourceCommandUtil.sendMlgCommand(player, "sigenticket", 15)
                ResourceCommandUtil.sendMlgCommand(player, "kenmazai1_15setTicket", 1)
            }
            in 13..16 -> ResourceCommandUtil.sendMlgCommand(player, "Yunknownbox", 1)
            else -> ResourceCommandUtil.sendGiveCommand(player, Material.SHULKER_SHELL, 1)
        }
    }
}

object UnknownOpenBox {
    fun get(player: Player) {
        when (Random.nextInt(54) + 1) {
            in 1..11 -> ResourceCommandUtil.sendMlgCommand(player, "diarod", 1)
            in 12..22 -> ResourceCommandUtil.sendMlgCommand(player, "emrod", 1)
            in 23..33 -> ResourceCommandUtil.sendMlgCommand(player, "goldrod", 1)
            in 34..44 -> ResourceCommandUtil.sendMlgCommand(player, "ironrod", 1)
            in 45..53 -> ResourceCommandUtil.sendMlgCommand(player, "nether_star", 1)
            else -> ResourceCommandUtil.sendMlgCommand(player, "weaponbox", 1)
        }
    }

    fun get16(player: Player) {
        when (Random.nextInt(54) + 1) {
            in 1..11 -> ResourceCommandUtil.sendMlgCommand(player, "diarodticket", 1)
            in 12..22 -> ResourceCommandUtil.sendMlgCommand(player, "emerodticket", 1)
            in 23..33 -> ResourceCommandUtil.sendMlgCommand(player, "goldrodticket", 1)
            in 34..44 -> ResourceCommandUtil.sendMlgCommand(player, "ironrodticket", 1)
            in 45..53 -> ResourceCommandUtil.sendMlgCommand(player, "nether_star", 1)
            else -> ResourceCommandUtil.sendMlgCommand(player, "weaponbox", 1)
        }
    }
}

object YUnknownOpenBox {
    fun get(player: Player) {
        when (Random.nextInt(49) + 1) {
            in 1..11 -> ResourceCommandUtil.sendMlgCommand(player, "sigencustomticket", 1)
            in 12..22 -> ResourceCommandUtil.sendMlgCommand(player, "sigencustomticketB", 1)
            in 23..33 -> ResourceCommandUtil.sendMlgCommand(player, "sigencustomticketC", 1)
            in 34..44 -> ResourceCommandUtil.sendMlgCommand(player, "sigencustomticketD", 1)
            in 45..48 -> ResourceCommandUtil.sendGiveCommand(player, Material.NETHER_STAR, 1)
            else -> ResourceCommandUtil.sendMlgCommand(player, "weaponboxaxe", 1)
        }
    }
}

object AfterWeaponBox {
    private val weapons = listOf("akuaria", "hebunria", "enpire", "sion", "ryuunokiba", "ryuunotugai")

    fun get(player: Player) {
        ResourceCommandUtil.sendWeaponCommand(player, weapons[Random.nextInt(weapons.size)], Random.nextInt(40) + 1)
    }
}

object AfterWeaponBoxAxe {
    private val weapons = listOf("seira", "agito", "rairai", "tamahagane", "ganzanzan", "danzetuha")

    fun get(player: Player) {
        ResourceCommandUtil.sendWeaponCommand(player, weapons[Random.nextInt(weapons.size)], Random.nextInt(40) + 1)
    }
}
