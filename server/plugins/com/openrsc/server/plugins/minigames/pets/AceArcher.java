package com.openrsc.server.plugins.minigames.pets;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.Functions.addItem;

public class AceArcher implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.GUARD_UNRELEASED.id() && item.getID() == ItemId.A_GLOWING_RED_CRYSTAL.id();
	}

	@Override
	public GameStateEvent onInvUseOnNpc(Player player, Npc npc, Item item) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (player.getWorld().getServer().getConfig().WANT_PETS) {
						npc.resetPath();
						//npc.resetRange();
						player.setBusy(true);
						npc.face(player);
						player.face(npc);
						showBubble(player, item);
						player.message("You attempt to put the baby blue dragon in the crystal.");
						npc.setBusyTimer(3);

						player.getWorld().getServer().getGameEventHandler().add(new ShortEvent(player.getWorld(), player, "Ace Archer Pet") {
							public void action() {
					/*Npc nearbyNpc = getMultipleNpcsInArea(player, 5, NpcId.BABY_BLUE_DRAGON.id(), NpcId.BLUE_DRAGON.id(), NpcId.RED_DRAGON.id(), NpcId.DRAGON.id());
					if (nearbyNpc != null) {
						int selected = npc.getRandom().nextInt(5);
						if (selected == 0)
							npcYell(player, nearbyNpc, "roar!");
						else if (selected == 1)
							npcYell(player, nearbyNpc, "grrrr!");
						else if (selected == 2)
							npcYell(player, nearbyNpc, "growl!");
						else if (selected == 3)
							npcYell(player, nearbyNpc, "grr!");
						else if (selected == 4) {
							npcYell(player, nearbyNpc, "roar!");
						} else if (selected == 5) {
							npcYell(player, nearbyNpc, "grrrarrr!");
						}
						message(player, 1300, "The nearby " + (nearbyNpc.getDef().getName().contains("dragon") ? nearbyNpc.getDef().getName() : "" + nearbyNpc.getDef().getName().toLowerCase()) + " take a sudden dislike to you.");
						nearbyNpc.setChasing(player);
						//transform(nearbyNpc, 11, true);
						//attack(npc, nearbyNpc);
					}*/
								if (random(0, 4) != 0) {
									player.message("You catch the baby blue dragon in the crystal.");
									removeItem(player, ItemId.A_GLOWING_RED_CRYSTAL.id(), 1);
									addItem(player, ItemId.A_RED_CRYSTAL.id(), 1);
									ActionSender.sendInventory(player);
									player.setBusy(false);
									npc.setBusyTimer(0);
									npc.remove();
								} else {
									player.message("The baby blue dragon manages to get away from you!");
									npc.setBusyTimer(0);
									player.setBusy(false);
								}
							}
						});
					}

					return null;
				});
			}
		};
	}
}
