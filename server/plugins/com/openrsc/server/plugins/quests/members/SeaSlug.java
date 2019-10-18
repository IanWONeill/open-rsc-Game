package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class SeaSlug implements QuestInterface , ObjectActionListener , PickupListener , TalkToNpcListener , WallObjectActionListener , ObjectActionExecutiveListener , PickupExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    @Override
    public int getQuestId() {
        return Quests.SEA_SLUG;
    }

    @Override
    public String getQuestName() {
        return "Sea Slug (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        p.message("@gre@You haved gained 1 quest point!");
        Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.SEA_SLUG), true);
        p.message("well done, you have completed the sea slug quest");
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return DataConversions.inArray(new int[]{ NpcId.CAROLINE.id(), NpcId.HOLGART_LAND.id(), NpcId.HOLGART_PLATFORM.id(), NpcId.HOLGART_ISLAND.id(), NpcId.KENNITH.id(), NpcId.KENT.id(), NpcId.PLATFORM_FISHERMAN_GOLDEN.id(), NpcId.PLATFORM_FISHERMAN_PURPLE.id(), NpcId.PLATFORM_FISHERMAN_GRAY.id(), NpcId.BAILEY.id() }, n.getID());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.KENT.id()) {
                        switch (p.getQuestStage(quest)) {
                            case 4 :
                                Functions.___npcTalk(p, n, "oh thank Saradomin", "i thought i would be left out here forever");
                                Functions.___playerTalk(p, n, "your wife sent me out to find you and your boy", "kennith's fine he's on the platform");
                                Functions.___npcTalk(p, n, "i knew the row boat wasn't sea worthy", "i couldn't risk bringing him along but you must get him of that platform");
                                Functions.___playerTalk(p, n, "what's going on on there?");
                                Functions.___npcTalk(p, n, "five days ago we pulled in huge catch", "as well as fish we caught small slug like sea creatures, hundreds of them", "that's when the fishermen began to act strange", "it was the sea slugs, they attach themselves to your body", "and somehow take over the mind of the carrier", "i told Kennith to hide until i returned but i was washed up here", "please go back and get my boy", "you can send help for me later", "traveler wait!");
                                Functions.___message(p, "kent reaches behind your neck", "slooop", "he pulls a sea slug from under your top");
                                Functions.___npcTalk(p, n, "a few more minutes and that thing would have full control you body");
                                Functions.___playerTalk(p, n, "yuck..thanks kent");
                                p.updateQuestStage(getQuestId(), 5);
                                break;
                            case 5 :
                                Functions.___playerTalk(p, n, "hello");
                                Functions.___npcTalk(p, n, "oh my", "i must get back to shore");
                                break;
                        }
                    } else
                        if (n.getID() == NpcId.KENNITH.id()) {
                            switch (p.getQuestStage(quest)) {
                                case 3 :
                                    Functions.___playerTalk(p, n, "are you okay young one?");
                                    Functions.___npcTalk(p, n, "no i want my daddy");
                                    Functions.___playerTalk(p, n, "Where is your father?");
                                    Functions.___npcTalk(p, n, "he went to get help days ago", "the nasty fisher men tried to throw me and daddy into the sea", "so he told me to hide in here");
                                    Functions.___playerTalk(p, n, "that's good advice", "you stay here and i'll go try and find your father");
                                    p.updateQuestStage(getQuestId(), 4);
                                    break;
                                case 4 :
                                    Functions.___playerTalk(p, n, "are you okay?");
                                    Functions.___npcTalk(p, n, "i want to see daddy");
                                    Functions.___playerTalk(p, n, "i'm working on it");
                                    break;
                                case 5 :
                                    if (p.getCache().hasKey("loose_panel")) {
                                        Functions.___playerTalk(p, n, "kennith i've made an opening in the wall", "you can come out there");
                                        Functions.___npcTalk(p, n, "are their any sea slugs on the other side?");
                                        Functions.___playerTalk(p, n, "not one");
                                        Functions.___npcTalk(p, n, "how will i get down stairs");
                                        Functions.___playerTalk(p, n, "i'll figure that out in a moment");
                                        Functions.___npcTalk(p, n, "okay, when you have i'll come out");
                                        return null;
                                    }
                                    Functions.___playerTalk(p, n, "hello kennith", "are you okay?");
                                    Functions.___npcTalk(p, n, "no i want my daddy");
                                    Functions.___playerTalk(p, n, "you'll be able to see him soon", "first we need to get you back to land", "come with me to the boat");
                                    Functions.___npcTalk(p, n, "no");
                                    Functions.___playerTalk(p, n, "what, why not?");
                                    Functions.___npcTalk(p, n, "i'm scared of those nasty sea slugs", "i won't go near them");
                                    Functions.___playerTalk(p, n, "okay, you wait here and i'll figure another way to get you out");
                                    break;
                                case -1 :
                                    Functions.___message(p, "He doesn't seem interested in talking");
                            }
                        } else
                            if (n.getID() == NpcId.BAILEY.id()) {
                                switch (p.getQuestStage(quest)) {
                                    case 3 :
                                    case 4 :
                                        Functions.___playerTalk(p, n, "hello");
                                        Functions.___npcTalk(p, n, "well hello there", "what are you doing here?");
                                        Functions.___playerTalk(p, n, "i'm trying to find out what happened to a boy named kennith");
                                        Functions.___npcTalk(p, n, "oh, you mean kent's son", "he's around somewhere, probably hiding");
                                        Functions.___playerTalk(p, n, "hiding from what?");
                                        Functions.___npcTalk(p, n, "haven't you seen all those things out there?");
                                        Functions.___playerTalk(p, n, "the sea slugs?");
                                        Functions.___npcTalk(p, n, "ever since we pulled up that haul something strange has been going on", "the fishermen spend all day pulling in hauls of fish", "only to throw back the fish and keep those nasty sea slugs", "what am i supposed to do with those", "i haven't figured out how to kill one yet", "if i put them near the stove they squirm and jump away");
                                        Functions.___playerTalk(p, n, "i doubt they would taste too good");
                                        break;
                                    case 5 :
                                        if (!p.getCache().hasKey("lit_torch")) {
                                            Functions.___playerTalk(p, n, "hello");
                                            Functions.___npcTalk(p, n, "oh thank god it's you", "they've all gone mad i tell you", "one of the fishermen tried to throw me into the sea");
                                            Functions.___playerTalk(p, n, "they're all being controlled by the sea slugs");
                                            Functions.___npcTalk(p, n, "i figured as much");
                                            Functions.___playerTalk(p, n, "i need to get kennith of this platform but i can't get past the fishermen");
                                            Functions.___npcTalk(p, n, "the sea slugs are scared of heat", "i figured that out when i tried to cook them");
                                            if (!Functions.hasItem(p, ItemId.UNLIT_TORCH.id())) {
                                                Functions.___npcTalk(p, n, "here");
                                                Functions.___message(p, "bailey gives you a torch");
                                                Functions.addItem(p, ItemId.UNLIT_TORCH.id(), 1);
                                                Functions.___npcTalk(p, n, "i doubt the fishermen will come near you if you can get this torch to light", "the only problem is all the wood and flint is damp", "i can't light a thing");
                                            } else {
                                                Functions.___playerTalk(p, n, "i better figure a way to light this torch");
                                            }
                                        } else {
                                            if (Functions.hasItem(p, ItemId.LIT_TORCH.id())) {
                                                Functions.___playerTalk(p, n, "i've managed to light the torch");
                                                Functions.___npcTalk(p, n, "well done traveler", "you better get kennith out of here soon", "the fishermen are becoming stranger by the minute", "and they keep pulling up those blasted sea slugs");
                                            } else
                                                if (Functions.hasItem(p, ItemId.UNLIT_TORCH.id())) {
                                                    // nothing
                                                } else {
                                                    Functions.___playerTalk(p, n, "i've managed to lose my torch");
                                                    Functions.___npcTalk(p, n, "that was silly, fortunately i have another", "here, take it");
                                                    Functions.addItem(p, ItemId.UNLIT_TORCH.id(), 1);
                                                }

                                        }
                                        break;
                                    case 6 :
                                        Functions.___playerTalk(p, n, "hello bailey");
                                        Functions.___npcTalk(p, n, "hello again", "i saw you managed to get kennith of the platform", "well done, he wasn't safe around these slugs");
                                        Functions.___playerTalk(p, n, "are you going to come back with us?");
                                        Functions.___npcTalk(p, n, "no, these fishermen are my friends", "i'm sure they can be saved", "i'm going to stay and try to get rid of all these slugs");
                                        Functions.___playerTalk(p, n, "you're braver than most", "take care of yourself bailey");
                                        Functions.___npcTalk(p, n, "you to traveler");
                                        break;
                                    case -1 :
                                        Functions.___playerTalk(p, n, "hello bailey");
                                        Functions.___npcTalk(p, n, "well hello again traveler", "what brings you back out here");
                                        Functions.___playerTalk(p, n, "just looking around");
                                        Functions.___npcTalk(p, n, "well don't go touching any of those blasted slugs");
                                        break;
                                }
                            } else
                                if ((n.getID() == NpcId.PLATFORM_FISHERMAN_PURPLE.id()) || (n.getID() == NpcId.PLATFORM_FISHERMAN_GRAY.id())) {
                                    Functions.___playerTalk(p, n, "hello there");
                                    p.message("his eyes are fixated");
                                    p.message("starring at the sea");
                                    Functions.___npcTalk(p, n, "must find family");
                                    Functions.___playerTalk(p, n, "what?");
                                    Functions.___npcTalk(p, n, "soon we'll all be together");
                                    Functions.___playerTalk(p, n, "are you okay?");
                                    Functions.___npcTalk(p, n, "must find family", "they're all under the blue", "deep deep under the blue");
                                    Functions.___playerTalk(p, n, "ermm..i'll leave you to it then");
                                } else
                                    if (n.getID() == NpcId.PLATFORM_FISHERMAN_GOLDEN.id()) {
                                        Functions.___playerTalk(p, n, "hello");
                                        p.message("his eyes are fixated");
                                        p.message("starring at the sea");
                                        Functions.___npcTalk(p, n, "keep away human", "leave or face the deep blue");
                                        Functions.___playerTalk(p, n, "pardon?");
                                        Functions.___npcTalk(p, n, "you'll all end up in the blue", "deep deep under the blue");
                                    } else
                                        if (n.getID() == NpcId.CAROLINE.id()) {
                                            switch (p.getQuestStage(quest)) {
                                                case 0 :
                                                    Functions.___playerTalk(p, n, "hello there");
                                                    Functions.___npcTalk(p, n, "is there any chance you could help me?");
                                                    Functions.___playerTalk(p, n, "what's wrong?");
                                                    Functions.___npcTalk(p, n, "it's my husband, he works on a fishing platform", "once a month he takes our son kennith out with him", "they usually write to me regularly but i've heard nothing all week", "it's very strange");
                                                    Functions.___playerTalk(p, n, "maybe the post was lost!");
                                                    Functions.___npcTalk(p, n, "maybe, but no one's heard from the other fishermen on the platform", "their families are becoming quite concerned", "is there any chance you could visit the platform and find out what's going on?");
                                                    int firstMenu = Functions.___showMenu(p, n, "i suppose so, how do i get there?", "i'm sorry i'm too busy");
                                                    if (firstMenu == 0) {
                                                        Functions.___npcTalk(p, n, "that's very good of you traveller", "my friend holgart will take you there");
                                                        Functions.___playerTalk(p, n, "okay i'll go and see if they're ok");
                                                        Functions.___npcTalk(p, n, "i will reward you for your time", "and it'll give me great piece of mind", "to know kennith and my husband kent are safe");
                                                        p.updateQuestStage(getQuestId(), 1);
                                                    } else
                                                        if (firstMenu == 1) {
                                                            Functions.___npcTalk(p, n, "thats a shame");
                                                            Functions.___playerTalk(p, n, "bye");
                                                            Functions.___npcTalk(p, n, "bye");
                                                        }

                                                    break;
                                                case 1 :
                                                case 2 :
                                                case 3 :
                                                case 4 :
                                                case 5 :
                                                    Functions.___playerTalk(p, n, "hello caroline");
                                                    Functions.___npcTalk(p, n, "brave adventurer have you any news about my son and his father?");
                                                    Functions.___playerTalk(p, n, "i'm working on it now caroline");
                                                    Functions.___npcTalk(p, n, "please bring them back safe and sound");
                                                    Functions.___playerTalk(p, n, "i'll do my best");
                                                    break;
                                                case 6 :
                                                    Functions.___playerTalk(p, n, "hello");
                                                    Functions.___npcTalk(p, n, "brave adventurer you've returned", "kennith told me about the strange going ons on the platform", "i had no idea it was so serious", "i could have lost my son and my husband if it wasn't for you");
                                                    Functions.___playerTalk(p, n, "we found kent stranded on a island");
                                                    Functions.___npcTalk(p, n, "yes, holgart told me and sent a rescue party out", "kent's back at home now, resting with kennith", "i don't think he'll be doing any fishing for a while", "here, take these oyster pearls as a reward", "they're worth a fair bit", "and can be used to make lethal crossbow bolts");
                                                    p.sendQuestComplete(Quests.SEA_SLUG);
                                                    Functions.___playerTalk(p, n, "thanks");
                                                    Functions.___npcTalk(p, n, "thank you", "take care of yourself adventurer");
                                                    Functions.addItem(p, ItemId.QUEST_OYSTER_PEARLS.id(), 1);
                                                    break;
                                                case -1 :
                                                    Functions.___playerTalk(p, n, "hello again");
                                                    Functions.___npcTalk(p, n, "hello traveler", "how are you?");
                                                    Functions.___playerTalk(p, n, "not bad thanks, yourself?");
                                                    Functions.___npcTalk(p, n, "i'm good", "busy as always looking after kent and kennith but no complaints");
                                                    break;
                                            }
                                        } else
                                            if (((n.getID() == NpcId.HOLGART_LAND.id()) || (n.getID() == NpcId.HOLGART_PLATFORM.id())) || (n.getID() == NpcId.HOLGART_ISLAND.id())) {
                                                /* Holgart */
                                                switch (p.getQuestStage(quest)) {
                                                    case 0 :
                                                        Functions.___playerTalk(p, n, "hello there");
                                                        Functions.___npcTalk(p, n, "well hello m'laddy", "beautiful day isn't it");
                                                        Functions.___playerTalk(p, n, "not bad i suppose");
                                                        Functions.___npcTalk(p, n, "just smell that sea air... beautiful");
                                                        Functions.___playerTalk(p, n, "hmm...lovely!");
                                                        break;
                                                    case 1 :
                                                        Functions.___playerTalk(p, n, "hello");
                                                        Functions.___npcTalk(p, n, "hello m'hearty");
                                                        Functions.___playerTalk(p, n, "i would like a ride on your boat to the fishing platform");
                                                        Functions.___npcTalk(p, n, "i'm afraid it isn't sea worthy, it's full of holes", "to fill the holes i'll need some swamp paste");
                                                        Functions.___playerTalk(p, n, "swamp paste?");
                                                        Functions.___npcTalk(p, n, "yes, swamp tar mixed with flour heated over a fire");
                                                        Functions.___playerTalk(p, n, "where can i find swamp tar?");
                                                        Functions.___npcTalk(p, n, "unfortunately the only supply of swamp tar is in the swamps below lumbridge", "it's too far for an old man like me to travel", "if you can make me some swamp paste i will give you a ride on my boat");
                                                        Functions.___playerTalk(p, n, "i'll see what i can do");
                                                        p.updateQuestStage(getQuestId(), 2);
                                                        break;
                                                    case 2 :
                                                        Functions.___playerTalk(p, n, "hello holgart");
                                                        Functions.___npcTalk(p, n, "hello m'hearty", "did you manage to make some swamp paste?");
                                                        if (Functions.removeItem(p, ItemId.SWAMP_PASTE.id(), 1)) {
                                                            Functions.___playerTalk(p, n, "yes i have some here");
                                                            p.message("you give holgart the swamp paste");
                                                            Functions.___npcTalk(p, n, "superb, this looks great");
                                                            p.message("holgart smears the paste over the under side of his boat");
                                                            Functions.___npcTalk(p, n, "that's done the job, now we can go", "jump aboard");
                                                            p.updateQuestStage(getQuestId(), 3);
                                                            int boatMenu = Functions.___showMenu(p, n, "i'll come back later", "okay, lets do it");
                                                            if (boatMenu == 0) {
                                                                Functions.___npcTalk(p, n, "okay then", "i'll wait here for you");
                                                            } else
                                                                if (boatMenu == 1) {
                                                                    Functions.___npcTalk(p, n, "hold on tight");
                                                                    Functions.___message(p, "you board the small row boat", "you arrive at the fishing platform");
                                                                    p.teleport(495, 618, false);
                                                                }

                                                        } else {
                                                            Functions.___playerTalk(p, n, "i'm afraid not");
                                                            Functions.___npcTalk(p, n, "to make it you need swamp tar mixed with flour heated over a fire", "the only supply of swamp tar is in the swamps below lumbridge", "i can't fix the row boat without it");
                                                            Functions.___playerTalk(p, n, "ok, i'll try to find some");
                                                        }
                                                        break;
                                                    case 3 :
                                                        if (p.getLocation().inArdougne()) {
                                                            Functions.___playerTalk(p, n, "hello holgart");
                                                            Functions.___npcTalk(p, n, "hello again land lover", "there's some strange going's on, on that platform i tell you");
                                                            int goMenu = // do not send over
                                                            Functions.___showMenu(p, n, false, "will you take me there?", "i'm keeping away from there");
                                                            if (goMenu == 0) {
                                                                Functions.___playerTalk(p, n, "will you take me back there?");
                                                                Functions.___npcTalk(p, n, "of course m'hearty", "if that's what you want");
                                                                Functions.___message(p, "you board the small row boat");
                                                                checkTorchCrossing(p);
                                                                Functions.___message(p, "you arrive at the fishing platform");
                                                                p.teleport(495, 618, false);
                                                            } else
                                                                if (goMenu == 1) {
                                                                    Functions.___playerTalk(p, n, "i'm keeping away from there");
                                                                    Functions.___npcTalk(p, n, "fair enough m'hearty");
                                                                }

                                                        } else {
                                                            Functions.___playerTalk(p, n, "hey holgart");
                                                            Functions.___npcTalk(p, n, "have you had enough of this place yet?", "it's scaring me");
                                                            int goBack = Functions.___showMenu(p, n, "no, i'm going to stay a while", "okay, lets go back");
                                                            if (goBack == 0) {
                                                                Functions.___npcTalk(p, n, "okay, you're the boss");
                                                            } else
                                                                if (goBack == 1) {
                                                                    Functions.___npcTalk(p, n, "okay m'hearty jump on");
                                                                    Functions.___message(p, "you arrive back on shore");
                                                                    p.teleport(515, 613, false);
                                                                }

                                                        }
                                                        break;
                                                    case 4 :
                                                        if (p.getLocation().inPlatformArea()) {
                                                            Functions.___playerTalk(p, n, "holgart, something strange is going on here");
                                                            Functions.___npcTalk(p, n, "you're telling me", "none of the sailors seem to remember who i am");
                                                            Functions.___playerTalk(p, n, "apparently kenniths father left for help a couple of days ago");
                                                            Functions.___npcTalk(p, n, "that's a worry, no ones heard from him on shore", "come on, we better go look for him");
                                                            Functions.___message(p, "you board the row boat", "you arrive on a small island");
                                                            p.teleport(512, 639, false);
                                                        } else
                                                            if (p.getLocation().inArdougne()) {
                                                                Functions.___playerTalk(p, n, "hello holgart");
                                                                Functions.___npcTalk(p, n, "hello again land lover", "there's some strange going's on, on that platform i tell you");
                                                                int goMenu = // do not send over
                                                                Functions.___showMenu(p, n, false, "will you take me there?", "i'm keeping away from there");
                                                                if (goMenu == 0) {
                                                                    Functions.___playerTalk(p, n, "will you take me back there?");
                                                                    Functions.___npcTalk(p, n, "of course m'hearty", "if that's what you want");
                                                                    Functions.___message(p, "you board the small row boat");
                                                                    checkTorchCrossing(p);
                                                                    Functions.___message(p, "you arrive at the fishing platform");
                                                                    p.teleport(495, 618, false);
                                                                } else
                                                                    if (goMenu == 1) {
                                                                        Functions.___playerTalk(p, n, "i'm keeping away from there");
                                                                        Functions.___npcTalk(p, n, "fair enough m'hearty");
                                                                    }

                                                            } else {
                                                                // kents island
                                                                Functions.___playerTalk(p, n, "where are we?");
                                                                Functions.___npcTalk(p, n, "someway of mainland still", "you better see if old matey's okay");
                                                            }

                                                        break;
                                                    case 5 :
                                                        if (p.getLocation().inPlatformArea()) {
                                                            Functions.___playerTalk(p, n, "hey holgart");
                                                            Functions.___npcTalk(p, n, "have you had enough of this place yet?", "it's scaring me");
                                                            int goBack = Functions.___showMenu(p, n, "no, i'm going to stay a while", "okay, lets go back");
                                                            if (goBack == 0) {
                                                                Functions.___npcTalk(p, n, "okay, you're the boss");
                                                            } else
                                                                if (goBack == 1) {
                                                                    Functions.___npcTalk(p, n, "okay m'hearty jump on");
                                                                    Functions.___message(p, "you arrive back on shore");
                                                                    p.teleport(515, 613, false);
                                                                }

                                                        } else
                                                            if (p.getLocation().inArdougne()) {
                                                                Functions.___playerTalk(p, n, "hello holgart");
                                                                Functions.___npcTalk(p, n, "hello again land lover", "there's some strange going's on, on that platform i tell you");
                                                                int goMenu = // do not send over
                                                                Functions.___showMenu(p, n, false, "will you take me there?", "i'm keeping away from there");
                                                                if (goMenu == 0) {
                                                                    Functions.___playerTalk(p, n, "will you take me back there?");
                                                                    Functions.___npcTalk(p, n, "of course m'hearty", "if that's what you want");
                                                                    Functions.___message(p, "you board the small row boat");
                                                                    checkTorchCrossing(p);
                                                                    Functions.___message(p, "you arrive at the fishing platform");
                                                                    p.teleport(495, 618, false);
                                                                } else
                                                                    if (goMenu == 1) {
                                                                        Functions.___playerTalk(p, n, "i'm keeping away from there");
                                                                        Functions.___npcTalk(p, n, "fair enough m'hearty");
                                                                    }

                                                            } else {
                                                                // kents island to fishing platform
                                                                Functions.___playerTalk(p, n, "we had better get back to the platform", "and see what's going on");
                                                                Functions.___npcTalk(p, n, "you're right", "it all sounds pretty creepy");
                                                                Functions.___message(p, "you arrive back at the fishing platform");
                                                                p.teleport(495, 618, false);
                                                            }

                                                        break;
                                                    case 6 :
                                                        if (p.getLocation().inPlatformArea()) {
                                                            Functions.___playerTalk(p, n, "did you get the kid back to shore?");
                                                            Functions.___npcTalk(p, n, "yes, he's safe and sound with his parents", "your turn to return to land now adventurer");
                                                            Functions.___playerTalk(p, n, "looking forward to it");
                                                            p.message("you board the small row boat");
                                                            p.message("you arrive back on shore");
                                                            p.teleport(515, 613, false);
                                                        } else {
                                                            Functions.___playerTalk(p, n, "hello holgart");
                                                            Functions.___npcTalk(p, n, "hello again land lover", "there's some strange going's on, on that platform i tell you");
                                                            int goMenu = // do not send over
                                                            Functions.___showMenu(p, n, false, "will you take me there?", "i'm keeping away from there");
                                                            if (goMenu == 0) {
                                                                Functions.___playerTalk(p, n, "will you take me back there?");
                                                                Functions.___npcTalk(p, n, "of course m'hearty", "if that's what you want");
                                                                Functions.___message(p, "you board the small row boat");
                                                                checkTorchCrossing(p);
                                                                Functions.___message(p, "you arrive at the fishing platform");
                                                                p.teleport(495, 618, false);
                                                            } else
                                                                if (goMenu == 1) {
                                                                    Functions.___playerTalk(p, n, "i'm keeping away from there");
                                                                    Functions.___npcTalk(p, n, "fair enough m'hearty");
                                                                }

                                                        }
                                                        break;
                                                    case -1 :
                                                        if (p.getLocation().inArdougne()) {
                                                            Functions.___playerTalk(p, n, "hello again holgart");
                                                            Functions.___npcTalk(p, n, "well hello again m'hearty", "your land loving legs getting bored?", "fancy some cold and wet underfoot?");
                                                            Functions.___playerTalk(p, n, "pardon");
                                                            Functions.___npcTalk(p, n, "fancy going out to sea?");
                                                            int goMenu = Functions.___showMenu(p, n, "i'll come back later", "okay lets do it");
                                                            if (goMenu == 0) {
                                                                Functions.___npcTalk(p, n, "okay then", "i'll wait here for you");
                                                            }
                                                            if (goMenu == 1) {
                                                                Functions.___npcTalk(p, n, "hold on tight");
                                                                Functions.___message(p, "you board the small row boat");
                                                                checkTorchCrossing(p);
                                                                Functions.___message(p, "you arrive at the fishing platform");
                                                                p.teleport(495, 618, false);
                                                            }
                                                        } else {
                                                            Functions.___playerTalk(p, n, "hey holgart");
                                                            Functions.___npcTalk(p, n, "have you had enough of this place yet?", "it's scaring me");
                                                            int goBack = Functions.___showMenu(p, n, "no, i'm going to stay a while", "okay, lets go back");
                                                            if (goBack == 0) {
                                                                Functions.___npcTalk(p, n, "okay, you're the boss");
                                                            } else
                                                                if (goBack == 1) {
                                                                    Functions.___npcTalk(p, n, "okay m'hearty jump on");
                                                                    Functions.___message(p, "you arrive back on shore");
                                                                    p.teleport(515, 613, false);
                                                                }

                                                        }
                                                        break;
                                                }
                                            }






                    return null;
                });
            }
        };
    }

    public void checkTorchCrossing(Player p) {
        if (Functions.hasItem(p, ItemId.LIT_TORCH.id())) {
            p.getInventory().replace(ItemId.LIT_TORCH.id(), ItemId.UNLIT_TORCH.id());
            Functions.___message(p, "your torch goes out on the crossing");
        }
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return i.getID() == ItemId.SEASLUG.id();
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ItemId.SEASLUG.id()) {
                        int damage = DataConversions.random(1, 9);
                        p.message("you pick up the seaslug");
                        p.message("it sinks its teeth deep into you hand");
                        p.damage(damage);
                        Functions.___playerTalk(p, null, "ouch");
                        p.message("you drop the sea slug");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return (obj.getID() == 458) || (obj.getID() == 453);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 458) {
                        if (p.getQuestStage(getQuestId()) < 5) {
                            Functions.___message(p, "You climb up the ladder");
                            p.teleport(494, 1561, false);
                            return null;
                        }
                        if (p.getQuestStage(getQuestId()) >= 5) {
                            if (!Functions.hasItem(p, ItemId.LIT_TORCH.id())) {
                                int damage = DataConversions.random(7, 8);
                                p.message("You attempt to climb up the ladder");
                                p.message("the fishermen approach you");
                                p.message("and throw you back down the ladder");
                                p.damage(damage);
                                Functions.___playerTalk(p, null, "ouch");
                            } else {
                                Functions.___message(p, "You climb up the ladder");
                                p.teleport(494, 1561, false);
                                p.message("the fishermen seem afraid of your torch");
                            }
                        }
                    } else
                        if (obj.getID() == 453) {
                            if (p.getQuestStage(getQuestId()) == 5) {
                                Functions.___message(p, "you rotate the crane around", "to the far platform");
                                GameObject firstRotation = new GameObject(obj.getWorld(), obj.getLocation(), 453, 5, 0);
                                p.getWorld().replaceGameObject(obj, firstRotation);
                                Functions.sleep(500);
                                GameObject secondRotation = new GameObject(obj.getWorld(), obj.getLocation(), 453, 6, 0);
                                p.getWorld().replaceGameObject(obj, secondRotation);
                                Functions.___playerTalk(p, null, "jump on kennith!");
                                p.message("kennith comes out through the broken panal");
                                GameObject thirdRotation = new GameObject(obj.getWorld(), obj.getLocation(), 453, 5, 0);
                                p.getWorld().replaceGameObject(obj, thirdRotation);
                                Functions.sleep(500);
                                GameObject fourthRotation = new GameObject(obj.getWorld(), obj.getLocation(), 453, 4, 0);
                                p.getWorld().replaceGameObject(obj, fourthRotation);
                                Functions.___message(p, "he climbs onto the fishing net", "you rotate the crane back around", "and lower kennith to the row boat waiting below");
                                p.updateQuestStage(getQuestId(), 6);
                                p.getCache().remove("loose_panel");
                                p.getCache().remove("lit_torch");
                            } else
                                if ((p.getQuestStage(getQuestId()) > 0) && (p.getQuestStage(getQuestId()) < 5)) {
                                    Functions.___message(p, "you rotate the crane around");
                                } else {
                                    p.message("Nothing interesting happens");
                                }

                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
        return obj.getID() == 124;
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 124) {
                        if (p.getQuestStage(getQuestId()) == 5) {
                            Functions.___message(p, "you kick the loose panel", "the wood is rotten and crumbles away", "leaving an opening big enough for kennith to climb through");
                            p.getCache().store("loose_panel", true);
                        } else {
                            Functions.___message(p, "you kick the loose panal", "nothing interesting happens");
                        }
                    }
                    return null;
                });
            }
        };
    }
}

