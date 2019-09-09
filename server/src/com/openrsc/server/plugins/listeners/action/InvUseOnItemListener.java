package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnItemListener {

	public GameStateEvent onInvUseOnItem(Player player, Item item1, Item item2);
}
