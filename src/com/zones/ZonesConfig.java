package com.zones;

import java.io.File;
import java.util.logging.Logger;

import com.zones.util.Properties;

public class ZonesConfig {
    public static final String ZONES_CONFIG_FILE = "./plugins/zones/Zones.properties";

    private static Logger      log               = Logger.getLogger(ZonesConfig.class.getName());

    public static String       DATABASE_URL;
    public static String       DATABASE_LOGIN;
    public static String       DATABASE_PASSWORD;

    public static String       ZONES_TABLE;
    public static String       ZONES_VERTICES_TABLE;

    public static boolean      FIRE_ENABLED;
    public static boolean      HEALTH_ENABLED;
    public static boolean      MOBS_ENABLED;
    public static boolean      ANIMALS_ENABLED;

    public static boolean      LIMIT_BY_BUILD_ENABLED;
    public static boolean      FALL_DAMAGE_ENABLED;
    public static boolean      TNT_ENABLED;

    public static void load() {
        try {
            Properties zp = new Properties(new File(ZONES_CONFIG_FILE));
            DATABASE_URL = zp.getProperty("URL", "jdbc:mysql://localhost/Minecraft");
            DATABASE_LOGIN = zp.getProperty("Login", "root");
            DATABASE_PASSWORD = zp.getProperty("Password", "");

            ZONES_TABLE = zp.getProperty("ZonesTable", "zones");
            ZONES_VERTICES_TABLE = zp.getProperty("ZonesVerticesTable", "zones_vertices");

            FIRE_ENABLED = zp.getBool("FireEnabled", false);
            HEALTH_ENABLED = zp.getBool("HealthEnabled", false);
            MOBS_ENABLED = zp.getBool("MobsEnabled", false);
            ANIMALS_ENABLED = zp.getBool("AnimalsEnabled", true);
            LIMIT_BY_BUILD_ENABLED = zp.getBool("ManageWorldBuild", true);
            FALL_DAMAGE_ENABLED = zp.getBool("FallDamageEnabled", false);
            TNT_ENABLED = zp.getBool("TntEnabled", false);
        } catch (Exception e) {
            log.warning("[Zones]Error loading configurations.");
            e.printStackTrace();
        }
    }
}
