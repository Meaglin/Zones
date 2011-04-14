package com.zones.model.settings;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.CreatureType;

import com.zones.model.ZoneSettings;

public class CreatureListSerializer implements Serializeble {
    
    @Override
    public String Serialize(Object data) {
        if(data != null && data instanceof List<?>) {
            
            List<?> list =  ( List<?> ) data;
            if(!list.isEmpty() && list.get(0) instanceof CreatureType) {
                String rt = "";
                for(Object i : list)
                    rt += ((CreatureType)i).getName() + ",";
                
                rt = rt.substring(0, rt.length()-1);
                
                return ZoneSettings.escape(rt);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Object UnSerialize(String serializedData) {
        List<CreatureType> list = new ArrayList<CreatureType>();
        for(String i : ZoneSettings.unEscape(serializedData).split(",")) {
            CreatureType t = CreatureType.fromName(i); 
            if(t != null)list.add(t);
        }
        return list;
    }
}
