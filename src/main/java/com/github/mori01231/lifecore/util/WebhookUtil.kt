package com.github.mori01231.lifecore.util

import com.github.mori01231.lifecore.LifeCore
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URI
import javax.net.ssl.HttpsURLConnection

object WebhookUtil {
    private val GSON = Gson()

    // send webhook to discord
    fun sendDiscordWebhook(plugin: LifeCore, configPath: String, username: String?, content: String) {
        try {
            val s = plugin.config.getString(configPath)
            if (s.isNullOrEmpty()) {
                return
            }
            val con = URI.create(s).toURL().openConnection() as HttpsURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("User-Agent", "LifeCore/" + plugin.pluginMeta.version)
            con.setRequestProperty("Accept", "application/json")
            con.doOutput = true
            con.connectTimeout = 5000
            con.readTimeout = 5000
            val json = JsonObject()
            if (username != null) json.addProperty("username", username)
            json.addProperty("content", content)
            con.outputStream.use { stream ->
                stream.write(GSON.toJson(json).toByteArray(Charsets.UTF_8))
                stream.flush()
            }
            con.connect()
            con.errorStream?.use { errorStream ->
                val err = errorStream.readBytes().toString(Charsets.UTF_8)
                plugin.logger.warning("Discord webhook returned " + con.responseCode + ": " + err)
            }
            con.inputStream.close()
            con.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
