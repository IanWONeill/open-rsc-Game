package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.player.Player;

public interface TeleportListener {

	/**
	 * Called when a user teleports (includes ::stuck)
	 * This does not include teleportations without bubbles (stairs, death, ladders etc)
	 */
	public GameStateEvent onTeleport(Player p);
}
