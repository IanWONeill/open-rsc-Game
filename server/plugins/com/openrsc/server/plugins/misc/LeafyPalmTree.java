package com.openrsc.server.plugins.misc;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class LeafyPalmTree implements ObjectActionListener , ObjectActionExecutiveListener {
    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == 1176;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() != 1176) {
                        return null;
                    }
                    getPlayerOwner().message("You give the palm tree a good shake.");
                    return invokeNextState(2);
                });
                addState(1, () -> {
                    getPlayerOwner().message("A palm leaf falls down.");
                    Functions.createGroundItem(ItemId.PALM_TREE_LEAF.id(), 1, obj.getX(), obj.getY(), p);
                    Functions.replaceObjectDelayed(obj, 15000, 33);
                    return null;
                });
            }
        };
    }
}

