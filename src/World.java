public class World {
	public static final int	minx			= -32000000;
	public static final int	maxx			= 32000000;

	public static final int	miny			= -32000000;
	public static final int	maxy			= 32000000;

	public static final int	minz			= 0;
	public static final int	maxz			= 127;

	public static final int	shiftsize		= 16;
	public static final int	blocksize		= (int) (Math.pow(2, shiftsize) - 1);

	public static final int	xregions		= ((maxx - minx) >> shiftsize) + 1;
	public static final int	yregions		= ((maxy - miny) >> shiftsize) + 1;

	public static final int	xregionoffset	= minx >> shiftsize;
	public static final int	yregionoffset	= miny >> shiftsize;

	private Region[][]		regions;

	public World() {
		System.out.println("init world");
		try {
			regions = new Region[xregions][yregions];
			System.out.println("init world2");
			for (int x = 0; x < xregions; x++) {
				for (int y = 0; y < yregions; y++) {
					if (x == 1 && y == 0)
						System.out.println("init world3");
					regions[x][y] = new Region();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("done world");
	}

	public Region getRegion(Player player) {
		return getRegion(player.getX(), player.getZ());
	}

	public Region getRegion(double x, double y) {
		return getRegion((int) Math.floor(x), (int) Math.floor(y));
	}

	public Region getRegion(int x, int y) {
		System.out.println("get region " + ((x - minx) >> shiftsize) + " " + ((y - miny) >> shiftsize));
		return regions[(x - minx) >> shiftsize][(y - miny) >> shiftsize];
	}

	public void addZone(int x, int y, ZoneType zone) {
		regions[x][y].addZone(zone);
	}

	public static final World getInstance() {
		return SingletonHolder._instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final World	_instance	= new World();
	}
}
