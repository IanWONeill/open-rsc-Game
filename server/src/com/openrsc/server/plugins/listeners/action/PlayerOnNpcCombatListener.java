package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerOnNpcCombatListener {
	public GameStateEvent onPlayerOnNpcCombat(Player p, Npc n);
}
