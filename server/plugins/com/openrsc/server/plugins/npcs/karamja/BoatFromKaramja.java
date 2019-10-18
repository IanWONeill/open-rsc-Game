package com.openrsc.server.plugins.npcs.karamja;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.IndirectTalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.IndirectTalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public final class BoatFromKaramja implements IndirectTalkToNpcListener , ObjectActionListener , TalkToNpcListener , IndirectTalkToNpcExecutiveListener , ObjectActionExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    int option = Functions.___showMenu(p, n, "Can I board this ship?", "Does Karamja have any unusual customs then?");
                    if (option == 0) {
                        onIndirectTalkToNpc(p, n);
                    } else
                        if (option == 1) {
                            Functions.___npcTalk(p, n, "I'm not that sort of customs officer");
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onIndirectTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "You need to be searched before you can board");
                    int sub_opt = Functions.___showMenu(p, n, "Why?", "Search away I have nothing to hide", "You're not putting your hands on my things");
                    if (sub_opt == 0) {
                        Functions.___npcTalk(p, n, "Because Asgarnia has banned the import of intoxicating spirits");
                    } else
                        if (sub_opt == 1) {
                            if (Functions.hasItem(p, ItemId.KARAMJA_RUM.id(), 1)) {
                                Functions.___npcTalk(p, n, "Aha trying to smuggle rum are we?");
                                Functions.___message(p, "The customs officer confiscates your rum");
                                Functions.removeItem(p, ItemId.KARAMJA_RUM.id(), 1);
                            } else {
                                Functions.___npcTalk(p, n, "Well you've got some odd stuff, but it's all legal", "Now you need to pay a boarding charge of 30 gold");
                                int pay_opt = Functions.___showMenu(p, n, false, "Ok", "Oh, I'll not bother then");
                                if (pay_opt == 0) {
                                    if (Functions.removeItem(p, ItemId.COINS.id(), 30)) {
                                        Functions.___playerTalk(p, n, "Ok");
                                        Functions.___message(p, "You pay 30 gold", "You board the ship");
                                        Functions.movePlayer(p, 269, 648, true);
                                        p.message("The ship arrives at Port Sarim");
                                    } else {
                                        // not enough money
                                        Functions.___playerTalk(p, n, "Oh dear I don't seem to have enough money");
                                    }
                                } else
                                    if (pay_opt == 1) {
                                        Functions.___playerTalk(p, n, "Oh, I'll not bother then");
                                    }

                            }
                        } else
                            if (sub_opt == 2) {
                                Functions.___npcTalk(p, n, "You're not getting on this ship then");
                            }


                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((obj.getID() == 161) || (obj.getID() == 162)) || (obj.getID() == 163)) {
                        if (command.equals("board")) {
                            if (p.getY() != 713) {
                                return null;
                            }
                            Npc officer = Functions.getNearestNpc(p, NpcId.CUSTOMS_OFFICER.id(), 4);
                            if (officer != null) {
                                officer.initializeIndirectTalkScript(p);
                            } else {
                                p.message("I need to speak to the customs officer before boarding the ship.");
                            }
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.CUSTOMS_OFFICER.id();
    }

    @Override
    public boolean blockIndirectTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.CUSTOMS_OFFICER.id();
    }

    @Override
    public boolean blockObjectAction(GameObject arg0, String arg1, Player arg2) {
        return (((arg0.getID() == 161) && arg0.getLocation().equals(Point.location(326, 710))) || ((arg0.getID() == 163) && arg0.getLocation().equals(Point.location(319, 710)))) || ((arg0.getID() == 162) && arg0.getLocation().equals(Point.location(324, 710)));
    }
}

