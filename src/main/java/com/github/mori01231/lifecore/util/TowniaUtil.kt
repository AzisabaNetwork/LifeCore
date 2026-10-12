package com.github.mori01231.lifecore.util

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import java.lang.reflect.Method
import java.util.Optional
import java.util.UUID

object TowniaUtil {
    private val towniaApiClass: Class<*>? by lazy {
        try {
            Class.forName("net.azisaba.townia.api.TowniaAPI")
        } catch (e: Throwable) {
            null
        }
    }

    private val getApiMethod: Method? by lazy {
        towniaApiClass?.getMethod("get")
    }

    private val getPlotMethod: Method? by lazy {
        towniaApiClass?.getMethod("getPlot", Chunk::class.java)
    }

    private val getTownMethod: Method? by lazy {
        towniaApiClass?.getMethod("getTown", UUID::class.java)
    }

    private val getResidentMethod: Method? by lazy {
        towniaApiClass?.getMethod("getResident", UUID::class.java)
    }

    private val towniaPluginClass: Class<*>? by lazy {
        try {
            Class.forName("net.azisaba.townia.Townia")
        } catch (e: Throwable) {
            null
        }
    }

    private val getTownManagerMethod: Method? by lazy {
        towniaPluginClass?.getMethod("getTownManager")
    }

    fun isPresent(): Boolean = towniaApiClass != null

    fun getApi(): Any? {
        return try {
            getApiMethod?.invoke(null)
        } catch (e: Throwable) {
            null
        }
    }

    fun getTowniaPlugin(): Any? {
        return try {
            Bukkit.getPluginManager().getPlugin("Townia")
        } catch (e: Throwable) {
            null
        }
    }

    fun getTownAt(location: Location): Any? {
        val api = getApi() ?: return null
        return try {
            val chunk = location.chunk
            val plotOptional = getPlotMethod?.invoke(api, chunk) as? Optional<*>
            val plot = plotOptional?.orElse(null) ?: return null
            val townUuid = plot.javaClass.getMethod("getTownUuid").invoke(plot) as? UUID ?: return null
            val townOptional = getTownMethod?.invoke(api, townUuid) as? Optional<*>
            townOptional?.orElse(null)
        } catch (e: Throwable) {
            null
        }
    }

    fun getTownName(town: Any): String? {
        return try {
            town.javaClass.getMethod("getName").invoke(town) as? String
        } catch (e: Throwable) {
            null
        }
    }

    fun getMayorUuid(town: Any): UUID? {
        return try {
            town.javaClass.getMethod("getMayorUuid").invoke(town) as? UUID
        } catch (e: Throwable) {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun isOutlaw(town: Any, playerUuid: UUID): Boolean {
        return try {
            val outlaws = town.javaClass.getMethod("getOutlaws").invoke(town) as? Set<UUID>
            outlaws?.contains(playerUuid) == true
        } catch (e: Throwable) {
            false
        }
    }

    fun isResident(town: Any, playerUuid: UUID): Boolean {
        val api = getApi() ?: return false
        return try {
            val townId = town.javaClass.getMethod("getId").invoke(town) as? UUID ?: return false
            val resOptional = getResidentMethod?.invoke(api, playerUuid) as? Optional<*>
            val resident = resOptional?.orElse(null) ?: return false
            val resTownUuid = resident.javaClass.getMethod("getTownUuid").invoke(resident) as? UUID
            resTownUuid == townId
        } catch (e: Throwable) {
            false
        }
    }

    fun isAllowInvisibility(town: Any): Boolean {
        return try {
            town.javaClass.getMethod("isAllowInvisibility").invoke(town) as? Boolean ?: true
        } catch (e: Throwable) {
            true
        }
    }

    fun setAllowInvisibility(town: Any, value: Boolean) {
        try {
            town.javaClass.getMethod("setAllowInvisibility", Boolean::class.javaPrimitiveType).invoke(town, value)
        } catch (_: Throwable) {}
    }

    fun isAllowSit(town: Any): Boolean {
        return try {
            town.javaClass.getMethod("isAllowSit").invoke(town) as? Boolean ?: true
        } catch (e: Throwable) {
            true
        }
    }

    fun setAllowSit(town: Any, value: Boolean) {
        try {
            town.javaClass.getMethod("setAllowSit", Boolean::class.javaPrimitiveType).invoke(town, value)
        } catch (_: Throwable) {}
    }

    fun isAllowPetPickup(town: Any): Boolean {
        return try {
            town.javaClass.getMethod("isAllowPetPickup").invoke(town) as? Boolean ?: true
        } catch (e: Throwable) {
            true
        }
    }

    fun setAllowPetPickup(town: Any, value: Boolean) {
        try {
            town.javaClass.getMethod("setAllowPetPickup", Boolean::class.javaPrimitiveType).invoke(town, value)
        } catch (_: Throwable) {}
    }

    fun isAllowPassenger(town: Any): Boolean {
        return try {
            town.javaClass.getMethod("isAllowPassenger").invoke(town) as? Boolean ?: true
        } catch (e: Throwable) {
            true
        }
    }

    fun setAllowPassenger(town: Any, value: Boolean) {
        try {
            town.javaClass.getMethod("setAllowPassenger", Boolean::class.javaPrimitiveType).invoke(town, value)
        } catch (_: Throwable) {}
    }

    fun saveTown(town: Any) {
        try {
            val plugin = getTowniaPlugin() ?: return
            val townManager = getTownManagerMethod?.invoke(plugin) ?: return
            val saveMethod = townManager.javaClass.getMethod("saveTown", town.javaClass)
            saveMethod.invoke(townManager, town)
        } catch (_: Throwable) {}
    }
}
