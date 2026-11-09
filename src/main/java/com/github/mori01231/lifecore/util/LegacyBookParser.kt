package com.github.mori01231.lifecore.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import java.util.logging.Logger

/**
 * Parses the legacy (pre-1.20.5) `/give` NBT format of a written book, e.g.
 * `written_book{pages:['<json>','<json>'],title:"...",author:"..."}`,
 * and builds the book through the Bukkit API instead of dispatching `/give`.
 */
object LegacyBookParser {
    fun parse(input: String, logger: Logger? = null): ItemStack {
        val pages = readStringList(input, "pages")
        val title = readString(input, "title") ?: "Book"
        val author = readString(input, "author") ?: ""

        val item = ItemStack(Material.WRITTEN_BOOK)
        item.editMeta(BookMeta::class.java) { meta ->
            meta.title(Component.text(title))
            meta.author(Component.text(author))
            meta.addPages(*pages.mapIndexed { index, page -> parsePage(page, index, logger) }.toTypedArray())
        }
        return item
    }

    private fun parsePage(json: String, index: Int, logger: Logger?): Component {
        val serializer = GsonComponentSerializer.gson()
        return try {
            serializer.deserialize(json)
        } catch (e: Exception) {
            // Repair unquoted values such as `"color":reset"` and try again
            val repaired = json.replace(Regex(""":(\w+)""""), """:"$1"""")
            try {
                serializer.deserialize(repaired).also {
                    logger?.warning("Book page ${index + 1} contains malformed JSON; repaired automatically.")
                }
            } catch (e2: Exception) {
                logger?.warning("Failed to parse book page ${index + 1}: ${e.message}")
                Component.text(json)
            }
        }
    }

    private fun findValueStart(input: String, key: String): Int {
        val match = Regex("""[{,]\s*$key\s*:\s*""").find(input) ?: return -1
        return match.range.last + 1
    }

    private fun readString(input: String, key: String): String? {
        val start = findValueStart(input, key)
        if (start < 0 || start >= input.length) return null
        return readQuoted(input, start)?.first
    }

    private fun readStringList(input: String, key: String): List<String> {
        var i = findValueStart(input, key)
        if (i < 0 || i >= input.length || input[i] != '[') return emptyList()
        i++
        val result = mutableListOf<String>()
        while (i < input.length) {
            while (i < input.length && (input[i].isWhitespace() || input[i] == ',')) i++
            if (i >= input.length || input[i] == ']') break
            val (value, next) = readQuoted(input, i) ?: break
            result += value
            i = next
        }
        return result
    }

    /** Reads an SNBT quoted string starting at [start]; returns the unescaped value and the index after it. */
    private fun readQuoted(input: String, start: Int): Pair<String, Int>? {
        val quote = input[start]
        if (quote != '\'' && quote != '"') return null
        val sb = StringBuilder()
        var i = start + 1
        while (i < input.length) {
            val c = input[i]
            when {
                c == '\\' && i + 1 < input.length -> {
                    sb.append(input[i + 1])
                    i += 2
                }
                c == quote -> return sb.toString() to i + 1
                else -> {
                    sb.append(c)
                    i++
                }
            }
        }
        return null
    }
}
