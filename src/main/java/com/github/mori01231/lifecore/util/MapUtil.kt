package com.github.mori01231.lifecore.util

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.map.SerializedMapCanvas
import com.github.mori01231.lifecore.map.SerializedMapDataRenderer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.map.CraftMapCanvas
import org.bukkit.craftbukkit.map.CraftMapRenderer
import org.bukkit.craftbukkit.map.CraftMapView
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

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

    private const val SERIALIZED_MAP_DATA = "SerializedMapData"

    /** IDs of map views whose SerializedMapData is currently being decoded. */
    private val preparingMapIds: MutableSet<Int> = ConcurrentHashMap.newKeySet()

    /**
     * Assigns a fresh map view to a SerializedMapData map whose map data does not exist on this server.
     * Must be called on the main thread.
     *
     * Maps without SerializedMapData are left untouched: replacing them would permanently detach the item
     * from its original map id (and its image) with a blank map.
     *
     * @return the modified item (the same instance as [item]) if the map view was replaced, otherwise null
     */
    fun checkMapView(item: ItemStack): ItemStack? {
        if (item.type != Material.FILLED_MAP) return null
        val meta = item.itemMeta as? MapMeta ?: return null
        if (meta.hasMapView() && meta.mapView != null) return null
        if (ItemUtil.getByteArrayTag(item, SERIALIZED_MAP_DATA) == null) return null
        meta.mapView = Bukkit.createMap(Bukkit.getWorlds()[0])
        item.itemMeta = meta
        return item
    }

    /**
     * Attaches a [SerializedMapDataRenderer] to the map view of [item] if it carries SerializedMapData.
     * Must be called on the main thread; only decoding runs asynchronously.
     *
     * Once the renderer is attached, the server renders it for every player holding/seeing the map
     * (the renderer marks the whole map dirty on its first render), so no per-player render is needed.
     */
    fun prepareMapRenderer(item: ItemStack) {
        if (item.type != Material.FILLED_MAP) return
        val meta = item.itemMeta as? MapMeta ?: return
        if (!meta.hasMapView()) return
        val mapView = meta.mapView ?: return
        if (isPrepared(mapView)) return
        val data = ItemUtil.getByteArrayTag(item, SERIALIZED_MAP_DATA) ?: return
        if (!preparingMapIds.add(mapView.id)) return
        val plugin = JavaPlugin.getPlugin(LifeCore::class.java)
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val serializedMapData = try {
                deserializeCanvasFromString(String(EncodeUtil.decodeBase64AndGunzip(data)))
            } catch (e: Exception) {
                preparingMapIds.remove(mapView.id)
                plugin.logger.warning("Failed to decode SerializedMapData of map #${mapView.id}: $e")
                return@Runnable
            }
            Bukkit.getScheduler().runTask(plugin, Runnable {
                try {
                    if (isPrepared(mapView)) return@Runnable
                    mapView.renderers.filterIsInstance<CraftMapRenderer>().forEach { mapView.removeRenderer(it) }
                    mapView.addRenderer(SerializedMapDataRenderer(serializedMapData))
                } finally {
                    preparingMapIds.remove(mapView.id)
                }
            })
        })
    }

    /** Fixes the map view (if needed) and prepares the renderer of a map in an item frame. Main thread only. */
    fun prepareItemFrame(itemFrame: ItemFrame) {
        if (!itemFrame.isValid) return
        val item = itemFrame.item
        if (item.type != Material.FILLED_MAP) return
        checkMapView(item)?.let { itemFrame.setItem(it, false) }
        prepareMapRenderer(item)
    }

    /** Fixes the map views (if needed) and prepares renderers of all maps in the player's inventory. Main thread only. */
    fun prepareInventory(player: Player) {
        val inventory = player.inventory
        inventory.contents.forEachIndexed { index, item ->
            if (item == null || item.type != Material.FILLED_MAP) return@forEachIndexed
            checkMapView(item)?.let { inventory.setItem(index, it) }
            prepareMapRenderer(item)
        }
    }

    /** true if the map view already uses a non-vanilla renderer (ours or another plugin's). */
    private fun isPrepared(mapView: MapView) = mapView.renderers.any { it !is CraftMapRenderer }
}
