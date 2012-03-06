package com.zones.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.zones.Zones;

public class ZonesWeatherListener implements Listener {
    
    private Zones plugin;
    public ZonesWeatherListener(Zones plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onThunderChange(org.bukkit.event.weather.ThunderChangeEvent event) {
        if(!event.toThunderState()) return;
        
        if(!plugin.getWorldManager(event.getWorld()).getConfig().WEATHER_THUNDER_ENABLED)
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onWeatherChange(org.bukkit.event.weather.WeatherChangeEvent event) {
        if(!event.toWeatherState()) return;
        
        if(!plugin.getWorldManager(event.getWorld()).getConfig().WEATHER_RAIN_ENABLED)
            event.setCancelled(true);
    }
}
