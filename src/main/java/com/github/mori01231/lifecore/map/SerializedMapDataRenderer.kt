package com.github.mori01231.lifecore.map

import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import org.bukkit.craftbukkit.v1_20_R2.map.CraftMapCanvas
import org.bukkit.craftbukkit.v1_20_R2.map.CraftMapView
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView

class SerializedMapDataRenderer(private val canvas: SerializedMapCanvas) : MapRenderer() {
    private var init = false

    override fun render(view: MapView, canvas: MapCanvas, player: Player) {
        if (init) return
        this.canvas.injectToCraftMapCanvas(canvas as CraftMapCanvas)
        for (i in 0 until canvas.cursors.size()) {
            canvas.cursors.removeCursor(canvas.cursors.getCursor(i))
        }
        val worldMap = CraftMapView::class.java.getDeclaredField("worldMap").apply { isAccessible = true }[view] as MapItemSavedData
        worldMap.decorations.clear()
        worldMap.setDecorationsDirty()
        for (x in 0..127) {
            for (y in 0..127) {
                worldMap.setColorsDirty(x, y)
            }
        }
        worldMap.setDirty()
        init = true
    }
}
