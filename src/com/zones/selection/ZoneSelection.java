package com.zones.selection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.zones.WorldManager;
import com.zones.ZoneManager;
import com.zones.Zones;
import com.zones.model.GhostBlock;
import com.zones.model.WorldConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;
import com.zones.model.types.ZoneInherit;
import com.zones.model.types.ZoneNormal;

public abstract class ZoneSelection {
    

    public enum Confirm {
        SAVE, STOP, NONE
    }

    private final Zones                 plugin;
    private final Player                player;
    private final String                zoneName;

    private Confirm                     confirm       = Confirm.NONE;

    private Selection                   selection     = new CuboidSelection(this);

    protected Class<? extends ZoneBase> type          = ZoneNormal.class;

    private ZoneBase                    inheritedZone = null;
    private List<GhostBlock>            ghostBlocks;
    
    public ZoneSelection(Zones plugin, Player player, String zoneName) {
        this.plugin = plugin;
        this.player = player;
        this.zoneName = zoneName;
        ghostBlocks = new ArrayList<GhostBlock>();
    }
    
    public void setInherited(ZoneBase inheritedZone) {
        if(inheritedZone == null)return;
        type = ZoneInherit.class;
        this.inheritedZone = inheritedZone;
    }
    public boolean hasInherited() { return inheritedZone != null; }
    
    public boolean insideInherited(ZoneVertice z) { return insideInherited(z.getX(), z.getY()); }
    public boolean insideInherited(Block block) { return insideInherited(block.getX(), block.getZ());}
    public boolean insideInherited(int x, int y) {
        if(!hasInherited()) return true;
        return inheritedZone.getForm().isInsideZone(x, y);
    }
    
    public World getWorld() { return player.getWorld(); }
    public Zones getPlugin() { return plugin; }
    public Player getPlayer() { return player; }
    public ZoneManager getZoneManager() { return plugin.getZoneManager(); }
    public ZoneBase getSelectedZone() { return getZoneManager().getSelectedZone(player.getEntityId()); }
    public WorldManager getWorldManager() { return getPlugin().getWorldManager(getWorld()); }
    public WorldConfig getWorldConfig() { return getWorldManager().getConfig(); }
    
    public String getZoneName() { return zoneName; }
    public Selection getSelection() { return selection; }
    public void setSelection(Selection selection) {
        this.selection = selection;
        revertGhostBlocks();
    }

    public Class<? extends ZoneBase> getType() { return type; }
    public Class<? extends ZoneForm> getForm() { return getSelection().getType(); }
    
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
    
    protected void setClass(Class<? extends ZoneBase> zoneclass) {
        this.type = zoneclass;
    }
    
    public void setForm(String name) {
        if(name.equalsIgnoreCase("Cuboid")) {
            if(getSelection() instanceof CuboidSelection) {
                getPlayer().sendMessage(ChatColor.YELLOW + "Selection is already a cuboid selection.");
                return;
            }
            selection = new CuboidSelection(this);
            getPlayer().sendMessage(ChatColor.GREEN + "Selection is now a cuboid selection.");
        } else if(name.equalsIgnoreCase("NPoly")) {
            if(getSelection() instanceof NPolySelection) {
                getPlayer().sendMessage(ChatColor.YELLOW + "Selection is already a polygon selection.");
                return;
            }
            selection = new NPolySelection(this);
            getPlayer().sendMessage(ChatColor.GREEN + "Selection is now a polygon selection.");
        }
    }
    
    public  boolean importWorldeditSelection() {
        com.sk89q.worldedit.bukkit.selections.Selection worldeditSelection = getPlugin().getWorldEdit().getSelection(getPlayer());
        Selection sel = null;
        if(worldeditSelection == null) {
            return false;
        } else if(worldeditSelection instanceof com.sk89q.worldedit.bukkit.selections.CuboidSelection) {
            sel = new CuboidSelection(this);
        } else if(worldeditSelection instanceof com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection) {
            sel = new NPolySelection(this);
        }
        if(sel == null || !sel.importWorldeditSelection()) return false;
        setSelection(sel);
        return true;
    }
    
    
    public void addGhostBlock(Block block) { 
        GhostBlock gb = new GhostBlock(block);
        ghostBlocks.add(gb); gb.show(getPlayer()); 
    }
    
    public void removeGhostBlock(int x, int y) {
        Iterator<GhostBlock> it = ghostBlocks.iterator();
        GhostBlock b;
        while(it.hasNext()) {
            b = it.next();
            if(b.getBlock().getX() == x && b.getBlock().getZ() == y) {
                b.hide(getPlayer());
                it.remove();
            }
        }
    }
    
    public void revertGhostBlocks() {
        for(GhostBlock b : ghostBlocks)
            b.hide(getPlayer());
        ghostBlocks.clear();
    }
    
    public void setConfirm(Confirm confirm) {
        if(confirm == Confirm.STOP) {
            this.confirm = Confirm.STOP;
            return;
        }
        
        if(!getSelection().isValid()) {
            getPlayer().sendMessage(ChatColor.RED + "Missing selection points.");
            return;
        }
        if(confirm == null) this.confirm = Confirm.NONE;
        else this.confirm = confirm;
    }
    
    public void confirm() {
        switch(confirm) {
            case STOP:
                getZoneManager().removeSelection(getPlayer().getEntityId());
                revertGhostBlocks();
                getPlayer().sendMessage(ChatColor.RED + "Zone creation mode stopped, work deleted.");
                break;
            case SAVE:
                if(!getSelection().isValid()) {
                    getPlayer().sendMessage(ChatColor.RED + "Missing selection points.");
                    break;
                }
                if(!sellectionAllowed()) {
                    getPlayer().sendMessage(ChatColor.RED + "All your points need to be in your currently selected zone.");
                    break;
                }
                if (save() != null) {
                    getZoneManager().removeSelection(getPlayer().getEntityId());
                    getPlayer().sendMessage(ChatColor.GREEN + "Zone Saved.");
                } else {
                    getPlayer().sendMessage(ChatColor.RED + "Error saving zone.");
                }
                break;
            default:
                getPlayer().sendMessage(ChatColor.YELLOW + "Nothing to confirm.");
                break;
        }
    }

    
    public boolean sellectionAllowed() {
        if(getPlugin().getPermissions().canUse(getPlayer(), getWorld().getName(), "zones.create"))
            return true;
        
        ZoneBase zone = getSelectedZone();
        if(!(zone instanceof ZoneInherit)) return false;
        if(zone instanceof ZoneInherit && ((ZoneInherit)zone).isAdmin(getPlayer()) && zone.getForm().contains(getSelection())) {
            return true;
        }
        
        List<ZoneBase> zones = ((ZoneInherit)zone).getInheritedZones();
        for(ZoneBase z : zones) {
            if(z instanceof ZoneInherit && ((ZoneInherit)z).isAdmin(getPlayer()) && z.getForm().contains(getSelection())) {
                return true;
            }
        }
        
        return false;
    }
    
    public void onRightClick(Block block) {
        if(!insideInherited(block)) {
            getPlayer().sendMessage(ChatColor.RED + "You cannot add points outside your zone.");
            return;
        }
        getSelection().onRightClick(block);
    }


    public void onLeftClick(Block block) {
        if(!insideInherited(block)) {
            getPlayer().sendMessage(ChatColor.RED + "You cannot add points outside your zone.");
            return;
        }
        getSelection().onLeftClick(block);
    }
    
    public abstract ZoneBase save();
    
    
    public static String getClassName(Class<?> c) {
        String className = c.getName();
        int firstChar;
        firstChar = className.lastIndexOf('.') + 1;
        if (firstChar > 0) {
            className = className.substring(firstChar);
        }
        return className;
    }
}
