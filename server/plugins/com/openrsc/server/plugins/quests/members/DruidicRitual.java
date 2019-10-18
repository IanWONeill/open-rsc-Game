package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class DruidicRitual implements QuestInterface , InvUseOnObjectListener , TalkToNpcListener , WallObjectActionListener , InvUseOnObjectExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    @Override
    public int getQuestId() {
        return Quests.DRUIDIC_RITUAL;
    }

    @Override
    public String getQuestName() {
        return "Druidic ritual (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        p.message("Well done you have completed the druidic ritual quest");
        p.message("@gre@You haved gained 4 quest points!");
        Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.DRUIDIC_RITUAL), true);
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return (n.getID() == NpcId.KAQEMEEX.id()) || (n.getID() == NpcId.SANFEW.id());
    }

    private void kaqemeexDialogue(Player p, Npc n, int cID) {
        if (cID == (-1)) {
            switch (p.getQuestStage(this)) {
                case 0 :
                    Functions.___npcTalk(p, n, "What brings you to our holy Monument");
                    int first = Functions.___showMenu(p, n, "Who are you?", "I'm in search of a quest", "Did you build this?");
                    if (first == 0) {
                        Functions.___npcTalk(p, n, "We are the druids of Guthix", "We worship our God at our famous stone circles");
                        int third = Functions.___showMenu(p, n, "What about the stone circle full of dark wizards?", "So whats so good about Guthix", "Well I'll be on my way now");
                        if (third == 0) {
                            kaqemeexDialogue(p, n, DruidicRitual.kaqemeex.STONE_CIRCLE);
                        } else
                            if (third == 1) {
                                Functions.___npcTalk(p, n, "Guthix is very important to this world", "He is the God of nature and balance", "He is in the trees and he is in the rock");
                            } else
                                if (third == 2) {
                                    kaqemeexDialogue(p, n, DruidicRitual.kaqemeex.ON_MY_WAY_NOW);
                                }


                    } else
                        if (first == 1) {
                            kaqemeexDialogue(p, n, DruidicRitual.kaqemeex.SEARCH_OF_QUEST);
                        } else
                            if (first == 2) {
                                Functions.___npcTalk(p, n, "Well I didn't build it personally", "Our forebearers did", "The first druids of Guthix built many stone circles 800 years ago", "Only 2 that we know of remain", "And this is the only 1 we can use any more");
                                int second = Functions.___showMenu(p, n, "What about the stone circle full of dark wizards?", "I'm in search of a quest", "Well I'll be on my way now");
                                if (second == 0) {
                                    kaqemeexDialogue(p, n, DruidicRitual.kaqemeex.STONE_CIRCLE);
                                } else
                                    if (second == 1) {
                                        kaqemeexDialogue(p, n, DruidicRitual.kaqemeex.SEARCH_OF_QUEST);
                                    } else
                                        if (second == 2) {
                                            kaqemeexDialogue(p, n, DruidicRitual.kaqemeex.ON_MY_WAY_NOW);
                                        }


                            }


                    break;
                case 1 :
                case 2 :
                    Functions.___playerTalk(p, n, "Hello again");
                    Functions.___npcTalk(p, n, "You need to speak to Sanfew in the village south of here", "To continue with your quest");
                    break;
                case 3 :
                    Functions.___npcTalk(p, n, "I've heard you were very helpful to Sanfew");
                    Functions.___npcTalk(p, n, "I will teach you the herblaw you need to know now");
                    p.sendQuestComplete(Quests.DRUIDIC_RITUAL);
                    break;
                case -1 :
                    Functions.___npcTalk(p, n, "Hello how is the herblaw going?");
                    int endMenu = Functions.___showMenu(p, n, "Very well thankyou", "I need more practice at it");
                    if (endMenu == 0) {
                        // NOTHING
                    } else
                        if (endMenu == 1) {
                            // NOTHING
                        }

                    break;
            }
        }
        switch (cID) {
            case DruidicRitual.kaqemeex.SEARCH_OF_QUEST :
                Functions.___npcTalk(p, n, "I think I may have a worthwhile quest for you actually", "I don't know if you are familair withe the stone circle south of Varrock");
                kaqemeexDialogue(p, n, DruidicRitual.kaqemeex.STONE_CIRCLE);
                break;
            case DruidicRitual.kaqemeex.STONE_CIRCLE :
                Functions.___npcTalk(p, n, "That used to be our stone circle", "Unfortunatley  many years ago dark wizards cast a wicked spell on it", "Corrupting it for their own evil purposes", "and making it useless for us", "We need someone who will go on a quest for us", "to help us purify the circle of Varrock");
                int four = // do not send over
                Functions.___showMenu(p, n, false, "Ok, I will try and help", "No that doesn't sound very interesting", "So is there anything in this for me?");
                if (four == 0) {
                    Functions.___playerTalk(p, n, "Ok I will try and help");
                    Functions.___npcTalk(p, n, "Ok go and speak to our Elder druid, Sanfew");
                    p.updateQuestStage(getQuestId(), 1);
                } else
                    if (four == 1) {
                        Functions.___playerTalk(p, n, "No that doesn't sound very interesting");
                        Functions.___npcTalk(p, n, "Well suit yourself, we'll have to find someone else");
                    } else
                        if (four == 2) {
                            Functions.___playerTalk(p, n, "So is there anything in this for me?");
                            Functions.___npcTalk(p, n, "We are skilled in the art of herblaw", "We can teach you some of our skill if you complete your quest");
                            int five = // do not send over
                            Functions.___showMenu(p, n, false, "Ok, I will try and help", "No that doesn't sound very interesting");
                            if (five == 0) {
                                Functions.___playerTalk(p, n, "Ok I will try and help");
                                Functions.___npcTalk(p, n, "Ok go and speak to our Elder druid, Sanfew");
                                p.updateQuestStage(getQuestId(), 1);
                            } else
                                if (five == 1) {
                                    Functions.___playerTalk(p, n, "No that doesn't sound very interesting");
                                    Functions.___npcTalk(p, n, "Well suit yourself, we'll have to find someone else");
                                }

                        }


                break;
            case DruidicRitual.kaqemeex.ON_MY_WAY_NOW :
                Functions.___npcTalk(p, n, "good bye");
                break;
        }
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.KAQEMEEX.id()) {
                        kaqemeexDialogue(p, n, -1);
                    } else
                        if (n.getID() == NpcId.SANFEW.id()) {
                            switch (p.getQuestStage(quest)) {
                                case 0 :
                                    Functions.___npcTalk(p, n, "What can I do for you young 'un?");
                                    int first = Functions.___showMenu(p, n, "I've heard you druids might be able to teach me herblaw", "Actually I don't need to speak to you");
                                    if (first == 0) {
                                        Functions.___npcTalk(p, n, "You should go to speak to kaqemeex", "He is probably our best teacher of herblaw at the moment", "I believe he is at our stone circle to the north of here");
                                    } else
                                        if (first == 1) {
                                            Functions.___message(p, "Sanfew grunts");
                                        }

                                    break;
                                case 1 :
                                    Functions.___npcTalk(p, n, "What can I do for you young 'un?");
                                    first = Functions.___showMenu(p, n, "I've been sent to help purify the varrock stone circle", "Actually I don't need to speak to you");
                                    if (first == 0) {
                                        Functions.___npcTalk(p, n, "Well what I'm struggling with", "Is the meats I needed for the sacrifice to Guthix", "I need the raw meat from 4 different animals", "Which all need to be dipped in the cauldron of thunder");
                                        int second = // do not send over
                                        Functions.___showMenu(p, n, false, "Where can I find this cauldron?", "Ok I'll do that then");
                                        if (second == 0) {
                                            Functions.___playerTalk(p, n, "Where can I find this cauldron");
                                            Functions.___npcTalk(p, n, "It is in the mysterious underground halls", "which are somewhere in the woods to the south of here");
                                            p.updateQuestStage(getQuestId(), 2);
                                        } else
                                            if (second == 1) {
                                                Functions.___playerTalk(p, n, "Ok I'll do that then");
                                                p.updateQuestStage(getQuestId(), 2);
                                            }

                                    } else
                                        if (first == 1) {
                                            Functions.___message(p, "Sanfew grunts");
                                        }

                                    break;
                                case 2 :
                                    Functions.___npcTalk(p, n, "Have you got what I need yet?");
                                    if (((Functions.hasItem(p, ItemId.ENCHANTED_CHICKEN_MEAT.id()) && Functions.hasItem(p, ItemId.ENCHANTED_BEAR_MEAT.id())) && Functions.hasItem(p, ItemId.ENCHANTED_RAT_MEAT.id())) && Functions.hasItem(p, ItemId.ENCHANTED_BEEF.id())) {
                                        Functions.___playerTalk(p, n, "Yes I have everything");
                                        Functions.___message(p, "You give the meats to Sanfew");
                                        Functions.removeItem(p, ItemId.ENCHANTED_CHICKEN_MEAT.id(), 1);
                                        Functions.removeItem(p, ItemId.ENCHANTED_BEAR_MEAT.id(), 1);
                                        Functions.removeItem(p, ItemId.ENCHANTED_RAT_MEAT.id(), 1);
                                        Functions.removeItem(p, ItemId.ENCHANTED_BEEF.id(), 1);
                                        Functions.___npcTalk(p, n, "thank you, that has brought us much closer to reclaiming our stone circle", "Now go and talk to kaqemeex", "He will show you what you need to know about herblaw");
                                        p.updateQuestStage(getQuestId(), 3);
                                    } else {
                                        Functions.___playerTalk(p, n, "no not yet");
                                        int menu = Functions.___showMenu(p, n, "What was I meant to be doing again?", "I'll get on with it");
                                        if (menu == 0) {
                                            Functions.___npcTalk(p, n, "I need the raw meat from 4 different animals", "Which all need to be dipped in the cauldron of thunder");
                                            int secondMenu = // do not send over
                                            Functions.___showMenu(p, n, false, "Where can I find this cauldron?", "Ok I'll do that then");
                                            if (secondMenu == 0) {
                                                Functions.___playerTalk(p, n, "Where can I find this cauldron");
                                                Functions.___npcTalk(p, n, "It is in the mysterious underground halls", "which are somewhere in the woods to the south of here");
                                            } else
                                                if (secondMenu == 1) {
                                                    Functions.___playerTalk(p, n, "Ok I'll do that then");
                                                }

                                        } else
                                            if (menu == 1) {
                                                // NOTHING
                                            }

                                    }
                                    break;
                                case 3 :
                                case -1 :
                                    Functions.___npcTalk(p, n, "What can I do for you young 'un?");
                                    int finalMenu = Functions.___showMenu(p, n, "Have you any more work for me, to help reclaim the circle?", "Actually I don't need to speak to you");
                                    if (finalMenu == 0) {
                                        Functions.___npcTalk(p, n, "Not at the moment", "I need to make some more preparations myself now");
                                    } else
                                        if (finalMenu == 1) {
                                            Functions.___message(p, "Sanfew grunts");
                                        }

                                    break;
                            }
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
        return ((obj.getID() == 63) && (obj.getY() == 3332)) || ((obj.getID() == 64) && ((obj.getY() == 3336) || (obj.getY() == 3332)));
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 63) && (obj.getY() == 3332)) {
                        Npc suit = p.getWorld().getNpc(NpcId.SUIT_OF_ARMOUR.id(), 374, 374, 3330, 3334);
                        if ((suit != null) && (!(p.getX() <= 373))) {
                            p.message("Suddenly the suit of armour comes to life!");
                            suit.setChasing(p);
                        } else {
                            Functions.doDoor(obj, p);
                        }
                    } else
                        if ((obj.getID() == 64) && ((obj.getY() == 3336) || (obj.getY() == 3332))) {
                            Functions.doDoor(obj, p);
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
        return (obj.getID() == 236) && ((((item.getID() == ItemId.RAW_CHICKEN.id()) || (item.getID() == ItemId.RAW_RAT_MEAT.id())) || (item.getID() == ItemId.RAW_BEEF.id())) || (item.getID() == ItemId.RAW_BEAR_MEAT.id()));
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 236) && ((((item.getID() == ItemId.RAW_CHICKEN.id()) || (item.getID() == ItemId.RAW_RAT_MEAT.id())) || (item.getID() == ItemId.RAW_BEEF.id())) || (item.getID() == ItemId.RAW_BEAR_MEAT.id()))) {
                        if (item.getID() == ItemId.RAW_CHICKEN.id()) {
                            Functions.___message(p, "You dip the chicken in the cauldron");
                            p.getInventory().remove(ItemId.RAW_CHICKEN.id(), 1);
                            Functions.addItem(p, ItemId.ENCHANTED_CHICKEN_MEAT.id(), 1);
                        } else
                            if (item.getID() == ItemId.RAW_BEAR_MEAT.id()) {
                                Functions.___message(p, "You dip the bear meat in the cauldron");
                                p.getInventory().remove(ItemId.RAW_BEAR_MEAT.id(), 1);
                                Functions.addItem(p, ItemId.ENCHANTED_BEAR_MEAT.id(), 1);
                            } else
                                if (item.getID() == ItemId.RAW_RAT_MEAT.id()) {
                                    Functions.___message(p, "You dip the rat meat in the cauldron");
                                    p.getInventory().remove(ItemId.RAW_RAT_MEAT.id(), 1);
                                    Functions.addItem(p, ItemId.ENCHANTED_RAT_MEAT.id(), 1);
                                } else
                                    if (item.getID() == ItemId.RAW_BEEF.id()) {
                                        Functions.___message(p, "You dip the beef in the cauldron");
                                        p.getInventory().remove(ItemId.RAW_BEEF.id(), 1);
                                        Functions.addItem(p, ItemId.ENCHANTED_BEEF.id(), 1);
                                    }



                    }
                    return null;
                });
            }
        };
    }

    class kaqemeex {
        public static final int STONE_CIRCLE = 0;

        public static final int ON_MY_WAY_NOW = 1;

        public static final int SEARCH_OF_QUEST = 2;
    }
}

