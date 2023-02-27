package com.github.mori01231.lifecore;

import com.github.mori01231.lifecore.util.SQLThrowableConsumer;
import com.github.mori01231.lifecore.util.SQLThrowableFunction;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mariadb.jdbc.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class DBConnector {
    private static @Nullable HikariDataSource dataSource;

    public static void init(@NotNull LifeCore plugin) throws SQLException {
        new Driver();
        HikariConfig config = new HikariConfig();
        if (plugin.getDatabaseConfig().getDriver() != null) {
            config.setDriverClassName(plugin.getDatabaseConfig().getDriver());
        }
        config.setJdbcUrl(plugin.getDatabaseConfig().toUrl());
        config.setUsername(plugin.getDatabaseConfig().getUsername());
        config.setPassword(plugin.getDatabaseConfig().getPassword());
        config.setDataSourceProperties(plugin.getDatabaseConfig().properties());
        dataSource = new HikariDataSource(config);
        // create table
        use(connection -> {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `ng_words` (" +
                    "  `id` VARCHAR(36) NOT NULL," +
                    "  `word` VARCHAR(255) NOT NULL," +
                    "  UNIQUE KEY `id_word` (`id`, `word`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;");
        });
    }

    /**
     * Returns the data source. Throws an exception if the data source is not initialized using {@link #init(LifeCore)}.
     * @return the data source
     * @throws NullPointerException if the data source is not initialized using {@link #init(LifeCore)}
     */
    @Contract(pure = true)
    @NotNull
    public static HikariDataSource getDataSource() {
        return Objects.requireNonNull(dataSource, "#init was not called");
    }

    @NotNull
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Contract(pure = true)
    public static <R> R use(@NotNull SQLThrowableFunction<Connection, R> action) throws SQLException {
        try (Connection connection = getConnection()) {
            return action.apply(connection);
        }
    }

    @Contract(pure = true)
    public static void use(@NotNull SQLThrowableConsumer<Connection> action) throws SQLException {
        try (Connection connection = getConnection()) {
            action.accept(connection);
        }
    }

    @Contract(pure = true)
    public static void runPrepareStatement(@Language("SQL") @NotNull String sql, @NotNull SQLThrowableConsumer<PreparedStatement> action) throws SQLException {
        use(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                action.accept(statement);
            }
        });
    }

    @Contract(pure = true)
    public static <R> R getPrepareStatement(@Language("SQL") @NotNull String sql, @NotNull SQLThrowableFunction<PreparedStatement, R> action) throws SQLException {
        return use(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                return action.apply(statement);
            }
        });
    }

    @Contract(pure = true)
    public static void useStatement(@NotNull SQLThrowableConsumer<Statement> action) throws SQLException {
        use(connection -> {
            try (Statement statement = connection.createStatement()) {
                action.accept(statement);
            }
        });
    }

    /**
     * Closes the data source if it is initialized.
     */
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
