/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cynicalnicole.sleeptracker.Events;

import me.cynicalnicole.sleeptracker.Models.PlayerSleepYML;
import me.cynicalnicole.sleeptracker.SleepTracker;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Nicole
 */
public class SleepTrackerListeners implements Listener {
    SleepTracker instance;
    
    public SleepTrackerListeners(final SleepTracker instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
        this.instance = instance;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //Get joining player
        Player joinedPlayer = event.getPlayer();
        
        //Make them/get their player
        PlayerSleepYML sleepState = instance.createPlayerSleep(joinedPlayer.getUniqueId().toString(), joinedPlayer.getName());
        
        if (sleepState.getLastSleepTick() == 0) {
            sleepState.setPlayerSleep(instance.getWorld().getFullTime());
            sleepState.savePlayerFile();
        }
    }
    
    @EventHandler
    public void onPlayerSleepEvent(PlayerBedEnterEvent e) {
        //Ensure the sleep is successful
        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        
        //Get their state 
        PlayerSleepYML sleepState = instance.getPlayerSleep(e.getPlayer().getUniqueId().toString());
        
        if (sleepState != null) {
            sleepState.setPlayerSleep(instance.getWorld().getFullTime());
            sleepState.savePlayerFile();
        }
    }
}
