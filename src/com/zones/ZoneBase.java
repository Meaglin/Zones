package com.zones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.zones.util.Settings;

/**
 * Abstract base class for any zone type Handles basic operations
 * 
 * @author durgus, Meaglin
 */
public abstract class ZoneBase {
    protected static final Logger        log           = Logger.getLogger(ZoneBase.class.getName());

    private final int                    id;
    protected ZoneForm                  form;
    protected HashMap<String, Player>    characterList;

    private String                       name;
    private Settings                    settings;

    protected Zones                        zones;
    protected String                       world;
    
    protected ZoneBase(Zones zones,String world, int id) {
        this.id = id;
        this.zones = zones;
        this.world = world;
        characterList = new HashMap<String, Player>();
    }
    
    public void loadSettings(String data) {
        try {
            loadSettings(Settings.unserialize(data));
        } catch(Exception e) {
            log.warning("[Zones]Error loading settings of " + name + "[" + id + "]");
            e.printStackTrace();
        }
    }
    
    public void loadSettings(Settings settings) {
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
    public boolean isInsideZone(int x, int y,String world) {
        if (this.world.equals(world) && form.isInsideZone(x, y, form.getHighZ()))
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
    public boolean isInsideZone(int x, int y, int z,String world) {
        if (this.world.equals(world) && form.isInsideZone(x, y, z))
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
    public boolean isInsideZone(Location loc)       {return isInsideZone(World.toInt(loc.getX()), World.toInt(loc.getZ()), World.toInt(loc.getY()),loc.getWorld().getName());}
    
    public double getDistanceToZone(int x, int y)   {return getZone().getDistanceToZone(x, y);}

    public double getDistanceToZone(Player player) {
        Location loc = player.getLocation();
        return getZone().getDistanceToZone(World.toInt(loc.getX()), World.toInt(loc.getZ()));
    }

    /**
     * Force fully removes a character from the zone Should use during teleport
     * / logoff
     * 
     * @param player
     */
    public void removeCharacter(Player player) {
        if (characterList.containsKey(player.getName())) {
            characterList.remove(player.getName());
            onExit(player);
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

    protected abstract void onEnter(Player character);
    protected abstract void onExit(Player character);

    public abstract boolean allowWater(Block b);
    public abstract boolean allowLava(Block b);
    public abstract boolean allowDynamite(Block b);
    public abstract boolean allowHealth(Player player);
    public abstract boolean allowLeafDecay(Block b);
    /**
     * 
     * @param player not null when fire is started by player using flintandsteel(lighter).
     * @param block
     * @return
     */
    public abstract boolean allowFire(Player player, Block block);
    
    public abstract boolean allowSpawn(Entity entity);
    
    public abstract boolean allowBlockDestroy(Player player, Block block);
    public abstract boolean allowBlockCreate(Player player, Block block);
    public abstract boolean allowBlockModify(Player player, Block block);
    public abstract boolean allowEnter(Player player, Location to);
    public abstract boolean allowTeleport(Player player, Location to);
    public abstract boolean allowEntityHit(Player attacker, Entity defender);
    
    public abstract ZonesAccess getAccess(Player player);
    public abstract ZonesAccess getAccess(String group);
    
    public abstract boolean canAdministrate(Player player);
    /**
     * 
     * @param attacker
     * @param defender (music blocks, etc..)
     * @return
     */
    public abstract boolean allowBlockHit(Player attacker, Block defender);
   
    
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
    
    private boolean saveSettings() {
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
    
    public Settings getSettings() {
        return settings;
    }
    
    public boolean setSetting(String name, boolean b) {
        getSettings().set(name, b);
        return saveSettings();
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof ZoneBase))
            return false;
        
        return (getId() == ((ZoneBase)o).getId());
    }

}
