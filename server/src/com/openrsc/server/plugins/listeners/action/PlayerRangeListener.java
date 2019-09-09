package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerRangeListener {

	/**
	 * Called when a player ranges another player
	 */
	public GameStateEvent onPlayerRange(Player p, Player affectedMob);

}
