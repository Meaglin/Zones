import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Abstract base class for any zone type Handles basic operations
 * 
 * @author durgus, Meaglin
 */
public abstract class ZoneType {
	protected static final Logger		log	= Logger.getLogger(ZoneType.class.getName());

	private final int					_id;
	protected List<ZoneForm>			_zone;
	protected HashMap<String, Player>	_characterList;
	protected HashMap<Integer, Integer>	_zones;

	private String						_name;
	private List<String>				_admingroups;
	private List<String>				_adminusers;

	private HashMap<String, Access>		_groups;
	private HashMap<String, Access>		_users;

	protected ZoneType(int id) {
		_id = id;
		_characterList = new HashMap<String, Player>();
		_zones = new HashMap<Integer, Integer>();

		_admingroups = new ArrayList<String>();
		_adminusers = new ArrayList<String>();

		_groups = new HashMap<String, Access>();
		_users = new HashMap<String, Access>();

	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	/**
	 * Setup new parameters for this zone
	 * 
	 * @param type
	 * @param value
	 */
	public void setParameter(String name, String value) {
		if (value == null || value.equals(""))
			return;

		if (name.equals("admins")) {
			String[] list = value.split(";");
			for (int i = 0; i < list.length; i++) {
				String[] item = list[i].split(",");

				switch (Integer.parseInt(item[0])) {
					// user
					case 1:
						_adminusers.add(item[1]);
						break;
					// group
					case 2:
						// boolean valid = false;

						// for(Group g : etc.getLoader().)
						// if(g.Name.equals(item[1]))
						// valid = true;

						// if(!valid){
						// log.info("Invalid admin grouptype in zonde id: " +
						// getId());
						// continue;
						// }
						_admingroups.add(item[1]);
						break;
					default:
						log.info("Unknown admin grouptype in zone id: " + getId());
						break;
				}
			}
		} else if (name.equals("users")) {
			String[] list = value.split(";");
			for (int i = 0; i < list.length; i++) {
				String[] item = list[i].split(",");
				int type = Integer.parseInt(item[0]);

				String itemname = item[1];
				String itemrights = item[2];
				// compatibility with old system.
				if (itemrights == null)
					itemrights = "*";

				switch (type) {
					// user
					case 1:
						_users.put(itemname, new Access(itemrights));
						break;
					// group
					case 2:
						// boolean valid = false;

						// for(Group g : etc.getDataSource().groups)
						// if(g.Name.equals(item[1]))
						// valid = true;

						// if(!valid){
						// log.info("Invalid admin grouptype in zonde id: " +
						// getId());
						// continue;
						// }
						_groups.put(itemname, new Access(itemrights));
						break;
					default:
						log.info("Unknown admin grouptype in zone id: " + getId());
						break;
				}
			}
		} else if (name.equals("name")) {
			_name = value;
		} else
			log.info(getClass().getSimpleName() + ": Unknown parameter - " + name + " in zone: " + getId());
	}

	/**
	 * Checks if the given character is affected by this zone
	 * 
	 * @param character
	 * @return
	 */
	private boolean isAffected(Player character) {
		return true;
	}

	/**
	 * Set the zone for this L2ZoneType Instance
	 * 
	 * @param zone
	 */
	public void setZone(ZoneForm zone) {
		getZones().add(zone);
	}

	/**
	 * Returns this zones zone form
	 * 
	 * @param zone
	 * @return
	 */
	public ZoneForm getZone() {
		for (ZoneForm zone : getZones()) {
			return zone;
		}
		return null;
	}

	public final List<ZoneForm> getZones() {
		if (_zone == null)
			_zone = new ArrayList<ZoneForm>();
		return _zone;
	}

	/**
	 * Checks if the given coordinates are within zone's plane
	 * 
	 * @param x
	 * @param y
	 */
	public boolean isInsideZone(int x, int y) {
		for (ZoneForm zone : getZones()) {
			if (zone.isInsideZone(x, y, zone.getHighZ()))
				return true;
		}
		return false;
	}

	/**
	 * Checks if the given coordinates are within the zone
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public boolean isInsideZone(int x, int y, int z) {
		for (ZoneForm zone : getZones()) {
			if (zone.isInsideZone(x, y, z))
				return true;
		}
		return false;
	}

	/**
	 * Checks if the given object is inside the zone.
	 * 
	 * @param object
	 */
	public boolean isInsideZone(Player object) {
		return isInsideZone((int) Math.floor(object.getX()), (int) Math.floor(object.getZ()), (int) Math.floor(object.getY()));
	}

	public double getDistanceToZone(int x, int y) {
		return getZone().getDistanceToZone(x, y);
	}

	public double getDistanceToZone(Player object) {
		return getZone().getDistanceToZone((int) Math.floor(object.getX()), (int) Math.floor(object.getZ()));
	}

	public void revalidateInZone(Player character) {

		// System.out.println("Revalidating zone " + getId());
		if (!isAffected(character))
			return;

		// If the object is inside the zone...
		if (isInsideZone((int) Math.floor(character.getX()), (int) Math.floor(character.getZ()), (int) Math.floor(character.getY()))) {
			// Was the character not yet inside this zone?
			if (!_characterList.containsKey(character.getName())) {
				_characterList.put(character.getName(), character);
				onEnter(character);
			}
		} else {
			// Was the character inside this zone?
			if (_characterList.containsKey(character.getName())) {
				_characterList.remove(character.getName());
				onExit(character);
			}
		}
	}

	/**
	 * Force fully removes a character from the zone Should use during teleport
	 * / logoff
	 * 
	 * @param character
	 */
	public void removeCharacter(Player character) {
		if (_characterList.containsKey(character.getName())) {
			_characterList.remove(character.getName());
			onExit(character);
		}
	}

	/**
	 * Will scan the zones char list for the character
	 * 
	 * @param character
	 * @return
	 */
	public boolean isCharacterInZone(Player character) {
		return _characterList.containsKey(character.getName());
	}

	protected abstract void onEnter(Player character);

	protected abstract void onExit(Player character);

	public HashMap<String, Player> getCharactersInside() {
		return _characterList;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + _id + "]";
	}

	public boolean canModify(Player player, Access.Rights right) {

		if (_users.containsKey(player.getName().toLowerCase()) && _users.get(player.getName().toLowerCase()).canDo(right))
			return true;

		for (String group : player.getGroups())
			if (_groups.containsKey(group.toLowerCase()) && _groups.get(group.toLowerCase()).canDo(right))
				return true;

		// Admins always have full access to the zone.
		return canAdministrate(player);
	}

	public boolean canAdministrate(Player player) {

		if (_adminusers.contains(player.getName().toLowerCase()))
			return true;

		for (String group : player.getGroups())
			if (_admingroups.contains(group.toLowerCase()))
				return true;

		return false;
	}

	public void addUser(String user, Access a) {
		user = user.toLowerCase();

		if (_users.containsKey(user))
			_users.remove(user);

		_users.put(user, a);

		updateRights();
	}

	public void addGroup(String group, Access a) {
		group = group.toLowerCase();

		if (_groups.containsKey(group))
			_groups.remove(group);

		_groups.put(group, a);

		updateRights();
	}

	public void addAdmin(String admin) {
		if (_adminusers.contains(admin.toLowerCase()))
			return;

		_adminusers.add(admin.toLowerCase());
		updateRights();
	}

	private void updateRights() {
		String admins = "";
		String users = "";
		for (Entry<String, Access> e : _users.entrySet()) {
			users += "1," + e.getKey() + "," + e.getValue().toString() + ";";
		}
		for (Entry<String, Access> e : _groups.entrySet()) {
			users += "2," + e.getKey() + "," + e.getValue().toString() + ";";
		}
		users = users.substring(0, users.length() - 1);

		for (String user : _adminusers) {
			admins += "1," + user + ";";
		}
		for (String group : _admingroups) {
			admins += "2," + group + ";";
		}
		admins = admins.substring(0, admins.length() - 1);
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = DB.getInstance().getConnection();
			st = conn.prepareStatement("UPDATE zones SET users = ?,admins = ? WHERE id = ?");
			st.setString(1, users);
			st.setString(2, admins);
			st.setInt(3, getId());
			st.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				st.close();
			} catch (Exception e) {
			}
		}
	}

	public void addUser(String string) {
		addUser(string, new Access("*"));
	}

	public void addGroup(String string) {
		addGroup(string, new Access("*"));
	}
}
