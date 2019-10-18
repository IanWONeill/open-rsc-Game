package com.openrsc.server.plugins.npcs.ardougne.east;


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
import java.time.Instant;
import java.util.concurrent.Callable;


public class SilkMerchant implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getCache().hasKey("silkStolen") && (Instant.now().getEpochSecond() < (p.getCache().getLong("silkStolen") + 1200))) {
                        Functions.___npcTalk(p, n, "Do you really think I'm going to buy something", "That you have just stolen from me", "guards guards");
                        Npc attacker = Functions.getNearestNpc(p, NpcId.GUARD_ARDOUGNE.id(), 5);// Guard

                        if (attacker != null)
                            attacker.setChasing(p);

                    } else
                        if (Functions.hasItem(p, ItemId.SILK.id())) {
                            Functions.___playerTalk(p, n, "Hello I have some fine silk from Al Kharid to sell to you");
                            Functions.___npcTalk(p, n, "Ah I may be intersted in that", "What sort of price were you looking at per piece of silk?");
                            int menu = Functions.___showMenu(p, n, "20 coins", "80 coins", "120 coins", "200 coins");
                            if (menu == 0) {
                                Functions.___npcTalk(p, n, "Ok that suits me");
                                Functions.removeItem(p, ItemId.SILK.id(), 1);
                                Functions.addItem(p, ItemId.COINS.id(), 20);
                            } else
                                if (menu == 1) {
                                    Functions.___npcTalk(p, n, "80 coins that's a bit steep", "How about 40 coins");
                                    int reply2 = Functions.___showMenu(p, n, "Ok 40 sounds good", "50 and that's my final price", "No that is not enough");
                                    if (reply2 == 0) {
                                        Functions.removeItem(p, ItemId.SILK.id(), 1);
                                        Functions.addItem(p, ItemId.COINS.id(), 40);
                                    } else
                                        if (reply2 == 1) {
                                            Functions.___npcTalk(p, n, "Done");
                                            Functions.removeItem(p, ItemId.SILK.id(), 1);
                                            Functions.addItem(p, ItemId.COINS.id(), 50);
                                        }

                                } else
                                    if (menu == 2) {
                                        Functions.___npcTalk(p, n, "You'll never get that much for it", "I'll be generous and give you 50 for it");
                                        int reply = Functions.___showMenu(p, n, false, "Ok I guess 50 will do", "I'll give it to you for 60", "No that is not enough");
                                        if (reply == 0) {
                                            Functions.___playerTalk(p, n, "Ok I guess 50 will do");
                                            Functions.removeItem(p, ItemId.SILK.id(), 1);
                                            Functions.addItem(p, ItemId.COINS.id(), 50);
                                        } else
                                            if (reply == 1) {
                                                Functions.___playerTalk(p, n, "I'll give it you for 60");
                                                Functions.___npcTalk(p, n, "You drive a hard bargain", "but I guess that will have to do");
                                                Functions.removeItem(p, ItemId.SILK.id(), 1);
                                                Functions.addItem(p, ItemId.COINS.id(), 60);
                                            } else
                                                if (reply == 2) {
                                                    Functions.___playerTalk(p, n, "No that is not enough");
                                                }


                                    } else
                                        if (menu == 3) {
                                            Functions.___npcTalk(p, n, "Don't be ridiculous that is far to much", "You insult me with that price");
                                        }



                        } else {
                            Functions.___npcTalk(p, n, "I buy silk", "If you get any silk to sell bring it here");
                        }

                    return null;
                });
            }
        };
    }

    // WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
    // Delay player busy (3000); after stealing and Npc shout out to you.
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.SILK_MERCHANT.id();
    }
}

