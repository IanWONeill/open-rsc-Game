package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

/**
 * Interface for handling Inv Actions
 *
 * @author Peeter.tomberg
 */
public interface InvActionListener {

	/**
	 * Called when a user performs an inventory action
	 *
	 * @param item
	 * @param player
	 */
	public GameStateEvent onInvAction(Item item, Player player, String command);
}
