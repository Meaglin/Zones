import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;


public class ZonesListener extends PluginListener {

	public static final int _pilonHeight = 3;
	//bedrock
	public static final int _pilonType = 7;
	
	public static final int _toolType = 280;
	
    @Override
	public boolean onBlockCreate(Player player, Block blockPlaced, Block blockClicked, int itemInHand) {
		ZoneType zone = World.getInstance().getRegion(blockPlaced.getX(),blockPlaced.getZ()).getActiveZone(blockPlaced.getX(),blockPlaced.getZ(),blockPlaced.getY());
		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.BUILD)) {
			player.getInventory().updateInventory();
			player.sendMessage(Colors.Red + "You cannot place blocks in '" + zone.getName() + "' !");
			return true;
		} else {
			if (itemInHand == _toolType) {
				ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy != null) {
					if (dummy._type == 1 && dummy._coords.size() == 2) {
						player.sendMessage(Colors.Red + "You can only use 2 points to define a cuboid zone.");
						return true;
					}
					int[] p = new int[2];
					
					
					p[0] = World.toInt(blockClicked.getX());
					p[1] = World.toInt(blockClicked.getZ());
					
					if(blockClicked.getY() < World.MAX_Z-_pilonHeight){
						for(int i = 1;i <= _pilonHeight;i++){
							Block t = etc.getServer().getBlockAt(blockClicked.getX(), blockClicked.getY()+i, blockClicked.getZ());
							dummy.addDeleteBlock(t);
							t.setType(_pilonType);
							t.update();
						}
					}
					if(dummy.getCoords().contains(p)){
						player.sendMessage(Colors.Red + "Already added this point.");
						return true;
					}
					
					player.sendMessage(Colors.Green + "Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
					dummy.addCoords(p);
				}
			}
			return false;
		}
	}

    @Override
	public boolean onBlockDestroy(Player player, Block block) {

		if(block.getType() == 54)
			return true;

		ZoneType zone = World.getInstance().getRegion(block.getX(),block.getZ()).getActiveZone(block.getX(),block.getZ(),block.getY());
		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.DESTROY)) {
			
			if(block.getStatus() == 0)
				player.sendMessage(Colors.Red + "You cannot destroy blocks in '" + zone.getName() + "' !");
			
			return true;
		} else {
			if (player.getItemInHand() == _toolType) {
				
				
				ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy != null) {
					if(dummy.containsDeleteBlock(block)){
						int[] p = new int[2];
						p[0] = block.getX();
						p[1] = block.getZ();
						dummy.removeCoords(p);
						dummy.fix(block.getX(), block.getZ());
						player.sendMessage(Colors.Green + "Removed point [" + p[0] + "," + p[1] + "] from temp zone.");
						
					}else{						
						player.sendMessage(Colors.Red + "Couldn't find point in zone so nothing could be removed");
					}
				}
			}
			return false;
		}

	}

    @Override
	public boolean onComplexBlockChange(Player player, ComplexBlock block) {

		//Logger.getLogger("Minecraft").info("C modf " + block.getX() + "," + block.getY() + "," + block.getZ() +  "   " + player.getX() + "," + player.getY() + "," + player.getZ() +  "  ");
		ZoneType zone = World.getInstance().getRegion(block.getX(),block.getZ()).getActiveZone(block.getX(),block.getZ(),block.getY());
		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.MODIFY)) {
			player.sendMessage(Colors.Red + "You cannot change chests or furnaces in '" + zone.getName() + "' !");
			return true;
		}
		return false;
	}

    @Override
	public boolean onSendComplexBlock(Player player, ComplexBlock block) {
		if (block instanceof Sign)
			return false;

		//Logger.getLogger("Minecraft").info("C send " + block.getX() + "," + block.getY() + "," + block.getZ() +  "   " + player.getX() + "," + player.getY() + "," + player.getZ() +  "  ");
		//you don't need a message here ;).
		ZoneType zone = World.getInstance().getRegion(block.getX(),block.getZ()).getActiveZone(block.getX(),block.getZ(),block.getY());
		return zone != null &&  !zone.canModify(player, ZonesAccess.Rights.MODIFY);

	}

	private static final int ITEMS_PER_PAGE = 7;
    @Override
	public boolean onCommand(Player player, String[] split) {
		String cmd = split[0].toLowerCase();

		if (getCommands().containsKey(cmd)) {
			if (cmd.equalsIgnoreCase("/zcreate") && player.canUseCommand(cmd)) {
				if (split.length < 2) {
					player.sendMessage(Colors.Yellow + "Usage: /zcreate [zone name]");
				} else {
					String name = "";
					for (int i = 1; i < split.length; i++)
						name += " " + split[i];

					name = name.substring(1);
					ZoneManager.getInstance().addDummy(player.getName(), new ZonesDummyZone(name));
					player.sendMessage("Entering zone creation mode. Zone name: '" + name + "'");
					//					ttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttT
					player.sendMessage("You can start adding the zone points of this zone by             " +
						    Colors.Red + " hitting blocks with a stick(280)" +
							Colors.White + " or using " + Colors.Red + " /zadd");
				}
			} else if (cmd.equalsIgnoreCase("/zhelp")) {
				List<String> availableCommands = new ArrayList<String>();

				for (Entry<String, String> entry : getCommands().entrySet()) 
					if (player.canUseCommand(entry.getKey())) 
						availableCommands.add(entry.getKey() + " " + entry.getValue());
				

				player.sendMessage(Colors.Blue + "Available commands (Page " + (split.length == 2 ? split[1] : "1") + " of " + (int) Math.ceil((double) availableCommands.size() / (double) ITEMS_PER_PAGE) + ") [] = required <> = optional:");
				int amount = 0;
				if (split.length > 1) {
					try {
						amount = Integer.parseInt(split[1]);
					} catch (NumberFormatException ex) {
						player.sendMessage(Colors.Rose + "Not a valid page number.");
					}
					if (amount > 1)
						amount = (amount - 1) * ITEMS_PER_PAGE;
					else
						amount = 0;
				}

				for (int i = amount; i < amount + ITEMS_PER_PAGE; i++)
					if (availableCommands.size() > i)
						player.sendMessage(Colors.Rose + availableCommands.get(i));
			} else if (cmd.equalsIgnoreCase("/zselect")) {
				if(split.length == 2){
					ZoneType zone = ZoneManager.getInstance().getZone(Integer.parseInt(split[1]));
					if (zone == null)
						player.sendMessage(Colors.Yellow + "No zone found with id : " + Integer.parseInt(split[1]));
					else if (!zone.canAdministrate(player))
						player.sendMessage(Colors.Red + "You don't have rights to administrate this zone.");
					else {
						ZoneManager.getInstance().setSelected(player.getName(), zone.getId());
						player.sendMessage(Colors.Green + "Selected zone '" + zone.getName() + "' .");
					}
				}else{
					ArrayList<ZoneType> zones = World.getInstance().getAdminZones(player);
					if(zones.size() < 1)
						player.sendMessage(Colors.Yellow + "No zones found in your current area(which you can modify).");
					else if(zones.size() == 1){
						ZoneManager.getInstance().setSelected(player.getName(), zones.get(0).getId());
						player.sendMessage(Colors.Green + "Selected zone '" + zones.get(0).getName() + "' .");
					} else {
						player.sendMessage(Colors.Yellow +  "Too much zones found, please specify a zone id.(/zselect <id>)");
						String temp = "";
						for (ZoneType zone : zones)
							temp += zone.getName() + "[" + zone.getId() + "]";
						player.sendMessage("Zones found: " + temp);
					}
				}
			} else if (cmd.equalsIgnoreCase("/zsetuser")) {
				if (split.length == 3) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(Colors.Red + "Please select a zone first with /zselect.");
					else{
						ZoneType zone = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						ZonesAccess z = new ZonesAccess(split[2]);
						Player tmp = etc.getServer().matchPlayer(split[1]);
						if(tmp != null && !zone.getAccess(tmp).canModify() && z.canModify() && zone.getDistanceToZone(tmp) < 200)
						{
							Location loc = tmp.getLocation();
							Location spawn = etc.getServer().getSpawnLocation();
							tmp.teleportTo(spawn);
							tmp.teleportTo(loc);
							tmp.sendMessage("Your chest access of a zone close to you changed, so your chunk info needed to be refreshed.");
						}
						zone.addUser(split[1], split[2]);
						player.sendMessage(Colors.Green + "Succesfully changed access of user " + split[1] + " of zone '" + zone.getName() + "' to access " + z.textual() + " .");
					}
				} else {
					player.sendMessage(Colors.Yellow + "Usage: /zsetuser [user name] b|d|m|e|*|- (combination of these) ");
				}
			} else if (cmd.equalsIgnoreCase("/zsetgroup")) {
				if (split.length == 3) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(Colors.Red + "Please select a zone first with /zselect.");
					else {
						ZoneType zone =	ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						zone.addGroup(split[1], split[2]);
						ZonesAccess z = new ZonesAccess(split[2]);
						player.sendMessage(Colors.Green + "Succesfully changed access of group '" + split[1] + "' of zone '" + zone.getName() + "' to access " + z.textual() + ".");
					}

				} else {
					player.sendMessage(Colors.Yellow + "Usage: /zsetgroup [group name] b|d|m|e|*|- (combination of these)");
				}
			} else if (cmd.equalsIgnoreCase("/zaddadmin")) {
				if (split.length == 2) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(Colors.Red + "Please select a zone first with /zselect.");
					else {
						ZoneType zone =	ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						Player tmp = etc.getServer().matchPlayer(split[1]);
						if(tmp != null && !zone.getAccess(tmp).canModify() && zone.getDistanceToZone(tmp) < 200)
						{
							Location loc = tmp.getLocation();
							Location spawn = etc.getServer().getSpawnLocation();
							tmp.teleportTo(spawn);
							tmp.teleportTo(loc);
							tmp.sendMessage("Your chest access of a zone close to you changed, so your chunk info needed to be refreshed.");
						}
						zone.addAdmin(split[1]);
						player.sendMessage(Colors.Green + "Succesfully added player " + split[1] + " as an admin of zone "  + zone.getName() +  " .");
					}
				} else {
					player.sendMessage(Colors.Yellow + "Usage: /zaddadmim [user name]");
				}
			}else if (cmd.equalsIgnoreCase("/zremoveadmin")) {
				if (split.length == 2) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(Colors.Red + "Please select a zone first with /zselect.");
					else {
						ZoneType zone =	ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						zone.removeAdmin(split[1]);
						player.sendMessage(Colors.Green + "Succesfully removed player " + split[1] + " as an admin of zone "  + zone.getName() +  " .");
					}
				} else {
					player.sendMessage(Colors.Yellow + "Usage: /zaddadmim [user name]");
				}
			} else if (cmd.equalsIgnoreCase("/zgetaccess")) {
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(Colors.Red + "Please select a zone first with /zselect.");
				else{
					ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName())).sendAccess(player);
				}
			} else if (cmd.equalsIgnoreCase("/zregion")) {
				Region r = World.getInstance().getRegion(player);
				player.sendMessage("Region[" + r.getX() + "," + r.getY() + "] Zone count: " + r.getZones().size() + ".");
			}else if(cmd.equalsIgnoreCase("/zdelete") && player.canUseCommand("/zcreate")) {
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(Colors.Red + "Please select a zone first with /zselect.");
				else{
					ZoneType toDelete = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
					boolean succes = ZoneManager.getInstance().delete(toDelete);
					if(succes)
						player.sendMessage(Colors.Green + "Succesfully deleted zone " + toDelete.getName() + ".");
					else
						player.sendMessage(Colors.Red + "Problems while deleting zone, please contact admin.");
				}
			} else {

				ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy == null) {
					player.sendMessage(Colors.Red + "First create a zone with:");
					player.sendMessage(Colors.Red + "/zcreate [zone name]");
					return true;
				}
				//revert confirms after a different command is used.
				if (!cmd.equalsIgnoreCase("/zconfirm")) 
					dummy.setConfirm("");

				if (cmd.equalsIgnoreCase("/zadd")) {
					if (dummy._type == 1 && dummy._coords.size() == 2) {
						player.sendMessage(Colors.Red + "You can only use 2 points to define a cuboid zone.");
						return true;
					}
					int[] p = new int[2];
					p[0] = World.toInt(player.getX());
					p[1] = World.toInt(player.getZ());
					for (int[] point : dummy.getCoords()) {
						if (p[0] == point[0] && p[1] == point[1]) {
							player.sendMessage(Colors.Yellow + "Already added this point.");
							return true;
						}
					}
					player.sendMessage(Colors.Green + "Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
					dummy.addCoords(p);
				} else if (cmd.equalsIgnoreCase("/zremove")) {
					int[] p = new int[2];
					p[0] = World.toInt(player.getX());
					p[1] = World.toInt(player.getZ());
					for (int[] point : dummy._coords) {
						if (p[0] == point[0] && p[1] == point[1]) {
							dummy._coords.remove(point);
							player.sendMessage(Colors.Green + "Removed point[" + p[0] + "," + p[1] + "]  from temp zone.");
							return true;
						}
					}
					player.sendMessage(Colors.Red + "Couldn't find point in zone so nothing could be removed");
				} else if (cmd.equalsIgnoreCase("/zsetplot")) {
					dummy.makePlot(player);
				} else if (cmd.equalsIgnoreCase("/zsetheight")) {
					if (split.length < 2 || Integer.parseInt(split[1]) < 1) {
						player.sendMessage(Colors.Yellow + "Usage: /zsetheight [height]");
					} else {
						dummy.setZ(dummy.getMin(),World.toInt(player.getY()) + Integer.parseInt(split[1]) - 1);

						player.sendMessage(Colors.Green + "Max z is now : " + dummy.getMax());
					}
				} else if (cmd.equalsIgnoreCase("/zsetdepth")) {
					if (split.length < 2 || Integer.parseInt(split[1]) < 1) {
						player.sendMessage(Colors.Yellow + "Usage: /zsetdepth [depth]");
					} else {
						dummy.setZ(World.toInt(player.getY()) - Integer.parseInt(split[1]),dummy.getMax());
						
						player.sendMessage(Colors.Green + "Min z is now : " + dummy.getMin());
					}
				} else if (cmd.equalsIgnoreCase("/zsetz")) {
					if (split.length < 3 || Integer.parseInt(split[1]) < 0 || Integer.parseInt(split[1]) > 127 || Integer.parseInt(split[2]) < 0 || Integer.parseInt(split[2]) > 127) {
						player.sendMessage(Colors.Yellow + "Usage: /zsetz [min Z] [max Z]");
					} else {
						dummy.setZ(Integer.parseInt(split[1]),Integer.parseInt(split[2]) );
						player.sendMessage(Colors.Green + "Min z and Max z now changed to : " + dummy.getMin() + " and " + dummy.getMax());
					}
				} else if (cmd.equalsIgnoreCase("/zsave")) {
					if (dummy._type == 1 && dummy.getCoords().size() != 2) {
						player.sendMessage(Colors.Red + "Not enough coordinates set for this zone type, you need 2.");
						return true;
					}else if(dummy._type == 2 && dummy.getCoords().size() < 3){
						player.sendMessage(Colors.Red + "Not enough coordinates set for this zone type, you need atleast 3.");
						return true;
					}
					if (dummy.getMax() == 127 && dummy.getMin() == 0)
						player.sendMessage(Colors.Red + "WARNING: default z values not changed!");
					
					player.sendMessage(Colors.Yellow + "If you are sure you want to save this zone do /zconfirm");
					
					dummy.setConfirm("save");
				} else if (cmd.equalsIgnoreCase("/zstop")) {
					
					player.sendMessage(Colors.Yellow + "Delete the zone? If yes do /zconfirm");
					dummy.setConfirm("stop");
					
				} else if (cmd.equalsIgnoreCase("/zconfirm")) {
					dummy.confirm(player);
				} else if (cmd.equalsIgnoreCase("/zsettype")) {
					if(split.length == 2){
						if(split[1].equals("Cuboid"))
							dummy.setType(split[1]);
						else if(split[1].equals("NPoly"))
							dummy.setType(split[1]);
						else{
							player.sendMessage(Colors.Yellow + "Usage: /zsettype Cuboid|NPoly - changes zone type.");
							return true;
						}
						player.sendMessage(Colors.Green + "Succesfully changed zone type to '" + split[1] + "' .");

					}else
						player.sendMessage(Colors.Yellow + "Usage: /zsettype Cuboid|NPoly - changes zone type.");

				}
			}
			return true;
		}

		return false;
	}

    @Override
	public void onPlayerMove(Player player, Location from, Location to) {

		// no actual change,just within the block.
		// hMod already does this for us.
		//if (Math.floor(from.x) == Math.floor(to.x) && Math.floor(from.y) == Math.floor(to.y) && Math.floor(from.z) == Math.floor(to.z))
		//	return;
		
		
		//debug only ;).
		//System.out.println("Moving from [" + Math.floor(from.x) + "," + Math.floor(from.y) + "," + Math.floor(from.z) + "] to [" + Math.floor(to.x) + "," + Math.floor(to.y) + "," + Math.floor(to.z) + "]" + "  [" + (Math.floor(to.x) - Math.floor(from.x)) + "," + (Math.floor(to.y) - Math.floor(from.y)) + "," + (Math.floor(to.z) - Math.floor(from.z)) + "]");
		
		//if the active zone changes we want to be sure the player can move into the zone.
		ZoneType aZone = World.getInstance().getRegion(from.x,from.z).getActiveZone(from.x,from.z,from.y);
		ZoneType bZone = World.getInstance().getRegion(to.x,to.z).getActiveZone(to.x,to.z,to.y);
		if(bZone != null && 
				(
						(aZone != null  && aZone.getId() != bZone.getId() && !bZone.canModify(player, ZonesAccess.Rights.ENTER)) 
							|| 
						(aZone == null && !bZone.canModify(player, ZonesAccess.Rights.ENTER))
				)
		){
			player.teleportTo(from);
			player.sendMessage(Colors.Red + "You can't enter " + bZone.getName() + ".");
			//we don't have to do overall revalidation if the player gets warped back to his previous location.
			return;
		}
		// debug only ;).
		//Logger.getLogger("Minecraft").info("revalidate.");

		World.getInstance().revalidateZones(player,World.toInt(from.x),World.toInt(from.z),World.toInt(to.x),World.toInt(to.z));
	}

    @Override
	public boolean onTeleport(Player player, Location from, Location to) { 
		// no actual change,just within the block.
		//hMod already does this for us.
		//if (Math.floor(from.x) == Math.floor(to.x) && Math.floor(from.y) == Math.floor(to.y) && Math.floor(from.z) == Math.floor(to.z))
		//	return false;
		
		//if the active zone changes we want to be sure the player can move into the zone.
		ZoneType aZone = World.getInstance().getRegion(from.x,from.z).getActiveZone(from.x,from.z,from.y);
		ZoneType bZone = World.getInstance().getRegion(to.x,to.z).getActiveZone(to.x,to.z,to.y);
		if(bZone != null && 
				(
						(aZone != null  && aZone.getId() != bZone.getId() && !bZone.canModify(player, ZonesAccess.Rights.ENTER)) 
							|| 
						(aZone == null && !bZone.canModify(player, ZonesAccess.Rights.ENTER))
				)
		){
			player.sendMessage(Colors.Red + "You cannot warp into " + bZone.getName() + ", since it is a protected area.");
			return true;
		}
		
		World.getInstance().revalidateZones(player,World.toInt(from.x),World.toInt(from.z),World.toInt(to.x),World.toInt(to.z));
		
		return false; 
	}
	 
	public static final Map<String, String> commands;
	static {
		commands = new LinkedHashMap<String,String>();
		commands.put("/zcreate", "[zone name] - starts zone creation in a new zone.");
		commands.put("/zadd", "- adds the current location to the temp zone.");
		commands.put("/zremove", "- removes the current location from the temp zone.");
		commands.put("/zsetplot", "- set height and depth to according to plot specs.");
		commands.put("/zhelp", "<page id> - shows <page id> page from the zone help.");
		commands.put("/zsetheight", "[height] - sets maxz to current z + [height].");
		commands.put("/zsetdepth", "[depth] - sets minz to current z - [depth].");
		commands.put("/zsave", "- saves the temp zone after confirmation.");
		commands.put("/zconfirm", "- confirms confirmations.");
		commands.put("/zsetz", "[minz] [maxz] - sets minz, maxz, range [0-127].");
		commands.put("/zstop", "- stop creation and delete zone (asks confirmation).");
		commands.put("/zsetuser", "[user name] b|d|m|e|*|- (combination of these)");
		commands.put("/zsetgroup", "[group name] b|d|m|e|*|- (combination of these)");
		commands.put("/zaddadmin", "[user name]");
		commands.put("/zremoveadmin", "[user name]");
		commands.put("/zselect", "<zone id>");
		commands.put("/zsettype", "Cuboid|NPoly - changes zone type.");
		commands.put("/zregion", " returns region info.");
		commands.put("/zgetaccess", "- sends a access list of the selected zone.");
	}
	private Map<String, String> getCommands() { return commands; }
}
