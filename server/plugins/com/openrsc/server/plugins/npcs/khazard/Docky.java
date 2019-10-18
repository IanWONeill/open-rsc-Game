package com.openrsc.server.plugins.npcs.khazard;


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


public class Docky implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.DOCKY.id()) {
                        Functions.___playerTalk(p, n, "hello there");
                        Functions.___npcTalk(p, n, "ah hoy there, wanting", "to travel on the beatiful", "lady valentine are we");
                        int menu = Functions.___showMenu(p, n, "not really, just looking around", "where are you travelling to");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "o.k land lover");
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "we sail direct to Birmhaven port", "it really is a speedy crossing", "so would you like to come", "it cost's 30 gold coin's");
                                int travel = // do not send over
                                Functions.___showMenu(p, n, false, "no thankyou", "ok");
                                if (travel == 0) {
                                    Functions.___playerTalk(p, n, "no thankyou");
                                } else
                                    if (travel == 1) {
                                        Functions.___playerTalk(p, n, "Ok");
                                        if (Functions.hasItem(p, ItemId.COINS.id(), 30)) {
                                            Functions.___message(p, 1900, "You pay 30 gold");
                                            Functions.removeItem(p, ItemId.COINS.id(), 30);
                                            Functions.___message(p, 3000, "You board the ship");
                                            p.teleport(467, 647);
                                            Functions.sleep(2000);
                                            p.message("The ship arrives at Port Birmhaven");
                                        } else {
                                            Functions.___playerTalk(p, n, "Oh dear I don't seem to have enough money");
                                        }
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
        return n.getID() == NpcId.DOCKY.id();
    }
}

