package com.openrsc.server.plugins.misc;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.random;
import static com.openrsc.server.plugins.Functions.showBubble;

public class Sheep implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.SHEEP.id() && item.getID() == ItemId.SHEARS.id();
	}

	@Override
	public GameStateEvent onInvUseOnNpc(Player player, Npc npc, Item item) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					npc.resetPath();

					npc.face(player);
					player.face(npc);
					showBubble(player, item);
					player.message("You attempt to shear the sheep");
					npc.setBusyTimer(2);
					player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1200, "Crafting Shear Wool", player.getInventory().getFreeSlots(), true) {

						@Override
						public void action() {
							npc.setBusyTimer(2);
							if (random(0, 4) != 0) {
								player.message("You get some wool");
								addItem(player, ItemId.WOOL.id(), 1);
							} else {
								player.message("The sheep manages to get away from you!");
								npc.setBusyTimer(0);
								interrupt();
							}
						}
					});

					return null;
				});
			}
		};
	}
}
