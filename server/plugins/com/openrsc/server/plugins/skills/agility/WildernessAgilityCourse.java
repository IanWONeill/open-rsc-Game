package com.openrsc.server.plugins.skills.agility;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;


public class WildernessAgilityCourse implements ObjectActionListener , ObjectActionExecutiveListener {
    private static final int GATE = 703;

    private static final int SECOND_GATE = 704;

    private static final int WILD_PIPE = 705;

    private static final int WILD_ROPESWING = 706;

    private static final int STONE = 707;

    private static final int LEDGE = 708;

    private static final int VINE = 709;

    private static Set<Integer> obstacles = new HashSet<Integer>(Arrays.asList(WildernessAgilityCourse.WILD_PIPE, WildernessAgilityCourse.WILD_ROPESWING, WildernessAgilityCourse.STONE, WildernessAgilityCourse.LEDGE));

    private static Integer lastObstacle = WildernessAgilityCourse.VINE;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return Functions.inArray(obj.getID(), WildernessAgilityCourse.GATE, WildernessAgilityCourse.SECOND_GATE, WildernessAgilityCourse.WILD_PIPE, WildernessAgilityCourse.WILD_ROPESWING, WildernessAgilityCourse.STONE, WildernessAgilityCourse.LEDGE, WildernessAgilityCourse.VINE);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        final int failRate = failRate();
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == WildernessAgilityCourse.GATE) {
                        if (Functions.getCurrentLevel(p, Skills.AGILITY) < 52) {
                            p.message("You need an agility level of 52 to attempt balancing along the ridge");
                            return null;
                        }
                        p.setBusy(true);
                        p.message("You go through the gate and try to edge over the ridge");
                        p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Gate") {
                            public void init() {
                                addState(0, () -> {
                                    Functions.movePlayer(getPlayerOwner(), 298, 130);
                                    return invokeNextState(2);
                                });
                                addState(1, () -> {
                                    if (failRate == 1) {
                                        Functions.___message(getPlayerOwner(), "you lose your footing and fall into the wolf pit");
                                        Functions.movePlayer(getPlayerOwner(), 300, 129);
                                    } else
                                        if (failRate == 2) {
                                            Functions.___message(getPlayerOwner(), "you lose your footing and fall into the wolf pit");
                                            Functions.movePlayer(getPlayerOwner(), 296, 129);
                                        } else {
                                            Functions.___message(getPlayerOwner(), "You skillfully balance across the ridge");
                                            Functions.movePlayer(getPlayerOwner(), 298, 125);
                                            getPlayerOwner().incExp(Skills.AGILITY, 50, true);
                                        }

                                    getPlayerOwner().setBusy(false);
                                    return null;
                                });
                            }
                        });
                        return null;
                    } else
                        if (obj.getID() == WildernessAgilityCourse.SECOND_GATE) {
                            p.message("You go through the gate and try to edge over the ridge");
                            p.setBusy(true);
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Gate") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 298, 130);
                                        return invokeNextState(2);
                                    });
                                    addState(1, () -> {
                                        if (failRate == 1) {
                                            Functions.___message(getPlayerOwner(), "you lose your footing and fall into the wolf pit");
                                            Functions.movePlayer(getPlayerOwner(), 300, 129);
                                        } else
                                            if (failRate == 2) {
                                                Functions.___message(getPlayerOwner(), "you lose your footing and fall into the wolf pit");
                                                Functions.movePlayer(getPlayerOwner(), 296, 129);
                                            } else {
                                                Functions.___message(getPlayerOwner(), "You skillfully balance across the ridge");
                                                Functions.movePlayer(getPlayerOwner(), 298, 134);
                                                getPlayerOwner().incExp(Skills.AGILITY, 50, true);
                                            }

                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        }

                    if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
                        if ((p.getFatigue() >= p.MAX_FATIGUE) && (!Functions.inArray(obj.getID(), WildernessAgilityCourse.WILD_PIPE, WildernessAgilityCourse.WILD_ROPESWING, WildernessAgilityCourse.STONE, WildernessAgilityCourse.LEDGE))) {
                            p.message("you are too tired to train");
                            return null;
                        }
                    }
                    p.setBusy(true);
                    boolean passObstacle = succeed(p);
                    switch (obj.getID()) {
                        case WildernessAgilityCourse.WILD_PIPE :
                            p.message("You squeeze through the pipe");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Gate") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 294, 112);
                                        getPlayerOwner().incExp(Skills.AGILITY, 50, true);
                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), WildernessAgilityCourse.obstacles, WildernessAgilityCourse.lastObstacle, 1500);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case WildernessAgilityCourse.WILD_ROPESWING :
                            p.message("You grab the rope and try and swing across");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Rope") {
                                public void init() {
                                    addState(0, () -> {
                                        if (passObstacle) {
                                            Functions.___message(getPlayerOwner(), "You skillfully swing across the hole");
                                            Functions.movePlayer(getPlayerOwner(), 292, 108);
                                            getPlayerOwner().incExp(Skills.AGILITY, 100, true);
                                            AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), WildernessAgilityCourse.obstacles, WildernessAgilityCourse.lastObstacle, 1500);
                                            getPlayerOwner().setBusy(false);
                                            return null;
                                        } else {
                                            // 13 damage on 85hp.
                                            // 11 damage on 73hp.
                                            // 
                                            getPlayerOwner().message("Your hands slip and you fall to the level below");
                                            return invokeNextState(2);
                                        }
                                    });
                                    addState(1, () -> {
                                        int damage = ((int) (Math.round(getPlayerOwner().getSkills().getLevel(Skills.AGILITY) * 0.15)));
                                        Functions.movePlayer(getPlayerOwner(), 293, 2942);
                                        getPlayerOwner().message("You land painfully on the spikes");
                                        Functions.___playerTalk(getPlayerOwner(), null, "ouch");
                                        getPlayerOwner().damage(damage);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case WildernessAgilityCourse.STONE :
                            p.message("you stand on the stepping stones");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Stones") {
                                public void init() {
                                    addState(0, () -> {
                                        if (passObstacle) {
                                            Functions.movePlayer(getPlayerOwner(), 293, 105);
                                            return invokeNextState(1);
                                        } else {
                                            getPlayerOwner().message("Your lose your footing and land in the lava");
                                            Functions.movePlayer(getPlayerOwner(), 292, 104);
                                            int lavaDamage = ((int) (Math.round(p.getSkills().getLevel(Skills.AGILITY) * 0.21)));
                                            getPlayerOwner().damage(lavaDamage);
                                            getPlayerOwner().setBusy(false);
                                            return null;
                                        }
                                    });
                                    addState(1, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 294, 104);
                                        return invokeNextState(1);
                                    });
                                    addState(2, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 295, 104);
                                        getPlayerOwner().message("and walk across");
                                        return invokeNextState(1);
                                    });
                                    addState(3, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 296, 105);
                                        return invokeNextState(1);
                                    });
                                    addState(4, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 297, 106);
                                        getPlayerOwner().incExp(Skills.AGILITY, 80, true);
                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), WildernessAgilityCourse.obstacles, WildernessAgilityCourse.lastObstacle, 1500);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case WildernessAgilityCourse.LEDGE :
                            p.message("you stand on the ledge");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Log") {
                                public void init() {
                                    addState(0, () -> {
                                        if (passObstacle) {
                                            Functions.movePlayer(getPlayerOwner(), 296, 112);
                                            return invoke(1, 1);
                                        } else {
                                            getPlayerOwner().message("you lose your footing and fall to the level below");
                                            return invoke(6, 2);
                                        }
                                    });
                                    addState(1, () -> {
                                        getPlayerOwner().message("and walk across");
                                        Functions.movePlayer(getPlayerOwner(), 297, 112);
                                        return invoke(2, 1);
                                    });
                                    addState(2, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 298, 112);
                                        return invoke(3, 1);
                                    });
                                    addState(3, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 299, 111);
                                        return invoke(4, 1);
                                    });
                                    addState(4, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 300, 111);
                                        return invoke(5, 1);
                                    });
                                    addState(5, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 301, 111);
                                        getPlayerOwner().incExp(Skills.AGILITY, 80, true);
                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), WildernessAgilityCourse.obstacles, WildernessAgilityCourse.lastObstacle, 1500);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                    addState(6, () -> {
                                        int ledgeDamage = ((int) (Math.round(getPlayerOwner().getSkills().getLevel(Skills.AGILITY) * 0.25)));
                                        Functions.movePlayer(getPlayerOwner(), 298, 2945);
                                        getPlayerOwner().message("You land painfully on the spikes");
                                        Functions.___playerTalk(getPlayerOwner(), null, "ouch");
                                        getPlayerOwner().damage(ledgeDamage);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                        case WildernessAgilityCourse.VINE :
                            p.message("You climb up the cliff");
                            p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Log") {
                                public void init() {
                                    addState(0, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 305, 118);
                                        return invokeNextState(1);
                                    });
                                    addState(1, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 304, 119);
                                        return invokeNextState(1);
                                    });
                                    addState(2, () -> {
                                        Functions.movePlayer(getPlayerOwner(), 304, 120);
                                        getPlayerOwner().incExp(Skills.AGILITY, 80, true);// COMPLETION OF THE COURSE.

                                        AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), WildernessAgilityCourse.obstacles, WildernessAgilityCourse.lastObstacle, 1500);
                                        getPlayerOwner().setBusy(false);
                                        return null;
                                    });
                                }
                            });
                            return null;
                    }
                    return null;
                });
            }
        };
    }

    private boolean succeed(Player player) {
        return Formulae.calcProductionSuccessful(52, Functions.getCurrentLevel(player, Skills.AGILITY), true, 102);
    }

    private int failRate() {
        return Functions.random(1, 5);
    }
}

