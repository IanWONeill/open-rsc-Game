package com.openrsc.server.model.entity.player;

import com.openrsc.server.net.rsc.ActionSender;

public class Pets {

	/*public static int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, ROCK_SKIN = 3,
		SUPERHUMAN_STRENGTH = 4, IMPROVED_REFLEXES = 5, RAPID_RESTORE = 6, RAPID_HEAL = 7, PROTECT_ITEMS = 8,
		STEEL_SKIN = 9, ULTIMATE_STRENGTH = 10, INCREDIBLE_REFLEXES = 11, PARALYZE_MONSTER = 12,
		PROTECT_FROM_MISSILES = 13;*/

	private Player player;
	private boolean[] activatedPets = new boolean[5];

	public Pets(Player player) {
		this.player = player;
	}

	public boolean isPetActivated(int pID) {
		return activatedPets[pID];
	}

	public void setPet(int pID, boolean b) {
		activatedPets[pID] = b;
		ActionSender.sendPets(player, activatedPets);
	}

	public void resetPets() {
		for (int x = 0; x < activatedPets.length; x++) {
			if (activatedPets[x]) {
				activatedPets[x] = false;
			}
		}
		ActionSender.sendPets(player, activatedPets);
	}
}
