package com.github.mori01231.lifecore.command

import com.mojang.serialization.Codec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.server.MinecraftServer
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * メインハンドとオフハンドのアイテムのデータコンポーネントを比較するコマンド。
 * 両方のアイテムに存在する全コンポーネントをキーごとに比較し、
 * 差分があるもの・片方にしかないものだけを表示する。
 */
class ItemCompareCommand : PlayerTabExecutor() {
    override fun execute(player: Player, args: Array<String>): Boolean {
        val main = player.inventory.itemInMainHand
        val off = player.inventory.itemInOffHand
        if (main.type.isAir && off.type.isAir) {
            player.sendMessage("${ChatColor.RED}比較するアイテムがありません。メインハンドまたはオフハンドにアイテムを持ってください。")
            return true
        }

        val ops = MinecraftServer.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE)
        val nmsMain = CraftItemStack.asNMSCopy(main)
        val nmsOff = CraftItemStack.asNMSCopy(off)

        class EncodeResult(val value: Tag?, val error: String?) {
            fun text(): String {
                if (error != null) return "${ChatColor.DARK_RED}<encode error: $error>"
                return value?.toString() ?: "<none>"
            }
        }

        fun encode(codec: Codec<Any>, value: Any?): EncodeResult {
            if (value == null) return EncodeResult(null, null)
            return runCatching {
                @Suppress("UNCHECKED_CAST")
                val dataResult = codec.encodeStart(ops, value)
                val optional = dataResult.result()
                if (optional.isPresent) {
                    EncodeResult(optional.get(), null)
                } else {
                    EncodeResult(null, dataResult.toString())
                }
            }.getOrElse { EncodeResult(null, it.message ?: it.toString()) }
        }

        player.sendMessage("${ChatColor.AQUA}----------Item Compare----------")
        player.sendMessage("${ChatColor.AQUA}Main : ${describe(main)}")
        player.sendMessage("${ChatColor.AQUA}Off  : ${describe(off)}")

        class Entry(val id: String, val main: EncodeResult, val off: EncodeResult)

        val diff = mutableListOf<Entry>()
        var identical = 0
        for (type in BuiltInRegistries.DATA_COMPONENT_TYPE) {
            @Suppress("UNCHECKED_CAST")
            val codec = type.codec() as? Codec<Any>
            if (codec == null) {
                // streamCodec のみを持つコンポーネント (MapID など) は NBT にシリアライズできないためスキップ
                continue
            }
            val mainResult = encode(codec, nmsMain.get(type))
            val offResult = encode(codec, nmsOff.get(type))
            if (mainResult.value == null && mainResult.error == null && offResult.value == null && offResult.error == null) {
                // both sides have no component
                continue
            }
            if (mainResult.error == null && offResult.error == null && mainResult.value == offResult.value) {
                identical++
                continue
            }
            diff.add(Entry(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type).toString(), mainResult, offResult))
        }

        if (diff.isEmpty()) {
            player.sendMessage("${ChatColor.GREEN}データコンポーネントに差分はありません。($identical identical components)")
            player.sendMessage("${ChatColor.AQUA}-------------------------------")
            return true
        }

        player.sendMessage("${ChatColor.AQUA}identical: $identical / diff: ${diff.size}")
        for ((index, entry) in diff.sortedBy { it.id }.withIndex()) {
            if (index >= MAX_DIFF_LINES) {
                player.sendMessage("${ChatColor.GRAY}... 他 ${diff.size - MAX_DIFF_LINES} 件")
                break
            }
            val marker = when {
                entry.main.value == null && entry.main.error == null -> "${ChatColor.YELLOW}[only-off ]"
                entry.off.value == null && entry.off.error == null -> "${ChatColor.RED}[only-main]"
                else -> "${ChatColor.LIGHT_PURPLE}[diff     ]"
            }
            player.sendMessage("$marker ${ChatColor.WHITE}${entry.id}")
            player.sendMessage("    ${ChatColor.AQUA}main: ${entry.main.text()}")
            player.sendMessage("    ${ChatColor.GOLD}off : ${entry.off.text()}")
        }
        player.sendMessage("${ChatColor.AQUA}-------------------------------")
        return true
    }

    private fun describe(item: ItemStack): String {
        val name = item.type.name
        return if (item.type.isAir) name else "$name x${item.amount}"
    }

    private companion object {
        const val MAX_DIFF_LINES = 40
    }
}
