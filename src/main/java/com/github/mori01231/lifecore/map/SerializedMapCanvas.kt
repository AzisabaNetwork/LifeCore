package com.github.mori01231.lifecore.map

import kotlinx.serialization.Serializable
import org.bukkit.craftbukkit.v1_20_R2.map.CraftMapCanvas

@Serializable
data class SerializedMapCanvas(val buffer: ByteArray, val base: ByteArray) {
    companion object {
        fun asSerializableMirror(canvas: CraftMapCanvas): SerializedMapCanvas {
            val buffer = CraftMapCanvas::class.java.getDeclaredField("buffer").apply { isAccessible = true }[canvas] as ByteArray
            val base = CraftMapCanvas::class.java.getDeclaredField("base").apply { isAccessible = true }[canvas] as ByteArray
            return SerializedMapCanvas(buffer, base)
        }
    }

    fun injectToCraftMapCanvas(canvas: CraftMapCanvas) {
        val nmsBuffer = CraftMapCanvas::class.java.getDeclaredField("buffer").apply { isAccessible = true }[canvas] as ByteArray
        val nmsBase = CraftMapCanvas::class.java.getDeclaredField("base").apply { isAccessible = true }[canvas] as ByteArray
        if (buffer.size != nmsBuffer.size) error("buffer size doesn't match")
        if (base.size != nmsBase.size) error("base size doesn't match")
        for ((index, byte) in buffer.withIndex()) {
            nmsBuffer[index] = byte
        }
        for ((index, byte) in base.withIndex()) {
            nmsBase[index] = byte
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SerializedMapCanvas

        if (!buffer.contentEquals(other.buffer)) return false
        if (!base.contentEquals(other.base)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = buffer.contentHashCode()
        result = 31 * result + base.contentHashCode()
        return result
    }
}
