package com.zones.types;

import com.zones.World;
import com.zones.ZoneBase;
import com.zones.Zones;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ZoneNormal extends ZoneBase {

    public ZoneNormal(Zones zones, String world, int id) {
        super(zones, world, id);
    }

    @Override
    public void onEnter(Player player) {
        ZoneBase zone = World.getInstance().getActiveZone(player);
        if (zone == null || zone.getZone().getSize() > getZone().getSize())
            zone = this;

        player.sendMessage("You have just entered " + getName() + "[" + zone.getAccess(player).toColorCode() + "].");
        if (zone.allowHealth(player)) {
            player.sendMessage(ChatColor.RED.toString() + "WARNING: you can die in this zone!");
        }
    }

    @Override
    public void onExit(Player character) {
        character.sendMessage("You have just exited " + getName() + ".");

    }

    @Override
    public boolean allowWater(Block b) {
        return isWaterAllowed();
    }

    @Override
    public boolean allowLava(Block b) {
        return isLavaAllowed();
    }

    @Override
    public boolean allowDynamite(Block b) {
        return isDynamiteAllowed();
    }

    @Override
    public boolean allowHealth(Player player) {
        return isHealthAllowed();
    }

}
