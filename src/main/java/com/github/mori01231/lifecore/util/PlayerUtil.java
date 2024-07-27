package com.github.mori01231.lifecore.util;

import com.github.mori01231.lifecore.DBConnector;
import com.github.mori01231.lifecore.DatabaseConfig;
import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.TableKey;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerUtil {
    @Contract("_, _ -> new")
    @NotNull
    public static CompletableFuture<UUID> resolveUUIDAsync(@NotNull LifeCore plugin, @NotNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            // try faster (cached) method first
            try {
                return resolveUUID(name);
            } catch (IllegalArgumentException ignore) {}

            // and then try slower (database) method
            UUID uuid = fetchUUID(plugin.getDatabaseConfig(), name);
            if (uuid != null) {
                return uuid;
            }
            return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
            //throw new NoSuchElementException(name + " cannot be resolved to a UUID");
        }, plugin.asyncExecutor);
    }

    @NotNull
    public static UUID resolveUUID(@NotNull String name) throws IllegalArgumentException {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return player.getUniqueId();
        }
        try {
            return UUID.fromString(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("name is not UUID or player name");
        }
    }

    @Nullable
    public static UUID fetchUUID(@NotNull DatabaseConfig databaseConfig, @NotNull String name) {
        String dbName = databaseConfig.getDatabaseName(TableKey.SpicyAzisaBan);
        try {
            return DBConnector.getPrepareStatement("SELECT `uuid` FROM `" + dbName + "`.`players` WHERE LOWER(`name`) = ? LIMIT 1", ps -> {
                ps.setString(1, name.toLowerCase());
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                return UUID.fromString(rs.getString("uuid"));
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Channel getChannel(@NotNull Player player) {
        // channel field is set to non-null value after #channelActive is called
        return Objects.requireNonNull(((CraftPlayer) player).getHandle().connection.connection.channel, "inactive channel");
    }
}
