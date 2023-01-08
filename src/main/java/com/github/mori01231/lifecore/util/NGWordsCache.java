package com.github.mori01231.lifecore.util;

import com.github.mori01231.lifecore.DBConnector;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public final class NGWordsCache {
    private final Multimap<UUID, String> cache = MultimapBuilder.hashKeys().hashSetValues().build();

    public void add(@NotNull UUID uuid, @NotNull String word) {
        cache.put(uuid, word);
        new Thread(() -> {
            try {
                DBConnector.runPrepareStatement("INSERT INTO `ng_words` (`id`, `word`) VALUES (?, ?)", ps -> {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, word);
                    ps.executeUpdate();
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void remove(@NotNull UUID uuid, @NotNull String word) {
        cache.remove(uuid, word);
        new Thread(() -> {
            try {
                DBConnector.runPrepareStatement("DELETE FROM `ng_words` WHERE `id` = ? AND `word` = ?", ps -> {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, word);
                    ps.executeUpdate();
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void clear(@NotNull UUID uuid) {
        cache.removeAll(uuid);
        new Thread(() -> {
            try {
                DBConnector.runPrepareStatement("DELETE FROM `ng_words` WHERE `id` = ?", ps -> {
                    ps.setString(1, uuid.toString());
                    ps.executeUpdate();
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Contract("_ -> new")
    public @NotNull Set<String> get(@NotNull UUID uuid) {
        return new HashSet<>(cache.get(uuid));
    }

    public void loadAsync(@NotNull UUID uuid) {
        new Thread(() -> {
            try {
                DBConnector.runPrepareStatement("SELECT `word` FROM `ng_words` WHERE `id` = ?", ps -> {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        cache.removeAll(uuid);
                        while (rs.next()) {
                            String word = rs.getString("word");
                            cache.put(uuid, word);
                        }
                    }
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static @NotNull String filter(@NotNull Set<String> words, @NotNull String originalMessage) {
        String message = originalMessage;
        for (String word : words) {
            message = message.replaceAll(Pattern.quote(word), repeat(word.length()));
        }
        int index;
        while ((index = message.indexOf("§*")) != -1) {
            message = message.substring(0, index + 1) + originalMessage.charAt(index + 1) + message.substring(index + 2);
        }
        return message;
    }

    @NotNull
    private static String repeat(int times) {
        return new String(new char[times]).replace("\0", "*");
    }
}
