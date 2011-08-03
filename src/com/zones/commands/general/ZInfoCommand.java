package com.zones.commands.general;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Region;
import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.settings.Serializer;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneInherit;

public class ZInfoCommand extends ZoneCommand {

    public ZInfoCommand(Zones plugin) {
        super("zinfo", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        ZoneBase b = getSelectedZone(player);
        player.sendMessage(ChatColor.DARK_GREEN + "Zone: " + b.getName() + ChatColor.BLUE + "(" + b.getId() + ")" + ChatColor.WHITE + "[" + b.getAccess(player).toColorCode() + "]" );
        ZoneForm f = b.getForm();
        player.sendMessage(ChatColor.AQUA + "Type: " + getClassName(b.getClass()) + " Form: " + getClassName(f.getClass()));
        player.sendMessage(ChatColor.AQUA + "Size: " + f.getSize() + " (" + Math.abs(f.getHighX()-f.getLowX()) + "," + Math.abs(f.getHighY()-f.getLowY()) + "," + Math.abs(f.getHighZ()-f.getLowZ()) + ")" );
        player.sendMessage(ChatColor.AQUA + "Location: (" + f.getLowX() + "," + f.getHighX() + ";" + f.getLowY() + "," + f.getHighY() + ";" + f.getLowZ() + "," + f.getHighZ() + ")");
        
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
        Region min = b.getWorldManager().getRegion(b.getForm().getLowX(),b.getForm().getLowZ());
        Region max = b.getWorldManager().getRegion(b.getForm().getHighX(),b.getForm().getHighZ());
        player.sendMessage(ChatColor.AQUA + "Region: " +  "(" + min.getX() + "," + max.getX() + ";" + min.getY() + "," + max.getY() + ")" );
        if(b instanceof ZoneInherit) {
            List<ZoneBase> inherits = ((ZoneInherit)b).getInheritedZones();
            if(inherits.size() > 0) {
                String message = "";
                for(ZoneBase zone : inherits) {
                    message += ", " + zone.getName() + "[" + zone.getId() + "]";
                }
                player.sendMessage(ChatColor.AQUA + "InheritedZones: " + message.substring(2));
            } else {
                player.sendMessage(ChatColor.AQUA + "InheritedZones: None.");
            }
        }
    }

    public static String getClassName(Class<?> c) {
        String className = c.getName();
        int firstChar;
        firstChar = className.lastIndexOf ('.') + 1;
        if ( firstChar > 0 ) {
            className = className.substring ( firstChar );
        }
        return className;
    }
}
