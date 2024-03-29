/**
* Generated By NPCScript :: A scripting engine created for openrsc by Zilent
*/

//scripted by Mr. Zain

package org.openrsc.server.npchandler.Monks_Friend;
import org.openrsc.server.Config;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.model.*;
import org.openrsc.server.npchandler.NpcHandler;
import org.openrsc.server.util.DataConversions;



public class Brother_Omad implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest q = owner.getQuest(Quests.MONKS_FRIEND);
		if(q != null) {
			if(q.finished()) {
				finished(npc, owner);
			} else {
				switch(q.getStage()) {
					case 0:
						noQuestStarted(npc, owner);
						break;
					case 1:
						questPart1Stage1(npc, owner);
						break;
					case 2:
						questPart2(npc, owner);
						break;
					case 3:
						questPart2Stage1(npc, owner);
						break;
					case 4:
						questPart2Stage2(npc, owner);
						break;
					case 5:
						questPart2Stage3(npc, owner);
						break;
					case 6:
						questPart2Stage4(npc, owner);
						break;
				}
			}
		} else {
			noQuestStarted(npc, owner);
		}
	}
	

	private void noQuestStarted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hello there"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"...yawn...oh, hello...yawn..", "I'm sorry, I'm just so tired..", "I haven't slept in a week", "It's driving me mad"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
							public void action() {
								final String[] options107 = {"Why can't you sleep, what's wrong?", "Sorry, I'm too busy to hear your problems"};
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
												questInfo(npc, owner);
												break;
											case 1:
												owner.setBusy(false);
												npc.unblock();
												break;
										}
									}
								});
							}
						});
					}
				});
			}
		});
	}
	
	
	private void questInfo(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"It's the brother Androe's son", "with his constant waaaaaah..waaaaaaaaah", "Androe said it's natural, but it's just annoying"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I suppose that's what kids do"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"He was fine, up until last week", "thieves broke in", "They stole his favourite sleeping blanket", "Now he won't rest until it's returned", "..and that means neither can I!"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
									public void action() {
										final String[] options107 = {"Can I help at all?", "I'm sorry to hear that, I hope you find his blanket"};
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
														questPart1Accepted(npc, owner);
														break;
													case 1:
														owner.setBusy(false);
														npc.unblock();
														break;
												}
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
	}
	
	private void questPart1Accepted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Please do, we are peaceful men", "But could you recover the blanket from the thieves?"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Where are they?"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"They hide in a secret cave in the forest", "..it's hidden under a ring of stones", "Please bring back the blanket"}) {
							public void finished() {
								owner.addQuest(Quests.MONKS_FRIEND, 1);		
								owner.incQuestCompletionStage(Quests.MONKS_FRIEND);
								owner.setBusy(false);
								npc.unblock();
							}
						});
					}
				});
			}
		});
	}

	
	
	private void questPart1Stage1(final Npc npc, final Player owner) {
		if(owner.getInventory().countId(716) < 1) {
			World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hello"}, true) {
				public void finished() {
					World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"...yawn...oh, hello again...yawn..", "...Please tell me you have the blanket"}) {
						public void finished() {
							World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I haven't found it yet", "I'll keep looking"}) {
								public void finished() {
									World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Remember, they hide in a secret cave in the forest", "..it's hidden under a ring of stones"}) {
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
			});
		}
		else
		if(owner.getInventory().countId(716) > 0) {
			World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hello"}, true) {
				public void finished() {
					World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"...yawn...oh, hello again...yawn..", "...Please tell me you have the blanket"}) {
						public void finished() {
							World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Yes I returned it from the clutches of the evil thieves"}) {
								public void finished() {
									owner.sendMessage("You give Brother Omad the blanket");
									owner.getInventory().remove(716, 1);
									owner.sendInventory();
									World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Really, that's excellent, well done", "that should cheer up Androe's son", "and maybe I will be able to get some rest", "..yawn.. I'm off to bed, farewell brave traveller"}) {
										public void finished() {
											owner.incQuestCompletionStage(Quests.MONKS_FRIEND);
											owner.sendMessage("You have completed part 1 of the monk's friend quest!");
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
	}
	
	
	private void questPart2(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hello, how are you"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Much better now I'm sleeping well", "Now I can organise the party"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"what party?"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Androe's son's birthday party", "he's going to be one year old"}) {
									public void finished() {
										World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"That's sweet"}) {
											public void finished() {
												World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"It's also a great excuse for a drink", "Now we just need brother Cedric to return", "with the wine"}) {
													public void finished() {
														World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
															public void action() {
																final String[] options107 = {"Who's brother cedric?", "Enjoy it, I'll see you soon"};
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
																				questPart2Info(npc, owner);
																				break;
																			case 1:
																				owner.setBusy(false);
																				npc.unblock();
																				break;
																		}
																	}
																});
															}
														});
													}
												});
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
	}
		
		
	private void questPart2Info(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Cedric lives here too", "We sent him out three days ago", "to collect wine, but here didn't return", "he most probably got drunk", "and lost in the forest", "I don't suppose you could look for him?", "then we can really party"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"I've got no time for that, sorry", "Where should I look?", "Can I come?"};
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
										owner.setBusy(false);
										npc.unblock();
										break;
									case 1:
										questPart2Accepted(npc, owner);
										break;
									case 2:
										needCedric(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	
	private void questPart2Accepted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Oh, he won't be far", "probably out in the forest"}) {
			public void finished() {
			owner.incQuestCompletionStage(Quests.MONKS_FRIEND);
			owner.setBusy(false);
			npc.unblock();
			}
		});
	}
	
	private void questPart2Stage1(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Where should I look for brother Cedric?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Oh, he won't be far", "probably out in the forest"}) {
					public void finished() {
						owner.setBusy(false);
						npc.unblock();
					}	
				});		
			}
		});
	}
	
	private void questPart2Stage2(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Where should I look for brother Cedric?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Oh, he won't be far", "probably out in the forest"}) {
					public void finished() {
						owner.setBusy(false);
						npc.unblock();
					}	
				});		
			}
		});
	}
	
	private void questPart2Stage3(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Where should I look for brother Cedric?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Oh, he won't be far", "probably out in the forest"}) {
					public void finished() {
						owner.setBusy(false);
						npc.unblock();
					}	
				});		
			}
		});
	}
	
	private void questPart2Stage4(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hi Omad, Brother Cedric is on his way"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"good, good, good", "now we can party"}) {
					public void finished() {
						owner.finishQuest(Quests.MONKS_FRIEND);
						owner.incQuestExp(Skills.WOODCUT, 2000);
						owner.sendStat(8);
						owner.getInventory().add(42, 8);
						owner.sendInventory();
						owner.sendMessage("@gre@ You have completed the Monk's friend quest!");
						owner.sendMessage("@gre@ You have been awarded 1 quest point!");
						owner.setBusy(false);
						npc.unblock();
						Logger.log(new eventLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), DataConversions.getTimeStamp(), "<strong>" + owner.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Monk's Friend</span> quest!"));
					}	
				});		
			}
		});
	}
	
	private void needCedric(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Sure, But we need Cedric to bring the wine", "to have a proper party"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"I've got no time for that, sorry", "Where should I look?"};
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
										owner.setBusy(false);
										npc.unblock();
										break;
									case 1:
										questPart2Accepted(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}

	
	private void finished(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello again", "thank you for your help"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"No problem"}) {
					public void finished() {
					owner.setBusy(false);
					npc.unblock();
					}
				});
			}
		});
	}
	
}