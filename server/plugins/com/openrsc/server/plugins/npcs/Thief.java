package com.openrsc.server.plugins.npcs;


import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class Thief implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return Functions.inArray(n.getID(), 64, 351, 352);
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    int mood = DataConversions.random(0, 13);
                    Functions.___playerTalk(p, n, "Hello", "How's it going?");
                    if (mood == 0)
                        Functions.___npcTalk(p, n, "Get out of my way", "I'm in a hurry");
                    else
                        if (mood == 1)
                            p.message("The man ignores you");
                        else
                            if (mood == 2)
                                Functions.___npcTalk(p, n, "No, I don't have any spare change");
                            else
                                if (mood == 3)
                                    Functions.___npcTalk(p, n, "Very well, thank you");
                                else
                                    if (mood == 4)
                                        Functions.___npcTalk(p, n, "I'm a little worried", "I've heard there's lots of people going about,", "killing citizens at random");
                                    else
                                        if (mood == 5) {
                                            Functions.___npcTalk(p, n, "I'm fine", "How are you?");
                                            Functions.___playerTalk(p, n, "Very well, thank you");
                                        } else
                                            if (mood == 6) {
                                                Functions.___npcTalk(p, n, "Who are you?");
                                                Functions.___playerTalk(p, n, "I am a bold adventurer");
                                                Functions.___npcTalk(p, n, "A very noble profession");
                                            } else
                                                if (mood == 7) {
                                                    Functions.___npcTalk(p, n, "Not too bad", "I'm a little worried about the increase in Goblins these days");
                                                    Functions.___playerTalk(p, n, "Don't worry. I'll kill them");
                                                } else
                                                    if (mood == 8)
                                                        Functions.___npcTalk(p, n, "Hello", "Nice weather we've been having");
                                                    else
                                                        if (mood == 9)
                                                            Functions.___npcTalk(p, n, "No, I don't want to buy anything");
                                                        else
                                                            if (mood == 10) {
                                                                Functions.___npcTalk(p, n, "Are you asking for a fight?");
                                                                n.setChasing(p);
                                                            } else
                                                                if (mood == 11) {
                                                                    Functions.___npcTalk(p, n, "How can I help you?");
                                                                    int option = Functions.___showMenu(p, n, "Do you wish to trade?", "I'm in search of a quest", "I'm in search of enemies to kill");
                                                                    if (option == 0)
                                                                        Functions.___npcTalk(p, n, "No, I have nothing I wish to get rid of", "If you want some trading,", "there are plenty of shops and market stalls around though");
                                                                    else
                                                                        if (option == 1)
                                                                            Functions.___npcTalk(p, n, "I'm sorry I can't help you there");
                                                                        else
                                                                            if (option == 2)
                                                                                Functions.___npcTalk(p, n, "I've heard there are many fearsome creatures under the ground");



                                                                } else
                                                                    if (mood == 12) {
                                                                        Functions.___npcTalk(p, n, "I think we need a new king");
                                                                        Functions.___npcTalk(p, n, "The one we've got isn't very good");
                                                                    } else
                                                                        if (mood == 13) {
                                                                            Functions.___npcTalk(p, n, "That is classified information");
                                                                        }













                    return null;
                });
            }
        };
    }
}

