package com.openrsc.server.plugins.quests.free;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class TheRestlessGhost implements QuestInterface , InvUseOnObjectListener , ObjectActionListener , PickupListener , TalkToNpcListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener , PickupExecutiveListener , TalkToNpcExecutiveListener {
    private static final int GHOST_COFFIN_OPEN = 40;

    private static final int GHOST_COFFIN_CLOSED = 39;

    @Override
    public int getQuestId() {
        return Quests.THE_RESTLESS_GHOST;
    }

    @Override
    public String getQuestName() {
        return "The restless ghost";
    }

    @Override
    public boolean isMembers() {
        return false;
    }

    @Override
    public void handleReward(Player player) {
        player.message("You have completed the restless ghost quest");
        Functions.incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.THE_RESTLESS_GHOST), true);
        player.message("@gre@You haved gained 1 quest point!");
    }

    private void ghostDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.GHOST_RESTLESS.id()) {
            if (p.getQuestStage(this) == (-1)) {
                p.message("The ghost doesn't appear interested in talking");
                return;
            }
            if (cID == (-1)) {
                if (p.getQuestStage(this) == 3) {
                    Functions.___playerTalk(p, n, "Hello ghost, how are you?");
                    Functions.___npcTalk(p, n, "How are you doing finding my skull?");
                    if (!Functions.hasItem(p, ItemId.QUEST_SKULL.id())) {
                        Functions.___playerTalk(p, n, "Sorry, I can't find it at the moment");
                        Functions.___npcTalk(p, n, "Ah well keep on looking", "I'm pretty sure it's somewhere in the tower south west from here", "There's a lot of levels to the tower, though", "I suppose it might take a little while to find");
                        // kosher: this condition made player need to restart skull process incl. skeleton fight
                        p.getCache().remove("tried_grab_skull");
                    } else
                        if (Functions.hasItem(p, ItemId.QUEST_SKULL.id())) {
                            Functions.___playerTalk(p, n, "I have found it");
                            Functions.___npcTalk(p, n, "Hurrah now I can stop being a ghost", "You just need to put it in my coffin over there", "And I will be free");
                        }

                    return;
                }
                if ((p.getQuestStage(this) == 0) || (!p.getInventory().wielding(ItemId.AMULET_OF_GHOSTSPEAK.id()))) {
                    Functions.___playerTalk(p, n, "Hello ghost, how are you?");
                    Functions.___npcTalk(p, n, "Wooo wooo wooooo");
                    int choice = Functions.___showMenu(p, n, "Sorry I don't speak ghost", "Ooh that's interesting", "Any hints where I can find some treasure?");
                    if (choice == 0) {
                        ghostDialogue(p, n, TheRestlessGhost.Ghost.DONTSPEAK);
                    } else
                        if (choice == 1) {
                            Functions.___npcTalk(p, n, "Woo wooo", "Woooooooooooooooooo");
                            int choice2 = Functions.___showMenu(p, n, "Did he really?", "Yeah that's what I thought");
                            if (choice2 == 0) {
                                Functions.___npcTalk(p, n, "Woo");
                                int choice3 = Functions.___showMenu(p, n, "My brother had exactly the same problem", "Goodbye. Thanks for the chat");
                                if (choice3 == 0) {
                                    Functions.___npcTalk(p, n, "Woo Wooooo", "Wooooo Woo woo woo");
                                    int choice4 = Functions.___showMenu(p, n, "Goodbye. Thanks for the chat", "You'll have to give me the recipe some time");
                                    if (choice4 == 0) {
                                        ghostDialogue(p, n, TheRestlessGhost.Ghost.GOODBYE);
                                    } else
                                        if (choice4 == 1) {
                                            Functions.___npcTalk(p, n, "Wooooooo woo woooooooo");
                                            int choice6 = Functions.___showMenu(p, n, "Goodbye. Thanks for the chat", "Hmm I'm not sure about that");
                                            if (choice6 == 0) {
                                                ghostDialogue(p, n, TheRestlessGhost.Ghost.GOODBYE);
                                            } else
                                                if (choice6 == 1) {
                                                    ghostDialogue(p, n, TheRestlessGhost.Ghost.NOTSURE);
                                                }

                                        }

                                } else
                                    if (choice3 == 1) {
                                        Functions.___npcTalk(p, n, "Wooo wooo", "Wooo woooooooooooooooo");
                                        int choice7 = Functions.___showMenu(p, n, "Goodbye. Thanks for the chat", "Hmm I'm not sure about that");
                                        if (choice7 == 0) {
                                            ghostDialogue(p, n, TheRestlessGhost.Ghost.GOODBYE);
                                        } else
                                            if (choice7 == 1) {
                                                ghostDialogue(p, n, TheRestlessGhost.Ghost.NOTSURE);
                                            }

                                    }

                            } else
                                if (choice2 == 1) {
                                    Functions.___npcTalk(p, n, "Wooo woooooooooooooo");
                                    int choice5 = Functions.___showMenu(p, n, "Goodbye. Thanks for the chat", "Hmm I'm not sure about that");
                                    if (choice5 == 0) {
                                        ghostDialogue(p, n, TheRestlessGhost.Ghost.GOODBYE);
                                    } else
                                        if (choice5 == 1) {
                                            ghostDialogue(p, n, TheRestlessGhost.Ghost.NOTSURE);
                                        }

                                }

                        } else
                            if (choice == 2) {
                                Functions.___npcTalk(p, n, "Wooooooo woo!");
                                int choice8 = // do not send over
                                Functions.___showMenu(p, n, false, "Sorry I don't speak ghost", "Thank you. You've been very helpful");
                                if (choice8 == 0) {
                                    Functions.___playerTalk(p, n, "Sorry I don't speak ghost");
                                    ghostDialogue(p, n, TheRestlessGhost.Ghost.DONTSPEAK);
                                } else
                                    if (choice8 == 1) {
                                        Functions.___playerTalk(p, n, "Thank you. You've been very helpfull");
                                        Functions.___npcTalk(p, n, "Wooooooo");
                                    }

                            }


                } else {
                    Functions.___playerTalk(p, n, "Hello ghost, how are you?");
                    Functions.___npcTalk(p, n, "Not very good actually");
                    Functions.___playerTalk(p, n, "What's the problem then?");
                    Functions.___npcTalk(p, n, "Did you just understand what I said?");
                    int choice = // do not send over
                    Functions.___showMenu(p, n, false, "Yep, now tell me what the problem is", "No, you sound like you're speaking nonsense to me", "Wow, this amulet works");
                    if (choice == 0) {
                        Functions.___playerTalk(p, n, "Yep, now tell me what the problem is");
                        Functions.___npcTalk(p, n, "Wow this is incredible, I didn't expect any one to understand me again");
                        Functions.___playerTalk(p, n, "Yes, yes I can understand you", "But have you any idea why you're doomed to be a ghost?");
                        Functions.___npcTalk(p, n, "I'm not sure");
                        Functions.___playerTalk(p, n, "I've been told a certain task may need to be completed", "So you can rest in peace");
                        Functions.___npcTalk(p, n, "I should think it is probably because ", "A warlock has come along and stolen my skull", "If you look inside my coffin there", "you'll find my corpse without a head on it");
                        Functions.___playerTalk(p, n, "Do you know where this warlock might be now?");
                        Functions.___npcTalk(p, n, "I think it was one of the warlocks who lives in the big tower", "In the sea southwest from here");
                        Functions.___playerTalk(p, n, "Ok I will try and get the skull back for you, so you can rest in peace.");
                        Functions.___npcTalk(p, n, "Ooh thank you. That would be such a great relief", "It is so dull being a ghost");
                        p.updateQuestStage(Quests.THE_RESTLESS_GHOST, 3);
                    } else
                        if (choice == 1) {
                            Functions.___playerTalk(p, n, "No");
                            Functions.___npcTalk(p, n, "Oh that's a pity. You got my hopes up there");
                            Functions.___playerTalk(p, n, "Yeah, it is pity. Sorry");
                            Functions.___npcTalk(p, n, "Hang on a second. You can understand me");
                            int choice2 = Functions.___showMenu(p, n, "No I can't", "Yep clever aren't I");
                            if (choice2 == 0) {
                                Functions.___npcTalk(p, n, "I don't know, the first person I can speak to in ages is a moron");
                            } else
                                if (choice2 == 1) {
                                    Functions.___npcTalk(p, n, "I'm impressed", "You must be very powerfull", "I don't suppose you can stop me being a ghost?");
                                    int choice3 = Functions.___showMenu(p, n, "Yes, Ok. Do you know why you're a ghost?", "No, you're scary");
                                    if (choice3 == 0) {
                                        ghostDialogue(p, n, TheRestlessGhost.Ghost.WHY);
                                    } else
                                        if (choice3 == 1) {
                                            ghostDialogue(p, n, TheRestlessGhost.Ghost.SCARY);
                                        }

                                }

                        } else
                            if (choice == 2) {
                                Functions.___playerTalk(p, n, "Wow, this amulet works");
                                Functions.___npcTalk(p, n, "Oh its your amulet that's doing it. I did wonder", "I don't suppose you can help me? I don't like being a ghost");
                                int choice3 = Functions.___showMenu(p, n, "Yes, Ok. Do you know why you're a ghost?", "No, you're scary");
                                if (choice3 == 0) {
                                    ghostDialogue(p, n, TheRestlessGhost.Ghost.WHY);
                                } else
                                    if (choice3 == 1) {
                                        ghostDialogue(p, n, TheRestlessGhost.Ghost.SCARY);
                                    }

                            }


                }
                return;
            }
            switch (cID) {
                case TheRestlessGhost.Ghost.DONTSPEAK :
                    Functions.___npcTalk(p, n, "Woo woo?");
                    Functions.___playerTalk(p, n, "Nope still don't understand you");
                    Functions.___npcTalk(p, n, "Woooooooo");
                    Functions.___playerTalk(p, n, "Never mind");
                    break;
                case TheRestlessGhost.Ghost.GOODBYE :
                    Functions.___npcTalk(p, n, "Wooo wooo");
                    break;
                case TheRestlessGhost.Ghost.NOTSURE :
                    Functions.___npcTalk(p, n, "Wooo woo");
                    Functions.___playerTalk(p, n, "Well if you insist");
                    Functions.___npcTalk(p, n, "Wooooooooo");
                    Functions.___playerTalk(p, n, "Ah well, better be off now");
                    Functions.___npcTalk(p, n, "Woo");
                    Functions.___playerTalk(p, n, "Bye");
                    break;
                case TheRestlessGhost.Ghost.WHY :
                    Functions.___npcTalk(p, n, "No, I just know I can't do anything much like this");
                    Functions.___playerTalk(p, n, "I've been told a certain task may need to be completed", "So you can rest in peace");
                    Functions.___npcTalk(p, n, "I should think it is probably because ", "a warlock has come along and stolen my skull", "If you look inside my coffin there", "you'll find my corpse without a head on it");
                    Functions.___playerTalk(p, n, "Do you know where this warlock might be now?");
                    Functions.___npcTalk(p, n, "I think it was one of the warlocks who lives in the big tower", "In the sea southwest from here");
                    Functions.___playerTalk(p, n, "Ok I will try and get the skull back for you, so you can rest in peace.");
                    Functions.___npcTalk(p, n, "Ooh thank you. That would be such a great relief", "It is so dull being a ghost");
                    p.updateQuestStage(Quests.THE_RESTLESS_GHOST, 3);
                    break;
                case TheRestlessGhost.Ghost.SCARY :
                    break;
            }
        }
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.GHOST_RESTLESS.id()) {
                        ghostDialogue(p, n, -1);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
        final QuestInterface quest = this;
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == TheRestlessGhost.GHOST_COFFIN_OPEN) || (obj.getID() == TheRestlessGhost.GHOST_COFFIN_CLOSED)) {
                        if (command.equalsIgnoreCase("open")) {
                            Functions.openGenericObject(obj, player, TheRestlessGhost.GHOST_COFFIN_OPEN, "You open the coffin");
                        } else
                            if (command.equalsIgnoreCase("close")) {
                                Functions.closeGenericObject(obj, player, TheRestlessGhost.GHOST_COFFIN_CLOSED, "You close the coffin");
                            } else {
                                if (player.getQuestStage(quest) > 0) {
                                    player.message("There's a skeleton without a skull in here");
                                } else
                                    if (player.getQuestStage(quest) == (-1)) {
                                        player.message("Theres a nice and complete skeleton in here!");
                                    } else {
                                        player.message("You search the coffin and find some human remains");
                                    }

                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player player) {
        final QuestInterface quest = this;
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((obj.getID() == TheRestlessGhost.GHOST_COFFIN_OPEN) && (player.getQuestStage(quest) == 3)) && (item.getID() == ItemId.QUEST_SKULL.id())) {
                        Functions.spawnNpc(player.getWorld(), NpcId.GHOST_RESTLESS.id(), 102, 675, 30);
                        Functions.___message(player, "You put the skull in the coffin");
                        Functions.removeItem(player, ItemId.QUEST_SKULL.id(), 1);
                        // on completion cache key no longer needed
                        player.getCache().remove("tried_grab_skull");
                        Npc npc = Functions.getNearestNpc(player, NpcId.GHOST_RESTLESS.id(), 8);
                        if (npc != null) {
                            npc.remove();
                        }
                        Functions.___message(player, "The ghost has vanished", "You think you hear a faint voice in the air", "Thank you");
                        player.sendQuestComplete(Quests.THE_RESTLESS_GHOST);
                        return null;
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.GHOST_RESTLESS.id();
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
        return (item.getID() == ItemId.QUEST_SKULL.id()) && (obj.getID() == TheRestlessGhost.GHOST_COFFIN_OPEN);
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return (obj.getID() == TheRestlessGhost.GHOST_COFFIN_OPEN) || (obj.getID() == TheRestlessGhost.GHOST_COFFIN_CLOSED);
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return i.getID() == ItemId.QUEST_SKULL.id();
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Npc skeleton = Functions.getNearestNpc(p, NpcId.SKELETON_RESTLESS.id(), 10);
                    if (i.getID() == ItemId.QUEST_SKULL.id()) {
                        // spawn-place
                        if ((i.getX() == 218) && (i.getY() == 3521)) {
                            if (p.getQuestStage(Quests.THE_RESTLESS_GHOST) != 3) {
                                Functions.___playerTalk(p, null, "That skull is scary", "I've got no reason to take it", "I think I'll leave it alone");
                                return null;
                            } else
                                if (!p.getCache().hasKey("tried_grab_skull")) {
                                    p.getCache().store("tried_grab_skull", true);
                                    p.getWorld().unregisterItem(i);
                                    Functions.addItem(p, ItemId.QUEST_SKULL.id(), 1);
                                    if (skeleton == null) {
                                        // spawn skeleton and give message
                                        p.message("Out of nowhere a skeleton appears");
                                        skeleton = Functions.spawnNpc(p.getWorld(), NpcId.SKELETON_RESTLESS.id(), 217, 3520, 100);
                                        skeleton.setChasing(p);
                                    } else {
                                        skeleton.setChasing(p);
                                    }
                                } else // allow if player had at least one time tried grab skull
                                {
                                    p.getWorld().unregisterItem(i);
                                    Functions.addItem(p, ItemId.QUEST_SKULL.id(), 1);
                                }

                        } else// allow wild if post-quest

                            if ((p.getQuestStage(Quests.THE_RESTLESS_GHOST) == (-1)) && (i.getY() <= 425)) {
                                p.getWorld().unregisterItem(i);
                                Functions.addItem(p, ItemId.QUEST_SKULL.id(), 1);
                            } else {
                                Functions.___playerTalk(p, null, "That skull is scary", "I've got no reason to take it", "I think I'll leave it alone");
                                return null;
                            }

                    }
                    return null;
                });
            }
        };
    }

    class Ghost {
        public static final int DONTSPEAK = 0;

        public static final int GOODBYE = 1;

        public static final int NOTSURE = 2;

        public static final int WHY = 3;

        public static final int SCARY = 4;
    }
}

