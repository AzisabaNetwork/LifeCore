package com.github.mori01231.lifecore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        this.getCommand("switch").setExecutor(new SwitchCommandExecutor());
        this.getCommand("wiki").setExecutor(new WikiCommandExecutor());
        this.getCommand("website").setExecutor(new WebsiteCommandExecutor());

        this.saveDefaultConfig();

    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("LifeCore has been disabled.");
    }
}
