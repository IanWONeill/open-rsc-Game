package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
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
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class HazeelCult implements QuestInterface , InvUseOnObjectListener , ObjectActionListener , PlayerKilledNpcListener , TalkToNpcListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener , PlayerKilledNpcExecutiveListener , TalkToNpcExecutiveListener {
    private static final int BUTLERS_CUPBOARD_OPEN = 441;

    private static final int BUTLERS_CUPBOARD_CLOSED = 440;

    private static final int BASEMENT_CRATE = 439;

    private static final int TOP_LEVEL_BOOKCASE = 436;

    private static final int CARNILLEAN_CHEST_OPEN = 437;

    private static final int CARNILLEAN_CHEST_CLOSED = 438;

    @Override
    public int getQuestId() {
        return Quests.THE_HAZEEL_CULT;
    }

    @Override
    public String getQuestName() {
        return "The Hazeel Cult (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        if (p.getCache().hasKey("good_side")) {
            p.message("Well done you have completed the Hazeel cult quest");
            Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.THE_HAZEEL_CULT), true);
            p.message("@gre@You haved gained 1 quest point!");
            p.message("ceril gives you 2000 gold coins");
            Functions.addItem(p, ItemId.COINS.id(), 2000);
        } else
            if (p.getCache().hasKey("evil_side")) {
                p.message("Hazeel gives you some coins");
                Functions.addItem(p, ItemId.COINS.id(), 2000);
                Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.THE_HAZEEL_CULT), true);
                p.message("@gre@You haved gained 1 quest point!");
                p.message("you have completed the hazeel cult quest");
            }

    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return DataConversions.inArray(new int[]{ NpcId.CLAUS.id(), NpcId.CERIL.id(), NpcId.BUTLER.id(), NpcId.HENRYETA.id(), NpcId.PHILIPE.id(), NpcId.CARNILLEAN_GUARD.id(), NpcId.CLIVET.id(), NpcId.CULT_MEMBER.id(), NpcId.ALOMONE.id() }, n.getID());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.CERIL.id()) {
                        switch (p.getQuestStage(quest)) {
                            case 0 :
                                Functions.___playerTalk(p, n, "hello there");
                                Functions.___npcTalk(p, n, "blooming, thieving, wierdos", "why don't they leave me alone?");
                                int menu = // do not send over
                                Functions.___showMenu(p, n, false, "What's wrong?", "You probably deserve it", "You seem uptight, I'll leave you alone");
                                if (menu == 0) {
                                    Functions.___playerTalk(p, n, "What's wrong?");
                                    Functions.___npcTalk(p, n, "it's those strange folk from the forest", "those freaks keep breaking into my house");
                                    Functions.___playerTalk(p, n, "have they taken much?");
                                    Functions.___npcTalk(p, n, "they first broke in months ago and stole a suit of armour", "the strange thing is that they've broken in four times since", "but took nothing");
                                    Functions.___playerTalk(p, n, "and you are...?");
                                    Functions.___npcTalk(p, n, "why, i'm ceril carnillean", "we really are quite a famous bloodline", "we've played a large part in ardounge pollitics for generations", "maybe you could help retrieve the armour?", "of course there would be a handsom cash reward for yourself");
                                    int option = // do not send over
                                    Functions.___showMenu(p, n, false, "No thanks i've got plans", "yes, off course,i'd be happy to help");
                                    if (option == 0) {
                                        Functions.___playerTalk(p, n, "no thanks i've got plans");
                                        Functions.___npcTalk(p, n, "no wonder i'm the one with the big house and you're on the streets");
                                    } else
                                        if (option == 1) {
                                            Functions.___playerTalk(p, n, "yes of course, i'd be happy to help");
                                            Functions.___npcTalk(p, n, "that's very kind of you", "I caught a glimpse of the thieves leaving", "but due to ermm... my cold... I was unable to give chase", "they were dressed all in black", "I think they may have belonged to some sort of cult");
                                            Functions.___playerTalk(p, n, "do you know where they are?");
                                            Functions.___npcTalk(p, n, "my old butler once followed them", "to a cave entrance in the forest south of here", "unfortunately the next night he died in his sleep");
                                            Functions.___playerTalk(p, n, "that's awful");
                                            Functions.___npcTalk(p, n, "it's ok, a replacement arrived the next day", "he's been great, cooks an excellent broth");
                                            Functions.___playerTalk(p, n, "ok ceril, i'll see what i can do");
                                            p.updateQuestStage(quest, 1);
                                        }

                                } else
                                    if (menu == 1) {
                                        Functions.___playerTalk(p, n, "you probably deserve it");
                                        Functions.___npcTalk(p, n, "who are you to judge me?", "hmmm, you look like a peasant", "i'm wasting my time talking to you");
                                    } else
                                        if (menu == 2) {
                                            Functions.___playerTalk(p, n, "you seem uptight,i'll leave you alone");
                                            Functions.___npcTalk(p, n, "yes, i doubt you could help");
                                        }


                                break;
                            case 1 :
                            case 2 :
                                Functions.___playerTalk(p, n, "hello ceril");
                                Functions.___npcTalk(p, n, "it's sir ceril to you", "and shouldn't you be out recovering my suit of armour?");
                                break;
                            case 3 :
                                Functions.___npcTalk(p, n, "have you had any luck yet?");
                                if (p.getCache().hasKey("good_side")) {
                                    Functions.___playerTalk(p, n, "hello ceril, i've discovered the hideout");
                                    Functions.___npcTalk(p, n, "well done... and the armour?");
                                    Functions.___playerTalk(p, n, "i'm afraid not", "i spoke to a cult member in the entrance of the cave", "but he escaped into the sewer systems", "seems they have a grievance with your family", "something to do with some bloke called hazeel");
                                    Functions.___npcTalk(p, n, "err errmm... no", "They're obviously all mad", "just find them and bring back the armour");
                                    return null;
                                } else
                                    if (p.getCache().hasKey("evil_side")) {
                                        Functions.___playerTalk(p, n, "i'm afraid not ceril");
                                        Functions.___npcTalk(p, n, "well that's strange", "the butler seemed quite sure about their location");
                                        return null;
                                    }

                                break;
                            case 4 :
                                if (p.getCache().hasKey("good_side")) {
                                    if (Functions.hasItem(p, ItemId.CARNILLEAN_ARMOUR.id())) {
                                        Functions.___playerTalk(p, n, "ceril, how are you?", "Look, I've found the armour");
                                        Functions.___npcTalk(p, n, "well done i must say i am impressed");
                                        Functions.___message(p, "you give ceril the family armour");
                                        Functions.removeItem(p, ItemId.CARNILLEAN_ARMOUR.id(), 1);
                                        Functions.___npcTalk(p, n, "before we send you on your way", "i'll get our butler jones", "to whip you up some of his special broth");
                                        Functions.___playerTalk(p, n, "i'd rather not", "i overheard the cult members talking", "the buttler is really working for them");
                                        Functions.___npcTalk(p, n, "that's it, come with me", "we'll sort this out once and for all");
                                        Functions.___message(p, "you follow ceril up to butler Jones' room");
                                        p.teleport(613, 1562);
                                        Functions.___message(p, "ceril speaks briefly with Jones");
                                        Npc ceril = Functions.getNearestNpc(p, NpcId.CERIL.id(), 10);
                                        Functions.___npcTalk(p, ceril, "Well, he assures me that he's a loyal hard working man", "I cannot fathom, why you would believe he is a spy");
                                        Functions.___playerTalk(p, ceril, "surely you won't take his word for it?");
                                        Functions.___npcTalk(p, ceril, "we have also decided that due to the humilliation you have caused", "it is only fair that Jones shall recieve your reward", "you shall recieve payment more suited to your low life personality");
                                        Functions.___message(p, "ceril gives you 5 gold coins");
                                        Functions.addItem(p, ItemId.COINS.id(), 5);
                                        Functions.___message(p, "ceril gives jones 695 gold coins");
                                        Functions.___npcTalk(p, ceril, "now take it and leave");
                                        Functions.___message(p, "butler Jones has a slight grin", "You're going to need more than just your word", "To prove Jones' treachary");
                                        p.updateQuestStage(quest, 5);
                                    } else {
                                        Functions.___playerTalk(p, n, "ceril, how are you?");
                                        Functions.___npcTalk(p, n, "Im ok. Have you found the armour");
                                        Functions.___playerTalk(p, n, "i'm afraid not");
                                        Functions.___npcTalk(p, n, "well i'm not paying you to see the sights");
                                        Functions.___playerTalk(p, n, "okay, i'll go and try and retrieve it for you");
                                    }
                                    return null;
                                } else
                                    if (p.getCache().hasKey("evil_side")) {
                                        Functions.___playerTalk(p, n, "hello again");
                                        Functions.___npcTalk(p, n, "oh my, the misery, the pain", "my son is a good boy but stupid as well", "i can't believe he gave his dinner to scruffy", "without having the servents check it for poison first", "how could he be so careless?");
                                        Functions.___playerTalk(p, n, "scruffy?");
                                        Functions.___npcTalk(p, n, "he's been in the family for twenty years the poor dog", "what did he ever do to hurt anyone?");
                                        return null;
                                    }

                                break;
                            case 5 :
                                if (p.getCache().hasKey("good_side")) {
                                    Functions.___playerTalk(p, n, "you owe me money");
                                    Functions.___npcTalk(p, n, "i owe you nothing now go away", "before i have jones throw you out");
                                    return null;
                                } else
                                    if (p.getCache().hasKey("evil_side")) {
                                        Functions.___playerTalk(p, n, "ceril, how are you?");
                                        Functions.___npcTalk(p, n, "I'm devestated", "i don't know what to do with myself since i lost scruffy");
                                        Functions.___message(p, "ceril bursts into tears");
                                        return null;
                                    }

                                break;
                            case 6 :
                                Functions.___playerTalk(p, n, "hello ceril, how are you?");
                                Functions.___npcTalk(p, n, "I think the thieves may have been back in the house");
                                Functions.___playerTalk(p, n, "why?");
                                Functions.___npcTalk(p, n, "i'm not sure but it seem's as if some of my books", "have been re-arranged in my study", "it's either that or i'm losing my marbles");
                                break;
                            case -1 :
                                if (p.getCache().hasKey("good_side")) {
                                    Functions.___playerTalk(p, n, "hello ceril");
                                    Functions.___npcTalk(p, n, "well hello there", "brave adventurer, it's good to see you again", "if it wasn't for you", "that butler jones would have poisoned me by now");
                                } else
                                    if (p.getCache().hasKey("evil_side")) {
                                        Functions.___playerTalk(p, n, "hello ceril");
                                        Functions.___npcTalk(p, n, "i maybe wrong", "but ever since i asked for your help", "thing's have gone from bad to worse", "i think from now on you better keep out of my way");
                                    }

                                break;
                        }
                    } else
                        if (n.getID() == NpcId.BUTLER.id()) {
                            switch (p.getQuestStage(quest)) {
                                case 0 :
                                case 2 :
                                    Functions.___playerTalk(p, n, "hello there");
                                    Functions.___npcTalk(p, n, "hello,how are you today?");
                                    Functions.___playerTalk(p, n, "good thanks and yourself");
                                    Functions.___npcTalk(p, n, "fine and dandy");
                                    break;
                                case 1 :
                                    Functions.___playerTalk(p, n, "hello, what is this building?");
                                    Functions.___npcTalk(p, n, "this is the property of Sir Ceril Carnillean", "of the noble carnillean family", "you're welcome to look around", "but i'm afraid i'll have to keep an eye on you", "we've been having a real problem with thieves", "strange cult folk coming out the forest");
                                    Functions.___playerTalk(p, n, "that's a shame");
                                    Functions.___npcTalk(p, n, "yes well these things are bound to happen", "when you're as wealthy as the Varnilleans");
                                    int butMenu = // do not send over
                                    Functions.___showMenu(p, n, false, "Have you any more info on the carnilleans?", "How long have you worked here?", "Ok then take care");
                                    if (butMenu == 0) {
                                        Functions.___playerTalk(p, n, "Have you any more info on the carnilleans?");
                                        Functions.___npcTalk(p, n, "there's a lot i could tell you", "about the carnillean family history", "i'm afraid if did speak about such matter's", "i would lose my job and that i cannot risk");
                                    } else
                                        if (butMenu == 1) {
                                            Functions.___playerTalk(p, n, "how long have you worked here?");
                                            Functions.___npcTalk(p, n, "long enough to know the carnilleans", "are not as innocent or noble as they seem");
                                        } else
                                            if (butMenu == 2) {
                                                Functions.___playerTalk(p, n, "ok then take care");
                                                Functions.___npcTalk(p, n, "you to");
                                            }


                                    break;
                                case 3 :
                                    Functions.___playerTalk(p, n, "how long have you worked here?");
                                    Functions.___npcTalk(p, n, "long enough to know the carnillean's", "are not as innocent or noble as they seem");
                                    break;
                                case 4 :
                                    if (p.getCache().hasKey("good_side")) {
                                        Functions.___playerTalk(p, n, "jones i need to talk to you");
                                        Functions.___npcTalk(p, n, "do you need some help with your quest?");
                                        Functions.___playerTalk(p, n, "you can stop the act jones", "i know you're working for the cult");
                                        Functions.___npcTalk(p, n, "what? don't be so silly");
                                        Functions.___playerTalk(p, n, "I overheard the cult leader talking about you");
                                        Functions.___npcTalk(p, n, "look here,you may think you know something", "but really you have no idea");
                                        Functions.___playerTalk(p, n, "i know once i reveal the truth", "you'll be locked up");
                                        Functions.___npcTalk(p, n, "you think that old fool ceril", "will take your word over mine", "he completely trust's me");
                                        Functions.___playerTalk(p, n, "we will have to see about that");
                                        Functions.___npcTalk(p, n, "i'll warn you once more traveller", "don't get involved");
                                        return null;
                                    } else
                                        if (p.getCache().hasKey("evil_side")) {
                                            Functions.___playerTalk(p, n, "hello there");
                                            Functions.___npcTalk(p, n, "hello friend,i heard about your handy work", "quite amusing really", "I'm sure hazeel will be pleased with you anyway", "keep up the good work");
                                            return null;
                                        }

                                    break;
                                case 5 :
                                    if (p.getCache().hasKey("good_side")) {
                                        Functions.___playerTalk(p, n, "hello");
                                        Functions.___npcTalk(p, n, "you fool", "did you think you could simply accuse me and save the day?", "we've been working on this for years", "your interference is only a minor set back to our plans", "and when the mighty hazeel does return", "the likes of you and the carnilleans will be the first of many to suffer");
                                        return null;
                                    } else
                                        if (p.getCache().hasKey("evil_side")) {
                                            Functions.___npcTalk(p, n, "hello again friend", "I see you you have the mark", "you should keep that covered up");
                                            Functions.___playerTalk(p, n, "oh that's just an old family pass down");
                                            Functions.___npcTalk(p, n, "you don't have to pretend to me friend", "our cause is one and the same", "the sooner lord hazeel is avenged", "the better for us and this city");
                                            Functions.___playerTalk(p, n, "have you any idea where the sacred script is");
                                            Functions.___npcTalk(p, n, "no idea i'm afraid", "it must be somewhere in the house", "but i can't find it for the life of me", "i've searched high and low");
                                            Functions.___playerTalk(p, n, "doesn't ceril get suspisous");
                                            Functions.___npcTalk(p, n, "that old fool", "he can't can't see the forest for the tree's");
                                            return null;
                                        }

                                    break;
                                case 6 :
                                    Functions.___playerTalk(p, n, "hello jones");
                                    Functions.___npcTalk(p, n, "have you managed to find the script?");
                                    if (Functions.hasItem(p, ItemId.SCRIPT_OF_HAZEEL.id())) {
                                        Functions.___playerTalk(p, n, "I have it here");
                                        Functions.___npcTalk(p, n, "incredible, we owe you a lot", "you better get it back to our hideout as quick as you can", "these our exciting times traveller", "once the great hazeel returns", "things are going to really change around here");
                                    } else {
                                        Functions.___playerTalk(p, n, "i'm afraid i've lost it");
                                        Functions.___npcTalk(p, n, "how could you be so foolish", "the future of our people completly relys on that script", "you better find it again quickly");
                                    }
                                    break;
                                case -1 :
                                    if (p.getCache().hasKey("good_side")) {
                                        Functions.___playerTalk(p, n, "hello stranger");
                                        Functions.___npcTalk(p, n, "why hello there");
                                        Functions.___playerTalk(p, n, "i take it you're the new butler");
                                        Functions.___npcTalk(p, n, "that's right", "i think they had some problems with the last one");
                                        Functions.___playerTalk(p, n, "you could say that");
                                    } else
                                        if (p.getCache().hasKey("evil_side")) {
                                            Functions.___playerTalk(p, n, "hello jones");
                                            Functions.___npcTalk(p, n, "it's an honour to be in your presence again traveller", "I hope things are well");
                                            Functions.___playerTalk(p, n, "not bad, yourself");
                                            Functions.___npcTalk(p, n, "i'm good thanks");
                                        }

                                    break;
                            }
                        } else
                            if (n.getID() == NpcId.HENRYETA.id()) {
                                switch (p.getQuestStage(quest)) {
                                    case 0 :
                                    case 1 :
                                    case 2 :
                                        Functions.___playerTalk(p, n, "hello");
                                        Functions.___npcTalk(p, n, "oh hello", "if you wish to look around the carnillean family home", "please refraine from touching anything", "with those grubby hands of yours");
                                        break;
                                    case 3 :
                                        Functions.___playerTalk(p, n, "hello madam");
                                        Functions.___npcTalk(p, n, "i hope you've found those awful holigans", "I can't sleep at night");
                                        if (p.getCache().hasKey("good_side")) {
                                            Functions.___playerTalk(p, n, "i'm working on it madam");
                                            Functions.___npcTalk(p, n, "i don't know", "there really are some strange folk around these parts");
                                        } else
                                            if (p.getCache().hasKey("evil_side")) {
                                                Functions.___playerTalk(p, n, "I'm afraid not");
                                                Functions.___npcTalk(p, n, "you really are useless");
                                                Functions.___playerTalk(p, n, "thanks a lot");
                                            }

                                        break;
                                    case 4 :
                                        if (p.getCache().hasKey("good_side")) {
                                            Functions.___playerTalk(p, n, "hello");
                                            Functions.___npcTalk(p, n, "oh, hello there adventurer", "i hope you were careful dealing with those nasty men");
                                            Functions.___playerTalk(p, n, "i was fine, thanks");
                                            return null;
                                        } else
                                            if (p.getCache().hasKey("evil_side")) {
                                                Functions.___playerTalk(p, n, "hello are you ok?");
                                                Functions.___npcTalk(p, n, "no i'm not ok", "those animals slaughtered my precious scruffy", "i'll never recover", "i'm emotionaly scarred for life");
                                                Functions.___playerTalk(p, n, "i'm sorry to hear that");
                                                Functions.___npcTalk(p, n, "don't be sorry it's not your fault", "just find those animals and punish them severely", "before i get to them first");
                                                return null;
                                            }

                                        break;
                                    case 5 :
                                    case 6 :
                                        if (p.getCache().hasKey("good_side")) {
                                            Functions.___playerTalk(p, n, "hello henyeta");
                                            Functions.___npcTalk(p, n, "don't think you can accuse my trusted staff", "then be friends with me");
                                            Functions.___playerTalk(p, n, "what i said about jones is true");
                                            Functions.___npcTalk(p, n, "don't be so ridiculous", "next you'll tell me he murdered our old butler");
                                            return null;
                                        } else
                                            if (p.getCache().hasKey("evil_side")) {
                                                Functions.___playerTalk(p, n, "hello");
                                                Functions.___npcTalk(p, n, "i'm sorry i'm too depressed to talk to you", "poor scruffy...");
                                                Functions.___playerTalk(p, n, "yeah, poor scruffy!");
                                                return null;
                                            }

                                        break;
                                    case -1 :
                                        if (p.getCache().hasKey("good_side")) {
                                            Functions.___playerTalk(p, n, "hello");
                                            Functions.___npcTalk(p, n, "hello again adventurer\"", "things really have picked up around here", "since you dealt with those nasty cult members", "good to hear");
                                        } else
                                            if (p.getCache().hasKey("evil_side")) {
                                                Functions.___playerTalk(p, n, "hello");
                                                Functions.___npcTalk(p, n, "i've been instructed by my husband not to talk to you", "so go away and leave me alone");
                                                Functions.___playerTalk(p, n, "charming");
                                            }

                                        break;
                                }
                            } else
                                if (n.getID() == NpcId.PHILIPE.id()) {
                                    switch (p.getQuestStage(quest)) {
                                        case 0 :
                                        case 1 :
                                        case 2 :
                                            Functions.___playerTalk(p, n, "hello there");
                                            Functions.___npcTalk(p, n, "what have you brought me?", "I want some more toys");
                                            Functions.___playerTalk(p, n, "I'm afraid i don't have any");
                                            Functions.___npcTalk(p, n, "toys, i want toys");
                                            break;
                                        case 3 :
                                            if (p.getCache().hasKey("good_side")) {
                                                Functions.___playerTalk(p, n, "hello");
                                                Functions.___npcTalk(p, n, "i want more toys");
                                                Functions.___playerTalk(p, n, "sorry i don't have any");
                                                Functions.___npcTalk(p, n, "i want sweets, gimme sweets");
                                                Functions.___playerTalk(p, n, "no sorrry i don't have sweets either");
                                                Functions.___npcTalk(p, n, "i hate you, i want my mum");
                                                return null;
                                            } else
                                                if (p.getCache().hasKey("evil_side")) {
                                                    Functions.___playerTalk(p, n, "hello");
                                                    Functions.___npcTalk(p, n, "i want more toys");
                                                    Functions.___playerTalk(p, n, "sorry i don't have any");
                                                    Functions.___npcTalk(p, n, "i want sweets, gimme sweets");
                                                    Functions.___playerTalk(p, n, "no sorrry i don't have sweets either");
                                                    Functions.___npcTalk(p, n, "i hate you, i want my mum");
                                                    return null;
                                                }

                                            break;
                                        case 4 :
                                            if (p.getCache().hasKey("good_side")) {
                                                Functions.___playerTalk(p, n, "hello");
                                                Functions.___npcTalk(p, n, "mommy said your here to", "kill all the nasty men", "that come into our house");
                                                Functions.___playerTalk(p, n, "something like that");
                                                Functions.___npcTalk(p, n, "can i watch?");
                                                Functions.___playerTalk(p, n, "no");
                                                return null;
                                            } else
                                                if (p.getCache().hasKey("evil_side")) {
                                                    Functions.___playerTalk(p, n, "hello youngster");
                                                    Functions.___message(p, "the boy looks very upset");
                                                    Functions.___npcTalk(p, n, "someone killed scruffy", "i liked scruffy", "he never told me off");
                                                    Functions.___playerTalk(p, n, "that's unfortunate");
                                                    Functions.___npcTalk(p, n, "i want my mommy");
                                                    return null;
                                                }

                                            break;
                                        case 5 :
                                            if (p.getCache().hasKey("good_side")) {
                                                Functions.___playerTalk(p, n, "hello youngster");
                                                Functions.___npcTalk(p, n, "daddy say's you dont like Jones", "Jones is nice", "he brings me toys and sweets");
                                                Functions.___playerTalk(p, n, "jones is a bad person philipe");
                                                Functions.___npcTalk(p, n, "you're a bad person", "i don't like you");
                                                Functions.___playerTalk(p, n, "ok");
                                                return null;
                                            } else
                                                if (p.getCache().hasKey("evil_side")) {
                                                    Functions.___playerTalk(p, n, "hello");
                                                    Functions.___npcTalk(p, n, "mommy said your here to", "kill all the nasty men", "that come into our house");
                                                    Functions.___playerTalk(p, n, "something like that");
                                                    Functions.___npcTalk(p, n, "can i watch?");
                                                    Functions.___playerTalk(p, n, "no");
                                                    return null;
                                                }

                                            break;
                                        case 6 :
                                            Functions.___playerTalk(p, n, "hello youngster");
                                            Functions.___npcTalk(p, n, "why are you still here?");
                                            Functions.___playerTalk(p, n, "just looking around");
                                            Functions.___npcTalk(p, n, "have you got me some toys?");
                                            Functions.___playerTalk(p, n, "no");
                                            Functions.___npcTalk(p, n, "then i don't like you");
                                            Functions.___playerTalk(p, n, "that's a shame");
                                            break;
                                        case -1 :
                                            if (p.getCache().hasKey("good_side")) {
                                                Functions.___playerTalk(p, n, "hello philipe");
                                                Functions.___npcTalk(p, n, "i want more toys");
                                                Functions.___playerTalk(p, n, "sorry i don't have any");
                                                Functions.___npcTalk(p, n, "i want sweets, gimme sweets");
                                                Functions.___playerTalk(p, n, "no sorrry", "I don't have any sweets either");
                                                Functions.___npcTalk(p, n, "i hate you,i want my mum");
                                            } else
                                                if (p.getCache().hasKey("evil_side")) {
                                                    Functions.___playerTalk(p, n, "hello philipe");
                                                    Functions.___npcTalk(p, n, "i want more toys");
                                                    Functions.___playerTalk(p, n, "sorry i don't have any");
                                                    Functions.___npcTalk(p, n, "i want sweets, gimme sweets");
                                                    Functions.___playerTalk(p, n, "no sorrry", "I don't have any sweets either");
                                                    Functions.___npcTalk(p, n, "i hate you,i want my mum");
                                                }

                                            break;
                                    }
                                } else
                                    if (n.getID() == NpcId.CLAUS.id()) {
                                        switch (p.getQuestStage(quest)) {
                                            case 0 :
                                            case 1 :
                                                Functions.___playerTalk(p, n, "hello");
                                                Functions.___npcTalk(p, n, "sorry i can't talk now", "you would be amazed how many", "meals this family can go through");
                                                break;
                                            case 2 :
                                                break;
                                            case 3 :
                                                Functions.___playerTalk(p, n, "hello");
                                                Functions.___npcTalk(p, n, "you're that chap they've asked to help get those nasty folk", "that keep breaking in");
                                                Functions.___playerTalk(p, n, "yep, that's me");
                                                Functions.___npcTalk(p, n, "well i wish the best of luck");
                                                break;
                                            case 4 :
                                                if (p.getCache().hasKey("good_side")) {
                                                    Functions.___playerTalk(p, n, "hello there");
                                                    Functions.___npcTalk(p, n, "hello, how are you today");
                                                    Functions.___playerTalk(p, n, "not bad thanks");
                                                    Functions.___npcTalk(p, n, "good good");
                                                    return null;
                                                } else
                                                    if (p.getCache().hasKey("evil_side")) {
                                                        Functions.___npcTalk(p, n, "hello there", "caught any thieves yet?");
                                                        Functions.___playerTalk(p, n, "afraid not");
                                                        Functions.___npcTalk(p, n, "keep at it");
                                                        return null;
                                                    }

                                                break;
                                            case 5 :
                                                if (p.getCache().hasKey("evil_side")) {
                                                    Functions.___playerTalk(p, n, "hello there");
                                                    Functions.___npcTalk(p, n, "i don't understand it", "how could someone slip poison in my cooking", "without me even noticing", "I'll be lucky if the carnilleans don't fire me", "those animals how could they do it", "poor scruffy");
                                                    return null;
                                                } else
                                                    if (p.getCache().hasKey("good_side")) {
                                                        Functions.___playerTalk(p, n, "hello there");
                                                        Functions.___npcTalk(p, n, "hello, how are you today");
                                                        Functions.___playerTalk(p, n, "not bad thanks");
                                                        Functions.___npcTalk(p, n, "good good");
                                                        return null;
                                                    }

                                                break;
                                            case 6 :
                                                Functions.___playerTalk(p, n, "hello there");
                                                Functions.___npcTalk(p, n, "those animals how could they do it", "poor scruffy");
                                                break;
                                            case -1 :
                                                if (p.getCache().hasKey("good_side")) {
                                                    Functions.___playerTalk(p, n, "hello cook");
                                                    Functions.___npcTalk(p, n, "well hello there traveller", "are we fit and well");
                                                    Functions.___playerTalk(p, n, "yes i'm fine");
                                                    Functions.___npcTalk(p, n, "good to hear");
                                                } else
                                                    if (p.getCache().hasKey("evil_side")) {
                                                        Functions.___playerTalk(p, n, "hello cook");
                                                        Functions.___npcTalk(p, n, "get out of my kitchen", "can't you tell your not welcome around here");
                                                    }

                                                break;
                                        }
                                    } else
                                        if (n.getID() == NpcId.CARNILLEAN_GUARD.id()) {
                                            switch (p.getQuestStage(quest)) {
                                                case 0 :
                                                case 1 :
                                                case 2 :
                                                    Functions.___playerTalk(p, n, "hello");
                                                    Functions.___npcTalk(p, n, "hello,i haven't seen you before", "if you've come to look at the carnillean family home", "just make sure you behave yourself", "we've had enough wierdos causing trouble", "round here of late");
                                                    break;
                                                case 3 :
                                                    Functions.___playerTalk(p, n, "hello");
                                                    Functions.___npcTalk(p, n, "hi i heard you're after the cult", "who broke in the other night", "blooming wierdo's");
                                                    break;
                                                case 4 :
                                                    if (p.getCache().hasKey("good_side")) {
                                                        Functions.___playerTalk(p, n, "hello");
                                                        Functions.___npcTalk(p, n, "hello brave adventurer", "keep up the good work");
                                                        return null;
                                                    } else
                                                        if (p.getCache().hasKey("evil_side")) {
                                                            Functions.___playerTalk(p, n, "hello there");
                                                            Functions.___npcTalk(p, n, "oh hello, did you hear?", "the cult members have been back", "I don't know what they've done", "but ceril is really upset");
                                                            return null;
                                                        }

                                                    break;
                                                case 5 :
                                                    if (p.getCache().hasKey("good_side")) {
                                                        Functions.___playerTalk(p, n, "hello");
                                                        Functions.___npcTalk(p, n, "hello adventurer", "i heard you've accused butler jones", "of being involved with the cult", "that's right", "to be honest i haven't", "trusted him since he turned up here", "a day after the old butler died", "it seems too much of a coincidence to me");
                                                        return null;
                                                    } else
                                                        if (p.getCache().hasKey("evil_side")) {
                                                            Functions.___playerTalk(p, n, "hello");
                                                            Functions.___npcTalk(p, n, "hello adventurer", "you're still hanging around then");
                                                            return null;
                                                        }

                                                    break;
                                                case 6 :
                                                    Functions.___playerTalk(p, n, "hello guard");
                                                    Functions.___npcTalk(p, n, "hello there", "i hope you find the cult soon", "we think there may have been another burglary");
                                                    Functions.___playerTalk(p, n, "that's worrying");
                                                    Functions.___npcTalk(p, n, "i just don't know how they do it", "it seems like they're right under our noses");
                                                    break;
                                                case -1 :
                                                    if (p.getCache().hasKey("good_side")) {
                                                        Functions.___playerTalk(p, n, "hello");
                                                        Functions.___npcTalk(p, n, "well if it isn't our own local hero", "it's good to see you in these parts again");
                                                    } else
                                                        if (p.getCache().hasKey("evil_side")) {
                                                            Functions.___playerTalk(p, n, "hello");
                                                            Functions.___npcTalk(p, n, "you again", "didn't i tell you you're not welcome around here", "now leave before we have to get rough with you");
                                                        }

                                                    break;
                                            }
                                        } else
                                            if (n.getID() == NpcId.CLIVET.id()) {
                                                switch (p.getQuestStage(quest)) {
                                                    case 0 :
                                                        Functions.___playerTalk(p, n, "hello there");
                                                        Functions.___npcTalk(p, n, "what do you want traveller");
                                                        Functions.___playerTalk(p, n, "just passing by");
                                                        Functions.___npcTalk(p, n, "you have no business here", "leave...now");
                                                        break;
                                                    case 1 :
                                                        Functions.___playerTalk(p, n, "do you know the carnilleans?");
                                                        Functions.___npcTalk(p, n, "i'll mind my business you mind yours");
                                                        Functions.___playerTalk(p, n, "look i know you're hiding something", "i've heard there's a cult hideout down here");
                                                        Functions.___npcTalk(p, n, "if you know what's best for you you'll leave now");
                                                        Functions.___playerTalk(p, n, "i have my orders");
                                                        Functions.___npcTalk(p, n, "so that two faced cold hearted snob has got to you too has he?");
                                                        Functions.___playerTalk(p, n, "ceril carnillean is a decent man");
                                                        Functions.___npcTalk(p, n, "there's a lot more than meets the eye to the carnilleans", "and none of it's decent");
                                                        p.updateQuestStage(quest, 2);
                                                        int menu = // do not send over
                                                        Functions.___showMenu(p, n, false, "What do you mean?", "I've heard enough of your rubbish");
                                                        if (menu == 0) {
                                                            Functions.___playerTalk(p, n, "what do you mean?");
                                                            Functions.___npcTalk(p, n, "the carnillean family house does not belong to them", "it's original owner was lord hazeel", "hazeel was one of the mahjarrat followers of zamorak", "The carnilleans harassed hazeel and his family for decades", "then one night they stormed hazeel's home", "one by one they tortured and then butchered him and his family", "the next day the carnillean forefathers moved into the property", "they've lived there on hazeel's wealth ever since");
                                                            Functions.___playerTalk(p, n, "ardounge history and pollitics are not my concern", "i've been asked to do a job and i plan to carry it through");
                                                            Functions.___npcTalk(p, n, "well now i'm asking you to do a job", "hazeel is going to return my friend", "those who aid his journey will gain rewards", "help us avenge hazeel's spirit so he may return");
                                                            int chooseSideMenu = // do not send over
                                                            Functions.___showMenu(p, n, false, "You're crazy, i'd never help you", "So what would i have to do?");
                                                            if (chooseSideMenu == 0) {
                                                                // GOOD SIDE;
                                                                Functions.___playerTalk(p, n, "You're crazy, i'd never help you");
                                                                Functions.___npcTalk(p, n, "then you're a fool", "go back to your adventures traveller");
                                                                Functions.___message(p, "clivet boards the raft and pushes of down the sewer system");
                                                                n.remove();
                                                                Functions.___message(p, "you hear him call out", "@yel@clivet:you'll never find us...");
                                                                // SET GOOD SIDE;
                                                                p.updateQuestStage(quest, 3);
                                                                p.getCache().store("good_side", true);
                                                            } else
                                                                if (chooseSideMenu == 1) {
                                                                    // EVIL SIDE;
                                                                    Functions.___playerTalk(p, n, "so what would i have to do?");
                                                                    Functions.___npcTalk(p, n, "first you must prove your loyalty to the cause", "you must kill one of the carnillean family members", "then we will know who's side you're really on", "so will you do it?");
                                                                    int whichSideMenu = // do not send over
                                                                    Functions.___showMenu(p, n, false, "No i won't do it", "Ok i'll do it");
                                                                    if (whichSideMenu == 0) {
                                                                        Functions.___playerTalk(p, n, "no i won't do it");
                                                                        Functions.___npcTalk(p, n, "then you're a fool", "go back to your adventures traveller");
                                                                        Functions.___message(p, "clivet boards the raft and pushes of down the sewer system");
                                                                        n.remove();
                                                                        Functions.___message(p, "you hear him call out", "@yel@clivet:you'll never find us...");
                                                                        // SET GOOD SIDE;
                                                                        p.updateQuestStage(quest, 3);
                                                                        p.getCache().store("good_side", true);
                                                                    } else
                                                                        if (whichSideMenu == 1) {
                                                                            Functions.___playerTalk(p, n, "ok, i'll do it");
                                                                            Functions.___npcTalk(p, n, "good, few see through the carnillean lies", "but i guessed you were of stronger character", "here take this poison, pour it into one of their meals", "once the deed is done return here");
                                                                            Functions.addItem(p, ItemId.POISON.id(), 1);
                                                                            p.updateQuestStage(quest, 3);
                                                                            p.getCache().store("evil_side", true);
                                                                        }

                                                                }

                                                        } else
                                                            if (menu == 1) {
                                                                Functions.___playerTalk(p, n, "I've heard enough of your rubbish");
                                                                Functions.___npcTalk(p, n, "then leave, fool");
                                                            }

                                                        break;
                                                    case 2 :
                                                        Functions.___playerTalk(p, n, "hello");
                                                        Functions.___npcTalk(p, n, "so you\'ve returned\"", "now do you want to know the truth about the carnilleans?");
                                                        int menu2 = // do not send over
                                                        Functions.___showMenu(p, n, false, "What do you mean?", "I've heard enough of your rubbish");
                                                        if (menu2 == 0) {
                                                            Functions.___playerTalk(p, n, "what do you mean?");
                                                            Functions.___npcTalk(p, n, "the carnillean family house does not belong to them", "it's original owner was lord hazeel", "hazeel was one of the mahjarrat followers of zamorak", "The carnilleans harassed hazeel and his family for decades", "then one night they stormed hazeel's home", "one by one they tortured and then butchered him and his family", "the next day the carnillean forefathers moved into the property", "they've lived there on hazeel's wealth ever since");
                                                            Functions.___playerTalk(p, n, "ardounge history and pollitics are not my concern", "i've been asked to do a job and i plan to carry it through");
                                                            Functions.___npcTalk(p, n, "well now i'm asking you to do a job", "hazeel is going to return my friend", "those who aid his journey will gain rewards", "help us avenge hazeel's spirit so he may return");
                                                            int chooseSideMenu = // do not send over
                                                            Functions.___showMenu(p, n, false, "You're crazy, i'd never help you", "So what would i have to do?");
                                                            if (chooseSideMenu == 0) {
                                                                // GOOD SIDE;
                                                                Functions.___playerTalk(p, n, "You're crazy, i'd never help you");
                                                                Functions.___npcTalk(p, n, "then you're a fool", "go back to your adventures traveller");
                                                                Functions.___message(p, "the man jumps onto a small raft", "and pushes off down the sewer system");
                                                                n.remove();
                                                                Functions.___message(p, "you hear him call out", "@yel@clivet:you'll never find us...");
                                                                // SET GOOD SIDE;
                                                                p.updateQuestStage(quest, 3);
                                                                p.getCache().store("good_side", true);
                                                            } else
                                                                if (chooseSideMenu == 1) {
                                                                    // EVIL SIDE;
                                                                    Functions.___playerTalk(p, n, "so what would i have to do?");
                                                                    Functions.___npcTalk(p, n, "first you must prove your loyalty to the cause", "you must kill one of the carnillean family members", "then we will know who's side you're really on", "so will you do it?");
                                                                    int whichSideMenu = // do not send over
                                                                    Functions.___showMenu(p, n, false, "No i won't do it", "Ok i'll do it");
                                                                    if (whichSideMenu == 0) {
                                                                        Functions.___playerTalk(p, n, "no i won't do it");
                                                                        Functions.___npcTalk(p, n, "then you're a fool", "go back to your adventures traveller");
                                                                        Functions.___message(p, "clivet boards the raft and pushes of down the sewer system");
                                                                        n.remove();
                                                                        Functions.___message(p, "you hear him call out", "@yel@clivet:you'll never find us...");
                                                                        // SET GOOD SIDE;
                                                                        p.updateQuestStage(quest, 3);
                                                                        p.getCache().store("good_side", true);
                                                                    } else
                                                                        if (whichSideMenu == 1) {
                                                                            Functions.___playerTalk(p, n, "ok, i'll do it");
                                                                            Functions.___npcTalk(p, n, "good, few see through the carnillean lies", "but i guessed you were of stronger character", "here take this poison, pour it into one of their meals", "once the deed is done return here");
                                                                            Functions.addItem(p, ItemId.POISON.id(), 1);
                                                                            p.updateQuestStage(quest, 3);
                                                                            p.getCache().store("evil_side", true);
                                                                        }

                                                                }

                                                        } else
                                                            if (menu2 == 1) {
                                                                Functions.___playerTalk(p, n, "I've heard enough of your rubbish");
                                                                Functions.___npcTalk(p, n, "then leave, fool");
                                                            }

                                                        break;
                                                    case 3 :
                                                        if (p.getCache().hasKey("good_side")) {
                                                            Functions.___playerTalk(p, n, "hello there");
                                                            Functions.___npcTalk(p, n, "oh not you again");
                                                            Functions.___playerTalk(p, n, "where is the cult hideout?");
                                                            Functions.___npcTalk(p, n, "you're a fool if you think you'll ever find it", "soon hazeel will return and you'll be punished");
                                                            return null;
                                                        } else
                                                            if (p.getCache().hasKey("evil_side")) {
                                                                Functions.___playerTalk(p, n, "hello there");
                                                                Functions.___npcTalk(p, n, "traveller you have a mission", "go to the carnillean house and poison their meal");
                                                                return null;
                                                            }

                                                        break;
                                                    case 4 :
                                                        if (p.getCache().hasKey("good_side")) {
                                                            Functions.___playerTalk(p, n, "hello");
                                                            Functions.___npcTalk(p, n, "You again! I warned you to keep away", "hazeel will punish you for your interference");
                                                            return null;
                                                        } else
                                                            if (p.getCache().hasKey("evil_side")) {
                                                                if (!Functions.hasItem(p, ItemId.MARK_OF_HAZEEL.id())) {
                                                                    Functions.___playerTalk(p, n, "hello", "I poured the poison into the carnillean's meal as requested");
                                                                    Functions.___npcTalk(p, n, "yes we have people on the inside who informed me of your deed", "hazeel will reward you for your loyalty");
                                                                    Functions.___playerTalk(p, n, "ok, so what's next?");
                                                                    Functions.___npcTalk(p, n, "first you must wear the sign of hazeel");
                                                                    Functions.___message(p, "clivet hands you a small metal amulet");
                                                                    Functions.addItem(p, ItemId.MARK_OF_HAZEEL.id(), 1);
                                                                    Functions.___npcTalk(p, n, "the amulet is proof to other cult members that you're one of us", "it is also the key to finding the cult hideout");
                                                                    Functions.___playerTalk(p, n, "in what way?");
                                                                    Functions.___npcTalk(p, n, "the flow of the sewer's are controlled by 5 sewer valves above", "turn them correctly and the sewer will carry you to the hideout", "the sign of hazeel is your guide - you must begin at the tail", "The cult leader alomone shall be expecting you");
                                                                } else {
                                                                    Functions.___playerTalk(p, n, "hello");
                                                                    Functions.___npcTalk(p, n, "hello traveller", "have you found the cult hideout yet?");
                                                                    Functions.___playerTalk(p, n, "not yet im afraid");
                                                                    Functions.___npcTalk(p, n, "hurry! soon hazeel will return");
                                                                }
                                                                return null;
                                                            }

                                                        break;
                                                    case 5 :
                                                        if (p.getCache().hasKey("good_side")) {
                                                            Functions.___playerTalk(p, n, "hello");
                                                            Functions.___npcTalk(p, n, "You again! I warned you to keep away", "hazeel will punish you for your interference");
                                                            return null;
                                                        } else
                                                            if (p.getCache().hasKey("evil_side")) {
                                                                Functions.___playerTalk(p, n, "hello");
                                                                Functions.___npcTalk(p, n, "hello traveller", "all we need now is the sacred script of hazeel", "once we have that Hazeel can return");
                                                                return null;
                                                            }

                                                        break;
                                                    case 6 :
                                                        Functions.___playerTalk(p, n, "hello again");
                                                        Functions.___npcTalk(p, n, "have you managed to find the script of hazeel?");
                                                        if (Functions.hasItem(p, ItemId.SCRIPT_OF_HAZEEL.id())) {
                                                            Functions.___playerTalk(p, n, "yes, i found it in the house");
                                                            Functions.___npcTalk(p, n, "amazing, the last piece", "now the time has come to change history and avenge lord hazeel", "take the script to alomone as quick as you can");
                                                        } else {
                                                            Functions.___playerTalk(p, n, "errm, no, i misplaced it");
                                                            Functions.___npcTalk(p, n, "go to the house and don't return until you have the script");
                                                        }
                                                        break;
                                                    case -1 :
                                                        if (p.getCache().hasKey("good_side")) {
                                                            Functions.___playerTalk(p, n, "hello");
                                                            Functions.___npcTalk(p, n, "You again! I warned you to keep away", "bother someone else", "go find some goblins to hack up");
                                                        } else
                                                            if (p.getCache().hasKey("evil_side")) {
                                                                Functions.___playerTalk(p, n, "hello");
                                                                Functions.___npcTalk(p, n, "It's good to see you again", "i am patiently waiting for for hazeel to call upon me");
                                                            }

                                                        break;
                                                }
                                            } else
                                                if (n.getID() == NpcId.CULT_MEMBER.id()) {
                                                    switch (p.getQuestStage(quest)) {
                                                        case 0 :
                                                        case 1 :
                                                        case 2 :
                                                            Functions.___playerTalk(p, n, "hello");
                                                            Functions.___npcTalk(p, n, "what how did you get in here?", "leave now traveller");
                                                            break;
                                                        case 3 :
                                                            if (p.getCache().hasKey("good_side")) {
                                                                Functions.___playerTalk(p, n, "hello there");
                                                                Functions.___npcTalk(p, n, "what, how did you get in here", "leave now traveller");
                                                                n.setChasing(p);
                                                                return null;
                                                            } else
                                                                if (p.getCache().hasKey("evil_side")) {
                                                                    Functions.___playerTalk(p, n, "hello there");
                                                                    Functions.___npcTalk(p, n, "can't you see i'm busy", "the great hazeel shall return soon");
                                                                    return null;
                                                                }

                                                            break;
                                                        case 4 :
                                                            if (p.getCache().hasKey("good_side")) {
                                                                Functions.___playerTalk(p, n, "hello");
                                                                Functions.___npcTalk(p, n, "what, an outsider", "how did you get in here", "you must leave, now");
                                                                n.setChasing(p);
                                                                return null;
                                                            } else
                                                                if (p.getCache().hasKey("evil_side")) {
                                                                    Functions.___playerTalk(p, n, "hi");
                                                                    Functions.___npcTalk(p, n, "hello, oh, are you new");
                                                                    Functions.___playerTalk(p, n, "that's right");
                                                                    Functions.___npcTalk(p, n, "well it's good to have you on board", "soon we should retrieved the sacred hazeel script", "then at last we can bring are lord back from the dead");
                                                                    return null;
                                                                }

                                                            break;
                                                        case 5 :
                                                            if (p.getCache().hasKey("good_side")) {
                                                                Functions.___playerTalk(p, n, "hello");
                                                                Functions.___npcTalk(p, n, "what, an outsider", "how did you get in here", "you must leave, now");
                                                                n.setChasing(p);
                                                                return null;
                                                            } else
                                                                if (p.getCache().hasKey("evil_side")) {
                                                                    Functions.___playerTalk(p, n, "hello");
                                                                    Functions.___npcTalk(p, n, "hello there", "untill we have the hazeel script", "we cannot summon our master");
                                                                    return null;
                                                                }

                                                            break;
                                                        case 6 :
                                                            Functions.___playerTalk(p, n, "hello");
                                                            Functions.___npcTalk(p, n, "you truly are our savior", "you found the script of hazeel", "now his injustice can be resolved");
                                                            break;
                                                        case -1 :
                                                            if (p.getCache().hasKey("good_side")) {
                                                                Functions.___playerTalk(p, n, "hello");
                                                                Functions.___npcTalk(p, n, "An outsider!", "how did you get in here?", "leave now fool or die");
                                                                n.setChasing(p);
                                                            } else
                                                                if (p.getCache().hasKey("evil_side")) {
                                                                    Functions.___playerTalk(p, n, "hello");
                                                                    Functions.___npcTalk(p, n, "the traveller returns", "we are forever in your dept brave adventurer", "i bow before you");
                                                                }

                                                            break;
                                                    }
                                                } else
                                                    if (n.getID() == NpcId.ALOMONE.id()) {
                                                        switch (p.getQuestStage(quest)) {
                                                            case 0 :
                                                            case 1 :
                                                            case 2 :
                                                                Functions.___playerTalk(p, n, "hello");
                                                                Functions.___npcTalk(p, n, "what, an intruder", "kill him");
                                                                Npc cults = Functions.getNearestNpc(p, NpcId.CULT_MEMBER.id(), 20);
                                                                cults.setChasing(p);
                                                                break;
                                                            case 3 :
                                                                if (p.getCache().hasKey("good_side")) {
                                                                    Functions.___npcTalk(p, n, "How did get you get in here?");
                                                                    Functions.___playerTalk(p, n, "I've come for the carnillean family armour");
                                                                    Functions.___npcTalk(p, n, "I thought I told the butler to get rid of you", "he must be going soft");
                                                                    Functions.___playerTalk(p, n, "so the butler is working for you too?", "Why's it always the Butler? I should have guessed");
                                                                    Functions.sleep(1900);
                                                                    n.setChasing(p);
                                                                } else
                                                                    if (p.getCache().hasKey("evil_side")) {
                                                                        Functions.___playerTalk(p, n, "hello");
                                                                        Functions.___npcTalk(p, n, "Can't you see I'm busy?");
                                                                        return null;
                                                                    }

                                                                break;
                                                            case 4 :
                                                                if (p.getCache().hasKey("good_side")) {
                                                                    Functions.___playerTalk(p, n, "hello alomone");
                                                                    Functions.___npcTalk(p, n, "out of my way", "can't you see we're busy here?");
                                                                    n.setChasing(p);
                                                                    return null;
                                                                } else
                                                                    if (p.getCache().hasKey("evil_side")) {
                                                                        Functions.___playerTalk(p, n, "hi there");
                                                                        Functions.___npcTalk(p, n, "well well, we have a new recruit", "Clivet told me about your willingness to prove yourself", "we must retrieve the sacred script of hazeel", "From the Carnillean house", "an ancient spell which if read over Hazeel's grave", "will bring him back to this world", "the Carnilleans aren't aware of it's existence", "we have eyes in the house", "Butler Jones is one of us", "go back to the house and try to find the script");
                                                                        p.updateQuestStage(quest, 5);
                                                                        return null;
                                                                    }

                                                                break;
                                                            case 5 :
                                                                if (p.getCache().hasKey("good_side")) {
                                                                    Functions.___playerTalk(p, n, "hello");
                                                                    Functions.___npcTalk(p, n, "out of my way", "can't you see we're busy here?");
                                                                    return null;
                                                                } else
                                                                    if (p.getCache().hasKey("evil_side")) {
                                                                        Functions.___playerTalk(p, n, "hello alomone");
                                                                        Functions.___npcTalk(p, n, "hazeel has waited long enough traveller", "the sooner you find the hazeel script the better");
                                                                        return null;
                                                                    }

                                                                break;
                                                            case 6 :
                                                                /**
                                                                 * COMPLETE EVIL SIDE *
                                                                 */
                                                                Functions.___playerTalk(p, n, "hello");
                                                                Functions.___npcTalk(p, n, "Do you have the sacred script of hazeel?");
                                                                if (Functions.hasItem(p, ItemId.SCRIPT_OF_HAZEEL.id())) {
                                                                    Functions.___playerTalk(p, n, "yes I have it here");
                                                                    Functions.___npcTalk(p, n, "finally our lord hazeel can return");
                                                                    Functions.___message(p, "alomone takes the hazeel script");
                                                                    Functions.removeItem(p, ItemId.SCRIPT_OF_HAZEEL.id(), 1);
                                                                    Functions.___npcTalk(p, n, "with these words our lord will return and save us all", "come with me adventurer and let the ceromony begin");
                                                                    p.teleport(580, 3419);
                                                                    n.teleport(580, 3419);
                                                                    Functions.___npcTalk(p, n, "I do this for you lord hazeel and all followers of zamorak");
                                                                    Functions.___message(p, "alomone kneels down infront of the shrine", "he begins to read the script");
                                                                    p.message("the language is something you have never heard");
                                                                    Functions.___message(p, "alomone reads on", "Alomone finishes the script", "the room is silent", "suddenly a shrill scream comes from the coffin of hazeel", "A shadowy figure appears");
                                                                    Npc lord_hazeel = Functions.spawnNpc(p.getWorld(), NpcId.LORD_HAZEEL.id(), 580, 3420, 60000 * 2);
                                                                    ActionSender.sendTeleBubble(p, 580, 3420, true);
                                                                    for (Player pe : p.getViewArea().getPlayersInView()) {
                                                                        ActionSender.sendTeleBubble(pe, 580, 3420, true);
                                                                    }
                                                                    lord_hazeel.face(p);
                                                                    p.face(lord_hazeel);
                                                                    Functions.___message(p, "the cult begin to chant");
                                                                    Functions.___npcTalk(p, lord_hazeel, "my followers i am proud of you all", "I never expected to retun to these lands", "I can see I have much to attend to", "In due time you will all be rewarded for your part", "brave adventurer, i believe your contribution was the most critical", "i owe you much, you may not be a follower of the great zamorak", "but you understand injustice and anger", "for this I certainly shall call upon your help in the future", "my people gain strength day to day", "you would be wise to join us while you can");
                                                                    Functions.___playerTalk(p, lord_hazeel, "I fight for myself");
                                                                    Functions.___npcTalk(p, lord_hazeel, "hmm, fair enough for now", "I shall reward you with money", "But your reward of Zamorak's approval is far greater");
                                                                    p.sendQuestComplete(Quests.THE_HAZEEL_CULT);
                                                                    Functions.___npcTalk(p, lord_hazeel, "now i must leave you", "i have much business to attend to with my brothers in the north", "i will see you all again but be aware", "soon much blood will be spilt over runescape");
                                                                    if (lord_hazeel != null) {
                                                                        lord_hazeel.remove();
                                                                    }
                                                                    ActionSender.sendTeleBubble(p, 580, 3420, true);
                                                                    for (Player pe : p.getViewArea().getPlayersInView()) {
                                                                        ActionSender.sendTeleBubble(pe, 580, 3420, true);
                                                                    }
                                                                } else {
                                                                    Functions.___playerTalk(p, n, "i'm afraid not");
                                                                    Functions.___npcTalk(p, n, "we need the script if hazeel is to return");
                                                                }
                                                                break;
                                                            case -1 :
                                                                if (p.getCache().hasKey("good_side")) {
                                                                    Functions.___playerTalk(p, n, "hello again");
                                                                    Functions.___npcTalk(p, n, "leave here now intruder", "before i loose my patience");
                                                                } else
                                                                    if (p.getCache().hasKey("evil_side")) {
                                                                        Functions.___playerTalk(p, n, "hello again");
                                                                        Functions.___npcTalk(p, n, "we wait patiently for lord hazeel's calling");
                                                                        Functions.___playerTalk(p, n, "ok, take care");
                                                                    }

                                                                break;
                                                        }
                                                    }








                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return n.getID() == NpcId.ALOMONE.id();
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.ALOMONE.id()) {
                        if (p.getCache().hasKey("good_side")) {
                            n.killedBy(p);
                            if (!Functions.hasItem(p, ItemId.CARNILLEAN_ARMOUR.id())) {
                                Functions.___message(p, "you have killed alomone", "lying behind his corpse", "you see the carnillean family armour", "you place it in your bag");
                                Functions.addItem(p, ItemId.CARNILLEAN_ARMOUR.id(), 1);
                                if (p.getQuestStage(quest) == 3) {
                                    p.updateQuestStage(quest, 4);
                                }
                            }
                        } else {
                            n.killedBy(p);
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return DataConversions.inArray(new int[]{ HazeelCult.BUTLERS_CUPBOARD_OPEN, HazeelCult.BUTLERS_CUPBOARD_CLOSED, HazeelCult.BASEMENT_CRATE, HazeelCult.TOP_LEVEL_BOOKCASE, HazeelCult.CARNILLEAN_CHEST_CLOSED }, obj.getID());
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
        final QuestInterface quest = this;
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == HazeelCult.BUTLERS_CUPBOARD_OPEN) || (obj.getID() == HazeelCult.BUTLERS_CUPBOARD_CLOSED)) {
                        if (command.equalsIgnoreCase("open")) {
                            Functions.openCupboard(obj, player, HazeelCult.BUTLERS_CUPBOARD_OPEN);
                        } else
                            if (command.equalsIgnoreCase("close")) {
                                Functions.closeCupboard(obj, player, HazeelCult.BUTLERS_CUPBOARD_CLOSED);
                            } else {
                                Functions.___message(player, "you search the cupboard");
                                if ((player.getQuestStage(quest) == 5) && player.getCache().hasKey("good_side")) {
                                    Functions.___message(player, "you find a bottle of poison", "and a strange amulet", "you pass your finds to ceril");
                                    Npc ceril = Functions.getNearestNpc(player, NpcId.CERIL.id(), 10);
                                    Functions.___playerTalk(player, ceril, "look what i've found?");
                                    Functions.___npcTalk(player, ceril, "what's this for jones?");
                                    Functions.___message(player, "ceril takes the bottle");
                                    Functions.___npcTalk(player, ceril, "i don't believe it, it's poison");
                                    Npc butler = Functions.getNearestNpc(player, NpcId.BUTLER.id(), 10);
                                    Functions.___npcTalk(player, butler, "mr carnillean, it's for the rats", "i'm just a loyal servent");
                                    Functions.___npcTalk(player, ceril, "i've seen this amulet before", "the thieves that broke in", "one of them  was wearing exactly the same amulet", "jones i don't believe it", "we trusted you");
                                    Functions.___npcTalk(player, butler, "that's because you're an old fool ceril", "I should have got rid of you and your family weeks ago");
                                    Functions.___message(player, "ceril calls for the guards");
                                    Functions.___npcTalk(player, butler, "don't worry ceril", "we'll make sure you and your family pay");
                                    Functions.___npcTalk(player, ceril, "looks like i owe you an apology traveller");
                                    Functions.___playerTalk(player, ceril, "that's ok, we all make mistakes");
                                    Functions.___npcTalk(player, ceril, "if it wasn't for you he could have poisoned my whole family", "i'm sorry for the way i spoke to you", "the least i can do is give you a proper reward");
                                    player.sendQuestComplete(Quests.THE_HAZEEL_CULT);
                                    Functions.___playerTalk(player, ceril, "thanks ceril");
                                    Functions.___npcTalk(player, ceril, "thankyou, you're welcome here any time traveller");
                                } else {
                                    Functions.___message(player, "but find nothing");
                                }
                            }

                    } else
                        if (obj.getID() == HazeelCult.BASEMENT_CRATE) {
                            Functions.___message(player, "you search the crate");
                            if ((player.getQuestStage(quest) == 5) && player.getCache().hasKey("evil_side")) {
                                if (!Functions.hasItem(player, ItemId.CARNILLEAN_KEY.id())) {
                                    player.message("under the food packages");
                                    player.message("you find an old rusty key");
                                    Functions.addItem(player, ItemId.CARNILLEAN_KEY.id(), 1);
                                } else {
                                    player.message("but find nothing");
                                }
                            } else {
                                player.message("but find nothing");
                            }
                        } else
                            if (obj.getID() == HazeelCult.TOP_LEVEL_BOOKCASE) {
                                Functions.___message(player, "you search the book case");
                                if ((player.getQuestStage(quest) == 5) && player.getCache().hasKey("evil_side")) {
                                    Functions.___message(player, "as you pull out one of the books", "the shelves slide to the side", "revealing a secret passage", "you walk through");
                                    player.teleport(614, 2504);
                                    Functions.___message(player, "the passage leads upwards", "to an empty room");
                                } else {
                                    player.message("but find nothing interesting");
                                }
                            } else
                                if (obj.getID() == HazeelCult.CARNILLEAN_CHEST_CLOSED) {
                                    player.message("the chest is locked");
                                }



                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
        return (obj.getID() == HazeelCult.CARNILLEAN_CHEST_CLOSED) && (item.getID() == ItemId.CARNILLEAN_KEY.id());
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player player) {
        final QuestInterface quest = this;
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == HazeelCult.CARNILLEAN_CHEST_CLOSED) && (item.getID() == ItemId.CARNILLEAN_KEY.id())) {
                        player.message("you use the key to open");
                        player.message("the chest");
                        Functions.replaceObjectDelayed(obj, HazeelCult.CARNILLEAN_CHEST_OPEN, 437);
                        player.message("inside the chest you find the sacred script of hazeel");
                        Functions.addItem(player, ItemId.SCRIPT_OF_HAZEEL.id(), 1);
                        if (player.getQuestStage(quest) == 5) {
                            player.updateQuestStage(quest, 6);
                        }
                    }
                    return null;
                });
            }
        };
    }
}

