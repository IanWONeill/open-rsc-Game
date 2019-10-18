package com.openrsc.server.plugins.skills;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.external.ItemHerbDef;
import com.openrsc.server.external.ItemHerbSecond;
import com.openrsc.server.external.ItemUnIdentHerbDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;


public class Herblaw implements InvActionListener , InvUseOnItemListener , InvActionExecutiveListener , InvUseOnItemExecutiveListener {
    @Override
    public GameStateEvent onInvAction(final Item item, Player player, String command) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (command.equalsIgnoreCase("Identify")) {
                        handleHerbIdentify(item, player);
                    }
                    return null;
                });
            }
        };
    }

    public boolean blockInvAction(final Item i, Player p, String command) {
        return command.equalsIgnoreCase("Identify");
    }

    private boolean handleHerbIdentify(final Item item, Player player) {
        if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
            player.sendMemberErrorMessage();
            return false;
        }
        ItemUnIdentHerbDef herb = item.getUnIdentHerbDef(player.getWorld());
        if (herb == null) {
            return false;
        }
        if (player.getSkills().getLevel(Skills.HERBLAW) < herb.getLevelRequired()) {
            player.playerServerMessage(MessageType.QUEST, "You cannot identify this herb");
            player.playerServerMessage(MessageType.QUEST, "you need a higher herblaw level");
            return false;
        }
        if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
            player.message("You need to complete Druidic ritual quest first");
            return false;
        }
        player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Herblaw Identify Herb", player.getInventory().countId(item.getID()), false) {
            @Override
            public void action() {
                if (getOwner().getSkills().getLevel(Skills.HERBLAW) < herb.getLevelRequired()) {
                    getOwner().playerServerMessage(MessageType.QUEST, "You cannot identify this herb");
                    getOwner().playerServerMessage(MessageType.QUEST, "you need a higher herblaw level");
                    interrupt();
                    return;
                }
                if (getOwner().getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                    getOwner().message("You need to complete Druidic ritual quest first");
                    interrupt();
                    return;
                }
                ItemUnIdentHerbDef herb = item.getUnIdentHerbDef(getWorld());
                Item newItem = new Item(herb.getNewId());
                if (getOwner().getInventory().remove(item.getID(), 1, false) > (-1)) {
                    getOwner().getInventory().add(newItem, true);
                    getOwner().playerServerMessage(MessageType.QUEST, "This herb is " + newItem.getDef(getWorld()).getName());
                    getOwner().incExp(Skills.HERBLAW, herb.getExp(), true);
                }
                getOwner().setBusy(false);
            }
        });
        return true;
    }

    @Override
    public GameStateEvent onInvUseOnItem(Player player, Item item, Item usedWith) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    ItemHerbSecond secondDef = null;
                    if ((secondDef = player.getWorld().getServer().getEntityHandler().getItemHerbSecond(item.getID(), usedWith.getID())) != null) {
                        doHerbSecond(player, item, usedWith, secondDef, false);
                    } else
                        if ((secondDef = player.getWorld().getServer().getEntityHandler().getItemHerbSecond(usedWith.getID(), item.getID())) != null) {
                            doHerbSecond(player, usedWith, item, secondDef, true);
                        } else
                            if (item.getID() == ItemId.PESTLE_AND_MORTAR.id()) {
                                doGrind(player, item, usedWith);
                            } else
                                if (usedWith.getID() == ItemId.PESTLE_AND_MORTAR.id()) {
                                    doGrind(player, usedWith, item);
                                } else
                                    if (item.getID() == ItemId.VIAL.id()) {
                                        doHerblaw(player, item, usedWith);
                                    } else
                                        if (usedWith.getID() == ItemId.VIAL.id()) {
                                            doHerblaw(player, usedWith, item);
                                        } else
                                            if ((item.getID() == ItemId.UNFINISHED_OGRE_POTION.id()) && (usedWith.getID() == ItemId.GROUND_BAT_BONES.id())) {
                                                makeLiquid(player, usedWith, item, true);
                                            } else
                                                if ((item.getID() == ItemId.GROUND_BAT_BONES.id()) && (usedWith.getID() == ItemId.UNFINISHED_OGRE_POTION.id())) {
                                                    makeLiquid(player, item, usedWith, false);
                                                } else
                                                    if ((item.getID() == ItemId.UNFINISHED_POTION.id()) && ((usedWith.getID() == ItemId.GROUND_BAT_BONES.id()) || (usedWith.getID() == ItemId.GUAM_LEAF.id()))) {
                                                        makeLiquid(player, item, usedWith, false);
                                                    } else
                                                        if ((usedWith.getID() == ItemId.UNFINISHED_POTION.id()) && ((item.getID() == ItemId.GROUND_BAT_BONES.id()) || (item.getID() == ItemId.GUAM_LEAF.id()))) {
                                                            makeLiquid(player, usedWith, item, true);
                                                        } else
                                                            if (((usedWith.getID() == ItemId.NITROGLYCERIN.id()) && (item.getID() == ItemId.AMMONIUM_NITRATE.id())) || ((usedWith.getID() == ItemId.AMMONIUM_NITRATE.id()) && (item.getID() == ItemId.NITROGLYCERIN.id()))) {
                                                                if (player.getSkills().getLevel(Skills.HERBLAW) < 10) {
                                                                    player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 10 or over to mix this liquid");
                                                                    return null;
                                                                }
                                                                if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                                                                    player.message("You need to complete Druidic ritual quest first");
                                                                    return null;
                                                                }
                                                                player.incExp(Skills.HERBLAW, 20, true);
                                                                player.playerServerMessage(MessageType.QUEST, "You mix the nitrate powder into the liquid");
                                                                player.message("It has produced a foul mixture");
                                                                Functions.showBubble(player, new Item(ItemId.AMMONIUM_NITRATE.id()));
                                                                player.getInventory().remove(ItemId.AMMONIUM_NITRATE.id(), 1);
                                                                player.getInventory().replace(ItemId.NITROGLYCERIN.id(), ItemId.MIXED_CHEMICALS_1.id());
                                                            } else
                                                                if (((usedWith.getID() == ItemId.GROUND_CHARCOAL.id()) && (item.getID() == ItemId.MIXED_CHEMICALS_1.id())) || ((usedWith.getID() == ItemId.MIXED_CHEMICALS_1.id()) && (item.getID() == ItemId.GROUND_CHARCOAL.id()))) {
                                                                    if (player.getSkills().getLevel(Skills.HERBLAW) < 10) {
                                                                        player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 10 or over to mix this liquid");
                                                                        return null;
                                                                    }
                                                                    if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                                                                        player.message("You need to complete Druidic ritual quest first");
                                                                        return null;
                                                                    }
                                                                    player.incExp(Skills.HERBLAW, 25, true);
                                                                    player.playerServerMessage(MessageType.QUEST, "You mix the charcoal into the liquid");
                                                                    player.message("It has produced an even fouler mixture");
                                                                    Functions.showBubble(player, new Item(ItemId.GROUND_CHARCOAL.id()));
                                                                    player.getInventory().remove(ItemId.GROUND_CHARCOAL.id(), 1);
                                                                    player.getInventory().replace(ItemId.MIXED_CHEMICALS_1.id(), ItemId.MIXED_CHEMICALS_2.id());
                                                                } else
                                                                    if (((usedWith.getID() == ItemId.ARCENIA_ROOT.id()) && (item.getID() == ItemId.MIXED_CHEMICALS_2.id())) || ((usedWith.getID() == ItemId.MIXED_CHEMICALS_2.id()) && (item.getID() == ItemId.ARCENIA_ROOT.id()))) {
                                                                        if (player.getSkills().getLevel(Skills.HERBLAW) < 10) {
                                                                            player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 10 or over to mix this liquid");
                                                                            return null;
                                                                        }
                                                                        if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                                                                            player.message("You need to complete Druidic ritual quest first");
                                                                            return null;
                                                                        }
                                                                        player.incExp(Skills.HERBLAW, 30, true);
                                                                        player.message("You mix the root into the mixture");
                                                                        player.message("You produce a potentially explosive compound...");
                                                                        Functions.showBubble(player, new Item(ItemId.ARCENIA_ROOT.id()));
                                                                        player.getInventory().remove(ItemId.ARCENIA_ROOT.id(), 1);
                                                                        player.getInventory().replace(ItemId.MIXED_CHEMICALS_2.id(), ItemId.EXPLOSIVE_COMPOUND.id());
                                                                        Functions.___playerTalk(player, null, "Excellent this looks just right");
                                                                    } else
                                                                        if (((usedWith.getID() == ItemId.UNFINISHED_HARRALANDER_POTION.id()) && (item.getID() == ItemId.BLAMISH_SNAIL_SLIME.id())) || ((usedWith.getID() == ItemId.BLAMISH_SNAIL_SLIME.id()) && (item.getID() == ItemId.UNFINISHED_HARRALANDER_POTION.id()))) {
                                                                            if (player.getSkills().getLevel(Skills.HERBLAW) < 25) {
                                                                                player.playerServerMessage(MessageType.QUEST, "You need a herblaw level of 25 to make this potion");
                                                                                return null;
                                                                            }
                                                                            if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                                                                                player.message("You need to complete Druidic ritual quest first");
                                                                                return null;
                                                                            }
                                                                            player.incExp(Skills.HERBLAW, 320, true);
                                                                            player.message("You mix the slime into your potion");
                                                                            player.getInventory().remove(ItemId.UNFINISHED_HARRALANDER_POTION.id(), 1);
                                                                            player.getInventory().replace(ItemId.BLAMISH_SNAIL_SLIME.id(), ItemId.BLAMISH_OIL.id());
                                                                        } else
                                                                            if (((usedWith.getID() == ItemId.SNAKES_WEED_SOLUTION.id()) && (item.getID() == ItemId.ARDRIGAL.id())) || ((usedWith.getID() == ItemId.ARDRIGAL.id()) && (item.getID() == ItemId.SNAKES_WEED_SOLUTION.id()))) {
                                                                                if (player.getSkills().getLevel(Skills.HERBLAW) < 45) {
                                                                                    player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 45 or over to mix this potion");
                                                                                    return null;
                                                                                }
                                                                                if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                                                                                    player.message("You need to complete Druidic ritual quest first");
                                                                                    return null;
                                                                                }
                                                                                // player needs to have learned secret from gujuo
                                                                                if ((player.getQuestStage(Quests.LEGENDS_QUEST) >= 0) && (player.getQuestStage(Quests.LEGENDS_QUEST) < 7)) {
                                                                                    player.message("You're not quite sure what effect this will have.");
                                                                                    player.message("You decide against experimenting.");
                                                                                    return null;
                                                                                }
                                                                                player.message("You add the Ardrigal to the Snakesweed Solution.");
                                                                                player.message("The mixture seems to bubble slightly with a strange effervescence...");
                                                                                player.getInventory().remove(ItemId.ARDRIGAL.id(), 1);
                                                                                player.getInventory().replace(ItemId.SNAKES_WEED_SOLUTION.id(), ItemId.GUJUO_POTION.id());
                                                                            } else
                                                                                if (((usedWith.getID() == ItemId.ARDRIGAL_SOLUTION.id()) && (item.getID() == ItemId.SNAKE_WEED.id())) || ((usedWith.getID() == ItemId.SNAKE_WEED.id()) && (item.getID() == ItemId.ARDRIGAL_SOLUTION.id()))) {
                                                                                    if (player.getSkills().getLevel(Skills.HERBLAW) < 45) {
                                                                                        player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 45 or over to mix this potion");
                                                                                        return null;
                                                                                    }
                                                                                    if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                                                                                        player.message("You need to complete Druidic ritual quest first");
                                                                                        return null;
                                                                                    }
                                                                                    // player needs to have learned secret from gujuo
                                                                                    if ((player.getQuestStage(Quests.LEGENDS_QUEST) >= 0) && (player.getQuestStage(Quests.LEGENDS_QUEST) < 7)) {
                                                                                        player.message("You're not quite sure what effect this will have.");
                                                                                        player.message("You decide against experimenting.");
                                                                                        return null;
                                                                                    }
                                                                                    player.message("You add the Snake Weed to the Ardrigal solution.");
                                                                                    player.message("The mixture seems to bubble slightly with a strange effervescence...");
                                                                                    player.getInventory().remove(ItemId.SNAKE_WEED.id(), 1);
                                                                                    player.getInventory().replace(ItemId.ARDRIGAL_SOLUTION.id(), ItemId.GUJUO_POTION.id());
                                                                                }















                    return null;
                });
            }
        };
    }

    public boolean blockInvUseOnItem(Player p, Item item, Item usedWith) {
        if ((p.getWorld().getServer().getEntityHandler().getItemHerbSecond(item.getID(), usedWith.getID()) != null) || (p.getWorld().getServer().getEntityHandler().getItemHerbSecond(usedWith.getID(), item.getID()) != null)) {
            return true;
        } else
            if ((item.getID() == ItemId.PESTLE_AND_MORTAR.id()) || (usedWith.getID() == ItemId.PESTLE_AND_MORTAR.id())) {
                return true;
            } else
                if ((item.getID() == ItemId.VIAL.id()) || (usedWith.getID() == ItemId.VIAL.id())) {
                    return true;
                } else
                    if (((item.getID() == ItemId.UNFINISHED_OGRE_POTION.id()) && (usedWith.getID() == ItemId.GROUND_BAT_BONES.id())) || ((item.getID() == ItemId.GROUND_BAT_BONES.id()) && (usedWith.getID() == ItemId.UNFINISHED_OGRE_POTION.id()))) {
                        return true;
                    } else
                        if (((item.getID() == ItemId.UNFINISHED_POTION.id()) && ((usedWith.getID() == ItemId.GROUND_BAT_BONES.id()) || (usedWith.getID() == ItemId.GUAM_LEAF.id()))) || ((usedWith.getID() == ItemId.UNFINISHED_POTION.id()) && ((item.getID() == ItemId.GROUND_BAT_BONES.id()) || (item.getID() == ItemId.GUAM_LEAF.id())))) {
                            return true;
                        } else
                            if (((usedWith.getID() == ItemId.NITROGLYCERIN.id()) && (item.getID() == ItemId.AMMONIUM_NITRATE.id())) || ((usedWith.getID() == ItemId.AMMONIUM_NITRATE.id()) && (item.getID() == ItemId.NITROGLYCERIN.id()))) {
                                return true;
                            } else
                                if (((usedWith.getID() == ItemId.GROUND_CHARCOAL.id()) && (item.getID() == ItemId.MIXED_CHEMICALS_1.id())) || ((usedWith.getID() == ItemId.MIXED_CHEMICALS_1.id()) && (item.getID() == ItemId.GROUND_CHARCOAL.id()))) {
                                    return true;
                                } else
                                    if (((usedWith.getID() == ItemId.ARCENIA_ROOT.id()) && (item.getID() == ItemId.MIXED_CHEMICALS_2.id())) || ((usedWith.getID() == ItemId.MIXED_CHEMICALS_2.id()) && (item.getID() == ItemId.ARCENIA_ROOT.id()))) {
                                        return true;
                                    } else
                                        if (((usedWith.getID() == ItemId.UNFINISHED_HARRALANDER_POTION.id()) && (item.getID() == ItemId.BLAMISH_SNAIL_SLIME.id())) || ((usedWith.getID() == ItemId.BLAMISH_SNAIL_SLIME.id()) && (item.getID() == ItemId.UNFINISHED_HARRALANDER_POTION.id()))) {
                                            return true;
                                        } else
                                            if (((usedWith.getID() == ItemId.SNAKES_WEED_SOLUTION.id()) && (item.getID() == ItemId.ARDRIGAL.id())) || ((usedWith.getID() == ItemId.ARDRIGAL.id()) && (item.getID() == ItemId.SNAKES_WEED_SOLUTION.id()))) {
                                                return true;
                                            } else
                                                if (((usedWith.getID() == ItemId.ARDRIGAL_SOLUTION.id()) && (item.getID() == ItemId.SNAKE_WEED.id())) || ((usedWith.getID() == ItemId.SNAKE_WEED.id()) && (item.getID() == ItemId.ARDRIGAL_SOLUTION.id()))) {
                                                    return true;
                                                }










        return false;
    }

    private boolean doHerblaw(Player player, final Item vial, final Item herb) {
        if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
            player.sendMemberErrorMessage();
            return false;
        }
        if ((vial.getID() == ItemId.VIAL.id()) && (herb.getID() == ItemId.GROUND_BAT_BONES.id())) {
            player.message("You mix the ground bones into the water");
            player.message("Fizz!!!");
            Functions.___playerTalk(player, null, "Oh dear, the mixture has evaporated!", "It's useless...");
            player.getInventory().remove(vial.getID(), 1);
            player.getInventory().remove(herb.getID(), 1);
            player.getInventory().add(new Item(ItemId.EMPTY_VIAL.id(), 1));
            return false;
        }
        if ((vial.getID() == ItemId.VIAL.id()) && (herb.getID() == ItemId.JANGERBERRIES.id())) {
            player.message("You mix the berries into the water");
            player.getInventory().remove(vial.getID(), 1);
            player.getInventory().remove(herb.getID(), 1);
            player.getInventory().add(new Item(ItemId.UNFINISHED_POTION.id(), 1));
            return false;
        }
        if ((vial.getID() == ItemId.VIAL.id()) && (herb.getID() == ItemId.ARDRIGAL.id())) {
            player.message("You put the ardrigal herb into the watervial.");
            player.message("You make a solution of Ardrigal.");
            player.getInventory().remove(vial.getID(), 1);
            player.getInventory().remove(herb.getID(), 1);
            player.getInventory().add(new Item(ItemId.ARDRIGAL_SOLUTION.id(), 1));
            return false;
        }
        if ((vial.getID() == ItemId.VIAL.id()) && (herb.getID() == ItemId.SNAKE_WEED.id())) {
            player.message("You put the Snake Weed herb into the watervial.");
            player.message("You make a solution of Snake Weed.");
            player.getInventory().remove(vial.getID(), 1);
            player.getInventory().remove(herb.getID(), 1);
            player.getInventory().add(new Item(ItemId.SNAKES_WEED_SOLUTION.id(), 1));
            return false;
        }
        final ItemHerbDef herbDef = player.getWorld().getServer().getEntityHandler().getItemHerbDef(herb.getID());
        if (herbDef == null) {
            return false;
        }
        int repeatTimes = player.getInventory().countId(ItemId.VIAL.id());
        repeatTimes = (player.getInventory().countId(herb.getID()) < repeatTimes) ? player.getInventory().countId(herb.getID()) : repeatTimes;
        player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1200, "Herblaw Make Potion", repeatTimes, false) {
            @Override
            public void action() {
                if (getOwner().getSkills().getLevel(Skills.HERBLAW) < herbDef.getReqLevel()) {
                    getOwner().playerServerMessage(MessageType.QUEST, ("you need level " + herbDef.getReqLevel()) + " herblaw to make this potion");
                    interrupt();
                    return;
                }
                if (getOwner().getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                    getOwner().message("You need to complete Druidic ritual quest first");
                    interrupt();
                    return;
                }
                if (getOwner().getInventory().hasItemId(vial.getID()) && getOwner().getInventory().hasItemId(herb.getID())) {
                    getOwner().getInventory().remove(vial.getID(), 1);
                    getOwner().getInventory().remove(herb.getID(), 1);
                    getOwner().playSound("mix");
                    getOwner().playerServerMessage(MessageType.QUEST, ("You put the " + herb.getDef(getWorld()).getName()) + " into the vial of water");
                    getOwner().getInventory().add(new Item(herbDef.getPotionId(), 1));
                } else {
                    interrupt();
                }
            }
        });
        return true;
    }

    private boolean doHerbSecond(Player player, final Item second, final Item unfinished, final ItemHerbSecond def, final boolean isSwapped) {
        if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
            player.sendMemberErrorMessage();
            return false;
        }
        if (unfinished.getID() != def.getUnfinishedID()) {
            return false;
        }
        final AtomicReference<Item> bubbleItem = new AtomicReference<Item>();
        bubbleItem.set(null);
        // constraint shaman potion
        if (((second.getID() == ItemId.JANGERBERRIES.id()) && (unfinished.getID() == ItemId.UNFINISHED_GUAM_POTION.id())) && ((player.getQuestStage(Quests.WATCHTOWER) >= 0) && (player.getQuestStage(Quests.WATCHTOWER) < 6))) {
            Functions.___playerTalk(player, null, "Hmmm...perhaps I shouldn't try and mix these items together", "It might have unpredictable results...");
            return false;
        } else
            if ((second.getID() == ItemId.JANGERBERRIES.id()) && (unfinished.getID() == ItemId.UNFINISHED_GUAM_POTION.id())) {
                if (!isSwapped) {
                    bubbleItem.set(unfinished);
                } else {
                    bubbleItem.set(second);
                }
            }

        int repeatTimes = player.getInventory().countId(unfinished.getID());
        repeatTimes = (player.getInventory().countId(second.getID()) < repeatTimes) ? player.getInventory().countId(second.getID()) : repeatTimes;
        player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1200, "Herblaw Make Potion", player.getInventory().countId(unfinished.getID()), false) {
            @Override
            public void action() {
                if (getOwner().getSkills().getLevel(Skills.HERBLAW) < def.getReqLevel()) {
                    getOwner().playerServerMessage(MessageType.QUEST, ("You need a herblaw level of " + def.getReqLevel()) + " to make this potion");
                    interrupt();
                    return;
                }
                if (getOwner().getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                    getOwner().message("You need to complete Druidic ritual quest first");
                    interrupt();
                    return;
                }
                if (getOwner().getInventory().hasItemId(second.getID()) && getOwner().getInventory().hasItemId(unfinished.getID())) {
                    if (bubbleItem.get() != null) {
                        Functions.showBubble(getOwner(), bubbleItem.get());
                    }
                    getOwner().playSound("mix");
                    getOwner().playerServerMessage(MessageType.QUEST, ("You mix the " + second.getDef(getWorld()).getName()) + " into your potion");
                    getOwner().getInventory().remove(second.getID(), 1);
                    getOwner().getInventory().remove(unfinished.getID(), 1);
                    getOwner().getInventory().add(new Item(def.getPotionID(), 1));
                    getOwner().incExp(Skills.HERBLAW, def.getExp(), true);
                } else
                    interrupt();

            }
        });
        return false;
    }

    private boolean makeLiquid(Player p, final Item ingredient, final Item unfinishedPot, final boolean isSwapped) {
        if (!p.getWorld().getServer().getConfig().MEMBER_WORLD) {
            p.sendMemberErrorMessage();
            return false;
        }
        if (((unfinishedPot.getID() == ItemId.UNFINISHED_POTION.id()) && ((ingredient.getID() == ItemId.GROUND_BAT_BONES.id()) || (ingredient.getID() == ItemId.GUAM_LEAF.id()))) || ((ingredient.getID() == ItemId.UNFINISHED_POTION.id()) && ((unfinishedPot.getID() == ItemId.GROUND_BAT_BONES.id()) || (unfinishedPot.getID() == ItemId.GUAM_LEAF.id())))) {
            p.playerServerMessage(MessageType.QUEST, "You mix the liquid with the " + ingredient.getDef(p.getWorld()).getName().toLowerCase());
            p.message("Bang!!!");
            Functions.displayTeleportBubble(p, p.getX(), p.getY(), true);
            p.damage(8);
            Functions.___playerTalk(p, null, "Ow!");
            p.playerServerMessage(MessageType.QUEST, "You mixed this ingredients incorrectly and the mixture exploded!");
            p.getInventory().remove(unfinishedPot.getID(), 1);
            p.getInventory().remove(ingredient.getID(), 1);
            p.getInventory().add(new Item(ItemId.EMPTY_VIAL.id(), 1));
            return false;
        }
        if (((unfinishedPot.getID() == ItemId.UNFINISHED_OGRE_POTION.id()) && (ingredient.getID() == ItemId.GROUND_BAT_BONES.id())) || ((unfinishedPot.getID() == ItemId.GROUND_BAT_BONES.id()) && (ingredient.getID() == ItemId.UNFINISHED_OGRE_POTION.id()))) {
            if (p.getSkills().getLevel(Skills.HERBLAW) < 14) {
                p.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 14 or over to mix this liquid");
                return false;
            }
            if (p.getQuestStage(Quests.DRUIDIC_RITUAL) != (-1)) {
                p.message("You need to complete Druidic ritual quest first");
                return false;
            }
            if ((p.getQuestStage(Quests.WATCHTOWER) >= 0) && (p.getQuestStage(Quests.WATCHTOWER) < 6)) {
                Functions.___playerTalk(p, null, "Hmmm...perhaps I shouldn't try and mix these items together", "It might have unpredictable results...");
                return false;
            } else
                if (p.getInventory().hasItemId(ingredient.getID()) && p.getInventory().hasItemId(unfinishedPot.getID())) {
                    if (!isSwapped) {
                        Functions.showBubble(p, unfinishedPot);
                    } else {
                        Functions.showBubble(p, ingredient);
                    }
                    p.playerServerMessage(MessageType.QUEST, ("You mix the " + ingredient.getDef(p.getWorld()).getName().toLowerCase()) + " into the liquid");
                    p.playerServerMessage(MessageType.QUEST, "You produce a strong potion");
                    p.getInventory().remove(ingredient.getID(), 1);
                    p.getInventory().remove(unfinishedPot.getID(), 1);
                    p.getInventory().add(new Item(ItemId.OGRE_POTION.id(), 1));
                    // the other half has been done already
                    p.incExp(Skills.HERBLAW, 100, true);
                }

        }
        return false;
    }

    private boolean doGrind(Player player, final Item mortar, final Item item) {
        if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
            player.sendMemberErrorMessage();
            return false;
        }
        int newID;
        switch (ItemId.getById(item.getID())) {
            case UNICORN_HORN :
                newID = ItemId.GROUND_UNICORN_HORN.id();
                break;
            case BLUE_DRAGON_SCALE :
                newID = ItemId.GROUND_BLUE_DRAGON_SCALE.id();
                break;
                /**
                 * Quest items.
                 */
            case BAT_BONES :
                newID = ItemId.GROUND_BAT_BONES.id();
                break;
            case A_LUMP_OF_CHARCOAL :
                newID = ItemId.GROUND_CHARCOAL.id();
                player.message("You grind the charcoal to a powder");
                break;
            case CHOCOLATE_BAR :
                newID = ItemId.CHOCOLATE_DUST.id();
                break;
                /**
                 * End of Herblaw Quest Items.
                 */
            default :
                return false;
        }
        player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Herblaw Grind", player.getInventory().countId(item.getID()), false) {
            @Override
            public void action() {
                if (getOwner().getInventory().remove(item) > (-1)) {
                    if (item.getID() != ItemId.A_LUMP_OF_CHARCOAL.id()) {
                        getOwner().playerServerMessage(MessageType.QUEST, ("You grind the " + item.getDef(getWorld()).getName()) + " to dust");
                    }
                    Functions.showBubble(getOwner(), new Item(ItemId.PESTLE_AND_MORTAR.id()));
                    getOwner().getInventory().add(new Item(newID, 1));
                }
            }
        });
        return true;
    }
}

