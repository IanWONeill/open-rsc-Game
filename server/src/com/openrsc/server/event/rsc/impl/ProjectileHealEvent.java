package com.openrsc.server.event.rsc.impl;

import static com.openrsc.server.plugins.Functions.getMaxLevel;
import com.openrsc.server.event.rsc.SingleTickEvent;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.entity.update.Heal;
import com.openrsc.server.model.entity.update.Projectile;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.PluginHandler;

/**
 * @author n0m
 */
public class ProjectileHealEvent extends SingleTickEvent {

	Mob caster, opponent;
	protected int healAmount;
	protected int type;
	boolean canceled;

	public ProjectileHealEvent(Mob caster, Mob opponent, int healAmount, int type) {
		super(caster, 1);
		this.caster = caster;
		this.opponent = opponent;
		this.healAmount = healAmount;
		this.type = type;
		if (caster.isPlayer() && opponent.isPlayer()) {
			caster.setAttribute("projectile", this);
			opponent.setAttribute("projectile", this);
		}

		// opponent.setCombatTimer();
		opponent.setAttribute("last_shot", System.currentTimeMillis());

		sendProjectile(caster, opponent);
		if (caster.isPlayer() && opponent.isPlayer()) {
			Player oppPlayer = (Player) opponent;
			Player casterPlayer = (Player) caster;
			if (!casterPlayer.getDuel().isDuelActive())
				casterPlayer.setSkulledOn(oppPlayer);
			String casterName = caster.isPlayer() ? ((Player) caster).getUsername() : ((Npc) caster).getDef().getName();
			oppPlayer.message("Warning! " + casterName + " is shooting at you!");
		}
	}

	private void sendProjectile(Mob caster, Mob opponent) {
		Projectile projectile = new Projectile(caster, opponent, type);
		caster.getUpdateFlags().setProjectile(projectile);
	}

	@Override
	public void action() {
		if (!canceled && caster.withinRange(opponent, 15)) {// maybe this will
			// cancel the damage
			// out on death.
			projectileHeal();
		}
		if (caster.isPlayer() && opponent.isPlayer()) {
			caster.removeAttribute("projectile");
			opponent.removeAttribute("projectile");
		}
	}

	private void projectileHeal() {
		if (caster.isPlayer()) {
			if (opponent.isRemoved() && type == 2) {
				((Player) caster).resetRange();
			}
		} /*if (caster.isNpc()) {
			if (opponent.isRemoved() && type == 2) {
				((Npc) caster).resetRange();
			}
		}*/
		
		if(opponent.getSkills().getLevel(3) + healAmount <= getMaxLevel(opponent, Skills.HITPOINTS)) {
		opponent.getSkills().addLevel(3, healAmount, false);
		} else
		if(opponent.getSkills().getLevel(3) + healAmount >= getMaxLevel(opponent, Skills.HITPOINTS)) {
		opponent.getSkills().setLevel(Skills.HITPOINTS, getMaxLevel(opponent, Skills.HITPOINTS));
		}
		opponent.getUpdateFlags().setHeal(new Heal(opponent, healAmount));

		/*if (caster.isPlayer()) {
			Player casterPlayer = (Player) caster;
			if (opponent.isNpc()) {
				Npc npcc = (Npc) opponent;
				if (type == 1 || type == 4)
					npcc.addMageDamage(casterPlayer, healAmount);
				else if (type == 2 || type == 5)
					npcc.addRangeDamage(casterPlayer, healAmount);
			}
		} else*/ if (opponent.isPlayer()) {
			Player affectedPlayer = (Player) opponent;
			ActionSender.sendStat(affectedPlayer, 3);
		}
		if (opponent.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			if (caster.isPlayer()) {
				Player player = (Player) caster;
				if (type == 2 || type == 5) {
					player.resetRange();
				}
			}/* else { caster.resetRange(); }*/
			if (opponent.isNpc() && caster.isPlayer()) {
				Player playerCaster = (Player) caster;
				Npc npcOpponent = (Npc) opponent;
				if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerKilledNpc",
					new Object[]{playerCaster, npcOpponent})) {
					return;
				}
				npcOpponent.killedBy(playerCaster);
			} else {
				opponent.killedBy(caster);
			}
		} else {
			if (opponent.isNpc() && caster.isPlayer()) {
				Npc npc = (Npc) opponent;
				Player player = (Player) caster;
				npc.setChasing(player);
			}
		}
	}

	public void setCanceled(boolean b) {
		canceled = b;
	}

}
