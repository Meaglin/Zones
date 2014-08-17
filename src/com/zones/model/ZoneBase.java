package com.zones.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.meaglin.json.JSONArray;
import com.meaglin.json.JSONObject;
import com.zones.Zones;
import com.zones.backwardscompat.OldZoneVar;
import com.zones.model.forms.ZoneCuboid;
import com.zones.model.forms.ZoneCylinder;
import com.zones.model.forms.ZoneNPoly;
import com.zones.model.forms.ZoneSphere;
import com.zones.model.settings.ZoneVar;
import com.zones.model.settings.ZoneVarType;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;
import com.zones.util.Point;
import com.zones.world.WorldConfig;
import com.zones.world.WorldManager;

/**
 * Abstract base class for any zone type Handles basic operations
 * 
 * @author Meaglin
 */
public abstract class ZoneBase {
    protected static final Logger     log = Logger.getLogger(ZoneBase.class.getName());
    private static final int CONFIG_VERSION = 3;
    
    private int                 id;
    protected ZoneForm                form;
    protected HashMap<Integer, Player> playerList;

    private String                    name;

    protected Zones                   zones;
    protected WorldManager            worldManager;
    
    protected Zone                      persistence;
    
    private boolean                   initialized;
    
    protected ZoneBase() {
        playerList = new HashMap<Integer, Player>();
    }
    
    public final void initialize(Zones plugin, WorldManager worldManager, Zone persistence) {
        if(!initialized) {
            initialized = true;
            this.zones = plugin;
            this.worldManager = worldManager;
            this.persistence = persistence;
            id = persistence.getId();
            onLoad(persistence);
        }
    }
    
    public boolean loadForm() {
        Zone zone = getPersistence();
        List<Vertice> vertices = getPersistence().getVertices();
        
        if(zone.getFormtype().equalsIgnoreCase("ZoneCuboid")) {
            if (vertices.size() == 2) {
                setForm(new ZoneCuboid(vertices, zone.getMiny(), zone.getMaxy()));
            } else {
                log.info("[Zones] Missing zone vertex for cuboid zone id: " + zone.getId());
                return false;
            }
        } else if(zone.getFormtype().equalsIgnoreCase("ZoneNPoly")) {
            if (vertices.size() > 2) {
                setForm(new ZoneNPoly(vertices, zone.getMiny() , zone.getMaxy()));
            } else {
                log.warning("[Zones] Bad data for zone: " + zone.getId());
                return false;
            }
        } else if(zone.getFormtype().equalsIgnoreCase("ZoneCylinder")) {
            if (vertices.size() == 2) {
                setForm(new ZoneCylinder(vertices, zone.getMiny(), zone.getMaxy()));
            } else {
                log.info("[Zones] Missing zone vertex for Cylinder zone id: " + zone.getId());
                return false;
            }
        } else if(zone.getFormtype().equalsIgnoreCase("ZoneSphere")) {
            if (vertices.size() == 1) {
                setForm(new ZoneSphere(vertices, zone.getMiny(), zone.getMaxy()));
            } else {
                log.info("[Zones] Missing zone vertex for Sphere zone id: " + zone.getId());
                return false;
            }
        } else {
            log.info("[Zones] Unknown zoneForm " + zone.getFormtype() + " zone id: " + zone.getId());
            return false;
        }
        return true;
    }
    
    protected void onLoad(Zone persistence) {
        name = persistence.getName();
        JSONObject cfg = getConfig();
        checkVersion(cfg);
    }
    
    private void checkVersion(JSONObject cfg) {
        if(cfg.getInt("version") == CONFIG_VERSION) {
            return;
        }
        int ver = cfg.getInt("version");
        if(ver < 1) {
            upgradeFrom0To1(cfg); 
        }
        if(ver < 2) {
            upgradeFrom1To2(cfg); 
        }
        if(ver < 3) {
            upgradeFrom2To3(cfg);
        }
        cfg.put("version", CONFIG_VERSION);
        saveSettings();
    }
    
    protected void upgradeFrom2To3(JSONObject cfg) {
        persistence.setAdmins(null);
        persistence.setUsers(null);
        persistence.setSettings(null);
    }

    protected void upgradeFrom0To1(JSONObject cfg) {
        ZoneSettings settings;
        if(persistence.getSettings() != null && !persistence.getSettings().trim().equals("")) {
            try {
                settings = ZoneSettings.unserialize(persistence.getSettings());
            } catch(Exception e) {
                log.warning("[Zones]Error loading settings of " + name + "[" + id + "]");
                e.printStackTrace();
                return;
            }
            JSONObject set = getConfig().getJSONObject("settings");
            for(Entry<OldZoneVar, Object> obj : settings.getMap().entrySet()) {
                if(obj.getValue() == null) {
                    continue;
                }
                switch(obj.getKey().getSerializer()) {
                    case ZONEVERTICE:
                        ZoneVertice vert = (ZoneVertice) obj.getValue();
                        set.put(obj.getKey().getName(), ((new JSONObject()).put("x", vert.getX()).put("y", vert.getZ())));
                        break;
                    case INTEGERLIST:
                        @SuppressWarnings("unchecked")
                        List<Integer> list = (List<Integer>) obj.getValue();
                        JSONArray arr = new JSONArray();
                        for(int type : list) {
                            arr.add(type);
                        }
                        set.put(obj.getKey().getName(), arr);
                        break;
                    case ENTITYLIST:
                        @SuppressWarnings("unchecked")
                        List<EntityType> elist = (List<EntityType>) obj.getValue();
                        JSONArray earr = new JSONArray();
                        for(EntityType type : elist) {
                            earr.add(type.name());
                        }
                        set.put(obj.getKey().getName(), earr);
                        break;
                    case LOCATION:
                        Point pnt = (Point) obj.getValue();
                        set.put(obj.getKey().getName(), ((new JSONObject())
                                .put("x", pnt.getX())
                                .put("y", pnt.getY())
                                .put("z", pnt.getZ())
                                .put("yaw", 0)
                                .put("pitch", 0)
                            ));
                        break;
                    default:
                        set.put(obj.getKey().getName(), obj.getValue());
                        break;
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    protected void upgradeFrom1To2(JSONObject cfg) {
        JSONObject set = getConfig().getJSONObject("settings");
        if(set.has("ProtectedPlaceBlocks")) {
            JSONArray arr = set.getJSONArray("ProtectedPlaceBlocks");
            JSONArray mats = new JSONArray();
            for(Object o : arr) {
                mats.add(Material.getMaterial((int) o).name());
            }
            set.remove("ProtectedPlaceBlocks");
            set.put("ProtectedPlaceMaterials", mats);
        }
        if(set.has("ProtectedBreakBlocks")) {
            JSONArray arr = set.getJSONArray("ProtectedBreakBlocks");
            JSONArray mats = new JSONArray();
            for(Object o : arr) {
                mats.add(Material.getMaterial((int) o).name());
            }
            set.remove("ProtectedBreakBlocks");
            set.put("ProtectedBreakMaterials", mats);
        }
    }
    
    public JSONObject getConfig() {
        return persistence.getConfig();
    }
    
    public JSONObject getSettings() {
        return getConfig().getJSONObject("settings");
    }
    
    /**
     * @return Returns the id.
     */
    public final int getId() {
        return id;
    }

    public final String getName() {
        return name;
    }
    
    public final Zone getPersistence() {
        return persistence;
    }

    public final WorldManager getWorldManager() {
        return worldManager;
    }
    
    public final WorldConfig getWorldConfig() {
        return getWorldManager().getConfig();
    }
    
    public final World getWorld() {
        return getWorldManager().getWorld();
    }
    
    public final Zones getPlugin() {
        return zones;
    }

    /**
     * Set the zone for this L2ZoneType Instance
     * 
     * @param zone
     */
    public final void setForm(ZoneForm zone) {
        form = zone;
    }
    
    @Deprecated
    public final void setZone(ZoneForm zone) {
        setForm(zone);
    }
    

    /**
     * Returns this zones zone form
     * 
     * @return form
     */
    public final ZoneForm getForm() {
        return form;
    }
    
    @Deprecated
    public final ZoneForm getZone() {
        return getForm();
    }

    /**
     * Checks if the given coordinates are within zone's plane
     * 
     * @param x
     * @param y
     */
    public final boolean isInsideZone(int x, int y) {
        if (form.isInsideZone(x, y))
            return true;
        else
            return false;
    }

    /**
     * Checks if the given coordinates are within the zone
     * 
     * @param x
     * @param y
     * @param z
     */
    public final boolean isInsideZone(int x, int y, int z) {
        if (form.isInsideZone(x, y, z))
            return true;
        else
            return false;
    }
    
    public final boolean isInsideZone(Block block) {
        return isInsideZone(block.getX(),block.getZ(), block.getY());
    }

    /**
     * Checks if the given object is inside the zone.
     * 
     * @param player
     */
    public final boolean isInsideZone(Player player)      {return isInsideZone(player.getLocation());}
    public final boolean isInsideZone(Location loc)       {return isInsideZone(WorldManager.toInt(loc.getX()), WorldManager.toInt(loc.getZ()), WorldManager.toInt(loc.getY()));}
    
    public final double getDistanceToZone(int x, int y)   {return getForm().getDistanceToZone(x, y);}

    public final double getDistanceToZone(Player player) {
        Location loc = player.getLocation();
        return getForm().getDistanceToZone(WorldManager.toInt(loc.getX()), WorldManager.toInt(loc.getZ()));
    }

    /**
     * Force fully removes a character from the zone Should use during teleport
     * / logoff
     * 
     * @param player
     */
    public final void removePlayer(Player player) { removePlayer(player,false); }
    public final void removePlayer(Player player, boolean silent) {
        if (playerList.containsKey(player.getEntityId())) {
            playerList.remove(player.getEntityId());
            if(!silent) onExit(player, player.getLocation());
        }
    }

    /**
     * Will scan the zones char list for the character
     * 
     * @param player
     * @return
     */
    public final boolean isPlayerInZone(Player player) {
        return playerList.containsKey(player.getEntityId());
    }
    
    protected abstract void onEnter(Player player, Location to);
    protected abstract void onExit(Player player, Location to);
    
    public abstract Location getSpawnLocation(Player player);
    
    public abstract ZonesAccess getAccess(OfflinePlayer player);
    public abstract ZonesAccess getAccess(String group);
    
    public abstract boolean canAdministrate(OfflinePlayer player);
   
    
    public final HashMap<Integer, Player> getPlayersInsideMap() {
        return playerList;
    }
    
    public final Collection<Player>  getPlayersInside() {
        return playerList.values();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "]" + getName();
    }

    public final boolean setName(String name) {
        try {
            getPersistence().setName(name);
            zones.getMysqlDatabase().update(getPersistence());
            //zones.getDatabase().update(getPersistence());
            this.name = name;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public final boolean saveSettings() {
        try {
            getPersistence().saveConfig();
            zones.getMysqlDatabase().update(getPersistence());
            //zones.getDatabase().update(getPersistence());      
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public final void revalidateInZone(Player player) {
        revalidateInZone(player,player.getLocation());
    }
    public final void revalidateInZone(Player player, Location loc) {
        if (isInsideZone(loc)) {
            if (!playerList.containsKey(player.getEntityId())) {
                playerList.put(player.getEntityId(), player);
                onEnter(player, loc);
            }
        } else {
            if (playerList.containsKey(player.getEntityId())) {
                playerList.remove(player.getEntityId());
                onExit(player, loc);
            }
        }
    }

    /**
     * Easy function to get boolean flags.
     * 
     * @param zoneconfigvar
     * @return the value or default if null.
     */
    public final boolean getFlag(ZoneVar name) {
        if(name.getType() != ZoneVarType.BOOLEAN) {
            return getSettings().has(name.getName());
        }
        Object obj;
        if(hasSetting(name)) {
            obj  = getSettings().get(name.getName());            
        } else {
            obj = name.getDefault();
        }
        
        return (Boolean) obj;
    }
    
    public final boolean hasSetting(ZoneVar name) {
        return getSettings().has(name.getName());
    }
    
    public final String getString(ZoneVar name) {
        if(name.getType() != ZoneVarType.STRING) {
            return "";
        }
        Object obj;
        if(hasSetting(name)) {
            obj = getSettings().get(name.getName());
        } else {
            if(getWorldConfig().isEnabled(name)) {
                obj = getWorldConfig().getSetting(name).get("value");
            } else {
                obj = name.getDefault();
            }
        }
        
        return (String) obj;
    }
    
    public final boolean isProtected(ZoneVar var, Material type) {
        if(var.getType() != ZoneVarType.MATERIALLIST) {
            return false;
        }
        if(!getSettings().has(var.getName())) {
            return false;
        }
        return getSettings().getJSONArray(var.getName()).contains(type.name());
    }
    
    @Override
    public final boolean equals(Object o) {
        if(super.equals(o))
            return true;
        
        if(!(o instanceof ZoneBase)) {
            return false;
        }
        
        return (getId() == ((ZoneBase)o).getId());
    }
    
    @Override
    public final int hashCode() {
        return getId();
    }
    
    public void sendMarkupMessage(String message, Player player) {
        sendMarkupMessage(message, player, player);
    }
    
    public void sendMarkupMessage(String message, Player content, Player receiver) {
        if(message.trim().equalsIgnoreCase("none")) return;
        
        message = message.replace("{zname}", getName());
        if(message.contains("{access}")) message = message.replace("{access}", getAccess(content).toColorCode());
        message = message.replace("{pname}", content.getDisplayName());
        message = message.replace("^", "\u00A7");
        receiver.sendMessage(message);
    }

}
