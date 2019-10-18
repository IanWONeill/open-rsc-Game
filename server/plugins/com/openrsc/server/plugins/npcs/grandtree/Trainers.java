package com.openrsc.server.plugins.npcs.grandtree;


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


public class Trainers implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return Functions.inArray(n.getID(), NpcId.GNOME_TRAINER_ENTRANCE.id(), NpcId.GNOME_TRAINER_STARTINGNET.id(), NpcId.GNOME_TRAINER_PLATFORM.id(), NpcId.GNOME_TRAINER_ENDINGNET.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.GNOME_TRAINER_ENTRANCE.id()) {
                        Functions.___playerTalk(p, n, "hello, what is this place?");
                        Functions.___npcTalk(p, n, "this my friend, is where we train", "it improves our agility, an essential skill");
                        Functions.___playerTalk(p, n, "looks easy enough");
                        Functions.___npcTalk(p, n, "if you complete the course...", "from the slippery log to the end", "your agilty will increase much faster..", ".. than repeating one obstical");
                    } else
                        if (n.getID() == NpcId.GNOME_TRAINER_STARTINGNET.id()) {
                            Functions.___playerTalk(p, n, "hello");
                            Functions.___npcTalk(p, n, "this isn't a granny's tea party", "let's see some sweat human", "go, go ,go ,go");
                        } else
                            if (n.getID() == NpcId.GNOME_TRAINER_PLATFORM.id()) {
                                Functions.___playerTalk(p, n, "this is fun");
                                Functions.___npcTalk(p, n, "this is training soldier", "if you want fun, go make some cocktails");
                            } else
                                if (n.getID() == NpcId.GNOME_TRAINER_ENDINGNET.id()) {
                                    Functions.___playerTalk(p, n, "hello");
                                    Functions.___npcTalk(p, n, "hi");
                                    Functions.___playerTalk(p, n, "how are you?");
                                    Functions.___npcTalk(p, n, "im amazed by how much you humans chat", "the sign say's training area...", "..not pointless conversation area", "now move it soldier");
                                }



                    return null;
                });
            }
        };
    }
}

