package com.github.mori01231.lifecore.firstjoinplus

import org.bukkit.Effect
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class FirstJoinListener(private val integration: FirstJoinPlusIntegration) : Listener {

    private val plugin = integration.plugin

    @EventHandler
    fun onFirstJoin(event: FirstJoinEvent) {
        val player = event.player
        val config = integration.manager.config

        if (config.getBoolean("on-first-join.first-join-message.enabled")) {
            event.setFirstJoinMessage(FirstJoinPlusUtilities.replaceVariables(config.getString("on-first-join.first-join-message.message") ?: "", player))
        }

        val delay = config.getInt("on-first-join.delay-everything-below-by").toLong()
        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            if (config.getBoolean("on-first-join.teleport.enabled")) {
                player.teleport(event.firstJoinLocation)
            }

            if (config.getBoolean("on-first-join.first-join-kit.enabled")) {
                for (i in FirstJoinPlusUtilities.getFirstJoinKit()) {
                    player.inventory.addItem(i)
                }
            }

            if (config.getBoolean("on-first-join.give-written-books.enabled")) {
                for (i in FirstJoinPlusUtilities.getWrittenBooks(player)) {
                    player.inventory.addItem(i)
                }
            }

            if (config.getBoolean("on-first-join.give-experience.enabled")) {
                player.level = config.getInt("on-first-join.give-experience.level-amount")
            }

            if (config.getBoolean("on-first-join.send-messages.enabled")) {
                for (message in config.getStringList("on-first-join.send-messages.messages")) {
                    player.sendMessage(FirstJoinPlusUtilities.replaceVariables(message, player))
                }
            }

            if (config.getBoolean("on-first-join.fun-stuff.play-sound.enabled")) {
                for (p in plugin.server.onlinePlayers) {
                    if (p.hasPermission(config.getString("on-first-join.fun-stuff.play-sound.listen-permission") ?: "")) {
                        val soundName = config.getString("on-first-join.fun-stuff.play-sound.sound-name")
                        if (soundName != null) {
                            p.playSound(p.location, FirstJoinPlusUtilities.normalizeSoundName(soundName), 1f, 1f)
                        }
                    }
                }
            }

            if (config.getBoolean("on-first-join.fun-stuff.smoke-effect.enabled")) {
                for (i in 0..25) {
                    event.firstJoinLocation.world?.playEffect(event.firstJoinLocation, Effect.SMOKE, i)
                }
            }

            if (config.getBoolean("on-first-join.fun-stuff.launch-firework.enabled")) {
                FirstJoinPlusUtilities.launchRandomFirework(event.firstJoinLocation)
            }

            if (config.getBoolean("on-first-join.run-commands.enabled")) {
                for (command in config.getStringList("on-first-join.run-commands.commands")) {
                    val cmnd = FirstJoinPlusUtilities.replaceVariables(command, player)
                    player.performCommand(cmnd)
                }
            }

            if (config.getBoolean("on-first-join.run-console-commands.enabled")) {
                for (command in config.getStringList("on-first-join.run-console-commands.commands")) {
                    val cmnd = FirstJoinPlusUtilities.replaceVariables(command, player)
                    plugin.server.dispatchCommand(plugin.server.consoleSender, cmnd)
                }
            }

            if (config.getBoolean("on-first-join.apply-potion-effects.enabled")) {
                val effects = ArrayList<PotionEffect>()
                for (s in config.getStringList("on-first-join.apply-potion-effects.effects")) {
                    val effect = s.split(":".toRegex()).toTypedArray()
                    val type = PotionEffectType.getByName(effect[0].uppercase()) ?: continue
                    effects.add(PotionEffect(type, effect[2].toInt() * 20, effect[1].toInt() - 1))
                }
                player.addPotionEffects(effects)
            }

            if (config.getBoolean("on-first-join.modify-damage.disable-pvp.enabled")) {
                val expire = config.getInt("on-first-join.modify-damage.disable-pvp.expire-after")
                integration.manager.noPVP.add(player.name)
                plugin.server.scheduler.runTaskLater(plugin, Runnable {
                    integration.manager.noPVP.remove(player.name)
                }, expire * 20L)
            }

            if (config.getBoolean("on-first-join.modify-damage.god-mode.enabled")) {
                val expire = config.getInt("on-first-join.modify-damage.god-mode.expire-after")
                integration.manager.godMode.add(player.name)
                plugin.server.scheduler.runTaskLater(plugin, Runnable {
                    integration.manager.godMode.remove(player.name)
                }, expire * 20L)
            }
        }, delay)
    }
}
