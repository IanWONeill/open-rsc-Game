package com.openrsc.server.plugins.npcs.khazard;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public final class KhazardBartender implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.KHAZARD_BARTENDER.id()) {
                        Functions.___playerTalk(p, n, "Hello");
                        Functions.___npcTalk(p, n, "Hello, what can i get you? we have all sorts of brew");
                        int bar = Functions.___showMenu(p, n, "I'll have a beer please", "I'd like a khali brew please", "Got any news?");
                        if (bar == 0) {
                            Functions.___npcTalk(p, n, "There you go, that's one gold coin");
                            p.getInventory().add(new Item(ItemId.BEER.id()));
                            p.getInventory().remove(ItemId.COINS.id(), 1);
                        } else
                            if (bar == 1) {
                                Functions.___npcTalk(p, n, "There you go", "No charge");
                                Functions.addItem(p, ItemId.KHALI_BREW.id(), 1);
                            } else
                                if (bar == 2) {
                                    Functions.___npcTalk(p, n, "Well have you seen the famous khazard fight arena?", "I've seen some grand battles in my time..", "Ogres, goblins, even dragons, they all come to fight", "The poor slaves of general khazard");
                                }


                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.KHAZARD_BARTENDER.id();
    }
}

