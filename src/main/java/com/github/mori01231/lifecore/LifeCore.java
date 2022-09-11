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
import com.github.mori01231.lifecore.command.Pve0Command;
import com.github.mori01231.lifecore.command.Pve1Command;
import com.github.mori01231.lifecore.command.Pve2Command;
import com.github.mori01231.lifecore.command.PveCommand;
import com.github.mori01231.lifecore.command.RankCommand;
import com.github.mori01231.lifecore.command.ResourceCommand;
import com.github.mori01231.lifecore.command.TrashCommand;
import com.github.mori01231.lifecore.command.TravelCommand;
import com.github.mori01231.lifecore.command.TutorialCommand;
import com.github.mori01231.lifecore.command.VoteCommand;
import com.github.mori01231.lifecore.command.WebsiteCommand;
import com.github.mori01231.lifecore.command.WikiCommand;
import com.github.mori01231.lifecore.listener.CreatureSpawnEventListener;
import com.github.mori01231.lifecore.listener.NoItemFrameObstructionListener;
import com.github.mori01231.lifecore.listener.SpawnOnJoinListener;
import com.github.mori01231.lifecore.listener.TrashListener;
import com.github.mori01231.lifecore.listener.UseAdminSwordListener;
import com.github.mori01231.lifecore.listener.VoteListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executor;

public final class LifeCore extends JavaPlugin {

    private static LifeCore instance;
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

        VotesFile.load(this);

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

        this.saveDefaultConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        registerEvents();

        try {
            DBConnector.init();
        } catch (Exception e) {
            getLogger().severe("Failed to connect to database.");
            e.printStackTrace();
        }
    }
    
    private void registerCommand(@NotNull String name, @NotNull CommandExecutor executor) {
        Objects.requireNonNull(getCommand(name), name + " is not registered in plugin.yml").setExecutor(executor);
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic

        VotesFile.save(this);
        DBConnector.close();
        getLogger().info("LifeCore has been disabled.");
    }

    public void registerEvents() {

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new CreatureSpawnEventListener(), this);
        pm.registerEvents(new TrashListener(), this);
        pm.registerEvents(new UseAdminSwordListener(), this);
        pm.registerEvents(new NoItemFrameObstructionListener(), this);
        pm.registerEvents(new SpawnOnJoinListener(), this);
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
}
