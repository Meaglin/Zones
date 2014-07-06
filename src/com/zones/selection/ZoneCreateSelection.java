package com.zones.selection;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.meaglin.json.JSONObject;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneVertice;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;
import com.zones.util.Log;

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
            pZ.getConfig().put("version", 1);
            pZ.getConfig().getJSONObject("groups").put(ZonesConfig.DEFAULT_GROUP, new JSONObject("{ access: \"e\"}"));
            pZ.getConfig().getJSONObject("groups").put("user", new JSONObject("{ access: \"ah\"}"));
            //getPlugin().getDatabase().save(pZ);
            List<ZoneVertice> points = getSelection().getPoints();
            for (int i = 0; i < getSelection().getPointsSize(); i++) {
                if (points.get(i) == null)
                    continue;
                Vertice v = new Vertice();
                //v.setId(pZ.getId());
                v.setVertexorder(i);
                v.setX(points.get(i).getX());
                v.setY(points.get(i).getY());
                v.setZone(pZ);
                pZ.addVertice(v);
            }
            getPlugin().getMysqlDatabase().save(pZ);
            //getPlugin().getDatabase().save(pZ);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        revertGhostBlocks();
        ZoneBase zone = getZoneManager().loadFromPersistentData(getWorldManager(), pZ);
        if(zone != null) {
            getZoneManager().setSelected(getPlayer().getEntityId(), zone.getId());
            getPlayer().sendMessage(ChatColor.GREEN + "Selected zone '" + zone.getName() + "' .");
            Log.info(getPlayer().getName() + " created zone " + zone.getName() + "[" + zone.getId() + "]");
        }
        return zone;
    }

}