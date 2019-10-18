package com.openrsc.server.plugins.npcs.alkharid;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class Tanner implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Greetings friend I'm a manufacturer of leather");
                    int option = // do not send over
                    Functions.___showMenu(p, n, false, "Can I buy some leather then?", "Here's some cow hides, can I buy some leather now?", "Leather is rather weak stuff");
                    switch (option) {
                        case 0 :
                            Functions.___playerTalk(p, n, "Can I buy some leather then?");
                            Functions.___npcTalk(p, n, "I make leather from cow hides", "Bring me some of them and a gold coin per hide");
                            break;
                        case 1 :
                            Functions.___playerTalk(p, n, "Here's some cow hides, Can I buy some leather");
                            Functions.___npcTalk(p, n, "Ok");
                            while (true) {
                                Functions.sleep(500);
                                if (p.getInventory().countId(ItemId.COW_HIDE.id()) < 1) {
                                    Functions.___playerTalk(p, n, "I don't have any cow hides left now");
                                    break;
                                } else
                                    if (p.getInventory().countId(ItemId.COINS.id()) < 1) {
                                        // message possibly non kosher
                                        Functions.___playerTalk(p, n, "I don't have any coins left now");
                                        break;
                                    } else
                                        if ((p.getInventory().remove(new Item(ItemId.COW_HIDE.id())) > (-1)) && (p.getInventory().remove(ItemId.COINS.id(), 1) > (-1))) {
                                            p.message("You swap a cow hide for a piece of leather");
                                            Functions.addItem(p, ItemId.LEATHER.id(), 1);
                                        } else {
                                            break;
                                        }


                            } 
                            break;
                        case 2 :
                            Functions.___playerTalk(p, n, "Leather is rather weak stuff");
                            Functions.___npcTalk(p, n, "Well yes if all you're concerned with is how much it will protect you in a fight");
                            break;
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.TANNER.id();
    }
}

