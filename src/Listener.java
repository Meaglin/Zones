import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Listener extends PluginListener {

	public boolean onBlockCreate(Player player, Block blockPlaced, Block blockClicked, int itemInHand) {
		if (canDoBlockModify(player, blockPlaced.getX(), blockPlaced.getZ(), blockPlaced.getY(), Access.Rights.BUILD)) {
			// player.giveItem(blockPlaced.getType(), 0);
			player.getInventory().updateInventory();
			// etc.getServer().setBlockAt(0, blockPlaced.getX(),
			// blockPlaced.getY(), blockPlaced.getZ());
			player.sendMessage(Colors.Red + "You cannot place blocks in this zone!");
			return true;
		} else {
			if (blockPlaced.getType() == 4) {
				DummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy != null) {
					if (dummy._type == 1 && dummy._coords.size() == 2) {
						player.sendMessage("You can only use 2 points to define a cuboid zone.");
						return true;
					}
					int[] p = new int[2];
					p[0] = (int) Math.floor(blockPlaced.getX());
					p[1] = (int) Math.floor(blockPlaced.getZ());
					for (int[] point : dummy._coords) {
						if (p[0] == point[0] && p[1] == point[1]) {
							player.sendMessage("Already added this point.");
							return true;
						}
					}
					player.sendMessage("Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
					dummy._coords.add(p);
					dummy.addDeleteBlock(blockPlaced);
					// blockPlaced.setType(0);
				}
			}
			return false;
		}
	}

	public boolean onBlockDestroy(Player player, Block block) {

		if (canDoBlockModify(player, block.getX(), block.getZ(), block.getY(), Access.Rights.DESTROY)) {
			player.sendMessage(Colors.Red + "You cannot destroy blocks in this zone!");
			return true;
		} else {
			if (block.getType() == 4) {
				DummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy != null) {
					int[] p = new int[2];
					p[0] = (int) Math.floor(player.getX());
					p[1] = (int) Math.floor(player.getZ());
					for (int[] point : dummy._coords) {
						if (p[0] == point[0] && p[1] == point[1]) {
							dummy._coords.remove(point);
							player.sendMessage("Removed point [" + p[0] + "," + p[1] + "] from temp zone.");
							return false;
						}
					}
					player.sendMessage("Couldn't find point in zone so nothing could be removed");
				}
			}
			return false;
		}

	}

	public boolean onComplexBlockChange(Player player, ComplexBlock block) {
		return canDoBlockModify(player, block.getX(), block.getZ(), block.getY(), Access.Rights.MODIFY);
	}

	public boolean onSendComplexBlock(Player player, ComplexBlock block) {
		if (block instanceof Sign)
			return false;

		return canDoBlockModify(player, block.getX(), block.getZ(), block.getY(), Access.Rights.MODIFY);

	}

	public boolean canDoBlockModify(Player player, int x, int y, int z, Access.Rights doen) {
		// Region reg = World.getInstance().getRegion(x, y);

		ArrayList<ZoneType> _zones = new ArrayList<ZoneType>();

		for (ZoneType zone : ZoneManager.getInstance().getAllZones())
			if (zone.isInsideZone(x, y, z))
				_zones.add(zone);

		if (_zones.size() == 0)
			return false;
		else if (_zones.size() == 1)
			return !_zones.get(0).canModify(player, doen);
		else {
			ZoneType _zone = null;
			for (ZoneType zone : _zones) {
				if (_zone == null || _zone.getZone().getSize() > zone.getZone().getSize())
					_zone = zone;
			}
			// preventing an NPE.
			if (_zone == null)
				return false;
			else
				return !_zone.canModify(player, doen);
		}

	}

	private static final int ITEMS_PER_PAGE = 7;
	public boolean onCommand(Player player, String[] split) {
		String cmd = split[0].toLowerCase();

		if (getCommands().containsKey(cmd)) {
			if (cmd.equalsIgnoreCase("/zcreate") && player.canUseCommand(cmd)) {
				if (split.length < 2) {
					player.sendMessage("Usage: /zcreate [zone name]");
				} else {
					String name = "";
					for (int i = 1; i < split.length; i++)
						name += " " + split[i];

					name = name.substring(1);
					ZoneManager.getInstance().addDummy(player.getName(), new DummyZone(name));
					player.sendMessage("Entering zone creation mode. Zone name: '" + name + "'");
					player.sendMessage("You can start adding the zone points of this zone by " + Colors.Red + " placing cobblestone blocks " + Colors.White + " or using " + Colors.Red + " /zadd");
				}
			} else if (cmd.equalsIgnoreCase("/zhelp")) {
				List<String> availableCommands = new ArrayList<String>();

				for (Entry<String, String> entry : getCommands().entrySet()) {
					if (player.canUseCommand(entry.getKey())) {
						availableCommands.add(entry.getKey() + " " + entry.getValue());
					}
				}

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

				for (int i = amount; i < amount + ITEMS_PER_PAGE; i++) {
					if (availableCommands.size() > i) {
						player.sendMessage(Colors.Rose + availableCommands.get(i));
					}
				}
			} else if (cmd.equalsIgnoreCase("/zadduser")) {
				if (split.length == 2) {
					ArrayList<ZoneType> zones = new ArrayList<ZoneType>();
					for (ZoneType zone : ZoneManager.getInstance().getAllZones())
						if (zone.isInsideZone(player) && zone.canAdministrate(player)) {
							zones.add(zone);
						}

					if (zones.size() < 1)
						player.sendMessage("No zones found in current area(which you can modify), please specify a zone id.");
					else if (zones.size() == 1) {
						zones.get(0).addUser(split[1]);
						player.sendMessage("Succesfully added " + split[1] + " as user of this zone.");
					} else {
						player.sendMessage("Too much zones found, please specify a zone id.");
						String temp = "";
						for (ZoneType zone : zones)
							temp += zone.getName() + "[" + zone.getId() + "]";
						player.sendMessage("Zones found: " + temp);
					}
				} else if (split.length == 3) {
					ZoneType zone = ZoneManager.getInstance().getZone(Integer.parseInt(split[2]));
					if (zone == null)
						player.sendMessage("No zone found with id : " + Integer.parseInt(split[2]));
					else if (!zone.canAdministrate(player))
						player.sendMessage("You don't have rights to administrate this zone.");
					else {
						zone.addUser(split[1]);
						player.sendMessage("Succesfully added " + split[1] + " as user of zone '" + zone.getName() + "' .");
					}
				} else {
					player.sendMessage("Usage: /zadduser [user name] <zone id>");
				}
			} else if (cmd.equalsIgnoreCase("/zaddgroup")) {
				if (split.length == 2) {
					ArrayList<ZoneType> zones = new ArrayList<ZoneType>();
					for (ZoneType zone : ZoneManager.getInstance().getAllZones())
						if (zone.isInsideZone(player) && zone.canAdministrate(player)) {
							zones.add(zone);
						}

					if (zones.size() < 1)
						player.sendMessage("No zones found in current area(which you can modify), please specify a zone id.");
					else if (zones.size() == 1) {
						zones.get(0).addGroup(split[1]);
						player.sendMessage("Succesfully added " + split[1] + " as group of this zone.");
					} else {
						player.sendMessage("Too much zones found, please specify a zone id.");
						String temp = "";
						for (ZoneType zone : zones)
							temp += zone.getName() + "[" + zone.getId() + "]";
						player.sendMessage("Zones found: " + temp);
					}
				} else if (split.length == 3) {
					ZoneType zone = ZoneManager.getInstance().getZone(Integer.parseInt(split[2]));
					if (zone == null)
						player.sendMessage("No zone found with id : " + Integer.parseInt(split[2]));
					else if (!zone.canAdministrate(player))
						player.sendMessage("You don't have rights to administrate this zone.");
					else {
						zone.addGroup(split[1]);
						player.sendMessage("Succesfully added " + split[1] + " as group of zone '" + zone.getName() + "' .");
					}
				} else {
					player.sendMessage("Usage: /zaddgroup [group name] <zone id>");
				}
			} else if (cmd.equalsIgnoreCase("/zaddadmin")) {
				if (split.length == 2) {
					ArrayList<ZoneType> zones = new ArrayList<ZoneType>();
					for (ZoneType zone : ZoneManager.getInstance().getAllZones())
						if (zone.isInsideZone(player) && zone.canAdministrate(player)) {
							zones.add(zone);
						}

					if (zones.size() < 1)
						player.sendMessage("No zones found in current area(which you can modify), please specify a zone id.");
					else if (zones.size() == 1) {
						zones.get(0).addAdmin(split[1]);
						player.sendMessage("Succesfully added " + split[1] + " as admin of this zone.");
					} else {
						player.sendMessage("Too much zones found, please specify a zone id.");
						String temp = "";
						for (ZoneType zone : zones)
							temp += zone.getName() + "[" + zone.getId() + "]";
						player.sendMessage("Zones found: " + temp);
					}
				} else if (split.length == 3) {
					ZoneType zone = ZoneManager.getInstance().getZone(Integer.parseInt(split[2]));
					if (zone == null)
						player.sendMessage("No zone found with id : " + Integer.parseInt(split[2]));
					else if (!zone.canAdministrate(player))
						player.sendMessage("You don't have rights to administrate this zone.");
					else {
						zone.addAdmin(split[1]);
						player.sendMessage("Succesfully added " + split[1] + " as admin of zone '" + zone.getName() + "' .");
					}
				} else {
					player.sendMessage("Usage: /zaddadmim [user name] <zone id>");
				}
			} else {

				DummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy == null) {
					player.sendMessage("First create a zone with:");
					player.sendMessage("/zcreate [zone name]");
					return true;
				}
				if (!cmd.equalsIgnoreCase("/zconfirm") && dummy._confirm != null) {
					dummy._confirm = null;
				}

				if (cmd.equalsIgnoreCase("/zadd")) {
					if (dummy._type == 1 && dummy._coords.size() == 2) {
						player.sendMessage("You can only use 2 points to define a cuboid zone.");
						return true;
					}
					int[] p = new int[2];
					p[0] = (int) Math.floor(player.getX());
					p[1] = (int) Math.floor(player.getZ());
					for (int[] point : dummy._coords) {
						if (p[0] == point[0] && p[1] == point[1]) {
							player.sendMessage("Already added this point.");
							return true;
						}
					}
					player.sendMessage("Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
					dummy._coords.add(p);
				} else if (cmd.equalsIgnoreCase("/zremove")) {
					int[] p = new int[2];
					p[0] = (int) Math.floor(player.getX());
					p[1] = (int) Math.floor(player.getZ());
					for (int[] point : dummy._coords) {
						if (p[0] == point[0] && p[1] == point[1]) {
							dummy._coords.remove(point);
							player.sendMessage("Removed point from temp zone.");
							return true;
						}
					}
					player.sendMessage("Couldn't find point in zone so nothing could be removed");
				} else if (cmd.equalsIgnoreCase("/zsetplot")) {
					if ((dummy._maxz - dummy._minz) == 19) {
						dummy._minz = 0;
						dummy._maxz = 127;
						player.sendMessage("Reseted the temp zones minz and maxz to 0 and 127");
					} else {
						dummy._minz = (int) Math.floor(player.getY()) - 10;
						dummy._maxz = (int) Math.floor(player.getY()) + 10;
						player.sendMessage("Current temp zone has now plot height and depth");
					}
				} else if (cmd.equalsIgnoreCase("/zsetheight")) {
					if (split.length < 2 || Integer.parseInt(split[1]) < 1) {
						player.sendMessage("Usage: /zsetheight [height]");
					} else {
						dummy._maxz = (int) Math.floor(player.getY()) + Integer.parseInt(split[1]) - 1;
						if (dummy._maxz < 0)
							dummy._maxz = 0;
						if (dummy._maxz > 127)
							dummy._maxz = 127;
						player.sendMessage("Max z is now : " + dummy._maxz);
					}
				} else if (cmd.equalsIgnoreCase("/zsetdepth")) {
					if (split.length < 2 || Integer.parseInt(split[1]) < 1) {
						player.sendMessage("Usage: /zsetdepth [depth]");
					} else {
						dummy._minz = (int) Math.floor(player.getY()) - Integer.parseInt(split[1]);
						if (dummy._minz < 0)
							dummy._minz = 0;
						if (dummy._minz > 127)
							dummy._minz = 127;
						player.sendMessage("Min z is now : " + dummy._minz);
					}
				} else if (cmd.equalsIgnoreCase("/zsetz")) {
					if (split.length < 3 || Integer.parseInt(split[1]) < 0 || Integer.parseInt(split[1]) > 127 || Integer.parseInt(split[2]) < 0 || Integer.parseInt(split[2]) > 127) {
						player.sendMessage("Usage: /zsetz [min Z] [max Z]");
					} else {
						dummy._minz = Integer.parseInt(split[1]);
						dummy._maxz = Integer.parseInt(split[2]);
						player.sendMessage("Min z and Max z now changed to : " + dummy._minz + " and " + dummy._maxz);
					}
				} else if (cmd.equalsIgnoreCase("/zsave")) {
					if (dummy._coords.size() != 2) {
						player.sendMessage("Not enough coordinates set for this zone type, you need 2.");
						return true;
					}
					if (dummy._maxz == 127 && dummy._minz == 0)
						player.sendMessage("WARNING: default z values not changed!");
					player.sendMessage("If you are sure you want to safe this zone do /zconfirm");
					dummy._confirm = "save";
				} else if (cmd.equalsIgnoreCase("/zstop")) {
					player.sendMessage("You sure you want to stop making a zone? if you are do /zconfirm");
					dummy._confirm = "stop";
				} else if (cmd.equalsIgnoreCase("/zconfirm")) {
					if (dummy._confirm == null) {
						player.sendMessage("Nothing to confirm.");
					} else if (dummy._confirm.equals("save")) {
						int[][] points = dummy._coords.toArray(new int[dummy._coords.size()][]);

						Class<?> newZone;
						try {
							newZone = Class.forName(dummy._class);
						} catch (ClassNotFoundException e) {
							player.sendMessage("No such zone class: " + dummy._class);
							return true;
						}
						Connection conn = null;
						PreparedStatement ps = null;
						ResultSet rs = null;
						int id = -1;
						try {
							conn = DB.getInstance().getConnection();
							ps = conn.prepareStatement("INSERT INTO zones (name,class,admins,users,minz,maxz,size) VALUES (?,?,'2,admins','',?,?,2) ", Statement.RETURN_GENERATED_KEYS);
							ps.setString(1, dummy._name);
							ps.setString(2, dummy._class);
							ps.setInt(3, dummy._minz);
							ps.setInt(4, dummy._maxz);

							ps.executeUpdate();

							rs = ps.getGeneratedKeys();
							if (rs.next()) {
								id = rs.getInt(1);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								if (ps != null) {
									ps.close();
								}
								if (rs != null) {
									rs.close();
								}
								if (conn != null) {
									conn.close();
								}
							} catch (SQLException ex) {
							}
						}
						if (id == -1) {
							player.sendMessage("sql error");
							return true;
						}
						Constructor<?> zoneConstructor;
						ZoneType temp = null;
						try {
							zoneConstructor = newZone.getConstructor(int.class);
							temp = (ZoneType) zoneConstructor.newInstance(id);
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int i = 0; i < points.length; i++) {
							if (points[i] == null)
								continue;
							PreparedStatement ps2 = null;
							Connection conn2 = null;
							try {
								conn2 = DB.getInstance().getConnection();
								ps2 = conn2.prepareStatement("INSERT INTO zones_vertices (`id`,`order`,`x`,`y`) VALUES (?,?,?,?) ");
								ps2.setInt(1, id);
								ps2.setInt(2, i);
								ps2.setInt(3, points[i][0]);
								ps2.setInt(4, points[i][1]);

								ps2.execute();
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								try {
									if (ps2 != null) {
										ps2.close();
									}
									if (conn2 != null) {
										conn2.close();
									}
								} catch (SQLException ex) {
								}
							}
						}
						temp.setZone(new ZoneCuboid(points[0][0], points[1][0], points[0][1], points[1][1], dummy._minz, dummy._maxz));
						temp.setParameter("admins", "2,admins");
						temp.setParameter("name", dummy._name);
						ZoneManager.getInstance().addZone(temp);
						ZoneManager.getInstance().removeDummy(player.getName());
						dummy.deleteBlocks();
						player.sendMessage(Colors.Green + "Zone Saved.");
					} else if (dummy._confirm.equals("stop")) {
						ZoneManager.getInstance().removeDummy(player.getName());
						dummy.deleteBlocks();
						player.sendMessage(Colors.Red + "Zone mode stopped, temp zone deleted.");
					}
				}
			}
			return true;
		}

		return false;
	}

	public boolean onChat(Player player, String message) {

		if (message.toLowerCase().contains("aziz light"))
			etc.getServer().setTime(0);
		else if (message.toLowerCase().contains("aziz no light"))
			etc.getServer().setTime(13000);

		return false;
	}

	public void onPlayerMove(Player player, Location from, Location to) {

		// no actual change,just within the block.
		if (Math.floor(from.x) == Math.floor(to.x) && Math.floor(from.y) == Math.floor(to.y) && Math.floor(from.z) == Math.floor(to.z))
			return;
		System.out.println("Moving from [" + Math.floor(from.x) + "," + Math.floor(from.y) + "," + Math.floor(from.z) + "] to [" + Math.floor(to.x) + "," + Math.floor(to.y) + "," + Math.floor(to.z) + "]" + "  [" + (Math.floor(to.x) - Math.floor(from.x)) + "," + (Math.floor(to.y) - Math.floor(from.y)) + "," + (Math.floor(to.z) - Math.floor(from.z)) + "]");
		/*
		 * Region f = World.getInstance().getRegion(from.x, from.z); Region t =
		 * World.getInstance().getRegion(to.x, to.z);
		 * 
		 * if(f == t){ t.revalidateZones(player); }else{
		 * f.revalidateZones(player); t.revalidateZones(player); }
		 */
		for (ZoneType zone : ZoneManager.getInstance().getAllZones())
			zone.revalidateInZone(player);
	}

	public static final HashMap<String, String> commands;
	static {
		commands = new HashMap<String,String>();
		commands.put("/zcreate", "[zone name] - creates at temp zone with name [zone name] and starts zone creation mode for that player.");
		commands.put("/zadd", "- adds the current player location as a point to the temp zone.");
		commands.put("/zremove", "- removes the current player location as a point from the temp zone.");
		commands.put("/zsetplot", "- sets the height and depth of this zone as ones of a plot.");
		commands.put("/zhelp", "<page id> - shows <page id> page from the zone help list.");
		commands.put("/zsetheight", "[height] - sets the max z of the temp zone as [height] + z of the block ur standing on.");
		commands.put("/zsetdepth", "[depth] - sets the minz of the temp zone as player z - [depth].");
		commands.put("/zsave", "- saves the temp zone after confirmation.");
		commands.put("/zconfirm", "- confirms the last action wich needed confirmation.");
		commands.put("/zsetz", "[minz] [maxz] - sets the min and max z of the temp zone, Range: [0-127].");
		commands.put("/zstop", "- stops the zone creation after confirmation and deletes the temp zone.");
		commands.put("/zadduser", "[user name] <zone id>");
		commands.put("/zaddgroup", "[group name] <zone id>");
		commands.put("/zaddadmin", "[user name] <zone id>");
	}
	private HashMap<String, String> getCommands() { return commands; }
}
