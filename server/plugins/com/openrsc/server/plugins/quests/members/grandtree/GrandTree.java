package com.openrsc.server.plugins.quests.members.grandtree;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class GrandTree implements QuestInterface , InvUseOnObjectListener , ObjectActionListener , PlayerAttackNpcListener , PlayerKilledNpcListener , TalkToNpcListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener , PlayerAttackNpcExecutiveListener , PlayerKilledNpcExecutiveListener , TalkToNpcExecutiveListener {
    /**
     * *********************************
     * Hazelmere coords: 535, 754       *
     * King narnode coords: 416, 165    *
     * *********************************
     */
    // the CAGE (prison) on top of the grand tree has open option but has no effect
    private static final int GLOUGHS_CUPBOARD_OPEN = 620;

    private static final int GLOUGHS_CUPBOARD_CLOSED = 619;

    private static final int GLOUGH_CHEST_OPEN = 631;

    private static final int GLOUGH_CHEST_CLOSED = 632;

    private static final int TREE_LADDER_UP = 585;

    private static final int TREE_LADDER_DOWN = 586;

    private static final int SHIPYARD_GATE = 624;

    private static final int STRONGHOLD_GATE = 626;

    private static final int WATCH_TOWER_UP = 635;

    private static final int WATCH_TOWER_DOWN = 646;

    private static final int WATCH_TOWER_STONE_STAND = 634;

    private static final int ROOT_ONE = 609;

    private static final int ROOT_TWO = 610;

    private static final int ROOT_THREE = 637;

    private static final int PUSH_ROOT = 638;

    private static final int PUSH_ROOT_BACK = 639;

    @Override
    public int getQuestId() {
        return Quests.GRAND_TREE;
    }

    @Override
    public String getQuestName() {
        return "Grand tree (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        p.message("well done you have completed the grand tree quest");
        int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.GRAND_TREE);
        // keep order kosher
        int[] skillIDs = new int[]{ Skills.AGILITY, Skills.ATTACK, Skills.MAGIC };
        // 1600 for agility, 1600 for attack, 600 for magic
        int[] baseAmounts = new int[]{ 1600, 1600, 600 };
        // 1200 for agility, 1200 for attack, 200 for magic
        int[] varAmounts = new int[]{ 1200, 1200, 200 };
        for (int i = 0; i < skillIDs.length; i++) {
            questData[Quests.MAPIDX_SKILL] = skillIDs[i];
            questData[Quests.MAPIDX_BASE] = baseAmounts[i];
            questData[Quests.MAPIDX_VAR] = varAmounts[i];
            Functions.incQuestReward(p, questData, i == (skillIDs.length - 1));
        }
        p.message("@gre@You haved gained 5 quest points!");
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return DataConversions.inArray(new int[]{ NpcId.KING_NARNODE_SHAREEN.id(), NpcId.KING_NARNODE_SHAREEN_UNDERGROUND.id(), NpcId.HAZELMERE.id(), NpcId.GLOUGH.id(), NpcId.CHARLIE.id(), NpcId.SHIPYARD_WORKER_WHITE.id(), NpcId.SHIPYARD_WORKER_BLACK.id(), NpcId.SHIPYARD_FOREMAN.id(), NpcId.SHIPYARD_FOREMAN_HUT.id(), NpcId.FEMI.id(), NpcId.FEMI_STRONGHOLD.id(), NpcId.ANITA.id() }, n.getID());
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.KING_NARNODE_SHAREEN.id()) {
                        switch (p.getQuestStage(quest)) {
                            case 0 :
                                Functions.___playerTalk(p, n, "hello there");
                                Functions.___npcTalk(p, n, "hello traveller, i'm king shareem, welcome", "it's nice to see an outsider");
                                Functions.___playerTalk(p, n, "it seems to be quite a busy settlement");
                                Functions.___npcTalk(p, n, "for now it is, thankfully");
                                p.message("King shareem seems troubled");
                                int option = Functions.___showMenu(p, n, "you seem worried, what's wrong?", "well, i'll be on my way");
                                if (option == 0) {
                                    Functions.___npcTalk(p, n, "adventurer, can i speak to you in the strictest confidence");
                                    Functions.___playerTalk(p, n, "of course narnode");
                                    Functions.___npcTalk(p, n, "not here, follow me");
                                    p.message("king shareem bends down and places his hands on the stone tile");
                                    Functions.___message(p, "you here a creak as he turns the tile clockwise", "the tile slides away, revealing a small tunnel", "you follow king shareem down");
                                    Functions.movePlayer(p, 703, 3284);
                                    Npc kingNarnode = Functions.getNearestNpc(p, NpcId.KING_NARNODE_SHAREEN_UNDERGROUND.id(), 15);
                                    if (kingNarnode != null) {
                                        Functions.___playerTalk(p, kingNarnode, "so what is this place?");
                                        Functions.___npcTalk(p, kingNarnode, "these my friend, are the foundations of the stronghold");
                                        Functions.___playerTalk(p, kingNarnode, "they just look like roots");
                                        Functions.___npcTalk(p, kingNarnode, "not any roots traveller", "these were conjured in the past age by gnome mages", "since then, they have grown into our mighty stronghold");
                                        Functions.___playerTalk(p, kingNarnode, "impressive, but what exactly is the problem?");
                                        Functions.___npcTalk(p, kingNarnode, "in the last two months our tree guardians have reported...", "...continuing deterioration of the grand trees health", "i've never seen this before, it could mean the end for all of us");
                                        Functions.___playerTalk(p, kingNarnode, "you mean the tree is ill");
                                        Functions.___npcTalk(p, kingNarnode, "in a magical sense yes", "would you be willing to help us discover the cause of this illness");
                                        int op = Functions.___showMenu(p, kingNarnode, "i'm sorry i don't want to get involved", "i'd be happy to help");
                                        if (op == 0) {
                                            Functions.___npcTalk(p, kingNarnode, "i understand traveller", "please keep this to yourself");
                                            Functions.___playerTalk(p, kingNarnode, "of course");
                                            Functions.___npcTalk(p, kingNarnode, "i'll show you the way back up");
                                            p.message("you follow king shareem up the ladder");
                                            p.teleport(415, 163);
                                        } else
                                            if (op == 1) {
                                                Functions.___npcTalk(p, kingNarnode, "thank guthix for you arrival", "the first task is to find out what's killing my tree");
                                                Functions.___playerTalk(p, kingNarnode, "have you any ideas?");
                                                Functions.___npcTalk(p, kingNarnode, "my top tree guardian, glough, believes it's human sabotage", "i'm not so sure", "the only way to really know, is to talk to Hazelmere");
                                                Functions.___playerTalk(p, kingNarnode, "who's hazelmere?");
                                                Functions.___npcTalk(p, kingNarnode, "a once all powerful mage who created the grand tree", "one of the only survivors of the old age", "take this bark sample to him, he should be able to help", "the mage only talks in the old tongue, you'll need this");
                                                Functions.___playerTalk(p, kingNarnode, "what is it?");
                                                Functions.___npcTalk(p, kingNarnode, "a translation book, translate carefully, his words may save us all", "you'll find his dwellings high upon a towering hill..", "..on a island south of the khazard fight arena");
                                                p.message("king shareem gives you a book and a bark sample");
                                                Functions.addItem(p, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
                                                Functions.addItem(p, ItemId.BARK_SAMPLE.id(), 1);
                                                Functions.___npcTalk(p, kingNarnode, "i'll show you the way back up");
                                                p.message("you follow king shareem up the ladder");
                                                Functions.movePlayer(p, 415, 163);
                                                p.updateQuestStage(quest, 1);
                                                // no longer needed
                                                p.getCache().remove("helped_femi");
                                            }

                                    }
                                } else
                                    if (option == 1) {
                                        Functions.___npcTalk(p, n, "ok then, enjoy your stay with us", "there's many shops and sights to see");
                                    }

                                break;
                            case 1 :
                                Functions.___playerTalk(p, n, "hello king shareem");
                                Functions.___npcTalk(p, n, "traveller, you've returned", "any word from hazelmere?");
                                Functions.___playerTalk(p, n, "not yet i'm afraid");
                                if ((!Functions.hasItem(p, ItemId.TREE_GNOME_TRANSLATION.id())) || (!Functions.hasItem(p, ItemId.BARK_SAMPLE.id()))) {
                                    if (!Functions.hasItem(p, ItemId.BARK_SAMPLE.id())) {
                                        Functions.___playerTalk(p, n, "but i've lost the bark sample");
                                        Functions.___npcTalk(p, n, "here take another and try to hang on to it");
                                        p.message("king shareem gives you another bark sample");
                                        Functions.addItem(p, ItemId.BARK_SAMPLE.id(), 1);
                                    }
                                    if (!Functions.hasItem(p, ItemId.TREE_GNOME_TRANSLATION.id())) {
                                        Functions.___playerTalk(p, n, "but i've lost the book you gave me");
                                        Functions.___npcTalk(p, n, "don't worry i have more", "here you go");
                                        p.message("king shareem gives you a translation book");
                                        Functions.addItem(p, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
                                    }
                                } else {
                                    Functions.___npcTalk(p, n, "hazalmere lives on a island just south of the fight arena", "give him the sample and translate his reply", "i just hope he can help in our hour of need");
                                }
                                break;
                            case 2 :
                                Functions.___playerTalk(p, n, "hello again king shareem");
                                Functions.___npcTalk(p, n, "well hello traveller, did you speak to hazelmere?");
                                Functions.___playerTalk(p, n, "yes, i managed to find him");
                                Functions.___npcTalk(p, n, "and do you know what he said?");
                                int menu = Functions.___showMenu(p, n, "i think so", "no, i need to go back");
                                if (menu == 0) {
                                    Functions.___npcTalk(p, n, "so what did he say?");
                                    int qmenu = Functions.___showMenu(p, "hello there traveller", "king shareem must be stopped", "praise to the great zamorak", "have you any bread", "none of the above");
                                    if (qmenu == 0) {
                                        questionMenu2(p, n);
                                    } else
                                        if (qmenu == 1) {
                                            questionMenu2(p, n);
                                        } else
                                            if (qmenu == 2) {
                                                questionMenu2(p, n);
                                            } else
                                                if (qmenu == 3) {
                                                    questionMenu2(p, n);
                                                } else
                                                    if (qmenu == 4) {
                                                        questionMenu2(p, n);
                                                        if (!p.getCache().hasKey("gt_q1")) {
                                                            p.getCache().store("gt_q1", true);
                                                        }
                                                    }




                                } else
                                    if (menu == 1) {
                                        if ((!Functions.hasItem(p, ItemId.TREE_GNOME_TRANSLATION.id())) || (!Functions.hasItem(p, ItemId.BARK_SAMPLE.id()))) {
                                            if (!Functions.hasItem(p, ItemId.BARK_SAMPLE.id())) {
                                                Functions.___playerTalk(p, n, "but i've lost the bark sample");
                                                Functions.___npcTalk(p, n, "here take another and try to hang on to it");
                                                p.message("king shareem gives you another bark sample");
                                                Functions.addItem(p, ItemId.BARK_SAMPLE.id(), 1);
                                            }
                                            if (!Functions.hasItem(p, ItemId.TREE_GNOME_TRANSLATION.id())) {
                                                Functions.___playerTalk(p, n, "but i've lost the book you gave me");
                                                Functions.___npcTalk(p, n, "don't worry i have more", "here you go");
                                                p.message("king shareem gives you a translation book");
                                                Functions.addItem(p, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
                                            }
                                        }
                                        Functions.___npcTalk(p, n, "time is of the essence adventurer");
                                    }

                                break;
                            case 3 :
                                Functions.___playerTalk(p, n, "hello narnode");
                                Functions.___npcTalk(p, n, "hello traveller, did you speak to glough?");
                                Functions.___playerTalk(p, n, "not yet");
                                Functions.___npcTalk(p, n, "ok, he lives just in front of the grand tree", "let me know once you've spoken to him");
                                break;
                            case 4 :
                                Functions.___playerTalk(p, n, "hello king shareem", "have you any news on the daconia stones?");
                                Functions.___npcTalk(p, n, "it's ok traveller, thank's to glough", "he found a human sneaking around...", "...with three daconia stones in his satchel");
                                Functions.___playerTalk(p, n, "i'm amazed that you retrieved them so easily");
                                Functions.___npcTalk(p, n, "yes, glough must really know what he's doing", "the human has been detained until we know who's involved", "maybe glough was right, maybe humans are invading");
                                Functions.___playerTalk(p, n, "i doubt it, can i speak to the prisoner");
                                Functions.___npcTalk(p, n, "certainly, he's on the top level of the grand tree", "be careful up there, it's a long way down");
                                p.updateQuestStage(quest, 5);
                                break;
                            case 5 :
                                Functions.___playerTalk(p, n, "hi narnode");
                                Functions.___npcTalk(p, n, "hello traveller", "if you wish to talk to the prisoner", "go to the top tree level", "you'll find him there");
                                Functions.___playerTalk(p, n, "thanks");
                                break;
                            case 6 :
                                Functions.___playerTalk(p, n, "king shareem");
                                Functions.___npcTalk(p, n, "hello adventurer, so did you speak to the culprit?");
                                Functions.___playerTalk(p, n, "yes i did and something's not right");
                                Functions.___npcTalk(p, n, "what do you mean?");
                                Functions.___playerTalk(p, n, "the prisoner claims he was paid by glough to get the stones");
                                Functions.___npcTalk(p, n, "that's an absurd story, he's just trying to save himself", "since glough's wife died he has been a little strange", "but he would never wrongly imprison someone", "now the culprit's locked up we can all relax", "it's sad but i think glough was right", "humans are planning to invade and wipe us tree gnomes out");
                                Functions.___playerTalk(p, n, "but why?");
                                Functions.___npcTalk(p, n, "who knows? but you may have to leave soon adventurer", "i trust you, but the local gnomes are getting paranoid");
                                Functions.___playerTalk(p, n, "that's a shame");
                                Functions.___npcTalk(p, n, "hopefully i can keep my people calm, we'll see");
                                break;
                            case 7 :
                                Functions.___playerTalk(p, n, "king shareem, i'm concerned about glough");
                                Functions.___npcTalk(p, n, "why, don't worry yourself about him", "now the culprit has been caught...", "..i'm sure glough's resentment of humans will die away");
                                Functions.___playerTalk(p, n, "i'm not so sure");
                                Functions.___npcTalk(p, n, "he just has an active imagination", "if your really concerned, speak to him");
                                break;
                            case 8 :
                            case 9 :
                                Functions.___playerTalk(p, n, "hello narnode");
                                Functions.___npcTalk(p, n, "traveller, haven't you heard", "glough has set a warrant for your arrest", "he has guards at the exit", "i shouldn't have told you this", "but i can see your a good person", "please take the glider and leave before it's too late");
                                Functions.___playerTalk(p, n, "all the best narnode");
                                break;
                            case 10 :
                                Functions.___playerTalk(p, n, "king shareem,i need to talk");
                                Functions.___npcTalk(p, n, "traveller, what are you doing here?", "the stronghold has been put on full alert", "it's not safe for you here");
                                Functions.___playerTalk(p, n, "narnode, i believe glough is killing the trees", "in order to make a mass fleet of warships");
                                Functions.___npcTalk(p, n, "that's an absurd accusation");
                                Functions.___playerTalk(p, n, "his hatred for humanity is stronger than you know");
                                Functions.___npcTalk(p, n, "that's enough traveller, you sound as paranoid as him", "traveller please leave", "it's bad enough having one human locked up");
                                break;
                            case 11 :
                                Functions.___playerTalk(p, n, "hello narnode");
                                Functions.___npcTalk(p, n, "please traveller, if the gnomes see me talking to you", "they'll revolt against me");
                                Functions.___playerTalk(p, n, "that's crazy");
                                Functions.___npcTalk(p, n, "glough's scared the whole town", "he expects the humans to attack any day", "he's even began to recuit hundreds of gnome soldiers");
                                Functions.___playerTalk(p, n, "don't you understand he's creating his own army");
                                Functions.___npcTalk(p, n, "please traveller, just leave before it's too late");
                                break;
                            case 12 :
                                Functions.___playerTalk(p, n, "look, i found this at glough's home");
                                Functions.___message(p, "you give the king the strategic notes");
                                Functions.removeItem(p, ItemId.GLOUGHS_NOTES.id(), 1);
                                Functions.___npcTalk(p, n, "hmmm, these are interesting", "but it's not proof, any one could have made these", "traveller, i understand your concern", "i had guards search glough's house", "but they found nothing suspicious", "just these old pebbles");
                                Functions.___message(p, "narnode gives you four old pebbles");
                                Functions.addItem(p, ItemId.PEBBLE_3.id(), 1);
                                Functions.addItem(p, ItemId.PEBBLE_2.id(), 1);
                                Functions.addItem(p, ItemId.PEBBLE_4.id(), 1);
                                Functions.addItem(p, ItemId.PEBBLE_1.id(), 1);
                                Functions.___npcTalk(p, n, "on the other hand, if glough's right about the humans", "we will need an army of gnomes to protect ourselves", "so i've decided to allow glough to raise a mighty gnome army", "the grand tree's still slowly dying, if it is human sabotage", "we must respond");
                                p.updateQuestStage(quest, 13);
                                break;
                            case 13 :
                                Functions.___playerTalk(p, n, "hello again narnode");
                                Functions.___npcTalk(p, n, "please traveller, take my advice and leave");
                                if ((((!Functions.hasItem(p, ItemId.PEBBLE_3.id())) || (!Functions.hasItem(p, ItemId.PEBBLE_2.id()))) || (!Functions.hasItem(p, ItemId.PEBBLE_4.id()))) || (!Functions.hasItem(p, ItemId.PEBBLE_1.id()))) {
                                    Functions.___playerTalk(p, n, "have you any more of those pebbles");
                                    Functions.___npcTalk(p, n, "well, yes as it goes, why?");
                                    Functions.___playerTalk(p, n, "i lost some");
                                    Functions.___npcTalk(p, n, "here take these, i don't see how it will help though");
                                    Functions.___message(p, "narnode replaces your lost pebbles");
                                    if (p.getCache().hasKey("pebble_1")) {
                                        p.getCache().remove("pebble_1");
                                    }
                                    if (p.getCache().hasKey("pebble_2")) {
                                        p.getCache().remove("pebble_2");
                                    }
                                    if (p.getCache().hasKey("pebble_3")) {
                                        p.getCache().remove("pebble_3");
                                    }
                                    if (p.getCache().hasKey("pebble_4")) {
                                        p.getCache().remove("pebble_4");
                                    }
                                    if (!Functions.hasItem(p, ItemId.PEBBLE_3.id())) {
                                        Functions.addItem(p, ItemId.PEBBLE_3.id(), 1);
                                    }
                                    if (!Functions.hasItem(p, ItemId.PEBBLE_2.id())) {
                                        Functions.addItem(p, ItemId.PEBBLE_2.id(), 1);
                                    }
                                    if (!Functions.hasItem(p, ItemId.PEBBLE_4.id())) {
                                        Functions.addItem(p, ItemId.PEBBLE_4.id(), 1);
                                    }
                                    if (!Functions.hasItem(p, ItemId.PEBBLE_1.id())) {
                                        Functions.addItem(p, ItemId.PEBBLE_1.id(), 1);
                                    }
                                } else
                                    if (((Functions.hasItem(p, ItemId.PEBBLE_3.id()) && Functions.hasItem(p, ItemId.PEBBLE_2.id())) && Functions.hasItem(p, ItemId.PEBBLE_4.id())) && Functions.hasItem(p, ItemId.PEBBLE_1.id())) {
                                        Functions.___npcTalk(p, n, "it's not safe for you here");
                                    }

                                break;
                            case 14 :
                                Functions.___playerTalk(p, n, "narnode, it's true about glough i tell you", "he's planning to take over runescape");
                                Functions.___npcTalk(p, n, "i'm sorry traveller but it's just not realistic", "how could glough- even with a gnome army- take over?");
                                Functions.___playerTalk(p, n, "he plans to make a fleet of warships from the grand tree's wood");
                                Functions.___npcTalk(p, n, "that's enough traveller, i've no time for make believe", "the tree's still dying, i must get to the truth of this");
                                break;
                            case -1 :
                                Functions.___playerTalk(p, n, "hello narnode");
                                Functions.___npcTalk(p, n, "well hello again adventurer", "how are you?");
                                Functions.___playerTalk(p, n, "i'm good thanks, how's the tree?");
                                Functions.___npcTalk(p, n, "better than ever, thanks for asking");
                                break;
                        }
                    } else
                        if (n.getID() == NpcId.HAZELMERE.id()) {
                            switch (p.getQuestStage(quest)) {
                                case 0 :
                                    Functions.___message(p, "the mage mumbles in an ancient tounge", "you can't understand a word");
                                    break;
                                case 1 :
                                    Functions.___playerTalk(p, n, "hello");
                                    if (Functions.hasItem(p, ItemId.BARK_SAMPLE.id())) {
                                        Functions.___message(p, "you give the mage the bark sample");
                                        Functions.removeItem(p, ItemId.BARK_SAMPLE.id(), 1);
                                        Functions.___message(p, "the mage speaks in a strange ancient tongue", "he says....");
                                        strangeTranslationBox(p);
                                        p.updateQuestStage(quest, 2);
                                    } else {
                                        Functions.___message(p, "the mage mumbles in an ancient tounge", "you can't understand a word");
                                        Functions.___message(p, "you need to give him the bark sample");
                                    }
                                    break;
                                case 2 :
                                    Functions.___message(p, "the mage speaks in a strange ancient tongue", "he says....");
                                    strangeTranslationBox(p);
                                    break;
                                case 3 :
                                case 4 :
                                case 5 :
                                case 6 :
                                case 7 :
                                case 8 :
                                case 9 :
                                case 10 :
                                case 11 :
                                case 12 :
                                case 13 :
                                case 14 :
                                case 15 :
                                case 16 :
                                case -1 :
                                    p.message("the mage mumbles in an ancient tounge");
                                    p.message("you can't understand a word");
                                    break;
                            }
                        } else
                            if (n.getID() == NpcId.GLOUGH.id()) {
                                switch (p.getQuestStage(quest)) {
                                    case 0 :
                                    case 1 :
                                    case 2 :
                                    case 4 :
                                    case 5 :
                                    case 6 :
                                    case 8 :
                                    case 9 :
                                    case 12 :
                                    case 13 :
                                    case 14 :
                                    case 15 :
                                    case 16 :
                                    case -1 :
                                        Functions.___playerTalk(p, n, "hello there");
                                        Functions.___npcTalk(p, n, "you shouldn't be here human");
                                        Functions.___playerTalk(p, n, "what do you mean?");
                                        Functions.___npcTalk(p, n, "the gnome stronghold is for gnomes alone");
                                        Functions.___playerTalk(p, n, "surely not!");
                                        Functions.___npcTalk(p, n, "we don't need you're sort around here");
                                        Functions.___message(p, "he doesn't seem very nice");
                                        break;
                                    case 3 :
                                        Functions.___playerTalk(p, n, "hello");
                                        Functions.___message(p, "the gnome is munching on a worm hole");
                                        Functions.___npcTalk(p, n, "can i help human, can't you see i'm eating?", "these are my favourite");
                                        Functions.___message(p, "the gnome continues to eat");
                                        Functions.___playerTalk(p, n, "the king asked me to inform you...", "that the daconia rocks have been taken");
                                        Functions.___npcTalk(p, n, "surley not!");
                                        Functions.___playerTalk(p, n, "apparently a human took them from hazelmere", "he had a permission note with the king's seal");
                                        Functions.___npcTalk(p, n, "i should have known, the humans are going to invade");
                                        Functions.___playerTalk(p, n, "never");
                                        Functions.___npcTalk(p, n, "your type can't be trusted", "i'll take care of this, you go back to the king");
                                        p.updateQuestStage(quest, 4);
                                        break;
                                    case 7 :
                                        Functions.___playerTalk(p, n, "glough, i don't know what you're up to...", "...but i know you paid charlie to get those rocks");
                                        Functions.___npcTalk(p, n, "you're a fool human", "you have no idea whats going on");
                                        Functions.___playerTalk(p, n, "i know the grand tree's dying", "and i think you're part of the reason");
                                        Functions.___npcTalk(p, n, "how dare you accuse me, i'm the head tree guardian", "guards...guards");
                                        p.message("gnome guards hurry up the ladder");
                                        // 418, 2992
                                        Npc gnome_guard = Functions.spawnNpc(p.getWorld(), NpcId.GNOME_GUARD_PRISON.id(), 714, 1421, 12000);
                                        Functions.___npcTalk(p, n, "take him away");
                                        p.face(gnome_guard);
                                        gnome_guard.face(p);
                                        Functions.___playerTalk(p, n, "what for?");
                                        Functions.___npcTalk(p, n, "grand treason against his majesty king shareem", "this man is a human spy");
                                        gnome_guard.remove();
                                        Functions.___npcTalk(p, n, "lock him up");
                                        Functions.___message(p, "the gnome guards take you to the top of the grand tree");
                                        p.teleport(419, 2992);
                                        Functions.sleep(5000);
                                        Npc jailCharlie = Functions.getNearestNpc(p, NpcId.CHARLIE.id(), 5);
                                        Functions.___npcTalk(p, jailCharlie, "so, they've got you as well");
                                        Functions.___playerTalk(p, jailCharlie, "it's glough, he's trying to cover something up");
                                        Functions.___npcTalk(p, jailCharlie, "i shouldn't tell you this adventurer", "but if you want to get to the bottom of this", "you should go and talk to the karamja foreman");
                                        Functions.___playerTalk(p, jailCharlie, "why?");
                                        Functions.___npcTalk(p, jailCharlie, "glough sent me to karamja to meet him", "i delivered a large amount of gold", "for what i do not know", "but he may be able to tell you what glough's up to", "that's if you can get out of here", "you'll find him in a ship yard south of birmhaven", "be careful, if he discovers that you're not...", "...working for glough there'll be trouble", "the sea men use the pass word ka-lu-min");
                                        Functions.___playerTalk(p, jailCharlie, "thanks charlie");
                                        Functions.sleep(5000);
                                        Npc narnode = Functions.spawnNpc(p.getWorld(), NpcId.KING_NARNODE_SHAREEN.id(), 419, 2993, 36000);
                                        Functions.___npcTalk(p, narnode, "adventurer please accept my apologies", "glough had no right to arrest you", "i just think he's scared of humans", "let me get you out of there");
                                        Functions.___message(p, "king shareem opens the cage");
                                        p.teleport(418, 2993);
                                        Functions.___playerTalk(p, narnode, "i don't think you can trust glough, narnode", "he seems to have a unatural hatred for humans");
                                        Functions.___npcTalk(p, narnode, "i know he can seem a little extreme at times", "but he's the best tree guardian i have", "he has however caused much fear towards humans", "i'm afraid he's placed guards on the front gate...", "...to stop you escaping", "let my glider pilot fly you away", "untill things calm down around here");
                                        Functions.___playerTalk(p, narnode, "well, if that's how you feel");
                                        narnode.remove();
                                        p.updateQuestStage(quest, 8);
                                        break;
                                    case 10 :
                                        Functions.___playerTalk(p, n, "I know what you're up to glough");
                                        Functions.___npcTalk(p, n, "you have no idea human");
                                        Functions.___playerTalk(p, n, "you may be able to make a fleet", "but the tree gnomes will never follow you into battle");
                                        Functions.___npcTalk(p, n, "so, you know more than i thought, i'm impressed", "the gnomes fear humanity more than any other race", "i just need to give them a push in the right direction", "there's nothing you can do traveller", "leave before it's too late", "soon all of runescape will feel the wrath of glough");
                                        Functions.___playerTalk(p, n, "king shareem won't allow it");
                                        Functions.___npcTalk(p, n, "the king's a fool and a coward, he'll soon bow to me", "and you'll soon be back in that cage");
                                        break;
                                    case 11 :
                                        Functions.___playerTalk(p, n, "i'm going to stop you glough");
                                        Functions.___npcTalk(p, n, "you're becoming quite annoying traveller");
                                        Functions.___message(p, "glough is searching his pockets", "he seems very uptight");
                                        Functions.___npcTalk(p, n, "damn keys", "leave human, before i have you put in the cage");
                                        break;
                                }
                            } else
                                if (n.getID() == NpcId.CHARLIE.id()) {
                                    switch (p.getQuestStage(quest)) {
                                        case 0 :
                                        case 1 :
                                        case 2 :
                                        case 3 :
                                        case 4 :
                                        case 12 :
                                        case 13 :
                                        case 14 :
                                        case 15 :
                                        case 16 :
                                        case -1 :
                                            p.message("the prisoner is in no mood to talk");
                                            break;
                                        case 5 :
                                            Functions.___playerTalk(p, n, "tell me,why would you want to kill the grand tree?");
                                            Functions.___npcTalk(p, n, "what do you mean?");
                                            Functions.___playerTalk(p, n, "don't tell me, you just happened to be caught carrying daconia rocks!");
                                            Functions.___npcTalk(p, n, "all i know, is that i did what i was asked");
                                            Functions.___playerTalk(p, n, "i don't understand?");
                                            Functions.___npcTalk(p, n, "glough paid me to go see this gnome on a hill", "i gave the gnome a letter glough gave me", "and he gave me some rocks to give glough", "i've been doing it for weeks, it's just this time..", "...when i returned glough locked me up here", "i just don't understand it");
                                            Functions.___playerTalk(p, n, "sounds like glough's hiding something");
                                            Functions.___npcTalk(p, n, "i don't know what he's up to", "but if you want to find out...", "..you better search his home");
                                            Functions.___playerTalk(p, n, "ok, thanks charlie");
                                            Functions.___npcTalk(p, n, "good luck");
                                            p.updateQuestStage(quest, 6);
                                            break;
                                        case 6 :
                                        case 7 :
                                            Functions.___playerTalk(p, n, "hello charlie");
                                            Functions.___npcTalk(p, n, "hello adventurer, have you figured out what's going on?");
                                            Functions.___playerTalk(p, n, "no idea");
                                            Functions.___npcTalk(p, n, "to get to the bottom of this you'll need to search glough's home");
                                            break;
                                        case 8 :
                                        case 9 :
                                            Functions.___playerTalk(p, n, "i can't figure this out charlie");
                                            Functions.___npcTalk(p, n, "go and see a forman in west karamja", "there's a shipyard there,you might find some clues", "don't forget the password's ka-lu-min", "if they realise that you're not working for glough...", "...there'll be trouble");
                                            break;
                                        case 10 :
                                        case 11 :
                                            Functions.___playerTalk(p, n, "how are you doing charlie");
                                            Functions.___npcTalk(p, n, "i've been better");
                                            Functions.___playerTalk(p, n, "glough has some plan to rule runescape");
                                            Functions.___npcTalk(p, n, "i wouldn't put it past him, the gnome's crazy");
                                            Functions.___playerTalk(p, n, "i need some proof to convince the king");
                                            Functions.___npcTalk(p, n, "hmmm, you could be in luck", "before glough had me locked up i heard him mention..", "..that he'd left his chest lock keys at his girlfriends");
                                            Functions.___playerTalk(p, n, "where does she live?");
                                            Functions.___npcTalk(p, n, "just west of the toad swamp");
                                            Functions.___playerTalk(p, n, "okay, i'll see what i can find");
                                            if (p.getQuestStage(quest) == 10) {
                                                p.updateQuestStage(quest, 11);
                                            }
                                            break;
                                    }
                                } else
                                    if ((n.getID() == NpcId.SHIPYARD_WORKER_WHITE.id()) || (n.getID() == NpcId.SHIPYARD_WORKER_BLACK.id())) {
                                        int selected = DataConversions.random(0, 14);
                                        Functions.___playerTalk(p, n, "hello");
                                        if (selected == 0) {
                                            Functions.___npcTalk(p, n, "ouch");
                                            Functions.___playerTalk(p, n, "what's wrong?");
                                            Functions.___npcTalk(p, n, "i cut my finger", "do you have a bandage?");
                                            Functions.___playerTalk(p, n, "i'm afraid not");
                                            Functions.___npcTalk(p, n, "that's ok, i'll use my shirt");
                                        } else
                                            if (selected == 1) {
                                                Functions.___playerTalk(p, n, "you look busy");
                                                Functions.___npcTalk(p, n, "we need double the men to get..", "...this order out on time");
                                            } else
                                                if (selected == 2) {
                                                    Functions.___npcTalk(p, n, "hello matey");
                                                    Functions.___playerTalk(p, n, "how are you?");
                                                    Functions.___npcTalk(p, n, "tired");
                                                    Functions.___playerTalk(p, n, "you shouldn't work so hard");
                                                } else
                                                    if (selected == 3) {
                                                        Functions.___playerTalk(p, n, "what are you building?");
                                                        Functions.___npcTalk(p, n, "are you serious?");
                                                        Functions.___playerTalk(p, n, "of course not", "you're obviously building a boat");
                                                    } else
                                                        if (selected == 4) {
                                                            Functions.___playerTalk(p, n, "looks like hard work");
                                                            Functions.___npcTalk(p, n, "i like to keep busy");
                                                        } else
                                                            if (selected == 5) {
                                                                Functions.___npcTalk(p, n, "no time to talk", "we've a fleet to build");
                                                            } else
                                                                if (selected == 6) {
                                                                    Functions.___playerTalk(p, n, "quite an impressive set up");
                                                                    Functions.___npcTalk(p, n, "it needs to be...", "..there's no other way to build a fleet of this size");
                                                                } else
                                                                    if (selected == 7) {
                                                                        Functions.___playerTalk(p, n, "quite a few ships you're building");
                                                                        Functions.___npcTalk(p, n, "this is just the start", "the completed fleet will be awesome");
                                                                    } else
                                                                        if (selected == 8) {
                                                                            Functions.___playerTalk(p, n, "so where are you sailing?");
                                                                            Functions.___npcTalk(p, n, "what do you mean?");
                                                                            Functions.___playerTalk(p, n, "don't worry, just kidding!");
                                                                        } else
                                                                            if (selected == 9) {
                                                                                Functions.___playerTalk(p, n, "how are you?");
                                                                                Functions.___npcTalk(p, n, "too busy to waste time gossiping");
                                                                                Functions.___playerTalk(p, n, "touchy");
                                                                            } else
                                                                                if (selected == 10) {
                                                                                    Functions.___npcTalk(p, n, "can i help you");
                                                                                    Functions.___playerTalk(p, n, "i'm just looking around");
                                                                                    Functions.___npcTalk(p, n, "well there's plenty of work to be done", "so if you don't mind...");
                                                                                    Functions.___playerTalk(p, n, "of course, sorry to have disturbed you");
                                                                                } else
                                                                                    if (selected == 11) {
                                                                                        Functions.___npcTalk(p, n, "hello there", "are you too lazy to work as well");
                                                                                        Functions.___playerTalk(p, n, "something like that");
                                                                                        Functions.___npcTalk(p, n, "i'm just sun bathing");
                                                                                    } else
                                                                                        if (selected == 12) {
                                                                                            Functions.___npcTalk(p, n, "hello there");
                                                                                            Functions.___npcTalk(p, n, "i haven't seen you before");
                                                                                            Functions.___playerTalk(p, n, "i'm new");
                                                                                            Functions.___npcTalk(p, n, "well it's hard work, but the pay is good");
                                                                                        } else
                                                                                            if (selected == 13) {
                                                                                                Functions.___npcTalk(p, n, "what do you want?");
                                                                                                Functions.___playerTalk(p, n, "is that any way to talk to your new superior?");
                                                                                                Functions.___npcTalk(p, n, "oh, i'm sorry, i didn't realise");
                                                                                            }













                                    } else
                                        if (n.getID() == NpcId.SHIPYARD_FOREMAN.id()) {
                                            if (p.getQuestStage(quest) >= 10) {
                                                p.message("the forman is too busy to talk");
                                                return null;
                                            }
                                            Functions.___playerTalk(p, n, "hello, are you in charge?");
                                            Functions.___npcTalk(p, n, "that's right, and you are?");
                                            Functions.___playerTalk(p, n, "glough sent me to check up on things");
                                            Functions.___npcTalk(p, n, "is that right, glough sent a human");
                                            Functions.___playerTalk(p, n, "his gnomes were all busy");
                                            Functions.___npcTalk(p, n, "ok, we had better go inside, follow me");
                                            p.teleport(408, 753);
                                            if (p.getQuestStage(quest) == 8) {
                                                p.message("you follow the foreman into the wooden hut");
                                                Npc HUT_FOREMAN = Functions.getNearestNpc(p, NpcId.SHIPYARD_FOREMAN_HUT.id(), 4);
                                                Functions.___npcTalk(p, HUT_FOREMAN, "so tell me again why you're here");
                                                Functions.___playerTalk(p, HUT_FOREMAN, "erm...glough sent me?");
                                                Functions.___npcTalk(p, HUT_FOREMAN, "ok and how is glough..still with his wife?");
                                                int menu = Functions.___showMenu(p, HUT_FOREMAN, "yes, they're both getting on great", "always arguing as usual", "his wife is no longer with us");
                                                if ((menu == 0) || (menu == 1)) {
                                                    p.updateQuestStage(quest, 9);
                                                    Functions.___npcTalk(p, HUT_FOREMAN, "really...", "..that's strange, considering she died last year", "die imposter");
                                                    HUT_FOREMAN.setChasing(p);
                                                } else
                                                    if (menu == 2) {
                                                        Functions.___npcTalk(p, HUT_FOREMAN, "right answear, i have to watch out for imposters", "if really know glough...", "you know his favourite gnome dish");
                                                        int menu2 = Functions.___showMenu(p, HUT_FOREMAN, "he loves tangled toads legs", "he loves worm holes", "he loves choc bombs");
                                                        if ((menu2 == 0) || (menu2 == 2)) {
                                                            p.updateQuestStage(quest, 9);
                                                            Functions.___npcTalk(p, HUT_FOREMAN, "he hates them", "die imposter");
                                                            HUT_FOREMAN.setChasing(p);
                                                        } else
                                                            if (menu2 == 1) {
                                                                Functions.___npcTalk(p, HUT_FOREMAN, "ok, one more question", "what's the name of his new girlfriend");
                                                                int menu3 = // do not send over
                                                                Functions.___showMenu(p, HUT_FOREMAN, false, "Alia", "Anita", "Elena");
                                                                if ((menu3 == 0) || (menu3 == 2)) {
                                                                    p.updateQuestStage(quest, 9);
                                                                    if (menu3 == 0) {
                                                                        Functions.___playerTalk(p, HUT_FOREMAN, "alia");
                                                                    } else
                                                                        if (menu3 == 2) {
                                                                            Functions.___playerTalk(p, HUT_FOREMAN, "elena");
                                                                        }

                                                                    Functions.___npcTalk(p, HUT_FOREMAN, "you almost fooled me", "die imposter");
                                                                    HUT_FOREMAN.setChasing(p);
                                                                } else
                                                                    if (menu3 == 1) {
                                                                        Functions.___playerTalk(p, HUT_FOREMAN, "anita");
                                                                        Functions.___npcTalk(p, HUT_FOREMAN, "well, well ,well, you do know glough", "sorry for the interrogation but i'm sure you understand");
                                                                        Functions.___playerTalk(p, HUT_FOREMAN, "of course, security is paramount");
                                                                        Functions.___npcTalk(p, HUT_FOREMAN, "as you can see the ship builders are ready");
                                                                        Functions.___playerTalk(p, HUT_FOREMAN, "indeed");
                                                                        Functions.___npcTalk(p, HUT_FOREMAN, "when i was asked to build a fleet large enough...", "..to invade port sarim and carry 300 gnome troops...", "..i said if anyone can, i can");
                                                                        Functions.___playerTalk(p, HUT_FOREMAN, "that's a lot of troops");
                                                                        Functions.___npcTalk(p, HUT_FOREMAN, "true but if the gnomes are really going to..", "..take over runescape, they'll need at least that");
                                                                        Functions.___playerTalk(p, HUT_FOREMAN, "take over?");
                                                                        Functions.___npcTalk(p, HUT_FOREMAN, "of course, why else would glough want 30 battleships", "between you and me, i don't think he stands a chance");
                                                                        Functions.___playerTalk(p, HUT_FOREMAN, "no");
                                                                        Functions.___npcTalk(p, HUT_FOREMAN, "i mean, for the kind of battleships glough's ordered..", "..i'll need ton's and ton's of timber", "more than any forest i can think of could supply", "still, if he say's he can supply the wood i'm sure he can", "any way, here's the invoice");
                                                                        Functions.___playerTalk(p, HUT_FOREMAN, "ok, thanks");
                                                                        Functions.___npcTalk(p, HUT_FOREMAN, "i'll need the wood as soon as possible", "if the orders going to be finished in time");
                                                                        Functions.___playerTalk(p, HUT_FOREMAN, "ok i'll tell glough");
                                                                        Functions.___message(p, "the foreman hands you the invoice");
                                                                        Functions.addItem(p, ItemId.INVOICE.id(), 1);
                                                                        p.updateQuestStage(quest, 10);
                                                                    }

                                                            }

                                                    }

                                            } else
                                                if (p.getQuestStage(quest) == 9) {
                                                    Npc HUT_FOREMAN = Functions.getNearestNpc(p, NpcId.SHIPYARD_FOREMAN_HUT.id(), 4);
                                                    Functions.npcYell(p, n, "die imposter");
                                                    HUT_FOREMAN.setChasing(p);
                                                }

                                        } else
                                            if (n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id()) {
                                                if (p.getQuestStage(quest) == 10) {
                                                    p.message("the forman is too busy to talk");
                                                    return null;
                                                }
                                                Functions.npcYell(p, n, "die imposter");
                                                n.setChasing(p);
                                            } else
                                                if (n.getID() == NpcId.FEMI.id()) {
                                                    switch (p.getQuestStage(quest)) {
                                                        case 10 :
                                                            Functions.___playerTalk(p, n, "i can't believe they won't let me in");
                                                            Functions.___npcTalk(p, n, "i don't believe all this rubbish about an invasion", "if mankind wanted to, they could have invaded before now");
                                                            Functions.___playerTalk(p, n, "i really need to see king shareem", "could you help sneak me in");
                                                            Functions.___npcTalk(p, n, "well, as you helped me i suppose i could", "we'll have to be careful", "if i get caught i'll be in the cage");
                                                            Functions.___playerTalk(p, n, "ok, what should i do");
                                                            Functions.___npcTalk(p, n, "jump in the back of the cart", "it's a food delivery, we should be fine");
                                                            Functions.___message(p, "you hide in the cart", "femi covers you with a sheet...", "...and drags the cart to the gate", "femi pulls you into the stronghold");
                                                            p.teleport(708, 510);
                                                            Npc femi = Functions.getNearestNpc(p, NpcId.FEMI_STRONGHOLD.id(), 2);
                                                            Functions.___npcTalk(p, femi, "ok traveller, you'd better get going");
                                                            Functions.___playerTalk(p, femi, "thanks again femi");
                                                            Functions.___npcTalk(p, femi, "that's ok, all the best");
                                                            break;
                                                        default :
                                                            p.message("the little gnome is too busy to talk");
                                                            break;
                                                    }
                                                } else
                                                    if (n.getID() == NpcId.FEMI_STRONGHOLD.id()) {
                                                        p.message("the little gnome is too busy to talk");
                                                    } else
                                                        if (n.getID() == NpcId.ANITA.id()) {
                                                            switch (p.getQuestStage(quest)) {
                                                                case 11 :
                                                                    Functions.___playerTalk(p, n, "hello there");
                                                                    Functions.___npcTalk(p, n, "oh hello, i've seen you with the king");
                                                                    Functions.___playerTalk(p, n, "yes, i'm helping him with a problem");
                                                                    Functions.___npcTalk(p, n, "you must know my boy friend glough then");
                                                                    Functions.___playerTalk(p, n, "indeed!");
                                                                    Functions.___npcTalk(p, n, "could you do me a favour?");
                                                                    Functions.___playerTalk(p, n, "i suppose so");
                                                                    Functions.___npcTalk(p, n, "give this key to glough", "he left it here last night");
                                                                    Functions.___message(p, "anita gives you a key");
                                                                    Functions.addItem(p, ItemId.GLOUGHS_KEY.id(), 1);
                                                                    Functions.___npcTalk(p, n, "thanks a lot");
                                                                    Functions.___playerTalk(p, n, "no, thankyou");
                                                                    break;
                                                                default :
                                                                    p.message("anita is to busy cleaning to talk");
                                                                    break;
                                                            }
                                                        } else
                                                            if (n.getID() == NpcId.KING_NARNODE_SHAREEN_UNDERGROUND.id()) {
                                                                // FINALE COMPLETION
                                                                switch (p.getQuestStage(quest)) {
                                                                    case 15 :
                                                                        Functions.___npcTalk(p, n, "traveller you're wounded, what happened?");
                                                                        Functions.___playerTalk(p, n, "it's glough, he set a demon on me");
                                                                        Functions.___npcTalk(p, n, "what, glough, with a demon?");
                                                                        Functions.___playerTalk(p, n, "glough has a store of daconia rocks further up the passage way", "he's been accessing the roots from a secret passage at his home");
                                                                        Functions.___npcTalk(p, n, "never, not glough, he's a good gnome at heart", "guard, go and check out that passage way");
                                                                        Functions.___message(p, "one of the king's guards runs of up the passage");
                                                                        Functions.___npcTalk(p, n, "look, maybe it's stress playing with your mind");
                                                                        Functions.___message(p, "the gnome guard returns", "and talks to the king");
                                                                        Functions.___npcTalk(p, n, "what?, never, why that little...", "they found glough hiding under a horde of daconia rocks..");
                                                                        Functions.___playerTalk(p, n, "that's what i've been trying to tell you", "glough's been fooling you");
                                                                        Functions.___npcTalk(p, n, "i..i don't know what to say", "how could i have been so blind");
                                                                        Functions.___message(p, "king shareem calls out to another guard");
                                                                        Functions.___npcTalk(p, n, "guard, call off the military training", "the humans are not attacking", "you have my full apologies traveller, and my gratitude", "a reward will have to wait though, the tree is still dying", "the guards are clearing glough's rock supply now", "but there must be more daconia hidden somewhere in the roots", "please traveller help us search, we have little time");
                                                                        p.updateQuestStage(quest, 16);
                                                                        break;
                                                                    case 16 :
                                                                        Functions.___npcTalk(p, n, "traveller, have you managed to find the rock", "i think there's only one");
                                                                        if (Functions.hasItem(p, ItemId.DACONIA_ROCK.id())) {
                                                                            Functions.___playerTalk(p, n, "is this it?");
                                                                            Functions.___npcTalk(p, n, "yes, excellent, well done");
                                                                            Functions.___message(p, "you give king shareem the daconia rock");
                                                                            Functions.removeItem(p, ItemId.DACONIA_ROCK.id(), 1);
                                                                            Functions.___npcTalk(p, n, "it's incredible, the tree's health is improving already", "i don't what to say, we owe you so much", "to think glough had me fooled all along");
                                                                            Functions.___playerTalk(p, n, "all that matters now is that man...", "...and gnome can live together in peace");
                                                                            Functions.___npcTalk(p, n, "i'll drink to that");
                                                                            p.sendQuestComplete(Quests.GRAND_TREE);
                                                                            Functions.___npcTalk(p, n, "from now on i vow to make this stronghold", "a welcome place for all no matter what their creed", "i'll grant you access to all our facilities");
                                                                            Functions.___playerTalk(p, n, "thanks, i think");
                                                                            Functions.___npcTalk(p, n, "it should make your stay here easier", "you can use the spirit tree to transport yourself", "..as well as the gnome glider", "i also give you access to our mine");
                                                                            Functions.___playerTalk(p, n, "mine?");
                                                                            Functions.___npcTalk(p, n, "very few know of the secret mine under the grand tree", "if you push on the roots just to my north", "the grand tree will take you there");
                                                                            Functions.___playerTalk(p, n, "strange");
                                                                            Functions.___npcTalk(p, n, "that's magic trees for you", "all the best traveller and thanks again");
                                                                            Functions.___playerTalk(p, n, "you too narnode");
                                                                        } else {
                                                                            Functions.___playerTalk(p, n, "no sign of it so far");
                                                                            Functions.___npcTalk(p, n, "the tree will still die if we don't find it", "it could be anywhere");
                                                                            Functions.___playerTalk(p, n, "don't worry narnode, we'll find it");
                                                                        }
                                                                        break;
                                                                    case -1 :
                                                                        Functions.___playerTalk(p, n, "hello narnode");
                                                                        Functions.___npcTalk(p, n, "well hello again adventurer", "how are you?");
                                                                        Functions.___playerTalk(p, n, "i'm good thanks, how's the tree?");
                                                                        Functions.___npcTalk(p, n, "better than ever, thanks for asking");
                                                                        break;
                                                                    default :
                                                                        break;
                                                                }
                                                            }










                    return null;
                });
            }
        };
    }

    private void questionMenu2(final Player p, final Npc n) {
        int menu = Functions.___showMenu(p, "you must warn the gnomes", "soon the eternal night will come", "the seven must reunite", "only one the fifth night", "none of the above");
        if (menu == 0) {
            questionMenu3(p, n);
        } else
            if (menu == 1) {
                questionMenu3(p, n);
            } else
                if (menu == 2) {
                    questionMenu3(p, n);
                } else
                    if (menu == 3) {
                        questionMenu3(p, n);
                    } else
                        if (menu == 4) {
                            if ((!p.getCache().hasKey("gt_q2")) && p.getCache().hasKey("gt_q1")) {
                                p.getCache().store("gt_q2", true);
                            }
                            questionMenu3(p, n);
                        }




    }

    private void questionMenu3(final Player p, final Npc n) {
        int menu = Functions.___showMenu(p, "all shall peril", "chicken, it must be chicken", "and then you will know", "the tree will live", "none of the above");
        if (menu == 0) {
            wrongQuestionMenu(p, n);
        } else
            if (menu == 1) {
                wrongQuestionMenu(p, n);
            } else
                if (menu == 2) {
                    wrongQuestionMenu(p, n);
                } else
                    if (menu == 3) {
                        wrongQuestionMenu(p, n);
                    } else
                        if (menu == 4) {
                            if (p.getCache().hasKey("gt_q1") && p.getCache().hasKey("gt_q2")) {
                                // remove to keep efficiency in cache.
                                p.getCache().remove("gt_q1");
                                p.getCache().remove("gt_q2");
                                // Continue the last three menus.
                                int realQuestionMenu = Functions.___showMenu(p, "monster came with king's sword", "giant left with tree stone", "ogre came with king's head", "human came with king's seal", "fairy came with eternal flower");
                                if (realQuestionMenu == 0) {
                                    wrongQuestionMenu(p, n);
                                } else
                                    if (realQuestionMenu == 1) {
                                        wrongQuestionMenu(p, n);
                                    } else
                                        if (realQuestionMenu == 2) {
                                            wrongQuestionMenu(p, n);
                                        } else
                                            if (realQuestionMenu == 3) {
                                                int realQuestionMenu2 = Functions.___showMenu(p, "gave the ever-light to human", "gave human daconia rock", "gave human rock to daconia", "human attacked by daconia", "human destroyed daconia rock");
                                                if (realQuestionMenu2 == 0) {
                                                    wrongQuestionMenu(p, n);
                                                } else
                                                    if (realQuestionMenu2 == 1) {
                                                        int realQuestionMenu3 = Functions.___showMenu(p, "daconia rocks will save tree", "daconia will fall to gnome kingdom", "gnome kingdom will fall to daconia", "daconia rocks will kill tree", "daconia rocks killed human");
                                                        if (realQuestionMenu3 == 0) {
                                                            wrongQuestionMenu(p, n);
                                                        } else
                                                            if (realQuestionMenu3 == 1) {
                                                                wrongQuestionMenu(p, n);
                                                            } else
                                                                if (realQuestionMenu3 == 2) {
                                                                    wrongQuestionMenu(p, n);
                                                                } else
                                                                    if (realQuestionMenu3 == 3) {
                                                                        Functions.___playerTalk(p, n, "he said a human came to him with the king's seal", "hazelmere gave the man daconia rocks", "and daconia rocks will kill the tree");
                                                                        Functions.___npcTalk(p, n, "of course, i should have known", "some one must have forged my royal seal", "and convinced hazelmere that i sent for the daconia stones");
                                                                        Functions.___playerTalk(p, n, "what are daconia stones?");
                                                                        Functions.___npcTalk(p, n, "hazelmere created the daconia stones", "they were a safty measure, in case the tree grew out of control", "they're the only thing that can kill the tree", "this is terrible, those stones must be retrieved");
                                                                        Functions.___playerTalk(p, n, "can i help?");
                                                                        Functions.___npcTalk(p, n, "first i must warn the tree guardians", "please, could you tell the chief tree guardian glough", "he lives in a tree house just in front of the grand tree", "if he's not there he will be at anita's, his girlfriend", "meet me back here once you've told him");
                                                                        Functions.___playerTalk(p, n, "ok, i'll be back soon");
                                                                        p.updateQuestStage(this, 3);
                                                                    } else
                                                                        if (realQuestionMenu3 == 4) {
                                                                            wrongQuestionMenu(p, n);
                                                                        }




                                                    } else
                                                        if (realQuestionMenu2 == 2) {
                                                            wrongQuestionMenu(p, n);
                                                        } else
                                                            if (realQuestionMenu2 == 3) {
                                                                wrongQuestionMenu(p, n);
                                                            } else
                                                                if (realQuestionMenu2 == 4) {
                                                                    wrongQuestionMenu(p, n);
                                                                }




                                            } else
                                                if (realQuestionMenu == 4) {
                                                    wrongQuestionMenu(p, n);
                                                }




                            } else {
                                wrongQuestionMenu(p, n);
                            }
                        }




    }

    private void wrongQuestionMenu(final Player p, final Npc n) {
        Functions.___npcTalk(p, n, "wait a minute, that doesn't sound like hazelmere", "are you sure you translated correctly?");
        Functions.___playerTalk(p, n, "erm...i think so");
        Functions.___npcTalk(p, n, "i'm sorry traveller but this is no good", "the translation must be perfect or the infomation's no use", "please come back when you know exactly what hazelmere said");
        /**
         * Remove the cache if they fail on the third question *
         */
        if (p.getCache().hasKey("gt_q1")) {
            p.getCache().remove("gt_q1");
        }
        if (p.getCache().hasKey("gt_q2")) {
            p.getCache().remove("gt_q2");
        }
    }

    private void shipyardPasswordMenu2(final Player p, final Npc worker) {
        int menu = Functions.___showMenu(p, "lo", "lu", "le");
        if (menu == 0) {
            shipyardPasswordMenu3(p, worker);
        } else
            if (menu == 1) {
                if ((!p.getCache().hasKey("gt_shipyard_q2")) && p.getCache().hasKey("gt_shipyard_q1")) {
                    p.getCache().store("gt_shipyard_q2", true);
                }
                shipyardPasswordMenu3(p, worker);
            } else
                if (menu == 2) {
                    shipyardPasswordMenu3(p, worker);
                }


    }

    private void wrongShipyardPassword(final Player p, final Npc worker) {
        Functions.___npcTalk(p, worker, "you have no idea");
        worker.setChasing(p);
        /**
         * Remove the cache if they fail on the third question *
         */
        if (p.getCache().hasKey("gt_shipyard_q1")) {
            p.getCache().remove("gt_shipyard_q1");
        }
        if (p.getCache().hasKey("gt_shipyard_q2")) {
            p.getCache().remove("gt_shipyard_q2");
        }
    }

    private void shipyardPasswordMenu3(final Player p, final Npc worker) {
        int menu = Functions.___showMenu(p, "mon", "min", "men");
        if (menu == 0) {
            wrongShipyardPassword(p, worker);
        } else
            if (menu == 1) {
                if (p.getCache().hasKey("gt_shipyard_q1") && p.getCache().hasKey("gt_shipyard_q2")) {
                    // remove to keep efficiency in cache.
                    p.getCache().remove("gt_q1");
                    p.getCache().remove("gt_q2");
                    // continue
                    Functions.___playerTalk(p, worker, "ka lu min");
                    Functions.___npcTalk(p, worker, "i'm sorry to have kept you", "but obviously high security is essential");
                    p.teleport(402, 760);
                    p.message("the worker opens the gate");
                    p.message("you walk through");
                    Functions.___npcTalk(p, worker, "you'll need to speak to the foreman", "he's on the pier, it'll give you a chance..", "...to see the fleet");
                }
            } else
                if (menu == 2) {
                    wrongShipyardPassword(p, worker);
                }


    }

    private void strangeTranslationBox(Player p) {
        ActionSender.sendBox(p, "@yel@x@red@z@yel@ql@red@:v@yel@ha @red@za@yel@:v@red@ql@yel@::: @red@h:@yel@xa@red@lat@yel@x @red@vo@yel@xa@red@ha@yel@qa@red@sol @yel@sol@red@:::@yel@:v@red@va% %" + ("@yel@qa@red@:v@yel@::@red@::: @yel@x@red@z@yel@ql@red@:v@yel@ha @red@qe@yel@:v@red@ha @yel@qe@red@:v@yel@za@red@ho@yel@ha@red@xa@yel@:v @red@qi@yel@ho@red@za@yel@vo% %" + "@red@qe@yel@:v@red@za@yel@ho@red@ha@yel@xa@red@:v @yel@qi@red@ho@yel@za@red@vo@yel@sol @red@h:@yel@xa@red@va@yel@va @red@vo@yel@xa@red@va@yel@va @yel@lat@red@qi@yel@:::@red@:::"), true);
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return DataConversions.inArray(new int[]{ GrandTree.GLOUGHS_CUPBOARD_OPEN, GrandTree.GLOUGHS_CUPBOARD_CLOSED, GrandTree.TREE_LADDER_UP, GrandTree.TREE_LADDER_DOWN, GrandTree.SHIPYARD_GATE, GrandTree.STRONGHOLD_GATE, GrandTree.GLOUGH_CHEST_CLOSED, GrandTree.WATCH_TOWER_UP, GrandTree.WATCH_TOWER_DOWN, GrandTree.WATCH_TOWER_STONE_STAND, GrandTree.ROOT_ONE, GrandTree.ROOT_TWO, GrandTree.ROOT_THREE, GrandTree.PUSH_ROOT, GrandTree.PUSH_ROOT_BACK }, obj.getID());
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, final Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == GrandTree.GLOUGHS_CUPBOARD_OPEN) || (obj.getID() == GrandTree.GLOUGHS_CUPBOARD_CLOSED)) {
                        if (command.equalsIgnoreCase("open")) {
                            Functions.openCupboard(obj, p, GrandTree.GLOUGHS_CUPBOARD_OPEN);
                        } else
                            if (command.equalsIgnoreCase("close")) {
                                Functions.closeCupboard(obj, p, GrandTree.GLOUGHS_CUPBOARD_CLOSED);
                            } else {
                                Functions.___message(p, "you search the cupboard");
                                if (p.getQuestStage(quest) == 6) {
                                    Functions.___message(p, "inside you find glough's journal");
                                    Functions.addItem(p, ItemId.GLOUGHS_JOURNAL.id(), 1);
                                    p.updateQuestStage(quest, 7);
                                } else {
                                    Functions.___message(p, "but find nothing of interest");
                                }
                            }

                    } else
                        if (obj.getID() == GrandTree.TREE_LADDER_UP) {
                            p.message("you climb up the ladder");
                            p.teleport(417, 2994, false);
                        } else
                            if (obj.getID() == GrandTree.TREE_LADDER_DOWN) {
                                p.message("you climb down the ladder");
                                p.teleport(415, 2051, false);
                            } else
                                if (obj.getID() == GrandTree.SHIPYARD_GATE) {
                                    if (p.getY() >= 762) {
                                        if ((p.getQuestStage(quest) >= 8) && (p.getQuestStage(quest) <= 9)) {
                                            Functions.___message(p, "the gate is locked");
                                            final Npc worker = Functions.getNearestNpc(p, NpcId.SHIPYARD_WORKER_ENTRANCE.id(), 5);
                                            // Continue
                                            if (worker != null) {
                                                Functions.___npcTalk(p, worker, "hey you, what are you up to?");
                                                Functions.___playerTalk(p, worker, "i'm trying to open the gate");
                                                Functions.___npcTalk(p, worker, "i can see that, but why?");
                                                Menu defaultMenu = new Menu();
                                                defaultMenu.addOption(new Option("i've come to check that you're working safley") {
                                                    @Override
                                                    public void action() {
                                                        Functions.___npcTalk(p, worker, "what business is that of yours?");
                                                        Functions.___playerTalk(p, worker, "as a runescape resident i have a right to know");
                                                        Functions.___npcTalk(p, worker, "get out of here before you get a beating");
                                                        Functions.___playerTalk(p, worker, "that's not very friendly");
                                                        Functions.___npcTalk(p, worker, "right, i'll show you friendly");
                                                        worker.setChasing(p);
                                                    }
                                                });
                                                if (p.getQuestStage(quest) == 8) {
                                                    defaultMenu.addOption(new Option("glough sent me") {
                                                        @Override
                                                        public void action() {
                                                            Functions.___npcTalk(p, worker, "hmm, really, what for?");
                                                            Functions.___playerTalk(p, worker, "your wasting my time, take me to your superior");
                                                            Functions.___npcTalk(p, worker, "ok, i can let you in but i need the password");
                                                            int menu = Functions.___showMenu(p, "Ka", "ko", "ke");
                                                            if (menu == 0) {
                                                                if (!p.getCache().hasKey("gt_shipyard_q1")) {
                                                                    p.getCache().store("gt_shipyard_q1", true);
                                                                }
                                                                shipyardPasswordMenu2(p, worker);
                                                            } else
                                                                if (menu == 1) {
                                                                    shipyardPasswordMenu2(p, worker);
                                                                } else
                                                                    if (menu == 2) {
                                                                        shipyardPasswordMenu2(p, worker);
                                                                    }


                                                        }
                                                    });
                                                }
                                                defaultMenu.addOption(new Option("i just fancied looking around") {
                                                    @Override
                                                    public void action() {
                                                        Functions.___npcTalk(p, worker, "this isn't a museum", "leave now");
                                                        Functions.___playerTalk(p, worker, "i'll leave when i choose");
                                                        Functions.___npcTalk(p, worker, "we'll see");
                                                        worker.setChasing(p);
                                                    }
                                                });
                                                defaultMenu.showMenu(p);
                                            } else {
                                                Functions.___message(p, "the gate is locked");
                                            }
                                        } else {
                                            Functions.___message(p, "the gate is locked");
                                        }
                                    } else {
                                        p.message("you open the gate");
                                        p.message("and walk through");
                                        Functions.___doGate(p, obj, 623);
                                        p.teleport(401, 763);
                                    }
                                } else
                                    if (obj.getID() == GrandTree.STRONGHOLD_GATE) {
                                        if ((p.getY() <= 531) || (p.getQuestStage(quest) != 0)) {
                                            Functions.___doGate(p, obj, 181);
                                        } else {
                                            if (p.getCache().hasKey("helped_femi")) {
                                                Functions.___doGate(p, obj, 181);
                                            } else {
                                                Npc femi = Functions.getNearestNpc(p, NpcId.FEMI.id(), 10);
                                                if (femi != null) {
                                                    Functions.___npcTalk(p, femi, "hello there");
                                                    Functions.___playerTalk(p, femi, "hi");
                                                    Functions.___npcTalk(p, femi, "could you help me lift this barrel", "it's really heavy");
                                                    int menu = Functions.___showMenu(p, femi, "sorry i'm a bit busy", "ok then");
                                                    if (menu == 0) {
                                                        Functions.___npcTalk(p, femi, "oh, ok, i'll do it myself");
                                                        p.getCache().store("helped_femi", false);
                                                    } else
                                                        if (menu == 1) {
                                                            Functions.___npcTalk(p, femi, "thanks traveller");
                                                            Functions.___message(p, "you help the gnome lift the barrel", "it's very heavy and quite hard work");
                                                            Functions.___npcTalk(p, femi, "thanks again friend");
                                                            p.getCache().store("helped_femi", true);
                                                        }

                                                } else {
                                                    p.message("the little gnome is busy at the moment");
                                                }
                                            }
                                        }
                                    } else
                                        if (obj.getID() == GrandTree.GLOUGH_CHEST_CLOSED) {
                                            Functions.___message(p, "the chest is locked...", "...you need a key");
                                        } else
                                            if (obj.getID() == GrandTree.WATCH_TOWER_UP) {
                                                if (Functions.getCurrentLevel(p, Skills.AGILITY) >= 25) {
                                                    p.message("you jump up and grab hold of the platform");
                                                    p.teleport(710, 2364);
                                                    p.incExp(Skills.AGILITY, 30, true);
                                                    Functions.sleep(3000);
                                                    p.message("and pull yourself up");
                                                } else {
                                                    p.message("You need an agility level of 25 to climb up the platform");
                                                }
                                            } else
                                                if (obj.getID() == GrandTree.WATCH_TOWER_DOWN) {
                                                    Functions.___message(p, "you climb down the tower");
                                                    p.teleport(712, 1420);
                                                    Functions.___message(p, "and drop to the platform below");
                                                } else// 711  3306 me.
                                                // 709 3306 glough.

                                                    if (obj.getID() == GrandTree.WATCH_TOWER_STONE_STAND) {
                                                        if (((p.getQuestStage(quest) == 15) || (p.getQuestStage(quest) == 16)) || (p.getQuestStage(quest) == (-1))) {
                                                            Functions.___message(p, "you squeeze down the inner of the tree trunk", "you drop out of the bottom onto a mud floor");
                                                            p.teleport(711, 3306);
                                                            return null;
                                                        }
                                                        Functions.___message(p, "you push down on the pillar");
                                                        p.message("you feel it shift downwards slightly");
                                                        if ((((p.getCache().hasKey("pebble_1") && p.getCache().hasKey("pebble_2")) && p.getCache().hasKey("pebble_3")) && p.getCache().hasKey("pebble_4")) || (p.getQuestStage(quest) == 14)) {
                                                            if (p.getCache().hasKey("pebble_1")) {
                                                                p.getCache().remove("pebble_1");
                                                            }
                                                            if (p.getCache().hasKey("pebble_2")) {
                                                                p.getCache().remove("pebble_2");
                                                            }
                                                            if (p.getCache().hasKey("pebble_3")) {
                                                                p.getCache().remove("pebble_3");
                                                            }
                                                            if (p.getCache().hasKey("pebble_4")) {
                                                                p.getCache().remove("pebble_4");
                                                            }
                                                            if (p.getQuestStage(quest) == 13) {
                                                                p.updateQuestStage(quest, 14);
                                                            }
                                                            p.message("the pillar shifts back revealing a ladder");
                                                            Functions.___message(p, "it seems to lead down through the tree trunk");
                                                            int menu = Functions.___showMenu(p, "climb down", "come back later");
                                                            if (menu == 0) {
                                                                Functions.___message(p, "you squeeze down the inner of the tree trunk", "you drop out of the bottom onto a mud floor");
                                                                p.teleport(711, 3306);
                                                                Functions.___message(p, "around you, you can see piles of strange looking rocks", "you here the sound of small footsteps coming from the darkness");
                                                                // glough despawns in 1 minute
                                                                Npc n = Functions.spawnNpc(p.getWorld(), NpcId.GLOUGH_UNDERGROUND.id(), 709, 3306, 60000);
                                                                Functions.___npcTalk(p, n, "you really are becoming a headache", "well, at least now you can die knowing you were right", "it will save me having to hunt you down", "like all the over human filth of runescape");
                                                                Functions.___playerTalk(p, n, "you're crazy glough");
                                                                Functions.___npcTalk(p, n, "i'm angry, you think you're so special", "well, soon you'll see, the gnome's are ready to fight", "in three weeks this tree will be dead wood", "in ten weeks it will be 30 battleships", "ready to finally rid the world of the disease called humanity");
                                                                Functions.___playerTalk(p, n, "what makes you think i'll let you get away with it?");
                                                                Functions.___npcTalk(p, n, "ha, do you think i would challange you humans alone", "fool.....meet my little friend");
                                                                Functions.___message(p, "from the darkness you hear a deep growl", "and the sound of heavy footsteps");
                                                                Npc demon = Functions.spawnNpc(p.getWorld(), NpcId.BLACK_DEMON_GRANDTREE.id(), 707, 3306, 60000 * 10);
                                                                if (demon != null) {
                                                                    Functions.npcYell(p, demon, "grrrrr");
                                                                    demon.setChasing(p);
                                                                }
                                                            } else
                                                                if (menu == 1) {
                                                                    p.message("you decide to come back later");
                                                                }

                                                        } else {
                                                            p.message("you here some noise below the pillar...");
                                                            p.message("...but nothing seems to happen");
                                                        }
                                                    } else
                                                        if (((obj.getID() == GrandTree.ROOT_ONE) || (obj.getID() == GrandTree.ROOT_TWO)) || (obj.getID() == GrandTree.ROOT_THREE)) {
                                                            Functions.___message(p, "you search the root...");
                                                            if (obj.getID() == GrandTree.ROOT_THREE) {
                                                                if (p.getQuestStage(quest) == 16) {
                                                                    if (!Functions.hasItem(p, ItemId.DACONIA_ROCK.id())) {
                                                                        Functions.___message(p, "and find a small glowing rock");
                                                                        Functions.addItem(p, ItemId.DACONIA_ROCK.id(), 1);
                                                                    } else {
                                                                        Functions.___message(p, "but find nothing");
                                                                    }
                                                                } else {
                                                                    Functions.___message(p, "...but find nothing");
                                                                }
                                                            } else {
                                                                Functions.___message(p, "...but find nothing");
                                                            }
                                                        } else
                                                            if ((obj.getID() == GrandTree.PUSH_ROOT) || (obj.getID() == GrandTree.PUSH_ROOT_BACK)) {
                                                                // ACCESS TO GNOME MINE
                                                                Functions.___message(p, "you push the roots");
                                                                if (p.getQuestStage(quest) == (-1)) {
                                                                    Functions.___message(p, "they wrap around your arms");
                                                                    p.message("and drag you deeper forwards");
                                                                    if (obj.getID() == GrandTree.PUSH_ROOT_BACK) {
                                                                        p.teleport(700, 3280);
                                                                    } else {
                                                                        p.teleport(701, 3278);
                                                                    }
                                                                } else {
                                                                    Functions.___message(p, "they don't seem to mind");
                                                                }
                                                            }










                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        return (n.getID() == NpcId.CHARLIE.id()) || ((n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id()) && (p.getQuestStage(this) == 10));
    }

    @Override
    public GameStateEvent onPlayerAttackNpc(Player p, Npc affectedmob) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (affectedmob.getID() == NpcId.CHARLIE.id()) {
                        p.message("you can't attack through the bars");
                    } else
                        if ((affectedmob.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id()) && (p.getQuestStage(quest) == 10)) {
                            p.message("the forman is too busy to talk");
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return (n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id()) || (n.getID() == NpcId.BLACK_DEMON_GRANDTREE.id());
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id()) {
                        if (p.getQuestStage(quest) == 9) {
                            n.killedBy(p);
                            Functions.___message(p, "you kill the foreman", "inside his pocket you find an invoice..", "it seems to be an order for timber");
                            Functions.addItem(p, ItemId.INVOICE.id(), 1);
                            p.updateQuestStage(quest, 10);
                        }
                    } else
                        if (n.getID() == NpcId.BLACK_DEMON_GRANDTREE.id()) {
                            if (p.getQuestStage(quest) == 14) {
                                n.killedBy(p);
                                Functions.___message(p, "the beast slumps to the floor", "glough has fled");
                                p.updateQuestStage(quest, 15);
                                Npc fleeGlough = Functions.getNearestNpc(p, NpcId.GLOUGH_UNDERGROUND.id(), 15);
                                if (fleeGlough != null) {
                                    fleeGlough.remove();
                                }
                            }
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
        return ((obj.getID() == GrandTree.GLOUGH_CHEST_CLOSED) && (item.getID() == ItemId.GLOUGHS_KEY.id())) || ((obj.getID() == GrandTree.WATCH_TOWER_STONE_STAND) && ((((item.getID() == ItemId.PEBBLE_3.id()) || (item.getID() == ItemId.PEBBLE_2.id())) || (item.getID() == ItemId.PEBBLE_4.id())) || (item.getID() == ItemId.PEBBLE_1.id())));
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player player) {
        final QuestInterface quest = this;
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == GrandTree.GLOUGH_CHEST_CLOSED) && (item.getID() == ItemId.GLOUGHS_KEY.id())) {
                        Functions.___message(player, "the key fits the chest");
                        player.message("you open the chest");
                        player.message("and search it...");
                        Functions.replaceObjectDelayed(obj, 3000, GrandTree.GLOUGH_CHEST_OPEN);
                        Functions.___message(player, "inside you find some paper work");
                        player.message("and an old gnome tongue translation book");
                        Functions.addItem(player, ItemId.GLOUGHS_NOTES.id(), 1);
                        Functions.addItem(player, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
                        if (player.getQuestStage(quest) == 11) {
                            player.updateQuestStage(quest, 12);
                        }
                        Functions.___message(player, "you close the chest");
                    } else
                        if ((obj.getID() == GrandTree.WATCH_TOWER_STONE_STAND) && ((((item.getID() == ItemId.PEBBLE_3.id()) || (item.getID() == ItemId.PEBBLE_2.id())) || (item.getID() == ItemId.PEBBLE_4.id())) || (item.getID() == ItemId.PEBBLE_1.id()))) {
                            Functions.___message(player, "on top are four pebble size indents", "they span from left to right", "you place the pebble...");
                            int menu = Functions.___showMenu(player, "To the far left", "Centre left", "Centre right", "To the far right");
                            if (menu == 0) {
                                Functions.___message(player, "you place the pebble in the indent", "it crumbles into dust");
                                Functions.removeItem(player, item.getID(), 1);
                                if (item.getID() == ItemId.PEBBLE_1.id()) {
                                    // HO
                                    if (!player.getCache().hasKey("pebble_1")) {
                                        player.getCache().store("pebble_1", true);
                                    }
                                }
                            } else
                                if (menu == 1) {
                                    Functions.___message(player, "you place the pebble in the indent", "it crumbles into dust");
                                    Functions.removeItem(player, item.getID(), 1);
                                    if (item.getID() == ItemId.PEBBLE_2.id()) {
                                        // NI
                                        if (!player.getCache().hasKey("pebble_2")) {
                                            player.getCache().store("pebble_2", true);
                                        }
                                    }
                                } else
                                    if (menu == 2) {
                                        Functions.___message(player, "you place the pebble in the indent", "it crumbles into dust");
                                        Functions.removeItem(player, item.getID(), 1);
                                        if (item.getID() == ItemId.PEBBLE_3.id()) {
                                            // :::
                                            if (!player.getCache().hasKey("pebble_3")) {
                                                player.getCache().store("pebble_3", true);
                                            }
                                        }
                                    } else
                                        if (menu == 3) {
                                            Functions.___message(player, "you place the pebble in the indent", "it crumbles into dust");
                                            Functions.removeItem(player, item.getID(), 1);
                                            if (item.getID() == ItemId.PEBBLE_4.id()) {
                                                // HA
                                                if (!player.getCache().hasKey("pebble_4")) {
                                                    player.getCache().store("pebble_4", true);
                                                }
                                            }
                                        }



                        }

                    return null;
                });
            }
        };
    }
}

