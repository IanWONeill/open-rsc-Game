package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageObjectListener {
	public GameStateEvent onPlayerMageObject(Player player, GameObject obj, SpellDef spell);
}
