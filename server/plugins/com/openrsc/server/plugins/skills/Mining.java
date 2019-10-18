package com.openrsc.server.plugins.skills;


import com.openrsc.server.Server;
import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.external.ObjectMiningDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


public final class Mining implements ObjectActionListener , ObjectActionExecutiveListener {
    /* static int[] ids;

    static {
    ids = new int[]{176, 100, 101, 102, 103, 104, 105, 106, 107, 108,
    109, 110, 111, 112, 113, 114, 115, 195, 196, 210, 211};
    Arrays.sort(ids);
    }
     */
    public static int getAxe(Player p) {
        int lvl = p.getSkills().getLevel(Skills.MINING);
        for (int i = 0; i < Formulae.miningAxeIDs.length; i++) {
            if (p.getInventory().countId(Formulae.miningAxeIDs[i]) > 0) {
                if (lvl >= Formulae.miningAxeLvls[i]) {
                    return Formulae.miningAxeIDs[i];
                }
            }
        }
        return -1;
    }

    @Override
    public GameStateEvent onObjectAction(final GameObject object, String command, Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (object.getID() == 269) {
                        if (command.equalsIgnoreCase("mine")) {
                            if (Functions.hasItem(player, Mining.getAxe(player))) {
                                if (Functions.getCurrentLevel(player, Skills.MINING) >= 50) {
                                    getPlayerOwner().playerServerMessage(MessageType.QUEST, "you manage to dig a way through the rockslide");
                                    if (player.getX() <= 425) {
                                        player.teleport(428, 438);
                                    } else {
                                        player.teleport(425, 438);
                                    }
                                } else {
                                    getPlayerOwner().playerServerMessage(MessageType.QUEST, "You need a mining level of 50 to clear the rockslide");
                                }
                            } else {
                                getPlayerOwner().playerServerMessage(MessageType.QUEST, "you need a pickaxe to clear the rockslide");
                            }
                        } else
                            if (command.equalsIgnoreCase("prospect")) {
                                getPlayerOwner().playerServerMessage(MessageType.QUEST, "these rocks contain nothing interesting");
                                getPlayerOwner().playerServerMessage(MessageType.QUEST, "they are just in the way");
                            }

                    } else
                        if (object.getID() == 770) {
                            if (Functions.hasItem(player, Mining.getAxe(player))) {
                                player.setBusyTimer(3);
                                Functions.___message(player, "you mine the rock", "and break of several large chunks");
                                Functions.addItem(player, ItemId.ROCKS.id(), 1);
                            } else {
                                getPlayerOwner().playerServerMessage(MessageType.QUEST, "you need a pickaxe to mine this rock");
                            }
                        } else
                            if (object.getID() == 1026) {
                                // watchtower - rock of dalgroth
                                if (command.equalsIgnoreCase("mine")) {
                                    if (player.getQuestStage(Quests.WATCHTOWER) == 9) {
                                        if (!Functions.hasItem(player, Mining.getAxe(player))) {
                                            getPlayerOwner().playerServerMessage(MessageType.QUEST, "You need a pickaxe to mine the rock");
                                            return null;
                                        }
                                        if (Functions.getCurrentLevel(player, Skills.MINING) < 40) {
                                            getPlayerOwner().playerServerMessage(MessageType.QUEST, "You need a mining level of 40 to mine this crystal out");
                                            return null;
                                        }
                                        if (Functions.hasItem(player, ItemId.POWERING_CRYSTAL4.id())) {
                                            Functions.___playerTalk(player, null, "I already have this crystal", "There is no benefit to getting another");
                                            return null;
                                        }
                                        player.playSound("mine");
                                        // special bronze pick bubble for rock of dalgroth - see wiki
                                        Functions.showBubble(player, new Item(ItemId.BRONZE_PICKAXE.id()));
                                        getPlayerOwner().playerServerMessage(MessageType.QUEST, "You have a swing at the rock!");
                                        Functions.___message(player, "You swing your pick at the rock...");
                                        getPlayerOwner().playerServerMessage(MessageType.QUEST, "A crack appears in the rock and you prize a crystal out");
                                        Functions.addItem(player, ItemId.POWERING_CRYSTAL4.id(), 1);
                                    } else {
                                        Functions.___playerTalk(player, null, "I can't touch it...", "Perhaps it is linked with the shaman some way ?");
                                    }
                                } else
                                    if (command.equalsIgnoreCase("prospect")) {
                                        player.playSound("prospect");
                                        Functions.___message(player, "You examine the rock for ores...");
                                        getPlayerOwner().playerServerMessage(MessageType.QUEST, "This rock contains a crystal!");
                                    }

                            } else {
                                handleMining(object, player, player.click);
                            }


                    return null;
                });
            }
        };
    }

    private void handleMining(final GameObject object, Player player, int click) {
        final ObjectMiningDef def = player.getWorld().getServer().getEntityHandler().getObjectMiningDef(object.getID());
        final int axeId = Mining.getAxe(player);
        final int retrytimes;
        final int mineLvl = player.getSkills().getLevel(Skills.MINING);
        final int mineXP = player.getSkills().getExperience(Skills.MINING);
        final int reqlvl;
        switch (ItemId.getById(axeId)) {
            default :
            case BRONZE_PICKAXE :
                retrytimes = 1;
                reqlvl = 1;
                break;
            case IRON_PICKAXE :
                retrytimes = 2;
                reqlvl = 1;
                break;
            case STEEL_PICKAXE :
                retrytimes = 3;
                reqlvl = 6;
                break;
            case MITHRIL_PICKAXE :
                retrytimes = 5;
                reqlvl = 21;
                break;
            case ADAMANTITE_PICKAXE :
                retrytimes = 8;
                reqlvl = 31;
                break;
            case RUNE_PICKAXE :
                retrytimes = 12;
                reqlvl = 41;
                break;
        }
        player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Mining") {
            public void init() {
                addState(0, () -> {
                    if (getPlayerOwner().isBusy()) {
                        return null;
                    }
                    if (!getPlayerOwner().withinRange(object, 1)) {
                        return null;
                    }
                    if ((getPlayerOwner().click == 0) && (((def == null) || ((def.getRespawnTime() < 1) && (object.getID() != 496))) || ((def.getOreId() == 315) && (getPlayerOwner().getQuestStage(Quests.FAMILY_CREST) < 6)))) {
                        if ((axeId < 0) || (reqlvl > mineLvl)) {
                            Functions.___message(getPlayerOwner(), "You need a pickaxe to mine this rock", "You do not have a pickaxe which you have the mining level to use");
                            return null;
                        }
                        getPlayerOwner().setBusyTimer(3);
                        getPlayerOwner().playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
                        return invoke(1, 3);
                    }
                    if (getPlayerOwner().click == 1) {
                        getPlayerOwner().playSound("prospect");
                        getPlayerOwner().setBusyTimer(3);
                        getPlayerOwner().playerServerMessage(MessageType.QUEST, "You examine the rock for ores...");
                        return invoke(2, 3);
                    }
                    if ((axeId < 0) || (reqlvl > mineLvl)) {
                        Functions.___message(getPlayerOwner(), "You need a pickaxe to mine this rock", "You do not have a pickaxe which you have the mining level to use");
                        return null;
                    }
                    if (getPlayerOwner().getFatigue() >= getPlayerOwner().MAX_FATIGUE) {
                        getPlayerOwner().playerServerMessage(MessageType.QUEST, "You are too tired to mine this rock");
                        return null;
                    }
                    if ((object.getID() == 496) && (mineXP >= 210)) {
                        getPlayerOwner().playerServerMessage(MessageType.QUEST, "Thats enough mining for now");
                        return null;
                    }
                    getPlayerOwner().playSound("mine");
                    Functions.showBubble(getPlayerOwner(), new Item(ItemId.IRON_PICKAXE.id()));
                    getPlayerOwner().playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
                    getPlayerOwner().setBatchEvent(new BatchEvent(getPlayerOwner().getWorld(), getPlayerOwner(), getPlayerOwner().getWorld().getServer().getConfig().GAME_TICK * 3, "Mining", retrytimes, true) {
                        @Override
                        public void action() {
                            final Item ore = new Item(def.getOreId());
                            if (getOre(getWorld().getServer(), def, getOwner().getSkills().getLevel(Skills.MINING), axeId) && (mineLvl >= def.getReqLevel())) {
                                if (DataConversions.random(1, 200) <= (getOwner().getInventory().wielding(ItemId.CHARGED_DRAGONSTONE_AMULET.id()) ? 2 : 1)) {
                                    getOwner().playSound("foundgem");
                                    Item gem = new Item(getGem(), 1);
                                    getOwner().getInventory().add(gem);
                                    getPlayerOwner().playerServerMessage(MessageType.QUEST, ("You just found a" + gem.getDef(getWorld()).getName().toLowerCase().replaceAll("uncut", "")) + "!");
                                    interrupt();
                                } else {
                                    // check if there is still ore at the rock
                                    GameObject obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
                                    if (obj == null) {
                                        getPlayerOwner().playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
                                    } else {
                                        getOwner().getInventory().add(ore);
                                        getPlayerOwner().playerServerMessage(MessageType.QUEST, "You manage to obtain some " + ore.getDef(getWorld()).getName().toLowerCase());
                                        getOwner().incExp(Skills.MINING, def.getExp(), true);
                                    }
                                    if (((object.getID() == 496) && getOwner().getCache().hasKey("tutorial")) && (getOwner().getCache().getInt("tutorial") == 51))
                                        getOwner().getCache().set("tutorial", 52);

                                    if ((!getWorld().getServer().getConfig().MINING_ROCKS_EXTENDED) || (DataConversions.random(1, 100) <= def.getDepletion())) {
                                        interrupt();
                                        if (((obj != null) && (obj.getID() == object.getID())) && (def.getRespawnTime() > 0)) {
                                            GameObject newObject = new GameObject(getWorld(), object.getLocation(), 98, object.getDirection(), object.getType());
                                            getWorld().replaceGameObject(object, newObject);
                                            getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000);
                                        }
                                    }
                                }
                            } else {
                                if (object.getID() == 496) {
                                    getPlayerOwner().playerServerMessage(MessageType.QUEST, "You fail to make any real impact on the rock");
                                } else {
                                    getPlayerOwner().playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
                                    if (getRepeatFor() > 1) {
                                        GameObject checkObj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
                                        if (checkObj == null) {
                                            interrupt();
                                        }
                                    }
                                }
                            }
                            if (!isCompleted()) {
                                Functions.showBubble(getOwner(), new Item(ItemId.IRON_PICKAXE.id()));
                                getPlayerOwner().playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
                            }
                        }
                    });
                    return null;
                });
                addState(1, () -> {
                    getPlayerOwner().playerServerMessage(MessageType.QUEST, "There is currently no ore available in this rock");
                    return null;
                });
                addState(2, () -> {
                    if (getPlayerOwner().getID() == 496) {
                        // Tutorial Island rock handler
                        Functions.___message(getPlayerOwner(), "This rock contains " + new Item(def.getOreId()).getDef(getPlayerOwner().getWorld()).getName(), "Sometimes you won't find the ore but trying again may find it", "If a rock contains a high level ore", "You will not find it until you increase your mining level");
                        if (getPlayerOwner().getCache().hasKey("tutorial") && (getPlayerOwner().getCache().getInt("tutorial") == 49))
                            getPlayerOwner().getCache().set("tutorial", 50);

                    } else {
                        if ((def == null) || (def.getRespawnTime() < 1)) {
                            getPlayerOwner().playerServerMessage(MessageType.QUEST, "There is currently no ore available in this rock");
                        } else {
                            getPlayerOwner().playerServerMessage(MessageType.QUEST, "This rock contains " + new Item(def.getOreId()).getDef(getPlayerOwner().getWorld()).getName());
                        }
                    }
                    return null;
                });
            }
        });
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return ((command.equals("mine") || command.equals("prospect")) && (obj.getID() != 588)) && (obj.getID() != 1227);
    }

    /**
     * Returns a gem ID
     */
    public int getGem() {
        int rand = DataConversions.random(0, 100);
        if (rand < 10) {
            return ItemId.UNCUT_DIAMOND.id();
        } else
            if (rand < 30) {
                return ItemId.UNCUT_RUBY.id();
            } else
                if (rand < 60) {
                    return ItemId.UNCUT_EMERALD.id();
                } else {
                    return ItemId.UNCUT_SAPPHIRE.id();
                }


    }

    private int calcAxeBonus(Server server, int axeId) {
        // If server doesn't use batching, pickaxe shouldn't improve gathering chance
        if (!server.getConfig().BATCH_PROGRESSION)
            return 0;

        int bonus = 0;
        switch (ItemId.getById(axeId)) {
            case BRONZE_PICKAXE :
                bonus = 0;
                break;
            case IRON_PICKAXE :
                bonus = 1;
                break;
            case STEEL_PICKAXE :
                bonus = 2;
                break;
            case MITHRIL_PICKAXE :
                bonus = 4;
                break;
            case ADAMANTITE_PICKAXE :
                bonus = 8;
                break;
            case RUNE_PICKAXE :
                bonus = 16;
                break;
        }
        return bonus;
    }

    /**
     * Should we can get an ore from the rock?
     */
    private boolean getOre(Server server, ObjectMiningDef def, int miningLevel, int axeId) {
        return Formulae.calcGatheringSuccessful(def.getReqLevel(), miningLevel, calcAxeBonus(server, axeId));
    }
}

