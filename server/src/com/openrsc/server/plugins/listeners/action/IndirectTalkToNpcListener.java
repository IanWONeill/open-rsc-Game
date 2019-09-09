package com.openrsc.server.plugins.listeners.action;


import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface IndirectTalkToNpcListener {
    /**
     * Called when an action of a player triggers talk to a npc
     *
     * @param p
     * @param n
     */
    public GameStateEvent onIndirectTalkToNpc(Player p, Npc n);

}
