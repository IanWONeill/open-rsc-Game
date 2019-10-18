package com.openrsc.server.plugins.npcs.ardougne.east;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
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


public final class CaptainBarnaby implements ObjectActionListener , TalkToNpcListener , ObjectActionExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Do you want to go on a trip to Karamja?", "The trip will cost you 30 gold");
                    String[] menu = new String[]{ "I'd rather go to Crandor Isle", "Yes please", "No thankyou" };
                    if ((p.getQuestStage(Quests.DRAGON_SLAYER) == (-1)) || p.getCache().hasKey("ned_hired")) {
                        menu = new String[]{ // Crandor option is not needed.
                        "Yes please", "No thankyou" };
                        int choice = Functions.___showMenu(p, n, menu);
                        if (choice >= 0) {
                            travel(p, n, choice + 1);
                        }
                    } else {
                        int choice = Functions.___showMenu(p, n, menu);
                        travel(p, n, choice);
                    }
                    return null;
                });
            }
        };
    }

    public void travel(final Player p, final Npc n, int option) {
        if (option == 0) {
            Functions.___npcTalk(p, n, "No I need to stay alive", "I have a wife and family to support");
        } else
            if (option == 1) {
                if (p.getInventory().remove(ItemId.COINS.id(), 30) > (-1)) {
                    Functions.___message(p, "You pay 30 gold", "You board the ship");
                    p.teleport(467, 651, false);
                    Functions.sleep(1000);
                    Functions.___message(p, "The ship arrives at Karamja");
                } else {
                    Functions.___playerTalk(p, n, "Oh dear I don't seem to have enough money");
                }
            }

    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.CAPTAIN_BARNABY.id();
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 157) {
                        if (command.equals("board")) {
                            if (p.getY() != 616) {
                                return null;
                            }
                            Npc captain = Functions.getNearestNpc(p, NpcId.CAPTAIN_BARNABY.id(), 5);
                            if (captain != null) {
                                captain.initializeTalkScript(p);
                            } else {
                                p.message("I need to speak to the captain before boarding the ship.");
                            }
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject arg0, String arg1, Player arg2) {
        return ((arg0.getID() == 157) && arg0.getLocation().equals(Point.location(536, 617))) || ((arg0.getID() == 155) && arg0.getLocation().equals(Point.location(531, 617)));
    }
}

