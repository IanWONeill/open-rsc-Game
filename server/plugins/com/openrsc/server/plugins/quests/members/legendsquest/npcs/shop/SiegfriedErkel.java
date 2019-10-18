package com.openrsc.server.plugins.quests.members.legendsquest.npcs.shop;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
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


public final class SiegfriedErkel implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 60000, 150, 50, 2, new Item(ItemId.MITHRIL_SEED.id(), 6), new Item(ItemId.DUSTY_KEY.id(), 5), new Item(ItemId.SILVERLIGHT.id(), 4), new Item(ItemId.MAZE_KEY.id(), 3), new Item(ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id(), 1), new Item(ItemId.CAPE_OF_LEGENDS.id(), 3));

    @Override
    public GameStateEvent onTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getQuestStage(Quests.LEGENDS_QUEST) != (-1)) {
                        Functions.___npcTalk(p, n, "I'm sorry but the services of this shop are only for ", "the pleasure of those who are rightfull members of the ", "Legends Guild. I would get into serious trouble if I sold ", "a non-member an item from this store.");
                    } else {
                        Functions.___npcTalk(p, n, "Hello there and welcome to the shop of useful items.", "Can I help you at all?");
                        int option = Functions.___showMenu(p, n, "Yes please. What are you selling?", "No thanks");
                        if (option == 0) {
                            Functions.___npcTalk(p, n, "Take a look");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        } else
                            if (option == 1) {
                                Functions.___npcTalk(p, n, "Ok, well, if you change your mind, do pop back.");
                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.SIEGFRIED_ERKLE.id();
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

