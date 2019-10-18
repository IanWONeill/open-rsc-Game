package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.Area;
import com.openrsc.server.model.world.Areas;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class UndergroundPassObstaclesMap3 implements ObjectActionListener , ObjectActionExecutiveListener {
    private static final Logger LOGGER = LogManager.getLogger(UndergroundPassObstaclesMap3.class);

    /**
     * OBJECT IDs
     */
    public static int[] CAGES = new int[]{ 888, 887 };

    public static int ZAMORAKIAN_TEMPLE_DOOR = 869;

    public static final int DEMONS_CHEST_OPEN = 911;

    public static final int DEMONS_CHEST_CLOSED = 912;

    public static final int[] PIT_COORDS = new int[]{ 802, 3469 };

    public static final Area boundArea = new Area(UndergroundPassObstaclesMap3.PIT_COORDS[0] - 24, UndergroundPassObstaclesMap3.PIT_COORDS[0] + 24, UndergroundPassObstaclesMap3.PIT_COORDS[1] - 24, UndergroundPassObstaclesMap3.PIT_COORDS[1] + 24);

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return (Functions.inArray(obj.getID(), UndergroundPassObstaclesMap3.CAGES) || (obj.getID() == UndergroundPassObstaclesMap3.DEMONS_CHEST_CLOSED)) || (obj.getID() == UndergroundPassObstaclesMap3.ZAMORAKIAN_TEMPLE_DOOR);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(obj.getID(), UndergroundPassObstaclesMap3.CAGES)) {
                        if (obj.getID() == UndergroundPassObstaclesMap3.CAGES[1]) {
                            p.message("the man seems to be entranced");
                            Functions.___message(p, "the cage is locked");
                            Functions.sleep(1600);
                            Functions.___message(p, "you search through the bottom of the cage");
                            if (!p.getCache().hasKey("cons_on_doll")) {
                                p.message("but the souless bieng bites into your arm");
                                if (p.getInventory().wielding(ItemId.KLANKS_GAUNTLETS.id())) {
                                    p.message("klanks gaunlett protects you");
                                } else {
                                    p.damage((((int) (Functions.getCurrentLevel(p, Skills.HITS))) / 10) + 5);
                                    Functions.___playerTalk(p, null, "aaarrgghh");
                                }
                            }
                            if ((!Functions.hasItem(p, ItemId.IBANS_CONSCIENCE.id())) && (!p.getCache().hasKey("cons_on_doll"))) {
                                p.message("you find the remains of a dove");
                                Functions.addItem(p, ItemId.IBANS_CONSCIENCE.id(), 1);
                            } else {
                                // kosher was separated lol
                                if (p.getInventory().wielding(ItemId.KLANKS_GAUNTLETS.id())) {
                                    p.message("but you find find nothing");
                                } else {
                                    p.message("you find nothing");
                                }
                            }
                        } else
                            if (obj.getID() == UndergroundPassObstaclesMap3.CAGES[0]) {
                                p.message("the man seems to be entranced");
                                Functions.___message(p, "the cage is locked");
                                Functions.sleep(1600);
                                Functions.___message(p, "you search through the bottom of the cage");
                                p.message("but the souless bieng bites into your arm");
                                if (p.getInventory().wielding(ItemId.KLANKS_GAUNTLETS.id())) {
                                    p.message("klanks gaunlett protects you");
                                    p.message("but you find find nothing");
                                } else {
                                    p.damage((((int) (Functions.getCurrentLevel(p, Skills.HITS))) / 10) + 5);
                                    Functions.___playerTalk(p, null, "aaarrgghh");
                                    p.message("you find nothing");
                                }
                            }

                    } else
                        if (obj.getID() == UndergroundPassObstaclesMap3.DEMONS_CHEST_CLOSED) {
                            Functions.___message(p, "you attempt to open the chest");
                            if (((Functions.hasItem(p, ItemId.AMULET_OF_OTHAINIAN.id()) && Functions.hasItem(p, ItemId.AMULET_OF_DOOMION.id())) && Functions.hasItem(p, ItemId.AMULET_OF_HOLTHION.id())) && (!p.getCache().hasKey("shadow_on_doll"))) {
                                Functions.___message(p, "the three amulets glow red in your satchel");
                                Functions.removeItem(p, ItemId.AMULET_OF_OTHAINIAN.id(), 1);
                                Functions.removeItem(p, ItemId.AMULET_OF_DOOMION.id(), 1);
                                Functions.removeItem(p, ItemId.AMULET_OF_HOLTHION.id(), 1);
                                p.message("you place them on the chest and the chest opens");
                                Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), UndergroundPassObstaclesMap3.DEMONS_CHEST_OPEN, obj.getDirection(), obj.getType()));
                                Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 2000);
                                Functions.sleep(1000);
                                p.message("inside you find a strange dark liquid");
                                Functions.addItem(p, ItemId.IBANS_SHADOW.id(), 1);
                            } else {
                                p.message("but it's magically sealed");
                            }
                        } else
                            if (obj.getID() == UndergroundPassObstaclesMap3.ZAMORAKIAN_TEMPLE_DOOR) {
                                if (p.getX() <= 792) {
                                    if (p.getQuestStage(Quests.UNDERGROUND_PASS) == (-1)) {
                                        Functions.___message(p, "the temple is in ruins...");
                                        p.message("...you cannot enter");
                                        return null;
                                    }
                                    if (p.getInventory().wielding(ItemId.ROBE_OF_ZAMORAK_TOP.id()) && p.getInventory().wielding(ItemId.ROBE_OF_ZAMORAK_BOTTOM.id())) {
                                        Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), 914, obj.getDirection(), obj.getType()));
                                        Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 3000);
                                        p.teleport(792, 3469);
                                        Functions.sleep(600);
                                        p.teleport(795, 3469);
                                        Functions.___message(p, "you pull open the large doors");
                                        p.message("and walk into the temple");
                                        if ((p.getQuestStage(Quests.UNDERGROUND_PASS) == 7) || (((p.getCache().hasKey("poison_on_doll") && p.getCache().hasKey("cons_on_doll")) && p.getCache().hasKey("ash_on_doll")) && p.getCache().hasKey("shadow_on_doll"))) {
                                            if (p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
                                                p.updateQuestStage(Quests.UNDERGROUND_PASS, 7);
                                            }
                                            p.message("Iban seems to sense danger");
                                            Functions.___message(p, "@yel@Iban: who dares bring the witches magic into my temple");
                                            Functions.___message(p, "his eyes fixate on you as he raises his arm");
                                            Functions.___message(p, "@yel@Iban: an imposter dares desecrate this sacred place..", "@yel@Iban: ..home to the only true child of zamorak", "@yel@Iban: join the damned, mortal");
                                            p.message("iban raises his staff to the air");
                                            Functions.___message(p, "a blast of energy comes from ibans staff");
                                            p.message("you are hit by ibans magic bolt");
                                            Functions.displayTeleportBubble(p, p.getX() + 1, p.getY(), true);
                                            p.damage((((int) (Functions.getCurrentLevel(p, Skills.HITS))) / 7) + 1);
                                            Functions.___playerTalk(p, null, "aarrgh");
                                            Functions.___message(p, "@yel@Iban:die foolish mortal");
                                            long start = System.currentTimeMillis();
                                            Area area = Areas.getArea("ibans_room");
                                            try {
                                                while (true) {
                                                    /* Time-out fail, handle appropriately */
                                                    if (((System.currentTimeMillis() - start) > ((1000 * 60) * 2)) && p.getLocation().inBounds(794, 3467, 799, 3471)) {
                                                        p.message("you're blasted out of the temple");
                                                        p.message("@yel@Iban: and stay out");
                                                        p.teleport(790, 3469);
                                                        break;
                                                    }
                                                    /* If player has logged out or not region area */
                                                    if (p.isRemoved() || (!p.getLocation().inBounds(UndergroundPassObstaclesMap3.boundArea.getMinX(), UndergroundPassObstaclesMap3.boundArea.getMinY(), UndergroundPassObstaclesMap3.boundArea.getMaxX(), UndergroundPassObstaclesMap3.boundArea.getMaxY()))) {
                                                        break;
                                                    }
                                                    /* ends it */
                                                    if (p.getAttribute("iban_bubble_show", false)) {
                                                        break;
                                                    }
                                                    /* Get random point on the area */
                                                    Point blastPosition = new Point(DataConversions.random(area.getMinX(), area.getMaxX()), DataConversions.random(area.getMinY(), area.getMaxY()));
                                                    ActionSender.sendTeleBubble(p, blastPosition.getX(), blastPosition.getY(), true);
                                                    if (p.getLocation().withinRange(blastPosition, 1)) {
                                                        /* Blast hit */
                                                        p.damage((((int) (Functions.getCurrentLevel(p, Skills.HITS))) / 6) + 2);
                                                        p.teleport(795, 3469);// insert the coords

                                                        Functions.___playerTalk(p, null, "aarrgh");
                                                        p.message("you're blasted back to the door");
                                                    }
                                                    Functions.sleep(650);
                                                } 
                                            } catch (Exception e) {
                                                UndergroundPassObstaclesMap3.LOGGER.catching(e);
                                            }
                                        } else {
                                            p.message("inside iban stands preaching at the alter");
                                        }
                                    } else {
                                        Functions.___message(p, "The door refuses to open");
                                        p.message("only followers of zamorak may enter");
                                    }
                                } else {
                                    Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), 914, obj.getDirection(), obj.getType()));
                                    Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 3000);
                                    p.teleport(794, 3469);
                                    Functions.sleep(600);
                                    p.teleport(791, 3469);
                                    Functions.sleep(1000);
                                    p.message("you pull open the large doors");
                                    p.message("and walk out of the temple");
                                }
                            }


                    return null;
                });
            }
        };
    }
}

