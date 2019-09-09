package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerAttackNpcListener {

	public GameStateEvent onPlayerAttackNpc(Player p, Npc affectedmob);

}
