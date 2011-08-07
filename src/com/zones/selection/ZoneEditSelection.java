package com.zones.selection;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;
import com.zones.model.forms.ZoneCuboid;
import com.zones.model.forms.ZoneNPoly;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;
import com.zones.util.Log;

public class ZoneEditSelection extends ZoneSelection {

    public ZoneEditSelection(Zones plugin, Player player, String zoneName) {
        super(plugin, player, zoneName);
        loadEdit();
    }
    
    private void loadEdit() {
        ZoneForm form = getSelectedZone().getForm();
        if(form instanceof ZoneCuboid) {
            CuboidSelection selection = new CuboidSelection(this);
            selection.setPoint1(new ZoneVertice(form.getLowX(), form.getLowY()));
            selection.setPoint2(new ZoneVertice(form.getHighX(), form.getHighY()));
            selection.setHeight(new ZoneVertice(form.getLowZ(), form.getHighZ()), true);
            setSelection(selection);
        } else if(form instanceof ZoneNPoly) {
            NPolySelection selection = new NPolySelection(this);
            int[][] points = form.getPoints();
            for(int i = 0; i < form.getPointsSize(); i++) {
                selection.addPoint(new ZoneVertice(points[0][i], points[1][i]));
            }
            selection.setHeight(new ZoneVertice(form.getLowZ(), form.getHighZ()), true);
            setSelection(selection);
        }
        setClass(getSelectedZone().getClass());
    }
    

    @Override
    public ZoneBase save() {
        ZoneBase z = getSelectedZone();
        if (z == null)
            return null;
        
        
        
        Zone pZ = z.getPersistence();
        try {
            pZ.setFormtype(getClassName(getForm()));
            pZ.setZonetype(getClassName(getType()));
            pZ.setWorld(getWorld().getName());
            pZ.setMinz(getSelection().getHeight().getMin());
            pZ.setMaxz(getSelection().getHeight().getMax());
            pZ.setSize(getSelection().getPointsSize());
            getPlugin().getDatabase().execute(getPlugin().getDatabase().createCallableSql("DELETE FROM zones_vertices WHERE id  = " + pZ.getId() + ""));
            //getPlugin().getDatabase().delete(getPlugin().getDatabase().find(Vertice.class).where().eq("id", pZ.getId()).findList());
            pZ.clearVertices();
            List<ZoneVertice> points = getSelection().getPoints();
            for (int i = 0; i < points.size(); i++) {
                if (points.get(i) == null)
                    continue;
                Vertice v = new Vertice();
                v.setId(z.getId());
                v.setVertexorder(i);
                v.setX(points.get(i).getX());
                v.setY(points.get(i).getY());
                v.setZone(pZ);
                pZ.addVertice(v);
            }
            getPlugin().getDatabase().update(pZ);
            getPlugin().getDatabase().save(pZ.getVertices());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        getZoneManager().removeZone(z);
        revertGhostBlocks();
        ZoneBase zone = getZoneManager().loadFromPersistentData(getWorldManager(), pZ);
        if(zone != null) {
            getZoneManager().setSelected(getPlayer().getEntityId(), zone.getId());
            getPlayer().sendMessage(ChatColor.GREEN + "Selected zone '" + zone.getName() + "' .");
            Log.info(getPlayer().getName() + " resized zone " + zone.getName() + "[" + zone.getId() + "]");
        }
        return zone;
    }

}
