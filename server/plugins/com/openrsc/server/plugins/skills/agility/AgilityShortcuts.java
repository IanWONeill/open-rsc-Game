package com.openrsc.server.plugins.skills.agility;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameNotifyEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;
import java.util.concurrent.Callable;


// HERRING SPAWN I CHEST ROOM SINISTER CHEST = 362, 614, 3564
public class AgilityShortcuts implements InvUseOnObjectListener , ObjectActionListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener {
    private static final int SHORTCUT_FALADOR_HANDHOLD = 693;

    private static final int SHORTCUT_BRIMHAVEN_SWING = 694;

    private static final int SHORTCUT_BRIMHAVEN_BACK_SWING = 695;

    private static final int SHORTCUT_EDGE_DUNGEON_SWING = 684;

    private static final int SHORTCUT_EDGE_DUNGEON_BACK_SWING = 685;

    private static final int SHORTCUT_WEST_COALTRUCKS_LOG = 681;

    private static final int SHORTCUT_EAST_COALTRUCKS_LOG = 680;

    private static final int SHILO_VILLAGE_ROCKS_TO_BRIDGE = 710;

    private static final int SHILO_VILLAGE_BRIDGE_BLOCKADE_JUMP = 691;

    private static final int SHORTCUT_YANILLE_AGILITY_ROPESWING = 628;

    private static final int SHORTCUT_YANILLE_AGILITY_ROPESWING_BACK = 627;

    private static final int SHORTCUT_YANILLE_AGILITY_LEDGE = 614;

    private static final int SHORTCUT_YANILLE_AGILITY_LEDGE_BACK = 615;

    private static final int SHORTCUT_YANILLE_PILE_OF_RUBBLE = 636;

    private static final int SHORTCUT_YANILLE_PILE_OF_RUBBLE_UP = 633;

    private static final int SHORTCUT_YANILLE_PIPE = 656;

    private static final int SHORTCUT_YANILLE_PIPE_BACK = 657;

    private static final int GREW_ISLAND_ROPE_ATTACH = 662;

    private static final int GREW_ISLAND_ROPE_ATTACHED = 663;

    private static final int GREW_ISLAND_SWING_BACK = 664;

    private static final int EAST_KARAMJA_LOG = 692;

    private static final int EAST_KARAMJA_STONES = 701;

    private static final int YANILLE_WATCHTOWER_HANDHOLDS = 658;

    private static final int TAVERLY_PIPE = 1236;

    private static final int TAVERLY_PIPE_RETURN = 1237;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return Functions.inArray(obj.getID(), AgilityShortcuts.SHORTCUT_YANILLE_PIPE, AgilityShortcuts.SHORTCUT_YANILLE_PIPE_BACK, AgilityShortcuts.SHORTCUT_YANILLE_PILE_OF_RUBBLE, AgilityShortcuts.SHORTCUT_YANILLE_PILE_OF_RUBBLE_UP, AgilityShortcuts.SHORTCUT_YANILLE_AGILITY_LEDGE, AgilityShortcuts.SHORTCUT_YANILLE_AGILITY_LEDGE_BACK, AgilityShortcuts.SHORTCUT_FALADOR_HANDHOLD, AgilityShortcuts.SHORTCUT_BRIMHAVEN_SWING, AgilityShortcuts.SHORTCUT_BRIMHAVEN_BACK_SWING, AgilityShortcuts.SHORTCUT_EDGE_DUNGEON_SWING, AgilityShortcuts.SHORTCUT_EDGE_DUNGEON_BACK_SWING, AgilityShortcuts.SHORTCUT_WEST_COALTRUCKS_LOG, AgilityShortcuts.SHORTCUT_EAST_COALTRUCKS_LOG, AgilityShortcuts.SHORTCUT_YANILLE_AGILITY_ROPESWING, AgilityShortcuts.SHORTCUT_YANILLE_AGILITY_ROPESWING_BACK, AgilityShortcuts.GREW_ISLAND_ROPE_ATTACHED, AgilityShortcuts.GREW_ISLAND_SWING_BACK, AgilityShortcuts.EAST_KARAMJA_LOG, AgilityShortcuts.EAST_KARAMJA_STONES, AgilityShortcuts.YANILLE_WATCHTOWER_HANDHOLDS, AgilityShortcuts.SHILO_VILLAGE_ROCKS_TO_BRIDGE, AgilityShortcuts.SHILO_VILLAGE_BRIDGE_BLOCKADE_JUMP, AgilityShortcuts.TAVERLY_PIPE, AgilityShortcuts.TAVERLY_PIPE_RETURN);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    switch (obj.getID()) {
                        case AgilityShortcuts.SHILO_VILLAGE_BRIDGE_BLOCKADE_JUMP :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 32) {
                                p.message("You need an agility level of 32 to climb the rocks");
                                return null;
                            }
                            p.setBusy(true);
                            p.message("The bridge beyond this fence looks very unsafe.");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 3, "Agility Shortcut: Shilo Bridge") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.___message(getPlayerOwner(), "However, you could try to negotiate it if you're feeling very agile.");
                                        return invoke(1, 3);
                                    });
                                    addState(1, () -> {
                                        getPlayerOwner().message("Would you like to try?");
                                        GameNotifyEvent event = Functions.showPlayerMenu(getPlayerOwner(), "No thanks! It looks far too dangerous!", "Yes, I'm totally brave and quite agile!");
                                        return invokeOnNotify(event, 3, 0);
                                    });
                                    addState(3, () -> {
                                        int jumpMenu = ((int) (getNotifyEvent().getObjectOut("int_option")));
                                        if (jumpMenu == 0) {
                                            getPlayerOwner().message("You decide that common sense is the better part of valour.");
                                            return invoke(4, 3);
                                        } else
                                            if (jumpMenu == 1) {
                                                getPlayerOwner().message("You prepare to negotiate the bridge fence...");
                                                return invoke(6, 3);
                                            }

                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                    addState(4, () -> {
                                        getPlayerOwner().message("And stop yourself from being hurled to what must be an ");
                                        return invoke(5, 3);
                                    });
                                    addState(5, () -> {
                                        getPlayerOwner().message("inevitable death.");
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                    addState(6, () -> {
                                        getPlayerOwner().message("You run and jump...");
                                        return invoke(7, 3);
                                    });
                                    addState(7, () -> {
                                        if (succeed(getPlayerOwner(), 32)) {
                                            getPlayerOwner().message("...and land perfectly on the other side!");
                                            if (getPlayerOwner().getX() >= 460) {
                                                // back
                                                getPlayerOwner().teleport(458, 828);
                                            } else {
                                                getPlayerOwner().teleport(460, 828);
                                            }
                                            getPlayerOwner().setBusy(false);
                                            return null;
                                        } else {
                                            getPlayerOwner().message("...slip and fall incompetently into the river below!");
                                            getPlayerOwner().teleport(458, 832);
                                            Functions.___playerTalk(getPlayerOwner(), "* Ahhhhhhhhhh! *");
                                            return invoke(8, 3);
                                        }
                                    });
                                    addState(8, () -> {
                                        getPlayerOwner().damage(Functions.getCurrentLevel(getPlayerOwner(), Skills.HITS) / 10);
                                        return invokeNextState(1);
                                    });
                                    addState(9, () -> {
                                        getPlayerOwner().teleport(458, 836);
                                        getPlayerOwner().damage(Functions.getCurrentLevel(getPlayerOwner(), Skills.HITS) / 10);
                                        return invokeNextState(2);
                                    });
                                    addState(10, () -> {
                                        Functions.___playerTalk(getPlayerOwner(), "* Gulp! *");
                                        return invokeNextState(6);
                                    });
                                    addState(11, () -> {
                                        getPlayerOwner().teleport(459, 841);
                                        Functions.___playerTalk(getPlayerOwner(), "* Gulp! *");
                                        return invokeNextState(5);
                                    });
                                    addState(12, () -> {
                                        getPlayerOwner().message("You just manage to drag your pitiful frame onto the river bank.");
                                        Functions.___playerTalk(getPlayerOwner(), "* Gasp! *");
                                        return invokeNextState(3);
                                    });
                                    addState(13, () -> {
                                        getPlayerOwner().damage(Functions.getCurrentLevel(getPlayerOwner(), Skills.HITS) / 10);
                                        return invokeNextState(2);
                                    });
                                    addState(14, () -> {
                                        getPlayerOwner().message("Though you nearly drowned in the river!");
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case AgilityShortcuts.SHILO_VILLAGE_ROCKS_TO_BRIDGE :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 32) {
                                p.message("You need an agility level of 32 to climb the rocks");
                                return null;
                            }
                            Functions.___message(p, "These rocks look quite dangerous to climb.", "But you may be able to scale them.");
                            p.message("Would you like to try?");
                            int menu = Functions.___showMenu(p, "Yes, I can easily climb this!", "Nope, I'm sure I'll probably fall!");
                            if (menu == 0) {
                                p.setBusy(true);
                                if (succeed(p, 32)) {
                                    Functions.___message(p, "You manage to climb the rocks succesfully and pick");
                                    if (obj.getX() == 450) {
                                        p.message("a route though the trecherous embankment to the top.");
                                        p.teleport(452, 829);
                                    } else {
                                        p.message("a route though the trecherous embankment to the bottom.");
                                        p.teleport(449, 828);
                                    }
                                    p.setBusy(false);
                                } else {
                                    p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "Agility Shortcut: Shilo Rocks") {
                                        public void init() {
                                            addState(0, () -> {
                                                getPlayerOwner().teleport(450, 828);
                                                Functions.___message(getPlayerOwner(), "You fall and hurt yourself.");
                                                getPlayerOwner().damage(Functions.getCurrentLevel(getPlayerOwner(), Skills.HITS) / 10);
                                                return invokeNextState(1);
                                            });
                                            addState(1, () -> {
                                                getPlayerOwner().teleport(449, 828);
                                                getPlayerOwner().setBusy(false);
                                                return null;
                                            });
                                        }
                                    });
                                }
                            } else
                                if (menu == 1) {
                                    p.message("You decide not to climb the rocks.");
                                }

                            break;
                        case AgilityShortcuts.SHORTCUT_FALADOR_HANDHOLD :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 5) {
                                p.message("You need an agility level of 5 to climb the wall");
                                return null;
                            }
                            p.setBusy(true);
                            p.message("You climb over the wall");
                            Functions.movePlayer(p, 338, 555);
                            p.incExp(Skills.AGILITY, 50, true);
                            p.setBusy(false);
                            break;
                        case AgilityShortcuts.SHORTCUT_BRIMHAVEN_SWING :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 10) {
                                p.message("You need an agility level of 10 to attempt to swing on this vine");
                                return null;
                            }
                            p.setBusy(true);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "Agility Shortcut: Brimhaven Swing") {
                                public void init() {
                                    addState(0, () -> {
                                        getPlayerOwner().message("You grab the vine and try and swing across");
                                        return invokeNextState(2);
                                    });
                                    addState(1, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 511, 669);
                                        getPlayerOwner().message("You skillfully swing across the stream");
                                        Functions.___playerTalk(getPlayerOwner(), null, "Aaaaahahah");
                                        getPlayerOwner().incExp(Skills.AGILITY, 20, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_BRIMHAVEN_BACK_SWING :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 10) {
                                p.message("You need an agility level of 10 to attempt to swing on this vine");
                                return null;
                            }
                            p.setBusy(true);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "Agility Shortcut: Brimhaven Swing Back") {
                                public void init() {
                                    addState(0, () -> {
                                        getPlayerOwner().message("You grab the vine and try and swing across");
                                        return invokeNextState(2);
                                    });
                                    addState(1, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 508, 668);
                                        getPlayerOwner().message("You skillfully swing across the stream");
                                        Functions.___playerTalk(getPlayerOwner(), null, "Aaaaahahah");
                                        getPlayerOwner().incExp(Skills.AGILITY, 20, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_EDGE_DUNGEON_SWING :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 15) {
                                p.message("You need an agility level of 15 to attempt to swing on this rope");
                                return null;
                            }
                            p.setBusy(true);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Agility Shortcut: Edge Dungeon Swing") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 207, 3221);
                                        getPlayerOwner().message("You skillfully swing across the hole");
                                        getPlayerOwner().incExp(Skills.AGILITY, 40, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_EDGE_DUNGEON_BACK_SWING :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 15) {
                                p.message("You need an agility level of 15 to attempt to swing on this rope");
                                return null;
                            }
                            p.setBusy(true);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Agility Shortcut: Edge Dungeon Swing Back") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(p, 206, 3225);
                                        p.message("You skillfully swing across the hole");
                                        p.incExp(Skills.AGILITY, 40, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_WEST_COALTRUCKS_LOG :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 20) {
                                p.message("You need an agility level of 20 to attempt balancing along this log");
                                return null;
                            }
                            p.setBusy(true);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "Agility Shortcut: Coaltrucks West") {
                                public void init() {
                                    addState(0, () -> {
                                        getPlayerOwner().message("You stand on the slippery log");
                                        Functions.movePlayer(getPlayerOwner(), 595, 458);
                                        return invokeNextState(1);
                                    });
                                    addState(1, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 594, 458);
                                        return invokeNextState(1);
                                    });
                                    addState(2, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 593, 458);
                                        return invokeNextState(1);
                                    });
                                    addState(3, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 592, 458);
                                        return invokeNextState(1);
                                    });
                                    addState(4, () -> {
                                        getPlayerOwner().message("and you walk across");
                                        getPlayerOwner().incExp(Skills.AGILITY, 34, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_EAST_COALTRUCKS_LOG :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 20) {
                                p.message("You need an agility level of 20 to attempt balancing along this log");
                                p.setBusy(false);
                                return null;
                            }
                            p.setBusy(true);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "Agility Shortcut: Coaltrucks East") {
                                public void init() {
                                    addState(0, () -> {
                                        getPlayerOwner().message("You stand on the slippery log");
                                        Functions.movePlayer(getPlayerOwner(), 595, 458);
                                        return invokeNextState(1);
                                    });
                                    addState(1, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 596, 458);
                                        return invokeNextState(1);
                                    });
                                    addState(2, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 597, 458);
                                        return invokeNextState(1);
                                    });
                                    addState(3, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 598, 458);
                                        return invokeNextState(1);
                                    });
                                    addState(4, () -> {
                                        getPlayerOwner().message("and you walk across");
                                        getPlayerOwner().incExp(Skills.AGILITY, 34, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                            // CONTINUE SHORTCUTS.
                        case AgilityShortcuts.SHORTCUT_YANILLE_AGILITY_ROPESWING :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 57) {
                                p.message("You need an agility level of 57 to attempt to swing on this rope");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= 69750) {
                                    p.message("You are too tired to swing on the rope");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            p.message("You grab the rope and try and swing across");
                            if (!succeed(p, 57, 77)) {
                                Functions.___message(p, "You miss the opposite side and fall to the level below");
                                Functions.movePlayer(p, 596, 3534);
                                p.setBusy(false);
                                return null;
                            }
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Agility Shortcut: Yanille Ropeswing") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 596, 3581);
                                        getPlayerOwner().message("You skillfully swing across the hole");
                                        getPlayerOwner().incExp(Skills.AGILITY, 110, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_YANILLE_AGILITY_ROPESWING_BACK :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 57) {
                                p.message("You need an agility level of 57 to attempt to swing on this rope");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= 69750) {
                                    p.message("You are too tired to swing on the rope");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            p.message("You grab the rope and try and swing across");
                            if (!succeed(p, 57, 77)) {
                                Functions.___message(p, "You miss the opposite side and fall to the level below");
                                Functions.movePlayer(p, 598, 3536);
                                p.setBusy(false);
                                return null;
                            }
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Agility Shortcut: Yanille Ropeswing Back") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 598, 3585);
                                        getPlayerOwner().message("You skillfully swing across the hole");
                                        getPlayerOwner().incExp(Skills.AGILITY, 110, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_YANILLE_AGILITY_LEDGE :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 40) {
                                p.message("You need an agility level of 40 to attempt balancing along this log");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= 69750) {
                                    p.message("You are too tired to balance on the ledge");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            p.message("You put your foot on the ledge and try to edge across");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Agility Shortcut: Yanille Ledge") {
                                public void init() {
                                    addState(0, () -> {
                                        if (!succeed(getPlayerOwner(), 40, 65)) {
                                            Functions.___message(getPlayerOwner(), "you lose your footing and fall to the level below");
                                            Functions.movePlayer(getPlayerOwner(), 603, 3520);
                                            getPlayerOwner().setBusy(false);
                                            return null;
                                        }
                                        Functions.movePlayer(p, 601, 3563);
                                        getPlayerOwner().message("You skillfully balance across the hole");
                                        getPlayerOwner().incExp(Skills.AGILITY, 90, true);
                                        return invokeNextState(2);
                                    });
                                    addState(1, () -> {
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_YANILLE_AGILITY_LEDGE_BACK :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 40) {
                                p.message("You need an agility level of 40 to attempt balancing along this log");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= 69750) {
                                    p.message("You are too tired to balance on the ledge");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            p.message("You put your foot on the ledge and try to edge across");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Agility Shortcut: Yanille Ledge Back") {
                                public void init() {
                                    addState(0, () -> {
                                        if (!succeed(getPlayerOwner(), 40, 65)) {
                                            Functions.___message(getPlayerOwner(), "you lose your footing and fall to the level below");
                                            Functions.movePlayer(getPlayerOwner(), 603, 3520);
                                            getPlayerOwner().setBusy(false);
                                            return null;
                                        }
                                        Functions.movePlayer(getPlayerOwner(), 601, 3557);
                                        getPlayerOwner().message("You skillfully balance across the hole");
                                        getPlayerOwner().incExp(Skills.AGILITY, 90, true);
                                        return invokeNextState(2);
                                    });
                                    addState(1, () -> {
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_YANILLE_PILE_OF_RUBBLE :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 67) {
                                p.message("You need an agility level of 67 to attempt to climb down the rubble");
                                return null;
                            }
                            p.setBusy(true);
                            Functions.movePlayer(p, 580, 3525);
                            p.message("You climb down the pile of rubble");
                            p.setBusy(false);
                            break;
                        case AgilityShortcuts.SHORTCUT_YANILLE_PILE_OF_RUBBLE_UP :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 67) {
                                p.message("You need an agility level of 67 to attempt to climb up the rubble");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= 69750) {
                                    p.message("You are too tired to climb up the rubble");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            Functions.movePlayer(p, 582, 3573);
                            p.message("You climb up the pile of rubble");
                            p.incExp(Skills.AGILITY, 54, true);
                            p.setBusy(false);
                            break;
                        case AgilityShortcuts.SHORTCUT_YANILLE_PIPE :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 49) {
                                p.message("You need an agility level of 49 to attempt to squeeze through the pipe");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= 69750) {
                                    p.message("You are too tired to squeeze through the pipe");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            p.message("You squeeze through the pipe");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Agility Shortcut: Yanille Pipe") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 608, 3568);
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.SHORTCUT_YANILLE_PIPE_BACK :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 49) {
                                p.message("You need an agility level of 49 to attempt to squeeze through the pipe");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= 69750) {
                                    p.message("You are too tired to squeeze through the pipe");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            p.message("You squeeze through the pipe");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Agility Shortcut: Yanille Pipe Back") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 605, 3568);
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.GREW_ISLAND_ROPE_ATTACHED :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 30) {
                                p.message("You need an agility level of 30 to attempt to swing across the stream");
                                return null;
                            }
                            p.setBusy(true);
                            p.message("You grab the rope and try and swing across");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Grew Island Rope") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 664, 755);
                                        getPlayerOwner().message("You skillfully swing across the stream");
                                        getPlayerOwner().incExp(Skills.AGILITY, 50, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.GREW_ISLAND_SWING_BACK :
                            p.message("You grab the rope and try and swing across");
                            p.setBusy(true);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Grew Island Swing Back") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 666, 755);
                                        getPlayerOwner().message("You skillfully swing across the stream");
                                        getPlayerOwner().incExp(Skills.AGILITY, 50, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.EAST_KARAMJA_LOG :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 32) {
                                p.message("You need an agility level of 32 to attempt balancing along this log");
                                return null;
                            }
                            p.setBusy(true);
                            p.message("You attempt to walk over the the slippery log..");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 3, "Karamja East Log") {
                                public void init() {
                                    addState(0, () -> {
                                        if (!succeed(getPlayerOwner(), 32)) {
                                            Functions.movePlayer(getPlayerOwner(), 368, 781);
                                            return invoke(1, 1);
                                        }
                                        if (getPlayerOwner().getX() <= 367) {
                                            Functions.movePlayer(getPlayerOwner(), 368, 781);
                                            return invoke(2, 1);
                                        } else {
                                            Functions.movePlayer(getPlayerOwner(), 368, 781);
                                            return invoke(3, 1);
                                        }
                                    });
                                    addState(1, () -> {
                                        getPlayerOwner().message("@red@You fall into the stream!");
                                        getPlayerOwner().message("You lose some health");
                                        Functions.movePlayer(getPlayerOwner(), 370, 776);
                                        getPlayerOwner().damage(1);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                    addState(2, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 370, 781);
                                        return invoke(4, 0);
                                    });
                                    addState(3, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 366, 781);
                                        return invoke(4, 0);
                                    });
                                    addState(4, () -> {
                                        getPlayerOwner().message("...and make it without any problems!");
                                        getPlayerOwner().incExp(Skills.AGILITY, 10, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.EAST_KARAMJA_STONES :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 32) {
                                p.message("You need an agility level of 32 to step on these stones");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= p.MAX_FATIGUE) {
                                    p.message("You are too fatigued to continue.");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "Karamja East Stones") {
                                public void init() {
                                    addState(0, () -> {
                                        getPlayerOwner().message("You jump onto the rock");
                                        if (getPlayerOwner().getY() <= 805) {
                                            Functions.movePlayer(getPlayerOwner(), 347, 806);
                                            return invoke(1, 1);
                                        } else {
                                            Functions.movePlayer(getPlayerOwner(), 346, 807);
                                            return invoke(2, 1);
                                        }
                                    });
                                    addState(1, () -> {
                                        if (!succeed(getPlayerOwner(), 32)) {
                                            return invoke(3, 2);
                                        }
                                        Functions.movePlayer(getPlayerOwner(), 346, 808);
                                        return invoke(5, 0);
                                    });
                                    addState(2, () -> {
                                        if (!succeed(getPlayerOwner(), 32)) {
                                            return invoke(4, 2);
                                        }
                                        Functions.movePlayer(getPlayerOwner(), 347, 805);
                                        return invoke(5, 0);
                                    });
                                    addState(3, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 341, 809);
                                        return invoke(6, 0);
                                    });
                                    addState(4, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 341, 805);
                                        return invoke(6, 0);
                                    });
                                    addState(5, () -> {
                                        getPlayerOwner().message("And cross the water without problems.");
                                        getPlayerOwner().incExp(Skills.AGILITY, 10, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                    addState(6, () -> {
                                        getPlayerOwner().message("@red@!!! You Fall !!!");
                                        Functions.___message(getPlayerOwner(), "You get washed up on the other side of the river...", "After being nearly half drowned");
                                        getPlayerOwner().setBusy(false);
                                        getPlayerOwner().damage(((int) (getPlayerOwner().getSkills().getLevel(Skills.HITS) / 4)) + 2);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.YANILLE_WATCHTOWER_HANDHOLDS :
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= p.MAX_FATIGUE) {
                                    p.message("You are too tired to climb up the wall");
                                    return null;
                                }
                            }
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 18) {
                                p.message("You need an agility level of 18 to climb the wall");
                                return null;
                            }
                            p.setBusy(true);
                            p.message("You climb up the wall");
                            p.teleport(637, 1680);
                            p.message("And climb in through the window");
                            p.incExp(Skills.AGILITY, 50, true);
                            p.setBusy(false);
                            break;
                        case AgilityShortcuts.TAVERLY_PIPE_RETURN :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 70) {
                                p.message("You need an agility level of 70 to attempt to squeeze through the pipe");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= 69750) {
                                    p.message("You are too tired to squeeze through the pipe");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            p.message("You squeeze through the pipe");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Agility Shortcut: Taverly Pipe Return") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 372, 3352);
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                        case AgilityShortcuts.TAVERLY_PIPE :
                            if (Functions.getCurrentLevel(p, Skills.AGILITY) < 70) {
                                p.message("You need an agility level of 70 to attempt to squeeze through the pipe");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                                if (p.getFatigue() >= 69750) {
                                    p.message("You are too tired to squeeze through the pipe");
                                    return null;
                                }
                            }
                            p.setBusy(true);
                            p.message("You squeeze through the pipe");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 4, "Agility Shortcut: Taverly Pipe Return") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 375, 3352);
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            break;
                    }
                    return null;
                });
            }
        };
    }

    boolean succeed(Player player, int req) {
        return Formulae.calcProductionSuccessful(req, Functions.getCurrentLevel(player, Skills.AGILITY), false, req + 30);
    }

    boolean succeed(Player player, int req, int lvlStopFail) {
        return Formulae.calcProductionSuccessful(req, Functions.getCurrentLevel(player, Skills.AGILITY), true, lvlStopFail);
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
        return (obj.getID() == AgilityShortcuts.GREW_ISLAND_ROPE_ATTACH) && (item.getID() == ItemId.ROPE.id());
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == AgilityShortcuts.GREW_ISLAND_ROPE_ATTACH) && (item.getID() == ItemId.ROPE.id())) {
                        p.message("you tie the rope to the tree");
                        Functions.removeItem(p, ItemId.ROPE.id(), 1);
                        p.getWorld().replaceGameObject(obj, new GameObject(p.getWorld(), obj.getLocation(), 663, obj.getDirection(), obj.getType()));
                        p.getWorld().delayedSpawnObject(obj.getLoc(), 60000);
                    }
                    return null;
                });
            }
        };
    }
}

