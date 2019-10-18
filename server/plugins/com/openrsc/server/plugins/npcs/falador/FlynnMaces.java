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


public final class FlynnMaces implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 25000, 100, 60, 1, new Item(ItemId.BRONZE_MACE.id(), 5), new Item(ItemId.IRON_MACE.id(), 4), new Item(ItemId.STEEL_MACE.id(), 4), new Item(ItemId.MITHRIL_MACE.id(), 3), new Item(ItemId.ADAMANTITE_MACE.id(), 2));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.FLYNN.id();
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
                    Functions.___npcTalk(p, n, "Hello do you want to buy or sell any maces?");
                    int opt = // do not send over
                    Functions.___showMenu(p, n, false, "No thanks", "Well I'll have a look anyway");
                    if (opt == 0) {
                        Functions.___playerTalk(p, n, "no thanks");
                    } else
                        if (opt == 1) {
                            Functions.___playerTalk(p, n, "Well I'll have a look anyway");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        }

                    return null;
                });
            }
        };
    }
}

