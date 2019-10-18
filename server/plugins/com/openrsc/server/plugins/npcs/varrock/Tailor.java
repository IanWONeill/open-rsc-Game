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


public final class Tailor implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 30000, 130, 40, 2, new Item(ItemId.CHEFS_HAT.id(), 0), new Item(ItemId.BLUE_WIZARDSHAT.id(), 3), new Item(ItemId.YELLOW_CAPE.id(), 1), new Item(ItemId.GREY_WOLF_FUR.id(), 3), new Item(ItemId.FUR.id(), 3), new Item(ItemId.NEEDLE.id(), 3), new Item(ItemId.THREAD.id(), 100), new Item(ItemId.LEATHER_GLOVES.id(), 10), new Item(ItemId.BOOTS.id(), 10), new Item(ItemId.PRIEST_ROBE.id(), 3), new Item(ItemId.PRIEST_GOWN.id(), 3), new Item(ItemId.BROWN_APRON.id(), 1), new Item(ItemId.PINK_SKIRT.id(), 5), new Item(ItemId.BLACK_SKIRT.id(), 3), new Item(ItemId.BLUE_SKIRT.id(), 2), new Item(ItemId.RED_CAPE.id(), 4), new Item(ItemId.EYE_PATCH.id(), 3));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.TAILOR.id();
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
                    Functions.___npcTalk(p, n, "Now you look like someone who goes to a lot of fancy dress parties");
                    Functions.___playerTalk(p, n, "Errr... what are you saying exactly?");
                    Functions.___npcTalk(p, n, "I'm just saying that perhaps you would like to peruse my selection of garments");
                    int opt = // do not send over
                    Functions.___showMenu(p, n, false, "I think I might just leave the perusing for now thanks", "OK,lets see what you've got then");
                    if (opt == 0) {
                        Functions.___playerTalk(p, n, "I think I might just leave the perusing for now thanks");
                    } else
                        if (opt == 1) {
                            Functions.___playerTalk(p, n, "OK,let's see what you've got then");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        }

                    return null;
                });
            }
        };
    }
}

