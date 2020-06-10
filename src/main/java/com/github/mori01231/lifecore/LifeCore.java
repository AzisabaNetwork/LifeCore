package com.github.mori01231.lifecore;

import org.bukkit.plugin.java.JavaPlugin;

public final class LifeCore extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("LifeCore has been enabled.");
        this.getCommand("switch").setExecutor(new SwitchCommandExecutor());
        this.getCommand("wiki").setExecutor(new WikiCommandExecutor());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
