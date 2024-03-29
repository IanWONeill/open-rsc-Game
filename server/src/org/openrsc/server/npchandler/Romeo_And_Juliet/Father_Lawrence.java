/**
* Generated By NPCScript :: A scripting engine created for openrsc by Zilent
*/
package org.openrsc.server.npchandler.Romeo_And_Juliet;

import org.openrsc.server.Config;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.Quests;
import org.openrsc.server.npchandler.NpcHandler;

public class Father_Lawrence implements NpcHandler {
	
	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		final Quest q = owner.getQuest(Quests.ROMEO_AND_JULIET);
		if(q != null) {
			if(q.finished()) { //Quest Finished
				questFinished(npc, owner);
			} else { //Quest Underway
				switch(q.getStage()) {
					case 0:
					case 1:
						questNotStarted(npc, owner);
						break;
					case 2:
						romeoSentMe(npc, owner);
						break;
					case 3:
						lookingForApothecary(npc, owner);
						break;
					case 4:
						foundApothecary(npc, owner);
						break;
					case 5:
						questFinished(npc, owner);
						break;
				}
			}
		} else { //Quest Not Started
			questNotStarted(npc, owner);
		}
	}
	
	private final void foundApothecary(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Did you find the Apothecary?"}, true) {
			public void finished() {
				if(owner.getInventory().countId(57) > 0) {
					World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I have the potion"}) {
						public void finished() {
							World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Good work. Get the potion to Juliet", "I will tell Romeo to be ready"}) {
								public void finished() {
									owner.setBusy(false);
									npc.unblock();
								}
							});
						}
					});
				} else {
					if(owner.getInventory().countId(55) > 0) {
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I am on my way back to him with the ingredients"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Good work. Get the potion to Juliet when you have it", "I will tell Romeo to be ready"}) {
									public void finished() {
										owner.setBusy(false);
										npc.unblock();
									}
								});
							}
						});
					} else {
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Yes, i must find some cavada berries"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well, take care. They are the poisonous touch", "You will need gloves"}) {
									public void finished() {
										owner.setBusy(false);
										npc.unblock();
									}
								});
							}
						});
					}
				}
			}
		});
	}
	
	private final void lookingForApothecary(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ah, have you found the Apothecary yet?", "Remember, Cadava potion, for Father Lawrence"}, true) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private final void romeoSentMe(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Romeo sent me.  He says you can help"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ah Romeo, yes.  A fine lad, but a little bit confused"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Juliet must be rescued from her father's control"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I know just the thing.  A potion to make her appear dead", "Then Romeo can collect her from the crypt", "Go to the Apothecary, tell him I sent you", "You need some Cadava Potion"}) {
									public void finished() {
										owner.incQuestCompletionStage(Quests.ROMEO_AND_JULIET);
										owner.setBusy(false);
										npc.unblock();
									}
								});
							}
						});
					}
				});
			}
		});
	}
	
	private final void questFinished(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Oh to be a father in the times of whiskey", "I sing and I drink and I wake up in the gutters", "Top of the morning to you", "To err is human, to forgive, quite difficult"}, true) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private final void questNotStarted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello adventurer, do you seek a quest?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options2 = {"I am always looking for a quest", "No, I prefer just to kill things", "Can you recommend a good bar?"};
						owner.setBusy(false);
						owner.sendMenu(options2);
						owner.setMenuHandler(new MenuHandler(options2) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well, I see poor Romeo wandering around the square", "I think he may need help", "I was helping him and Juliet to meet, but it became impossible", "I am sure he can use some help"}) {
											public void finished() {
												owner.setBusy(false);
												npc.unblock();
											}
										});
										break;
									case 1:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"That's a fine career in these lands", "There is more that needs killing every day"}) {
											public void finished() {
												owner.setBusy(false);
												npc.unblock();
											}
										});
										break;
									case 2:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Drinking will be the death of you", "But the Blue Moon in the city is cheap enough", "And providing you buy one drink an hour, they let you stay all night"}) {
											public void finished() {
												owner.setBusy(false);
												npc.unblock();
											}
										});
										break;
								}
							}
						});
					}
				});
			}
		});		
	}
}