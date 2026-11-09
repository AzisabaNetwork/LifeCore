package com.github.mori01231.lifecore.util

import com.github.mori01231.lifecore.DBConnector
import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import java.sql.SQLException
import java.util.UUID
import java.util.regex.Pattern

class NGWordsCache {
    private val cache: Multimap<UUID, String> = MultimapBuilder.hashKeys().hashSetValues().build()

    fun add(uuid: UUID, word: String) {
        cache.put(uuid, word)
        runAsync {
            DBConnector.runPrepareStatement("INSERT INTO `ng_words` (`id`, `word`) VALUES (?, ?)") { ps ->
                ps.setString(1, uuid.toString())
                ps.setString(2, word)
                ps.executeUpdate()
            }
        }
    }

    fun remove(uuid: UUID, word: String) {
        cache.remove(uuid, word)
        runAsync {
            DBConnector.runPrepareStatement("DELETE FROM `ng_words` WHERE `id` = ? AND `word` = ?") { ps ->
                ps.setString(1, uuid.toString())
                ps.setString(2, word)
                ps.executeUpdate()
            }
        }
    }

    fun clear(uuid: UUID) {
        cache.removeAll(uuid)
        runAsync {
            DBConnector.runPrepareStatement("DELETE FROM `ng_words` WHERE `id` = ?") { ps ->
                ps.setString(1, uuid.toString())
                ps.executeUpdate()
            }
        }
    }

    fun get(uuid: UUID): MutableSet<String> = HashSet(cache.get(uuid))

    fun loadAsync(uuid: UUID) {
        runAsync {
            DBConnector.runPrepareStatement("SELECT `word` FROM `ng_words` WHERE `id` = ?") { ps ->
                ps.setString(1, uuid.toString())
                ps.executeQuery().use { rs ->
                    cache.removeAll(uuid)
                    while (rs.next()) {
                        cache.put(uuid, rs.getString("word"))
                    }
                }
            }
        }
    }

    private fun runAsync(action: () -> Unit) {
        Thread {
            try {
                action()
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }.start()
    }

    companion object {
        fun filter(words: Set<String>, originalMessage: String): String {
            var message = originalMessage
            for (word in words) {
                message = message.replace(Pattern.quote(word).toRegex(), "*".repeat(word.length))
            }
            var index: Int
            while (message.indexOf("§*").also { index = it } != -1) {
                message = message.substring(0, index + 1) + originalMessage[index + 1] + message.substring(index + 2)
            }
            return message
        }
    }
}
