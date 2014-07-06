package com.zones.util;

import com.zones.model.ZoneVertice;

public class test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ZoneVertice vert = new ZoneVertice(5, 4);
        System.out.println(vert.toString());
        vert = new ZoneVertice(-5, 4);
        System.out.println(vert.toString());
        vert = new ZoneVertice(5, -4);
        System.out.println(vert.toString());
        vert = new ZoneVertice(-5, -4);
        System.out.println(vert.toString());
        
    }

}
