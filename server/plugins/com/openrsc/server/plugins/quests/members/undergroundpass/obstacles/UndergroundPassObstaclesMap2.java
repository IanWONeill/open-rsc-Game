package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class UndergroundPassObstaclesMap2 implements ObjectActionListener , WallObjectActionListener , ObjectActionExecutiveListener , WallObjectActionExecutiveListener {
    /**
     * OBJECT IDs
     */
    public static int[] PILE_OF_MUD_MAP_LEVEL_2 = new int[]{ 841, 843, 844, 845, 846, 847 };

    public static int CRATE = 868;

    public static int[] RAILINGS = new int[]{ 167, 170, 169, 168 };

    public static int[] DUG_UP_SOIL = new int[]{ 839, 840 };

    public static int LEDGE = 837;

    public static int WALL_GRILL_EAST = 836;

    public static int WALL_GRILL_WEST = 838;

    public static int[] ROCKS = new int[]{ 849, 850, 851, 852, 860, 853, 854, 855, 859, 857, 858 };

    public static int HIJACK_ROCK = 856;

    public static int PASSAGE = 873;

    public static int CAGE_REMAINS = 871;

    public static int GATE_OF_IBAN = 722;

    public static int FLAMES_OF_ZAMORAK = 830;

    public static int GATE_OF_ZAMORAK = 875;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return (((((((((((Functions.inArray(obj.getID(), UndergroundPassObstaclesMap2.PILE_OF_MUD_MAP_LEVEL_2) || (obj.getID() == UndergroundPassObstaclesMap2.CRATE)) || Functions.inArray(obj.getID(), UndergroundPassObstaclesMap2.DUG_UP_SOIL)) || (obj.getID() == UndergroundPassObstaclesMap2.LEDGE)) || (obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_EAST)) || (obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_WEST)) || Functions.inArray(obj.getID(), UndergroundPassObstaclesMap2.ROCKS)) || (obj.getID() == UndergroundPassObstaclesMap2.HIJACK_ROCK)) || (obj.getID() == UndergroundPassObstaclesMap2.PASSAGE)) || (obj.getID() == UndergroundPassObstaclesMap2.CAGE_REMAINS)) || (obj.getID() == UndergroundPassObstaclesMap2.GATE_OF_IBAN)) || (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK)) || (obj.getID() == UndergroundPassObstaclesMap2.GATE_OF_ZAMORAK);
    }

    // 753, 3475
    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(obj.getID(), UndergroundPassObstaclesMap2.PILE_OF_MUD_MAP_LEVEL_2)) {
                        if (obj.getID() == UndergroundPassObstaclesMap2.PILE_OF_MUD_MAP_LEVEL_2[0]) {
                            return invokeOnNotify(Functions.questMessage(p, "you climb the pile of mud...", "it leads to a small tunnel..."), 1);
                        } else
                            if (obj.getID() == UndergroundPassObstaclesMap2.PILE_OF_MUD_MAP_LEVEL_2[1]) {
                                return invokeOnNotify(Functions.questMessage(p, "you climb the pile of mud"), 2);
                            } else
                                if (obj.getID() == UndergroundPassObstaclesMap2.PILE_OF_MUD_MAP_LEVEL_2[2]) {
                                    return invokeOnNotify(Functions.questMessage(p, "you climb the pile of mud"), 3);
                                } else
                                    if (obj.getID() == UndergroundPassObstaclesMap2.PILE_OF_MUD_MAP_LEVEL_2[3]) {
                                        return invokeOnNotify(Functions.questMessage(p, "you climb the pile of mud"), 4);
                                    } else
                                        if (obj.getID() == UndergroundPassObstaclesMap2.PILE_OF_MUD_MAP_LEVEL_2[4]) {
                                            return invokeOnNotify(Functions.questMessage(p, "you climb the pile of mud"), 5);
                                        } else
                                            if (obj.getID() == UndergroundPassObstaclesMap2.PILE_OF_MUD_MAP_LEVEL_2[5]) {
                                                return invokeOnNotify(Functions.questMessage(p, "you climb the pile of mud"), 6);
                                            }





                    } else
                        if (obj.getID() == UndergroundPassObstaclesMap2.CRATE) {
                            return invokeOnNotify(Functions.questMessage(p, "you search the crate"), 7);
                        } else
                            if (Functions.inArray(obj.getID(), UndergroundPassObstaclesMap2.DUG_UP_SOIL)) {
                                return invokeOnNotify(Functions.questMessage(p, "under the soil is a tunnel"), 8);
                            } else
                                if (obj.getID() == UndergroundPassObstaclesMap2.LEDGE) {
                                    if (command.equalsIgnoreCase("climb up")) {
                                        p.message("you climb the ledge");
                                        if (DataConversions.getRandom().nextInt(10) <= 1) {
                                            p.message("but you slip");
                                            p.damage(((int) (Functions.getCurrentLevel(p, Skills.HITS) / 42)) + 1);
                                            return endOnNotify(Functions.playerDialogue(p, null, "aargh"));
                                        } else {
                                            p.teleport(764, 3463);
                                        }
                                    } else {
                                        p.message("you take a few paces back...");
                                        p.message("and run torwards the ledge...");
                                        p.teleport(764, 3461);
                                        return invoke(11, 3);
                                    }
                                } else
                                    if (obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_EAST) {
                                        if (!p.getCache().hasKey("rope_wall_grill")) {
                                            return endOnNotify(Functions.questMessage(p, "the wall grill is too high", "you can't quite reach"));
                                        } else {
                                            return invokeOnNotify(Functions.questMessage(p, "you use the rope tied to the grill to pull yourself up", "you then climb across the grill to the otherside"), 12);
                                        }
                                    } else
                                        if (obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_WEST) {
                                            return invokeOnNotify(Functions.questMessage(p, "you climb across the grill to the otherside"), 13);
                                        } else
                                            if (Functions.inArray(obj.getID(), UndergroundPassObstaclesMap2.ROCKS)) {
                                                switch (obj.getID()) {
                                                    case 859 :
                                                    case 858 :
                                                        return endOnNotify(UndergroundPassObstaclesMap1.doRock(obj, p, ((int) (Functions.getCurrentLevel(p, Skills.HITS) / 5)) + 5, false, 5));// fall side 5.

                                                    case 854 :
                                                    case 853 :
                                                    case 855 :
                                                    case 857 :
                                                        return endOnNotify(UndergroundPassObstaclesMap1.doRock(obj, p, ((int) (Functions.getCurrentLevel(p, Skills.HITS) / 5)) + 5, false, 4));// fall side 4.

                                                    case 852 :
                                                        return endOnNotify(UndergroundPassObstaclesMap1.doRock(obj, p, ((int) (Functions.getCurrentLevel(p, Skills.HITS) / 5)) + 5, false, 3));// fall side 3.

                                                    case 851 :
                                                        return endOnNotify(UndergroundPassObstaclesMap1.doRock(obj, p, ((int) (Functions.getCurrentLevel(p, Skills.HITS) / 5)) + 5, false, 2));// fall side 2.

                                                    default :
                                                        return endOnNotify(UndergroundPassObstaclesMap1.doRock(obj, p, ((int) (Functions.getCurrentLevel(p, Skills.HITS) / 5)) + 5, false, 1));// fall side 1.

                                                }
                                            } else
                                                if (obj.getID() == UndergroundPassObstaclesMap2.HIJACK_ROCK) {
                                                    p.setBusyTimer(1);
                                                    p.message("you climb onto the rock");
                                                    if (DataConversions.getRandom().nextInt(5) == 4) {
                                                        p.message("but you slip");
                                                        p.damage(((int) (Functions.getCurrentLevel(p, Skills.HITS) / 5)) + 5);
                                                        p.teleport(734, 3483);
                                                        return endOnNotify(Functions.playerDialogue(p, null, "aargh"));
                                                    } else {
                                                        if (p.getX() == 734) {
                                                            p.teleport(735, 3479);
                                                        } else {
                                                            p.teleport(734, 3480);
                                                        }
                                                        p.message("and step down the other side");
                                                    }
                                                } else
                                                    if (obj.getID() == UndergroundPassObstaclesMap2.PASSAGE) {
                                                        return invokeOnNotify(Functions.questMessage(p, "you walk down the passage way"), 14);
                                                    } else
                                                        if (obj.getID() == UndergroundPassObstaclesMap2.CAGE_REMAINS) {
                                                            if ((p.getQuestStage(Quests.UNDERGROUND_PASS) >= 5) || (p.getQuestStage(Quests.UNDERGROUND_PASS) == (-1))) {
                                                                return endOnNotify(Functions.questMessage(p, "you search the cage remains", "nothing remains"));
                                                            }
                                                            if (!Functions.hasItem(p, ItemId.UNDERGROUND_PASS_UNICORN_HORN.id())) {
                                                                return invoke(15, 3);
                                                            } else {
                                                                p.message("nothing remains");
                                                                return null;
                                                            }
                                                        } else
                                                            if (obj.getID() == UndergroundPassObstaclesMap2.GATE_OF_IBAN) {
                                                                p.message("you pull on the great door");
                                                                if (((p.getCache().hasKey("flames_of_zamorak1") && p.getCache().hasKey("flames_of_zamorak2")) && (p.getCache().hasKey("flames_of_zamorak3") && (p.getCache().getInt("flames_of_zamorak3") >= 2))) || Functions.atQuestStages(p, Quests.UNDERGROUND_PASS, 7, 8, -1)) {
                                                                    return invokeOnNotify(Functions.questMessage(p, "from behind the door you hear cry's and moans"), 16);
                                                                } else {
                                                                    p.message("the door refuses to open");
                                                                    return null;
                                                                }
                                                            } else
                                                                if (obj.getID() == UndergroundPassObstaclesMap2.GATE_OF_ZAMORAK) {
                                                                    Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), 723, obj.getDirection(), obj.getType()));
                                                                    Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 3000);
                                                                    p.teleport(766, 3417);
                                                                    return invokeOnNotify(Functions.questMessage(p, "you open the huge wooden door"), 18);
                                                                } else
                                                                    if (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK) {
                                                                        return invokeOnNotify(Functions.questMessage(p, "you search the stone structure"), 19);
                                                                    }












                    return null;
                });
                addState(1, () -> {
                    p.teleport(727, 3448);
                    return endOnNotify(Functions.questMessage(p, "..ending at the well entrance"));
                });
                addState(2, () -> {
                    p.teleport(753, 3481);
                    return null;
                });
                addState(3, () -> {
                    p.teleport(753, 3475);
                    return null;
                });
                addState(4, () -> {
                    p.teleport(743, 3483);
                    return null;
                });
                addState(5, () -> {
                    p.teleport(740, 3476);
                    return null;
                });
                addState(6, () -> {
                    p.teleport(735, 3478);
                    return null;
                });
                addState(7, () -> {
                    if (!p.getCache().hasKey("crate_food")) {
                        p.message("inside you find some food");
                        Functions.addItem(p, ItemId.SALMON.id(), 2);
                        Functions.addItem(p, ItemId.MEAT_PIE.id(), 2);
                        p.getCache().store("crate_food", true);
                    } else {
                        p.message("but you find nothing");
                    }
                    return null;
                });
                addState(8, () -> {
                    p.message("would you like to enter?");
                    return invokeOnNotify(Functions.showPlayerMenu(getPlayerOwner(), "no, im scared of small spaces", "yep, let's do it"), 9);
                });
                addState(9, () -> {
                    final int menu = ((int) (getNotifyEvent().getObjectOut("int_option")));
                    if (menu == 1) {
                        return invokeOnNotify(Functions.questMessage(p, "you climb into the small tunnel"), 10);
                    }
                    return null;
                });
                addState(10, () -> {
                    if (obj.getID() == UndergroundPassObstaclesMap2.DUG_UP_SOIL[1])
                        p.teleport(745, 3457);
                    else
                        p.teleport(747, 3470);

                    p.message("and crawl into a small dark passage");
                    return null;
                });
                addState(11, () -> {
                    p.message("you land way short of the other platform");
                    p.damage(((int) (Functions.getCurrentLevel(p, Skills.HITS) / 5)) + 5);
                    p.teleport(764, 3467);
                    return endOnNotify(Functions.playerDialogue(p, null, "ooof"));
                });
                addState(12, () -> {
                    p.teleport(762, 3472);
                    return null;
                });
                addState(13, () -> {
                    p.teleport(766, 3463);
                    return null;
                });
                addState(14, () -> {
                    p.message("you step on a pressure trigger");
                    p.message("it's a trap");
                    if ((obj.getX() == 737) || (obj.getX() == 735)) {
                        p.teleport(737, 3489);
                    } else
                        if (obj.getX() == 733) {
                            p.teleport(733, 3489);
                        }

                    p.getWorld().replaceGameObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), 826, obj.getDirection(), obj.getType()));
                    p.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
                    p.damage(((int) (Functions.getCurrentLevel(p, Skills.HITS) / 5)) + 5);
                    return endOnNotify(Functions.playerDialogue(p, null, "aaarghh"));
                });
                addState(15, () -> {
                    p.message("all that remains is a damaged horn");
                    Functions.addItem(p, ItemId.UNDERGROUND_PASS_UNICORN_HORN.id(), 1);
                    return null;
                });
                addState(16, () -> {
                    p.message("the door slowly creeks open");
                    Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), 723, obj.getDirection(), obj.getType()));
                    Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 3000);
                    p.teleport(766, 3417);
                    return invoke(17, 2);
                });
                addState(17, () -> {
                    p.teleport(770, 3417);
                    p.message("you walk into the darkness");
                    return null;
                });
                addState(18, () -> {
                    p.teleport(763, 3417);
                    p.message("and walk through");
                    return null;
                });
                addState(19, () -> {
                    p.message("on the side you find an old inscription");
                    p.message("it reads...");
                    ActionSender.sendBox(p, "@red@While I sense the soft beating of a good heart I will not open% %@red@Feed me three crests of the blessed warriors, and the%@red@creatures remains% %@red@Throw them to me as an offering, a gift of hatred, a token% %@red@Then finally rejoice as all goodness dies in my flames", true);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
        return Functions.inArray(obj.getID(), UndergroundPassObstaclesMap2.RAILINGS);
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(obj.getID(), UndergroundPassObstaclesMap2.RAILINGS)) {
                        if (click == 0) {
                            if (obj.getID() == 168) {
                                return endOnNotify(Functions.questMessage(getPlayerOwner(), "the cage door has been sealed shut", "the poor unicorn can't escape"));
                            }
                            getPlayerOwner().message("you attempt to pick the lock");
                            if ((obj.getID() == 169) && (Functions.getCurrentLevel(getPlayerOwner(), Skills.THIEVING) < 50)) {
                                getPlayerOwner().message("you need a level of 50 thieving to pick this lock");
                                return null;
                            }
                            getPlayerOwner().setBusyTimer(3);
                            getPlayerOwner().message("You manage to pick the lock");
                            getPlayerOwner().message("you walk through");
                            if (obj.getDirection() == 0) {
                                if (obj.getY() == getPlayerOwner().getY())
                                    getPlayerOwner().teleport(obj.getX(), obj.getY() - 1);
                                else
                                    getPlayerOwner().teleport(obj.getX(), obj.getY());

                            }
                            if (obj.getDirection() == 1) {
                                if (obj.getX() == getPlayerOwner().getX())
                                    getPlayerOwner().teleport(obj.getX() - 1, obj.getY());
                                else
                                    getPlayerOwner().teleport(obj.getX(), obj.getY());

                            }
                            getPlayerOwner().incExp(Skills.THIEVING, 15, true);
                            return invoke(1, 3);
                        } else
                            if (click == 1) {
                                if (obj.getID() == 168) {
                                    return invokeOnNotify(Functions.questMessage(getPlayerOwner(), "you search the cage"), 2);
                                }
                                getPlayerOwner().message("the cage has been locked");
                            }

                    }
                    return null;
                });
                addState(1, () -> {
                    getPlayerOwner().message("the cage slams shut behind you");
                    return null;
                });
                addState(2, () -> {
                    if (!Functions.hasItem(getPlayerOwner(), ItemId.RAILING.id())) {
                        getPlayerOwner().message("you find a loose railing lying on the floor");
                        Functions.addItem(getPlayerOwner(), ItemId.RAILING.id(), 1);
                    } else
                        getPlayerOwner().message("but you find nothing");

                    return null;
                });
            }
        };
    }
}

