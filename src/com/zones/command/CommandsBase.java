package com.zones.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZoneBase;
import com.zones.model.types.ZoneInherit;
import com.zones.selection.ZoneSelection;

public abstract class CommandsBase {
    protected Zones plugin;
    
    public CommandsBase(Zones plugin) {
        this.plugin = plugin;
    }
    
    protected boolean canUseCommand(CommandSender sender, String command) {
        return (sender instanceof Player ? canUseCommand((Player)sender, command) : true);
    }
    
    protected boolean canUseCommand(Player player, String command) {
        return getPlugin().getPermissions().has(player, command);
    }
    
    protected ZoneSelection getZoneSelection(Player p) {
        return getPlugin().getZoneManager().getSelection(p.getEntityId());
    }
    
    protected ZoneBase getSelectedZone(Player player) {
        return plugin.getZoneManager().getSelectedZone(player.getEntityId());
    }
    
    protected boolean hasSelected(Player p) {
        return getSelectedZone(p) != null;
    }
    
    protected boolean canEdit(Player p, ZoneBase base, ZoneSelection sel) {
        if(this.canUseCommand(p, "zones.create")) return true;
        if(!this.hasSelected(p)) return false;
        ZoneBase zone = getSelectedZone(p);
        if(!(zone instanceof ZoneInherit)) return false;
        List<ZoneBase> zones = ((ZoneInherit)zone).getInheritedZones();
        for(ZoneBase z : zones) {
            if(z instanceof ZoneInherit && ((ZoneInherit)z).isAdmin(p) && z.getForm().contains(sel.getSelection())) {
                return true;
            }
        }
        
        return false;
    }
    
    protected Zones getPlugin() {
        return plugin;
    }
}
