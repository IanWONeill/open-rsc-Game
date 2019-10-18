package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class Jungle_Potion implements QuestInterface , ObjectActionListener , TalkToNpcListener , WallObjectActionListener , ObjectActionExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    @Override
    public int getQuestId() {
        return Quests.JUNGLE_POTION;
    }

    @Override
    public String getQuestName() {
        return "Jungle potion (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player player) {
        player.message("You gain experience in Herblaw !");
        player.message("@gre@You haved gained 1 quest point!");
        Functions.incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.JUNGLE_POTION), true);
        player.getCache().store("jungle_completed", true);
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.TRUFITUS.id();
    }

    private void trufitusDialogue(Player p, Npc n) {
        Functions.___npcTalk(p, n, "My people are afraid to stay in the village.", "They have returned to the jungle", "I need to commune with the gods", "to see what fate befalls us", "you could help me by collecting", "some herbs that I need.");
        int s_opt = // do not send over
        Functions.___showMenu(p, n, false, "Me, how can I help?", "I am very sorry, but I don't have time for that at the moment.");
        if (s_opt == 0) {
            Functions.___playerTalk(p, n, "Me, how can I help?");
            Functions.___npcTalk(p, n, "I need to make a special brew", "A potion that helps me to commune with the gods.", "For this potion, I need very", "special herbs that are only found in", "deep jungle", "I can guide you only so far as the", "herbs are not easy to find", "With some luck, you will find each herb in turn", "and bring it to me. I will give you", "details of where to find the next herb. ", "In return I will give you training in Herblaw");
            int opts = // do not send over
            Functions.___showMenu(p, n, false, "Hmm, sounds difficult, I don't know if I am ready for the challenge", "It sounds like just the challenge for me!");
            if (opts == 0) {
                Functions.___playerTalk(p, n, "Hmm, sounds difficult, I don't know if I am ready for the challenge");
                Functions.___npcTalk(p, n, "Very well then Bwana", "maybe you will return to me invigorated", "and ready to take up the challenge one day ?");
            } else
                if (opts == 1) {
                    Functions.___playerTalk(p, n, "It sounds like just the challenge for me.", "And it would make a nice break from killing things !");
                    Functions.___npcTalk(p, n, "That is excellent then Bwana!", "The first herb you need to gather is called", "'Snake Weed'", "It grows near vines in an area to the south west", "where the ground turns soft and water kisses your feet.");
                    Functions.setQuestStage(p, this, 1);
                    p.getCache().store("got_snake_weed", false);
                }

        } else
            if (s_opt == 1) {
                Functions.___playerTalk(p, n, "I am very sorry, but I don't have time for that.");
                Functions.___npcTalk(p, n, "Very well then Bwana", "may your journeys bring you much joy", "maybe you will pass this way again and", "you will then take up my proposal", "but for now, farewell !");
            }

    }

    private void trufitisChat(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.TRUFITUS.id()) {
            if (cID == (-1)) {
                /**
                 * TRUFITUS *
                 */
                switch (p.getQuestStage(this)) {
                    case 0 :
                        Functions.___npcTalk(p, n, "Greetings Bwana,", "I am Trufitus Shakaya of the", "Taie Bwo Wannai Village. ", "Welcome to our humble settlement.");
                        int opt = // do not send over
                        Functions.___showMenu(p, n, false, "What does Bwana mean?", "Taie Bwo Wannai? What does that mean?", "It's a nice village, where is everyone?");
                        if (opt == 0) {
                            Functions.___playerTalk(p, n, "What does Bwana mean?");
                            Functions.___npcTalk(p, n, "Gracious sir, it means 'friend'", "And friends come in peace", "I assume that you come in peace?");
                            int s = // do not send over
                            Functions.___showMenu(p, n, false, "Yes, of course I do.", "What does a warrior like me know about peace?");
                            if (s == 0) {
                                Functions.___playerTalk(p, n, "Yes, of course I do!");
                                Functions.___npcTalk(p, n, "Well, that is good news", "as I may have a proposition for you");
                                int s1 = Functions.___showMenu(p, n, "A proposition eh, sounds interesting!", "I am sorry, but I am very busy");
                                if (s1 == 0) {
                                    Functions.___npcTalk(p, n, "I hoped that you would think so.");
                                    trufitusDialogue(p, n);
                                } else
                                    if (s1 == 1) {
                                        Functions.___npcTalk(p, n, "Very well then", "may your journeys bring you much joy", "maybe you will pass this way again", "and you will then take up my proposal,", "but for now", "fare thee well");
                                    }

                            } else
                                if (s == 1) {
                                    Functions.___playerTalk(p, n, "What does a warrior like me know about peace?");
                                    Functions.___npcTalk(p, n, "When you grow weary of violence", "and seek a more enlightened path", "please pay me a visit", "as I may have a proposal for you", "Now I need to attend to the plight", "of my people, please excuse me");
                                }

                        } else
                            if (opt == 1) {
                                Functions.___playerTalk(p, n, "Taie Bwo Wannai? What does that mean?");
                                Functions.___npcTalk(p, n, "It means 'small clearing in the jungle'", "But now it is the name of our village.");
                                int ss = // do not send over
                                Functions.___showMenu(p, n, false, "It's a nice village, where is everyone?", "I am sorry, but I am very busy");
                                if (ss == 0) {
                                    Functions.___playerTalk(p, n, "It seems like a nice village, where is everyone?");
                                    trufitusDialogue(p, n);
                                } else
                                    if (ss == 1) {
                                        Functions.___playerTalk(p, n, "I am sorry, but I am very busy");
                                        Functions.___npcTalk(p, n, "Very well then", "may your journeys bring you much joy", "maybe you will pass this way again", "and you will then take up my proposal,", "but for now", "fare thee well");
                                    }

                            } else
                                if (opt == 2) {
                                    Functions.___playerTalk(p, n, "It seems like a nice village, where is everyone?");
                                    trufitusDialogue(p, n);
                                }


                        break;
                    case 1 :
                        p.getCache().store("got_snake_weed", false);
                        Functions.___npcTalk(p, n, "Hello Bwana, do you have the Snake Weed?");
                        int option = // do not send over
                        Functions.___showMenu(p, n, false, "Of course!", "Not yet, sorry, what's the clue again?");
                        if (option == 0) {
                            Functions.___playerTalk(p, n, "Of Course!");
                            if (!Functions.hasItem(p, ItemId.SNAKE_WEED.id())) {
                                Functions.___npcTalk(p, n, "Please don't try to deceive me!", "I really need that Snake Weed if I am to make this potion");
                            } else {
                                // DONE
                                Functions.___npcTalk(p, n, "Great, you have the 'Snake Weed'", "Ok, the next herb is called, 'Ardrigal'", "it is related to the palm and grows", "to the East in its brother's shady profusion.");
                                p.message("You give the Snake Weed to Trufitus");
                                Functions.removeItem(p, ItemId.SNAKE_WEED.id(), 1);
                                Functions.___npcTalk(p, n, "Many thanks for the 'Snake Weed'");
                                Functions.setQuestStage(p, this, 2);
                                p.getCache().store("got_ardigal", false);
                                // no longer needed
                                p.getCache().remove("got_snake_weed");
                            }
                        } else
                            if (option == 1) {
                                Functions.___playerTalk(p, n, "Not yet, sorry, what's the clue again?");
                                Functions.___npcTalk(p, n, "It is related to the palm and grows", "well to the north in its brother's shady profusion.", "I really need that Snake Weed if I am to make this potion");
                            }

                        break;
                    case 2 :
                        p.getCache().store("got_ardigal", false);
                        Functions.___npcTalk(p, n, "Hello again, have you been able to get the Ardrigal ?");
                        int o = // do not send over
                        Functions.___showMenu(p, n, false, "Of course!", "Not yet, sorry.");
                        if (o == 0) {
                            Functions.___playerTalk(p, n, "Of Course!");
                            if (Functions.hasItem(p, ItemId.ARDRIGAL.id())) {
                                // DONE
                                Functions.___npcTalk(p, n, "Ah, I see you have found the 'Ardrigal'", "you are doing well Bwana, the next", "herb is called, 'Sito Foil' and grows best", "where the ground has been blackened", "by the living flame.");
                                Functions.___message(p, "You give the Ardrigal to Trufitus");
                                Functions.removeItem(p, ItemId.ARDRIGAL.id(), 1);
                                Functions.setQuestStage(p, this, 3);
                                p.getCache().store("got_sito_foil", false);
                                // no longer needed
                                p.getCache().remove("got_ardigal");
                            } else {
                                Functions.___npcTalk(p, n, "Please don't try to deceive me!", "I still require Ardrigal,", "this potion will remain incomplete without it.");
                            }
                        } else
                            if (o == 1) {
                                Functions.___playerTalk(p, n, "Not yet, sorry.");
                                Functions.___npcTalk(p, n, "I still require Ardrigal,", "this potion will remain incomplete without it.");
                            }

                        break;
                    case 3 :
                        p.getCache().store("got_sito_foil", false);
                        Functions.___npcTalk(p, n, "Greetings Bwana", "have you been successful in getting Sito Foil?");
                        int os = // do not send over
                        Functions.___showMenu(p, n, false, "Of course!", "Not yet, sorry.");
                        if (os == 0) {
                            // DONE
                            Functions.___playerTalk(p, n, "Of Course!");
                            if (Functions.hasItem(p, ItemId.SITO_FOIL.id())) {
                                Functions.___npcTalk(p, n, "Well done Bwana, just two more herbs", "to collect. The next herb is called, 'Volencia Moss'", "And it clings to rocks for it's existence", "It is difficult to see, so you must search for it well.");
                                Functions.___message(p, "You give the Sito Foil to Trufitus");
                                Functions.removeItem(p, ItemId.SITO_FOIL.id(), 1);
                                Functions.setQuestStage(p, this, 4);
                                p.getCache().store("got_volencia_moss", false);
                                // no longer needed
                                p.getCache().remove("got_sito_foil");
                            } else {
                                Functions.___npcTalk(p, n, "Please don't try to deceive me!", "I still require Sito Foil, every herb is vital.");
                            }
                        } else
                            if (os == 1) {
                                Functions.___playerTalk(p, n, "Not yet, sorry.");
                                Functions.___npcTalk(p, n, "I still require Sito Foil, every herb is vital.");
                            }

                        break;
                    case 4 :
                        p.getCache().store("got_volencia_moss", false);
                        Functions.___npcTalk(p, n, "Greetings Bwana", "Do you have the 'Volencia Moss' ?");
                        int oo = // do not send over
                        Functions.___showMenu(p, n, false, "Of course!", "Not yet, sorry.");
                        if (oo == 0) {
                            Functions.___playerTalk(p, n, "Of Course!");
                            if (!Functions.hasItem(p, ItemId.VOLENCIA_MOSS.id())) {
                                Functions.___npcTalk(p, n, "Please don't try to deceive me!", "I know it is difficult to find, but I do need Volencia Moss", "After that herb, you only have one more to find.");
                            } else {
                                // DONE
                                Functions.___npcTalk(p, n, "Ah, Volencia Moss, beautiful!", "One final herb and the potion will", "be complete. This is the most difficult to", "find as it inhabits the darkness of the", "underground. It is called 'Rogues Purse'", "And is found in the darkest place on the Island", "A secret entrance to the caverns is set into", "The Northern cliffs of this land", "Take care Bwana as it may be very dangerous");
                                Functions.___message(p, "You give the Volencia Moss to Trufitus");
                                Functions.removeItem(p, ItemId.VOLENCIA_MOSS.id(), 1);
                                Functions.setQuestStage(p, this, 5);
                                p.getCache().store("got_rogues_purse", false);
                                // no longer needed
                                p.getCache().remove("got_volencia_moss");
                            }
                        } else
                            if (oo == 1) {
                                Functions.___playerTalk(p, n, "Not yet, sorry.");
                                Functions.___npcTalk(p, n, "I know it is difficult to find, but I do need Volencia Moss", "After that herb, you only have one more to find.");
                            }

                        break;
                    case 5 :
                        p.getCache().store("got_rogues_purse", false);
                        Functions.___npcTalk(p, n, "Have you found 'Rogues Purse' ?");
                        int ol = Functions.___showMenu(p, n, "Yes Sir, indeedy I do!", "Not yet, sorry.");
                        if (ol == 0) {
                            if (!Functions.hasItem(p, ItemId.ROGUES_PURSE.id())) {
                                Functions.___npcTalk(p, n, "Please don't try to deceive me!", "Rogues Purse is the last herb", "for the potion and possibly the most", "difficult to find but I do need it.");
                            } else {
                                // DONE
                                Functions.___npcTalk(p, n, "Most excellent Bwana!", "You have returned all the herbs to me", "and I can now finish the preparations", "for the potion and thankfully divine with the gods.", "Many blessings on you!", "I must now prepare", "please excuse me while I make", "the arrangements");
                                p.message("You give the Rogues Purse to Trufitus");
                                Functions.removeItem(p, ItemId.ROGUES_PURSE.id(), 1);
                                p.message("Trufitus shows you some techniques in Herblaw");
                                Functions.completeQuest(p, this);// COMPLETED AND FULLY WORKING

                                // no longer needed
                                p.getCache().remove("got_rogues_purse");
                            }
                        } else
                            if (ol == 1) {
                                Functions.___npcTalk(p, n, "Rogues Purse is the last herb", "for the potion and possibly the most", "difficult to find but I do need it.");
                            }

                        break;
                    case -1 :
                        // Two after completion dialogues (first dialogue with
                        // cache is used only once after Quest)
                        if (p.getCache().hasKey("jungle_completed")) {
                            Functions.___npcTalk(p, n, "My greatest respects Bwana", "I have communed with the gods", "and the future looks good for my people", "We are happy now that the gods are not angry with us", "With some blessings we will be safe here.");
                            p.getCache().remove("jungle_completed");
                            return;
                        }
                        if (p.getQuestStage(Quests.SHILO_VILLAGE) == (-1)) {
                            Functions.___playerTalk(p, n, "Greetings");
                            Functions.___npcTalk(p, n, "Hello Bwana.", "I conclude that you have been succesful.", "Mosol sent word that the village is clearing of Zombies.", "You have done us all a great dead!", "Why not go and visit him and have a look around Shilo", "village. You may find some interesting things there!");
                        } else
                            if ((p.getQuestStage(Quests.SHILO_VILLAGE) == 1) || (p.getQuestStage(Quests.SHILO_VILLAGE) == 2)) {
                                /* Handle shilo village start. */
                                Functions.___playerTalk(p, n, "Greetings.");
                                Functions.___npcTalk(p, n, "Greetings Bwana!", "You look like you have some serious news...");
                                Functions.___playerTalk(p, n, "Well, I think I may have.", "I have just spoken to Mosol Rei and he says that ", "Rashiliyia has returned...");
                                Functions.___npcTalk(p, n, "Oh dear, it is more serious than I imagined.");
                                int menu = Functions.___showMenu(p, n, "How are you anyway my friend?", "What do you know about Rashiliyia?", "What do you know about Mosol Rei?");
                                if (menu == 0) {
                                    Functions.___npcTalk(p, n, "I'm very well thanks.");
                                    int sub_menu = Functions.___showMenu(p, n, "What do you know about Rashiliyia?", "What do you know about Mosol Rei?");
                                    if (sub_menu == 0) {
                                        trufitisChat(p, n, Jungle_Potion.Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA);
                                    } else
                                        if (sub_menu == 1) {
                                            trufitisChat(p, n, Jungle_Potion.Trufitus.WHAT_DO_YOU_KNOW_ABOUT_MOSEL_REI);
                                        }

                                } else
                                    if (menu == 1) {
                                        trufitisChat(p, n, Jungle_Potion.Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA);
                                    } else
                                        if (menu == 2) {
                                            trufitisChat(p, n, Jungle_Potion.Trufitus.WHAT_DO_YOU_KNOW_ABOUT_MOSEL_REI);
                                        }


                            } else
                                if (Functions.atQuestStages(p, Quests.SHILO_VILLAGE, 3, 4, 5)) {
                                    Functions.___playerTalk(p, n, "Greetings...");
                                    Functions.___npcTalk(p, n, "Greetings Bwana, you have been away!", "The situation with Rashiliyia is worsening!", "I pray that you have some good news for me.");
                                    Functions.___playerTalk(p, n, "I think I found the temple of Ah Za Rhoon.");
                                    int menu;
                                    if ((p.getQuestStage(Quests.SHILO_VILLAGE) == 4) || (p.getQuestStage(Quests.SHILO_VILLAGE) == 5)) {
                                        menu = Functions.___showMenu(p, n, "I have some items that I need help with.", "I need some help with the Temple of Ah Za Rhoon.", "I have just buried Zadimus's corpse.");
                                    } else {
                                        menu = Functions.___showMenu(p, n, "I have some items that I need help with.", "I need some help with the Temple of Ah Za Rhoon.");
                                    }
                                    if (menu == 0) {
                                        Functions.___npcTalk(p, n, "Well, just let me see the item and I'll help as much as I can.");
                                        trufitisChat(p, n, Jungle_Potion.Trufitus.SHOW_ME_TEMPLE_ITEMS);
                                    } else
                                        if (menu == 1) {
                                            Functions.___npcTalk(p, n, "If you have found the temple, you should search it", "thoroughly and see if there are any clues about", "Rashiliyia.");
                                            trufitisChat(p, n, Jungle_Potion.Trufitus.SHOW_ME_TEMPLE_ITEMS);
                                        } else
                                            if ((menu == 2) && ((p.getQuestStage(Quests.SHILO_VILLAGE) == 4) || (p.getQuestStage(Quests.SHILO_VILLAGE) == 5))) {
                                                Functions.___npcTalk(p, n, "Something seems different about you. You look like ", "you have seen a ghost?");
                                                Functions.___playerTalk(p, n, "It just so happens that I have!");
                                                Functions.___npcTalk(p, n, "Oh! So you managed to bury Zadimus's Corpse?");
                                                Functions.___playerTalk(p, n, "Yes, it was pretty grisly!");
                                                int m = Functions.___showMenu(p, n, "The spirit said something about keys and kin?", "The spirit rambled on about some nonsense.");
                                                if (m == 0) {
                                                    trufitisChat(p, n, Jungle_Potion.Trufitus.KEYS_AND_KIN);
                                                } else
                                                    if (m == 1) {
                                                        Functions.___npcTalk(p, n, "Oh, so it most likely was not very important then?");
                                                    }

                                            }


                                } else
                                    if (p.getQuestStage(Quests.SHILO_VILLAGE) == 6) {
                                        Functions.___playerTalk(p, n, "Greetings...");
                                        Functions.___npcTalk(p, n, "Greetings Bwana, did you find the tomb of Bervirius?");
                                        int chat = Functions.___showMenu(p, n, "Yes, I found his tomb.", "No, I didn't find a thing.", "I actually need help with something else.");
                                        if (chat == 0) {
                                            Functions.___npcTalk(p, n, "That is truly great news Bwana!", "You are certainly very resourceful.", "If you have found any items that you need help with", "please let me see them and I will help as much as I can.");
                                            int ex5 = Functions.___showMenu(p, n, "I actually need help with something else.", "I didn't find anything in the tomb.");
                                            if (ex5 == 0) {
                                                trufitisChat(p, n, Jungle_Potion.Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
                                            } else
                                                if (ex5 == 1) {
                                                    trufitisChat(p, n, Jungle_Potion.Trufitus.DIDNT_FIND_ANYTHING_IN_THE_TOMB);
                                                }

                                        } else
                                            if (chat == 1) {
                                                Functions.___npcTalk(p, n, "That is a shame Bwana, we really do need to act against", "Rashiliyia soon if we are ever to stand a chance of defeating her.");
                                                int chat2 = Functions.___showMenu(p, n, "Actually I did find the tomb, I was just joking.", "I actually need help with something else.", "I didn't find anything in the tomb.");
                                                if (chat2 == 0) {
                                                    Functions.___npcTalk(p, n, "Well, Bwana, this is no laughing matter.", "We need to take this very seriously and act now!", "If you have found any items at the tomb that you need help ", "with please let me see them and I will help as much as I can.");
                                                    int ex4 = Functions.___showMenu(p, n, "I didn't find anything in the tomb.", "I actually need help with something else.");
                                                    if (ex4 == 0) {
                                                        trufitisChat(p, n, Jungle_Potion.Trufitus.DIDNT_FIND_ANYTHING_IN_THE_TOMB);
                                                    } else
                                                        if (ex4 == 1) {
                                                            trufitisChat(p, n, Jungle_Potion.Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
                                                        }

                                                } else
                                                    if (chat2 == 1) {
                                                        trufitisChat(p, n, Jungle_Potion.Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
                                                    } else
                                                        if (chat2 == 2) {
                                                            trufitisChat(p, n, Jungle_Potion.Trufitus.DIDNT_FIND_ANYTHING_IN_THE_TOMB);
                                                        }


                                            } else
                                                if (chat == 2) {
                                                    trufitisChat(p, n, Jungle_Potion.Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
                                                }


                                    } else
                                        if (p.getQuestStage(Quests.SHILO_VILLAGE) == 7) {
                                            Functions.___npcTalk(p, n, "You may want to start looking for Rashiliyia's Tomb.", "Do you need extra help with locating it?");
                                            int off = Functions.___showMenu(p, n, "Yes please.", "No thanks, I've got a good idea where it is.", "I actually need help with something else.");
                                            if (off == 0) {
                                                Functions.___npcTalk(p, n, "You may like to start checking North of Ah Za Rhoon.", "There must be some clue as to what to look for when locating", "the tomb. Was there anything else at the tomb of Bervirius?");
                                                int off2 = Functions.___showMenu(p, n, "Just a Dolmen with some symbols on it.", "Nothing that was significant.");
                                                if (off2 == 0) {
                                                    Functions.___npcTalk(p, n, "Well, what symbols were they, perhaps that will", "give a clue to the location?");
                                                } else
                                                    if (off2 == 1) {
                                                        Functions.___npcTalk(p, n, "Oh, perhaps you should take another look at them?", "Any scrap of information might be useful.");
                                                    }

                                            } else
                                                if (off == 1) {
                                                    Functions.___npcTalk(p, n, "Well, that is very good Bwana,", "perhaps you should locate it already?");
                                                } else
                                                    if (off == 2) {
                                                        trufitisChat(p, n, Jungle_Potion.Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
                                                    }


                                        } else
                                            if (p.getQuestStage(Quests.SHILO_VILLAGE) == 8) {
                                                if ((p.getCache().hasKey("dolmen_zombie") && p.getCache().hasKey("dolmen_skeleton")) && p.getCache().hasKey("dolmen_ghost")) {
                                                    Functions.___playerTalk(p, n, "Hello");
                                                    Functions.___npcTalk(p, n, "Greetings again Bwana.", "I hope that you have managed to locate Rashiliyias Tomb.", "Again, if you found any interesting items, please show", "them to me.");
                                                    int newMenu2 = Functions.___showMenu(p, n, "What should I do now?", "Thanks!");
                                                    if (newMenu2 == 0) {
                                                        p.message("Trufitus scratches his head.");
                                                        Functions.___npcTalk(p, n, "Well Bwana, if you have Rashiliyias remains,", "you need to find a way to put her spirit to rest.", "Perhaps there was a clue with one of the artifacts", "that you have?", "Why not have a look through the artifacts that you have ", "found and see if there is something clue that might help?", "If you do not have her remains, ", "you will need to find them.");
                                                    } else
                                                        if (newMenu2 == 1) {
                                                            Functions.___npcTalk(p, n, "You're more than welcome Bwana!", "Good luck for the rest of your quest.");
                                                        }

                                                    return;
                                                }
                                                Functions.___playerTalk(p, n, "Hello again..");
                                                Functions.___npcTalk(p, n, "And greetings to you Bwana!", "Have you found anything new Bwana?");
                                                int tomb = Functions.___showMenu(p, n, "Nope, I haven't found anything.", "Yes, I've found Rashiliyia's Tomb!", "I get choked when I go into Rashiliyias Tomb.");
                                                if (tomb == 0) {
                                                    Functions.___npcTalk(p, n, "Well, that is a pity? Perhaps you should keep on looking?");
                                                } else
                                                    if (tomb == 1) {
                                                        Functions.___npcTalk(p, n, "Very good Bwana, this is very good!", "Did you find her remains?");
                                                        int newMenu = // do not send over
                                                        Functions.___showMenu(p, n, false, "Yes, In fact I did!", "Nope, I haven't found them yet.");
                                                        if (newMenu == 0) {
                                                            Functions.___playerTalk(p, n, "Yes, In fact I did!");
                                                            Functions.___npcTalk(p, n, "This is truly great Bwana.", "If you need help with the remains, ", "please show them to me.");
                                                        } else
                                                            if (newMenu == 1) {
                                                                Functions.___playerTalk(p, n, "No, I haven't found them yet.");
                                                                Functions.___npcTalk(p, n, "You really need to find the remains before we", "can hope to defeat her and remove her influence from", "Shilo village.");
                                                            }

                                                    } else
                                                        if (tomb == 2) {
                                                            Functions.___npcTalk(p, n, "Maybe you have missed something, a special clue?", "It might be worth searching the temple of Ah Za Rhoon again.", "Or go back to Bervirius Tomb", "for a more thorough search.");
                                                        }


                                            } else {
                                                Functions.___npcTalk(p, n, "Greetings once again Bwana,", "I have no more news since we last spoke.");
                                            }





                        break;
                }
            }
        }
        switch (cID) {
            case Jungle_Potion.Trufitus.DIDNT_FIND_ANYTHING_IN_THE_TOMB :
                Functions.___npcTalk(p, n, "Maybe you need to look around a little more.", "There must be some small detail at least that can help us");
                int chat3 = Functions.___showMenu(p, n, "I have some items that I need some help with.", "I actually need help with something else.");
                if (chat3 == 0) {
                    trufitisChat(p, n, Jungle_Potion.Trufitus.SHOW_ME_TEMPLE_ITEMS);
                } else
                    if (chat3 == 1) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
                    }

                break;
            case Jungle_Potion.Trufitus.HELP_WITH_ZADIMUS :
                Functions.___npcTalk(p, n, "All I know is that Zadimus was a high priest of Zamorak,", "Rashiliyia loved him but he did not return her affections.", "When she become a more powerful sorceress, she attacked the", "Ah Za Rhoon temple to Zamorak that Zadimus built and ", "reduced it to rubble. What his fate was, I do not know. ", "If you find anything relating to him at the temple of ", "Ah Za Rhoon, please let me see it.");
                int ex = Functions.___showMenu(p, n, "Is there any sacred ground around here?", "I need help with Bervirius.", "I need help with Rashliyia.", "I need some help with the Temple of Ah Za Rhoon.", "Ok, thanks!");
                if (ex == 0) {
                    Functions.___npcTalk(p, n, "The ground in the centre of the village is very sacred to us", "Maybe you could try there ?");
                } else
                    if (ex == 1) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_BERVIRIUS);
                    } else
                        if (ex == 2) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_RASH);
                        } else
                            if (ex == 3) {
                                trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE);
                            } else
                                if (ex == 4) {
                                    Functions.___npcTalk(p, n, "You're quite welcome Bwana.");
                                }




                break;
            case Jungle_Potion.Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE :
                Functions.___npcTalk(p, n, "If you have found the temple, you should search it", "thoroughly and see if there are any clues about", "Rashiliyia.");
                int ex3 = // do not send over
                Functions.___showMenu(p, n, false, "I need help with Rashlilia.", "I need help with Zadimus.", "I have some items that I need help with.", "I need help with Bervirius.", "Ok, thanks!");
                if (ex3 == 0) {
                    Functions.___playerTalk(p, n, "I need help with Rashliyia.");
                    trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_RASH);
                } else
                    if (ex3 == 1) {
                        Functions.___playerTalk(p, n, "I need help with Zadimus.");
                        trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_ZADIMUS);
                    } else
                        if (ex3 == 2) {
                            Functions.___playerTalk(p, n, "I have some items that I need help with.");
                            trufitisChat(p, n, Jungle_Potion.Trufitus.SHOW_ME_TEMPLE_ITEMS);
                        } else
                            if (ex3 == 3) {
                                Functions.___playerTalk(p, n, "I need help with Bervirius.");
                                trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_BERVIRIUS);
                            } else
                                if (ex3 == 4) {
                                    Functions.___playerTalk(p, n, "Ok, thanks!");
                                    Functions.___npcTalk(p, n, "You're quite welcome Bwana.");
                                }




                break;
            case Jungle_Potion.Trufitus.HELP_WITH_BERVIRIUS :
                Functions.___npcTalk(p, n, "Bervirius is the Son of Rashiliyia.", "His tomb may hold some clues as to how", "Rashiliyia may be defeated.");
                int ex2 = Functions.___showMenu(p, n, "I need help with Zadimus.", "I have some items that I need help with.", "I need help with Rashliyia.", "I need some help with the Temple of Ah Za Rhoon.", "Ok, thanks!");
                if (ex2 == 0) {
                    trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_ZADIMUS);
                } else
                    if (ex2 == 1) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.SHOW_ME_TEMPLE_ITEMS);
                    } else
                        if (ex2 == 2) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_RASH);
                        } else
                            if (ex2 == 3) {
                                trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE);
                            } else
                                if (ex2 == 4) {
                                    Functions.___npcTalk(p, n, "You're quite welcome Bwana.");
                                }




                break;
            case Jungle_Potion.Trufitus.HELP_WITH_RASH :
                Functions.___npcTalk(p, n, "We need to find Rashiliyia's resting place ", "and learn how to put her spirit to rest. ", "You may find some clues to her resting place", "in Ah Za Rhoon or Bervirius Tomb.");
                int b = Functions.___showMenu(p, n, "I need help with Zadimus.", "I have some items that I need help with.", "I need help with Bervirius.", "I need some help with the Temple of Ah Za Rhoon.", "Ok, thanks!");
                if (b == 0) {
                    trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_ZADIMUS);
                } else
                    if (b == 1) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.SHOW_ME_TEMPLE_ITEMS);
                    } else
                        if (b == 2) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_BERVIRIUS);
                        } else
                            if (b == 3) {
                                trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE);
                            } else
                                if (b == 4) {
                                    Functions.___npcTalk(p, n, "You're quite welcome Bwana.");
                                }




                break;
            case Jungle_Potion.Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE :
                Functions.___npcTalk(p, n, "What could I possibly help you with Bwana?");
                int c = // do not send over
                Functions.___showMenu(p, n, false, "I need help with Rashiliyia.", "I need help with Zadimus.", "I have some items that I need help with.", "I need help with Bervirius.", "Ok, thanks!");
                if (c == 0) {
                    Functions.___playerTalk(p, n, "I need help with Rashliyia.");
                    trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_RASH);
                } else
                    if (c == 1) {
                        Functions.___playerTalk(p, n, "I need help with Zadimus.");
                        trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_ZADIMUS);
                    } else
                        if (c == 2) {
                            Functions.___playerTalk(p, n, "I have some items that I need help with.");
                            trufitisChat(p, n, Jungle_Potion.Trufitus.SHOW_ME_TEMPLE_ITEMS);
                        } else
                            if (c == 3) {
                                Functions.___playerTalk(p, n, "I need help with Bervirius.");
                                trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_BERVIRIUS);
                            } else
                                if (c == 4) {
                                    Functions.___playerTalk(p, n, "Ok, thanks!");
                                    Functions.___npcTalk(p, n, "You're quite welcome Bwana.");
                                }




                break;
            case Jungle_Potion.Trufitus.WHAT_DO_YOU_KNOW_ABOUT_MOSEL_REI :
                Functions.___npcTalk(p, n, "I know he is a brave warrior, he lives in a village south of here.", "Your journeys have taken you far!");
                int opt = Functions.___showMenu(p, n, "What do you know about Rashiliyia?", "Do you trust him?");
                if (opt == 0) {
                    trufitisChat(p, n, Jungle_Potion.Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA);
                } else
                    if (opt == 1) {
                        Functions.___npcTalk(p, n, "He is a little headstrong, but for the right reasons.", "I think he is generally to be trusted.");
                        int opt2 = Functions.___showMenu(p, n, "What do you know about Rashiliyia?", "Mosol Rei said something about a legend?");
                        if (opt2 == 0) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA);
                        } else
                            if (opt2 == 1) {
                                trufitisChat(p, n, Jungle_Potion.Trufitus.SOMETHING_ABOUT_A_LEGEND);
                            }

                    }

                break;
            case Jungle_Potion.Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA :
                Functions.___npcTalk(p, n, "Hmmm, it's been a long time since I heard that name.", "She is the Queen of the Undead.", "and a more fearsome enemy you will be unlikely to find.", "I fear that you bring me news that she has returned to plague us once again?", "Alas I know of no weakness that she has.");
                int opt3 = Functions.___showMenu(p, n, "So there is nothing we can do?", "Should I start to evacuate the island?", "Mosol Rei said something about a legend?");
                if (opt3 == 0) {
                    Functions.___npcTalk(p, n, "Not that I can think of");
                    int opt8 = Functions.___showMenu(p, n, "Oh, ok!", "Should I start to evacuate the Island?");
                    if (opt8 == 0) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.OH_OK);
                    } else
                        if (opt8 == 1) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.EVACUATE_ISLAND);
                        }

                } else
                    if (opt3 == 1) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.EVACUATE_ISLAND);
                    } else
                        if (opt3 == 2) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.SOMETHING_ABOUT_A_LEGEND);
                        }


                break;
            case Jungle_Potion.Trufitus.SOMETHING_ABOUT_A_LEGEND :
                Functions.___npcTalk(p, n, "Ah, yes, there is a legend, but it is lost in the midst of antiquity...", "The last place to hold any details regarding this mystery", "was in the temple of Ah-Za_Rhoon", "And that has long since vanished, it crumbled into dust.");
                int opt4 = Functions.___showMenu(p, n, "Why was it called Ah Za Rhoon?", "Do you know anything more about the temple?");
                if (opt4 == 0) {
                    trufitisChat(p, n, Jungle_Potion.Trufitus.AH_ZA_RHOON);
                } else
                    if (opt4 == 1) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.MORE_ABOUT_THE_TEMPLE);
                    }

                break;
            case Jungle_Potion.Trufitus.MORE_ABOUT_THE_TEMPLE :
                Functions.___npcTalk(p, n, "Not much", "I would say that is about it...", "Even the great priest Zadimus who built the temple did not survive.", "Some say that Rashiliyia caused the temple to colapse.", "She was angry at Zadimus for not returning her affections.", "She was a great sorceress even before they met.");
                int opt6 = Functions.___showMenu(p, n, "Tell me more", "Are there any traps there?");
                if (opt6 == 0) {
                    Functions.___npcTalk(p, n, "I don't know anymore.", "You're very demanding aren't you!");
                } else
                    if (opt6 == 1) {
                        Functions.___npcTalk(p, n, "How am I supposed to know?", "Alot of what I know is most probably wrong", "But some of it seems right to me.", "Excuse me but I must get back to my studies.");
                    }

                break;
            case Jungle_Potion.Trufitus.EVACUATE_ISLAND :
                Functions.___npcTalk(p, n, "Yes, that may be a good idea", "Many people could die!", "If only there was a way to defeat her!");
                int opt7 = Functions.___showMenu(p, n, "Mosol Rei said something about a legend?", "Will you pack your things now?");
                if (opt7 == 0) {
                    trufitisChat(p, n, Jungle_Potion.Trufitus.SOMETHING_ABOUT_A_LEGEND);
                } else
                    if (opt7 == 1) {
                        Functions.___npcTalk(p, n, "I will wait and see what will happen.", "Maybe she does not have the power to strike too far from her resting place?", "But there are many things that I need to do now");
                        int opt9 = Functions.___showMenu(p, n, "Is her resting place important?", "Oh, ok!");
                        if (opt9 == 0) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.RESTING_PLACE);
                        } else
                            if (opt9 == 1) {
                                trufitisChat(p, n, Jungle_Potion.Trufitus.OH_OK);
                            }

                    }

                break;
            case Jungle_Potion.Trufitus.THANKS_FOR_THE_INFORMATION :
                Functions.___npcTalk(p, n, "What information?");
                Functions.___message(p, "Trufitus looks at you blankly, then wanders off.");
                Functions.___npcTalk(p, n, "Hmmm, well, you are welcome bwana.");
                break;
            case Jungle_Potion.Trufitus.AH_ZA_RHOON :
                Functions.___npcTalk(p, n, "It is from an ancient language.", "The direct translation is...", "'Magnificence floating on water'", "But my research makes me believe that the temple was built on land", "And most likely between large bodies of water, for example large lakes.", "However, many people have searched for the temple, and have failed.", "I would hate to see you waste your time on a pointless search like that.");
                if (p.getQuestStage(Quests.SHILO_VILLAGE) == 1) {
                    p.updateQuestStage(Quests.SHILO_VILLAGE, 2);
                }
                int opt5 = Functions.___showMenu(p, n, "Thanks for the information!", "Do you know anything more about the temple?");
                if (opt5 == 0) {
                    trufitisChat(p, n, Jungle_Potion.Trufitus.THANKS_FOR_THE_INFORMATION);
                } else
                    if (opt5 == 1) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.MORE_ABOUT_THE_TEMPLE);
                    }

                break;
            case Jungle_Potion.Trufitus.OH_OK :
                Functions.___npcTalk(p, n, "Yes, it's a bit sad really, I liked that village.");
                Functions.___message(p, "Trufitus seems deeply touched...");
                Functions.___npcTalk(p, n, "Well, I hope you will excuse me, but I need to get back to my studies.");
                break;
            case Jungle_Potion.Trufitus.WEAKNESS :
                Functions.___npcTalk(p, n, "I am not sure, but the legend about her certainly is long", "It's a pity that the temple of Ah Za Rhoon has crumbled", "as there my be some clues that could help us to defeat her.", "Usually, the largest problem is locating her resting place.");
                int opt12 = Functions.___showMenu(p, n, "Why was it called Ah Za Rhoon?", "Is her resting place important?");
                if (opt12 == 0) {
                    trufitisChat(p, n, Jungle_Potion.Trufitus.AH_ZA_RHOON);
                } else
                    if (opt12 == 1) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.RESTING_PLACE);
                    }

                break;
            case Jungle_Potion.Trufitus.RESTING_PLACE :
                Functions.___npcTalk(p, n, "Only a few people ever reported seeing a ghost like wraith", "It only ever appeared in the place where her bones were laid to rest", "Of course, she only has to get one of her minions to move the bones", "And she has a new land to unleash her undead plague.");
                int opt10 = Functions.___showMenu(p, n, "What are minions?", "What are onions?");
                if (opt10 == 0) {
                    Functions.___npcTalk(p, n, "Minions are the fiendish undead creatures that she controls.", "She has very few living worshippers, but they need to be dealt with at some point", "Usually a strong creature of some sort will be guarding her remains", "And of course, she is a very powerful spell caster herself ", "Not to be tackled lightly");
                    int opt13 = Functions.___showMenu(p, n, "Thanks for the information!", "Does she have any weaknesses?");
                    if (opt13 == 0) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.THANKS_FOR_THE_INFORMATION);
                    } else
                        if (opt13 == 1) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.WEAKNESS);
                        }

                } else
                    if (opt10 == 1) {
                        Functions.___message(p, "Trufitus looks at you blankly");
                        Functions.___npcTalk(p, n, "Surely you mean Minions?");
                        Functions.___playerTalk(p, n, "Yes of course, I mean Minions, what made you think I said Onions?");
                        Functions.___message(p, "Trufitus frowns at you but continues about...minions...");
                        Functions.___npcTalk(p, n, "Minions are the fiendish undead creatures that Rashiliyia controls.", "She has very few living worshippers, but they need to be dealt with at some point", "Usually a strong creature of some sort will be guarding the bones", "And it is not to be tackled lightly");
                        int opt11 = Functions.___showMenu(p, n, "Thanks for the information!", "Does she have any weaknesses?");
                        if (opt11 == 0) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.THANKS_FOR_THE_INFORMATION);
                        } else
                            if (opt11 == 1) {
                                trufitisChat(p, n, Jungle_Potion.Trufitus.WEAKNESS);
                            }

                    }

                break;
            case Jungle_Potion.Trufitus.SHOW_ME_TEMPLE_ITEMS :
                Functions.___npcTalk(p, n, "Well, just let me see the item and I'll help as much as I can.");
                if (p.getQuestStage(Quests.SHILO_VILLAGE) >= 6) {
                    int optTemp = Functions.___showMenu(p, n, "I need help with Zadimus.", "I need help with Bervirius.", "I need help with Rashliyia.", "I need some help with the Temple of Ah Za Rhoon.", "Ok, thanks!");
                    if (optTemp == 0) {
                        trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_BERVIRIUS);
                    } else
                        if (optTemp == 1) {
                            trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_RASH);
                        } else
                            if (optTemp == 2) {
                                trufitisChat(p, n, Jungle_Potion.Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE);
                            } else
                                if (optTemp == 3) {
                                    Functions.___npcTalk(p, n, "You're quite welcome Bwana.");
                                }



                    return;
                }
                // no stone-plaque in bank or inventory
                if ((!p.getBank().hasItemId(ItemId.STONE_PLAQUE.id())) && (!p.getInventory().hasItemId(ItemId.STONE_PLAQUE.id()))) {
                    Functions.___npcTalk(p, n, "Look for something that can identify the place.", "Leave no stone unturned.");
                } else {
                    Functions.___npcTalk(p, n, "We need to identify that the place you have found", "is indeed Ah Za Rhoon.");
                }
                // player has not explored inner Ah Za Rhoon
                if (!p.getCache().hasKey("obtained_shilo_info")) {
                    Functions.___npcTalk(p, n, "Look for details of Rashiliyias Kin, these may be well hidden.", "There is a legend about Rashiliyia, look for it in the temple.", "Look for something relating to Zadimus at the temple.", "And best of luck!");
                } else {
                    Functions.___npcTalk(p, n, "Any scrolls or information about Rashiliyias Kin would be helpful", "Have you got any items concerning Rashiliyia?", "If so, please show me them.", "There must be something relating to Zadimus at the temple", "Did you find anything? If so, let me see it.", "And best of luck!");
                }
                break;
            case Jungle_Potion.Trufitus.KEYS_AND_KIN :
                Functions.___npcTalk(p, n, "Hmmm, maybe it's a clue of some kind?", "Well, Rashiliyias only kin, Bervirius, is entombed", "on a small island which lies to the South West.", "I will do some research into this as well.", "But I think we must take this clue literally", "and get some item that belonged to Bervirius", "as it may be the only way to approach Rashiliyia.");
                if (p.getQuestStage(Quests.SHILO_VILLAGE) == 4) {
                    p.updateQuestStage(Quests.SHILO_VILLAGE, 5);
                }
                break;
        }
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.TRUFITUS.id()) {
                        trufitisChat(p, n, -1);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return (((obj.getID() == Jungle_Potion.QuestObjects.Snake_Jungle_Vine) || Functions.isObject(obj, Jungle_Potion.QuestObjects.Ardrigal_Palm_Tree)) || Functions.isObject(obj, Jungle_Potion.QuestObjects.Sito_Scorched_Earth)) || Functions.isObject(obj, Jungle_Potion.QuestObjects.Volencia_Rocks);
    }

    // herbs should only be obtainable if player is assigned to find them, must pass with
    // Trufitus to drop trick, unless the player is on the legends quest (for snakes weed + ardrigal)
    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.isObject(obj, Jungle_Potion.QuestObjects.Snake_Jungle_Vine)) {
                        if ((!Functions.atQuestStage(p, quest, 1)) && (p.getQuestStage(Quests.LEGENDS_QUEST) == 0)) {
                            p.message("Yep, it looks like a vine...");
                            return null;
                        }
                        if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 1) && (p.getQuestStage(Quests.LEGENDS_QUEST) <= 6)) {
                            p.message("Yep, it looks like a vine...");
                            return null;
                        }
                        if (((!Functions.hasItem(p, ItemId.UNIDENTIFIED_SNAKE_WEED.id())) && (!Functions.hasItem(p, ItemId.SNAKE_WEED.id()))) && ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 6) || ((!hasCacheKeySetTrue(p, "got_snake_weed")) && Functions.atQuestStage(p, quest, 1)))) {
                            Functions.___message(p, "Small amounts of a herb are growing near this vine");
                            Functions.createGroundItem(ItemId.UNIDENTIFIED_SNAKE_WEED.id(), 1, obj.getX(), obj.getY(), p);
                            if (Functions.atQuestStage(p, quest, 1)) {
                                p.getCache().store("got_snake_weed", true);
                            }
                        } else {
                            p.message("Yep, it looks like a vine...");
                        }
                    } else
                        if (Functions.isObject(obj, Jungle_Potion.QuestObjects.Ardrigal_Palm_Tree)) {
                            if ((p.getQuestStage(quest) < 1) && (p.getQuestStage(Quests.LEGENDS_QUEST) == 0)) {
                                p.message("You find nothing of interest this time, sorry!");
                                return null;
                            }
                            if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 1) && (p.getQuestStage(Quests.LEGENDS_QUEST) <= 6)) {
                                p.message("You find nothing of interest this time, sorry!");
                                return null;
                            }
                            if (((!Functions.hasItem(p, ItemId.UNIDENTIFIED_ARDRIGAL.id())) && (!Functions.hasItem(p, ItemId.ARDRIGAL.id()))) && ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 6) || ((!hasCacheKeySetTrue(p, "got_ardigal")) && Functions.atQuestStage(p, quest, 2)))) {
                                Functions.___message(p, "You find a herb plant growing at the base of the palm");
                                Functions.createGroundItem(ItemId.UNIDENTIFIED_ARDRIGAL.id(), 1, obj.getX(), obj.getY(), p);
                                if (Functions.atQuestStage(p, quest, 2)) {
                                    p.getCache().store("got_ardigal", true);
                                }
                            } else {
                                p.message("You find nothing of interest this time, sorry!");
                            }
                        } else
                            if (Functions.isObject(obj, Jungle_Potion.QuestObjects.Sito_Scorched_Earth)) {
                                if ((((!Functions.hasItem(p, ItemId.UNIDENTIFIED_SITO_FOIL.id())) && (!Functions.hasItem(p, ItemId.SITO_FOIL.id()))) && (!hasCacheKeySetTrue(p, "got_sito_foil"))) && Functions.atQuestStage(p, quest, 3)) {
                                    Functions.___message(p, "A small herb plant is growing in the scorched soil.");
                                    Functions.createGroundItem(ItemId.UNIDENTIFIED_SITO_FOIL.id(), 1, obj.getX(), obj.getY(), p);
                                    p.getCache().store("got_sito_foil", true);
                                } else {
                                    p.message("You just find scorched earth.");
                                }
                            } else
                                if (Functions.isObject(obj, Jungle_Potion.QuestObjects.Volencia_Rocks)) {
                                    if ((((!Functions.hasItem(p, ItemId.UNIDENTIFIED_VOLENCIA_MOSS.id())) && (!Functions.hasItem(p, ItemId.VOLENCIA_MOSS.id()))) && (!hasCacheKeySetTrue(p, "got_volencia_moss"))) && Functions.atQuestStage(p, quest, 4)) {
                                        Functions.___message(p, "Small amounts of herb moss are growing at the base of this rock");
                                        Functions.createGroundItem(ItemId.UNIDENTIFIED_VOLENCIA_MOSS.id(), 1, obj.getX(), obj.getY(), p);
                                        p.getCache().store("got_volencia_moss", true);
                                    } else {
                                        p.message("You find nothing of interest.");
                                    }
                                }



                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
        return obj.getID() == Jungle_Potion.QuestObjects.Rogues_Purse_Wall;
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.isObject(obj, Jungle_Potion.QuestObjects.Rogues_Purse_Wall)) {
                        if ((((!Functions.hasItem(p, ItemId.UNIDENTIFIED_ROGUES_PURSE.id())) && (!Functions.hasItem(p, ItemId.ROGUES_PURSE.id()))) && (!hasCacheKeySetTrue(p, "got_rogues_purse"))) && Functions.atQuestStage(p, quest, 5)) {
                            Functions.___message(p, "Small amounts of herb fungus are growing at the base of this cavern wall");
                            Functions.createGroundItem(ItemId.UNIDENTIFIED_ROGUES_PURSE.id(), 1, p.getX(), p.getY(), p);
                            p.getCache().store("got_rogues_purse", true);
                        } else
                            p.message("You find nothing of interest.");

                    }
                    return null;
                });
            }
        };
    }

    private boolean hasCacheKeySetTrue(Player p, String key) {
        return p.getCache().hasKey(key) && p.getCache().getBoolean(key);
    }

    public class Trufitus {
        public static final int WHAT_DO_YOU_KNOW_ABOUT_MOSEL_REI = 0;

        public static final int WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA = 1;

        public static final int SOMETHING_ABOUT_A_LEGEND = 2;

        public static final int MORE_ABOUT_THE_TEMPLE = 3;

        public static final int EVACUATE_ISLAND = 4;

        public static final int THANKS_FOR_THE_INFORMATION = 5;

        public static final int AH_ZA_RHOON = 6;

        public static final int RESTING_PLACE = 7;

        public static final int OH_OK = 8;

        public static final int WEAKNESS = 9;

        public static final int SHOW_ME_TEMPLE_ITEMS = 10;

        public static final int KEYS_AND_KIN = 11;

        public static final int ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE = 12;

        public static final int HELP_WITH_RASH = 13;

        public static final int HELP_WITH_ZADIMUS = 14;

        public static final int HELP_WITH_BERVIRIUS = 15;

        public static final int HELP_WITH_AH_ZA_RHOON_TEMPLE = 16;

        public static final int DIDNT_FIND_ANYTHING_IN_THE_TOMB = 17;
    }

    class QuestObjects {
        public static final int Snake_Jungle_Vine = 564;

        public static final int Ardrigal_Palm_Tree = 553;

        public static final int Sito_Scorched_Earth = 554;

        public static final int Volencia_Rocks = 555;

        public static final int Rogues_Purse_Wall = 151;
    }
}

