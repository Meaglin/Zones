package com.zones.model;

import gnu.trove.TIntObjectHashMap;

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
import com.zones.model.settings.ZoneVar;
import com.zones.persistence.Zone;

/**
 * Abstract base class for any zone type Handles basic operations
 * 
 * @author durgus, Meaglin
 */
public abstract class ZoneBase {
    protected static final Logger     log = Logger.getLogger(ZoneBase.class.getName());

    private int                 id;
    protected ZoneForm                form;
    protected TIntObjectHashMap<Player> playerList;

    private String                    name;
    private ZoneSettings              settings = new ZoneSettings();

    protected Zones                   zones;
    protected WorldManager            worldManager;
    
    private Zone                      persistence;
    
    private boolean                   initialized;
    
    protected ZoneBase() {
        playerList = new TIntObjectHashMap<Player>();
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
    
    protected void onLoad(Zone persistence) {
        name = persistence.getName();
        if(persistence.getSettings() != null && !persistence.getSettings().trim().equals("")) {
            try {
                settings = ZoneSettings.unserialize(persistence.getSettings());
            } catch(Exception e) {
                log.warning("[Zones]Error loading settings of " + name + "[" + id + "]");
                e.printStackTrace();
            }
        }
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
    
    public Zone getPersistence() {
        return persistence;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }
    
    public WorldConfig getWorldConfig() {
        return getWorldManager().getConfig();
    }
    
    public World getWorld() {
        return getWorldManager().getWorld();
    }
    
    public Zones getPlugin() {
        return zones;
    }

    /**
     * Set the zone for this L2ZoneType Instance
     * 
     * @param zone
     */
    public void setForm(ZoneForm zone) {
        form = zone;
    }
    
    @Deprecated
    public void setZone(ZoneForm zone) {
        setForm(zone);
    }
    

    /**
     * Returns this zones zone form
     * 
     * @return form
     */
    public ZoneForm getForm() {
        return form;
    }
    
    @Deprecated
    public ZoneForm getZone() {
        return getForm();
    }

    /**
     * Checks if the given coordinates are within zone's plane
     * 
     * @param x
     * @param y
     */
    public boolean isInsideZone(int x, int y) {
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
    
    public double getDistanceToZone(int x, int y)   {return getForm().getDistanceToZone(x, y);}

    public double getDistanceToZone(Player player) {
        Location loc = player.getLocation();
        return getForm().getDistanceToZone(WorldManager.toInt(loc.getX()), WorldManager.toInt(loc.getZ()));
    }

    /**
     * Force fully removes a character from the zone Should use during teleport
     * / logoff
     * 
     * @param player
     */
    public void removePlayer(Player player) { removePlayer(player,false); }
    public void removePlayer(Player player, boolean silent) {
        if (playerList.containsKey(player.getEntityId())) {
            playerList.remove(player.getEntityId());
            if(!silent)onExit(player);
        }
    }

    /**
     * Will scan the zones char list for the character
     * 
     * @param player
     * @return
     */
    public boolean isPlayerInZone(Player player) {
        return playerList.containsKey(player.getEntityId());
    }

    protected abstract void onEnter(Player player);
    protected abstract void onExit(Player player);

    public abstract boolean allowWater(Block from, Block to);
    public abstract boolean allowLava(Block from, Block to);
    public abstract boolean allowDynamite(Block block);
    public abstract boolean allowHealth(Player player);
    public abstract boolean allowLeafDecay(Block block);
    public abstract boolean allowSnowFall(Block block);
    public abstract boolean allowIceForm(Block block);
    public abstract boolean allowSnowMelt(Block block);
    public abstract boolean allowIceMelt(Block block);
    public abstract boolean allowMushroomSpread(Block block);
    public abstract boolean allowPhysics(Block block);
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
   
    
    public TIntObjectHashMap<Player> getPlayersInsideMap() {
        return playerList;
    }
    
    public Player[]  getPlayersInside() {
        return playerList.getValues(new Player[playerList.size()]);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "]";
    }

    public boolean setName(String name) {

        getPersistence().setName(name);
        zones.getDatabase().update(getPersistence());
        this.name = name;
        return true;
    }
    
    public boolean saveSettings() {
        getPersistence().setSettings(getSettings().serialize());
        zones.getDatabase().update(getPersistence());      
        return true;
    }


    public void revalidateInZone(Player player) {
        revalidateInZone(player,player.getLocation());
    }
    public void revalidateInZone(Player player, Location loc) {
        if (isInsideZone(loc)) {
            if (!playerList.containsKey(player.getEntityId())) {
                playerList.put(player.getEntityId(), player);
                onEnter(player);
            }
        } else {
            if (playerList.containsKey(player.getEntityId())) {
                playerList.remove(player.getEntityId());
                onExit(player);
            }
        }
    }
    
    public ZoneSettings getSettings() {
        return settings;
    }
    
    /**
     * Easy function to get boolean flags.
     * 
     * @param zoneconfigvar
     * @return the value or default if null.
     */
    public boolean getFlag(ZoneVar name) {
        if(!name.getType().equals(Boolean.class))
            return false;
        return getSettings().getBool(name, (Boolean)name.getDefault(this));
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
