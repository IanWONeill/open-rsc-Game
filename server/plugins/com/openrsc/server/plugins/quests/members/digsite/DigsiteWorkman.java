package com.openrsc.server.plugins.quests.members.digsite;


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
import java.util.concurrent.Callable;


public class DigsiteWorkman implements InvUseOnNpcListener , TalkToNpcListener , InvUseOnNpcExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return (n.getID() == NpcId.WORKMAN.id()) || (n.getID() == NpcId.WORKMAN_UNDERGROUND.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.WORKMAN.id()) {
                        switch (p.getQuestStage(Quests.DIGSITE)) {
                            case -1 :
                                Functions.___playerTalk(p, n, "Hello there");
                                Functions.___npcTalk(p, n, "Ah it's the great archaeologist!", "Congratulations on your discovery");
                                break;
                            case 0 :
                            case 1 :
                            case 2 :
                            case 3 :
                            case 4 :
                            case 5 :
                            case 6 :
                                Functions.___playerTalk(p, n, "Hello there");
                                Functions.___npcTalk(p, n, "Good day, what can I do for you ?");
                                int menu = Functions.___showMenu(p, n, "What do you do here ?", "I'm not sure...", "Can I dig around here ?");
                                if (menu == 0) {
                                    Functions.___npcTalk(p, n, "I am involved in various stages of the dig", "From the initial investigation to the installation of the mine shafts");
                                    Functions.___playerTalk(p, n, "Oh okay, thanks");
                                } else
                                    if (menu == 1) {
                                        Functions.___npcTalk(p, n, "Well, let me know when you are");
                                    } else
                                        if (menu == 2) {
                                            Functions.___npcTalk(p, n, "You can only use the site you have the appropriate exam level for");
                                            int sub_menu = Functions.___showMenu(p, n, "Appropriate exam level ?", "I am already skilled in digging");
                                            if (sub_menu == 0) {
                                                Functions.___npcTalk(p, n, "Yes, only persons with the correct certificate of earth sciences can dig here", "A level 1 certificate will let you dig in a level 1 site and so on...");
                                                Functions.___playerTalk(p, n, "Oh, okay I understand");
                                            } else
                                                if (sub_menu == 1) {
                                                    Functions.___npcTalk(p, n, "Well that's nice for you...", "You can't dig around here without a certificate though");
                                                }

                                        }


                                break;
                        }
                    } else
                        if (n.getID() == NpcId.WORKMAN_UNDERGROUND.id()) {
                            Functions.___playerTalk(p, n, "Hello");
                            Functions.___npcTalk(p, n, "Well well...", "I have a visitor", "What are you doing here ?");
                            int menu = Functions.___showMenu(p, n, "I have been invited to research here", "I am not sure really", "I'm here to get rich rich rich!");
                            if (menu == 0) {
                                Functions.___npcTalk(p, n, "Indeed you must be someone special to be allowed down here...");
                                int opt1 = Functions.___showMenu(p, n, "Do you know where to find a specimen jar ?", "Do you know where to find a chest key");
                                if (opt1 == 0) {
                                    Functions.___npcTalk(p, n, "Hmmm, let me think...", "Nope, can't help you there i'm afraid");
                                } else
                                    if (opt1 == 1) {
                                        Functions.___npcTalk(p, n, "Yes I might have one...");
                                        int opt2 = Functions.___showMenu(p, n, "I don't suppose I could use it ?", "Can I buy it from you ?", "Hey that's my key!");
                                        if (opt2 == 0) {
                                            Functions.___npcTalk(p, n, "Aww, but I need it...");
                                            int opt3 = Functions.___showMenu(p, n, "Please", "Can I buy it from you ?", "Hey that's my key!");
                                            if (opt3 == 0) {
                                                Functions.___npcTalk(p, n, "I am not sure about this...");
                                                int opt4 = Functions.___showMenu(p, n, "Aww...go on", "Can I buy it from you ?", "Hey that's my key!");
                                                if (opt4 == 0) {
                                                    Functions.___npcTalk(p, n, "Hmmm...well I don't know");
                                                    int opt5 = Functions.___showMenu(p, n, "Pretty please!", "Can I buy it from you ?", "Hey that's my key!");
                                                    if (opt5 == 0) {
                                                        Functions.___npcTalk(p, n, "You are trying to change my mind");
                                                        Functions.___playerTalk(p, n, "Of course!");
                                                        int opt6 = Functions.___showMenu(p, n, "Pretty please with sugar on top!", "Can I buy it from you ?", "Hey that's my key!");
                                                        if (opt6 == 0) {
                                                            Functions.addItem(p, ItemId.DIGSITE_CHEST_KEY.id(), 1);
                                                            Functions.___npcTalk(p, n, "All right, all right!", "Stop begging I can't stand it.", "Here's the key...take care of it");
                                                            Functions.___playerTalk(p, n, "Thanks");
                                                        } else
                                                            if (opt6 == 1) {
                                                                canIBuyIt(p, n);
                                                            } else
                                                                if (opt6 == 2) {
                                                                    myKey(p, n);
                                                                }


                                                    } else
                                                        if (opt5 == 1) {
                                                            canIBuyIt(p, n);
                                                        } else
                                                            if (opt5 == 2) {
                                                                myKey(p, n);
                                                            }


                                                } else
                                                    if (opt4 == 1) {
                                                        canIBuyIt(p, n);
                                                    } else
                                                        if (opt4 == 2) {
                                                            myKey(p, n);
                                                        }


                                            } else
                                                if (opt3 == 1) {
                                                    canIBuyIt(p, n);
                                                } else
                                                    if (opt3 == 2) {
                                                        myKey(p, n);
                                                    }


                                        } else
                                            if (opt2 == 1) {
                                                canIBuyIt(p, n);
                                            } else
                                                if (opt2 == 2) {
                                                    myKey(p, n);
                                                }


                                    }

                            } else
                                if (menu == 1) {
                                    Functions.___npcTalk(p, n, "A miner without a clue - how funny");
                                } else
                                    if (menu == 2) {
                                        Functions.___npcTalk(p, n, "Oh, well don't forget that wealth and riches isn't everything...");
                                    }


                        }

                    return null;
                });
            }
        };
    }

    private void canIBuyIt(Player p, Npc n) {
        Functions.___npcTalk(p, n, "Ooo no, I need it!");
    }

    private void myKey(Player p, Npc n) {
        Functions.___npcTalk(p, n, "You don't think im going to fall for that do you ?", "Get lost!");
    }

    @Override
    public boolean blockInvUseOnNpc(Player p, Npc n, Item item) {
        return (n.getID() == NpcId.WORKMAN.id()) && (item.getID() == ItemId.DIGSITE_SCROLL.id());
    }

    @Override
    public GameStateEvent onInvUseOnNpc(Player p, Npc n, Item item) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((n.getID() == NpcId.WORKMAN.id()) && (item.getID() == ItemId.DIGSITE_SCROLL.id())) {
                        Functions.___playerTalk(p, n, "Here, have a look at this...");
                        Functions.___npcTalk(p, n, "I give permission...blah de blah etc....", "Okay that's all in order, you may use the mineshafts now", "I'll hang onto this scroll shall I ?");
                        Functions.___playerTalk(p, n, "Thanks");
                        Functions.removeItem(p, ItemId.DIGSITE_SCROLL.id(), 1);
                        if ((!p.getCache().hasKey("digsite_winshaft")) && (p.getQuestStage(Quests.DIGSITE) == 5)) {
                            p.getCache().store("digsite_winshaft", true);
                        }
                    }
                    return null;
                });
            }
        };
    }
}

