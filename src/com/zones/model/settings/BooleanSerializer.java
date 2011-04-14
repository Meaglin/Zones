package com.zones.model.settings;

public class BooleanSerializer implements Serializeble {

    @Override
    public String Serialize(Object data) {
        if(data != null && data instanceof Boolean) {
            return ((Boolean)data).toString();
        } else {
            return null;
        }
    }

    @Override
    public Object UnSerialize(String serializedData) {
        try {
            return Boolean.parseBoolean(serializedData);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
}
