package com.github.mori01231.lifecore

import java.util.Locale

enum class TableKey {
    MPDB,
    SpicyAzisaBan,
    ;

    private val lowercase: String = name.lowercase(Locale.ROOT)

    fun lowercase(): String = lowercase
}
