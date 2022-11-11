package com.github.mori01231.lifecore;

import com.github.mori01231.lifecore.command.DebtCommand;
import com.github.mori01231.lifecore.command.DebugVoteCommand;
import com.github.mori01231.lifecore.command.EventCommand;
import com.github.mori01231.lifecore.command.GuideCommand;
import com.github.mori01231.lifecore.command.HelpCommand;
import com.github.mori01231.lifecore.command.KiaiCommand;
import com.github.mori01231.lifecore.command.KillNonAdminCommand;
import com.github.mori01231.lifecore.command.LifeCommand;
import com.github.mori01231.lifecore.command.MMIDCommand;
import com.github.mori01231.lifecore.command.NoobCommand;
import com.github.mori01231.lifecore.command.PackCommand;
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
import com.github.mori01231.lifecore.config.PetClickFile;
import com.github.mori01231.lifecore.config.VotesFile;
import com.github.mori01231.lifecore.listener.CancelJoinAfterStartupListener;
import com.github.mori01231.lifecore.listener.CancelPetClickListener;
import com.github.mori01231.lifecore.listener.CreatureSpawnEventListener;
import com.github.mori01231.lifecore.listener.DestroyExperienceOrbListener;
import com.github.mori01231.lifecore.listener.NoItemFrameObstructionListener;
import com.github.mori01231.lifecore.listener.PlayerJoinListener;
import com.github.mori01231.lifecore.listener.TrashListener;
import com.github.mori01231.lifecore.listener.UseAdminSwordListener;
import com.github.mori01231.lifecore.listener.VoteListener;
import com.github.mori01231.lifecore.network.PacketHandler;
import com.github.mori01231.lifecore.util.GCListener;
import com.github.mori01231.lifecore.util.PlayerUtil;
import io.netty.channel.ChannelPipeline;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Executor;

public final class LifeCore extends JavaPlugin {

    private static LifeCore instance;
    private final GCListener gcListener = new GCListener();
    public final Executor asyncExecutor = r -> Bukkit.getScheduler().runTaskAsynchronously(this, r);
    private DatabaseConfig databaseConfig;

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

        databaseConfig = new DatabaseConfig(Objects.requireNonNull(getConfig().getConfigurationSection("database"), "database section is missing"));

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
        registerCommand("pack", new PackCommand());
        registerCommand("kiai", new KiaiCommand());
        registerCommand("noob", new NoobCommand());
        registerCommand("killnonadmin", new KillNonAdminCommand());
        registerCommand("mmid", new MMIDCommand());
        registerCommand("debt", new DebtCommand());
        registerCommand("vote", new VoteCommand());
        registerCommand("debugvote", new DebugVoteCommand());
        registerCommand("petclick", new PetClickCommand());

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
                PlayerUtil.getChannel(player).pipeline()
                        .addBefore("packet_handler", "lifecore", new PacketHandler(player));
            } catch (Exception e) {
                getSLF4JLogger().warn("Failed to inject channel handler to player " + player.getName(), e);
            }
        }
    }
    
    private void registerCommand(@NotNull String name, @NotNull CommandExecutor executor) {
        Objects.requireNonNull(getCommand(name), name + " is not registered in plugin.yml").setExecutor(executor);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        VotesFile.save(this);
        PetClickFile.save(this);
        DBConnector.close();
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
        pm.registerEvents(new NoItemFrameObstructionListener(), this);
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new CancelJoinAfterStartupListener(), this);
        pm.registerEvents(new CancelPetClickListener(), this);

        if (getConfig().getBoolean("destroy-experience-orb-on-chunk-load", false)) {
            pm.registerEvents(new DestroyExperienceOrbListener(), this);
        }

        try {
            Class.forName("com.vexsoftware.votifier.model.VotifierEvent");
            pm.registerEvents(new VoteListener(), this);
        } catch (Exception | NoClassDefFoundError e) {
            getSLF4JLogger().warn("Votifier not detected, not registering VoteListener.", e);
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
}
