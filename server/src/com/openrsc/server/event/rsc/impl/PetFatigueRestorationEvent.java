package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author n0m
 */
public class PetFatigueRestorationEvent extends GameTickEvent {

	private long lastRestoration = System.currentTimeMillis();

	public PetFatigueRestorationEvent(Mob mob) {
		super(mob, 1);
	}

	@Override
	public void run() {

		boolean restored = false;
		checkAndStartRestoration0(((Player) owner).getPet0Fatigue());
		checkAndStartRestoration3(((Player) owner).getPet3Fatigue());
		checkAndStartRestoration4(((Player) owner).getPet4Fatigue());
		checkAndStartRestoration2(((Player) owner).getPet2Fatigue());
		checkAndStartRestoration1(((Player) owner).getPet1Fatigue());

		//Iterator<Entry<Integer, Integer>> it = restoringStats.entrySet().iterator();
		//while (it.hasNext()) {

			//Entry<Integer, Integer> set = it.next();

			long delay = 1000; // 60 seconds
			/*if (owner.isPlayer()) {
				Player player = (Player) owner;
				if (player.getPrayers().isPrayerActivated(Prayers.RAPID_HEAL) && stat == 3) {
					delay = 30000;
				} else if (player.getPrayers().isPrayerActivated(Prayers.RAPID_RESTORE) && stat != 3) {
					delay = 30000;
				}
			}*/
			if (System.currentTimeMillis() - this.lastRestoration > delay) {
				if(!((Player) owner).getPets().isPetActivated(0)) {
					normalizeLevel0(((Player) owner).getPet0Fatigue());
					restored = true;
					//p.message("Resting");
					if (((Player) owner).getPet0Fatigue() == 0) {
						//it.remove();
						if (owner.isPlayer()) {
							Player p = (Player) owner;
							//p.message("Your Healer pet is fully rested.");
						}
					}
				}
				if(!((Player) owner).getPets().isPetActivated(3)) {
					normalizeLevel3(((Player) owner).getPet3Fatigue());
					restored = true;
					//p.message("Resting");
					if (((Player) owner).getPet3Fatigue() == 0) {
						//it.remove();
						if (owner.isPlayer()) {
							Player p = (Player) owner;
							//p.message("Your Healer pet is fully rested.");
						}
					}
				}
				if(!((Player) owner).getPets().isPetActivated(4)) {
					normalizeLevel4(((Player) owner).getPet4Fatigue());
					restored = true;
					//p.message("Resting");
					if (((Player) owner).getPet4Fatigue() == 0) {
						//it.remove();
						if (owner.isPlayer()) {
							Player p = (Player) owner;
							//p.message("Your Healer pet is fully rested.");
						}
					}
				}
				if(!((Player) owner).getPets().isPetActivated(1)) {
					normalizeLevel1(((Player) owner).getPet1Fatigue());
					restored = true;
					//p.message("Resting");
					if (((Player) owner).getPet1Fatigue() == 0) {
						//it.remove();
						if (owner.isPlayer()) {
							Player p = (Player) owner;
							//p.message("Your Warrior pet is fully rested.");
						}
					}
				}
				if(!((Player) owner).getPets().isPetActivated(2)) {
					normalizeLevel2(((Player) owner).getPet2Fatigue());
					restored = true;
					//p.message("Resting");
					if (((Player) owner).getPet2Fatigue() == 0) {
						//it.remove();
						if (owner.isPlayer()) {
							Player p = (Player) owner;
							//p.message("Your Warrior pet is fully rested.");
						}
					}
				}
			}
		//}
		if (restored)
			this.lastRestoration = System.currentTimeMillis();
	}

	/**
	 * Normalises level to max level by 1.
	 *
	 * @param skill
	 * @return true if action done, false if skill is already normal
	 */
	private void normalizeLevel0(int fatigue) {
		int cur = ((Player) owner).getPet0Fatigue();
		int norm = 0;

		if (cur > norm) {
			((Player) owner).setPet0Fatigue(cur - 120);
		}

		//if (cur == norm)
			//restoringStats.put(fatigue, 0);
	}
	private void normalizeLevel3(int fatigue) {
		int cur = ((Player) owner).getPet3Fatigue();
		int norm = 0;

		if (cur > norm) {
			((Player) owner).setPet3Fatigue(cur - 120);
		}

		//if (cur == norm)
			//restoringStats.put(fatigue, 0);
	}
	private void normalizeLevel4(int fatigue) {
		int cur = ((Player) owner).getPet4Fatigue();
		int norm = 0;

		if (cur > norm) {
			((Player) owner).setPet4Fatigue(cur - 120);
		}

		//if (cur == norm)
			//restoringStats.put(fatigue, 0);
	}
	private void normalizeLevel1(int fatigue) {
		int cur = ((Player) owner).getPet1Fatigue();
		int norm = 0;

		if (cur > norm) {
			((Player) owner).setPet1Fatigue(cur - 120);
		}

		//if (cur == norm)
			//restoringStats.put(fatigue, 0);
	}
	private void normalizeLevel2(int fatigue) {
		int cur = ((Player) owner).getPet2Fatigue();
		int norm = 0;

		if (cur > norm) {
			((Player) owner).setPet2Fatigue(cur - 120);
		}

		//if (cur == norm)
			//restoringStats.put(fatigue, 0);
	}

	private void checkAndStartRestoration0(int fatigue) {
		int curFatigue = ((Player) owner).getPet0Fatigue();
		int fullyRested = 0;
		if (curFatigue > fullyRested) {
		}
	}
	private void checkAndStartRestoration3(int fatigue) {
		int curFatigue = ((Player) owner).getPet3Fatigue();
		int fullyRested = 0;
		if (curFatigue > fullyRested) {
		}
	}
	private void checkAndStartRestoration4(int fatigue) {
		int curFatigue = ((Player) owner).getPet4Fatigue();
		int fullyRested = 0;
		if (curFatigue > fullyRested) {
		}
	}
	private void checkAndStartRestoration1(int fatigue) {
		int curFatigue = ((Player) owner).getPet1Fatigue();
		int fullyRested = 0;
		if (curFatigue > fullyRested) {
		}
	}
	private void checkAndStartRestoration2(int fatigue) {
		int curFatigue = ((Player) owner).getPet2Fatigue();
		int fullyRested = 0;
		if (curFatigue > fullyRested) {
		}
	}
}
