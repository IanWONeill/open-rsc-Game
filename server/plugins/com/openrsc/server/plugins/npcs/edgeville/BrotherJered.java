package com.openrsc.server.plugins.npcs.edgeville;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class BrotherJered implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    int option = Functions.___showMenu(p, n, "What can you do to help a bold adventurer like myself?", "Praise be to Saradomin");
                    if (option == 0) {
                        if ((!Functions.hasItem(p, ItemId.UNBLESSED_HOLY_SYMBOL.id())) && (!Functions.hasItem(p, ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id()))) {
                            Functions.___npcTalk(p, n, "If you have a silver star", "Which is the holy symbol of Saradomin", "Then I can bless it", "Then if you are wearing it", "It will help you when you are praying");
                        } else
                            if (Functions.hasItem(p, ItemId.UNBLESSED_HOLY_SYMBOL.id())) {
                                Functions.___npcTalk(p, n, "Well I can bless that star of Saradomin you have");
                                int sub_option = // do not send over
                                Functions.___showMenu(p, n, false, "Yes Please", "No thankyou");
                                if (sub_option == 0) {
                                    Functions.removeItem(p, ItemId.UNBLESSED_HOLY_SYMBOL.id(), 1);
                                    Functions.___playerTalk(p, n, "Yes Please");
                                    Functions.___message(p, "You give Jered the symbol", "Jered closes his eyes and places his hand on the symbol", "He softly chants", "Jered passes you the holy symbol");
                                    Functions.addItem(p, ItemId.HOLY_SYMBOL_OF_SARADOMIN.id(), 1);
                                } else
                                    if (sub_option == 1) {
                                        Functions.___playerTalk(p, n, "No Thankyou");
                                    }

                            } else
                                if (Functions.hasItem(p, ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id())) {
                                    Functions.___npcTalk(p, n, "Well if you put a string on that holy symbol", "I can bless it for you\"");
                                }


                    } else
                        if (option == 1) {
                            Functions.___npcTalk(p, n, "Yes praise he who brings life to this world");
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.BROTHER_JERED.id();
    }
}

