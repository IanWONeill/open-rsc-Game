package com.openrsc.server.plugins.npcs.varrock;


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


public class DancingDonkeyInnBartender implements TalkToNpcListener , TalkToNpcExecutiveListener {
    public static int BARTENDER = NpcId.BARTENDER_EAST_VARROCK.id();

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == DancingDonkeyInnBartender.BARTENDER;
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == DancingDonkeyInnBartender.BARTENDER) {
                        Functions.___playerTalk(p, n, "hello");
                        Functions.___npcTalk(p, n, "good day to you, brave adventurer", "can i get you a refreshing beer");
                        int menu = Functions.___showMenu(p, n, "yes please", "no thanks", "how much?");
                        if (menu == 0) {
                            buyBeer(p, n);
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "let me know if you change your mind");
                            } else
                                if (menu == 2) {
                                    Functions.___npcTalk(p, n, "two gold pieces a pint", "so, what do you say?");
                                    int subMenu = Functions.___showMenu(p, n, "yes please", "no thanks");
                                    if (subMenu == 0) {
                                        buyBeer(p, n);
                                    } else
                                        if (subMenu == 1) {
                                            Functions.___npcTalk(p, n, "let me know if you change your mind");
                                        }

                                }


                    }
                    return null;
                });
            }
        };
    }

    private void buyBeer(Player p, Npc n) {
        Functions.___npcTalk(p, n, "ok then, that's two gold coins please");
        if (Functions.hasItem(p, ItemId.COINS.id(), 2)) {
            p.message("you give two coins to the barman");
            Functions.removeItem(p, ItemId.COINS.id(), 2);
            p.message("he gives you a cold beer");
            Functions.addItem(p, ItemId.BEER.id(), 1);
            Functions.___npcTalk(p, n, "cheers");
            Functions.___playerTalk(p, n, "cheers");
        } else {
            p.message("you don't have enough gold");
        }
    }
}

