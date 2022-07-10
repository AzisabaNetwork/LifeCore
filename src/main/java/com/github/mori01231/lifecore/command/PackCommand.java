package com.github.mori01231.lifecore.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class PackCommand implements CommandExecutor {

    // server.propertiesの値を取得するメソッドが見つからなかったため
    private static final String URL = "https://packs.azisaba.net/life.zip";

    private final String PREFIX = chat("&7[&ePack&7] &r");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(chat("&cこのコマンドはプレイヤーのみ有効です"));
            return true;
        }

        Player p = (Player) sender;

        p.setResourcePack(URL);

        p.sendMessage(chat("{0}&aテクスチャの読み込みを要求しました", PREFIX));
        p.sendMessage(chat("{0}&b{1}&7確認画面が出ない場合&b{1}", PREFIX, "↓"));
        p.sendMessage(chat("{0}&eサーバー選択画面&7にてアジ鯖を選択し、&e設定 &7の &cサーバーリソースパック&7を&c毎回確認&7(&cPrompt&7)にしてください", PREFIX));

        return true;
    }

    public String chat(String convertString, Object... args){
        // メッセージをフォーマットして、&で色をつける
        return MessageFormat.format(ChatColor.translateAlternateColorCodes('&', convertString), args);

    }
}
