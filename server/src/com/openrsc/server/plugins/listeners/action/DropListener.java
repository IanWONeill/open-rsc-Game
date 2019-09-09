package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface DropListener {
	/**
	 * Called when a user drops an item
	 */
	public GameStateEvent onDrop(Player p, Item i);
}
