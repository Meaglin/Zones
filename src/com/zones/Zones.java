package com.zones;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Zones extends JavaPlugin implements CommandExecutor {

    public static final int            Rev             = 31;
    protected static final Logger      log             = Logger.getLogger("Minecraft");
    private final ZonesPlayerListener  playerListener  = new ZonesPlayerListener(this);
    private final ZonesBlockListener   blockListener   = new ZonesBlockListener(this);
    private final ZonesEntityListener  entityListener  = new ZonesEntityListener(this);
    private final ZonesVehicleListener vehicleListener = new ZonesVehicleListener(this);
    public static final int            pilonHeight     = 4;
    // snow
    public static final int            pilonType       = 80;
    // stick
    public static final int            toolType        = 280;
    private final ZoneCommandMap      commandMap = new ZoneCommandMap(this);
    
    
    PermissionHandler accessmanager;
    
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
            ZoneManager.getInstance();
            ZoneManager.getInstance().load(this);
            registerEvents();
            log.info("[Zones]finished Loading.");
        }
    }
    
    public PermissionHandler getP() {
        return accessmanager;
    }
    
    public World getWorldManager() {
        return World.getInstance();
    }
    
    public ZoneManager getZoneManager() {
        return ZoneManager.getInstance();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ZoneCommand cmd = commandMap.getCommand(label);
        if(cmd != null) {
            return cmd.execute(sender, label, args);
        }
        return false;
    }
    
}

