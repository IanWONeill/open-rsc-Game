package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Casket implements InvActionListener, InvActionExecutiveListener {

	@Override
	public boolean blockInvAction(Item item, Player p, String command) {
		return item.getID() == ItemId.CASKET.id();
	}

	@Override
	public GameStateEvent onInvAction(Item item, Player p, String command) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (item.getID() == ItemId.CASKET.id()) {
						getPlayerOwner().setBusy(true);
						getPlayerOwner().message("you open the casket");
						return nextState(3);
					}
					return null;
				});
				addState(1, () -> {
					int randomChanceOpen = DataConversions.random(0, 1081);
					getPlayerOwner().playerServerMessage(MessageType.QUEST, "you find some treasure inside!");
					removeItem(getPlayerOwner(), ItemId.CASKET.id(), 1);
					getPlayerOwner().setBusy(false);
					// Coins, 54.11% chance
					if (randomChanceOpen <= 585) {
						// Randomly gives different coin amounts
						int randomChanceCoin = DataConversions.random(0, 6);
						if (randomChanceCoin == 0) {
							addItem(getPlayerOwner(), ItemId.COINS.id(), 10);
						} else if (randomChanceCoin == 1) {
							addItem(getPlayerOwner(), ItemId.COINS.id(), 20);
						} else if (randomChanceCoin == 2) {
							addItem(getPlayerOwner(), ItemId.COINS.id(), 40);
						} else if (randomChanceCoin == 3) {
							addItem(getPlayerOwner(), ItemId.COINS.id(), 80);
						} else if (randomChanceCoin == 4) {
							addItem(getPlayerOwner(), ItemId.COINS.id(), 160);
						} else if (randomChanceCoin == 5) {
							addItem(getPlayerOwner(), ItemId.COINS.id(), 320);
						} else {
							addItem(getPlayerOwner(), ItemId.COINS.id(), 640);
						}
					} else if (randomChanceOpen <= 859) {
						// Uncut sapphire, 25.34% chance
						addItem(getPlayerOwner(), ItemId.UNCUT_SAPPHIRE.id(), 1);
					} else if (randomChanceOpen <= 990) {
						// Uncut emerald, 12.11% chance
						addItem(getPlayerOwner(), ItemId.UNCUT_EMERALD.id(), 1);
					} else if (randomChanceOpen <= 1047) {
						//Uncut ruby, 5.27% chance
						addItem(getPlayerOwner(), ItemId.UNCUT_RUBY.id(), 1);
					} else if (randomChanceOpen <= 1064) {
						// Uncut diamond, 1.57% chance
						addItem(getPlayerOwner(), ItemId.UNCUT_DIAMOND.id(), 1);
					} else {
						// Tooth halves, 1.56% chance
						// Randomly give one part or the other
						int randomChanceKey = DataConversions.random(0, 1);
						if (randomChanceKey == 0) {
							addItem(getPlayerOwner(), ItemId.TOOTH_KEY_HALF.id(), 1);
						} else {
							addItem(getPlayerOwner(), ItemId.LOOP_KEY_HALF.id(), 1);
						}
					}
					return null;
				});
			}
		};
	}
}
