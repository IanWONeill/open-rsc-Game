package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;


import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class UndergroundPassPuzzle implements ObjectActionListener , ObjectActionExecutiveListener {
    public static int WALK_HERE_ROCK_EAST = 792;

    public static int WALK_HERE_ROCK_WEST = 793;

    public static int FAIL_GRILL = 782;

    public static int LEVER = 801;

    public static int CAGE = 802;

    /**
     * Tile puzzle grills
     */
    public static int[] WORKING_GRILLS = new int[]{ 777, 785, 786, 787, 788, 789, 790, 791 };

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return (((Functions.inArray(obj.getID(), UndergroundPassPuzzle.WORKING_GRILLS) || (obj.getID() == UndergroundPassPuzzle.FAIL_GRILL)) || (obj.getID() == UndergroundPassPuzzle.WALK_HERE_ROCK_EAST)) || (obj.getID() == UndergroundPassPuzzle.WALK_HERE_ROCK_WEST)) || (obj.getID() == UndergroundPassPuzzle.LEVER);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(obj.getID(), UndergroundPassPuzzle.WORKING_GRILLS)) {
                        getPlayerOwner().message("you step onto the metal grill");
                        getPlayerOwner().message("you tread carefully as you move forward");
                        if (obj.getID() == 777) {
                            getPlayerOwner().teleport(681, 3446);
                        } else
                            if (obj.getID() == 785) {
                                getPlayerOwner().teleport(683, 3446);
                            } else
                                if (obj.getID() == 786) {
                                    getPlayerOwner().teleport(683, 3448);
                                } else
                                    if (obj.getID() == 787) {
                                        getPlayerOwner().teleport(685, 3448);
                                    } else
                                        if (obj.getID() == 788) {
                                            getPlayerOwner().teleport(687, 3448);
                                        } else
                                            if (obj.getID() == 789) {
                                                getPlayerOwner().teleport(687, 3450);
                                            } else
                                                if (obj.getID() == 790) {
                                                    getPlayerOwner().teleport(687, 3452);
                                                } else
                                                    if (obj.getID() == 791) {
                                                        getPlayerOwner().teleport(689, 3452);
                                                    }







                    } else
                        if (obj.getID() == UndergroundPassPuzzle.FAIL_GRILL) {
                            Functions.___message(p, "you step onto the metal grill");
                            getPlayerOwner().message("it's a trap");
                            getPlayerOwner().teleport(711, 3464);
                            return invoke(1, 3);
                        } else
                            if (obj.getID() == UndergroundPassPuzzle.WALK_HERE_ROCK_EAST) {
                                getPlayerOwner().teleport(679, 3447);
                            } else
                                if (obj.getID() == UndergroundPassPuzzle.WALK_HERE_ROCK_WEST) {
                                    getPlayerOwner().walkToEntity(689, 3452);
                                } else
                                    if (obj.getID() == UndergroundPassPuzzle.LEVER) {
                                        Functions.___message(getPlayerOwner(), "you pull on the lever", "you hear a loud mechanical churning");
                                        GameObject cage_closed = new GameObject(getPlayerOwner().getWorld(), Point.location(690, 3449), UndergroundPassPuzzle.CAGE, 6, 0);
                                        GameObject cage_open = new GameObject(getPlayerOwner().getWorld(), Point.location(690, 3449), UndergroundPassPuzzle.CAGE + 1, 6, 0);
                                        getPlayerOwner().getWorld().registerGameObject(cage_open);
                                        getPlayerOwner().getWorld().delayedSpawnObject(cage_closed.getLoc(), 5000);
                                        getPlayerOwner().message("as the huge railing raises to the cave roof");
                                        getPlayerOwner().message("the cage lowers behind you");
                                        getPlayerOwner().teleport(690, 3451);
                                    }




                    return null;
                });
                addState(1, () -> {
                    Functions.___message(p, "you fall onto a pit of spikes");
                    getPlayerOwner().teleport(679, 3448);
                    getPlayerOwner().damage(((int) (Functions.getCurrentLevel(getPlayerOwner(), Skills.HITS) * 0.2)));
                    getPlayerOwner().message("you crawl out of the pit");
                    getPlayerOwner().getWorld().replaceGameObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), 778, obj.getDirection(), obj.getType()));
                    getPlayerOwner().getWorld().delayedSpawnObject(obj.getLoc(), 1000);
                    return invoke(2, 3);
                });
                addState(2, () -> {
                    getPlayerOwner().message("and off the metal grill");
                    return null;
                });
            }
        };
    }
}

