package com.zones.world;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Location;

import com.meaglin.json.JSONObject;
import com.zones.model.settings.ZoneVar;
import com.zones.model.settings.ZoneVarScope;
import com.zones.model.settings.ZoneVarType;
import com.zones.util.FileUtil;

public class WorldConfig {
    private String filename;
    @SuppressWarnings("unused")
    private WorldManager manager;

    protected static final Logger      log             = Logger.getLogger("Minecraft");
    
    private JSONObject config;
    private JSONObject settings;
    
    public WorldConfig(WorldManager manager,String filename) {
        this.manager = manager;
        this.filename = filename;
        
        load();
        
        if(!getFlag(ZoneVar.THUNDER) && manager.getWorld().isThundering()) {
            manager.getWorld().setThundering(false);
        }
        
        if(!getFlag(ZoneVar.RAIN) && manager.getWorld().hasStorm()) {
            manager.getWorld().setStorm(false);
        }
    }
    
    public void load() {
        File worldConfigFile = new File(filename);
        if(!worldConfigFile.exists()) {
            FileUtil.writeFile(worldConfigFile, "{"
                + "version: 1,"
                + "settings: {}"
            + "}");
        }
        config = new JSONObject(FileUtil.readFile(worldConfigFile));
        settings = config.getJSONObject("settings");
        
        checkDefaults();
    }
    
    public void checkDefaults() {
        boolean changed = false;
        for(ZoneVar var : ZoneVar.values()) {
            if(!var.inScope(ZoneVarScope.WORLD)) {
                continue;
            }
            if(settings.has(var.getName())) {
                continue;
            }
            if(!var.inScope(ZoneVarScope.LOCAL)) {
                if(var.getType() == ZoneVarType.BOOLEAN) {
                    settings.put(var.getName(), (new JSONObject())
                            .put("value", var.getDefault()));
                } else {
                    settings.put(var.getName(), (new JSONObject())
                            .put("enabled", false)
                            .put("value", var.getDefault()));
                }
                continue;
            }
            if(var.getType() == ZoneVarType.BOOLEAN) {
                settings.put(var.getName(), (new JSONObject())
                        .put("enforced", false)
                        .put("value", var.getDefault()));
            } else {
                settings.put(var.getName(), (new JSONObject())
                        .put("enabled", false)
                        .put("enforced", false)
                        .put("value", var.getDefault()));
            }
            changed = true;
        }
        if(changed) {
            save();
        }
    }
    
    public boolean isEnabled(ZoneVar var) {
        if(!var.inScope(ZoneVarScope.WORLD)) {
            return false;
        }
        if(var.getType() == ZoneVarType.BOOLEAN) {
            return true;
        }
        return settings.getJSONObject(var.getName()).getBoolean("enabled");
    }
    
    public boolean isEnforced(ZoneVar var) {
        if(!var.inScope(ZoneVarScope.WORLD)) {
            return false;
        }
        return settings.getJSONObject(var.getName()).getBoolean("enforced");
    }
    
    public JSONObject getSetting(ZoneVar var) {
        return settings.getJSONObject(var.getName());
    }
    
    public boolean getFlag(ZoneVar var) {
        if(!var.inScope(ZoneVarScope.WORLD)) {
            return false;
        }
        return settings.getJSONObject(var.getName()).getBoolean("value");
    }
    
    public boolean getFlagEnabledEnforced(ZoneVar var) {
        if(!var.inScope(ZoneVarScope.WORLD)) {
            return false;
        }
        JSONObject obj = settings.getJSONObject(var.getName());
        if(!obj.getBoolean("enabled")) {
            return (boolean) var.getDefault();
        }
        if(!obj.getBoolean("enforced")) {
            return (boolean) var.getDefault();
        }
        return obj.getBoolean("value");
    }

    public boolean getFlagEnabled(ZoneVar var) {
        if(!var.inScope(ZoneVarScope.WORLD)) {
            return false;
        }
        JSONObject obj = settings.getJSONObject(var.getName());
        if(!obj.getBoolean("enabled")) {
            return (boolean) var.getDefault();
        }
        return obj.getBoolean("value");
    }
    
    public void setFlag(ZoneVar var, boolean set) {
        if(!var.inScope(ZoneVarScope.WORLD)) {
            return;
        }
        settings.getJSONObject(var.getName()).put("value", set);
    }
    
    public void setEnabled(ZoneVar var, boolean enabled) {
        if(!var.inScope(ZoneVarScope.WORLD)) {
            return;
        }
        settings.getJSONObject(var.getName()).put("enabled", enabled);
    }
    
    public void setEnforced(ZoneVar var, boolean set) {
        if(!var.inScope(ZoneVarScope.WORLD)) {
            return;
        }
        settings.getJSONObject(var.getName()).put("enforced", set);
    }
    
    public void setValue(ZoneVar var, Object obj) {
        if(!var.inScope(ZoneVarScope.WORLD)) {
            return;
        }
        settings.getJSONObject(var.getName()).put("value", obj);
    }
    
    public boolean isOutsideBorder(Location loc) {
        int x = 0;
        int z = 0;
        int locx = WorldManager.toInt(loc.getX());
        int locz = WorldManager.toInt(loc.getZ());
        int borderRange = getSetting(ZoneVar.BORDER_RANGE).getInt("value");
        if(!getFlag(ZoneVar.BORDER_USE_SPAWN)) {
            JSONObject obj = getSetting(ZoneVar.BORDER_ALTERNATIVE_CENTER).getJSONObject("value");
            x = (int) obj.getDouble("x");
            z = (int) obj.getDouble("x");
        } else {
            Location spawn = loc.getWorld().getSpawnLocation();
            x = WorldManager.toInt(spawn.getX());
            z = WorldManager.toInt(spawn.getZ());
        }
        switch (getSetting(ZoneVar.BORDER_SHAPE).getString("value")) {
            case "CIRCULAIR":
                int xdistance = x - locx;
                int zdistance = z - locz;
                double range = StrictMath.sqrt(xdistance * xdistance + zdistance * zdistance);
                if(range > borderRange)
                    return true;
                
                return false;
            default:
                if(locz > (z+borderRange) || locz < (z-borderRange))
                    return true;
                if(locx > (x+borderRange) || locx < (x-borderRange))
                    return true;
                return false;
        }
    }
    
    public void save() {
        File worldConfigFile = new File(filename);
        FileUtil.writeFile(worldConfigFile, config.toString(2));
    }
}
