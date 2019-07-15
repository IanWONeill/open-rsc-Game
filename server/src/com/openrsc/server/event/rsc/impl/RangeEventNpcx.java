package com.openrsc.server.event.rsc.impl;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

/**
 * @author n0m
 */
public class RangeEventNpcx extends GameTickEvent {

	private boolean deliveredFirstProjectile;
	
	private int[][] allowedArrows = {{189, 11, 638, 639}, // Shortbow
		{188, 11, 574, 638, 639}, // Longbow
		{649, 11, 574, 638, 639}, // Oak Shortbow
		{648, 11, 574, 638, 639, 640, 641}, // Oak Longbow
		{651, 11, 574, 638, 639, 640, 641}, // Willow Shortbow
		{650, 11, 574, 638, 639, 640, 641, 642, 643}, // Willow Longbow
		{653, 11, 574, 638, 639, 640, 641, 642, 643}, // Maple Shortbow
		{652, 11, 574, 638, 639, 640, 641, 642, 643, 644, 645}, // Maple
		// Longbow
		{655, 11, 574, 638, 639, 640, 641, 642, 643, 644, 645, 723}, // Yew
		// Shortbow
		{654, 11, 574, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 723}, // Yew
		// Longbow
		{657, 11, 574, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 723}, // Magic
		// Shortbow
		{656, 11, 574, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 723} // Magic
		// Longbow
	};
	private Mob target;

	public RangeEventNpcx(Npc owner, Mob victim) {
		super(owner, 1);
		this.setImmediate(true);
		this.target = victim;
		this.deliveredFirstProjectile = false;
	}

	public boolean equals(Object o) {
		if (o instanceof RangeEventNpcx) {
			RangeEventNpcx e = (RangeEventNpcx) o;
			return e.belongsTo(owner);
		}
		return false;
	}

	public Mob getTarget() {
		return target;
	}

	private GroundItem getArrows(int id) {
		return target.getViewArea().getGroundItem(id, target.getLocation());
	}
	

	public void run() {
		for (Player p22 : World.getWorld().getPlayers()) {
			int combDiff = Math.abs(owner.getCombatLevel() - target.getCombatLevel());
			int targetWildLvl = target.getLocation().wildernessLevel();
			int myWildLvl = owner.getLocation().wildernessLevel();
			if ((target.isPlayer() && !((Player) target).loggedIn()) || target.getSkills().getLevel(Skills.HITPOINTS) <= 0 || !owner.withinRange(target)) {
				owner.resetRange();
				//p22.message("TEST 45454545");
				stop();
				return;
			}
			if (target.isNpc() && ((Npc) target).isRemoved()) {
				owner.resetRange();
				p22.message("TEST 45454545");
				stop();
				return;
			}
			if (owner.isNpc() && ((Npc) owner).isRemoved()) {
				owner.resetRange();
				p22.message("TEST 12121212");
				stop();
				return;
			}
			if (owner.inCombat()) {
				owner.resetRange();
				//p22.message("TEST 76767676");
				stop();
				return;
			}
			if (!target.getLocation().inBounds(((Npc) owner).getLoc().minX - 9, ((Npc) owner).getLoc().minY - 9,
				((Npc) owner).getLoc().maxX + 9, ((Npc) owner).getLoc().maxY + 9) && ((Npc) owner).isNpc()) {
				owner.resetRange();
				//p22.message("TEST 28282828");
				stop();
				return;
			}
			//p22.message("TEST 12341234");
			owner.resetPath();
			//owner.resetRange();
			boolean canShoot = System.currentTimeMillis() - owner.getAttribute("rangedTimeout", 0L) > 1900;
			if (canShoot) {
				if (!PathValidation.checkPath(owner.getLocation(), target.getLocation())) {
					//getPlayerOwner().message("I can't get a clear shot from here");
					owner.resetRange();
					//p22.message("TEST 44334433");
					stop();
					return;
				}
				owner.face(target);
				owner.setAttribute("rangedTimeout", System.currentTimeMillis());

				if (target.isPlayer()) {
					Player playerTarget = (Player) target;
					if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
						playerTarget.message(owner + " is trying to shoot you!");
						stop();
						return;
					}
				}	
				int arrowID = -1;
				//int damage = 1;
				int damage = Formulae.calcRangeHitNpc(owner, owner.getSkills().getLevel(Skills.RANGED), target.getArmourPoints(), 11);
				if (Formulae.looseArrow(damage)) {
					GroundItem arrows = getArrows(11);
					if (arrows == null) {
						for (Player p : World.getWorld().getPlayers()) {
						World.getWorld().registerItem(new GroundItem(11, target.getX(), target.getY(), 1, p));
						}
					} else {
						arrows.setAmount(arrows.getAmount() + 1);
					}
				}
				if (target.isPlayer() && owner.isNpc()) {
					((Player) target).message(owner + " is shooting at you!");
				}
				if (owner.isNpc() && owner.getPetNpc() > 0) {
					Player p28x = owner.getPetOwnerA2();
					p28x.setPet2Fatigue(p28x.getPet2Fatigue() + 50);
				}
				//ActionSender.sendSound(getPlayerOwner(), "shoot");
				if (EntityHandler.getItemDef(11).getName().toLowerCase().contains("poison") && target.isPlayer()) {
					if (DataConversions.random(0, 100) <= 10) {
						target.poisonDamage = target.getSkills().getMaxStat(Skills.HITPOINTS);
						target.startPoisonEvent();
					}
				}
				Server.getServer().getGameEventHandler().add(new ProjectileEvent(owner, target, damage, 2));
				//if(owner instanceof Npc){
				//owner.setChasing(target);
				//}
				owner.setKillType(2);
				deliveredFirstProjectile = true;
			}
	}
	}

	private boolean canReach(Mob mob) {
		int radius = 5;
		return owner.withinRange(mob, radius);
	}
	
	private boolean canReachx(Mob mob) {
		int radius = 4;
		return owner.withinRange(mob, radius);
	}

}
