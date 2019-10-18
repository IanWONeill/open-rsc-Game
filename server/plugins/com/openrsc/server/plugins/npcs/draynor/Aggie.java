package com.openrsc.server.plugins.npcs.draynor;


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


public final class Aggie implements TalkToNpcListener , TalkToNpcExecutiveListener {
    private static final int SKIN_PASTE = 0;

    private static final int FROGS = 1;

    private static final int MADWITCH = 2;

    private static final int RED_DYE = 4;

    private static final int DONT_HAVE = 5;

    private static final int WITHOUT_DYE = 6;

    private static final int YELLOW_DYE = 7;

    private static final int BLUE_DYE = 8;

    private static final int DYES = 9;

    private static final int MAKEME = 10;

    private static final int HAPPY = 11;

    @Override
    public GameStateEvent onTalkToNpc(Player player, final Npc npc) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    aggieDialogue(player, npc, -1);
                    return null;
                });
            }
        };
    }

    public void aggieDialogue(Player p, Npc n, int cID) {
        if (cID == (-1)) {
            Functions.___npcTalk(p, n, "What can I help you with?");
            if (p.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 2) {
                int choice = Functions.___showMenu(p, n, "Could you think of a way to make pink skin paste", "What could you make for me", "Cool, do you turn people into frogs?", "You mad old witch you can't help me", "Can you make dyes for me please");
                if (choice == 0) {
                    aggieDialogue(p, n, Aggie.SKIN_PASTE);
                } else
                    if (choice == 1) {
                        aggieDialogue(p, n, Aggie.MAKEME);
                    } else
                        if (choice == 2) {
                            aggieDialogue(p, n, Aggie.FROGS);
                        } else
                            if (choice == 3) {
                                aggieDialogue(p, n, Aggie.MADWITCH);
                            } else
                                if (choice == 4) {
                                    aggieDialogue(p, n, Aggie.DYES);
                                }




            } else {
                int choiceOther = Functions.___showMenu(p, n, "What could you make for me", "Cool, do you turn people into frogs?", "You mad old witch you can't help me", "Can you make dyes for me please");
                if (choiceOther == 0) {
                    aggieDialogue(p, n, Aggie.MAKEME);
                } else
                    if (choiceOther == 1) {
                        aggieDialogue(p, n, Aggie.FROGS);
                    } else
                        if (choiceOther == 2) {
                            aggieDialogue(p, n, Aggie.MADWITCH);
                        } else
                            if (choiceOther == 3) {
                                aggieDialogue(p, n, Aggie.DYES);
                            }



            }
            return;
        }
        switch (cID) {
            case Aggie.DYES :
                Functions.___npcTalk(p, n, "What sort of dye would you like? Red, yellow or Blue?");
                int menu13 = Functions.___showMenu(p, n, "What do you need to make some red dye please", "What do you need to make some yellow dye please", "What do you need to make some blue dye please", "No thanks, I am happy the colour I am");
                if (menu13 == 0) {
                    aggieDialogue(p, n, Aggie.RED_DYE);
                } else
                    if (menu13 == 1) {
                        aggieDialogue(p, n, Aggie.YELLOW_DYE);
                    } else
                        if (menu13 == 2) {
                            aggieDialogue(p, n, Aggie.BLUE_DYE);
                        } else
                            if (menu13 == 3) {
                                aggieDialogue(p, n, Aggie.HAPPY);
                            }



                break;
            case Aggie.SKIN_PASTE :
                if (((Functions.hasItem(p, ItemId.ASHES.id()) && (Functions.hasItem(p, ItemId.POT_OF_FLOUR.id()) || Functions.hasItem(p, ItemId.FLOUR.id()))) && (Functions.hasItem(p, ItemId.BUCKET_OF_WATER.id()) || Functions.hasItem(p, ItemId.JUG_OF_WATER.id()))) && Functions.hasItem(p, ItemId.REDBERRIES.id())) {
                    Functions.___npcTalk(p, n, "Yes I can, you have the ingredients for it already");
                    Functions.___npcTalk(p, n, "Would you like me to mix you some?");
                    int menu = // do not send over
                    Functions.___showMenu(p, n, false, "Yes please, mix me some skin paste", "No thankyou, I don't need paste");
                    if (menu == 0) {
                        Functions.___playerTalk(p, n, "Yes please, mix me some skin paste");
                        Functions.___npcTalk(p, n, "That should be simple, hand the things to Aggie then");
                        Functions.___message(p, "You hand ash, flour, water and redberries to Aggie", "She tips it into a cauldron and mutters some words");
                        Functions.removeItem(p, ItemId.ASHES.id(), 1);
                        if (!Functions.removeItem(p, ItemId.POT_OF_FLOUR.id(), 1))
                            Functions.removeItem(p, ItemId.FLOUR.id(), 1);

                        if (!Functions.removeItem(p, ItemId.BUCKET_OF_WATER.id(), 1))
                            Functions.removeItem(p, ItemId.JUG_OF_WATER.id(), 1);

                        Functions.removeItem(p, ItemId.REDBERRIES.id(), 1);
                        Functions.___npcTalk(p, n, "Tourniquet, Fenderbaum, Tottenham, MonsterMunch, MarbleArch");
                        Functions.___message(p, "Aggie hands you the skin paste");
                        Functions.addItem(p, ItemId.PASTE.id(), 1);
                        Functions.___npcTalk(p, n, "There you go dearie, your skin potion", "That will make you look good at the Varrock dances");
                    } else
                        if (menu == 1) {
                            Functions.___playerTalk(p, n, "No thank you, I don't need skin paste");
                            Functions.___npcTalk(p, n, "Okay dearie, thats always your choice");
                        }

                } else {
                    Functions.___npcTalk(p, n, "Why, its one of my most popular potions", "The women here, they like to have smooth looking skin", "(and I must admit, some of the men buy it too)", "I can make it for you, just get me whats needed");
                    Functions.___playerTalk(p, n, "What do you need to make it?");
                    Functions.___npcTalk(p, n, "Well deary, you need a base for the paste", "That's a mix of ash, flour and water", "Then you need red berries to colour it as you want", "bring me those four items and I will make you some");
                }
                break;
            case Aggie.FROGS :
                Functions.___npcTalk(p, n, "Oh, not for years, but if you meet a talking chicken,", "You have probably met the professor in the Manor north of here", "A few years ago it was flying fish, that machine is a menace");
                break;
            case Aggie.MADWITCH :
                Functions.___npcTalk(p, n, "Oh, you like to call a witch names, do you?");
                if (Functions.hasItem(p, ItemId.COINS.id(), 20)) {
                    Functions.___message(p, "Aggie waves her hands about, and you seem to be 20 coins poorer");
                    Functions.removeItem(p, ItemId.COINS.id(), 20);
                    Functions.___npcTalk(p, n, "Thats a fine for insulting a witch, you should learn some respect");
                } else
                    if (Functions.hasItem(p, ItemId.POT_OF_FLOUR.id())) {
                        Functions.___message(p, "Aggie waves her hands near you, and you seem to have lost some flour");
                        Functions.removeItem(p, ItemId.POT_OF_FLOUR.id(), 1);
                        Functions.___npcTalk(p, n, "Thankyou for your kind present of flour", "I am sure you never meant to insult me");
                    } else {
                        Functions.___npcTalk(p, n, "You should be careful about insulting a Witch", "You never know what shape you could wake up in");
                    }

                break;
            case Aggie.MAKEME :
                Functions.___npcTalk(p, n, "I mostly just make what I find pretty", "I sometimes make dye for the womens clothes, brighten the place up", "I can make red,yellow and blue dyes would u like some");
                int menu2 = Functions.___showMenu(p, n, "What do you need to make some red dye please", "What do you need to make some yellow dye please", "What do you need to make some blue dye please", "No thanks, I am happy the colour I am");
                if (menu2 == 0) {
                    aggieDialogue(p, n, Aggie.RED_DYE);
                } else
                    if (menu2 == 1) {
                        aggieDialogue(p, n, Aggie.YELLOW_DYE);
                    } else
                        if (menu2 == 2) {
                            aggieDialogue(p, n, Aggie.BLUE_DYE);
                        } else
                            if (menu2 == 3) {
                                aggieDialogue(p, n, Aggie.HAPPY);
                            }



                break;
            case Aggie.YELLOW_DYE :
                Functions.___npcTalk(p, n, "Yellow is a strange colour to get, comes from onion skins", "I need 2 onions, and 5 coins to make yellow");
                int menu4 = // do not send over
                Functions.___showMenu(p, n, false, "Okay, make me some yellow dye please", "I don't think I have all the ingredients yet", "I can do without dye at that price");
                if (menu4 == 0) {
                    if (!Functions.hasItem(p, ItemId.ONION.id(), 2)) {
                        Functions.___message(p, "You don't have enough onions to make the yellow dye!");
                    } else
                        if (!Functions.hasItem(p, ItemId.COINS.id(), 5)) {
                            Functions.___message(p, "You don't have enough coins to pay for the dye!");
                        } else {
                            Functions.___playerTalk(p, n, "Okay, make me some yellow dye please");
                            Functions.___message(p, "You hand the onions and payment to Aggie");
                            p.getInventory().remove(ItemId.ONION.id(), 2);
                            Functions.removeItem(p, ItemId.COINS.id(), 5);
                            Functions.___message(p, "she takes a yellow bottle from nowhere and hands it to you");
                            Functions.addItem(p, ItemId.YELLOWDYE.id(), 1);
                        }

                } else
                    if (menu4 == 1) {
                        Functions.___playerTalk(p, n, "I don't think I have all the ingredients yet");
                        aggieDialogue(p, n, Aggie.DONT_HAVE);
                    } else
                        if (menu4 == 2) {
                            Functions.___playerTalk(p, n, "I can do without dye at that price");
                            aggieDialogue(p, n, Aggie.WITHOUT_DYE);
                        }


                break;
            case Aggie.RED_DYE :
                Functions.___npcTalk(p, n, "3 lots of Red berries, and 5 coins, to you");
                int menu3 = // do not send over
                Functions.___showMenu(p, n, false, "Okay, make me some red dye please", "I don't think I have all the ingredients yet", "I can do without dye at that price");
                if (menu3 == 0) {
                    if (!Functions.hasItem(p, ItemId.REDBERRIES.id(), 3)) {
                        Functions.___message(p, "You don't have enough berries to make the red dye!");
                    } else
                        if (!Functions.hasItem(p, ItemId.COINS.id(), 5)) {
                            Functions.___message(p, "You don't have enough coins to pay for the dye!");
                        } else {
                            Functions.___playerTalk(p, n, "Okay, make me some red dye please");
                            Functions.___message(p, "You hand the berries and payment to Aggie");
                            p.getInventory().remove(ItemId.REDBERRIES.id(), 3);
                            Functions.removeItem(p, ItemId.COINS.id(), 5);
                            Functions.___message(p, "she takes a red bottle from nowhere and hands it to you");
                            Functions.addItem(p, ItemId.REDDYE.id(), 1);
                        }

                } else
                    if (menu3 == 1) {
                        Functions.___playerTalk(p, n, "I don't think I have all the ingredients yet");
                        aggieDialogue(p, n, Aggie.DONT_HAVE);
                    } else
                        if (menu3 == 2) {
                            Functions.___playerTalk(p, n, "I can do without dye at that price");
                            aggieDialogue(p, n, Aggie.WITHOUT_DYE);
                        }


                break;
            case Aggie.BLUE_DYE :
                Functions.___npcTalk(p, n, "2 woad leaves, and 5 coins, to you");
                int menu6 = // do not send over
                Functions.___showMenu(p, n, false, "Okay, make me some blue dye please", "I don't think I have all the ingredients yet", "I can do without dye at that price");
                if (menu6 == 0) {
                    if (!Functions.hasItem(p, ItemId.WOAD_LEAF.id(), 2)) {
                        Functions.___message(p, "You don't have enough woad leaves to make the blue dye!");
                    } else
                        if (!Functions.hasItem(p, ItemId.COINS.id(), 5)) {
                            Functions.___message(p, "You don't have enough coins to pay for the dye!");
                        } else {
                            Functions.___playerTalk(p, n, "Okay, make me some blue dye please");
                            Functions.___message(p, "You hand the woad leaves and payment to Aggie");
                            p.getInventory().remove(ItemId.WOAD_LEAF.id(), 2);
                            Functions.removeItem(p, ItemId.COINS.id(), 5);
                            Functions.___message(p, "she takes a blue bottle from nowhere and hands it to you");
                            Functions.addItem(p, ItemId.BLUEDYE.id(), 1);
                        }

                } else
                    if (menu6 == 1) {
                        Functions.___playerTalk(p, n, "I don't think I have all the ingredients yet");
                        Functions.___playerTalk(p, n, "Where on earth am I meant to find woad leaves?");
                        Functions.___npcTalk(p, n, "I'm not entirely sure", "I used to go and nab the stuff from the public gardens in Falador", "It hasn't been growing there recently though");
                    } else
                        if (menu6 == 2) {
                            Functions.___playerTalk(p, n, "I can do without dye at that price");
                            aggieDialogue(p, n, Aggie.WITHOUT_DYE);
                        }


                break;
            case Aggie.DONT_HAVE :
                Functions.___npcTalk(p, n, "You know what you need to get now, come back when you have them", "goodbye for now");
                break;
            case Aggie.WITHOUT_DYE :
                Functions.___npcTalk(p, n, "Thats your choice, but I would think you have killed for less", "I can see it in your eyes");
                break;
            case Aggie.HAPPY :
                Functions.___npcTalk(p, n, "You are easily pleased with yourself then", "when you need dyes, come to me");
                break;
        }
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.AGGIE.id();
    }
}

