package com.openrsc.server.plugins.npcs.grandtree;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
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


public class Gnomes implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return Functions.inArray(n.getID(), NpcId.GNOME_LOCAL_RED.id(), NpcId.GNOME_LOCAL_PURPLE.id(), NpcId.GNOME_CHILD_GREEN_PURPLE.id(), NpcId.GNOME_CHILD_PURPLE_PINK.id(), NpcId.GNOME_CHILD_PINK_GREEN.id(), NpcId.GNOME_CHILD_CREAM_PURPLE.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.GNOME_LOCAL_RED.id()) {
                        Functions.___playerTalk(p, n, "hello");
                        int chatRandom = DataConversions.random(0, 4);
                        switch (chatRandom) {
                            case 0 :
                                Functions.___npcTalk(p, n, "can't stop sorry, busy, busy, busy");
                                p.message("the gnome is too busy to talk");
                                break;
                            case 1 :
                                Functions.___npcTalk(p, n, "hello traveller", "are you enjoying your stay?");
                                Functions.___playerTalk(p, n, "it's a nice place");
                                Functions.___npcTalk(p, n, "yes, we try to keep it that way");
                                break;
                            case 2 :
                                Functions.___npcTalk(p, n, "i don't think i can take much more");
                                Functions.___playerTalk(p, n, "what's wrong?");
                                Functions.___npcTalk(p, n, "it's just the wife, she won't stop moaning");
                                Functions.___playerTalk(p, n, "maybe you should give her less to moan about");
                                Functions.___npcTalk(p, n, "she'll always find something");
                                break;
                            case 3 :
                                Functions.___npcTalk(p, n, "how's life treating you");
                                Functions.___playerTalk(p, n, "not bad, not bad at all");
                                Functions.___npcTalk(p, n, "it's good to see a human with a positive attitude");
                                break;
                        }
                    } else
                        if (n.getID() == NpcId.GNOME_LOCAL_PURPLE.id()) {
                            Functions.___playerTalk(p, n, "hello");
                            int chatRandom = DataConversions.random(0, 4);
                            switch (chatRandom) {
                                case 0 :
                                    Functions.___npcTalk(p, n, "hello traveller", "are you eating properly?, you look tired");
                                    Functions.___playerTalk(p, n, "i think so");
                                    Functions.___npcTalk(p, n, "here get this worm down you", "it'll do you the world of good");
                                    p.message("the gnome gives you a worm");
                                    Functions.addItem(p, ItemId.KING_WORM.id(), 1);
                                    Functions.___playerTalk(p, n, "thanks!");
                                    break;
                                case 1 :
                                    Functions.___playerTalk(p, n, "how are you?");
                                    Functions.___npcTalk(p, n, "not bad, a little worn out");
                                    Functions.___playerTalk(p, n, "maybe you should have a lie down");
                                    Functions.___npcTalk(p, n, "with three kids to feed i've no time for naps");
                                    Functions.___playerTalk(p, n, "sounds like hard work");
                                    Functions.___npcTalk(p, n, "it is but they're worth it");
                                    break;
                                case 2 :
                                    Functions.___npcTalk(p, n, "Some people grumble because roses have thorns", "I'm thankful that thorns have roses");
                                    Functions.___playerTalk(p, n, "good attitude");
                                    break;
                                    // case 3 nothing but hello.
                            }
                        } else
                            if ((n.getID() == NpcId.GNOME_CHILD_GREEN_PURPLE.id()) || (n.getID() == NpcId.GNOME_CHILD_CREAM_PURPLE.id())) {
                                Functions.___playerTalk(p, n, "hi there");
                                int chatRandom = DataConversions.random(0, 6);
                                switch (chatRandom) {
                                    case 0 :
                                        Functions.___npcTalk(p, n, "hello, why aren't you green?");
                                        Functions.___playerTalk(p, n, "i don't know");
                                        Functions.___npcTalk(p, n, "maybe you should eat more vegtables");
                                        break;
                                    case 1 :
                                        Functions.___npcTalk(p, n, "she loves me");
                                        Functions.___playerTalk(p, n, "really");
                                        Functions.___npcTalk(p, n, "she does i tell you", "she really loves me");
                                        break;
                                        // case 2 nothing by hi there.
                                    case 3 :
                                        p.message("the gnome appears to be singing");
                                        Functions.___npcTalk(p, n, "oh baby, oh my sweet");
                                        Functions.___playerTalk(p, n, "are you talking to me?");
                                        Functions.___npcTalk(p, n, "no, i'm just singing", "i'm gonna sweep you of your feet");
                                        break;
                                    case 4 :
                                        Functions.___npcTalk(p, n, "hello, would you like a worm?");
                                        Functions.___playerTalk(p, n, "erm ok");
                                        p.message("the gnome gives you a worm");
                                        Functions.addItem(p, ItemId.KING_WORM.id(), 1);
                                        Functions.___playerTalk(p, n, "thanks");
                                        Functions.___npcTalk(p, n, "in the gnome village those who are needy..", "recieve what they need, and those who are able..", "... give what they can");
                                        break;
                                    case 5 :
                                        Functions.___npcTalk(p, n, "low");
                                        Functions.___playerTalk(p, n, "what?");
                                        Functions.___npcTalk(p, n, "when?");
                                        Functions.___playerTalk(p, n, "cheeky");
                                        Functions.___npcTalk(p, n, "hee hee");
                                        break;
                                }
                            } else
                                if (n.getID() == NpcId.GNOME_CHILD_PURPLE_PINK.id()) {
                                    Functions.___playerTalk(p, n, "hello little man");
                                    int chatRandom = DataConversions.random(0, 9);
                                    switch (chatRandom) {
                                        case 0 :
                                            Functions.___playerTalk(p, n, "how are you");
                                            Functions.___npcTalk(p, n, "a warning traveller, the new world..", "..will rise from the underground");
                                            Functions.___playerTalk(p, n, "what do you mean underground?");
                                            Functions.___npcTalk(p, n, "just a warning");
                                            break;
                                        case 1 :
                                            Functions.___npcTalk(p, n, "a little inaccuracy sometimes...", "..saves tons of explanation");
                                            Functions.___playerTalk(p, n, "true");
                                            break;
                                        case 2 :
                                            Functions.___playerTalk(p, n, "you look happy");
                                            Functions.___npcTalk(p, n, "i'm always at peace with myself");
                                            Functions.___playerTalk(p, n, "how do you manage that?");
                                            Functions.___npcTalk(p, n, "i know, therefore i am");
                                            break;
                                        case 3 :
                                            Functions.___npcTalk(p, n, "hello, would you like a worm?");
                                            Functions.___playerTalk(p, n, "erm ok");
                                            p.message("the gnome gives you a worm");
                                            Functions.addItem(p, ItemId.KING_WORM.id(), 1);
                                            Functions.___playerTalk(p, n, "thanks");
                                            Functions.___npcTalk(p, n, "in the gnome village those who are needy..", "recieve what they need, and those who are able..", "... give what they can");
                                            break;
                                        case 4 :
                                            Functions.___npcTalk(p, n, "some advice traveller", "we can walk, run, row or fly", "but never lose sight of the reason for the journey", "or miss the chance to see a rainbow on the way");
                                            Functions.___playerTalk(p, n, "i like that");
                                            break;
                                        case 5 :
                                            Functions.___npcTalk(p, n, "my mum says...", "A friendly look, a kindly smile", "one good act, and life's worthwhile!");
                                            Functions.___playerTalk(p, n, "sweet");
                                            break;
                                        case 6 :
                                            Functions.___npcTalk(p, n, "hello");
                                            Functions.___playerTalk(p, n, "are you alright?");
                                            Functions.___npcTalk(p, n, "i just want something to happen");
                                            Functions.___playerTalk(p, n, "what?");
                                            Functions.___npcTalk(p, n, "something, anything i don't know what");
                                            break;
                                            // case 7 hello little man
                                        case 8 :
                                            Functions.___message(p, "the gnome is preying");
                                            Functions.___npcTalk(p, n, "guthix's angels fly so high as to be beyond our sight", "but they are always looking down upon us");
                                            Functions.___playerTalk(p, n, "maybe");
                                            break;
                                    }
                                } else
                                    if (n.getID() == NpcId.GNOME_CHILD_PINK_GREEN.id()) {
                                        Functions.___playerTalk(p, n, "hello");
                                        int chatRandom = DataConversions.random(0, 7);
                                        switch (chatRandom) {
                                            case 0 :
                                                Functions.___npcTalk(p, n, "To be or not to be");
                                                Functions.___playerTalk(p, n, "Hey I know that. Where's it from?");
                                                Functions.___npcTalk(p, n, "Existentialism for insects");
                                                break;
                                            case 1 :
                                                Functions.___npcTalk(p, n, "The human mind is a tremendous thing");
                                                break;
                                            case 2 :
                                                Functions.___npcTalk(p, n, "i have a riddle for you");
                                                Functions.___playerTalk(p, n, "ok");
                                                Functions.___npcTalk(p, n, "I am the beginning of eternity and the end of time and space...", "I am the beginning of every end and the end of every place. What am i?");
                                                Functions.___playerTalk(p, n, "?", "erm..not sure...annoying");
                                                Functions.___npcTalk(p, n, "i'm E, hee hee, do you get it");
                                                break;
                                            case 3 :
                                                Functions.___npcTalk(p, n, "hardy ha ha", "hee hee hee");
                                                Functions.___playerTalk(p, n, "are you ok?");
                                                Functions.___npcTalk(p, n, "i'm a little tree gnome", "that is me");
                                                Functions.___playerTalk(p, n, "i've heard better");
                                                break;
                                            case 4 :
                                                Functions.___playerTalk(p, n, "hello there");
                                                Functions.___npcTalk(p, n, "bla bla bla");
                                                Functions.___playerTalk(p, n, "what?");
                                                Functions.___npcTalk(p, n, "bla bla bla");
                                                p.message("rude little gnome");
                                                break;
                                            case 5 :
                                                Functions.___npcTalk(p, n, "Nice weather we're having today", "But then it doesn't tend to rain much round here");
                                                break;
                                                // case 6 hello
                                        }
                                    }




                    return null;
                });
            }
        };
    }
}

