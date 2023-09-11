package com.github.mori01231.lifecore.listener

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import xyz.acrylicstyle.util.expression.RuntimeData
import xyz.acrylicstyle.util.expression.instruction.InstInvokeVirtual
import xyz.acrylicstyle.util.expression.instruction.InstLoadVariable
import xyz.acrylicstyle.util.expression.instruction.InstStoreString
import xyz.acrylicstyle.util.expression.instruction.InstructionSet

object FixMythicItemListener : Listener {
    private val getPlayerProfileInst = InstructionSet().apply {
        add(InstStoreString("plugin"))
        add(InstLoadVariable())
        add(InstInvokeVirtual("io.lumine.mythiccrucible.MythicCrucible", "getProfileManager", "()Lio/lumine/mythiccrucible/profiles/ProfileManager;"))
        add(InstStoreString("player"))
        add(InstLoadVariable())
        add(InstInvokeVirtual("io.lumine.mythiccrucible.profiles.ProfileManager", "getPlayerProfile", "(Lorg/bukkit/entity/Player;)Lio/lumine/mythiccrucible/profiles/Profile;"))
    }
    private val parseWeapon = InstructionSet().apply {
        addAll(getPlayerProfileInst)
        add(InstInvokeVirtual("io.lumine.mythiccrucible.profiles.Profile", "parseWeapons", "()V"))
    }
    private val parseArmor = InstructionSet().apply {
        addAll(getPlayerProfileInst)
        add(InstInvokeVirtual("io.lumine.mythiccrucible.profiles.Profile", "parseArmor", "()V"))
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        if (e.click != ClickType.NUMBER_KEY) {
            return
        }
        scheduleFix(e.whoClicked as Player)
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        scheduleFix(e.player)
    }

    private fun scheduleFix(player: Player) {
        val plugin = Bukkit.getPluginManager().getPlugin("MythicCrucible") ?: return
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val runtimeData = RuntimeData.builder()
                .allowPrivate(false)
                .addVariable("player", player)
                .addVariable("plugin", plugin)
                .build()
            parseWeapon.execute(runtimeData)
            parseArmor.execute(runtimeData)
            player.updateInventory()
        })
    }
}
