package com.openrsc.server.plugins.npcs.ardougne.west;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class DarkMage implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.DARK_MAGE.id()) {
                        Functions.___playerTalk(p, n, "hello there");
                        Functions.___npcTalk(p, n, "why do do you interupt me traveller?");
                        Functions.___playerTalk(p, n, "i just wondered what you're doing?");
                        Functions.___npcTalk(p, n, "i experiment with dark magic", "it's a dangerous craft");
                        if (Functions.hasItem(p, ItemId.STAFF_OF_IBAN_BROKEN.id()) && (p.getQuestStage(Quests.UNDERGROUND_PASS) == (-1))) {
                            Functions.___playerTalk(p, n, "could you fix this staff?");
                            p.message("you show the mage your staff of iban");
                            Functions.___npcTalk(p, n, "almighty zamorak! the staff of iban!");
                            Functions.___playerTalk(p, n, "can you fix it?");
                            Functions.___npcTalk(p, n, "this truly is dangerous magic traveller", "i can fix it, but it will cost you", "the process could kill me");
                            Functions.___playerTalk(p, n, "how much?");
                            Functions.___npcTalk(p, n, "200,000 gold pieces, not a penny less");
                            int menu = Functions.___showMenu(p, n, "no chance, that's ridiculous", "ok then");
                            if (menu == 0) {
                                Functions.___npcTalk(p, n, "fine by me");
                            } else
                                if (menu == 1) {
                                    if (!Functions.hasItem(p, ItemId.COINS.id(), 200000)) {
                                        p.message("you don't have enough money");
                                        Functions.___playerTalk(p, n, "oops, i'm a bit short");
                                    } else {
                                        Functions.___message(p, "you give the mage 200,000 coins", "and the staff of iban");
                                        Functions.removeItem(p, ItemId.COINS.id(), 200000);
                                        Functions.removeItem(p, ItemId.STAFF_OF_IBAN_BROKEN.id(), 1);
                                        p.message("the mage fixes the staff and returns it to you");
                                        Functions.addItem(p, ItemId.STAFF_OF_IBAN.id(), 1);
                                        Functions.___playerTalk(p, n, "thanks mage");
                                        Functions.___npcTalk(p, n, "you be carefull with that thing");
                                    }
                                }

                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.DARK_MAGE.id();
    }
}

