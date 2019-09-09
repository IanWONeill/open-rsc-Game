package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.player.Player;

public interface CommandListener {
	public GameStateEvent onCommand(String cmd, String[] args, Player player);

	public GameStateEvent handleCommand(String cmd, String[] args, Player player);
	public boolean isCommandAllowed(Player player, String cmd);
}
