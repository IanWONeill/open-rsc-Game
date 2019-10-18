package com.openrsc.server.plugins.npcs.ardougne.west;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public final class Chadwell implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(true, 3000, 130, 40, 3, new Item(ItemId.ROPE.id(), 7), new Item(ItemId.BRONZE_PICKAXE.id(), 10), new Item(ItemId.SALMON.id(), 2), new Item(ItemId.BUCKET.id(), 2), new Item(ItemId.TINDERBOX.id(), 10), new Item(ItemId.MEAT_PIE.id(), 2), new Item(ItemId.HAMMER.id(), 5), new Item(ItemId.BREAD.id(), 10), new Item(ItemId.BOOTS.id(), 10), new Item(ItemId.POT.id(), 3), new Item(ItemId.COOKEDMEAT.id(), 2), new Item(ItemId.LONGBOW.id(), 2), new Item(ItemId.BRONZE_ARROWS.id(), 200), new Item(ItemId.SLEEPING_BAG.id(), 10));

    @Override
    public GameStateEvent onTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.CHADWELL.id();
    }

    @Override
    public Shop[] getShops(World world) {
        return new Shop[]{ shop };
    }

    @Override
    public boolean isMembers() {
        return true;
    }
}

