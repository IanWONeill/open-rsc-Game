package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Trainers implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.GNOME_TRAINER_ENTRANCE.id(), NpcId.GNOME_TRAINER_STARTINGNET.id(), NpcId.GNOME_TRAINER_PLATFORM.id(), NpcId.GNOME_TRAINER_ENDINGNET.id());
	}

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (n.getID() == NpcId.GNOME_TRAINER_ENTRANCE.id()) {
						playerTalk(p, n, "hello, what is this place?");
						npcTalk(p, n, "this my friend, is where we train",
							"it improves our agility, an essential skill");
						playerTalk(p, n, "looks easy enough");
						npcTalk(p, n, "if you complete the course...",
							"from the slippery log to the end",
							"your agilty will increase much faster..",
							".. than repeating one obstical");
					} else if (n.getID() == NpcId.GNOME_TRAINER_STARTINGNET.id()) {
						playerTalk(p, n, "hello");
						npcTalk(p, n, "this isn't a granny's tea party",
							"let's see some sweat human",
							"go, go ,go ,go");
					} else if (n.getID() == NpcId.GNOME_TRAINER_PLATFORM.id()) {
						playerTalk(p, n, "this is fun");
						npcTalk(p, n, "this is training soldier",
							"if you want fun, go make some cocktails");
					} else if (n.getID() == NpcId.GNOME_TRAINER_ENDINGNET.id()) {
						playerTalk(p, n, "hello");
						npcTalk(p, n, "hi");
						playerTalk(p, n, "how are you?");
						npcTalk(p, n, "im amazed by how much you humans chat",
							"the sign say's training area...",
							"..not pointless conversation area",
							"now move it soldier");
					}

					return null;
				});
			}
		};
	}
}
