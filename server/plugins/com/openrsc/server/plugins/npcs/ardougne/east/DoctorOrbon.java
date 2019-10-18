package com.openrsc.server.plugins.npcs.ardougne.east;


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


public class DoctorOrbon implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.DOCTOR_ORBON.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.DOCTOR_ORBON.id()) {
                        if (p.getQuestStage(Quests.SHEEP_HERDER) == (-1)) {
                            Functions.___npcTalk(p, n, "well hello again", "i was so relieved when i heard you disposed of the plagued sheep", "Now the town is safe");
                            return null;
                        }
                        if (p.getQuestStage(Quests.SHEEP_HERDER) == 2) {
                            Functions.___playerTalk(p, n, "hello again");
                            Functions.___npcTalk(p, n, "have you managed to get rid of those sheep?");
                            Functions.___playerTalk(p, n, "not yet");
                            Functions.___npcTalk(p, n, "you must hurry", "they could have the whole town infected in days");
                            if ((!Functions.hasItem(p, ItemId.PROTECTIVE_TROUSERS.id())) || (!Functions.hasItem(p, ItemId.PROTECTIVE_JACKET.id()))) {
                                Functions.___npcTalk(p, n, "I see you don't have your protective clothing with you", "Would you like to buy some more?", "Same price as before");
                                int moreMenu = Functions.___showMenu(p, n, "No i don't need any more", "Ok i'll take it");
                                if (moreMenu == 0) {
                                    // NOTHING
                                } else
                                    if (moreMenu == 1) {
                                        if (Functions.removeItem(p, ItemId.COINS.id(), 100)) {
                                            Functions.___message(p, "you give doctor orbon 100 coins", "doctor orbon gives you a protective suit");
                                            Functions.addItem(p, ItemId.PROTECTIVE_TROUSERS.id(), 1);
                                            Functions.addItem(p, ItemId.PROTECTIVE_JACKET.id(), 1);
                                            Functions.___npcTalk(p, n, "these will keep you safe from the plague");
                                        } else {
                                            Functions.___playerTalk(p, n, "oops, I don't have enough money");
                                            Functions.___npcTalk(p, n, "that's ok, but don't go near those sheep", "if you can find the money i'll be waiting here");
                                        }
                                    }

                            }
                            return null;
                        }
                        if (p.getQuestStage(Quests.SHEEP_HERDER) == 1) {
                            Functions.___playerTalk(p, n, "hi doctor", "I need to aquire some protective clothing", "so i can recapture some escaped sheep who have the plague");
                            Functions.___npcTalk(p, n, "I'm afraid i only have one suit", "Which i made to keep myself safe from infected patients", "I could sell it to you", "then i could make myself another", "hmmm..i'll need at least 100 gold coins");
                            int menu = Functions.___showMenu(p, n, "Sorry doc, that's too much", "Ok i'll take it");
                            if (menu == 0) {
                                // NOTHING
                            } else
                                if (menu == 1) {
                                    if (Functions.removeItem(p, ItemId.COINS.id(), 100)) {
                                        Functions.___message(p, "you give doctor orbon 100 coins", "doctor orbon gives you a protective suit");
                                        Functions.addItem(p, ItemId.PROTECTIVE_TROUSERS.id(), 1);
                                        Functions.addItem(p, ItemId.PROTECTIVE_JACKET.id(), 1);
                                        Functions.___npcTalk(p, n, "these will keep you safe from the plague");
                                        p.updateQuestStage(Quests.SHEEP_HERDER, 2);
                                    } else {
                                        Functions.___playerTalk(p, n, "oops, I don't have enough money");
                                        Functions.___npcTalk(p, n, "that's ok, but don't go near those sheep", "if you can find the money i'll be waiting here");
                                    }
                                }

                            return null;
                        }
                        Functions.___playerTalk(p, n, "hello");
                        Functions.___npcTalk(p, n, "how do you feel?", "no heavy flu or the shivers?");
                        Functions.___playerTalk(p, n, "no, i'm fine");
                        Functions.___npcTalk(p, n, "how about nightmares?", "have you had any problems with really scary nightmares?");
                        Functions.___playerTalk(p, n, "no, not since i was young");
                        Functions.___npcTalk(p, n, "good good", "have to be carefull nowadays", "the plague spreads faster than a common cold");
                        int m = Functions.___showMenu(p, n, "The plague? tell me more", "Ok i'll be careful");
                        if (m == 0) {
                            Functions.___npcTalk(p, n, "the virus came from the west and is deadly");
                            Functions.___playerTalk(p, n, "what are the symtoms?");
                            Functions.___npcTalk(p, n, "watch out for abnormal nightmares and strong flu symtoms", "when you find a thick black liquid dripping from your nose and eyes", "then no one can save you");
                        } else
                            if (m == 1) {
                                Functions.___npcTalk(p, n, "you do that traveller");
                            }

                    }
                    return null;
                });
            }
        };
    }
}

