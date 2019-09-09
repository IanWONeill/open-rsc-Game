package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public final class MonkOfEntrana implements ObjectActionExecutiveListener, ObjectActionListener, TalkToNpcExecutiveListener,
	TalkToNpcListener {

	private boolean CAN_GO(Player p) {
		for (Item item : p.getInventory().getItems()) {
			String name = item.getDef(p.getWorld()).getName().toLowerCase();
			if (name.contains("dagger") || name.contains("scimitar")
				|| (name.contains("bow") && !name.contains("unstrung") && !name.contains("string")) || name.contains("mail")
				|| (name.contains("sword")
				&& !name.equalsIgnoreCase("Swordfish") && !name.equalsIgnoreCase("Burnt Swordfish") && !name.equalsIgnoreCase("Raw Swordfish"))
				|| name.contains("mace") || name.contains("helmet")
				|| name.contains("axe") || name.contains("recoil")) {
				return true;
			}
		}

		if (p.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item item;
			for (int i = 0; i < Equipment.slots; i++) {
				item = p.getEquipment().get(i);
				if (item == null)
					continue;
				String name = item.getDef(p.getWorld()).getName().toLowerCase();
				if (name.contains("dagger") || name.contains("scimitar")
					|| (name.contains("bow") && !name.contains("unstrung") && !name.contains("string")) || name.contains("mail")
					|| (name.contains("sword")
					&& !name.equalsIgnoreCase("Swordfish") && !name.equalsIgnoreCase("Burnt Swordfish") && !name.equalsIgnoreCase("Raw Swordfish"))
					|| name.contains("mace") || name.contains("helmet")
					|| name.contains("axe") || name.contains("recoil")) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.MONK_OF_ENTRANA_PORTSARIM.id() || n.getID() == NpcId.MONK_OF_ENTRANA_UNRELEASED.id();
	}

	@Override
	public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (n.getID() == NpcId.MONK_OF_ENTRANA_PORTSARIM.id()) {
						npcTalk(p, n, "Are you looking to take passage to our holy island?",
							"If so your weapons and armour must be left behind");
						final Menu defaultMenu = new Menu();
						defaultMenu.addOption(new Option("No I don't wish to go") {
							@Override
							public void action() {
							}
						});
						defaultMenu.addOption(new Option("Yes, Okay I'm ready to go") {
							@Override
							public void action() {
								message(p, "The monk quickly searches you");
								if (CAN_GO(p)) {
									npcTalk(p, n, "Sorry we cannow allow you on to our island",
										"Make sure you are not carrying weapons or armour please");
								} else {
									message(p, "You board the ship");
									p.teleport(418, 570, false);
									sleep(2200);
									p.message("The ship arrives at Entrana");
								}
							}
						});
						defaultMenu.showMenu(p);
					}
					else if (n.getID() == NpcId.MONK_OF_ENTRANA_UNRELEASED.id()) {
						npcTalk(p, n, "Are you looking to take passage back to port sarim?");
						final Menu defaultMenu = new Menu();
						defaultMenu.addOption(new Option("No I don't wish to go") {
							@Override
							public void action() {
							}
						});
						defaultMenu.addOption(new Option("Yes, Okay I'm ready to go") {
							@Override
							public void action() {
								message(p, "You board the ship");
								p.teleport(264, 660, false);
								sleep(2200);
								p.message("The ship arrives at Port Sarim");
							}
						});
						defaultMenu.showMenu(p);
						return null;
					}

					return null;
				});
			}
		};
	}
	
	@Override
	public GameStateEvent onObjectAction(GameObject arg0, String arg1, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					Npc monk = getNearestNpc(p, NpcId.MONK_OF_ENTRANA_PORTSARIM.id(), 10);
					if (monk != null) {
						monk.initializeTalkScript(p);
					} else {
						p.message("I need to speak to the monk before boarding the ship.");
					}

					return null;
				});
			}
		};
	}
	
	@Override
	public boolean blockObjectAction(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 240 && arg0.getLocation().equals(Point.location(257, 661)))
			|| (arg0.getID() == 239 && arg0.getLocation().equals(Point.location(262, 661)))
			|| (arg0.getID() == 239 && arg0.getLocation().equals(Point.location(264, 661)))
			|| (arg0.getID() == 238 && arg0.getLocation().equals(Point.location(266, 661)));
	}
}
