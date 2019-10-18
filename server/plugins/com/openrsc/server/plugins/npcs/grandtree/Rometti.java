package com.openrsc.server.plugins.npcs.grandtree;


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


public final class Rometti implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 3000, 100, 55, 1, new Item(ItemId.GNOME_ROBE_PINK.id(), 5), new Item(ItemId.GNOME_ROBE_GREEN.id(), 5), new Item(ItemId.GNOME_ROBE_PURPLE.id(), 5), new Item(ItemId.GNOME_ROBE_CREAM.id(), 5), new Item(ItemId.GNOME_ROBE_BLUE.id(), 5), new Item(ItemId.GNOMESHAT_PINK.id(), 5), new Item(ItemId.GNOMESHAT_GREEN.id(), 5), new Item(ItemId.GNOMESHAT_PURPLE.id(), 5), new Item(ItemId.GNOMESHAT_CREAM.id(), 5), new Item(ItemId.GNOMESHAT_BLUE.id(), 5), new Item(ItemId.GNOME_TOP_PINK.id(), 5), new Item(ItemId.GNOME_TOP_GREEN.id(), 5), new Item(ItemId.GNOME_TOP_PURPLE.id(), 5), new Item(ItemId.GNOME_TOP_CREAM.id(), 5), new Item(ItemId.GNOME_TOP_BLUE.id(), 5), new Item(ItemId.BOOTS_PINK.id(), 5), new Item(ItemId.BOOTS_GREEN.id(), 5), new Item(ItemId.BOOTS_PURPLE.id(), 5), new Item(ItemId.BOOTS_CREAM.id(), 5), new Item(ItemId.BOOTS_BLUE.id(), 5));

    @Override
    public GameStateEvent onTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___playerTalk(p, n, "hello");
                    Functions.___npcTalk(p, n, "hello traveller", "have a look at my latest range of gnome fashion", "rometti is the ultimate label in gnome high society");
                    Functions.___playerTalk(p, n, "really");
                    Functions.___npcTalk(p, n, "pastels are all the rage this season");
                    int option = // do not send over
                    Functions.___showMenu(p, n, false, "i've no time for fashion", "ok then let's have a look");
                    switch (option) {
                        case 0 :
                            Functions.___playerTalk(p, n, "i've no time for fashion");
                            Functions.___npcTalk(p, n, "hmm...i did wonder");
                            break;
                        case 1 :
                            Functions.___playerTalk(p, n, "ok then, let's have a look");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                            break;
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.ROMETTI.id();
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

