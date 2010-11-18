import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class ZonesDummyZone {

	public String			_name;
	private String _class;
	public int				_type;
	public final ArrayList<int[]>	_coords;
	public final ArrayList<int[]>	_deleteBlocks;
	public int				_minz, _maxz;
	private String			_confirm;
	protected static final Logger		log	= Logger.getLogger("Minecraft");
	
	public ZonesDummyZone(String name) {
		_name = name;
		_type = 1;
		_minz = 0;
		_maxz = 127;
		_class = "ZoneNormal";
		_coords = new ArrayList<int[]>();
		_deleteBlocks = new ArrayList<int[]>();
	}
	
	public void setZ(int min,int max){
		if(min > 127)min = 127;
		if(min < 0)min = 0;
		
		if(max > 127)max = 127;
		if(max < 0)max = 0;
		
		if(min > max){
			int t = max;
			max = min;
			min = t;
		}
		_minz = min;
		_maxz = max;
	}
	public int getMax(){return _maxz; }
	public int getMin(){return _minz; }
	
	public ArrayList<int[]> getCoords() { return _coords; }
	public void addCoords(int[] c ) { _coords.add(c); }
	public void removeCoords(int[] r ) { 
		
		for(int i = 0; i < _coords.size();i++)
			if(Arrays.equals(_coords.get(i),r)){
				_coords.remove(i);

		}
	}
	
	public void setConfirm(String c){ _confirm = c; }
	public void confirm(Player p){
		if (_confirm == null) {
			p.sendMessage("Nothing to confirm.");
		} else if(_confirm.equals("save")) {
			ZoneManager.getInstance().removeDummy(p.getName());
			if(Save())
				p.sendMessage(Colors.Green + "Zone Saved.");
			else
				p.sendMessage(Colors.Red + "Error saving zone.");
		} else if (_confirm.equals("stop")) {
			ZoneManager.getInstance().removeDummy(p.getName());
			Delete();
			p.sendMessage(Colors.Red + "Zone mode stopped, temp zone deleted.");
		}
	}
	
	
	public void setClass(Player p, String name){
		 
		try {
			@SuppressWarnings("unused")
			Class<?> t = Class.forName(name);
		} catch (ClassNotFoundException e) {
			p.sendMessage("No such zone class: " + name);
			return;
		}
		_class = name;
	}
	
	
	
	private boolean Save(){
		int[][] points = _coords.toArray(new int[_coords.size()][]);

		Class<?> newZone = null;
		try {
			newZone = Class.forName(_class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		int id = -1;
		try {
			conn = etc.getSQLConnection();
			st = conn.prepareStatement("INSERT INTO zones (name,class,type,admins,users,minz,maxz,size) VALUES (?,?,?,'2,admin;2,serveradmin','2,default,e',?,?,?) ", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, _name);
			st.setString(2, _class);
			st.setInt(3, _type);
			st.setInt(4, _minz);
			st.setInt(5, _maxz);
			st.setInt(6, _coords.size());

			st.executeUpdate();

			rs = st.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)		st.close();
				if (rs != null)		rs.close();
				if (conn != null)	conn.close();
			} catch (SQLException ex) {}
		}
		//SQL error, so were gonna stop here.
		if (id == -1)
			return false;
			
		Constructor<?> zoneConstructor;
		ZoneType temp = null;
		try {
			zoneConstructor = newZone.getConstructor(int.class);
			temp = (ZoneType) zoneConstructor.newInstance(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(temp == null)
			return false;
		
		for (int i = 0; i < points.length; i++) {
			if (points[i] == null)
				continue;
			PreparedStatement st2 = null;
			Connection conn2 = null;
			try {
				conn2 = etc.getSQLConnection();
				st2 = conn2.prepareStatement("INSERT INTO zones_vertices (`id`,`order`,`x`,`y`) VALUES (?,?,?,?) ");
				st2.setInt(1, id);
				st2.setInt(2, i);
				st2.setInt(3, points[i][0]);
				st2.setInt(4, points[i][1]);

				st2.execute();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (st2 != null)	st2.close();
					if (conn2 != null)	conn2.close();
				} catch (SQLException ex) {}
			}
		}
		int[][] coords = _coords.toArray(new int[_coords.size()][]);
		switch (_type) {
			case 1:
				if (_coords.size() == 2) {
					temp.setZone(new ZoneCuboid(coords[0][0], coords[1][0], coords[0][1], coords[1][1], _minz, _maxz));
				} else {
					log.info("Missing zone vertex for cuboid zone id: " + id);
					return false;
				}
				break;
			case 2:
				if (coords.length > 2) {
					final int[] aX = new int[coords.length];
					final int[] aY = new int[coords.length];
					for (int i = 0; i < coords.length; i++) {
						aX[i] = coords[i][0];
						aY[i] = coords[i][1];
					}
					temp.setZone(new ZoneNPoly(aX, aY, _minz, _maxz));
				} else {
					log.warning("Bad data for zone: " + id);
					return false;
				}
				break;
			default:
				log.severe("Unknown zone form " + _type + " for id " + id);
				break;
		}
		temp.setParameter("admins", "2,admin;2,serveradmin");
		temp.setParameter("users", "2,default,e");
		temp.setParameter("name",_name);
		ZoneManager.getInstance().addZone(temp);
		revertBlocks();
		
		return true;
	}
	public void Delete(){
		revertBlocks();
	}
	
	private void revertBlocks() {

			for (int[] block : _deleteBlocks)
				etc.getServer().setBlockAt(block[3], block[0], block[1], block[2]);

			_deleteBlocks.clear();
		
	}

	public void addDeleteBlock(Block block) {

			_deleteBlocks.add(new int[] { block.getX(), block.getY(), block.getZ(),block.getType() });
		
	}
	public boolean containsDeleteBlock(Block block){

			for(int[] b : _deleteBlocks)
				if(b[0] == block.getX() && b[1] == block.getY() && b[2] == block.getZ())
					return true;
		
		return false;
	}
	
	public void fix(int x,int y){

			ArrayList<int[]> list = ((ArrayList<int[]>)_deleteBlocks.clone());
			for (int[] block : list)
				if(block[0] == x && block[2] == y){
					etc.getServer().setBlockAt(block[3], block[0], block[1], block[2]);
					_deleteBlocks.remove(block);
				}
		
	}

	public void makePlot(Player player) {
		if(_class.equals("ZonePlot")){
			setZ(0,127);
			_class = "ZoneNormal";
			player.sendMessage("Reverted zone to default z and class.");
		}else{
			setZ(World.toInt(player.getY()) - 10,World.toInt(player.getY()) + 10);
			_class = "ZonePlot";
			player.sendMessage("Zone is now a plot zone.");
		}
	}

	public void setType(String string) {
		if(string.equals("Cuboid")){
			_type = 1;
			_coords.clear();
			revertBlocks();
		}
		else if(string.equals("NPoly"))
			_type = 2;
		else{
			log.info("Trying to set a invalid zone shape in dummyZone, type: " + string);
		}
	}
}
