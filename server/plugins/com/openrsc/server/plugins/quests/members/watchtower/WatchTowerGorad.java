package com.openrsc.server.plugins.quests.members.watchtower;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


/**
 *
 *
 * @author Imposter/Fate
 */
public class WatchTowerGorad implements PlayerAttackNpcListener , PlayerKilledNpcListener , TalkToNpcListener , PlayerAttackNpcExecutiveListener , PlayerKilledNpcExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return n.getID() == NpcId.GORAD.id();
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.GORAD.id()) {
                        n.killedBy(p);
                        p.message("Gorad has gone");
                        p.message("He's dropped a tooth, I'll keep that!");
                        Functions.addItem(p, ItemId.OGRE_TOOTH.id(), 1);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.GORAD.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.GORAD.id()) {
                        if (p.getCache().hasKey("ogre_grew")) {
                            Functions.___playerTalk(p, n, "I've come to knock your teeth out!");
                            Functions.___npcTalk(p, n, "How dare you utter that foul language in my prescence!", "You shall die quickly vermin");
                            n.startCombat(p);
                        } else
                            if (p.getCache().hasKey("ogre_grew_p1") || (p.getQuestStage(Quests.WATCHTOWER) > 0)) {
                                Functions.___playerTalk(p, n, "Hello");
                                Functions.___npcTalk(p, n, "Do you know who you are talking to ?");
                                int menu = Functions.___showMenu(p, n, "A big ugly brown creature...", "I don't know who you are");
                                if (menu == 0) {
                                    Functions.___npcTalk(p, n, "The impudence! take that...");
                                    p.damage(16);
                                    Functions.___playerTalk(p, n, "Ouch!");
                                    p.message("The ogre punched you hard in the face!");
                                } else
                                    if (menu == 1) {
                                        Functions.___npcTalk(p, n, "I am Gorad - who you are dosen't matter", "Go now and you may live another day!");
                                    }

                            } else {
                                p.message("Gorad is busy, try again later");
                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        return n.getID() == NpcId.GORAD.id();
    }

    @Override
    public GameStateEvent onPlayerAttackNpc(Player p, Npc affectedmob) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (affectedmob.getID() == NpcId.GORAD.id()) {
                        Functions.___npcTalk(p, affectedmob, "Ho Ho! why would I want to fight a worm ?", "Get lost!");
                    }
                    return null;
                });
            }
        };
    }
}

