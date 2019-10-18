package com.openrsc.server.plugins.npcs.shilo;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class Yanni implements InvUseOnNpcListener , TalkToNpcListener , InvUseOnNpcExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.YANNI.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    boolean hasItemsInterest = false;
                    if (n.getID() == NpcId.YANNI.id()) {
                        Functions.___playerTalk(p, n, "Hello there!");
                        Functions.___npcTalk(p, n, "Greetings Bwana!", "My name is Yanni and I buy and sell antiques ", "and other interesting items.", "If you have any interesting items that you might", "want to sell me, please let me see them and I'll", "offer you a fair price.", "Would you like me to have a look at your items", "and give you a quote?");
                        int menu = Functions.___showMenu(p, n, "Yes please!", "Maybe some other time?");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "Great Bwana!");
                            if (Functions.hasItem(p, ItemId.BONE_KEY.id())) {
                                Functions.___npcTalk(p, n, "I'll give you 100 Gold for the Bone Key.");
                                hasItemsInterest |= true;
                            }
                            if (Functions.hasItem(p, ItemId.STONE_PLAQUE.id())) {
                                Functions.___npcTalk(p, n, "I'll give you 100 Gold for the Stone-Plaque.");
                                hasItemsInterest |= true;
                            }
                            if (Functions.hasItem(p, ItemId.TATTERED_SCROLL.id())) {
                                Functions.___npcTalk(p, n, "I'll give you 100 Gold for your tattered scroll");
                                hasItemsInterest |= true;
                            }
                            if (Functions.hasItem(p, ItemId.CRUMPLED_SCROLL.id())) {
                                Functions.___npcTalk(p, n, "I'll give you 100 Gold for your crumpled scroll");
                                hasItemsInterest |= true;
                            }
                            if (Functions.hasItem(p, ItemId.BERVIRIUS_TOMB_NOTES.id())) {
                                Functions.___npcTalk(p, n, "I'll give you 100 Gold for your Bervirius Tomb Notes.");
                                hasItemsInterest |= true;
                            }
                            if (Functions.hasItem(p, ItemId.LOCATING_CRYSTAL.id())) {
                                Functions.___npcTalk(p, n, "WOW! I'll give you 500 Gold for your Locating Crystal!");
                                hasItemsInterest |= true;
                            }
                            if (Functions.hasItem(p, ItemId.BEADS_OF_THE_DEAD.id())) {
                                Functions.___npcTalk(p, n, "Great I'll give you 1000 Gold for your Beads of the Dead.");
                                hasItemsInterest |= true;
                            }
                            if (hasItemsInterest) {
                                Functions.___npcTalk(p, n, "Those are the items I am interested in Bwana.", "If you want to sell me those items, simply show them to me.");
                            } else {
                                Functions.___npcTalk(p, n, "Sorry Bwana, you have nothing I am interested in.");
                            }
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "Sure thing.", "Have a nice day Bwana.");
                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnNpc(Player p, Npc npc, Item item) {
        return npc.getID() == NpcId.YANNI.id();
    }

    @Override
    public GameStateEvent onInvUseOnNpc(Player p, Npc npc, Item item) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (npc.getID() == NpcId.YANNI.id()) {
                        switch (ItemId.getById(item.getID())) {
                            case BONE_KEY :
                                Functions.___npcTalk(p, npc, "Great item, here's 100 Gold for it.");
                                Functions.removeItem(p, ItemId.BONE_KEY.id(), 1);
                                Functions.addItem(p, ItemId.COINS.id(), 100);
                                p.message("You sell the Bone Key.");
                                break;
                            case STONE_PLAQUE :
                                Functions.___npcTalk(p, npc, "Great item, here's 100 Gold for it.");
                                Functions.removeItem(p, ItemId.STONE_PLAQUE.id(), 1);
                                Functions.addItem(p, ItemId.COINS.id(), 100);
                                p.message("You sell the Stone Plaque.");
                                break;
                            case TATTERED_SCROLL :
                                Functions.___npcTalk(p, npc, "Great item, here's 100 Gold for it.");
                                Functions.removeItem(p, ItemId.TATTERED_SCROLL.id(), 1);
                                Functions.addItem(p, ItemId.COINS.id(), 100);
                                p.message("You sell the Tattered Scroll.");
                                break;
                            case CRUMPLED_SCROLL :
                                Functions.___npcTalk(p, npc, "Great item, here's 100 Gold for it.");
                                Functions.removeItem(p, ItemId.CRUMPLED_SCROLL.id(), 1);
                                Functions.addItem(p, ItemId.COINS.id(), 100);
                                p.message("You sell the crumpled Scroll.");
                                break;
                            case BERVIRIUS_TOMB_NOTES :
                                Functions.___npcTalk(p, npc, "Great item, here's 100 Gold for it.");
                                Functions.removeItem(p, ItemId.BERVIRIUS_TOMB_NOTES.id(), 1);
                                Functions.addItem(p, ItemId.COINS.id(), 100);
                                p.message("You sell the Bervirius Tomb Notes.");
                                break;
                            case LOCATING_CRYSTAL :
                                Functions.___npcTalk(p, npc, "Great item, here's 500 Gold for it.");
                                Functions.removeItem(p, ItemId.LOCATING_CRYSTAL.id(), 1);
                                Functions.addItem(p, ItemId.COINS.id(), 500);
                                p.message("You sell the Locating Crystal.");
                                break;
                            case BEADS_OF_THE_DEAD :
                                Functions.___npcTalk(p, npc, "Great item, here's 1000 Gold for it.");
                                Functions.removeItem(p, ItemId.BEADS_OF_THE_DEAD.id(), 1);
                                Functions.addItem(p, ItemId.COINS.id(), 1000);
                                p.message("You sell Beads of the Dead.");
                                break;
                            default :
                                p.message("Nothing interesting happens");
                                break;
                        }
                    }
                    return null;
                });
            }
        };
    }
}

