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


public class FatigueExpert implements TalkToNpcListener , TalkToNpcExecutiveListener {
    /**
     *
     *
     * @author Davve
    Tutorial island fatigue expert
     */
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") <= 85)) {
                        Functions.___playerTalk(p, n, "Hi I'm feeling a little tired after all this learning");
                        Functions.___npcTalk(p, n, "Yes when you use your skills you will slowly get fatigued", "If you look on your stats menu you will see a fatigue stat", "When your fatigue reaches 100 percent then you will be very tired", "You won't be able to concentrate enough to gain experience in your skills", "To reduce your fatigue you will need to go to sleep", "Click on the bed to go sleep", "Then follow the instructions to wake up", "When you have done that talk to me again");
                        p.getCache().set("tutorial", 85);
                    } else
                        if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") == 86)) {
                            Functions.___npcTalk(p, n, "How are you feeling now?");
                            Functions.___playerTalk(p, n, "I feel much better rested now");
                            Functions.___npcTalk(p, n, "Tell you what, I'll give you this useful sleeping bag", "So you can rest anywhere");
                            Functions.addItem(p, ItemId.SLEEPING_BAG.id(), 1);
                            p.message("The expert hands you a sleeping bag");
                            Functions.___npcTalk(p, n, "This saves you the trouble of finding a bed", "but you will need to sleep longer to restore your fatigue fully", "You can now go through the next door\"");
                            p.getCache().set("tutorial", 90);
                        } else {
                            Functions.___npcTalk(p, n, "When you use your skills you will slowly get fatigued", "If you look on your stats menu you will see a fatigue stat", "When your fatigue reaches 100 percent then you will be very tired", "You won't be able to concentrate enough to gain experience in your skills", "To reduce your fatigue you can either eat some food or go to sleep", "Click on a bed  or sleeping bag to go sleep", "Then follow the instructions to wake up", "You can now go through the next door\"");
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.FATIGUE_EXPERT.id();
    }
}

