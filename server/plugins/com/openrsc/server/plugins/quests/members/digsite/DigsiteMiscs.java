package com.openrsc.server.plugins.quests.members.digsite;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class DigsiteMiscs implements DropListener , DropExecutiveListener {
    @Override
    public boolean blockDrop(Player p, Item i) {
        return DataConversions.inArray(new int[]{ ItemId.UNIDENTIFIED_LIQUID.id(), ItemId.NITROGLYCERIN.id(), ItemId.MIXED_CHEMICALS_1.id(), ItemId.MIXED_CHEMICALS_2.id(), ItemId.EXPLOSIVE_COMPOUND.id() }, i.getID());
    }

    @Override
    public GameStateEvent onDrop(Player p, Item i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ItemId.UNIDENTIFIED_LIQUID.id()) {
                        p.message("bang!");
                        Functions.removeItem(p, ItemId.UNIDENTIFIED_LIQUID.id(), 1);
                        p.damage(((int) ((Functions.getCurrentLevel(p, Skills.HITS) * 0.3) + 5)));
                        Functions.___playerTalk(p, null, "Ow!");
                        p.message("The liquid exploded!");
                        p.message("You were injured by the burning liquid");
                    } else
                        if ((i.getID() == ItemId.MIXED_CHEMICALS_1.id()) || (i.getID() == ItemId.MIXED_CHEMICALS_2.id())) {
                            p.message("bang!");
                            Functions.removeItem(p, i.getID(), 1);
                            p.damage(((int) ((Functions.getCurrentLevel(p, Skills.HITS) / 2) + 6)));
                            Functions.___playerTalk(p, null, "Ow!");
                            p.message("The chemicals exploded!");
                            p.message("You were injured by the exploding liquid");
                        } else
                            if (i.getID() == ItemId.NITROGLYCERIN.id()) {
                                p.message("bang!");
                                Functions.removeItem(p, ItemId.NITROGLYCERIN.id(), 1);
                                p.damage(((int) ((Functions.getCurrentLevel(p, Skills.HITS) / 2) - 3)));
                                Functions.___playerTalk(p, null, "Ow!");
                                p.message("The nitroglycerin exploded!");
                                p.message("You were injured by the exploding liquid");
                            } else
                                if (i.getID() == ItemId.EXPLOSIVE_COMPOUND.id()) {
                                    Functions.___message(p, "bang!");
                                    Functions.removeItem(p, ItemId.EXPLOSIVE_COMPOUND.id(), 1);
                                    p.damage(61);
                                    Functions.___playerTalk(p, null, "Ow!");
                                    p.message("The compound exploded!");
                                    p.message("You were badly injured by the exploding liquid");
                                }



                    return null;
                });
            }
        };
    }
}

