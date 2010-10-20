public class ZoneNormal extends ZoneType {

	public ZoneNormal(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEnter(Player character) {
		character.sendMessage("You have just entered '" + getName() + "'.");

	}

	@Override
	public void onExit(Player character) {
		character.sendMessage("You have just exited '" + getName() + "'.");

	}

}
