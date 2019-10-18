package com.openrsc.server.plugins.npcs.alkharid;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class SilkTrader implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.SILK_TRADER.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    final String[] options;
                    Functions.___npcTalk(p, n, "Do you want to buy any fine silks?");
                    if ((p.getQuestStage(Quests.FAMILY_CREST) <= 2) || (p.getQuestStage(Quests.FAMILY_CREST) >= 5)) {
                        options = new String[]{ "How much are they?", "No. Silk doesn't suit me" };
                    } else {
                        options = new String[]{ "How much are they?", "No. Silk doesn't suit me", "I'm in search of a man named adam fitzharmon" };
                    }
                    int option1 = Functions.___showMenu(p, n, options);
                    if (option1 == 0) {
                        Functions.___npcTalk(p, n, "3 Coins");
                        int option2 = Functions.___showMenu(p, n, "No. That's too much for me", "OK, that sounds good");
                        if (option2 == 0) {
                            Functions.___npcTalk(p, n, "Two coins and that's as low as I'll go", "I'm not selling it for any less", "You'll probably go and sell it in Varrock for a profit anyway");
                            int option3 = Functions.___showMenu(p, n, "Two coins sounds good", "No, really. I don't want it");
                            if (option3 == 0) {
                                p.message("You buy some silk for 2 coins");
                                if (p.getInventory().remove(ItemId.COINS.id(), 2) > (-1)) {
                                    Functions.addItem(p, ItemId.SILK.id(), 1);
                                } else {
                                    Functions.___playerTalk(p, n, "Oh dear. I don't have enough money");
                                }
                            } else
                                if (option3 == 1) {
                                    Functions.___npcTalk(p, n, "OK, but that's the best price you're going to get");
                                }

                        } else
                            if (option2 == 1) {
                                if (p.getInventory().remove(ItemId.COINS.id(), 3) > (-1)) {
                                    Functions.addItem(p, ItemId.SILK.id(), 1);
                                    p.message("You buy some silk for 3 coins");
                                } else {
                                    Functions.___playerTalk(p, n, "Oh dear. I don't have enough money");
                                }
                            }

                    } else
                        if (option1 == 2) {
                            Functions.___npcTalk(p, n, "I haven't seen him", "I'm sure if he's been to Al Kharid recently", "Someone around here will have seen him though");
                        }

                    return null;
                });
            }
        };
    }
}

