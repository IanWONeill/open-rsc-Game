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


public class PineappleTree implements ObjectActionListener , ObjectActionExecutiveListener {
    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 430) {
                        p.message("you pick a pineapple");
                        Functions.addItem(p, ItemId.FRESH_PINEAPPLE.id(), 1);
                        if (!p.getCache().hasKey("pineapple_pick")) {
                            p.getCache().set("pineapple_pick", 1);
                        } else {
                            int pineappleCount = p.getCache().getInt("pineapple_pick");
                            p.getCache().set("pineapple_pick", pineappleCount + 1);
                            if (pineappleCount >= 4) {
                                Functions.replaceObjectDelayed(obj, 60000 * 8, 431);// 8 minutes respawn time.

                                p.getCache().remove("pineapple_pick");
                            }
                        }
                    }
                    if (obj.getID() == 431) {
                        p.message("there are no pineapples left on the tree");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return (obj.getID() == 431) || (obj.getID() == 430);
    }
}

