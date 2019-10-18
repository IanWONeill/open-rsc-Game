package com.openrsc.server.plugins.npcs.barbarian;


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
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public final class Oracle implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Menu defaultMenu = new Menu();
                    if (p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
                        defaultMenu.addOption(new Option("I seek a piece of the map of the isle of Crondor") {
                            @Override
                            public void action() {
                                Functions.___npcTalk(p, n, "The map's behind a door below", "But entering is rather tough", "And this is what you need to know", "You must hold the following stuff", "First a drink used by the mage", "Next some worm string, changed to sheet", "Then a small crustacean cage", "Last a bowl that's not seen heat");
                            }
                        });
                    }
                    defaultMenu.addOption(new Option("Can you impart your wise knowledge to me oh oracle?") {
                        public void action() {
                            if (p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
                                Functions.___npcTalk(p, n, "You must search from within to find your true destiny");
                            } else {
                                int rand = DataConversions.random(0, 7);
                                switch (rand) {
                                    case 0 :
                                        Functions.___npcTalk(p, n, "You must search from within to find your true destiny");
                                        break;
                                    case 1 :
                                        Functions.___npcTalk(p, n, "No crisps at the party");
                                        break;
                                    case 2 :
                                        Functions.___npcTalk(p, n, "It is cunning, almost foxlike");
                                        break;
                                    case 3 :
                                        Functions.___npcTalk(p, n, "Is it waking up time, I'm not quite sure");
                                        break;
                                    case 4 :
                                        Functions.___npcTalk(p, n, "When in Asgarnia do as the Asgarnians do");
                                        break;
                                    case 5 :
                                        Functions.___npcTalk(p, n, "The light at the end of the tunnel is the demon infested lava pit");
                                        break;
                                    case 6 :
                                        Functions.___npcTalk(p, n, "Watch out for cabbages they are green and leafy");
                                        break;
                                    case 7 :
                                        Functions.___npcTalk(p, n, "Too many cooks spoil the anchovie pizza");
                                        break;
                                }
                            }
                        }
                    });
                    defaultMenu.showMenu(p);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.ORACLE.id();
    }
}

