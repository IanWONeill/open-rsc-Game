package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;

public interface PickupListener {
	/**
	 * Called when a user picks up an item
	 */
	public GameStateEvent onPickup(Player p, GroundItem i);
}
