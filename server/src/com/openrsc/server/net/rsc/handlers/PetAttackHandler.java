package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Server;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.event.rsc.impl.RangeEvent;
import com.openrsc.server.event.rsc.impl.ThrowingEvent;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.model.action.WalkToMobActionNpc;
import com.openrsc.server.model.action.WalkToPointActionNpc;
import com.openrsc.server.model.action.WalkToActionNpc;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;

import static com.openrsc.server.plugins.Functions.transform;

public class PetAttackHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();
		player.resetAll();
		Mob affectedMob = null;
		int serverIndex = p.readShort();
		int packetOne = OpcodeIn.NPC_PETATTACK1.getOpcode();
		int packetTwo = OpcodeIn.NPC_PETATTACK1.getOpcode();

		if (pID == packetOne) {
			affectedMob = world.getNpc(serverIndex);
		} else if (pID == packetTwo) {
			affectedMob = world.getNpc(serverIndex);
		}
		if (affectedMob == null || affectedMob.equals(player)) {
			player.resetPath();
			return;
		}
		if(!player.getPets().isPetActivated(1) && !player.getPets().isPetActivated(2) && !player.getPets().isPetActivated(3)){
			player.message("Only use orsc client please.");
			player.resetPath();
			player.setSuspiciousPlayer(true);
			return;
		}
		if (affectedMob.isPlayer()) {
			if (affectedMob.getLocation().inBounds(220, 108, 225, 111)) { // mage arena block real rsc.
				player.message("Here kolodion protects all from your attack");
				player.resetPath();
				return;
			}
		}
		if (affectedMob.isNpc()) {
			Npc n = (Npc) affectedMob;
			if(n.getPetNpc() > 0) {
			player.message("This npc belongs too " + n.getPetOwnerA2() + ". You may not attack it.");
			player.resetPath();
			return;
			}
			if (n.getX() == 0 && n.getY() == 0)
				return;
			if (n.getID() == 525 && player.getRangeEquip() < 0 && player.getThrowingEquip() < 0) {
				player.message("these ogres are for range combat training only");
				return;
			}
		}

		if (!player.getPets().isPetActivated(2) && !player.getPets().isPetActivated(3)) {
			for (Npc n2 : World.getWorld().getNpcs()) {
				if(n2.getPetOwnerA2() == player){
					/*if(n2.inCombat()) {
						player.message("Your pet is already in combat.");
						player.resetPath();
						return;
					}*/
					if(n2.inCombat()) {
						if (n2.getOpponent().getHitsMade() >= 3) {
							n2.setPetOpponent(null);
							n2.resetCombatEvent();
							Point walkTo = Point.location(DataConversions.random(player.getX() - 1, player.getX() + 1), DataConversions.random(player.getY() - 1, player.getY() + 1));
						if(walkTo.getX() == player.getX() && walkTo.getY() == player.getY()){
				
						} else {
							n2.walk(walkTo.getX(), walkTo.getY());
							n2.face(player);
						}
							player.message("You call your pet out of combat");
							player.resetPath();
							player.setPetInCombat(0);
							return;
						} else {
							player.message("You cannot call your pet away from combat during the first 3 rounds of combat");
							player.resetPath();
							return;
						}
					}
					player.resetPath();
				if (affectedMob.inCombat() && !player.getPets().isPetActivated(2) && !player.getPets().isPetActivated(3)) {
						player.message(affectedMob + " is already in combat.");
						return;
				}
				if (n2.isBusy() || affectedMob.isBusy() || !n2.canReach(affectedMob)
					|| !n2.checkAttack(affectedMob, false)) {
						player.message("Your pet cannot reach " + affectedMob);
						return;
				}
					n2.setPetOpponent(affectedMob);
					player.setPetInCombat(1);
				/*if(affectedMob.getSkills().getLevel(Skills.HITPOINTS) <= 0 || affectedMob.isRemoved() || affectedMob == null) {
						player.setPetInCombat(0);
					}*/
				}
			}
		} else {
			for (Npc n2 : World.getWorld().getNpcs()) {
			if (n2.isBusy() || !n2.inCombat()) {
				return;
			}
			final Mob target = affectedMob;
			player.resetPath();
			player.resetAll();
			/* To skip the walk packet resetAll() */
			Server.getServer().getEventHandler().add(new MiniEvent(player) {
				@Override
				public void action() {
					owner.setStatus(Action.RANGING_MOB);
					if (target.isPlayer()) {
						Player affectedPlayer = (Player) target;
						owner.setSkulledOn(affectedPlayer);
						affectedPlayer.getTrade().resetAll();
						if (affectedPlayer.getMenuHandler() != null) {
							affectedPlayer.resetMenuHandler();
						}
						if (affectedPlayer.accessingBank()) {
							affectedPlayer.resetBank();
						}
						if (affectedPlayer.accessingShop()) {
							affectedPlayer.resetShop();
						}
					}
					if (player.getRangeEquip() > 0) {
						owner.setRangeEvent(new RangeEvent(owner, target));
					} else {
						owner.setThrowingEvent(new ThrowingEvent(owner, target));
					}
				}
			});
			}
		}
	}
	}
	//}

//}
