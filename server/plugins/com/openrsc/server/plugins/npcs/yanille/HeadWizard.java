package com.openrsc.server.plugins.npcs.yanille;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.npcTalk;

public class HeadWizard implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (p.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.HEAD_WIZARD.id()) {
						if (getCurrentLevel(p, Skills.MAGIC) < 66) {
							npcTalk(p, n, "Hello, you need a magic level of 66 to get in here",
								"The magical energy in here is unsafe for those below that level");
						} else {
							npcTalk(p, n, "Hello welcome to the wizard's guild",
								"Only accomplished wizards are allowed in here",
								"Feel free to use any of our facilities");
						}
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return p.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.HEAD_WIZARD.id();
	}

}
