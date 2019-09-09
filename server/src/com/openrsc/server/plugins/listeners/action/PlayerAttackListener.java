package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerAttackListener {

	public GameStateEvent onPlayerAttack(Player p, Player affectedmob);
}
