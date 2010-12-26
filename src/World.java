import java.util.ArrayList;

public class World {
	public static final int	MIN_X			= -10240;
	public static final int	MAX_X			= 10240;

	public static final int	MIN_Y			= -10240;
	public static final int	MAX_Y			= 10240;

	public static final int	MIN_Z 			= 0;
	public static final int	MAX_Z			= 127;

	public static final int	SHIFT_SIZE		= 8;
	public static final int	BLOCK_SIZE		= (int) (Math.pow(2, SHIFT_SIZE) - 1);

	public static final int	X_REGIONS		= ((MAX_X - MIN_X) >> SHIFT_SIZE) + 1;
	public static final int	Y_REGIONS		= ((MAX_Y - MIN_Y) >> SHIFT_SIZE) + 1;

	public static final int XMOD = (MIN_X < 0 ? -1 : 1);
	public static final int YMOD = (MIN_Y < 0 ? -1 : 1);
	
	public static final int OFFSET_X = ((MIN_X * XMOD) >> SHIFT_SIZE)*XMOD;
	public static final int OFFSET_Y = ((MIN_Y * YMOD) >> SHIFT_SIZE)*YMOD;
	


	private Region[][]		_regions;

	public World() {
		
		try {
			_regions = new Region[X_REGIONS][Y_REGIONS];
			for (int x = 0; x < X_REGIONS; x++) {
				for (int y = 0; y < Y_REGIONS; y++) {
					_regions[x][y] = new Region(x,y);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ZoneManager.log.info("Loaded " + X_REGIONS*Y_REGIONS  + " regions.");
	}

	public Region getRegion(Player player) {
		return getRegion(player.getX(), player.getZ());
	}

	public Region getRegion(double x, double y) {
		return getRegion((int) Math.floor(x), (int) Math.floor(y));
	}

	public ArrayList<ZoneType> getAdminZones(Player player){
		return getRegion(player).getAdminZones(player);
	}
	
	public ArrayList<ZoneType> getActiveZones(Player player){
		return getRegion(player).getActiveZones(player);
	}
	
	public ZoneType getActiveZone(Player player){
		return getRegion(player).getActiveZone(player);
	}
	public ZoneType getActiveZone(double x,double y,double z){
		return getRegion(x,y).getActiveZone(x, y, z);
	}
	
	public Region getRegion(int x, int y) {
		//debug only ;) .
		//System.out.println("get region " + ((x - MIN_X) >> SHIFT_SIZE) + " " + ((y - MIN_Y) >> SHIFT_SIZE));
		if(x > MAX_X || x < MIN_X || y > MAX_Y || y < MIN_Y){
			ZoneManager.log.warning("Warning: Player moving outside world!");
			return new Region(0,0);
		}
		
		return _regions[(x - MIN_X) >> SHIFT_SIZE][(y - MIN_Y) >> SHIFT_SIZE];
	}

	public void addZone(int x, int y, ZoneType zone) {
		_regions[x][y].addZone(zone);
	}

	public static final World getInstance() {
		return SingletonHolder._instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final World	_instance	= new World();
	}

	public void revalidateZones(Player player, int ax, int ay, int bx, int by) {
		
		//region changes.
		if(!((ax - MIN_X) >> SHIFT_SIZE == (bx - MIN_X) >> SHIFT_SIZE && (ay - MIN_X) >> SHIFT_SIZE == (by - MIN_X) >> SHIFT_SIZE)){
			getRegion(ax,ay).revalidateZones(player);
		}
		//default revalidation.
		getRegion(bx,by).revalidateZones(player);
		
		
	}

	public static int toInt(double b){
		return (int) Math.floor(b);
	}
}
