import java.util.ArrayList;


public class Region {

	private ArrayList<ZoneType> _zones;
	public Region() {
		_zones = new ArrayList<ZoneType>();
	}
	public void addZone(ZoneType zone){
		if(_zones.contains(zone))
			return;
		
		_zones.add(zone);
	}
	public void removeZone(ZoneType zone){
		_zones.remove(zone);
	}
	public ArrayList<ZoneType> getZones(){
		return _zones;
	}
	
	public void revalidateZones(Player character)
	{	
		System.out.println("Revalidating zones.");
		for (ZoneType z : getZones())
		{
			if(z != null) z.revalidateInZone(character);
		}
	}
	
}
