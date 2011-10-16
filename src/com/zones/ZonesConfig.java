package com.zones;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import com.zones.util.properties.ExtendedProperties;
import com.zones.util.properties.Properties;

/**
 * 
 * @author Meaglin
 *
 */
public class ZonesConfig {
    public static final String ZONES_CONFIG_FILE = "Zones.properties";

    private static Logger      log                         = Logger.getLogger(ZonesConfig.class.getName());

    public static boolean      WORLDEDIT_ENABLED;
    
    public static boolean      RESTORE_MISSING_PROPERTIES;
    
    public static String       DEFAULT_ENTER_MESSAGE;
    public static String       DEFAULT_LEAVE_MESSAGE;
    
    public static int          CREATION_TOOL_TYPE;
    public static int          CREATION_PILON_TYPE;
    public static int          CREATION_PILON_HEIGHT;

    
    public static final String PLAYER_CANT_BUILD_BLOCKS_IN_ZONE = ChatColor.RED + "You cannot place blocks in zone '{zname}' !";
    public static final String PLAYER_CANT_MODIFY_BLOCKS_IN_ZONE = ChatColor.RED + "You cannot change blocks in zone '{zname}' !";
    public static final String PLAYER_CANT_DESTROY_BLOCKS_IN_ZONE = ChatColor.RED + "You cannot destroy blocks in zone '{zname}' !";
    public static final String PLAYER_CANT_HIT_BLOCKS_IN_ZONE = ChatColor.RED + "You cannot trigger blocks in zone '{zname}' !";
    public static final String PLAYER_CANT_HIT_ENTITYS_IN_ZONE = ChatColor.RED + "You cannot hit entities in zone '{zname}' !";
    public static final String PLAYER_CANT_PICKUP_ITEMS_IN_ZONE = ChatColor.RED + "You're not allowed to drop items in zone '{zname}'!";
    public static final String PLAYER_CANT_DROP_ITEMS_IN_ZONE = ChatColor.RED + "You're not allowed to pickup items in '{zname}'!";
    
    public static final String PLAYER_CANT_PLACE_CHEST_IN_ZONE = ChatColor.RED + "You cannot place Chests/Furnaces in zone'{zname}' !";
    public static final String PLAYER_CANT_DESTROY_CHEST_IN_ZONE = ChatColor.RED + "You cannot destroy Chests/Furnaces in zone'{zname}' !";
    public static final String PLAYER_CANT_USE_LIGHTER = ChatColor.RED + "You cannot use lighters in zone '{zname}' !";
    
    public static final String PLAYER_CANT_ENTER_INTO_ZONE = ChatColor.RED + "You can't enter zone '{zname}' !";
    public static final String PLAYER_CANT_TELEPORT_INTO_ZONE = ChatColor.RED + "You cannot warp into zone '{zname}', since it is a protected area !";
    public static final String TELEPORT_INTO_ZONE_DISABLED = ChatColor.RED + "You cannot warp into zone '{zname}' because it has teleporting disabled !";
    
    public static final String PLAYER_CANT_BUILD_WORLD = ChatColor.RED + "You cannot build blocks in this world !";
    public static final String PLAYER_CANT_CHANGE_WORLD = ChatColor.RED + "You cannot change blocks in this world !";
    public static final String PLAYER_CANT_DESTROY_WORLD = ChatColor.RED + "You cannot destroy blocks in this world !";
    
    public static final String PLAYER_REACHED_BORDER = ChatColor.RED + "You have reached the border.";
    public static final String PLAYER_CANT_WARP_OUTSIDE_BORDER = ChatColor.RED + "You cannot warp outside the border.";
    public static final String PLAYER_ILLIGAL_POSITION = ChatColor.RED + "You were moved to spawn because you were in an illigal position !";
    
    public static final String BLOCK_IS_BLACKLISTED = ChatColor.RED + "This block type is protected in zone '{zname}' !";
    public static final String BLOCK_IS_PROTECTED = ChatColor.RED + "This block type is blacklisted in zone '{zname}' !";

    public static final String ZONE_ALREADY_CLAIMED = ChatColor.RED + "Zone {zname} is already claimed.";
    public static final String PLAYER_CLAIMES_ZONES = ChatColor.GREEN + "You are now the proud owner of zone {zname}!";
    public static final String PLAYER_ALREADY_CLAIMED_ZONE = ChatColor.RED + "You have already claimed a zone in zone {zname}!";
    public static final String PLAYER_CAN_DIE_IN_ZONE = ChatColor.RED + "WARNING: you can die in this zone!";
    
    public static final String PLAYER_ENTERED_ZONE = ChatColor.YELLOW + "Player {pname}" + ChatColor.YELLOW + " has entered zone {zname}.";
    public static final String PLAYER_LEFT_ZONE = ChatColor.YELLOW + "Player {pname}" + ChatColor.YELLOW + " has left zone {zname}.";
    
    private static final Properties defaultproperties = new Properties(Zones.class.getResourceAsStream("/com/zones/config/Zones.properties"));
    
    public static void load(File f) {
        try {
            ExtendedProperties zp = new ExtendedProperties(f);
            zp.load();
            WORLDEDIT_ENABLED = zp.getBool("EnableWorldEdit", false);
            
            RESTORE_MISSING_PROPERTIES = zp.getBool("RestoreMissingProperties", true);
            
            DEFAULT_ENTER_MESSAGE = zp.getProperty("DefaultEnterMessage", "You have just entered zone {zname}[{acces}].");
            DEFAULT_LEAVE_MESSAGE = zp.getProperty("DefaultLeaveMessage", "You have just exited zone {zname}.");
            
            CREATION_TOOL_TYPE      = zp.getInt("CreationToolType", 280);
            CREATION_PILON_TYPE     = zp.getInt("CreationPilonType", 3);
            CREATION_PILON_HEIGHT   = zp.getInt("CreationPilonHeight", 4);
            if(zp.isMissingProperties() && RESTORE_MISSING_PROPERTIES) {
                int count = zp.restore(defaultproperties);
                zp.save(true);
                log.info("[Zones] Restored " + count + " missing properties in " + zp.getFile().getName() + "!");
            }
        } catch (Exception e) {
            log.warning("[Zones]Error loading configurations.");
            e.printStackTrace();
        }
    }
}
