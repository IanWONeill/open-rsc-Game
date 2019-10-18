package com.openrsc.server.plugins.npcs.tutorial;


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


public class FishingInstructor implements TalkToNpcListener , TalkToNpcExecutiveListener {
    /**
     *
     *
     * @author Davve
    Tutorial island fishing instructor
     */
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") == 40)) {
                        Functions.___playerTalk(p, n, "Hi are you here to tell me how to catch fish?");
                        Functions.___npcTalk(p, n, "Yes that's right, you're a smart one", "Fishing is a useful skill", "You can sell high level fish for lots of money", "Or of course you can cook it and eat it to heal yourself", "Unfortunately you'll have to start off catching shrimps", "Till your fishing level gets higher", "you'll need this");
                        p.message("the fishing instructor gives you a somewhat old looking net");
                        Functions.addItem(p, ItemId.NET.id(), 1);// Add a net to the players inventory

                        Functions.___npcTalk(p, n, "Go catch some shrimp", "left click on that sparkling piece of water", "While you have the net in your inventory you might catch some fish");
                        p.getCache().set("tutorial", 41);
                    } else
                        if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") == 41)) {
                            Functions.___npcTalk(p, n, "Left click on that splashing sparkling water", "then you can catch some shrimp");
                            if (!Functions.hasItem(p, ItemId.NET.id())) {
                                Functions.___playerTalk(p, n, "I have lost my net");
                                Functions.___npcTalk(p, n, "Hmm a good fisherman doesn't lose his net", "Ah well heres another one");
                                Functions.addItem(p, ItemId.NET.id(), 1);
                            }
                        } else
                            if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") == 42)) {
                                Functions.___npcTalk(p, n, "Well done you can now continue with the tutorial", "first You can cook the shrimps on my fire here if you like");
                                p.getCache().set("tutorial", 45);
                            } else {
                                Functions.___npcTalk(p, n, "Go through the next door to continue with the tutorial now");
                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.FISHING_INSTRUCTOR.id();
    }
}

