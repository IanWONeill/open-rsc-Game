package com.openrsc.server.plugins.npcs.ardougne.east;


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


public class BartenderFlyingHorseInn implements TalkToNpcListener , TalkToNpcExecutiveListener {
    public final int BARTENDER = NpcId.BARTENDER_ARDOUGNE.id();

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == BARTENDER;
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == BARTENDER) {
                        Functions.___npcTalk(p, n, "Would you like to buy a drink?");
                        Functions.___playerTalk(p, n, "What do you serve?");
                        Functions.___npcTalk(p, n, "Beer");
                        int menu = Functions.___showMenu(p, n, "I'll have a beer then", "I'll not have anything then");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "Ok, that'll be two coins");
                            if (Functions.hasItem(p, ItemId.COINS.id(), 2)) {
                                Functions.removeItem(p, ItemId.COINS.id(), 2);
                                Functions.addItem(p, ItemId.BEER.id(), 1);
                                p.message("You buy a pint of beer");
                            } else {
                                Functions.___playerTalk(p, n, "Oh dear. I don't seem to have enough money");
                            }
                        }
                    }
                    return null;
                });
            }
        };
    }
}

