package com.zones.model.types;

import org.bukkit.entity.Player;

import com.zones.model.settings.ZoneVar;

public class ZoneCraftCity extends ZonePlot {

    @Override
    protected void doClaim(Player player) {
        this.getSettings().put(ZoneVar.ENTER_MESSAGE.getName(), (String) null);
        this.getSettings().put(ZoneVar.LEAVE_MESSAGE.getName(), "NONE");
        this.saveSettings();
        
        super.doClaim(player);
    }
    
    @Override
    public void unclaim(Player player) {
        this.getSettings().put(ZoneVar.ENTER_MESSAGE.getName(), "Claim {zname} using /zclaim.");
        this.getSettings().put(ZoneVar.LEAVE_MESSAGE.getName(), "NONE");
        this.saveSettings();
        
        super.unclaim(player);
    }
}
