package com.zones.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;
import com.zones.world.WorldManager;


/**
 * 
 * @author Meaglin
 *
 */
public class ZonesVehicleListener implements Listener {

    private Zones plugin;

    public ZonesVehicleListener(Zones zones) {
        this.plugin = zones;
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        Entity attacker = event.getAttacker();
        if (!(attacker instanceof Player))
            return;
        
        Player player = (Player)attacker;

        ZoneNormal zone = plugin.getWorldManager(player).getActiveZone(event.getVehicle().getLocation());
        if (zone != null && zone.canModify(player, Rights.ATTACK)) {
            zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_HIT_ENTITYS_IN_ZONE, player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        Entity entity = event.getVehicle().getPassenger();
        if (entity == null || !(entity instanceof Player))
            return;
        Player player = (Player) entity;
        Location from = event.getFrom();
        Location to = event.getTo();
        /*
         * For the heck of it al:
         * if you're wondering why we use the same world manager for both aZone and bZone it's because as far as i know you cant MOVE to another world
         * and always get teleported.
         */
        WorldManager wm = plugin.getWorldManager(from);
        ZoneNormal aZone = wm.getActiveZone(from);
        ZoneNormal bZone = wm.getActiveZone(to);
        
        if (bZone != null) {
            if(!bZone.canModify(player, Rights.ENTER)) {
                bZone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_ENTER_INTO_ZONE, player);
                /*
                 * In principle this should only occur when someones access to a zone gets revoked when still inside the zone.
                 * This prevents players getting stuck ;).
                 */
                event.getVehicle().setVelocity(new Vector(0,0,0));
                if (aZone != null && !aZone.canModify(player, Rights.ENTER)) {
                    event.getVehicle().teleport(wm.getWorld().getSpawnLocation());
                    event.getVehicle().eject();
                    player.sendMessage(ZonesConfig.PLAYER_ILLIGAL_POSITION);
                    return;
                } 
                player.teleport(from);
                event.getVehicle().teleport(from);
                return;
            } else if (wm.getFlag(ZoneVar.BORDER) 
                    && wm.getConfig().isEnforced(ZoneVar.BORDER)
                    && wm.getConfig().isOutsideBorder(to)
                    && (!wm.getFlag(ZoneVar.BORDER_EXCEMPT_ADMIN) || plugin.hasPermission(player, "zones.override.border"))
                ) {
                if(wm.getConfig().isOutsideBorder(from)) {
                    player.teleport(wm.getWorld().getSpawnLocation());
                    event.getVehicle().teleport(wm.getWorld().getSpawnLocation());
                    event.getVehicle().eject();
                    player.sendMessage(ZonesConfig.PLAYER_ILLIGAL_POSITION);
                    return;
                }
                player.sendMessage(ZonesConfig.PLAYER_REACHED_BORDER);
                player.teleport(from);
                event.getVehicle().teleport(from);
                return;
            }
        } else if(wm.getFlag(ZoneVar.BORDER) 
                && wm.getConfig().isOutsideBorder(to)
                && (!wm.getFlag(ZoneVar.BORDER_EXCEMPT_ADMIN) || plugin.hasPermission(player, "zones.override.border"))
            ) {
            if(wm.getConfig().isOutsideBorder(from)
                && (
                        wm.getConfig().isEnforced(ZoneVar.BORDER) ||
                        aZone == null ||
                        aZone.canModify(player, Rights.ENTER)
            )) {
                player.sendMessage(ZonesConfig.PLAYER_ILLIGAL_POSITION);
                player.teleport(wm.getWorld().getSpawnLocation());
                event.getVehicle().teleport(wm.getWorld().getSpawnLocation());
                event.getVehicle().eject();
                return;
            }
            player.sendMessage(ZonesConfig.PLAYER_REACHED_BORDER);
            player.teleport(from);
            event.getVehicle().teleport(from);
            return;
        }

        plugin.getWorldManager(to).revalidateZones(player, from, to);
    }
}
