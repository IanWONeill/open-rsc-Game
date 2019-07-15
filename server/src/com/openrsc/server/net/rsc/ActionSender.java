package com.openrsc.server.net.rsc;

import com.openrsc.server.GameStateUpdater;
import com.openrsc.server.Server;
import com.openrsc.server.content.clan.Clan;
import com.openrsc.server.content.clan.ClanManager;
import com.openrsc.server.content.clan.ClanPlayer;
import com.openrsc.server.content.market.Market;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.ConnectionAttachment;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.net.RSCConnectionHandler;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.util.rsc.CaptchaGenerator;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import static com.openrsc.server.Constants.GameServer.ALLOW_RESIZE;
import static com.openrsc.server.Constants.GameServer.AUTO_MESSAGE_SWITCH_TOGGLE;
import static com.openrsc.server.Constants.GameServer.BATCH_PROGRESSION;
import static com.openrsc.server.Constants.GameServer.CUSTOM_FIREMAKING;
import static com.openrsc.server.Constants.GameServer.DEBUG;
import static com.openrsc.server.Constants.GameServer.DISPLAY_LOGO_SPRITE;
import static com.openrsc.server.Constants.GameServer.EXPERIENCE_COUNTER_TOGGLE;
import static com.openrsc.server.Constants.GameServer.EXPERIENCE_DROPS_TOGGLE;
import static com.openrsc.server.Constants.GameServer.FIGHTMODE_SELECTOR_TOGGLE;
import static com.openrsc.server.Constants.GameServer.FISHING_SPOTS_DEPLETABLE;
import static com.openrsc.server.Constants.GameServer.FIX_OVERHEAD_CHAT;
import static com.openrsc.server.Constants.GameServer.FOG_TOGGLE;
import static com.openrsc.server.Constants.GameServer.FPS;
import static com.openrsc.server.Constants.GameServer.GROUND_ITEM_TOGGLE;
import static com.openrsc.server.Constants.GameServer.INVENTORY_COUNT_TOGGLE;
import static com.openrsc.server.Constants.GameServer.ITEMS_ON_DEATH_MENU;
import static com.openrsc.server.Constants.GameServer.LENIENT_CONTACT_DETAILS;
import static com.openrsc.server.Constants.GameServer.LOGO_SPRITE_ID;
import static com.openrsc.server.Constants.GameServer.MAX_WALKING_SPEED;
import static com.openrsc.server.Constants.GameServer.MEMBER_WORLD;
import static com.openrsc.server.Constants.GameServer.MENU_COMBAT_STYLE_TOGGLE;
import static com.openrsc.server.Constants.GameServer.PLAYER_COMMANDS;
import static com.openrsc.server.Constants.GameServer.PLAYER_LEVEL_LIMIT;
import static com.openrsc.server.Constants.GameServer.PROPER_MAGIC_TREE_NAME;
import static com.openrsc.server.Constants.GameServer.RIGHT_CLICK_BANK;
import static com.openrsc.server.Constants.GameServer.SERVER_NAME;
import static com.openrsc.server.Constants.GameServer.SERVER_NAME_WELCOME;
import static com.openrsc.server.Constants.GameServer.SHOW_FLOATING_NAMETAGS;
import static com.openrsc.server.Constants.GameServer.SHOW_ROOF_TOGGLE;
import static com.openrsc.server.Constants.GameServer.SHOW_UNIDENTIFIED_HERB_NAMES;
import static com.openrsc.server.Constants.GameServer.SIDE_MENU_TOGGLE;
import static com.openrsc.server.Constants.GameServer.SPAWN_AUCTION_NPCS;
import static com.openrsc.server.Constants.GameServer.SPAWN_IRON_MAN_NPCS;
import static com.openrsc.server.Constants.GameServer.WANT_BANK_NOTES;
import static com.openrsc.server.Constants.GameServer.WANT_BANK_PINS;
import static com.openrsc.server.Constants.GameServer.WANT_CERTER_BANK_EXCHANGE;
import static com.openrsc.server.Constants.GameServer.WANT_CERT_DEPOSIT;
import static com.openrsc.server.Constants.GameServer.WANT_CLANS;
import static com.openrsc.server.Constants.GameServer.WANT_CUSTOM_BANKS;
import static com.openrsc.server.Constants.GameServer.WANT_CUSTOM_RANK_DISPLAY;
import static com.openrsc.server.Constants.GameServer.WANT_CUSTOM_SPRITES;
import static com.openrsc.server.Constants.GameServer.WANT_DECANTING;
import static com.openrsc.server.Constants.GameServer.WANT_DROP_X;
import static com.openrsc.server.Constants.GameServer.WANT_EMAIL;
import static com.openrsc.server.Constants.GameServer.WANT_EXPERIENCE_ELIXIRS;
import static com.openrsc.server.Constants.GameServer.WANT_EXP_INFO;
import static com.openrsc.server.Constants.GameServer.WANT_FATIGUE;
import static com.openrsc.server.Constants.GameServer.WANT_GLOBAL_CHAT;
import static com.openrsc.server.Constants.GameServer.WANT_HIDE_IP;
import static com.openrsc.server.Constants.GameServer.WANT_KEYBOARD_SHORTCUTS;
import static com.openrsc.server.Constants.GameServer.WANT_KILL_FEED;
import static com.openrsc.server.Constants.GameServer.WANT_PETS;
import static com.openrsc.server.Constants.GameServer.WANT_QUEST_MENUS;
import static com.openrsc.server.Constants.GameServer.WANT_QUEST_STARTED_INDICATOR;
import static com.openrsc.server.Constants.GameServer.WANT_REGISTRATION_LIMIT;
import static com.openrsc.server.Constants.GameServer.WANT_REMEMBER;
import static com.openrsc.server.Constants.GameServer.WANT_SKILL_MENUS;
import static com.openrsc.server.Constants.GameServer.WANT_WOODCUTTING_GUILD;
import static com.openrsc.server.Constants.GameServer.WELCOME_TEXT;
import static com.openrsc.server.Constants.GameServer.ZOOM_VIEW_TOGGLE;


/**
 * @author n0m
 */
public class ActionSender {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Hides the bank windows
	 */
	public static void hideBank(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_BANK_CLOSE.opcode);
		player.write(s.toPacket());
	}

	/**
	 * Hides a question menu
	 */
	public static void hideMenu(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_OPTIONS_MENU_CLOSE.opcode);
		player.write(s.toPacket());
	}

	/**
	 * Hides the shop window
	 */
	public static void hideShop(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_SHOP_CLOSE.opcode);
		player.write(s.toPacket());
	}

	/**
	 * Sends a message box
	 */
	public static void sendBox(Player player, String message, boolean big) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(big ? Opcode.SEND_BOX.opcode : Opcode.SEND_BOX2.opcode);
		s.writeString(message);
		player.write(s.toPacket());
	}

	/**
	 * Inform client to start displaying the appearance changing screen.
	 *
	 * @param player
	 */
	public static void sendAppearanceScreen(Player player) {
		player.setChangingAppearance(true);
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_APPEARANCE_CHANGE.opcode);
		player.write(s.toPacket());
	}

	public static void sendRecoveryScreen(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_OPEN_RECOVERY.opcode);
		player.write(s.toPacket());
	}

	public static void sendDetailsScreen(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_OPEN_DETAILS.opcode);
		player.write(s.toPacket());
	}

	public static void sendPlayerOnTutorial(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_ON_TUTORIAL.opcode);
		s.writeByte((byte) (player.getLocation().onTutorialIsland() ? 1 : 0));
		player.write(s.toPacket());
	}

	public static void sendPlayerOnBlackHole(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_ON_BLACK_HOLE.opcode);
		s.writeByte((byte) (player.getLocation().onBlackHole() ? 1 : 0));
		player.write(s.toPacket());
	}

	/**
	 * Inform client of log-out request denial.
	 */
	public static void sendCantLogout(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_CANT_LOGOUT.opcode);
		player.write(s.toPacket());
	}

	/**
	 * Inform client of combat style
	 *
	 * @param player
	 */
	private static void sendCombatStyle(Player player) {
		// com.rscr.server.net.PacketBuilder s = new
		// com.rscr.server.net.PacketBuilder();
		// s.setID(129);
		// s.writeByte((byte) player.getCombatStyle());
		// player.write(s.toPacket());
	}

	/**
	 * Inform client to display the 'Oh dear...you are dead' screen.
	 *
	 * @param player
	 */
	public static void sendDied(Player player) {
		hideBank(player);
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_DEATH.opcode);
		player.write(s.toPacket());
	}

	/**
	 * Inform client of everything on the duel screen
	 *
	 * @param player
	 */
	public static void sendDuelConfirmScreen(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_DUEL_CONFIRMWINDOW.opcode);
		s.writeString(with.getUsername());
		s.writeByte((byte) with.getDuel().getDuelOffer().getItems().size());
		for (Item item : with.getDuel().getDuelOffer().getItems()) {
			s.writeShort(item.getID());
			s.writeInt(item.getAmount());
		}
		s.writeByte((byte) player.getDuel().getDuelOffer().getItems().size());
		for (Item item : player.getDuel().getDuelOffer().getItems()) {
			s.writeShort(item.getID());
			s.writeInt(item.getAmount());
		}

		s.writeByte((byte) (player.getDuel().getDuelSetting(0) ? 1 : 0));
		s.writeByte((byte) (player.getDuel().getDuelSetting(1) ? 1 : 0));
		s.writeByte((byte) (player.getDuel().getDuelSetting(2) ? 1 : 0));
		s.writeByte((byte) (player.getDuel().getDuelSetting(3) ? 1 : 0));

		player.write(s.toPacket());
	}

	/**
	 * Inform client of duel accept
	 *
	 * @param player
	 */

	public static void sendOwnDuelAcceptUpdate(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		com.openrsc.server.net.PacketBuilder s1 = new com.openrsc.server.net.PacketBuilder();
		s1.setID(Opcode.SEND_DUEL_ACCEPTED.opcode);
		s1.writeByte((byte) (player.getDuel().isDuelAccepted() ? 1 : 0));
		player.write(s1.toPacket());
	}

	public static void sendOpponentDuelAcceptUpdate(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		com.openrsc.server.net.PacketBuilder s1 = new com.openrsc.server.net.PacketBuilder();
		s1.setID(Opcode.SEND_DUEL_OTHER_ACCEPTED.opcode);
		s1.writeByte((byte) (with.getDuel().isDuelAccepted() ? 1 : 0));
		player.write(s1.toPacket());
	}

	/**
	 * Inform client of the offer changes on duel window.
	 *
	 * @param player
	 */
	public static void sendDuelOpponentItems(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) {
			return;
		}
		ArrayList<Item> items = with.getDuel().getDuelOffer().getItems();
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_DUEL_OPPONENTS_ITEMS.opcode);
		s.writeByte((byte) items.size());
		for (Item item : items) {
			s.writeShort(item.getID());
			s.writeInt(item.getAmount());
		}
		player.write(s.toPacket());
	}

	/**
	 * Inform client to update the duel settings on duel window.
	 *
	 * @param player
	 */
	public static void sendDuelSettingUpdate(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_DUEL_SETTINGS.opcode);
		s.writeByte((byte) (player.getDuel().getDuelSetting(0) ? 1 : 0));
		s.writeByte((byte) (player.getDuel().getDuelSetting(1) ? 1 : 0));
		s.writeByte((byte) (player.getDuel().getDuelSetting(2) ? 1 : 0));
		s.writeByte((byte) (player.getDuel().getDuelSetting(3) ? 1 : 0));
		player.write(s.toPacket());
	}

	/**
	 * Inform client to close the duel window
	 *
	 * @param player
	 */
	public static void sendDuelWindowClose(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_DUEL_CLOSE.opcode);
		player.write(s.toPacket());
	}

	/**
	 * Inform client to open duel window
	 *
	 * @param player
	 */
	public static void sendDuelWindowOpen(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_DUEL_WINDOW.opcode);
		s.writeShort(with.getIndex());
		player.write(s.toPacket());
	}

	/**
	 * Inform client to start drawing sleep screen and the captcha.
	 *
	 * @param player
	 */
	public static void sendEnterSleep(Player player) {
		player.setSleeping(true);
		byte[] image = CaptchaGenerator.generateCaptcha(player);
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_SLEEPSCREEN.opcode);
		s.writeBytes(image);
		player.write(s.toPacket());
	}

	/**
	 * Updates the equipment status
	 */
	public static void sendEquipmentStats(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_EQUIPMENT_STATS.opcode);
		s.writeByte(player.getArmourPoints());
		s.writeByte(player.getWeaponAimPoints());
		s.writeByte(player.getWeaponPowerPoints());
		s.writeByte(player.getMagicPoints());
		s.writeByte(player.getPrayerPoints());
		player.write(s.toPacket());
	}

	/**
	 * Sends fatigue
	 *
	 * @param player
	 */
	public static void sendFatigue(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_FATIGUE.opcode);
		s.writeShort(player.getFatigue() / 750);
		player.write(s.toPacket());
	}
	public static void sendPet0Fatigue(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PET0_FATIGUE.opcode);
		s.writeShort(player.getPet0Fatigue() / 750);
		player.write(s.toPacket());
	}
	public static void sendPet1Fatigue(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PET1_FATIGUE.opcode);
		s.writeShort(player.getPet1Fatigue() / 750);
		player.write(s.toPacket());
	}
	public static void sendPet2Fatigue(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PET2_FATIGUE.opcode);
		s.writeShort(player.getPet2Fatigue() / 750);
		player.write(s.toPacket());
	}
	public static void sendPet3Fatigue(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PET3_FATIGUE.opcode);
		s.writeShort(player.getPet3Fatigue() / 750);
		player.write(s.toPacket());
	}
	public static void sendPet4Fatigue(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PET4_FATIGUE.opcode);
		s.writeShort(player.getPet4Fatigue() / 750);
		player.write(s.toPacket());
	}
	public static void sendPetOut(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PETOUT.opcode);
		s.writeShort(player.getPetOut());
		player.write(s.toPacket());
	}
	public static void sendPetInCombat(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PETINCOMBAT.opcode);
		s.writeShort(player.getPetInCombat());
		player.write(s.toPacket());
	}
	public static void sendBeingHealed(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_BEINGHEALED.opcode);
		s.writeShort(player.getBeingHealed());
		player.write(s.toPacket());
	}
	public static void sendKills2(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_KILLS2.opcode);
		s.writeShort(player.getKills2());
		player.write(s.toPacket());
	}

	/**
	 * Sends the sleeping state fatigue
	 *
	 * @param player
	 * @param fatigue
	 */
	public static void sendSleepFatigue(Player player, int fatigue) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_SLEEP_FATIGUE.opcode);
		s.writeShort(fatigue / 750);
		player.write(s.toPacket());
	}

	/**
	 * Sends friend list
	 *
	 * @param player
	 */
	public static void sendFriendList(Player player) {
		Server.getServer().getEventHandler().add(new DelayedEvent(player, 50) {
			int currentFriend = 0;

			@Override
			public void run() {
				if (currentFriend == player.getSocial().getFriendListEntry().size()) {
					stop();
					return;
				}
				int iteratorindex = 0;
				for (Entry<Long, Integer> entry : player.getSocial().getFriendListEntry()) {
					if (iteratorindex == currentFriend) {
						sendFriendUpdate(player, entry.getKey());
						break;
					}
					iteratorindex++;
				}
				currentFriend++;
			}
		});
	}

	/**
	 * Updates a friends login status
	 */
	public static void sendFriendUpdate(Player player, long usernameHash) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		int arg = 0;

		if (
			World.getWorld().getPlayer(usernameHash) != null &&
				World.getWorld().getPlayer(usernameHash).isLoggedIn() &&
				(!World.getWorld().getPlayer(usernameHash).getSettings().getPrivacySetting(1) ||
					World.getWorld().getPlayer(usernameHash).getSocial().isFriendsWith(player.getUsernameHash()) ||
					player.isMod()
				)
		) {
			arg |= 4 | 2; // 4 for is online and 2 for on same world. 1 would be if the User's name changed from original
		}

		s.setID(Opcode.SEND_FRIEND_UPDATE.opcode);

		String username = DataConversions.hashToUsername(usernameHash);
		s.writeString(username);

		// if(usernameChanged)
		//s.writeString(username);
		s.writeByte(10); // Is this meant to be a null string?

		s.writeByte(arg);

		if ((arg & 4) != 0)
			s.writeString("OpenRSC");

		player.write(s.toPacket());
	}

	/**
	 * Updates game settings, ie sound effects etc
	 */
	public static void sendGameSettings(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_GAME_SETTINGS.opcode);
		s.writeByte((byte) (player.getSettings().getGameSetting(0) ? 1 : 0)); // Camera Auto Angle 0
		s.writeByte((byte) (player.getSettings().getGameSetting(1) ? 1 : 0)); // Mouse buttons 1
		s.writeByte((byte) (player.getSettings().getGameSetting(2) ? 1 : 0)); // Sound Effects 2
		s.writeByte((byte) player.getCombatStyle());
		s.writeByte(player.getGlobalBlock()); // 9
		s.writeByte((byte) (player.getClanInviteSetting() ? 0 : 1)); // 11
		s.writeByte((byte) (player.getVolumeToRotate() ? 1 : 0)); // 16
		s.writeByte((byte) (player.getSwipeToRotate() ? 1 : 0)); // 17
		s.writeByte((byte) (player.getSwipeToScroll() ? 1 : 0)); // 18
		s.writeByte(player.getLongPressDelay()); // 19
		s.writeByte(player.getFontSize()); // 20
		s.writeByte((byte) (player.getHoldAndChoose() ? 1 : 0)); // 21
		s.writeByte((byte) (player.getSwipeToZoom() ? 1 : 0)); // 22
		s.writeByte(player.getLastZoom()); // 23
		s.writeByte((byte) (player.getBatchProgressBar() ? 1 : 0)); // 24
		s.writeByte((byte) (player.getExperienceDrops() ? 1 : 0)); // 25
		s.writeByte((byte) (player.getHideRoofs() ? 1 : 0)); // 26
		s.writeByte((byte) (player.getHideFog() ? 1 : 0)); // 27
		s.writeByte(player.getGroundItemsToggle()); // 28
		s.writeByte((byte) (player.getAutoMessageSwitch() ? 1 : 0)); // 29
		s.writeByte((byte) (player.getHideSideMenu() ? 1 : 0)); // 30
		s.writeByte((byte) (player.getHideKillFeed() ? 1 : 0)); // 31
		s.writeByte(player.getFightModeSelectorToggle()); // 32
		s.writeByte(player.getExperienceCounterToggle()); // 33
		s.writeByte((byte) (player.getHideInventoryCount() ? 1 : 0)); // 34
		s.writeByte((byte) (player.getHideNameTag() ? 1 : 0)); // 35
		player.write(s.toPacket());
	}

	public static void sendInitialServerConfigs(Channel channel) {
		LOGGER.info("Sending initial configs to: " + channel.remoteAddress());
		if (DEBUG) {
			LOGGER.info("Debug server configs being sent:");
			LOGGER.info(SERVER_NAME + " 1");
			LOGGER.info(SERVER_NAME_WELCOME + " 2");
			LOGGER.info(PLAYER_LEVEL_LIMIT + " 3");
			LOGGER.info(SPAWN_AUCTION_NPCS + " 4");
			LOGGER.info(SPAWN_IRON_MAN_NPCS + " 5");
			LOGGER.info(SHOW_FLOATING_NAMETAGS + " 6");
			LOGGER.info(WANT_CLANS + " 7");
			LOGGER.info(WANT_KILL_FEED + " 8");
			LOGGER.info(FOG_TOGGLE + " 9");
			LOGGER.info(GROUND_ITEM_TOGGLE + " 10");
			LOGGER.info(AUTO_MESSAGE_SWITCH_TOGGLE + " 11");
			LOGGER.info(BATCH_PROGRESSION + " 12");
			LOGGER.info(SIDE_MENU_TOGGLE + " 13");
			LOGGER.info(INVENTORY_COUNT_TOGGLE + " 14");
			LOGGER.info(ZOOM_VIEW_TOGGLE + " 15");
			LOGGER.info(MENU_COMBAT_STYLE_TOGGLE + " 16");
			LOGGER.info(FIGHTMODE_SELECTOR_TOGGLE + " 17");
			LOGGER.info(EXPERIENCE_COUNTER_TOGGLE + " 18");
			LOGGER.info(EXPERIENCE_DROPS_TOGGLE + " 19");
			LOGGER.info(ITEMS_ON_DEATH_MENU + " 20");
			LOGGER.info(SHOW_ROOF_TOGGLE + " 21");
			LOGGER.info(WANT_HIDE_IP + " 22");
			LOGGER.info(WANT_REMEMBER + " 23");
			LOGGER.info(WANT_GLOBAL_CHAT + " 24");
			LOGGER.info(WANT_SKILL_MENUS + " 25");
			LOGGER.info(WANT_QUEST_MENUS + " 26");
			LOGGER.info(WANT_EXPERIENCE_ELIXIRS + " 27");
			LOGGER.info(WANT_KEYBOARD_SHORTCUTS + " 28");
			LOGGER.info(WANT_CUSTOM_BANKS + " 29");
			LOGGER.info(WANT_BANK_PINS + " 30");
			LOGGER.info(WANT_BANK_NOTES + " 31");
			LOGGER.info(WANT_CERT_DEPOSIT + " 32");
			LOGGER.info(CUSTOM_FIREMAKING + " 33");
			LOGGER.info(WANT_DROP_X + " 34");
			LOGGER.info(WANT_EXP_INFO + " 35");
			LOGGER.info(WANT_WOODCUTTING_GUILD + " 36");
			LOGGER.info(WANT_DECANTING + " 37");
			LOGGER.info(WANT_CERTER_BANK_EXCHANGE + " 38");
			LOGGER.info(WANT_CUSTOM_RANK_DISPLAY + " 39");
			LOGGER.info(RIGHT_CLICK_BANK + " 40");
			LOGGER.info(FIX_OVERHEAD_CHAT + " 41");
			LOGGER.info(WELCOME_TEXT + " 42");
			LOGGER.info(MEMBER_WORLD + " 43");
			LOGGER.info(DISPLAY_LOGO_SPRITE + " 44");
			LOGGER.info(LOGO_SPRITE_ID + " 45");
			LOGGER.info(FPS + " 46");
			LOGGER.info(WANT_EMAIL + " 47");
			LOGGER.info(WANT_REGISTRATION_LIMIT + " 48");
			LOGGER.info(ALLOW_RESIZE + " 49");
			LOGGER.info(LENIENT_CONTACT_DETAILS + " 50");
			LOGGER.info(WANT_FATIGUE + " 51");
			LOGGER.info(WANT_CUSTOM_SPRITES + " 52");
			LOGGER.info(PLAYER_COMMANDS + " 53");
			LOGGER.info(WANT_PETS + " 54");
			LOGGER.info(MAX_WALKING_SPEED + " 55");
			LOGGER.info(SHOW_UNIDENTIFIED_HERB_NAMES + " 56");
			LOGGER.info(WANT_QUEST_STARTED_INDICATOR + " 57");
			LOGGER.info(FISHING_SPOTS_DEPLETABLE + " 58");
			LOGGER.info(PROPER_MAGIC_TREE_NAME + " 59");
		}
		com.openrsc.server.net.PacketBuilder s = prepareServerConfigs();
		ConnectionAttachment attachment = new ConnectionAttachment();
		channel.attr(RSCConnectionHandler.attachment).set(attachment);
		channel.writeAndFlush(s.toPacket());
		channel.close();
	}

	static void sendServerConfigs(Player player) {
		com.openrsc.server.net.PacketBuilder s = prepareServerConfigs();
		player.write(s.toPacket());
	}

	private static com.openrsc.server.net.PacketBuilder prepareServerConfigs() {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_SERVER_CONFIGS.opcode);
		s.writeString(SERVER_NAME); // 1
		s.writeString(SERVER_NAME_WELCOME); // 2
		s.writeByte((byte) PLAYER_LEVEL_LIMIT); // 3
		s.writeByte((byte) (SPAWN_AUCTION_NPCS ? 1 : 0)); // 4
		s.writeByte((byte) (SPAWN_IRON_MAN_NPCS ? 1 : 0)); // 5
		s.writeByte((byte) (SHOW_FLOATING_NAMETAGS ? 1 : 0)); // 6
		s.writeByte((byte) (WANT_CLANS ? 1 : 0)); // 7
		s.writeByte((byte) (WANT_KILL_FEED ? 1 : 0)); // 8
		s.writeByte((byte) (FOG_TOGGLE ? 1 : 0)); // 9
		s.writeByte((byte) (GROUND_ITEM_TOGGLE ? 1 : 0)); // 10
		s.writeByte((byte) (AUTO_MESSAGE_SWITCH_TOGGLE ? 1 : 0)); // 11
		s.writeByte((byte) (BATCH_PROGRESSION ? 1 : 0)); // 12
		s.writeByte((byte) (SIDE_MENU_TOGGLE ? 1 : 0)); // 13
		s.writeByte((byte) (INVENTORY_COUNT_TOGGLE ? 1 : 0)); // 14
		s.writeByte((byte) (ZOOM_VIEW_TOGGLE ? 1 : 0)); // 15
		s.writeByte((byte) (MENU_COMBAT_STYLE_TOGGLE ? 1 : 0)); // 16
		s.writeByte((byte) (FIGHTMODE_SELECTOR_TOGGLE ? 1 : 0)); // 17
		s.writeByte((byte) (EXPERIENCE_COUNTER_TOGGLE ? 1 : 0)); // 18
		s.writeByte((byte) (EXPERIENCE_DROPS_TOGGLE ? 1 : 0)); // 19
		s.writeByte((byte) (ITEMS_ON_DEATH_MENU ? 1 : 0)); // 20
		s.writeByte((byte) (SHOW_ROOF_TOGGLE ? 1 : 0)); // 21
		s.writeByte((byte) (WANT_HIDE_IP ? 1 : 0)); // 22
		s.writeByte((byte) (WANT_REMEMBER ? 1 : 0)); // 23
		s.writeByte((byte) (WANT_GLOBAL_CHAT ? 1 : 0)); // 24
		s.writeByte((byte) (WANT_SKILL_MENUS ? 1 : 0)); // 25
		s.writeByte((byte) (WANT_QUEST_MENUS ? 1 : 0)); // 26
		s.writeByte((byte) (WANT_EXPERIENCE_ELIXIRS ? 1 : 0)); // 27
		s.writeByte((byte) (WANT_KEYBOARD_SHORTCUTS ? 1 : 0)); // 28
		s.writeByte((byte) (WANT_CUSTOM_BANKS ? 1 : 0)); // 29
		s.writeByte((byte) (WANT_BANK_PINS ? 1 : 0)); // 30
		s.writeByte((byte) (WANT_BANK_NOTES ? 1 : 0)); // 31
		s.writeByte((byte) (WANT_CERT_DEPOSIT ? 1 : 0)); // 32
		s.writeByte((byte) (CUSTOM_FIREMAKING ? 1 : 0)); // 33
		s.writeByte((byte) (WANT_DROP_X ? 1 : 0)); // 34
		s.writeByte((byte) (WANT_EXP_INFO ? 1 : 0)); // 35
		s.writeByte((byte) (WANT_WOODCUTTING_GUILD ? 1 : 0)); // 36
		s.writeByte((byte) (WANT_DECANTING ? 1 : 0)); // 37
		s.writeByte((byte) (WANT_CERTER_BANK_EXCHANGE ? 1 : 0)); // 38
		s.writeByte((byte) (WANT_CUSTOM_RANK_DISPLAY ? 1 : 0)); // 39
		s.writeByte((byte) (RIGHT_CLICK_BANK ? 1 : 0)); // 40
		s.writeByte((byte) (FIX_OVERHEAD_CHAT ? 1 : 0)); // 41
		s.writeString(WELCOME_TEXT); // 42
		s.writeByte((byte) (MEMBER_WORLD ? 1 : 0)); // 43
		s.writeByte((byte) (DISPLAY_LOGO_SPRITE ? 1 : 0)); // 44
		s.writeString(LOGO_SPRITE_ID); // 45
		s.writeByte((byte) FPS); // 46
		s.writeByte((byte) (WANT_EMAIL ? 1 : 0)); // 47
		s.writeByte((byte) (WANT_REGISTRATION_LIMIT ? 1 : 0)); // 48
		s.writeByte((byte) (ALLOW_RESIZE ? 1 : 0)); // 49
		s.writeByte((byte) (LENIENT_CONTACT_DETAILS ? 1 : 0)); // 50
		s.writeByte((byte) (WANT_FATIGUE ? 1 : 0)); // 51
		s.writeByte((byte) (WANT_CUSTOM_SPRITES ? 1 : 0)); // 52
		s.writeByte((byte) (PLAYER_COMMANDS ? 1 : 0)); // 53
		s.writeByte((byte) (WANT_PETS ? 1 : 0)); // 54
		s.writeByte((byte) MAX_WALKING_SPEED); // 55
		s.writeByte((byte) (SHOW_UNIDENTIFIED_HERB_NAMES ? 1 : 0)); // 56
		s.writeByte((byte) (WANT_QUEST_STARTED_INDICATOR ? 1 : 0)); // 57
		s.writeByte((byte) (FISHING_SPOTS_DEPLETABLE ? 1 : 0)); // 58
		s.writeByte((byte) (PROPER_MAGIC_TREE_NAME ? 1 : 0)); // 59
		return s;
	}

	/**
	 * Sends the whole ignore list
	 */
	public static void sendIgnoreList(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_IGNORE_LIST.opcode);
		s.writeByte((byte) player.getSocial().getIgnoreList().size());
		for (long usernameHash : player.getSocial().getIgnoreList()) {
			String username = DataConversions.hashToUsername(usernameHash);
			s.writeString(username);
			s.writeString(username);
			s.writeString(username);
			s.writeString(username);
		}
		player.write(s.toPacket());
	}

	/**
	 * Incorrect sleep word!
	 */
	public static void sendIncorrectSleepword(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_SLEEPWORD_INCORRECT.opcode);
		player.write(s.toPacket());
	}

	/**
	 * @param player
	 * sends the player inventory
	 */
	public static void sendInventory(Player player) {
		if (player == null)
			return; /* In this case, it is a trade offer */
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_INVENTORY.opcode);
		s.writeByte((byte) player.getInventory().size());
		for (Item item : player.getInventory().getItems()) {
			s.writeShort(item.getID());
			s.writeByte((byte) (item.isWielded() ? 1 : 0));
			if (item.getDef().isStackable())
				s.writeInt(item.getAmount());
		}
		player.write(s.toPacket());
	}

	/**
	 * Displays the login box and last IP and login date
	 */
	private static void sendLoginBox(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_WELCOME_INFO.opcode);
		s.writeString(player.getLastIP());
		s.writeShort(player.getDaysSinceLastLogin());
		if (player.getDaysSinceLastRecoveryChangeRequest() < 14) {
			s.writeShort(14 - player.getDaysSinceLastRecoveryChangeRequest());
		} else {
			s.writeShort(0);
		}
		//s.writeShort(player.getUnreadMessages());
		player.write(s.toPacket());
	}

	/**
	 * Confirm logout allowed
	 */
	public static void sendLogout(final Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_LOGOUT.opcode);
		player.write(s.toPacket());
	}

	public static void sendLogoutRequestConfirm(final Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_LOGOUT_REQUEST_CONFIRM.opcode);
		player.getChannel().writeAndFlush(s.toPacket()).addListener((ChannelFutureListener) arg0 -> arg0.channel().close());
	}

	/**
	 * Sends quest names and stages
	 */
	private static void sendQuestInfo(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		List<QuestInterface> quests = World.getWorld().getQuests();
		s.setID(Opcode.SEND_QUESTS.opcode);
		s.writeByte((byte) 0);
		s.writeByte((byte) quests.size());
		for (QuestInterface q : quests) {
			s.writeInt(q.getQuestId());
			s.writeInt(player.getQuestStage(q));
			s.writeString(q.getQuestName());
		}
		player.write(s.toPacket());
	}

	/**
	 * Sends quest stage
	 */
	public static void sendQuestInfo(Player player, int questID, int stage) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_QUESTS.opcode);
		s.writeByte((byte) 1);
		s.writeInt(questID);
		s.writeInt(stage);
		player.write(s.toPacket());
	}

	/**
	 * Shows a question menu
	 */
	public static void sendMenu(Player player, String[] options) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_OPTIONS_MENU_OPEN.opcode);
		s.writeByte((byte) options.length);
		for (String option : options) {
			s.writeString(option);
		}
		player.write(s.toPacket());
	}

	public static void sendMessage(Player player, String message) {
		sendMessage(player, null, 0, MessageType.GAME, message, 0);
	}

	public static void sendPlayerServerMessage(Player player, MessageType type, String message) {
		sendMessage(player, null, 0, type, message, 0);
	}

	public static void sendMessage(Player player, Player sender, int prefix, MessageType type, String message,
								   int iconSprite) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_SERVER_MESSAGE.opcode);
		s.writeInt(iconSprite);
		s.writeByte(type.getRsID());
		/*
		  This is actually a controller which check if we should present
		  (SENDER USERNAME, CLAN TAG OR COLOR) 0 = nothing, 1 = SENDER & CLAN,
		  2 = COLOR
		 */
		s.writeByte(prefix);// Used for clan/color/sender.
		s.writeString(message);
		if ((prefix & 1) != 0) {
			s.writeString(sender.getUsername());
			s.writeString(sender.getUsername());
		}
		if ((prefix & 2) != 0) {
			s.writeString((String) null); // Interpreted as colour by the client.
		}
		player.write(s.toPacket());
	}

	public static void sendPrayers(Player player, boolean[] activatedPrayers) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PRAYERS_ACTIVE.opcode);
		for (boolean prayerActive : activatedPrayers) {
			s.writeByte((byte) (prayerActive ? 1 : 0));
		}
		player.write(s.toPacket());
	}
	public static void sendPets(Player player, boolean[] activatedPets) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PETS_ACTIVE.opcode);
		for (boolean petActive : activatedPets) {
			s.writeByte((byte) (petActive ? 1 : 0));
		}
		player.write(s.toPacket());
	}

	private static void sendPrivacySettings(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PRIVACY_SETTINGS.opcode);
		s.writeByte(
			(byte) (player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES) ? 1 : 0));
		s.writeByte(
			(byte) (player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES) ? 1 : 0));
		s.writeByte(
			(byte) (player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS) ? 1 : 0));
		s.writeByte(
			(byte) (player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS) ? 1 : 0));
		player.write(s.toPacket());
	}

	/**
	 * Send a private message
	 */
	public static void sendPrivateMessageReceived(Player player, Player sender, String message) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PRIVATE_MESSAGE.opcode);
		s.writeString(sender.getUsername());
		s.writeString(sender.getUsername());// former name
		s.writeInt(sender.getIcon());
		s.writeRSCString(message);
		player.write(s.toPacket());
	}

	public static void sendPrivateMessageSent(Player player, long usernameHash, String message) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PRIVATE_MESSAGE_SENT.opcode);
		s.writeString(DataConversions.hashToUsername(usernameHash));
		s.writeRSCString(message);
		player.write(s.toPacket());
	}

	public static void sendRemoveItem(Player player, int slot) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_INVENTORY_REMOVE_ITEM.opcode);
		s.writeByte((byte) slot);
		player.write(s.toPacket());
	}

	/**
	 * Sends a sound effect
	 */
	public static void sendSound(Player player, String soundName) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PLAY_SOUND.opcode);
		s.writeString(soundName);
		player.write(s.toPacket());
	}

	/**
	 * Updates just one stat
	 */
	public static void sendStat(Player player, int stat) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_STAT.opcode);
		s.writeByte((byte) stat);
		s.writeByte((byte) player.getSkills().getLevel(stat));
		s.writeByte((byte) player.getSkills().getMaxStat(stat));
		s.writeInt(player.getSkills().getExperience(stat));

		player.write(s.toPacket());
	}

	public static void sendExperience(Player p, int stat) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_EXPERIENCE.opcode);
		s.writeByte((byte) stat);
		s.writeInt(p.getSkills().getExperience(stat));
		p.write(s.toPacket());
	}

	/**
	 * Updates the users stats
	 */
	public static void sendStats(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_STATS.opcode);
		for (int lvl : player.getSkills().getLevels())
			s.writeByte((byte) lvl);
		for (int lvl : player.getSkills().getMaxStats())
			s.writeByte((byte) lvl);
		for (int exp : player.getSkills().getExperiences())
			s.writeInt(exp);

		s.writeByte(player.getQuestPoints());
		player.write(s.toPacket());
	}

	public static void sendTeleBubble(Player player, int x, int y, boolean grab) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_BUBBLE.opcode);
		s.writeByte((byte) (grab ? 1 : 0));
		s.writeByte((byte) (x - player.getX()));
		s.writeByte((byte) (y - player.getY()));
		player.write(s.toPacket());
	}

	public static void sendSecondTradeScreen(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_TRADE_OPEN_CONFIRM.opcode);
		s.writeString(with.getUsername());
		s.writeByte((byte) with.getTrade().getTradeOffer().getItems().size());
		for (Item item : with.getTrade().getTradeOffer().getItems()) {
			s.writeShort(item.getID());
			s.writeInt(item.getAmount());
		}
		s.writeByte((byte) player.getTrade().getTradeOffer().getItems().size());
		for (Item item : player.getTrade().getTradeOffer().getItems()) {
			s.writeShort(item.getID());
			s.writeInt(item.getAmount());
		}
		player.write(s.toPacket());
	}

	public static void sendTradeAcceptUpdate(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		PacketBuilder p = new PacketBuilder();
		p.setID(Opcode.SEND_TRADE_OTHER_ACCEPTED.opcode);
		p.writeByte((byte) (with.getTrade().isTradeAccepted() ? 1 : 0));
		player.write(p.toPacket());
	}

	public static void sendOwnTradeAcceptUpdate(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		PacketBuilder p = new PacketBuilder();
		p.setID(Opcode.SEND_TRADE_ACCPETED.opcode);
		p.writeByte((byte) (player.getTrade().isTradeAccepted() ? 1 : 0));
		player.write(p.toPacket());
	}

	public static void sendTradeItems(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		ArrayList<Item> items = with.getTrade().getTradeOffer().getItems();
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_TRADE_OTHER_ITEMS.opcode);

		// Other player's items first
		s.writeByte((byte) items.size());
		for (Item item : items) {
			s.writeShort(item.getID());
			s.writeInt(item.getAmount());
		}

		// Our items second
		items = player.getTrade().getTradeOffer().getItems();
		s.writeByte((byte) items.size());
		for (Item item : items) {
			s.writeShort(item.getID());
			s.writeInt(item.getAmount());
		}

		player.write(s.toPacket());
	}

	public static void sendTradeWindowClose(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_TRADE_CLOSE.opcode);
		player.write(s.toPacket());
	}

	public static void sendTradeWindowOpen(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_TRADE_WINDOW.opcode);
		s.writeShort(with.getIndex());
		player.write(s.toPacket());
	}

	public static void sendInventoryUpdateItem(Player player, int slot) {
		Item item = player.getInventory().get(slot);
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_INVENTORY_UPDATEITEM.opcode);
		s.writeByte((byte) slot);
		s.writeShort(item.getID() + (item.isWielded() ? 32768 : 0));

		if (item.getDef().isStackable()) {
			s.writeInt(item.getAmount());
		}
		player.write(s.toPacket());
	}

	public static void sendWakeUp(Player player, boolean success, boolean silent) {
		if (!silent) {
			if (success) {
				player.handleWakeup();
				sendMessage(player, "You wake up - feeling refreshed");
			} else {
				sendMessage(player, "You are unexpectedly awoken! You still feel tired");
			}
		}
		player.setSleeping(false);
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_STOPSLEEP.opcode);
		player.write(s.toPacket());
	}

	/**
	 * Sent when the user changes coords incase they moved up/down a level
	 */
	public static void sendWorldInfo(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_WORLD_INFO.opcode);
		s.writeShort(player.getIndex());
		s.writeShort(2304);
		s.writeShort(1776);
		s.writeShort(Formulae.getHeight(player.getLocation()));
		s.writeShort(944);
		player.write(s.toPacket());
	}

	/**
	 * Show the bank window
	 */
	public static void showBank(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_BANK_OPEN.opcode);
		s.writeShort(player.getBank().size());
		s.writeShort(player.getBankSize());
		for (Item i : player.getBank().getItems()) {
			s.writeShort(i.getID());
			s.writeInt(i.getAmount());
		}
		player.write(s.toPacket());
	}

	public static void showShop(Player player, Shop shop) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		player.setAccessingShop(shop);
		s.setID(Opcode.SEND_SHOP_OPEN.opcode);

		s.writeByte((byte) shop.getShopSize());
		s.writeByte((byte) (shop.isGeneral() ? 1 : 0));
		s.writeByte((byte) shop.getSellModifier());
		s.writeByte((byte) shop.getBuyModifier());
		s.writeByte((byte) shop.getPriceModifier()); // price modifier?

		for (int i = 0; i < shop.getShopSize(); i++) {
			Item item = shop.getShopItem(i);
			s.writeShort(item.getID());
			s.writeShort(item.getAmount());
			s.writeShort(shop.getStock(item.getID()));
		}
		player.write(s.toPacket());
	}

	/**
	 * Sends a system update message
	 */
	public static void startShutdown(Player player, int seconds) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_SYSTEM_UPDATE.opcode);
		s.writeShort((int) (((double) seconds / 32D) * 50));
		player.write(s.toPacket());
	}

	/**
	 * Sends the elixir timer
	 */
	public static void sendElixirTimer(Player player, int seconds) {
		if (!WANT_EXPERIENCE_ELIXIRS) return;
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_ELIXIR.opcode);
		s.writeShort((int) (((double) seconds / 32D) * 50));
		player.write(s.toPacket());
	}

	/**
	 * Updates the id and amount of an item in the bank
	 */
	public static void updateBankItem(Player player, int slot, int newId, int amount) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_BANK_UPDATE.opcode);
		s.writeByte((byte) slot);
		s.writeShort(newId);
		s.writeInt(amount);
		player.write(s.toPacket());
	}

	public static void sendRemoveProgressBar(Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_REMOVE_PROGRESS_BAR.opcode);
		s.writeByte(2); // interface ID
		//s.writeByte((byte) 2);
		player.write(s.toPacket());
	}

	public static void sendProgressBar(Player player, int delay, int repeatFor) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_PROGRESS_BAR.opcode);
		s.writeByte(1); // interface ID then it writes a byte of 1 which it shouldnt...
		//s.writeByte((byte) 1);
		s.writeShort(delay);
		s.writeByte((byte) repeatFor);
		player.write(s.toPacket());
	}
	
	public static void sendUpdateProgressBar(Player player, int repeatFor) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_UPDATE_PROGRESS_BAR.opcode);
		s.writeByte(3); // interface ID
		//s.writeByte((byte) 3);
		s.writeByte((byte) repeatFor); //where is this called from
		player.write(s.toPacket());
	}

	public static void sendProgress(Player player, long repeated) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(Opcode.SEND_AUCTION_PROGRESS.opcode);
		s.writeByte(0); // interface ID
		s.writeByte((byte) 3);
		s.writeByte((byte) repeated);
		player.write(s.toPacket());
	}

	public static void sendBankPinInterface(Player player) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_BANK_PIN_INTERFACE.opcode);
		pb.writeByte(1); // interface ID
		pb.writeByte(0);
		player.write(pb.toPacket());
	}

	public static void sendCloseBankPinInterface(Player player) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_BANK_PIN_INTERFACE.opcode);
		pb.writeByte(1); // interface ID
		pb.writeByte(1);
		player.write(pb.toPacket());
	}

	public static void sendInputBox(Player player, String s) {
		com.openrsc.server.net.PacketBuilder pb = new com.openrsc.server.net.PacketBuilder();
		pb.setID(Opcode.SEND_INPUT_BOX.opcode);
		pb.writeString(s);
		player.write(pb.toPacket());
	}

	static void sendLogin(Player p) {
		try {
			if (World.getWorld().registerPlayer(p)) {

				sendWorldInfo(p);
				GameStateUpdater.sendUpdatePackets(p);

				int timeTillShutdown = Server.getServer().timeTillShutdown();
				if (timeTillShutdown > -1)
					startShutdown(p, (timeTillShutdown / 1000));

				int elixir = p.getElixir();
				if (elixir > -1)
					sendElixirTimer(p, p.getElixir());

				sendPlayerOnTutorial(p);
				sendPlayerOnBlackHole(p);
				if (p.getLastLogin() == 0L) {
					sendAppearanceScreen(p);
					for (Item i : Player.STARTER_ITEMS) {
						p.getInventory().add(i, false);
					}
					//Block PK chat by default.
					p.getCache().set("setting_block_global", 3);
				}

				sendWakeUp(p, false, true);
				sendLoginBox(p);
				sendMessage(p, null, 0, MessageType.QUEST, "Welcome to " + SERVER_NAME + "!", 0);
				if (p.isMuted()) {
					sendMessage(p, "You are muted for "
						+ (double) (System.currentTimeMillis() - p.getMuteExpires()) / 3600000D + " hours.");
				}

				if (p.getLocation().inTutorialLanding()) {
					sendBox(p, "@gre@Welcome to the " + SERVER_NAME + " tutorial.% %Most actions are performed with the mouse. To walk around left click on the ground where you want to walk. To interact with something, first move your mouse pointer over it. Then left click or right click to perform different actions% %Try left clicking on one of the guides to talk to her. She will tell you more about how to play", true);
				}

				sendGameSettings(p);
				sendPrivacySettings(p);

				sendStats(p);
				sendEquipmentStats(p);
				sendFatigue(p);
				sendPet2Fatigue(p);
				sendPet0Fatigue(p);
				sendPet1Fatigue(p);
				sendPet3Fatigue(p);
				sendPet4Fatigue(p);
				sendKills2(p);
				sendBeingHealed(p);

				sendCombatStyle(p);
				sendIronManMode(p);

				sendInventory(p);
				p.checkEquipment();

				/*if (!Constants.GameServer.MEMBER_WORLD) {
					p.unwieldMembersItems();
				}*/

				if (!p.getLocation().inWilderness()) {
					if (SPAWN_AUCTION_NPCS) {
						Market.getInstance().addCollectableItemsNotificationTask(p);
					}
				}

				sendQuestInfo(p);
				//AchievementSystem.achievementListGUI(p);
				sendFriendList(p);
				sendIgnoreList(p);
			} else {
				p.getChannel().close();
			}
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
	}

	public static void sendOnlineList(Player player) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_ONLINE_LIST.opcode);
		pb.writeShort(World.getWorld().getPlayers().size());
		for (Player p : World.getWorld().getPlayers()) {
			pb.writeString(p.getUsername());
			pb.writeInt(p.getIcon());
		}
		player.write(pb.toPacket());
	}

	public static void showFishingTrawlerInterface(Player p) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_FISHING_TRAWLER.opcode);
		pb.writeByte(6);
		pb.writeByte(0);
		p.write(pb.toPacket());
	}

	public static void hideFishingTrawlerInterface(Player p) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_FISHING_TRAWLER.opcode);
		pb.writeByte(6);
		pb.writeByte(2);
		p.write(pb.toPacket());
	}

	public static void updateFishingTrawler(Player p, int waterLevel, int minutesLeft, int fishCaught,
											boolean netBroken) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_FISHING_TRAWLER.opcode);
		pb.writeByte(6);
		pb.writeByte(1);
		pb.writeShort(waterLevel);
		pb.writeShort(fishCaught);
		pb.writeByte(minutesLeft);
		pb.writeByte(netBroken ? 1 : 0);
		p.write(pb.toPacket());
	}

	public static void sendKillUpdate(Player player, long killedHash, long killerHash, int type) {
		if (!WANT_KILL_FEED) return;
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_KILL_ANNOUNCEMENT.opcode);
		pb.writeString(DataConversions.hashToUsername(killedHash));
		pb.writeString(DataConversions.hashToUsername(killerHash));
		pb.writeInt(type);
		player.write(pb.toPacket());
	}

	public static void sendOpenAuctionHouse(final Player player) {
		Market.getInstance().addRequestOpenAuctionHouseTask(player);
	}

	public static void sendClan(Player p) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_CLAN.opcode);
		pb.writeByte(0);
		pb.writeString(p.getClan().getClanName());
		pb.writeString(p.getClan().getClanTag());
		pb.writeString(p.getClan().getLeader().getUsername());
		pb.writeByte(p.getClan().getLeader().getUsername().equalsIgnoreCase(p.getUsername()) ? 1 : 0);
		pb.writeByte(p.getClan().getPlayers().size());
		for (ClanPlayer m : p.getClan().getPlayers()) {
			pb.writeString(m.getUsername());
			pb.writeByte(m.getRank().getRankIndex());
			pb.writeByte(m.isOnline() ? 1 : 0);
		}
		p.write(pb.toPacket());
	}

	public static void sendClans(Player p) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_CLAN.opcode);
		pb.writeByte(4);
		pb.writeShort(ClanManager.clans.size());
		int rank = 1;
		ClanManager.clans.sort(ClanManager.CLAN_COMPERATOR);
		for (Clan c : ClanManager.clans) {
			pb.writeShort(c.getClanID());
			pb.writeString(c.getClanName());
			pb.writeString(c.getClanTag());
			pb.writeByte(c.getPlayers().size());
			pb.writeByte(c.getAllowSearchJoin());
			pb.writeInt(c.getClanPoints());
			pb.writeShort(rank++);
		}
		p.write(pb.toPacket());
	}

	public static void sendLeaveClan(Player playerReference) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_CLAN.opcode);
		pb.writeByte(1);
		playerReference.write(pb.toPacket());
	}

	public static void sendClanInvitationGUI(Player invited, String name, String username) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_CLAN.opcode);
		pb.writeByte(2);
		pb.writeString(username);
		pb.writeString(name);
		invited.write(pb.toPacket());
	}

	public static void sendClanSetting(Player p) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_CLAN.opcode);
		pb.writeByte(3);
		pb.writeByte(p.getClan().getKickSetting());
		pb.writeByte(p.getClan().getInviteSetting());
		pb.writeByte(p.getClan().getAllowSearchJoin());
		pb.writeByte(p.getClan().isAllowed(0, p) ? 1 : 0);
		pb.writeByte(p.getClan().isAllowed(1, p) ? 1 : 0);
		p.write(pb.toPacket());
	}

	public static void sendIronManMode(Player player) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_IRONMAN.opcode);
		pb.writeByte(2);
		pb.writeByte(0);
		pb.writeByte((byte) player.getIronMan());
		pb.writeByte((byte) player.getIronManRestriction());
		player.write(pb.toPacket());
	}

	public static void sendIronManInterface(Player player) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_IRONMAN.opcode);
		pb.writeByte(2);
		pb.writeByte(1);
		player.write(pb.toPacket());
	}

	public static void sendHideIronManInterface(Player player) {
		PacketBuilder pb = new PacketBuilder(Opcode.SEND_IRONMAN.opcode);
		pb.writeByte(2);
		pb.writeByte(2);
		player.write(pb.toPacket());
	}

	public enum Opcode {
		/**
		 * int slot = this.packetsIncoming.getUnsignedByte();
		 * --this.inventoryItemCount;
		 * <p>
		 * for (int index = slot; this.inventoryItemCount > index; ++index) {
		 * this.inventoryItemID[index] = this.inventoryItemID[index + 1];
		 * this.inventoryItemSize[index] = this.inventoryItemSize[index + 1];
		 * this.inventoryItemEquipped[index] = this.inventoryItemEquipped[index
		 * + 1]; }
		 */
		SEND_LOGOUT(4),
		SEND_QUESTS(5),
		SEND_DUEL_OPPONENTS_ITEMS(6),
		SEND_TRADE_ACCPETED(15),
		SEND_SERVER_CONFIGS(19),
		SEND_TRADE_OPEN_CONFIRM(20),
		SEND_WORLD_INFO(25),
		SEND_DUEL_SETTINGS(30),
		SEND_EXPERIENCE(33),
		SEND_BUBBLE(36),
		SEND_BANK_OPEN(42),
		SEND_SYSTEM_UPDATE(52),
		SEND_INVENTORY(53),
		SEND_ELIXIR(54),
		SEND_PETOUT(55),
		SEND_PETINCOMBAT(62),
		SEND_APPEARANCE_CHANGE(59),
		SEND_DEATH(83),
		SEND_STOPSLEEP(84),
		SEND_PRIVATE_MESSAGE_SENT(87),
		SEND_BOX2(89),
		SEND_INVENTORY_UPDATEITEM(90),
		SEND_TRADE_WINDOW(92),
		SEND_TRADE_OTHER_ITEMS(97),
		SEND_SHOP_OPEN(101),
		SEND_IGNORE_LIST(109),
		SEND_INPUT_BOX(110),
		SEND_ON_TUTORIAL(111),
		SEND_CLAN(112),
		SEND_IRONMAN(113),
		SEND_KILLS2(147),
		SEND_BEINGHEALED(85),
		SEND_FATIGUE(114),
		SEND_PET2_FATIGUE(140),
		SEND_PET1_FATIGUE(144),
		SEND_PET0_FATIGUE(138),
		SEND_PET3_FATIGUE(148),
		SEND_PET4_FATIGUE(150),
		SEND_ON_BLACK_HOLE(115),
		SEND_SLEEPSCREEN(117),
		SEND_KILL_ANNOUNCEMENT(118),
		SEND_PRIVATE_MESSAGE(120),
		SEND_INVENTORY_REMOVE_ITEM(123),
		SEND_DUEL_CANCEL_ACCEPTED(128),
		SEND_TRADE_CLOSE(128),
		SEND_SERVER_MESSAGE(131),
		SEND_AUCTION_PROGRESS(132),
		SEND_FISHING_TRAWLER(133),
		SEND_PROGRESS_BAR(134),
		SEND_UPDATE_PROGRESS_BAR(134),
		SEND_REMOVE_PROGRESS_BAR(134),
		SEND_BANK_PIN_INTERFACE(135),
		SEND_ONLINE_LIST(136),
		SEND_SHOP_CLOSE(137),
		SEND_FRIEND_UPDATE(149),
		SEND_EQUIPMENT_STATS(153),
		SEND_STATS(156),
		SEND_PRIVACY_SETTINGS(158),
		SEND_STAT(159),
		SEND_UPDATE_STAT(159),
		SEND_TRADE_OTHER_ACCEPTED(162),
		SEND_LOGOUT_REQUEST_CONFIRM(165),
		SEND_DUEL_CONFIRMWINDOW(172),
		SEND_DUEL_WINDOW(176),
		SEND_WELCOME_INFO(182),
		SEND_CANT_LOGOUT(183),
		SEND_SLEEPWORD_INCORRECT(194),
		SEND_BANK_CLOSE(203),
		SEND_PLAY_SOUND(204),
		SEND_PRAYERS_ACTIVE(206),
		SEND_PETS_ACTIVE(207),
		SEND_DUEL_ACCEPTED(210),
		SEND_BOX(222),
		SEND_OPEN_RECOVERY(224),
		SEND_DUEL_CLOSE(225),
		SEND_OPEN_DETAILS(232),
		SEND_GAME_SETTINGS(240),
		SEND_SLEEP_FATIGUE(244),
		SEND_OPTIONS_MENU_OPEN(245),
		SEND_BANK_UPDATE(249),
		SEND_OPTIONS_MENU_CLOSE(252),
		SEND_DUEL_OTHER_ACCEPTED(253);

		private int opcode;

		Opcode(int i) {
			this.opcode = i;
		}
	}
}
