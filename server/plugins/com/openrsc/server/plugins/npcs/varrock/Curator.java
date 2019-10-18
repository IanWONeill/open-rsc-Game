package com.openrsc.server.plugins.npcs.varrock;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import java.util.concurrent.Callable;


public class Curator implements InvUseOnNpcListener , TalkToNpcListener , InvUseOnNpcExecutiveListener , TalkToNpcExecutiveListener {
    public boolean blockTalkToNpc(final Player player, final Npc npc) {
        return npc.getID() == NpcId.CURATOR.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Welcome to the museum of Varrock");
                    if (p.getInventory().hasItemId(ItemId.BROKEN_SHIELD_ARRAV_1.id()) && p.getInventory().hasItemId(ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
                        // curator authentically does not check if you already have a certificate in your inventory before triggering this
                        if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5) {
                            Functions.___playerTalk(p, n, "I have retrieved the shield of Arrav and I would like to claim my reward");
                            Functions.___npcTalk(p, n, "The shield of Arrav?", "Let me see that");
                            Functions.___message(p, "The curator peers at the shield");
                            Functions.___npcTalk(p, n, "This is incredible", "That shield has been missing for about twenty five years", "Well give me the shield", "And I'll write you out a certificate", "Saying you have returned the shield", "So you can claim your reward from the king");
                            Functions.___playerTalk(p, n, "Can I have two certificates?", "I needed significant help from a friend to get the shield", "We'll split the reward");
                            Functions.___npcTalk(p, n, "Oh ok");
                            Functions.___message(p, "You hand over the shield parts");
                            Functions.removeItem(p, ItemId.BROKEN_SHIELD_ARRAV_1.id(), 1);
                            Functions.removeItem(p, ItemId.BROKEN_SHIELD_ARRAV_2.id(), 1);
                            Functions.___message(p, "The curator writes out two certificates");
                            Functions.addItem(p, ItemId.CERTIFICATE.id(), 1);
                            Functions.addItem(p, ItemId.CERTIFICATE.id(), 1);
                            Functions.___npcTalk(p, n, "Take these to the king", "And he'll pay you both handsomely");
                            return null;
                        }
                    } else
                        if (p.getInventory().hasItemId(ItemId.BROKEN_SHIELD_ARRAV_1.id()) || p.getInventory().hasItemId(ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
                            if ((p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5) || (p.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0)) {
                                // possible this triggers always, but confirmed that it does occur authentically after the quest is complete. (state < 0)
                                Functions.___playerTalk(p, n, "I have half the shield of Arrav here", "Can I get a reward");
                                Functions.___npcTalk(p, n, "Well it might be worth a small reward", "The entire shield would me worth much much more");
                                Functions.___playerTalk(p, n, "Ok I'll hang onto it", "And see if I can find the other half");
                                return null;
                            }
                        }

                    Menu defaultMenu = new Menu();
                    defaultMenu.addOption(new Option("Have you any interesting news?") {
                        @Override
                        public void action() {
                            Functions.___npcTalk(p, n, "No, I'm only interested in old stuff");
                        }
                    });
                    defaultMenu.addOption(new Option("Do you know where I could find any treasure?") {
                        @Override
                        public void action() {
                            Functions.___npcTalk(p, n, "This museum is full of treasures");
                            Functions.___playerTalk(p, n, "No, I meant treasures for me");
                            Functions.___npcTalk(p, n, "Any treasures this museum knows about", "It aquires");
                        }
                    });
                    defaultMenu.showMenu(p);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnNpc(Player p, Npc n, Item item) {
        if ((n.getID() == NpcId.CURATOR.id()) && ((((item.getID() == ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()) || (item.getID() == ItemId.LEVEL_1_CERTIFICATE.id())) || (item.getID() == ItemId.LEVEL_2_CERTIFICATE.id())) || (item.getID() == ItemId.LEVEL_3_CERTIFICATE.id()))) {
            return true;
        }
        return false;
    }

    @Override
    public GameStateEvent onInvUseOnNpc(Player p, Npc n, Item item) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.CURATOR.id()) {
                        if (item.getID() == ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()) {
                            Functions.___playerTalk(p, n, "I have been given this by the examiner at the digsite", "Can you stamp this for me ?");
                            Functions.___npcTalk(p, n, "What have we here ?", "A letter of recommendation indeed", "Normally I wouldn't do this", "But in this instance I don't see why not", "There you go, good luck student...");
                            Functions.removeItem(p, ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id(), 1);
                            Functions.addItem(p, ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id(), 1);
                            Functions.___npcTalk(p, n, "Be sure to come back and show me your certificates", "I would like to see how you get on");
                            Functions.___playerTalk(p, n, "Okay, I will, thanks, see you later");
                        } else
                            if (item.getID() == ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id()) {
                                Functions.___npcTalk(p, n, "No, I don't want it back, thankyou");
                            } else
                                if (item.getID() == ItemId.LEVEL_1_CERTIFICATE.id()) {
                                    Functions.___playerTalk(p, n, "Look what I have been awarded");
                                    Functions.removeItem(p, ItemId.LEVEL_1_CERTIFICATE.id(), 1);
                                    Functions.___npcTalk(p, n, "Well that's great, well done", "I'll take that for safekeeping", "Come and tell me when you are the next level");
                                } else
                                    if (item.getID() == ItemId.LEVEL_2_CERTIFICATE.id()) {
                                        Functions.___playerTalk(p, n, "Look, I am level 2 now...");
                                        Functions.___npcTalk(p, n, "Excellent work!");
                                        Functions.removeItem(p, ItemId.LEVEL_2_CERTIFICATE.id(), 1);
                                        Functions.___npcTalk(p, n, "I'll take that for safekeeping", "Remember to come and see me when you have graduated");
                                    } else
                                        if (item.getID() == ItemId.LEVEL_3_CERTIFICATE.id()) {
                                            Functions.___playerTalk(p, n, "Look at this certificate, curator...");
                                            Functions.___npcTalk(p, n, "Well well, a level 3 graduate!", "I'll keep your certificate safe for you", "I feel I must reward you for your work...", "What would you prefer, something to eat or drink ?");
                                            int menu = Functions.___showMenu(p, n, "Something to eat please", "Something to drink please");
                                            if (menu == 0) {
                                                Functions.removeItem(p, ItemId.LEVEL_3_CERTIFICATE.id(), 1);
                                                Functions.___npcTalk(p, n, "Very good, come and eat this cake I baked");
                                                Functions.___playerTalk(p, n, "Yum, thanks!");
                                                Functions.addItem(p, ItemId.CHOCOLATE_CAKE.id(), 1);
                                            } else
                                                if (menu == 1) {
                                                    Functions.removeItem(p, ItemId.LEVEL_3_CERTIFICATE.id(), 1);
                                                    Functions.___npcTalk(p, n, "Certainly, have this...");
                                                    Functions.addItem(p, ItemId.FRUIT_BLAST.id(), 1);
                                                    Functions.___playerTalk(p, n, "A cocktail ?");
                                                    Functions.___npcTalk(p, n, "It's a new recipie from the gnome kingdom", "You'll like it I'm sure");
                                                    Functions.___playerTalk(p, n, "Cheers!");
                                                    Functions.___npcTalk(p, n, "Cheers!");
                                                }

                                        }




                    }
                    return null;
                });
            }
        };
    }
}

