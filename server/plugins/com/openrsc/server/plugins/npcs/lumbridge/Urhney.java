package com.openrsc.server.plugins.npcs.lumbridge;


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
import java.util.concurrent.Callable;


public class Urhney implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Menu defaultMenu = new Menu();
                    Functions.___npcTalk(p, n, "Go away, I'm meditating");
                    if ((p.getQuestStage(Quests.THE_RESTLESS_GHOST) == 1) && (!Functions.hasItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id()))) {
                        defaultMenu.addOption(new Option("Father Aereck sent me to talk to you") {
                            @Override
                            public void action() {
                                Functions.___npcTalk(p, n, "I suppose I'd better talk to you then", "What problems has he got himself into this time?");
                                new Menu().addOptions(new Option("He's got a ghost haunting his graveyard") {
                                    @Override
                                    public void action() {
                                        Functions.___npcTalk(p, n, "Oh the silly fool", "I leave town for just five months", "and already he can't manage", "Sigh", "Well I can't go back and exorcise it", "I vowed not to leave this place", "Until I had done a full two years of prayer and meditation", "Tell you what I can do though", "Take this amulet");
                                        Functions.___message(p, "Father Urhney hands you an amulet");
                                        Functions.addItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1);// AMULET OF GHOST SPEAK.

                                        Functions.___npcTalk(p, n, "It is an amulet of Ghostspeak", "So called because when you wear it you can speak to ghosts", "A lot of ghosts are doomed to be ghosts", "Because they have left some task uncompleted", "Maybe if you know what this task is", "You can get rid of the ghost", "I'm not making any guarantees mind you", "But it is the best I can do right now");
                                        Functions.___playerTalk(p, n, "Thank you, I'll give it a try");
                                        p.updateQuestStage(Quests.THE_RESTLESS_GHOST, 2);
                                    }
                                }, new Option("You mean he gets himself into lots of problems?") {
                                    @Override
                                    public void action() {
                                        Functions.___npcTalk(p, n, "Yeah. For example when we were trainee priests", "He kept on getting stuck up bell ropes", "Anyway I don't have time for chitchat", "What's his problem this time?");
                                        Functions.___playerTalk(p, n, "He's got a ghost haunting his graveyard");
                                        Functions.___npcTalk(p, n, "Oh the silly fool", "I leave town for just five months", "and already he can't manage", "Sigh", "Well I can't go back and exorcise it", "I vowed not to leave this place", "Until I had done a full two years of prayer and meditation", "Tell you what I can do though", "Take this amulet");
                                        Functions.___message(p, "Father Urhney hands you an amulet");
                                        Functions.addItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1);// AMULET OF GHOST SPEAK.

                                        Functions.___npcTalk(p, n, "It is an amulet of Ghostspeak", "So called because when you wear it you can speak to ghosts", "A lot of ghosts are doomed to be ghosts", "Because they have left some task uncompleted", "Maybe if you know what this task is", "You can get rid of the ghost", "I'm not making any guarantees mind you", "But it is the best I can do right now");
                                        Functions.___playerTalk(p, n, "Thank you, I'll give it a try");
                                        p.updateQuestStage(Quests.THE_RESTLESS_GHOST, 2);
                                    }
                                }).showMenu(p);
                            }
                        });
                    }
                    if ((p.getQuestStage(Quests.THE_RESTLESS_GHOST) >= 2) && (!Functions.hasItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id()))) {
                        defaultMenu.addOption(new Option("I've lost the amulet") {
                            @Override
                            public void action() {
                                Functions.___message(p, "Father Urhney sighs");
                                Functions.___npcTalk(p, n, "How careless can you get", "Those things aren't easy to come by you know", "It's a good job I've got a spare");
                                Functions.addItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1);
                                Functions.___message(p, "Father Urhney hands you an amulet");
                                Functions.___npcTalk(p, n, "Be more careful this time");
                                Functions.___playerTalk(p, n, "Ok I'll try to be");
                            }
                        });
                    }
                    defaultMenu.addOption(new Option("Well that's friendly") {
                        @Override
                        public void action() {
                            Functions.___npcTalk(p, n, "I said go away!");
                            Functions.___playerTalk(p, n, "Ok, ok");
                        }
                    });
                    defaultMenu.addOption(new Option("I've come to repossess your house") {
                        @Override
                        public void action() {
                            Functions.___npcTalk(p, n, "Under what grounds?");
                            new Menu().addOptions(new Option("Repeated failure on mortgage payments") {
                                @Override
                                public void action() {
                                    Functions.___npcTalk(p, n, "I don't have a mortgage", "I built this house myself");
                                    Functions.___playerTalk(p, n, "Sorry I must have got the wrong address", "All the houses look the same around here");
                                }
                            }, new Option("I don't know, I just wanted this house") {
                                @Override
                                public void action() {
                                    Functions.___npcTalk(p, n, "Oh go away and stop wasting my time");
                                }
                            }).showMenu(p);
                        }
                    });
                    defaultMenu.showMenu(p);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.URHNEY.id();
    }
}

