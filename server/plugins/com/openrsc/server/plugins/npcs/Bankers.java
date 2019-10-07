package com.openrsc.server.plugins.npcs;

import com.openrsc.server.event.rsc.GameNotifyEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class Bankers implements TalkToNpcExecutiveListener, TalkToNpcListener, NpcCommandListener, NpcCommandExecutiveListener {
	private static final Logger LOGGER = LogManager.getLogger(Bankers.class);
	public static int[] BANKERS = {95, 224, 268, 540, 617};

	@Override
	public boolean blockTalkToNpc(final Player player, final Npc npc) {
		if (inArray(npc.getID(), BANKERS)) {
			return true;
		}
		return false;
	}

	@Override
	public GameStateEvent onTalkToNpc(Player player, final Npc npc) {
		ArrayList<String> messages = new ArrayList<>();
		messages.add("I'd like to access my bank account please");
		messages.add("What is this place?");
		if (player.getWorld().getServer().getConfig().WANT_BANK_PINS)
			messages.add("I'd like to talk about bank pin");
		if (player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS)
			messages.add("I'd like to collect my items from auction");

		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addCleanupState(() -> {
					getPlayerOwner().setBusy(false);
					npc.setBusy(false);
					return null;
				});
				addState(0, () -> {
					getPlayerOwner().setBusy(true);
					npc.setBusy(true);
					GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "Good day" + (npc.getID() == 617 ? " Bwana" : "") + ", how may I help you?");
					return invokeNextStateOnNotify(notifier);
				});
				addState(1, () -> {
					GameNotifyEvent event = showPlayerMenu(getPlayerOwner(), npc, messages.toArray(new String[messages.size()]));
					return invokeOnNotify(event, 2, 0);
				});
				addState(2, () -> {
					final int menu = (int)getNotifyEvent().getObjectOut("int_option");
					if (menu == 0) {
						if (getPlayerOwner().isIronMan(2)) {
							getPlayerOwner().message("As an Ultimate Iron Man, you cannot use the bank.");
							return null;
						}
						if (getPlayerOwner().getCache().hasKey("bank_pin") && !getPlayerOwner().getAttribute("bankpin", false)) {
							GameNotifyEvent pinevent = getBankPinEntered(getPlayerOwner());
							return invokeOnNotify(pinevent, 3, 0);
						}
						return invoke(4, 0);
					} else if (menu == 1) {
						GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "This is a branch of the bank of Runescape");
						return invokeOnNotify(notifier, 6);
					} else if (menu == 2 && getPlayerOwner().getWorld().getServer().getConfig().WANT_BANK_PINS) {
						GameNotifyEvent event = showPlayerMenu(getPlayerOwner(), npc,
							"Set a bank pin", "Change bank pin", "Delete bank pin");
						return invokeOnNotify(event, 12, 0);
					} else if ((menu == 2 || menu == 3) && getPlayerOwner().getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
						if (getPlayerOwner().getCache().hasKey("bank_pin") && !getPlayerOwner().getAttribute("bankpin", false)) {
							GameNotifyEvent event = getBankPinEntered(getPlayerOwner());
							return invokeOnNotify(event, 19, 0);
						}
					}
					return null;
				});
				addState(3, () -> {
					String pin = (String)getNotifyEvent().getObjectOut("string_pin");
					try {
						PreparedStatement statement = getPlayerOwner().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + getPlayerOwner().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, getPlayerOwner().getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							pin = DataConversions.hashPassword(pin, result.getString("salt"));
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					if (!getPlayerOwner().getCache().getString("bank_pin").equals(pin)) {
						ActionSender.sendBox(getPlayerOwner(), "Incorrect bank pin", false);
						return null;
					}
					getPlayerOwner().setAttribute("bankpin", true);
					getPlayerOwner().message("You have correctly entered your PIN");
					return invoke(4, 0);
				});
				addState(4, () -> {
					final GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "Certainly " + (getPlayerOwner().isMale() ? "Sir" : "Miss"));
					return invokeOnNotify(notifier, 5);
				});
				addState(5, () -> {
					ActionSender.showBank(getPlayerOwner());
					getPlayerOwner().setAccessingBank(true);
					return null;
				});
				addState(6, () -> {
					final GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "We have branches in many towns");
					return invokeOnNotify(notifier, 7);
				});
				addState(7, () -> {
					GameNotifyEvent menuevent = showPlayerMenu(getPlayerOwner(), npc,
						"And what do you do?", "Didn't you used to be called the bank of Varrock");
					return invokeOnNotify(menuevent, 8);
				});
				addState(8, () -> {
					int branchMenu = (int)getNotifyEvent().getObjectOut("int_option");
					if (branchMenu == 0) {
						final GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "We will look after your items and money for you");
						return invokeOnNotify(notifier, 9);
					} else if (branchMenu == 1) {
						final GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "Yes we did, but people kept on coming into our branches outside of varrock");
						return invokeOnNotify(notifier, 10);
					}
					return null;
				});
				addState(9, () -> {
					final GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "So leave your valuables with us if you want to keep them safe");
					return endOnNotify(notifier);
				});
				addState(10, () -> {
					final GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "And telling us our signs were wrong");
					return invokeOnNotify(notifier, 11);
				});
				addState(11, () -> {
					final GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "As if we didn't know what town we were in or something!");
					return endOnNotify(notifier);
				});
				addState(12, () -> {
					int bankPinMenu = (int)getNotifyEvent().getObjectOut("int_option");
					if (bankPinMenu == 0) {
						if (!getPlayerOwner().getCache().hasKey("bank_pin")) {
							GameNotifyEvent event = getBankPinEntered(getPlayerOwner());
							return invokeOnNotify(event, 13, 0);
						} else {
							ActionSender.sendBox(getPlayerOwner(), "You already have a bank pin", false);
						}
					} else if (bankPinMenu == 1) {
						if (getPlayerOwner().getCache().hasKey("bank_pin")) {
							GameNotifyEvent event = getBankPinEntered(getPlayerOwner());
							return invokeOnNotify(event, 14, 0);
						} else {
							getPlayerOwner().message("You don't have a bank pin");
						}
					} else if (bankPinMenu == 2) {
						if (getPlayerOwner().getCache().hasKey("bank_pin")) {
							GameNotifyEvent event = getBankPinEntered(getPlayerOwner());
							return invokeOnNotify(event, 16, 0);
						} else {
							getPlayerOwner().message("You don't have a bank pin");
						}
					}
					return null;
				});
				addState(13, () -> {
					String bankPin = (String)getNotifyEvent().getObjectOut("string_pin");
					if (bankPin == "cancel") {
						ActionSender.sendBox(getPlayerOwner(), "You have not entered a bank pin.%No bank pin set.", false);
						return null;
					}
					try {
						PreparedStatement statement = getPlayerOwner().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + getPlayerOwner().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, getPlayerOwner().getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
							getPlayerOwner().getCache().store("bank_pin", bankPin);
							ActionSender.sendBox(getPlayerOwner(), "You have set your bank pin.", false);
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					return null;
				});
				addState(14, () -> {
					String bankPin = (String)getNotifyEvent().getObjectOut("string_pin");
					try {
						PreparedStatement statement = getPlayerOwner().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + getPlayerOwner().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, getPlayerOwner().getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					if (!getPlayerOwner().getCache().getString("bank_pin").equals(bankPin)) {
						ActionSender.sendBox(getPlayerOwner(), "Incorrect bank pin", false);
						return null;
					}
					GameNotifyEvent event = getBankPinEntered(getPlayerOwner());
					return invokeOnNotify(event, 15, 0);
				});
				addState(15, () -> {
					try {
						String changeTo = (String)getNotifyEvent().getObjectOut("string_pin");
						if (changeTo == "cancel") {
							ActionSender.sendBox(getPlayerOwner(), "You have not entered a bank pin.%No bank pin set.", false);
							return null;
						}
						PreparedStatement statement = getPlayerOwner().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + getPlayerOwner().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, getPlayerOwner().getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							changeTo = DataConversions.hashPassword(changeTo, result.getString("salt"));
							getPlayerOwner().getCache().store("bank_pin", changeTo);
							ActionSender.sendBox(getPlayerOwner(), "You have set your bank pin.", false);
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					return null;
				});
				addState(16, () -> {
					String bankPin = (String)getNotifyEvent().getObjectOut("string_pin");
					try {
						PreparedStatement statement = getPlayerOwner().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + getPlayerOwner().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, getPlayerOwner().getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					if (!getPlayerOwner().getCache().getString("bank_pin").equals(bankPin)) {
						ActionSender.sendBox(getPlayerOwner(), "Incorrect bank pin", false);
						return null;
					}
					if (getPlayerOwner().getIronMan() > 0 && getPlayerOwner().getIronManRestriction() == 0) {
						getPlayerOwner().message("Deleting your bankpin results in permanent iron man restriction");
						return invoke(17, 3);
					}
					getPlayerOwner().getCache().remove("bank_pin");
					ActionSender.sendBox(getPlayerOwner(), "Your bank pin is removed", false);
					return null;
				});
				addState(17, () -> {
					getPlayerOwner().message("Are you sure you want to do it?");
					GameNotifyEvent event = showPlayerMenu(getPlayerOwner(), npc, "I want to do it!", "No thanks.");
					return invokeOnNotify(event, 18, 0);
				});
				addState(18, () -> {
					int deleteMenu = (int)getNotifyEvent().getObjectOut("int_option");
					if (deleteMenu == 0) {
						getPlayerOwner().getCache().remove("bank_pin");
						ActionSender.sendBox(getPlayerOwner(), "Your bank pin is removed", false);
						getPlayerOwner().message("Your iron man restriction status is now permanent.");
						getPlayerOwner().setIronManRestriction(1);
						ActionSender.sendIronManMode(getPlayerOwner());
					} else if (deleteMenu == 1) {
						getPlayerOwner().message("You decide to not remove your Bank PIN.");
					}
					return null;
				});
				addState(19, () -> {
					String pin = (String)getNotifyEvent().getObjectOut("string_pin");
					try {
						PreparedStatement statement = getPlayerOwner().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + getPlayerOwner().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, getPlayerOwner().getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							pin = DataConversions.hashPassword(pin, result.getString("salt"));
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					if (!getPlayerOwner().getCache().getString("bank_pin").equals(pin)) {
						ActionSender.sendBox(getPlayerOwner(), "Incorrect bank pin", false);
						return null;
					}
					getPlayerOwner().setAttribute("bankpin", true);
					ActionSender.sendBox(getPlayerOwner(), "Bank pin correct", false);
					getPlayerOwner().getWorld().getMarket().addPlayerCollectItemsTask(getPlayerOwner());
					return null;
				});
			}
		};
	}

	@Override
	public GameStateEvent onNpcCommand(Npc n, String command, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (inArray(n.getID(), BANKERS)) {
						if (command.equalsIgnoreCase("Bank") && p.getWorld().getServer().getConfig().RIGHT_CLICK_BANK) {
							quickFeature(n, p, false);
						} else if (command.equalsIgnoreCase("Collect") && p.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
							quickFeature(n, p, true);
						}
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockNpcCommand(Npc n, String command, Player p) {
		if (inArray(n.getID(), BANKERS) && command.equalsIgnoreCase("Bank")) {
			return true;
		}
		if (inArray(n.getID(), BANKERS) && command.equalsIgnoreCase("Collect")) {
			return true;
		}
		return false;
	}

	private void quickFeature(Npc npc, Player player, boolean auction) {
		player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Bank Quick Access") {
			public void init() {
				addState(0, () -> {
					if (getPlayerOwner().getCache().hasKey("bank_pin") && !getPlayerOwner().getAttribute("bankpin", false)) {
						GameNotifyEvent event = getBankPinEntered(getPlayerOwner());
						return invokeOnNotify(event,1, 0);
					}
					return invoke(2, 0);
				});
				addState(1, () -> {
					String pin = (String)getNotifyEvent().getObjectOut("string_pin");
					try {
						PreparedStatement statement = getPlayerOwner().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + getPlayerOwner().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, getPlayerOwner().getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							pin = DataConversions.hashPassword(pin, result.getString("salt"));
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					if (!getPlayerOwner().getCache().getString("bank_pin").equals(pin)) {
						ActionSender.sendBox(getPlayerOwner(), "Incorrect bank pin", false);
						return null;
					}
					getPlayerOwner().setAttribute("bankpin", true);
					getPlayerOwner().message("You have correctly entered your PIN");

					return invoke(2, 0);
				});
				addState(2, () -> {
					if (auction) {
						getPlayerOwner().getWorld().getMarket().addPlayerCollectItemsTask(getPlayerOwner());
					} else {
						getPlayerOwner().setAccessingBank(true);
						ActionSender.showBank(getPlayerOwner());
					}
					return null;
				});
			}
		});

	}
}
