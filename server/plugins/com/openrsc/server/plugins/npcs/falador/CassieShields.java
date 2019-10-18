package com.openrsc.server.plugins.npcs.falador;


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


public final class CassieShields implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 25000, 100, 60, 2, new Item(ItemId.WOODEN_SHIELD.id(), 5), new Item(ItemId.BRONZE_SQUARE_SHIELD.id(), 3), new Item(ItemId.BRONZE_KITE_SHIELD.id(), 3), new Item(ItemId.IRON_SQUARE_SHIELD.id(), 2), new Item(ItemId.IRON_KITE_SHIELD.id(), 0), new Item(ItemId.STEEL_SQUARE_SHIELD.id(), 0), new Item(ItemId.STEEL_KITE_SHIELD.id(), 0), new Item(ItemId.MITHRIL_SQUARE_SHIELD.id(), 0));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.CASSIE.id();
    }

    @Override
    public Shop[] getShops(World world) {
        return new Shop[]{ shop };
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
                    if (n.getID() == NpcId.CASSIE.id()) {
                        Functions.___playerTalk(p, n, "What wares are you selling?");
                        Functions.___npcTalk(p, n, "I buy and sell shields", "Do you want to trade?");
                        int option = Functions.___showMenu(p, n, "Yes please", "No thank you");
                        if (option == 0) {
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        }
                    }
                    return null;
                });
            }
        };
    }
}

