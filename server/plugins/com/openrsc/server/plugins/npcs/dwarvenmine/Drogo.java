package com.openrsc.server.plugins.npcs.dwarvenmine;


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


public class Drogo implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 30000, 100, 70, 2, new Item(ItemId.HAMMER.id(), 4), new Item(ItemId.BRONZE_PICKAXE.id(), 4), new Item(ItemId.COPPER_ORE.id(), 0), new Item(ItemId.TIN_ORE.id(), 0), new Item(ItemId.IRON_ORE.id(), 0), new Item(ItemId.COAL.id(), 0), new Item(ItemId.BRONZE_BAR.id(), 0), new Item(ItemId.IRON_BAR.id(), 0), new Item(ItemId.GOLD_BAR.id(), 0));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.DROGO.id();
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
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Ello");
                    int m = // do not send over
                    Functions.___showMenu(p, n, false, "Do you want to trade?", "Hello shorty", "Why don't you ever restock ores and bars?");
                    if (m == 0) {
                        Functions.___playerTalk(p, n, "Do you want to trade?");
                        Functions.___npcTalk(p, n, "Yeah sure, I run a mining store.");
                        ActionSender.showShop(p, shop);
                    } else
                        if (m == 1) {
                            Functions.___playerTalk(p, n, "Hello Shorty.");
                            Functions.___npcTalk(p, n, "I may be short, but at least I've got manners");
                        } else
                            if (m == 2) {
                                Functions.___playerTalk(p, n, "Why don't you ever restock ores and bars?");
                                Functions.___npcTalk(p, n, "The only ores and bars I sell are those sold to me");
                            }


                    return null;
                });
            }
        };
    }
}

