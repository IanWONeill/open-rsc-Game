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


public class GemMerchant implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 60000 * 5, 150, 80, 3, new Item(ItemId.SAPPHIRE.id(), 2), new Item(ItemId.EMERALD.id(), 1), new Item(ItemId.RUBY.id(), 1), new Item(ItemId.DIAMOND.id(), 0));

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getCache().hasKey("gemStolen") && (Instant.now().getEpochSecond() < (p.getCache().getLong("gemStolen") + 1200))) {
                        Functions.___npcTalk(p, n, "Do you really think I'm going to buy something", "That you have just stolen from me", "guards guards");
                        Npc attacker = Functions.getNearestNpc(p, NpcId.HERO.id(), 5);// Hero first

                        if (attacker == null)
                            attacker = Functions.getNearestNpc(p, NpcId.PALADIN.id(), 5);
                        // Paladin second

                        if (attacker == null)
                            attacker = Functions.getNearestNpc(p, NpcId.KNIGHT.id(), 5);
                        // Knight third

                        if (attacker == null)
                            attacker = Functions.getNearestNpc(p, NpcId.GUARD_ARDOUGNE.id(), 5);
                        // Guard fourth

                        if (attacker != null)
                            attacker.setChasing(p);

                    } else {
                        Functions.___npcTalk(p, n, "Here, look at my lovely gems");
                        int menu = Functions.___showMenu(p, n, false, "Ok show them to me", "I'm not interested thankyou");
                        if (menu == 0) {
                            Functions.___playerTalk(p, n, "Ok show them to me");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        } else
                            if (menu == 1) {
                                Functions.___playerTalk(p, n, "I'm not intersted thankyou");
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
        return n.getID() == NpcId.GEM_MERCHANT.id();
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

