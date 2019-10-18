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


public final class WaynesChains implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 25000, 100, 65, 1, new Item(ItemId.BRONZE_CHAIN_MAIL_BODY.id(), 3), new Item(ItemId.IRON_CHAIN_MAIL_BODY.id(), 2), new Item(ItemId.STEEL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.BLACK_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(), 1));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.WAYNE.id();
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
                    if (n.getID() == NpcId.WAYNE.id()) {
                        Functions.___npcTalk(p, n, "Welcome to Wayne's chains", "Do you wanna buy or sell some chain mail?");
                        int option = // do not send over
                        Functions.___showMenu(p, n, false, "Yes please", "No thanks");
                        if (option == 0) {
                            Functions.___playerTalk(p, n, "Yes Please");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        } else
                            if (option == 1) {
                                Functions.___playerTalk(p, n, "No thanks");
                            }

                    }
                    return null;
                });
            }
        };
    }
}

