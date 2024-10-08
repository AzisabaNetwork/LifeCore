package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ScheduleRestartCommand implements TabExecutor {
    private static final Map<Integer, Set<Action>> ACTIONS = new HashMap<>();
    private static final List<TimerTask> tasks = new ArrayList<>();
    private static final Timer TIMER = new Timer();
    private static boolean whitelistWasOn = false;
    private final LifeCore plugin;

    public ScheduleRestartCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/schedulerestart <minutes>");
            return true;
        }
        if (args[0].equalsIgnoreCase("gc")) {
            plugin.getGcListener().triggerNow();
            return true;
        }
        if (args[0].endsWith("s")) {
            int seconds = Integer.parseInt(args[0].substring(0, args[0].length() - 1));
            schedule(seconds);
            return true;
        }
        int minutes = Integer.parseInt(args[0]);
        schedule(minutes * 60);
        return true;
    }

    public static void schedule(int paramSeconds) {
        for (TimerTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
        Bukkit.broadcastMessage("§6[§dお知らせ§6] §aこのサーバーは§d" + paramSeconds + "秒後" + "§aに再起動されます。");
        ACTIONS.forEach((seconds, actions) -> {
            if (seconds <= paramSeconds) {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Bukkit.getScheduler().runTask(LifeCore.getPlugin(LifeCore.class), () -> {
                            for (Action action : actions) {
                                action.execute(seconds);
                            }
                        });
                    }
                };
                TIMER.schedule(timerTask, (paramSeconds - seconds) * 1000L);
                tasks.add(timerTask);
            }
        });
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.singletonList("gc");
    }

    public enum Action {
        BROADCAST {
            @Override
            public void execute(int seconds) {
                Bukkit.getScheduler().runTask(LifeCore.getPlugin(LifeCore.class), () -> {
                    if (seconds >= 60) {
                        int minutes = seconds / 60;
                        Bukkit.broadcastMessage("§6[§dお知らせ§6] §aこのサーバーは§d" + minutes + "分後" + "§aに再起動されます。");
                    } else {
                        Bukkit.broadcastMessage("§6[§dお知らせ§6] §aこのサーバーは§c" + seconds + "秒後" + "§aに再起動されます。");
                    }
                });
            }
        },
        ENABLE_WHITELIST {
            @Override
            public void execute(int seconds) {
                Bukkit.getScheduler().runTask(LifeCore.getPlugin(LifeCore.class), () -> {
                    whitelistWasOn = Bukkit.hasWhitelist();
                    Bukkit.setWhitelist(true);
                });
            }
        },
        SAVE_AND_KICK_ALL_PLAYERS {
            @Override
            public void execute(int seconds) {
                Bukkit.getScheduler().runTask(LifeCore.getPlugin(LifeCore.class), () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer("Server closed");
                    }
                });
            }
        },
        SCHEDULE_SHUTDOWN_SERVER {
            @Override
            public void execute(int seconds) {
                Bukkit.getScheduler().runTaskLater(LifeCore.getPlugin(LifeCore.class), () -> {
                    if (!whitelistWasOn) Bukkit.setWhitelist(false);
                    Bukkit.shutdown();
                }, 20 * 60);
            }
        },
        ;

        public abstract void execute(int seconds);
    }

    @NotNull
    @Contract("_ -> new")
    private static Set<Action> setOf(@NotNull Action... actions) {
        return new HashSet<>(Arrays.asList(actions));
    }

    static {
        ACTIONS.put(60 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(55 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(50 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(45 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(40 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(35 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(30 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(25 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(20 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(15 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(10 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(5 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(4 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(3 * 60, setOf(Action.BROADCAST));
        ACTIONS.put(2 * 60, setOf(Action.BROADCAST, Action.ENABLE_WHITELIST));
        ACTIONS.put(60, setOf(Action.BROADCAST));
        ACTIONS.put(30, setOf(Action.BROADCAST));
        ACTIONS.put(10, setOf(Action.BROADCAST));
        ACTIONS.put(5, setOf(Action.BROADCAST));
        ACTIONS.put(4, setOf(Action.BROADCAST));
        ACTIONS.put(3, setOf(Action.BROADCAST));
        ACTIONS.put(2, setOf(Action.BROADCAST));
        ACTIONS.put(1, setOf(Action.BROADCAST));
        ACTIONS.put(0, setOf(Action.SAVE_AND_KICK_ALL_PLAYERS, Action.SCHEDULE_SHUTDOWN_SERVER));
    }
}
