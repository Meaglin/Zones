package com.zones;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import mc.alk.virtualPlayer.VirtualPlayer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.zones.command.CommandMap;
import com.zones.listeners.ZonesBlockListener;
import com.zones.listeners.ZonesEntityListener;
import com.zones.listeners.ZonesPlayerListener;
import com.zones.listeners.ZonesStoneProtectListener;
import com.zones.listeners.ZonesVehicleListener;
import com.zones.listeners.ZonesWeatherListener;
import com.zones.persistence.Database;
import com.zones.util.FileUtil;
import com.zones.util.ZoneUtil;
import com.zones.world.RegionWorldManager;
import com.zones.world.WorldManager;

/**
 * 
 * @author Meaglin
 *
 */
public class Zones extends JavaPlugin implements CommandExecutor {

    public static final int                 Rev             = 180;
    public static final Logger              log             = Logger.getLogger("Minecraft");
    public final ZonesPlayerListener       playerListener  = new ZonesPlayerListener(this);
    public final ZonesBlockListener        blockListener   = new ZonesBlockListener(this);
    public final ZonesVehicleListener      vehicleListener = new ZonesVehicleListener(this);
    public final ZonesEntityListener       entityListener  = new ZonesEntityListener(this);
    public final ZonesWeatherListener      weatherListener = new ZonesWeatherListener(this);

    private CommandMap                      commandMap;
    
    private WorldEditPlugin                 worldedit;
    private Permission                     permissionsManager;
    private Economy economy;

    private final HashMap<Long, WorldManager> worlds        = new HashMap<Long, WorldManager>(2);
    private final ZoneManager               zoneManager     = new ZoneManager(this);
    private Database                        database        = null;
    private final ZoneUtil                  util            = new ZoneUtil(this);
    
    public static final boolean             debugEnabled    = false;
    
    public Zones() {
    }

    /**
     * Register used events.
     */
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(playerListener, this);
        pm.registerEvents(vehicleListener, this);
        pm.registerEvents(weatherListener, this);
        pm.registerEvents(new ZonesStoneProtectListener(this), this);
    }

    public void registerWorldEdit() {
        if(worldedit == null) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldEdit");
            if(plugin != null) {
                worldedit = (WorldEditPlugin) plugin;
            }
        }
    }

    @Override
    public void onDisable() {
        log.info("[Zones] plugin disabled!");
    }

    @Override
    public void onEnable() {
        File configFile = new File(getDataFolder(), ZonesConfig.ZONES_CONFIG_FILE);
        if(!configFile.exists()) {
            getDataFolder().mkdirs();
            if(FileUtil.copyFile(getResource("/com/zones/config/Zones.properties"), configFile)) {
                log.info("[Zones] Missing configuration file restored.");                
            } else {
                log.info("[Zones] Error while restoring configuration file.");
            }       
        }
        ZonesConfig.load(this, configFile);
        commandMap = new CommandMap(this);  
        database = new Database(this);
        
        resolvePermissions();
        resolveEconomy();
        
        loadWorlds();
        registerEvents();
        if(ZonesConfig.WORLDEDIT_ENABLED) {
            registerWorldEdit();
        }
        log.info("[Zones] Rev " + Rev + " Loaded " + getZoneManager().getZoneCount()  + " zones in " + worlds.size() + " worlds, WorldEditSupport:" + ZonesConfig.WORLDEDIT_ENABLED + ".");
        
    }
    
    private void resolvePermissions() {
        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        permissionsManager = rsp.getProvider();
    }
    
    private boolean resolveEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    
    private void loadWorlds() {
        try {
            worlds.clear();
            for(World world : getServer().getWorlds()) {
                WorldManager wm = new RegionWorldManager(this, world);
                worlds.put(world.getUID().getLeastSignificantBits(), wm);
                getZoneManager().load(wm);
            }
        } catch(Throwable t) {
            log.warning("[Zones] Error loading worlds.");
            t.printStackTrace();
        }
    }
    
    
    public Permission getPermissions() {
        return permissionsManager;
    }
    
    public boolean hasPermission(Player player, String node) {
        if(player instanceof VirtualPlayer) {
            return false;
        }
        return getPermissions().playerHas(player, node);
    }
    
    public String getGroup(String world, OfflinePlayer player) {
        if(player instanceof VirtualPlayer) {
            return "default";
        }
        return getPermissions().getPrimaryGroup(world, player);
    }
    
    public String[] getGroups(String world, OfflinePlayer player) {
        if(player instanceof VirtualPlayer) {
            return new String[] {"default"};
        }
        return getPermissions().getPlayerGroups(world, player);
    }
    
    public boolean hasPermission(String world, OfflinePlayer player, String node) {
        if(player instanceof VirtualPlayer) {
            return false;
        }
        return getPermissions().playerHas(world, player, node);
    }
    
    public Economy getEconomy() {
        return economy;
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
        WorldManager wm = worlds.get(world.getUID().getLeastSignificantBits());
        if(wm == null) {
            wm = new RegionWorldManager(this, world);
            worlds.put(world.getUID().getLeastSignificantBits(), wm);
            getZoneManager().load(wm);
        }
        return wm;
    }
    
    public Collection<WorldManager> getWorlds() {
        return worlds.values();
    }

    protected WorldManager getWorldManager(String worldName) {
        World world = getServer().getWorld(worldName);
        if(world == null) {
            log.warning("[Zones] Trying to find world '" + worldName + "' which doesn't exist !");
            return null;
        }
        WorldManager wm = worlds.get(world.getUID().getLeastSignificantBits());
        if(wm == null) {
            wm = new RegionWorldManager(this, world);
            worlds.put(world.getUID().getLeastSignificantBits(), wm);
            getZoneManager().load(wm);
        }
        
        return wm;
    }
    
    public ZoneManager getZoneManager() {
        return zoneManager;
    }
    
    public boolean reload() {
        return reloadZonesConfig() && reloadZones();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandMap.run(sender, command, label, args);
    }

    public boolean reloadZones() {
        try {
            for(WorldManager w : worlds.values())
                w.loadRegions();
            
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean reloadZonesConfig() {
        try {
            ZonesConfig.load(this, new File(getDataFolder().getPath()+"/"+ZonesConfig.ZONES_CONFIG_FILE));
            for(WorldManager w : worlds.values())
                w.loadConfig();
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }
    
    public Database getMysqlDatabase() {
        return database;
    }
    
    public ZoneUtil getUtils() {
        return util;
    }
    
    public ZoneUtil getApi() {
        return getUtils();
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }
    
    public OfflinePlayer matchPlayer(String name) {
        name = name.toLowerCase();
        for(Player player : getServer().getOnlinePlayers()) {
            if(player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return getOfflinePlayer(name);
    }

//    private Map<String, Set<String>> parentGroupCache;
//    private static final long groupCacheTimeout = 60 * 1000;
//    private long lastGroupUpdate;
//    public Set<String> getParentGroups(String group) {
//        if(parentGroupCache == null || (lastGroupUpdate < (System.currentTimeMillis() - groupCacheTimeout))) {
//            parentGroupCache = new HashMap<>();
//            permissionsManager.getGroups();
//        }
//    }
    
    private Map<String, OfflinePlayer> nameToPlayerCache;
    public OfflinePlayer getOfflinePlayer(String playerName) {
        if(nameToPlayerCache == null) {
            nameToPlayerCache = new HashMap<>();
            for(OfflinePlayer p : getServer().getOfflinePlayers()) {
                nameToPlayerCache.put(p.getName().toLowerCase(), p);
            }
        }
        return nameToPlayerCache.get(playerName.toLowerCase());
    }
    
}

