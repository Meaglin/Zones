package com.zones.model.settings;

import com.zones.model.ZoneSettings;

public class StringSerializer implements Serializeble {
    
    @Override
    public String Serialize(Object data) {
        if(data != null && data instanceof String) {
            return ZoneSettings.escape((String)data);
        } else {
            return null;
        }
    }

    @Override
    public Object UnSerialize(String serializedData) {
        return ZoneSettings.unEscape(serializedData);
    }
}