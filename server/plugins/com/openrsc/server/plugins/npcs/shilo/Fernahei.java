package com.openrsc.server.plugins.npcs.shilo;


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


public class Fernahei implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 15000, 100, 70, 2, new Item(ItemId.FISHING_ROD.id(), 5), new Item(ItemId.FLY_FISHING_ROD.id(), 5), new Item(ItemId.FISHING_BAIT.id(), 200), new Item(ItemId.FEATHER.id(), 200), new Item(ItemId.RAW_TROUT.id(), 0), new Item(ItemId.RAW_PIKE.id(), 0), new Item(ItemId.RAW_SALMON.id(), 0));

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.FERNAHEI.id()) {
                        Functions.___npcTalk(p, n, "Welcome to Fernahei's Fishing Shop Bwana!", "Would you like to see my items?");
                        int menu = Functions.___showMenu(p, n, "Yes please!", "No, but thanks for the offer.");
                        if (menu == 0) {
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "That's fine and thanks for your interest.");
                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.FERNAHEI.id();
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

