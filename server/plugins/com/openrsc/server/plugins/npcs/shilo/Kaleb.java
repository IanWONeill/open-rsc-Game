package com.openrsc.server.plugins.npcs.shilo;


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


public class Kaleb implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.KALEB.id()) {
                        Functions.___playerTalk(p, n, "Hello.");
                        Functions.___npcTalk(p, n, "Hello Bwana,", "What can I do for you today?");
                        int menu = // do not send over
                        Functions.___showMenu(p, n, false, "Can you tell me a bit about this place?", "Buy some wine: 1 Gold.", "Buy some Beer: 2 Gold.", "Buy a nights rest: 35 Gold", "Buy a pack of 5 Dorm tickets: 175 Gold");
                        if (menu == 0) {
                            Functions.___playerTalk(p, n, "Can you tell me a bit about this place?");
                            Functions.___npcTalk(p, n, "Of course Bwana, you look like a traveler!");
                            Functions.___playerTalk(p, n, "Yes I am actually!");
                            Functions.___npcTalk(p, n, "Well, I am a traveller myself, and I have set up this hostel", "for adventurers and travellers who are weary from their journey", "There is a dormitory upstairs if you are tired, it costs 35 gold", "pieces which covers the costs of laundry and cleaning.");
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, ("Very good " + (p.isMale() ? "sir" : "madam")) + "!");
                                if (Functions.hasItem(p, ItemId.COINS.id(), 1)) {
                                    Functions.removeItem(p, ItemId.COINS.id(), 1);
                                    Functions.addItem(p, ItemId.WINE.id(), 1);
                                    p.message("You purchase a jug of wine.");
                                } else {
                                    Functions.___npcTalk(p, n, "Sorry Bwana, you don't have enough money.");
                                }
                            } else
                                if (menu == 2) {
                                    Functions.___npcTalk(p, n, ("Very good " + (p.isMale() ? "sir" : "madam")) + "!");
                                    if (Functions.hasItem(p, ItemId.COINS.id(), 2)) {
                                        Functions.removeItem(p, ItemId.COINS.id(), 2);
                                        Functions.addItem(p, ItemId.BEER.id(), 1);
                                        p.message("You purchase a frothy glass of beer.");
                                    } else {
                                        Functions.___npcTalk(p, n, "Sorry Bwana, you don't have enough money.");
                                    }
                                } else
                                    if (menu == 3) {
                                        Functions.___npcTalk(p, n, ("Very good " + (p.isMale() ? "sir" : "madam")) + "!");
                                        if (Functions.hasItem(p, ItemId.COINS.id(), 35)) {
                                            Functions.removeItem(p, ItemId.COINS.id(), 35);
                                            Functions.addItem(p, ItemId.PARAMAYA_REST_TICKET.id(), 1);
                                            p.message("You purchase a ticket to access the dormitory.");
                                        } else {
                                            Functions.___npcTalk(p, n, "Sorry Bwana, you don't have enough money.");
                                        }
                                    } else
                                        if (menu == 5) {
                                            Functions.___npcTalk(p, n, ("Very good " + (p.isMale() ? "sir" : "madam")) + "!");
                                            if (Functions.hasItem(p, ItemId.COINS.id(), 175)) {
                                                Functions.removeItem(p, ItemId.COINS.id(), 175);
                                                Functions.addItem(p, ItemId.PARAMAYA_REST_TICKET.id(), 5);
                                                p.message("You purchase 5 tickets to access the dormitory.");
                                            } else {
                                                Functions.___npcTalk(p, n, "Sorry Bwana, you don't have enough money.");
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
        return n.getID() == NpcId.KALEB.id();
    }
}

