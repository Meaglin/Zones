package com.zones;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.zones.commands.ZoneCommand;
import com.zones.commands.ZoneCommandMap;
import com.zones.listeners.ZonesBlockListener;
import com.zones.listeners.ZonesEntityListener;
import com.zones.listeners.ZonesPlayerListener;
import com.zones.listeners.ZonesVehicleListener;
import com.zones.permissions.BukkitPermissions;
import com.zones.permissions.NijiPermissions;
import com.zones.permissions.Permissions;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;
import com.zones.util.FileUtil;

import gnu.trove.TLongObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Meaglin
 *
 */
public class Zones extends JavaPlugin implements CommandExecutor {

    public static final int                 Rev             = 94;
    protected static final Logger           log             = Logger.getLogger("Minecraft");
    private final ZonesPlayerListener       playerListener  = new ZonesPlayerListener(this);
    private final ZonesBlockListener        blockListener   = new ZonesBlockListener(this);
    private final ZonesEntityListener       entityListener  = new ZonesEntityListener(this);
    private final ZonesVehicleListener      vehicleListener = new ZonesVehicleListener(this);

    private final ZoneCommandMap            commandMap      = new ZoneCommandMap(this);

    private WorldEditPlugin                 worldedit;
    private Permissions                     permissionsManager;

    private final TLongObjectHashMap<WorldManager> worlds   = new TLongObjectHashMap<WorldManager>(1);
    private final ZoneManager               zoneManager     = new ZoneManager(this);
    
    public Zones() {
    }

    /**
     * Register used events.
     */
    private void registerEvents() {
        
        registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Low);
        registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Low);
        registerEvent(Event.Type.LEAVES_DECAY, blockListener, Priority.Low);
        registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.High);

        /**
         * Will be replaced by/fall under BLOCK_FORM after the next RB
         */
        registerEvent(Event.Type.SNOW_FORM, blockListener, Priority.High);
        
        registerEvent(Event.Type.BLOCK_FORM, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_SPREAD, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_FADE, blockListener, Priority.High);

        registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.High);
        registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.High);
        registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.High);
        registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.High);
        registerEvent(Event.Type.PAINTING_PLACE, entityListener, Priority.Normal);
        registerEvent(Event.Type.PAINTING_BREAK, entityListener, Priority.Normal);

        registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Low);
        registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.High);
        //registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_BUCKET_FILL, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Priority.Normal);

        registerEvent(Event.Type.VEHICLE_DAMAGE, vehicleListener, Priority.High);
        registerEvent(Event.Type.VEHICLE_MOVE, vehicleListener, Priority.High);
    }

    public void registerWorldEdit() {
        if(worldedit == null) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldEdit");
            if(plugin != null)
                worldedit = (WorldEditPlugin) plugin;
        }
    }

    /**
     * Register an event.
     * 
     * @param type
     * @param listener
     * @param priority
     */
    private void registerEvent(Event.Type type, Listener listener, Priority priority) {
        getServer().getPluginManager().registerEvent(type, listener, priority, this);
    }
    
    private void setupDatabase() {
        try {
            getDatabase().find(Zone.class);
        } catch (PersistenceException ex) {
            System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }
    
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Zone.class);
        list.add(Vertice.class);
        return list;
    }

    @Override
    public void onDisable() {
        log.info("[Zones]plugin disabled!");
    }

    @Override
    public void onEnable() {
        log.info("[Zones]Rev " + Rev + "  Loading...");
        
        File configFile = new File(getDataFolder().getPath()+"/"+ZonesConfig.ZONES_CONFIG_FILE);
        if(!configFile.exists()) {
            getDataFolder().mkdirs();
            if(FileUtil.copyFile(Zones.class.getResourceAsStream("/com/zones/config/Zones.properties"), configFile)) {
                log.info("[Zones]Missing configuration file restored.");                
            } else {
                log.info("[Zones]Error while restorting configuration file.");
            }       
        }  
        resolvePermissions();
        setupDatabase();
        ZonesConfig.load(configFile);
        commandMap.load();
        loadWorlds();
        registerEvents();
        if(ZonesConfig.WORLDEDIT_ENABLED) {
            log.info("[Zones] Loading worldedit support...");
            registerWorldEdit();
        }
        log.info("[Zones]finished Loading.");
        
    }
    
    private void resolvePermissions() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Permissions");
        if(plugin != null && plugin instanceof com.nijikokun.bukkit.Permissions.Permissions) {
            if(!plugin.isEnabled()) {
                getPluginLoader().enablePlugin(plugin);
            }
            permissionsManager = new NijiPermissions((com.nijikokun.bukkit.Permissions.Permissions)plugin);
            log.info("[Zones]Using Nijikokun Permissions for permissions managing.");
        } else {
            permissionsManager = new BukkitPermissions();
            log.info("[Zones]Using built in isOp() for permissions managing.");
        }
    }
    
    private void loadWorlds() {
        worlds.clear();
        for(World world : getServer().getWorlds())
            worlds.put(world.getId(),new WorldManager(this,world));
        
    }
    
    
    public Permissions getPermissions() {
        return permissionsManager;
    }

    public WorldEditPlugin getWorldEdit() {
        return worldedit;
    }
    
    public WorldManager getWorldManager(Player p) { return getWorldManager(p.getWorld()); }
    public WorldManager getWorldManager(Location l) { return getWorldManager(l.getWorld()); }
    
    /*
     * It's more efficient to do the null call instead of using containskey since it has more underlying calls.
     */
    public WorldManager getWorldManager(World world) {
        WorldManager wm = worlds.get(world.getId());
        if(wm == null) {
            wm = new WorldManager(this,world);
            worlds.put(world.getId(), wm);
        }
        return wm;
    }
    
    public WorldManager[] getWorlds() {
        return worlds.getValues(new WorldManager[worlds.size()]);
    }

    protected WorldManager getWorldManager(String world) {
        World w = getServer().getWorld(world);
        if(w == null) {
            log.warning("[Zones] Trying to find world '" + world + "' which doesn't exist !");
            return null;
        }
        WorldManager wm = worlds.get(w.getId());
        if(wm == null) {
            wm = new WorldManager(this,w);
            worlds.put(w.getId(), wm);
        }
        
        return wm;
    }
    
    public ZoneManager getZoneManager() {
        return zoneManager;
    }
    
    public boolean reload() {
        return reloadConfig() && reloadZones();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ZoneCommand cmd = commandMap.getCommand(command.getName());
        if(cmd != null) {
            return cmd.execute(sender, label, args);
        }
        return false;
    }

    public boolean reloadZones() {
        try {
            for(WorldManager w : worlds.getValues(new WorldManager[worlds.size()]))
                w.loadRegions();
            
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean reloadConfig() {
        try {
            ZonesConfig.load(new File(getDataFolder().getPath()+"/"+ZonesConfig.ZONES_CONFIG_FILE));
            for(WorldManager w : worlds.getValues(new WorldManager[worlds.size()]))
                w.loadConfig();
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    
}

