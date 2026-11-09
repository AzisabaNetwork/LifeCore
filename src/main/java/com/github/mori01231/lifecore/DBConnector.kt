package com.github.mori01231.lifecore

import com.github.mori01231.lifecore.util.SQLThrowableConsumer
import com.github.mori01231.lifecore.util.SQLThrowableFunction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.intellij.lang.annotations.Language
import org.mariadb.jdbc.Driver
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

object DBConnector {
    private var dataSource: HikariDataSource? = null

    @Throws(SQLException::class)
    fun init(plugin: LifeCore) {
        Driver()
        val databaseConfig = plugin.getDatabaseConfig()
        val config = HikariConfig()
        databaseConfig.driver?.let { config.driverClassName = it }
        config.jdbcUrl = databaseConfig.toUrl()
        config.username = databaseConfig.username
        config.password = databaseConfig.password
        config.dataSourceProperties = databaseConfig.properties()
        dataSource = HikariDataSource(config)
        // create table
        useStatement { statement ->
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `ng_words` (" +
                        "  `id` VARCHAR(36) NOT NULL," +
                        "  `word` VARCHAR(255) NOT NULL," +
                        "  UNIQUE KEY `id_word` (`id`, `word`)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
            )
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `player_maps` (" +
                        "  `owner` VARCHAR(36) NOT NULL," +
                        "  `name` VARCHAR(128) NOT NULL," +
                        "  `data` MEDIUMBLOB NOT NULL," +
                        "  `amount` BIGINT NOT NULL DEFAULT 1," +
                        "  `hash` VARCHAR(256) NOT NULL," +
                        "  PRIMARY KEY (`owner`, `hash`)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
            )
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `online_rewards` (" +
                        "  `reward_id` VARCHAR(64) NOT NULL," +
                        "  `slot` BIGINT NOT NULL," +
                        "  `uuid` VARCHAR(36) NOT NULL," +
                        "  `server` VARCHAR(64) NOT NULL," +
                        "  `given_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "  PRIMARY KEY (`reward_id`, `slot`, `uuid`)," +
                        "  KEY `given_at` (`given_at`)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
            )
        }
    }

    /**
     * Returns the data source. Throws an exception if the data source is not initialized using [init].
     * @throws IllegalStateException if the data source is not initialized using [init]
     */
    fun getDataSource(): HikariDataSource = checkNotNull(dataSource) { "#init was not called" }

    @Throws(SQLException::class)
    fun getConnection(): Connection = getDataSource().connection

    @Throws(SQLException::class)
    fun <R> use(action: SQLThrowableFunction<Connection, R>): R =
        getConnection().use { connection -> action.apply(connection) }

    @Throws(SQLException::class)
    fun runPrepareStatement(@Language("SQL") sql: String, action: SQLThrowableConsumer<PreparedStatement>) {
        use { connection ->
            connection.prepareStatement(sql).use { statement -> action.accept(statement) }
        }
    }

    @Throws(SQLException::class)
    fun <R> getPrepareStatement(@Language("SQL") sql: String, action: SQLThrowableFunction<PreparedStatement, R>): R =
        use { connection ->
            connection.prepareStatement(sql).use { statement -> action.apply(statement) }
        }

    @Throws(SQLException::class)
    fun useStatement(action: SQLThrowableConsumer<Statement>) {
        use { connection ->
            connection.createStatement().use { statement -> action.accept(statement) }
        }
    }

    /**
     * Closes the data source if it is initialized.
     */
    fun close() {
        dataSource?.close()
    }
}
