package com.openrsc.server.plugins.quests.members.watchtower;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


/**
 *
 *
 * @author Imposter/Fate
 */
public class WatchTowerObstacles implements ObjectActionListener , WallObjectActionListener , ObjectActionExecutiveListener , WallObjectActionExecutiveListener {
    /**
     * OBJECT IDs
     */
    private static int TOWER_FIRST_FLOOR_LADDER = 659;

    private static int COMPLETED_QUEST_LADDER = 1017;

    private static int TOWER_SECOND_FLOOR_LADDER = 1021;

    private static int WATCHTOWER_LEVER = 1014;

    private static int WATCHTOWER_LEVER_DOWNPOSITION = 1015;

    private static int[] WRONG_BUSHES = new int[]{ 960 };

    private static int[] CORRECT_BUSHES = new int[]{ 993, 961, 992, 991, 990 };

    private static int[] TELEPORT_CAVES = new int[]{ 970, 972, 950, 971, 949, 975 };

    private static int[] CAVE_EXITS = new int[]{ 188, 189, 190, 191, 187, 192 };

    private static int TUNNEL_CAVE = 998;

    private static final int TOBAN_CHEST_OPEN = 979;

    private static final int TOBAN_CHEST_CLOSED = 978;

    private static int ISLAND_LADDER = 997;

    private static int BATTLEMENT = 201;

    private static int SOUTH_WEST_BATTLEMENT = 195;

    private static int WRONG_STEAL_COUNTER = 973;

    private static int OGRE_CAVE_ENCLAVE = 955;

    private static int ROCK_CAKE_COUNTER = 999;

    private static int ROCK_CAKE_COUNTER_EMPTY = 1034;

    private static int CHEST_WEST = 1003;

    private static int ROCK_OVER = 995;

    private static int ROCK_BACK = 996;

    private static int CHEST_EAST = 1001;

    private static int DARK_PLACE_ROCKS = 1007;

    private static int DARK_PLACE_TELEPORT_ROCK = 1008;

    private static int YANILLE_HOLE = 968;

    private static int SKAVID_HOLE = 969;

    private static int OGRE_ENCLAVE_EXIT = 1024;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return (((((((((((((obj.getID() == WatchTowerObstacles.TOWER_FIRST_FLOOR_LADDER) || (obj.getID() == WatchTowerObstacles.COMPLETED_QUEST_LADDER)) || (obj.getID() == WatchTowerObstacles.TOWER_SECOND_FLOOR_LADDER)) || ((obj.getID() == WatchTowerObstacles.WATCHTOWER_LEVER) || (obj.getID() == WatchTowerObstacles.WATCHTOWER_LEVER_DOWNPOSITION))) || Functions.inArray(obj.getID(), WatchTowerObstacles.WRONG_BUSHES)) || Functions.inArray(obj.getID(), WatchTowerObstacles.CORRECT_BUSHES)) || Functions.inArray(obj.getID(), WatchTowerObstacles.TELEPORT_CAVES)) || (((obj.getID() == WatchTowerObstacles.TUNNEL_CAVE) || (obj.getID() == WatchTowerObstacles.TOBAN_CHEST_CLOSED)) || (obj.getID() == WatchTowerObstacles.ISLAND_LADDER))) || ((((obj.getID() == WatchTowerObstacles.WRONG_STEAL_COUNTER) || (obj.getID() == WatchTowerObstacles.OGRE_CAVE_ENCLAVE)) || (obj.getID() == WatchTowerObstacles.ROCK_CAKE_COUNTER)) || (obj.getID() == WatchTowerObstacles.ROCK_CAKE_COUNTER_EMPTY))) || ((obj.getID() == WatchTowerObstacles.CHEST_WEST) || (obj.getID() == WatchTowerObstacles.CHEST_EAST))) || ((obj.getID() == WatchTowerObstacles.ROCK_OVER) || (obj.getID() == WatchTowerObstacles.ROCK_BACK))) || ((obj.getID() == WatchTowerObstacles.DARK_PLACE_ROCKS) || (obj.getID() == WatchTowerObstacles.DARK_PLACE_TELEPORT_ROCK))) || ((obj.getID() == WatchTowerObstacles.YANILLE_HOLE) || (obj.getID() == WatchTowerObstacles.SKAVID_HOLE))) || (obj.getID() == WatchTowerObstacles.OGRE_ENCLAVE_EXIT);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == WatchTowerObstacles.COMPLETED_QUEST_LADDER) {
                        p.teleport(636, 1684);
                    } else
                        if (obj.getID() == WatchTowerObstacles.TOWER_SECOND_FLOOR_LADDER) {
                            if (p.getQuestStage(Quests.WATCHTOWER) == (-1)) {
                                p.teleport(492, 3524);
                            } else {
                                p.teleport(636, 2628);
                            }
                        } else
                            if (obj.getID() == WatchTowerObstacles.TOWER_FIRST_FLOOR_LADDER) {
                                Npc t_guard = Functions.getNearestNpc(p, NpcId.TOWER_GUARD.id(), 5);
                                switch (p.getQuestStage(Quests.WATCHTOWER)) {
                                    case 0 :
                                        if (t_guard != null) {
                                            Functions.___npcTalk(p, t_guard, "You can't go up there", "That's private that is");
                                        }
                                        break;
                                    case 1 :
                                    case 2 :
                                    case 3 :
                                    case 4 :
                                    case 5 :
                                    case 6 :
                                    case 7 :
                                    case 8 :
                                    case 9 :
                                    case 10 :
                                    case -1 :
                                        Functions.___npcTalk(p, t_guard, "It is the wizards helping hand", "Let 'em up");
                                        int[] coords = Functions.coordModifier(p, true, obj);
                                        p.teleport(coords[0], coords[1], false);
                                        break;
                                }
                            } else
                                if (obj.getID() == WatchTowerObstacles.OGRE_ENCLAVE_EXIT) {
                                    p.teleport(662, 788);
                                } else
                                    if (obj.getID() == WatchTowerObstacles.WATCHTOWER_LEVER_DOWNPOSITION) {
                                        p.message("The lever is stuck in the down position");
                                    } else
                                        if (obj.getID() == WatchTowerObstacles.WATCHTOWER_LEVER) {
                                            Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), 1015, obj.getDirection(), obj.getType()));
                                            Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 2000);
                                            p.message("You pull the lever");
                                            if (p.getQuestStage(Quests.WATCHTOWER) == 10) {
                                                p.message("The magic forcefield activates");
                                                p.teleport(492, 3521);
                                                Functions.removeItem(p, ItemId.POWERING_CRYSTAL1.id(), 1);
                                                Functions.removeItem(p, ItemId.POWERING_CRYSTAL2.id(), 1);
                                                Functions.removeItem(p, ItemId.POWERING_CRYSTAL3.id(), 1);
                                                Functions.removeItem(p, ItemId.POWERING_CRYSTAL4.id(), 1);
                                                Npc wizard = Functions.getNearestNpc(p, NpcId.WATCHTOWER_WIZARD.id(), 6);
                                                if (wizard != null) {
                                                    p.message("@gre@You haved gained 4 quest points!");
                                                    Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.WATCHTOWER), true);
                                                    Functions.___npcTalk(p, wizard, "Marvellous! it works!", "The town will now be safe", "Your help was invaluable", "Take this payment as a token of my gratitude...");
                                                    p.message("The wizard gives you 5000 pieces of gold");
                                                    Functions.addItem(p, ItemId.COINS.id(), 5000);
                                                    Functions.___npcTalk(p, wizard, "Also, let me improve your magic level for you");
                                                    p.message("The wizard lays his hands on you...");
                                                    p.message("You feel magic power increasing");
                                                    Functions.___npcTalk(p, wizard, "Here is a special item for you...");
                                                    Functions.addItem(p, ItemId.SPELL_SCROLL.id(), 1);
                                                    Functions.___npcTalk(p, wizard, "It's a new spell", "Read the scroll and you will be able", "To teleport yourself to here magically...");
                                                    p.message("Congratulations, you have finished the watchtower quest");
                                                    p.sendQuestComplete(Quests.WATCHTOWER);
                                                    /* TODO REMOVE ALL CACHES AND USE QUEST -1 */
                                                } else {
                                                    p.message("Seems like the wizards were busy, please go back and complete again");
                                                }
                                            } else {
                                                p.message("It had no effect");
                                            }
                                        } else
                                            if (Functions.inArray(obj.getID(), WatchTowerObstacles.WRONG_BUSHES)) {
                                                if (p.getQuestStage(Quests.WATCHTOWER) == 0) {
                                                    Functions.___playerTalk(p, null, "I am not sure why I am searching this bush...");
                                                    return null;
                                                }
                                                Functions.___playerTalk(p, null, "Hmmm, nothing here");
                                            } else
                                                if (Functions.inArray(obj.getID(), WatchTowerObstacles.CORRECT_BUSHES)) {
                                                    if (p.getQuestStage(Quests.WATCHTOWER) == 0) {
                                                        Functions.___playerTalk(p, null, "I am not sure why I am searching this bush...");
                                                        return null;
                                                    }
                                                    if (obj.getID() == WatchTowerObstacles.CORRECT_BUSHES[0]) {
                                                        Functions.___playerTalk(p, null, "Here's Some armour, it could be evidence...");
                                                        Functions.addItem(p, ItemId.ARMOUR.id(), 1);
                                                    } else
                                                        if (obj.getID() == WatchTowerObstacles.CORRECT_BUSHES[1]) {
                                                            if (!Functions.hasItem(p, ItemId.FINGERNAILS.id())) {
                                                                Functions.___playerTalk(p, null, "What's this ?", "Disgusting! some fingernails", "They may be a clue though... I'd better take them");
                                                                Functions.addItem(p, ItemId.FINGERNAILS.id(), 1);
                                                            } else {
                                                                Functions.___playerTalk(p, null, "I have already searched this place");
                                                            }
                                                        } else
                                                            if (obj.getID() == WatchTowerObstacles.CORRECT_BUSHES[2]) {
                                                                if (!Functions.hasItem(p, ItemId.WATCH_TOWER_EYE_PATCH.id())) {
                                                                    Functions.___playerTalk(p, null, "I've found an eyepatch, I better show this to the wizards");
                                                                    Functions.addItem(p, ItemId.WATCH_TOWER_EYE_PATCH.id(), 1);
                                                                } else {
                                                                    Functions.___playerTalk(p, null, "I have already searched this place");
                                                                }
                                                            } else
                                                                if (obj.getID() == WatchTowerObstacles.CORRECT_BUSHES[3]) {
                                                                    if (!Functions.hasItem(p, ItemId.ROBE.id())) {
                                                                        Functions.___playerTalk(p, null, "Aha! a robe");
                                                                        Functions.addItem(p, ItemId.ROBE.id(), 1);
                                                                        Functions.___playerTalk(p, null, "This could be a clue...");
                                                                    } else {
                                                                        Functions.___playerTalk(p, null, "I have already searched this place");
                                                                    }
                                                                } else
                                                                    if (obj.getID() == WatchTowerObstacles.CORRECT_BUSHES[4]) {
                                                                        Functions.___playerTalk(p, null, "Aha a dagger");
                                                                        Functions.addItem(p, ItemId.DAGGER.id(), 1);
                                                                        Functions.___playerTalk(p, null, "I wonder if this is evidence...");
                                                                    }




                                                } else
                                                    if (Functions.inArray(obj.getID(), WatchTowerObstacles.TELEPORT_CAVES)) {
                                                        if (Functions.hasItem(p, ItemId.SKAVID_MAP.id())) {
                                                            // a light source
                                                            if ((Functions.hasItem(p, ItemId.LIT_CANDLE.id()) || Functions.hasItem(p, ItemId.LIT_BLACK_CANDLE.id())) || Functions.hasItem(p, ItemId.LIT_TORCH.id())) {
                                                                p.message("You enter the cave");
                                                                if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[0]) {
                                                                    p.teleport(650, 3555);
                                                                } else
                                                                    if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[1]) {
                                                                        p.teleport(626, 3564);
                                                                    } else
                                                                        if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[2]) {
                                                                            p.teleport(627, 3591);
                                                                        } else
                                                                            if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[3]) {
                                                                                p.teleport(638, 3564);
                                                                            } else
                                                                                if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[4]) {
                                                                                    p.teleport(629, 3574);
                                                                                } else
                                                                                    if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[5]) {
                                                                                        p.teleport(647, 3596);
                                                                                    }





                                                            } else {
                                                                p.teleport(629, 3558);
                                                                Functions.___playerTalk(p, null, "Oh my! It's dark!", "All I can see are lots of rocks on the floor", "I suppose I better search them for a way out");
                                                            }
                                                        } else {
                                                            p.message("There's no way I can find my way through without a map of some kind");
                                                            if ((obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[0]) || (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[4])) {
                                                                p.teleport(629, 777);
                                                            } else
                                                                if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[1]) {
                                                                    p.teleport(624, 807);
                                                                } else
                                                                    if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[2]) {
                                                                        p.teleport(648, 769);
                                                                    } else
                                                                        if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[3]) {
                                                                            p.teleport(631, 789);
                                                                        } else
                                                                            if (obj.getID() == WatchTowerObstacles.TELEPORT_CAVES[5]) {
                                                                                p.teleport(638, 777);
                                                                            }




                                                        }
                                                    } else
                                                        if (obj.getID() == WatchTowerObstacles.TUNNEL_CAVE) {
                                                            p.message("You enter the cave");
                                                            p.teleport(605, 803);
                                                            Functions.___playerTalk(p, null, "Wow! that tunnel went a long way");
                                                        } else
                                                            if (obj.getID() == WatchTowerObstacles.TOBAN_CHEST_CLOSED) {
                                                                if (Functions.hasItem(p, ItemId.KEY.id(), 1)) {
                                                                    p.message("You use the key Og gave you");
                                                                    Functions.removeItem(p, ItemId.KEY.id(), 1);
                                                                    Functions.openChest(obj, 2000, WatchTowerObstacles.TOBAN_CHEST_OPEN);
                                                                    if (Functions.hasItem(p, ItemId.STOLEN_GOLD.id())) {
                                                                        Functions.___message(p, "You have already got the stolen gold");
                                                                    } else {
                                                                        p.message("You find a stash of gold inside");
                                                                        Functions.___message(p, "You take the gold");
                                                                        Functions.addItem(p, ItemId.STOLEN_GOLD.id(), 1);
                                                                    }
                                                                    p.message("The chest springs shut");
                                                                } else {
                                                                    p.message("The chest is locked");
                                                                    Functions.___playerTalk(p, null, "I think I need a key of some sort...");
                                                                }
                                                            } else
                                                                if (obj.getID() == WatchTowerObstacles.ISLAND_LADDER) {
                                                                    p.message("You climb down the ladder");
                                                                    p.teleport(669, 826);
                                                                } else
                                                                    if (obj.getID() == WatchTowerObstacles.WRONG_STEAL_COUNTER) {
                                                                        p.message("You find nothing to steal");
                                                                    } else
                                                                        if (obj.getID() == WatchTowerObstacles.OGRE_CAVE_ENCLAVE) {
                                                                            if (p.getQuestStage(Quests.WATCHTOWER) == (-1)) {
                                                                                p.message("The ogres have blocked this entrance now");
                                                                                // TODO should we sell this entrance for 100,000 coins or etc?
                                                                                return null;
                                                                            }
                                                                            Npc ogre_guard = Functions.getNearestNpc(p, NpcId.OGRE_GUARD_CAVE_ENTRANCE.id(), 5);
                                                                            if (ogre_guard != null) {
                                                                                Functions.___npcTalk(p, ogre_guard, "No you don't!");
                                                                                ogre_guard.startCombat(p);
                                                                            }
                                                                        } else
                                                                            if (obj.getID() == WatchTowerObstacles.ROCK_CAKE_COUNTER) {
                                                                                Npc ogre_trader = Functions.getNearestNpc(p, NpcId.OGRE_TRADER_ROCKCAKE.id(), 5);
                                                                                if (ogre_trader != null) {
                                                                                    Functions.___npcTalk(p, ogre_trader, "Grr! get your hands off those cakes");
                                                                                    ogre_trader.startCombat(p);
                                                                                } else {
                                                                                    if (Functions.getCurrentLevel(p, Skills.THIEVING) < 15) {
                                                                                        p.message("You need a thieving level of 15 to steal from this stall");
                                                                                        return null;
                                                                                    }
                                                                                    p.message("You cautiously grab a cake from the stall");
                                                                                    Functions.addItem(p, ItemId.ROCK_CAKE.id(), 1);
                                                                                    p.incExp(Skills.THIEVING, 64, true);
                                                                                    Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), WatchTowerObstacles.ROCK_CAKE_COUNTER_EMPTY, obj.getDirection(), obj.getType()));
                                                                                    Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 5000);
                                                                                }
                                                                            } else
                                                                                if (obj.getID() == WatchTowerObstacles.ROCK_CAKE_COUNTER_EMPTY) {
                                                                                    p.message("The stall is empty at the moment");
                                                                                } else
                                                                                    if (obj.getID() == WatchTowerObstacles.CHEST_WEST) {
                                                                                        randomizedChest(p, obj);
                                                                                    } else
                                                                                        if (obj.getID() == WatchTowerObstacles.CHEST_EAST) {
                                                                                            p.message("You open the chest");
                                                                                            Functions.openChest(obj, 2000, 1002);
                                                                                            p.message("Ahh! there is a poison spider inside");
                                                                                            p.message("Someone's idea of a joke...");
                                                                                            Npc spider = Functions.spawnNpc(p.getWorld(), NpcId.POISON_SPIDER.id(), obj.getX(), obj.getY() + 1, 60000 * 5);
                                                                                            spider.startCombat(p);
                                                                                            return invoke(1, 3);
                                                                                        } else
                                                                                            if ((obj.getID() == WatchTowerObstacles.ROCK_OVER) || (obj.getID() == WatchTowerObstacles.ROCK_BACK)) {
                                                                                                if (command.equalsIgnoreCase("look at")) {
                                                                                                    p.message("The bridge has collapsed");
                                                                                                    p.message("It seems this rock is placed here to jump from");
                                                                                                } else
                                                                                                    if (command.equalsIgnoreCase("jump over")) {
                                                                                                        if (Functions.getCurrentLevel(p, Skills.AGILITY) < 30) {
                                                                                                            p.message("You need agility level of 30 to attempt this jump");
                                                                                                            return null;
                                                                                                        }
                                                                                                        if (obj.getID() == WatchTowerObstacles.ROCK_BACK) {
                                                                                                            p.teleport(646, 805);
                                                                                                            Functions.___playerTalk(p, null, "I'm glad that was easier on the way back!");
                                                                                                        } else {
                                                                                                            Npc ogre_guard = Functions.getNearestNpc(p, NpcId.OGRE_GUARD_BRIDGE.id(), 5);
                                                                                                            if (ogre_guard != null) {
                                                                                                                Functions.___npcTalk(p, ogre_guard, "Oi! Little thing, if you want to cross here", "You can pay me first - 20 gold pieces!");
                                                                                                                Functions.___playerTalk(p, ogre_guard, "20 gold pieces to jump off a bridge!!?");
                                                                                                                Functions.___npcTalk(p, ogre_guard, "That's what I said, like it or lump it");
                                                                                                                int menu = Functions.___showMenu(p, ogre_guard, "Okay i'll pay it", "Forget it, i'm not paying");
                                                                                                                if (menu == 0) {
                                                                                                                    Functions.___npcTalk(p, ogre_guard, "A wise choice little thing");
                                                                                                                    if (!Functions.hasItem(p, ItemId.COINS.id(), 20)) {
                                                                                                                        Functions.___npcTalk(p, ogre_guard, "And where is your money ? Grrrr!", "Do you want to get hurt or something ?");
                                                                                                                    } else {
                                                                                                                        Functions.removeItem(p, ItemId.COINS.id(), 20);
                                                                                                                        if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                                                                                                            if (p.getFatigue() >= 7500) {
                                                                                                                                p.message("You are too tired to attempt this jump");
                                                                                                                                return null;
                                                                                                                            }
                                                                                                                        }
                                                                                                                        p.message("You daringly jump across the chasm");
                                                                                                                        p.teleport(647, 799);
                                                                                                                        p.incExp(Skills.AGILITY, 50, true);
                                                                                                                        Functions.___playerTalk(p, null, "Phew! I just made it");
                                                                                                                    }
                                                                                                                } else
                                                                                                                    if (menu == 1) {
                                                                                                                        Functions.___npcTalk(p, ogre_guard, "In that case you're not crossing");
                                                                                                                        p.message("The guard blocks your path");
                                                                                                                    }

                                                                                                            } else {
                                                                                                                if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                                                                                                    if (p.getFatigue() >= 7500) {
                                                                                                                        p.message("You are too tired to attempt this jump");
                                                                                                                        return null;
                                                                                                                    }
                                                                                                                }
                                                                                                                p.message("You daringly jump across the chasm");
                                                                                                                p.teleport(647, 799);
                                                                                                                p.incExp(Skills.AGILITY, 50, true);
                                                                                                                Functions.___playerTalk(p, null, "Phew! I just made it");
                                                                                                            }
                                                                                                        }
                                                                                                    }

                                                                                            } else
                                                                                                if (obj.getID() == WatchTowerObstacles.DARK_PLACE_ROCKS) {
                                                                                                    p.message("You search the rock");
                                                                                                    p.message("There's nothing here");
                                                                                                } else
                                                                                                    if (obj.getID() == WatchTowerObstacles.DARK_PLACE_TELEPORT_ROCK) {
                                                                                                        p.message("You search the rock");
                                                                                                        p.message("You uncover a tunnel entrance");
                                                                                                        p.teleport(638, 776);
                                                                                                        Functions.___playerTalk(p, null, "Phew! At last i'm out...", "Next time I will take some light!");
                                                                                                    } else
                                                                                                        if (obj.getID() == WatchTowerObstacles.YANILLE_HOLE) {
                                                                                                            Functions.___playerTalk(p, null, "I can't get through this way", "This hole must lead to somewhere...");
                                                                                                        } else
                                                                                                            if (obj.getID() == WatchTowerObstacles.SKAVID_HOLE) {
                                                                                                                p.playerServerMessage(MessageType.QUEST, "You enter the tunnel");
                                                                                                                p.message("So that's how the skavids are getting into yanille!");
                                                                                                                p.teleport(609, 742);
                                                                                                            }






















                    return null;
                });
                addState(1, () -> {
                    p.message("The chest snaps shut");
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
        return ((obj.getID() == WatchTowerObstacles.BATTLEMENT) || (obj.getID() == WatchTowerObstacles.SOUTH_WEST_BATTLEMENT)) || Functions.inArray(obj.getID(), WatchTowerObstacles.CAVE_EXITS);
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(obj.getID(), WatchTowerObstacles.CAVE_EXITS)) {
                        // 6 WEBS!!
                        if (obj.getID() == WatchTowerObstacles.CAVE_EXITS[0]) {
                            p.teleport(648, 769);
                        } else
                            if (obj.getID() == WatchTowerObstacles.CAVE_EXITS[1]) {
                                p.teleport(638, 777);
                            } else
                                if (obj.getID() == WatchTowerObstacles.CAVE_EXITS[2]) {
                                    p.teleport(629, 777);
                                } else
                                    if (obj.getID() == WatchTowerObstacles.CAVE_EXITS[3]) {
                                        p.teleport(631, 789);
                                    } else
                                        if (obj.getID() == WatchTowerObstacles.CAVE_EXITS[4]) {
                                            p.teleport(624, 807);
                                        } else
                                            if (obj.getID() == WatchTowerObstacles.CAVE_EXITS[5]) {
                                                p.teleport(645, 812);
                                            }





                    } else
                        if (obj.getID() == WatchTowerObstacles.BATTLEMENT) {
                            Functions.___playerTalk(p, null, "What's this ?", "The bridge is out - i'll need to find another way in", "I can see a ladder up there coming out of a hole", "Maybe I should check out some of these tunnels around here...");
                        } else
                            if (obj.getID() == WatchTowerObstacles.SOUTH_WEST_BATTLEMENT) {
                                Npc ogre_guard = Functions.getNearestNpc(p, NpcId.OGRE_GUARD_BATTLEMENT.id(), 5);
                                if (p.getX() <= 664) {
                                    p.teleport(p.getX() + 1, p.getY());
                                } else {
                                    if (p.getCache().hasKey("has_ogre_gift")) {
                                        Functions.___npcTalk(p, ogre_guard, "It's that creature again", "This time we will let it go...");
                                        p.teleport(p.getX() - 1, p.getY());
                                        p.message("You climb over the battlement");
                                    } else
                                        if (p.getCache().hasKey("get_ogre_gift") || (p.getQuestStage(Quests.WATCHTOWER) == (-1))) {
                                            if (ogre_guard != null) {
                                                Functions.___npcTalk(p, ogre_guard, "Stop creature!... Oh its you", "Well what have you got for us then ?");
                                                if (p.getQuestStage(Quests.WATCHTOWER) == (-1)) {
                                                    Functions.___playerTalk(p, ogre_guard, "I didn't bring anything");
                                                    Functions.___npcTalk(p, ogre_guard, "Didn't bring anything!", "In that case shove off!");
                                                    p.message("The guard pushes you out of the city");
                                                    p.teleport(635, 774);
                                                    return null;
                                                }
                                                if (Functions.hasItem(p, ItemId.ROCK_CAKE.id())) {
                                                    Functions.___playerTalk(p, ogre_guard, "How about this ?");
                                                    p.message("You give the guard a rock cake");
                                                    Functions.removeItem(p, ItemId.ROCK_CAKE.id(), 1);
                                                    Functions.___npcTalk(p, ogre_guard, "Well well, looks at this", "My favourite, rock cake!", "Okay we will let it through");
                                                    p.teleport(663, 812);
                                                    p.message("You climb over the battlement");
                                                    p.getCache().remove("get_ogre_gift");
                                                    p.getCache().store("has_ogre_gift", true);
                                                } else {
                                                    Functions.___playerTalk(p, ogre_guard, "I didn't bring anything");
                                                    Functions.___npcTalk(p, ogre_guard, "Didn't bring anything!", "In that case shove off!");
                                                    p.message("The guard pushes you out of the city");
                                                    p.teleport(635, 774);
                                                }
                                            }
                                        } else {
                                            if (ogre_guard != null) {
                                                Functions.___npcTalk(p, ogre_guard, "Oi! where do you think you are going ?", "You are for the cooking pot!");
                                                int menu = Functions.___showMenu(p, ogre_guard, "But I am a friend to ogres...", "Not if I can help it");
                                                if (menu == 0) {
                                                    Functions.___npcTalk(p, ogre_guard, "Prove it to us with a gift", "Get us something from the market");
                                                    Functions.___playerTalk(p, ogre_guard, "Like what ?");
                                                    Functions.___npcTalk(p, ogre_guard, "Surprise us...");
                                                    p.getCache().store("get_ogre_gift", true);
                                                } else
                                                    if (menu == 1) {
                                                        Functions.___npcTalk(p, ogre_guard, "You can help by being tonight's dinner...", "Or you can go away, now what shall it be ?");
                                                        int subMenu = Functions.___showMenu(p, ogre_guard, "Okay, okay i'm going", "I tire of ogres, prepare to die!");
                                                        if (subMenu == 0) {
                                                            Functions.___npcTalk(p, ogre_guard, "back to whence you came");
                                                            p.teleport(635, 774);
                                                        } else
                                                            if (subMenu == 1) {
                                                                Functions.___npcTalk(p, ogre_guard, "Grrrrr!");
                                                                ogre_guard.startCombat(p);
                                                            }

                                                    }

                                            }
                                        }

                                }
                            }


                    return null;
                });
            }
        };
    }

    private void randomizedChest(Player p, GameObject o) {
        p.message("You open the chest");
        Functions.openChest(o, 2500, 1002);
        int[] randomChestReward = new int[]{ NpcId.POISON_SCORPION.id(), NpcId.POISON_SPIDER.id(), NpcId.CHAOS_DWARF.id(), NpcId.RAT_LVL8.id(), ItemId.ROTTEN_APPLES.id(), ItemId.BONES.id(), ItemId.EMERALD.id(), ItemId.BURNT_PIKE.id() };
        int choosenReward = ((int) (Math.random() * randomChestReward.length));
        if (choosenReward == 0) {
            Functions.___playerTalk(p, null, "Hey! a scorpion is in here!");
            Npc scorp = Functions.spawnNpc(p.getWorld(), NpcId.POISON_SCORPION.id(), o.getX() - 1, o.getY(), 60000 * 5);
            scorp.startCombat(p);
        } else
            if (choosenReward == 1) {
                Functions.___playerTalk(p, null, "Oh no, not one of these spider things!");
                Npc spider = Functions.spawnNpc(p.getWorld(), NpcId.POISON_SPIDER.id(), o.getX() - 1, o.getY(), 60000 * 5);
                spider.startCombat(p);
            } else
                if (choosenReward == 2) {
                    Functions.___playerTalk(p, null, "How on earth did this dwarf get in here ?");
                    Npc dwarf = Functions.spawnNpc(p.getWorld(), NpcId.CHAOS_DWARF.id(), o.getX() - 1, o.getY(), 60000 * 5);
                    dwarf.startCombat(p);
                } else
                    if (choosenReward == 3) {
                        Functions.___playerTalk(p, null, "Ugh! a dirty rat!");
                        Functions.spawnNpc(p.getWorld(), NpcId.RAT_LVL8.id(), o.getX() - 1, o.getY(), 60000 * 5);
                    } else
                        if (choosenReward == 4) {
                            Functions.___playerTalk(p, null, "Oh dear, I bet these apples taste disgusting");
                            Functions.addItem(p, ItemId.ROTTEN_APPLES.id(), 1);
                        } else
                            if (choosenReward == 5) {
                                Functions.___playerTalk(p, null, "Oh great, some bones!");
                                Functions.addItem(p, ItemId.BONES.id(), 1);
                            } else
                                if (choosenReward == 6) {
                                    Functions.___playerTalk(p, null, "Wow, look at the size of this emerald!");
                                    Functions.addItem(p, ItemId.EMERALD.id(), 1);
                                } else
                                    if (choosenReward == 7) {
                                        Functions.___playerTalk(p, null, "Burnt fish - why did I bother ?");
                                        Functions.addItem(p, ItemId.BURNT_PIKE.id(), 1);
                                    }







        p.message("The chest snaps shut");
    }
}

