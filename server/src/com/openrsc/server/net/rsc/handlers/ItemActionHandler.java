package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;

public class ItemActionHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		int idx = (int) p.readShort();
		int amount = p.readInt();
		int commandIndex;

		if (player == null || player.getInventory() == null) {
			return;
		}

		if (idx < -1 || idx >= player.getInventory().size()) {
			player.setSuspiciousPlayer(true, "item idx < -1 or idx >= inv size");
			return;
		}
		Item tempitem = null;

		//User wants to use the item from equipment tab
		if (idx == -1)
		{
			idx = (int) p.readShort();
			if (player.getEquipment().hasEquipped(idx) != -1)
				tempitem = new Item(idx);
			commandIndex = p.readByte();

		} else {
			tempitem = player.getInventory().get(idx);
			commandIndex = p.readByte();
		}

		final Item item = tempitem;
		if (item == null || item.getDef(player.getWorld()).getCommand() == null
		|| commandIndex < 0 || commandIndex >= item.getDef(player.getWorld()).getCommand().length) {
			player.setSuspiciousPlayer(true, "item action item null or null item def");
			return;
		}

		if (item.getDef(player.getWorld()).isMembersOnly() && !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.message("You need to be a member to use this object");
			return;
		}

		if (player.isBusy()) {
			if (player.inCombat()) {
				player.message("You can't do that whilst you are fighting");
			}
			return;
		}

		player.resetAll();

		if (player.getWorld().getServer().getPluginHandler().blockDefaultAction("InvAction",
			new Object[]{item, player, item.getDef(player.getWorld()).getCommand()[commandIndex]})) {
			return;
		}

		if (item.getID() == ItemId.SLEEPING_BAG.id() && !player.isSleeping()) {
			ActionSender.sendEnterSleep(player);
			player.startSleepEvent(false);
			// player.resetPath(); - real rsc.
			return;
		}

		if (item.getDef(player.getWorld()).getCommand()[commandIndex].equalsIgnoreCase("bury")) {
			if (item.getID() == 1308 || item.getID() == 1648 || item.getID() == 1793 || item.getID() == 1871 || item.getID() == 2257) {
				player.message("You can't bury noted bones");
				return;
			}
			if (amount > 1) { // bury all
				player.message("You dig a hole in the ground");
				player.setBatchEvent(new BatchEvent(player.getWorld(), player, 650, String.format("Bury %s", item.getDef(player.getWorld()).getName()), amount, false) {
					@Override
					public void action() {
						buryBonesHelper(player, item);

					}
				});
			} else {
				player.setBusyTimer(1);
				player.message("You dig a hole in the ground");
				player.getWorld().getServer().getGameEventHandler()
					.add(new MiniEvent(player.getWorld(), player, "Bury Bones") {
						public void action() {
							buryBonesHelper(player, item);
						}
					});
			}
		} else {
			switch (ItemId.getById(item.getID())) {
				case DISK_OF_RETURNING:
					if (player.getLocation().onBlackHole()) {
						player.message("You spin your disk of returning");
						player.teleport(311, 3348, true);
						player.getInventory().remove(ItemId.DISK_OF_RETURNING.id(), 1);
					} else {
						player.message("The disk will only work from in Thordur's black hole");
					}
					break;
				case BURNTPIE:
					if (item.getDef(player.getWorld()).getCommand()[commandIndex].equalsIgnoreCase("empty dish")) {
						player.message("you remove the burnt pie from the pie dish");
						player.getInventory().replace(item.getID(), ItemId.PIE_DISH.id());
					}
					break;
				case BURNT_STEW:
					if (item.getDef(player.getWorld()).getCommand()[commandIndex].equalsIgnoreCase("empty")) {
						player.message("you remove the burnt stew from the bowl");
						player.getInventory().replace(item.getID(), ItemId.BOWL.id());
					}
					break;
				case BURNT_CURRY:
					if (item.getDef(player.getWorld()).getCommand()[commandIndex].equalsIgnoreCase("empty")) {
						player.message("you remove the burnt curry from the bowl");
						player.getInventory().replace(item.getID(), ItemId.BOWL.id());
					}
					break;
				case BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER:
					if (item.getDef(player.getWorld()).getCommand()[commandIndex].equalsIgnoreCase("empty")) {
						player.message("You empty the plain water out of the Blessed Golden Bowl.");
						player.getInventory().replace(item.getID(), ItemId.BLESSED_GOLDEN_BOWL.id());
					}
					break;
				case GOLDEN_BOWL_WITH_PLAIN_WATER:
					if (item.getDef(player.getWorld()).getCommand()[commandIndex].equalsIgnoreCase("empty")) {
						player.message("You empty the plain water out of the Golden Bowl.");
						player.getInventory().replace(item.getID(), ItemId.GOLDEN_BOWL.id());
					}
					break;
				case SPADE:
					// nothing - no action/message was triggered with spade's dig option
					break;
				case AIR_TALISMAN:
				case MIND_TALISMAN:
				case WATER_TALISMAN:
				case EARTH_TALISMAN:
				case FIRE_TALISMAN:
				case BODY_TALISMAN:
				case COSMIC_TALISMAN:
				case CHAOS_TALISMAN:
				case NATURE_TALISMAN:
				case LAW_TALISMAN:
				case DEATH_TALISMAN:
				case BLOOD_TALISMAN:
					if (item.getDef(player.getWorld()).getCommand()[commandIndex].equalsIgnoreCase("locate")) {
						if (player.getQuestStage(Quests.RUNE_MYSTERIES) != -1) {
							player.message("You can't understand what the talisman is trying to tell you.");
							return;
						}
						String northORsouth = "", eastORwest = "";
						int playerX, playerY, altarX, altarY;
						playerX = player.getX();
						playerY = player.getY();

						switch (ItemId.getById(item.getID())) {
							case AIR_TALISMAN:
								altarX = 306;
								altarY = 593;
								break;
							case MIND_TALISMAN:
								altarX = 297;
								altarY = 438;
								break;
							case WATER_TALISMAN:
								altarX = 447;
								altarY = 684;
								break;
							case EARTH_TALISMAN:
								altarX = 62;
								altarY = 464;
								break;
							case FIRE_TALISMAN:
								altarX = 50;
								altarY = 633;
								break;
							case BODY_TALISMAN:
								altarX = 259;
								altarY = 503;
								break;
							case COSMIC_TALISMAN:
								altarX = 106;
								altarY = 3565;
								break;
							case CHAOS_TALISMAN:
								altarX = 232;
								altarY = 375;
								break;
							case NATURE_TALISMAN:
								altarX = 392;
								altarY = 804;
								break;
							case LAW_TALISMAN:
								altarX = 409;
								altarY = 534;
								break;
							case DEATH_TALISMAN:
								altarX = 0;
								altarY = 0;
								break;
							case BLOOD_TALISMAN:
								altarX = 0;
								altarY = 0;
								break;
							default:
								altarX = 0;
								altarY = 0;
								break;
						}
						int diffX = altarX - playerX;
						int diffY = altarY - playerY;

						if (diffX != 0)
							eastORwest = diffX > 0 ? "west" : "east";

						if (diffY != 0)
							northORsouth = diffY > 0 ? "south" : "north";

						player.message("The talisman pulls towards the " + northORsouth + eastORwest + ".");
					}
					break;
				default:
					player.message("Nothing interesting happens");
			}
		}
	}

	private void buryBonesHelper(Player owner, Item item) {
		owner.message("You bury the "
			+ item.getDef(owner.getWorld()).getName().toLowerCase());
		owner.getInventory().remove(item);
		switch (ItemId.getById(item.getID())) {
			case BONES:
				owner.incExp(com.openrsc.server.constants.Skills.PRAYER, 15, true); // 3.75
				break;
			case BAT_BONES:
				owner.incExp(com.openrsc.server.constants.Skills.PRAYER, 18, true); // 4.5
				break;
			case BIG_BONES:
				owner.incExp(com.openrsc.server.constants.Skills.PRAYER, 50, true); // 12.5
				break;
			case DRAGON_BONES:
				owner.incExp(com.openrsc.server.constants.Skills.PRAYER, 240, true); // 60
				break;
			default:
				owner.message("Nothing interesting happens");
				break;
		}
	}

	public boolean insideMines(Player p) {
		return ((p.getX() >= 250 && p.getX() <= 315) && (p.getY() >= 3325 && p.getY() <= 3400));
	}
}
