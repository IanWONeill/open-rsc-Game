package com.openrsc.server.plugins.npcs.shilo;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class Yohnus implements TalkToNpcListener , WallObjectActionListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.YOHNUS.id()) {
                        Functions.___playerTalk(p, n, "Hello");
                        Functions.___npcTalk(p, n, "Hello Bwana, can I help you in anyway?");
                        yohnusChat(p, n);
                    }
                    return null;
                });
            }
        };
    }

    private void yohnusChat(Player p, Npc n) {
        int menu = // do not send over
        Functions.___showMenu(p, n, false, "Use Furnace - 20 Gold", "No thanks!");
        if (menu == 0) {
            if (Functions.hasItem(p, ItemId.COINS.id(), 20)) {
                Functions.removeItem(p, ItemId.COINS.id(), 20);
                Functions.___npcTalk(p, n, "Thanks Bwana!", "Enjoy the facilities!");
                p.teleport(400, 844);
                p.message("You're shown into the Blacksmiths where you can see a furnace");
            } else {
                Functions.___npcTalk(p, n, "Sorry Bwana, it seems that you are short of funds.");
            }
        } else
            if (menu == 1) {
                Functions.___playerTalk(p, n, "No thanks!");
                Functions.___npcTalk(p, n, "Very well Bwana, have a nice day.");
            }

    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.YOHNUS.id();
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
        return obj.getID() == 165;
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 165) {
                        if (p.getY() <= 844) {
                            p.teleport(400, 845);
                            return null;
                        }
                        Npc yohnus = Functions.getNearestNpc(p, NpcId.YOHNUS.id(), 5);
                        if (yohnus != null) {
                            Functions.___npcTalk(p, yohnus, "Sorry but the blacksmiths is closed.", "But I can let you use the furnace at the cost", "of 20 gold pieces.");
                            yohnusChat(p, yohnus);
                        }
                    }
                    return null;
                });
            }
        };
    }
}

