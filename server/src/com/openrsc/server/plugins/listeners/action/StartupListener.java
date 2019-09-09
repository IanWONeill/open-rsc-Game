package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;

public interface StartupListener {
	/**
	 * Called when the server starts up
	 */
	public GameStateEvent onStartup();
}
