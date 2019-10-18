package com.openrsc.server.plugins.npcs.lostcity;


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


public class FairyLunderwin implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.FAIRY_LUNDERWIN.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.FAIRY_LUNDERWIN.id()) {
                        Functions.___npcTalk(p, n, "I am buying cabbage, we have no such thing where I come from", "I pay hansomly for this wounderous object", "Would 100 gold coins per cabbage be a fair price?");
                        if (Functions.hasItem(p, ItemId.CABBAGE.id()) || Functions.hasItem(p, ItemId.SPECIAL_DEFENSE_CABBAGE.id())) {
                            int menu = // do not send over
                            Functions.___showMenu(p, n, false, "Yes, I will sell you all my cabbages", "No, I will keep my cabbbages");
                            if (menu == 0) {
                                Functions.___playerTalk(p, n, "Yes, I will sell you all my cabbages");
                                while (Functions.hasItem(p, ItemId.CABBAGE.id()) || Functions.hasItem(p, ItemId.SPECIAL_DEFENSE_CABBAGE.id())) {
                                    Functions.___message(p, 60, "You sell a cabbage");
                                    if (Functions.hasItem(p, ItemId.CABBAGE.id())) {
                                        Functions.removeItem(p, ItemId.CABBAGE.id(), 1);
                                    } else
                                        if (Functions.hasItem(p, ItemId.SPECIAL_DEFENSE_CABBAGE.id())) {
                                            Functions.removeItem(p, ItemId.SPECIAL_DEFENSE_CABBAGE.id(), 1);
                                        }

                                    Functions.addItem(p, ItemId.COINS.id(), 100);
                                } 
                                Functions.___npcTalk(p, n, "Good doing buisness with you");
                            } else
                                if (menu == 1) {
                                    Functions.___playerTalk(p, n, "No, I will keep my cabbages");
                                }

                        } else {
                            Functions.___playerTalk(p, n, "Alas I have no cabbages either");
                        }
                    }
                    return null;
                });
            }
        };
    }
}

