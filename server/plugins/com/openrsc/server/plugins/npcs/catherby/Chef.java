package com.openrsc.server.plugins.npcs.catherby;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.external.Gauntlets;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.plugins.quests.members.FamilyCrest;
import java.util.concurrent.Callable;


public class Chef implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    switch (p.getQuestStage(Quests.FAMILY_CREST)) {
                        case -1 :
                            Functions.___npcTalk(p, n, "I hear you have brought the completed crest to my father", "Impressive work I must say");
                            if (Functions.hasItem(p, ItemId.STEEL_GAUNTLETS.id()) && (FamilyCrest.getGauntletEnchantment(p) == Gauntlets.STEEL.id())) {
                                Functions.___playerTalk(p, n, "My Father says you can improve these gauntlets for me");
                                Functions.___npcTalk(p, n, "Yes that is true", "I can change them to gauntlets of cooking", "Wearing them means you will burn your lobsters, swordish and shark less");
                                int menu = Functions.___showMenu(p, n, "Yes please do that for me", "I'll see what your brothers have to offer first");
                                if (menu == 0) {
                                    Functions.___message(p, "Caleb holds the gauntlets and closes his eyes", "Caleb concentrates", "Caleb hands the gauntlets to you");
                                    p.getInventory().replace(ItemId.STEEL_GAUNTLETS.id(), ItemId.GAUNTLETS_OF_COOKING.id());
                                    p.getCache().set("famcrest_gauntlets", Gauntlets.COOKING.id());
                                } else
                                    if (menu == 1) {
                                        Functions.___npcTalk(p, n, "Ok suit yourself");
                                    }

                            }
                            return null;
                        case 0 :
                        case 1 :
                            Functions.___npcTalk(p, n, "Who are you? What are you after?");
                            String[] menuOps = new String[]{ "Are you Caleb Fitzharmon?", "Nothing, I will be on my way", "I see you are a chef, will you cook me anything?" };
                            if (p.getQuestStage(Quests.FAMILY_CREST) == 0) {
                                menuOps = new String[]{ "Nothing, I will be on my way", "I see you are a chef, will you cook me anything?" };
                                int choice = Functions.___showMenu(p, n, false, menuOps);
                                if (choice >= 0) {
                                    initialDialogue(p, n, choice + 1);
                                }
                            } else {
                                int choice = Functions.___showMenu(p, n, false, menuOps);
                                initialDialogue(p, n, choice);
                            }
                            break;
                        case 2 :
                            Functions.___npcTalk(p, n, "How is the fish collecting going?");
                            if (((((!Functions.hasItem(p, ItemId.SWORDFISH.id())) && (!Functions.hasItem(p, ItemId.BASS.id()))) && (!Functions.hasItem(p, ItemId.TUNA.id()))) && (!Functions.hasItem(p, ItemId.SALMON.id()))) && (!Functions.hasItem(p, ItemId.SHRIMP.id()))) {
                                Functions.___playerTalk(p, n, "I haven't got all the fish yet");
                                Functions.___npcTalk(p, n, "Remember I want cooked swordfish, bass, tuna, salmon and shrimp");
                            } else {
                                Functions.___playerTalk(p, n, "Yes i have all of that now");
                                Functions.___message(p, "You give all of the fish to Caleb");
                                Functions.removeItem(p, ItemId.SWORDFISH.id(), 1);
                                Functions.removeItem(p, ItemId.BASS.id(), 1);
                                Functions.removeItem(p, ItemId.TUNA.id(), 1);
                                Functions.removeItem(p, ItemId.SALMON.id(), 1);
                                Functions.removeItem(p, ItemId.SHRIMP.id(), 1);
                                p.message("Caleb gives you his piece of the crest");
                                Functions.addItem(p, ItemId.CREST_FRAGMENT_ONE.id(), 1);
                                p.getCache().store("skipped_menu", true);
                                p.updateQuestStage(Quests.FAMILY_CREST, 3);
                                int m = Functions.___showMenu(p, n, "Err what happened to the rest of it?", "Thankyou very much");
                                if (m == 0) {
                                    Functions.___npcTalk(p, n, "Well we had a bit of a fight over it", "We all wanted to be the heir of our fathers lands", "we each ended up with a piece of the crest", "none of us wanted to give their piece of the crest up to any of the others", "And none of us wanted to face our father", "coming home without a complete crest");
                                    Functions.___playerTalk(p, n, "So do you know where I could find any of your brothers?");
                                    Functions.___npcTalk(p, n, "Well we haven't really kept in touch", "What with all falling out over the crest", "I did hear from my brother Avan about a year ago though", "He said he was a living in a town in the desert", "Ask around the desert and you may find him", "My brother has very expensive tastes", "He may not give up the crest easily");
                                    p.getCache().remove("skipped_menu");
                                }
                            }
                            return null;
                        case 3 :
                            if (p.getCache().hasKey("skipped_menu")) {
                                Functions.___npcTalk(p, n, "Hello again, I'm just putting the finishing touches to my salad");
                                int menu = Functions.___showMenu(p, n, "Err what happened to the rest of the crest?", "Good luck with that then");
                                if (menu == 0) {
                                    Functions.___npcTalk(p, n, "Well we had a bit of a fight over it", "We all wanted to be the heir of our fathers lands", "we each ended up with a piece of the crest", "none of us wanted to give their piece of the crest up to any of the others", "And none of us wanted to face our father", "coming home without a complete crest");
                                    Functions.___playerTalk(p, n, "So do you know where I could find any of your brothers?");
                                    Functions.___npcTalk(p, n, "Well we haven't really kept in touch", "What with all falling out over the crest", "I did hear from my brother Avan about a year ago though", "He said he was a living in a town in the desert", "Ask around the desert and you may find him", "My brother has very expensive tastes", "He may not give up the crest easily");
                                    p.getCache().remove("skipped_menu");
                                }
                                return null;
                            }
                            Functions.___playerTalk(p, n, "Where did you say I could find Avan?");
                            Functions.___npcTalk(p, n, "He said he was a living in a town in the desert", "Ask around the desert and you may find him");
                            return null;
                        case 4 :
                        case 5 :
                        case 6 :
                        case 7 :
                        case 8 :
                            Functions.___npcTalk(p, n, "How are you doing getting the rest of the crest?");
                            if (!Functions.hasItem(p, ItemId.CREST_FRAGMENT_ONE.id())) {
                                int menu = Functions.___showMenu(p, n, "I am still working on it", "I have lost the piece you gave me");
                                if (menu == 0) {
                                    Functions.___npcTalk(p, n, "Well good luck in your quest");
                                } else
                                    if (menu == 1) {
                                        Functions.___npcTalk(p, n, "Ah well here is another one");
                                        Functions.addItem(p, ItemId.CREST_FRAGMENT_ONE.id(), 1);
                                    }

                            } else {
                                Functions.___playerTalk(p, n, "I am still working on it");
                                Functions.___npcTalk(p, n, "Well good luck in your quest");
                            }
                            return null;
                    }
                    return null;
                });
            }
        };
    }

    public void initialDialogue(final Player p, final Npc n, int option) {
        if (option == 0) {
            Functions.___playerTalk(p, n, "Are you Caleb Fitzharmon?");
            Functions.___npcTalk(p, n, "I am he, and who might you be?");
            Functions.___playerTalk(p, n, "I have been sent by your father", "He wants me to retrieve the Fitzharmon family crest");
            Functions.___npcTalk(p, n, "Ah, yes hmm well I do have a bit of it yes");
            new Menu().addOptions(new Option("Err what happened to the rest of crest?") {
                public void action() {
                    Functions.___npcTalk(p, n, "Well we had a bit of a fight over it", "We all wanted to be the heir of our fathers lands", "we each ended up with a piece of the crest", "none of us wanted to give their piece of the crest up to any of the others", "And none of us wanted to face our father", "coming home without a complete crest");
                    Functions.___playerTalk(p, n, "So can I have your bit?");
                    HAVE_YOUR_BIT(p, n);
                }
            }, new Option("So can I have your bit?") {
                public void action() {
                    HAVE_YOUR_BIT(p, n);
                }
            }).showMenu(p);
        } else
            if (option == 1) {
                Functions.___playerTalk(p, n, "Nothing I will be on my way");
            } else
                if (option == 2) {
                    Functions.___playerTalk(p, n, "I see you are a chef", "Will you cook me anything?");
                    Functions.___npcTalk(p, n, "I would, but I am very busy", "Trying to prepare my special fish salad", "Which I hope will significantly increase my renown as a master chef");
                }


    }

    private void HAVE_YOUR_BIT(Player p, Npc n) {
        Functions.___npcTalk(p, n, "Well I am the oldest son, by rights it is mine");
        Functions.___playerTalk(p, n, "It's not a lot of use to you without the rest of it though");
        Functions.___npcTalk(p, n, "Well true", "So I'll tell you what I'll do", "I am struggling to complete my seafood salad", "I don't seem to be able to get hold of the ingredients I need", "Help me and I'll help you");
        Functions.___playerTalk(p, n, "What are you missing exactly?");
        Functions.___npcTalk(p, n, "I need cooked swordfish,bass,tuna,salmon and shrimp");
        int menu = Functions.___showMenu(p, n, "Ok I will get those", "Why don't you just give me the crest?");
        if (menu == 0) {
            p.updateQuestStage(Quests.FAMILY_CREST, 2);
        } else
            if (menu == 1) {
                Functions.___npcTalk(p, n, "No I don't want to just give it away");
            }

    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.CHEF.id();
    }
}

