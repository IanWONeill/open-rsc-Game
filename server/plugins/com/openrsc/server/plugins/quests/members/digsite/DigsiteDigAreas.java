package com.openrsc.server.plugins.quests.members.digsite;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


/**
 *
 *
 * @author Imposter/Fate
 */
public class DigsiteDigAreas implements InvActionListener , InvUseOnObjectListener , ObjectActionListener , InvActionExecutiveListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener {
    private static int[] SOIL = new int[]{ 1065, 1066, 1067 };

    private static int ROCK = 1059;

    private int[] TRAINING_AREA_ITEMS = new int[]{ ItemId.NOTHING.id(), ItemId.NOTHING_INTEREST.id(), ItemId.VASE.id(), ItemId.BROKEN_ARROW.id(), ItemId.COINS.id(), ItemId.CRACKED_ROCK_SAMPLE.id(), ItemId.A_LUMP_OF_CHARCOAL.id() };

    private int[] DIGSITE_LEVEL1_ITEMS = new int[]{ ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.OPAL.id(), ItemId.OLD_BOOT.id(), ItemId.OLD_TOOTH.id(), ItemId.BROKEN_GLASS.id(), ItemId.COPPER_ORE.id(), ItemId.ROTTEN_APPLES.id(), ItemId.BUTTONS.id(), ItemId.RUSTY_SWORD.id(), ItemId.VASE.id() };

    private int[] DIGSITE_LEVEL2_ITEMS = new int[]{ ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.PURPLEDYE.id(), ItemId.POT.id(), ItemId.CLAY.id(), ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id(), ItemId.RATS_TAIL.id(), ItemId.BROKEN_STAFF.id(), ItemId.DAMAGED_ARMOUR_2.id(), ItemId.JUG.id(), ItemId.OLD_BOOT.id() };

    private int[] DIGSITE_LEVEL3_ITEMS = new int[]{ ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.DAMAGED_ARMOUR_1.id(), ItemId.BROKEN_STAFF.id(), ItemId.TALISMAN_OF_ZAROS.id(), ItemId.BROKEN_ARROW.id(), ItemId.BRONZE_SPEAR.id(), ItemId.PIE_DISH.id(), ItemId.BUTTONS.id(), ItemId.OLD_TOOTH.id(), ItemId.COINS.id(), ItemId.NEEDLE.id(), ItemId.CLAY.id(), ItemId.IRON_THROWING_KNIFE.id(), ItemId.MEDIUM_BLACK_HELMET.id(), ItemId.CERAMIC_REMAINS.id(), ItemId.BELT_BUCKLE.id(), ItemId.OLD_BOOT.id(), ItemId.PURPLEDYE.id() };

    private static boolean getLevel3Digsite(Player p) {
        // Top North DONE. + WEST MIDDLE AREA DONE
        return p.getLocation().inBounds(10, 495, 14, 499) || p.getLocation().inBounds(23, 518, 28, 524);
    }

    static void doDigsiteItemMessages(Player p, int item) {
        if (item == ItemId.NOTHING.id()) {
            p.message("You find nothing");
        } else
            if (item == ItemId.NOTHING_INTEREST.id()) {
                p.message("You find nothing of interest");
            } else
                if (item == ItemId.BONES.id()) {
                    p.message("You find some bones");
                } else
                    if (item == ItemId.PURPLEDYE.id()) {
                        p.message("You find some purple dye");
                    } else
                        if (item == ItemId.POT.id()) {
                            p.message("You find an old pot");
                        } else
                            if (item == ItemId.CLAY.id()) {
                                p.message("You find some clay");
                            } else
                                if ((item == ItemId.BROKEN_GLASS.id()) || (item == ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id())) {
                                    p.message("You find some broken glass");
                                } else
                                    if (item == ItemId.RATS_TAIL.id()) {
                                        p.message("You find a rat's tail");
                                    } else
                                        if (item == ItemId.BROKEN_STAFF.id()) {
                                            p.message("You find a broken staff");
                                        } else
                                            if ((item == ItemId.DAMAGED_ARMOUR_1.id()) || (item == ItemId.DAMAGED_ARMOUR_2.id())) {
                                                p.message("You find some old armour");
                                            } else
                                                if (item == ItemId.JUG.id()) {
                                                    p.message("You find an old jug");
                                                } else
                                                    if (item == ItemId.OLD_BOOT.id()) {
                                                        p.message("You find an old boot");
                                                    } else
                                                        if (item == ItemId.VASE.id()) {
                                                            p.message("You find an old vase");
                                                        } else
                                                            if (item == ItemId.COINS.id()) {
                                                                if (DigsiteDigAreas.getLevel3Digsite(p)) {
                                                                    p.message("You find some coins");
                                                                } else {
                                                                    p.message("You find a coin");
                                                                }
                                                            } else
                                                                if (item == ItemId.CRACKED_ROCK_SAMPLE.id()) {
                                                                    p.message("You find a broken rock sample");
                                                                } else
                                                                    if (item == ItemId.A_LUMP_OF_CHARCOAL.id()) {
                                                                        p.message("You find some charcoal");
                                                                    } else
                                                                        if (item == ItemId.BROKEN_ARROW.id()) {
                                                                            p.message("You find a broken arrow");
                                                                        } else
                                                                            if (item == ItemId.OPAL.id()) {
                                                                                p.message("You find an opal");
                                                                            } else
                                                                                if (item == ItemId.OLD_TOOTH.id()) {
                                                                                    p.message("You find an old tooth");
                                                                                } else
                                                                                    if (item == ItemId.COPPER_ORE.id()) {
                                                                                        p.message("You find some copper ore");
                                                                                    } else
                                                                                        if (item == ItemId.ROTTEN_APPLES.id()) {
                                                                                            p.message("You find a rotten apple");
                                                                                        } else
                                                                                            if (item == ItemId.BUTTONS.id()) {
                                                                                                p.message("You find some buttons");
                                                                                            } else
                                                                                                if (item == ItemId.RUSTY_SWORD.id()) {
                                                                                                    p.message("You find a rusty sword");
                                                                                                } else
                                                                                                    if (item == ItemId.TALISMAN_OF_ZAROS.id()) {
                                                                                                        p.message("You find a strange talisman");
                                                                                                    } else
                                                                                                        if (item == ItemId.BRONZE_SPEAR.id()) {
                                                                                                            p.message("You find a bronze spear");
                                                                                                        } else
                                                                                                            if (item == ItemId.PIE_DISH.id()) {
                                                                                                                p.message("You find a pie dish");
                                                                                                            } else
                                                                                                                if (item == ItemId.NEEDLE.id()) {
                                                                                                                    p.message("You find a needle");
                                                                                                                } else
                                                                                                                    if (item == ItemId.IRON_THROWING_KNIFE.id()) {
                                                                                                                        p.message("You find a throwing knife");
                                                                                                                    } else
                                                                                                                        if (item == ItemId.MEDIUM_BLACK_HELMET.id()) {
                                                                                                                            p.message("You find an black helmet");
                                                                                                                        } else
                                                                                                                            if (item == ItemId.CERAMIC_REMAINS.id()) {
                                                                                                                                p.message("You find some old pottery");
                                                                                                                            } else
                                                                                                                                if (item == ItemId.BELT_BUCKLE.id()) {
                                                                                                                                    p.message("You find a belt buckle");
                                                                                                                                } else
                                                                                                                                    if (item == ItemId.IRON_DAGGER.id()) {
                                                                                                                                        p.message("You find a dagger");
                                                                                                                                    }































    }

    private boolean getTrainingAreas(Player p) {
        // EAST DONE + WEST DONE
        return p.getLocation().inBounds(13, 526, 17, 529) || p.getLocation().inBounds(24, 526, 27, 529);
    }

    private boolean getLevel2Digsite(Player p) {
        // WEST MIDDLE SMALL WINCH AREA DONE + EAST NORTH DONE + WEST NORTH DONE
        return (p.getLocation().inBounds(24, 514, 26, 516) || p.getLocation().inBounds(14, 506, 15, 509)) || p.getLocation().inBounds(20, 505, 27, 509);
    }

    private boolean getLevel1Digsite(Player p) {
        // MIDDLE DONE + EAST MIDDLE DONE
        return p.getLocation().inBounds(19, 516, 21, 526) || p.getLocation().inBounds(13, 516, 17, 524);
    }

    private boolean getDigsite(Player p) {
        return p.getLocation().inBounds(5, 492, 42, 545);// ENTIRE DIGSITE AREA used for spade

    }

    private void doSpade(Player p, Item item, GameObject obj) {
        Npc workmanCheck = Functions.getNearestNpc(p, NpcId.WORKMAN.id(), 15);
        if (workmanCheck != null) {
            Npc workman = Functions.spawnNpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
            if ((((item.getID() == ItemId.SPADE.id()) && (item.getDef(p.getWorld()).getCommand() != null)) && item.getDef(p.getWorld()).getCommand()[0].equalsIgnoreCase("Dig")) && (obj == null)) {
                if (workman != null) {
                    Functions.___npcTalk(p, workman, "Oi! what do you think you are doing ?");
                    Functions.npcWalkFromPlayer(p, workman);
                    Functions.___npcTalk(p, workman, "Don't you realize there are fragile specimens around here ?");
                    workman.remove();
                }
            } else
                if (((item.getID() == ItemId.SPADE.id()) && Functions.inArray(obj.getID(), DigsiteDigAreas.SOIL)) && (obj != null)) {
                    if (workman != null) {
                        Functions.___npcTalk(p, workman, "Oi! dont use that spade!");
                        Functions.npcWalkFromPlayer(p, workman);
                        Functions.___npcTalk(p, workman, "What are you trying to do, destroy everything of value ?");
                        workman.remove();
                    }
                }

        }
    }

    private void rockPickOnSite(Player p, Item item, GameObject obj) {
        if ((item.getID() == ItemId.ROCK_PICK.id()) && Functions.inArray(obj.getID(), DigsiteDigAreas.SOIL)) {
            if (!getLevel2Digsite(p)) {
                Npc workman = Functions.spawnNpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
                if (workman != null) {
                    Functions.___npcTalk(p, workman, "No no, rockpicks should only be used");
                    Functions.npcWalkFromPlayer(p, workman);
                    Functions.___npcTalk(p, workman, "To dig in a level 2 site...");
                    workman.remove();
                }
                return;
            }
            if ((p.getQuestStage(Quests.DIGSITE) < 4) && getLevel2Digsite(p)) {
                Npc workman = Functions.spawnNpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
                if (workman != null) {
                    Functions.___npcTalk(p, workman, "Sorry, you haven't passed level 2 earth sciences exam");
                    Functions.npcWalkFromPlayer(p, workman);
                    Functions.___npcTalk(p, workman, "I can't let you dig here");
                    workman.remove();
                }
                return;
            }
            if ((p.getQuestStage(Quests.DIGSITE) >= 4) && getLevel2Digsite(p)) {
                if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                    if (p.getFatigue() >= 69750) {
                        p.message("You are too tired to do any more digging");
                        return;
                    }
                }
                Functions.showBubble(p, new Item(ItemId.ROCK_PICK.id()));
                p.incExp(Skills.MINING, 70, true);
                Functions.___message(p, "You dig through the earth");
                Functions.sleep(1500);
                int randomize = DataConversions.random(0, DIGSITE_LEVEL2_ITEMS.length - 1);
                int selectedItem = DIGSITE_LEVEL2_ITEMS[randomize];
                DigsiteDigAreas.doDigsiteItemMessages(p, selectedItem);
                if (selectedItem != (-1)) {
                    Functions.addItem(p, selectedItem, 1);
                }
            }
        }
    }

    private void trowelOnSite(Player p, Item item, GameObject obj) {
        if ((item.getID() == ItemId.TROWEL.id()) && Functions.inArray(obj.getID(), DigsiteDigAreas.SOIL)) {
            if (getTrainingAreas(p)) {
                Functions.showBubble(p, new Item(ItemId.TROWEL.id()));
                p.incExp(Skills.MINING, 50, true);
                Functions.___message(p, "You dig with the trowel...");
                Functions.sleep(1500);
                int randomize = DataConversions.random(0, TRAINING_AREA_ITEMS.length - 1);
                int selectedItem = TRAINING_AREA_ITEMS[randomize];
                DigsiteDigAreas.doDigsiteItemMessages(p, selectedItem);
                if ((selectedItem != (-1)) || (selectedItem != (-2))) {
                    Functions.addItem(p, selectedItem, 1);
                }
            }
            if (getLevel1Digsite(p)) {
                if (((((((!p.getInventory().wielding(ItemId.LEATHER_GLOVES.id())) && (!p.getInventory().wielding(ItemId.ICE_GLOVES.id()))) && (!p.getInventory().wielding(ItemId.KLANKS_GAUNTLETS.id()))) && (!p.getInventory().wielding(ItemId.STEEL_GAUNTLETS.id()))) && (!p.getInventory().wielding(ItemId.GAUNTLETS_OF_CHAOS.id()))) && (!p.getInventory().wielding(ItemId.GAUNTLETS_OF_COOKING.id()))) && (!p.getInventory().wielding(ItemId.GAUNTLETS_OF_GOLDSMITHING.id()))) {
                    Npc workman = Functions.spawnNpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
                    if (workman != null) {
                        Functions.___npcTalk(p, workman, "Hey, where are your gloves ?");
                        Functions.npcWalkFromPlayer(p, workman);
                        Functions.___playerTalk(p, workman, "Err...I haven't got any");
                        Functions.___npcTalk(p, workman, "Well get some and put them on first!");
                        workman.remove();
                    }
                    return;
                }
                if (!p.getInventory().wielding(ItemId.BOOTS.id())) {
                    Npc workman = Functions.spawnNpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
                    if (workman != null) {
                        Functions.___npcTalk(p, workman, "Oi, no boots!");
                        Functions.npcWalkFromPlayer(p, workman);
                        Functions.___npcTalk(p, workman, "No boots no digging!");
                        workman.remove();
                    }
                    return;
                }
                if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                    if (p.getFatigue() >= 69750) {
                        p.message("You are too tired to do any more digging");
                        return;
                    }
                }
                Functions.showBubble(p, new Item(ItemId.TROWEL.id()));
                p.incExp(Skills.MINING, 60, true);
                Functions.___message(p, "You dig through the earth");
                Functions.sleep(1500);
                int randomize = DataConversions.random(0, DIGSITE_LEVEL1_ITEMS.length - 1);
                int selectedItem = DIGSITE_LEVEL1_ITEMS[randomize];
                DigsiteDigAreas.doDigsiteItemMessages(p, selectedItem);
                if (selectedItem != (-1)) {
                    Functions.addItem(p, selectedItem, 1);
                }
            }
            if (getLevel2Digsite(p)) {
                Npc workman = Functions.spawnNpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
                if (workman != null) {
                    Functions.___npcTalk(p, workman, "Sorry, you must use a rockpick");
                    Functions.npcWalkFromPlayer(p, workman);
                    Functions.___npcTalk(p, workman, "To dig in a level 2 site...");
                    workman.remove();
                } else {
                    p.message("No rockpicks should only be used in a level 2 site...");
                }
            }
            if (DigsiteDigAreas.getLevel3Digsite(p)) {
                if (!Functions.hasItem(p, ItemId.SPECIMEN_JAR.id())) {
                    // HAS SPECIMEN JAR
                    Npc workman = Functions.spawnNpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
                    if (workman != null) {
                        Functions.___npcTalk(p, workman, "Ahem! I don't see your sample jar");
                        Functions.npcWalkFromPlayer(p, workman);
                        Functions.___npcTalk(p, workman, "You must carry one to be able to dig here...");
                        Functions.___playerTalk(p, workman, "Oh, okay");
                        workman.remove();
                    } else {
                        p.message("You need a sample jar to dig here");
                    }
                    return;
                }
                if (!Functions.hasItem(p, ItemId.SPECIMEN_BRUSH.id())) {
                    // HAS SPECIMEN BRUSH
                    Npc workman = Functions.spawnNpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
                    if (workman != null) {
                        Functions.___npcTalk(p, workman, "Wait just a minute!");
                        Functions.npcWalkFromPlayer(p, workman);
                        Functions.___npcTalk(p, workman, "I can't let you dig here", "Unless you have a specimen brush with you", "Rules is rules!");
                        workman.remove();
                    } else {
                        p.message("you can't dig here unless you have a specimen brush with you");
                    }
                    return;
                }
                if (p.getQuestStage(Quests.DIGSITE) < 5) {
                    Npc workman = Functions.spawnNpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
                    if (workman != null) {
                        Functions.___npcTalk(p, workman, "Sorry, you haven't passed level 3 earth sciences exam");
                        Functions.npcWalkFromPlayer(p, workman);
                        Functions.___npcTalk(p, workman, "I can't let you dig here");
                        workman.remove();
                    }
                    return;
                }
                Functions.showBubble(p, new Item(ItemId.TROWEL.id()));
                p.incExp(Skills.MINING, 80, true);
                Functions.___message(p, "You dig through the earth");
                Functions.sleep(1500);
                int randomize = DataConversions.random(0, DIGSITE_LEVEL3_ITEMS.length - 1);
                int selectedItem = DIGSITE_LEVEL3_ITEMS[randomize];
                DigsiteDigAreas.doDigsiteItemMessages(p, selectedItem);
                if (selectedItem != (-1)) {
                    if (selectedItem == ItemId.COINS.id()) {
                        Functions.addItem(p, ItemId.COINS.id(), DataConversions.random(0, 1) == 1 ? 5 : 10);
                    } else {
                        Functions.addItem(p, selectedItem, 1);
                    }
                }
            }
        }
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return Functions.inArray(obj.getID(), DigsiteDigAreas.SOIL) || ((obj.getID() == DigsiteDigAreas.ROCK) && (item.getID() == ItemId.ROCK_PICK.id()));
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(obj.getID(), DigsiteDigAreas.SOIL)) {
                        switch (ItemId.getById(item.getID())) {
                            case TROWEL :
                                trowelOnSite(p, item, obj);
                                break;
                            case SPADE :
                                doSpade(p, item, obj);
                                break;
                            case ROCK_PICK :
                                rockPickOnSite(p, item, obj);
                                break;
                            case PANNING_TRAY :
                                Functions.___playerTalk(p, null, "No I'd better not - it may damage the tray...");
                                break;
                            default :
                                p.message("Nothing interesting happens");
                                break;
                        }
                    }
                    if ((obj.getID() == DigsiteDigAreas.ROCK) && (item.getID() == ItemId.ROCK_PICK.id())) {
                        p.message("You chip at the rock with the rockpick");
                        p.message("You take the pieces of cracked rock");
                        Functions.addItem(p, ItemId.CRACKED_ROCK_SAMPLE.id(), 1);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return Functions.inArray(obj.getID(), DigsiteDigAreas.SOIL);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(obj.getID(), DigsiteDigAreas.SOIL)) {
                        p.playerServerMessage(MessageType.QUEST, "You examine the patch of soil");
                        p.message("You see nothing on the surface");
                        Functions.___playerTalk(p, null, "I think I need something to dig with");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvAction(Item item, Player p, String command) {
        return (item.getID() == ItemId.SPADE.id()) && getDigsite(p);
    }

    @Override
    public GameStateEvent onInvAction(Item item, Player p, String command) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((item.getID() == ItemId.SPADE.id()) && getDigsite(p)) {
                        doSpade(p, item, null);
                    }
                    return null;
                });
            }
        };
    }
}

