/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cynicalnicole.sleeptracker.Commands;

import java.util.HashMap;
import java.util.Map;
import me.cynicalnicole.sleeptracker.MessageFormatting;
import me.cynicalnicole.sleeptracker.Models.PlayerSleepYML;
import me.cynicalnicole.sleeptracker.SleepTracker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Nicole
 */
public class TimeAwake implements CommandExecutor {
    SleepTracker instance;
    
    public TimeAwake(SleepTracker instance) {
        instance.getCommand("timeawake").setExecutor(this);
        this.instance = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (!(cs instanceof Player)) return true;
        if (!((Player) cs).hasPermission("timeawake.self") && strings.length == 0) {
            //cs.sendMessage(String.format("%sYou do not have permission to check your days awake.", org.bukkit.ChatColor.RED));
            cs.sendMessage(MessageFormatting.getMessageString("messages.errors.timeawake-self"));
            return true;
        }
        Player target = (Player) cs;
        
        if (strings.length > 1) {
            //cs.sendMessage(String.format("%sUnknown command arguments.", org.bukkit.ChatColor.RED));
            cs.sendMessage(MessageFormatting.getMessageString("messages.errors.unknown-arguments"));
            return true;
        }
        
        if (strings.length == 1) {
            Player player = (Player) cs;
            if (!player.hasPermission("timeawake.others")) {
                //cs.sendMessage(String.format("%sYou do not have permission to check awake time for other players.", org.bukkit.ChatColor.RED));
                cs.sendMessage(MessageFormatting.getMessageString("messages.errors.timeawake-others"));
                return true;
            }
            
            target = getOnlinePlayerByName(strings[0]);
            if (target == null) {
                //cs.sendMessage(String.format("%sThe specified player does not exist.", org.bukkit.ChatColor.RED));
                cs.sendMessage(MessageFormatting.getMessageString("messages.errors.unknown-player"));       
                return true;
            }
            
            if (target == (Player)cs && !((Player) cs).hasPermission("timeawake.self")) {
                 //cs.sendMessage(String.format("%sYou do not have permission to check your days awake.", org.bukkit.ChatColor.RED));
                cs.sendMessage(MessageFormatting.getMessageString("messages.errors.timeawake-self"));
                return true;
            }
        }
        
        PlayerSleepYML playerSleep = instance.getPlayerSleep(target.getUniqueId().toString());
        
        if (playerSleep == null) {
            //cs.sendMessage(String.format("%sThere was an error fetching the sleep information for %s.", org.bukkit.ChatColor.RED, target.getName()));
            Map<MessageFormatting.PlaceholderType, String> variables = new HashMap<MessageFormatting.PlaceholderType, String>();
            variables.put(MessageFormatting.PlaceholderType.PLAYER, target.getName());
            
            cs.sendMessage(MessageFormatting.getMessageString("messages.errors.player-info", variables));      
            return true;
        }
        
        int daysSinceSlept = getMinecraftDaysSinceLastSlept(playerSleep.getLastSleepTick());
        Map<MessageFormatting.PlaceholderType, String> variables = new HashMap<MessageFormatting.PlaceholderType, String>();
        variables.put(MessageFormatting.PlaceholderType.PLAYER, target.getName());
        
        if (daysSinceSlept > 0) {
            variables.put(MessageFormatting.PlaceholderType.AWAKE_TIME, String.valueOf(daysSinceSlept));
            cs.sendMessage(MessageFormatting.getMessageString("messages.timeawake", variables)); 
        }
        else {
            cs.sendMessage(MessageFormatting.getMessageString("messages.timeawake-slept", variables)); 
        }
        
        //cs.sendMessage(String.format("%s%s has not slept in %s days", org.bukkit.ChatColor.LIGHT_PURPLE, target.getName(), String.valueOf(daysSinceSlept)));
        return true;
    }
    
    private Player getOnlinePlayerByName(String playerName) {
        Player player = instance.getServer().getPlayer(playerName);
        
        if (player == null) {
            for (Player online : instance.getServer().getOnlinePlayers()) {
                if (online.getDisplayName().equals(playerName)) {
                    player = online;
                    break;
                }
            }
        }
        
        return player;
    }
    
    private int getMinecraftDaysSinceLastSlept(long lastSleptTick) {
        long currentWorldTick = instance.getWorld().getFullTime();
        long diff = currentWorldTick - lastSleptTick;
        
        //24000 ticks = 1 minecraft day
        return (int) Math.floor(diff / 24000);
    }
}
