package com.zones.util;

import java.io.File;
import java.io.IOException;

import com.zones.util.properties.ExtendedProperties;

public class PropertiesTest {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        ExtendedProperties zp = new ExtendedProperties(new File("Zones/src/com/zones/config/Zones2.properties"));
        zp.load();
        zp.getBool("EnableWorldEdit", false);
        zp.getBool("EnableTextureManager", false);
        
        zp.getBool("RestoreMissingProperties", true);
        
        zp.getProperty("DefaultEnterMessage", "You have just entered zone {zname}[{acces}].");
        zp.getProperty("DefaultLeaveMessage", "You have just exited zone {zname}.");
        
        zp.getInt("CreationToolType", 280);
        zp.getInt("CreationPilonType", 3);
        zp.getInt("CreationPilonHeight", 4);
        
        zp.getInt("DatabaseVersion", 0);
        
        zp.getProperty("DatabaseVersion").setValue(3);
        zp.save(true);
        if(zp.isMissingProperties()) {
            ExtendedProperties props = new ExtendedProperties(zp.getClass().getResourceAsStream("/com/zones/config/Zones.properties"));
            props.load();
            System.out.println(zp.getClass().getResourceAsStream("/com/zones/config/Zones.properties"));
            zp.restore(props);
            zp.save(true);
        }
        //        System.out.println(zp.isMissingProperties());
        System.out.println(zp.isMissingProperties());
    }

}
