package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class InspectNet implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1102 || obj.getID() == 1101;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		final FishingTrawler trawler = player.getWorld().getFishingTrawler(player);
		player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Fishing Trawler Inspect Net") {
			public void init() {
				addState(0, () -> {
					getPlayerOwner().message("you inspect the net");
					return nextState(3);
				});
				addState(1, () -> {
					if (trawler != null && trawler.isNetBroken()) {
						getPlayerOwner().message("it's begining to rip");
						if (!hasItem(getPlayerOwner(), ItemId.ROPE.id())) {
							getPlayerOwner().message("you'll need some rope to fix it");
							return null;
						}
						getPlayerOwner().message("you attempt to fix it with your rope");
						return nextState(3);
					}
					getPlayerOwner().message("it is not damaged");
					return null;
				});
				addState(2, () -> {
					if (DataConversions.random(0, 1) == 0) {
						getPlayerOwner().message("you manage to fix the net");
						removeItem(getPlayerOwner(), ItemId.ROPE.id(), 1);
						trawler.setNetBroken(false);
					} else {
						getPlayerOwner().message("but you fail in the harsh conditions");
					}
					return null;
				});
			}
		});
	}

}
