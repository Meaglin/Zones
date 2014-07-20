package com.zones.listeners;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.zones.Zones;
import com.zones.model.settings.ZoneVar;
import com.zones.world.WorldManager;

public class ZonesWeatherListener implements Listener {
    
    private Zones plugin;
    private Random random = new Random();
    
    public ZonesWeatherListener(Zones plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onThunderChange(org.bukkit.event.weather.ThunderChangeEvent event) {
        if(!event.toThunderState()) {
            return;
        }
        
        WorldManager wm = plugin.getWorldManager(event.getWorld());
        if(!wm.getConfig().getFlag(ZoneVar.THUNDER)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onWeatherChange(org.bukkit.event.weather.WeatherChangeEvent event) {
        if(!event.toWeatherState()) {
            return;
        }
        
        WorldManager wm = plugin.getWorldManager(event.getWorld());
        if(!wm.getConfig().getFlag(ZoneVar.RAIN)) {
            event.setCancelled(true);
            return;
        }
        
        if(wm.getConfig().isEnabled(ZoneVar.RAIN_DIVIDER)) {
            if(random.nextInt(wm.getConfig().getSetting(ZoneVar.RAIN_DIVIDER).getInt("value")) != 0) {
                event.setCancelled(true);
            }
        }
    }
}
