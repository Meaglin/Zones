package com.zones.textiel;

import java.io.UnsupportedEncodingException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.zones.Zones;

/**
 * 
 * @author Meaglin
 *
 */
public class TextielManager implements PluginMessageListener {
    
    private Zones plugin;
    private TexturePackManager textures;
    
    public TextielManager(Zones plugin) {
        this.plugin = plugin;
        this.textures = new TexturePackManager(plugin);
    }

    public void load() {
        textures.load();
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, TexturePackManager.getTag(), this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, TexturePackManager.getTag());
    }
    
    public void sendNew(Player player, String texturepack) {
        textures.sendNew(player, texturepack);
    }
    
    public void sendReset(Player player) {
        textures.sendReset(player);
    }
    
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        if(data.length == 0) return;
        String message = null;
        try {
            message = new String(data, 1, data.length - 1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        if(data[0] == (byte) 10) {
            textures.sendPack(player, message);
        } else if(data[0] == (byte) 20) {
            String[] split = message.split("\0");
            String texturePack = split[0];
            long checksum = Long.parseLong(split[1]);
            if(!textures.isValid(texturePack, checksum)) textures.sendPack(player, texturePack);
        }
    }
}
