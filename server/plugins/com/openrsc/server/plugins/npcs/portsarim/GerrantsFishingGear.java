package com.openrsc.server.plugins.npcs.portsarim;


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


public final class GerrantsFishingGear implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(false, 12000, 100, 70, 3, new Item(ItemId.NET.id(), 5), new Item(ItemId.FISHING_ROD.id(), 5), new Item(ItemId.FLY_FISHING_ROD.id(), 5), new Item(ItemId.HARPOON.id(), 2), new Item(ItemId.LOBSTER_POT.id(), 2), new Item(ItemId.FISHING_BAIT.id(), 200), new Item(ItemId.FEATHER.id(), 200), new Item(ItemId.RAW_SHRIMP.id(), 30), new Item(ItemId.RAW_SARDINE.id(), 0), new Item(ItemId.RAW_HERRING.id(), 0), new Item(ItemId.RAW_ANCHOVIES.id(), 0), new Item(ItemId.RAW_TROUT.id(), 0), new Item(ItemId.RAW_PIKE.id(), 0), new Item(ItemId.RAW_SALMON.id(), 0), new Item(ItemId.RAW_TUNA.id(), 0), new Item(ItemId.RAW_LOBSTER.id(), 0), new Item(ItemId.RAW_SWORDFISH.id(), 0));

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return n.getID() == NpcId.GERRANT.id();
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
                    Functions.___npcTalk(p, n, "Welcome you can buy any fishing equipment at my store", "We'll also buy anything you catch off you");
                    String[] options;
                    if (p.getQuestStage(Quests.HEROS_QUEST) >= 1) {
                        options = new String[]{ "Let's see what you've got then", "Sorry, I'm not interested", "I want to find out how to catch a lava eel" };
                    } else {
                        options = new String[]{ "Let's see what you've got then", "Sorry, I'm not interested" };
                    }
                    int option = Functions.___showMenu(p, n, false, options);
                    if (option == 0) {
                        Functions.___playerTalk(p, n, "Let's see what you've got then");
                        p.setAccessingShop(shop);
                        ActionSender.showShop(p, shop);
                    } else
                        if (option == 1) {
                            Functions.___playerTalk(p, n, "Sorry,I'm not interested");
                        } else
                            if (option == 2) {
                                Functions.___playerTalk(p, n, "I want to find out how to catch a lava eel");
                                Functions.___npcTalk(p, n, "Lava eels eh?", "That's a tricky one that is", "I wouldn't even know where find them myself", "Probably in some lava somewhere", "You'll also need a lava proof fishing line", "The method for this would be take an ordinary fishing rod", "And cover it with fire proof blamish oil");
                                // check no Blaimish snail slime, oil and rod to re-issue
                                if (((!Functions.hasItem(p, ItemId.BLAMISH_SNAIL_SLIME.id())) && (!Functions.hasItem(p, ItemId.BLAMISH_OIL.id()))) && (!Functions.hasItem(p, ItemId.OILY_FISHING_ROD.id()))) {
                                    Functions.___npcTalk(p, n, "Now I may have a jar of Blamish snail slime", "I wonder where I put it");
                                    p.message("Gerrant searches about a bit");
                                    Functions.___npcTalk(p, n, "Aha here it is");
                                    p.message("Gerrant passes you a small jar");
                                    Functions.addItem(p, ItemId.BLAMISH_SNAIL_SLIME.id(), 1);
                                    Functions.___npcTalk(p, n, "You'll need to mix this with some of the Harralander herb and water");
                                }
                            }


                    return null;
                });
            }
        };
    }
}

