package com.zones;
/*

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 *
 * @author Meaglin
 *
public class ZonesCommandsHandler {


    private static final int ITEMS_PER_PAGE = 7;
    
	public static boolean onCommand(Zones zones,Player player,String[] split){
		String cmd = split[0].toLowerCase();

		if (getCommands().containsKey(cmd)) {
			if (cmd.equalsIgnoreCase("/zcreate") && zones.getP().permission(player, "zones.create")) {
				if (split.length < 2) {
					player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zcreate [zone name]");
				} else {
					String name = "";
					for (int i = 1; i < split.length; i++)
						name += " " + split[i];

					name = name.substring(1);
					if(name.length() < 4)
					{
						player.sendMessage(ChatColor.RED.toString() + "Too short zone name.");
						return true;
					}
					ZoneManager.getInstance().addDummy(player.getName(), new ZonesDummyZone(zones,player.getWorld(),name));
					player.sendMessage("Entering zone creation mode. Zone name: '" + name + "'");
					player.sendMessage("You can start adding the zone points of this zone by             " +
						    ChatColor.RED.toString() + " hitting blocks with a stick(280)" +
							ChatColor.WHITE.toString() + " or using " + ChatColor.RED.toString() + " /zadd");
				}
			} else if (cmd.equalsIgnoreCase("/zhelp")) {
				List<String> availableCommands = new ArrayList<String>();

				for (Entry<String, String[]> entry : getCommands().entrySet())
					if (entry.getValue()[0] == null || zones.getP().permission(player, entry.getValue()[0]))
						availableCommands.add(entry.getKey() + " " + entry.getValue()[1]);

				int amount = 0;
				boolean isCommand = false;
				if (split.length > 1) {
					try {
						amount = Integer.parseInt(split[1]);
					} catch (NumberFormatException ex) {

						if(getCommands().containsKey("/" + split[1].toLowerCase()) && (getCommands().get("/" + split[1].toLowerCase())[0] == null || zones.getP().permission(player, getCommands().get("/" + split[1].toLowerCase())[0])))
							isCommand = true;
						else
							player.sendMessage(ChatColor.RED.toString() + "Not a valid page number.");
					}
					if (amount > 1)
						amount = (amount - 1) * ITEMS_PER_PAGE;
					else
						amount = 0;
				}
				if(isCommand){
					String[] info = getCommands().get("/" + split[1].toLowerCase())[2].split("\n");
					String command = "/" + split[1].toLowerCase();

					player.sendMessage(ChatColor.BLUE.toString() + "Description of " + command + " :");
					for(String part : info)
						player.sendMessage(ChatColor.AQUA.toString() + part);

					return true;
				}

				player.sendMessage(ChatColor.BLUE.toString() + "Available commands (Page " + (split.length == 2 ? split[1] : "1") + " of " + (int) Math.ceil((double) availableCommands.size() / (double) ITEMS_PER_PAGE) + ") [] = required <> = optional:");
				player.sendMessage(ChatColor.BLUE.toString() + "For more info: /zhelp <command name>");
				for (int i = amount; i < amount + ITEMS_PER_PAGE; i++)
					if (availableCommands.size() > i)
						player.sendMessage(ChatColor.RED.toString() + availableCommands.get(i));
			} else if (cmd.equalsIgnoreCase("/zselect")) {
				if(split.length == 2){
					ZoneBase zone = ZoneManager.getInstance().getZone(Integer.parseInt(split[1]));
					if (zone == null)
						player.sendMessage(ChatColor.YELLOW.toString() + "No zone found with id : " + Integer.parseInt(split[1]));
					else if (!zone.canAdministrate(player))
						player.sendMessage(ChatColor.RED.toString() + "You don't have rights to administrate this zone.");
					else {
						ZoneManager.getInstance().setSelected(player.getName(), zone.getId());
						player.sendMessage(ChatColor.GREEN.toString() + "Selected zone '" + zone.getName() + "' .");
					}
				}else{
					ArrayList<ZoneBase> zoneslist = World.getInstance().getAdminZones(player);
					if(zoneslist.size() < 1)
						player.sendMessage(ChatColor.YELLOW.toString() + "No zones found in your current area(which you can modify).");
					else if(zoneslist.size() == 1){
						ZoneManager.getInstance().setSelected(player.getName(), zoneslist.get(0).getId());
						player.sendMessage(ChatColor.GREEN.toString() + "Selected zone '" + zoneslist.get(0).getName() + "' .");
					} else {
						player.sendMessage(ChatColor.YELLOW.toString() +  "Too much zones found, please specify a zone id.(/zselect <id>)");
						String temp = "";
						for (ZoneBase zone : zoneslist)
							temp += zone.getName() + "[" + zone.getId() + "]";
						player.sendMessage("Zones found: " + temp);
					}
				}
			} else if (cmd.equalsIgnoreCase("/zsetuser")) {
				if (split.length == 3) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
					else{
						ZoneBase zone = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						ZonesAccess z = new ZonesAccess(split[2]);

						Player p = zones.getServer().getPlayer(split[1]);

						if(p != null)
							split[1] = p.getName();

						zone.addUser(split[1], split[2]);

						

						player.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed access of user " + split[1] + " of zone '" + zone.getName() + "' to access " + z.textual() + " .");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetuser [user name] b|d|m|e|*|- (combination of these) ");
				}
			} else if (cmd.equalsIgnoreCase("/zsetgroup")) {
				if (split.length == 3) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
					else {
						ZoneBase zone =	ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						zone.addGroup(split[1], split[2]);
						ZonesAccess newAccess = new ZonesAccess(split[2]);

						player.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed access of group '" + split[1] + "' of zone '" + zone.getName() + "' to access " + newAccess.textual() + ".");
					}

				} else {
					player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetgroup [group name] b|d|m|e|*|- (combination of these)");
				}
			} else if (cmd.equalsIgnoreCase("/zaddadmin")) {
				if (split.length == 2) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
					else {
						ZoneBase zone =	ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));

						Player p = zones.getServer().getPlayer(split[1]);

						if(p != null)
							split[1] = p.getName();

						zone.addAdmin(split[1]);

						player.sendMessage(ChatColor.GREEN.toString() + "Succesfully added player " + split[1] + " as an admin of zone "  + zone.getName() +  " .");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zaddadmim [user name]");
				}
			}else if (cmd.equalsIgnoreCase("/zremoveadmin")) {
				if (split.length == 2) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
					else {
						ZoneBase zone =	ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
						zone.removeAdmin(split[1]);
						player.sendMessage(ChatColor.GREEN.toString() + "Succesfully removed player " + split[1] + " as an admin of zone "  + zone.getName() +  " .");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zaddadmim [user name]");
				}
			} else if (cmd.equalsIgnoreCase("/zgetaccess")) {
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
				else{
					ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName())).sendAccess(player);
				}
			} else if (cmd.equalsIgnoreCase("/zregion")) {
				Region r = World.getInstance().getRegion(player);
				player.sendMessage("Region[" + r.getX() + "," + r.getY() + "] Zone count: " + r.getZones().size() + ".");
			}else if(cmd.equalsIgnoreCase("/zdelete") && zones.getP().permission(player, "zones.create")) {
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
				else{
					ZoneBase toDelete = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
					if(ZoneManager.getInstance().delete(toDelete))
						player.sendMessage(ChatColor.GREEN.toString() + "Succesfully deleted zone " + toDelete.getName() + ".");
					else
						player.sendMessage(ChatColor.RED.toString() + "Problems while deleting zone, please contact admin.");
				}
			}else if (cmd.equalsIgnoreCase("/zsetname")){
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
				else{
					if(split.length < 2)
						player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetname [zone name]");
					else {
						String name = "";
						for (int i = 1; i < split.length; i++)
							name += " " + split[i];

						name = name.substring(1);

						if(name.length() < 4)
							player.sendMessage(ChatColor.RED.toString() + "Too short zone name.");
						else if(name.length() > 40)
							player.sendMessage(ChatColor.RED.toString() + "Too long zone name.");
						else if(ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName())).setName(name))
							player.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed zone name to " + name + ".");
						else
							player.sendMessage(ChatColor.RED.toString() + "Unable to change zone name, please contact a admin.");

					}

				}
			}else if (cmd.equalsIgnoreCase("/ztogglehealth") && zones.getP().permission(player, "zones.toggle.health")){
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
				else{
					ZoneBase z = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
					if(z.toggleLava())
						player.sendMessage(ChatColor.GREEN.toString() + "Health is now "+(z.isHealthAllowed() ? "enabled" : "disabled" )+".");
					else
						player.sendMessage(ChatColor.RED.toString() + "Unable to change health flag, please contact a admin.");
				}
			}else if (cmd.equalsIgnoreCase("/ztoggledynamite") && zones.getP().permission(player, "zones.toggle.tnt")){
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
				else{
					ZoneBase z = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
					if(z.toggleDynamite())
						player.sendMessage(ChatColor.GREEN.toString() + "Dynamite is now "+(z.isDynamiteAllowed() ? "enabled" : "disabled" )+".");
					else
						player.sendMessage(ChatColor.RED.toString() + "Unable to change dynamite flag, please contact a admin.");
				}
			}else if (cmd.equalsIgnoreCase("/ztogglelava") && zones.getP().permission(player, "zones.toggle.lava")){
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
				else{
					ZoneBase z = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
					if(z.toggleLava())
						player.sendMessage(ChatColor.GREEN.toString() + "Lava is now "+(z.isLavaAllowed() ? "allowed" : "blocked" )+".");
					else
						player.sendMessage(ChatColor.RED.toString() + "Unable to change lava flag, please contact a admin.");
				}
			} else if (cmd.equalsIgnoreCase("/ztogglewater") && zones.getP().permission(player, "zones.toggle.water")) {
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
				else{
					ZoneBase z = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
					if(z.toggleWater())
						player.sendMessage(ChatColor.GREEN.toString() + "Water is now "+(z.isWaterAllowed() ? "allowed" : "blocked" )+".");
					else
						player.sendMessage(ChatColor.RED.toString() + "Unable to change water flag, please contact a admin.");
				}
			} else if (cmd.equalsIgnoreCase("/ztogglemobs") && zones.getP().permission(player, "zones.toggle.mobs")) {
                if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
                    player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
                else{
                    ZoneBase z = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
                    if(z.toggleMobs())
                        player.sendMessage(ChatColor.GREEN.toString() + "Mob spawning is now "+(z.isMobsAllowed() ? "enabled" : "disabled" )+".");
                    else
                        player.sendMessage(ChatColor.RED.toString() + "Unable to change mobs flag, please contact a admin.");
                }
            } else if (cmd.equalsIgnoreCase("/ztoggleanimals") && zones.getP().permission(player, "zones.toggle.animals")) {
                if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
                    player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
                else{
                    ZoneBase z = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
                    if(z.toggleAnimals())
                        player.sendMessage(ChatColor.GREEN.toString() + "Animal spawning is now "+(z.isAnimalsAllowed() ? "enabled" : "disabled" )+".");
                    else
                        player.sendMessage(ChatColor.RED.toString() + "Unable to change animals flag, please contact a admin.");
                }
            } else if (cmd.equalsIgnoreCase("/zedit") && zones.getP().permission(player, "zones.create")) {
				if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
					player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
				else{
					ZoneBase z = ZoneManager.getInstance().getZone(ZoneManager.getInstance().getSelected(player.getName()));
					ZonesDummyZone dummy = new ZonesDummyZone(zones,player.getWorld(),z.getName());
					dummy.loadEdit(z);
					ZoneManager.getInstance().addDummy(player.getName(), dummy);
					player.sendMessage(ChatColor.GREEN.toString() + " Loaded zone " + z.getName() + " into a dummy zone.");
				}
			} else {

				ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy == null) {
				    if(zones.getP().permission(player, "zones.create")) {
				        player.sendMessage(ChatColor.RED.toString() + "First create a zone with:");
					    player.sendMessage(ChatColor.RED.toString() + "/zcreate [zone name]");
				    } else {
				        player.sendMessage(ChatColor.RED + "You are not allowed to create zones.");
				    }
					return true;
				}
				//revert confirms after a different command is used.
				if (!cmd.equalsIgnoreCase("/zconfirm"))
					dummy.setConfirm("");

				if (cmd.equalsIgnoreCase("/zadd")) {
					if (dummy.getType() == 1 && dummy.getCoords().size() == 2) {
						player.sendMessage(ChatColor.RED.toString() + "You can only use 2 points to define a cuboid zone.");
						return true;
					}
					int[] p = new int[2];
					p[0] = World.toInt(player.getLocation().getX());
					p[1] = World.toInt(player.getLocation().getZ());
					for (int[] point : dummy.getCoords()) {
						if (p[0] == point[0] && p[1] == point[1]) {
							player.sendMessage(ChatColor.YELLOW.toString() + "Already added this point.");
							return true;
						}
					}
					player.sendMessage(ChatColor.GREEN.toString() + "Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
					dummy.addCoords(p);
				} else if (cmd.equalsIgnoreCase("/zremove")) {
					int[] p = new int[2];
					p[0] = World.toInt(player.getLocation().getX());
					p[1] = World.toInt(player.getLocation().getZ());
					for (int[] point : dummy.getCoords()) {
						if (p[0] == point[0] && p[1] == point[1]) {
							dummy.remove(point);
							player.sendMessage(ChatColor.GREEN.toString() + "Removed point[" + p[0] + "," + p[1] + "]  from temp zone.");
							return true;
						}
					}
					player.sendMessage(ChatColor.RED.toString() + "Couldn't find point in zone so nothing could be removed");
				} else if (cmd.equalsIgnoreCase("/zsetplot")) {
					dummy.makePlot(player);
				} else if (cmd.equalsIgnoreCase("/zsetheight")) {
					if (split.length < 2 || Integer.parseInt(split[1]) < 1) {
						player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetheight [height]");
					} else {
						dummy.setZ(dummy.getMin(),World.toInt(player.getLocation().getY()) + Integer.parseInt(split[1]) - 1);

						player.sendMessage(ChatColor.GREEN.toString() + "Max z is now : " + dummy.getMax());
					}
				} else if (cmd.equalsIgnoreCase("/zsetdepth")) {
					if (split.length < 2 || Integer.parseInt(split[1]) < 0) {
						player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetdepth [depth]");
					} else {
						dummy.setZ(World.toInt(player.getLocation().getY()) - Integer.parseInt(split[1]),dummy.getMax());

						player.sendMessage(ChatColor.GREEN.toString() + "Min z is now : " + dummy.getMin());
					}
				} else if (cmd.equalsIgnoreCase("/zsetz")) {
					if (split.length < 3 || Integer.parseInt(split[1]) < 0 || Integer.parseInt(split[1]) > 127 || Integer.parseInt(split[2]) < 0 || Integer.parseInt(split[2]) > 127) {
						player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetz [min Z] [max Z]");
					} else {
						dummy.setZ(Integer.parseInt(split[1]),Integer.parseInt(split[2]) );
						player.sendMessage(ChatColor.GREEN.toString() + "Min z and Max z now changed to : " + dummy.getMin() + " and " + dummy.getMax());
					}
				} else if (cmd.equalsIgnoreCase("/zsave")) {
					if (dummy.getType() == 1 && dummy.getCoords().size() != 2) {
						player.sendMessage(ChatColor.RED.toString() + "Not enough coordinates set for this zone type, you need 2.");
						return true;
					}else if(dummy.getType() == 2 && dummy.getCoords().size() < 3){
						player.sendMessage(ChatColor.RED.toString() + "Not enough coordinates set for this zone type, you need atleast 3.");
						return true;
					}
					if (dummy.getMax() == 130 && dummy.getMin() == 0)
						player.sendMessage(ChatColor.RED.toString() + "WARNING: default z values not changed!");

					player.sendMessage(ChatColor.YELLOW.toString() + "If you are sure you want to save this zone do /zconfirm");

					dummy.setConfirm("save");
				} else if (cmd.equalsIgnoreCase("/zstop")) {

					player.sendMessage(ChatColor.YELLOW.toString() + "Delete the zone? If yes do /zconfirm");
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
							player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsettype Cuboid|NPoly - changes zone type.");
							return true;
						}
						player.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed zone type to '" + split[1] + "' .");

					}else
						player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsettype Cuboid|NPoly - changes zone type.");

				} else if (cmd.equalsIgnoreCase("/zmerge")) {
					if(ZoneManager.getInstance().getSelected(player.getName()) == 0)
						player.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
					else{
						if (dummy.getType() == 1 && dummy.getCoords().size() != 2) {
							player.sendMessage(ChatColor.RED.toString() + "Not enough coordinates set for this zone type, you need 2.");
							return true;
						}else if(dummy.getType() == 2 && dummy.getCoords().size() < 3){
							player.sendMessage(ChatColor.RED.toString() + "Not enough coordinates set for this zone type, you need atleast 3.");
							return true;
						}
						if (dummy.getMax() == 130 && dummy.getMin() == 0)
							player.sendMessage(ChatColor.RED.toString() + "WARNING: default z values not changed!");

						player.sendMessage(ChatColor.YELLOW.toString() + "If you are sure you want to save this zone do /zconfirm");

						dummy.setConfirm("merge");
					}
				}
				// disabled until i think of a better name.
//				else if (cmd.equalsIgnoreCase("/ztogglehealth")) {
//					dummy.toggleHealth();
//					player.sendMessage(ChatColor.GREEN.toString() + "Health is now " + (dummy.healthAllowed() ? "enabled" : "disabled") + ".");
//				}
			}
			return true;
		}

		return false;
	}
		public static final Map<String, String[]> commands;
	static {
		commands = new LinkedHashMap<String,String[]>();
		commands.put("/zcreate", new String[] {
			"zones.create",
			"[zone name] - starts zone creation in a new zone.",

			"Starts Zone creation mode in which you can set the \n zones perimiter and type and height en depth."
		});

		commands.put("/zadd", new String[] {
			"zones.create",
			"- adds the current location to the temp zone.",
			"Adds the current player x and y as a point of the  \n zone you are making."
		});

		commands.put("/zremove", new String[] {
			"zones.create",
			"- removes the current location from the temp zone.",
			"If the current player location is a point of \n the zone you are making it will be removed from the zone \n you are making. "
		});

		commands.put("/zsetplot", new String[] {
			"zones.create",
			"- set height and depth to according to plot specs.",
			"Changes the zone you are making to a plot type \n with the related height and depth of the zone relative to \n your z position."
		});

		commands.put("/zhelp",new String[] {
			null,
			"<cmd> - shows <cmd> page/command from the zone help.",
			"Shows <cmd> page or command description from the \n zone help file."
		});

		commands.put("/zsetheight",new String[] {
			"zones.create",
			 "[height] - sets maxz to current z + [height].",
			"Sets the zone you are creating height to your \n current z position + [height]"
		});

		commands.put("/zsetdepth",new String[] {
			"zones.create",
			 "[depth] - sets minz to current z - [depth].",
			"Sets the depth of the zone you are creating \n to your current z position - [depth]"
		});

		commands.put("/zsave",new String[] {
			"zones.create",
			"- saves the temp zone after confirmation.",
			"Initiates saving of the zone you were creating \n you will need to confirm this with \n /zconfirm to make it actually save the zone. \n THIS CANNOT BE USED WHEN EDITTING A ZONE USE /zmerge!!!! "
		});

		commands.put("/zconfirm",new String[] {
			"zones.create",
			"- confirms confirmations.",
			"Confirms the last action that needs confirmation \n needed when /zsave or /zstop is used."
		});

		commands.put("/zsetz",new String[] {
			"zones.create",
			"[minz] [maxz] - sets minz, maxz, range [0-127].",
			"Sets the depth and height of the zone according to \n [minz] and [maxz] limited by the max \n and min height of the map [0-127]."
		});

		commands.put("/zstop",new String[] {
			"zones.create",
			"- stop creation and delete zone (asks confirmation).",
			"Stops the creation of the current zone and deletes \n all relative data this needs to be confirmed \n with /zconfirm though."
		});
			//ttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttT
		commands.put("/zsetuser",new String[] {
			null,
			"[user name] b|m|d|e|h|*|- (combination of these)",
			"Sets the access of [user name] to what is specified\n "
			+ "b = Build(placing blocks),\n"
			+ "m = Modify(accessing chest/furnaces),\n "
			+ "d = Destroy(destroying blocks),\n"
			+ "e = Enter(entering your zone),\n"
			+ "h = Hit Entity's(killing mobs/destroying minecarts or boats),\n"
			+ "* = full access(all of the above) and - = remove all access. \n"
			+ "Example: /zsetuser Meaglin bde this will give meaglin access \n"
			+ " to build,destroy and walk around in your zone but not to \n"
			+ "access your chests."
		});

		commands.put("/zsetgroup",new String[] {
			null,
			"[group name] b|m|d|e|h|*|- (combination of these)",
			"Sets the access of [group name] to what is specified \n "
			+ "Possible group names: beunhaas, default, builder and vip \n"
			+ "b = Build(placing blocks),\n"
			+ "m = Modify(accessing chest/furnaces),\n "
			+ "d = Destroy(destroying blocks),\n"
			+ "e = Enter(entering your zone), \n"
			+ "h = Hit Entity's(killing mobs/destroying minecarts or boats),\n"
			+ "* = full access(all of the above) and - = remove all access. \n"
			+ "Example: /zsetuser default bde this will give all users access \n"
			+ " to build,destroy and walk around in your zone but not to \n"
			+ "access your chests."

		});

		commands.put("/zaddadmin",new String[] {
			null,
			"[user name]",
			"Adds [user name] as admin to your zone which gives \n"
			+ "[user name] rights to build,modify,destroy,enter your zone \n"
			+ "and to give other people rights to do so . (access to \n"
			+ "/zsetuser and /zsetgroup in your zone)"
		});

		commands.put("/zremoveadmin",new String[] {
			"zones.admin",
			"[user name]",
			"Removes [user name] as an admin from the zone."
		});

		commands.put("/zselect",new String[] {
			null,
			"<zone id>",
			"Selects a zone so you can modify the rights of the zone and \n"
			+ "or modify other properties of the zone."
		});

		commands.put("/zsettype",new String[] {
			"zones.create",
			"Cuboid|NPoly - changes zone type.",
			"changes the zone type to a square(cuboid) or polygon(NPoly)."
		});

		commands.put("/zregion",new String[] {
			"zones.info",
			" returns region info.",
			"Return the region x and y index and the amount of zones in \n"
			+ " the region"
		});

		commands.put("/zgetaccess",new String[] {
			null,
			"- sends a access list of the selected zone.",
			"Sends you a list of all the access given in your currently \n"
			+ "selected zone."
		});

		commands.put("/zdelete",new String[] {
			"zones.create",
			"- deletes selected zone.",
			"Deletes the currently selected zone, No confirmation!"
		});

		commands.put("/zsetname",new String[] {
			null,
			"[zone name] - changes zone name.",
			"Changes your current selected zones name to [zone name] \n"
			+ "(note: [zone name] is allowed to have spaces)."
		});
		commands.put("/ztogglehealth", new String[] {
			"zones.toggle.health",
			"Enables or disables health in the selected zone.",
			""
		});
		commands.put("/ztoggledynamite", new String[] {
			"zones.toggle.tnt",
			"Enables or disables dynamite in the selected zone.",
			""
		});
		commands.put("/ztogglelava", new String[] {
			"zones.toggle.lava",
			"Prevents or allowes lava flow into the zone.",
			""
		});
		commands.put("/ztogglewater", new String[] {
			"zones.toggle.water",
			"Prevents or allowes water flow into the zone..",
			""
		});
		commands.put("/ztogglemobs", new String[] {
	            "zones.toggle.mobs",
	            "Enables or disables mobs spawning inside the zone.",
	            ""
	        });
		commands.put("/ztoggleanimals", new String[] {
	            "zones.toggle.animals",
	            "Enables or disables animals spawning inside the zone.",
	            ""
	        });
		commands.put("/zedit", new String[] {
			"zones.create",
			"see extended help.",
			"loads the current selected zone into a dummy \n"
			+ "zone so it can be editted and merged with a zone.\n"
			+ "Edited zones CAN'T be saved as new zones but have to be MERGED!"
		});
		commands.put("/zmerge", new String[] {
			"zones.create",
			"merges the dummy zone points/form with the selected zone.",
			"Changes the 'area'/'form' of the zone you selected \n with your current dummy zone 'area'/'form'."
		});
		
	}


	private static Map<String, String[]> getCommands() { return commands; }


}
*/
