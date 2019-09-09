package com.openrsc.server.plugins.npcs.falador;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class Barmaid implements TalkToNpcExecutiveListener,
	TalkToNpcListener {
	private final String notEnoughMoney = "Oh dear. I don't seem to have enough money";

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BARMAID.id();
	}
	
	@Override
	public GameStateEvent onTalkToNpc(Player p, final Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (!p.getCache().hasKey("barcrawl")
						&& !p.getCache().hasKey("barthree")) {
						NORMAL_ALES(p, n);
					} else {
						int barCrawlOpt = showMenu(p, n, false, //do not send over
							"Hi what ales are you serving",
							"I'm doing Alfred Grimhand's barcrawl");
						if (barCrawlOpt == 0) {
							NORMAL_ALES(p, n);
						} else if (barCrawlOpt == 1) {
							playerTalk(p, n, "I'm doing Alfred Grimhand's barcrawl");
							npcTalk(p,
								n,
								"Hehe this'll be fun",
								"You'll be after our off the menu hand of death cocktail then",
								"Lots of expensive parts to the cocktail though",
								"So it will cost you 70 coins");
							if (hasItem(p, ItemId.COINS.id(), 70)) {
								message(p, "You buy a hand of death cocktail");
								p.getInventory().remove(ItemId.COINS.id(), 70);
								message(p, "You drink the cocktail",
									"You stumble around the room");
								drinkAle(p);
								p.damage(p.getRandom().nextInt(2) + 1);
								message(p, "The barmaid giggles",
									"The barmaid signs your card");
								p.getCache().store("barthree", true);
							} else {
								playerTalk(p, n, "I don't have that much money on me");
							}
						}
					}

					return null;
				});
			}
		};
	}
	
	private void NORMAL_ALES(Player p, Npc n) {
		playerTalk(p, n, "Hi, what ales are you serving?");
		npcTalk(p,
			n,
			"Well you can either have a nice Asgarnian Ale or a Wizards Mind Bomb",
			"Or a Dwarven Stout");

		String[] options = new String[]{"One Asgarnian Ale please",
			"I'll try the mind bomb", "Can I have a Dwarven Stout?",
			"I don't feel like any of those"};
		int option = showMenu(p, n, options);
		switch (option) {
			case 0:
				npcTalk(p, n, "That'll be two gold");

				if (hasItem(p, ItemId.COINS.id(), 2)) {
					p.message("You buy an Asgarnian Ale");
					p.getInventory().remove(ItemId.COINS.id(), 2);
					p.getInventory().add(new Item(ItemId.ASGARNIAN_ALE.id(), 1));
				} else {
					playerTalk(p, n, notEnoughMoney);
				}
				break;
			case 1:
				npcTalk(p, n, "That'll be two gold");

				if (hasItem(p, ItemId.COINS.id(), 2)) {
					p.message("You buy a pint of Wizard's Mind Bomb");
					p.getInventory().remove(ItemId.COINS.id(), 2);
					p.getInventory().add(new Item(ItemId.WIZARDS_MIND_BOMB.id(), 1));
				} else {
					playerTalk(p, n, notEnoughMoney);
				}
				break;
			case 2:
				npcTalk(p, n, "That'll be three gold");

				if (hasItem(p, ItemId.COINS.id(), 3)) {
					p.message("You buy a pint of Dwarven Stout");
					p.getInventory().remove(ItemId.COINS.id(), 3);
					p.getInventory().add(new Item(ItemId.DWARVEN_STOUT.id(), 1));
				} else {
					playerTalk(p, n, notEnoughMoney);
				}
				break;
			case 3:
				break;
		}
	}
	
	private void drinkAle(Player p) {
		int[] skillIDs = {Skills.ATTACK, Skills.DEFENSE, Skills.RANGED, Skills.FISHING};
		for (int i = 0; i < skillIDs.length; i++) {
			setAleEffect(p, skillIDs[i]);
		}
	}
	
	private void setAleEffect(Player p, int skillId) {
		int reduction, currentStat, maxStat;
		maxStat = p.getSkills().getMaxStat(skillId);
		//estimated
		reduction = maxStat < 15 ? 5 :
			maxStat < 45 ? 6 : 
			maxStat < 75 ? 7 : 8;
		currentStat = p.getSkills().getLevel(skillId);
		if (currentStat <= 8) {
			p.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
		}
		else {
			p.getSkills().setLevel(skillId, currentStat - reduction);
		}
	}

}
