package com.github.mori01231.lifecore.util

import com.github.mori01231.lifecore.map.SerializedMapCanvas
import com.github.mori01231.lifecore.map.SerializedMapDataRenderer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R2.map.CraftMapCanvas
import org.bukkit.craftbukkit.v1_20_R2.map.CraftMapRenderer
import org.bukkit.craftbukkit.v1_20_R2.map.CraftMapView
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView

object MapUtil {
    private fun convertCanvasToSerializable(canvas: MapCanvas) =
        when (canvas) {
            is CraftMapCanvas -> SerializedMapCanvas.asSerializableMirror(canvas)
            else -> error("Unsupported type: ${canvas::class.java.name}")
        }

    fun serializeCanvasToJsonElement(canvas: MapCanvas): JsonElement =
        Json.encodeToJsonElement(convertCanvasToSerializable(canvas))

    fun serializeCanvasToString(canvas: MapCanvas): String =
        Json.encodeToString(convertCanvasToSerializable(canvas))

    fun deserializeCanvasFromJsonElement(element: JsonElement): SerializedMapCanvas =
        Json.decodeFromJsonElement(element)

    fun deserializeCanvasFromString(json: String): SerializedMapCanvas =
        Json.decodeFromString(json)

    @Suppress("UNCHECKED_CAST")
    fun MapView.getCanvases() =
        CraftMapView::class.java.getDeclaredField("canvases").apply { isAccessible = true }[this] as Map<MapRenderer, Map<CraftPlayer, CraftMapCanvas>>

    fun checkMapView(item: ItemStack): ItemStack? {
        if (item.type != Material.FILLED_MAP) return null
        val meta = item.itemMeta as? MapMeta ?: return null
        if (meta.mapView == null) {
            meta.mapView = Bukkit.createMap(Bukkit.getWorlds()[0])
            item.itemMeta = meta
            return item
        }
        return null
    }

    fun initializeMapRenderer(player: Player, item: ItemStack) {
        if (item.type != Material.FILLED_MAP) return
        val meta = item.itemMeta as? MapMeta? ?: return
        val mapView = meta.mapView ?: return
        val hasRenderer = mapView.renderers.isNotEmpty()
        if (hasRenderer && mapView.renderers[0] !is CraftMapRenderer) {
            if (mapView is CraftMapView) mapView.render(player as CraftPlayer)
            return
        }
        val serializedMapData =
            try {
                item.let { ItemUtil.getByteArrayTag(it, "SerializedMapData") ?: return }
                    .let { EncodeUtil.decodeBase64AndGunzip(it) }
                    .let { String(it) }
                    .let { deserializeCanvasFromString(it) }
            } catch (_: Exception) {
                return
            }
        if (hasRenderer) {
            mapView.removeRenderer(mapView.renderers[0])
        }
        mapView.addRenderer(SerializedMapDataRenderer(serializedMapData))
        if (mapView is CraftMapView) mapView.render(player as CraftPlayer)
    }
}
