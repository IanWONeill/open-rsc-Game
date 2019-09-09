package com.openrsc.server.plugins.defaults;

import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.DefaultHandler;
import com.openrsc.server.plugins.listeners.action.*;

/**
 * Theoretically we do not need to block, as everything that is not handled
 * should be handled here.
 *
 * @author openfrog
 */

public class Default implements DefaultHandler, TalkToNpcListener, ObjectActionListener,
	InvUseOnObjectListener, InvUseOnWallObjectListener,
	InvUseOnNpcListener, WallObjectActionListener {

	public static final DoorAction doors = new DoorAction();
	private static final Ladders ladders = new Ladders();

	@Override
	public GameStateEvent onInvUseOnNpc(final Player player, final Npc npc, final Item item) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
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
		return new GameStateEvent(owner.getWorld(), owner, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (doors.blockInvUseOnWallObject(object, item, owner)) {
						doors.onInvUseOnWallObject(object, item, owner);
					} else {
						owner.message("Nothing interesting happens");
						//possibly unhandled
						System.out.println("InvUseOnObject unhandled: item " + item.getID()
							+ " used with object: " + object.getID());
					}

					return null;
				});
			}
		};
	}

	@Override
	public GameStateEvent onObjectAction(final GameObject obj, final String command, final Player player) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (doors.blockObjectAction(obj, command, player)) {
						doors.onObjectAction(obj, command, player);
					} else if (ladders.blockObjectAction(obj, command, player)) {
						ladders.onObjectAction(obj, command, player);
					} else
						player.message("Nothing interesting happens");

					return null;
				});
			}
		};
	}

	@Override
	public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					p.message(
						"The " + n.getDef().getName()
							+ " does not appear interested in talking");

					return null;
				});
			}
		};
	}

	@Override
	public GameStateEvent onInvUseOnWallObject(GameObject object, Item item, Player owner) {
		return new GameStateEvent(owner.getWorld(), owner, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (doors.blockInvUseOnWallObject(object, item, owner)) {
						doors.onInvUseOnWallObject(object, item, owner);
					} else
						owner.message("Nothing interesting happens");

					return null;
				});
			}
		};
	}

	@Override
	public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (doors.blockWallObjectAction(obj, click, p)) {
						doors.onWallObjectAction(obj, click, p);
					} else
						p.message("Nothing interesting happens");

					return null;
				});
			}
		};
	}
}
