package com.zones.model.settings;

import java.util.ArrayList;
import java.util.List;

import com.zones.model.ZoneSettings;

public class IntListSerializer implements Serializeble {
    
    @Override
    public String Serialize(Object data) {
        if(data != null && data instanceof List<?>) {
            
            List<?> list =  ( List<?> ) data;
            if(!list.isEmpty() && list.get(0) instanceof Integer) {
                String rt = "";
                for(Object i : list)
                    rt += ((Integer)i) + ",";
                
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
        List<Integer> list = new ArrayList<Integer>();
        for(String i : ZoneSettings.unEscape(serializedData).split(",")) {
            list.add(Integer.parseInt(i));
        }
        return list;
    }
}