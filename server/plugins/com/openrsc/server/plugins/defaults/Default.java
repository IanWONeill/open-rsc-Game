package com.openrsc.server.plugins.defaults;


import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.DefaultHandler;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import java.util.concurrent.Callable;


/**
 * Theoretically we do not need to block, as everything that is not handled
 * should be handled here.
 *
 * @author openfrog
 */
public class Default implements DefaultHandler , InvUseOnNpcListener , InvUseOnObjectListener , InvUseOnWallObjectListener , ObjectActionListener , TalkToNpcListener , WallObjectActionListener {
    public static final DoorAction doors = new DoorAction();

    private static final Ladders ladders = new Ladders();

    @Override
    public GameStateEvent onInvUseOnNpc(final Player player, final Npc npc, final Item item) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    player.message("Nothing interesting happens");
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onInvUseOnObject(final GameObject object, final Item item, final Player owner) {
        return new GameStateEvent(owner.getWorld(), owner, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Default.doors.blockInvUseOnWallObject(object, item, owner)) {
                        Default.doors.onInvUseOnWallObject(object, item, owner);
                    } else {
                        owner.message("Nothing interesting happens");
                        // possibly unhandled
                        System.out.println((("InvUseOnObject unhandled: item " + item.getID()) + " used with object: ") + object.getID());
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onObjectAction(final GameObject obj, final String command, final Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Default.doors.blockObjectAction(obj, command, player)) {
                        Default.doors.onObjectAction(obj, command, player);
                    } else
                        if (Default.ladders.blockObjectAction(obj, command, player)) {
                            Default.ladders.onObjectAction(obj, command, player);
                        } else
                            player.message("Nothing interesting happens");


                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    p.message(("The " + n.getDef().getName()) + " does not appear interested in talking");
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onInvUseOnWallObject(GameObject object, Item item, Player owner) {
        return new GameStateEvent(owner.getWorld(), owner, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Default.doors.blockInvUseOnWallObject(object, item, owner)) {
                        Default.doors.onInvUseOnWallObject(object, item, owner);
                    } else
                        owner.message("Nothing interesting happens");

                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Default.doors.blockWallObjectAction(obj, click, p)) {
                        Default.doors.onWallObjectAction(obj, click, p);
                    } else
                        p.message("Nothing interesting happens");

                    return null;
                });
            }
        };
    }
}

