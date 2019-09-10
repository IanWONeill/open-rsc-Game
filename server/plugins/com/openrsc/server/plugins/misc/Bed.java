package com.openrsc.server.plugins.misc;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class Bed implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public GameStateEvent onObjectAction(final GameObject object, String command, Player owner) {
		return new GameStateEvent(owner.getWorld(), owner, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if ((command.equalsIgnoreCase("rest") || command.equalsIgnoreCase("sleep")) && !owner.isSleeping() || command.equalsIgnoreCase("lie in")) {
						ActionSender.sendEnterSleep(owner);
						if (object.getID() == 1035 || object.getID() == 1162) // Crude Bed is like Sleeping Bag.
							owner.startSleepEvent(false);
						else
							owner.startSleepEvent(true);
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return command.equals("rest") || command.equals("sleep") || command.equals("lie in");
	}
}
