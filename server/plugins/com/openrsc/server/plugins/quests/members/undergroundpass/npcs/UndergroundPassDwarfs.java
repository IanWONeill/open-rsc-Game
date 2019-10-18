package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;


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


public class UndergroundPassDwarfs implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return ((n.getID() == NpcId.KAMEN.id()) || (n.getID() == NpcId.NILOOF.id())) || (n.getID() == NpcId.KLANK.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.KAMEN.id()) {
                        switch (p.getQuestStage(Quests.UNDERGROUND_PASS)) {
                            case 5 :
                            case 6 :
                            case 7 :
                            case 8 :
                            case -1 :
                                Functions.___message(p, "the dwarf is leaning on a barrel of home made brew");
                                p.message("he looks a little drunk");
                                Functions.___playerTalk(p, n, "hi there, you ok?");
                                Functions.___npcTalk(p, n, "ooooh, my head ...im gone");
                                Functions.___playerTalk(p, n, "what's wrong?");
                                Functions.___npcTalk(p, n, "to much of this home brew my friend", "we make from plant roots", "but it blows your head off", "you don't wanna put it near any naked flames", "want some?");
                                int menu = Functions.___showMenu(p, n, "ok then", "no thanks");
                                if (menu == 0) {
                                    Functions.___npcTalk(p, n, "here you go");
                                    p.message("you take a sip of brew from kamens glass");
                                    p.damage(5);
                                    Functions.___playerTalk(p, n, "aaarrgghh");
                                    p.message("it tastse horrific and burns your throat");
                                    Functions.___npcTalk(p, n, "ha ha", "i warned you that it's strong stuff");
                                } else
                                    if (menu == 1) {
                                        Functions.___npcTalk(p, n, "your losh");
                                    }

                                break;
                        }
                    } else
                        if (n.getID() == NpcId.NILOOF.id()) {
                            switch (p.getQuestStage(Quests.UNDERGROUND_PASS)) {
                                case 5 :
                                    Functions.___npcTalk(p, n, "back away..back away..wait..", "..you're human!");
                                    Functions.___playerTalk(p, n, "that's right, i'm on a quest for king lathas", "we need to find a way through these caverns");
                                    Functions.___npcTalk(p, n, "ha ha, listen up, we came here as miners decades ago", "completely unaware of the evil that lurked in the caverns", "there's no way through, not while iban still rules", "he controls the gateway,the only way to the other side");
                                    Functions.___playerTalk(p, n, "what gateway?");
                                    Functions.___npcTalk(p, n, "it once stood as the the 'well of voyage'", "a gateway to west runescape", "now ibans moulded it into a pit of the damned", "a portal to zamoraks darkest realms", "he sends his followers there, never to return", "only once iban is destroyed can the well be restored");
                                    Functions.___playerTalk(p, n, "but how?");
                                    Functions.___npcTalk(p, n, "if i knew, i would have slain him already", "seek out the witch, his guide , his only confidante", "only she knows how to rid us of iban", "she lives on the platforms above, we dare not go there", "here, take some food to aid your journey");
                                    p.message("Niloof give you some food");
                                    Functions.addItem(p, ItemId.MEAT_PIE.id(), 2);
                                    Functions.addItem(p, ItemId.CHOCOLATE_BOMB.id(), 1);
                                    Functions.addItem(p, ItemId.MEAT_PIZZA.id(), 1);
                                    Functions.___playerTalk(p, n, "thanks niloof, take care");
                                    Functions.___npcTalk(p, n, "you too");
                                    p.updateQuestStage(Quests.UNDERGROUND_PASS, 6);
                                    break;
                                case 6 :
                                    if (p.getCache().hasKey("doll_of_iban")) {
                                        Functions.___playerTalk(p, n, "niloof, i found the witch's house");
                                        Functions.___npcTalk(p, n, "and...?");
                                        if (Functions.hasItem(p, ItemId.A_DOLL_OF_IBAN.id()) && (!Functions.hasItem(p, ItemId.OLD_JOURNAL.id()))) {
                                            Functions.___npcTalk(p, n, "i found this old book", "i'm not sure if it's of any use to you traveller");
                                            Functions.addItem(p, ItemId.OLD_JOURNAL.id(), 1);
                                            return null;
                                        } else
                                            if (!Functions.hasItem(p, ItemId.A_DOLL_OF_IBAN.id())) {
                                                Functions.___playerTalk(p, n, "i found a strange doll and a book", "but i've lost the doll");
                                                Functions.___npcTalk(p, n, "well it's a good job i found it");
                                                Functions.addItem(p, ItemId.A_DOLL_OF_IBAN.id(), 1);
                                                Functions.___npcTalk(p, n, "the witches rag doll, this here be black magic traveller", "mixed with the right ingredients the doll can inflict serious harm", "these four elements of being are guarded somewhere in this cave", "his shadow, his flesh, his conscience and his blood", "if you can retrieve these,combined with the doll...", "you will be able destroy iban...", "and ressurect the 'well of voyage'");
                                                if (!Functions.hasItem(p, ItemId.OLD_JOURNAL.id())) {
                                                    Functions.___npcTalk(p, n, "i found this old book", "i'm not sure if it's of any use to you traveller");
                                                    Functions.addItem(p, ItemId.OLD_JOURNAL.id(), 1);
                                                }
                                                return null;
                                            }

                                        Functions.___playerTalk(p, n, "i found a strange book and this..");
                                        p.message("you show niloof the strange doll");
                                        Functions.___npcTalk(p, n, "the witches rag doll, this here be black magic traveller");
                                        Functions.___npcTalk(p, n, "iban was magically conjured in that very item");
                                        Functions.___npcTalk(p, n, "his four elements of bieng are guarded somewhere in this cave");
                                        Functions.___npcTalk(p, n, "his shadow, his flesh, his conscience and his blood");
                                        Functions.___npcTalk(p, n, "if you can retrieve these, with the flask...");
                                        Functions.___npcTalk(p, n, "you will be able destroy iban...");
                                        Functions.___npcTalk(p, n, "and ressurect the 'well of voyage'");
                                    } else {
                                        Functions.___playerTalk(p, n, "hello niloof");
                                        Functions.___npcTalk(p, n, "so you still live, not many survive down here");
                                        Functions.___playerTalk(p, n, "as i can see");
                                        Functions.___npcTalk(p, n, "don't stay too long traveller", "ibans calls will soon penetrate your delicate human mind", "and you'll also become one of his minions", "you must go above and find the witch kardia", "she holds the secret to ibans destruction");
                                    }
                                    break;
                                case 7 :
                                    Functions.___playerTalk(p, n, "hi niloof");
                                    Functions.___npcTalk(p, n, "traveller, thank the stars you're still around", "i thought your time had come");
                                    Functions.___playerTalk(p, n, "i've still a few years in me yet");
                                    if (!Functions.hasItem(p, ItemId.A_DOLL_OF_IBAN.id())) {
                                        Functions.___npcTalk(p, n, "i found something i think you need traveller");
                                        Functions.___playerTalk(p, n, "the doll?");
                                        Functions.___npcTalk(p, n, "i found it while slaying some of the souless, here");
                                        p.message("niloof gives you the doll of iban");
                                        Functions.addItem(p, ItemId.A_DOLL_OF_IBAN.id(), 1);
                                    }
                                    Functions.___playerTalk(p, n, "it's about time i delt with iban");
                                    Functions.___npcTalk(p, n, "good luck to you, you'll need it", "may the strength of the elders be with you");
                                    Functions.___playerTalk(p, n, "take care niloof");
                                    break;
                                case 8 :
                                case -1 :
                                    p.message("the dwarf seems to be busy");
                                    break;
                            }
                        } else
                            if (n.getID() == NpcId.KLANK.id()) {
                                switch (p.getQuestStage(Quests.UNDERGROUND_PASS)) {
                                    case 5 :
                                    case 6 :
                                        if (p.getCache().hasKey("doll_of_iban")) {
                                            Functions.___playerTalk(p, n, "hi klank");
                                            Functions.___npcTalk(p, n, "traveller,I hear you plan to destroy iban");
                                            Functions.___playerTalk(p, n, "that's right");
                                            Functions.___npcTalk(p, n, "i have a gift for you, they may help", "i crafted these long ago to protect myself...", "from the teeth of the souless, their bite is vicous", "i haven't seen a another pair which can with stand their jaws");
                                            p.message("klank gives you a pair of gaunlets");
                                            Functions.addItem(p, ItemId.KLANKS_GAUNTLETS.id(), 1);
                                            p.message("and a tinderbox");
                                            Functions.addItem(p, ItemId.TINDERBOX.id(), 1);
                                            Functions.___playerTalk(p, n, "thanks klank");
                                            Functions.___npcTalk(p, n, "good luck traveller, give iban a slap for me");
                                        } else {
                                            Functions.___playerTalk(p, n, "hello my good man");
                                            Functions.___npcTalk(p, n, "Good day to you outsider", "i'm klank, i'm the only blacksmith still alive down here", "infact we're the only ones that haven't yet turned", "if you're not carefull you'll become one of them too");
                                            Functions.___playerTalk(p, n, "who?.. ibans followers");
                                            Functions.___npcTalk(p, n, "they're not followers, they're slaves, they're the souless");
                                            int menu = // do not send over
                                            Functions.___showMenu(p, n, false, "what happened to them?", "no wonder their breath was soo bad");
                                            if (menu == 0) {
                                                Functions.___playerTalk(p, n, "what happened to them?");
                                                Functions.___npcTalk(p, n, "they were normal once, adventurers, treasure hunters", "but men are weak, they couldn't ignore the vocies", "now they all seem to think with one conscience..", "as if they're being controlled by one being");
                                                Functions.___playerTalk(p, n, "iban?");
                                                Functions.___npcTalk(p, n, "maybe?... maybe zamorak himself", "those who try and fight it...", "iban locks in cages, until their minds are too weak to resist", "eventually they all fall to his control", "here take this, i don't need it");
                                                p.message("klank gives you a tinderbox");
                                                Functions.addItem(p, ItemId.TINDERBOX.id(), 1);
                                            } else
                                                if (menu == 1) {
                                                    Functions.___playerTalk(p, n, "no wonder they're breath was soo bad");
                                                    Functions.___npcTalk(p, n, "you think this is funny.. eh");
                                                    Functions.___playerTalk(p, n, "not really, just trying to lighten up the conversation");
                                                    Functions.___npcTalk(p, n, "here take this, i don't need it");
                                                    p.message("klank gives you a tinderbox");
                                                    Functions.addItem(p, ItemId.TINDERBOX.id(), 1);
                                                }

                                        }
                                        break;
                                    case 7 :
                                    case 8 :
                                    case -1 :
                                        Functions.___playerTalk(p, n, "hello klank");
                                        Functions.___npcTalk(p, n, "hello again adventurer, so you're still around");
                                        Functions.___playerTalk(p, n, "still here!");
                                        int menu = Functions.___showMenu(p, n, "have you anymore gauntlets?", "take care klank");
                                        if (menu == 0) {
                                            Functions.___npcTalk(p, n, "well..yes, but they're not cheap to make", "i'll have to sell you a pair");
                                            Functions.___playerTalk(p, n, "how much?");
                                            Functions.___npcTalk(p, n, "5000 coins");
                                            int menu2 = Functions.___showMenu(p, n, "5000, you must be joking", "ok then, i'll take a pair");
                                            if (menu2 == 0) {
                                                Functions.___npcTalk(p, n, "we don't joke down here, friend");
                                            } else
                                                if (menu2 == 1) {
                                                    if (Functions.hasItem(p, ItemId.COINS.id(), 5000)) {
                                                        p.message("you give klank 5000 coins...");
                                                        Functions.removeItem(p, ItemId.COINS.id(), 5000);
                                                        p.message("...and klank gives you a pair of guanletts");
                                                        Functions.addItem(p, ItemId.KLANKS_GAUNTLETS.id(), 1);
                                                        Functions.___npcTalk(p, n, "there you go..i hope they help");
                                                        Functions.___playerTalk(p, n, "i'll see you around klank");
                                                    } else {
                                                        Functions.___playerTalk(p, n, "oh dear, i haven't enough money");
                                                        Functions.___npcTalk(p, n, "sorry, i can't sell them any cheaper than that");
                                                    }
                                                }

                                        } else
                                            if (menu == 1) {
                                                Functions.___npcTalk(p, n, "you too adventurer");
                                            }

                                        break;
                                }
                            }


                    return null;
                });
            }
        };
    }
}

