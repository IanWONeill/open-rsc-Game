package com.openrsc.server.plugins.npcs.varrock;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.plugins.quests.free.ShieldOfArrav;
import java.util.concurrent.Callable;


public class ManPhoenix implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Npc man = Functions.getNearestNpc(p, NpcId.STRAVEN.id(), 20);
                    if (ShieldOfArrav.isBlackArmGang(p)) {
                        if (man != null) {
                            Functions.___npcTalk(p, man, "hey get away from there", "Black arm dog");
                            man.setChasing(p);
                        }
                    } else
                        if ((p.getQuestStage(Quests.HEROS_QUEST) >= 1) && ShieldOfArrav.isPhoenixGang(p)) {
                            if ((!Functions.hasItem(p, ItemId.MASTER_THIEF_ARMBAND.id())) && p.getCache().hasKey("armband")) {
                                Functions.___playerTalk(p, n, "I have lost my master thief armband");
                                Functions.___npcTalk(p, n, "You need to be more careful", "Ah well", "Have this spare");
                                Functions.addItem(p, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
                                return null;
                            } else
                                if (Functions.hasItem(p, ItemId.CANDLESTICK.id()) && (!p.getCache().hasKey("armband"))) {
                                    Functions.___playerTalk(p, n, "I have retrieved a candlestick");
                                    Functions.___npcTalk(p, n, "Hmm not a bad job", "Let's see it, make sure it's genuine");
                                    p.message("You hand Straven the candlestick");
                                    Functions.removeItem(p, ItemId.CANDLESTICK.id(), 1);
                                    Functions.___playerTalk(p, n, "So is this enough to get me a master thieves armband?");
                                    Functions.___npcTalk(p, n, "Hmm I dunno", "I suppose I'm in a generous mood today");
                                    p.message("Straven hands you a master thief armband");
                                    Functions.addItem(p, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
                                    p.getCache().store("armband", true);
                                    return null;
                                }

                            Functions.___playerTalk(p, n, "How would I go about getting a master thieves armband?");
                            Functions.___npcTalk(p, n, "Ooh tricky stuff, took me years to get that rank", "Well what some of aspiring thieves in our gang are working on right now", "Is to steal some very valuable rare candlesticks", "From scarface Pete - the pirate leader on Karamja", "His security is good enough and the target valuable enough", "That might be enough to get you the rank", "Go talk to our man Alfonse the waiter in the shrimp and parrot", "Use the secret word gherkin to show you're one of us");
                            p.getCache().store("pheonix_mission", true);
                            p.getCache().store("pheonix_alf", true);
                        } else
                            if ((((!p.getBank().hasItemId(ItemId.PHOENIX_GANG_WEAPON_KEY.id())) && (!Functions.hasItem(p, ItemId.PHOENIX_GANG_WEAPON_KEY.id()))) && ((p.getQuestStage(Quests.SHIELD_OF_ARRAV) >= 5) || (p.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0))) && ShieldOfArrav.isPhoenixGang(p)) {
                                Functions.___npcTalk(p, n, "Greetings fellow gang member");
                                Functions.___playerTalk(p, n, "I have lost the key you gave me");
                                Functions.___npcTalk(p, n, "You need to be more careful", "We don't want that key falling into the wrong hands", "Ah well", "Have this spare");
                                Functions.addItem(p, ItemId.PHOENIX_GANG_WEAPON_KEY.id(), 1);
                                Functions.___message(p, "Straven hands you a key");
                            } else
                                if (((p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 4) && ShieldOfArrav.isPhoenixGang(p)) || (p.getCache().hasKey("arrav_mission") && ((p.getCache().getInt("arrav_mission") & 2) == ShieldOfArrav.PHOENIX_MISSION))) {
                                    Functions.___npcTalk(p, n, "Hows your little mission going?");
                                    if (p.getInventory().hasItemId(ItemId.SCROLL.id())) {
                                        Functions.___playerTalk(p, n, "I have the intelligence report");
                                        Functions.___npcTalk(p, n, "Lets see it then");
                                        Functions.___message(p, "You hand over the report");
                                        Functions.removeItem(p, ItemId.SCROLL.id(), 1);
                                        Functions.___message(p, "The man reads the report");
                                        Functions.___npcTalk(p, n, "Yes this is very good", "Ok you can join the phoenix gang", "I am Straven, one of the gang leaders");
                                        Functions.___playerTalk(p, n, "Nice to meet you");
                                        Functions.___npcTalk(p, n, "Here is a key");
                                        Functions.___message(p, "Straven hands you a key");
                                        Functions.addItem(p, ItemId.PHOENIX_GANG_WEAPON_KEY.id(), 1);
                                        Functions.___npcTalk(p, n, "It will let you enter our weapon supply area", "Round the front of this building");
                                        p.updateQuestStage(Quests.SHIELD_OF_ARRAV, 5);
                                        if (p.getCache().hasKey("arrav_mission")) {
                                            p.getCache().remove("arrav_mission");
                                        }
                                        if (p.getCache().hasKey("spoken_tramp")) {
                                            p.getCache().remove("spoken_tramp");
                                        }
                                    } else {
                                        Functions.___playerTalk(p, n, "I haven't managed to find the report yet");
                                        Functions.___npcTalk(p, n, "You need to kill Jonny the beard", "Who should be in the blue moon inn");
                                    }
                                } else
                                    if (((p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5) || (p.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0)) || (p.getQuestStage(Quests.HEROS_QUEST) == (-1))) {
                                        memberOfPhoenixConversation(p, n);
                                    } else
                                        if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) <= 3) {
                                            defaultConverstation(p, n);
                                        }





                    return null;
                });
            }
        };
    }

    private void memberOfPhoenixConversation(final Player p, final Npc n) {
        Menu defaultMenu = new Menu();
        if (ShieldOfArrav.isPhoenixGang(p)) {
            Functions.___npcTalk(p, n, "Greetings fellow gang member");
            defaultMenu.addOption(new Option("I've heard you've got some cool treasures in this place") {
                public void action() {
                    Functions.___npcTalk(p, n, "Oh yeah, we've all stolen some stuff in our time", "The candlesticks down here", "Were quite a challenge to get out the palace");
                    Functions.___playerTalk(p, n, "And the shield of Arrav", "I heard you got that");
                    Functions.___npcTalk(p, n, "hmm", "That was a while ago", "We don't even have all the shield anymore", "About 5 years ago", "We had a massive fight in our gang", "The shield got broken in half during the fight", "Shortly after the fight", "Some gang members decided", "They didn't want to be part of our gang anymore", "So they split off to form their own gang", "The black arm gang", "On their way out", "They looted what treasures they could from us", "Which included one of the halves of the shield", "We've been rivals with the black arms ever since");
                }
            });
            defaultMenu.addOption(new Option("Any suggestions for where I can go thieving?") {
                public void action() {
                    Functions.___npcTalk(p, n, "You can always try the market", "Lots of opportunity there");
                }
            });
            defaultMenu.addOption(new Option("Where's the Blackarm gang hideout?") {
                public void action() {
                    Functions.___playerTalk(p, n, "I wanna go sabotage em");
                    Functions.___npcTalk(p, n, "That would be a little tricky", "Their security is pretty good", "Not as good as ours obviously", "But still good", "If you really want to go there", "It is in the alleyway", "To the west as you come in the south gate", "One of our operatives is often near the alley", "A red haired tramp", "He may be able to give you some ideas");
                    Functions.___playerTalk(p, n, "Thanks for the help");
                }
            });
            defaultMenu.showMenu(p);
        }
    }

    private void defaultConverstation(final Player p, final Npc n) {
        Menu defaultMenu = new Menu();
        Functions.___playerTalk(p, n, "What's through that door?");
        Functions.___npcTalk(p, n, "Heh you can't go in there", "Only authorised personnel of the VTAM corporation are allowed beyond this point");
        if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 3) {
            defaultMenu.addOption(new Option("I know who you are") {
                public void action() {
                    Functions.___npcTalk(p, n, "I see", "Carry on");
                    Functions.___playerTalk(p, n, "This is the headquarters of the Phoenix Gang", "The most powerful crime gang this city has seen");
                    Functions.___npcTalk(p, n, "And supposing we were this crime gang", "What would you want with us?");
                    new Menu().addOptions(new Option("I'd like to offer you my services") {
                        public void action() {
                            Functions.___npcTalk(p, n, "You mean you'd like to join the phoenix gang?", "Well the phoenix gang doesn't let people join just like that", "You can't be too careful, you understand", "Generally someone has to prove their loyalty before they can join");
                            Functions.___playerTalk(p, n, "How would I go about this?");
                            Functions.___npcTalk(p, n, "Let me think", "I have an idea", "A rival gang of ours", "Called the black arm gang", "Is meant to be meeting their contact from Port Sarim today", "In the blue moon inn", "By the south entrance to this city", "The name of the contact is Jonny the beard", "Kill him and bring back his intelligence report");
                            if (p.getCache().hasKey("arrav_mission")) {
                                p.getCache().set("arrav_mission", ShieldOfArrav.ANY_MISSION);
                            } else {
                                p.getCache().set("arrav_mission", ShieldOfArrav.PHOENIX_MISSION);
                            }
                            Functions.___playerTalk(p, n, "Ok, I'll get on it");
                        }
                    }, new Option("I want nothing. I was just making sure you were them") {
                        @Override
                        public void action() {
                            Functions.___npcTalk(p, n, "Well stop wasting my time");
                        }
                    }).showMenu(p);
                }
            });
        }
        defaultMenu.addOption(new Option("How do I get a job with the VTAM corporation?") {
            public void action() {
                Functions.___npcTalk(p, n, "Get a copy of the Varrock Herald", "If we have any positions right now", "They'll be advertised in there");
            }
        });
        defaultMenu.addOption(new Option("Why not?") {
            public void action() {
                Functions.___npcTalk(p, n, "Sorry that is classified information");
            }
        });
        defaultMenu.showMenu(p);
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.STRAVEN.id();
    }
}

