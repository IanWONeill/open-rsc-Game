package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestOnDrop implements DropListener, DropExecutiveListener {

	@Override
	public boolean blockDrop(Player p, Item i) {
		return inArray(i.getID(), ItemId.A_CHUNK_OF_CRYSTAL.id(), ItemId.A_LUMP_OF_CRYSTAL.id(), ItemId.A_HUNK_OF_CRYSTAL.id(),
				ItemId.A_RED_CRYSTAL.id(), ItemId.A_GLOWING_RED_CRYSTAL.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(),
				ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id(), ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id());
	}

	@Override
	public GameStateEvent onDrop(Player p, Item i) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (i.getID() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()
						|| i.getID() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id()) {
						removeItem(p, i.getID(), 1);
						p.message("You drop the bowl on the floor and the water spills out everywhere.");
						createGroundItem(p.getWorld(), ItemId.BLESSED_GOLDEN_BOWL.id(), 1, p.getX(), p.getY());
					}
					else if (i.getID() == ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id()
						|| i.getID() == ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id()) {
						removeItem(p, i.getID(), 1);
						p.message("You drop the bowl on the floor and the water spills out everywhere.");
						createGroundItem(p.getWorld(), ItemId.GOLDEN_BOWL.id(), 1, p.getX(), p.getY());
					}
					else if (inArray(i.getID(), ItemId.A_CHUNK_OF_CRYSTAL.id(), ItemId.A_LUMP_OF_CRYSTAL.id(), ItemId.A_HUNK_OF_CRYSTAL.id(),
						ItemId.A_RED_CRYSTAL.id(), ItemId.A_GLOWING_RED_CRYSTAL.id())) {
						removeItem(p, i.getID(), 1);
						message(p, 600, "The crystal starts fading..");
					}

					return null;
				});
			}
		};
	}
}
