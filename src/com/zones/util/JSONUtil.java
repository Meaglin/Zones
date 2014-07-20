package com.zones.util;

import org.bukkit.Location;

import com.meaglin.json.JSONObject;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class JSONUtil {

    public static Location getLocation(ZoneBase base, ZoneVar var) {
        JSONObject loc = base.getSettings().getJSONObject(var.getName());
        return new Location(base.getWorld(), 
                loc.getDouble("x"),
                loc.getDouble("y"),
                loc.getDouble("z"),
                loc.getFloat("yaw"),
                loc.getFloat("pitch")
            );
    }
    
    public static JSONObject saveLocation(Location loc) {
        return (new JSONObject())
                .put("world", loc.getWorld().getName()) // Not used in recovering.
                .put("x", loc.getX())
                .put("y", loc.getY())
                .put("z", loc.getZ())
                .put("yaw", loc.getYaw())
                .put("pitch", loc.getPitch());
    }
}
