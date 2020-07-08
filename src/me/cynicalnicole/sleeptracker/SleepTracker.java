/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cynicalnicole.sleeptracker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import me.cynicalnicole.sleeptracker.Commands.TimeAwake;
import me.cynicalnicole.sleeptracker.Models.PlayerSleepYML;
import me.cynicalnicole.sleeptracker.Events.SleepTrackerListeners;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nicole
 */
public class SleepTracker extends JavaPlugin {
    private static SleepTracker instance;
    private static World overworld;
    
    /**
     * Enabling the plugin
     */
    @Override
    public void onEnable() {
        //Get instance
        instance = this;
        overworld = getServer().getWorlds().get(0);
        
        //Setup config
        SetupConfiguration();
        
        //Players
        SetupPlayers();
        
        //Check for updates 
        //UpdateCheck();
        
        //Setup Commands
        SetupCommands();
        
        //Add events
        SetupEvents();
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Disabled SleepTracker");
    }
    
    private void SetupConfiguration() {
        //Initialise folders
        File dataFolder = new File(this.getDataFolder(), "");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        
        //defaults
        getConfig().addDefault("messages.errors.unknown-arguments", "&cUnknown command arguments.");
        getConfig().addDefault("messages.errors.timeawake-self", "&cYou do not have permission to check your awake time.");
        getConfig().addDefault("messages.errors.timeawake-others", "&cYou do not have permission to check the awake time of others.");
        getConfig().addDefault("messages.errors.unknown-player", "&cThe specified player does not exist.");
        getConfig().addDefault("messages.errors.player-info", "&cThere was an error fetching the sleep information for {target}.");
        
        getConfig().addDefault("messages.timeawake", "&d{target} has not slept in {awake} days.");
        getConfig().addDefault("messages.timeawake-slept", "&d{target} is well rested.");    
        
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
    
    private void SetupPlayers() {
        //Check to see if players folder exists & make it if it doesn't
        File playerFolder = new File(this.getDataFolder(), "players");
        if (!playerFolder.exists()) {
            playerFolder.mkdir();
        }
        
        ConfigurationSerialization.registerClass(PlayerSleepYML.class, "pState");
    }
    
    private void SetupCommands() {
        new TimeAwake(instance);
    }
    
    private void SetupEvents() {
        new SleepTrackerListeners(instance);
    }
    
    public static SleepTracker getInstance() {
        return instance;
    }
    
    public static World getWorld() {
        return overworld;
    }
    
    public PlayerSleepYML getPlayerSleep(String UUID) {
        //If we're getting it, it should exist
        File file = new File(new File(this.getDataFolder().getAbsolutePath(), "players"), UUID + ".pSleep.yml");
        
        //Oh no
        if (!file.exists()) {
            getLogger().warning("Could not find file: " + file.getAbsolutePath());
            return null;
        }
        
        //Oh yes
        try {
            FileConfiguration playerConf = YamlConfiguration.loadConfiguration(file);
            Object playerState = playerConf.get("playersleepyml");
            if (playerState instanceof PlayerSleepYML) {
                return (PlayerSleepYML) playerState;
            }
        } catch (IllegalArgumentException ex) {
            getLogger().warning(ex.getMessage());
        }
        
        return null;
    }
    
    public PlayerSleepYML createPlayerSleep(String UUID, String PlayerName) {
        //Check if it exists, if it does just return it
        PlayerSleepYML getCheck = getPlayerSleep(UUID);
        
        if (getCheck != null) {
            return getCheck;
        } else {
            PlayerSleepYML newPlayerState = new PlayerSleepYML(UUID, PlayerName);
            newPlayerState.savePlayerFile();
            return newPlayerState;
        }
    }
}
