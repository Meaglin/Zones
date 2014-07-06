package com.zones.model.types.normal;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.meaglin.json.JSONObject;
import com.zones.ZonesConfig;
import com.zones.accessresolver.interfaces.PlayerBlockResolver;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;
import com.zones.util.Log;

public class NormalPlayerBlockDestroyResolver implements PlayerBlockResolver {

    @Override
    public void sendDeniedMessage(ZoneBase zone, Player player) {
    }

    @Override
    public boolean isAllowed(ZoneBase zone, Player player, Block block, Material type) {
        Log.info(player, "trigger block destroy '" + zone.getName() + "'[" + zone.getId() + "] (" + block.getX() + "," + block.getY() + "," + block.getZ() + ") " + type);
        if(!((ZoneNormal)zone).canModify(player, Rights.DESTROY)) {
            zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_DESTROY_BLOCKS_IN_ZONE, player);
            return false;
        } else {
            JSONObject settings = zone.getConfig().getJSONObject("settings");
            type = type == null ? block.getType() : type;
            
            if(settings.has(ZoneVar.BREAK_BLOCKS.getName())
                    && !((ZoneNormal)zone).canAdministrate(player)
                    && settings.getJSONArray(ZoneVar.BREAK_BLOCKS.getName()).contains(type.name())) {
                zone.sendMarkupMessage(ZonesConfig.BLOCK_IS_BLACKLISTED, player);
                return false;
            }
        }
        return true;
    }

}
