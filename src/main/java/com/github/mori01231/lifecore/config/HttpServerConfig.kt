package com.github.mori01231.lifecore.config

import kotlinx.serialization.Serializable

@Serializable
data class HttpServerConfig(
    val enabled: Boolean = false,
    val port: Int = 8080,
    val token: String = DEFAULT_TOKEN,
) {
    companion object {
        const val DEFAULT_TOKEN = "this-should-be-a-random-string"
    }
}
