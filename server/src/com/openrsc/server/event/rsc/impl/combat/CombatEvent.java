package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.Constants;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScriptLoader;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.handlers.PetHandler;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.model.entity.player.Pets;

/***
 *
 * @author n0m
 *
 */
public class CombatEvent extends GameTickEvent {

	private final Mob attackerMob, defenderMob;
	private int roundNumber = 0;

	public CombatEvent(Mob attacker, Mob defender) {
		super(null, 0);
		this.attackerMob = attacker;
		this.defenderMob = defender;
		CombatScriptLoader.checkAndExecuteOnStartCombatScript(attacker, defender);
		if (attacker.isNpc()) {
			((Npc)attacker).setExecutedAggroScript(false);
		} else if (defender.isNpc()) {
			((Npc)defender).setExecutedAggroScript(false);
		}
	}

	private static void onDeath(Mob killed, Mob killer) {
		if (killer.isPlayer() && killed.isNpc()) {
			if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerKilledNpc",
				new Object[]{((Player) killer), ((Npc) killed)})) {
				return;
			}
		}
		
		if(killer.getPetOwnerA2() != null) {
			killer.setPetOpponent(null);
		}
		
		killed.setLastCombatState(CombatState.WON);
		killer.setLastCombatState(CombatState.LOST);
		
		if(killed.getPetOwnerA2() != null){
			Player p28x = killed.getPetOwnerA2();
			if(p28x.getPets().isPetActivated(1)) {
				p28x.getPets().setPet(1, false);
				p28x.setPetInCombat(0);
				p28x.setPetOut(99);
				p28x.message("Your Warrior pet has died.");
			}
		}

		if (killed.isPlayer() && killer.isPlayer()) {
			Player playerKiller = (Player) killer;
			Player playerKilled = (Player) killed;

			int exp = Formulae.combatExperience(playerKilled);
			switch (playerKiller.getCombatStyle()) {
				case 0:
					for (int x = 0; x < 3; x++) {
						playerKiller.incExp(x, exp, true);
					}
					break;
				case 1:
					playerKiller.incExp(2, exp * 3, true);
					break;
				case 2:
					playerKiller.incExp(0, exp * 3, true);
					break;
				case 3:
					playerKiller.incExp(1, exp * 3, true);
					break;
			}
			playerKiller.incExp(3, exp, true);
		}
		killer.setKillType(0);
		killed.killedBy(killer);
	}

	public final void run() {
		setDelayTicks(2);
		Mob hitter, target = null;

		if (roundNumber++ % 2 == 0) {
			hitter = attackerMob;
			target = defenderMob;
		} else {
			hitter = defenderMob;
			target = attackerMob;
		}

		if (!combatCanContinue()) {
			hitter.setLastCombatState(CombatState.ERROR);
			target.setLastCombatState(CombatState.ERROR);
			resetCombat();
		} else {
			inflictDamage(hitter, target, MeleeFormula.getDamage(hitter, target));
		}
	}

	private void inflictDamage(final Mob hitter, final Mob target, int damage) {
		if(hitter.isNpc()){
			if(hitter.getID() == 808){
				Player p28x = hitter.getPetOwnerA2();
				p28x.setPet1Fatigue(p28x.getPet1Fatigue() + 225);
			}
		}
		hitter.incHitsMade();
		if (hitter.isNpc() && target.isPlayer()) {
			Player targetPlayer = (Player) target;

			if (targetPlayer.getPrayers().isPrayerActivated(Prayers.PARALYZE_MONSTER)) {
				CombatScriptLoader.checkAndExecuteCombatScript(hitter, target);
				return;
			}
		}

		target.getSkills().subtractLevel(Skills.HITPOINTS, damage, false);
		target.getUpdateFlags().setDamage(new Damage(target, damage));
		if (target.isNpc() && hitter.isPlayer()) {
			Npc n = (Npc) target;
			Player p = ((Player) hitter);
			n.addCombatDamage(p, damage);
		}

		String combatSound = null;
		combatSound = damage > 0 ? "combat1b" : "combat1a";

		if (target.isPlayer()) {
			if (hitter.isNpc()) {
				Npc n = (Npc) hitter;
				if (DataConversions.inArray(Constants.GameServer.ARMOR_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (DataConversions.inArray(Constants.GameServer.UNDEAD_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			Player opponentPlayer = ((Player) target);
			ActionSender.sendSound(opponentPlayer, combatSound);
		}
		if (hitter.isPlayer()) {
			if (target.isNpc()) {
				Npc n = (Npc) target;
				if (DataConversions.inArray(Constants.GameServer.ARMOR_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (DataConversions.inArray(Constants.GameServer.UNDEAD_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			Player attackerPlayer = (Player) hitter;
			ActionSender.sendSound(attackerPlayer, combatSound);
		}

		if (target.getSkills().getLevel(3) > 0) {
			CombatScriptLoader.checkAndExecuteCombatScript(hitter, target);
		} else {
			onDeath(target, hitter);
		}
	}

	public void resetCombat() {
		if (running) {
			if (defenderMob != null) {
				Mob j = defenderMob.getOpponent();
				int delayedAggro = 0;
				if (defenderMob.isPlayer()) {
					Player player = (Player) defenderMob;
					player.setStatus(Action.IDLE);
					player.resetAll();
				} else {
					if (j.getPetOwnerA2() == null && defenderMob.getID() != 210 && defenderMob.getCombatState() == CombatState.RUNNING)
						delayedAggro = 17000; // 17 + 3 second aggro timer for npds running
				}

				defenderMob.setBusy(false);
				defenderMob.setOpponent(null);
				defenderMob.setCombatEvent(null);
				defenderMob.setHitsMade(0);
				defenderMob.setSprite(4);
				defenderMob.setCombatTimer(delayedAggro);
			}
			if (attackerMob != null) {
				int delayedAggro = 0;
				if (attackerMob.isPlayer()) {
					Player player = (Player) attackerMob;
					player.setStatus(Action.IDLE);
					player.resetAll();
				} else {
					if (attackerMob.getCombatState() == CombatState.RUNNING)
						delayedAggro = 17000; // 17 + 3 second timer for npcs running
				}

				attackerMob.setBusy(false);
				attackerMob.setOpponent(null);
				attackerMob.setCombatEvent(null);
				attackerMob.setHitsMade(0);
				attackerMob.setSprite(4);
				attackerMob.setCombatTimer(delayedAggro);
			}
		}
		stop();
	}

	private boolean combatCanContinue() {
		boolean removed = attackerMob.isRemoved() || defenderMob.isRemoved();
		boolean nextToVictim = attackerMob.getLocation().equals(defenderMob.getLocation());
		if (defenderMob.isNpc() && attackerMob.isNpc()) {
			return !removed && nextToVictim && running;
		}
		boolean bothLoggedIn = (attackerMob.isPlayer() && ((Player) attackerMob).loggedIn())
			|| (defenderMob.isPlayer() && ((Player) defenderMob).loggedIn());
		return bothLoggedIn && !removed && nextToVictim && running;
	}

	public Mob getAttacker() {
		return attackerMob;
	}

	public Mob getVictim() {
		return defenderMob;
	}

}
