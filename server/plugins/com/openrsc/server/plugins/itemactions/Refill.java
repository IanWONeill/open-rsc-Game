package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Refill implements InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

	final int[] VALID_OBJECTS_WELL = {2, 466, 814};
	final int[] VALID_OBJECTS_OTHER = {48, 26, 86, 1130};
	private final int[] REFILLABLE = {
		ItemId.BUCKET.id(), ItemId.JUG.id(), ItemId.BOWL.id(), ItemId.EMPTY_VIAL.id()
	};
	private int[] REFILLED = {
		ItemId.BUCKET_OF_WATER.id(), ItemId.JUG_OF_WATER.id(), ItemId.BOWL_OF_WATER.id(), ItemId.VIAL.id()
	};

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return (inArray(obj.getID(), VALID_OBJECTS_OTHER)
			&& inArray(item.getID(),REFILLABLE)) || (inArray(obj.getID(), VALID_OBJECTS_WELL) && item.getID() == ItemId.BUCKET.id());
	}

	@Override
	public GameStateEvent onInvUseOnObject(GameObject obj, final Item item, Player player) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					for (int i = 0; i < REFILLABLE.length; i++) {
						if (REFILLABLE[i] == item.getID()) {
							final int itemID = item.getID();
							final int refilledID = REFILLED[i];
							player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Refill Water Jug", player.getInventory().countId(itemID), false) {
								@Override
								public void action() {
									if (getOwner().getInventory().hasInInventory(itemID)) {
										showBubble(getOwner(), item);
										getOwner().playSound("filljug");
										sleep(300);
										getOwner().message(
											"You fill the "
												+ item.getDef(getWorld()).getName().toLowerCase()
												+ " from the "
												+ obj.getGameObjectDef().getName().toLowerCase()
										);
										getOwner().getInventory().replace(itemID, refilledID,true);
									} else {
										interrupt();
									}
								}
							});
							break;
						}
					}

					return null;
				});
			}
		};
	}

}
