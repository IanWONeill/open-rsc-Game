package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToObjectAction;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public class ItemUseOnObject implements PacketHandler {

	private void handleDoor(final Player player, final Point location,
							final GameObject object, final int dir, final Item item) {
		player.setStatus(Action.USING_Item_ON_DOOR);
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void execute() {
				player.resetPath();
				GameObject obj = player.getViewArea().getWallObjectWithDir(
					object.getLocation(), object.getDirection());
				if (player.isBusy() || player.isRanging()
					|| !player.getInventory().hasItemId(item.getID()) || obj == null
					|| !obj.equals(object)
					|| player.getStatus() != Action.USING_Item_ON_DOOR) {
					return;
				}
				player.resetAll();

				if (item.getDef(player.getWorld()).isMembersOnly()
					&& !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
					player.message(player.MEMBER_MESSAGE);
					return;
				}
				if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(
					"InvUseOnWallObject",
					new Object[]{object, item, player}))
					return;
			}
		});
	}

	private void handleObject(final Player player, final Point location,
							  final GameObject object, final Item item) {
		player.setStatus(Action.USING_Item_ON_OBJECT);
		if ((object.getID() == 226 || object.getID() == 232) && player.withinRange(object, 2)) {
			player.resetPath();
			player.resetAll();
			if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(
				"InvUseOnObject", new Object[]{object, item, player}))
				return;
		}
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void execute() {
				player.resetPath();
				player.face(object);
				GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (obj == null || player.isBusy() || player.isRanging()
					|| !player.getInventory().contains(item)
					|| !player.atObject(object) || obj == null
					|| player.getStatus() != Action.USING_Item_ON_OBJECT) {
					return;
				}
				player.resetAll();

				if (item.getDef(player.getWorld()).isMembersOnly()
					&& !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
					player.message(player.MEMBER_MESSAGE);
					return;
				}

				if (player.getWorld().getServer().getPluginHandler()
					.blockDefaultAction("InvUseOnObject",
						new Object[]{(GameObject) object, item, player}))
					return;

				// Using items on objects
				switch (object.getID()) {
					default:
						// owner.message("Nothing interesting happens");
						return;
				}
			}
		});
	}

	public void handlePacket(Packet p, Player player) throws Exception {

		int pID = p.getID();
		if (player.isBusy()) {
			player.resetPath();// sendSound
			return;
		}
		player.resetAll();
		GameObject object;
		Item item;
		int packetOne = OpcodeIn.WALL_USE_ITEM.getOpcode();
		int packetTwo = OpcodeIn.OBJECT_USE_ITEM.getOpcode();

		if (pID == packetOne) { // Use Item on Door
			object = player.getViewArea().getWallObjectWithDir(Point.location(p.readShort(), p.readShort()), p.readByte());
			if (object == null) {
				player.setSuspiciousPlayer(true, "item on null door");
				player.resetPath();
				return;
			}
			int dir = object.getDirection();
			int slotID = p.readShort();
			if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && slotID == -1)
			{
				//they used the item from their equipment slot
				int itemID = p.readShort();
				int realSlot = player.getEquipment().hasEquipped(itemID);
				if (realSlot == -1)
					return;
				item = player.getEquipment().get(realSlot);
				if (item == null)
					return;
			} else
				item = player.getInventory().get(slotID);
			if (object == null || object.getType() == 0 || item == null) { // This
				player.setSuspiciousPlayer(true, "item on null equipment slot or something");
				return;
			}
			handleDoor(player, object.getLocation(), object, dir, item);
		} else if (pID == packetTwo) { // Use Item on GameObject
			object = player.getViewArea().getGameObject(Point.location(p.readShort(), p.readShort()));
			if (object == null) {
				player.setSuspiciousPlayer(true, "item on null GameObject");
				player.resetPath();
				return;
			}
			int slotID = p.readShort();
			if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && slotID > Inventory.MAX_SIZE) {
				item = player.getEquipment().get(slotID - Inventory.MAX_SIZE);
			} else
				item = player.getInventory().get(slotID);
			if (object == null || object.getType() == 1 || item == null) { // This
				player.setSuspiciousPlayer(true, "null item or object");
				return;
			}
			handleObject(player, object.getLocation(), object, item);
		}
	}

}
