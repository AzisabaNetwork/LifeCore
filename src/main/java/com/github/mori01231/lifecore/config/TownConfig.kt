package com.github.mori01231.lifecore.config

import kotlinx.serialization.Serializable

@Serializable
data class TownConfig(
    @JvmField var allowInvisibility: Boolean = true,
    @JvmField var allowSit: Boolean = true,
)
