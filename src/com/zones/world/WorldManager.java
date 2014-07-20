package com.zones.world;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.meaglin.json.JSONObject;
import com.zones.Zones;
import com.zones.backwardscompat.OldWorldConfig;
import com.zones.model.settings.ZoneVar;
import com.zones.model.settings.ZoneVarScope;
import com.zones.model.settings.ZoneVarType;
import com.zones.model.types.ZoneNormal;

/**
 * 
 * @author Meaglin
 *
 */
public abstract class WorldManager {
    private WorldConfig             worldConfig;
    
    private World                      world;
    private Zones                      plugin;
    
    public WorldManager(Zones plugin, World world) {
        if(world == null) {
            Zones.log.warning("[Zones] --------------------------");
            Zones.log.warning("[Zones] Trying to create a WorldManager with a NULL world, this should NEVER occur!");
            Zones.log.warning("[Zones] if this error occurs you have either a faulty plugin running or your server is seriously screwed up!");
            Zones.log.warning("[Zones] restarting your server is highly recommended.");
            Zones.log.warning("[Zones] --------------------------");
        }
        this.world = world;
        this.plugin = plugin;
        worldConfig = new WorldConfig(this, plugin.getDataFolder().getPath() + "/" + world.getName() + ".json");
        
        init();
        // ARGGGGGHHHHH allow parents to initialize proper maps and stuff
        //        plugin.getZoneManager().load(this);
    }
    
    public void init() {
        
    }
    
    public void checkCompatiblity() {
        File old = new File(plugin.getDataFolder().getPath() + "/" + world.getName() + ".properties");
        if(old.exists()) {
            OldWorldConfig cfg = new OldWorldConfig(this, plugin.getDataFolder().getPath() + "/" + world.getName() + ".properties");
            cfg.copySettings(worldConfig);
        }
    }
    
    public void load() {
        worldConfig.load();
        plugin.getZoneManager().load(this);
        //ZoneManager.log.info("[Zones]Loaded " + X_REGIONS * Y_REGIONS + " regions.");
    }
    
    public void loadRegions() {
        plugin.getZoneManager().load(this);
    }
    
    public void loadConfig() {
        worldConfig.load();
    }
    public List<ZoneNormal> getAdminZones(Player player)                     { return getAdminZones(player, player.getLocation()); }
    public List<ZoneNormal> getAdminZones(Player player, Location loc)        { return getAdminZones(player, toInt(loc.getX()), toInt(loc.getY()), toInt(loc.getZ()));}
    
    public abstract List<ZoneNormal> getAdminZones(Player player, int x, int y, int z);
    
    public List<ZoneNormal> getActiveZones(Player player)                         {return getActiveZones(player.getLocation());}
    public List<ZoneNormal> getActiveZones(Location loc)                          {return getActiveZones(loc.getX(),loc.getY(),loc.getZ()); } 
    public List<ZoneNormal> getActiveZones(double x,double y,double z)            {return getActiveZones(toInt(x),toInt(y),toInt(z)); }
    public List<ZoneNormal> getActiveZones(Block block)                           {return getActiveZones(block.getX(), block.getY(), block.getZ()); }
    
    public abstract List<ZoneNormal> getActiveZones(int x,int y,int z);
    
    public ZoneNormal getActiveZone(Player player)                                {return getActiveZone(player.getLocation()); }
    public ZoneNormal getActiveZone(Location loc)                                 {return getActiveZone(loc.getX(), loc.getY(), loc.getZ()); }
    public ZoneNormal getActiveZone(double x, double y, double z)                 {return getActiveZone(toInt(x), toInt(y), toInt(z));}
    public ZoneNormal getActiveZone(Block block)                                  {return getActiveZone(block.getX(),block.getY(),block.getZ());}

    public abstract ZoneNormal getActiveZone(int x, int y, int z);
    
    public abstract List<ZoneNormal> getZones(int minx, int maxx, int miny, int maxy, int minz, int maxz);
    
    public abstract void revalidateZones(Player player);
    
    public boolean testFlag(Block loc, ZoneVar var) {
        if(var.getType() != ZoneVarType.BOOLEAN) {
            return false;
        }
        if(!getConfig().isEnabled(var)) {
            return false;
        }
        JSONObject set = getConfig().getSetting(var);
        if(set.getBoolean("enforced") || !var.inScope(ZoneVarScope.LOCAL)) {
            return set.getBoolean("value");
        }
        ZoneNormal zone = this.getActiveZone(loc);
        if(zone == null) {
            return set.getBoolean("value");
        } 
        return zone.hasSetting(var) ? zone.getFlag(var) : set.getBoolean("value");
    }

    public boolean testFlag(Location loc, ZoneVar var) {
        if(var.getType() != ZoneVarType.BOOLEAN) {
            return false;
        }
        if(!getConfig().isEnabled(var)) {
            return false;
        }
        JSONObject set = getConfig().getSetting(var);
        if(set.getBoolean("enforced") || !var.inScope(ZoneVarScope.LOCAL)) {
            return set.getBoolean("value");
        }
        ZoneNormal zone = this.getActiveZone(loc);
        if(zone == null) {
            return set.getBoolean("value");
        } 
        return zone.hasSetting(var) ? zone.getFlag(var) : set.getBoolean("value");
    }
    
    public boolean isProtected(Player player, ZoneVar var, Material mat, boolean onlyEnforce) {
        if(var.getType() != ZoneVarType.MATERIALLIST) {
            return false;
        }
        if(!getConfig().isEnabled(var)) {
            return false;
        }
        JSONObject set = getConfig().getSetting(var);
        boolean prot = false;
        if(onlyEnforce) {
            if(set.getBoolean("enforced")) {
                prot = set.getJSONArray("value").contains(mat.name());
            }
        } else {
            prot = set.getJSONArray("value").contains(mat.name());
        }
        if(prot && getPlugin().hasPermission(player, "zones.override." + var.getName())) {
            prot = false;
        }
        return prot;
    }
    
    // TODO: create optimized version with parent flag.
    public boolean isProtected(Block block, ZoneVar var, Material mat) {
        if(var.getType() != ZoneVarType.MATERIALLIST) {
            return false;
        }
        
        if(!getConfig().isEnabled(var)) {
            ZoneNormal zone = getActiveZone(block);
            if(zone != null && zone.getSettings().has(var.getName())) {
                return zone.getSettings().getJSONArray(var.getName()).contains(mat.name());
            }
            return false;
        } else {
            JSONObject set = getConfig().getSetting(var);
            if(set.getBoolean("enforced")) {
                return set.getJSONArray("value").contains(mat.name());
            } else {
                ZoneNormal zone = getActiveZone(block);
                if(zone != null && zone.getSettings().has(var.getName())) {
                    return zone.getSettings().getJSONArray(var.getName()).contains(mat.name());
                } else {
                    return set.getJSONArray("value").contains(mat.name());
                }
            }
        }
    }
    
    public boolean canReceiveDamage(Player player, ZoneVar sub) {
        ZoneNormal zone = getActiveZone(player);
        if(zone == null) {
            if(!getConfig().getFlag(ZoneVar.HEALTH)) {
                return false;
            }
            return getConfig().getFlag(sub);
        } else {
            if(getConfig().isEnforced(ZoneVar.HEALTH)) {
                if(!getConfig().getFlag(ZoneVar.HEALTH)) {
                    return false;
                }
                return getConfig().getFlag(sub);
            }
            if(zone.getSettings().has(ZoneVar.HEALTH.getName())
                    && !zone.getFlag(ZoneVar.HEALTH)) {
                return false;
            }
            if(getConfig().isEnforced(sub)) {
                return getConfig().getFlag(sub);
            }
            return zone.hasSetting(sub) ? zone.getFlag(sub) : getConfig().getFlag(sub);
        }
    }
    
    public boolean canSpawn(Location loc, ZoneVar flag, ZoneVar list, EntityType type) {
        ZoneNormal zone = getActiveZone(loc);
        if(zone == null) {
            if(!getConfig().getFlag(flag)) {
                return false;
            }
            if(!getConfig().isEnabled(list)) {
                return true;
            }
            return getConfig().getSetting(list).getJSONArray("value").contains(type.name());
        } else {
            if(getConfig().isEnforced(flag)) {
                if(!getConfig().getFlag(flag)) {
                    return false;
                }
                if(!getConfig().isEnabled(list)) {
                    return true;
                }
                return getConfig().getSetting(list).getJSONArray("value").contains(type.name());
            }
            if(getConfig().isEnabled(list) && getConfig().isEnforced(list)) {
                return getConfig().getSetting(list).getJSONArray("value").contains(type.name());
            }
            if(zone.hasSetting(flag) && !zone.getFlag(flag)) {
                return false;
            }
            if(zone.hasSetting(list)) {
                return zone.getSettings().getJSONArray(list.getName()).contains(type.name());
            }
            if(!getConfig().getFlag(flag)) {
                return false;
            }
            if(!getConfig().isEnabled(list)) {
                return true;
            }
            return getConfig().getSetting(list).getJSONArray("value").contains(type.name());
        }
    }
    
    public boolean getFlag(ZoneVar var) {
        if(var.getType() != ZoneVarType.BOOLEAN) {
            return false;
        }
        JSONObject set = getConfig().getSetting(var);
        return set.getBoolean("value");
    }
    
    public abstract void addZone(ZoneNormal zone);
    public abstract void addZones(List<ZoneNormal> zone);
    public abstract void revalidateZones(Player player, Location from, Location to);
    public abstract void revalidateOutZones(Player player, Location from);
    public abstract void removeZone(ZoneNormal toDelete);
    
    public String getWorldName() { return getWorld().getName(); }
    public World getWorld() {
        return world;
    }
    
    public Zones getPlugin() {
        return plugin;
    }
    
    public WorldConfig getConfig() {
        return worldConfig;
    }
    
    public static final int toInt(double b) {
        int r = (int) b;
        return b < r ? r - 1 : r;
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof WorldManager)) {
            return false;
        }
        return ((WorldManager)o).getWorld().getName().equals(getWorld().getName());
    }
    
    @Override
    public int hashCode() {
        return getWorldName().hashCode();
    }

}
