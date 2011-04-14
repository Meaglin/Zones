package com.zones.model.settings;

public class IntegerSerializer implements Serializeble {

    @Override
    public String Serialize(Object data) {
        if(data != null && data instanceof Integer) {
            return ((Integer)data).toString();
        }
        return null;
    }

    @Override
    public Object UnSerialize(String serializedData) {
        try {
            return Integer.parseInt(serializedData);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
