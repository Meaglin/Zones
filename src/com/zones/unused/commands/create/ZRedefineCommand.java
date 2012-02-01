package com.zones.unused.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.selection.ZoneEditSelection;
import com.zones.selection.ZoneSelection;
import com.zones.unused.commands.ZoneCommand;

public class ZRedefineCommand extends ZoneCommand {

    public ZRedefineCommand(Zones plugin) {
        super("zredefine", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        if(!ZonesConfig.WORLDEDIT_ENABLED || getPlugin().getWorldEdit() == null) {
            player.sendMessage(ChatColor.RED + "WorldEdit support needs to be enabled!");
            return;
        }
        
        ZoneBase zone = getSelectedZone(player);
        ZoneSelection selection = new ZoneEditSelection(getPlugin(),player,zone.getName());
        if(!selection.importWorldeditSelection()) {
            player.sendMessage(ChatColor.RED + "Invalid/Missing worldedit Selection");
            return;
        }
        
        if(!this.canEdit(player, zone, selection)) {
            player.sendMessage(ChatColor.RED + "You're not allowed to edit this zone.");
            return;
        }

        ZoneBase save = selection.save();
        if(save != null) {
            player.sendMessage(ChatColor.GREEN + "Zone '" + save.getName() + "' redefined.");
        } else {
            player.sendMessage(ChatColor.RED + "Error saving zone.");
        }
    }

}
