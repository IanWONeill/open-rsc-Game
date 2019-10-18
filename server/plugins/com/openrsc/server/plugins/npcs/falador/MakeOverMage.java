package com.openrsc.server.plugins.npcs.falador;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class MakeOverMage implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Are you happy with your looks?", "If not I can change them for the cheap cheap price", "Of 3000 coins");
                    int opt = Functions.___showMenu(p, n, "I'm happy with how I look thank you", "Yes change my looks please");
                    if (opt == 1) {
                        if (!Functions.hasItem(p, ItemId.COINS.id(), 3000)) {
                            Functions.___playerTalk(p, n, "I'll just go and get the cash");
                        } else {
                            Functions.removeItem(p, ItemId.COINS.id(), 3000);
                            p.setChangingAppearance(true);
                            ActionSender.sendAppearanceScreen(p);
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.MAKE_OVER_MAGE.id();
    }
}

