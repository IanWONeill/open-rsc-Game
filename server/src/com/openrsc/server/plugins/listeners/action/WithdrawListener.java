package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.player.Player;

/**
 * This interface is called when a user withdraws an item from the bank
 *
 * @author Peeter
 */
public interface WithdrawListener {
	/**
	 * Called when a user withdraws an item
	 */
	public GameStateEvent onWithdraw(Player p, int itemID, int amount);
}
