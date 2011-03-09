package com.zones.listeners;

import com.zones.World;
import com.zones.ZoneType;
import com.zones.Zones;
import com.zones.ZonesAccess;
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

/**
 * 
 * @author Meaglin
 */
public class ZonesVehicleListener extends VehicleListener {

    @SuppressWarnings("unused")
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
    public void onVehicleDamage(VehicleDamageEvent event) {
        Entity attacker = event.getAttacker();
        if (!(attacker instanceof Player))
            return;

        ZoneType z = World.getInstance().getActiveZone(event.getVehicle().getLocation());
        if (z != null && !z.canModify((Player) attacker, ZonesAccess.Rights.HIT)) {
            ((Player) attacker).sendMessage("You cannot damage vehicles in '" + z.getName() + "'!");
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
    public void onVehicleMove(VehicleMoveEvent event) {
        Entity entity = event.getVehicle().getPassenger();
        if (entity == null || !(entity instanceof Player))
            return;
        Player player = (Player) entity;

        Location from = event.getFrom();
        Location to = event.getTo();
        ZoneType aZone = World.getInstance().getActiveZone(from);
        ZoneType bZone = World.getInstance().getActiveZone(to);
        if (bZone != null && ((aZone != null && aZone.getId() != bZone.getId() && !bZone.canModify(player, ZonesAccess.Rights.ENTER)) || (aZone == null && !bZone.canModify(player, ZonesAccess.Rights.ENTER)))) {
            player.teleportTo(from);
            player.sendMessage(ChatColor.RED.toString() + "You can't enter " + bZone.getName() + ".");
            // we don't have to do overall revalidation if the player gets
            // warped back to his previous location.
            return;
        }
        if (bZone != null && !bZone.canModify(player, ZonesAccess.Rights.ENTER)) {
            event.getVehicle().eject();
            player.teleportTo(player.getWorld().getSpawnLocation());
            player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
        }

        World.getInstance().revalidateZones(player, from, to);
    }
}
