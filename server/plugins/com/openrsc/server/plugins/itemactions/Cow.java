package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Cow implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return item.getID() == ItemId.BUCKET.id() && (npc.getID() == 217 || npc.getID() == 6);
	}

	@Override
	public GameStateEvent onInvUseOnNpc(Player player, Npc npc, Item item) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					npc.resetPath();
					npc.face(player);
					npc.setBusy(true);
					showBubble(getPlayerOwner(), item);
					if (removeItem(getPlayerOwner(), item.getID(), 1)) {
						addItem(getPlayerOwner(), ItemId.MILK.id(), 1);
					}
					getPlayerOwner().message("You milk the cow");
					return nextState(5);
				});
				addState(1, () -> {
					npc.setBusy(false);
					return null;
				});
			}
		};
	}
}
