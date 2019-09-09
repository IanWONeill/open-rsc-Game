package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnNpcListener {

	public GameStateEvent onInvUseOnNpc(Player player, Npc npc, Item item);

}
