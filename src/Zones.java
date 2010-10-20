public class Zones extends Plugin {

	public static final int	_Rev	= 2;

	@Override
	public void initialize() {

		System.out.println("Zones Rev " + _Rev + "  Loading...");
		Listener list = new Listener();
		ZoneManager.getInstance();
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_CREATED, list,
				this, PluginListener.Priority.CRITICAL);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, list,
				this, PluginListener.Priority.CRITICAL);
		etc.getLoader().addListener(PluginLoader.Hook.COMPLEX_BLOCK_CHANGE,
				list, this, PluginListener.Priority.CRITICAL);
		etc.getLoader().addListener(PluginLoader.Hook.COMPLEX_BLOCK_SEND, list,
				this, PluginListener.Priority.CRITICAL);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, list, this,
				PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, list, this,
				PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.CHAT, list, this,
				PluginListener.Priority.LOW);
		System.out.println("Zones finished Loading.");
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enable() {
		// TODO Auto-generated method stub

	}

}
