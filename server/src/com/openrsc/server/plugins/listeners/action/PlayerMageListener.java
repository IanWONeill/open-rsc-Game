package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageListener {
	/**
	 * Called when you mage a Player
	 */
	public GameStateEvent onPlayerMage(Player player, Player affectedPlayer, int spell);
}
