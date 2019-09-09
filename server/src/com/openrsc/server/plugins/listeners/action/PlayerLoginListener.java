package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.player.Player;

/**
 * Interface for handling player logins
 */
public interface PlayerLoginListener {
	/**
	 * Called when player logins
	 */
	public GameStateEvent onPlayerLogin(Player player);
}
