package com.github.mori01231.lifecore.block

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class CustomBlockRegion(val x: Int, val z: Int) {
    private val states = mutableMapOf<Long, CustomBlockState>()
    var dirty: Boolean = false
        private set

    fun packXYZ(x: Int, y: Int, z: Int) = (x.toLong() shl 42) or (y.toLong() shl 21) or z.toLong()

    fun unpackX(packed: Long) = (packed shr 42).toInt()

    fun unpackY(packed: Long) = (packed shr 21 and 0x1FFFFF).toInt()

    fun unpackZ(packed: Long) = (packed and 0x1FFFFF).toInt()

    fun getState(x: Int, y: Int, z: Int): CustomBlockState? {
        val packed = packXYZ(x and 511, y, z and 511)
        return states[packed]
    }

    fun setState(x: Int, y: Int, z: Int, state: CustomBlockState?) {
        val packed = packXYZ(x and 511, y, z and 511)
        if (state == null) {
            states.remove(packed)
        } else {
            states[packed] = state
        }
        dirty = true
    }

    fun save() {
        if (dirty) {
            val file = File(CustomBlockManager.regionDir, "$x.$z.json.writing")
            dirty = false
            file.writeText(Json.encodeToString(this))
            file.renameTo(File(CustomBlockManager.regionDir, "$x.$z.json"))
        }
    }
}
