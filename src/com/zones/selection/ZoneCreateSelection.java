package com.zones.selection;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneVertice;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;

public class ZoneCreateSelection extends ZoneSelection {

    public ZoneCreateSelection(Zones plugin, Player player, String zoneName) {
        super(plugin, player, zoneName);
    }

    @Override
    public ZoneBase save() {

        Zone pZ = new Zone();
        try {
            pZ.setName(getZoneName());
            pZ.setZonetype(getClassName(getType()));
            pZ.setFormtype(getClassName(getForm()));
            pZ.setMinz(getSelection().getHeight().getMin());
            pZ.setMaxz(getSelection().getHeight().getMax());
            pZ.setWorld(getWorld().getName());
            pZ.setSize(getSelection().getPointsSize());
            pZ.setSettings("");
            pZ.setAdmins("");
            pZ.setUsers("2,default,he");
            getPlugin().getDatabase().save(pZ);
            List<ZoneVertice> points = getSelection().getPoints();
            for (int i = 0; i < getSelection().getPointsSize(); i++) {
                if (points.get(i) == null)
                    continue;
                Vertice v = new Vertice();
                v.setId(pZ.getId());
                v.setVertexorder(i);
                v.setX(points.get(i).getX());
                v.setY(points.get(i).getY());
                v.setZone(pZ);
                pZ.addVertice(v);
            }
            getPlugin().getDatabase().save(pZ.getVertices());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        revertGhostBlocks();
        ZoneBase zone = getZoneManager().loadFromPersistentData(getWorldManager(), pZ);
        if(zone != null) {
            getZoneManager().setSelected(getPlayer().getEntityId(), zone.getId());
            getPlayer().sendMessage(ChatColor.GREEN + "Selected zone '" + zone.getName() + "' .");
        }
        return zone;
    }

}