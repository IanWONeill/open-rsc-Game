package com.openrsc.server.plugins.npcs.tutorial;


import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class ControlsGuide implements TalkToNpcListener , TalkToNpcExecutiveListener {
    /**
     *
     *
     * @author Davve
    Tutorial island second room guide
     */
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Hello I'm here to tell you more about the game's controls", "Most of your options and character information", "can be accesed by the menus in the top right corner of the screen", "moving your mouse over the map icon", "which is the second icon from the right", "gives you a view of the area you are in", "clicking on this map is an effective way of walking around", "though if the route is blocked, for example by a closed door", "then your character won't move", "Also notice the compass on the map which may be of help to you");
                    Functions.___playerTalk(p, n, "Thankyou for your help");
                    Functions.___npcTalk(p, n, "Now carry on to speak to the combat instructor");
                    if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") < 15))
                        p.getCache().set("tutorial", 15);

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.CONTROLS_GUIDE.id();
    }
}

