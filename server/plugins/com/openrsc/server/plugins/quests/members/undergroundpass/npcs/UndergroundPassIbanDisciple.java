package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import java.util.concurrent.Callable;


public class UndergroundPassIbanDisciple implements PlayerKilledNpcListener , PlayerKilledNpcExecutiveListener {
    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return n.getID() == NpcId.IBAN_DISCIPLE.id();
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.IBAN_DISCIPLE.id()) {
                        n.killedBy(p);
                        if (p.getQuestStage(Quests.UNDERGROUND_PASS) == (-1)) {
                            Functions.___message(p, "you search the diciples remains");
                            if ((!Functions.hasItem(p, ItemId.STAFF_OF_IBAN.id())) && (!Functions.hasItem(p, ItemId.STAFF_OF_IBAN_BROKEN.id()))) {
                                p.message("and find a staff of iban");
                                Functions.addItem(p, ItemId.STAFF_OF_IBAN_BROKEN.id(), 1);
                            } else {
                                p.message("but find nothing");
                            }
                        } else {
                            Functions.createGroundItem(ItemId.ROBE_OF_ZAMORAK_TOP.id(), 1, p.getX(), p.getY(), p);
                            Functions.createGroundItem(ItemId.ROBE_OF_ZAMORAK_BOTTOM.id(), 1, p.getX(), p.getY(), p);
                        }
                    }
                    return null;
                });
            }
        };
    }
}

