package com.openrsc.server.plugins.npcs;


import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class MonkHealer implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Greetings traveller");
                    int option = // do not send over
                    Functions.___showMenu(p, n, false, "Can you heal me? I'm injured", "Isn't this place built a bit out the way?");
                    if (option == 0) {
                        Functions.___playerTalk(p, n, "Can you heal me?", "I'm injured");
                        Functions.___npcTalk(p, n, "Ok");
                        Functions.___message(p, "The monk places his hands on your head", "You feel a little better");
                        int newHp = Functions.getCurrentLevel(p, Skills.HITS) + 5;
                        if (newHp > Functions.getMaxLevel(p, Skills.HITS)) {
                            newHp = Functions.getMaxLevel(p, Skills.HITS);
                        }
                        p.getSkills().setLevel(Skills.HITS, newHp);
                    } else
                        if (option == 1) {
                            Functions.___playerTalk(p, n, "Isn't this place built a bit out the way?");
                            Functions.___npcTalk(p, n, "We like it that way", "We get disturbed less", "We still get rather a large amount of travellers", "looking for sanctuary and healing here as it is");
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return (n.getID() == NpcId.MONK.id()) || (n.getID() == NpcId.ABBOT_LANGLEY.id());
    }
}

