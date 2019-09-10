package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class PortSarimSailor implements ObjectActionExecutiveListener, ObjectActionListener, TalkToNpcExecutiveListener,
	TalkToNpcListener {

	@Override
	public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					npcTalk(p, n, "Do you want to go on a trip to Karamja?",
						"The trip will cost you 30 gold");
					String[] menu = new String[]{
						"I'd rather go to Crandor Isle",
						"Yes please", "No thankyou"
					};
					if (p.getQuestStage(Quests.DRAGON_SLAYER) == -1 || p.getCache().hasKey("ned_hired")) {
						menu = new String[]{ // Crandor option is not needed.
							"Yes please", "No thankyou"
						};
						int choice = showMenu(p, n, menu);
						if (choice >= 0) {
							travel(p, n, choice + 1);
						}
					} else {
						int choice = showMenu(p, n, menu);
						travel(p, n, choice);
					}

					return null;
				});
			}
		};
	}

	public void travel(final Player p, final Npc n, int option) {
		if (option == 0) {
			npcTalk(p, n, "No I need to stay alive",
				"I have a wife and family to support");
		} else if (option == 1) {
			if (p.getInventory().remove(ItemId.COINS.id(), 30) > -1) {
				message(p, "You pay 30 gold", "You board the ship");
				p.teleport(324, 713, false);
				sleep(1000);
				message(p, "The ship arrives at Karamja");
			} else {
				playerTalk(p, n, "Oh dear I don't seem to have enough money");
			}
		}
	}


	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.CAPTAIN_TOBIAS.id() || n.getID() == NpcId.SEAMAN_THRESNOR.id() || n.getID() == NpcId.SEAMAN_LORRIS.id();
	}

	@Override
	public GameStateEvent onObjectAction(GameObject arg0, String arg1, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {		Npc sailor = getNearestNpc(p, NpcId.CAPTAIN_TOBIAS.id(), 5);
					if (sailor != null) {
						sailor.initializeTalkScript(p);
					} else {
						p.message("I need to speak to the captain before boarding the ship.");
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockObjectAction(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 155 && arg0.getLocation().equals(Point.location(265, 645)))
			|| (arg0.getID() == 156 && arg0.getLocation().equals(Point.location(265, 650)))
			|| (arg0.getID() == 157 && arg0.getLocation().equals(Point.location(265, 652)));
	}
}

