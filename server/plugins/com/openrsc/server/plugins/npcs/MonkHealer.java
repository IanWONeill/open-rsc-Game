package com.openrsc.server.plugins.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class MonkHealer implements TalkToNpcListener, TalkToNpcExecutiveListener {
	@Override
	public GameStateEvent onTalkToNpc(Player p, final Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					npcTalk(p, n, "Greetings traveller");
					int option = showMenu(p, n, false, //do not send over
						"Can you heal me? I'm injured", "Isn't this place built a bit out the way?");
					if (option == 0) {
						playerTalk(p, n, "Can you heal me?", "I'm injured");
						npcTalk(p, n, "Ok");
						message(p, "The monk places his hands on your head", "You feel a little better");
						int newHp = getCurrentLevel(p, Skills.HITS) + 5;
						if (newHp > getMaxLevel(p, Skills.HITS)) {
							newHp = getMaxLevel(p, Skills.HITS);
						}
						p.getSkills().setLevel(Skills.HITS, newHp);
					} else if (option == 1) {
						playerTalk(p, n, "Isn't this place built a bit out the way?");
						npcTalk(p, n, "We like it that way",
							"We get disturbed less",
							"We still get rather a large amount of travellers",
							"looking for sanctuary and healing here as it is");
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.MONK.id() || n.getID() == NpcId.ABBOT_LANGLEY.id();
	}
}
