package com.openrsc.server.plugins.minigames.mage_arena;


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


public class Chamber_Guardian implements ShopInterface , TalkToNpcListener , TalkToNpcExecutiveListener {
    private final Shop shop = new Shop(true, 60000 * 5, 100, 60, 2, new Item(ItemId.STAFF_OF_ZAMORAK.id(), 5), new Item(ItemId.STAFF_OF_SARADOMIN.id(), 5), new Item(ItemId.STAFF_OF_GUTHIX.id(), 5));

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getCache().hasKey("mage_arena") && (p.getCache().getInt("mage_arena") == 2)) {
                        Functions.___playerTalk(p, n, "hello my friend, kolodion sent me down");
                        Functions.___npcTalk(p, n, "sssshhh...the gods are talking..i can hear their whispers", "..can you hear them adventurer...they're calling you");
                        Functions.___playerTalk(p, n, "erm...ok!");
                        Functions.___npcTalk(p, n, "go and chant to the the sacred stone of your chosen god", "you will be rewarded");
                        Functions.___playerTalk(p, n, "ok?");
                        Functions.___npcTalk(p, n, "once you're done come back to me...", "...and i'll supply you with a mage staff ready for battle");
                        p.getCache().set("mage_arena", 3);
                    } else
                        if ((p.getCache().hasKey("mage_arena") && (p.getCache().getInt("mage_arena") == 3)) && ((Functions.hasItem(p, ItemId.ZAMORAK_CAPE.id()) || Functions.hasItem(p, ItemId.SARADOMIN_CAPE.id())) || Functions.hasItem(p, ItemId.GUTHIX_CAPE.id()))) {
                            Functions.___npcTalk(p, n, "hello adventurer, have you made your choice?");
                            Functions.___playerTalk(p, n, "i have");
                            Functions.___npcTalk(p, n, "good, good .. i hope you chose well", "you will have been rewarded with a magic cape", "now i will give you a magic staff", "these are all the weapons and armour you'll need here");
                            p.message("the mage guardian gives you a magic staff");
                            if (Functions.hasItem(p, ItemId.ZAMORAK_CAPE.id(), 1)) {
                                Functions.addItem(p, ItemId.STAFF_OF_ZAMORAK.id(), 1);
                            } else
                                if (Functions.hasItem(p, ItemId.SARADOMIN_CAPE.id(), 1)) {
                                    Functions.addItem(p, ItemId.STAFF_OF_SARADOMIN.id(), 1);
                                } else
                                    if (Functions.hasItem(p, ItemId.GUTHIX_CAPE.id(), 1)) {
                                        Functions.addItem(p, ItemId.STAFF_OF_GUTHIX.id(), 1);
                                    }


                            p.getCache().set("mage_arena", 4);
                        } else
                            if (p.getCache().hasKey("mage_arena") && (p.getCache().getInt("mage_arena") == 4)) {
                                Functions.___playerTalk(p, n, "hello again");
                                Functions.___npcTalk(p, n, "hello adventurer, are you looking for another staff?");
                                int choice = Functions.___showMenu(p, n, "what do you have to offer?", "no thanks", "tell me what you know about the charge spell?");
                                if (choice == 0) {
                                    Functions.___npcTalk(p, n, "take a look");
                                    ActionSender.showShop(p, shop);
                                } else
                                    if (choice == 1) {
                                        Functions.___npcTalk(p, n, "well, let me know if you need one");
                                    } else
                                        if (choice == 2) {
                                            Functions.___npcTalk(p, n, "we believe the spells are gifts from the gods", "the charge spell draws even more power from the cosmos", "while wearing a matching cape and staff", "it will double the damage caused by ...", "battle mage spells for several minutes");
                                            Functions.___playerTalk(p, n, "good stuff");
                                        }


                            } else {
                                Functions.___npcTalk(p, n, "hello adventurer, have you made your choice?");
                                Functions.___playerTalk(p, n, "no, not yet.");
                                Functions.___npcTalk(p, n, "once you're done come back to me...", "...and i'll supply you with a mage staff ready for battle");
                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.CHAMBER_GUARDIAN.id();
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

