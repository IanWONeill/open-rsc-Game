package com.openrsc.server.plugins.skills;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.external.ObjectFishDef;
import com.openrsc.server.external.ObjectFishingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;


public class Fishing implements ObjectActionListener , ObjectActionExecutiveListener {
    private ObjectFishDef getFish(ObjectFishingDef objectFishDef, int fishingLevel, int click) {
        ArrayList<ObjectFishDef> fish = new ArrayList<ObjectFishDef>();
        for (ObjectFishDef def : objectFishDef.getFishDefs()) {
            if ((fishingLevel >= def.getReqLevel()) && Formulae.calcGatheringSuccessful(def.getReqLevel(), fishingLevel)) {
                fish.add(def);
            }
        }
        if (fish.size() <= 0) {
            return null;
        }
        return fish.get(DataConversions.random(0, fish.size() - 1));
    }

    @Override
    public GameStateEvent onObjectAction(final GameObject object, String command, Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((((command.equals("lure") || command.equals("bait")) || command.equals("net")) || command.equals("harpoon")) || command.equals("cage")) {
                        handleFishing(object, player, player.click, command);
                    }
                    return null;
                });
            }
        };
    }

    private void handleFishing(final GameObject object, Player player, final int click, final String command) {
        final ObjectFishingDef def = player.getWorld().getServer().getEntityHandler().getObjectFishingDef(object.getID(), click);
        if (player.isBusy()) {
            return;
        }
        if (!player.withinRange(object, 1)) {
            return;
        }
        if (def == null) {
            // This shouldn't happen
            return;
        }
        if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
            if (player.getFatigue() >= player.MAX_FATIGUE) {
                player.message("You are too tired to catch this fish");
                return;
            }
        }
        if ((object.getID() == 493) && (player.getSkills().getExperience(Skills.FISHING) >= 200)) {
            Functions.___message(player, "that's enough fishing for now", "go through the next door to continue the tutorial");
            return;
        }
        if (player.getSkills().getLevel(Skills.FISHING) < def.getReqLevel(player.getWorld())) {
            player.playerServerMessage(MessageType.QUEST, (((("You need at least level " + def.getReqLevel(player.getWorld())) + " ") + fishingRequirementString(object, command)) + " ") + (!command.contains("cage") ? "these fish" : player.getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase().substring(4) + "s"));
            return;
        }
        final int netId = def.getNetId();
        if (player.getInventory().countId(netId) <= 0) {
            player.playerServerMessage(MessageType.QUEST, (((("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(netId).getName().toLowerCase()) + " to ") + (command.equals("lure") || command.equals("bait") ? command : def.getBaitId() > 0 ? "bait" : "catch")) + " ") + (!command.contains("cage") ? "these fish" : player.getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase().substring(4) + "s"));
            return;
        }
        final int baitId = def.getBaitId();
        if (baitId >= 0) {
            if (player.getInventory().countId(baitId) <= 0) {
                player.playerServerMessage(MessageType.QUEST, ("You don't have any " + player.getWorld().getServer().getEntityHandler().getItemDef(baitId).getName().toLowerCase()) + " left");
                return;
            }
        }
        player.playSound("fish");
        player.playerServerMessage(MessageType.QUEST, "You attempt to catch " + tryToCatchFishString(def));
        Functions.showBubble(player, new Item(netId));
        player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1800, "Fishing", Formulae.getRepeatTimes(player, Skills.FISHING), true) {
            @Override
            public void action() {
                if (getOwner().getSkills().getLevel(Skills.FISHING) < def.getReqLevel(getWorld())) {
                    getOwner().playerServerMessage(MessageType.QUEST, (((("You need at least level " + def.getReqLevel(getWorld())) + " ") + fishingRequirementString(object, command)) + " ") + (!command.contains("cage") ? "these fish" : getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase().substring(4) + "s"));
                    interrupt();
                    return;
                }
                final int baitId = def.getBaitId();
                if (baitId >= 0) {
                    if (getOwner().getInventory().countId(baitId) <= 0) {
                        getOwner().playerServerMessage(MessageType.QUEST, ("You don't have any " + getWorld().getServer().getEntityHandler().getItemDef(baitId).getName().toLowerCase()) + " left");
                        interrupt();
                        return;
                    }
                }
                if (getWorld().getServer().getConfig().WANT_FATIGUE) {
                    if (getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
                        getOwner().playerServerMessage(MessageType.QUEST, "You are too tired to catch this fish");
                        interrupt();
                        return;
                    }
                }
                List<ObjectFishDef> fishLst = new ArrayList<ObjectFishDef>();
                ObjectFishDef aFishDef = getFish(def, getOwner().getSkills().getLevel(Skills.FISHING), click);
                if (aFishDef != null)
                    fishLst.add(aFishDef);

                if (fishLst.size() > 0) {
                    // check if the spot is still active
                    GameObject obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
                    if (obj == null) {
                        getOwner().playerServerMessage(MessageType.QUEST, "You fail to catch anything");
                        interrupt();
                    } else {
                        if (baitId >= 0) {
                            int idx = getOwner().getInventory().getLastIndexById(baitId);
                            Item bait = getOwner().getInventory().get(idx);
                            int newCount = bait.getAmount() - 1;
                            if (newCount <= 0) {
                                getOwner().getInventory().remove(idx);
                            } else {
                                bait.setAmount(newCount);
                            }
                            ActionSender.sendInventory(getOwner());
                        }
                        if (netId == ItemId.BIG_NET.id()) {
                            // big net spot may get 4 items but 1 already gotten
                            int max = bigNetRand() - 1;
                            for (int i = 0; i < max; i++) {
                                aFishDef = getFish(def, getOwner().getSkills().getLevel(Skills.FISHING), click);
                                if (aFishDef != null)
                                    fishLst.add(aFishDef);

                            }
                            if (DataConversions.random(0, 200) == 100) {
                                getOwner().playerServerMessage(MessageType.QUEST, "You catch a casket");
                                getOwner().incExp(Skills.FISHING, 40, true);
                                Functions.addItem(getOwner(), ItemId.CASKET.id(), 1);
                            }
                            for (Iterator<ObjectFishDef> iter = fishLst.iterator(); iter.hasNext();) {
                                ObjectFishDef fishDef = iter.next();
                                Item fish = new Item(fishDef.getId());
                                getOwner().getInventory().add(fish);
                                getOwner().playerServerMessage(MessageType.QUEST, ((("You catch " + (((fish.getID() == ItemId.BOOTS.id()) || (fish.getID() == ItemId.SEAWEED.id())) || (fish.getID() == ItemId.LEATHER_GLOVES.id()) ? "some" : fish.getID() == ItemId.OYSTER.id() ? "an" : "a")) + " ") + fish.getDef(getWorld()).getName().toLowerCase().replace("raw ", "").replace("leather ", "")) + (fish.getID() == ItemId.OYSTER.id() ? " shell" : ""));
                                getOwner().incExp(Skills.FISHING, fishDef.getExp(), true);
                            }
                        } else {
                            Item fish = new Item(fishLst.get(0).getId());
                            getOwner().getInventory().add(fish);
                            getOwner().playerServerMessage(MessageType.QUEST, (((("You catch " + (netId == ItemId.NET.id() ? "some" : "a")) + " ") + fish.getDef(getWorld()).getName().toLowerCase().replace("raw ", "")) + (fish.getID() == ItemId.RAW_SHRIMP.id() ? "s" : "")) + (fish.getID() == ItemId.RAW_SHARK.id() ? "!" : ""));
                            getOwner().incExp(Skills.FISHING, fishLst.get(0).getExp(), true);
                            if (((object.getID() == 493) && getOwner().getCache().hasKey("tutorial")) && (getOwner().getCache().getInt("tutorial") == 41))
                                getOwner().getCache().set("tutorial", 42);

                        }
                    }
                    if (getWorld().getServer().getConfig().FISHING_SPOTS_DEPLETABLE && (DataConversions.random(1, 1000) <= def.getDepletion())) {
                        obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
                        interrupt();
                        if (((obj != null) && (obj.getID() == object.getID())) && (def.getRespawnTime() > 0)) {
                            GameObject newObject = new GameObject(getWorld(), object.getLocation(), 668, object.getDirection(), object.getType());
                            getWorld().replaceGameObject(object, newObject);
                            getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000, true);
                        }
                    }
                } else {
                    getOwner().playerServerMessage(MessageType.QUEST, "You fail to catch anything");
                    if (((object.getID() == 493) && getOwner().getCache().hasKey("tutorial")) && (getOwner().getCache().getInt("tutorial") == 41)) {
                        getOwner().message("keep trying, you'll catch something soon");
                    }
                    if ((object.getID() != 493) && (getRepeatFor() > 1)) {
                        GameObject checkObj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
                        if (checkObj == null) {
                            interrupt();
                        }
                    }
                }
                if (!isCompleted()) {
                    Functions.showBubble(getOwner(), new Item(netId));
                    getOwner().playerServerMessage(MessageType.QUEST, "You attempt to catch " + tryToCatchFishString(def));
                }
            }
        });
    }

    private int bigNetRand() {
        int roll = DataConversions.random(0, 30);
        if (roll <= 23) {
            return 1;
        } else
            if (roll <= 27) {
                return 2;
            } else
                if (roll <= 29) {
                    return 3;
                } else {
                    return 4;
                }


    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        if (obj.getID() == 352)
            return false;

        if ((((command.equals("lure") || command.equals("bait")) || command.equals("net")) || command.equals("harpoon")) || command.equals("cage")) {
            return true;
        }
        return false;
    }

    private String fishingRequirementString(GameObject obj, String command) {
        String name = "";
        if (command.equals("bait")) {
            name = "fishing to bait";
        } else
            if (command.equals("lure")) {
                name = "fishing to lure";
            } else
                if (command.equals("net")) {
                    name = "fishing to net";
                } else
                    if (command.equals("harpoon")) {
                        name = "fishing to harpoon";
                    } else
                        if (command.equals("cage")) {
                            name = "fishing to catch";
                        }




        return name;
    }

    private String tryToCatchFishString(ObjectFishingDef def) {
        String name = "";
        if (def.getNetId() == ItemId.NET.id()) {
            name = "some fish";
        } else
            if (def.getNetId() == ItemId.LOBSTER_POT.id()) {
                name = "a lobster";
            } else {
                name = "a fish";
            }

        return name;
    }
}

