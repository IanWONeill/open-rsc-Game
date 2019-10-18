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


public final class GemTrader implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 60000 * 10, 100, 70, 3, new Item(ItemId.UNCUT_SAPPHIRE.id(), 1), new Item(ItemId.UNCUT_EMERALD.id(), 1), new Item(ItemId.UNCUT_RUBY.id(), 0), new Item(ItemId.UNCUT_DIAMOND.id(), 0), new Item(ItemId.SAPPHIRE.id(), 1), new Item(ItemId.EMERALD.id(), 1), new Item(ItemId.RUBY.id(), 0), new Item(ItemId.DIAMOND.id(), 0));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.GEM_TRADER.id();
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
                    if (n.getID() == NpcId.GEM_TRADER.id()) {
                        Functions.___npcTalk(p, n, "good day to you " + (p.isMale() ? "sir" : "madam"), "Would you be interested in buying some gems?");
                        final String[] options;
                        if ((p.getQuestStage(Quests.FAMILY_CREST) <= 2) || (p.getQuestStage(Quests.FAMILY_CREST) >= 5)) {
                            options = new String[]{ "Yes please", "No thankyou" };
                        } else {
                            options = new String[]{ "Yes please", "No thankyou", "I'm in search of a man named adam fitzharmon" };
                        }
                        int option = Functions.___showMenu(p, n, false, options);
                        if (option == 0) {
                            Functions.___playerTalk(p, n, "Yes please");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        } else
                            if (option == 1) {
                                Functions.___playerTalk(p, n, "No thankyou");
                            } else
                                if (option == 2) {
                                    Functions.___playerTalk(p, n, "I'm in search of a man named Adam Fitzharmon");
                                    Functions.___npcTalk(p, n, "Fitzharmon eh?", "Thats the name of a Varrocian noble family if I'm not mistaken", "I have seen a man of that persuasion about the place as of late", "Wearing a poncey yellow cape", "Came to my store, said he was after jewelry made from the perfect gold", "Whatever that means", "He's round about the desert still, looking for the perfect gold", "He'll be somewhere where he might get some gold I'd wager", "He might even be desperate enough to brave the scorpions");
                                    p.updateQuestStage(Quests.FAMILY_CREST, 4);
                                }


                    }
                    return null;
                });
            }
        };
    }
}

