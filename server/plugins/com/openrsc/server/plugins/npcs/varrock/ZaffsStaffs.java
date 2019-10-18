package com.openrsc.server.plugins.npcs.varrock;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public final class ZaffsStaffs implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private Shop shop = null;

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.ZAFF.id();
    }

    @Override
    public Shop[] getShops(World world) {
        return new Shop[]{ getShop(world) };
    }

    @Override
    public boolean isMembers() {
        return false;
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Would you like to buy or sell some staffs?");
                    int option = Functions.___showMenu(p, n, "Yes please", "No, thank you");
                    if (option == 0) {
                        p.setAccessingShop(getShop(p.getWorld()));
                        ActionSender.showShop(p, getShop(p.getWorld()));
                    }
                    return null;
                });
            }
        };
    }

    public Shop getShop(World world) {
        if (shop == null) {
            shop = (world.getServer().getConfig().MEMBER_WORLD) ? new Shop(false, 30000, 100, 55, 2, new Item(ItemId.BATTLESTAFF.id(), 5), new Item(ItemId.STAFF.id(), 5), new Item(ItemId.MAGIC_STAFF.id(), 5), new Item(ItemId.STAFF_OF_AIR.id(), 2), new Item(ItemId.STAFF_OF_WATER.id(), 2), new Item(ItemId.STAFF_OF_EARTH.id(), 2), new Item(ItemId.STAFF_OF_FIRE.id(), 2)) : new Shop(false, 30000, 100, 55, 2, new Item(ItemId.STAFF.id(), 5), new Item(ItemId.MAGIC_STAFF.id(), 5), new Item(ItemId.STAFF_OF_AIR.id(), 2), new Item(ItemId.STAFF_OF_WATER.id(), 2), new Item(ItemId.STAFF_OF_EARTH.id(), 2), new Item(ItemId.STAFF_OF_FIRE.id(), 2));
        }
        return shop;
    }
}

