package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;


import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class UndergroundPassWell implements ObjectActionListener , ObjectActionExecutiveListener {
    public static final int WELL = 814;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == UndergroundPassWell.WELL;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == UndergroundPassWell.WELL) {
                        Functions.___message(p, "you climb into the well");
                        if ((((p.getCache().hasKey("orb_of_light1") && p.getCache().hasKey("orb_of_light2")) && p.getCache().hasKey("orb_of_light3")) && p.getCache().hasKey("orb_of_light4")) || Functions.atQuestStages(p, Quests.UNDERGROUND_PASS, 7, 8, -1)) {
                            Functions.___message(p, "you feel the grip of icy hands all around you...");
                            p.teleport(722, 3461);
                            return invoke(1, 1);
                        } else {
                            p.damage(((int) (Functions.getCurrentLevel(p, Skills.HITS) * 0.2)));
                            Functions.displayTeleportBubble(p, obj.getX(), obj.getY(), false);
                            Functions.___message(p, "from below an icy blast of air chills you to your bones", "a mystical force seems to blast you back out of the well");
                            p.message("there must be a positive force near by!");
                        }
                    }
                    return null;
                });
                addState(1, () -> {
                    Functions.displayTeleportBubble(p, p.getX(), p.getY(), true);
                    p.message("..slowly dragging you futher down into the caverns");
                    return null;
                });
            }
        };
    }
}

