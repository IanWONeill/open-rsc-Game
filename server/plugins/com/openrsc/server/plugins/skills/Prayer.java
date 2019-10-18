package com.openrsc.server.plugins.skills;


import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


public class Prayer implements ObjectActionListener , ObjectActionExecutiveListener {
    @Override
    public GameStateEvent onObjectAction(final GameObject object, String command, Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (command.equalsIgnoreCase("recharge at")) {
                        int maxPray = Functions.getMaxLevel(getPlayerOwner(), Skills.PRAYER) + (object.getID() == 200 ? 2 : 0);
                        if (Functions.getCurrentLevel(getPlayerOwner(), Skills.PRAYER) == maxPray) {
                            getPlayerOwner().playerServerMessage(MessageType.QUEST, "You already have full prayer points");
                        } else {
                            getPlayerOwner().playerServerMessage(MessageType.QUEST, "You recharge your prayer points");
                            getPlayerOwner().playSound("recharge");
                            if (Functions.getCurrentLevel(getPlayerOwner(), Skills.PRAYER) < maxPray) {
                                getPlayerOwner().getSkills().setLevel(Skills.PRAYER, maxPray);
                            }
                        }
                        if ((object.getID() == 625) && (object.getY() == 3573)) {
                            return invoke(1, 1);
                        }
                    }
                    return null;
                });
                addState(1, () -> {
                    Functions.___message(getPlayerOwner(), "Suddenly a trapdoor opens beneath you");
                    getPlayerOwner().teleport(608, 3525);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return command.equals("recharge at");
    }
}

