package com.openrsc.server.plugins.quests.members.undergroundpass.mechanism;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.quests.members.undergroundpass.obstacles.UndergroundPassObstaclesMap2;
import java.util.concurrent.Callable;


public class UndergroundPassMechanismMap2 implements InvUseOnObjectListener , InvUseOnObjectExecutiveListener {
    /**
     * ITEMS_TO_FLAMES: Unicorn horn, coat of arms red and blue.
     */
    private static int[] ITEMS_TO_FLAMES = new int[]{ ItemId.UNDERGROUND_PASS_UNICORN_HORN.id(), ItemId.COAT_OF_ARMS_RED.id(), ItemId.COAT_OF_ARMS_BLUE.id() };

    /**
     * OBJECT IDs
     */
    private static int BOULDER = 867;

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return (((((obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_EAST) && (item.getID() == ItemId.ROPE.id())) || ((obj.getID() == UndergroundPassObstaclesMap2.PASSAGE) && (item.getID() == ItemId.PLANK.id()))) || ((obj.getID() == UndergroundPassMechanismMap2.BOULDER) && (item.getID() == ItemId.RAILING.id()))) || ((obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK) && Functions.inArray(item.getID(), UndergroundPassMechanismMap2.ITEMS_TO_FLAMES))) || ((obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK) && (item.getID() == ItemId.STAFF_OF_IBAN.id()));
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_EAST) && (item.getID() == ItemId.ROPE.id())) {
                        if ((p.getX() == 763) && (p.getY() == 3463)) {
                            p.message("you can't reach the grill from here");
                        } else {
                            Functions.___message(p, "you tie the rope to the grill...");
                            p.message("..and poke it through to the otherside");
                            if (!p.getCache().hasKey("rope_wall_grill")) {
                                p.getCache().store("rope_wall_grill", true);
                            }
                        }
                    } else
                        if ((item.getID() == ItemId.PLANK.id()) && (obj.getID() == UndergroundPassObstaclesMap2.PASSAGE)) {
                            p.message("you carefully place the planks over the pressure triggers");
                            p.message("you walk across the wooden planks");
                            Functions.removeItem(p, ItemId.PLANK.id(), 1);
                            p.teleport(735, 3489);
                            Functions.sleep(850);
                            if (obj.getX() == 737) {
                                p.teleport(732, 3489);
                            } else
                                if (obj.getX() == 733) {
                                    p.teleport(738, 3489);
                                }

                        } else
                            if ((obj.getID() == UndergroundPassMechanismMap2.BOULDER) && (item.getID() == ItemId.RAILING.id())) {
                                Functions.___message(p, "you use the pole as leverage...", "..and tip the bolder onto its side");
                                Functions.removeObject(obj);
                                Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 5000);
                                p.message("it tumbles down the slope");
                                if (p.getQuestStage(Quests.UNDERGROUND_PASS) == 3) {
                                    p.updateQuestStage(Quests.UNDERGROUND_PASS, 4);
                                }
                            } else
                                if ((obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK) && Functions.inArray(item.getID(), UndergroundPassMechanismMap2.ITEMS_TO_FLAMES)) {
                                    Functions.___message(p, ("you throw the " + item.getDef(p.getWorld()).getName().toLowerCase()) + " into the flames");
                                    if (!Functions.atQuestStages(p, Quests.UNDERGROUND_PASS, 7, 8, -1)) {
                                        if ((!p.getCache().hasKey("flames_of_zamorak1")) && (item.getID() == ItemId.UNDERGROUND_PASS_UNICORN_HORN.id())) {
                                            p.getCache().store("flames_of_zamorak1", true);
                                        }
                                        if ((!p.getCache().hasKey("flames_of_zamorak2")) && (item.getID() == ItemId.COAT_OF_ARMS_RED.id())) {
                                            p.getCache().store("flames_of_zamorak2", true);
                                        }
                                        int stage = 0;
                                        if (item.getID() == ItemId.COAT_OF_ARMS_BLUE.id()) {
                                            if (!p.getCache().hasKey("flames_of_zamorak3")) {
                                                p.getCache().set("flames_of_zamorak3", 1);
                                            } else {
                                                stage = p.getCache().getInt("flames_of_zamorak3");
                                                if (stage < 2)
                                                    p.getCache().set("flames_of_zamorak3", stage + 1);

                                            }
                                        }
                                    }
                                    Functions.removeItem(p, item.getID(), 1);
                                    p.message("you hear a howl in the distance");
                                } else
                                    if ((obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK) && (item.getID() == ItemId.STAFF_OF_IBAN.id())) {
                                        Functions.___message(p, "you hold the staff above the well");
                                        Functions.displayTeleportBubble(p, p.getX(), p.getY(), true);
                                        p.message("and feel the power of zamorak flow through you");
                                        p.getCache().set("Iban blast_casts", 25);
                                    }




                    return null;
                });
            }
        };
    }
}

