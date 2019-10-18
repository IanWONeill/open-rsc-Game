package com.openrsc.server.plugins.misc;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class MuddyChest implements InvUseOnObjectListener , ObjectActionListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener {
    private final int MUDDY_CHEST = 222;

    private final int MUDDY_CHEST_OPEN = 221;

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == MUDDY_CHEST) {
                        p.message("the chest is locked");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == MUDDY_CHEST) && (item.getID() == ItemId.MUDDY_KEY.id())) {
                        int respawnTime = 3000;
                        p.message("you unlock the chest with your key");
                        Functions.replaceObjectDelayed(obj, respawnTime, MUDDY_CHEST_OPEN);
                        p.message("You find some treasure in the chest");
                        Functions.removeItem(p, ItemId.MUDDY_KEY.id(), 1);// remove the muddy key.

                        Functions.addItem(p, ItemId.UNCUT_RUBY.id(), 1);
                        Functions.addItem(p, ItemId.MITHRIL_BAR.id(), 1);
                        Functions.addItem(p, ItemId.LAW_RUNE.id(), 2);
                        Functions.addItem(p, ItemId.ANCHOVIE_PIZZA.id(), 1);
                        Functions.addItem(p, ItemId.MITHRIL_DAGGER.id(), 1);
                        Functions.addItem(p, ItemId.COINS.id(), 50);
                        Functions.addItem(p, ItemId.DEATH_RUNE.id(), 2);
                        Functions.addItem(p, ItemId.CHAOS_RUNE.id(), 10);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == MUDDY_CHEST;
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return (obj.getID() == MUDDY_CHEST) && (item.getID() == ItemId.MUDDY_KEY.id());
    }
}

