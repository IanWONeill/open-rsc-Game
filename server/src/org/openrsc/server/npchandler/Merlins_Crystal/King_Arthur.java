/**
* Generated By NPCScript :: A scripting engine created for openrsc by Zilent
*/
package org.openrsc.server.npchandler.Merlins_Crystal;
import org.openrsc.server.Config;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.Quests;
import org.openrsc.server.model.World;
import org.openrsc.server.npchandler.NpcHandler;



public class King_Arthur implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest q = owner.getQuest(Quests.MERLINS_CRYSTAL);
		if(q != null) {
			if(q.finished()) {
				finished(npc, owner);
			} else {
				switch(q.getStage()) {
					case 0:
						noQuestStarted(npc, owner);
						break;
					case 1:
						questStage1(npc, owner);
						break;
					case 2:
						questStage1(npc, owner);
						break;
					case 3:
						questStage1(npc, owner);
						break;
					case 4:
						questStage4(npc, owner);
						break;
					case 5:
						questStage5(npc, owner);
						break;
					case 6:
						questStage5(npc, owner);
						break;
					case 7:
						questStage7(npc, owner);
						break;
					default:
						noQuestStarted(npc, owner);
				}
			}
		} else {
			noQuestStarted(npc, owner);
		}
	}
	

	private void noQuestStarted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello there", "What brings you to my castle?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"Who are you?", "I'm in search of a quest", "Nothing goodbye"};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										whoAreYou(npc, owner);
										break;
									case 1:
										quest(npc, owner);
										break;
									case 2:
										goodBye(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	
	private void whoAreYou(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I am king arthur", "leader of the knights of camelot", "now what do you want?"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"Could I join your order?", "Nothing, goodbye"};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										quest(npc, owner);
										break;
									case 1:
										goodBye(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	
	
	private void quest(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Actually, now that you mention it", "our wizard merlin has been put in a giant crystal", "and we can't figure how to get him out", "Could you help us?"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"Of course sire!", "Nah that sounds boring, goodbye"};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										questAccepted(npc, owner);
										break;
									case 1:
										goodBye(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	
	private void questStage1(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Have you found out anything about merlin's accident?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"No not yet"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok well come back to me when you have"}) {
							public void finished() {
							owner.setBusy(false);
							npc.unblock();
							}
						});
					}
				});
			}
		});
	}
	
	
	private void questStage4(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Have you found out anything about merlin's accident?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Yes, I found out how to release merlin!", "But I need a lit black candle & bat bones to lift the curse", "then i need to go to the summoning circle", "Just north-east of camelot castle"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Great work!", "Please release him as soon as you get those items"}) {
							public void finished() {
							owner.setBusy(false);
							npc.unblock();
							}
						});
					}
				});
			}
		});
	}
	
	
	private void questStage5(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I released the curse of the crystal", "but merlin is still trapped inside"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"hmm..", "seems like we will have to smash it", "It also seems to have magical properties protecting it", "You will need the sword excalibur to shatter it", "Find the lady of the lake south of Taverly"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Right away sire!"}) {
							public void finished() {
							owner.setBusy(false);
							npc.unblock();
							}
						});
					}
				});
			}
		});
	}
	
	
	private void questStage7(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I have excalibur!"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"This is good news!", "Go upstairs and shatter that crystal right away!"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"yes sire!"}) {
							public void finished() {
							owner.setBusy(false);
							npc.unblock();
							}
						});
					}
				});
			}
		});
	}
	
	
	private void questAccepted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Excellent!", "You should start with asking our members", "about what happened to merlin", "that should lead towards a solution to get him out"}) {
			public void finished() {
				owner.addQuest(Quests.MERLINS_CRYSTAL, 6);
				owner.incQuestCompletionStage(Quests.MERLINS_CRYSTAL);
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void goodBye(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Farewell"}) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void finished(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Thanks again for your help adventurer"}, true) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	
}