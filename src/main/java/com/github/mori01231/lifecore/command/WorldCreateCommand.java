package com.github.mori01231.lifecore.command;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class WorldCreateCommand  implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(args.length >= 2)) return help(sender);
        String name = args[0];
        if (!Pattern.compile("[a-zA-Z0-9/._-]+").matcher(name).matches()) return failed(sender, "そのワールド名は無効です。");

        String environment = args[1];
        World.Environment we;
        try {
            we = World.Environment.valueOf(environment.toUpperCase());
        } catch (IllegalArgumentException e) {
            return failed(sender, "environmentを正しく指定してください。");
        }

        Difficulty difficulty = Difficulty.EASY;
        WorldType type = WorldType.NORMAL;
        String generator = "";
        String seed = UUID.randomUUID().toString();
        boolean allowStractures = true;

        int count = 0;
        try {
            for (String arg : args) {
                if (arg.equals("-t")) {
                    type = WorldType.valueOf(args[count + 1].toUpperCase());
                }
                if (arg.equals("-a")) {
                    allowStractures = Boolean.parseBoolean(args[count + 1]);
                }
                if (arg.equals("-s")) {
                    seed = args[count + 1];
                }
                if (arg.equals("-d")) {
                    difficulty = Difficulty.valueOf(args[count + 1]);
                }
                count++;
            }
        } catch (IllegalArgumentException e) {
            return failed(sender, "オプションを正しく入力してください。");
        }

        createWorld(name, generator, difficulty, type, we, seed, allowStractures);
        return true;
    }

    private boolean help(@NotNull CommandSender sender) {
        sender.sendMessage("§e§l/wc [<name>] [<environment>] <-g [<generator>]> <-t [<worldType>]> <-s [<seed>]> <-a [<false or true>]> <-d [<difficulty>]>");
        return true;
    }

    private boolean failed(@NotNull CommandSender sender, String reason) {
        sender.sendMessage("§c" + reason);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 2 && args[1].isEmpty()) {
            return Arrays.asList("NORMAL", "NETHER", "THE_END", "CUSTOM");
        }
        for (int count = 0; count < args.length; count++) {
            if (args.length == count + 1 && args[count].isEmpty() && count > 2) {
                if (args[count - 1].equals("-t")) {
                    return Arrays.asList("NORMAL", "FLAT", "LARGE_BIOMES", "AMPLIFIED", "CUSTOM");
                }
                if (args[count - 1].equals("-a")) {
                    return Arrays.asList("true", "false");
                }
                if (args[count - 1].equals("-s")) {
                    return Collections.singletonList(UUID.randomUUID().toString());
                }
                if (args[count - 1].equals("-d")) {
                    return Arrays.asList("EASY", "NORMAL", "HARD");
                }
            }
        }
        if (args.length == 1) {
            return Collections.singletonList("ワールド名を入力してください");
        }
        return null;
    }

    public void createWorld(String name, String gen, Difficulty dif, WorldType type, World.Environment environment, String seed, boolean generate) {
        MultiverseCore core = JavaPlugin.getPlugin(MultiverseCore.class);
        if (core.getMVWorldManager()
                .addWorld(name, environment, seed, type, generate, gen)) {

            Bukkit.getScheduler().runTaskLater(core, ()-> {
                World w = core.getMVWorldManager().getMVWorld(name).getCBWorld();
                if (w == null) return;
                settings(w, dif);
            }, 20);
        }
    }

    private void settings(@NotNull World w, Difficulty dif) {
        w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        w.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        w.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        w.setGameRule(GameRule.KEEP_INVENTORY, true);
        w.setGameRule(GameRule.SPAWN_RADIUS, 0);
        w.setPVP(false);
        w.setDifficulty(dif);
        w.getWorldBorder().setCenter(0, 0);
    }
}
