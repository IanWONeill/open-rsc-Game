package com.openrsc.server.plugins.npcs.yanille;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class ColonelRadick implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.COLONEL_RADICK.id();
	}

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (n.getID() == NpcId.COLONEL_RADICK.id()) {
						npcTalk(p, n, "Who goes there?",
							"friend or foe?");
						int menu = showMenu(p, n, false, //do not send over
							"Friend",
							"foe",
							"Why's this town so heavily defended?");
						if (menu == 0) {
							playerTalk(p, n, "Friend");
							npcTalk(p, n, "Ok good to hear it");
						} else if (menu == 1) {
							playerTalk(p, n, "Foe");
							npcTalk(p, n, "Oh righty");
							n.startCombat(p);
						} else if (menu == 2) {
							playerTalk(p, n, "Why's this town so heavily defended?");
							npcTalk(p, n, "Yanille is on the southwest border of Kandarin",
								"Beyond here you go into the feldip hills",
								"Which is major ogre teritory",
								"Our job is to defend Yanille from the ogres");
						}
					}

					return null;
				});
			}
		};
	}
}
