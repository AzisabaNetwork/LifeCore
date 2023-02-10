package com.github.mori01231.lifecore;

import com.github.mori01231.lifecore.command.DebtCommand;
import com.github.mori01231.lifecore.command.DebugVoteCommand;
import com.github.mori01231.lifecore.command.DropNotifyCommand;
import com.github.mori01231.lifecore.command.DropProtectCommand;
import com.github.mori01231.lifecore.command.EventCommand;
import com.github.mori01231.lifecore.command.GuideCommand;
import com.github.mori01231.lifecore.command.HelpCommand;
import com.github.mori01231.lifecore.command.KiaiCommand;
import com.github.mori01231.lifecore.command.KillNonAdminCommand;
import com.github.mori01231.lifecore.command.LifeCommand;
import com.github.mori01231.lifecore.command.MMIDCommand;
import com.github.mori01231.lifecore.command.NgWordCommand;
import com.github.mori01231.lifecore.command.NoobCommand;
import com.github.mori01231.lifecore.command.PetClickCommand;
import com.github.mori01231.lifecore.command.Pve0Command;
import com.github.mori01231.lifecore.command.Pve1Command;
import com.github.mori01231.lifecore.command.Pve2Command;
import com.github.mori01231.lifecore.command.Pve3Command;
import com.github.mori01231.lifecore.command.PveCommand;
import com.github.mori01231.lifecore.command.RankCommand;
import com.github.mori01231.lifecore.command.ResourceCommand;
import com.github.mori01231.lifecore.command.TrashCommand;
import com.github.mori01231.lifecore.command.TravelCommand;
import com.github.mori01231.lifecore.command.TutorialCommand;
import com.github.mori01231.lifecore.command.VoteCommand;
import com.github.mori01231.lifecore.command.WebsiteCommand;
import com.github.mori01231.lifecore.command.WikiCommand;
import com.github.mori01231.lifecore.config.DropNotifyFile;
import com.github.mori01231.lifecore.config.DropProtectFile;
import com.github.mori01231.lifecore.config.PetClickFile;
import com.github.mori01231.lifecore.config.VotesFile;
import com.github.mori01231.lifecore.listener.CancelJoinAfterStartupListener;
import com.github.mori01231.lifecore.listener.CancelPetClickListener;
import com.github.mori01231.lifecore.listener.CreatureSpawnEventListener;
import com.github.mori01231.lifecore.listener.DeathLoopListener;
import com.github.mori01231.lifecore.listener.DestroyExperienceOrbListener;
import com.github.mori01231.lifecore.listener.DropProtectListener;
import com.github.mori01231.lifecore.listener.FilterNgWordsListener;
import com.github.mori01231.lifecore.listener.DropNotifyListener;
import com.github.mori01231.lifecore.listener.item.GlassHammerItemListener;
import com.github.mori01231.lifecore.listener.item.OreOnlyItemListener;
import com.github.mori01231.lifecore.listener.PlayerJoinListener;
import com.github.mori01231.lifecore.listener.TownyOutlawListener;
import com.github.mori01231.lifecore.listener.TrashListener;
import com.github.mori01231.lifecore.listener.UseAdminSwordListener;
import com.github.mori01231.lifecore.listener.VoteListener;
import com.github.mori01231.lifecore.network.PacketHandler;
import com.github.mori01231.lifecore.util.GCListener;
import com.github.mori01231.lifecore.util.NGWordsCache;
import com.github.mori01231.lifecore.util.PlayerUtil;
import com.sun.net.httpserver.HttpServer;
import io.netty.channel.ChannelPipeline;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LifeCore extends JavaPlugin {
    private static LifeCore instance;
    private final GCListener gcListener = new GCListener();
    private final NGWordsCache ngWordsCache = new NGWordsCache();
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    public final Executor asyncExecutor = r -> Bukkit.getScheduler().runTaskAsynchronously(this, r);
    private DatabaseConfig databaseConfig;
    private HttpServer httpServer;

    public LifeCore(){
        instance = this;
    }

    public static LifeCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        if (getConfig().getBoolean("enable-gc-detector", false)) {
            gcListener.register();
        }

        VotesFile.load(this);
        PetClickFile.load(this);
        DropProtectFile.load(this);
        DropNotifyFile.load(this);

        databaseConfig = new DatabaseConfig(Objects.requireNonNull(getConfig().getConfigurationSection("database"), "database section is missing"));

        if (getConfig().getBoolean("http-server.enabled", false)) {
            String token = getConfig().getString("http-server.token", "this-should-be-a-random-string");
            if (token == null || "this-should-be-a-random-string".equals(token)) {
                getLogger().warning("Token is not set. Please set a random string to http-server.token in config.yml");
            }
            startHttpServer(getConfig().getInt("http-server.port", 8080));
        }

        getLogger().info("LifeCore has been enabled.");

        registerCommand("wiki", new WikiCommand());
        registerCommand("website", new WebsiteCommand());
        registerCommand("help", new HelpCommand());
        registerCommand("guide", new GuideCommand());
        registerCommand("tutorial", new TutorialCommand());
        registerCommand("travel", new TravelCommand());
        registerCommand("pve", new PveCommand());
        registerCommand("pve0", new Pve0Command());
        registerCommand("pve1", new Pve1Command());
        registerCommand("pve2", new Pve2Command());
        registerCommand("pve3", new Pve3Command());
        registerCommand("life", new LifeCommand());
        registerCommand("resource", new ResourceCommand());
        registerCommand("event", new EventCommand());
        registerCommand("rank", new RankCommand());
        registerCommand("trash", new TrashCommand());
        registerCommand("kiai", new KiaiCommand());
        registerCommand("noob", new NoobCommand());
        registerCommand("killnonadmin", new KillNonAdminCommand());
        registerCommand("mmid", new MMIDCommand());
        registerCommand("debt", new DebtCommand());
        registerCommand("vote", new VoteCommand());
        registerCommand("debugvote", new DebugVoteCommand());
        registerCommand("petclick", new PetClickCommand());
        registerCommand("dropprotect", new DropProtectCommand(this));
        registerCommand("ngword", new NgWordCommand(this));
        registerCommand("dropnotify", new DropNotifyCommand());

        this.saveDefaultConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        registerEvents();

        try {
            DBConnector.init();
        } catch (Exception e) {
            getLogger().severe("Failed to connect to database.");
            e.printStackTrace();
        }

        // register channel handler
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                ngWordsCache.loadAsync(player.getUniqueId());
                PlayerUtil.getChannel(player).pipeline()
                        .addBefore("packet_handler", "lifecore", new PacketHandler(player));
            } catch (Exception e) {
                getSLF4JLogger().warn("Failed to inject channel handler to player " + player.getName(), e);
            }
        }

        // check for negative max health
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                DeathLoopListener.checkAttribute(player);
            }
        }, 100, 100);
    }

    private void startHttpServer(int port) {
        try {
            httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.createContext("/accept-rules", new RequestHandler(this));
        httpServer.setExecutor(executorService);
        httpServer.start();
    }
    
    private void registerCommand(@NotNull String name, @NotNull CommandExecutor executor) {
        Objects.requireNonNull(getCommand(name), name + " is not registered in plugin.yml").setExecutor(executor);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        VotesFile.save(this);
        PetClickFile.save(this);
        DropProtectFile.save(this);
        DropNotifyFile.save(this);
        DBConnector.close();
        executorService.shutdownNow();
        httpServer.stop(1);
        gcListener.unregister();

        // unregister all channel handlers
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChannelPipeline pipeline = PlayerUtil.getChannel(player).pipeline();
            try {
                if (pipeline.get("lifecore") != null) {
                    pipeline.remove("lifecore");
                }
            } catch (NoSuchElementException ignored) {}
        }

        getLogger().info("LifeCore has been disabled.");
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new CreatureSpawnEventListener(), this);
        pm.registerEvents(new TrashListener(), this);
        pm.registerEvents(new UseAdminSwordListener(), this);
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new CancelJoinAfterStartupListener(), this);
        pm.registerEvents(new DeathLoopListener(), this);
        pm.registerEvents(new DropProtectListener(), this);
        pm.registerEvents(new DropNotifyListener(), this);

        // Items
        pm.registerEvents(new OreOnlyItemListener(), this);
        pm.registerEvents(new GlassHammerItemListener(), this);

        if (getConfig().getBoolean("destroy-experience-orb-on-chunk-load", false)) {
            pm.registerEvents(new DestroyExperienceOrbListener(), this);
        }

        try {
            Class.forName("de.Keyle.MyPet.MyPetApi");
            pm.registerEvents(new CancelPetClickListener(), this);
        } catch (Exception | NoClassDefFoundError e) {
            getSLF4JLogger().warn("MyPet not detected, skipping event listener registration");
        }

        try {
            Class.forName("com.vexsoftware.votifier.model.VotifierEvent");
            pm.registerEvents(new VoteListener(), this);
        } catch (Exception | NoClassDefFoundError e) {
            getSLF4JLogger().warn("Votifier not detected, skipping event listener registration");
        }

        try {
            Class.forName("com.palmergames.bukkit.towny.TownyAPI");
            pm.registerEvents(new TownyOutlawListener(), this);
        } catch (Exception | NoClassDefFoundError e) {
            getSLF4JLogger().warn("Towny not detected, skipping event listener registration");
        }

        try {
            Class.forName("net.azisaba.ryuzupluginchat.event.AsyncGlobalMessageEvent");
            pm.registerEvents(new FilterNgWordsListener(this), this);
        } catch (Exception | NoClassDefFoundError e) {
            getSLF4JLogger().warn("RyuZUPluginChat not detected, skipping event listener registration");
        }
    }

    @NotNull
    public DatabaseConfig getDatabaseConfig() {
        return Objects.requireNonNull(databaseConfig, "databaseConfig is null");
    }

    @NotNull
    public GCListener getGcListener() {
        return gcListener;
    }

    public @NotNull NGWordsCache getNgWordsCache() {
        return ngWordsCache;
    }
}
