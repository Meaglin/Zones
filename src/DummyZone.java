import java.util.ArrayList;

public class DummyZone {

	public String			_name, _class;
	public int				_type;
	public ArrayList<int[]>	_coords;
	public ArrayList<int[]>	_deleteBlocks;
	public int				_minz, _maxz;
	public String			_confirm;

	public DummyZone(String name) {
		_name = name;
		_type = 1;
		_minz = 0;
		_maxz = 127;
		_class = "ZoneNormal";
		_coords = new ArrayList<int[]>();
		_deleteBlocks = new ArrayList<int[]>();
	}

	public void deleteBlocks() {
		for (int[] block : _deleteBlocks) {
			etc.getServer().setBlockAt(0, block[0], block[1], block[2]);
		}
	}

	public void addDeleteBlock(Block block) {
		_deleteBlocks
				.add(new int[] { block.getX(), block.getY(), block.getZ() });

	}
}
