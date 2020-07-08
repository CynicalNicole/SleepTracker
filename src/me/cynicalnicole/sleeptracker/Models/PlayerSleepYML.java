/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cynicalnicole.sleeptracker.Models;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import me.cynicalnicole.sleeptracker.SleepTracker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

/**
 *
 * @author Nicole
 */
@SerializableAs("pSleep")
public class PlayerSleepYML implements ConfigurationSerializable {
    private String UUID;
    private String playerName;
    private long lastSleptTick;
    
    public PlayerSleepYML(String UUID, String playerName) {
        this.UUID = UUID;
        this.playerName = playerName;
        this.lastSleptTick = 0;
    }
    
    public PlayerSleepYML(Map<String, Object> map) {
        UUID = (String) map.get("UUID");
        playerName = (String) map.get("playerName");        
        lastSleptTick = (int) map.get("lastSleptTick");
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("UUID", UUID);
        map.put("playerName", playerName);
        map.put("lastSleptTick", lastSleptTick);
        
        return map;
    }
    
    public void setPlayerSleep(long sleepTick) {
        this.lastSleptTick = sleepTick;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public long getLastSleepTick() {
        return lastSleptTick;
    }
    
    /**
     * Saves the player file
     * @return true if successful save 
     */
    public boolean savePlayerFile() {       
        File playerFile = new File(new File(SleepTracker.getInstance().getDataFolder().getAbsolutePath(), "players"), UUID + ".pSleep.yml");
        FileConfiguration playerConf = YamlConfiguration.loadConfiguration(playerFile);
        playerConf.set("playersleepyml", this);
        
        try {
            playerConf.save(playerFile);
        } catch (IOException ex) {
            SleepTracker.getInstance().getLogger().warning("Unable to serialize player: " + playerName);
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Deletes the player file
     * @return true if successful deletion 
     */
    public boolean deletePlayerFile() {
        File playerFolder = new File(SleepTracker.getInstance().getDataFolder(), "players");
        if(!playerFolder.exists() || !playerFolder.isDirectory()) return false;
        
        File[] playerFilesList = playerFolder.listFiles(new FileFilter() {
            public boolean accept(File file) { return file.getName().contains(".pSleep.yml"); }
        });
        
        for (File playerFile : playerFilesList) {
            if (playerFile.getName().equals(UUID + ".pSleep.yml")) {
                return playerFile.delete();
            }
        }
        
        return false;
    }
}
