package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneVertice;

public class ZRedefineCommand extends ZoneCommand {

    public ZRedefineCommand(Zones plugin) {
        super("zredefine", plugin);
        this.setRequiresSelected(true);
        this.setRequiredAccess("zones.create");
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!ZonesConfig.WORLDEDIT_ENABLED || getPlugin().getWorldEdit() == null) {
            player.sendMessage(ChatColor.RED + "WorldEdit support needs to be enabled!");
            return true;
        }
        if(vars.length < 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /zredefine");
            return true;
        }
        Selection selection = getPlugin().getWorldEdit().getSelection(player);
        if(selection == null) {
            player.sendMessage(ChatColor.RED + "No WorldEdit selection found.");
            return true;
        }
        if(selection.getArea() < 1) {
            player.sendMessage(ChatColor.RED + "Your WorldEdit selection is not a valid selection.");
        }
        ZoneBase zone = getSelectedZone(player);
        ZoneVertice point1 = new ZoneVertice(selection.getMinimumPoint().getBlockX(), selection.getMinimumPoint().getBlockZ());
        ZoneVertice point2 = new ZoneVertice(selection.getMaximumPoint().getBlockX(), selection.getMaximumPoint().getBlockZ());
        ZoneVertice height = new ZoneVertice(selection.getMinimumPoint().getBlockY(), selection.getMaximumPoint().getBlockY());

        ZonesDummyZone dummy = new ZonesDummyZone(getPlugin(),player,zone.getName());
        //dummy.loadEdit(getSelectedZone(player));
        dummy.clearCoords();
        dummy.addCoords(point1);
        dummy.addCoords(point2);
        dummy.setZ(height);
        if(dummy.merge()) {
            player.sendMessage(ChatColor.GREEN + "Zone '" + zone.getName() + "' redefined.");
        } else {
            player.sendMessage(ChatColor.RED + "Error saving zone.");
        }
        return false;
    }

}
