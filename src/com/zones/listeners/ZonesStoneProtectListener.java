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
                    stone.removeCenter(p, b);
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
                    stone.removeUpgrade(p, b);
                    stone.sendMarkupMessage(ZonesConfig.PROTECTED_ZONE_DEGRADED, p);
                    event.setCancelled(true);
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
        Block block = event.getBlock(); Player player = event.getPlayer();
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        if(!wm.getConfig().isEnabled(ZoneVar.PROTECT_STONE)) {
            return;
        }
        JSONObject val = wm.getConfig().getSetting(ZoneVar.PROTECT_STONE).getJSONObject("value");
        Material mat = block.getType();
        if(!val.has(mat.name())) {
            return;
        }
        if(!plugin.hasPermission(player, "zones.protectionstone." + mat.name())) {
            player.sendMessage(ChatColor.RED + "You are not allowed to create a protectionstone area.");
            event.setCancelled(true);
            return;
        }
        JSONObject obj = val.getJSONObject(mat.name());
        
        int rx = obj.getInt("radiusX"), ry = obj.getInt("radiusY"), rz = obj.getInt("radiusZ");
        
        int minX = block.getX() - rx, maxX = block.getX() + rx, 
        minY = block.getY() - ry, maxY = block.getY() + ry, 
        minZ = block.getZ() - rz, maxZ = block.getZ() + rz;
        
        List<ZoneNormal> list = wm.getZones(minX - 1, maxX + 1, minY - 1, maxY + 1, minZ - 1, maxZ + 1);
        for(ZoneNormal zone : list) {
            if(!zone.canAdministrate(player)) {
                zone.sendMarkupMessage(ZonesConfig.PROTECTED_AREA_CONFLICTS, player);
                event.setCancelled(true);
                return;
            }
            if(zone instanceof ZoneStone) {
                ZoneStone stone = (ZoneStone) zone;
                if(stone.isNearCenter(block, 3)) {
                    if(!plugin.hasPermission(player, "zones.protectionstoneupgrade." + mat.name())) {
                        player.sendMessage(ChatColor.RED + "You are not allowed to upgrade a protectionstone area.");
                        event.setCancelled(true);
                        return;
                    }
                    if(!stone.canUpgrade(obj)) {
                        player.sendMessage(ChatColor.RED + "Upgrading this zone would conflict with an other zone.");
                        event.setCancelled(true);
                        return;
                    }
                    stone.addUpgrade(block, obj);
                    stone.sendMarkupMessage(ZonesConfig.PROTECTED_ZONE_UPGRADED, player);
                    return;
                }
            }
        }
        
        Zone zoneCfg = new Zone();
        zoneCfg.setName(player.getName() + "'s zone");
        zoneCfg.setZonetype("ZoneNormal");
        zoneCfg.setFormtype("ZoneCuboid");
        zoneCfg.setWorld(player.getWorld().getName());
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
        user.put("name", player.getName());
        user.put("uuid", player.getUniqueId().toString());
        zoneCfg.getConfig().getJSONObject("users").put(player.getUniqueId().toString(), user);
        zoneCfg.getConfig().put("center", (new JSONObject())
                .put("x", block.getX())
                .put("y", block.getY())
                .put("z", block.getZ())
                .put("xChange", rx)
                .put("yChange", ry)
                .put("zChange", rz)
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
