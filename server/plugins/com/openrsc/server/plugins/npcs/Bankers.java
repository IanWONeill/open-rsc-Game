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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
						GameNotifyEvent pinevent = validateBankPin(getPlayerOwner());
						return invokeOnNotify(pinevent, 3, 0);
					} else if (menu == 1) {
						GameNotifyEvent notifier = npcDialogue(getPlayerOwner(), npc, "This is a branch of the bank of Runescape");
						return invokeOnNotify(notifier, 6);
					} else if (menu == 2 && getPlayerOwner().getWorld().getServer().getConfig().WANT_BANK_PINS) {
						GameNotifyEvent event = showPlayerMenu(getPlayerOwner(), npc, false,
							"Set a bank pin", "Change bank pin", "Delete bank pin");
						return invokeOnNotify(event, 12, 0);
					} else if ((menu == 2 || menu == 3) && getPlayerOwner().getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
						GameNotifyEvent event = validateBankPin(getPlayerOwner());
						return invokeOnNotify(event, 15, 0);
					}
					return null;
				});
				addState(3, () -> {
					if(getPlayerOwner().getAttribute("bankpin", false)) {
						return invoke(4, 0);
					}
					else {
						return null;
					}
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
						GameNotifyEvent event = setBankPin(getPlayerOwner());
						return endOnNotify(event);
					} else if (bankPinMenu == 1) {
						GameNotifyEvent event = changeBankPin(getPlayerOwner());
						return endOnNotify(event);
					} else if (bankPinMenu == 2) {
						if (getPlayerOwner().getIronMan() > 0 && getPlayerOwner().getIronManRestriction() == 0) {
							getPlayerOwner().message("Deleting your bankpin results in permanent iron man restriction");
							return invoke(13, 3);
						}
						GameNotifyEvent event = removeBankPin(getPlayerOwner());
						return endOnNotify(event);
					}
					return null;
				});
				addState(13, () -> {
					getPlayerOwner().message("Are you sure you want to do it?");
					GameNotifyEvent event = showPlayerMenu(getPlayerOwner(), npc, "I want to do it!", "No thanks.");
					return invokeOnNotify(event, 14, 0);
				});
				addState(14, () -> {
					int deleteMenu = (int)getNotifyEvent().getObjectOut("int_option");
					if (deleteMenu == 0) {
						GameNotifyEvent event = removeBankPin(getPlayerOwner());
						return endOnNotify(event);
					} else if (deleteMenu == 1) {
						getPlayerOwner().message("You decide to not remove your Bank PIN.");
					}
					return null;
				});
				addState(15, () -> {
					if(!getPlayerOwner().getAttribute("bankpin", false)) {
						return null;
					}

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
				addCleanupState(() -> {
					getPlayerOwner().setBusy(false);
					npc.setBusy(false);
					return null;
				});
				addState(0, () -> {
					getPlayerOwner().setBusy(true);
					npc.setBusy(true);
					GameNotifyEvent event = validateBankPin(getPlayerOwner());
					return invokeOnNotify(event,1, 0);
				});
				addState(1, () -> {
					if(getPlayerOwner().getAttribute("bankpin", false)) {
						return invoke(2, 0);
					}
					else {
						return null;
					}
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
