package com.openrsc.server.plugins.npcs.alkharid;


import com.openrsc.server.constants.ItemId;
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


public class Warrior implements TalkToNpcListener , TalkToNpcExecutiveListener {
    private final int WARRIOR = 86;

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == WARRIOR;
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == WARRIOR) {
                        int chatRandom = DataConversions.random(0, 17);
                        Functions.___playerTalk(p, n, "Hello", "How's it going?");
                        if (chatRandom == 0) {
                            Functions.___npcTalk(p, n, "Very well, thank you");
                        } else
                            if (chatRandom == 1) {
                                Functions.___npcTalk(p, n, "How can I help you?");
                                int menu = Functions.___showMenu(p, n, "Do you wish to trade?", "I'm in search of a quest", "I'm in search of enemies to kill");
                                if (menu == 0) {
                                    Functions.___npcTalk(p, n, "No, I have nothing I wish to get rid of", "If you want to do some trading,", "there are plenty of shops and market stalls around though");
                                } else
                                    if (menu == 1) {
                                        Functions.___npcTalk(p, n, "I'm sorry I can't help you there");
                                    } else
                                        if (menu == 2) {
                                            Functions.___npcTalk(p, n, "I've heard there are many fearsome creatures under the ground");
                                        }


                            } else
                                if (chatRandom == 2) {
                                    Functions.___npcTalk(p, n, "None of your business");
                                } else
                                    if (chatRandom == 3) {
                                        Functions.___npcTalk(p, n, "No, I don't want to buy anything");
                                    } else
                                        if (chatRandom == 4) {
                                            Functions.___npcTalk(p, n, "Get out of my way", "I'm in a hurry");
                                        } else
                                            if (chatRandom == 5) {
                                                Functions.___npcTalk(p, n, "Who are you?");
                                                Functions.___playerTalk(p, n, "I am a bold adventurer");
                                                Functions.___npcTalk(p, n, "A very noble profession");
                                            } else
                                                if (chatRandom == 6) {
                                                    Functions.___npcTalk(p, n, "I'm fine", "How are you?");
                                                    Functions.___playerTalk(p, n, "Very well, thank you");
                                                } else
                                                    if (chatRandom == 7) {
                                                        Functions.___npcTalk(p, n, "Hello", "Nice weather we've been having");
                                                    } else
                                                        if (chatRandom == 8) {
                                                            Functions.___npcTalk(p, n, "Do I know you?");
                                                            Functions.___playerTalk(p, n, "No, I was just wondering if you had anything interesting to say");
                                                        } else
                                                            if (chatRandom == 9) {
                                                                Functions.___npcTalk(p, n, "Not too bad", "I'm a little worried about the increase in Goblins these days");
                                                                Functions.___playerTalk(p, n, "Don't worry. I'll kill them");
                                                            } else
                                                                if (chatRandom == 10) {
                                                                    Functions.___npcTalk(p, n, "No, I don't have any spare change");
                                                                } else
                                                                    if (chatRandom == 11) {
                                                                        Functions.___playerTalk(p, n, "I'm in search of enemies to kill");
                                                                        Functions.___npcTalk(p, n, "I've heard there are many fearsome creatures under the ground");
                                                                    } else
                                                                        if (chatRandom == 12) {
                                                                            Functions.___playerTalk(p, n, "Do you wish to trade?");
                                                                            Functions.___npcTalk(p, n, "No, I have nothing I wish to get rid of", "If you want to do some trading,", "there are plenty of shops and market stalls around though");
                                                                        } else
                                                                            if (chatRandom == 13) {
                                                                                Functions.___npcTalk(p, n, "Not too bad");
                                                                            } else
                                                                                if (chatRandom == 14) {
                                                                                    p.message("The man ignores you");
                                                                                } else
                                                                                    if (chatRandom == 15) {
                                                                                        Functions.___npcTalk(p, n, "Have this flier");
                                                                                        Functions.addItem(p, ItemId.FLIER.id(), 1);
                                                                                    } else
                                                                                        if (chatRandom == 16) {
                                                                                            Functions.___npcTalk(p, n, "Hello");
                                                                                        }
















                    }
                    return null;
                });
            }
        };
    }
}

