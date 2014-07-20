package com.zones.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.meaglin.json.JSONObject;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;
import com.zones.model.types.ZoneStone;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;
import com.zones.world.WorldManager;

public class ZonesStoneProtectListener implements Listener {
    private Zones plugin;

    public ZonesStoneProtectListener(Zones plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock(); Player p = event.getPlayer();
        WorldManager wm = plugin.getWorldManager(b.getWorld());
        if(!wm.getConfig().isEnabled(ZoneVar.PROTECT_STONE)) {
            return;
        }
        JSONObject val = wm.getConfig().getSetting(ZoneVar.PROTECT_STONE).getJSONObject("value");
        Material mat = b.getType();
        if(!val.has(mat.name())) {
            return;
        }
        List<ZoneNormal> zones = wm.getActiveZones(b);

        for(ZoneNormal zone : zones) {
            if(!(zone instanceof ZoneStone)) {
                continue;
            }
            ZoneStone stone = (ZoneStone) zone;
            if(stone.isCenter(b)) {
                if(stone.canAdministrate(p)) {
                    plugin.getZoneManager().delete(stone);
                    stone.sendMarkupMessage(ZonesConfig.PROTECTED_ZONE_DELETED, p);
                    return;
                }
                stone.sendMarkupMessage(ZonesConfig.PROTECTED_CANNOT_REMOVE, p);
                event.setCancelled(true);
                return;
            }
            if(stone.isCenterUpgrade(b)) {
                if(stone.canAdministrate(p)) {
                    stone.removeUpgrade(b);
                    stone.sendMarkupMessage(ZonesConfig.PROTECTED_ZONE_DELETED, p);
                    return;
                }
                stone.sendMarkupMessage(ZonesConfig.PROTECTED_CANNOT_DEGRADE, p);
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock(); Player p = event.getPlayer();
        WorldManager wm = plugin.getWorldManager(b.getWorld());
        if(!wm.getConfig().isEnabled(ZoneVar.PROTECT_STONE)) {
            return;
        }
        JSONObject val = wm.getConfig().getSetting(ZoneVar.PROTECT_STONE).getJSONObject("value");
        Material mat = b.getType();
        if(!val.has(mat.name())) {
            return;
        }
        if(!plugin.hasPermission(p, "zones.protectionstone." + mat.name())) {
            p.sendMessage(ChatColor.RED + "You are not allowed to create a protectionstone area.");
            return;
        }
        JSONObject obj = val.getJSONObject(mat.name());
        
        int minX = b.getX() - obj.getInt("radiusX"), maxX = b.getX() + obj.getInt("radiusX"), 
        minY = b.getY() - obj.getInt("radiusY"), maxY = b.getY() + obj.getInt("radiusY"), 
        minZ = b.getZ() - obj.getInt("radiusZ"), maxZ = b.getZ() + obj.getInt("radiusZ");
        
        List<ZoneNormal> list = wm.getZones(minX - 1, maxX + 1, minY - 1, maxY + 1, minZ - 1, maxZ + 1);
        for(ZoneNormal zone : list) {
            if(!zone.canAdministrate(p)) {
                zone.sendMarkupMessage(ZonesConfig.PROTECTED_AREA_CONFLICTS, p);
                event.setCancelled(true);
                return;
            }
        }
        
        Zone zoneCfg = new Zone();
        zoneCfg.setName(p.getName() + "'s zone");
        zoneCfg.setZonetype("ZoneNormal");
        zoneCfg.setFormtype("ZoneCuboid");
        zoneCfg.setWorld(p.getWorld().getName());
        zoneCfg.setSize(2);
        zoneCfg.getConfig().put("version", 1);
        zoneCfg.setMinY(minY);
        zoneCfg.setMaxY(maxY);
        
        Vertice v = new Vertice();
        v.setVertexorder(0);
        v.setX(minX);
        v.setZ(minZ);
        zoneCfg.addVertice(v);
        
        v = new Vertice();
        v.setVertexorder(1);
        v.setX(maxX);
        v.setZ(maxZ);
        zoneCfg.addVertice(v);

        JSONObject user = new JSONObject();
        user.put("admin", true);
        user.put("access", "*");
        user.put("name", p.getName());
        user.put("uuid", p.getUniqueId().toString());
        zoneCfg.getConfig().getJSONObject("users").put(p.getUniqueId().toString(), user);
        zoneCfg.getConfig().put("center", (new JSONObject())
                .put("x", b.getX())
                .put("y", b.getY())
                .put("z", b.getZ())
        );
        zoneCfg.saveConfig();
        
        plugin.getMysqlDatabase().save(zoneCfg);
        plugin.getZoneManager().loadFromPersistentData(wm, zoneCfg);
        plugin.getZoneManager().addZone(plugin.getZoneManager().loadFromPersistentData(wm, zoneCfg));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        WorldManager wm = plugin.getWorldManager(event.getBlock().getWorld());
        if(!wm.getConfig().isEnabled(ZoneVar.PROTECT_STONE)) {
            return;
        }
        List<Block> blocks = event.getBlocks();
        JSONObject val = wm.getConfig().getSetting(ZoneVar.PROTECT_STONE).getJSONObject("value");
        for(Block b : blocks) {
            if(val.has(b.getType().name())) {
                ZoneNormal z = wm.getActiveZone(b);
                if(z != null && z instanceof ZoneStone) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if(!event.isSticky()) {
            return;
        }
        Block block = event.getBlock().getRelative(event.getDirection(), 2);
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        if(!wm.getConfig().isEnabled(ZoneVar.PROTECT_STONE)) {
            return;
        }
        JSONObject val = wm.getConfig().getSetting(ZoneVar.PROTECT_STONE).getJSONObject("value");
        Material mat = block.getType();
        if(!val.has(mat.name())) {
            return;
        }
        ZoneNormal z = wm.getActiveZone(block);
        if(z != null && z instanceof ZoneStone) {
            event.setCancelled(true);
            return;
        }
    }
}
