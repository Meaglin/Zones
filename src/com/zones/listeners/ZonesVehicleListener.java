package com.zones.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.PlayerHitEntityResolver;
import com.zones.accessresolver.interfaces.PlayerLocationResolver;
import com.zones.model.ZoneBase;


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

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if(event.isCancelled()) return;
        
        Entity attacker = event.getAttacker();
        if (!(attacker instanceof Player))
            return;
        
        Player player = (Player)attacker;

        ZoneBase zone = plugin.getWorldManager(player).getActiveZone(event.getVehicle().getLocation());
        if (zone != null && !((PlayerHitEntityResolver)zone.getResolver(AccessResolver.PLAYER_ENTITY_HIT)).isAllowed(zone, player, event.getVehicle(), -1)) {
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
        ZoneBase aZone = wm.getActiveZone(from);
        ZoneBase bZone = wm.getActiveZone(to);
        
        if (bZone != null) {
            if(!((PlayerLocationResolver)bZone.getResolver(AccessResolver.PLAYER_ENTER)).isAllowed(bZone, player, from, to)) {
                ((PlayerLocationResolver)bZone.getResolver(AccessResolver.PLAYER_ENTER)).sendDeniedMessage(bZone, player);
                /*
                 * In principle this should only occur when someone's access to a zone gets revoked when still inside the zone.
                 * This prevents players getting stuck ;).
                 */
                if (aZone != null && !((PlayerLocationResolver)aZone.getResolver(AccessResolver.PLAYER_ENTER)).isAllowed(aZone, player, from, to)) {
                    event.getVehicle().teleport(wm.getWorld().getSpawnLocation());
                    //wm.revalidateZones(player, from, player.getWorld().getSpawnLocation());
                    event.getVehicle().eject();
                    player.sendMessage(ZonesConfig.PLAYER_ILLIGAL_POSITION);
                    return;
                } 
                player.teleport(from);
                event.getVehicle().teleport(from);
                return;
            } else if (wm.getConfig().BORDER_ENABLED && wm.getConfig().BORDER_ENFORCE) {
                if(wm.getConfig().isOutsideBorder(to) && (!wm.getConfig().BORDER_OVERRIDE_ENABLED || !plugin.getPermissions().canUse(player, wm.getWorldName(), "zones.override.border"))) {
                    if(wm.getConfig().isOutsideBorder(from)) {
                        event.getVehicle().teleport(wm.getWorld().getSpawnLocation());
                        //wm.revalidateZones(player, from, wm.getWorld().getSpawnLocation());
                        event.getVehicle().eject();
                        player.sendMessage(ZonesConfig.PLAYER_ILLIGAL_POSITION);
                        return;
                    }
                    player.sendMessage(ZonesConfig.PLAYER_REACHED_BORDER);
                    player.teleport(from);
                    event.getVehicle().teleport(from);
                    return;
                }
            }
        } else if(wm.getConfig().BORDER_ENABLED) {
            if(wm.getConfig().isOutsideBorder(to) && (!wm.getConfig().BORDER_OVERRIDE_ENABLED || !plugin.getPermissions().canUse(player, wm.getWorldName(), "zones.override.border"))) {
                if(wm.getConfig().isOutsideBorder(from)) {
                    event.getVehicle().teleport(wm.getWorld().getSpawnLocation());
                    player.sendMessage(ZonesConfig.PLAYER_ILLIGAL_POSITION);
                    //wm.revalidateZones(player, from, wm.getWorld().getSpawnLocation());
                    event.getVehicle().eject();
                    return;
                }
                player.sendMessage(ZonesConfig.PLAYER_REACHED_BORDER);
                player.teleport(from);
                event.getVehicle().teleport(from);
                return;
            } 
        }

        plugin.getWorldManager(to).revalidateZones(player, from, to);
    }
}
