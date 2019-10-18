package com.openrsc.server.plugins.npcs.lumbridge;


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


public class Hans implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.HANS.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Hello what are you doing here?");
                    int option = Functions.___showMenu(p, n, "I'm looking for whoever is in charge of this place", "I have come to kill everyone in this castle", "I don't know. I'm lost. Where am I?");
                    if (option == 0)
                        Functions.___npcTalk(p, n, "Sorry, I don't know where he is right now");
                    else
                        if (option == 1)
                            Functions.___npcTalk(p, n, "HELP HELP!");
                        else
                            if (option == 2)
                                Functions.___npcTalk(p, n, "You are in Lumbridge Castle");



                    return null;
                });
            }
        };
    }
}

