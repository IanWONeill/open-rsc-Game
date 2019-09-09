package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerNpcRunListener {
	/**
	 * Called when a player runs from npc
	 *
	 * @param p
	 * @param n
	 */
	public GameStateEvent onPlayerNpcRun(Player p, Npc n);
}
