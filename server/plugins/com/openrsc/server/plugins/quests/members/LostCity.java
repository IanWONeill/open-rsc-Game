package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openrsc.server.plugins.Functions.*;

/**
 * Rewritten in Java.
 *
 * @author n0m
 */
public class LostCity implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener, PlayerAttackNpcExecutiveListener,
	PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener,
	InvUseOnItemListener, InvUseOnItemExecutiveListener,
	WallObjectActionListener, WallObjectActionExecutiveListener {
	private static final Logger LOGGER = LogManager.getLogger(LostCity.class);
	
	/* Objects */
	final int LEPROCHAUN_TREE = 237, ENTRANA_LADDER = 244, DRAMEN_TREE = 245,
		MAGIC_DOOR = 65, ZANARIS_DOOR = 66;

	/**
	 * Quest stage 1: Talked to adventurer Quest stage 2: Talked to leprechaun
	 * Quest stage 3: Chopped the spirit tree Quest stage 4: Defeated the spirit
	 * tree.
	 */
	@Override
	public int getQuestId() {
		return Quests.LOST_CITY;
	}

	@Override
	public String getQuestName() {
		return "Lost City (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.LOST_CITY), true);
		player.message("@gre@You haved gained 3 quest points!");
		player.message("Well done you have completed the Lost City of Zanaris quest");
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		return inArray(obj.getID(), LEPROCHAUN_TREE, ENTRANA_LADDER,
			DRAMEN_TREE);
	}

	@Override
	public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
		final QuestInterface quest = this;
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					switch (obj.getID()) {
						case 244:
							Npc monk = getNearestNpc(p, NpcId.MONK_OF_ENTRANA_ENTRANA.id(), 10);
							if (monk != null)
								monk.initializeTalkScript(p);
							break;
						case 237:
							if (atQuestStage(p, quest, 0)) {
								p.message("There is nothing in this tree");
							} else if (getQuestStage(p, quest) >= 1
								&& getQuestStage(p, quest) <= 3) {
								Npc leprechaun = getNearestNpc(p, NpcId.LEPRECHAUN.id(), 15);
								if (leprechaun != null) {
									p.message("There is nothing in this tree");
								} else {
									p.message("A Leprechaun jumps down from the tree and runs off");
									final Npc lepr = spawnNpc(p.getWorld(), NpcId.LEPRECHAUN.id(), 172, 661, 60000 * 3);
									p.setBusyTimer(3);
									lepr.walk(173, 661);
									try {
										p.getWorld().getServer().getGameEventHandler().add(new SingleEvent(p.getWorld(),null, 600, "Lost City Leprechaun", true) {
											@Override
											public void action() {
												lepr.walk(177, 661 + DataConversions.random(0, 10) - 5);
											}
										});
									} catch (Exception e) {
										LOGGER.catching(e);
									}

								}
							} else {
								p.message("There is nothing in this tree");
							}
							break;
						case 245:
							if (atQuestStages(p, quest, 4, 3, 2, -1)) {
								if (getCurrentLevel(p, Skills.WOODCUT) < 36) {
									message(p,
										"You are not a high enough woodcutting level to chop down this tree",
										"You need a woodcutting level of 36");
									return null;
								}
								if (getWoodcutAxe(p) == -1) {
									p.message("You need an axe to chop down this tree");
									return null;
								}

								/*
								 * New method I made, quite useful, no need for OR checks
								 * anymore
								 */
								if (atQuestStages(p, quest, 4, -1)) {
									message(p, "You cut a branch from the Dramen tree");
									addItem(p, ItemId.DRAMEN_BRANCH.id(), 1);
									return null;
								}
								if (isNpcNearby(p, NpcId.TREE_SPIRIT.id())) {
									Npc spawnedTreeSpirit = getNearestNpc(p, NpcId.TREE_SPIRIT.id(), 15);
									/*
									 * Check if the spawned tree spirit contains spawnedFor
									 * attribute
									 */
									if (spawnedTreeSpirit != null) {
										if (spawnedTreeSpirit.getAttribute("spawnedFor") != null) {
											/* Check if the spawned tree spirit was spawned for us */
											if (spawnedTreeSpirit.getAttribute("spawnedFor")
												.equals(p)) {
												npcTalk(p, spawnedTreeSpirit, "Stop",
													"I am the spirit of the Dramen Tree",
													"You must come through me before touching that tree");
												return null;
											}
										}
									}
								}
								Npc treeSpirit = spawnNpc(NpcId.TREE_SPIRIT.id(), p.getX() + 1, p.getY() + 1,
									300000, p);
								if (treeSpirit == null) {
									return null;
								}
								sleep(2000);
								npcTalk(p, treeSpirit, "Stop",
									"I am the spirit of the Dramen Tree",
									"You must come through me before touching that tree");
								if (atQuestStages(p, quest, 2)) {
									setQuestStage(p, quest, 3);
								}
							}
							break;
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ADVENTURER_ARCHER.id(), NpcId.ADVENTURER_CLERIC.id(), NpcId.ADVENTURER_WARRIOR.id(),
				NpcId.ADVENTURER_WIZARD.id(), NpcId.LEPRECHAUN.id(), NpcId.MONK_OF_ENTRANA_ENTRANA.id()}, n.getID());
	}

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		final QuestInterface quest = this;
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (n.getID() == NpcId.LEPRECHAUN.id()) {
						if (atQuestStage(p, quest, 0)) {
							npcTalk(p, n, "Ay you big elephant", "You have caught me",
								"What would you be wanting with old Shamus then?");
							playerTalk(p, n, "I'm not sure");
							npcTalk(p, n, "Well you'll have to catch me again when you are");
							p.message("The leprechaun magically disapeers");
							n.remove();
						} else if (atQuestStage(p, quest, 1)) {
							npcTalk(p, n, "Ay you big elephant", "You have caught me",
								"What would you be wanting with old Shamus then?");
							playerTalk(p, n, "I want to find Zanaris");
							npcTalk(p, n, "Zanaris?",
								"You need to go in the funny little shed",
								"in the middle of the swamp");
							playerTalk(p, n, "Oh I thought Zanaris was a city");
							npcTalk(p, n, "It is");
							int menu = showMenu(p, n, false, //do not send over
								"How does it fit in a shed then?",
								"I've been in that shed, I didn't see a city");
							if (menu == 0) {
								playerTalk(p, n, "How does it fit in a shed then?");
								npcTalk(p, n, "The city isn't in the shed",
									"The shed is a portal to Zanaris");
								playerTalk(p, n, "So I just walk into the shed and end up in Zanaris?");
							} else if (menu == 1) {
								playerTalk(p, n, "I've been in that shed",
									"I didn't see a city");
							}
							npcTalk(p, n, "Oh didn't I say?",
								"You need to be carrying a Dramenwood staff",
								"Otherwise you do just end up in a shed");
							playerTalk(p, n, "So where would I get a staff?");
							npcTalk(p, n, "Dramenwood staffs are crafted from branches",
								"These staffs are cut from the Dramen tree",
								"located somewhere in a cave on the island of Entrana",
								"I believe the monks of Entrana have recetnly",
								"Started running a ship from port sarim to Entrana");
							setQuestStage(p, quest, 2);
							p.message("The leprechaun magically disapeers");
							n.remove();
						} else if (atQuestStages(p, quest, 4, 3, 2, -1)) {
							npcTalk(p, n, "Ay you big elephant",
								"You have caught me",
								"What would you be wanting with old Shamus then?");
							int menu = showMenu(p, n, "I'm not sure", "How do I get to Zanaris again?");
							if (menu == 0) {
								npcTalk(p, n, "I dunno, what stupid people",
									"Who go to all the trouble to catch leprechaun's",
									"When they don't even know what they want");
								p.message("The leprechaun magically disapeers");
								n.remove();
							} else if (menu == 1) {
								npcTalk(p, n, "You need to enter the shed in the middle of the swamp",
									"While holding a dramenwood staff",
									"Made from a branch",
									"Cut from the dramen tree on the island of Entrana");
								p.message("The leprechaun magically disapeers");
								n.remove();
							}
						}
					}
					else if (DataConversions.inArray(new int[] {NpcId.ADVENTURER_ARCHER.id(), NpcId.ADVENTURER_CLERIC.id(),
						NpcId.ADVENTURER_WARRIOR.id(), NpcId.ADVENTURER_WIZARD.id()}, n.getID())) {
						if (atQuestStage(p, quest, 0)) {
							npcTalk(p, n, "hello traveller");
							int option = showMenu(p, n, false, //do not send over
								"What are you camped out here for?",
								"Do you know any good adventures I can go on?");
							if (option == 0) {
								playerTalk(p, n, "What are you camped here for?");
								npcTalk(p, n, "We're looking for Zanaris");
								int sub_option = showMenu(p, n, "Who's Zanaris?",
									"what's Zanaris?",
									"What makes you think it's out here");
								if (sub_option == 0 || sub_option == 2) {
									if (sub_option == 0)
										npcTalk(p, n, "hehe Zanaris isn't a person",
											"It's a magical hidden city");
									else
										npcTalk(p, n, "Don't you know of the legends?",
											"of the magical city, hidden in the swamp");
									ZANARIS_MENU(p, n);
								} else if (sub_option == 1) {
									npcTalk(p, n,
										"I don't think we want other people competing with us to find it");
									int next_option = showMenu(p, n, "Please tell me",
										"Oh well never mind");
									if (next_option == 0) {
										npcTalk(p, n, "No");
									}
								}
							} else if (option == 1) {
								playerTalk(p, n, "Do you know any good adventures I can go on");
								npcTalk(p, n, "Well we're on an adventure now",
									"Mind you this is our adventure",
									"We don't want to share it - find your own");
								int insist = showMenu(p, n, "Please tell me",
									"I don't think you've found a good adventure at all");
								if (insist == 0) {
									npcTalk(p, n, "No");
								} else if (insist == 1) {
									npcTalk(p, n, "We're on one of the greatest adventures I'll have you know",
										"Searching for Zanaris isn't a walk in the park");
									int sub_option = showMenu(p, n, "Who's Zanaris?",
										"what's Zanaris?",
										"What makes you think it's out here");
									if (sub_option == 0 || sub_option == 2) {
										if (sub_option == 0)
											npcTalk(p, n, "hehe Zanaris isn't a person",
												"It's a magical hidden city");
										else
											npcTalk(p, n, "Don't you know of the legends?",
												"of the magical city, hidden in the swamp");
										ZANARIS_MENU(p, n);
									} else if (sub_option == 1) {
										npcTalk(p, n,
											"I don't think we want other people competing with us to find it");
										int next_option = showMenu(p, n, "Please tell me",
											"Oh well never mind");
										if (next_option == 0) {
											npcTalk(p, n, "No");
										}
									}
								}
							}
						} else if (atQuestStage(p, quest, 1)) {
							playerTalk(
								p,
								n,
								"So let me get this straight",
								"I need to search the trees near here for a leprechaun?",
								"And he will tell me where Zanaris is?");
							npcTalk(p, n, "That is what the legends and rumours are,yes");
						} else if (atQuestStages(p, quest, 4, 3, 2, -1)) {
							playerTalk(p, n, "thankyou for your information",
								"It has helped me a lot in my quest to find Zanaris");
							npcTalk(p, n, "So what have you found out?",
								"Where is Zanaris?");
							playerTalk(p, n, "I think I will keep that to myself");
						}
					} else if (n.getID() == NpcId.MONK_OF_ENTRANA_ENTRANA.id()) {
						npcTalk(p, n, "Be careful going in there",
							"You are unarmed, and there is much evilness lurking down there",
							"The evilness seems to block off our contact with our gods",
							"Our prayers seem to have less effect down there",
							"Oh also you won't be able to come back this way",
							"This ladder only goes one way",
							"The only way out is a portal which leads deep into the wilderness");
						int option = showMenu(p, n,
							"I don't think I'm strong enough to enter then",
							"Well that is a risk I will have to take");
						if (option == 1) {
							p.message("You climb down the ladder");
							sleep(1000);
							movePlayer(p, 427, 3380, true);
							/* What is the point of this? */
							if (getCurrentLevel(p, Skills.PRAYER) <= 3)
								setCurrentLevel(p, Skills.PRAYER, 1);
							else if (getCurrentLevel(p, Skills.PRAYER) <= 39)
								setCurrentLevel(p, Skills.PRAYER, 2);
							else
								setCurrentLevel(p, Skills.PRAYER, 3);
						}
					}

					return null;
				});
			}
		};
	}

	public void ZANARIS_MENU(Player p, Npc n) {
		int next_option = showMenu(p, n,
			"If it's hidden how are you planning to find it",
			"There's no such thing");
		if (next_option == 0) {
			npcTalk(p, n, "Well we don't want to tell others that",
				"We want all the glory of finding it for ourselves");
			int after_option = showMenu(p, n, false, //do not send over
				"please tell me",
				"looks like you don't know either if you're sitting around here");
			if (after_option == 0) {
				playerTalk(p, n, "Please tell me");
				npcTalk(p, n, "No");
			} else if (after_option == 1) {
				playerTalk(p, n, "looks like you don't know either if you're sitting around here");
				npcTalk(p,
					n,
					"Of course we know",
					"We haven't worked out which tree the stupid leprechaun is in yet",
					"Oops I didn't mean to tell you that");
				playerTalk(p, n, "So a Leprechaun knows where Zanaris is?");
				npcTalk(p, n, "Eerm", "yes");
				playerTalk(p, n, "And he's in a tree somewhere around here",
					"thankyou very much");
				setQuestStage(p, this, 1);
			}
		} else if (next_option == 1) {
			npcTalk(p, n, "Well when we find which tree the leprechaun is in",
				"You can eat those words",
				"Oops I didn't mean to tell you that");
			playerTalk(p, n, "So a Leprechaun knows where Zanaris is?");
			npcTalk(p, n, "Eerm", "yes");
			playerTalk(p, n, "And he's in a tree somewhere around here",
				"thankyou very much");
			setQuestStage(p, this, 1);
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.TREE_SPIRIT.id()) {
			if (n.getAttribute("spawnedFor", null) != null) {
				if (!n.getAttribute("spawnedFor").equals(p)) {
					p.message("That npc is not after you.");
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.TREE_SPIRIT.id();
	}

	@Override
	public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
		final QuestInterface quest = this;
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (atQuestStage(p, quest, 3)) {
						kill(n, p);
						setQuestStage(p, quest, 4);
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		return Functions.compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.DRAMEN_BRANCH.id());
	}

	@Override
	public GameStateEvent onInvUseOnItem(Player p, Item item1, Item item2) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (hasItem(p, ItemId.DRAMEN_BRANCH.id(), 1)) {
						if (getCurrentLevel(p, Skills.CRAFTING) < 31) {
							message(p,
								"You are not a high enough crafting level to craft this staff",
								"You need a crafting level of 31");
							return null;
						}
						removeItem(p, ItemId.DRAMEN_BRANCH.id(), 1);
						message(p, "you carve the branch into a staff");
						addItem(p, ItemId.DRAMEN_STAFF.id(), 1);
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
										 Player player) {
		return inArray(obj.getID(), MAGIC_DOOR, ZANARIS_DOOR);
	}

	@Override
	public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
		final QuestInterface quest = this;
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (obj.getID() == MAGIC_DOOR) {
						p.teleport(109, 245, true);
						sleep(500);
						p.message("you go through the door and find yourself somewhere else");
					} else if (obj.getID() == ZANARIS_DOOR) {
						if (isWielding(p, ItemId.DRAMEN_STAFF.id()) && atQuestStages(p, quest, 4, -1)) {
							p.setBusy(true);
							message(p, "The world starts to shimmer",
								"You find yourself in different surroundings");
							if (getQuestStage(p, quest) != -1) {
								movePlayer(p, 126, 3518, true);
								completeQuest(p, quest);
							} else {
								movePlayer(p, 126, 3518, true);
							}
							p.setBusy(false);
						} else {
							doDoor(obj, p);
							p.message("you go through the door and find yourself in a shed.");
						}
					}

					return null;
				});
			}
		};
	}
}
