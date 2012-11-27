package com.zones.textiel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;

import com.zones.Zones;

/**
 * TODO: remove this.
 * @author GuntherDW
 */
public class TexturePackManager {

    private Map<String, byte[]> texturePacks        = new HashMap<String, byte[]>();
    private Map<String, Long> texturePacks_checkSum = new HashMap<String, Long>();

    private Zones plugin;

    public TexturePackManager(Zones plugin) {
        this.plugin = plugin;
    }

    public void load() {

        texturePacks.clear();
        texturePacks_checkSum.clear();

        File pluginFolder = plugin.getDataFolder();
        File texturepackFolder = new File(pluginFolder, getTag());
        if(!texturepackFolder.exists())  return;
        
        for(File inputFile : texturepackFolder.listFiles()) {
            if(!inputFile.getName().endsWith(".png")) continue;
            try{
                String texpackName = inputFile.getName().replace(".png", "");
                long bytes = inputFile.length();
                FileInputStream bis = new FileInputStream(inputFile);
                byte[] fileData = new byte[(int) bytes];
                bis.read(fileData);
                bis.close();
                Checksum checksum = new CRC32();
                checksum.update(fileData, 0, fileData.length);
                long texpackCRC32 = checksum.getValue();

                texturePacks.put(texpackName, fileData);
                texturePacks_checkSum.put(texpackName, texpackCRC32);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<byte[]> generatePackets(String pack) {
        List<byte[]> packets = new ArrayList<byte[]>();
        byte[] texPack = texturePacks.get(pack);
        
        if(texPack == null) return packets;
        
        int amount = (int) Math.floor(texPack.length / Messenger.MAX_MESSAGE_SIZE);
        if((texPack.length % Messenger.MAX_MESSAGE_SIZE) != 0)
            amount++;

        ByteArrayOutputStream bos;
        int packetlen = Messenger.MAX_MESSAGE_SIZE - 2;
        int pos = 0;

        try{
            if(amount > 0) {
                bos = new ByteArrayOutputStream();
                bos.write(251);
                bos.write(pack.getBytes("UTF-8"));
                bos.write((byte) 0);
                bos.write((Integer.toString(texPack.length)).getBytes("UTF-8"));
                bos.write((byte) 0);
                bos.write(texturePacks_checkSum.get(pack).toString().getBytes("UTF-8"));
                bos.write((byte) 0);
                bos.write((Integer.toString(amount)).getBytes("UTF-8"));
                packets.add(bos.toByteArray());
            }

            for(int x = 0; x < amount; x++) {
                bos = new ByteArrayOutputStream();
                bos.write(250);
                bos.write(x);

                if(pos+packetlen > texPack.length) {
                    packetlen = texPack.length - pos;
                }

                bos.write(texPack, pos, packetlen);
                pos += packetlen;

                packets.add(bos.toByteArray());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return packets;
    }

    public boolean exists(String texturepack) {
        return texturePacks.containsKey(texturepack);
    }
    
    public void sendNew(Player player, String texturepack) {
        if(!exists(texturepack)) return;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(252);
            bos.write(texturepack.getBytes("UTF-8"));
            bos.write(0);
            bos.write(texturePacks_checkSum.get(texturepack).toString().getBytes("UTF-8"));
            bos.write(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        player.sendPluginMessage(plugin, getTag(), bos.toByteArray());
    }
    
    public void sendReset(Player player) {
        player.sendPluginMessage(plugin, getTag(), new byte[] { (byte) 253 });
    }

    public void sendPack(Player player, String texturepack) {
        if(!exists(texturepack)) return;

        for(byte[] packet : generatePackets(texturepack))
            player.sendPluginMessage(plugin, getTag(), packet);
    }
    
    public boolean isValid(String texturepack, long checksum) {
        if(texturePacks_checkSum.containsKey(texturepack))
            return texturePacks_checkSum.get(texturepack).equals(checksum);

        return false;
    }

    public static final String getTag() {
        return "Textiel";
    }

}
