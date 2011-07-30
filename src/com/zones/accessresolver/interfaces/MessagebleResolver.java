package com.zones.accessresolver.interfaces;

import org.bukkit.entity.Player;

import com.zones.model.ZoneBase;

public interface MessagebleResolver extends Resolver {
    public void sendDeniedMessage(ZoneBase zone, Player player);
}
