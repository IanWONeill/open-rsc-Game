package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;


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


public class UndergroundPassSlaves implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return Functions.inArray(n.getID(), NpcId.SLAVE_1.id(), NpcId.SLAVE_2.id(), NpcId.SLAVE_3.id(), NpcId.SLAVE_4.id(), NpcId.SLAVE_5.id(), NpcId.SLAVE_6.id(), NpcId.SLAVE_7.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(n.getID(), NpcId.SLAVE_1.id(), NpcId.SLAVE_2.id(), NpcId.SLAVE_3.id(), NpcId.SLAVE_4.id(), NpcId.SLAVE_5.id(), NpcId.SLAVE_6.id(), NpcId.SLAVE_7.id())) {
                        p.message("the man seems to be in a weak state of mind");
                        switch (NpcId.getById(n.getID())) {
                            case SLAVE_1 :
                                Functions.___playerTalk(p, n, "hello");
                                Functions.___npcTalk(p, n, "Eating me...they keep eating me!", "Eating me from the inside. Please stop  them eating me", "... I can feel them wriggling around inside me right now", "... please  stop them!");
                                n.startCombat(p);
                                break;
                            case SLAVE_2 :
                                Functions.___playerTalk(p, n, "hi");
                                Functions.___npcTalk(p, n, "Blood, blood... never enough blood to go round", "When I lift up my arm like this,it all pours back into my body", "I hope it remembers to go back inside my arm", "And don't even ask me about my legs..", "how much blood are they going to need?", "Blood is important", "We must offer it to Zamorak every day as proof of our devotion", "I just hope I don't run out.");
                                p.message("the prisoner has clearly been here too long");
                                break;
                            case SLAVE_3 :
                                Functions.___playerTalk(p, n, "hello, are you ok?");
                                Functions.___npcTalk(p, n, "Oh yes, you're a fine one. Nice red cheeks, shiny hair", "Let's see now, some potatoes, some cabbage,", "maybe half a clove of garlic", "...yes  I think you'd  make a fine soup", "You don't mind do you?");
                                Functions.___playerTalk(p, n, "actually i do");
                                Functions.___npcTalk(p, n, "You're welcome to have some with me of course.");
                                n.startCombat(p);
                                break;
                            case SLAVE_4 :
                                Functions.___playerTalk(p, n, "hi");
                                Functions.___npcTalk(p, n, "Mwaarrr fnnntchh. Gbpp dng sktd delp?");
                                Functions.___playerTalk(p, n, "pardon?");
                                Functions.___npcTalk(p, n, "Kjp lar falut: Gbpp dng sktd delp?");
                                Functions.___playerTalk(p, n, "sorry, i dont under..");
                                Functions.___npcTalk(p, n, "Mwaarrr fnnntchh. Gbpp dng sktd delp?", "GBPP DNG SKTD DELP! GBPP DNG SKTD DELP!");
                                n.startCombat(p);
                                break;
                            case SLAVE_5 :
                                Functions.___playerTalk(p, n, "hi");
                                Functions.___npcTalk(p, n, "Kill the villagers, burn them all- every last one", "I want nothing to survive: nothing but the sweet smell of burning flesh");
                                Functions.___playerTalk(p, n, "you're ill");
                                Functions.___npcTalk(p, n, "What's that - you've never smelt it before", "Well, let's just say that it's an acquired taste");
                                n.startCombat(p);
                                break;
                            case SLAVE_6 :
                                Functions.___playerTalk(p, n, "hello");
                                Functions.___npcTalk(p, n, "Danger, everywhere danger! But not from man nor beast-", "no this is the danger that is inside you.", "Bring it out, nurture it, cherish it", "Stroke it like you would stroke a wounded bird-", "then strangle it before it takes hold of your very being", "Don't say I didn't warn you");
                                break;
                            case SLAVE_7 :
                                Functions.___playerTalk(p, n, "hello");
                                Functions.___npcTalk(p, n, "What's that...is that a dagger I see before me?", "Why should I bear the slings and arrows of outrageous fortune", "for what is a man but this quintessence of dust?", "And so I say goodnight sweet prince. The rest is silence.");
                                break;
                            default :
                                break;
                        }
                    }
                    return null;
                });
            }
        };
    }
}

