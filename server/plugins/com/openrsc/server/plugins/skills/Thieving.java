package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

public class Thieving extends Functions
	implements ObjectActionListener, NpcCommandListener, NpcCommandExecutiveListener, ObjectActionExecutiveListener,
	WallObjectActionExecutiveListener, WallObjectActionListener {

	private static final String piece_of = "piece of ";

	public static boolean succeedPickLockThieving(Player player, int req_level) {
		//lockpick said to make picking a bit easier
		int effectiveLevel = player.getSkills().getLevel(Skills.THIEVING) + (hasItem(player, ItemId.LOCKPICK.id()) ? 10 : 0);
		return Formulae.calcGatheringSuccessful(req_level, effectiveLevel);
	}

	private boolean succeedThieving(Player player, int req_level) {
		return Formulae.calcGatheringSuccessful(req_level, player.getSkills().getLevel(Skills.THIEVING), 40);
	}

	public void stallThieving(Player player, GameObject object, final Stall stall) {
		player.setBusy(true);
		String objectName = object.getGameObjectDef().getName().toLowerCase();

		if (stall.equals(Stall.BAKERS_STALL))
			player.message("You attempt to steal some cake from the " + objectName);
		else if (stall.equals(Stall.TEA_STALL)) {
			int chance_player_caught = 60;
			Npc teaseller = Functions.getNearestNpc(player, stall.getOwnerID(), 8);
			boolean caught = (chance_player_caught > DataConversions.random(0, 100)) && !teaseller.isBusy();
			if (caught) {
				npcTalk(player, teaseller, "Oi what do you think you are doing ?", "I'm not like those stallholders in Al Kharid", "No one steals from my stall..");
				player.setBusy(false);
				return;
			} else
				player.message("You attempt to steal a cup of tea...");
		} else if (stall.equals(Stall.GEMS_STALL))
			player.message("You attempt to steal gem from the " + objectName);
		else
			player.message("You attempt to steal some " + objectName.replaceAll("stall", "").trim() + " from the " + objectName);

		player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 2, "Stall Thieving") {
			public void init() {
				addState(0, () -> {
					String failNoun = stall.equals(Stall.BAKERS_STALL) ? "cake" : objectName.replaceAll("stall", "").trim();
					if (!failNoun.endsWith("s")) {
						failNoun += "s";
					}
					if (getPlayerOwner().getSkills().getLevel(Skills.THIEVING) < stall.getRequiredLevel()) {
						getPlayerOwner().message("You are not a high enough level to steal the " + failNoun);
						getPlayerOwner().setBusy(false);
						return null;
					}

					Npc shopkeeper = Functions.getNearestNpc(getPlayerOwner(), stall.getOwnerID(), 8);
					Npc guard = null;
					if (stall.equals(Stall.BAKERS_STALL)) {
						guard = getMultipleNpcsInArea(getPlayerOwner(), 5, NpcId.GUARD_ARDOUGNE.id());
					} else if (stall.equals(Stall.SILK_STALL) || stall.equals(Stall.FUR_STALL)) {
						guard = getMultipleNpcsInArea(getPlayerOwner(), 5, NpcId.KNIGHT.id(), NpcId.GUARD_ARDOUGNE.id());
					} else if (stall.equals(Stall.SILVER_STALL) || stall.equals(Stall.SPICES_STALL)) {
						guard = getMultipleNpcsInArea(getPlayerOwner(), 5, NpcId.PALADIN.id(), NpcId.KNIGHT.id(), NpcId.GUARD_ARDOUGNE.id());
					} else if (stall.equals(Stall.GEMS_STALL)) {
						guard = getMultipleNpcsInArea(getPlayerOwner(), 5, NpcId.HERO.id(), NpcId.PALADIN.id(), NpcId.KNIGHT.id(), NpcId.GUARD_ARDOUGNE.id());
					}

					if (shopkeeper != null) {
						if (canBeSeen(getPlayerOwner().getWorld(), shopkeeper.getX(), shopkeeper.getY(), getPlayerOwner().getX(), getPlayerOwner().getY())) {
							Functions.npcYell(getPlayerOwner(), shopkeeper, "Hey thats mine");
							if (!getPlayerOwner().getCache().hasKey("stolenFrom" + stall.getOwnerID())) {
								getPlayerOwner().getCache().store("stolenFrom" + stall.getOwnerID(), true);
							}
							getPlayerOwner().setBusy(false);
							return null;
						}
					}
					if (guard != null) {
						if (canBeSeen(getPlayerOwner().getWorld(), guard.getX(), guard.getY(), getPlayerOwner().getX(), getPlayerOwner().getY())) {
							Functions.npcYell(getPlayerOwner(), guard, "Hey! Get your hands off there!");
							getPlayerOwner().setAttribute("stolenFrom" + stall.getOwnerID(), true);
							guard.setChasing(getPlayerOwner());
							getPlayerOwner().setBusy(false);
							return null;
						}
					}

					int random = DataConversions.random(1, 100);
					Item selectedLoot = null;
					for (LootItem loot : stall.lootTable) {
						if (loot.getChance() >= random) {
							selectedLoot = new Item(loot.getId(), loot.getAmount());
							break;
						}
					}
					if (selectedLoot == null) {
						selectedLoot = new Item(stall.lootTable.get(0).getId(), stall.lootTable.get(0).getAmount());
						getPlayerOwner().setBusy(false);
						return null;
					}
					if (getPlayerOwner().getWorld().getServer().getConfig().WANT_FATIGUE) {
						if (getPlayerOwner().getFatigue() >= getPlayerOwner().MAX_FATIGUE)
							getPlayerOwner().message("@gre@You are too tired to gain experience, get some rest");
					}

					getPlayerOwner().getInventory().add(selectedLoot);
					String loot = stall.equals(Stall.GEMS_STALL) ? "gem" : selectedLoot.getDef(getPlayerOwner().getWorld()).getName().toLowerCase();
					getPlayerOwner().message("You steal a " + stall.getLootPrefix() + loot);

					getPlayerOwner().incExp(Skills.THIEVING, stall.getXp(), true);

					if (stall.equals(Stall.BAKERS_STALL)) { // Cake
						getPlayerOwner().getCache().put("cakeStolen", Instant.now().getEpochSecond());
					} else if (stall.equals(Stall.SILK_STALL)) { // Silk
						getPlayerOwner().getCache().put("silkStolen", Instant.now().getEpochSecond());
					} else if (stall.equals(Stall.FUR_STALL)) { // Fur
						getPlayerOwner().getCache().put("furStolen", Instant.now().getEpochSecond());
					} else if (stall.equals(Stall.SILVER_STALL)) { // Silver
						getPlayerOwner().getCache().put("silverStolen", Instant.now().getEpochSecond());
					} else if (stall.equals(Stall.SPICES_STALL)) { // Spice
						getPlayerOwner().getCache().put("spiceStolen", Instant.now().getEpochSecond());
					} else if (stall.equals(Stall.GEMS_STALL)) { // Gem
						getPlayerOwner().getCache().put("gemStolen", Instant.now().getEpochSecond());
					}

					// Replace stall with empty version
					object.getWorld().replaceGameObject(object,
						new GameObject(getPlayerOwner().getWorld(), object.getLocation(), 341, object.getDirection(), object.getType()));
					object.getWorld().delayedSpawnObject(object.getLoc(), stall.getRespawnTime());
					getPlayerOwner().setBusy(false);
					return null;
				});
			}
		});
	}

	public void handleChestThieving(Player player, GameObject obj) {
		player.setBusy(true);
		int reqtemp = 1;
		int respawnTimetmep = 0;
		ArrayList<LootItem> loottemp = new ArrayList<LootItem>();
		Point teleLoctemp = null;
		int xptemp = 0;
		switch (obj.getID()) {
			case 334:
				// 10gp Chest
				reqtemp = 13;
				xptemp = 30;
				respawnTimetmep = 10000;
				loottemp.add(new LootItem(ItemId.COINS.id(), 10, 100));
				break;
			case 335:
				// Nature-rune Chest
				reqtemp = 28;
				xptemp = 100;
				respawnTimetmep = 25000;
				loottemp = getLootAsList(new LootItem(ItemId.COINS.id(), 3, 100), new LootItem(ItemId.NATURE_RUNE.id(), 1, 100));
				break;
			case 336:
				// 50gp Chest
				reqtemp = 43;
				xptemp = 500;
				respawnTimetmep = 100000;
				loottemp.add(new LootItem(ItemId.COINS.id(), 50, 100));
				break;
			case 337:
				// blood Chest
				reqtemp = 59;
				xptemp = 1000;
				respawnTimetmep = 250000;
				loottemp = getLootAsList(new LootItem(ItemId.COINS.id(), 500, 100), new LootItem(ItemId.BLOOD_RUNE.id(), 2, 100));
				teleLoctemp = Point.location(614, 568);
				break;
			case 338:
				// paladin Chest
				reqtemp = 72;
				xptemp = 2000;
				respawnTimetmep = 500000;
				loottemp = getLootAsList(new LootItem(ItemId.COINS.id(), 1000, 100), new LootItem(ItemId.RAW_SHARK.id(), 1, 100),
					new LootItem(ItemId.ADAMANTITE_ORE.id(), 1, 100), new LootItem(ItemId.UNCUT_SAPPHIRE.id(), 1, 100));
				teleLoctemp = Point.location(523, 606);
				break;
		}
		final int req = reqtemp;
		final int respawnTime = respawnTimetmep;
		final ArrayList<LootItem> loot = loottemp;
		final Point teleLoc = teleLoctemp;
		final int xp = xptemp;
		player.message("You search the chest for traps");
		player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 2, "Chest Thieving") {
			public void init() {
				boolean makeChestStuck = getPlayerOwner().getWorld().getServer().getConfig().LOOTED_CHESTS_STUCK;
				addState(0, () -> {
					if (getPlayerOwner().getSkills().getLevel(Skills.THIEVING) < req) {
						getPlayerOwner().message("You find nothing");
						getPlayerOwner().setBusy(false);
						return null;
					}
					if (getPlayerOwner().getWorld().getServer().getConfig().WANT_FATIGUE) {
						if (getPlayerOwner().getFatigue() >= getPlayerOwner().MAX_FATIGUE) {
							getPlayerOwner().message("You are too tired to thieve here");
							getPlayerOwner().setBusy(false);
							return null;
						}
					}
					//check if the chest is still same
					GameObject checkObj = getPlayerOwner().getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
					if (checkObj == null) {
						getPlayerOwner().message("You find nothing");
						getPlayerOwner().setBusy(false);
						return null;
					}

					GameObject tempChest = null;
					getPlayerOwner().message("You find a trap on the chest");
					if (!makeChestStuck) {
						tempChest = new GameObject(getPlayerOwner().getWorld(), obj.getLocation(), 340, obj.getDirection(), obj.getType());
						replaceObject(obj, tempChest);
					}
					return nextState(2);
				});
				addState(1, () -> {
					GameObject tempChest = null;
					getPlayerOwner().message("You disable the trap");

					message(getPlayerOwner(), "You open the chest");
					if (!makeChestStuck && (tempChest = new GameObject(getPlayerOwner().getWorld(), obj.getLocation(), 340, obj.getDirection(), obj.getType())) != null) {
						openChest(tempChest);
					} else {
						replaceObjectDelayed(obj, respawnTime, 339);
					}
					int random = DataConversions.random(1, 100);
					Collections.sort(loot);
					for (LootItem l : loot) {
						if (l.getChance() >= random) {
							getPlayerOwner().getInventory().add(new Item(l.getId(), l.getAmount()));
						}
					}
					getPlayerOwner().incExp(Skills.THIEVING, xp, true);
					message(getPlayerOwner(), "You find treasure inside!");
					if (!makeChestStuck) {
						replaceObjectDelayed(obj, respawnTime, 340);
					}
					if (teleLoc != null) {
						message(getPlayerOwner(), "suddenly a second magical trap triggers");
						getPlayerOwner().teleport(teleLoc.getX(), teleLoc.getY(), true);
					}
					getPlayerOwner().setBusy(false);
					return null;
				});
			}
		});
	}

	private ArrayList<LootItem> getLootAsList(LootItem... lootItem) {
		ArrayList<LootItem> l = new ArrayList<LootItem>();
		for (LootItem loot : lootItem) {
			l.add(loot);
		}
		return l;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		String formattedName = obj.getGameObjectDef().getName().toUpperCase().replaceAll(" ", "_");

		if (formattedName.contains("STALL")) {
			if (obj.getGameObjectDef().getName().equalsIgnoreCase("empty stall")) {
				return false;
			}
			if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
				player.message(player.MEMBER_MESSAGE);
				return false;
			}
			Stall stall = Stall.valueOf(formattedName);
			if (stall != null) {
				return true;
			}
		} else if (obj.getID() >= 334 && obj.getID() <= 339) {
			if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
				player.message(player.MEMBER_MESSAGE);
				return false;
			}
			return true;
		} else if (obj.getID() == 340) {
			player.message("It looks like this chest has already been looted");
			return true;
		} else if (obj.getID() == 379) { // hemenster chest
			return true;
		}
		return false;
	}

	@Override
	public boolean blockNpcCommand(Npc n, String command, Player p) {
		if (command.equalsIgnoreCase("pickpocket")) {
			Pickpocket pickpocket = null;
			try {
				pickpocket = Pickpocket.valueOf(n.getDef().getName().toUpperCase().replace(" ", "_"));
			} catch (Exception e) {
				//Ignore..
			}

			if (pickpocket != null) {
				if (!p.getWorld().getServer().getConfig().MEMBER_WORLD) {
					p.message(p.MEMBER_MESSAGE);
					return false;
				}
				return true;
			} else if (n.getID() == NpcId.CIVILLIAN_PICKPOCKET.id()) {
				p.message("Nothing interesting happens");
			} else {
				p.message("You can't pickpocket that person, it has not been implemented yet");
			}

		}
		return false;
	}

	public void doPickpocket(final Player player, final Npc npc, final Pickpocket pickpocket) {
		player.face(npc);
		if (npc.inCombat()) {
			player.message("I can't get close enough");
			return;
		}
		final ArrayList<LootItem> lootTable = new ArrayList<LootItem>(pickpocket.getLootTable());
		String thievedMobName = npc.getDef().getName().toLowerCase();
		//gnome local, child, trainer and barman all known as gnome for the thiev messages
		//yanille watchman known simply as watchman
		final String thievedMobSt = (thievedMobName.contains("gnome") || thievedMobName.contains("blurberry")) ? "gnome" :
			thievedMobName.contains("watchman") ? "watchman" : thievedMobName;
		player.playerServerMessage(MessageType.QUEST, "You attempt to pick the " + thievedMobSt + "'s pocket");
		if (player.getSkills().getLevel(Skills.THIEVING) < pickpocket.getRequiredLevel()) {
			player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 3, "Pickpocket Check") {
				public void init() {
					addState(0, () -> {
						getPlayerOwner().message("You need to be a level " + pickpocket.getRequiredLevel() + " thief to pick the " + thievedMobSt + "'s pocket");
						return null;
					});
				}
			});
			return;
		}
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1200, "Thieving Pickpocket", Formulae.getRepeatTimes(player, Skills.THIEVING), true) {
			@Override
			public void action() {
				getOwner().setBusyTimer(2);
				npc.setBusyTimer(2 * 2);
				if (npc.inCombat()) {
					interrupt();
					return;
				}
				if (getOwner().getSkills().getLevel(Skills.THIEVING) < pickpocket.getRequiredLevel()) {
					getOwner().message("You need to be a level " + pickpocket.getRequiredLevel() + " thief to pick the " + thievedMobSt + "'s pocket");
					interrupt();
					return;
				}
				boolean succeededPickpocket = succeedThieving(getOwner(), pickpocket.getRequiredLevel());
				if (succeededPickpocket) {
					if (getWorld().getServer().getConfig().WANT_FATIGUE) {
						if (getOwner().getFatigue() >= getOwner().MAX_FATIGUE)
							getOwner().message("@gre@You are too tired to gain experience, get some rest");
					}

					getOwner().incExp(Skills.THIEVING, pickpocket.getXp(), true);
					Item selectedLoot = null;
					int total = 0;
					for (LootItem loot : lootTable) {
						total += loot.getChance();
					}
					int hit = DataConversions.random(0, total);
					total = 0;
					for (LootItem loot : lootTable) {
						if (loot.getChance() >= 100) {
							getOwner().getInventory().add(new Item(loot.getId(), loot.getAmount()));
							continue;
						}
						if (hit >= total && hit < (total + loot.getChance())) {
							if (loot.getId() == -1) {
								getOwner().message("You find nothing to steal");
								return;
							}
							selectedLoot = (new Item(loot.getId(), loot.getAmount()));
							break;
						}
						total += loot.getChance();
					}
					getOwner().message("You pick the " + thievedMobSt + "'s pocket");
					if (selectedLoot != null) {
						getOwner().getInventory().add(selectedLoot);
					}
				} else {
					getOwner().face(npc);
					getOwner().setBusyTimer(0);
					npc.setBusyTimer(0);
					setDelayTicks(1);
					getOwner().playerServerMessage(MessageType.QUEST, "You fail to pick the " + thievedMobSt + "'s pocket");
					npc.getUpdateFlags()
						.setChatMessage(new ChatMessage(npc, pickpocket.shoutMessage, getOwner()));
					interrupt();
					npc.startCombat(getOwner());
				}
				if (!isCompleted()) {
					getOwner().playerServerMessage(MessageType.QUEST, "You attempt to pick the " + thievedMobSt + "'s pocket");
				}
			}
		});
	}

	@Override
	public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					String formattedName = obj.getGameObjectDef().getName().toUpperCase().replaceAll(" ", "_");
					if (formattedName.contains("STALL")) {
						if (obj.getGameObjectDef().getName().equalsIgnoreCase("empty stall")) {
							return null;
						}
						Stall stall = Stall.valueOf(formattedName);
						if (stall != null) {
							stallThieving(player, obj, stall);
						}
					} else if (obj.getID() >= 334 && obj.getID() <= 339) {
						if (command.contains("trap")) {
							handleChestThieving(player, obj);
						} else {
							player.message("You have activated a trap on the chest");
							player.damage(DataConversions.random(0, 8));
						}
					} else if (obj.getID() == 379) { // HEMENSTER CHEST HARDCODE
						if (command.equalsIgnoreCase("Open")) {
							player.message("This chest is locked");
						} else {
							player.setBusyTimer(5);
							player.message("you attempt to pick the lock");
							if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
								if (player.getFatigue() >= player.MAX_FATIGUE) {
									player.message("You are too tired to thieve here");
									player.setBusyTimer(0);
									return null;
								}
							}
							if (player.getSkills().getLevel(Skills.THIEVING) < 47) {
								player.message("You are not a high enough level to pick this lock");
								player.setBusyTimer(0);
								return null;
							}
							if (!hasItem(player, ItemId.LOCKPICK.id())) {
								player.message("You need a lockpick for this lock");
								player.setBusyTimer(0);
								return null;
							}
							message(player, "You manage to pick the lock");

							openChest(obj);
							message(player, "You open the chest");

							message(player, "You find a treasure inside!");

							player.incExp(Skills.THIEVING, 600, true);
							addItem(player, ItemId.COINS.id(), 20);
							addItem(player, ItemId.STEEL_ARROW_HEADS.id(), 5);

							player.getWorld().replaceGameObject(obj,
								new GameObject(player.getWorld(), obj.getLocation(), 340, obj.getDirection(), obj.getType()));
							player.getWorld().delayedSpawnObject(obj.getLoc(), 150000);
						}
					}

					return null;
				});
			}
		};
	}

	private boolean canBeSeen(World world, int fromX, int fromY, int targetX, int targetY) {
		int count = 0;
		boolean stop = false;
		while (!stop) {
			if (count++ > 10)
				break;
			if (fromY < targetY) {// Target is at South
				fromY++;
			}
			if (fromY > targetY) {// Target is north
				fromY--;
			}
			if (fromX >= targetX) { // Target is at West
				fromX--;
			}
			if (fromX <= targetX) { // Target is East
				fromX++;
			}
			/* If there is no unwalkable object in the way */
			if ((world.getTile(fromX, fromY).traversalMask & 64) != 0) {
				stop = true;
				return false;
			}

			if (fromX == targetX && fromY == targetY) {
				stop = true;
				return true;
			}
		}
		return true;
	}

	/**
	 * Nature rune chest door id (94) [3.11.2013 22:02:55] Kevin: 10gp chest
	 * door id (93) [3.11.2013 22:05:47] Kevin: house where u can find nature
	 * chest + 10gp chest door id (94) [3.11.2013 22:07:17] Kevin: Chaos druid
	 * tower door id (96) [3.11.2013 22:11:49] Kevin: yanille druid door id(162)
	 * [3.11.2013 22:15:08] Kevin: axe huts door id (100) [3.11.2013 22:17:28]
	 * Kevin: pirate hut door id (99) [3.11.2013 22:22:29] Kevin:paladin 2nd
	 * floor door id (97) [3.11.2013 22:23:46] Kevin: the paladin chest give all
	 * the items it says it gie all at once [3.11.2013 22:23:58] Kevin: when you
	 * loot the chest you get teleported at 523, 606 [3.11.2013
	 *
	 * @param player
	 * @param obj
	 */
	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		if (obj.getID() >= 93 && obj.getID() <= 97 || obj.getID() >= 99 & obj.getID() <= 100 || obj.getID() == 162) {
			if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
				player.message(player.MEMBER_MESSAGE);
				return false;
			}
			return true;
		}
		return false;
	}

	private void handlePicklock(GameObject obj, Player player, Integer click) {
		int req = 1;
		int exp = 0;
		boolean goThrough = false; // no need for picklock again.
		boolean requiresLockpick = false;
		switch (obj.getID()) {
			case 93: // 10gp chest door id (93)
				req = 7;
				exp = 15;
				if (player.getY() <= 591) {
					goThrough = true;
				}
				break;
			case 94: // Nature rune chest + 50 gp chest door id (94)
				if (obj.getX() == 586 && obj.getY() == 581 || obj.getX() == 539 && obj.getY() == 599
					|| obj.getX() == 581 && obj.getY() == 580) {
					req = 16;
					exp = 60;
					if (player.getX() == 539 && player.getY() >= 599) {
						goThrough = true;
					} else if (player.getX() <= 585 && player.getY() == 581) {
						goThrough = true;
					} else if (player.getX() >= 581 && player.getY() == 580) {
						goThrough = true;
					}
				}
				break;
			case 95: // Ardougne Sewer mine (95)
				req = 31;
				exp = 100;
				if (player.getX() <= 556) {
					goThrough = true;
				}
				break;
			case 96: // Chaos druid tower door id (96)
				req = 46;
				exp = 150;
				if (player.getY() <= 555) {
					goThrough = true;
				}
				break;
			case 162: // yanille druid door id(162)
				req = 82;
				exp = 200;
				requiresLockpick = true;
				break;
			case 100: // axe huts door id (100)
				req = 32;
				exp = 100;
				requiresLockpick = true;
				if (player.getY() >= 103 && player.getY() <= 107) {
					goThrough = true;
				}
				break;
			case 99: // pirate hut door id (99)
				req = 39;
				exp = 140;
				requiresLockpick = true;
				if ((player.getX() >= 263 && player.getX() <= 269 && player.getY() == 104)
					|| (player.getX() == 266 && player.getY() >= 100)) {
					goThrough = true;
				}
				break;
			case 97:// Ardougne Paladin 2nd floor door id (97)
				req = 61;
				exp = 200;
				if (player.getY() >= 1548 && player.getX() == 609) {
					goThrough = true;
				}
				break;
		}
		if (click == 0) {
			if (goThrough) {
				player.message("You go through the door");
				doDoor(obj, player);
			} else {
				player.message("The door is locked");
			}
		} else if (click == 1) {
			if (goThrough) {
				player.message("You have already unlocked the door");
				return;
			}
			message(player, 1200, "you attempt to pick the lock");

			if (getCurrentLevel(player, Skills.THIEVING) < req) {
				player.message("You are not a high enough level to pick this lock");
				return;
			}
			if (!hasItem(player, ItemId.LOCKPICK.id()) && requiresLockpick) {
				player.message("You need a lockpick for this lock");
				return;
			}
			if (succeedPickLockThieving(player, req) && !goThrough) {
				player.message("You manage to pick the lock");
				doDoor(obj, player);
				player.message("You go through the door");
				if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("@gre@You are too tired to gain experience, get some rest");
						return;
					}
				}
				player.incExp(Skills.THIEVING, (int) exp, true);
			} else {
				player.message("You fail to pick the lock");
			}
		}
	}

	@Override
	public GameStateEvent onNpcCommand(Npc n, String command, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (command.equalsIgnoreCase("pickpocket")) {
						Pickpocket pickpocket = Pickpocket.valueOf(n.getDef().getName().toUpperCase().replace(" ", "_"));
						if (pickpocket != null) {
							doPickpocket(p, n, pickpocket);
						} else {
							p.message("ERROR: Pickpocket handler not found.");
						}
					}

					return null;
				});
			}
		};
	}

	@Override
	public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					handlePicklock(obj, p, click);

					return null;
				});
			}
		};
	}

	enum Pickpocket {
		MAN(1, 32, "Oi what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 3, 100)),
		FARMER(10, 58, "What do you think you're doing",
			new LootItem(ItemId.COINS.id(), 9, 100)),
		WARRIOR(25, 104, "Hey what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 18, 100)),
		WORKMAN(25, 0, "Hey what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 10, 30),
			new LootItem(ItemId.SPADE.id(), 1, 20),
			new LootItem(ItemId.BUCKET.id(), 1, 18),
			new LootItem(ItemId.ROPE.id(), 1, 16),
			new LootItem(ItemId.SPECIMEN_BRUSH.id(), 1, 14),
			new LootItem(ItemId.ROCK_SAMPLE_GREEN.id(), 1, 12),
			new LootItem(ItemId.LEATHER_GLOVES.id(), 1, 10),
			new LootItem(ItemId.NOTHING.id(), 0, 8)),
		ROGUE(32, 146, "Hey what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 25, 40),
			new LootItem(ItemId.COINS.id(), 40, 30),
			new LootItem(ItemId.WINE.id(), 1, 10),
			new LootItem(ItemId.AIR_RUNE.id(), 8, 10),
			new LootItem(ItemId.LOCKPICK.id(), 1, 10),
			new LootItem(ItemId.POISONED_IRON_DAGGER.id(), 1, 3)),
		GUARD(40, 187, "Err what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 30, 100)),
		KNIGHT(55, 337, "Err what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 50, 100)),
		YANILLE_WATCHMAN(65, 550, "Oi you nasty little thief",
			new LootItem(ItemId.COINS.id(), 60, 100),
			new LootItem(ItemId.BREAD.id(), 1, 100)),
		PALADIN(70, 607, "Get your hands off my valuables",
			new LootItem(ItemId.COINS.id(), 80, 100),
			new LootItem(ItemId.CHAOS_RUNE.id(), 1, 100)),
		GNOME_LOCAL(75, 793, "Get your hands off my valuables human",
			new LootItem(ItemId.COINS.id(), 200, 22),
			new LootItem(ItemId.COINS.id(), 400, 18),
			new LootItem(ItemId.GOLD.id(), 1, 10),
			new LootItem(ItemId.EARTH_RUNE.id(), 1, 15),
			new LootItem(ItemId.SWAMP_TOAD.id(), 1, 15),
			new LootItem(ItemId.KING_WORM.id(), 1, 20)),
		GNOME_CHILD(75, 793, "Get your hands off my valuables human",
			new LootItem(ItemId.COINS.id(), 200, 22),
			new LootItem(ItemId.COINS.id(), 400, 18),
			new LootItem(ItemId.GOLD.id(), 1, 10),
			new LootItem(ItemId.EARTH_RUNE.id(), 1, 15),
			new LootItem(ItemId.SWAMP_TOAD.id(), 1, 15),
			new LootItem(ItemId.KING_WORM.id(), 1, 20)),
		GNOME_TRAINER(75, 793, "Get your hands off my valuables human",
			new LootItem(ItemId.COINS.id(), 200, 22),
			new LootItem(ItemId.COINS.id(), 400, 18),
			new LootItem(ItemId.GOLD.id(), 1, 10),
			new LootItem(ItemId.EARTH_RUNE.id(), 1, 15),
			new LootItem(ItemId.SWAMP_TOAD.id(), 1, 15),
			new LootItem(ItemId.KING_WORM.id(), 1, 20)),
		BLURBERRY_BARMAN(75, 793, "Get your hands off my valuables human",
			new LootItem(ItemId.COINS.id(), 200, 22),
			new LootItem(ItemId.COINS.id(), 400, 18),
			new LootItem(ItemId.GOLD.id(), 1, 10),
			new LootItem(ItemId.EARTH_RUNE.id(), 1, 15),
			new LootItem(ItemId.SWAMP_TOAD.id(), 1, 15),
			new LootItem(ItemId.KING_WORM.id(), 1, 20)),
		HERO(80, 1093, "Get your hands off my valuables",
			new LootItem(ItemId.COINS.id(), 100, 25),
			new LootItem(ItemId.COINS.id(), 200, 15),
			new LootItem(ItemId.COINS.id(), 300, 10),
			new LootItem(ItemId.FIRE_ORB.id(), 1, 10),
			new LootItem(ItemId.WINE.id(), 1, 14),
			new LootItem(ItemId.GOLD.id(), 1, 5),
			new LootItem(ItemId.DEATH_RUNE.id(), 2, 10),
			new LootItem(ItemId.BLOOD_RUNE.id(), 1, 5),
			new LootItem(ItemId.DIAMOND.id(), 1, 1));

		private final ArrayList<LootItem> lootTable;
		private final int xp;
		private final int requiredLevel;
		private final String shoutMessage;

		Pickpocket(int req, int xp, String shoutMessage, LootItem... possibleLoot) {
			this.xp = xp;
			this.requiredLevel = req;
			this.shoutMessage = shoutMessage;
			lootTable = new ArrayList<LootItem>();
			for (LootItem lootItem : possibleLoot) {
				lootTable.add(lootItem);
			}
			Collections.sort(lootTable);
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public ArrayList<LootItem> getLootTable() {
			return lootTable;
		}

		public int getXp() {
			return xp;
		}
	}

	enum Stall {
		TEA_STALL(5, 64, NpcId.TEA_SELLER.id(), 5000,
			"", new LootItem(ItemId.CUP_OF_TEA.id(), 1, 100)),
		BAKERS_STALL(5, 64, NpcId.BAKER.id(), 5000,
			"", new LootItem(ItemId.CAKE.id(), 1, 100)),
		SILK_STALL(20, 96, NpcId.SILK_MERCHANT.id(), 8000,
			piece_of, new LootItem(ItemId.SILK.id(), 1, 100)),
		FUR_STALL(35, 144, NpcId.FUR_TRADER.id(), 15000,
			piece_of, new LootItem(ItemId.GREY_WOLF_FUR.id(), 1, 10),
			new LootItem(ItemId.FUR.id(), 1, 100)),
		SILVER_STALL(50, 216, NpcId.SILVER_MERCHANT.id(), 30000,
			piece_of, new LootItem(ItemId.SILVER.id(), 1, 100)),
		SPICES_STALL(65, 324, NpcId.SPICE_MERCHANT.id(), 80000,
			"pot of ", new LootItem(ItemId.SPICE.id(), 1, 100)),
		GEMS_STALL(75, 640, NpcId.GEM_MERCHANT.id(), 180000,
			"", new LootItem(ItemId.UNCUT_SAPPHIRE.id(), 1, 65),
			new LootItem(ItemId.UNCUT_EMERALD.id(), 1, 20),
			new LootItem(ItemId.UNCUT_RUBY.id(), 1, 10),
			new LootItem(ItemId.UNCUT_DIAMOND.id(), 1, 5));

		ArrayList<LootItem> lootTable;
		private String lootPrefix;
		private int xp;
		private int requiredLevel;
		private int respawnTime;
		private int ownerID;

		Stall(int req, int xp, int ownerID, int respawnTime, String lootPrefix, LootItem... loot) {
			this.setXp(xp);
			this.setRespawnTime(respawnTime);
			this.setRequiredLevel(req);
			this.ownerID = ownerID;
			this.setLootPrefix(lootPrefix);
			lootTable = new ArrayList<LootItem>();
			for (LootItem lootItem : loot) {
				lootTable.add(lootItem);
			}
			Collections.sort(lootTable);
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public void setRequiredLevel(int requiredLevel) {
			this.requiredLevel = requiredLevel;
		}

		public int getRespawnTime() {
			return respawnTime;
		}

		public void setRespawnTime(int respawnTime) {
			this.respawnTime = respawnTime;
		}

		public int getXp() {
			return xp;
		}

		public void setXp(int xp) {
			this.xp = xp;
		}

		public int getOwnerID() {
			return ownerID;
		}

		public String getLootPrefix() {
			return lootPrefix;
		}

		public void setLootPrefix(String lootPrefix) {
			this.lootPrefix = lootPrefix;
		}
	}

	static class LootItem implements Comparable<LootItem> {
		private final int id;
		private final int amount;
		private int chance;

		public LootItem(int id, int amount, int chance) {
			this.id = id;
			this.amount = amount;
			this.chance = chance;
		}

		public int getChance() {
			return chance;
		}

		@Override
		public int compareTo(LootItem arg0) {
			if (getChance() > arg0.getChance())
				return 1;
			else
				return -1;
		}

		public int getAmount() {
			return amount;
		}

		public int getId() {
			return id;
		}
	}
}
