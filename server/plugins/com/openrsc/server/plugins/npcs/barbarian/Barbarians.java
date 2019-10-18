package com.openrsc.server.plugins.npcs.barbarian;


import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class Barbarians implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return (n.getID() == NpcId.BARBARIAN.id()) || (n.getID() == NpcId.GUNTHOR_THE_BRAVE.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___playerTalk(p, n, "Hello");
                    int randomDiag = DataConversions.random(0, 10);
                    if (randomDiag == 0) {
                        Functions.___npcTalk(p, n, "Go away", "This is our village");
                    } else
                        if (randomDiag == 1) {
                            Functions.___npcTalk(p, n, "Hello");
                        } else
                            if (randomDiag == 2) {
                                Functions.___npcTalk(p, n, "Wanna fight?");
                                n.startCombat(p);
                            } else
                                if (randomDiag == 3) {
                                    Functions.___npcTalk(p, n, "Who are you?");
                                    Functions.___playerTalk(p, n, "I'm a bold adventurer");
                                    Functions.___npcTalk(p, n, "You don't look very strong");
                                } else
                                    if (randomDiag == 4) {
                                        p.message("The barbarian grunts");
                                    } else
                                        if (randomDiag == 5) {
                                            Functions.___npcTalk(p, n, "Good day, my dear fellow");
                                        } else
                                            if (randomDiag == 6) {
                                                Functions.___npcTalk(p, n, "ug");
                                            } else
                                                if (randomDiag == 7) {
                                                    Functions.___npcTalk(p, n, "I'm a little busy right now", "We're getting ready for our next barbarian raid");
                                                } else
                                                    if (randomDiag == 8) {
                                                        Functions.___npcTalk(p, n, "Beer?");
                                                    } else
                                                        if (randomDiag == 9) {
                                                            p.message("The barbarian ignores you");
                                                        } else
                                                            if (randomDiag == 10) {
                                                                Functions.___npcTalk(p, n, "Grr");
                                                            }










                    return null;
                });
            }
        };
    }
}

