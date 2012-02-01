package com.zones.unused.commands;

import java.util.HashMap;
import java.util.logging.Logger;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.unused.commands.admin.ZAddAdminCommand;
import com.zones.unused.commands.admin.ZDeleteCommand;
import com.zones.unused.commands.admin.ZExportCommand;
import com.zones.unused.commands.admin.ZGetAccessCommand;
import com.zones.unused.commands.admin.ZReloadCommand;
import com.zones.unused.commands.admin.ZRemoveAdminCommand;
import com.zones.unused.commands.admin.ZSetGroupCommand;
import com.zones.unused.commands.admin.ZSetUserCommand;
import com.zones.unused.commands.create.ZConfirmCommand;
import com.zones.unused.commands.create.ZCreateCommand;
import com.zones.unused.commands.create.ZDefineCommand;
import com.zones.unused.commands.create.ZEditCommand;
import com.zones.unused.commands.create.ZImportCommand;
import com.zones.unused.commands.create.ZRedefineCommand;
import com.zones.unused.commands.create.ZSaveCommand;
import com.zones.unused.commands.create.ZSetClassCommand;
import com.zones.unused.commands.create.ZSetDepthCommand;
import com.zones.unused.commands.create.ZSetHeightCommand;
import com.zones.unused.commands.create.ZSetTypeCommand;
import com.zones.unused.commands.create.ZSetzCommand;
import com.zones.unused.commands.create.ZStopCommand;
import com.zones.unused.commands.general.ZAboutCommand;
import com.zones.unused.commands.general.ZAccessCommand;
import com.zones.unused.commands.general.ZHelpCommand;
import com.zones.unused.commands.general.ZInfoCommand;
import com.zones.unused.commands.general.ZRegionInfoCommand;
import com.zones.unused.commands.general.ZSelectCommand;
import com.zones.unused.commands.general.ZWhoCommand;
import com.zones.unused.commands.god.GodCommand;
import com.zones.unused.commands.god.UnGodCommand;
import com.zones.unused.commands.settings.ZAddCommand;
import com.zones.unused.commands.settings.ZRemoveCommand;
import com.zones.unused.commands.settings.ZSetCommand;
import com.zones.unused.commands.settings.ZSetNameCommand;
import com.zones.unused.commands.settings.ZToggleAnimalsCommand;
import com.zones.unused.commands.settings.ZToggleCommand;
import com.zones.unused.commands.settings.ZToggleDynamiteCommand;
import com.zones.unused.commands.settings.ZToggleHealthCommand;
import com.zones.unused.commands.settings.ZToggleLavaCommand;
import com.zones.unused.commands.settings.ZToggleMobsCommand;
import com.zones.unused.commands.settings.ZToggleWaterCommand;

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
    }
    
    public void load() {
        commands.clear();
        
        registerCommand(new ZCommand(plugin));
        //CUI
        registerCommand(new CuiValidateCommand(plugin));
        
        // Admin
        registerCommand(new ZAddAdminCommand(plugin));
        registerCommand(new ZDeleteCommand(plugin));
        registerCommand(new ZGetAccessCommand(plugin));
        registerCommand(new ZReloadCommand(plugin));
        registerCommand(new ZRemoveAdminCommand(plugin));
        registerCommand(new ZSetGroupCommand(plugin));
        registerCommand(new ZSetUserCommand(plugin));
        
        //Create
        registerCommand(new ZConfirmCommand(plugin));
        registerCommand(new ZCreateCommand(plugin));
        registerCommand(new ZEditCommand(plugin));
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

        // God
        registerCommand(new GodCommand(plugin));
        registerCommand(new UnGodCommand(plugin));
        
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
        
        if(ZonesConfig.WORLDEDIT_ENABLED) {
            registerCommand(new ZDefineCommand(plugin));
            registerCommand(new ZExportCommand(plugin));
            registerCommand(new ZRedefineCommand(plugin));
            registerCommand(new ZImportCommand(plugin));
        }
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
