package com.openrsc.server.plugins.npcs.taverly;


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


public class HelemosShop implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 60000, 100, 55, 3, new Item(ItemId.DRAGON_AXE.id(), 1));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.HELEMOS.id();
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
                    Functions.___npcTalk(p, n, "Welcome to the hero's guild");
                    final int option = // do not send over
                    Functions.___showMenu(p, n, false, "So do you sell anything here?", "So what can I do here?");
                    if (option == 0) {
                        Functions.___playerTalk(p, n, "So do you sell anything here?");
                        Functions.___npcTalk(p, n, "Why yes we do run an exclusive shop for our members");
                        p.setAccessingShop(shop);
                        ActionSender.showShop(p, shop);
                    } else
                        if (option == 1) {
                            Functions.___playerTalk(p, n, "so what can I do here?");
                            Functions.___npcTalk(p, n, "Look around there are all sorts of things to keep our members entertained");
                        }

                    return null;
                });
            }
        };
    }
}

