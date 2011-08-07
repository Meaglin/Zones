package com.zones.model.types.normal;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
    public boolean isAllowed(ZoneBase zone, Player player, Block block, int typeId) {
        Log.info(player, "trigger block destroy '" + zone.getName() + "'[" + zone.getId() + "] (" + block.getX() + "," + block.getY() + "," + block.getZ() + ") " + typeId);
        if(!((ZoneNormal)zone).canModify(player, Rights.DESTROY)) {
            zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_DESTROY_BLOCKS_IN_ZONE, player);
            return false;
        } else {
            List<?> list = zone.getSettings().getList(ZoneVar.BREAK_BLOCKS);
            if(list != null && list.contains(typeId) && !((ZoneNormal)zone).canAdministrate(player)) {
                zone.sendMarkupMessage(ZonesConfig.BLOCK_IS_BLACKLISTED, player);
                return false;
            } else {
                return true;
            }
        }
    }

}
