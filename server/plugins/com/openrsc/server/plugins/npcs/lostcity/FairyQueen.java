package com.openrsc.server.plugins.npcs.lostcity;


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


public class FairyQueen implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.FAIRY_QUEEN.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.FAIRY_QUEEN.id()) {
                        int menu = Functions.___showMenu(p, n, "How do crops and such survive down here?", "What's so good about this place?");
                        if (menu == 0) {
                            Functions.___playerTalk(p, n, "Surely they need a bit of sunlight?");
                            Functions.___npcTalk(p, n, "Clearly you come from a plane dependant on sunlight", "Down here the plants grow in the aura of faerie");
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "Zanaris is a meeting point of cultures", "those from many worlds converge here to exchange knowledge and goods");
                            }

                    }
                    return null;
                });
            }
        };
    }
}

