package com.openrsc.server.plugins.npcs.varrock;


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
import java.util.concurrent.Callable;


public class Guildmaster implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.GUILDMASTER.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Menu defaultMenu = new Menu();
                    defaultMenu.addOption(new Option("What is this place?") {
                        @Override
                        public void action() {
                            Functions.___npcTalk(p, n, "This is the champions' guild", "Only Adventurers who have proved themselves worthy", "by  gaining influence from quests are allowed in here", "As the number of quests in the world rises", "So will the requirements to get in here", "But so will the rewards");
                        }
                    });
                    defaultMenu.addOption(new Option("Do you know where I could get a rune plate mail body?") {
                        @Override
                        public void action() {
                            Functions.___npcTalk(p, n, "I have a friend called Oziach who lives by the cliffs", "He has a supply of rune plate mail", "He may sell you some if you're lucky, he can be a little strange sometimes though");
                            if (p.getQuestStage(Quests.DRAGON_SLAYER) == 0) {
                                p.updateQuestStage(Quests.DRAGON_SLAYER, 1);
                            }
                        }
                    });
                    defaultMenu.showMenu(p);
                    return null;
                });
            }
        };
    }
}

