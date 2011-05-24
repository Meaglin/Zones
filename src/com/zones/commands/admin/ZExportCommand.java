package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.forms.ZoneCuboid;

public class ZExportCommand extends ZoneCommand {

    public ZExportCommand(Zones plugin) {
        super("zexport", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!ZonesConfig.WORLDEDIT_ENABLED || getPlugin().getWorldEdit() == null) {
            player.sendMessage(ChatColor.RED + "WorldEdit support needs to be enabled!");
            return true;
        }
        
        ZoneBase zone = getSelectedZone(player);
        ZoneForm form = zone.getForm();
        if(form instanceof ZoneCuboid) {
            Vector pt1 = new Vector(form.getLowX(),form.getLowZ(),form.getLowY());
            Vector pt2 = new Vector(form.getHighX(),form.getHighZ(),form.getHighY());
            CuboidSelection selection = new CuboidSelection(zone.getWorld(), pt1, pt2);
            getPlugin().getWorldEdit().setSelection(player, selection);
            player.sendMessage(ChatColor.GREEN + "Zone " + zone.getName() + " selected as cuboid selection.");
        } else {
            player.sendMessage(ChatColor.RED + "NPoly is not supported yet.");
        }
        return true;
    }

}
