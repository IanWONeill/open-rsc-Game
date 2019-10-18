package com.openrsc.server.plugins.quests.members.digsite;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


public class DigsiteObjects implements InvUseOnObjectListener , ObjectActionListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener {
    private static final int[] SIGNPOST = new int[]{ 1060, 1061, 1062, 1063 };

    /* Objects */
    private static final int HOUSE_EAST_CHEST_OPEN = 1105;

    private static final int HOUSE_EAST_CHEST_CLOSED = 1104;

    private static final int HOUSE_EAST_CUPBOARD_OPEN = 1078;

    private static final int HOUSE_EAST_CUPBOARD_CLOSED = 1074;

    private static final int HOUSE_WEST_CHESTS_OPEN = 17;

    private static final int HOUSE_WEST_CHESTS_CLOSED = 18;

    private static final int TENT_CHEST_OPEN = 1084;

    private static final int TENT_CHEST_LOCKED = 1085;

    private static final int HOUSE_BOOKCASE = 1090;

    private static final int[] SACKS = new int[]{ 1075, 1076 };

    private static final int[] BUSH = new int[]{ 1072, 1073 };

    private static final int[] BURIED_SKELETON = new int[]{ 1057, 1049 };

    private static final int SPECIMEN_TRAY = 1052;

    private static final int CLIMB_UP_ROPE_SMALL_CAVE = 1097;

    private static final int CLIMB_UP_ROPE_BIG_CAVE = 1098;

    private static final int BRICK = 1096;

    private static final int X_BARREL = 1082;

    private static final int X_BARREL_OPEN = 1083;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return (((DataConversions.inArray(new int[]{ DigsiteObjects.HOUSE_EAST_CHEST_OPEN, DigsiteObjects.HOUSE_EAST_CHEST_CLOSED, DigsiteObjects.HOUSE_EAST_CUPBOARD_OPEN, DigsiteObjects.HOUSE_EAST_CUPBOARD_CLOSED, DigsiteObjects.HOUSE_WEST_CHESTS_OPEN, DigsiteObjects.HOUSE_WEST_CHESTS_CLOSED, DigsiteObjects.TENT_CHEST_OPEN, DigsiteObjects.TENT_CHEST_LOCKED, DigsiteObjects.HOUSE_BOOKCASE, DigsiteObjects.SPECIMEN_TRAY, DigsiteObjects.CLIMB_UP_ROPE_SMALL_CAVE, DigsiteObjects.CLIMB_UP_ROPE_BIG_CAVE, DigsiteObjects.BRICK, DigsiteObjects.X_BARREL_OPEN }, obj.getID()) || Functions.inArray(obj.getID(), DigsiteObjects.SIGNPOST)) || Functions.inArray(obj.getID(), DigsiteObjects.SACKS)) || Functions.inArray(obj.getID(), DigsiteObjects.BURIED_SKELETON)) || Functions.inArray(obj.getID(), DigsiteObjects.BUSH);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == DigsiteObjects.X_BARREL_OPEN) {
                        p.message("You search the barrel");
                        p.message("The barrel has a foul-smelling liquid inside...");
                        Functions.___playerTalk(p, null, "I can't pick this up with my bare hands!", "I'll need something to put it in");
                    } else
                        if (obj.getID() == DigsiteObjects.BRICK) {
                            Functions.___playerTalk(p, null, "Hmmm, There's a room past these bricks", "If I could move them out of the way", "Then I could find out what's inside...");
                        } else
                            if ((obj.getID() == DigsiteObjects.CLIMB_UP_ROPE_SMALL_CAVE) || (obj.getID() == DigsiteObjects.CLIMB_UP_ROPE_BIG_CAVE)) {
                                p.message("You climb the ladder");
                                if (obj.getID() == DigsiteObjects.CLIMB_UP_ROPE_BIG_CAVE) {
                                    p.teleport(25, 515);
                                } else
                                    if (obj.getID() == DigsiteObjects.CLIMB_UP_ROPE_SMALL_CAVE) {
                                        p.teleport(14, 506);
                                    }

                            } else
                                if (obj.getID() == DigsiteObjects.TENT_CHEST_LOCKED) {
                                    p.message("The chest is locked");
                                } else
                                    if (obj.getID() == DigsiteObjects.TENT_CHEST_OPEN) {
                                        if (command.equalsIgnoreCase("Search")) {
                                            Functions.___message(p, "You search the chest");
                                            p.message("You find some unusual powder inside...");
                                            Functions.addItem(p, ItemId.UNIDENTIFIED_POWDER.id(), 1);
                                            p.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), DigsiteObjects.TENT_CHEST_LOCKED, obj.getDirection(), obj.getType()));
                                        } else // kosher special case - chest does not close on that command, player must search the chest
                                        {
                                            p.message("Nothing interesting happens");
                                        }
                                    } else
                                        if (Functions.inArray(obj.getID(), DigsiteObjects.BUSH)) {
                                            p.message("You search the bush");
                                            if (obj.getID() == DigsiteObjects.BUSH[1]) {
                                                Functions.___playerTalk(p, null, "Hey, something has been dropped here...");
                                                p.message("You find a rock sample!");
                                                Functions.addItem(p, ItemId.ROCK_SAMPLE_PURPLE.id(), 1);
                                            } else {
                                                p.playerServerMessage(MessageType.QUEST, "You find nothing of interest");
                                            }
                                        } else
                                            if (obj.getID() == DigsiteObjects.SPECIMEN_TRAY) {
                                                int[] TRAY_ITEMS = new int[]{ ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.CRACKED_ROCK_SAMPLE.id(), ItemId.IRON_DAGGER.id(), ItemId.BROKEN_ARROW.id(), ItemId.BROKEN_GLASS.id(), ItemId.CERAMIC_REMAINS.id(), ItemId.COINS.id(), ItemId.A_LUMP_OF_CHARCOAL.id() };
                                                p.incExp(Skills.MINING, 4, true);
                                                Functions.___message(p, "You sift through the earth in the tray");
                                                int randomize = DataConversions.random(0, TRAY_ITEMS.length - 1);
                                                int chosenItem = TRAY_ITEMS[randomize];
                                                DigsiteDigAreas.doDigsiteItemMessages(p, chosenItem);
                                                if (chosenItem != ItemId.NOTHING.id()) {
                                                    Functions.addItem(p, chosenItem, 1);
                                                }
                                            } else
                                                if (Functions.inArray(obj.getID(), DigsiteObjects.SIGNPOST)) {
                                                    if (obj.getID() == DigsiteObjects.SIGNPOST[0]) {
                                                        p.message("This site is for training purposes only");
                                                    } else
                                                        if (obj.getID() == DigsiteObjects.SIGNPOST[1]) {
                                                            p.message("Level 1 digs only");
                                                        } else
                                                            if (obj.getID() == DigsiteObjects.SIGNPOST[2]) {
                                                                p.message("Level 2 digs only");
                                                            } else
                                                                if (obj.getID() == DigsiteObjects.SIGNPOST[3]) {
                                                                    p.message("Level 3 digs only");
                                                                }



                                                } else
                                                    if (Functions.inArray(obj.getID(), DigsiteObjects.SACKS)) {
                                                        p.playerServerMessage(MessageType.QUEST, "You search the sacks");
                                                        if ((obj.getID() == DigsiteObjects.SACKS[0]) || Functions.hasItem(p, ItemId.SPECIMEN_JAR.id())) {
                                                            p.playerServerMessage(MessageType.QUEST, "You find nothing of interest");
                                                        } else
                                                            if ((obj.getID() == DigsiteObjects.SACKS[1]) && (!Functions.hasItem(p, ItemId.SPECIMEN_JAR.id()))) {
                                                                Functions.___playerTalk(p, null, "Hey there's something under here");
                                                                p.message("You find a specimen jar!");
                                                                Functions.addItem(p, ItemId.SPECIMEN_JAR.id(), 1);
                                                            }

                                                    } else
                                                        if (Functions.inArray(obj.getID(), DigsiteObjects.BURIED_SKELETON)) {
                                                            p.message("You search the skeleton");
                                                            p.message("You find nothing of interest");
                                                        } else
                                                            if (obj.getID() == DigsiteObjects.HOUSE_EAST_CHEST_CLOSED) {
                                                                p.message("You open the chest");
                                                                Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), DigsiteObjects.HOUSE_EAST_CHEST_OPEN, obj.getDirection(), obj.getType()));
                                                            } else
                                                                if (obj.getID() == DigsiteObjects.HOUSE_EAST_CHEST_OPEN) {
                                                                    if (command.equalsIgnoreCase("Search")) {
                                                                        p.message("You search the chest");
                                                                        p.message("You find a rock sample");
                                                                        Functions.addItem(p, ItemId.CRACKED_ROCK_SAMPLE.id(), 1);
                                                                        Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), DigsiteObjects.HOUSE_EAST_CHEST_CLOSED, obj.getDirection(), obj.getType()));
                                                                    }
                                                                } else
                                                                    if (obj.getID() == DigsiteObjects.HOUSE_BOOKCASE) {
                                                                        p.message("You search through the bookcase");
                                                                        p.message("You find a book on chemicals");
                                                                        Functions.addItem(p, ItemId.BOOK_OF_EXPERIMENTAL_CHEMISTRY.id(), 1);
                                                                    } else
                                                                        if (obj.getID() == DigsiteObjects.HOUSE_EAST_CUPBOARD_CLOSED) {
                                                                            Functions.openCupboard(obj, p, DigsiteObjects.HOUSE_EAST_CUPBOARD_OPEN);
                                                                        } else
                                                                            if (obj.getID() == DigsiteObjects.HOUSE_EAST_CUPBOARD_OPEN) {
                                                                                if (command.equalsIgnoreCase("search")) {
                                                                                    if (!Functions.hasItem(p, ItemId.ROCK_PICK.id())) {
                                                                                        p.message("You find a rock pick");
                                                                                        Functions.addItem(p, ItemId.ROCK_PICK.id(), 1);
                                                                                    } else {
                                                                                        p.message("You find nothing of interest");
                                                                                    }
                                                                                    Functions.closeCupboard(obj, p, DigsiteObjects.HOUSE_EAST_CUPBOARD_CLOSED);
                                                                                }
                                                                            } else
                                                                                if ((obj.getID() == DigsiteObjects.HOUSE_WEST_CHESTS_OPEN) || (obj.getID() == DigsiteObjects.HOUSE_WEST_CHESTS_CLOSED)) {
                                                                                    if (command.equalsIgnoreCase("Open")) {
                                                                                        p.message("You open the chest");
                                                                                        Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), DigsiteObjects.HOUSE_WEST_CHESTS_OPEN, obj.getDirection(), obj.getType()));
                                                                                    } else
                                                                                        if (command.equalsIgnoreCase("Close")) {
                                                                                            p.message("You close the chest");
                                                                                            Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), DigsiteObjects.HOUSE_WEST_CHESTS_CLOSED, obj.getDirection(), obj.getType()));
                                                                                        } else
                                                                                            if (command.equalsIgnoreCase("Search")) {
                                                                                                p.message("You search the chest, but find nothing");
                                                                                            }


                                                                                }















                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return ((((obj.getID() == DigsiteObjects.TENT_CHEST_LOCKED) && (item.getID() == ItemId.DIGSITE_CHEST_KEY.id())) || (obj.getID() == DigsiteObjects.X_BARREL)) || (obj.getID() == DigsiteObjects.X_BARREL_OPEN)) || (obj.getID() == DigsiteObjects.BRICK);
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == DigsiteObjects.TENT_CHEST_LOCKED) && (item.getID() == ItemId.DIGSITE_CHEST_KEY.id())) {
                        Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), DigsiteObjects.TENT_CHEST_OPEN, obj.getDirection(), obj.getType()));
                        p.message("you use the key in the chest");
                        p.message("you open the chest");
                        Functions.removeItem(p, ItemId.DIGSITE_CHEST_KEY.id(), 1);
                        Functions.___playerTalk(p, null, "Oops I dropped the key", "Never mind it's open now...");
                    } else
                        if (obj.getID() == DigsiteObjects.X_BARREL) {
                            switch (ItemId.getById(item.getID())) {
                                case BRONZE_PICKAXE :
                                    Functions.___playerTalk(p, null, "I better not - it might break it to pieces!");
                                    break;
                                case ROCK_PICK :
                                    Functions.___playerTalk(p, null, "The rockpick is too fat to fit in the gap...");
                                    break;
                                case SPADE :
                                    Functions.___playerTalk(p, null, "The spade is far too big to fit");
                                    break;
                                case IRON_DAGGER :
                                    Functions.___playerTalk(p, null, "The dagger's blade might break, I need something stronger");
                                    break;
                                case BROKEN_ARROW :
                                    Functions.___playerTalk(p, null, "It nearly fits, just a little too thin");
                                    break;
                                case TROWEL :
                                    Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), DigsiteObjects.X_BARREL_OPEN, obj.getDirection(), obj.getType()));
                                    Functions.___playerTalk(p, null, "Great, it's opened it!");
                                    break;
                                default :
                                    p.message("Nothing interesting happens");
                                    break;
                            }
                        } else
                            if (obj.getID() == DigsiteObjects.X_BARREL_OPEN) {
                                switch (ItemId.getById(item.getID())) {
                                    case PANNING_TRAY :
                                        Functions.___playerTalk(p, null, "Not the best idea i've had...", "It's likely to spill everywhere in that!");
                                        break;
                                    case SPECIMEN_JAR :
                                        Functions.___playerTalk(p, null, "Perhaps not, it might contaminate the samples");
                                        break;
                                    case JUG :
                                        Functions.___playerTalk(p, null, "I had better not, someone might want to drink from this!");
                                        break;
                                    case EMPTY_VIAL :
                                        p.message("You fill the vial with the liquid");
                                        p.message("You close the barrel");
                                        p.getInventory().replace(ItemId.EMPTY_VIAL.id(), ItemId.UNIDENTIFIED_LIQUID.id());
                                        Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), DigsiteObjects.X_BARREL, obj.getDirection(), obj.getType()));
                                        Functions.___playerTalk(p, null, "I'm not sure what this stuff is", "I had better be very careful with it", "I had better not spill any I think...");
                                        break;
                                    default :
                                        p.message("Nothing interesting happens");
                                        break;
                                }
                            } else
                                if (obj.getID() == DigsiteObjects.BRICK) {
                                    switch (ItemId.getById(item.getID())) {
                                        case EXPLOSIVE_COMPOUND :
                                            p.message("You pour the compound over the bricks");
                                            Functions.removeItem(p, ItemId.EXPLOSIVE_COMPOUND.id(), 1);
                                            Functions.___playerTalk(p, null, "I need some way to ignite this compound...");
                                            if (!p.getCache().hasKey("brick_ignite")) {
                                                p.getCache().store("brick_ignite", true);
                                            }
                                            break;
                                        case TINDERBOX :
                                            if (p.getCache().hasKey("brick_ignite")) {
                                                p.message("You strike the tinderbox");
                                                p.message("Fizz...");
                                                Functions.sleep(300);
                                                Functions.___playerTalk(p, null, "Whoa! this is going to blow!\"", "I'd better run!");
                                                Functions.sleep(1500);
                                                p.teleport(22, 3379);
                                                p.updateQuestStage(Quests.DIGSITE, 6);
                                                p.getCache().remove("brick_ignite");
                                                Functions.___message(p, "\"Bang!!!\"");
                                                Functions.___playerTalk(p, null, "Wow that was a big explosion!", "...What's that noise I can hear ?", "...Sounds like bones moving or something");
                                            } else {
                                                Functions.___playerTalk(p, null, "Now what am I trying to achieve here ?");
                                            }
                                            break;
                                        default :
                                            p.message("Nothing interesting happens");
                                            break;
                                    }
                                }



                    return null;
                });
            }
        };
    }
}

