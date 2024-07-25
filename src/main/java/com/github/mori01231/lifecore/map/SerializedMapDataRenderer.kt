package com.github.mori01231.lifecore.map

import net.minecraft.server.v1_15_R1.WorldMap
import org.bukkit.craftbukkit.v1_15_R1.map.CraftMapCanvas
import org.bukkit.craftbukkit.v1_15_R1.map.CraftMapView
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
        val worldMap = CraftMapView::class.java.getDeclaredField("worldMap").apply { isAccessible = true }[view] as WorldMap
        for (x in 0..127) {
            for (y in 0..127) {
                worldMap.decorations.clear()
                worldMap.flagDirty(x, y)
            }
        }
        init = true
    }
}