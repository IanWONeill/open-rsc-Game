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
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public final class Baraek implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    int menu;
                    boolean bargained = false;
                    boolean hasFur = p.getInventory().hasItemId(ItemId.FUR.id());
                    if (canGetInfoGang(p) && hasFur) {
                        menu = // do not send over
                        Functions.___showMenu(p, n, false, "Can you tell me where I can find the phoenix gang?", "Can you sell me some furs?", "Hello. I am in search of a quest", "Would you like to buy my fur?");
                    } else
                        if (canGetInfoGang(p) && (!hasFur)) {
                            menu = // do not send over
                            Functions.___showMenu(p, n, false, "Can you tell me where I can find the phoenix gang?", "Can you sell me some furs?", "Hello. I am in search of a quest");
                        } else
                            if (hasFur) {
                                menu = // do not send over
                                Functions.___showMenu(p, n, false, "Can you sell me some furs?", "Hello. I am in search of a quest", "Would you like to buy my fur?");
                                if (menu >= 0) {
                                    menu += 1;
                                }
                            } else {
                                menu = // do not send over
                                Functions.___showMenu(p, n, false, "Can you sell me some furs?", "Hello. I am in search of a quest");
                                if (menu >= 0) {
                                    menu += 1;
                                }
                            }


                    if (menu == 0) {
                        Functions.___playerTalk(p, n, "Can you tell me where I can find the phoenix gang?");
                        Functions.___npcTalk(p, n, "Sh Sh, not so loud", "You don't want to get me in trouble");
                        Functions.___playerTalk(p, n, "So do you know where they are?");
                        Functions.___npcTalk(p, n, "I may do", "Though I don't want to get into trouble for revealing their hideout", "Now if I was say 20 gold coins richer", "I may happen to be more inclined to take that sort of risk");
                        int sub_menu = Functions.___showMenu(p, n, "Okay have 20 gold coins", "No I don't like things like bribery", "Yes I'd like to be 20 gold coins richer too");
                        if (sub_menu == 0) {
                            if (!Functions.hasItem(p, ItemId.COINS.id(), 20)) {
                                Functions.___playerTalk(p, n, "Oops. I don't have 20 coins. Silly me.");
                            } else {
                                Functions.removeItem(p, ItemId.COINS.id(), 20);
                                Functions.___npcTalk(p, n, "Cheers", "Ok to get to the gang hideout", "After entering Varrock through the south gate", "If you take the first turning east", "Somewhere along there is an alleyway to the south", "The door at the end of there is the entrance to the phoenix gang", "They're operating there under the name of the VTAM corporation", "Be careful", "The phoenix gang ain't the types to be messed with");
                                Functions.___playerTalk(p, n, "Thanks");
                                if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 2) {
                                    p.updateQuestStage(Quests.SHIELD_OF_ARRAV, 3);
                                }
                            }
                        } else
                            if (sub_menu == 1) {
                                Functions.___npcTalk(p, n, "Heh, if you wanna deal with the phoenix gang", "They're involved in much worse than a bit of bribery");
                            } else
                                if (sub_menu == 2) {
                                    // nothing
                                }


                    } else
                        if (menu == 1) {
                            Functions.___playerTalk(p, n, "Can you sell me some furs?");
                            Functions.___npcTalk(p, n, "Yeah sure they're 20 gold coins a piece");
                            int opts = // do not send over
                            Functions.___showMenu(p, n, false, "Yeah, okay here you go", "20 gold coins thats an outrage");
                            if (opts == 0) {
                                if (!Functions.hasItem(p, ItemId.COINS.id(), 20)) {
                                    Functions.___playerTalk(p, n, "Oh dear I don't seem to have enough money");
                                    Functions.___npcTalk(p, n, "Well, okay I'll go down to 18 coins");
                                    bargained = true;
                                } else {
                                    Functions.___playerTalk(p, n, "Yeah okay here you go");
                                    p.getInventory().remove(ItemId.COINS.id(), 20);
                                    p.message("You buy a fur from Baraek");
                                    p.getInventory().add(new Item(ItemId.FUR.id()));
                                }
                            } else
                                if (opts == 1) {
                                    Functions.___playerTalk(p, n, "20 gold coins that's an outrage");
                                    Functions.___npcTalk(p, n, "Well, okay I'll go down to 18");
                                    bargained = true;
                                }

                        } else
                            if (menu == 2) {
                                Functions.___playerTalk(p, n, "Hello I am in search of a quest");
                                Functions.___npcTalk(p, n, "Sorry kiddo, I'm a fur trader not a damsel in distress");
                            } else
                                if (menu == 3) {
                                    Functions.___playerTalk(p, n, "Would you like to buy my fur?");
                                    Functions.___npcTalk(p, n, "Lets have a look at it");
                                    p.message("Baraek examines a fur");
                                    Functions.___npcTalk(p, n, "It's not in the best of condition", "I guess I could give 12 coins to take it off your hands");
                                    int opts = Functions.___showMenu(p, n, "Yeah that'll do", "I think I'll keep hold of it actually");
                                    if (opts == 0) {
                                        Functions.___message(p, "You give Baraek a fur", "And he gives you twelve coins");
                                        Functions.removeItem(p, ItemId.FUR.id(), 1);
                                        Functions.addItem(p, ItemId.COINS.id(), 12);
                                    } else
                                        if (opts == 1) {
                                            Functions.___npcTalk(p, n, "Oh ok", "Didn't want it anyway");
                                        }

                                }



                    if (bargained) {
                        int sub_opts = // do not send over
                        Functions.___showMenu(p, n, false, "Okay here you go", "No thanks I'll leave it");
                        if (sub_opts == 0) {
                            if (!Functions.hasItem(p, ItemId.COINS.id(), 18)) {
                                Functions.___playerTalk(p, n, "Oh dear I don't seem to have enough money");
                                Functions.___npcTalk(p, n, "Well I can't go any cheaper than that mate", "I have a family to feed");
                            } else {
                                Functions.___playerTalk(p, n, "Okay here you go");
                                p.getInventory().remove(ItemId.COINS.id(), 18);
                                p.message("You buy a fur from Baraek");
                                p.getInventory().add(new Item(ItemId.FUR.id()));
                            }
                        } else
                            if (sub_opts == 1) {
                                Functions.___playerTalk(p, n, "No thanks, I'll leave it");
                                Functions.___npcTalk(p, n, "It's your loss mate");
                            }

                    }
                    return null;
                });
            }
        };
    }

    private boolean canGetInfoGang(Player p) {
        return (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 2) || ((p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 3) && (!p.getCache().hasKey("arrav_mission")));
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.BARAEK.id();
    }
}

