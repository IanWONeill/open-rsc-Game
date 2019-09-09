package com.openrsc.server.plugins.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.*;


public class WeaponMaster implements TalkToNpcListener, TalkToNpcExecutiveListener,
	PickupExecutiveListener, PickupListener, PlayerAttackNpcExecutiveListener, PlayerAttackNpcListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.WEAPONSMASTER.id();
	}

	@Override
	public GameStateEvent onPlayerAttackNpc(Player p, Npc affectedmob) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (isPhoenixGang(p)) {
						playerTalk(p, affectedmob, "Nope, I'm not going to attack a fellow gang member");
						return null;
					} else {
						p.startCombat(affectedmob);
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.WEAPONSMASTER.id();
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		return (i.getX() == 107 || i.getX() == 105) && i.getY() == 1476
				&& i.getID() == ItemId.PHOENIX_CROSSBOW.id();
	}

	@Override
	public GameStateEvent onPickup(Player p, GroundItem i) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if ((i.getX() == 107 || i.getX() == 105) && i.getY() == 1476 && i.getID() == ItemId.PHOENIX_CROSSBOW.id()) {
						Npc weaponMaster = getNearestNpc(p, NpcId.WEAPONSMASTER.id(), 20);
						if (weaponMaster == null) {
							p.getWorld().unregisterItem(i);
							addItem(p, ItemId.PHOENIX_CROSSBOW.id(), 1);
							if (p.getCache().hasKey("arrav_mission") && (p.getCache().getInt("arrav_mission") & 1) == BLACKARM_MISSION) {
								p.getCache().set("arrav_gang", BLACK_ARM);
								p.updateQuestStage(Quests.SHIELD_OF_ARRAV, 4);
								p.getCache().remove("arrav_mission");
								p.getCache().remove("spoken_tramp");
							}
						} else if (!p.getCache().hasKey("arrav_gang") || isBlackArmGang(p)) {
							npcTalk(p, weaponMaster, "Hey thief!");
							weaponMaster.setChasing(p);
						} else if (isPhoenixGang(p)) {
							npcTalk(p, weaponMaster, "Hey, that's Straven's",
								"He won't like you messing with that");
						}
					}

					return null;
				});
			}
		};
	}

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (!p.getCache().hasKey("arrav_gang") || isBlackArmGang(p)) {
						playerTalk(p, n, "Hello");
						npcTalk(p, n, "Hey I don't know you",
							"You're not meant to be here");
						n.setChasing(p);
					} else if (isPhoenixGang(p)) {
						npcTalk(p, n, "Hello Fellow phoenix",
							"What are you after?");
						int menu = showMenu(p, n, "I'm after a weapon or two",
							"I'm looking for treasure");
						if (menu == 0) {
							npcTalk(p, n, "Sure have a look around");
						} else if (menu == 1) {
							npcTalk(p, n, "We've not got any up here",
								"Go mug someone somewhere",
								"If you want some treasure");
						}
					}

					return null;
				});
			}
		};
	}
}
