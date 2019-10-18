package com.openrsc.server.plugins.npcs.shilo;


import com.openrsc.server.constants.ItemId;
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


public class Serevel implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.SEREVEL.id()) {
                        Functions.___playerTalk(p, n, "Hello");
                        Functions.___npcTalk(p, n, "Hello Bwana.", "Are you interested in buying a ticket for the 'Lady of the Waves'?", "It's a ship that can take you to either Port Sarim or Khazard Port", "The ship lies west of Shilo Village and south of Cairn Island.", "The tickets cost 100 Gold Pieces.", "Would you like to purchase a ticket Bwana?");
                        int menu = Functions.___showMenu(p, n, "Yes, that sounds great!", "No thanks.");
                        if (menu == 0) {
                            if (Functions.hasItem(p, ItemId.COINS.id(), 100)) {
                                Functions.removeItem(p, ItemId.COINS.id(), 100);
                                Functions.___npcTalk(p, n, "Great, nice doing business with you.");
                                Functions.addItem(p, ItemId.SHIP_TICKET.id(), 1);
                            } else {
                                Functions.___npcTalk(p, n, "Sorry Bwana, you don't have enough money.", "Come back when you have 100 Gold Pieces.");
                            }
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "Fair enough Bwana, let me know if you change your mind.");
                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.SEREVEL.id();
    }
}

