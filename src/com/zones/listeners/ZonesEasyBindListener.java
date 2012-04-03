package com.zones.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.easybind.listeners.EasyBindEvent;
import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.model.ZoneBase;

public class ZonesEasyBindListener implements Listener {

    private Zones plugin;

    public ZonesEasyBindListener(Zones plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void zoneTool(EasyBindEvent event){
        if(!event.getName().equals("zones")) return;
        Player player = event.getPlayer();
        if(event.getTriggerEvent().getClickedBlock() != null) {
            Block block = event.getTriggerEvent().getClickedBlock();
            WorldManager wm = plugin.getWorldManager(player.getWorld());
            List<ZoneBase> zones = wm.getActiveZones(block.getX(), block.getZ(), block.getY());
            if(zones.size() > 0) {
                player.sendMessage(ChatColor.DARK_GREEN + "Permission:" + wm.getActiveZone(block).getAccess(player).toColorCode() + ", zones found:");
                String str = "";
                for(ZoneBase zone : zones) {
                    str += "," + zone.getName() + "[" + zone.getId() + "]";
                }
                player.sendMessage(ChatColor.AQUA + str.substring(1));
            } else {
                player.sendMessage(ChatColor.GREEN + "No zones found.");
            }
            event.setCancelled(true);
            return;
        }
    }
}
