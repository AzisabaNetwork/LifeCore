package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import static org.bukkit.Bukkit.getServer;

public class SwitchCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;

            //NO PERMISSION

            if(!(sender.hasPermission("lifecore.switchadmin") || sender.hasPermission("lifecore.switchdeveloper") || sender.hasPermission("lifecore.switchowner")
                    || sender.hasPermission("lifecore.isadmin") || sender.hasPermission("lifecore.isdeveloper") || sender.hasPermission("lifecore.isowner"))){

                sender.sendMessage(ChatColor.translateAlternateColorCodes
                        ('&',"&4権限不足です。\n&3Mori01231#9559のDMにこのメッセージのスクショをもって泣きつきましょう！" ));
            }


            //CHANGE TO ADMIN MODE

            //member to admin
            if (sender.hasPermission("lifecore.switchadmin") && !sender.hasPermission("lifecore.isadmin")){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3Adminモードになりました。" ));
                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add admin server=life");

                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove switchadmin");
            }

            //member to developer
            if (sender.hasPermission("lifecore.switchdeveloper") && !sender.hasPermission("lifecore.isdeveloper")){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3Developerモードになりました。" ));
                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add developer");

                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove switchdeveloper");
            }

            //member to owner
            if (sender.hasPermission("lifecore.switchowner") && !sender.hasPermission("lifecore.isowner")){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3Ownerモードになりました。" ));
                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add owner");

                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove switchowner");

            }




            //CHANGE TO MEMBER MODE

            //admin to member
            if (sender.hasPermission("lifecore.isadmin")){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3Memberモードになりました。" ));
                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add switchadmin");

                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove admin server=life");

            }

            //developer to member
            if (sender.hasPermission("lifecore.isdeveloper")){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3Memberモードになりました。" ));
                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add switchdeveloper");

                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove developer");

            }

            //owner to member
            if (sender.hasPermission("lifecore.isowner")){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3Memberモードになりました。" ));
                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent add switchowner");

                getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + player.getName() + " parent remove owner");

            }


        }
        else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }

        return true;
    }
}
