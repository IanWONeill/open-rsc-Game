package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;


import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class LegendsQuestGates implements ObjectActionListener , ObjectActionExecutiveListener {
    public static final int LEGENDS_HALL_DOOR = 1080;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == LegendsQuestGates.LEGENDS_HALL_DOOR;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == LegendsQuestGates.LEGENDS_HALL_DOOR) {
                        if (command.equalsIgnoreCase("open")) {
                            if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 11) || (p.getQuestStage(Quests.LEGENDS_QUEST) == (-1))) {
                                Functions.doDoor(obj, p, 497);
                                p.message("You open the impressive wooden doors.");
                                if (p.getY() <= 539) {
                                    p.teleport(513, 541);
                                } else {
                                    p.teleport(513, 539);
                                }
                            } else {
                                Functions.___message(p, 1300, "You need to complete the Legends Guild Quest");
                                Functions.___message(p, 1200, "before you can enter the Legends Guild");
                            }
                        } else
                            if (command.equalsIgnoreCase("search")) {
                                p.message("Nothing interesting happens");
                            }

                    }
                    return null;
                });
            }
        };
    }
}

