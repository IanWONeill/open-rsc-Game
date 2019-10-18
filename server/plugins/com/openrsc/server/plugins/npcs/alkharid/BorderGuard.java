package com.openrsc.server.plugins.npcs.alkharid;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public final class BorderGuard implements ObjectActionListener , TalkToNpcListener , ObjectActionExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((p.getQuestStage(Quests.PRINCE_ALI_RESCUE) == (-1)) || (p.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 3)) {
                        Functions.___playerTalk(p, n, "Can I come through this gate?");
                        Functions.___npcTalk(p, n, "You may pass for free, you are a friend of Al Kharid");
                        p.message("The gate swings open");
                        if (p.getX() > 91)
                            p.teleport(90, 649, false);
                        else
                            p.teleport(93, 649, false);

                        return null;
                    }
                    Functions.___playerTalk(p, n, "Can I come through this gate?");
                    Functions.___npcTalk(p, n, "You must pay a toll of 10 gold coins to pass");
                    int option = Functions.___showMenu(p, n, false, "No thankyou, I'll walk round", "Who does my money go to?", "yes ok");
                    switch (option) {
                        case 0 :
                            // no thanks
                            Functions.___playerTalk(p, n, "No thankyou");
                            Functions.___npcTalk(p, n, "Ok suit yourself");
                            break;
                        case 1 :
                            // who does money go to
                            Functions.___playerTalk(p, n, "Who does my money go to?");
                            Functions.___npcTalk(p, n, "The money goes to the city of Al Kharid");
                            break;
                        case 2 :
                            Functions.___playerTalk(p, n, "Yes ok");
                            if (p.getInventory().remove(ItemId.COINS.id(), 10) > (-1)) {
                                p.message("You pay the guard");
                                Functions.___npcTalk(p, n, "You may pass");
                                p.message("The gate swings open");
                                if (p.getX() > 91) {
                                    return endOnNotify(Functions.walkThenTeleport(p, 92, 649, 91, 649, false));
                                } else {
                                    return endOnNotify(Functions.walkThenTeleport(p, 91, 649, 92, 649, false));
                                }
                            } else {
                                Functions.___playerTalk(p, n, "Oh dear I don't actually seem to have enough money");
                            }
                            break;
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return (n.getID() == NpcId.BORDER_GUARD_ALKHARID.id()) || (n.getID() == NpcId.BORDER_GUARD_LUMBRIDGE.id());
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 180) && command.equals("open")) {
                        player.message("You need to talk to the border guard");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return (obj.getID() == 180) && command.equals("open");
    }
}

