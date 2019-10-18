package com.openrsc.server.plugins.npcs.tutorial;


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


public class QuestAdvisor implements TalkToNpcListener , TalkToNpcExecutiveListener {
    /**
     *
     *
     * @author Davve
    Tutorial island quest advisor
     */
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Greetings traveller", "If you're interested in a bit of adventure", "I can recommend going on a good quest", "There are many secrets to be unconvered", "And wrongs to be set right", "If you talk to the various characters in the game", "Some of them will give you quests");
                    Functions.___playerTalk(p, n, "What sort of quests are there to do?");
                    Functions.___npcTalk(p, n, "If you select the bar graph in the menu bar", "And then select the quests tabs", "You will see a list of quests", "quests you have completed will show up in green", "You can only do each quest once");
                    int menu = Functions.___showMenu(p, n, false, "Thank you for the advice", "Can you recommend any quests?");
                    if (menu == 0) {
                        Functions.___playerTalk(p, n, "thank you for the advice");
                        Functions.___npcTalk(p, n, "good questing traveller");
                        if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") < 65)) {
                            p.getCache().set("tutorial", 65);
                        }
                    } else
                        if (menu == 1) {
                            Functions.___playerTalk(p, n, "Can you recommend any quests?");
                            Functions.___npcTalk(p, n, "Well I hear the cook in Lumbridge castle is having some problems", "When you get to Lumbridge, go into the castle there", "Find the cook and have a chat with him");
                            Functions.___playerTalk(p, n, "Okay thanks for the advice");
                            if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") < 65)) {
                                p.getCache().set("tutorial", 65);
                            }
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.QUEST_ADVISOR.id();
    }
}

