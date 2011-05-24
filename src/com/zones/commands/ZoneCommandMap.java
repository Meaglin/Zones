package com.zones.commands;

import java.util.HashMap;
import java.util.logging.Logger;

import com.zones.Zones;
import com.zones.commands.admin.*;
import com.zones.commands.create.*;
import com.zones.commands.general.*;
import com.zones.commands.settings.*;

/**
 * 
 * @author Meaglin
 *
 */
public class ZoneCommandMap {
    private HashMap<String, ZoneCommand> commands = new HashMap<String, ZoneCommand>();;
    private Zones   plugin;
    protected static final Logger      log             = Logger.getLogger("Minecraft");
    
    public ZoneCommandMap(Zones plugin) {
        this.plugin = plugin;
        load();
    }
    
    public void load() {
        commands.clear();
        
        //CUI
        registerCommand(new CuiValidateCommand(plugin));
        
        // Admin
        registerCommand(new ZAddAdminCommand(plugin));
        registerCommand(new ZDeleteCommand(plugin));
        registerCommand(new ZExportCommand(plugin));
        registerCommand(new ZGetAccessCommand(plugin));
        registerCommand(new ZReloadCommand(plugin));
        registerCommand(new ZRemoveAdminCommand(plugin));
        registerCommand(new ZSetGroupCommand(plugin));
        registerCommand(new ZSetUserCommand(plugin));
        
        //Create
        registerCommand(new ZConfirmCommand(plugin));
        registerCommand(new ZCreateCommand(plugin));
        registerCommand(new ZDefineCommand(plugin));
        registerCommand(new ZEditCommand(plugin));
        registerCommand(new ZRedefineCommand(plugin));
        registerCommand(new ZImportCommand(plugin));
        registerCommand(new ZSaveCommand(plugin));
        registerCommand(new ZSetClassCommand(plugin));
        registerCommand(new ZSetDepthCommand(plugin));
        registerCommand(new ZSetHeightCommand(plugin));
        registerCommand(new ZSetTypeCommand(plugin));
        registerCommand(new ZSetzCommand(plugin));
        registerCommand(new ZStopCommand(plugin));

        //General
        registerCommand(new ZAboutCommand(plugin));
        registerCommand(new ZAccessCommand(plugin));
        registerCommand(new ZHelpCommand(plugin));
        registerCommand(new ZInfoCommand(plugin));
        registerCommand(new ZRegionInfoCommand(plugin));
        registerCommand(new ZSelectCommand(plugin));
        registerCommand(new ZWhoCommand(plugin));

        // Settings
        registerCommand(new ZAddCommand(plugin));
        registerCommand(new ZRemoveCommand(plugin));
        registerCommand(new ZSetCommand(plugin));
        registerCommand(new ZSetNameCommand(plugin));
        registerCommand(new ZToggleAnimalsCommand(plugin));
        registerCommand(new ZToggleCommand(plugin));
        registerCommand(new ZToggleDynamiteCommand(plugin));
        registerCommand(new ZToggleHealthCommand(plugin));
        registerCommand(new ZToggleLavaCommand(plugin));
        registerCommand(new ZToggleMobsCommand(plugin));
        registerCommand(new ZToggleWaterCommand(plugin));
        
    }
    
    public void registerCommand(ZoneCommand cmd) {
        commands.put(cmd.getName(), cmd);
        if(cmd.getAliases() != null && cmd.getAliases().size() > 0) {
            for(String str : cmd.getAliases()) {
                if(commands.containsKey(str))
                    log.info("[Zones] " + cmd.getName() + " tryes to register " + str + " but it's already taken by " + commands.get(str).getName());
                else
                    commands.put(str, cmd);
            }
        }
    }
    
    public ZoneCommand getCommand(String name) {
        return commands.get(name);
    }
    
    public boolean commandExists(String name) {
        return commandExists(name,true);
    }
    
    public boolean commandExists(String name,boolean aliasAllowed){
        if(aliasAllowed)
            return commands.containsKey(name);
        else
            return (commands.containsKey(name) && commands.get(name).getName().equals(name));
    }
}
