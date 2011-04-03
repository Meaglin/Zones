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

import com.zones.ZoneBase;
import com.zones.Zones;


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
        Entity attacker = event.getAttacker();
        if (!(attacker instanceof Player))
            return;
        
        Player player = (Player)attacker;

        ZoneBase zone = plugin.getWorldManager().getActiveZone(event.getVehicle().getLocation());
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
        
        ZoneBase aZone = plugin.getWorldManager().getActiveZone(from);
        ZoneBase bZone = plugin.getWorldManager().getActiveZone(to);
        if (bZone != null && !bZone.allowEnter(player, to)) {
            event.getVehicle().teleport(from);
            player.sendMessage(ChatColor.RED.toString() + "You can't enter " + bZone.getName() + ".");
            if (aZone != null && !aZone.allowEnter(player, from)) {
                event.getVehicle().teleport(player.getWorld().getSpawnLocation());
                player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
                plugin.getWorldManager().revalidateZones(player, from, player.getWorld().getSpawnLocation());
            } 
            // we don't have to do overall revalidation if the player gets
            // warped back to his previous location.
            return;
        }

        plugin.getWorldManager().revalidateZones(player, from, to);
    }
}
