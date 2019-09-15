package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class Dummy implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 49 || obj.getID() == 562;
	}

	@Override
	public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
						getPlayerOwner().message("You swing at the dummy");
						return invokeNextState(5);
					});
				addState(1, () -> {
					if (obj.getID() == 49) { // Dummy
						if (getPlayerOwner().getSkills().getLevel(Skills.ATTACK) > 7) {
							getPlayerOwner().message("There is only so much you can learn from hitting a dummy");
						} else {
							getPlayerOwner().message("You hit the dummy");
							getPlayerOwner().incExp(Skills.ATTACK, 20, true);
						}
					} else if (obj.getID() == 562) { // fight Dummy
						if (getPlayerOwner().getCache().hasKey("combat_dummy")) {
							if (getPlayerOwner().getCache().getInt("combat_dummy") < 10) {
								getPlayerOwner().getCache().set("combat_dummy", getPlayerOwner().getCache().getInt("combat_dummy") + 1);
							} else {
								player.message("There is nothing more you can learn from hitting this dummy");
								return null;
							}
						} else
							getPlayerOwner().getCache().set("combat_dummy", 1);

						// TODO: Proper message for this prop.
						getPlayerOwner().message("You hit the dummy");
						getPlayerOwner().incExp(Skills.ATTACK, 200, true);
					}
					return null;
				});
			}
		};
	}
}
