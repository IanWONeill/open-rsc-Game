package com.openrsc.server.plugins.misc;


import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class Bed implements ObjectActionListener , ObjectActionExecutiveListener {
    @Override
    public GameStateEvent onObjectAction(final GameObject object, String command, Player owner) {
        return new GameStateEvent(owner.getWorld(), owner, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((command.equalsIgnoreCase("rest") || command.equalsIgnoreCase("sleep")) && (!owner.isSleeping())) || command.equalsIgnoreCase("lie in")) {
                        ActionSender.sendEnterSleep(owner);
                        // Crude Bed is like Sleeping Bag.
                        if ((object.getID() == 1035) || (object.getID() == 1162))
                            owner.startSleepEvent(false);
                        else
                            owner.startSleepEvent(true);

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return (command.equals("rest") || command.equals("sleep")) || command.equals("lie in");
    }
}

