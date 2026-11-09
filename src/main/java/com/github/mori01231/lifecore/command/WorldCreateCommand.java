package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class WorldCreateCommand implements TabExecutor {
    private static final Pattern WORLD_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9/._-]+");
    private final LifeCore plugin;

    public WorldCreateCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) return help(sender);
        String name = args[0];
        if (!WORLD_NAME_PATTERN.matcher(name).matches()) return failed(sender, "そのワールド名は無効です。");
        if (Bukkit.getWorld(name) != null) return failed(sender, "そのワールドはすでに存在します。");

        World.Environment environment;
        try {
            environment = World.Environment.valueOf(args[1].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return failed(sender, "environmentを正しく指定してください。");
        }

        Difficulty difficulty = Difficulty.EASY;
        WorldType type = WorldType.NORMAL;
        String seed = UUID.randomUUID().toString();
        boolean generateStructures = true;

        try {
            for (int i = 2; i < args.length - 1; i++) {
                String value = args[i + 1];
                switch (args[i]) {
                    case "-t" -> type = WorldType.valueOf(value.toUpperCase(Locale.ROOT));
                    case "-a" -> generateStructures = Boolean.parseBoolean(value);
                    case "-s" -> seed = value;
                    case "-d" -> difficulty = Difficulty.valueOf(value.toUpperCase(Locale.ROOT));
                    default -> {
                        continue;
                    }
                }
                i++;
            }
        } catch (IllegalArgumentException e) {
            return failed(sender, "オプションを正しく入力してください。");
        }

        sender.sendMessage(Component.text("ワールド " + name + " を作成中です...", NamedTextColor.GRAY));
        World world = new WorldCreator(name)
                .environment(environment)
                .type(type)
                .seed(parseSeed(seed))
                .generateStructures(generateStructures)
                .createWorld();
        if (world == null) {
            return failed(sender, "ワールドの作成に失敗しました。");
        }
        applySettings(world, difficulty);

        // register the world to Multiverse (if installed) so that it will be loaded on startup
        if (Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import " + name + " " + environment.name());
        }
        plugin.getSLF4JLogger().info("{} created world {} (environment: {}, type: {}, seed: {}, structures: {}, difficulty: {})",
                sender.getName(), name, environment, type, seed, generateStructures, difficulty);
        sender.sendMessage(Component.text("ワールド " + name + " を作成しました。", NamedTextColor.GREEN));
        return true;
    }

    private static long parseSeed(@NotNull String seed) {
        try {
            return Long.parseLong(seed);
        } catch (NumberFormatException e) {
            return seed.hashCode();
        }
    }

    private boolean help(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("/wc [<name>] [<environment>] <-t [<worldType>]> <-s [<seed>]> <-a [<false or true>]> <-d [<difficulty>]>", NamedTextColor.YELLOW, TextDecoration.BOLD));
        return true;
    }

    private boolean failed(@NotNull CommandSender sender, @NotNull String reason) {
        sender.sendMessage(Component.text(reason, NamedTextColor.RED));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("ワールド名を入力してください");
        }
        if (args.length == 2) {
            return Arrays.stream(World.Environment.values()).map(Enum::name).filter(s -> s.startsWith(args[1].toUpperCase(Locale.ROOT))).toList();
        }
        String prev = args[args.length - 2];
        switch (prev) {
            case "-t":
                return Arrays.stream(WorldType.values()).map(Enum::name).toList();
            case "-a":
                return Arrays.asList("true", "false");
            case "-s":
                return Collections.singletonList(UUID.randomUUID().toString());
            case "-d":
                return Arrays.stream(Difficulty.values()).map(Enum::name).toList();
        }
        return Arrays.asList("-t", "-a", "-s", "-d");
    }

    private static void applySettings(@NotNull World w, @NotNull Difficulty difficulty) {
        w.setGameRule(GameRules.SPAWN_MOBS, false);
        w.setGameRule(GameRules.RANDOM_TICK_SPEED, 0);
        w.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
        w.setGameRule(GameRules.KEEP_INVENTORY, true);
        w.setGameRule(GameRules.RESPAWN_RADIUS, 0);
        w.setPVP(false);
        w.setDifficulty(difficulty);
        w.getWorldBorder().setCenter(0, 0);
    }
}
