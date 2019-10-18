package com.openrsc.server.plugins.npcs.alkharid;


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


public final class RanaelSkirt implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 25000, 100, 65, 1, new Item(ItemId.BRONZE_PLATED_SKIRT.id(), 5), new Item(ItemId.IRON_PLATED_SKIRT.id(), 3), new Item(ItemId.STEEL_PLATED_SKIRT.id(), 2), new Item(ItemId.BLACK_PLATED_SKIRT.id(), 1), new Item(ItemId.MITHRIL_PLATED_SKIRT.id(), 1), new Item(ItemId.ADAMANTITE_PLATED_SKIRT.id(), 1));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.RANAEL.id();
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
                    final String[] options;
                    Functions.___npcTalk(p, n, "Do you want to buy any armoured skirts?", "Designed especially for ladies who like to fight");
                    if ((p.getQuestStage(Quests.FAMILY_CREST) <= 2) || (p.getQuestStage(Quests.FAMILY_CREST) >= 5)) {
                        options = new String[]{ "Yes please", "No thank you that's not my scene" };
                    } else {
                        options = new String[]{ "Yes please", "No thank you that's not my scene", "I'm in search of a man named adam fitzharmon" };
                    }
                    int option = Functions.___showMenu(p, n, false, options);
                    if (option == 0) {
                        Functions.___playerTalk(p, n, "Yes Please");
                        p.setAccessingShop(shop);
                        ActionSender.showShop(p, shop);
                    } else
                        if (option == 1) {
                            Functions.___playerTalk(p, n, "No thank you that's not my scene");
                        } else
                            if (option == 2) {
                                Functions.___playerTalk(p, n, "I'm in search of a man named adam fitzharmon");
                                Functions.___npcTalk(p, n, "I haven't seen him", "I'm sure if he's been to Al Kharid recently", "Someone around here will have seen him though");
                            }


                    return null;
                });
            }
        };
    }
}

