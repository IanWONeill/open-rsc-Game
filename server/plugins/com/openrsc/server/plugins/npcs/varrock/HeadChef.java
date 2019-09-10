package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;

public class HeadChef implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					npcTalk(p, n, "Hello welcome to the chef's guild",
						"Only accomplished chefs and cooks are allowed in here",
						"Feel free to use any of our facilities");

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.HEAD_CHEF.id();
	}

}
