package com.openrsc.server.plugins.misc;


import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import java.util.concurrent.Callable;


public class SalarinTheTwistedMageAI implements PlayerMageNpcListener , PlayerMageNpcExecutiveListener {
    /* Player maging Salarin the twisted AI - Just to degenerate ATTACK AND STRENGTH if over 2 in said skill. */
    @Override
    public boolean blockPlayerMageNpc(Player p, Npc n) {
        return (n.getID() == NpcId.SALARIN_THE_TWISTED.id()) && ((p.getSkills().getLevel(Skills.ATTACK) > 2) || (p.getSkills().getLevel(Skills.STRENGTH) > 2));
    }

    @Override
    public GameStateEvent onPlayerMageNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((n.getID() == NpcId.SALARIN_THE_TWISTED.id()) && ((p.getSkills().getLevel(Skills.ATTACK) > 2) || (p.getSkills().getLevel(Skills.STRENGTH) > 2))) {
                        if (!p.withinRange(n, 5))
                            return null;

                        n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Amshalaraz Nithcosh dimarilo", p));
                        Functions.sleep(600);
                        p.message("You suddenly feel much weaker");
                        p.getSkills().setLevel(Skills.ATTACK, 0);
                        p.getSkills().setLevel(Skills.STRENGTH, 0);
                    }
                    return null;
                });
            }
        };
    }
}

