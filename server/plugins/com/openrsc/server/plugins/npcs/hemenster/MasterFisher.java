package com.openrsc.server.plugins.npcs.hemenster;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class MasterFisher implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return p.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && (n.getID() == NpcId.MASTER_FISHER.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && (n.getID() == NpcId.MASTER_FISHER.id())) {
                        if (Functions.getCurrentLevel(p, Skills.FISHING) < 68) {
                            Functions.___npcTalk(p, n, "Hello only the top fishers are allowed in here");
                            p.message("You need a fishing level of 68 to enter");
                        } else {
                            Functions.___npcTalk(p, n, "Hello, welcome to the fishing guild", "Please feel free to make use of any of our facilities");
                        }
                    }
                    return null;
                });
            }
        };
    }
}

