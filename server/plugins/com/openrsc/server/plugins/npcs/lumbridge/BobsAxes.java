package com.openrsc.server.plugins.npcs.lumbridge;


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


public final class BobsAxes implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 15000, 100, 60, 2, new Item(ItemId.BRONZE_PICKAXE.id(), 5), new Item(ItemId.BRONZE_AXE.id(), 10), new Item(ItemId.IRON_AXE.id(), 5), new Item(ItemId.STEEL_AXE.id(), 3), new Item(ItemId.IRON_BATTLE_AXE.id(), 5), new Item(ItemId.STEEL_BATTLE_AXE.id(), 2), new Item(ItemId.MITHRIL_BATTLE_AXE.id(), 1));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.BOB.id();
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
                    Functions.___npcTalk(p, n, "Hello. How can I help you?");
                    int option = Functions.___showMenu(p, n, "Give me a quest!", "Have you anything to sell?");
                    switch (option) {
                        case 0 :
                            Functions.___npcTalk(p, n, "Get yer own!");
                            break;
                        case 1 :
                            Functions.___npcTalk(p, n, "Yes, I buy and sell axes, take your pick! (or axe)");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                            break;
                    }
                    return null;
                });
            }
        };
    }
}

