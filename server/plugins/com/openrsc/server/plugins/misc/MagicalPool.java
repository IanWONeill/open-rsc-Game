package com.openrsc.server.plugins.misc;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class MagicalPool implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1166 || obj.getID() == 1155;
	}

	@Override
	public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (obj.getID() == 1155) {
			/*
			if (!player.canUsePool()) {
				player.message("You have just died, you must wait for "
										+ player.secondsUntillPool()
										+ " seconds before using this pool again");
				return;
			}
			while (System.currentTimeMillis()
					- player.getLastMoved() < 10000
					&& player.getLocation().inWilderness()) {
				player.message("You must stand still for 10 seconds before using portal");
				return;
			}
			while (System.currentTimeMillis()
					- player.getCombatTimer() < 10000
					&& player.getLocation().inWilderness()) {
				player.message("You must be out of combat for 10 seconds before using portal");
				return;
			}
			int option = showMenu(player, "Edgeville", "Varrock",
					"Castle (dangerous)", "Graveyard (dangerous)", "Hobgoblins (dangerous)", "Altar (dangerous)",
					"Dragon Maze (dangerous)", "Mage Arena (dangerous)", "Rune rocks (dangerous)", "Red dragons (dangerous)", "Further underground mage arena");

			if (option == 0) {
				player.teleport(215, 436);
			} else if (option == 1) {
				player.teleport(111, 505);
			} else if (option == 2) {
				player.teleport(272, 354);
			} else if (option == 3) {
				player.teleport(187, 297);
			} else if (option == 4) {
				player.teleport(218, 271);
			} else if (option == 5) {
				player.teleport(316, 199);
			} else if (option == 6) {
				player.teleport(271, 195);
			} else if (option == 7) {
				player.teleport(224, 110);
			} else if (option == 8) {
				player.teleport(264, 148);
			} else if(option == 9) {
				player.teleport(143, 173);
			} else if(option == 10) {
			*/
						if (getPlayerOwner().getCache().hasKey("mage_arena") && getPlayerOwner().getCache().getInt("mage_arena") >= 2) {
							movePlayer(getPlayerOwner(), 471, 3385);
							getPlayerOwner().message("you are teleported further under ground");
						} else {
							getPlayerOwner().message("you step into the pool");
							return invoke(3, 2);
						}
					}
					if (obj.getID() == 1166) {
						getPlayerOwner().message("you step into the sparkling water");
						return nextState(2);
					}
					return null;
				});
				addState(1, () -> {
					getPlayerOwner().message("you feel energy rush through your veins");
					return nextState(2);

				});
				addState(2, () -> {
					movePlayer(getPlayerOwner(), 447, 3373);
					getPlayerOwner().message("you are teleported to kolodions cave");
					return null;
				});
				addState(3, () -> {
					getPlayerOwner().message("you wet your boots");
					return null;
				});
			}
		};
	}
}
