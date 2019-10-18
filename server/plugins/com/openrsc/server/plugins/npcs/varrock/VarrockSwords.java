package com.openrsc.server.plugins.npcs.varrock;


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


public final class VarrockSwords implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(ItemId.BRONZE_SHORT_SWORD.id(), 5), new Item(ItemId.IRON_SHORT_SWORD.id(), 4), new Item(ItemId.STEEL_SHORT_SWORD.id(), 4), new Item(ItemId.BLACK_SHORT_SWORD.id(), 3), new Item(ItemId.MITHRIL_SHORT_SWORD.id(), 3), new Item(ItemId.ADAMANTITE_SHORT_SWORD.id(), 2), new Item(ItemId.BRONZE_LONG_SWORD.id(), 4), new Item(ItemId.IRON_LONG_SWORD.id(), 3), new Item(ItemId.STEEL_LONG_SWORD.id(), 3), new Item(ItemId.BLACK_LONG_SWORD.id(), 2), new Item(ItemId.MITHRIL_LONG_SWORD.id(), 2), new Item(ItemId.ADAMANTITE_LONG_SWORD.id(), 1), new Item(ItemId.BRONZE_DAGGER.id(), 10), new Item(ItemId.IRON_DAGGER.id(), 6), new Item(ItemId.STEEL_DAGGER.id(), 5), new Item(ItemId.BLACK_DAGGER.id(), 4), new Item(ItemId.MITHRIL_DAGGER.id(), 3), new Item(ItemId.ADAMANTITE_DAGGER.id(), 2));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return ((n.getID() == NpcId.SHOPKEEPER_VARROCK_SWORD.id()) || (n.getID() == NpcId.SHOP_ASSISTANT_VARROCK_SWORD.id())) && p.getLocation().inBounds(133, 522, 138, 527);
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
                    if ((n.getID() == NpcId.SHOPKEEPER_VARROCK_SWORD.id()) || ((n.getID() == NpcId.SHOP_ASSISTANT_VARROCK_SWORD.id()) && p.getLocation().inBounds(133, 522, 138, 527))) {
                        Functions.___npcTalk(p, n, "Hello bold adventurer", "Can I interest you in some swords?");
                        final String[] options = new String[]{ "Yes please", "No, I'm OK for swords right now" };
                        int option = Functions.___showMenu(p, n, options);
                        switch (option) {
                            case 0 :
                                p.setAccessingShop(shop);
                                ActionSender.showShop(p, shop);
                                break;
                            case 1 :
                                Functions.___npcTalk(p, n, "Come back if you need any");
                                break;
                        }
                    }
                    return null;
                });
            }
        };
    }
}

