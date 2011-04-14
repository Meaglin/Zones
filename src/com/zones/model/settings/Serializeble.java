package com.zones.model.settings;

public interface Serializeble {
    public String Serialize(Object data);
    public Object UnSerialize(String serializedData);
}
