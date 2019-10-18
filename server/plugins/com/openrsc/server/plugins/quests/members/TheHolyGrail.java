package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class TheHolyGrail implements QuestInterface , InvActionListener , ObjectActionListener , PickupListener , PlayerKilledNpcListener , TalkToNpcListener , WallObjectActionListener , InvActionExecutiveListener , ObjectActionExecutiveListener , PickupExecutiveListener , PlayerKilledNpcExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    /**
     *
     *
     * @author Davve
     */
    @Override
    public int getQuestId() {
        return Quests.THE_HOLY_GRAIL;
    }

    @Override
    public String getQuestName() {
        return "The Holy Grail (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player player) {
        player.message("Well done you have completed the holy grail quest");
        player.message("@gre@You haved gained 2 quest points!");
        int[] questData = player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.THE_HOLY_GRAIL);
        // keep order kosher
        int[] skillIDs = new int[]{ Skills.PRAYER, Skills.DEFENSE };
        // 1000 for prayer, 1200 for defense
        int[] amounts = new int[]{ 1000, 1200 };
        for (int i = 0; i < skillIDs.length; i++) {
            questData[Quests.MAPIDX_SKILL] = skillIDs[i];
            questData[Quests.MAPIDX_BASE] = amounts[i];
            questData[Quests.MAPIDX_VAR] = amounts[i];
            Functions.incQuestReward(player, questData, i == (skillIDs.length - 1));
        }
    }

    /**
     * NPCS: #275 King Arthur - NPC HANDLED IN MERLINS CRYSTAL QUEST FILE. #287 is Merlin trapped
     * Merlin should be the one of library (393)
     */
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return DataConversions.inArray(new int[]{ NpcId.MERLIN_LIBRARY.id(), NpcId.BLACK_KNIGHT_TITAN.id(), NpcId.UNHAPPY_PEASANT.id(), NpcId.FISHERMAN.id(), NpcId.FISHER_KING.id(), NpcId.KING_PERCIVAL.id(), NpcId.HAPPY_PEASANT.id() }, n.getID());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.MERLIN_LIBRARY.id()) {
                        switch (p.getQuestStage(Quests.THE_HOLY_GRAIL)) {
                            case 1 :
                            case 2 :
                            case 3 :
                                Functions.___playerTalk(p, n, "Hello King Arthur has sent me on a quest for the holy grail", "He thought you could offer some assistance");
                                Functions.___npcTalk(p, n, "Ah yes the holy grail", "That is a powerful artifact indeed", "Returning it here would help Camelot a lot", "Due to its nature the holy grail is likely to reside in a holy place");
                                Functions.___playerTalk(p, n, "Any suggestions?");
                                Functions.___npcTalk(p, n, "I believe there is a holy island somewhere not far away", "I'm not entirely sure", "I spent too long inside that crystal", "Anyway go and talk to someone over there", "I suppose you could also try speaking to Sir Galahad", "He returned from the quest many years after everyone else", "He seems to know something about it", "but he can only speak about those experiences cryptically");
                                if (p.getQuestStage(quest) == 1) {
                                    Functions.setQuestStage(p, quest, 2);
                                }
                                int menu = // do not send over
                                Functions.___showMenu(p, n, false, "Thankyou for the advice", "Where can I find Sir Galahad?");
                                if (menu == 0) {
                                    Functions.___playerTalk(p, n, "Thankyou for the advice");
                                } else
                                    if (menu == 1) {
                                        Functions.___playerTalk(p, n, "Where can I find Sir Galahad");
                                        Functions.___npcTalk(p, n, "Galahad now lives a life of religious contemplation", "He lives somewhere west of McGrubors Wood");
                                    }

                                break;
                            case -1 :
                                Functions.___npcTalk(p, n, "hello I'm working on a new spell", "To turn people into hedgehogs");
                                break;
                        }
                    } else
                        if (n.getID() == NpcId.BLACK_KNIGHT_TITAN.id()) {
                            Functions.___npcTalk(p, n, "I am the black knight titan", "You must pass through me before you can continue in this realm");
                            int menu = Functions.___showMenu(p, n, "Ok, have at ye oh evil knight", "Actually I think I'll run away");
                            if (menu == 0) {
                                Functions.sleep(800);
                                n.setChasing(p);
                            }
                        } else
                            if (n.getID() == NpcId.UNHAPPY_PEASANT.id()) {
                                Functions.___npcTalk(p, n, "Woe is me", "Our crops are all failing", "How shall I feed myself this winter?");
                            } else
                                if (n.getID() == NpcId.FISHERMAN.id()) {
                                    Functions.___npcTalk(p, n, "Hi - I don't get many visitors here");
                                    int menu = Functions.___showMenu(p, n, "How's the fishing?", "Any idea how to get into the castle?", "Yes well this place is a dump");
                                    if (menu == 0) {
                                        Functions.___npcTalk(p, n, "Not amazing", "Not many fish can live in this gungey stuff", "I remember when this was a pleasant river", "Teaming with every sort of fish");
                                    } else
                                        if (menu == 1) {
                                            Functions.___npcTalk(p, n, "why thats easy", "just ring one of the bells outside");
                                            Functions.___playerTalk(p, n, "I didn't see any bells");
                                            Functions.___npcTalk(p, n, "You must be blind then", "There's always bells there when I go to the castle");
                                            Functions.createGroundItem(ItemId.BELL.id(), 1, 421, 30, p);
                                        } else
                                            if (menu == 2) {
                                                Functions.___npcTalk(p, n, "This place used to be very beautiful", "However as our king grows old and weak", "the land seems to be dying too");
                                            }


                                } else
                                    if (n.getID() == NpcId.FISHER_KING.id()) {
                                        Functions.___npcTalk(p, n, "Ah you got inside at last", "You spent all that time fumbling around outside", "I thought you'd never make it here");
                                        if (p.getQuestStage(quest) == 3) {
                                            p.updateQuestStage(quest, 4);
                                        }
                                        int menu = Functions.___showMenu(p, n, "How did you know what I have been doing?", "I seek the holy grail", "You don't look too well");
                                        if (menu == 0) {
                                            Functions.___npcTalk(p, n, "Oh I can see what is happening in my realm", "I have sent clues to help you get here", "Such as the fisherman", "And the crone");
                                            int mm = Functions.___showMenu(p, n, "I seek the holy grail", "You don't look too well", "Do you mind if I have a look around?");
                                            if (mm == 0) {
                                                Functions.___npcTalk(p, n, "Ah excellent, a knight come to seek the holy grail", "Maybe now our land can be restored to it's former glory", "At the moment the grail cannot be removed from the castle", "legend has it a questing knight will one day", "Work out how to restore our land", "then he will claim the grail as his prize");
                                                Functions.___playerTalk(p, n, "Any ideas how I can restore the land?");
                                                Functions.___npcTalk(p, n, "None at all");
                                                int m = // do not send over
                                                Functions.___showMenu(p, n, false, "You don't look to well", "Do you mind if I have a look around?");
                                                if (m == 0) {
                                                    Functions.___playerTalk(p, n, "You don't look too well");
                                                    Functions.___npcTalk(p, n, "Nope I don't feel so good either", "I fear my life is running short", "Alas my son and heir is not here", "I am waiting for my son to return to this castle", "If you could find my son that would be a great weight off my shoulders");
                                                    Functions.___playerTalk(p, n, "Who is your son?");
                                                    Functions.___npcTalk(p, n, "He is known as Percival", "I believe he is a knight of the round table");
                                                    Functions.___playerTalk(p, n, "I shall go and see if I can find him");
                                                } else
                                                    if (m == 1) {
                                                        Functions.___playerTalk(p, n, "Do you mind if I have a look around?");
                                                        Functions.___npcTalk(p, n, "No not at all, be my guest");
                                                    }

                                            } else
                                                if (mm == 1) {
                                                    Functions.___npcTalk(p, n, "Nope I don't feel so good either", "I fear my life is running short", "Alas my son and heir is not here", "I am waiting for my son to return to this castle", "If you could find my son that would be a great weight off my shoulders");
                                                    Functions.___playerTalk(p, n, "Who is your son?");
                                                    Functions.___npcTalk(p, n, "He is known as Percival", "I believe he is a knight of the round table");
                                                    Functions.___playerTalk(p, n, "I shall go and see if I can find him");
                                                } else
                                                    if (mm == 2) {
                                                        Functions.___npcTalk(p, n, "No not at all, be my guest");
                                                    }


                                        } else
                                            if (menu == 1) {
                                                Functions.___npcTalk(p, n, "Ah excellent, a knight come to seek the holy grail", "Maybe now our land can be restored to it's former glory", "At the moment the grail cannot be removed from the castle", "legend has it a questing knight will one day", "Work out how to restore our land", "then he will claim the grail as his prize");
                                                Functions.___playerTalk(p, n, "Any ideas how I can restore the land?");
                                                Functions.___npcTalk(p, n, "None at all");
                                                int m = // do not send over
                                                Functions.___showMenu(p, n, false, "You don't look to well", "Do you mind if I have a look around?");
                                                if (m == 0) {
                                                    Functions.___playerTalk(p, n, "You don't look too well");
                                                    Functions.___npcTalk(p, n, "Nope I don't feel so good either", "I fear my life is running short", "Alas my son and heir is not here", "I am waiting for my son to return to this castle", "If you could find my son that would be a great weight off my shoulders");
                                                    Functions.___playerTalk(p, n, "Who is your son?");
                                                    Functions.___npcTalk(p, n, "He is known as Percival", "I believe he is a knight of the round table");
                                                    Functions.___playerTalk(p, n, "I shall go and see if I can find him");
                                                } else
                                                    if (m == 1) {
                                                        Functions.___playerTalk(p, n, "Do you mind if I have a look around?");
                                                        Functions.___npcTalk(p, n, "No not at all, be my guest");
                                                    }

                                            } else
                                                if (menu == 2) {
                                                    Functions.___npcTalk(p, n, "Nope I don't feel so good either", "I fear my life is running short", "Alas my son and heir is not here", "I am waiting for my son to return to this castle", "If you could find my son that would be a great weight off my shoulders");
                                                    Functions.___playerTalk(p, n, "Who is your son?");
                                                    Functions.___npcTalk(p, n, "He is known as Percival", "I believe he is a knight of the round table");
                                                    Functions.___playerTalk(p, n, "I shall go and see if I can find him");
                                                }


                                    } else
                                        if (n.getID() == NpcId.KING_PERCIVAL.id()) {
                                            Functions.___npcTalk(p, n, "You missed all the excitement", "I got here and agreed to take over duties as king here", "Then before my eyes the most miraculous changes occured here", "Grass and trees were growing outside before our very eyes", "Thankyou very much for showing me the way home");
                                        } else
                                            if (n.getID() == NpcId.HAPPY_PEASANT.id()) {
                                                Functions.___npcTalk(p, n, "Oh happy day", "suddenly our crops are growing again", "It'll be a bumper harvest this year");
                                            }






                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
        return (obj.getID() == 117) || (obj.getID() == 116);
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 117) {
                        if (((p.getQuestStage(quest) >= 1) && Functions.atQuestStage(p, Quests.MERLINS_CRYSTAL, -1)) || (p.getQuestStage(quest) == (-1))) {
                            Functions.doDoor(obj, p);
                        } else {
                            p.message("The door won't open");
                        }
                    }
                    if (obj.getID() == 116) {
                        p.message("You go through the door");
                        Functions.doDoor(obj, p);
                        if ((p.getInventory().countId(ItemId.MAGIC_WHISTLE.id()) != 2) && ((p.getQuestStage(Quests.THE_HOLY_GRAIL) >= 3) || (p.getQuestStage(Quests.THE_HOLY_GRAIL) == (-1)))) {
                            Functions.createGroundItem(ItemId.MAGIC_WHISTLE.id(), 1, 204, 2440, p);
                            Functions.createGroundItem(ItemId.MAGIC_WHISTLE.id(), 1, 204, 2440, p);
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvAction(Item item, Player p, String command) {
        return ((item.getID() == ItemId.MAGIC_WHISTLE.id()) || (item.getID() == ItemId.BELL.id())) || (item.getID() == ItemId.MAGIC_GOLDEN_FEATHER.id());
    }

    @Override
    public GameStateEvent onInvAction(Item item, Player p, String command) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (item.getID() == ItemId.MAGIC_WHISTLE.id()) {
                        if (p.getLocation().inBounds(490, 652, 491, 653)) {
                            // SQUARE PLOT
                            if ((p.getQuestStage(quest) == 5) || (p.getQuestStage(quest) == (-1))) {
                                p.teleport(492, 18, false);
                            } else {
                                p.teleport(396, 18, false);
                            }
                        } else
                            if (p.getLocation().inBounds(388, 4, 427, 40)) {
                                // 1st
                                // ISLAND
                                p.teleport(490, 651, false);
                            } else
                                if ((p.getLocation().inBounds(484, 4, 523, 40) || p.getLocation().inBounds(511, 976, 519, 984)) || p.getLocation().inBounds(511, 1920, 518, 1925)) {
                                    // 2nd
                                    // ISLAND
                                    // -
                                    // 2nd
                                    // floor
                                    // -
                                    // top
                                    // floor
                                    // castle.
                                    p.teleport(490, 651, false);
                                } else {
                                    Functions.___message(p, "The whistle makes no noise", "It will not work in this location");
                                }


                    } else
                        if (item.getID() == ItemId.BELL.id()) {
                            p.message("Ting a ling a ling");
                            if (p.getLocation().inBounds(411, 27, 425, 40)) {
                                p.message("Somehow you are now inside the castle");
                                p.teleport(420, 35, false);
                            }
                        } else// Prod sack = 328, 446

                            if (item.getID() == ItemId.MAGIC_GOLDEN_FEATHER.id()) {
                                int x = p.getLocation().getX();
                                int y = p.getLocation().getY();
                                int sX = 328;
                                int sY = 446;
                                int pX = x - sX;
                                int pY = y - sY;
                                if (p.getQuestStage(quest) == (-1)) {
                                    p.message("nothing interesting happens");
                                } else
                                    if ((Math.abs(pY) > Math.abs(pX)) && (y <= sY)) {
                                        p.message("the feather points south");
                                    } else
                                        if ((Math.abs(pX) > Math.abs(pY)) && (x > sX)) {
                                            p.message("the feather points east");
                                        } else
                                            if (x < sX) {
                                                p.message("the feather points west");
                                            } else
                                                if ((Math.abs(pY) > Math.abs(pX)) && (y >= sY)) {
                                                    p.message("the feather points north");
                                                } else {
                                                    // TODO we may or may not need this.
                                                }




                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return n.getID() == NpcId.BLACK_KNIGHT_TITAN.id();
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.BLACK_KNIGHT_TITAN.id()) {
                        if (p.getInventory().wielding(ItemId.EXCALIBUR.id())) {
                            n.killedBy(p);
                            n.resetCombatEvent();
                            p.message("Well done you have defeated the black knight titan");
                            p.teleport(414, 11, false);
                        } else {
                            n.resetCombatEvent();
                            n.getSkills().setLevel(Skills.HITS, n.getDef().hits);
                            n.teleport(n.getLoc().startX, n.getLoc().startY);
                            p.message("Maybe you need something more to beat the titan");
                        }
                    }
                    return null;
                });
            }
        };
    }

    /**
     * playerTalk(p,n, "You feel that the grail shouldn't be moved");
     * playerTalk(p,n,
     * "You must complete some task here before you are worthy");
     */
    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((i.getID() == ItemId.HOLY_GRAIL.id()) && (i.getX() == 418)) && (i.getY() == 1924)) {
                        Functions.___message(p, "You feel that the grail shouldn't be moved", "You must complete some task here before you are worthy");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return ((i.getID() == ItemId.HOLY_GRAIL.id()) && (i.getX() == 418)) && (i.getY() == 1924);
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == 408;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 408) {
                        if (p.getQuestStage(quest) == 4) {
                            Functions.___message(p, "You hear muffled noises from the sack");
                            p.message("You open the sack");
                            Npc percival = Functions.spawnNpc(p.getWorld(), NpcId.SIR_PERCIVAL.id(), 328, 446, 120000);
                            Functions.___npcTalk(p, percival, "Wow thankyou", "I could hardly breathe in there");
                            int menu = Functions.___showMenu(p, percival, "How did you end up in a sack?", "Come with me, I shall make you a king", "Your father wishes to speak to you");
                            if (menu == 0) {
                                Functions.___npcTalk(p, percival, "It's a little embarrassing really", "After going on a long and challenging quest", "to retrieve the boots of arkaneeses", "defeating many powerful enemies on the way", "I fell into a goblin trap", "I've been kept as a slave here for the last 3 months", "a day or so ago, they decided it was a fun game", "To put me in this sack", "Then they forgot about me", "I'm now very hungry and my bones feel very stiff");
                                int menu2 = Functions.___showMenu(p, percival, "Come with me, I shall make you a king", "Your father wishes to speak to you");
                                if (menu2 == 0) {
                                    Functions.___npcTalk(p, percival, "What are you talking about?", "The king of where?");
                                    Functions.___playerTalk(p, percival, "Your father is apparently someone called the fisher king");
                                    beHisHeir(p, percival);
                                } else
                                    if (menu2 == 1) {
                                        Functions.___npcTalk(p, percival, "My father? you have spoken to him recently?");
                                        beHisHeir(p, percival);
                                    }

                            } else
                                if (menu == 1) {
                                    Functions.___npcTalk(p, percival, "What are you talking about?", "The king of where?");
                                    Functions.___playerTalk(p, percival, "Your father is apparently someone called the fisher king");
                                    beHisHeir(p, percival);
                                } else
                                    if (menu == 2) {
                                        Functions.___npcTalk(p, percival, "My father? you have spoken to him recently?");
                                        beHisHeir(p, percival);
                                    }


                        } else {
                            p.message("nothing interesting happens");
                        }
                    }
                    return null;
                });
            }
        };
    }

    private void beHisHeir(Player p, Npc percival) {
        Functions.___playerTalk(p, percival, "He is dying and wishes you to be his heir");
        Functions.___npcTalk(p, percival, "I have been told that before", "I have not been able to find that castle again though");
        Functions.___playerTalk(p, percival, "Well I do have the means to get us there - a magic whistle");
        if (Functions.hasItem(p, ItemId.MAGIC_WHISTLE.id())) {
            Functions.___message(p, "You give a whistle to Sir Percival", "You tell sir Percival what to do with the whistle");
            Functions.removeItem(p, ItemId.MAGIC_WHISTLE.id(), 1);
            Functions.___npcTalk(p, percival, "Ok I will see you there then");
            p.updateQuestStage(this, 5);
        } else {
            Functions.___playerTalk(p, percival, "I will just go and get you one");
        }
    }
}

