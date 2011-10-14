package com.zones.listeners;

import org.bukkit.event.weather.WeatherListener;

import com.zones.Zones;

public class ZonesWeatherListener extends WeatherListener {
    
    private Zones plugin;
    public ZonesWeatherListener(Zones plugin) {
        this.plugin = plugin;
    }
    
    public void onThunderChange(org.bukkit.event.weather.ThunderChangeEvent event) {
        if(event.isCancelled()) return;
        if(!event.toThunderState()) return;
        
        if(!plugin.getWorldManager(event.getWorld()).getConfig().WEATHER_THUNDER_ENABLED)
            event.setCancelled(true);
    }
    
    public void onWeatherChange(org.bukkit.event.weather.WeatherChangeEvent event) {
        if(event.isCancelled()) return;
        if(!event.toWeatherState()) return;
        
        if(!plugin.getWorldManager(event.getWorld()).getConfig().WEATHER_RAIN_ENABLED)
            event.setCancelled(true);
    }
}
