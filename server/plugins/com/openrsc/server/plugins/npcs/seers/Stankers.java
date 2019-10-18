package com.openrsc.server.plugins.npcs.seers;


import com.openrsc.server.constants.ItemId;
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


public class Stankers implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.STANKERS.id()) {
                        Functions.___npcTalk(p, n, "Hello bold adventurer");
                        int menu = Functions.___showMenu(p, n, "Are these your trucks?", "Hello Mr Stankers");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "Yes, I use them to transport coal over the river", "I will let other people use them too", "I'm a nice person like that", "Just put coal in a truck and I'll move it down to my depot over the river");
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "Would you like a poison chalice?");
                                int subMenu = // do not send over
                                Functions.___showMenu(p, n, false, "Yes please", "what's a poison chalice?", "no thankyou");
                                if (subMenu == 0) {
                                    Functions.___playerTalk(p, n, "Yes please");
                                    p.message("Stankers hands you a glass of strangely coloured liquid");
                                    Functions.addItem(p, ItemId.POISON_CHALICE.id(), 1);
                                } else
                                    if (subMenu == 1) {
                                        Functions.___playerTalk(p, n, "What's a poison chalice?");
                                        Functions.___npcTalk(p, n, "It's an exciting drink I've invented", "I don't know what it tastes like", "I haven't tried it myself");
                                    } else
                                        if (subMenu == 2) {
                                            Functions.___playerTalk(p, n, "No thankyou");
                                        }


                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.STANKERS.id();
    }
}

