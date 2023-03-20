package com.github.mori01231.lifecore

import com.github.mori01231.lifecore.command.*
import com.github.mori01231.lifecore.config.*
import com.github.mori01231.lifecore.gui.DropProtectScreen
import com.github.mori01231.lifecore.listener.*
import com.github.mori01231.lifecore.listener.item.GlassHammerItemListener
import com.github.mori01231.lifecore.listener.item.OreOnlyItemListener
import com.github.mori01231.lifecore.network.PacketHandler
import com.github.mori01231.lifecore.util.GCListener
import com.github.mori01231.lifecore.util.NGWordsCache
import com.github.mori01231.lifecore.util.PlayerUtil
import com.github.mori01231.lifecore.util.YAML.yaml
import com.github.mori01231.lifecore.util.decode
import com.github.mori01231.lifecore.util.encode
import com.github.mori01231.lifecore.util.getYamlMap
import com.github.mori01231.lifecore.util.nonNull
import com.github.mori01231.lifecore.util.runTaskTimerAsynchronously
import com.github.mori01231.lifecore.util.toReadonlyNode
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.net.InetSocketAddress
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class LifeCore : JavaPlugin() {
    private val gcListener = GCListener(this)
    @JvmField
    val ngWordsCache = NGWordsCache()
    private val executorService = Executors.newFixedThreadPool(2)
    @JvmField
    val asyncExecutor = Executor { Bukkit.getScheduler().runTaskAsynchronously(this, it) }
    private var databaseConfig: DatabaseConfig? = null
    lateinit var voteConfig: VoteConfig
        private set
    private var httpServer: HttpServer? = null
    lateinit var dropProtectConfig: DropProtectConfig
        private set

    init {
        instance = this
    }

    override fun onEnable() {
        // Plugin startup logic
        if (config.getBoolean("enable-gc-detector", false)) {
            gcListener.register()
        }
        VotesFile.load(this)
        PetClickFile.load(this)
        DamageLogFile.load(this)
        DropNotifyFile.load(this)
        dataFolder.resolve("drop-protect.yml").let {
            if (!it.exists()) {
                it.writeText("players: {}")
            }
            dropProtectConfig = yaml.decodeFromString(it.readText())
        }

        // save every 5 minutes
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, 20 * 60 * 5, 20 * 60 * 5) {
            dataFolder.resolve("drop-protect.yml").writeText(dropProtectConfig.encode())
        }

        databaseConfig = config.getYamlMap("database").decode(DatabaseConfig.serializer())
        dataFolder.resolve("config-vote.yml").let {
            if (!it.exists()) {
                it.writeText(yaml.encodeToString(VoteConfig()))
            }
            voteConfig = yaml.decodeFromString(it.readText())
        }

        val httpServerConfig =
            config.getConfigurationSection("http-server")
                ?.toReadonlyNode()
                ?.decode(HttpServerConfig.serializer())
        if (httpServerConfig != null && httpServerConfig.enabled) {
            if (httpServerConfig.token == HttpServerConfig.DEFAULT_TOKEN) {
                logger.warning("Token is not set. Please set a random string to http-server.token in config.yml")
            }
            startHttpServer(httpServerConfig.port)
        }

        registerCommand("wiki", WikiCommand(this))
        registerCommand("website", WebsiteCommand(this))
        registerCommand("help", HelpCommand(this))
        registerCommand("guide", GuideCommand(this))
        registerCommand("tutorial", TutorialCommand(this))
        registerCommand("travel", TransferCommand(this, "lifetravel"))
        registerCommand("pve", PveCommand(this))
        registerCommand("pve0", TransferCommand(this, "lifepve"))
        registerCommand("pve1", TransferCommand(this, "lifepve1"))
        registerCommand("pve2", TransferCommand(this, "lifepve2"))
        registerCommand("pve3", TransferCommand(this, "lifepve3"))
        registerCommand("life", TransferCommand(this, "life"))
        registerCommand("resource", TransferCommand(this, "liferesource"))
        registerCommand("event", TransferCommand(this, "lifeevent"))
        registerCommand("rank", RankCommand(this))
        registerCommand("trash", TrashCommand())
        registerCommand("kiai", KiaiCommand())
        registerCommand("noob", NoobCommand())
        registerCommand("killnonadmin", KillNonAdminCommand())
        registerCommand("mmid", MMIDCommand())
        registerCommand("debt", DebtCommand(this))
        registerCommand("vote", VoteCommand(this))
        registerCommand("debugvote", DebugVoteCommand(this))
        registerCommand("petclick", PetClickCommand())
        registerCommand("ngword", NgWordCommand(this))
        registerCommand("dropnotify", DropNotifyCommand())
        registerCommand("dropprotect", DropProtectCommand(this))
        registerCommand("damagelog", DamageLogCommand())
        registerCommand("respawn") { _, _, _, args ->
            args.getOrNull(0)?.let { Bukkit.getPlayerExact(it)?.spigot()?.respawn() }
            true
        }
        saveDefaultConfig()
        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        registerEvents()
        try {
            DBConnector.init(this)
        } catch (e: Exception) {
            logger.severe("Failed to connect to database.")
            e.printStackTrace()
        }

        // register channel handler
        for (player in Bukkit.getOnlinePlayers()) {
            try {
                ngWordsCache.loadAsync(player.uniqueId)
                PlayerUtil.getChannel(player).pipeline()
                    .addBefore("packet_handler", "lifecore", PacketHandler(player))
            } catch (e: Exception) {
                slF4JLogger.warn("Failed to inject channel handler to player " + player.name, e)
            }
        }

        // check for negative max health
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                DeathLoopListener.checkAttribute(this, player)
            }
        }, 100, 100)

        logger.info("LifeCore has been enabled.")
    }

    private fun startHttpServer(port: Int) {
        httpServer = try {
            HttpServer.create(InetSocketAddress("0.0.0.0", port), 0).apply {
                createContext("/accept-rules", RequestHandler(this@LifeCore))
                executor = executorService
                start()
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun registerCommand(name: String, executor: CommandExecutor) {
        getCommand(name).nonNull("$name is not registered in plugin.yml").setExecutor(executor)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        runCatching { VotesFile.save(this) }
        runCatching { PetClickFile.save(this) }
        runCatching { DamageLogFile.save(this) }
        runCatching { DropNotifyFile.save(this) }
        runCatching { dataFolder.resolve("drop-protect.yml").writeText(dropProtectConfig.encode()) }
        runCatching { DBConnector.close() }
        runCatching { executorService.shutdownNow() }
        runCatching { httpServer?.stop(1) }
        runCatching { gcListener.unregister() }

        runCatching {
            // unregister all channel handlers
            for (player in Bukkit.getOnlinePlayers()) {
                val pipeline = PlayerUtil.getChannel(player).pipeline()
                try {
                    if (pipeline["lifecore"] != null) {
                        pipeline.remove("lifecore")
                    }
                } catch (ignored: NoSuchElementException) {
                }
            }
        }
        logger.info("LifeCore has been disabled.")
    }

    private fun runCatching(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerEvents() {
        val pm = server.pluginManager
        pm.registerEvents(CreatureSpawnEventListener(this), this)
        pm.registerEvents(TrashListener(this), this)
        pm.registerEvents(UseAdminSwordListener(this), this)
        pm.registerEvents(PlayerJoinListener(this), this)
        pm.registerEvents(CancelJoinAfterStartupListener(), this)
        pm.registerEvents(DeathLoopListener(), this)
        pm.registerEvents(DropNotifyListener(), this)
        pm.registerEvents(DropProtectScreen.EventListener(this), this)
        pm.registerEvents(UnableCraftListener(), this)
        pm.registerEvents(AZISAVIORListener(this), this)
        pm.registerEvents(DamageLogListener(), this)
        pm.registerEvents(UnusableDyeListener(), this)

        // Items
        pm.registerEvents(OreOnlyItemListener(), this)
        pm.registerEvents(GlassHammerItemListener(), this)
        if (config.getBoolean("destroy-experience-orb-on-chunk-load", false)) {
            pm.registerEvents(DestroyExperienceOrbListener(), this)
        }
        try {
            Class.forName("de.Keyle.MyPet.MyPetApi")
            pm.registerEvents(CancelPetClickListener(), this)
        } catch (e: ClassNotFoundException) {
            slF4JLogger.warn("MyPet not detected, skipping event listener registration")
        }
        try {
            Class.forName("com.vexsoftware.votifier.model.VotifierEvent")
            pm.registerEvents(VoteListener(this), this)
        } catch (e: ClassNotFoundException) {
            slF4JLogger.warn("Votifier not detected, skipping event listener registration")
        }
        try {
            Class.forName("com.palmergames.bukkit.towny.TownyAPI")
            pm.registerEvents(TownyOutlawListener(), this)
        } catch (e: ClassNotFoundException) {
            slF4JLogger.warn("Towny not detected, skipping event listener registration")
        }
        try {
            Class.forName("net.azisaba.ryuzupluginchat.event.AsyncGlobalMessageEvent")
            pm.registerEvents(FilterNgWordsListener(this), this)
        } catch (e: ClassNotFoundException) {
            slF4JLogger.warn("RyuZUPluginChat not detected, skipping event listener registration")
        }
        try {
            Class.forName("net.azisaba.rarity.api.RarityAPI")
            Class.forName("net.azisaba.itemstash.ItemStash")
            pm.registerEvents(DropProtectListener(this), this)
        } catch (e: ClassNotFoundException) {
            slF4JLogger.warn("Rarity and/or ItemStash not detected, skipping event listener registration")
        }
    }

    fun getDatabaseConfig(): DatabaseConfig {
        return databaseConfig.nonNull("databaseConfig is null")
    }

    companion object {
        @get:Deprecated("Using this field can cause a memory leak", ReplaceWith("getPlugin(LifeCore::class.java)"))
        @JvmStatic
        lateinit var instance: LifeCore
            private set
    }
}
