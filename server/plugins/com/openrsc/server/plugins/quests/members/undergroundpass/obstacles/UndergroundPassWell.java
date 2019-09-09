package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassWell implements ObjectActionListener, ObjectActionExecutiveListener {

	public static final int WELL = 814;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == WELL;
	}

	@Override
	public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					if (obj.getID() == WELL) {
						message(p, "you climb into the well");
						if ((p.getCache().hasKey("orb_of_light1") && p.getCache().hasKey("orb_of_light2") && p.getCache().hasKey("orb_of_light3") && p.getCache().hasKey("orb_of_light4")) ||
							atQuestStages(p, Quests.UNDERGROUND_PASS, 7, 8, -1)) {
							message(p, "you feel the grip of icy hands all around you...");
							p.teleport(722, 3461);
							return invoke(1, 1);
						} else {
							p.damage((int) (getCurrentLevel(p, Skills.HITS) * 0.2D));
							displayTeleportBubble(p, obj.getX(), obj.getY(), false);
							message(p, "from below an icy blast of air chills you to your bones",
								"a mystical force seems to blast you back out of the well");
							p.message("there must be a positive force near by!");
						}
					}
					return null;
				});
				addState(1, () -> {
					displayTeleportBubble(p, p.getX(), p.getY(), true);
					p.message("..slowly dragging you futher down into the caverns");
					return null;
				});
			}
		};
	}
}
