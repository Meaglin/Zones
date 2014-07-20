package com.zones.test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.zones.Zones;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;

public class BlockTest extends Test {

    public BlockTest(Zones plugin, Player player) {
        super(plugin, player);
    }

    @testFunction
    void location() {
        Location loc = player.getLocation().clone();
        loc.setX(loc.getX() + 20);
        
        ZoneNormal z = plugin.getWorldManager(loc).getActiveZone(loc);
        Expect(zone).equal(z);
        
        loc = player.getLocation().clone();
        z = plugin.getWorldManager(loc).getActiveZone(loc);
        Expect(zone).not().equal(z);
    }
    
    @testFunction
    void placeBlock() {
        Location loc = player.getLocation().clone();
        loc.setX(loc.getX() + 20);
        Block b = loc.getBlock();
        BlockPlaceEvent event = new BlockPlaceEvent(b, null, null, null, player, false);

        plugin.blockListener.onBlockPlace(event);
        Expect(event.isCancelled()).equal(true);
        
        zone.setUser(player, "b");
        event.setCancelled(false);
        plugin.blockListener.onBlockPlace(event);
        Expect(event.isCancelled()).equal(false);
        
        zone.setUser(player, "");
        zone.setAdmin(player, true);
        event.setCancelled(false);
        plugin.blockListener.onBlockPlace(event);
        Expect(event.isCancelled()).equal(false);
        zone.setAdmin(player, false);
    }
    
    @testFunction
    void breakBlock() {
        Location loc = player.getLocation().clone();
        loc.setX(loc.getX() + 20);
        Block b = loc.getBlock();
        BlockBreakEvent event = new BlockBreakEvent(b, player);

        plugin.blockListener.onBlockBreak(event);
        Expect(event.isCancelled()).equal(true);
        
        zone.setUser(player, "d");
        event.setCancelled(false);
        plugin.blockListener.onBlockBreak(event);
        Expect(event.isCancelled()).equal(false);
        
        zone.setUser(player, "");
        zone.setAdmin(player, true);
        event.setCancelled(false);
        plugin.blockListener.onBlockBreak(event);
        Expect(event.isCancelled()).equal(false);
        zone.setAdmin(player, false);
    }
    
    @testFunction
    void fire() {
        Location loc = player.getLocation().clone();
        loc.setX(loc.getX() + 20);
        Block b = loc.getBlock();
        
        zone.getSettings().put(ZoneVar.FIRE.getName(), false);
        Expect(plugin.blockListener.onFire(null, b, null)).equal(true);

        zone.getSettings().put(ZoneVar.FIRE.getName(), true);
        Expect(plugin.blockListener.onFire(null, b, null)).equal(false);
        
        zone.getSettings().remove(ZoneVar.FIRE.getName());
        zone.getWorldManager().getConfig().getSetting(ZoneVar.FIRE).put("value", false);
        Expect(plugin.blockListener.onFire(null, b, null)).equal(true);
        
        zone.getWorldManager().getConfig().getSetting(ZoneVar.FIRE).put("value", true);
        Expect(plugin.blockListener.onFire(null, b, null)).equal(false);
        
        zone.getSettings().put(ZoneVar.FIRE.getName(), false);
        Expect(plugin.blockListener.onFire(null, b, null)).equal(true);
        
        zone.getWorldManager().getConfig().setEnforced(ZoneVar.FIRE, true);
        Expect(plugin.blockListener.onFire(null, b, null)).equal(false);
        
        zone.getWorldManager().getConfig().setEnforced(ZoneVar.FIRE, false);
    }
    
    @testFunction
    void flow() {
        Location loc = player.getLocation().clone();
        loc.setX(loc.getX() + 20);
        Block b = loc.getBlock();
        BlockFromToEvent event = new BlockFromToEvent(b, b.getRelative(1, 0, 0));
        b.setType(Material.WATER);
        Expect(b.getType()).equal(Material.WATER);
        
        zone.getSettings().put(ZoneVar.WATER.getName(), false);
        plugin.blockListener.onBlockFromTo(event);
        Expect(event.isCancelled()).equal(true);
        event.setCancelled(false);
        
        zone.getSettings().put(ZoneVar.WATER.getName(), true);
        plugin.blockListener.onBlockFromTo(event);
        Expect(event.isCancelled()).equal(false);
        
        zone.getSettings().remove(ZoneVar.WATER.getName());
        zone.getWorldManager().getConfig().getSetting(ZoneVar.WATER).put("value", false);
        plugin.blockListener.onBlockFromTo(event);
        Expect(event.isCancelled()).equal(true);
        event.setCancelled(false);
        
        zone.getWorldManager().getConfig().getSetting(ZoneVar.WATER).put("value", true);
        plugin.blockListener.onBlockFromTo(event);
        Expect(event.isCancelled()).equal(false);
        
        zone.getSettings().put(ZoneVar.WATER.getName(), false);
        plugin.blockListener.onBlockFromTo(event);
        Expect(event.isCancelled()).equal(true);
        event.setCancelled(false);
        
        zone.getWorldManager().getConfig().setEnforced(ZoneVar.WATER, true);
        plugin.blockListener.onBlockFromTo(event);
        Expect(event.isCancelled()).equal(false);
        
        zone.getWorldManager().getConfig().getSetting(ZoneVar.WATER).put("value", false);
        plugin.blockListener.onBlockFromTo(event);
        Expect(event.isCancelled()).equal(true);
        
        b.setType(Material.AIR);
        zone.getWorldManager().getConfig().setEnforced(ZoneVar.WATER, false);
        Expect(b.getType()).equal(Material.AIR);
    }
}
