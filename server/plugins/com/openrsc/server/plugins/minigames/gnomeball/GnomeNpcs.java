package com.openrsc.server.plugins.minigames.gnomeball;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.event.rsc.impl.BallProjectileEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.IndirectTalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.IndirectTalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;

import static com.openrsc.server.plugins.minigames.gnomeball.GnomeField.Zone.ZONE_NO_PASS;
import static com.openrsc.server.plugins.minigames.gnomeball.GnomeField.Zone.ZONE_PASS;


public class GnomeNpcs implements IndirectTalkToNpcListener , NpcCommandListener , PlayerAttackNpcListener , PlayerMageNpcListener , PlayerRangeNpcListener , TalkToNpcListener , IndirectTalkToNpcExecutiveListener , NpcCommandExecutiveListener , PlayerAttackNpcExecutiveListener , PlayerMageNpcExecutiveListener , PlayerRangeNpcExecutiveListener , TalkToNpcExecutiveListener {
    private static final int[] GNOME_BALLERS_ZONE_PASS = new int[]{ 605, 606, 607, 608 };

    private static final int[] GNOME_BALLERS_ZONE1XP_OUTER = new int[]{ 603, 604 };

    private static final int[] GNOME_BALLERS_ZONE2XP_OUTER = new int[]{ 595, 600, 602 };

    private static final int[] GNOME_BALLERS_ZONE1XP_INNER = new int[]{ 597, 598, 599 };

    public static final int GNOME_BALLER_NORTH = 609;

    public static final int GNOME_BALLER_SOUTH = 610;

    public static final int GOALIE = 596;

    public static final int CHEERLEADER = 611;

    private static final int REFEREE = 601;

    private static final int OFFICIAL = 625;

    private static final int[] TACKLING_XP = new int[]{ 15, 20 };

    @Override
    public boolean blockPlayerRangeNpc(Player p, Npc n) {
        return ((DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID()) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID());
    }

    @Override
    public boolean blockPlayerMageNpc(Player p, Npc n) {
        return ((DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID()) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID());
    }

    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        return ((DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID()) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID());
    }

    @Override
    public GameStateEvent onPlayerRangeNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID()) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
                        Functions.___message(p, 1200, "you can't attack this gnome", "that's cheating");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onPlayerMageNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID()) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
                        Functions.___message(p, 1200, "you can't attack this gnome", "that's cheating");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onPlayerAttackNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID()) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
                        Functions.___message(p, 1200, "you can't attack this gnome", "that's cheating");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return ((((((n.getID() == GnomeNpcs.CHEERLEADER) || (n.getID() == GnomeNpcs.OFFICIAL)) || (n.getID() == GnomeNpcs.REFEREE)) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID());
    }

    @Override
    public boolean blockNpcCommand(Npc n, String command, Player p) {
        return (((((n.getID() == GnomeNpcs.GNOME_BALLER_NORTH) || (n.getID() == GnomeNpcs.GNOME_BALLER_SOUTH)) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == GnomeNpcs.CHEERLEADER) {
                        Functions.___playerTalk(p, n, "hello");
                        Functions.___npcTalk(p, n, "hi there, how are you doing?");
                        Functions.___playerTalk(p, n, "not bad thanks");
                        Functions.___npcTalk(p, n, "i just love the big games", "all those big muscle bound gnomes running around");
                        Functions.___playerTalk(p, n, "big?");
                        Functions.___npcTalk(p, n, "do you play gnome ball?");
                        int option = // do not send over
                        Functions.___showMenu(p, n, false, "what is it?", "play! i'm a gnome ball master");
                        if (option == 0) {
                            Functions.___playerTalk(p, n, "what is it?");
                            Functions.___npcTalk(p, n, "like, only the greatest gnome ball game ever made!");
                            Functions.___playerTalk(p, n, "are there many gnome ball games");
                            Functions.___npcTalk(p, n, "no, there's just one", "and it's the best");
                            Functions.___playerTalk(p, n, "ok, so how do you play?");
                            Functions.___npcTalk(p, n, "the attacker gets the ball and runs towards the goal net");
                            Functions.___playerTalk(p, n, "and...?");
                            Functions.___npcTalk(p, n, "scores of course");
                            Functions.___playerTalk(p, n, "sounds easy enough");
                            Functions.___npcTalk(p, n, "you'll be playing against the best defenders in the gnome ball league");
                            Functions.___playerTalk(p, n, "really, are there many teams in the league?");
                            Functions.___npcTalk(p, n, "nope, just us!");
                        } else
                            if (option == 1) {
                                Functions.___playerTalk(p, n, "play! i'm a gnome ball master?");
                                Functions.___npcTalk(p, n, "really, that's amazing, you're not even a gnome");
                                Functions.___playerTalk(p, n, "it does give me a height advantage");
                                Functions.___npcTalk(p, n, "i look forward to cheering you on");
                                Functions.___playerTalk(p, n, "the first goal's for you");
                                Functions.___npcTalk(p, n, "wow!, thanks");
                            }

                    } else
                        if (n.getID() == GnomeNpcs.OFFICIAL) {
                            Functions.___playerTalk(p, n, "hello there");
                            Functions.___npcTalk(p, n, "well hello adventurer, are you playing?");
                            int option = Functions.___showMenu(p, n, "not at the moment", "yes, i'm just having a break");
                            if (option == 0) {
                                Functions.___npcTalk(p, n, "well really you shouldn't be on the pitch", "some of these games get really rough");
                                int sub_option = Functions.___showMenu(p, n, "how do you play?", "it looks like a silly game anyway");
                                if (sub_option == 0) {
                                    Functions.___npcTalk(p, n, "the gnomes in orange are on your team", "you then charge at the gnome defense and try to throw the ball..", "..through the net to the goal catcher, it's a rough game but fun", "it's also great way to improve your agility");
                                } else
                                    if (option == 1) {
                                        Functions.___npcTalk(p, n, "gnome ball silly!, this my friend is the backbone of our community", "it also happens to be a great way to stay fit and agile");
                                    }

                            } else
                                if (option == 1) {
                                    Functions.___npcTalk(p, n, "good stuff, there's nothing like chasing a pigs bladder..", "..to remind one that they're alive");
                                }

                        } else
                            if (n.getID() == GnomeNpcs.REFEREE) {
                                if (!p.getCache().hasKey("gnomeball")) {
                                    Functions.___npcTalk(p, n, "hi, welcome to gnome ball");
                                    Functions.___playerTalk(p, n, "gnome ball?, how do you play?");
                                    Functions.___npcTalk(p, n, "it's pretty simple really, you take the ball from me", "charge at the gnome defense and try to throw the ball..", "..through the net to the goal catcher, it's a rough game but great fun", "it's also a great way to improve your agility", "so do you fancy a game?");
                                    int option = Functions.___showMenu(p, n, "looks too dangerous for me", "ok then i'll have a go");
                                    if (option == 0) {
                                        Functions.___npcTalk(p, n, "you may be right, we've seen humans die on this field");
                                    } else
                                        if (option == 1) {
                                            Functions.___npcTalk(p, n, "great stuff", "there are no rules to gnome ball, so it can get a bit rough", "you can pass to the winger gnomes if your behind the start line", "otherwise, if you're feeling brave you, can just charge and dodge");
                                            Functions.___playerTalk(p, n, "sounds easy enough");
                                            Functions.___npcTalk(p, n, "the main aim is to leave with no broken limbs", "i think you should be fine");
                                            p.getCache().store("gnomeball", true);
                                            Functions.___npcTalk(p, n, "ready ...  go");
                                            Functions.___message(p, 1200, "the ref throws the ball into the air", "you jump up and catch it");
                                            Functions.addItem(p, ItemId.GNOME_BALL.id(), 1);
                                        }

                                } else {
                                    // player does not have ball
                                    if (!Functions.hasItem(p, ItemId.GNOME_BALL.id())) {
                                        loadIfNotMemory(p, "gnomeball_npc");
                                        // and neither does a gnome baller
                                        if (Functions.inArray(p.getSyncAttribute("gnomeball_npc", -1), 0)) {
                                            p.setSyncAttribute("throwing_ball_game", false);
                                            Functions.___npcTalk(p, n, "ready ...  go");
                                            Functions.___message(p, 1200, "the ref throws the ball into the air", "you jump up and catch it");
                                            Functions.addItem(p, ItemId.GNOME_BALL.id(), 1);
                                        } else {
                                            Functions.___npcTalk(p, n, "the ball's still in play");
                                        }
                                    } else {
                                        Functions.___npcTalk(p, n, "the ball's still in play");
                                    }
                                }
                            } else
                                if (((DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID()) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
                                    tackleGnomeBaller(p, n);
                                }



                    return null;
                });
            }
        };
    }

    private void loadIfNotMemory(Player p, String cacheName) {
        // load from player cache if not present in memory
        if ((p.getSyncAttribute(cacheName, -1) == (-1)) && p.getCache().hasKey(cacheName)) {
            p.setSyncAttribute(cacheName, p.getCache().getInt(cacheName));
        } else
            if (p.getSyncAttribute(cacheName, -1) == (-1)) {
                p.setSyncAttribute(cacheName, 0);
            }

    }

    @Override
    public GameStateEvent onNpcCommand(Npc n, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((n.getID() == GnomeNpcs.GNOME_BALLER_NORTH) || (n.getID() == GnomeNpcs.GNOME_BALLER_SOUTH)) {
                        GnomeField.Zone currentZone = GnomeField.getInstance().resolvePositionToZone(p);
                        if (currentZone == ZONE_NO_PASS) {
                            p.message("you can't make the pass from here");
                        } else
                            if (currentZone == ZONE_PASS) {
                                if (!Functions.hasItem(p, ItemId.GNOME_BALL.id())) {
                                    p.message("you need the ball first");
                                } else {
                                    p.setSyncAttribute("throwing_ball_game", true);
                                    p.getWorld().getServer().getGameEventHandler().add(new BallProjectileEvent(p.getWorld(), p, n, 3) {
                                        @Override
                                        public void doSpell() {
                                        }
                                    });
                                    p.message("you pass the ball to the gnome");
                                    Functions.removeItem(p, ItemId.GNOME_BALL.id(), 1);
                                    Functions.___npcTalk(p, n, 100, "run long..");
                                    Functions.sleep(5000);
                                    p.message("the gnome throws you a long ball");
                                    Functions.addItem(p, ItemId.GNOME_BALL.id(), 1);
                                    p.setSyncAttribute("throwing_ball_game", false);
                                }
                            } else
                                if (command.equals("pass to")) {
                                    if (!Functions.hasItem(p, ItemId.GNOME_BALL.id())) {
                                        p.message("you need the ball first");
                                    } else {
                                        p.message("you can't make the pass from here");
                                    }
                                }


                    } else
                        if (((DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE_PASS, n.getID()) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE2XP_OUTER, n.getID())) || DataConversions.inArray(GnomeNpcs.GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
                            tackleGnomeBaller(p, n);
                        }

                    return null;
                });
            }
        };
    }

    private void tackleGnomeBaller(Player p, Npc n) {
        loadIfNotMemory(p, "gnomeball_npc");
        // and neither does a gnome baller
        if ((p.getSyncAttribute("gnomeball_npc", -1) == 0) || (n.getID() != p.getSyncAttribute("gnomeball_npc", -1))) {
            p.message("the gnome isn't carrying the ball");
        } else {
            Functions.showBubble(p, new Item(ItemId.GNOME_BALL.id()));
            Functions.___message(p, "you attempt to tackle the gnome");
            if (DataConversions.random(0, 1) == 0) {
                // successful tackles gives agility xp
                p.playerServerMessage(MessageType.QUEST, "You skillfully grab the ball");
                p.playerServerMessage(MessageType.QUEST, "and push the gnome to the floor");
                Functions.___npcTalk(p, n, "grrrr");
                Functions.addItem(p, ItemId.GNOME_BALL.id(), 1);
                p.incExp(Skills.AGILITY, GnomeNpcs.TACKLING_XP[DataConversions.random(0, 1)], true);
                p.setSyncAttribute("gnomeball_npc", 0);
            } else {
                p.playerServerMessage(MessageType.QUEST, "You're pushed away by the gnome");
                Functions.___playerTalk(p, n, "ouch");
                p.damage(((int) (Math.ceil(p.getSkills().getLevel(Skills.HITS) * 0.05))));
                Functions.___npcTalk(p, n, "hee hee");
            }
        }
    }

    // this should only happens when player passes ball to gnome baller (team)
    @Override
    public GameStateEvent onIndirectTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((n.getID() == GnomeNpcs.GNOME_BALLER_NORTH) || (n.getID() == GnomeNpcs.GNOME_BALLER_SOUTH)) {
                        // pass to -> direct use of command
                        // pass -> passing via gnome ball's shoot (requires player to be in correct position)
                        onNpcCommand(n, "pass", p);
                    }
                    return null;
                });
            }
        };
    }

    // work around, technically these should be on command but wasnt being triggered
    @Override
    public boolean blockIndirectTalkToNpc(Player p, Npc n) {
        return (n.getID() == GnomeNpcs.GNOME_BALLER_NORTH) || (n.getID() == GnomeNpcs.GNOME_BALLER_SOUTH);
    }
}

