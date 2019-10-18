package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnGroundItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnGroundItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class ClockTower implements QuestInterface , InvUseOnGroundItemListener , InvUseOnObjectListener , ObjectActionListener , PickupListener , TalkToNpcListener , WallObjectActionListener , InvUseOnGroundItemExecutiveListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener , PickupExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    @Override
    public int getQuestId() {
        return Quests.CLOCK_TOWER;
    }

    @Override
    public String getQuestName() {
        return "Clock tower (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.CLOCK_TOWER), true);
        p.message("@gre@You haved gained 1 quest point!");
        p.getCache().remove("rats_dead");
        p.getCache().remove("1st_cog");
        p.getCache().remove("2nd_cog");
        p.getCache().remove("3rd_cog");
        p.getCache().remove("4th_cog");
        Functions.addItem(p, ItemId.COINS.id(), 500);
    }

    /**
     * NPCS: #366 Brother Kojo
     */
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.BROTHER_KOJO.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.BROTHER_KOJO.id()) {
                        switch (p.getQuestStage(quest)) {
                            case 0 :
                                Functions.___playerTalk(p, n, "Hello Monk");
                                Functions.___npcTalk(p, n, "Hello traveller, I'm Brother Kojo", "Do you know the time?");
                                Functions.___playerTalk(p, n, "No... Sorry");
                                Functions.___npcTalk(p, n, "Oh dear, oh dear, I must fix the clock", "The town people are becoming angry", "Please could you help?");
                                int menu = Functions.___showMenu(p, n, "Ok old monk what can I do?", "Not now old monk");
                                if (menu == 0) {
                                    Functions.___npcTalk(p, n, "Oh thank you kind sir", "In the cellar below you'll find four cogs", "They're too heavy for me, but you should", "Be able to carry them one at a time", "One goes on each floor", "But I can't remember which goes where");
                                    Functions.___playerTalk(p, n, "I'll do my best");
                                    Functions.___npcTalk(p, n, "Be careful, strange beasts dwell in the cellars");
                                    Functions.setQuestStage(p, quest, 1);
                                } else
                                    if (menu == 1) {
                                        Functions.___npcTalk(p, n, "Ok then");
                                    }

                                break;
                            case 1 :
                                if (((p.getCache().hasKey("1st_cog") && p.getCache().hasKey("2nd_cog")) && p.getCache().hasKey("3rd_cog")) && p.getCache().hasKey("4th_cog")) {
                                    Functions.___playerTalk(p, n, "I have replaced all the cogs");
                                    Functions.___npcTalk(p, n, "Really..? wait, listen");
                                    p.message("Tick Tock, Tick Tock");
                                    Functions.___npcTalk(p, n, "Well done, well done");
                                    p.message("Tick Tock, Tick Tock");
                                    Functions.___npcTalk(p, n, "Yes yes yes, you've done it", "You are clever");
                                    p.message("You have completed the clock tower quest");
                                    Functions.___npcTalk(p, n, "That will please the village folk", "Please take these coins as a reward");
                                    p.sendQuestComplete(Quests.CLOCK_TOWER);
                                    return null;
                                }
                                Functions.___playerTalk(p, n, "Hello again");
                                Functions.___npcTalk(p, n, "Oh hello, are you having trouble?", "The cogs are in four rooms below us", "Place one cog on a pole on each", "Of the four tower levels");
                                break;
                            case -1 :
                                Functions.___playerTalk(p, n, "Hello again Brother Kojo");
                                Functions.___npcTalk(p, n, "Oh hello there traveller", "You've done a grand job with the clock", "It's just like new");
                                break;
                        }
                    }
                    return null;
                });
            }
        };
    }

    /**
     * Objects: #362 Clock pole blue #363 Clock pole red #364 Clock pole purple
     * #365 Clock pole black
     * <p>
     * #372 Gates open for first large cog (rats cage) #371 Gates closed #374
     * Second Lever (rats cage) #373 First Lever (rats cage)
     */
    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return (((((obj.getID() == 362) || (obj.getID() == 363)) || (obj.getID() == 364)) || (obj.getID() == 365)) || ((obj.getID() == 373) || (obj.getID() == 374))) || ((obj.getID() == 371) && (obj.getY() == 3475));
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((((obj.getID() == 362) || (obj.getID() == 363)) || (obj.getID() == 364)) || (obj.getID() == 365)) {
                        switch (p.getQuestStage(quest)) {
                            case 0 :
                            case 1 :
                                if (((p.getCache().hasKey("1st_cog") && (obj.getID() == 364)) && (obj.getX() == 581)) && (obj.getY() == 2525)) {
                                    p.message("There's a large cog on this pole");
                                    return null;
                                } else
                                    if (((p.getCache().hasKey("2nd_cog") && (obj.getID() == 365)) && (obj.getX() == 581)) && (obj.getY() == 639)) {
                                        p.message("There's a large cog on this pole");
                                        return null;
                                    } else
                                        if (((p.getCache().hasKey("3rd_cog") && (obj.getID() == 362)) && (obj.getX() == 580)) && (obj.getY() == 3470)) {
                                            p.message("There's a large cog on this pole");
                                            return null;
                                        } else
                                            if (((p.getCache().hasKey("4th_cog") && (obj.getID() == 363)) && (obj.getX() == 582)) && (obj.getY() == 1582)) {
                                                p.message("There's a large cog on this pole");
                                                return null;
                                            }



                                p.message("A large pole, a cog is missing");
                                break;
                            case -1 :
                                p.message("The clock is now working");
                                break;
                        }
                    } else
                        if ((obj.getID() == 373) || (obj.getID() == 374)) {
                            GameObject dynGate;
                            GameObject statGate;
                            GameObject newGate;
                            boolean correctSetup = false;
                            if (obj.getID() == 373) {
                                dynGate = p.getWorld().getRegionManager().getRegion(Point.location(594, 3475)).getGameObject(Point.location(594, 3475));
                                statGate = p.getWorld().getRegionManager().getRegion(Point.location(590, 3475)).getGameObject(Point.location(590, 3475));
                                // outer gate was open + inner gate is open
                                correctSetup = (dynGate.getID() == 372) && (statGate.getID() == 372);
                            } else {
                                dynGate = p.getWorld().getRegionManager().getRegion(Point.location(590, 3475)).getGameObject(Point.location(590, 3475));
                                statGate = p.getWorld().getRegionManager().getRegion(Point.location(594, 3475)).getGameObject(Point.location(594, 3475));
                                // inner gate was closed + outer gate is closed
                                correctSetup = (dynGate.getID() == 371) && (statGate.getID() == 371);
                            }
                            // gate closed
                            if (dynGate.getID() == 371) {
                                p.message("The gate swings open");
                                newGate = new GameObject(p.getWorld(), dynGate.getLocation(), 372, 0, 0);
                                p.getWorld().registerGameObject(newGate);
                            } else // gate open
                            {
                                p.message("The gate creaks shut");
                                newGate = new GameObject(p.getWorld(), dynGate.getLocation(), 371, 0, 0);
                                p.getWorld().registerGameObject(newGate);
                            }
                            if (p.getCache().hasKey("foodtrough") && correctSetup) {
                                Functions.___message(p, "In their panic the rats bend and twist", "The cage bars with their teeth", "They're becoming weak, some have collapsed", "The rats are eating the poison", "They're becoming weak, some have collapsed", "The rats are slowly dying");
                                for (Npc rats : p.getViewArea().getNpcsInView()) {
                                    if (rats.getID() == NpcId.DUNGEON_RAT.id()) {
                                        rats.remove();
                                    }
                                }
                                p.getCache().remove("foodtrough");
                                p.getCache().store("rats_dead", true);
                            }
                        } else
                            if ((obj.getID() == 371) && (obj.getY() == 3475)) {
                                p.message("The gate is locked");
                                p.message("The gate will not open from here");
                            }


                    return null;
                });
            }
        };
    }

    /**
     * InvUseObjects: #375 Foodtrough #731 Rat Poison used for killing rats (put
     * poison in the trough) #730 Large cog #364 Purple clock pole (attaching)
     */
    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return ((obj.getID() == 375) && (item.getID() == ItemId.RAT_POISON.id())) || (((((obj.getID() == 364) || (obj.getID() == 363)) || (obj.getID() == 362)) || (obj.getID() == 365)) && ((((item.getID() == ItemId.LARGE_COG_PURPLE.id()) || (item.getID() == ItemId.LARGE_COG_BLACK.id())) || (item.getID() == ItemId.LARGE_COG_BLUE.id())) || (item.getID() == ItemId.LARGE_COG_RED.id())));
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 375) && (item.getID() == ItemId.RAT_POISON.id())) {
                        p.message("You pour the rat poison into the feeding trough");
                        Functions.removeItem(p, ItemId.RAT_POISON.id(), 1);
                        p.getCache().store("foodtrough", true);
                    } else/**
                     * TOP PURPLE POLE OTHERWISE NOT FIT MESSAGE - 1st cog *
                     */

                        if (((((obj.getID() == 364) || (obj.getID() == 363)) || (obj.getID() == 362)) || (obj.getID() == 365)) && (item.getID() == ItemId.LARGE_COG_PURPLE.id())) {
                            if (((obj.getID() == 364) && (obj.getX() == 581)) && (obj.getY() == 2525)) {
                                if (Functions.atQuestStage(p, quest, 1) && (!p.getCache().hasKey("1st_cog"))) {
                                    p.message("The cog fits perfectly");
                                    Functions.removeItem(p, ItemId.LARGE_COG_PURPLE.id(), 1);
                                    p.getCache().store("1st_cog", true);
                                } else
                                    if (Functions.atQuestStage(p, quest, -1) || p.getCache().hasKey("1st_cog")) {
                                        p.message("You have already placed a cog here");
                                    }

                            } else {
                                p.message("The cog doesn't fit");
                            }
                        } else/**
                         * GROUND FLOOR BLACK POLE OTHERWISE NOT FIT MESSAGE - 2nd cog *
                         */

                            if (((((obj.getID() == 364) || (obj.getID() == 363)) || (obj.getID() == 362)) || (obj.getID() == 365)) && (item.getID() == ItemId.LARGE_COG_BLACK.id())) {
                                if (((obj.getID() == 365) && (obj.getX() == 581)) && (obj.getY() == 639)) {
                                    if (Functions.atQuestStage(p, quest, 1) && (!p.getCache().hasKey("2nd_cog"))) {
                                        p.message("The cog fits perfectly");
                                        Functions.removeItem(p, ItemId.LARGE_COG_BLACK.id(), 1);
                                        p.getCache().store("2nd_cog", true);
                                    } else
                                        if (Functions.atQuestStage(p, quest, -1) || p.getCache().hasKey("2nd_cog")) {
                                            p.message("You have already placed a cog here");
                                        }

                                } else {
                                    p.message("The cog doesn't fit");
                                }
                            } else/**
                             * BOTTOM FLOOR BLUE POLE OTHERWISE NOT FIT MESSAGE - 3rd cog *
                             */

                                if (((((obj.getID() == 364) || (obj.getID() == 363)) || (obj.getID() == 362)) || (obj.getID() == 365)) && (item.getID() == ItemId.LARGE_COG_BLUE.id())) {
                                    if (((obj.getID() == 362) && (obj.getX() == 580)) && (obj.getY() == 3470)) {
                                        if (Functions.atQuestStage(p, quest, 1) && (!p.getCache().hasKey("3rd_cog"))) {
                                            p.message("The cog fits perfectly");
                                            Functions.removeItem(p, ItemId.LARGE_COG_BLUE.id(), 1);
                                            p.getCache().store("3rd_cog", true);
                                        } else
                                            if (Functions.atQuestStage(p, quest, -1) || p.getCache().hasKey("3rd_cog")) {
                                                p.message("You have already placed a cog here");
                                            }

                                    } else {
                                        p.message("The cog doesn't fit");
                                    }
                                } else/**
                                 * SECOND FLOOR RED POLE OTHERWISE NOT FIT MESSAGE - 4th cog *
                                 */

                                    if (((((obj.getID() == 364) || (obj.getID() == 363)) || (obj.getID() == 362)) || (obj.getID() == 365)) && (item.getID() == ItemId.LARGE_COG_RED.id())) {
                                        if (((obj.getID() == 363) && (obj.getX() == 582)) && (obj.getY() == 1582)) {
                                            if (Functions.atQuestStage(p, quest, 1) && (!p.getCache().hasKey("4th_cog"))) {
                                                p.message("The cog fits perfectly");
                                                Functions.removeItem(p, ItemId.LARGE_COG_RED.id(), 1);
                                                p.getCache().store("4th_cog", true);
                                            } else
                                                if (Functions.atQuestStage(p, quest, -1) || p.getCache().hasKey("4th_cog")) {
                                                    p.message("You have already placed a cog here");
                                                }

                                        } else {
                                            p.message("The cog doesn't fit");
                                        }
                                    }




                    return null;
                });
            }
        };
    }

    /**
     * Wallobjects: #111 rat cage cell
     */
    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
        return (obj.getID() == 111) || (((obj.getID() == 22) && (obj.getX() == 584)) && (obj.getY() == 3457));
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 111) {
                        if (p.getCache().hasKey("rats_dead") || Functions.atQuestStage(p, quest, -1)) {
                            p.message("In a panic to escape, the rats have..");
                            Functions.sleep(500);
                            p.message("..bent the bars, you can just crawl through");
                            if (p.getX() >= 583) {
                                p.setLocation(Point.location(582, 3476), true);
                            } else {
                                p.setLocation(Point.location(583, 3476), true);
                            }
                        }
                    } else
                        if (((obj.getID() == 22) && (obj.getX() == 584)) && (obj.getY() == 3457)) {
                            p.playSound("secretdoor");
                            p.message("You just went through a secret door");
                            Functions.doDoor(obj, p, 16);
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnGroundItem(Item myItem, GroundItem item, Player p) {
        return (myItem.getID() == ItemId.BUCKET_OF_WATER.id()) && (item.getID() == ItemId.LARGE_COG_BLACK.id());
    }

    @Override
    public GameStateEvent onInvUseOnGroundItem(Item myItem, GroundItem item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((myItem.getID() == ItemId.BUCKET_OF_WATER.id()) && (item.getID() == ItemId.LARGE_COG_BLACK.id())) {
                        Functions.___message(p, "You pour water over the cog", "The cog quickly cools down");
                        if (((Functions.hasItem(p, ItemId.LARGE_COG_BLACK.id()) || Functions.hasItem(p, ItemId.LARGE_COG_PURPLE.id())) || Functions.hasItem(p, ItemId.LARGE_COG_BLUE.id())) || Functions.hasItem(p, ItemId.LARGE_COG_RED.id())) {
                            p.message("You can only carry one");
                        } else {
                            p.message("You take the cog");
                            Functions.addItem(p, ItemId.LARGE_COG_BLACK.id(), 1);
                            Functions.removeItem(p, ItemId.BUCKET_OF_WATER.id(), 1);
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        if (((i.getID() == ItemId.LARGE_COG_PURPLE.id()) || (i.getID() == ItemId.LARGE_COG_BLUE.id())) || (i.getID() == ItemId.LARGE_COG_RED.id())) {
            if (((Functions.hasItem(p, ItemId.LARGE_COG_PURPLE.id()) || Functions.hasItem(p, ItemId.LARGE_COG_BLACK.id())) || Functions.hasItem(p, ItemId.LARGE_COG_BLUE.id())) || Functions.hasItem(p, ItemId.LARGE_COG_RED.id())) {
                p.message("The cogs are heavy, you can only carry one");
                return true;
            }
            return false;
        } else
            if (i.getID() == ItemId.LARGE_COG_BLACK.id()) {
                return true;
            }

        return false;
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ItemId.LARGE_COG_BLACK.id()) {
                        if (p.getInventory().hasItemId(ItemId.ICE_GLOVES.id()) && p.getInventory().wielding(ItemId.ICE_GLOVES.id())) {
                            Functions.___message(p, "The ice gloves cool down the cog", "You can carry it now");
                            if (((Functions.hasItem(p, ItemId.LARGE_COG_BLACK.id()) || Functions.hasItem(p, ItemId.LARGE_COG_PURPLE.id())) || Functions.hasItem(p, ItemId.LARGE_COG_BLUE.id())) || Functions.hasItem(p, ItemId.LARGE_COG_RED.id())) {
                                p.message("You can only carry one");
                            } else {
                                p.message("You take the cog");
                                Functions.addItem(p, ItemId.LARGE_COG_BLACK.id(), 1);
                            }
                        } else
                            if (Functions.hasItem(p, ItemId.BUCKET_OF_WATER.id())) {
                                Functions.___message(p, "You pour water over the cog", "The cog quickly cools down");
                                if (((Functions.hasItem(p, ItemId.LARGE_COG_BLACK.id()) || Functions.hasItem(p, ItemId.LARGE_COG_PURPLE.id())) || Functions.hasItem(p, ItemId.LARGE_COG_BLUE.id())) || Functions.hasItem(p, ItemId.LARGE_COG_RED.id())) {
                                    p.message("You can only carry one");
                                } else {
                                    p.message("You take the cog");
                                    Functions.addItem(p, ItemId.LARGE_COG_BLACK.id(), 1);
                                    Functions.removeItem(p, ItemId.BUCKET_OF_WATER.id(), 1);
                                }
                            } else {
                                Functions.___message(p, "The cog is red hot from the flames, too hot to carry", "The cogs are heavy");
                                if (((Functions.hasItem(p, ItemId.LARGE_COG_BLACK.id()) || Functions.hasItem(p, ItemId.LARGE_COG_PURPLE.id())) || Functions.hasItem(p, ItemId.LARGE_COG_BLUE.id())) || Functions.hasItem(p, ItemId.LARGE_COG_RED.id())) {
                                    p.message("You can only carry one");
                                }
                            }

                    }
                    return null;
                });
            }
        };
    }
}

