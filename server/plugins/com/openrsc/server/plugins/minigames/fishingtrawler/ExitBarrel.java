package com.openrsc.server.plugins.minigames.fishingtrawler;


import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class ExitBarrel implements ObjectActionListener , ObjectActionExecutiveListener {
    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return obj.getID() == 1070;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    getPlayerOwner().message("you climb onto the floating barrel");
                    return invokeNextState(3);
                });
                addState(1, () -> {
                    getPlayerOwner().message("and begin to kick your way to the shore");
                    return invokeNextState(3);
                });
                addState(2, () -> {
                    getPlayerOwner().message("you make it to the shore tired and weary");
                    return invokeNextState(3);
                });
                addState(3, () -> {
                    getPlayerOwner().teleport(550, 711);
                    getPlayerOwner().damage(3);
                    return null;
                });
            }
        };
    }
}

