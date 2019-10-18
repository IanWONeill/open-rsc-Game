package com.openrsc.server.plugins.npcs.ardougne.east;


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
import java.time.Instant;
import java.util.concurrent.Callable;


public class FurMerchant implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 15000, 120, 95, 2, new Item(ItemId.FUR.id(), 3), new Item(ItemId.GREY_WOLF_FUR.id(), 3));

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getCache().hasKey("furStolen") && (Instant.now().getEpochSecond() < (p.getCache().getLong("furStolen") + 1200))) {
                        Functions.___npcTalk(p, n, "Do you really think I'm going to buy something", "That you have just stolen from me", "guards guards");
                        Npc attacker = Functions.getNearestNpc(p, NpcId.KNIGHT.id(), 5);// Knight first

                        if (attacker == null)
                            attacker = Functions.getNearestNpc(p, NpcId.GUARD_ARDOUGNE.id(), 5);
                        // Guard second

                        if (attacker != null)
                            attacker.setChasing(p);

                    } else {
                        Functions.___npcTalk(p, n, "would you like to do some fur trading?");
                        int menu = Functions.___showMenu(p, n, false, "yes please", "No thank you");
                        if (menu == 0) {
                            Functions.___playerTalk(p, n, "Yes please");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        } else
                            if (menu == 1) {
                                Functions.___playerTalk(p, n, "No thank you");
                            }

                    }
                    return null;
                });
            }
        };
    }

    // WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
    // Delay player busy (3000); after stealing and Npc shout out to you.
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.FUR_TRADER.id();
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

