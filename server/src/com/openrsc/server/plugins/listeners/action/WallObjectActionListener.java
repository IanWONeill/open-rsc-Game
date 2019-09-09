package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface WallObjectActionListener {

	public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p);

}
