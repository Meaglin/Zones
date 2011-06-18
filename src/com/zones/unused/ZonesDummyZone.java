package com.zones.unused;
/*
import com.zones.model.GhostBlock;
import com.zones.model.WorldConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;
import com.zones.model.forms.ZoneCuboid;
import com.zones.model.forms.ZoneNPoly;
import com.zones.model.types.ZoneInherit;
import com.zones.model.types.ZoneNormal;
import com.zones.model.types.ZonePlot;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * 
 * @author Meaglin
 *
 *

public class ZonesDummyZone {

    public enum Confirm {
        SAVE,
        MERGE,
        STOP,
        NONE
    }
    
    public enum Mode {
        NEW,
        EDIT
    }
    
    private final Zones               plugin;
    private final Player              player;

    private final String              name;
    private Confirm                   confirm = Confirm.NONE;
    private Mode                mode;

    private ZoneVertice               height;
    private ArrayList<ZoneVertice>    coords;

    private Class<? extends ZoneBase> type    = ZoneNormal.class;
    private Class<? extends ZoneForm> form    = ZoneCuboid.class;
    
    
    private boolean cuiEnabled = false;
    private static final String CUI = "§5§6§4§5";
    
    
    private ZoneBase inheritedZone = null;
    
    private List<GhostBlock> revertBlocks;
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    public ZonesDummyZone(Zones plugin, Player player,String name) {
        this.plugin = plugin;
        this.player = player;
        this.mode = Mode.NEW;
        this.name = name;
        height = new ZoneVertice(0,130);
        coords = new ArrayList<ZoneVertice>();
        revertBlocks = new ArrayList<GhostBlock>();
        //sendCUIHandShake();
    }

    public World getWorld() { return player.getWorld(); }
    public Zones getPlugin() { return plugin; }
    public Player getPlayer() { return player; }
    public ZoneManager getZoneManager() { return plugin.getZoneManager(); }
    public ZoneBase getSelectedZone() { return getZoneManager().getSelectedZone(player.getEntityId()); }
    public WorldManager getWorldManager() { return getPlugin().getWorldManager(getWorld()); }
    public WorldConfig getWorldConfig() { return getWorldManager().getConfig(); }
    
    public String getName() { return name; }
    public Class<? extends ZoneBase> getType() { return type; }
    public Class<? extends ZoneForm> getForm() { return form; }
    
    
    public void setZ(int min , int max) { setZ(new ZoneVertice(min,max)); }
    public void setZ(ZoneVertice vertice) {
        if(hasInherited()) {
            if(inheritedZone.getZone().getHighZ() < vertice.getMax() || inheritedZone.getZone().getLowZ() > vertice.getMin()) {
                player.sendMessage(ChatColor.RED + "Cannot create a selection outside your zone.");
                return;
            }
        }
        height = vertice;
        getPlayer().sendMessage(ChatColor.GREEN + "Selection height now changed to: " + getMin() + " - " + getMax() + ".");
        updateCUISelection();
    }

    public int getMax() {
        return height.getMax();
    }

    public int getMin() {
        return height.getMin();
    }

    public ArrayList<ZoneVertice> getCoords() {
        return coords;
    }

    public void setInherited(ZoneBase inheritedZone) {
        if(inheritedZone == null)return;
        type = ZoneInherit.class;
        this.inheritedZone = inheritedZone;
    }
    public boolean hasInherited() { return inheritedZone != null; }
    public boolean insideInherited(ZoneVertice z) { 
        if(!hasInherited()) return true;
        return inheritedZone.getZone().isInsideZone(z.getX(), z.getY());
    }
    
    public boolean addCoords(int x, int y) { return addCoords(new ZoneVertice(x,y)); }
    public boolean addCoords(ZoneVertice z) {
        if(!insideInherited(z)) {
            player.sendMessage(ChatColor.RED + "Cannot create a selection outside your zone.");
            return false;
        }
        coords.add(z);
        updateCUISelection();
        player.sendMessage(ChatColor.GREEN + "Succefully added point (" + z.getX() + "," + z.getY() + ") to the selection.");
        return true;
    }

    public void removeCoords(int x, int y) { removeCoords(new ZoneVertice(x,y)); }
    public void removeCoords(ZoneVertice z) {
        coords.remove(z);
        updateCUISelection();
    }

    public boolean containsCoords(int x, int y) {
        for(ZoneVertice v : coords)
            if(v.getX() == x && v.getY() == y)
                return true;
        return false;
    }
    public boolean containsCoords(ZoneVertice z) {
        return coords.contains(z);
    }
    
    public void clearCoords() {
        coords.clear();
        this.revertBlocks();
    }
    
    public void setConfirm(Confirm confirm) {
        this.confirm = confirm;
    }

    public void confirm() {
        switch(confirm) {
            case STOP:
                getZoneManager().removeDummy(getPlayer().getEntityId());
                revertBlocks();
                getPlayer().sendMessage(ChatColor.RED + "Zone creation mode stopped, work deleted.");
                break;
            case SAVE:
                if (save() != null) {
                    getZoneManager().removeDummy(getPlayer().getEntityId());
                    getPlayer().sendMessage(ChatColor.GREEN + "Zone Saved.");
                } else {
                    getPlayer().sendMessage(ChatColor.RED + "Error saving zone.");
                }
                break;
            case MERGE:
                if (merge()) {
                    getZoneManager().removeDummy(getPlayer().getEntityId());
                    getPlayer().sendMessage(ChatColor.GREEN + "Zone merged.");
                } else {
                    getPlayer().sendMessage(ChatColor.RED + "Error merging zone.");
                }
                break;
            default:
                getPlayer().sendMessage(ChatColor.YELLOW + "Nothing to confirm.");
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void setClass(String name) {
        
        Class<?> newtype = null;
        try {
            newtype = Class.forName("com.zones.model.types." + name);
        } catch (Exception e) {
            getPlayer().sendMessage("No such zone class: " + name);
            return;
        }
        if(newtype != null) {
            if(hasInherited())  {
                if(!ZoneInherit.class.isAssignableFrom(newtype)) {
                    getPlayer().sendMessage(ChatColor.RED + "You cannot change the zone type when making an subzone.");
                    return;
                }
            } else if(!ZoneBase.class.isAssignableFrom(newtype)) {
                player.sendMessage(ChatColor.RED + "Invalid zone type '" + name + "'!");
                return;
            }
            type = (Class<? extends ZoneBase>) newtype;
            getPlayer().sendMessage(ChatColor.GREEN + "Zone Type succesfully changed to " + type.getName() + ".");
        } else {
            getPlayer().sendMessage(ChatColor.RED + "Error changing zone type.");
        }
    }

    /**
     * 
     * Pilon management.
     * 
     *
    private void revertBlocks() {
        for(GhostBlock b : revertBlocks)
            b.revert();
        
        revertBlocks.clear();
    }

    public void addDeleteBlock(Block block) {
        revertBlocks.add(new GhostBlock(block));
    }

    public boolean containsDeleteBlock(Block block) {
        for(GhostBlock b : revertBlocks) {
            if(b.getBlock().getX() == block.getX() && b.getBlock().getY() == block.getY() && b.getBlock().getZ() == block.getZ())
                return true;
        }
        return false;
    }

    public void fix(int x, int y) {

        Iterator<GhostBlock> it = revertBlocks.iterator();
        GhostBlock b;
        while(it.hasNext()) {
            b = it.next();
            if(b.getBlock().getX() == x && b.getBlock().getZ() == y) {
                b.revert();
                it.remove();
            }
        }

    }

    /**
     * Little feature to allow easy creation of plots.
     * also allows easy recognition in the db.
     *
    public void makePlot() {
        if(hasInherited()) {
            getPlayer().sendMessage(ChatColor.RED + "You cannot change the type when making an subzone.");
            return;
        }
        if (type.equals(ZonePlot.class)) {
            setZ(0, 127);
            type = ZoneNormal.class;
            getPlayer().sendMessage("Reverted zone to default z and class.");
        } else {
            setZ(0, WorldManager.toInt(player.getLocation().getY()) + 19);
            type = ZonePlot.class;
            getPlayer().sendMessage("Zone is now a plot zone.");
        }
    }

    public void setType(String string) {
        if (string.equals("Cuboid")) {
            form = ZoneCuboid.class;
            coords.clear();
            revertBlocks();
        } else if (string.equals("NPoly"))
            form = ZoneNPoly.class;
        else {
            log.info("Trying to set a invalid zone shape in dummyZone, type: " + string);
        }
    }

    public void loadEdit(ZoneBase z) {
        ZoneForm form = z.getZone();
        setZ(new ZoneVertice(form.getLowZ(),form.getHighZ()));
        this.form   = form.getClass();
        if (form instanceof ZoneCuboid) {
            addCoords(form.getLowX(), form.getLowY());
            addCoords(form.getHighX(), form.getHighY());

        } else if (form instanceof ZoneNPoly) {
            int[] x = ((ZoneNPoly) form).getX();
            int[] y = ((ZoneNPoly) form).getY();
            for (int i = 0; i < x.length; i++) {
                addCoords(x[i], y[i]);
            }
        } else {
            // wut?
        }
        mode = Mode.EDIT;
    }
    
    public boolean hasCUIEnabled() { return cuiEnabled; }
    public void enableCUI() {
        //cuiEnabled = true;
        //player.sendMessage(ChatColor.GREEN + "[Zones]WorldEdit CUI compatibility enabled.");
    }
    
    public void updateCUISelection() {
        if(!hasCUIEnabled()) return;
        sendCUIType();
        sendCUIPoints();
    }
    
    @SuppressWarnings("unused")
    private void sendCUIHandShake() {
        player.sendRawMessage(CUI);
    }
    
    private void sendCUIType() {
        player.sendRawMessage(CUI + "s" + "|" + (getForm().equals(ZoneCuboid.class) ? "cuboid" : "polygon2d"));
    }

    private void sendCUIPoints() { 
        for(int i = 0;i < getCoords().size();i++) {
            if(getCoords().get(i) != null) {
                if(i == (getCoords().size()-1))
                    sendCUIPoint(i,getCoords().get(i),getMax(),getSize());
                else
                    sendCUIPoint(i,getCoords().get(i),getMin(),getSize());
            }
        }
    }
    
    private int getSize() {
        if(getCoords().size() == 0)
            return -1;
        else if(getCoords().size() == 1)
            return -1;
        else if(getCoords().size() == 2)
            return Math.abs(getCoords().get(0).getX() - getCoords().get(1).getX()) * Math.abs(getCoords().get(0).getY() - getCoords().get(1).getY()) * Math.abs(getMax() - getMin());
        else
            return -1;
    }
    
    private void sendCUIPoint(int index, ZoneVertice point,int height, int size) {
        player.sendRawMessage(CUI + join("|","p",index,point.getX(),height,point.getY(),size));
    }
    
    private static String join(String del,Object... o) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < o.length; i++) {
            buffer.append(del).append(o[i]);
        }
        return buffer.toString();
    }
    
    public ZoneBase save() {
        if(mode == Mode.EDIT) return null;

        Zone pZ = new Zone();
        try {
            pZ.setName(getName());
            pZ.setZonetype(getClassName(getType()));
            pZ.setFormtype(getClassName(getForm()));
            pZ.setMinz(getMin());
            pZ.setMaxz(getMax());
            pZ.setWorld(getWorld().getName());
            pZ.setSize(getCoords().size());
            pZ.setSettings("");
            pZ.setAdmins("");
            pZ.setUsers("2,default,he");
            getPlugin().getDatabase().save(pZ);
            for (int i = 0; i < getCoords().size(); i++) {
                if (getCoords().get(i) == null)
                    continue;
                Vertice v = new Vertice();
                v.setId(pZ.getId());
                v.setVertexorder(i);
                v.setX(getCoords().get(i).getX());
                v.setY(getCoords().get(i).getY());
                v.setZone(pZ);
                pZ.addVertice(v);
            }
            getPlugin().getDatabase().save(pZ.getVertices());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        revertBlocks();
        return getZoneManager().loadFromPersistentData(getWorldManager(), pZ);
    }
    

    public boolean merge() {
        ZoneBase z = getSelectedZone();
        if (z == null)
            return false;
        
        
        
        Zone pZ = z.getPersistence();
        try {
            pZ.setFormtype(getClassName(getForm()));
            pZ.setZonetype(getClassName(getType()));
            pZ.setWorld(getWorld().getName());
            pZ.setMinz(getMin());
            pZ.setMaxz(getMax());
            pZ.setSize(getCoords().size());
            getPlugin().getDatabase().execute(getPlugin().getDatabase().createCallableSql("DELETE FROM zones_vertices WHERE id  = " + pZ.getId() + ""));
            //getPlugin().getDatabase().delete(getPlugin().getDatabase().find(Vertice.class).where().eq("id", pZ.getId()).findList());
            /*for(Vertice vertice : pZ.getVertices()) {
                vertice.setId(pZ.getId());
                vertice.setZone(null);
                getPlugin().getDatabase().delete(vertice);
            }*
            pZ.clearVertices();
            for (int i = 0; i < getCoords().size(); i++) {
                if (getCoords().get(i) == null)
                    continue;
                Vertice v = new Vertice();
                v.setId(z.getId());
                v.setVertexorder(i);
                v.setX(getCoords().get(i).getX());
                v.setY(getCoords().get(i).getY());
                v.setZone(pZ);
                pZ.addVertice(v);
            }
            getPlugin().getDatabase().update(pZ);
            getPlugin().getDatabase().save(pZ.getVertices());
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            getZoneManager().removeZone(z);
            getZoneManager().loadFromPersistentData(getWorldManager(), pZ);
            revertBlocks();
        }
        return true;
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
*/
