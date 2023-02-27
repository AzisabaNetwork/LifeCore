package com.github.mori01231.lifecore

import com.github.mori01231.lifecore.util.toProperties
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DatabaseConfig(
    val driver: String? = null,
    val scheme: String = "jdbc:mariadb",
    val hostname: String = "localhost",
    val port: Int = 3306,
    val name: String = "lifecore",
    val username: String? = null,
    val password: String? = null,
    val properties: Map<String, String> = emptyMap(),
    @SerialName("names")
    val databaseNames: Map<String, String> = emptyMap(),
) {
    fun properties() = properties.toProperties()

    fun getDatabaseName(key: TableKey) = databaseNames.getOrDefault(key.lowercase(), key.lowercase())

    fun toUrl() = "$scheme://$hostname:$port/$name"
}
