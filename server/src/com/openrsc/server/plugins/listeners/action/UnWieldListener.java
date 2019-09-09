package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface UnWieldListener {

	public GameStateEvent onUnWield(Player player, Item item);

}
