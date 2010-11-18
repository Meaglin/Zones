
import java.util.logging.Logger;

public class Zones extends Plugin {

	public static final int	_Rev	= 4;
	protected static final Logger		log	= Logger.getLogger("Minecraft");

	@Override
	public void initialize() {

		log.info("Zones Rev " + _Rev + "  Loading...");

		ZonesListener list = new ZonesListener();
		ZoneManager.getInstance();

		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_CREATED, list, this, PluginListener.Priority.CRITICAL);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, list, this, PluginListener.Priority.CRITICAL);
		etc.getLoader().addListener(PluginLoader.Hook.COMPLEX_BLOCK_CHANGE, list, this, PluginListener.Priority.CRITICAL);
		etc.getLoader().addListener(PluginLoader.Hook.COMPLEX_BLOCK_SEND, list, this, PluginListener.Priority.CRITICAL);
		etc.getLoader().addListener(PluginLoader.Hook.TELEPORT, list, this, PluginListener.Priority.CRITICAL);
		
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, list, this, PluginListener.Priority.HIGH);
		
		log.info("Zones finished Loading.");
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
