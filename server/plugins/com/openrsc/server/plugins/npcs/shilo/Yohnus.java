package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Yohnus implements TalkToNpcExecutiveListener, TalkToNpcListener, WallObjectActionListener, WallObjectActionExecutiveListener {

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (n.getID() == NpcId.YOHNUS.id()) {
						playerTalk(p, n, "Hello");
						npcTalk(p, n, "Hello Bwana, can I help you in anyway?");
						yohnusChat(p, n);
					}

					return null;
				});
			}
		};
	}

	private void yohnusChat(Player p, Npc n) {
		int menu = showMenu(p, n, false, //do not send over
			"Use Furnace - 20 Gold",
			"No thanks!");
		if (menu == 0) {
			if (hasItem(p, ItemId.COINS.id(), 20)) {
				removeItem(p, ItemId.COINS.id(), 20);
				npcTalk(p, n, "Thanks Bwana!",
					"Enjoy the facilities!");
				p.teleport(400, 844);
				p.message("You're shown into the Blacksmiths where you can see a furnace");
			} else {
				npcTalk(p, n, "Sorry Bwana, it seems that you are short of funds.");
			}
		} else if (menu == 1) {
			playerTalk(p, n, "No thanks!");
			npcTalk(p, n, "Very well Bwana, have a nice day.");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.YOHNUS.id();
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return obj.getID() == 165;
	}

	@Override
	public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (obj.getID() == 165) {
						if (p.getY() <= 844) {
							p.teleport(400, 845);
							return null;
						}
						Npc yohnus = getNearestNpc(p, NpcId.YOHNUS.id(), 5);
						if (yohnus != null) {
							npcTalk(p, yohnus, "Sorry but the blacksmiths is closed.",
								"But I can let you use the furnace at the cost",
								"of 20 gold pieces.");
							yohnusChat(p, yohnus);
						}
					}

					return null;
				});
			}
		};
	}
}
