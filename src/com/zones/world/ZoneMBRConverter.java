package com.zones.world;

import org.khelekore.prtree.MBRConverter;

import com.zones.model.types.ZoneNormal;

public class ZoneMBRConverter implements MBRConverter<ZoneNormal> {
    @Override
    public int getDimensions() {
        return 3;
    }
    
    @Override
    public double getMin(int dim, ZoneNormal zone) {
        switch(dim) {
            case 0:
                return zone.getForm().getLowX();
            case 1:
                return zone.getForm().getLowY();
            case 2:
                return zone.getForm().getLowZ();
            default:
                return 0;
        }
    }

    @Override
    public double getMax(int dim, ZoneNormal zone) {
        switch(dim) {
            case 0:
                return zone.getForm().getHighX();
            case 1:
                return zone.getForm().getHighY();
            case 2:
                return zone.getForm().getHighZ();
            default:
                return 0;
        }
    }
}