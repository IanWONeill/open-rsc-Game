package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameNotifyEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.quests.members.undergroundpass.npcs.UndergroundPassKoftik;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassObstaclesMap1 implements ObjectActionListener, ObjectActionExecutiveListener {

	/**
	 * Quest Objects
	 **/
	public static int UNDERGROUND_CAVE = 725;
	public static int CRUMBLED_ROCK = 728;
	public static int FIRST_SWAMP = 754;
	public static int PILE_OF_MUD_MAP_LEVEL_1 = 767;
	public static int LEVER = 733;
	public static int BLESSED_SPIDER_SWAMP_OBJ = 795;
	public static int CLEAR_ROCKS = 772;
	public static int DROP_DOWN_LEDGE = 812;

	/**
	 * Main floor of the cave rocks
	 **/
	public static int[] MAIN_ROCKS = {731, 737, 738, 739, 740, 741, 742, 743, 744, 745, 746, 747, 748, 749};

	/**
	 * Main floor ledges
	 **/
	public static int[] MAIN_LEDGE = {732, 750, 751, 752, 753};

	/**
	 * From failing the swamp on main floor - rock riddle to get back to main floor.
	 **/
	public static int[] FAIL_SWAMP_ROCKS = {756, 757, 758, 759, 760, 762, 763, 764, 765, 766};

	/**
	 * Read rocks
	 **/
	public static int[] READ_ROCKS = {832, 833, 834, 835, 923, 922, 881};

	/**
	 * Spear rocks
	 **/
	public static int[] SPEAR_ROCKS = {806, 807, 808, 809, 810, 811, 882, 883};

	@Override
	public boolean blockObjectAction(GameObject obj, String cmd, Player p) {
		return obj.getID() == UNDERGROUND_CAVE || obj.getID() == CRUMBLED_ROCK
				|| inArray(obj.getID(), READ_ROCKS) || inArray(obj.getID(), MAIN_ROCKS)
				|| inArray(obj.getID(), MAIN_LEDGE) || obj.getID() == FIRST_SWAMP
				|| inArray(obj.getID(), FAIL_SWAMP_ROCKS) || obj.getID() == PILE_OF_MUD_MAP_LEVEL_1
				|| obj.getID() == LEVER || obj.getID() == BLESSED_SPIDER_SWAMP_OBJ
				|| obj.getID() == CLEAR_ROCKS || obj.getID() == DROP_DOWN_LEDGE || inArray(obj.getID(), SPEAR_ROCKS);
	}

	@Override
	public GameStateEvent onObjectAction(GameObject obj, String cmd, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (obj.getID() == UNDERGROUND_CAVE) {
						switch (getPlayerOwner().getQuestStage(Quests.UNDERGROUND_PASS)) {
							case 0:
								if (getPlayerOwner().getQuestStage(Quests.BIOHAZARD) != -1) {
									getPlayerOwner().message("You must first complete the biohazard quest...");
									getPlayerOwner().message("...before you can enter");
								}
								else {
									getPlayerOwner().message("you must talk to king lathas before you can enter");
								}
								break;
							case 1:
								Npc koftik = getNearestNpc(getPlayerOwner(), NpcId.KOFTIK_ARDOUGNE.id(), 10);
								if (koftik != null) {
									UndergroundPassKoftik.koftikEnterCaveDialogue(getPlayerOwner(), koftik);
								}
								break;
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
							case 7:
							case 8:
							case -1:
								return invokeOnNotify(questMessage(getPlayerOwner(), "you cautiously enter the cave"), 12);
						}
					}
					else if (obj.getID() == CRUMBLED_ROCK) {
						return invokeOnNotify(questMessage(getPlayerOwner(), "you climb the rock pile"), 13);
					}
					else if (inArray(obj.getID(), READ_ROCKS)) {
						return invokeOnNotify(questMessage(getPlayerOwner(), "the writing seems to have been scracthed...",
							"..into the rock with bare hands, it reads.."), 14);
					}
					else if (inArray(obj.getID(), MAIN_ROCKS)) {
						return endOnNotify(doRock(obj, getPlayerOwner(), (int) (getCurrentLevel(getPlayerOwner(), Skills.HITS) / 42) + 1, true, -1));
					}
					else if (obj.getID() == FIRST_SWAMP) {
						return invokeOnNotify(questMessage(p, "you try to cross but you're unable to",
							"the swamp seems to cling to your legs"), 9);
					}
					else if (inArray(obj.getID(), FAIL_SWAMP_ROCKS)) {
						return endOnNotify(doRock(obj, getPlayerOwner(), (int) (getCurrentLevel(getPlayerOwner(), Skills.HITS) / 42) + 1, true, -1));
					}
					else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_1) {
						return invokeOnNotify(questMessage(getPlayerOwner(), "you climb up the mud pile"), 15);
					}
					else if (inArray(obj.getID(), MAIN_LEDGE)) {
						return endOnNotify(doLedge(obj, getPlayerOwner(), (int) (getCurrentLevel(getPlayerOwner(), Skills.HITS) / 42) + 1));
					}
					else if (obj.getID() == LEVER) {
						return invokeOnNotify(questMessage(getPlayerOwner(), "you pull back on the old lever",
							"the bridge slowly lowers"), 16);
					}
					else if (obj.getID() == BLESSED_SPIDER_SWAMP_OBJ) {
						return endOnNotify(questMessage(getPlayerOwner(), "you step in rancid swamp",
							"it clings to your feet, you cannot cross"));
					}
					else if (obj.getID() == CLEAR_ROCKS) {
						if (getPlayerOwner().getX() == 695 && (getPlayerOwner().getY() == 3436 || getPlayerOwner().getY() == 3435)) {
							getPlayerOwner().teleport(695, 3435);
							return null;
						}
						return invokeOnNotify(questMessage(getPlayerOwner(), "you move the rocks from your path"), 20);
					}
					else if (inArray(obj.getID(), SPEAR_ROCKS)) {
						if (cmd.equalsIgnoreCase("step over")) {
							return invokeOnNotify(questMessage(getPlayerOwner(), "you step over the rock"), 22);
						} else {
							return invokeOnNotify(questMessage(getPlayerOwner(), "you search the rock",
								"you find a trip wire"), 23);
						}
					}
					else if (obj.getID() == DROP_DOWN_LEDGE) {
						getPlayerOwner().message("you drop down to the cave floor");
						getPlayerOwner().teleport(706, 3439);
					}

					return null;
				});
				addState(1, () -> {
					getPlayerOwner().teleport(677, 3462);

					return invoke(2, 1);
				});
				addState(2, () -> {
					getPlayerOwner().teleport(680, 3465);

					return invoke(3, 1);
				});
				addState(3, () -> {
					getPlayerOwner().teleport(682, 3462);
					return invoke(4, 1);
				});
				addState(4, () -> {
					getPlayerOwner().teleport(683, 3465);

					return invoke(5, 1);
				});
				addState(5, () -> {
					getPlayerOwner().teleport(685, 3464);

					return invoke(6, 1);
				});
				addState(6, () -> {
					getPlayerOwner().teleport(687, 3462);

					return invoke(7, 1);
				});
				addState(7, () -> {
					return invokeOnNotify(playerDialogue(p, null, "aargh"), 8);
				});
				addState(8, () -> {
					getPlayerOwner().damage((int) (getCurrentLevel(p, Skills.HITS) / 42) + 1);
					getPlayerOwner().teleport(690, 3461);

					return endOnNotify(questMessage(p, "you tumble deep into the cravass",
						"and land battered and bruised at the base"));
				});
				addState(9, () -> {
					getPlayerOwner().message("you slowly feel yourself being dragged below");
					return invokeOnNotify(playerDialogue(p, null, "gulp!"), 10);
				});
				addState(10, () -> {
					getPlayerOwner().teleport(674, 3462);
					return invokeOnNotify(playerDialogue(p, null, "aargh"), 11);
				});
				addState(11, () -> {
					getPlayerOwner().damage((int) (getCurrentLevel(getPlayerOwner(), Skills.HITS) / 42) + 1);
					return invoke(1, 4);
				});
				addState(12, () -> {
					getPlayerOwner().teleport(673, 3420);
					return null;
				});
				addState(13, () -> {
					getPlayerOwner().teleport(713, 581);
					return null;
				});
				addState(14, () -> {
					if (obj.getID() == 832) {
						ActionSender.sendBox(getPlayerOwner(), "@red@All those who thirst for knowledge%@red@Bow down to the lord.% %@red@All you that crave eternal life%@red@Come and meet your God.% %@red@For no man nor beast can cast a spell%@red@Against the wake of eternal hell.", true);
					} else if (obj.getID() == 833) {
						ActionSender.sendBox(getPlayerOwner(), "@red@Most men do live in fear of death%@red@That it might steal their soul.% %@red@Some work and pray to shield their life%@red@From the ravages of the cold.% %@red@But only those who embrace the end%@red@Can truly make their life extend.% %@red@And when all hope begins to fade% %@red@look above and use nature as your aid", true);
					} else if (obj.getID() == 834) {
						ActionSender.sendBox(getPlayerOwner(), "@red@And now our God has given us%@red@One who is from our own.% %@red@A saviour who once sat upon%@red@His father's glorious thrown.% %@red@It is in your name that we will lead the attack Iban%@red@son of Zamorak!", true);
					} else if (obj.getID() == 835) {
						ActionSender.sendBox(getPlayerOwner(), "@red@Here lies the sacred font%@red@Where the great Iban will bless all his disciples%@red@in the name of evil.% %@red@Here the forces of darkness are so concentrated they rise%@red@when they detect any positive force close by", true);
					} else if (obj.getID() == 923) {
						ActionSender.sendBox(getPlayerOwner(), "@red@Ibans Shadow% %@red@Then came the hard part: recreating the parts of a man%@red@that cannot be seen or touched: those intangible things%@red@that are life itself. Using all the mystical force that I could%@red@muster, I performed the ancient ritual of Incantia, a spell%@red@so powerful that it nearly stole the life from my frail and%@red@withered body. Opening my eyes again, I saw the three%@red@demons that had been summoned. Standing in a triangle,%@red@their energy was focused on the doll. These demons%@red@would be the keepers of Iban's shadow. Black as night,%@red@their shared spirit would follow his undead body like an%@red@angel of death.", true);
					} else if (obj.getID() == 922) {
						ActionSender.sendBox(getPlayerOwner(), "% %@red@Crumbling some of the dove's bones onto the doll, I cast%@red@my mind's eye onto Iban's body. My ritual was complete,%@red@soon he would be coming to life. I, Kardia, had resurrected%@red@the legendary Iban, the most powerful evil being ever to%@red@take human form. And I alone knew that the same%@red@process that I had used to create him, was also capable%@red@of destroying him.% %@red@But now I was exhausted. As I closed my eyes to sleep, I%@red@was settled by a strange feeling of contentment%@red@anticipation of the evil that Iban would soon unleash.", true);
					} else if (obj.getID() == 881) {
						ActionSender.sendBox(getPlayerOwner(), "@red@Leave this battered corpse be% %@red@For now he lives as spirit alone% %@red@Let his flesh rest and become one with the earth% %@red@As it is the soil that shall rise to protect him% %@red@Only as flesh becomes dust, as wood becomes ash...% %@red@..will Iban's corpse embrace nature and finally rest", true);
					}
					return null;
				});
				addState(15, () -> {
					getPlayerOwner().teleport(685, 3420);
					return endOnNotify(questMessage(getPlayerOwner(), "it leads into darkness, the stench is almost unbearable",
						"you surface by the swamp, covered in muck"));
				});
				addState(16, () -> {
					GameObject bridge_open = new GameObject(obj.getWorld(), Point.location(704, 3417), 727, 2, 0);
					GameObject bridge_closed = new GameObject(obj.getWorld(), Point.location(704, 3417), 726, 2, 0);
					bridge_open.getWorld().registerGameObject(bridge_open);
					bridge_closed.getWorld().delayedSpawnObject(bridge_closed.getLoc(), 10000);
					getPlayerOwner().teleport(709, 3420);
					return invoke(17, 1);
				});
				addState(18, () -> {
					getPlayerOwner().teleport(706, 3420);
					return invoke(19, 1);
				});
				addState(19, () -> {
					getPlayerOwner().teleport(703, 3420);
					getPlayerOwner().message("you cross the bridge");
					return null;
				});
				addState(20, () -> {
					getPlayerOwner().message("you hear a strange mechanical sound");
					obj.getWorld().replaceGameObject(obj,
						new GameObject(obj.getWorld(), obj.getLocation(), CLEAR_ROCKS + 1, obj.getDirection(), obj
							.getType()));
					obj.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
					getPlayerOwner().damage((int) (getCurrentLevel(getPlayerOwner(), Skills.HITS) * 0.2D));
					return invokeOnNotify(playerDialogue(getPlayerOwner(), null, "aaarrghhh"), 21);
				});
				addState(21, () -> {
					return endOnNotify(questMessage(getPlayerOwner(), "You've triggered a trap"));
				});
				addState(22, () -> {
					getPlayerOwner().message("you feel a thread tug at your boot");
					getPlayerOwner().message("it's a trap");
					getPlayerOwner().teleport(obj.getX(), obj.getY());
					obj.getWorld().replaceGameObject(obj,
						new GameObject(obj.getWorld(), obj.getLocation(), 805, obj.getDirection(), obj
							.getType()));
					obj.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
					getPlayerOwner().damage((int) (getCurrentLevel(p, Skills.HITS) / 6) + 1);
					return endOnNotify(playerDialogue(getPlayerOwner(), null, "aaarghh"));
				});
				addState(23, () -> {
					getPlayerOwner().message("do you wish to disarm the trap?");
					return invokeOnNotify(showPlayerMenu(getPlayerOwner(), "yes, i'll have a go", "no chance"), 24);
				});
				addState(24, () -> {
					final int menu = (int)getNotifyEvent().getObjectOut("int_option");
					if (menu == 0) {
						return invokeOnNotify(questMessage(getPlayerOwner(), "you carefully try and diconnect the trip wire"), 25);
					} else if (menu == 1) {
						getPlayerOwner().message("you back away from the trap");
						return null;
					}

					return null;
				});
				addState(25, () -> {
					if (succeed(getPlayerOwner(), 1)) {
						getPlayerOwner().message("you manage to delay the trap..");
						getPlayerOwner().message("...long enough to cross the rocks");
						if (obj.getX() == getPlayerOwner().getX() + 1)
							getPlayerOwner().teleport(obj.getX() + 1, obj.getY());
						else
							getPlayerOwner().teleport(obj.getX() - 1, obj.getY());

						return null;
					}
					else {
						getPlayerOwner().message("but the trap activates");
						getPlayerOwner().teleport(obj.getX(), obj.getY());
						obj.getWorld().replaceGameObject(obj,
							new GameObject(obj.getWorld(), obj.getLocation(), 805, obj.getDirection(), obj
								.getType()));
						obj.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
						getPlayerOwner().damage((int) (getCurrentLevel(getPlayerOwner(), Skills.HITS) / 6) + 1);

						return endOnNotify(playerDialogue(getPlayerOwner(), null, "aaarghh"));
					}
				});
			}
		};
	}

	boolean succeed(Player player, int req) {
		int level_difference = getCurrentLevel(player, Skills.THIEVING) - req;
		int percent = random(1, 100);

		if (level_difference < 0)
			return true;
		if (level_difference >= 15)
			level_difference = 70;
		if (level_difference >= 20)
			level_difference = 80;
		else
			level_difference = 40 + level_difference;

		return percent <= level_difference;
	}

	public static GameNotifyEvent doMessages(final Player p, final int initialDelay) {
		return new GameNotifyEvent(p.getWorld(), p, initialDelay, UndergroundPassObstaclesMap1.class.getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					int random = DataConversions.getRandom().nextInt(6);
					if (random == 0) {
						getPlayerOwner().playerServerMessage(MessageType.QUEST, "@red@iban will save you....he'll save us all");
					} else if (random == 1) {
						getPlayerOwner().playerServerMessage(MessageType.QUEST, "@red@join us...join us...embrace the mysery");
					} else if (random == 2 && getPlayerOwner().getQuestStage(Quests.UNDERGROUND_PASS) >= 4) {
						getPlayerOwner().playerServerMessage(MessageType.QUEST, "@red@I see you adventurer...you can't hide");
					} else if (random == 3 && getPlayerOwner().getQuestStage(Quests.UNDERGROUND_PASS) >= 4) {
						getPlayerOwner().playerServerMessage(MessageType.QUEST, "@red@Come taste the pleasure of evil");
					} else if (random == 4 && getPlayerOwner().getQuestStage(Quests.UNDERGROUND_PASS) >= 4) {
						getPlayerOwner().playerServerMessage(MessageType.QUEST, "@red@Death is only the beginning");
					} else if (random == 5) {
						return null;
					}
					return null;
				});
			}
		};
	}

	public static GameNotifyEvent doLedge(final GameObject object, final Player p, final int damage) {
		return new GameNotifyEvent(p.getWorld(), p, 0, UndergroundPassObstaclesMap1.class.getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					p.setBusyTimer(1);
					p.message("you climb the ledge");
					boolean failLedge = false;
					int random = DataConversions.getRandom().nextInt(10);
					if (random == 5) {
						failLedge = true;
					} else {
						failLedge = false;
					}
					if (object != null && !failLedge) {
						if (object.getDirection() == 2 || object.getDirection() == 6) {
							if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) { // X
								if (object.getID() == 753) {
									p.message("and drop down to the cave floor");
									movePlayer(p, object.getX() - 2, object.getY());
								} else {
									p.message("and drop down to the cave floor");
									movePlayer(p, object.getX() - 1, object.getY());
								}
							} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) { // Y
								if (object.getID() == 753) {
									p.message("and drop down to the cave floor");
									movePlayer(p, object.getX() + 2, object.getY());
								} else {
									p.message("and drop down to the cave floor");
									movePlayer(p, object.getX() + 1, object.getY());
								}
							}
						}
						if (object.getDirection() == 4 || object.getDirection() == 0) {
							if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // X
								movePlayer(p, object.getX(), object.getY() + 1);
								p.message("and drop down to the cave floor");
							} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // Y
								movePlayer(p, object.getX(), object.getY() - 1);
							}
						}

						return null;
					} else {
						p.message("but you slip");
						p.damage(damage);

						return endOnNotify(playerDialogue(p, null, "aargh"));
					}
				});
			}
		};
	}

	public static GameNotifyEvent doRock(final GameObject object, final Player p, int damage, boolean eventMessage, int spikeLocation) {
		return new GameNotifyEvent(p.getWorld(), p, 0, UndergroundPassObstaclesMap1.class.getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					p.setBusyTimer(1);
					p.message("you climb onto the rock");
					boolean failRock = false;
					int random = DataConversions.getRandom().nextInt(5);
					if (random == 4) {
						failRock = true;
					} else {
						failRock = false;
					}
					if (object != null && !failRock) {
						if (object.getDirection() == 1 || object.getDirection() == 2 || object.getDirection() == 4
							|| object.getDirection() == 3) {
							if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) { // X
								movePlayer(p, object.getX() - 1, object.getY());
							} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) { // Y
								movePlayer(p, object.getX() + 1, object.getY());
							} else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // left
								// side
								if (object.getID() == 749) {
									movePlayer(p, object.getX(), object.getY() + 1);
								} else {
									movePlayer(p, object.getX() + 1, object.getY());
								}
							} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // right
								// side.
								if (object.getID() == 749) {
									movePlayer(p, object.getX(), object.getY() - 1);
								} else {
									movePlayer(p, object.getX() + 1, object.getY());
								}
							}
						}
						if (object.getDirection() == 6) {
							if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // left
								// side
								movePlayer(p, object.getX(), object.getY() + 1);
							} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // right
								// side.
								movePlayer(p, object.getX(), object.getY() - 1);
							} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
								movePlayer(p, object.getX() + 1, object.getY() + 1);
							} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
								movePlayer(p, object.getX(), object.getY() + 1);
							}
						}
						if (object.getDirection() == 0) {
							if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) { // X
								movePlayer(p, object.getX() - 1, object.getY());
							} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) { // Y
								movePlayer(p, object.getX() + 1, object.getY());
							} else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // left
								// side
								movePlayer(p, object.getX(), object.getY() + 1);
							} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // right
								// side.
								movePlayer(p, object.getX(), object.getY() - 1);
							}
						}
						if (object.getDirection() == 7) {
							if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) { // X
								movePlayer(p, object.getX() - 1, object.getY() - 1);
							} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) { // Y
								movePlayer(p, object.getX() + 1, object.getY());
							} else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // left
								// side
								movePlayer(p, object.getX(), object.getY() + 1);
							} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // right
								// side.
								movePlayer(p, object.getX() + 1, object.getY());
							}
						}
						p.message("and step down the other side");
					} else {
						p.message("but you slip");
						p.damage(damage);
						if (spikeLocation == 1) {
							p.teleport(743, 3475);
						} else if (spikeLocation == 2) {
							p.teleport(748, 3482);
						} else if (spikeLocation == 3) {
							p.teleport(738, 3483);
						} else if (spikeLocation == 4) {
							p.teleport(736, 3475);
						} else if (spikeLocation == 5) {
							p.teleport(730, 3478);
						}
						return invokeOnNotify(playerDialogue(p, null, "aargh"), 1);
					}

					return invoke(1, 0);
				});
				addState(1, () -> {
					if(eventMessage) {
						return endOnNotify(doMessages(p, DataConversions.random(3, 16)));
					}

					return null;
				});
			}
		};
	}
}
