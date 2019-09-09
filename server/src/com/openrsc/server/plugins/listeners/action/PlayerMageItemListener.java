package com.openrsc.server.plugins.listeners.action;


import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageItemListener {
	/**
	 * Called when you cast on an item
	 */
	public GameStateEvent onPlayerMageItem(Player p, Integer itemID, Integer spellID);
}
