package com.openrsc.server.plugins.npcs.lumbridge;


import com.openrsc.server.ServerConfiguration;
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
import com.openrsc.server.plugins.quests.members.RuneMysteries;
import java.util.ArrayList;
import java.util.concurrent.Callable;


public final class DukeOfLumbridge implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Greetings welcome to my castle");
                    ArrayList<String> menu = new ArrayList<String>();
                    menu.add("Have you any quests for me?");
                    menu.add("Where can I find money?");
                    if (p.getWorld().getServer().getConfig().WANT_RUNECRAFTING)
                        if (p.getQuestStage(Quests.RUNE_MYSTERIES) > 0)
                            menu.add("Rune mysteries");


                    if ((p.getQuestStage(Quests.DRAGON_SLAYER) >= 2) || ((p.getQuestStage(Quests.DRAGON_SLAYER) < 0) && (!Functions.hasItem(p, ItemId.ANTI_DRAGON_BREATH_SHIELD.id())))) {
                        menu.add(0, "I seek a shield that will protect me from dragon breath");
                        int choice = Functions.___showMenu(p, n, false, menu.toArray(new String[menu.size()]));
                        if (choice > (-1))
                            handleResponse(p, n, choice);

                    } else {
                        int choice = Functions.___showMenu(p, n, false, menu.toArray(new String[menu.size()]));
                        if (choice > (-1))
                            handleResponse(p, n, choice + 1);

                    }
                    return null;
                });
            }
        };
    }

    public void handleResponse(Player p, Npc n, int option) {
        if (option == 0) {
            // Dragon Slayer
            Functions.___playerTalk(p, n, "I seek a shield that will protect me from dragon's breath");
            Functions.___npcTalk(p, n, "A knight going on a dragon quest hmm?", "A most worthy cause", "Guard this well my friend");
            Functions.___message(p, "The duke hands you a shield");
            Functions.addItem(p, ItemId.ANTI_DRAGON_BREATH_SHIELD.id(), 1);
        } else
            if (option == 1) {
                Functions.___playerTalk(p, n, "Have you any quests for me?");
                if (!p.getWorld().getServer().getConfig().WANT_RUNECRAFTING) {
                    Functions.___npcTalk(p, n, "All is well for me");
                    return;
                }
                RuneMysteries.dukeDialog(p.getQuestStage(Quests.RUNE_MYSTERIES), p, n);
            } else
                if (option == 2) {
                    Functions.___playerTalk(p, n, "Where can I find money?");
                    Functions.___npcTalk(p, n, "I've heard the blacksmiths are prosperous amoung the peasantry");
                    Functions.___npcTalk(p, n, "Maybe you could try your hand at that");
                } else
                    if (option == 3) {
                        RuneMysteries.dukeDialog(p.getQuestStage(Quests.RUNE_MYSTERIES), p, n);
                    }



    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.DUKE_OF_LUMBRIDGE.id();
    }
}

