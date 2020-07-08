/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cynicalnicole.sleeptracker;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

/**
 *
 * @author Nicole
 */
public class MessageFormatting {
    private static final Pattern REPLACE_ALL_PATTERN = Pattern.compile("(&)?&([0-9a-fk-orA-FK-OR])");
    
    private static final Set<ChatColor> COLORS = EnumSet.of(ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE);
    private static final Set<ChatColor> FORMATS = EnumSet.of(ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.RESET);
    private static final Set<ChatColor> MAGIC = EnumSet.of(ChatColor.MAGIC);
    
    public static enum PlaceholderType {
        PLAYER,
        WORLD,
        AWAKE_TIME
    }
    
    public static String getMessageString(String messageStringName) {
        return getMessageString(messageStringName, null);
    }
    
    public static String getMessageString(String messageStringName, Map<PlaceholderType, String> variables) {
        String baseString = SleepTracker.getInstance().getConfig().getString(messageStringName);
        if (baseString == null) {
            SleepTracker.getInstance().getLogger().severe(String.format("The config value \"%s\" was not found.", messageStringName));
            return "";
        }
        
        baseString = replaceColours(baseString);
        
        if (variables != null) {
            for (Map.Entry<PlaceholderType, String> variable : variables.entrySet()) {
                String replaceRegex = null;
                switch(variable.getKey()) {
                    case PLAYER:
                        replaceRegex = "(?i)\\{target\\}";
                        break;
                    case WORLD:
                        replaceRegex = "(?i)\\{world\\}";
                        break;
                    case AWAKE_TIME:
                        replaceRegex = "(?i)\\{awake\\}";
                        break;
                }

                if (replaceRegex != null) {
                    baseString = baseString.replaceAll(replaceRegex, variable.getValue());
                }            
            }
        }

        return baseString;
    }
    
    private static String replaceColours(String message) {
        StringBuffer legacyBuilder = new StringBuffer();
        Matcher legacyMatcher = REPLACE_ALL_PATTERN.matcher(message);
        legacyLoop: while (legacyMatcher.find()) {
            boolean isEscaped = (legacyMatcher.group(1) != null);
            if (!isEscaped) {
                char code = legacyMatcher.group(2).toLowerCase(Locale.ROOT).charAt(0);
                for (ChatColor color : EnumSet.allOf(ChatColor.class)) {
                    if (color.getChar() == code) {
                        legacyMatcher.appendReplacement(legacyBuilder, "\u00a7$2");
                        continue legacyLoop;
                    }
                }
            }
            // Don't change & to section sign (or replace two &'s with one)
            legacyMatcher.appendReplacement(legacyBuilder, "&$2");
        }
        legacyMatcher.appendTail(legacyBuilder);
        return legacyBuilder.toString();
    }
}

