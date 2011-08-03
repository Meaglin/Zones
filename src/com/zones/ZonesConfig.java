package com.zones;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import com.zones.util.Properties;

/**
 * 
 * @author Meaglin
 *
 */
public class ZonesConfig {
    public static final String ZONES_CONFIG_FILE = "Zones.properties";

    private static Logger      log                         = Logger.getLogger(ZonesConfig.class.getName());

    public static boolean      WORLDEDIT_ENABLED;
    
    public static String       DEFAULT_ENTER_MESSAGE;
    public static String       DEFAULT_LEAVE_MESSAGE;
    
    public static int          CREATION_TOOL_TYPE;
    public static int          CREATION_PILON_TYPE;
    public static int          CREATION_PILON_HEIGHT;

    
    public static final String PLAYER_CANT_BUILD_BLOCKS_IN_ZONE = ChatColor.RED + "You cannot place blocks in '{zname}' !";
    public static final String PLAYER_CANT_MODIFY_BLOCKS_IN_ZONE = ChatColor.RED + "You cannot change blocks in '{zname}' !";
    public static final String PLAYER_CANT_DESTROY_BLOCKS_IN_ZONE = ChatColor.RED + "You cannot destroy blocks in '{zname}' !";
    public static final String PLAYER_CANT_HIT_BLOCKS_IN_ZONE = ChatColor.RED + "You cannot trigger blocks in '{zname}' !";
    public static final String PLAYER_CANT_HIT_ENTITYS_IN_ZONE = ChatColor.RED + "You cannot hit entities in '{zname}' !";
    
    public static final String PLAYER_CANT_PLACE_CHEST_IN_ZONE = ChatColor.RED + "You cannot place Chests/Furnaces in '{zname}' !";
    public static final String PLAYER_CANT_DESTROY_CHEST_IN_ZONE = ChatColor.RED + "You cannot destroy Chests/Furnaces in '{zname}' !";
    public static final String PLAYER_CANT_USE_LIGHTER = ChatColor.RED + "You cannot use lighters in '{zname}' !";
    
    public static final String PLAYER_CANT_ENTER_INTO_ZONE = ChatColor.RED + "You can't enter '{zname}' !";
    public static final String PLAYER_CANT_TELEPORT_INTO_ZONE = ChatColor.RED + "You cannot warp into '{zname}', since it is a protected area !";
    public static final String TELEPORT_INTO_ZONE_DISABLED = ChatColor.RED + "You cannot warp into '{zname}' because it has teleporting disabled !";
    
    public static final String PLAYER_ILLIGAL_POSITION = ChatColor.RED + "You were moved to spawn because you were in an illigal position !";
    
    public static final String BLOCK_IS_BLACKLISTED = ChatColor.RED + "This block type is protected in '{zname}' !";
    public static final String BLOCK_IS_PROTECTED = ChatColor.RED + "This block type is blacklisted in '{zname}' !";

    public static void load(File f) {
        try {
            Properties zp = new Properties(f);
            WORLDEDIT_ENABLED = zp.getBool("EnableWorldEdit", false);
            
            DEFAULT_ENTER_MESSAGE = zp.getProperty("DefaultEnterMessage", "You have just entered {zname}[{acces}].");
            DEFAULT_LEAVE_MESSAGE = zp.getProperty("DefaultLeaveMessage", "You have just exited {zname}.");
            
            CREATION_TOOL_TYPE      = zp.getInt("CreationToolType", 280);
            CREATION_PILON_TYPE     = zp.getInt("CreationPilonType", 3);
            CREATION_PILON_HEIGHT   = zp.getInt("CreationPilonHeight", 4);
        } catch (Exception e) {
            log.warning("[Zones]Error loading configurations.");
            e.printStackTrace();
        }
    }
}
