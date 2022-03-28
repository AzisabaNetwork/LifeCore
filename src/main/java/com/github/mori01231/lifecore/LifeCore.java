package com.github.mori01231.lifecore;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class LifeCore extends JavaPlugin {

    private static LifeCore instance;

    public LifeCore (){
        instance = this;
    }

    public static LifeCore getInstance() {
        return instance;
    }




    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("LifeCore has been enabled.");
        this.getCommand("wiki").setExecutor(new WikiCommandExecutor());
        this.getCommand("website").setExecutor(new WebsiteCommandExecutor());
        this.getCommand("help").setExecutor(new HelpCommandExecutor());
        this.getCommand("guide").setExecutor(new GuideCommandExecutor());
        this.getCommand("tutorial").setExecutor(new TutorialCommandExecutor());
        this.getCommand("pve").setExecutor(new PveCommandExecutor());
        this.getCommand("pve0").setExecutor(new Pve0CommandExecutor());
        this.getCommand("pve1").setExecutor(new Pve1CommandExecutor());
        this.getCommand("pve2").setExecutor(new Pve2CommandExecutor());
        this.getCommand("life").setExecutor(new LifeCommandExecutor());
        this.getCommand("resource").setExecutor(new ResourceCommandExecutor());
        this.getCommand("event").setExecutor(new EventCommandExecutor());
        this.getCommand("rank").setExecutor(new RankCommandExecutor());
        this.getCommand("sara").setExecutor(new SaraCommandExecutor());
        this.getCommand("trash").setExecutor(new TrashCommandExecutor());
        this.getCommand("pack").setExecutor(new PackCommandExecutor());
        this.getCommand("kiai").setExecutor(new KiaiCommandExecutor());
        this.getCommand("noob").setExecutor(new NoobCommandExecutor());
        this.getCommand("killnonadmin").setExecutor(new KillNonAdminCommandExecutor());

        this.saveDefaultConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        registerEvents();

    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("LifeCore has been disabled.");
    }

    public void registerEvents(){

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new CreatureSpawnEventListener(this),  this);
        pm.registerEvents(new TrashListener(this),  this);
        pm.registerEvents(new UseAdminSwordListener(),  this);
    }
}
