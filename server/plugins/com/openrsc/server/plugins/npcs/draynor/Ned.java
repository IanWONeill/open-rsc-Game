package com.openrsc.server.plugins.npcs.draynor;


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
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public final class Ned implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.NED.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Why hello there, me friends call me Ned", "I was a man of the sea, but its past me now", "Could I be making or selling you some Rope?");
                    String[] menu = new String[]{ // Default Menu
                    "Yes, I would like some Rope", "No thanks Ned, I don't need any" };
                    if ((p.getQuestStage(Quests.DRAGON_SLAYER) == 2) && (!p.getCache().hasKey("ned_hired"))) {
                        if (p.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 2) {
                            menu = new String[]{ // Dragon Slayer + Prince Ali Rescue
                            "You're a sailor? Could you take me to the Isle of Crandor", "Yes, I would like some Rope", "Ned, could you make other things from wool?", "No thanks Ned, I don't need any" };
                            int choice = Functions.___showMenu(p, n, menu);
                            makeChoice(p, n, choice);
                        } else {
                            menu = new String[]{ // Dragon Slayer
                            "You're a sailor? Could you take me to the Isle of Crandor", "Yes, I would like some Rope", "No thanks Ned, I don't need any" };
                            int choice = Functions.___showMenu(p, n, menu);
                            if (choice >= 2)
                                makeChoice(p, n, 3);
                            else
                                makeChoice(p, n, choice);

                        }
                    } else
                        if (p.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 2) {
                            menu = new String[]{ // Prince Ali Rescue
                            "Yes, I would like some Rope", "Ned, could you make other things from wool?", "No thanks Ned, I don't need any" };
                            int choice = Functions.___showMenu(p, n, menu);
                            if (choice >= 0) {
                                makeChoice(p, n, choice + 1);
                            }
                        } else {
                            int choice = Functions.___showMenu(p, n, menu);
                            if (choice == 0)
                                makeChoice(p, n, 1);
                            else
                                if (choice == 1)
                                    makeChoice(p, n, 3);


                        }

                    return null;
                });
            }
        };
    }

    public void makeChoice(Player p, Npc n, int option) {
        if (option == 0) {
            // Dragon Slayer
            Functions.___npcTalk(p, n, "Well I was a sailor", "I've not been able to get work at sea these days though", "They say I am too old");
            Functions.___message(p, "There is a wistfull look in Ned's eyes");
            Functions.___npcTalk(p, n, "I miss those days", "If you could get me a ship I would take you anywhere");
            if (p.getCache().hasKey("ship_fixed")) {
                Functions.___playerTalk(p, n, "As it happens I do have a ship ready to sail");
                Functions.___npcTalk(p, n, "That'd be grand, where is it");
                Functions.___playerTalk(p, n, "It's called the Lumbridge Lady and it's docked in Port Sarim");
                Functions.___npcTalk(p, n, "I'll go right over there and check her out then", "See you over there");
                p.getCache().store("ned_hired", true);
            } else {
                Functions.___playerTalk(p, n, "I will work on finding a sea worthy ship then");
            }
        } else
            if (option == 1) {
                // Buy Rope
                Functions.___npcTalk(p, n, "Well, I can sell you some rope for 15 coins", "Or I can be making you some if you gets me 4 balls of wool", "I strands them together I does, makes em strong");
                int choice;
                if (!Functions.hasItem(p, ItemId.BALL_OF_WOOL.id(), 4)) {
                    choice = // do not send over
                    Functions.___showMenu(p, n, false, "Okay, please sell me some Rope", "Thats a little more than I want to pay", "I will go and get some wool");
                } else {
                    choice = // do not send over
                    Functions.___showMenu(p, n, false, "Okay, please sell me some Rope", "Thats a little more than I want to pay", "I have some balls of wool. could you make me some Rope?");
                }
                if (choice == 0) {
                    if (p.getInventory().countId(ItemId.COINS.id()) < 15) {
                        p.message("You Don't have enough coins to buy any rope!");
                    } else {
                        Functions.___playerTalk(p, n, "Okay, please sell me some Rope");
                        p.message("You hand Ned 15 coins");
                        Functions.___npcTalk(p, n, "There you go, finest rope in Runescape");
                        p.getInventory().add(new Item(ItemId.ROPE.id(), 1));
                        p.getInventory().remove(ItemId.COINS.id(), 15);
                        p.message("Ned gives you a coil of rope");
                    }
                } else
                    if (choice == 1) {
                        Functions.___playerTalk(p, n, "Thats a little more than I want to pay");
                        Functions.___npcTalk(p, n, "Well, if you ever need rope. thats the price. sorry", "An old sailor needs money for a little drop o rum.");
                    } else
                        if (choice == 2) {
                            if (!Functions.hasItem(p, ItemId.BALL_OF_WOOL.id(), 4)) {
                                Functions.___playerTalk(p, n, "I will go and get some wool");
                                Functions.___npcTalk(p, n, "Aye, you do that", "Remember, it takes 4 balls of wool to make strong rope");
                            } else {
                                Functions.___playerTalk(p, n, "I have some balls of wool. could you make me some Rope?");
                                Functions.___npcTalk(p, n, "Sure I can.");
                                p.getInventory().add(new Item(ItemId.ROPE.id(), 1));
                                p.getInventory().remove(ItemId.BALL_OF_WOOL.id(), 4);
                            }
                        }


            } else
                if (option == 2) {
                    // Prince Ali's Rescue
                    Functions.___npcTalk(p, n, "I am sure I can. What are you thinking of?");
                    int wool_menu = Functions.___showMenu(p, n, "Could you knit me a sweater?", "How about some sort of a wig?", "Could you repair the arrow holes in the back of my shirt?");
                    if (wool_menu == 0) {
                        Functions.___npcTalk(p, n, "Do I look like a member of a sewing circle?", "Be off wi' you, I have fought monsters that would turn your hair blue", "I don't need to be laughed at just 'cos I am getting a bit old");
                    } else
                        if (wool_menu == 1) {
                            Functions.___npcTalk(p, n, "Well... Thats an interesting thought", "yes, I think I could do something", "Give me 3 balls of wool and I might be able to do it");
                            if (p.getInventory().countId(ItemId.BALL_OF_WOOL.id()) >= 3) {
                                int choice = Functions.___showMenu(p, n, "I have that now. Please, make me a wig", "I will come back when I need you to make me one");
                                if (choice == 0) {
                                    Functions.___npcTalk(p, n, "Okay, I will have a go.");
                                    Functions.___message(p, "You hand Ned 3 balls of wool", "Ned works with the wool. His hands move with a speed you couldn't imagine");
                                    p.getInventory().remove(ItemId.BALL_OF_WOOL.id(), 3);
                                    Functions.___npcTalk(p, n, "Here you go, hows that for a quick effort? Not bad I think!");
                                    p.message("Ned gives you a pretty good wig");
                                    Functions.addItem(p, ItemId.WOOL_WIG.id(), 1);
                                    Functions.___playerTalk(p, n, "Thanks Ned, theres more to you than meets the eye");
                                } else
                                    if (choice == 1) {
                                        Functions.___npcTalk(p, n, "Well, it sounds like a challenge", "come to me if you need one");
                                    }

                            } else {
                                Functions.___playerTalk(p, n, "great, I will get some. I think a wig would be useful");
                            }
                        } else
                            if (wool_menu == 2) {
                                Functions.___npcTalk(p, n, "Ah yes, its a tough world these days", "Theres a few brave enough to attack from 10 metres away");
                                p.message("Ned pulls out a needle and attacks your shirt");
                                Functions.___npcTalk(p, n, "There you go, good as new");
                                Functions.___playerTalk(p, n, "Thanks Ned, maybe next time they will attack me face to face");
                            }


                } else
                    if (option == 3) {
                        // No thanks
                        Functions.___npcTalk(p, n, "Well, old Neddy is always here if you do", "Tell your friends, I can always be using the business");
                    }



    }
}

