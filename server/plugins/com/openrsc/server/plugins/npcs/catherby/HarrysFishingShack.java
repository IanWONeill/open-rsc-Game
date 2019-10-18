package com.openrsc.server.plugins.npcs.catherby;


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


public class HarrysFishingShack implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 3000, 100, 70, 2, new Item(ItemId.NET.id(), 3), new Item(ItemId.FISHING_ROD.id(), 3), new Item(ItemId.HARPOON.id(), 2), new Item(ItemId.LOBSTER_POT.id(), 2), new Item(ItemId.FISHING_BAIT.id(), 200), new Item(ItemId.BIG_NET.id(), 5), new Item(ItemId.RAW_SHRIMP.id(), 0), new Item(ItemId.RAW_SARDINE.id(), 0), new Item(ItemId.RAW_HERRING.id(), 0), new Item(ItemId.RAW_MACKEREL.id(), 0), new Item(ItemId.RAW_COD.id(), 0), new Item(ItemId.RAW_ANCHOVIES.id(), 0), new Item(ItemId.RAW_TUNA.id(), 0), new Item(ItemId.RAW_LOBSTER.id(), 0), new Item(ItemId.RAW_BASS.id(), 0), new Item(ItemId.RAW_SWORDFISH.id(), 0), new Item(ItemId.RAW_SHARK.id(), 0));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.HARRY.id();
    }

    @Override
    public Shop[] getShops(World world) {
        return new Shop[]{ shop };
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Welcome you can buy fishing equipment at my store", "We'll also buy fish that you catch off you");
                    final int option = Functions.___showMenu(p, n, false, "Let's see what you've got then", "Sorry, I'm not interested");
                    if (option == 0) {
                        Functions.___playerTalk(p, n, "Let's see what you've got then");
                        p.setAccessingShop(shop);
                        ActionSender.showShop(p, shop);
                    } else
                        if (option == 1) {
                            Functions.___playerTalk(p, n, "Sorry,I'm not interested");
                        }

                    return null;
                });
            }
        };
    }
}

