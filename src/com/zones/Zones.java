package com.zones;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.zones.commands.ZoneCommand;
import com.zones.commands.ZoneCommandMap;
import com.zones.listeners.ZonesBlockListener;
import com.zones.listeners.ZonesEntityListener;
import com.zones.listeners.ZonesPlayerListener;
import com.zones.listeners.ZonesVehicleListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static final int            Rev             = 42;
    protected static final Logger      log             = Logger.getLogger("Minecraft");
    private final ZonesPlayerListener  playerListener  = new ZonesPlayerListener(this);
    private final ZonesBlockListener   blockListener   = new ZonesBlockListener(this);
    private final ZonesEntityListener  entityListener  = new ZonesEntityListener(this);
    private final ZonesVehicleListener vehicleListener = new ZonesVehicleListener(this);
    
    private final ZoneCommandMap      commandMap = new ZoneCommandMap(this);
    
    private WorldEditPlugin   worldedit;
    private PermissionHandler accessmanager;
    
    private final Map<String,WorldManager> worlds = new HashMap<String, WorldManager>();
    private final ZoneManager zoneManager = new ZoneManager();
    
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

        registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.High);
        registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.High);
        registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.High);
        registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.High);

        registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.High);
        //registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal);

        registerEvent(Event.Type.VEHICLE_DAMAGE, vehicleListener, Priority.High);
        registerEvent(Event.Type.VEHICLE_MOVE, vehicleListener, Priority.High);
    }

    public void registerWorldEdit() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldEdit");
        if(worldedit == null)
            if(plugin != null)
                worldedit = (WorldEditPlugin) plugin;
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

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(ZonesConfig.DATABASE_URL + "?autoReconnect=true&user=" + ZonesConfig.DATABASE_LOGIN + "&password=" + ZonesConfig.DATABASE_PASSWORD);
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to retreive connection", ex);
        }
        return null;
    }

    @Override
    public void onDisable() {
        log.info("[Zones]plugin disabled!");
    }

    @Override
    public void onEnable() {
        log.info("[Zones]Rev " + Rev + "  Loading...");
        
        if(!(new File(ZonesConfig.ZONES_CONFIG_FILE)).exists()) {
            try {
                (new File(ZonesConfig.ZONES_CONFIG_FILE)).mkdirs();
            InputStream input = Zones.class.getResourceAsStream("/com/zones/config/Zones.properties");

            //For Overwrite the file.
            OutputStream output = new FileOutputStream(new File(ZonesConfig.ZONES_CONFIG_FILE));

            byte[] buf = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0){
              output.write(buf, 0, len);
            }
            input.close();
            output.close();
            
            
            } catch (Exception e) {
                log.info("[Zones]Error while restorting configuration file.");
                e.printStackTrace();
            }
            log.info("[Zones]Missing configuration file restored.");
            log.info("----------------------");
            log.info("Zones will NOT finish loading since it has to be configured first to be able to load properly!");
            log.info("----------------------");            
        } else {    
            Plugin p = this.getServer().getPluginManager().getPlugin("Permissions");
            
            if(p != null && p instanceof Permissions) {
                if(!p.isEnabled()) {
                    getPluginLoader().enablePlugin(p);
                }
                accessmanager = ((Permissions)p).getHandler();
            } else {
                log.info("----------------------");
                log.info("Permissions manager NOT found, this will probably break the plugin!");
                log.info("----------------------");                
            }
            
            ZonesConfig.load();
            loadWorlds();
            getZoneManager().load(this);
            registerEvents();
            if(ZonesConfig.WORLDEDIT_ENABLED) {
                log.info("[Zones] Loading worldedit support...");
                registerWorldEdit();
            }
            log.info("[Zones]finished Loading.");
        }
    }
    
    private void loadWorlds() {
        worlds.clear();
        for(World world : getServer().getWorlds())
            worlds.put(world.getName(),new WorldManager(this,world));
        
    }
    
    public PermissionHandler getP() {
        return accessmanager;
    }

    public WorldEditPlugin getWorldEdit()
    {
        return worldedit;
    }
    
    public WorldManager getWorldManager(Player p) { return getWorldManager(p.getWorld()); }
    public WorldManager getWorldManager(Location l) { return getWorldManager(l.getWorld()); }
    
    public WorldManager getWorldManager(World world) {
        if(!worlds.containsKey(world.getName())) {
            worlds.put(world.getName(), new WorldManager(this,world));
        }
        return worlds.get(world.getName());
    }
    

    public WorldManager getWorldManager(String world) {
        if(!worlds.containsKey(world)) {
            World w = getServer().getWorld(world);
            if(w != null) {
                worlds.put(w.getName(), new WorldManager(this,w));
            } else {
                log.warning("[Zones] Trying to find world '" + world + "' which doesn't exist !");
            }
        }
        return worlds.get(world);
    }
    
    public ZoneManager getZoneManager() {
        return zoneManager;
    }
    
    public boolean reload() {
        return reloadConfig() && reloadZones();
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ZoneCommand cmd = commandMap.getCommand(label);
        if(cmd != null) {
            return cmd.execute(sender, label, args);
        }
        return false;
    }

    public boolean reloadZones() {
        try {
            for(WorldManager w : worlds.values())
                w.loadRegions();
            
            getZoneManager().load(this);
            //commandMap.load();
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean reloadConfig() {
        try {
            ZonesConfig.load();
            for(WorldManager w : worlds.values())
                w.loadConfig();
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    
}

