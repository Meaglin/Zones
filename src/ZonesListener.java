
public class ZonesListener extends PluginListener {

	public static final int _pilonHeight = 4;
	//bedrock
	public static final int _pilonType = 80;
	//stick
	public static final int _toolType = 280;

	@Override
	public boolean onBlockCreate(Player player, Block blockPlaced, Block blockClicked, int itemInHand) {
		if (itemInHand == _toolType) {
				ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy != null) {
					if (dummy.getType() == 1 && dummy.getCoords().size() == 2) {
						player.sendMessage(Colors.Rose + "You can only use 2 points to define a cuboid zone.");
						return true;
					}
					int[] p = new int[2];


					p[0] = World.toInt(blockClicked.getX());
					p[1] = World.toInt(blockClicked.getZ());

					if(blockClicked.getY() < World.MAX_Z-_pilonHeight){
						for(int i = 1;i <= _pilonHeight;i++){
							Block t = etc.getServer().getBlockAt(blockClicked.getX(), blockClicked.getY()+i, blockClicked.getZ());
							dummy.addDeleteBlock(t);
							t.setType(_pilonType);
							t.update();
						}
					}
					if(dummy.getCoords().contains(p)){
						player.sendMessage(Colors.Rose + "Already added this point.");
						return true;
					}

					player.sendMessage(Colors.Green + "Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
					dummy.addCoords(p);
				}
			}
		return false;
	}
	@Override
	public boolean onBlockDestroy(Player player, Block block) {
		if (player.getItemInHand() == _toolType) {
				ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
				if (dummy != null) {
					if(dummy.containsDeleteBlock(block)){
						int[] p = new int[2];
						p[0] = block.getX();
						p[1] = block.getZ();
						dummy.removeCoords(p);
						dummy.fix(block.getX(), block.getZ());
						player.sendMessage(Colors.Green + "Removed point [" + p[0] + "," + p[1] + "] from temp zone.");

					}else{
						player.sendMessage(Colors.Rose + "Couldn't find point in zone so nothing could be removed");
					}
				}
			}
        return false;
    }
    @Override
	public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {


		ZoneType zone = World.getInstance().getRegion(blockPlaced.getX(),blockPlaced.getZ()).getActiveZone(blockPlaced.getX(),blockPlaced.getZ(),blockPlaced.getY());
		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.BUILD)) {
			player.getInventory().update();
			player.sendMessage(Colors.Rose + "You cannot place blocks in '" + zone.getName() + "' !");
			return true;
		}else if(zone != null && (blockPlaced.getType() == 54 || blockPlaced.getType() == 61 || blockPlaced.getType() == 62 ) && !zone.canModify(player, ZonesAccess.Rights.MODIFY)){
			player.getInventory().update();
			player.sendMessage(Colors.Rose + "You cannot place chests/furnaces in '" + zone.getName() + "' since you don't have modify rights !");
			return true;
		} else if(player.canUseCommand("/build") || (zone != null && zone.canModify(player, ZonesAccess.Rights.BUILD)))
			return false;
		else{
			player.sendMessage(Colors.Rose + "You cannot build in the world.");
			return true;
		}
		
	}

    @Override
	public boolean onBlockBreak(Player player, Block block) {

		ZoneType zone = World.getInstance().getRegion(block.getX(),block.getZ()).getActiveZone(block.getX(),block.getZ(),block.getY());
		if (zone != null && !zone.canModify(player, ZonesAccess.Rights.DESTROY)) {
				player.sendMessage(Colors.Rose + "You cannot destroy blocks in '" + zone.getName() + "' !");
				return true;
		}else if(zone != null && (block.getType() == 54 || block.getType() == 61 || block.getType() == 62 ) && !zone.canModify(player, ZonesAccess.Rights.MODIFY)){

			if(block.getStatus() == 0)
				player.sendMessage(Colors.Rose + "You cannot destroy a chest/furnace in '" + zone.getName() + "' since you dont have modify rights!");

			return true;
		} else if(player.canUseCommand("/build") || (zone != null && zone.canModify(player, ZonesAccess.Rights.DESTROY)))
			return false;
		else{
			player.sendMessage(Colors.Rose + "You cannot destroy in the world.");
			return true;
		}
		

	}

	@Override
	public boolean onOpenInventory(Player player, Inventory inventory) {
		if(!(inventory instanceof ComplexBlock))
			return false;
		ComplexBlock block = (ComplexBlock)inventory;
		ZoneType zone = World.getInstance().getRegion(block.getX(),block.getZ()).getActiveZone(block.getX(),block.getZ(),block.getY());
		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.MODIFY)) {
			if(inventory instanceof Chest || inventory instanceof DoubleChest)
				player.sendMessage(Colors.Rose + "You cannot change chests in '" + zone.getName() + "' !");
			else if(inventory instanceof Furnace)
				player.sendMessage(Colors.Rose + "You cannot change furnaces in '" + zone.getName() + "' !");

			return true;
		} else if(player.canUseCommand("/build") || (zone != null && zone.canModify(player, ZonesAccess.Rights.MODIFY)))
			return false;
		else{
			
			if(inventory instanceof Chest || inventory instanceof DoubleChest)
				player.sendMessage(Colors.Rose + "You cannot change chests in the world !");
			else if(inventory instanceof Furnace)
				player.sendMessage(Colors.Rose + "You cannot change furnaces in the world !");
			return true;
		}
    }

//    @Override
//	public boolean onComplexBlockChange(Player player, ComplexBlock block) {
//
//		//Logger.getLogger("Minecraft").info("C modf " + block.getX() + "," + block.getY() + "," + block.getZ() +  "   " + player.getX() + "," + player.getY() + "," + player.getZ() +  "  ");
//		ZoneType zone = World.getInstance().getRegion(block.getX(),block.getZ()).getActiveZone(block.getX(),block.getZ(),block.getY());
//		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.MODIFY)) {
//			player.sendMessage(Colors.Rose + "You cannot change chests or furnaces in '" + zone.getName() + "' !");
//			return true;
//		} else if(player.canUseCommand("/build") || (zone != null && zone.canModify(player, ZonesAccess.Rights.MODIFY)))
//			return false;
//		else{
//			player.sendMessage(Colors.Rose + "You cannot modify chests or furnaces in the world.");
//			return true;
//		}
//	}
//
//    @Override
//	public boolean onSendComplexBlock(Player player, ComplexBlock block) {
//		if (block instanceof Sign)
//			return false;
//		//Logger.getLogger("Minecraft").info("C send " + block.getX() + "," + block.getY() + "," + block.getZ() +  "   " + player.getX() + "," + player.getY() + "," + player.getZ() +  "  ");
//		//you don't need a message here ;).
//		ZoneType zone = World.getInstance().getRegion(block.getX(),block.getZ()).getActiveZone(block.getX(),block.getZ(),block.getY());
//		if(zone != null && !zone.canModify(player, ZonesAccess.Rights.MODIFY))
//			return true;
//		else if(player.canUseCommand("/build") || (zone != null && zone.canModify(player, ZonesAccess.Rights.MODIFY)))
//			return false;
//		else
//			return true;
//
//
//	}

	@Override
	public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item) {
		int id = item.getItemId();
		if(id != 328 && id != 326 && id != 327 && id != 333 && id != 342 && id != 343 && id != 323)
			return false;

		ZoneType zone = World.getInstance().getActiveZone(blockPlaced.getX(),blockPlaced.getZ(),blockPlaced.getY());
		if (zone != null &&  !zone.canModify(player, ZonesAccess.Rights.BUILD)) {
			player.sendMessage(Colors.Rose + "You cannot place blocks in '" + zone.getName() + "' !");
			return true;
		} else if (player.canUseCommand("/build") || (zone != null && zone.canModify(player, ZonesAccess.Rights.BUILD)))
			return false;
		else
			return true;
    }

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {

		//debug only ;).
		//System.out.println("Moving from [" + Math.floor(from.x) + "," + Math.floor(from.y) + "," + Math.floor(from.z) + "] to [" + Math.floor(to.x) + "," + Math.floor(to.y) + "," + Math.floor(to.z) + "]" + "  [" + (Math.floor(to.x) - Math.floor(from.x)) + "," + (Math.floor(to.y) - Math.floor(from.y)) + "," + (Math.floor(to.z) - Math.floor(from.z)) + "]");

		//if the active zone changes we want to be sure the player can move into the zone.
		ZoneType aZone = World.getInstance().getRegion(from.x,from.z).getActiveZone(from.x,from.z,from.y);
		ZoneType bZone = World.getInstance().getRegion(to.x,to.z).getActiveZone(to.x,to.z,to.y);
		if(bZone != null &&
				(
						(aZone != null  && aZone.getId() != bZone.getId() && !bZone.canModify(player, ZonesAccess.Rights.ENTER))
							||
						(aZone == null && !bZone.canModify(player, ZonesAccess.Rights.ENTER))
				)
		){
			player.teleportTo(from);
			player.sendMessage(Colors.Rose + "You can't enter " + bZone.getName() + ".");
			//we don't have to do overall revalidation if the player gets warped back to his previous location.
			return;
		}
		if(bZone != null && !bZone.canModify(player, ZonesAccess.Rights.ENTER)){
			player.teleportTo(etc.getServer().getSpawnLocation());
			player.sendMessage(Colors.Rose + "You were moved to spawn because you were in an illigal position.");
		}

		World.getInstance().revalidateZones(player,World.toInt(from.x),World.toInt(from.z),World.toInt(to.x),World.toInt(to.z));
	}

    @Override
	public boolean onTeleport(Player player, Location from, Location to) {

		//if the active zone changes we want to be sure the player can move into the zone.
		ZoneType aZone = World.getInstance().getRegion(from.x,from.z).getActiveZone(from.x,from.z,from.y);
		ZoneType bZone = World.getInstance().getRegion(to.x,to.z).getActiveZone(to.x,to.z,to.y);
		if(bZone != null &&
				(
						(aZone != null  && aZone.getId() != bZone.getId() && !bZone.canModify(player, ZonesAccess.Rights.ENTER))
							||
						(aZone == null && !bZone.canModify(player, ZonesAccess.Rights.ENTER))
				)
		){
			player.sendMessage(Colors.Rose + "You cannot warp into " + bZone.getName() + ", since it is a protected area.");
			return true;
		}

		World.getInstance().revalidateZones(player,World.toInt(from.x),World.toInt(from.z),World.toInt(to.x),World.toInt(to.z));

		return false;
	}
	/*
     * Called when a dynamite block or a creeper is triggerd.
     * block status depends on explosive compound:
     * 1 = dynamite.
     * 2 = creeper.
     * @param block
     *          dynamite block/creeper location block.
     *
     * @return true if you dont the block to explode.
     */
	@Override
    public boolean onExplode(Block block) {

		ZoneType zone = World.getInstance().getActiveZone(block.getX(), block.getZ(), block.getY());
		if(zone != null && zone.allowDynamite(block))
			return false;
		else
			return true;
    }

	@Override
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {

		if(!defender.isPlayer())
			return false;

		if(PluginLoader.DamageType.FALL == type)
			return true;

		ZoneType zone = World.getInstance().getActiveZone(defender.getX(),defender.getZ(),defender.getY());
		if(zone != null && zone.allowHealth())
			return false;
		else
			return true;
    }

	/**
     * @param mob Mob attempting to spawn.
     * @return true if you dont want mob to spawn.
     */
	@Override
    public boolean onMobSpawn(Mob mob) {
		ZoneType zone = World.getInstance().getActiveZone(mob.getX(),mob.getZ(),mob.getY());
		if(zone != null && zone.allowHealth())
			return false;
		else
			return true;
    }

    /*
     * Called when fluid wants to flow to a certain block.
     * (10 & 11 for lava and 8 & 9 for water)
     *
     * @param blockFrom
     *              the block where the fluid came from.
     *              (blocktype = fluid type)
     * @param blockTo
     *              the block where fluid wants to flow to.
     *
     *
     * @return true if you dont want the substance to flow.
     */
	@Override
    public boolean onFlow(Block blockFrom, Block blockTo) {
		if(blockFrom.getType() == 8 || blockFrom.getType() == 9){

			ZoneType fromZone = World.getInstance().getActiveZone(blockFrom.getX(), blockFrom.getZ(), blockFrom.getY());
			ZoneType toZone = World.getInstance().getActiveZone(blockTo.getX(), blockTo.getZ(), blockTo.getY());

			if(toZone != null && (fromZone == null || fromZone.getId() != toZone.getId()) && !toZone.allowWater(blockTo))
				return true;
		}

		if(blockFrom.getType() == 10 || blockFrom.getType() == 11){

			ZoneType fromZone = World.getInstance().getActiveZone(blockFrom.getX(), blockFrom.getZ(), blockFrom.getY());
			ZoneType toZone = World.getInstance().getActiveZone(blockTo.getX(), blockTo.getZ(), blockTo.getY());

			if(toZone != null && (fromZone == null || fromZone.getId() != toZone.getId()) && !toZone.allowLava(blockTo))
				return true;
		}

        return false;
    }

	@Override
	public boolean onVehicleDamage(BaseVehicle vehicle, BaseEntity attacker, int damage) {
		if(!attacker.isPlayer())
			return false;

        ZoneType z = World.getInstance().getActiveZone(vehicle.getX(), vehicle.getZ(), vehicle.getY());
		if(z != null && !z.canModify(attacker.getPlayer(), ZonesAccess.Rights.DESTROY)){
			attacker.getPlayer().sendMessage("You cannot destroy vehicles in '" + z.getName() + "'!");
			return true;
		}else{
			return false;
		}
    }
	    /**
     * Called during the later login process
     *
     * @param player
     */
	@Override
    public void onLogin(Player player) {
		World.getInstance().getRegion(player).revalidateZones(player);
    }
	/**
     * Called on player disconnect
     *
     * @param player
     */
	@Override
    public void onDisconnect(Player player) {
		ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
		if (dummy != null) {
			dummy.setConfirm("stop");
			dummy.confirm(player);
		}
    }
	
	
    @Override
	public boolean onCommand(Player player, String[] split) {
		return ZonesCommandsHandler.onCommand(player, split);
	}

    

	
}
