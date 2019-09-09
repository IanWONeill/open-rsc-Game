package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class boatman implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island boat man - last npc before main land (Lumbridge)
	 */
	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					npcTalk(p, n, "Hello my job is to take you to the main game area",
						"It's only a short row",
						"I shall take you to the small town of Lumbridge",
						"In the kingdom of Misthalin");
					int menu = showMenu(p, n, "Ok I'm ready to go", "I'm not done here yet");
					if (menu == 0) {
						npcTalk(p, n, "Lets go then");
						p.message("You have completed the tutorial");
						p.teleport(120, 648, false);
						if (p.getCache().hasKey("tutorial")) {
							p.getCache().remove("tutorial");
						}
						sleep(2000);
						p.message("The boat arrives in Lumbridge");
						p.getWorld().sendWorldAnnouncement("New adventurer @gre@" + p.getUsername() + "@whi@ has arrived in lumbridge!");
						ActionSender.sendPlayerOnTutorial(p);
					} else if (menu == 1) {
						npcTalk(p, n, "Ok come back when you are ready");
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BOATMAN.id();
	}

}
