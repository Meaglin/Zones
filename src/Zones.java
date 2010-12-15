
import java.util.logging.Logger;

public class Zones extends Plugin {

	public static final int	_Rev	= 19;
	protected static final Logger		log	= Logger.getLogger("Minecraft");

	@Override
	public void initialize() {

		log.info("Zones Rev " + _Rev + "  Loading...");

		ZonesListener list = new ZonesListener();
		ZoneManager.getInstance();
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.COMPLEX_BLOCK_CHANGE, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.COMPLEX_BLOCK_SEND, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.TELEPORT, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.FLOW, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.EXPLODE, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.DAMAGE, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE, list, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.MOB_SPAWN, list, this, PluginListener.Priority.HIGH);

		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, list, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_CREATED, list, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, list, this, PluginListener.Priority.MEDIUM);

		etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, list, this, PluginListener.Priority.HIGH);
		
		log.info("Zones finished Loading.");
	}

	// we don't need to do anything here since the plugin always saves changes directly to the database.
	@Override
	public void disable() {}

	@Override
	public void enable() {}

}
