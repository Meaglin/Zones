package com.zones;

import java.io.File;
import java.util.logging.Logger;

import com.zones.util.Properties;

/**
 * 
 * @author Meaglin
 *
 */
public class ZonesConfig {
    public static final String ZONES_CONFIG_FILE = "Zones.properties";

    private static Logger      log                         = Logger.getLogger(ZonesConfig.class.getName());

    public static String       DATABASE_URL;
    public static String       DATABASE_LOGIN;
    public static String       DATABASE_PASSWORD;
    public static boolean      WORLDEDIT_ENABLED;

    public static String       ZONES_TABLE;
    public static String       ZONES_VERTICES_TABLE;
    
    public static String       DEFAULT_ENTER_MESSAGE;
    public static String       DEFAULT_LEAVE_MESSAGE;
    
    public static int          CREATION_TOOL_TYPE;
    public static int          CREATION_PILON_TYPE;
    public static int          CREATION_PILON_HEIGHT;
    
    public static void load(File f) {
        try {
            Properties zp = new Properties(f);
            DATABASE_URL = zp.getProperty("URL", "jdbc:mysql://localhost/Minecraft");
            DATABASE_LOGIN = zp.getProperty("Login", "root");
            DATABASE_PASSWORD = zp.getProperty("Password", "");
            WORLDEDIT_ENABLED = zp.getBool("EnableWorldEdit", false);

            ZONES_TABLE = zp.getProperty("ZonesTable", "zones");
            ZONES_VERTICES_TABLE = zp.getProperty("ZonesVerticesTable", "zones_vertices");
            
            DEFAULT_ENTER_MESSAGE = zp.getProperty("DefaultEnterMessage", "You have just entered {zname}[{acces}].");
            DEFAULT_LEAVE_MESSAGE = zp.getProperty("DefaultLeaveMessage", "You have just exited {zname}.");
            
            CREATION_TOOL_TYPE      = zp.getInt("CreationToolType", 280);
            CREATION_PILON_TYPE     = zp.getInt("CreationPilonType", 80);
            CREATION_PILON_HEIGHT   = zp.getInt("CreationPilonHeight", 4);
        } catch (Exception e) {
            log.warning("[Zones]Error loading configurations.");
            e.printStackTrace();
        }
    }
}
