package com.zones.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.model.ZoneBase;


/**
 * 
 * @author Meaglin
 *
 */
public class ZonesVehicleListener extends VehicleListener {

    private Zones plugin;

    public ZonesVehicleListener(Zones zones) {
        this.plugin = zones;
    }

    /**
     * Called when a vehicle is created by a player. This hook will be called
     * for all vehicles created.
     * 
     * @param event
     */
    public void onVehicleCreate(VehicleCreateEvent event) {
    }

    /**
     * Called when a vehicle is damaged by the player.
     * 
     * @param event
     */
    @Override
    public void onVehicleDamage(VehicleDamageEvent event) {
        if(event.isCancelled()) return;
        
        Entity attacker = event.getAttacker();
        if (!(attacker instanceof Player))
            return;
        
        Player player = (Player)attacker;

        ZoneBase zone = plugin.getWorldManager(player).getActiveZone(event.getVehicle().getLocation());
        if (zone != null && !zone.allowEntityHit(player, event.getVehicle())) {
            player.sendMessage("You cannot damage vehicles in '" + zone.getName() + "'!");
            event.setCancelled(true);
        }
    }

    /**
     * Called when a vehicle collides with a block.
     * 
     * @param event
     */
    public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
    }

    /**
     * Called when a vehicle collides with an entity.
     * 
     * @param event
     */
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
    }

    /**
     * Called when an entity enters a vehicle.
     * 
     * @param event
     */
    public void onVehicleEnter(VehicleEnterEvent event) {
    }

    /**
     * Called when an entity exits a vehicle.
     * 
     * @param event
     */
    public void onVehicleExit(VehicleExitEvent event) {
    }

    /**
     * Called when an vehicle moves.
     * 
     * @param event
     */
    @Override
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
            if(!bZone.allowEnter(player, to)) {
                event.getVehicle().teleport(from);
                player.sendMessage(ChatColor.RED.toString() + "You can't enter " + bZone.getName() + ".");
                /*
                 * In principle this should only occur when someone's access to a zone gets revoked when still inside the zone.
                 * This prevents players getting stuck ;).
                 */
                if (aZone != null && !aZone.allowEnter(player, from)) {
                    event.getVehicle().teleport(wm.getWorld().getSpawnLocation());
                    player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
                    wm.revalidateZones(player, from, player.getWorld().getSpawnLocation());
                } 
                return;
            } else if (wm.getConfig().BORDER_ENABLED && wm.getConfig().BORDER_ENFORCE && !plugin.getPermissions().canUse(player, "zones.override.border")) {
                if(wm.getConfig().isOutsideBorder(to)) {
                    if(wm.getConfig().isOutsideBorder(from)) {
                        event.getVehicle().teleport(wm.getWorld().getSpawnLocation());
                        player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
                        wm.revalidateZones(player, from, wm.getWorld().getSpawnLocation());
                        return;
                    }
                    player.sendMessage(ChatColor.RED + "You have reached the border.");
                    event.getVehicle().teleport(from);
                    return;
                }
            }
        } else if(wm.getConfig().BORDER_ENABLED && !plugin.getPermissions().canUse(player, "zones.override.border")) {
            if(wm.getConfig().isOutsideBorder(to)) {
                if(wm.getConfig().isOutsideBorder(from)) {
                    event.getVehicle().teleport(wm.getWorld().getSpawnLocation());
                    player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
                    wm.revalidateZones(player, from, wm.getWorld().getSpawnLocation());
                    return;
                }
                player.sendMessage(ChatColor.RED + "You have reached the border.");
                event.getVehicle().teleport(from);
                return;
            } 
        }

        plugin.getWorldManager(to).revalidateZones(player, from, to);
    }
}
