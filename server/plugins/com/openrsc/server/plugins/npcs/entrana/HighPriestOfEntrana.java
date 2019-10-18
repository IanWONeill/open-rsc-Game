package com.openrsc.server.plugins.npcs.entrana;


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


public class HighPriestOfEntrana implements TalkToNpcListener , TalkToNpcExecutiveListener {
    private static void entranaPriestDialogue(Player p, Npc n, int cID) {
        if (cID == (-1)) {
            Functions.___npcTalk(p, n, "Many greetings welcome to our fair island");
            if (p.getQuestStage(Quests.THE_HOLY_GRAIL) >= 2) {
                Functions.___playerTalk(p, n, "Hello, I am in search of the holy grail");
                Functions.___npcTalk(p, n, "The object of which you speak did once pass through holy entrana", "I know not where it is now", "Nor do I really care");
                n = Functions.getNearestNpc(p, NpcId.CRONE.id(), 20);
                Functions.___npcTalk(p, n, "Wait!", "Did you say the grail?", "You are a grail knight yes?", "Well you'd better hurry, a fisher king is in pain");
                Functions.___playerTalk(p, n, "Well I would but I don't know where I am going");
                Functions.___npcTalk(p, n, "Go to where the six heads face", "blow the whistle and away you go");
                if (p.getQuestStage(Quests.THE_HOLY_GRAIL) == 2) {
                    p.updateQuestStage(Quests.THE_HOLY_GRAIL, 3);
                }
                int menu = Functions.___showMenu(p, n, "What are the six heads?", "What's a fisher king?", "Ok I will go searching", "What do you mean by the whistle?");
                if (menu == 0) {
                    HighPriestOfEntrana.entranaPriestDialogue(p, n, HighPriestOfEntrana.EntranaPriest.sixHeads);
                } else
                    if (menu == 1) {
                        HighPriestOfEntrana.entranaPriestDialogue(p, n, HighPriestOfEntrana.EntranaPriest.fisherKing);
                    } else
                        if (menu == 3) {
                            HighPriestOfEntrana.entranaPriestDialogue(p, n, HighPriestOfEntrana.EntranaPriest.whistle);
                        }


                return;
            } else {
                Functions.___npcTalk(p, n, "enjoy your stay hear", "May it be spiritually uplifting");
            }
        }
        switch (cID) {
            case HighPriestOfEntrana.EntranaPriest.fisherKing :
                Functions.___npcTalk(p, n, "The fisher king is the owner and slave of the grail");
                Functions.___playerTalk(p, n, "What are the four heads?");
                // authentic from replay ended dialogue
                break;
            case HighPriestOfEntrana.EntranaPriest.sixHeads :
                Functions.___npcTalk(p, n, "The six  stone heads have appeared just recently in the world", "They all face the point of realm crossing", "Find where two of the heads face", "And you should be able to pinpoint where it is");
                int m = // do not send over
                Functions.___showMenu(p, n, false, "What's a fisher king?", "Ok I will go searching", "What do you mean by the whistle?", "the point of realm crossing?");
                if (m == 0) {
                    Functions.___playerTalk(p, n, "What's a fisher king?");
                    HighPriestOfEntrana.entranaPriestDialogue(p, n, HighPriestOfEntrana.EntranaPriest.fisherKing);
                } else
                    if (m == 1) {
                        Functions.___playerTalk(p, n, "Ok I will go searching");
                    } else
                        if (m == 2) {
                            Functions.___playerTalk(p, n, "What do you mean by the whistle?");
                            HighPriestOfEntrana.entranaPriestDialogue(p, n, HighPriestOfEntrana.EntranaPriest.whistle);
                        } else
                            if (m == 3) {
                                Functions.___playerTalk(p, n, "The point of realm crossing");
                                Functions.___npcTalk(p, n, "The realm of the fisher king is not quite of this reality", "It is of a reality very close to ours though", "Where it's easiest to cross that is a point of realm crossing");
                            }



                break;
            case HighPriestOfEntrana.EntranaPriest.whistle :
                Functions.___npcTalk(p, n, "You don't know about the whistles yet?", "the whistles are easy", "You will need one to get to and from the fisher king's realm", "they reside in a haunted manor house in Misthalin", "though you may not perceive them unless you carry something", "From the realm of the fisher king");
                int m1 = // do not send over
                Functions.___showMenu(p, n, false, "What are the four heads?", "What's a fisher king?", "Ok I will go searching");
                if (m1 == 0) {
                    Functions.___playerTalk(p, n, "What are the six heads?");
                    HighPriestOfEntrana.entranaPriestDialogue(p, n, HighPriestOfEntrana.EntranaPriest.sixHeads);
                } else
                    if (m1 == 1) {
                        Functions.___playerTalk(p, n, "What's a fisher king?");
                        HighPriestOfEntrana.entranaPriestDialogue(p, n, HighPriestOfEntrana.EntranaPriest.fisherKing);
                    } else
                        if (m1 == 2) {
                            Functions.___playerTalk(p, n, "Ok I will go searching");
                        }


                break;
        }
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.HIGH_PRIEST_OF_ENTRANA.id()) {
                        HighPriestOfEntrana.entranaPriestDialogue(p, n, -1);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.HIGH_PRIEST_OF_ENTRANA.id();
    }

    class EntranaPriest {
        public static final int fisherKing = 0;

        public static final int sixHeads = 1;

        public static final int whistle = 2;
    }
}

