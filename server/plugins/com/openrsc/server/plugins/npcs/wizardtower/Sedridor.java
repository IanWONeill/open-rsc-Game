package com.openrsc.server.plugins.npcs.wizardtower;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.quests.members.RuneMysteries;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;



public class Sedridor implements  TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					npcTalk(p,n,"Welcome, adventurer, to the world-renowned Wizards' Tower",
						"How many I help you?");

					ArrayList<String> menu = new ArrayList<>();
					menu.add("Nothing, thanks. I'm just looking around");
					if (p.getWorld().getServer().getConfig().WANT_RUNECRAFTING && p.getQuestStage(Quests.RUNE_MYSTERIES) == -1)
						menu.add("Teleport me to the rune essence");
					else if (p.getWorld().getServer().getConfig().WANT_RUNECRAFTING && p.getQuestStage(Quests.RUNE_MYSTERIES) < 2)
						menu.add("What are you doing down here?");
					else
						menu.add("Rune Mysteries");
					if (p.getWorld().getServer().getConfig().WANT_RUNECRAFTING && p.getQuestStage(Quests.RUNE_MYSTERIES) == 1)
						menu.add("I'm looking for the head wizard.");
					int choice = showMenu(p,n, menu.toArray(new String[menu.size()]));
					if (choice > 0) {
						RuneMysteries.sedridorDialog(p,n, choice);
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.SEDRIDOR.id();
	}
}
