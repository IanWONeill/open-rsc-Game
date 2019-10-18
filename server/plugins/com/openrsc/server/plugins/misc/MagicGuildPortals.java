package com.openrsc.server.plugins.misc;


import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


public class MagicGuildPortals implements WallObjectActionListener , WallObjectActionExecutiveListener {
    private static int[] MAGIC_PORTALS = new int[]{ 147, 148, 149 };

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
        return Functions.inArray(obj.getID(), MagicGuildPortals.MAGIC_PORTALS);
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(obj.getID(), MagicGuildPortals.MAGIC_PORTALS)) {
                        p.playerServerMessage(MessageType.QUEST, "you enter the magic portal");
                        if (obj.getID() == MagicGuildPortals.MAGIC_PORTALS[0]) {
                            p.teleport(212, 695);
                        } else
                            if (obj.getID() == MagicGuildPortals.MAGIC_PORTALS[1]) {
                                p.teleport(511, 1452);
                            } else
                                if (obj.getID() == MagicGuildPortals.MAGIC_PORTALS[2]) {
                                    p.teleport(362, 1515);
                                }


                        Functions.sleep(600);
                        Functions.displayTeleportBubble(p, p.getX(), p.getY(), false);
                    }
                    return null;
                });
            }
        };
    }
}

