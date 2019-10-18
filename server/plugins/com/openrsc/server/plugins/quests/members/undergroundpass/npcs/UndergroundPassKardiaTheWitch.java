package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class UndergroundPassKardiaTheWitch implements InvUseOnWallObjectListener , ObjectActionListener , PickupListener , WallObjectActionListener , InvUseOnWallObjectExecutiveListener , ObjectActionExecutiveListener , PickupExecutiveListener , WallObjectActionExecutiveListener {
    /**
     * OBJECT IDs
     */
    private static int WITCH_RAILING = 172;

    private static int WITCH_DOOR = 173;

    private static int WITCH_CHEST = 885;

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
        return (obj.getID() == UndergroundPassKardiaTheWitch.WITCH_RAILING) || (obj.getID() == UndergroundPassKardiaTheWitch.WITCH_DOOR);
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == UndergroundPassKardiaTheWitch.WITCH_RAILING) {
                        Functions.___message(p, "inside you see Kardia the witch");
                        p.message("her appearence make's you feel quite ill");
                    } else
                        if (obj.getID() == UndergroundPassKardiaTheWitch.WITCH_DOOR) {
                            if (click == 0) {
                                if (p.getCache().hasKey("kardia_cat")) {
                                    p.message("you open the door");
                                    Functions.doDoor(obj, p);
                                    Functions.___message(p, "and walk through");
                                    p.message("the witch is busy talking to the cat");
                                } else {
                                    Npc witch = Functions.getNearestNpc(p, NpcId.KARDIA_THE_WITCH.id(), 5);
                                    p.message("you reach to open the door");
                                    Functions.___npcTalk(p, witch, "get away...far away from here");
                                    Functions.sleep(1000);
                                    p.message("the witch raises her hands above her");
                                    Functions.displayTeleportBubble(p, p.getX(), p.getY(), true);
                                    p.damage((((int) (Functions.getCurrentLevel(p, Skills.HITS))) / 5) + 5);// 6 lowest, 25 max.

                                    Functions.___npcTalk(p, witch, "haa haa.. die mortal");
                                }
                            } else
                                if (click == 1) {
                                    if (Functions.hasItem(p, ItemId.KARDIA_CAT.id()) && (!p.getCache().hasKey("kardia_cat"))) {
                                        Functions.___message(p, "you place the cat by the door");
                                        Functions.removeItem(p, ItemId.KARDIA_CAT.id(), 1);
                                        p.teleport(776, 3535);
                                        Functions.___message(p, "you knock on the door and hide around the corner");
                                        p.message("the witch takes the cat inside");
                                        if (!p.getCache().hasKey("kardia_cat")) {
                                            p.getCache().store("kardia_cat", true);
                                        }
                                    } else
                                        if (p.getCache().hasKey("kardia_cat")) {
                                            Functions.___message(p, "there is no reply");
                                            p.message("inside you can hear the witch talking to her cat");
                                        } else {
                                            Functions.___message(p, "you knock on the door");
                                            p.message("there is no reply");
                                        }

                                }

                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return (i.getID() == ItemId.KARDIA_CAT.id()) && Functions.hasItem(p, ItemId.KARDIA_CAT.id());
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((i.getID() == ItemId.KARDIA_CAT.id()) && Functions.hasItem(p, ItemId.KARDIA_CAT.id())) {
                        Functions.___message(p, "it's not very nice to squeeze one cat into a satchel");
                        p.message("...two's just plain cruel!");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnWallObject(GameObject obj, Item item, Player p) {
        return (obj.getID() == UndergroundPassKardiaTheWitch.WITCH_DOOR) && (item.getID() == ItemId.KARDIA_CAT.id());
    }

    @Override
    public GameStateEvent onInvUseOnWallObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == UndergroundPassKardiaTheWitch.WITCH_DOOR) && (item.getID() == ItemId.KARDIA_CAT.id())) {
                        if (!p.getCache().hasKey("kardia_cat")) {
                            Functions.___message(p, "you place the cat by the door");
                            Functions.removeItem(p, ItemId.KARDIA_CAT.id(), 1);
                            p.teleport(776, 3535);
                            Functions.___message(p, "you knock on the door and hide around the corner");
                            p.message("the witch takes the cat inside");
                            if (!p.getCache().hasKey("kardia_cat")) {
                                p.getCache().store("kardia_cat", true);
                            }
                        } else {
                            Functions.___message(p, "the witch is busy playing...");
                            p.message("with her other cat");
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == UndergroundPassKardiaTheWitch.WITCH_CHEST;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == UndergroundPassKardiaTheWitch.WITCH_CHEST) {
                        Functions.___message(p, "you search the chest");
                        if ((p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) && (!p.getCache().hasKey("doll_of_iban"))) {
                            p.message("..inside you find a book a wooden doll..");
                            p.message("...and two potions");
                            Functions.addItem(p, ItemId.A_DOLL_OF_IBAN.id(), 1);
                            Functions.addItem(p, ItemId.OLD_JOURNAL.id(), 1);
                            Functions.addItem(p, ItemId.FULL_SUPER_ATTACK_POTION.id(), 1);
                            Functions.addItem(p, ItemId.FULL_STAT_RESTORATION_POTION.id(), 1);
                            if (!p.getCache().hasKey("doll_of_iban")) {
                                p.getCache().store("doll_of_iban", true);
                            }
                        } else {
                            p.message("but you find nothing of interest");
                        }
                    }
                    return null;
                });
            }
        };
    }
}

