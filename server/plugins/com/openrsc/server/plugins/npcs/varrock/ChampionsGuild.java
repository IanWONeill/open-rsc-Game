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


public final class ChampionsGuild implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop scavvosShop = new Shop(false, 300000, 100, 60, 2, new Item(ItemId.RUNE_SKIRT.id(), 1), new Item(ItemId.RUNE_PLATE_MAIL_LEGS.id(), 1), new Item(ItemId.RUNE_MACE.id(), 1), new Item(ItemId.RUNE_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.RUNE_LONG_SWORD.id(), 1), new Item(ItemId.RUNE_SHORT_SWORD.id(), 1));

    private final Shop valsShop = new Shop(true, 60000, 130, 40, 3, new Item(ItemId.BLUE_CAPE.id(), 2), new Item(ItemId.LARGE_BLACK_HELMET.id(), 1), new Item(ItemId.BLACK_PLATE_MAIL_LEGS.id(), 1), new Item(ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(), 1));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return (n.getID() == NpcId.SCAVVO.id()) || (n.getID() == NpcId.VALAINE.id());
    }

    @Override
    public Shop[] getShops(World world) {
        return new Shop[]{ scavvosShop, valsShop };
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
                    switch (NpcId.getById(n.getID())) {
                        case SCAVVO :
                            Functions.___npcTalk(p, n, "Ello matey", "Want to buy some exciting new toys?");
                            int options = Functions.___showMenu(p, n, "No, toys are for kids", "Lets have a look then", "Ooh goody goody toys");
                            if ((options == 1) || (options == 2)) {
                                p.setAccessingShop(scavvosShop);
                                ActionSender.showShop(p, scavvosShop);
                            }
                            break;
                        case VALAINE :
                            Functions.___npcTalk(p, n, "Hello there.", "Want to have a look at what we're selling today?");
                            int opt = // do not send over
                            Functions.___showMenu(p, n, false, "Yes please", "No thank you");
                            if (opt == 0) {
                                Functions.___playerTalk(p, n, "Yes please.");
                                p.setAccessingShop(valsShop);
                                ActionSender.showShop(p, valsShop);
                            }
                            break;
                        default :
                            break;
                    }
                    return null;
                });
            }
        };
    }
}

