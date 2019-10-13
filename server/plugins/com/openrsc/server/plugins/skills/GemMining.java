package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.external.ObjectMiningDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.Functions.showBubble;

public class GemMining implements ObjectActionListener,
	ObjectActionExecutiveListener {

	private static final int GEM_ROCK = 588;

	private static final int[] gemWeightsWithoutDragonstone = {64, 32, 16, 8, 3, 3, 2};
	private static final int[] gemWeightsWithDragonstone = {60, 30, 15, 9, 5, 5, 4};
	private static final int[] gemIds = {
		ItemId.UNCUT_OPAL.id(),
		ItemId.UNCUT_JADE.id(),
		ItemId.UNCUT_RED_TOPAZ.id(),
		ItemId.UNCUT_SAPPHIRE.id(),
		ItemId.UNCUT_EMERALD.id(),
		ItemId.UNCUT_RUBY.id(),
		ItemId.UNCUT_DIAMOND.id()
	};

	private void handleGemRockMining(final GameObject obj, Player p, int click) {
		final ObjectMiningDef def = p.getWorld().getServer().getEntityHandler().getObjectMiningDef(obj.getID());
		final int axeId = getAxe(p);
		final int mineLvl = p.getSkills().getLevel(com.openrsc.server.constants.Skills.MINING);
		final int retrytimes;
		final int reqlvl;
		switch (ItemId.getById(axeId)) {
			default:
			case BRONZE_PICKAXE:
				retrytimes = 1;
				reqlvl = 1;
				break;
			case IRON_PICKAXE:
				retrytimes = 2;
				reqlvl = 1;
				break;
			case STEEL_PICKAXE:
				retrytimes = 3;
				reqlvl = 6;
				break;
			case MITHRIL_PICKAXE:
				retrytimes = 5;
				reqlvl = 21;
				break;
			case ADAMANTITE_PICKAXE:
				retrytimes = 8;
				reqlvl = 31;
				break;
			case RUNE_PICKAXE:
				retrytimes = 12;
				reqlvl = 41;
				break;
		}

		p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "Gem Mining") {
			public void init() {
				addState(0, () -> {
					if (getPlayerOwner().isBusy()) {
						return null;
					}
					if (!getPlayerOwner().withinRange(obj, 1)) {
						return null;
					}

					if (getPlayerOwner().click == 1) {
						getPlayerOwner().playSound("prospect");
						getPlayerOwner().setBusyTimer(getPlayerOwner().getWorld().getServer().getConfig().GAME_TICK * 3);
						getPlayerOwner().playerServerMessage(MessageType.QUEST, "You examine the rock for ores...");
						return invoke(1, 3);
					}

					if (axeId < 0 || reqlvl > mineLvl) {
						message(getPlayerOwner(), "You need a pickaxe to mine this rock",
							"You do not have a pickaxe which you have the mining level to use");
						return null;
					}

					if (getPlayerOwner().getWorld().getServer().getConfig().WANT_FATIGUE) {
						if (getPlayerOwner().getFatigue() >= getPlayerOwner().MAX_FATIGUE) {
							getPlayerOwner().playerServerMessage(MessageType.QUEST, "You are too tired to mine this rock");
							return null;
						}
					}

					getPlayerOwner().playSound("mine");
					showBubble(getPlayerOwner(), new Item(ItemId.IRON_PICKAXE.id()));
					getPlayerOwner().playerServerMessage(MessageType.QUEST, "You have a swing at the rock!");
					getPlayerOwner().setBatchEvent(new BatchEvent(getPlayerOwner().getWorld(), getPlayerOwner(), getPlayerOwner().getWorld().getServer().getConfig().GAME_TICK * 3, "Gem Mining", getPlayerOwner().getWorld().getServer().getConfig().BATCH_PROGRESSION ? Formulae.getRepeatTimes(getPlayerOwner(), com.openrsc.server.constants.Skills.MINING) : retrytimes + 1000, true) {
						@Override
						public void action() {
							if (getGem(getPlayerOwner(), 40, getOwner().getSkills().getLevel(com.openrsc.server.constants.Skills.MINING), axeId) && mineLvl >= 40) { // always 40 required mining.
								Item gem = new Item(getGemFormula(getPlayerOwner().getInventory().wielding(ItemId.CHARGED_DRAGONSTONE_AMULET.id())), 1);
								//check if there is still gem at the rock
								GameObject object = getOwner().getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
								if (object == null) {
									getOwner().playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
								} else {
									getOwner().message(minedString(gem.getID()));
									getOwner().incExp(com.openrsc.server.constants.Skills.MINING, 200, true); // always 50XP
									getOwner().getInventory().add(gem);
								}

								if (!getWorld().getServer().getConfig().MINING_ROCKS_EXTENDED || DataConversions.random(1, 100) <= 39) {
									interrupt();
									if (object != null && object.getID() == obj.getID()) {
										GameObject newObject = new GameObject(getWorld(), obj.getLocation(), 98, obj.getDirection(), obj.getType());
										getWorld().replaceGameObject(obj, newObject);
										getWorld().delayedSpawnObject(object.getLoc(), 120 * 1000); // 2 minute respawn time
									}
								}
							} else {
								getOwner().playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
								if (getRepeatFor() > 1) {
									GameObject checkObj = getOwner().getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
									if (checkObj == null) {
										interrupt();
									}
								}
							}
							if (!isCompleted()) {
								showBubble(getOwner(), new Item(ItemId.IRON_PICKAXE.id()));
								getOwner().playerServerMessage(MessageType.QUEST, "You have a swing at the rock!");
							}
						}
					});

					return null;
				});
				addState(1, () -> {
					if (obj.getID() == GEM_ROCK) {
						getPlayerOwner().playerServerMessage(MessageType.QUEST, "You fail to find anything interesting");
						return null;
					}
					//should not get into the else, just a fail-safe
					else {
						getPlayerOwner().playerServerMessage(MessageType.QUEST, "There is currently no ore available in this rock");
						return null;
					}
				});
			}
		});
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == GEM_ROCK && (command.equals("mine") || command.equals("prospect"));
	}

	@Override
	public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (obj.getID() == GEM_ROCK && (command.equals("mine") || command.equals("prospect"))) {
						handleGemRockMining(obj, p, p.click);
					}

					return null;
				});
			}
		};
	}

	private int getAxe(Player p) {
		int lvl = p.getSkills().getLevel(com.openrsc.server.constants.Skills.MINING);
		for (int i = 0; i < Formulae.miningAxeIDs.length; i++) {
			if (p.getInventory().countId(Formulae.miningAxeIDs[i]) > 0) {
				if (lvl >= Formulae.miningAxeLvls[i]) {
					return Formulae.miningAxeIDs[i];
				}
			}
		}
		return -1;
	}

	private int calcAxeBonus(int axeId) { // No evidence wielding different pickaxes gives a bonus, only more swings
		/*switch (axeId) {
			case BRONZE_PICKAXE:
				bonus = 0;
				break;
			case IRON_PICKAXE:
				bonus = 1;
				break;
			case STEEL_PICKAXE:
				bonus = 2;
				break;
			case MITHRIL_PICKAXE:
				bonus = 4;
				break;
			case ADAMANTITE_PICKAXE:
				bonus = 8;
				break;
			case RUNE_PICKAXE:
				bonus = 16;
				break;
		}*/
		return 0;
	}

	private boolean getGem(Player p, int req, int miningLevel, int axeId) {
		return Formulae.calcGatheringSuccessful(req, miningLevel, calcAxeBonus(axeId));
	}

	/**
	 * Returns a gem ID
	 */
	private int getGemFormula(boolean dragonstoneAmmy) {
		return dragonstoneAmmy ?
			Formulae.weightedRandomChoice(gemIds, gemWeightsWithDragonstone) :
			Formulae.weightedRandomChoice(gemIds, gemWeightsWithoutDragonstone);
	}

	private String minedString(int gemID) {
		if (gemID == ItemId.UNCUT_OPAL.id()) {
			return "You just mined an Opal!";
		} else if (gemID == ItemId.UNCUT_JADE.id()) {
			return "You just mined a piece of Jade!";
		} else if (gemID == ItemId.UNCUT_RED_TOPAZ.id()) {
			return "You just mined a Red Topaz!";
		} else if (gemID == ItemId.UNCUT_SAPPHIRE.id()) {
			return "You just found a sapphire!";
		} else if (gemID == ItemId.UNCUT_EMERALD.id()) {
			return "You just found an emerald!";
		} else if (gemID == ItemId.UNCUT_RUBY.id()) {
			return "You just found a ruby!";
		} else if (gemID == ItemId.UNCUT_DIAMOND.id()) {
			return "You just found a diamond!";
		}
		return null;
	}
}
