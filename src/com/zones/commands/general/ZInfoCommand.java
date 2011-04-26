package com.zones.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.settings.Serializer;
import com.zones.model.settings.ZoneVar;

public class ZInfoCommand extends ZoneCommand {

    public ZInfoCommand(Zones plugin) {
        super("zinfo", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        ZoneBase b = getSelectedZone(player);
        player.sendMessage(ChatColor.GREEN + "Zone: " + b.getName() + "[" + b.getAccess(player).toColorCode() + "]" );
        ZoneForm f = b.getZone();
        player.sendMessage(ChatColor.AQUA + "Size: " + f.getSize() + " " +
        		"Location: (" + f.getLowX() + "-" + f.getHighX() + "," + f.getLowY() + "-" + f.getHighY() + "," + f.getLowZ() + "-" + f.getHighZ() + ")");
        
        String bools = "";
        for(ZoneVar v : ZoneVar.values()) {
            if(v.getType().equals(Boolean.class)) {
                if(b.getSettings().get(v) != null) {
                    bools += " " + (b.getSettings().getBool(v) ? ChatColor.GREEN : ChatColor.RED) + v.getName();
                }
            }
        }
        if(!bools.equals(""))
            player.sendMessage(ChatColor.AQUA + "Booleans:" + bools);
        
        String settings = "";
        for(ZoneVar v : ZoneVar.values()) {
            if(!v.getType().equals(Boolean.class)) {
                if(b.getSettings().get(v) != null) {
                    settings += " " + ChatColor.BLUE + v.getName() + ":" + ChatColor.WHITE + Serializer.unEscape(v.serialize(b.getSettings().get(v)));
                }
            }
        }
        if(!settings.equals("")) 
            player.sendMessage(ChatColor.AQUA + "Settings:" + settings);
        
        return true;
    }

}
