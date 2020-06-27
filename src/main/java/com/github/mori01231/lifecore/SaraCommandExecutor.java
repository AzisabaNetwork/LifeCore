package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class SaraCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String servername = LifeCore.getInstance().getConfig().getString("lp-server-name");

        if (sender instanceof Player){

            Player player = (Player) sender;

            //NO PERMISSION

            if(!(sender.hasPermission("lifecore.100yen") || sender.hasPermission("lifecore.500yen")|| sender.hasPermission("lifecore.1000yen") || sender.hasPermission("lifecore.2000yen") || sender.hasPermission("lifecore.5000yen") || sender.hasPermission("lifecore.10000yen"))){

                sender.sendMessage(ChatColor.translateAlternateColorCodes
                        ('&',"&4権限不足です。\n&3もしあなたが皿を持っているのであればMori01231#9559のDMにこのメッセージのスクショをもって泣きつきましょう！" ));
            }


            //CHANGE TO SHOW SARA MODE

            //10000yen
            if (sender.hasPermission("lifecore.10000yen")){
                if (!sender.hasPermission("lifecore.show10000yen")){
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add show10000yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3皿表示モードに切り替えました。" ));
                }
                else{
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove show10000yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ランク表示モードに切り替えました。" ));
                }
            }

            //5000yen
            else if (sender.hasPermission("lifecore.5000yen")){
                if (!sender.hasPermission("lifecore.show5000yen")){
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add show5000yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3皿表示モードに切り替えました。" ));
                }
                else{
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove show5000yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ランク表示モードに切り替えました。" ));
                }
            }

            //2000yen
            else if (sender.hasPermission("lifecore.2000yen")){
                if (!sender.hasPermission("lifecore.show2000yen")){
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add show2000yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3皿表示モードに切り替えました。" ));
                }
                else{
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove show2000yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ランク表示モードに切り替えました。" ));
                }
            }

            //1000yen
            else if (sender.hasPermission("lifecore.1000yen")){
                if (!sender.hasPermission("lifecore.show1000yen")){
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add show1000yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3皿表示モードに切り替えました。" ));
                }
                else{
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove show1000yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ランク表示モードに切り替えました。" ));
                }
            }

            //500yen
            else if (sender.hasPermission("lifcore.500yen")){
                if (!sender.hasPermission("lifcore.show500yen")){
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add show500yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3皿表示モードに切り替えました。" ));
                }
                else{
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove show500yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ランク表示モードに切り替えました。" ));
                }
            }

            //100yen
            else if (sender.hasPermission("lifcore.100yen")){
                if (!sender.hasPermission("lifcore.show100yen")){
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add show100yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3皿表示モードに切り替えました。" ));
                }
                else{
                    getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove show100yen server=" + servername);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ランク表示モードに切り替えました。" ));
                }
            }

            else{
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3皿が検知されませんでした。" ));
            }

        }
        else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }


        return true;
    }
}
