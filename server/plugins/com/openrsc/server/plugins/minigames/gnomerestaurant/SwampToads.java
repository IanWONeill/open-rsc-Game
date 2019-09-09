package com.openrsc.server.plugins.minigames.gnomerestaurant;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;

public class SwampToads implements PickupListener, PickupExecutiveListener, InvActionListener, InvActionExecutiveListener {

	@Override
	public boolean blockInvAction(Item item, Player p, String command) {
		return item.getID() == ItemId.SWAMP_TOAD.id();
	}

	@Override
	public GameStateEvent onInvAction(Item item, Player p, String command) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (item.getID() == ItemId.SWAMP_TOAD.id()) {
						getPlayerOwner().message("you pull the legs off the toad");
						return nextState(3);
					}
					return null;
				});
				addState(1, () -> {
					getPlayerOwner().message("poor toad..at least they'll grow back");
					getPlayerOwner().getInventory().replace(item.getID(), ItemId.TOAD_LEGS.id());
					return null;
				});

			}
		};
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		return i.getID() == ItemId.SWAMP_TOAD.id();
	}

	@Override
	public GameStateEvent onPickup(Player p, GroundItem i) {
		if (i.getID() == ItemId.SWAMP_TOAD.id()) {
			return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
				public void init() {
					addState(0, () -> {
						getPlayerOwner().message("you pick up the swamp toad");
						if (DataConversions.random(0, 10) >= 3) {
							getPlayerOwner().message("but it jumps out of your hands..");
							return nextState(3);
						}

						i.remove();
						addItem(getPlayerOwner(), ItemId.SWAMP_TOAD.id(), 1);
						getPlayerOwner().message("you just manage to hold onto it");
						return null;
					});
					addState(1, () -> {
						getPlayerOwner().message("..slippery little blighters");
						return null;
					});
				}
			};
		}

		return null;
	}
}
