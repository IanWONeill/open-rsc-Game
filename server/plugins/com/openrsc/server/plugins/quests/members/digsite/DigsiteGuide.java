package com.openrsc.server.plugins.quests.members.digsite;


import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class DigsiteGuide implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.DIGSITE_GUIDE.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.DIGSITE_GUIDE.id()) {
                        Functions.___playerTalk(p, n, "Hello, who are you ?");
                        Functions.___npcTalk(p, n, "Hello, I am the panning guide", "I'm here to teach you how to pan for gold");
                        Functions.___playerTalk(p, n, "Excellent!");
                        Functions.___npcTalk(p, n, "Let me explain how panning works...", "First You need a panning tray", "Use the tray in the panning points in the water", "Then examine your tray", "If you find any gold, take it to the expert", "Up in the museum storage facility", "He will calculate it's value for you");
                        Functions.___playerTalk(p, n, "Okay thanks");
                    }
                    return null;
                });
            }
        };
    }
}

