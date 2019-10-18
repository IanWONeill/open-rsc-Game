package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class Waterfall_Quest implements QuestInterface , InvActionListener , InvUseOnObjectListener , InvUseOnWallObjectListener , ObjectActionListener , TalkToNpcListener , WallObjectActionListener , InvActionExecutiveListener , InvUseOnObjectExecutiveListener , InvUseOnWallObjectExecutiveListener , ObjectActionExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    private static final int BAXTORIAN_CUPBOARD_OPEN = 507;

    private static final int BAXTORIAN_CUPBOARD_CLOSED = 506;

    @Override
    public int getQuestId() {
        return Quests.WATERFALL_QUEST;
    }

    @Override
    public String getQuestName() {
        return "Waterfall Quest (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        p.message("@gre@You haved gained 1 quest point!");
        p.message("you have completed the Baxtorian waterfall quest");
        for (int i = 473; i < 478; i++) {
            for (int y = 32; i < 34; i++) {
                if (p.getCache().hasKey((("waterfall_" + i) + "_") + y)) {
                    p.getCache().remove((("waterfall_" + i) + "_") + y);
                }
            }
        }
        Functions.addItem(p, ItemId.MITHRIL_SEED.id(), 40);
        Functions.addItem(p, ItemId.GOLD_BAR.id(), 2);
        Functions.addItem(p, ItemId.DIAMOND.id(), 2);
        int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.WATERFALL_QUEST);
        // keep order kosher
        int[] skillIDs = new int[]{ Skills.STRENGTH, Skills.ATTACK };
        for (int i = 0; i < skillIDs.length; i++) {
            questData[Quests.MAPIDX_SKILL] = skillIDs[i];
            Functions.incQuestReward(p, questData, i == (skillIDs.length - 1));
        }
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.ALMERA.id()) {
                        switch (p.getQuestStage(quest)) {
                            case 0 :
                                Functions.___playerTalk(p, n, "hello madam");
                                Functions.___npcTalk(p, n, "ah, hello there", "nice to see an outsider for a change", "are you busy young man?, i have a problem");
                                int option = Functions.___showMenu(p, n, "i'm afraid i'm in a rush", "how can i help?");
                                if (option == 0) {
                                    Functions.___npcTalk(p, n, "oh okay, never mind");
                                } else
                                    if (option == 1) {
                                        Functions.___npcTalk(p, n, "it's my son hudon, he's always getting into trouble", "the boy's convinced there's hidden treasure in the river", "and i'm a bit worried about his safety", "the poor lad can't even swim");
                                        Functions.___playerTalk(p, n, "i could go and take a look for you if you like");
                                        Functions.___npcTalk(p, n, "would you kind sir?", "you can use the small raft out back if you wish", "do be careful, the current down stream is very strong");
                                        p.updateQuestStage(quest, 1);
                                    }

                                break;
                            case 1 :
                                Functions.___playerTalk(p, n, "hello almera");
                                Functions.___npcTalk(p, n, "hello brave adventurer", "have you seen my boy yet?");
                                Functions.___playerTalk(p, n, "i'm afraid not, but i'm sure he hasn't gone far");
                                Functions.___npcTalk(p, n, "i do hope so", "you can't be too careful these days");
                                break;
                            case 2 :
                                Functions.___npcTalk(p, n, "well hello, you're still around then");
                                Functions.___playerTalk(p, n, "i saw hudon by the river but he refused to come back with me");
                                Functions.___npcTalk(p, n, "yes he told me", "the foolish lad came in drenched to the bone", "he had fallen into the waterfall, lucky he wasn't killed", "now he can spend the rest of the summer in his room");
                                Functions.___playerTalk(p, n, "any ideas on what i could do while i'm here?");
                                Functions.___npcTalk(p, n, "why don't you visit the tourist centre south of the waterfall?");
                                break;
                            case 3 :
                                Functions.___playerTalk(p, n, "hello again almera");
                                Functions.___npcTalk(p, n, "well hello again brave adventurer", "are you enjoying the tranquil scenery of these parts?");
                                Functions.___playerTalk(p, n, "yes, very relaxing");
                                Functions.___npcTalk(p, n, "well i'm glad to hear it", "the authorities wanted to dig up this whole area for a mine", "but the few locals who lived here wouldn't budge and they gave up");
                                Functions.___playerTalk(p, n, "good for you");
                                Functions.___npcTalk(p, n, "good for all of us");
                                break;
                            case 4 :
                            case -1 :
                                Functions.___playerTalk(p, n, "hello almera");
                                Functions.___npcTalk(p, n, "hello adventurer", "how's your treasure hunt going?");
                                Functions.___playerTalk(p, n, "oh, i'm just sight seeing");
                                Functions.___npcTalk(p, n, "no adventurer stays here this long just to sight see", "but your business is yours alone", "if you need to use the raft go ahead", "but please try not crash it this time");
                                Functions.___playerTalk(p, n, "thanks almera");
                                break;
                        }
                    } else
                        if (n.getID() == NpcId.HUDON.id()) {
                            switch (p.getQuestStage(quest)) {
                                case 0 :
                                    Functions.___playerTalk(p, n, "hello there");
                                    Functions.___npcTalk(p, n, "what do you want?");
                                    Functions.___playerTalk(p, n, "nothing, just passing by");
                                    break;
                                case 1 :
                                    Functions.___playerTalk(p, n, "Hello hudon", "hello son, are you alright?");
                                    Functions.___npcTalk(p, n, "don't play nice with me", "i know your looking for the treasure to");
                                    Functions.___playerTalk(p, n, "your mother sent me to find you hudon");
                                    Functions.___npcTalk(p, n, "i'll go home when i've found the treasure", "i'm going to be a rich rich man");
                                    Functions.___playerTalk(p, n, "where is this treasure you talk of?");
                                    Functions.___npcTalk(p, n, "just because i'm small doesn't mean i'm dumb", "if i told you, then you'd take it all for yourself");
                                    Functions.___playerTalk(p, n, "maybe i could help?");
                                    Functions.___npcTalk(p, n, "if you want to help go and tell my mother that i won't be back for a while");
                                    Functions.___message(p, "hudon is refusing to leave the waterfall");
                                    Functions.___playerTalk(p, n, "ok i'll leave you to it");
                                    p.updateQuestStage(quest, 2);
                                    break;
                                case 2 :
                                    Functions.___playerTalk(p, n, "so your still here");
                                    Functions.___npcTalk(p, n, "i'll find that treasure soon", "just you wait and see");
                                    break;
                                case 3 :
                                    Functions.___playerTalk(p, n, "hello hudon");
                                    Functions.___npcTalk(p, n, "oh it's you", "trying to find my treasure again are you?");
                                    Functions.___playerTalk(p, n, "i didn't know it belonged to you");
                                    Functions.___npcTalk(p, n, "it will do when i find it", "i just need to get into this blasted waterfall", "i've been washed downstream three times already");
                                    break;
                                case 4 :
                                    Functions.___playerTalk(p, n, "hello again");
                                    Functions.___npcTalk(p, n, "not you still, why don't you give up?");
                                    Functions.___playerTalk(p, n, "and miss all the fun!");
                                    Functions.___npcTalk(p, n, "you do understand that anything you find you have to share it with me");
                                    Functions.___playerTalk(p, n, "why's that?");
                                    Functions.___npcTalk(p, n, "because i told you about the treasure");
                                    Functions.___playerTalk(p, n, "well, i wouldn't count on it");
                                    Functions.___npcTalk(p, n, "that's not fair");
                                    Functions.___playerTalk(p, n, "neither is life kid");
                                    break;
                                case -1 :
                                    Functions.___playerTalk(p, n, "hello again");
                                    Functions.___npcTalk(p, n, "you stole my treasure i saw you");
                                    Functions.___playerTalk(p, n, "i'll make sure it goes to a good cause");
                                    Functions.___npcTalk(p, n, "hmmmm");
                                    break;
                            }
                        } else
                            if (n.getID() == NpcId.GERALD.id()) {
                                if (p.getQuestStage(quest) == 0) {
                                    Functions.___playerTalk(p, n, "hello there");
                                    Functions.___npcTalk(p, n, "good day to you traveller", "are you here to fish or just looking around?", "i've caught some beauties down here");
                                    Functions.___playerTalk(p, n, "really");
                                    Functions.___npcTalk(p, n, "the last one was this big");
                                    Functions.___message(p, "gerald stretches his arms out to full width");
                                } else {
                                    Functions.___playerTalk(p, n, "hello");
                                    Functions.___npcTalk(p, n, "hello traveller", "are you here to fish or to hunt for treasure?");
                                    Functions.___playerTalk(p, n, "why do you say that?");
                                    Functions.___npcTalk(p, n, "adventurers pass through here every week", "they never find anything though");
                                }
                            } else
                                if (n.getID() == NpcId.HADLEY.id()) {
                                    if ((p.getQuestStage(quest) == 0) || (p.getQuestStage(quest) == 1)) {
                                        hadleyAltDialogue(p, n, Waterfall_Quest.HADLEY.ALL);
                                    } else {
                                        hadleyMainDialogue(p, n, Waterfall_Quest.HADLEY.ALL);
                                    }
                                } else
                                    if (n.getID() == NpcId.GOLRIE.id()) {
                                        if (!Functions.hasItem(p, ItemId.GLARIALS_PEBBLE.id(), 1)) {
                                            Functions.___playerTalk(p, n, "is your name golrie?");
                                            Functions.___npcTalk(p, n, "that's me", "i've been stuck in here for weeks", "those goblins are trying to steal my families heirlooms", "my grandad gave me all sorts of old junk");
                                            Functions.___playerTalk(p, n, "do you mind if i have a look?");
                                            Functions.___npcTalk(p, n, "no, of course not");
                                            Functions.___message(p, "mixed with the junk on the floor", "you find glarials pebble");
                                            Functions.addItem(p, ItemId.GLARIALS_PEBBLE.id(), 1);
                                            Functions.___playerTalk(p, n, "could i take this old pebble?");
                                            Functions.___npcTalk(p, n, "oh that, yes have it", "it's just some old elven junk i believe");
                                            Functions.removeItem(p, ItemId.LARGE_KEY.id(), 1);
                                            Functions.___message(p, "you give golrie the key");
                                            Functions.___npcTalk(p, n, "well thanks again for the key", "i think i'll wait in here until those goblins get bored and leave");
                                            Functions.___playerTalk(p, n, "okay, take care golrie");
                                            if (!p.getCache().hasKey("golrie_key")) {
                                                p.getCache().store("golrie_key", true);
                                            }
                                        } else {
                                            Functions.___playerTalk(p, n, "is your name golrie?");
                                            Functions.___npcTalk(p, n, "that's me", "i've been stuck in here for weeks", "those goblins are trying to steal my families heirlooms", "my grandad gave me all sorts of old junk");
                                            Functions.___playerTalk(p, n, "do you mind if i have a look?");
                                            Functions.___npcTalk(p, n, "no, of course not");
                                            Functions.removeItem(p, ItemId.LARGE_KEY.id(), 1);
                                            Functions.___message(p, "you find nothing of interest", "you give golrie the key");
                                            Functions.___npcTalk(p, n, "thanks a lot for the key traveller", "i think i'll wait in here until those goblins get bored and leave");
                                            Functions.___playerTalk(p, n, "okay, take care golrie");
                                            if (!p.getCache().hasKey("golrie_key")) {
                                                p.getCache().store("golrie_key", true);
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
        return DataConversions.inArray(new int[]{ NpcId.ALMERA.id(), NpcId.HUDON.id(), NpcId.HADLEY.id(), NpcId.GERALD.id(), NpcId.GOLRIE.id() }, n.getID());
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return DataConversions.inArray(new int[]{ 492, 486, 467, 469, Waterfall_Quest.BAXTORIAN_CUPBOARD_OPEN, Waterfall_Quest.BAXTORIAN_CUPBOARD_CLOSED, 481, 471, 479, 470, 480, 463, 462, 482, 464 }, obj.getID());
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 464) {
                        Functions.___message(p, "you board the small raft", "and push off down stream", "the raft is pulled down stream by strong currents", "you crash into a small land mound");
                        p.teleport(662, 463, false);
                        Npc hudon = p.getWorld().getNpc(NpcId.HUDON.id(), 0, 2000, 0, 2000);
                        if ((hudon != null) && (p.getQuestStage(quest) == 1)) {
                            Functions.___playerTalk(p, hudon, "hello son, are you okay?");
                            Functions.___npcTalk(p, hudon, "it looks like you need the help");
                            Functions.___playerTalk(p, hudon, "your mum sent me to find you");
                            Functions.___npcTalk(p, hudon, "don't play nice with me");
                            Functions.___npcTalk(p, hudon, "i know your looking for the treasure");
                            Functions.___playerTalk(p, hudon, "where is this treasure you talk of?");
                            Functions.___npcTalk(p, hudon, "just because i'm small doesn't mean i'm dumb");
                            Functions.___npcTalk(p, hudon, "if i told you, you would take it all for yourself");
                            Functions.___playerTalk(p, hudon, "maybe i could help");
                            Functions.___npcTalk(p, hudon, "i'm fine alone");
                            p.updateQuestStage(quest, 2);
                            Functions.___message(p, "hudon is refusing to leave the waterfall");
                        }
                    } else
                        if (((obj.getID() == 463) || (obj.getID() == 462)) || (obj.getID() == 482)) {
                            if (command.equals("jump to next")) {
                                Functions.___message(p, "the tree is too far off to jump to", "you need someway to pull yourself across");
                            } else
                                if (command.equals("jump off")) {
                                    Functions.___message(p, "you jump into the wild rapids");
                                    p.teleport(654, 485, false);
                                    p.damage(DataConversions.random(4, 10));
                                    Functions.___playerTalk(p, null, "ouch!");
                                    Functions.___message(p, "you tumble over the water fall", "and are washed up by the river side");
                                }

                        } else
                            if (obj.getID() == 469) {
                                Functions.___message(p, "you jump into the wild rapids below");
                                p.teleport(654, 485, false);
                                p.damage(DataConversions.random(4, 10));
                                Functions.___playerTalk(p, null, "ouch!");
                                Functions.___message(p, "you tumble over the water fall", "and are washed up by the river side");
                            } else
                                if (obj.getID() == 470) {
                                    Functions.___message(p, "you search the bookcase");
                                    if (!p.getInventory().hasItemId(ItemId.BOOK_ON_BAXTORIAN.id())) {
                                        Functions.___message(p, "and find a book named 'book on baxtorian'");
                                        Functions.addItem(p, ItemId.BOOK_ON_BAXTORIAN.id(), 1);
                                    } else
                                        Functions.___message(p, "but find nothing of interest");

                                } else
                                    if (obj.getID() == 481) {
                                        Functions.___message(p, "you search the crate");
                                        if (!p.getInventory().hasItemId(ItemId.LARGE_KEY.id())) {
                                            Functions.___message(p, "and find a large key");
                                            Functions.addItem(p, ItemId.LARGE_KEY.id(), 1);
                                        } else {
                                            p.message("but find nothing");
                                        }
                                    } else
                                        if (obj.getID() == 480) {
                                            Npc n = p.getWorld().getNpc(NpcId.GOLRIE.id(), 663, 668, 3520, 3529);
                                            if (p.getQuestStage(quest) == 0) {
                                                Functions.___npcTalk(p, n, "what are you doing down here", "leave before you get yourself into trouble");
                                                return null;
                                            } else
                                                if (p.getLocation().getY() <= 3529) {
                                                    Functions.___doGate(p, obj);
                                                    return null;
                                                } else
                                                    if (((p.getLocation().getY() >= 3530) && p.getCache().hasKey("golrie_key")) || (p.getQuestStage(quest) == (-1))) {
                                                        p.message("golrie has locked himself in");
                                                        return null;
                                                    }


                                            if (p.getLocation().getY() >= 3530) {
                                                if (n != null) {
                                                    Functions.___playerTalk(p, n, "are you ok?");
                                                    Functions.___npcTalk(p, n, "it's just those blasted hobgoblins", "i locked myself in here for protection", "but i've left the key somewhere", "and now i'm stuck");
                                                    if (!p.getInventory().hasItemId(ItemId.LARGE_KEY.id())) {
                                                        Functions.___playerTalk(p, n, "okay, i'll have a look for a key");
                                                    } else {
                                                        Functions.___playerTalk(p, n, "i found a key");
                                                        Functions.___npcTalk(p, n, "well don't wait all day", "give it a try");
                                                    }
                                                    return null;
                                                }
                                            }
                                        } else
                                            if (obj.getID() == 479) {
                                                Functions.___message(p, "the grave is covered in elven script", "some of the writing is in common tongue, it reads", "here lies glarial, wife of baxtorian", "true friend of nature in life and death", "may she now rest knowing", "only visitors with peaceful intent can enter");
                                            } else
                                                if ((obj.getID() == Waterfall_Quest.BAXTORIAN_CUPBOARD_OPEN) || (obj.getID() == Waterfall_Quest.BAXTORIAN_CUPBOARD_CLOSED)) {
                                                    if (command.equalsIgnoreCase("open")) {
                                                        Functions.openGenericObject(obj, p, Waterfall_Quest.BAXTORIAN_CUPBOARD_OPEN, "you open the cupboard");
                                                    } else
                                                        if (command.equalsIgnoreCase("close")) {
                                                            Functions.closeGenericObject(obj, p, Waterfall_Quest.BAXTORIAN_CUPBOARD_CLOSED, "you shut the cupboard");
                                                        } else {
                                                            Functions.___message(p, "you search the cupboard");
                                                            if (!Functions.hasItem(p, ItemId.GLARIALS_URN.id(), 1)) {
                                                                p.message("and find a metel urn");
                                                                Functions.addItem(p, ItemId.GLARIALS_URN.id(), 1);
                                                            } else {
                                                                p.message("it's empty");
                                                            }
                                                        }

                                                } else
                                                    if (obj.getID() == 467) {
                                                        Functions.___message(p, "you search the coffin");
                                                        if (!Functions.hasItem(p, ItemId.GLARIALS_AMULET.id())) {
                                                            Functions.___message(p, "inside you find a small amulet", "you take the amulet and close the coffin");
                                                            Functions.addItem(p, ItemId.GLARIALS_AMULET.id(), 1);
                                                        } else {
                                                            Functions.___message(p, "it's empty");
                                                        }
                                                    } else
                                                        if (obj.getID() == 471) {
                                                            Functions.___message(p, "the doors begin to open");
                                                            if (p.getInventory().wielding(ItemId.GLARIALS_AMULET.id())) {
                                                                Functions.___doGate(p, obj, 63);
                                                                Functions.___message(p, "You go through the door");
                                                            } else {
                                                                Functions.___message(p, "suddenly the corridor floods", "flushing you back into the river");
                                                                p.teleport(654, 485, false);
                                                                p.damage(DataConversions.random(4, 10));
                                                                Functions.___playerTalk(p, null, "ouch!");
                                                                Functions.___message(p, "you tumble over the water fall");
                                                            }
                                                        } else
                                                            if (obj.getID() == 492) {
                                                                Functions.___message(p, "you search the crate");
                                                                if (!p.getInventory().hasItemId(ItemId.AN_OLD_KEY.id())) {
                                                                    Functions.___message(p, "you find an old key");
                                                                    Functions.addItem(p, ItemId.AN_OLD_KEY.id(), 1);
                                                                } else {
                                                                    p.message("it is empty");
                                                                }
                                                            } else
                                                                if (obj.getID() == 135) {
                                                                    p.message("the door is locked");
                                                                } else
                                                                    if (obj.getID() == 485) {
                                                                        Functions.___message(p, "as you touch the chalice it tips over", "it falls to the floor", "you hear a gushing of water", "water floods into the cavern");
                                                                        p.damage(DataConversions.random(1, 10));
                                                                        p.teleport(654, 485, false);
                                                                        Functions.___message(p, "ouch!", "you tumble over the water fall", "and are washed up by the river side");
                                                                    } else
                                                                        if (obj.getID() == 486) {
                                                                            p.message("you walk through the doorway");
                                                                            p.teleport(667, 3279, false);
                                                                        }













                    return null;
                });
            }
        };
    }

    public void hadleyMainDialogue(final Player p, final Npc n, int cID) {
        if (cID == (-1)) {
            Functions.___playerTalk(p, n, "hello there");
            Functions.___npcTalk(p, n, "are you on holiday?, if so you've come to the right place", "i'm hadley the tourist guide, anything you need to know just ask me", "we have some of the most unspoilt wildlife and scenery in runescape", "people come from miles around to fish in the clear lakes", "or to wander the beautiful hill sides");
            Functions.___playerTalk(p, n, "it is quite pretty");
            Functions.___npcTalk(p, n, "surely pretty is an understatement kind sir", "beautiful, amazing or possibly life changing would be more suitable wording", "have your seen the baxtorian waterfall?", "it's named after the elf king who was buried beneath");
            cID = hadleyMainMenuOptions(p, n, Waterfall_Quest.HADLEY.ALL);
        }
        // can you tell me what happened to the elf king?
        if (cID == 0) {
            Functions.___npcTalk(p, n, "there are many myths about baxtorian", "One popular story is this", "after defending his kingdom against the invading dark forces from the west", "baxtorian returned to find his wife glarial had been captured by the enemy", "this destroyed baxtorian, after years of searching he reclused", "to the secret home he had made for glarial under the waterfall", "he never came out and it is told that only glarial could enter");
            Functions.___playerTalk(p, n, "what happened to him?");
            Functions.___npcTalk(p, n, "oh, i don't know", "i believe we have some pages on him upstairs in our archives", "if you wish to look at them please be careful, they're all pretty delicate");
            cID = hadleyMainMenuOptions(p, n, Waterfall_Quest.HADLEY.WHAT_HAPPENED);
        } else// where else is worth visiting around here?

            if (cID == 1) {
                Functions.___npcTalk(p, n, "there's a lovely spot for a picnic on the hill to the north east", "there lies a monument to the deceased elven queen glarial", "it really is quite pretty");
                Functions.___playerTalk(p, n, "who was queen glarial?");
                Functions.___npcTalk(p, n, "baxtorians wife, the only person who could also enter the waterfall", "she was queen when this land was inhabited by elven kind", "glarial was kidnapped while buxtorian was away", "but they eventually recovered her body and brought her home to rest");
                Functions.___playerTalk(p, n, "that's sad");
                Functions.___npcTalk(p, n, "true, i believe there's some information about her upstairs", "if you look at them please be careful");
                cID = hadleyMainMenuOptions(p, n, Waterfall_Quest.HADLEY.WHERE_ELSE);
            } else// is there treasure under the waterfall?

                if (cID == 2) {
                    Functions.___npcTalk(p, n, "ha ha, another treasure hunter", "well if there is no one's been able to get to it", "they've been searching that river for decades, all to no avail");
                    cID = hadleyMainMenuOptions(p, n, Waterfall_Quest.HADLEY.IS_THERE_TREAS);
                } else// thanks then, goodbye

                    if (cID == 3) {
                        Functions.___npcTalk(p, n, "enjoy your visit");
                        return;
                    }



        if (cID >= 0) {
            hadleyMainDialogue(p, n, cID);
        }
    }

    private int hadleyMainMenuOptions(Player p, Npc n, int discardOp) {
        String[] menuOpts;
        int choice;
        if (discardOp == 0) {
            menuOpts = new String[]{ "where else is worth visiting around here?", "is there treasure under the waterfall?", "thanks then, goodbye" };
        } else
            if (discardOp == 1) {
                menuOpts = new String[]{ "can you tell me what happened to the elf king?", "is there treasure under the waterfall?", "thanks then, goodbye" };
            } else
                if (discardOp == 2) {
                    menuOpts = new String[]{ "can you tell me what happened to the elf king?", "where else is worth visiting around here?", "thanks then, goodbye" };
                } else
                    if (discardOp == 3) {
                        menuOpts = new String[]{ "can you tell me what happened to the elf king?", "where else is worth visiting around here?", "is there treasure under the waterfall?" };
                    } else {
                        menuOpts = new String[]{ "can you tell me what happened to the elf king?", "where else is worth visiting around here?", "is there treasure under the waterfall?", "thanks then, goodbye" };
                    }



        choice = Functions.___showMenu(p, n, menuOpts);
        if ((discardOp != (-1)) && (choice >= discardOp)) {
            choice = choice + 1;
        }
        return choice;
    }

    public void hadleyAltDialogue(final Player p, final Npc n, int cID) {
        if (cID == (-1)) {
            Functions.___playerTalk(p, n, "hello there");
            Functions.___npcTalk(p, n, "well hello, come in, come in", "my names hadley, i'm head of tourism here in hemenster", "there's some of the most unspoilt wildlife and scenery in runescape here", "people come from miles around to fish in the clear lakes", "or to wander the beautiful hill sides");
            Functions.___playerTalk(p, n, "it is quite pretty");
            Functions.___npcTalk(p, n, "surely pretty is an understatement kind sir", "beautiful, amazing or possibly life changing would be more suitable wording", "have your seen the baxtorian waterfall", "it's quite a sight");
            cID = hadleyAltMenuOptions(p, n, Waterfall_Quest.HADLEY.ALL);
        }
        // what happened to the elf king?
        if (cID == 0) {
            Functions.___npcTalk(p, n, "baxtorian, i guess he died a long long time ago", "it's quite sad really", "after defending his kingdom against the invading dark forces from the west", "baxtorian returned to find his beautiful wife glarial had been captured", "this destroyed baxtorian, after years of searching he became a recluse", "in the secret home he had made for glarial under the waterfall", "he never came out and to this day no one has managed to get in");
            Functions.___playerTalk(p, n, "what happened to him?");
            Functions.___npcTalk(p, n, "no one knows");
            cID = hadleyAltMenuOptions(p, n, Waterfall_Quest.HADLEY.WHAT_HAPPENED);
        } else// where else is worth visiting around here?

            if (cID == 1) {
                Functions.___npcTalk(p, n, "well, there's a wide variety wildlife", "although unfortunately most of it's quite dangerous", "please don't feed the goblins");
                Functions.___playerTalk(p, n, "ok");
                Functions.___npcTalk(p, n, "there is a lovely spot for a picnic on the hill to the north east", "there's a monument to the deceased elven queen glarial", "it really is quite pretty");
                cID = hadleyAltMenuOptions(p, n, Waterfall_Quest.HADLEY.WHERE_ELSE);
            } else// i don't like nature, it gives me a rash

                if (cID == 2) {
                    Functions.___npcTalk(p, n, "that's just silly talk");
                    return;
                } else// thanks then, goodbye

                    if (cID == 3) {
                        Functions.___npcTalk(p, n, "enjoy your visit");
                        return;
                    }



        if (cID >= 0) {
            hadleyAltDialogue(p, n, cID);
        }
    }

    private int hadleyAltMenuOptions(Player p, Npc n, int discardOp) {
        String[] menuOpts;
        int choice;
        if (discardOp == 0) {
            menuOpts = new String[]{ "where else is worth visiting around here?", "i don't like nature, it gives me a rash", "thanks then, goodbye" };
        } else
            if (discardOp == 1) {
                menuOpts = new String[]{ "what happened to the elf king?", "i don't like nature, it gives me a rash", "thanks then, goodbye" };
            } else
                if (discardOp == 2) {
                    menuOpts = new String[]{ "what happened to the elf king?", "where else is worth visiting around here?", "thanks then, goodbye" };
                } else
                    if (discardOp == 3) {
                        menuOpts = new String[]{ "what happened to the elf king?", "where else is worth visiting around here?", "i don't like nature, it gives me a rash" };
                    } else {
                        menuOpts = new String[]{ "what happened to the elf king?", "where else is worth visiting around here?", "i don't like nature, it gives me a rash", "thanks then, goodbye" };
                    }



        choice = Functions.___showMenu(p, n, menuOpts);
        if ((discardOp != (-1)) && (choice >= discardOp)) {
            choice = choice + 1;
        }
        return choice;
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
        return (((((((item.getID() == ItemId.LARGE_KEY.id()) && (obj.getID() == 480)) || ((item.getID() == ItemId.AN_OLD_KEY.id()) && (obj.getID() == 135))) || (((((obj.getID() == 462) || (obj.getID() == 463)) || (obj.getID() == 462)) || (obj.getID() == 482)) && (item.getID() == ItemId.ROPE.id()))) || ((obj.getID() == 479) && (item.getID() == ItemId.GLARIALS_PEBBLE.id()))) || (((obj.getID() >= 473) && (obj.getID() <= 478)) && (((item.getID() == ItemId.WATER_RUNE.id()) || (item.getID() == ItemId.AIR_RUNE.id())) || (item.getID() == ItemId.EARTH_RUNE.id())))) || ((obj.getID() == 483) && (item.getID() == ItemId.GLARIALS_AMULET.id()))) || ((obj.getID() == 485) && (item.getID() == ItemId.GLARIALS_URN.id()));
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 480) && (item.getID() == ItemId.LARGE_KEY.id())) {
                        if (Functions.hasItem(p, ItemId.LARGE_KEY.id(), 1)) {
                            Functions.___doGate(p, obj);
                        }
                    } else
                        if ((obj.getID() == 479) && (item.getID() == ItemId.GLARIALS_PEBBLE.id())) {
                            Functions.___message(p, "you place the pebble in the gravestones small indent", "it fits perfectly");
                            if (CANT_GO(p)) {
                                Functions.___message(p, "but nothing happens");
                                return null;
                            } else {
                                Functions.___message(p, "You hear a loud creek", "the stone slab slides back revealing a ladder down", "you climb down to an underground passage");
                                p.teleport(631, 3305, false);
                                if (p.getQuestStage(quest) == 3) {
                                    p.updateQuestStage(quest, 4);
                                }
                                return null;
                            }
                        } else
                            if ((((obj.getID() == 462) || (obj.getID() == 463)) || (obj.getID() == 462)) || ((obj.getID() == 482) && (item.getID() == ItemId.ROPE.id()))) {
                                Functions.___message(p, "you tie one end of the rope around the tree", "you tie the other end into a loop", "and throw it towards the other dead tree");
                                if (obj.getID() == 462) {
                                    Functions.___message(p, "the rope loops around the tree", "you lower yourself into the rapidly flowing stream");
                                    p.teleport(662, 467, false);
                                    Functions.___message(p, "you manage to pull yourself over to the land mound");
                                } else
                                    if (obj.getID() == 463) {
                                        Functions.___message(p, "the rope loops around the tree", "you lower yourself into the rapidly flowing stream");
                                        p.teleport(659, 471, false);
                                        Functions.___message(p, "you manage to pull yourself over to the land mound");
                                    } else
                                        if (obj.getID() == 482) {
                                            Functions.___message(p, "you gently drop to the rock below", "under the waterfall there is a secret passage");
                                            p.teleport(659, 3305, false);
                                        }


                            } else
                                if ((obj.getID() == 135) && (item.getID() == ItemId.AN_OLD_KEY.id())) {
                                    Functions.doDoor(obj, p);
                                } else
                                    if (((obj.getID() >= 473) && (obj.getID() <= 478)) && (((item.getID() == ItemId.WATER_RUNE.id()) || (item.getID() == ItemId.AIR_RUNE.id())) || (item.getID() == ItemId.EARTH_RUNE.id()))) {
                                        if (!p.getCache().hasKey((("waterfall_" + obj.getID()) + "_") + item.getID())) {
                                            p.message(("you place the " + item.getDef(p.getWorld()).getName().toLowerCase()) + " on the stand");
                                            p.message("the rune stone crumbles into dust");
                                            p.getCache().store((("waterfall_" + obj.getID()) + "_") + item.getID(), true);
                                            p.getInventory().remove(item.getID(), 1);
                                        } else {
                                            p.message((("you have already placed " + article(item.getDef(p.getWorld()).getName())) + item.getDef(p.getWorld()).getName()) + " here");
                                        }
                                    } else
                                        if ((obj.getID() == 483) && (item.getID() == ItemId.GLARIALS_AMULET.id())) {
                                            boolean flag = false;
                                            for (int i = 473; i < 478; i++) {
                                                for (int y = 32; i < 34; i++) {
                                                    if (!p.getCache().hasKey((("waterfall_" + i) + "_") + y)) {
                                                        flag = true;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                Functions.___message(p, "you place the amulet around the statue", "nothing happens");
                                            } else {
                                                Functions.___message(p, "you place the amulet around the statue", "you hear a loud rumble beneath you", "the ground raises up before you");
                                                p.teleport(647, 3267, false);
                                            }
                                        } else
                                            if ((obj.getID() == 485) && (item.getID() == ItemId.GLARIALS_URN.id())) {
                                                Functions.___message(p, "you carefully poor the ashes in the chalice", "as you remove the baxtorian treasure", "the chalice remains standing", "inside you find a mithril case", "containing 40 seeds", "two diamond's and two gold bars");
                                                Functions.removeItem(p, ItemId.GLARIALS_URN.id(), 1);
                                                p.sendQuestComplete(getQuestId());
                                            }






                    return null;
                });
            }
        };
    }

    private String article(String word) {
        char c = word.toLowerCase().charAt(0);
        if (((((c == 'a') || (c == 'e')) || (c == 'i')) || (c == 'o')) || (c == 'u')) {
            return "an";
        } else {
            return "a";
        }
    }

    @Override
    public boolean blockInvAction(Item item, Player p, String command) {
        return (item.getID() == ItemId.BOOK_ON_BAXTORIAN.id()) || (item.getID() == ItemId.MITHRIL_SEED.id());
    }

    @Override
    public GameStateEvent onInvAction(Item i, Player p, String command) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ItemId.MITHRIL_SEED.id()) {
                        Functions.___message(p, "you open the small mithril case");
                        if (p.getViewArea().getGameObject(p.getLocation()) != null) {
                            p.message("you can't plant a tree here");
                            return null;
                        }
                        Functions.removeItem(p, ItemId.MITHRIL_SEED.id(), 1);
                        Functions.___message(p, "and drop a seed by your feet");
                        GameObject object = new GameObject(p.getWorld(), Point.location(p.getX(), p.getY()), 490, 0, 0);
                        p.getWorld().registerGameObject(object);
                        p.getWorld().delayedRemoveObject(object, 60000);
                        p.message("a tree magically sprouts around you");
                    } else
                        if (i.getID() == ItemId.BOOK_ON_BAXTORIAN.id()) {
                            Functions.___message(p, "the book is old with many pages missing", "a few are translated from elven into common tongue");
                            if (p.getQuestStage(quest) == 2) {
                                p.updateQuestStage(quest, 3);
                            }
                            int menu = Functions.___showMenu(p, "the missing relics", "the sonnet of baxtorian", "the power of nature", "ode to eternity");
                            if (menu == 0) {
                                ActionSender.sendBox(p, "@yel@The Missing Relics@whi@% %" + (((((((((("Many artifacts of elven history were lost after the second age. % " + "The greatest loss to our collection of elf history were the hidden%") + "treasures of Baxtorian.") + "% %Some believe these treasures are still unclaimed, but it is more") + "%commonly believed that dwarf miners recovered the treasure at") + "%the beginning of the third age. ") + "% %Another great loss was Glarial's pebble a key which allowed her") + "% ancestors to visit her tomb. The stone was stolen by a gnome") + "% family over a century ago.") + "% % It is believed that the gnomes ancestor Glorie still has the stone") + "hidden in the caves of the gnome tree village."), true);
                            } else
                                if (menu == 1) {
                                    ActionSender.sendBox(p, "@yel@The Sonnet of Baxtorian@whi@" + ((((((((((("% %The love between Baxtorian and Glarial was said to have lasted" + "%over a century. They lived a peaceful life learning and teaching ") + "%the laws of nature.") + "% %When Baxtorian's kingdom was invaded by the dark forces he left") + "%on a five year campaign. He returned to find his people") + "%slaughtered and his wife taken by the enemy.") + "% %After years of searching for his love he finally gave up, he") + "%returned to the home he made for himself and Glarial under the ") + "% baxtorian waterfall. Once he entered he never returned.") + "% % Only Glarial had the power to also enter the waterfall. Since") + "%Baxtorian entered no one but her can follow him in, it's as if the") + "%powers of nature still work to protect him."), true);
                                } else
                                    if (menu == 2) {
                                        ActionSender.sendBox(p, "" + (((("@yel@The Power of Nature@whi@" + "%Glarial and Baxtorian were masters of nature. Trees would grow,") + "%mountains form and rivers flood all to their command. Baxtorian") + "%in particular had perfected rune lore. It was said that he could") + "%use the stones to control the water, earth and air."), false);
                                    } else
                                        if (menu == 3) {
                                            ActionSender.sendBox(p, "@yel@Ode to Eternity@whi@" + ((((("% %@yel@A Short Piece Written by Baxtorian himself@whi@" + "% % What care I for this mortal coil, where treasures are yet so frail,") + "%for it is you that is my life blood, the wine to my holy grail") + "% %and if I see the judgement day, when the gods fill the air with") + "% dust, I'll happily choke on your memory, as my kingdom turns to ") + "rust."), true);
                                        }



                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
        return obj.getID() == 135;
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 135) {
                        Functions.___message(p, "the door is locked", "you need a key");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnWallObject(GameObject obj, Item item, Player player) {
        return (obj.getID() == 135) && (item.getID() == ItemId.AN_OLD_KEY.id());
    }

    @Override
    public GameStateEvent onInvUseOnWallObject(GameObject obj, Item item, Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 135) && (item.getID() == ItemId.AN_OLD_KEY.id())) {
                        Functions.___message(player, "you open the door with the key");
                        Functions.doDoor(obj, player);
                        Functions.___message(player, "You go through the door");
                    }
                    return null;
                });
            }
        };
    }

    private boolean CANT_GO(Player p) {
        for (Item item : p.getInventory().getItems()) {
            String name = item.getDef(p.getWorld()).getName().toLowerCase();
            if ((((((((((((name.contains("dagger") || name.contains("scimitar")) || name.contains("bow")) || name.contains("mail")) || name.contains("plated")) || (item.getID() == ItemId.RUNE_SKIRT.id())) || name.contains("shield")) || (((name.contains("sword") && (!name.equalsIgnoreCase("Swordfish"))) && (!name.equalsIgnoreCase("Burnt Swordfish"))) && (!name.equalsIgnoreCase("Raw Swordfish")))) || name.contains("mace")) || name.contains("helmet")) || name.contains("axe")) || name.contains("throwing knife")) || name.contains("spear")) {
                return true;
            }
        }
        return false;
    }

    class HADLEY {
        public static final int ALL = -1;

        public static final int WHAT_HAPPENED = 0;

        public static final int WHERE_ELSE = 1;

        public static final int IS_THERE_TREAS = 2;
    }
}

