package com.zones.accessresolver.interfaces;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.zones.model.ZoneBase;

public interface PlayerBlockResolver extends MessagebleResolver {
    public boolean isAllowed(ZoneBase zone, Player player, Block block, Material type);
}
