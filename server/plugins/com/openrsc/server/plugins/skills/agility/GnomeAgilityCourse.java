package com.openrsc.server.plugins.skills.agility;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;


public class GnomeAgilityCourse implements ObjectActionListener , ObjectActionExecutiveListener {
    private static final int BALANCE_LOG = 655;

    private static final int NET = 647;

    private static final int WATCH_TOWER = 648;

    private static final int ROPE_SWING = 650;

    private static final int LANDING = 649;

    private static final int SECOND_NET = 653;

    private static final int PIPE = 654;

    // private static int[] obstacleOrder = {BALANCE_LOG, NET, WATCH_TOWER, ROPE_SWING, LANDING, SECOND_NET, PIPE};
    private static Set<Integer> obstacles = new HashSet<Integer>(Arrays.asList(GnomeAgilityCourse.BALANCE_LOG, GnomeAgilityCourse.NET, GnomeAgilityCourse.WATCH_TOWER, GnomeAgilityCourse.ROPE_SWING, GnomeAgilityCourse.LANDING, GnomeAgilityCourse.SECOND_NET));

    private static Integer lastObstacle = GnomeAgilityCourse.PIPE;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return Functions.inArray(obj.getID(), GnomeAgilityCourse.BALANCE_LOG, GnomeAgilityCourse.NET, GnomeAgilityCourse.WATCH_TOWER, GnomeAgilityCourse.ROPE_SWING, GnomeAgilityCourse.LANDING, GnomeAgilityCourse.SECOND_NET, GnomeAgilityCourse.PIPE);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                        if ((p.getFatigue() >= p.MAX_FATIGUE) && (!Functions.inArray(obj.getID(), GnomeAgilityCourse.WATCH_TOWER, GnomeAgilityCourse.ROPE_SWING, GnomeAgilityCourse.LANDING))) {
                            p.message("you are too tired to train");
                            return null;
                        }
                    }
                    Npc gnomeTrainer;
                    p.setBusy(true);
                    switch (obj.getID()) {
                        case GnomeAgilityCourse.BALANCE_LOG :
                            p.message("you stand on the slippery log");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "Gnome Agility Log") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 692, 494);
                                        return invokeNextState(1);
                                    });
                                    addState(1, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 692, 495);
                                        return invokeNextState(1);
                                    });
                                    addState(2, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 692, 496);
                                        return invokeNextState(1);
                                    });
                                    addState(3, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 692, 497);
                                        return invokeNextState(1);
                                    });
                                    addState(4, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 692, 498);
                                        return invokeNextState(1);
                                    });
                                    addState(5, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 692, 499);
                                        return invokeNextState(1);
                                    });
                                    addState(6, () -> {
                                        p.message("and walk across");
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), GnomeAgilityCourse.obstacles, GnomeAgilityCourse.lastObstacle, 150);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case GnomeAgilityCourse.NET :
                            gnomeTrainer = Functions.getNearestNpc(p, NpcId.GNOME_TRAINER_STARTINGNET.id(), 10);
                            if ((gnomeTrainer != null) && (!AgilityUtils.hasDoneObstacle(p, GnomeAgilityCourse.NET, GnomeAgilityCourse.obstacles))) {
                                Functions.___npcTalk(p, gnomeTrainer, "move it, move it, move it");
                            }
                            p.message("you climb the net");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 3, "Gnome Agility Net") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(p, 692, 1448);
                                        p.message("and pull yourself onto the platform");
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), GnomeAgilityCourse.obstacles, GnomeAgilityCourse.lastObstacle, 150);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case GnomeAgilityCourse.WATCH_TOWER :
                            gnomeTrainer = Functions.getNearestNpc(p, NpcId.GNOME_TRAINER_PLATFORM.id(), 10);
                            if ((gnomeTrainer != null) && (!AgilityUtils.hasDoneObstacle(p, GnomeAgilityCourse.WATCH_TOWER, GnomeAgilityCourse.obstacles))) {
                                Functions.___npcTalk(p, gnomeTrainer, "that's it, straight up, no messing around");
                            }
                            p.message("you pull yourself up the tree");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Gnome Agility Tower") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 693, 2394);
                                        getPlayerOwner().message("to the platform above");
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), GnomeAgilityCourse.obstacles, GnomeAgilityCourse.lastObstacle, 150);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case GnomeAgilityCourse.ROPE_SWING :
                            p.message("you reach out and grab the rope swing");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Gnome Agility Rope") {
                                public void init() {
                                    addState(0, () -> {
                                        getPlayerOwner().message("you hold on tight");
                                        return invokeNextState(4);
                                    });
                                    addState(1, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 685, 2396);
                                        getPlayerOwner().message("and swing to the oppisite platform");
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), GnomeAgilityCourse.obstacles, GnomeAgilityCourse.lastObstacle, 150);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case GnomeAgilityCourse.LANDING :
                            p.message("you hang down from the tower");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Gnome Agility Landing") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(p, 683, 506);
                                        p.message("and drop to the floor");
                                        Functions.___playerTalk(p, null, "ooof");
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), GnomeAgilityCourse.obstacles, GnomeAgilityCourse.lastObstacle, 150);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case GnomeAgilityCourse.SECOND_NET :
                            gnomeTrainer = Functions.getNearestNpc(p, NpcId.GNOME_TRAINER_ENDINGNET.id(), 10);
                            if ((gnomeTrainer != null) && (!AgilityUtils.hasDoneObstacle(p, GnomeAgilityCourse.SECOND_NET, GnomeAgilityCourse.obstacles))) {
                                Functions.___npcTalk(p, gnomeTrainer, "my granny can move faster than you");
                            }
                            p.message("you take a few steps back");
                            final int initialY = p.getY();
                            Functions.movePlayer(p, p.getX(), initialY + 2);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 1, "Gnome Agility Net") {
                                public void init() {
                                    addState(0, () -> {
                                        getPlayerOwner().message("and run towards the net");
                                        Functions.movePlayer(getPlayerOwner(), getPlayerOwner().getX(), initialY - 2);
                                        return invokeNextState(1);
                                    });
                                    addState(1, () -> {
                                        Functions.movePlayer(getPlayerOwner(), getPlayerOwner().getX(), initialY - 2);
                                        getPlayerOwner().incExp(Skills.AGILITY, 30, true);
                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), GnomeAgilityCourse.obstacles, GnomeAgilityCourse.lastObstacle, 150);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case GnomeAgilityCourse.PIPE :
                            Functions.___message(p, "you squeeze into the pipe", "and shuffle down into it");
                            Functions.movePlayer(p, 683, 494);
                            gnomeTrainer = Functions.getNearestNpc(p, NpcId.GNOME_TRAINER_ENTRANCE.id(), 10);
                            if ((gnomeTrainer != null) && (!AgilityUtils.hasDoneObstacle(p, GnomeAgilityCourse.PIPE, GnomeAgilityCourse.obstacles))) {
                                Functions.___npcTalk(p, gnomeTrainer, "that's the way, well done");
                            }
                            p.incExp(Skills.AGILITY, 30, true);
                            AgilityUtils.completedObstacle(p, obj.getID(), GnomeAgilityCourse.obstacles, GnomeAgilityCourse.lastObstacle, 150);
                            p.setBusy(false);
                            return null;
                    }
                    return null;
                });
            }
        };
    }
}

