package com.openrsc.server.plugins.npcs.yanille;


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


public class ColonelRadick implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.COLONEL_RADICK.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.COLONEL_RADICK.id()) {
                        Functions.___npcTalk(p, n, "Who goes there?", "friend or foe?");
                        int menu = // do not send over
                        Functions.___showMenu(p, n, false, "Friend", "foe", "Why's this town so heavily defended?");
                        if (menu == 0) {
                            Functions.___playerTalk(p, n, "Friend");
                            Functions.___npcTalk(p, n, "Ok good to hear it");
                        } else
                            if (menu == 1) {
                                Functions.___playerTalk(p, n, "Foe");
                                Functions.___npcTalk(p, n, "Oh righty");
                                n.startCombat(p);
                            } else
                                if (menu == 2) {
                                    Functions.___playerTalk(p, n, "Why's this town so heavily defended?");
                                    Functions.___npcTalk(p, n, "Yanille is on the southwest border of Kandarin", "Beyond here you go into the feldip hills", "Which is major ogre teritory", "Our job is to defend Yanille from the ogres");
                                }


                    }
                    return null;
                });
            }
        };
    }
}

