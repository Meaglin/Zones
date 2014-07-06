package com.zones.util;

import org.bukkit.Material;

public class BlockUtil {

    
    public static boolean isContainer(Material mat) {
        switch(mat) {
            case CHEST:
            case FURNACE:
            case BURNING_FURNACE:
            case DISPENSER:
            case DROPPER:
            case HOPPER:
            case TRAPPED_CHEST:
            case BREWING_STAND:
            case JUKEBOX:
                return true;
        }
        return false;
    }
}
