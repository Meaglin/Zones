import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class ZonesListener extends PluginListener {

	public static final int _pilonHeight = 3;
	//bedrock
	public static final int _pilonType = 7;
	//stick
	public static final int _toolType = 280;

	@Override
	public boolean onBlockCreate(Player player, Block blockPlaced, Block blockClicked, int itemInHand) {
		if (itemInHand == _toolType) {
				ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy != null) {
					if (dummy._type == 1 && dummy._coords.size() == 2) {
						player.sendMessage(Colors.Rose + "You can only use 2 points to define a cuboid zone.");
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
						player.sendMessage(Colors.Rose + "Already added this point.");
						return true;
					}

					player.sendMessage(Colors.Green + "Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
					dummy.addCoords(p);
				}
			}
		return false;
	}
	public boolean onBlockDestroy(Player player, Block block) {
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
						player.sendMessage(Colors.Rose + "Couldn't find point in zone so nothing could be removed");
					}
				}
			}
        return false;
    }
    @Override
	public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {


		ZoneType zone = World.getInstance().getRegion(blockPlaced.getX(),blockPlaced.getZ()).getActiveZone(blockPlaced.getX(),blockPlaced.getZ(),blockPlaced.getY());
		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.BUILD)) {
			player.getInventory().updateInventory();
			player.sendMessage(Colors.Rose + "You cannot place blocks in '" + zone.getName() + "' !");
			return true;
		}else if(zone != null && (blockPlaced.getType() == 54 || blockPlaced.getType() == 61 || blockPlaced.getType() == 62 ) && !zone.canModify(player, ZonesAccess.Rights.MODIFY)){
			player.getInventory().updateInventory();
			player.sendMessage(Colors.Rose + "You cannot place chests/furnaces in '" + zone.getName() + "' since you don't have modify rights !");
			return true;
		} else 
			return false;
		
	}

    @Override
	public boolean onBlockBreak(Player player, Block block) {

		ZoneType zone = World.getInstance().getRegion(block.getX(),block.getZ()).getActiveZone(block.getX(),block.getZ(),block.getY());
		if (zone != null && !zone.canModify(player, ZonesAccess.Rights.DESTROY)) {
					player.sendMessage(Colors.Rose + "You cannot destroy blocks in '" + zone.getName() + "' !");
				return true;	

		}else if(zone != null && (block.getType() == 54 || block.getType() == 61 || block.getType() == 62 ) && !zone.canModify(player, ZonesAccess.Rights.MODIFY)){

			if(block.getStatus() == 0)
				player.sendMessage(Colors.Rose + "You cannot destroy a chest/furnace in '" + zone.getName() + "' since you dont have modify rights!");

			return true;
		}else
			return false;
		

	}

    @Override
	public boolean onComplexBlockChange(Player player, ComplexBlock block) {

		//Logger.getLogger("Minecraft").info("C modf " + block.getX() + "," + block.getY() + "," + block.getZ() +  "   " + player.getX() + "," + player.getY() + "," + player.getZ() +  "  ");
		ZoneType zone = World.getInstance().getRegion(block.getX(),block.getZ()).getActiveZone(block.getX(),block.getZ(),block.getY());
		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.MODIFY)) {
			player.sendMessage(Colors.Rose + "You cannot change chests or furnaces in '" + zone.getName() + "' !");
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
					if(name.length() < 4)
					{
						player.sendMessage(Colors.Rose + "Too short zone name.");
						return true;
					}
					ZoneManager.getInstance().addDummy(player.getName(), new ZonesDummyZone(name));
					player.sendMessage("Entering zone creation mode. Zone name: '" + name + "'");
					//					ttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttT
					player.sendMessage("You can start adding the zone points of this zone by             " +
						    Colors.Rose + " hitting blocks with a stick(280)" +
							Colors.White + " or using " + Colors.Rose + " /zadd");
				}
			} else if (cmd.equalsIgnoreCase("/zhelp")) {
				List<String> availableCommands = new ArrayList<String>();

				for (Entry<String, String[]> entry : getCommands().entrySet()) 
					if (entry.getValue()[0].equals("0") || player.canUseCommand("/zcreate"))
						availableCommands.add(entry.getKey() + " " + entry.getValue()[1]);

				int amount = 0;
				boolean isCommand = false;
				if (split.length > 1) {
					try {
						amount = Integer.parseInt(split[1]);
					} catch (NumberFormatException ex) {

						if(getCommands().containsKey("/" + split[1].toLowerCase()) && (getCommands().get("/" + split[1].toLowerCase())[0].equals("0") || player.canUseCommand("/zcreate")))
							isCommand = true;
						else
							player.sendMessage(Colors.Rose + "Not a valid page number.");
					}
					if (amount > 1)
						amount = (amount - 1) * ITEMS_PER_PAGE;
					else
						amount = 0;
				}
				if(isCommand){
					String[] info = getCommands().get("/" + split[1].toLowerCase())[2].split("\n");
					String command = "/" + split[1].toLowerCase();

					player.sendMessage(Colors.Blue + "Description of " + command + " :");
					for(String part : info)
						player.sendMessage(Colors.LightBlue + part);

					return true;
				}

				player.sendMessage(Colors.Blue + "Available commands (Page " + (split.length == 2 ? split[1] : "1") + " of " + (int) Math.ceil((double) availableCommands.size() / (double) ITEMS_PER_PAGE) + ") [] = required <> = optional:");
				player.sendMessage(Colors.Blue + "For more info: /zhelp <command name>");
				for (int i = amount; i < amount + ITEMS_PER_PAGE; i++)
					if (availableCommands.size() > i)
						player.sendMessage(Colors.Rose + availableCommands.get(i));
			} else if (cmd.equalsIgnoreCase("/zselect")) {
				if(split.length == 2){
					ZoneType zone = ZoneManager.getInstance().getZone(Integer.parseInt(split[1]));
					if (zone == null)
						player.sendMessage(Colors.Yellow + "No zone found with id : " + Integer.parseInt(split[1]));
					else if (!zone.canAdministrate(player))
						player.sendMessage(Colors.Rose + "You don't have rights to administrate this zone.");
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
						player.sendMessage(Colors.Rose + "Please select a zone first with /zselect.");
					else{
						ZoneType zone = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						ZonesAccess z = new ZonesAccess(split[2]);

						Player p = etc.getServer().matchPlayer(split[1]);
						if(p != null)
							split[1] = p.getName();

						zone.addUser(split[1], split[2]);
						
						if(p != null)
							fixChests(p,zone);
						
						player.sendMessage(Colors.Green + "Succesfully changed access of user " + split[1] + " of zone '" + zone.getName() + "' to access " + z.textual() + " .");
					}
				} else {
					player.sendMessage(Colors.Yellow + "Usage: /zsetuser [user name] b|d|m|e|*|- (combination of these) ");
				}
			} else if (cmd.equalsIgnoreCase("/zsetgroup")) {
				if (split.length == 3) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(Colors.Rose + "Please select a zone first with /zselect.");
					else {
						ZoneType zone =	ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						ZonesAccess oldAccess = zone.getAccess(split[1]);
						zone.addGroup(split[1], split[2]);
						ZonesAccess newAccess = new ZonesAccess(split[2]);

						if((oldAccess.canModify() && !newAccess.canModify()) || (!oldAccess.canModify() && newAccess.canModify()))
							fixChests(zone);


						

						player.sendMessage(Colors.Green + "Succesfully changed access of group '" + split[1] + "' of zone '" + zone.getName() + "' to access " + newAccess.textual() + ".");
					}

				} else {
					player.sendMessage(Colors.Yellow + "Usage: /zsetgroup [group name] b|d|m|e|*|- (combination of these)");
				}
			} else if (cmd.equalsIgnoreCase("/zaddadmin")) {
				if (split.length == 2) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(Colors.Rose + "Please select a zone first with /zselect.");
					else {
						ZoneType zone =	ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						
						Player p = etc.getServer().matchPlayer(split[1]);

						if(p != null)
							split[1] = p.getName();

						zone.addAdmin(split[1]);

						if(p != null)
							fixChests(p,zone);

						player.sendMessage(Colors.Green + "Succesfully added player " + split[1] + " as an admin of zone "  + zone.getName() +  " .");
					}
				} else {
					player.sendMessage(Colors.Yellow + "Usage: /zaddadmim [user name]");
				}
			}else if (cmd.equalsIgnoreCase("/zremoveadmin")) {
				if (split.length == 2) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(Colors.Rose + "Please select a zone first with /zselect.");
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
					player.sendMessage(Colors.Rose + "Please select a zone first with /zselect.");
				else{
					ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName())).sendAccess(player);
				}
			} else if (cmd.equalsIgnoreCase("/zregion")) {
				Region r = World.getInstance().getRegion(player);
				player.sendMessage("Region[" + r.getX() + "," + r.getY() + "] Zone count: " + r.getZones().size() + ".");
			}else if(cmd.equalsIgnoreCase("/zdelete") && player.canUseCommand("/zcreate")) {
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(Colors.Rose + "Please select a zone first with /zselect.");
				else{
					ZoneType toDelete = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
					if(ZoneManager.getInstance().delete(toDelete))
						player.sendMessage(Colors.Green + "Succesfully deleted zone " + toDelete.getName() + ".");
					else
						player.sendMessage(Colors.Rose + "Problems while deleting zone, please contact admin.");
				}
			}else if (cmd.equalsIgnoreCase("/zsetname")){
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(Colors.Rose + "Please select a zone first with /zselect.");
				else{
					if(split.length < 2)
						player.sendMessage(Colors.Yellow + "Usage: /zsetname [zone name]");
					else {
						String name = "";
						for (int i = 1; i < split.length; i++)
							name += " " + split[i];

						name = name.substring(1);

						if(name.length() < 4)
							player.sendMessage(Colors.Rose + "Too short zone name.");
						else if(name.length() > 40)
							player.sendMessage(Colors.Rose + "Too long zone name.");
						else if(ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName())).setName(name))
							player.sendMessage(Colors.Green + "Succesfully changed zone name to " + name + ".");
						else
							player.sendMessage(Colors.Rose + "Unable to change zone name, please contact a admin.");

					}

				}
			} else {

				ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy == null) {
					player.sendMessage(Colors.Rose + "First create a zone with:");
					player.sendMessage(Colors.Rose + "/zcreate [zone name]");
					return true;
				}
				//revert confirms after a different command is used.
				if (!cmd.equalsIgnoreCase("/zconfirm")) 
					dummy.setConfirm("");

				if (cmd.equalsIgnoreCase("/zadd")) {
					if (dummy._type == 1 && dummy._coords.size() == 2) {
						player.sendMessage(Colors.Rose + "You can only use 2 points to define a cuboid zone.");
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
					player.sendMessage(Colors.Rose + "Couldn't find point in zone so nothing could be removed");
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
						player.sendMessage(Colors.Rose + "Not enough coordinates set for this zone type, you need 2.");
						return true;
					}else if(dummy._type == 2 && dummy.getCoords().size() < 3){
						player.sendMessage(Colors.Rose + "Not enough coordinates set for this zone type, you need atleast 3.");
						return true;
					}
					if (dummy.getMax() == 127 && dummy.getMin() == 0)
						player.sendMessage(Colors.Rose + "WARNING: default z values not changed!");
					
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

				} else if (cmd.equalsIgnoreCase("/ztogglehealth")) {
					dummy.toggleHealth();
					player.sendMessage(Colors.Green + "Health is now " + (dummy.healthAllowed() ? "enabled" : "disabled") + ".");
				}
			}
			return true;
		}

		return false;
	}

    @Override
	public void onPlayerMove(Player player, Location from, Location to) {
		
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
			player.sendMessage(Colors.Rose + "You can't enter " + bZone.getName() + ".");
			//we don't have to do overall revalidation if the player gets warped back to his previous location.
			return;
		}

		World.getInstance().revalidateZones(player,World.toInt(from.x),World.toInt(from.z),World.toInt(to.x),World.toInt(to.z));
	}

    @Override
	public boolean onTeleport(Player player, Location from, Location to) { 
		
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
			player.sendMessage(Colors.Rose + "You cannot warp into " + bZone.getName() + ", since it is a protected area.");
			return true;
		}
		
		World.getInstance().revalidateZones(player,World.toInt(from.x),World.toInt(from.z),World.toInt(to.x),World.toInt(to.z));
		
		return false; 
	}

	private void fixChests(Player p,ZoneType zone) {

		ZoneForm form = zone.getZone();
		Server serv = etc.getServer();
		
		for (int i = form.getLowX(); i <= form.getHighX(); i++)
			for (int j = form.getLowY(); j <= form.getHighY(); j++){
					int distance = (int) Math.sqrt(Math.pow(i - p.getX(), 2) + Math.pow(j-p.getZ(), 2));
					if(distance > 100)
						continue;

					for (int k = form.getLowZ(); k <= form.getHighZ(); k++){
						ComplexBlock c = serv.getComplexBlock(i, k, j);
						if (c != null)
							c.update();
						
					}

				}


	}
	private void fixChests(ZoneType zone) {

		ZoneForm form = zone.getZone();
		Server serv = etc.getServer();

		for (int i = form.getLowX(); i <= form.getHighX(); i++)
			for (int j = form.getLowY(); j <= form.getHighY(); j++)
				for (int k = form.getLowZ(); k <= form.getHighZ(); k++){
						ComplexBlock c = serv.getComplexBlock(i, k, j);
						if (c != null)
							c.update();

					}

	}
	public static final Map<String, String[]> commands;
	static {
		commands = new LinkedHashMap<String,String[]>();
		commands.put("/zcreate", new String[] {
			"1",
			"[zone name] - starts zone creation in a new zone.",
			
			"Starts Zone creation mode in which you can set the \n zones perimiter and type and height en depth."
		});
		
		commands.put("/zadd", new String[] {
			"1",
			"- adds the current location to the temp zone.",
			"Adds the current player x and y as a point of the  \n zone you are making."
		});
		
		commands.put("/zremove", new String[] {
			"1",
			"- removes the current location from the temp zone.",
			"If the current player location is a point of \n the zone you are making it will be removed from the zone \n you are making. "
		});
		
		commands.put("/zsetplot", new String[] {
			"1",
			"- set height and depth to according to plot specs.",
			"Changes the zone you are making to a plot type \n with the related height and depth of the zone relative to \n your z position."
		});
		
		commands.put("/zhelp",new String[] { 
			"0",
			"<cmd> - shows <cmd> page/command from the zone help.",
			"Shows <cmd> page or command description from the \n zone help file."
		});
		
		commands.put("/zsetheight",new String[] {
			"1",
			 "[height] - sets maxz to current z + [height].",
			"Sets the zone you are creating height to your \n current z position + [height]"
		});
		
		commands.put("/zsetdepth",new String[] {
			"1",
			 "[depth] - sets minz to current z - [depth].",
			"Sets the depth of the zone you are creating \n to your current z position - [depth]"
		});
		
		commands.put("/zsave",new String[] { 
			"1",
			"- saves the temp zone after confirmation.",
			"Initiates saving of the zone you were creating \n you will need to confirm this with \n /zconfirm to make it actually save the zone."
		});
		
		commands.put("/zconfirm",new String[] { 
			"1",
			"- confirms confirmations.",
			"Confirms the last action that needs confirmation \n needed when /zsave or /zstop is used."
		});
		
		commands.put("/zsetz",new String[] { 
			"1",
			"[minz] [maxz] - sets minz, maxz, range [0-127].",
			"Sets the depth and height of the zone according to \n [minz] and [maxz] limited by the max \n and min height of the map [0-127]."
		});
		
		commands.put("/zstop",new String[] { 
			"1",
			"- stop creation and delete zone (asks confirmation).",
			"Stops the creation of the current zone and deletes \n all relative data this needs to be confirmed \n with /zconfirm though."
		});
			//ttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttT
		commands.put("/zsetuser",new String[] { 
			"0",
			"[user name] b|m|d|e|*|- (combination of these)",
			"Sets the access of [user name] to what is specified \n "
			+ "b = Build(placing blocks),\n"
			+ "m = Modify(accessing chest/furnaces),\n "
			+ "d = Destroy(destroying blocks),\n"
			+ "e = Enter(entering your zone), \n"
			+ "* = full access(all of the above) and - = remove all access. \n"
			+ "Example: /zsetuser Meaglin bde this will give meaglin access \n"
			+ " to build,destroy and walk around in your zone but not to \n"
			+ "access your chests."
		});
		
		commands.put("/zsetgroup",new String[] {  
			"0",
			"[group name] b|m|d|e|*|- (combination of these)",
			"Sets the access of [group name] to what is specified \n "
			+ "Possible group names: beunhaas, default, builder and vip \n"
			+ "b = Build(placing blocks),\n"
			+ "m = Modify(accessing chest/furnaces),\n "
			+ "d = Destroy(destroying blocks),\n"
			+ "e = Enter(entering your zone), \n"
			+ "* = full access(all of the above) and - = remove all access. \n"
			+ "Example: /zsetuser default bde this will give all users access \n"
			+ " to build,destroy and walk around in your zone but not to \n"
			+ "access your chests."
			
		});
		
		commands.put("/zaddadmin",new String[] {  
			"0",
			"[user name]",
			"Adds [user name] as admin to your zone which gives \n"
			+ "[user name] rights to build,modify,destroy,enter your zone \n"
			+ "and to give other people rights to do so . (access to \n"
			+ "/zsetuser and /zsetgroup in your zone)"
		});
		
		commands.put("/zremoveadmin",new String[] {  
			"1",
			"[user name]",
			"Removes [user name] as an admin from the zone."
		});
		
		commands.put("/zselect",new String[] {  
			"0",
			"<zone id>",
			"Selects a zone so you can modify the rights of the zone and \n"
			+ "or modify other properties of the zone."
		});
		
		commands.put("/zsettype",new String[] {  
			"1",
			"Cuboid|NPoly - changes zone type.",
			"changes the zone type to a square(cuboid) or polygon(NPoly)."
		});
		
		commands.put("/zregion",new String[] { 
			"1",
			" returns region info.",
			"Return the region x and y index and the amount of zones in \n"
			+ " the region"
		});
		
		commands.put("/zgetaccess",new String[] { 
			"0",
			"- sends a access list of the selected zone.",
			"Sends you a list of all the access given in your currently \n"
			+ "selected zone."
		});
		
		commands.put("/zdelete",new String[] { 
			"1",
			"- deletes selected zone.",
			"Deletes the currently selected zone, No confirmation!"
		});
		
		commands.put("/zsetname",new String[] {  
			"0",
			"[zone name] - changes zone name.",
			"Changes your current selected zones name to [zone name] \n"
			+ "(note: [zone name] is allowed to have spaces)."
		});
		commands.put("/ztogglehealth", new String[] {
			"1",
			".",
			""
		});
		
	}
	/*
     * Called when a dynamite block or a creeper is triggerd.
     * block status depends on explosive compound:
     * 1 = dynamite.
     * 2 = creeper.
     * @param block
     *          dynamite block/creeper location block.
     *
     * @return true if you dont the block to explode.
     */
	@Override
    public boolean onExplode(Block block) {
		if(block.getStatus() == 2)
			return true;

		ZoneType zone = World.getInstance().getActiveZone(block.getX(), block.getZ(), block.getY());
		if(zone != null && !zone.allowDynamite(block))
			return true;
		else
			return false;
    }

	@Override
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {

		if(!defender.isPlayer())
			return false;
		
		if(PluginLoader.DamageType.FALL == type)
			return true;

		ZoneType zone = World.getInstance().getActiveZone(defender.getX(),defender.getZ(),defender.getY());
		if(zone != null && zone.allowHealth())
			return false;
		else
			return true;
    }

	/**
     * @param mob Mob attempting to spawn.
     * @return true if you dont want mob to spawn.
     */
	@Override
    public boolean onMobSpawn(Mob mob) {
		ZoneType zone = World.getInstance().getActiveZone(mob.getX(),mob.getZ(),mob.getY());
		if(zone != null && zone.allowHealth())
			return false;
		else
			return true;
    }

	@Override
	public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item) {
		if(item.getItemId() != 326 && item.getItemId() != 327)
			return false;

		ZoneType zone = World.getInstance().getRegion(blockPlaced.getX(),blockPlaced.getZ()).getActiveZone(blockPlaced.getX(),blockPlaced.getZ(),blockPlaced.getY());
		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.BUILD)) {
			player.sendMessage(Colors.Rose + "You cannot place blocks in '" + zone.getName() + "' !");
			return true;
		}else
			return false;
    }

    /*
     * Called when fluid wants to flow to a certain block.
     * (10 & 11 for lava and 8 & 9 for water)
     *
     * @param blockFrom
     *              the block where the fluid came from.
     *              (blocktype = fluid type)
     * @param blockTo
     *              the block where fluid wants to flow to.
     *
     *
     * @return true if you dont want the substance to flow.
     */
	@Override
    public boolean onFlow(Block blockFrom, Block blockTo) {
		if(blockFrom.getType() == 8 || blockFrom.getType() == 9){

			ZoneType fromZone = World.getInstance().getActiveZone(blockFrom.getX(), blockFrom.getZ(), blockFrom.getY());
			ZoneType toZone = World.getInstance().getActiveZone(blockTo.getX(), blockTo.getZ(), blockTo.getY());

			if(toZone != null && (fromZone == null || fromZone.getId() != toZone.getId()) && !toZone.allowWater(blockTo))
				return true;
		}

		if(blockFrom.getType() == 10 || blockFrom.getType() == 11){

			ZoneType fromZone = World.getInstance().getActiveZone(blockFrom.getX(), blockFrom.getZ(), blockFrom.getY());
			ZoneType toZone = World.getInstance().getActiveZone(blockTo.getX(), blockTo.getZ(), blockTo.getY());

			if(toZone != null && (fromZone == null || fromZone.getId() != toZone.getId()) && !toZone.allowLava(blockTo))
				return true;
		}
		
        return false;
    }

	private Map<String, String[]> getCommands() { return commands; }
}
