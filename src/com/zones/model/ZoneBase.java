package com.zones.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.settings.ZoneVar;

/**
 * Abstract base class for any zone type Handles basic operations
 * 
 * @author durgus, Meaglin
 */
public abstract class ZoneBase {
    protected static final Logger     log = Logger.getLogger(ZoneBase.class.getName());

    private final int                 id;
    protected ZoneForm                form;
    protected HashMap<String, Player> characterList;

    private String                    name;
    private ZoneSettings                  settings = new ZoneSettings();

    protected Zones                   zones;
    protected WorldManager            worldManager;
    
    protected ZoneBase(Zones zones,WorldManager worldManager, int id) {
        this.id = id;
        this.zones = zones;
        this.worldManager = worldManager;
        characterList = new HashMap<String, Player>();
    }
    
    public void loadSettings(String data) {
        try {
            loadSettings(ZoneSettings.unserialize(data));
        } catch(Exception e) {
            log.warning("[Zones]Error loading settings of " + name + "[" + id + "]");
            e.printStackTrace();
        }
    }
    
    public void loadSettings(ZoneSettings settings) {
        this.settings = settings;
    }
    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }
    
    public World getWorld() {
        return getWorldManager().getWorld();
    }
    /**
     * Setup new parameters for this zone
     * 
     * @param type
     * @param value
     */
    public void setParameter(String name, String value) {
        if (value == null || value.equals(""))
            return;
        
        if (name.equals("name")) {
            this.name = value;
        } else
            log.info(getClass().getSimpleName() + ": Unknown parameter - " + name + " in zone: " + getId());
    }

    /**
     * Set the zone for this L2ZoneType Instance
     * 
     * @param zone
     */
    public void setZone(ZoneForm zone) {
        form = zone;
    }

    /**
     * Returns this zones zone form
     * 
     * @param zone
     * @return
     */
    public ZoneForm getZone() {
        return form;
    }

    /**
     * Checks if the given coordinates are within zone's plane
     * 
     * @param x
     * @param y
     */
    public boolean isInsideZone(int x, int y) {
        if (form.isInsideZone(x, y, form.getHighZ()))
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
    public boolean isInsideZone(int x, int y, int z) {
        if (form.isInsideZone(x, y, z))
            return true;
        else
            return false;
    }

    /**
     * Checks if the given object is inside the zone.
     * 
     * @param player
     */
    public boolean isInsideZone(Player player)      {return isInsideZone(player.getLocation());}
    public boolean isInsideZone(Location loc)       {return isInsideZone(WorldManager.toInt(loc.getX()), WorldManager.toInt(loc.getZ()), WorldManager.toInt(loc.getY()));}
    
    public double getDistanceToZone(int x, int y)   {return getZone().getDistanceToZone(x, y);}

    public double getDistanceToZone(Player player) {
        Location loc = player.getLocation();
        return getZone().getDistanceToZone(WorldManager.toInt(loc.getX()), WorldManager.toInt(loc.getZ()));
    }

    /**
     * Force fully removes a character from the zone Should use during teleport
     * / logoff
     * 
     * @param player
     */
    public void removeCharacter(Player player) { removeCharacter(player,false); }
    public void removeCharacter(Player player, boolean silent) {
        if (characterList.containsKey(player.getName())) {
            characterList.remove(player.getName());
            if(!silent)onExit(player);
        }
    }

    /**
     * Will scan the zones char list for the character
     * 
     * @param player
     * @return
     */
    public boolean isCharacterInZone(Player player) {
        return characterList.containsKey(player.getName());
    }

    protected abstract void onEnter(Player player);
    protected abstract void onExit(Player player);

    public abstract boolean allowWater(Block from, Block to);
    public abstract boolean allowLava(Block from, Block to);
    public abstract boolean allowDynamite(Block block);
    public abstract boolean allowHealth(Player player);
    public abstract boolean allowLeafDecay(Block block);
    public abstract boolean allowSnowFall(Block block);
    public abstract boolean allowFire(Player player, Block block);
    
    public abstract boolean allowSpawn(Entity entity,CreatureType type);
    
    public abstract boolean allowBlockCreate(Player player, Block block);
    public abstract boolean allowBlockCreate(Player player, Block block, ItemStack item);
    public abstract boolean allowBlockModify(Player player, Block block);
    public abstract boolean allowBlockDestroy(Player player, Block block);
    public abstract boolean allowBlockHit(Player attacker, Block defender);
    public abstract boolean allowEntityHit(Player attacker, Entity defender);
    
    public abstract boolean allowEnter(Player player,Location to);
    public abstract boolean allowTeleport(Player player,Location to);
    
    public abstract Location getSpawnLocation(Player player);
    
    public abstract ZonesAccess getAccess(Player player);
    public abstract ZonesAccess getAccess(String group);
    
    public abstract boolean canAdministrate(Player player);
   
    
    public HashMap<String, Player> getCharactersInside() {
        return characterList;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "]";
    }

    public boolean setName(String name) {

        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET name = ? WHERE id = ?");
            st.setString(1, name);
            st.setInt(2, getId());
            u = st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(conn != null)    conn.close();
                if(st != null)      st.close();
            } catch (Exception e) {
            }
        }

        if (u < 1)
            return false;

        this.name = name;

        return true;
    }
    
    public boolean saveSettings() {
        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET settings = ? WHERE id = ?");
            st.setString(1, settings.serialize());
            st.setInt(2, getId());
            u = st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(conn != null)    conn.close();
                if(st != null)      st.close();
            } catch (Exception e) {
            }
        }

        if (u < 1)
            return false;
        
        return true;
    }


    public void revalidateInZone(Player player) {
        revalidateInZone(player,player.getLocation());
    }
    public void revalidateInZone(Player player, Location loc) {
        if (isInsideZone(loc)) {
            if (!characterList.containsKey(player.getName())) {
                characterList.put(player.getName(), player);
                onEnter(player);
            }
        } else {
            if (characterList.containsKey(player.getName())) {
                characterList.remove(player.getName());
                onExit(player);
            }
        }
    }
    
    public ZoneSettings getSettings() {
        return settings;
    }
    
    public boolean setSetting(ZoneVar name, boolean b) {
        getSettings().set(name, b);
        return saveSettings();
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof ZoneBase))
            return false;
        
        return (getId() == ((ZoneBase)o).getId());
    }

}
