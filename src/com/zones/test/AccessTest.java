package com.zones.test;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZonesAccess.Rights;

public class AccessTest extends Test {

    
    
    public AccessTest(Zones plugin, Player player) {
        super(plugin, player);
    }
    
   
    @testFunction
    void build() {
        Expect(zone.canModify(player, Rights.BUILD)).equal(false);
        zone.setUser(player, "b");
        Expect(zone.canModify(player, Rights.BUILD)).equal(true);
        zone.setUser(player, "*");
        Expect(zone.canModify(player, Rights.BUILD)).equal(true);
        zone.setUser(player, "");
    }
    
    @testFunction
    void attack() {
        Expect(zone.canModify(player, Rights.ATTACK)).equal(false);
        zone.setUser(player, "a");
        Expect(zone.canModify(player, Rights.ATTACK)).equal(true);
        zone.setUser(player, "*");
        Expect(zone.canModify(player, Rights.ATTACK)).equal(true);
        zone.setUser(player, "");
    }
    
    @testFunction
    void modify() {
        Expect(zone.canModify(player, Rights.MODIFY)).equal(false);
        zone.setUser(player, "c");
        Expect(zone.canModify(player, Rights.MODIFY)).equal(true);
        zone.setUser(player, "*");
        Expect(zone.canModify(player, Rights.MODIFY)).equal(true);
        zone.setUser(player, "");
    }
    
    @testFunction
    void enter() {
        Expect(zone.canModify(player, Rights.ENTER)).equal(false);
        zone.setUser(player, "e");
        Expect(zone.canModify(player, Rights.ENTER)).equal(true);
        zone.setUser(player, "*");
        Expect(zone.canModify(player, Rights.ENTER)).equal(true);
        zone.setUser(player, "");
    }
    
    @testFunction
    void destroy() {
        Expect(zone.canModify(player, Rights.DESTROY)).equal(false);
        zone.setUser(player, "d");
        Expect(zone.canModify(player, Rights.DESTROY)).equal(true);
        zone.setUser(player, "*");
        Expect(zone.canModify(player, Rights.DESTROY)).equal(true);
        zone.setUser(player, "");
    }
    @testFunction
    void buildAdmin() {
        Expect(zone.canModify(player, Rights.BUILD)).equal(false);
        zone.setAdmin(player, true);
        Expect(zone.canModify(player, Rights.BUILD)).equal(true);
        zone.setAdmin(player, false);
    }
    
    @testFunction
    void attackAdmin() {
        Expect(zone.canModify(player, Rights.ATTACK)).equal(false);
        zone.setAdmin(player, true);
        Expect(zone.canModify(player, Rights.ATTACK)).equal(true);
        zone.setAdmin(player, false);
    }
    
    @testFunction
    void modifyAdmin() {
        Expect(zone.canModify(player, Rights.MODIFY)).equal(false);
        zone.setAdmin(player, true);
        Expect(zone.canModify(player, Rights.MODIFY)).equal(true);
        zone.setAdmin(player, false);
    }
    
    @testFunction
    void enterAdmin() {
        Expect(zone.canModify(player, Rights.ENTER)).equal(false);
        zone.setAdmin(player, true);
        Expect(zone.canModify(player, Rights.ENTER)).equal(true);
        zone.setAdmin(player, false);
    }
    
    @testFunction
    void destroyAdmin() {
        Expect(zone.canModify(player, Rights.DESTROY)).equal(false);
        zone.setAdmin(player, true);
        Expect(zone.canModify(player, Rights.DESTROY)).equal(true);
        zone.setAdmin(player, false);
    }
}
