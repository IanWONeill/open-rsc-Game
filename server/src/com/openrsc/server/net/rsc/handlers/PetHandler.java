package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.external.PetDef;
import com.openrsc.server.external.PrayerDef;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.net.rsc.ActionSender;

import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

public class PetHandler implements PacketHandler {

	private boolean activatePet(Player player, int petID) {
		/*if (!player.getPets().isPetActivated(petID)) {*/
			if (petID == 1) {
				if(player.getPets().isPetActivated(2)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 805 && petDragon.getPetOwnerA2() == player){
							player.message("You return your archer pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 2);
						}
					}
				}
				if(player.getPets().isPetActivated(3)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 806 && petDragon.getPetOwnerA2() == player){
							player.message("You return your mage pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 3);
						}
					}
				}
				if(player.getPets().isPetActivated(0)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 803 && petDragon.getPetOwnerA2() == player){
							player.message("You return your healer pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 0);
						}
					}
				}
				if(player.getPets().isPetActivated(4)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 804 && petDragon.getPetOwnerA2() == player){
							player.message("You return your banker pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 4);
						}
					}
				}
			} else if (petID == 2) {
				if(player.getPets().isPetActivated(1)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 807 && petDragon.getPetOwnerA2() == player){
							if(petDragon.inCombat()){
								player.message("You cannot return your pet while it is in combat.");
							} else {
							player.message("You return your warrior pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 1);
							}
						}
					}
				}
				if(player.getPets().isPetActivated(3)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 806 && petDragon.getPetOwnerA2() == player){
							player.message("You return your mage pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 3);
						}
					}
				}
				if(player.getPets().isPetActivated(0)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 803 && petDragon.getPetOwnerA2() == player) {
							player.message("You return your healer pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 0);
						}
					}
				}
			if(player.getPets().isPetActivated(4)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 804 && petDragon.getPetOwnerA2() == player){
							player.message("You return your banker pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 4);
						}
					}
				}
			} else if (petID == 3) {
				if(player.getPets().isPetActivated(1)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 807 && petDragon.getPetOwnerA2() == player){
							if(petDragon.inCombat()){
								player.message("You cannot return your pet while it is in combat.");
							} else {
							player.message("You return your warrior pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 1);
							}
						}
					}
				}
				if(player.getPets().isPetActivated(2)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 805 && petDragon.getPetOwnerA2() == player){
							player.message("You return your archer pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 2);
						}
					}
				}
				if(player.getPets().isPetActivated(0)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 803 && petDragon.getPetOwnerA2() == player) {
							player.message("You return your healer pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 0);
						}
					}
				}
				if(player.getPets().isPetActivated(4)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 804 && petDragon.getPetOwnerA2() == player){
							player.message("You return your banker pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 4);
						}
					}
				}
			} else if (petID == 4) {
				if(player.getPets().isPetActivated(1)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 807 && petDragon.getPetOwnerA2() == player){
							if(petDragon.inCombat()){
								player.message("You cannot return your pet while it is in combat.");
							} else {
							player.message("You return your warrior pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 1);
							}
						}
					}
				}
				if(player.getPets().isPetActivated(2)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 805 && petDragon.getPetOwnerA2() == player){
							player.message("You return your archer pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 2);
						}
					}
				}
				if(player.getPets().isPetActivated(3)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 806 && petDragon.getPetOwnerA2() == player){
							player.message("You return your mage pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 3);
						}
					}
				}
				if(player.getPets().isPetActivated(0)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 803 && petDragon.getPetOwnerA2() == player){
							player.message("You return your healer pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 0);
						}
					}
				}
			} else if (petID == 0) {
				/*if(player.getPets().isPetActivated(0)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 807 && petDragon.getPetOwnerA2() == player){
							if(petDragon.inCombat()){
								player.message("You cannot return your pet while it is in combat.");
							} else {
							//deactivatePet(player, 0);
							}
						}
					}
				}*/
				if(player.getPets().isPetActivated(1)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 807 && petDragon.getPetOwnerA2() == player){
							if(petDragon.inCombat()){
								player.message("You cannot return your pet while it is in combat.");
							} else {
							player.message("You return your warrior pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 1);
							}
						}
					}
				}
				if(player.getPets().isPetActivated(2)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 805 && petDragon.getPetOwnerA2() == player){
							player.message("You return your archer pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 2);
						}
					}
				}
				if(player.getPets().isPetActivated(3)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 806 && petDragon.getPetOwnerA2() == player){
							player.message("You return your mage pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 3);
						}
					}
				}
				if(player.getPets().isPetActivated(4)) {
					for (Npc petDragon : player.getViewArea().getNpcsInView()) {
						if(petDragon.getID() == 804 && petDragon.getPetOwnerA2() == player){
							player.message("You return your banker pet");
							//player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
							deactivatePet(player, 4);
						}
					}
				}
			} else if (petID == 6 || petID == 7) {
				//TODO:
			}
			
			player.getPets().setPet(petID, true);
			return true;
		/*}
		return false;*/
	}

	public boolean deactivatePet(Player player, int petID) {
		if (player.getPets().isPetActivated(petID)) {
			player.getPets().setPet(petID, false);
			if (petID == 6 || petID == 7) {
				//TODO:
			}
			return true;
		}
		return false;
	}

	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();
		int petID = (int) p.readByte();
		if (petID < 0 || petID >= 6) {
			player.setSuspiciousPlayer(true);
			return;
		}
		if (player.getDuel().isDuelActive()) {
			player.message("Pets cannot be used during this duel!");
			player.getPets().resetPets();
			return;
		}
		
		if (petID == 0) {
			if (Constants.GameServer.WANT_PETS) {			
			if(!player.getPets().isPetActivated(0)) {
				final Npc petDragon = spawnNpc(803, player.getX() + 1, player.getY(), 1000 * 60 * 24, player); // spawns for 5 hours and then poof!	
				sleep(Constants.GameServer.GAME_TICK);
				player.message("You summon your healer pet.");
				player.setBusy(true);
				petDragon.petOwner(player);
				//petOwnerA = player;
				petDragon.setPetOwnerA2(player);
				petDragon.setPetNpc(1);
				player.setPetOut(0);
				ActionSender.sendPetOut(player);
				//player.getPets().setPet(0, true);
				petDragon.setPetNpcType(4);//4 = healer
				petDragon.setShouldRespawn(false);
				player.setBusy(false);
			} else if(player.getPets().isPetActivated(0)) {
				for (Npc petDragon : player.getViewArea().getNpcsInView()) {
				if(petDragon.getID() == 803 && petDragon.getPetOwnerA2() == player){
				player.message("You return your healer pet");
				player.setPetOut(99);
				ActionSender.sendPetOut(player);
				petDragon.setPetNpcType(0);
				petDragon.setPetNpc(0);
				player.setBusy(false);
				petDragon.setBusyTimer(0);
				petDragon.remove();
				//player.getPets().setPet(0, false);
				}
				}
			}
		} else {
			player.message("Pets are currently disabled.");
		}
		}
		if (petID == 1) {
		if (Constants.GameServer.WANT_PETS) {
			if(!player.getPets().isPetActivated(1)) {
				final Npc petDragon = spawnNpc(807, player.getX() + 1, player.getY(), 1000 * 60 * 24, player); // spawns for 5 hours and then poof!	
				sleep(Constants.GameServer.GAME_TICK);
				player.message("You summon your warrior pet.");
				player.setBusy(true);
				petDragon.petOwner(player);
				//petOwnerA = player;
				petDragon.setPetOwnerA2(player);
				petDragon.setPetNpc(1);
				player.setPetOut(1);
				ActionSender.sendPetOut(player);
				//player.getPets().setPet(0, true);
				petDragon.setPetNpcType(1);//4 = healer
				petDragon.setShouldRespawn(false);
				player.setBusy(false);
			} else if(player.getPets().isPetActivated(1)) {
				for (Npc petDragon : player.getViewArea().getNpcsInView()) {
					if(petDragon.getID() == 807 && petDragon.getPetOwnerA2() == player){
						if(petDragon.inCombat()){
							player.message("You cannot return your pet while it is in combat.");
						} else {
							player.message("You return your warrior pet");
							player.setPetOut(99);
							ActionSender.sendPetOut(player);
							petDragon.setPetNpcType(0);
							petDragon.setPetNpc(0);
							player.setBusy(false);
							petDragon.setBusyTimer(0);
							petDragon.remove();
							//player.getPets().setPet(0, false);
						}
					}
				}
			}
		} else {
			player.message("Pets are currently disabled.");
		}
		}
		if (petID == 2) {
if (Constants.GameServer.WANT_PETS) {			
			if(!player.getPets().isPetActivated(2)) {
				final Npc petDragon = spawnNpc(805, player.getX() + 1, player.getY(), 1000 * 60 * 24, player); // spawns for 5 hours and then poof!	
				sleep(Constants.GameServer.GAME_TICK);
				player.message("You summon your archer pet.");
				player.setBusy(true);
				petDragon.petOwner(player);
				//petOwnerA = player;
				petDragon.setPetOwnerA2(player);
				petDragon.setPetNpc(1);
				player.setPetOut(2);
				//player.getPets().setPet(0, true);
				petDragon.setPetNpcType(3);//3 = archer
				petDragon.setShouldRespawn(false);
				player.setBusy(false);
			} else if(player.getPets().isPetActivated(2)) {
				for (Npc petDragon : player.getViewArea().getNpcsInView()) {
				if(petDragon.getID() == 805 && petDragon.getPetOwnerA2() == player){
				player.message("You return your archer pet");
				player.setPetOut(99);
				petDragon.setPetNpcType(0);
				petDragon.setPetNpc(0);
				player.setBusy(false);
				petDragon.setBusyTimer(0);
				petDragon.remove();
				//player.getPets().setPet(0, false);
				}
				}
			}
		} else {
			player.message("Pets are currently disabled.");
		}
		}
		if (petID == 3) {
			if (Constants.GameServer.WANT_PETS) {
			if(!player.getPets().isPetActivated(3)) {
				final Npc petDragon = spawnNpc(806, player.getX() + 1, player.getY(), 1000 * 60 * 24, player); // spawns for 5 hours and then poof!	
				sleep(Constants.GameServer.GAME_TICK);
				player.message("You summon your mage pet.");
				player.setBusy(true);
				petDragon.petOwner(player);
				//petOwnerA = player;
				petDragon.setPetOwnerA2(player);
				petDragon.setPetNpc(1);
				player.setPetOut(3);
				//player.getPets().setPet(0, true);
				petDragon.setPetNpcType(2);//2 = mage
				petDragon.setShouldRespawn(false);
				player.setBusy(false);
			} else if(player.getPets().isPetActivated(3)) {
				for (Npc petDragon : player.getViewArea().getNpcsInView()) {
				if(petDragon.getID() == 806 && petDragon.getPetOwnerA2() == player){
				player.message("You return your mage pet");
				player.setPetOut(99);
				petDragon.setPetNpcType(0);
				petDragon.setPetNpc(0);
				player.setBusy(false);
				petDragon.setBusyTimer(0);
				petDragon.remove();
				//player.getPets().setPet(0, false);
				}
				}
			}
		} else {
			player.message("Pets are currently disabled.");
		}
		}
		if (petID == 4) {
			if (Constants.GameServer.WANT_PETS) {
			if(!player.getPets().isPetActivated(4)) {
				final Npc petDragon = spawnNpc(804, player.getX() + 1, player.getY(), 1000 * 60 * 24, player); // spawns for 5 hours and then poof!	
				sleep(Constants.GameServer.GAME_TICK);
				player.message("You summon your banker pet.");
				player.setBusy(true);
				petDragon.petOwner(player);
				//petOwnerA = player;
				petDragon.setPetOwnerA2(player);
				petDragon.setPetNpc(1);
				player.setPetOut(4);
				//player.getPets().setPet(0, true);
				petDragon.setPetNpcType(5);//4 = healer
				petDragon.setShouldRespawn(false);
				player.setBusy(false);
			} else if(player.getPets().isPetActivated(4)) {
				for (Npc petDragon : player.getViewArea().getNpcsInView()) {
				if(petDragon.getID() == 804 && petDragon.getPetOwnerA2() == player){
				player.message("You return your banker pet");
				player.setPetOut(99);
				petDragon.setPetNpcType(0);
				petDragon.setPetNpc(0);
				player.setBusy(false);
				petDragon.setBusyTimer(0);
				petDragon.remove();
				//player.getPets().setPet(0, false);
				}
				}
			}
		} else {
			player.message("Pets are currently disabled.");
		}
		}

		//PetDef pet = EntityHandler.getPetDef(petID);
		int packetOne = OpcodeIn.PET_ACTIVATED.getOpcode();
		int packetTwo = OpcodeIn.PET_DEACTIVATED.getOpcode();
		if (pID == packetOne) {
			activatePet(player, petID);
		} else if (pID == packetTwo) {
			deactivatePet(player, petID);
		}
	}
}
