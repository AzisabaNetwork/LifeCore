package com.github.mori01231.lifecore.block

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class CustomBlockRegion(val world: String, val x: Int, val z: Int) {
    private val states = mutableMapOf<Long, CustomBlockState>()
    var dirty: Boolean = false
        private set

    fun packXYZ(x: Int, y: Int, z: Int) = (x.toLong() shl 42) or (y.toLong() shl 21) or z.toLong()

    fun unpackX(packed: Long) = (packed shr 42).toInt()

    fun unpackY(packed: Long) = (packed shr 21 and 0x1FFFFF).toInt()

    fun unpackZ(packed: Long) = (packed and 0x1FFFFF).toInt()

    fun getAllStates(): Map<Triple<Int, Int, Int>, CustomBlockState> {
        return states.mapKeys {
            val regionX = x * 512
            val regionZ = z * 512
            val x = unpackX(it.key) + regionX
            val y = unpackY(it.key)
            val z = unpackZ(it.key) + regionZ
            Triple(x, y, z)
        }
    }

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
            val file = File(CustomBlockManager.regionDir, "$world/$x.$z.json.writing")
            file.parentFile.mkdirs()
            dirty = false
            file.writeText(Json.encodeToString(this))
            file.renameTo(File(CustomBlockManager.regionDir, "$world/$x.$z.json"))
        }
    }
}
