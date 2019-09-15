package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class FillHole implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					getPlayerOwner().setBusy(true);
					if (removeItem(player, ItemId.SWAMP_PASTE.id(), 1)) {
						removeObject(obj);
						getPlayerOwner().message("you fill the hole with swamp paste");
					} else {
						getPlayerOwner().message("you'll need some swamp paste to fill that");
					}
					return invokeNextState(1);
				});
				addState(1, () -> {
					getPlayerOwner().setBusy(false);
					return null;
				});
			}
		};
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1077 || obj.getID() == 1071;
	}
}
