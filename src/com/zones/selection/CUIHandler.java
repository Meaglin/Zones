package com.zones.selection;

import java.util.List;

import org.bukkit.entity.Player;

import com.zones.model.ZoneVertice;

public class CUIHandler {
    private final ZoneSelection selection;
    private static final String CUI = "§5§6§4§5";
    
    public CUIHandler(ZoneSelection selection) {
        this.selection = selection;
    }
    
    private Selection getSelection() {
        return selection.getSelection();
    }
    
    private Player getPlayer() {
        return selection.getPlayer();
    }
    
    public void sendHandShake() {
        getPlayer().sendRawMessage(CUI);
    }
    
    public void updateSelection() {
        updateType();
        updatePoints();
    }
    
    public void updateType() {
        getPlayer().sendRawMessage(CUI + "s" + "|" + (selection.getSelection() instanceof CuboidSelection ? "cuboid" : "polygon2d"));
    }
    
    public void updatePoints() {
        if(!getSelection().isValid())
            return;
        
        List<ZoneVertice> points = getSelection().getPoints();
        for(int i = 0;i < points.size();i++) {
            if(points.get(i) != null) {
                if(i == (points.size()-1))
                    sendCUIPoint(i,points.get(i),getSelection().getHeight().getMax(),getSelection().getSize());
                else
                    sendCUIPoint(i,points.get(i),getSelection().getHeight().getMin(),getSelection().getSize());
            }
        }
    }
    
    private void sendCUIPoint(int index, ZoneVertice point,int height, long size) {
        getPlayer().sendRawMessage(CUI + join("|","p",index,point.getX(),height,point.getY(),size));
    }
    
    private static String join(String del,Object... o) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < o.length; i++) {
            buffer.append(del).append(o[i]);
        }
        return buffer.toString();
    }
}
