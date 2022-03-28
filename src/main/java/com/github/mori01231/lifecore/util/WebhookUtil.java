package com.github.mori01231.lifecore.util;

import com.github.mori01231.lifecore.LifeCore;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookUtil {
    private static final Gson GSON = new Gson();

    // send webhook to discord
    public static void sendDiscordWebhook(String configPath, String username, String content) {
        try {
            String s = LifeCore.getInstance().getConfig().getString(configPath);
            if (s == null || s.isEmpty()) {
                return;
            }
            HttpsURLConnection con = (HttpsURLConnection) new URL(s).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("User-Agent", "LifeCore/" + LifeCore.getInstance().getDescription().getVersion());
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            OutputStream stream = con.getOutputStream();
            JsonObject json = new JsonObject();
            if (username != null) json.addProperty("username", username);
            json.addProperty("content", content);
            stream.write(GSON.toJson(json).getBytes(StandardCharsets.UTF_8));
            stream.flush();
            stream.close();
            con.connect();

            InputStream errorStream = con.getErrorStream();
            if (errorStream != null) {
                String err = new String(ByteStreams.toByteArray(errorStream), StandardCharsets.UTF_8);
                LifeCore.getInstance().getLogger().warning("Discord webhook returned " + con.getResponseCode() + ": " + err);
            }
            con.getInputStream().close();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
