package com.openrsc.server.plugins.quests.free;


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
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class PrinceAliRescue implements QuestInterface , InvUseOnNpcListener , InvUseOnWallObjectListener , TalkToNpcListener , WallObjectActionListener , InvUseOnNpcExecutiveListener , InvUseOnWallObjectExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    @Override
    public int getQuestId() {
        return Quests.PRINCE_ALI_RESCUE;
    }

    @Override
    public String getQuestName() {
        return "Prince Ali rescue";
    }

    @Override
    public boolean isMembers() {
        return false;
    }

    @Override
    public void handleReward(final Player p) {
        if (p.getCache().hasKey("pre_paid")) {
            Functions.___message(p, "The chancellor pays you 620 coins");
            Functions.addItem(p, ItemId.COINS.id(), 620);
            p.getCache().remove("pre_paid");
        } else {
            Functions.___message(p, "The chancellor pays you 700 coins");
            Functions.addItem(p, ItemId.COINS.id(), 700);
        }
        p.message("You have completed the quest of the Prince of Al Kharid");
        Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.PRINCE_ALI_RESCUE), true);
        p.message("@gre@You haved gained 3 quest points!");
    }

    @Override
    public boolean blockInvUseOnNpc(final Player player, final Npc npc, final Item item) {
        return (npc.getID() == NpcId.LADY_KELI.id()) && (item.getID() == ItemId.ROPE.id());
    }

    @Override
    public boolean blockInvUseOnWallObject(final GameObject obj, final Item item, final Player player) {
        return ((obj.getID() == 45) && (obj.getY() == 640)) && (item.getID() == ItemId.BRONZE_KEY.id());
    }

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        // AGGIE -> separate file
        // NED -> separate file
        return DataConversions.inArray(new int[]{ NpcId.LADY_KELI.id(), NpcId.HASSAN.id(), NpcId.OSMAN.id(), NpcId.JOE.id(), NpcId.LEELA.id(), NpcId.PRINCE_ALI.id(), NpcId.JAILGUARD.id() }, n.getID());
    }

    @Override
    public boolean blockWallObjectAction(final GameObject obj, final Integer click, final Player player) {
        return (obj.getID() == 45) && (obj.getY() == 640);
    }

    private void hassanDialogue(final Player p, final Npc n) {
        switch (p.getQuestStage(this)) {
            case 0 :
                Functions.___npcTalk(p, n, "Greetings. I am Hassan, Chancellor to the Emir of Al Kharid");
                final int choice = Functions.___showMenu(p, n, "Can I help you? You must need some help here in the desert.", "Its just too hot here. How can you stand it?", "Do you mind if I just kill your Warriors?");
                if (choice == 0) {
                    Functions.___npcTalk(p, n, "I need the services of someone, yes.", "If you are interested, see the spymaster, Osman", "I manage the finances here. come to me when you need payment");
                    p.updateQuestStage(this, 1);// yepp

                } else
                    if (choice == 1) {
                        Functions.___npcTalk(p, n, "We manage, in our humble way. We are a wealthy town", "And we have water. It cures many thirsts");
                        p.message("The chancellor hands you some water");
                        Functions.addItem(p, ItemId.BUCKET_OF_WATER.id(), 1);
                    } else
                        if (choice == 2) {
                            Functions.___npcTalk(p, n, "You are welcome. They are not expensive.", "We have them here to stop the elite guard being bothered", "They are a little harder to kill.");
                        }


                break;
            case 1 :
                Functions.___npcTalk(p, n, "Have you found the spymaster, Osman, Yet?", "You cannot proceed in your task without reporting to him");
                break;
            case 2 :
                if (!p.getCache().hasKey("key_made")) {
                    Functions.___npcTalk(p, n, "I understand the spymaster has hired you", "I will pay the reward only when the Prince is rescued", "I can pay some expenses once the spymaster approves it");
                } else {
                    if (!p.getCache().hasKey("pre_paid")) {
                        Functions.___npcTalk(p, n, "You have proved your services useful to us", "Here is 80 coins for the work you have already done");
                        Functions.___message(p, "The chancellor hands you 80 coins");
                        Functions.addItem(p, ItemId.COINS.id(), 80);
                        p.getCache().store("pre_paid", true);
                    } else {
                        Functions.___npcTalk(p, n, "Hello again adventurer", "You have received payment for your tasks so far", "No more will be paid until the Prince is rescued");
                    }
                }
                break;
            case 3 :
                if (p.getCache().hasKey("key_made")) {
                    p.getCache().remove("key_made");
                }
                if (p.getCache().hasKey("pre_paid")) {
                    Functions.___npcTalk(p, n, "You have the eternal gratitude of the Emir for rescuing his son", "I am authorised to pay you 700 coins", "80 was put aside for the key. that leaves 620");
                } else {
                    Functions.___npcTalk(p, n, "You have the eternal gratitude of the Emir for rescuing his son", "I am authorised to pay you 700 coins");
                }
                p.sendQuestComplete(getQuestId());
                break;
            case -1 :
                Functions.___npcTalk(p, n, "You are a friend of the town of Al Kharid", "If we have more tasks to complete, we will ask you.", "Please, keep in contact. Good employees are not easy to find");
                break;
        }
    }

    private void jailGuardDialogue(final Player p, final Npc n) {
        Functions.___playerTalk(p, n, "Hi, who are you guarding here?");
        Functions.___npcTalk(p, n, "Can't say, all very secret. you should get out of here", "I am not supposed to talk while I guard");
        final int choice = Functions.___showMenu(p, n, "Hey, chill out, I won't cause you trouble", "I had better leave, I don't want trouble");
        if (choice == 0) {
            Functions.___playerTalk(p, n, "I was just wondering what you do to relax");
            Functions.___npcTalk(p, n, "You never relax with these people, but its a good career for a young man");
        } else
            if (choice == 1) {
                Functions.___npcTalk(p, n, "Thanks I appreciate that", "Talking on duty can be punishable by having your mouth stitched up", "These are tough people, no mistake");
            }

    }

    private void joeDialogue(final Player p, final Npc n, final int cID) {
        if (cID == (-1)) {
            switch (p.getQuestStage(this)) {
                case 0 :
                    Functions.___npcTalk(p, n, "Hi, I'm Joe, door guard for Lady Keli");
                    Functions.___playerTalk(p, n, "Hi, who are you guarding here?");
                    Functions.___npcTalk(p, n, "Can't say, all very secret. you should get out of here", "I am not supposed to talk while I guard");
                    final int choice = Functions.___showMenu(p, n, "Hey, chill out, I won't cause you trouble", "Tell me about the life of a guard", "What did you want to be when you were a boy", "I had better leave, I don't want trouble");
                    if (choice == 0) {
                        joeDialogue(p, n, PrinceAliRescue.Joe.CHILL);
                    } else
                        if (choice == 1) {
                            joeDialogue(p, n, PrinceAliRescue.Joe.LIFE);
                        } else
                            if (choice == 2) {
                                joeDialogue(p, n, PrinceAliRescue.Joe.BOY);
                            } else
                                if (choice == 3) {
                                    joeDialogue(p, n, PrinceAliRescue.Joe.BETTERLEAVE);
                                }



                    break;
                case 2 :
                    if (p.getCache().hasKey("joe_is_drunk")) {
                        Functions.___npcTalk(p, n, "Halt. Who goes there?");
                        Functions.___playerTalk(p, n, "Hello friend, i am just rescuing the prince, is that ok?");
                        Functions.___npcTalk(p, n, "Thatsh a funny joke. You are lucky i am shober", "Go in peace, friend");
                        return;
                    }
                    Functions.___npcTalk(p, n, "Hi, I'm Joe, door guard for Lady Keli");
                    Functions.___playerTalk(p, n, "Hi, who are you guarding here?");
                    Functions.___npcTalk(p, n, "Can't say, all very secret, you should get out of here", "I am not supposed to talk while I guard");
                    final int menu2 = Functions.___showMenu(p, n, "I have some beer here, fancy one?", "Tell me about the life of a guard", "What did you want to be when you were a boy", "I had better leave, I don't want trouble");
                    if (menu2 == 0) {
                        joeDialogue(p, n, PrinceAliRescue.Joe.BEER);
                    } else
                        if (menu2 == 1) {
                            joeDialogue(p, n, PrinceAliRescue.Joe.LIFE);
                        } else
                            if (menu2 == 2) {
                                joeDialogue(p, n, PrinceAliRescue.Joe.BOY);
                            } else
                                if (menu2 == 3) {
                                    joeDialogue(p, n, PrinceAliRescue.Joe.BETTERLEAVE);
                                }



                    break;
                case 3 :
                case -1 :
                    Functions.___playerTalk(p, n, "Hi friend, i am just checking out things here");
                    Functions.___npcTalk(p, n, "The prince got away, i am in trouble", "I better not talk to you, they are not sure i was drunk");
                    Functions.___playerTalk(p, n, "I won't say anything, your secret is safe with me");
                    break;
            }
            return;
        }
        switch (cID) {
            case PrinceAliRescue.Joe.BEER :
                if (p.getInventory().countId(ItemId.BEER.id()) == 0) {
                    Functions.___npcTalk(p, n, "Ah, that would be lovely, just one now, just to wet my throat");
                    Functions.___playerTalk(p, n, "Of course, it must be tough being here without a drink", "Oh dear seems like I don't have any beer");
                } else
                    if ((p.getInventory().countId(ItemId.BEER.id()) >= 1) && (p.getInventory().countId(ItemId.BEER.id()) <= 2)) {
                        Functions.___npcTalk(p, n, "Ah, that would be lovely, just one now, just to wet my throat");
                        Functions.___playerTalk(p, n, "Of course, it must be tough being here without a drink");
                        Functions.___message(p, "You hand a beer to the guard, he drinks it in seconds");
                        Functions.removeItem(p, ItemId.BEER.id(), 1);
                        Functions.___npcTalk(p, n, "Thas was perfect, i cant thank you enough");
                        Functions.___playerTalk(p, n, "How are you? still ok. Not too drunk?");
                        Functions.___npcTalk(p, n, "No, I don't get drunk with only one drink", "You would need a few to do that, but thanks for the beer");
                    } else
                        if (p.getInventory().countId(ItemId.BEER.id()) >= 3) {
                            Functions.___npcTalk(p, n, "Ah, that would be lovely, just one now, just to wet my throat");
                            Functions.___playerTalk(p, n, "Of course, it might be tough being here without a drink");
                            Functions.___message(p, "You hand a beer to the guard, he drinks it in seconds");
                            Functions.removeItem(p, ItemId.BEER.id(), 1);// takes 2 more after dialogue

                            Functions.___npcTalk(p, n, "Thas was perfect, i cant thank you enough");
                            Functions.___playerTalk(p, n, "Would you care for another, my friend?");
                            Functions.___npcTalk(p, n, "I better not, I don't want to be drunk on duty");
                            Functions.___playerTalk(p, n, "Here, just keep these for later, I hate to see a thirsty guard");
                            Functions.___message(p, "You hand two more beers to the guard");
                            p.getInventory().remove(ItemId.BEER.id(), 2);
                            Functions.___message(p, "he takes a sip of one, and then he drinks them both");
                            Functions.___npcTalk(p, n, "Franksh, that wash just what I need to shtay on guard", "No more beersh, i don't want to get drunk");
                            p.message("The guard is drunk, and no longer a problem");
                            p.getCache().store("joe_is_drunk", true);
                        }


                break;
            case PrinceAliRescue.Joe.CHILL :
                Functions.___playerTalk(p, n, "I was just wondering what you do to relax");
                Functions.___npcTalk(p, n, "You never relax with these people, but its a good career for a young man", "And some of the shouting I rather like", "RESISTANCE IS USELESS!");
                final int choice = Functions.___showMenu(p, n, "So what do you buy with these great wages?", "Tell me about the life of a guard", "Would you be interested in making a little more money?", "I had better leave, I don't want trouble");
                if (choice == 0) {
                    joeDialogue(p, n, PrinceAliRescue.Joe.BUYWITH);
                } else
                    if (choice == 1) {
                        joeDialogue(p, n, PrinceAliRescue.Joe.LIFE);
                    } else
                        if (choice == 2) {
                            joeDialogue(p, n, PrinceAliRescue.Joe.MOREMONEY);
                        } else
                            if (choice == 3) {
                                joeDialogue(p, n, PrinceAliRescue.Joe.BETTERLEAVE);
                            }



                break;
            case PrinceAliRescue.Joe.BUYWITH :
                Functions.___npcTalk(p, n, "Really, after working here, theres only time for a drink or three", "All us guards go to the same bar, And drink ourselves stupid", "Its what I enjoy these days, that fade into unconciousness", "I can't resist the sight of a really cold beer");
                final int choice1 = Functions.___showMenu(p, n, "Tell me about the life of a guard", "What did you want to be when you were a boy", "I had better leave, I don't want trouble");
                if (choice1 == 0) {
                    joeDialogue(p, n, PrinceAliRescue.Joe.LIFE);
                } else
                    if (choice1 == 1) {
                        joeDialogue(p, n, PrinceAliRescue.Joe.BOY);
                    } else
                        if (choice1 == 2) {
                            joeDialogue(p, n, PrinceAliRescue.Joe.BETTERLEAVE);
                        }


                break;
            case PrinceAliRescue.Joe.MOREMONEY :
                Functions.___npcTalk(p, n, "WHAT! are you trying to bribe me?", "I may not be a great guard, but I am loyal", "How DARE you try to bribe me!");
                Functions.___playerTalk(p, n, "No,no, you got the wrong idea, totally", "I just wondered if you wanted some part-time bodyguard work");
                Functions.___npcTalk(p, n, "Oh. sorry. no, I don't need money", "As long as you were not offering me a bribe");
                final int choice11 = Functions.___showMenu(p, n, "Tell me about the life of a guard", "What did you want to be when you were a boy", "I had better leave, I don't want trouble");
                if (choice11 == 0) {
                    joeDialogue(p, n, PrinceAliRescue.Joe.LIFE);
                } else
                    if (choice11 == 1) {
                        joeDialogue(p, n, PrinceAliRescue.Joe.BOY);
                    } else
                        if (choice11 == 2) {
                            joeDialogue(p, n, PrinceAliRescue.Joe.BETTERLEAVE);
                        }


                break;
            case PrinceAliRescue.Joe.LIFE :
                Functions.___npcTalk(p, n, "Well, the hours are good.....", ".... But most of those hours are a drag", "If only I had spent more time in Knight school when I was a young boy", "Maybe I wouldn't be here now, scared of Keli");
                final int choice2 = Functions.___showMenu(p, n, "Hey chill out, I won't cause you trouble", "What did you want to be when you were a boy", "I had better leave, I don't want trouble");
                if (choice2 == 0) {
                    joeDialogue(p, n, PrinceAliRescue.Joe.CHILL);
                } else
                    if (choice2 == 1) {
                        joeDialogue(p, n, PrinceAliRescue.Joe.BOY);
                    } else
                        if (choice2 == 2) {
                            joeDialogue(p, n, PrinceAliRescue.Joe.BETTERLEAVE);
                        }


                break;
            case PrinceAliRescue.Joe.BOY :
                Functions.___npcTalk(p, n, "Well, I loved to sit by the lake, with my toes in the water", "And shoot the fish with my bow and arrow");
                Functions.___playerTalk(p, n, "That was a strange hobby for a little boy");
                Functions.___npcTalk(p, n, "It kept us from goblin hunting, which was what most boys did", "What are you here for?");
                final int choice3 = Functions.___showMenu(p, n, "Hey chill out, I won't cause you trouble", "Tell me about the life of a guard", "I had better leave, I don't want trouble");
                if (choice3 == 0) {
                    joeDialogue(p, n, PrinceAliRescue.Joe.CHILL);
                } else
                    if (choice3 == 1) {
                        joeDialogue(p, n, PrinceAliRescue.Joe.LIFE);
                    } else
                        if (choice3 == 2) {
                            joeDialogue(p, n, PrinceAliRescue.Joe.BETTERLEAVE);
                        }


                break;
            case PrinceAliRescue.Joe.BETTERLEAVE :
                Functions.___npcTalk(p, n, "Thanks I appreciate that", "Talking on duty can be punishable by having your mouth stitched up", "These are tough people, no mistake");
                break;
        }
    }

    private void keliDialogue(final Player p, final Npc n, final int cID) {
        if (cID == (-1)) {
            switch (p.getQuestStage(this)) {
                case 0 :
                case 1 :
                case 2 :
                    Functions.___playerTalk(p, n, "Are you the famous Lady Keli?", "Leader of the toughest gang of mercenary killers around?");
                    Functions.___npcTalk(p, n, "I am Keli, you have heard of me then");
                    final int choice = Functions.___showMenu(p, n, "Heard of you? you are famous in Runescape!", "I have heard a little, but I think Katrine is tougher", "I have heard rumours that you kill people", "No I have never really heard of you");
                    if (choice == 0) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.FAMOUS);
                    } else
                        if (choice == 1) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.KATRINE);
                        } else
                            if (choice == 2) {
                                keliDialogue(p, n, PrinceAliRescue.Keli.KILLPEOPLE);
                            } else
                                if (choice == 3) {
                                    keliDialogue(p, n, PrinceAliRescue.Keli.NEVERHEARD);
                                }



                    break;
                case 3 :
                case -1 :
                    Functions.___npcTalk(p, n, "You tricked me, and tied me up", "You should not stay here if you want to remain alive", "Guards! Guards! Kill this stranger");
                    break;
            }
        }
        switch (cID) {
            case PrinceAliRescue.Keli.FAMOUS :
                Functions.___npcTalk(p, n, "Thats very kind of you to say. Reputations are not easily earnt", "I have managed to succeed where many fail");
                final int choice = Functions.___showMenu(p, n, "I think Katrine is still tougher", "What is your latest plan then?", "You must have trained a lot for this work", "I should not disturb someone as tough as you");
                if (choice == 0) {
                    keliDialogue(p, n, PrinceAliRescue.Keli.KATRINE);
                } else
                    if (choice == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.PLAN);
                    } else
                        if (choice == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.TRAINED);
                        } else
                            if (choice == 3) {
                                keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                            }



                break;
            case PrinceAliRescue.Keli.KATRINE :
                Functions.___npcTalk(p, n, "Well you can think that all you like", "I know those blackarm cowards dare not leave the city", "Out here, I am toughest. You can tell them that!", "Now get out of my sight, before I call my guards");
                break;
            case PrinceAliRescue.Keli.NOT_DISTURB :
                Functions.___npcTalk(p, n, "I need to do a lot of work, goodbye", "When you get a little tougher, maybe I will give you a job");
                break;
            case PrinceAliRescue.Keli.PLAN :
                Functions.___playerTalk(p, n, "Of course, you need not go into specific details");
                Functions.___npcTalk(p, n, "Well, I can tell you, I have a valuable prisoner here in my cells", "I can expect a high reward to be paid very soon for this guy", "I can't tell you who he is, but he is a lot colder now");
                final int choice2 = Functions.___showMenu(p, n, "Ah, I see. You must have been very skilful", "Thats great, are you sure they will pay?", "Can you be sure they will not try to get him out?", "I should not disturb someone as tough as you");
                if (choice2 == 0) {
                    keliDialogue(p, n, PrinceAliRescue.Keli.SKILLFUL);
                } else
                    if (choice2 == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.GREAT);
                    } else
                        if (choice2 == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.BESURE);
                        } else
                            if (choice2 == 3) {
                                keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                            }



                break;
            case PrinceAliRescue.Keli.SKILLFUL :
                Functions.___npcTalk(p, n, "Yes, I did most of the work, we had to grab the Pr...", "er, we had to grab him under cover of ten of his bodyguards", "It was a stronke of genius");
                final int choice3 = Functions.___showMenu(p, n, "Are you sure they will pay?", "Can you be sure they will not try to get him out?", "I should not disturb someone as tough as you");
                if (choice3 == 0) {
                    keliDialogue(p, n, PrinceAliRescue.Keli.GREAT);
                } else
                    if (choice3 == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.BESURE);
                    } else
                        if (choice3 == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                        }


                break;
            case PrinceAliRescue.Keli.GREAT :
                Functions.___npcTalk(p, n, "They will pay, or we will cut his hair off and send it to them");
                Functions.___playerTalk(p, n, "Don't you think that something tougher, maybe cut his finger off?");
                Functions.___npcTalk(p, n, "Thats a good idea, I could use talented people like you", "I may call on you if I need work doing");
                final int choice4 = Functions.___showMenu(p, n, "You must have been very skilful", "Can you be sure they will not try to get him out?", "I should not disturb someone as tough as you");
                if (choice4 == 0) {
                    keliDialogue(p, n, PrinceAliRescue.Keli.SKILLFUL);
                } else
                    if (choice4 == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.BESURE);
                    } else
                        if (choice4 == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                        }


                break;
            case PrinceAliRescue.Keli.BESURE :
                Functions.___npcTalk(p, n, "There is no way to release him", "The only key to the door is on a chain around my neck", "And the locksmith who made the lock,", "died suddenly when he had finished", "There is not another key like this in the world");
                final int choice5 = Functions.___showMenu(p, n, "Could I see the key please", "That is a good way to keep secrets", "I should not disturb someone as tough as you");
                if (choice5 == 0) {
                    keliDialogue(p, n, PrinceAliRescue.Keli.KEYPLEASE);
                } else
                    if (choice5 == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.SECRETS);
                    } else
                        if (choice5 == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                        }


                break;
            case PrinceAliRescue.Keli.KEYPLEASE :
                Functions.___playerTalk(p, n, "It would be something I can tell my grandchildren", "When you are even more famous than you are now");
                Functions.___npcTalk(p, n, "As you put it that way, I am sure you can see it", "You cannot steal the key, it is on an Adamantite chain", "I cannot see the harm");
                p.message("Keli shows you a small key on a stronglooking chain");
                if ((p.getQuestStage(this) == 2) && Functions.hasItem(p, ItemId.SOFT_CLAY.id())) {
                    final int menu1 = Functions.___showMenu(p, n, "Could I touch the key for a moment please", "I should not disturb someone as tough as you");
                    if (menu1 == 0) {
                        Functions.___npcTalk(p, n, "Only for a moment then");
                        Functions.___message(p, "You put a piece of your soft clay in your hand", "As you touch the key, you take an imprint of it");
                        Functions.removeItem(p, ItemId.SOFT_CLAY.id(), 1);
                        Functions.addItem(p, ItemId.KEYPRINT.id(), 1);
                        Functions.___playerTalk(p, n, "Thankyou so much, you are too kind, o great Keli");
                        Functions.___npcTalk(p, n, "You are welcome, run along now, I am very busy");
                    } else
                        if (menu1 == 1) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                        }

                } else {
                    Functions.___npcTalk(p, n, "There, run along now, I am very busy");
                }
                break;
            case PrinceAliRescue.Keli.SECRETS :
                Functions.___npcTalk(p, n, "It is the best way I know", "Dead men tell no tales");
                Functions.___playerTalk(p, n, "I am glad I know none of your secrets, Keli");
                break;
            case PrinceAliRescue.Keli.TRAINED :
                Functions.___npcTalk(p, n, "I have used a sword since I was a small girl", "I stabbed three people before I was 6 years old");
                final int choice6 = Functions.___showMenu(p, n, "What is your latest plan then?", "You must have trained a lot for this work", "I think Katrine is still tougher");
                if (choice6 == 0) {
                    keliDialogue(p, n, PrinceAliRescue.Keli.PLAN);
                } else
                    if (choice6 == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.TRAINED);
                    } else
                        if (choice6 == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.KATRINE);// this is long

                        }


                break;
            case PrinceAliRescue.Keli.KILLPEOPLE :
                Functions.___npcTalk(p, n, "Theres always someone ready to spread rumours", "I heard a rumour the other day, that some men are wearing skirts", "If one of my men wore a skirt, he would wish he hadn't");
                final int choice7 = Functions.___showMenu(p, n, "I think Katrine is still tougher", "What is your latest plan then?", "You must have trained a lot for this work", "I should not disturb someone as tough as you");
                if (choice7 == 0) {
                    keliDialogue(p, n, PrinceAliRescue.Keli.KATRINE);
                } else
                    if (choice7 == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.PLAN);
                    } else
                        if (choice7 == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.TRAINED);
                        } else
                            if (choice7 == 3) {
                                keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                            }



                break;
            case PrinceAliRescue.Keli.NEVERHEARD :
                Functions.___npcTalk(p, n, "You must be new to this land then", "EVERYONE knows of Lady Keli and her prowess with the sword");
                final int choice8 = Functions.___showMenu(p, n, "No, still doesn't ring a bell", "Yes, of course I have heard of you", "You must have trained a lot for this work", "I should not disturb someone as tough as you");
                if (choice8 == 0) {
                    keliDialogue(p, n, PrinceAliRescue.Keli.NOBELL);
                } else
                    if (choice8 == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.OFCOURSE);
                    } else
                        if (choice8 == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.TRAINED);
                        } else
                            if (choice8 == 3) {
                                keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                            }



                break;
            case PrinceAliRescue.Keli.NOBELL :
                Functions.___npcTalk(p, n, "Well, you know of me now", "I will ring your bell if you do not show respect");
                final int choice9 = Functions.___showMenu(p, n, "I do not show respect to killers and hoodlums", "You must have trained a lot for this work", "I should not disturb someone as tough as you, great lady");
                if (choice9 == 0) {
                    Functions.___npcTalk(p, n, "You should, you really should", "I am wealthy enough to place a bounty on your head", "Or just remove your head myself", "Now go, I am busy, too busy to fight a would-be hoodlum");
                } else
                    if (choice9 == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.TRAINED);
                    } else
                        if (choice9 == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                        }


                break;
            case PrinceAliRescue.Keli.OFCOURSE :
                Functions.___playerTalk(p, n, "You are famous in Runescape!");
                Functions.___npcTalk(p, n, "Thats very kind of you to say. Reputations are not easily earnt", "I have managed to succeed where many fail");
                final int menu = Functions.___showMenu(p, n, "I think Katrine is still tougher", "What is your latest plan then?", "You must have trained a lot for this work", "I should not disturb someone as tough as you");
                if (menu == 0) {
                    keliDialogue(p, n, PrinceAliRescue.Keli.KATRINE);
                } else
                    if (menu == 1) {
                        keliDialogue(p, n, PrinceAliRescue.Keli.PLAN);
                    } else
                        if (menu == 2) {
                            keliDialogue(p, n, PrinceAliRescue.Keli.TRAINED);
                        } else
                            if (menu == 3) {
                                keliDialogue(p, n, PrinceAliRescue.Keli.NOT_DISTURB);
                            }



                break;
        }
    }

    private void leelaDialogue(final Player p, final Npc n, final int cID) {
        if (cID == (-1)) {
            switch (p.getQuestStage(this)) {
                case 0 :
                case 1 :
                    Functions.___playerTalk(p, n, "What are you waiting here for");
                    Functions.___npcTalk(p, n, "That is no concern of yours, adventurer");
                    break;
                case 2 :
                    if (p.getCache().hasKey("joe_is_drunk") && Functions.hasItem(p, ItemId.BRONZE_KEY.id())) {
                        Functions.___npcTalk(p, n, "Great! The guard is now harmless", "Now you just need to use the rope on Keli to remove her", "Then you can go in and give everything to the prince");
                        return;
                    }
                    if (p.getCache().hasKey("key_sent") && (!Functions.hasItem(p, ItemId.BRONZE_KEY.id()))) {
                        Functions.___npcTalk(p, n, "My father sent this key for you, be careful not to lose it");
                        Functions.___message(p, "Leela gives you a copy of the key to the princes door");
                        Functions.addItem(p, ItemId.BRONZE_KEY.id(), 1);
                        p.getCache().remove("key_sent");
                        return;
                    }
                    if (((((!p.getCache().hasKey("joe_is_drunk")) && Functions.hasItem(p, ItemId.ROPE.id())) && Functions.hasItem(p, ItemId.PINK_SKIRT.id())) && Functions.hasItem(p, ItemId.PASTE.id())) && Functions.hasItem(p, ItemId.BLONDE_WIG.id())) {
                        Functions.___npcTalk(p, n, "Good, you have all the basic equipment", "What are your plans to stop the guard interfering?");
                        final int chose = Functions.___showMenu(p, n, "I haven't spoken to him yet", "I was going to attack him", "I hoped to get him drunk", "Maybe I could bribe him to leave");
                        if (chose == 0) {
                            leelaDialogue(p, n, PrinceAliRescue.Leela.HAVENT_SPOKE);
                        } else
                            if (chose == 1) {
                                leelaDialogue(p, n, PrinceAliRescue.Leela.ATTACK_HIM);
                            } else
                                if (chose == 2) {
                                    leelaDialogue(p, n, PrinceAliRescue.Leela.DRUNK);
                                } else
                                    if (chose == 3) {
                                        leelaDialogue(p, n, PrinceAliRescue.Leela.BRIBE);
                                    }



                        return;
                    }
                    Functions.___playerTalk(p, n, "I am here to help you free the Prince");
                    Functions.___npcTalk(p, n, "Your employment is known to me.", "Now, do you know all that we need to make the break?");
                    String[] choices = new String[]{ "I must make a disguise. What do you suggest?", "I need to get the key made", "What can i do with the guards?", "I will go and get the rest of the escape equipment" };
                    if (Functions.hasItem(p, ItemId.BRONZE_KEY.id())) {
                        choices = new String[]{ "I must make a disguise. What do you suggest?", "What can i do with the guards?", "I will go and get the rest of the escape equipment" };
                    }
                    int choice = Functions.___showMenu(p, n, choices);
                    if (Functions.hasItem(p, ItemId.BRONZE_KEY.id()) && (choice > 0)) {
                        // skip option of key made
                        choice++;
                    }
                    if (choice == 0) {
                        leelaDialogue(p, n, PrinceAliRescue.Leela.DISGUISE);
                    } else
                        if (choice == 1) {
                            leelaDialogue(p, n, PrinceAliRescue.Leela.KEYMADE);
                        } else
                            if (choice == 2) {
                                leelaDialogue(p, n, PrinceAliRescue.Leela.GUARDS);
                            } else
                                if (choice == 3) {
                                    leelaDialogue(p, n, PrinceAliRescue.Leela.ESCAPE);
                                }



                    break;
                case 3 :
                case -1 :
                    Functions.___npcTalk(p, n, "Thankyou, Al Kharid will forever owe you for your help", "I think that if there is ever anything that needs to be done,", "you will be someone they can rely on");
                    break;
            }
        }
        switch (cID) {
            // if you loose your items u need to get them again ? yes.
            // but how, just put the 3 case there too
            case PrinceAliRescue.Leela.HAVENT_SPOKE :
                Functions.___npcTalk(p, n, "Well, speaking to him may find a weakness he has", "See if theres something that could stop him bothering us", "Good luck with the guard. When the guard is out you can tie up Keli");
                break;
            case PrinceAliRescue.Leela.ATTACK_HIM :
                Functions.___npcTalk(p, n, "I don't think you should", "If you do the rest of the gang and Keli would attack you", "The door guard should be removed first, to make it easy", "Good luck with the guard. When the guard is out you can tie up Keli");
                break;
            case PrinceAliRescue.Leela.DRUNK :
                Functions.___npcTalk(p, n, "Well, thats possible. These guards do like a drink", "I would think it will take at least 3 beers to do it well", "You would probably have to do it all at the same time too", "The effects of the local beer wear of quickly", "Good luck with the guard. When the guard is out you can tie up Keli");
                break;
            case PrinceAliRescue.Leela.BRIBE :
                Functions.___npcTalk(p, n, "You could try. I don't think the emir will pay anything towards it", "And we did bribe one of their guards once", "Keli killed him in front of the other guards, as a deterrent", "It would probably take a lot of gold", "Good luck with the guard. When the guard is out you can tie up Keli");
                break;
            case PrinceAliRescue.Leela.DISGUISE :
                // Make it, u cant make it with the npc playa talk
                Functions.___npcTalk(p, n, "Only the lady Keli, can wander about outside the jail", "The guards will shoot to kill if they see the prince out", "so we need a disguise well enough to fool them at a distance");
                // note: not known if there was a check for regular wig, yet osrs-rs2 didn't feature one
                if (!Functions.hasItem(p, ItemId.BLONDE_WIG.id())) {
                    Functions.___npcTalk(p, n, "You need a wig, maybe made from wool", "If you find someone who can work with wool, ask them about it", "Then the old witch may be able to help you dye it");
                } else {
                    Functions.___npcTalk(p, n, "The wig you have got, well done");
                }
                Functions.___npcTalk(p, n, !Functions.hasItem(p, ItemId.PINK_SKIRT.id()) ? "You will need to get a pink skirt, same as Keli's" : "You have got the skirt, good");
                if (!Functions.hasItem(p, ItemId.PASTE.id())) {
                    Functions.___npcTalk(p, n, "we still need something to colour the Princes skin lighter", "Theres an old witch close to here, she knows about many things", "She may know some way to make the skin lighter");
                } else {
                    Functions.___npcTalk(p, n, "You have the skin paint, well done", "I thought you would struggle to make that");
                }
                if ((Functions.hasItem(p, ItemId.BLONDE_WIG.id()) && Functions.hasItem(p, ItemId.PINK_SKIRT.id())) && Functions.hasItem(p, ItemId.PASTE.id())) {
                    Functions.___npcTalk(p, n, "You do have everything for the disguise");
                }
                if (!Functions.hasItem(p, ItemId.ROPE.id())) {
                    Functions.___npcTalk(p, n, "You will still need some rope to tie up Keli, of course", "I heard that there was a good ropemaker around here");
                } else {
                    Functions.___npcTalk(p, n, "You have rope I see, tie up Keli", "that will be the most dangerous part of the plan");
                }
                String[] choices = new String[]{ "I need to get the key made", "What can i do with the guards?", "I will go and get the rest of the escape equipment" };
                if (Functions.hasItem(p, ItemId.BRONZE_KEY.id())) {
                    choices = new String[]{ "What can i do with the guards?", "I will go and get the rest of the escape equipment" };
                }
                int choice = Functions.___showMenu(p, n, choices);
                if (Functions.hasItem(p, ItemId.BRONZE_KEY.id()) && (choice >= 0)) {
                    choice++;
                }
                if (choice == 0) {
                    leelaDialogue(p, n, PrinceAliRescue.Leela.KEYMADE);
                } else
                    if (choice == 1) {
                        leelaDialogue(p, n, PrinceAliRescue.Leela.GUARDS);
                    } else
                        if (choice == 2) {
                            leelaDialogue(p, n, PrinceAliRescue.Leela.ESCAPE);
                        }


                break;
            case PrinceAliRescue.Leela.KEYMADE :
                Functions.___npcTalk(p, n, "Yes, that is most important", "There is no way you can get the real key.", "It is on a chain around Keli's neck. almost impossible to steal", "get some soft clay, and get her to show you the key somehow", "then take the print, with bronze, to my father");
                break;
            case PrinceAliRescue.Leela.GUARDS :
                Functions.___npcTalk(p, n, "Most of the guards will be easy", "The disguise will get past them", "The only guard who will be a problem will be the one at the door", "He is talkative, try to find a weakness in him", "We can discuss this more when you have the rest of the escape kit");
                String[] choices2 = new String[]{ "I must make a disguise. What do you suggest?", "I need to get the key made", "I will go and get the rest of the escape equipment" };
                if (Functions.hasItem(p, ItemId.BRONZE_KEY.id())) {
                    choices2 = new String[]{ "I must make a disguise. What do you suggest?", "I will go and get the rest of the escape equipment" };
                }
                int choice2 = Functions.___showMenu(p, n, choices2);
                if (Functions.hasItem(p, ItemId.BRONZE_KEY.id()) && (choice2 > 0)) {
                    choice2++;
                }
                if (choice2 == 0) {
                    leelaDialogue(p, n, PrinceAliRescue.Leela.DISGUISE);
                } else
                    if (choice2 == 1) {
                        leelaDialogue(p, n, PrinceAliRescue.Leela.KEYMADE);
                    } else
                        if (choice2 == 2) {
                            leelaDialogue(p, n, PrinceAliRescue.Leela.ESCAPE);
                        }


                break;
            case PrinceAliRescue.Leela.ESCAPE :
                Functions.___npcTalk(p, n, "Good, I shall await your return with everything");
                break;
        }
    }

    @Override
    public GameStateEvent onInvUseOnNpc(final Player p, final Npc npc, final Item item) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((npc.getID() == NpcId.LADY_KELI.id()) && (item.getID() == ItemId.ROPE.id())) {
                        if (p.getCache().hasKey("joe_is_drunk") && (p.getQuestStage(quest) == 2)) {
                            npc.remove();
                            p.message("You overpower Keli, tie her up, and put her in a cupboard");
                        } else
                            if (p.getCache().hasKey("joe_is_drunk")) {
                                p.message("You have rescued the prince already, you cannot use the same plan again");
                            } else {
                                p.message("You cannot tie Keli up until you have all equipment and disabled the guard");
                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onInvUseOnWallObject(final GameObject obj, final Item item, final Player player) {
        final QuestInterface quest = this;
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 45) && (item.getID() == ItemId.BRONZE_KEY.id())) {
                        if (obj.getY() == 640) {
                            final Npc keli = Functions.getNearestNpc(player, NpcId.LADY_KELI.id(), 20);
                            if (player.getX() <= 198) {
                                if (keli != null) {
                                    player.message("You'd better get rid of Lady Keli before trying to go through there");
                                    return null;
                                } else {
                                    if (player.getQuestStage(quest) == 2) {
                                        player.message("You unlock the door");
                                        player.message("You go through the door");
                                        Functions.doDoor(obj, player);
                                    } else {
                                        player.message("I have no reason to do this");
                                    }
                                }
                            } else {
                                player.message("You go through the door");
                                Functions.doDoor(obj, player);
                            }
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    // border guard in separate file
                    if (n.getID() == NpcId.LADY_KELI.id()) {
                        keliDialogue(p, n, -1);
                    } else
                        if (n.getID() == NpcId.HASSAN.id()) {
                            hassanDialogue(p, n);
                        } else
                            if (n.getID() == NpcId.OSMAN.id()) {
                                osmanDialogue(p, n, -1);
                            } else
                                if (n.getID() == NpcId.JOE.id()) {
                                    joeDialogue(p, n, -1);
                                } else
                                    if (n.getID() == NpcId.LEELA.id()) {
                                        leelaDialogue(p, n, -1);
                                    } else
                                        if (n.getID() == NpcId.PRINCE_ALI.id()) {
                                            princeAliDialogue(p, n, -1);
                                        } else
                                            if (n.getID() == NpcId.JAILGUARD.id()) {
                                                jailGuardDialogue(p, n);
                                            }






                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onWallObjectAction(final GameObject obj, final Integer click, final Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 45) {
                        if (obj.getY() == 640) {
                            final Npc keli = Functions.getNearestNpc(p, NpcId.LADY_KELI.id(), 20);
                            if (p.getX() <= 198) {
                                if (keli != null) {
                                    p.message("You'd better get rid of Lady Keli before trying to go through there");
                                    return null;
                                }
                                p.message("The door is locked");
                                if (Functions.hasItem(p, ItemId.BRONZE_KEY.id())) {
                                    p.message("Maybe you should try using your key on it");
                                }
                            } else {
                                p.message("You go through the door");
                                Functions.doDoor(obj, p);
                            }
                        }
                    }
                    return null;
                });
            }
        };
    }

    private void osmanDialogue(final Player p, final Npc n, final int cID) {
        if (cID == (-1)) {
            switch (p.getQuestStage(this)) {
                case 0 :
                    Functions.___npcTalk(p, n, "Hello, I am Osman", "What can I assist you with");
                    final int choice = Functions.___showMenu(p, n, "You don't seem very tough. Who are you?", "I hear wild rumours about a Prince", "I am just being nosy.");
                    if (choice == 0) {
                        Functions.___npcTalk(p, n, "I am in the employ of the Emir", "That is all you need to know");
                    } else
                        if (choice == 1) {
                            Functions.___npcTalk(p, n, "The prince is not here. He is... away", "If you can be trusted, speak to the chancellor, Hassan");
                        } else
                            if (choice == 2) {
                                Functions.___npcTalk(p, n, "That bothers me not", "The secrets of Al Kharid protect themselves");
                            }


                    break;
                case 1 :
                    Functions.___playerTalk(p, n, "The chancellor trusts me. I have come for instructions");
                    Functions.___npcTalk(p, n, "Our Prince is captive by the Lady Keli", "We just need to make the rescue", "There are three things we need you to do");
                    final int menu = Functions.___showMenu(p, n, "What is first thing I must do?", "What is needed second?", "And the final thing you need?");
                    if (menu == 0) {
                        osmanDialogue(p, n, PrinceAliRescue.Osman.FIRST);
                    } else
                        if (menu == 1) {
                            osmanDialogue(p, n, PrinceAliRescue.Osman.SECOND);
                        } else
                            if (menu == 2) {
                                osmanDialogue(p, n, PrinceAliRescue.Osman.FINAL);
                            }


                    break;
                case 2 :
                    if (Functions.hasItem(p, ItemId.KEYPRINT.id()) && Functions.hasItem(p, ItemId.BRONZE_BAR.id())) {
                        Functions.___npcTalk(p, n, "Well done, we can make the key now.");
                        p.message("Osman takes the Key imprint and the bronze bar");
                        Functions.removeItem(p, ItemId.KEYPRINT.id(), 1);
                        Functions.removeItem(p, ItemId.BRONZE_BAR.id(), 1);
                        p.getCache().store("key_sent", true);
                        p.getCache().store("key_made", true);
                        Functions.___npcTalk(p, n, "Pick the key up from Leela.", "I will let you get 80 coins from the chancellor for getting this key");
                        final int wutwut = Functions.___showMenu(p, n, "Thankyou, I will try to find the other items", "Can you tell me what I still need to get?");
                        if (wutwut == 0) {
                        } else
                            if (wutwut == 1) {
                                osmanDialogue(p, n, PrinceAliRescue.Osman.STILL_NEED);
                            }

                    } else {
                        osmanDialogue(p, n, PrinceAliRescue.Osman.STILL_NEED);
                    }
                    break;
                case 3 :
                    Functions.___npcTalk(p, n, "The prince is safe, and on his way home with Leela");
                    Functions.___npcTalk(p, n, "You can pick up your payment from the chancellor");
                    break;
                case -1 :
                    Functions.___npcTalk(p, n, "Well done. A great rescue", "I will remember you if I have anything dangerous to do");
                    break;
            }
            return;
        }
        switch (cID) {
            case PrinceAliRescue.Osman.FIRST :
                Functions.___npcTalk(p, n, "The prince is guarded by some stupid guards, and a clever woman", "The woman is our only way to get the prince out", "Only she can walk freely about the area", "I think you will need to tie her up", "one coil of rope should do for that", "And then disguise the prince as her to get him out without suspicion");
                Functions.___playerTalk(p, n, "How good must the disguise be?");
                Functions.___npcTalk(p, n, "Only enough to fool the guards at a distance", "Get a skirt like hers. Same colour, same style", "We will only have a short time", "A blonde wig too. That is up to you to make or find", "Something to colour the skin of the prince.", "My daughter and top spy, leela, can help you there");
                final int firstMenu = Functions.___showMenu(p, n, "Explain the first thing again", "What is needed second?", "And the final thing you need?", "Okay, I better go find some things");
                if (firstMenu == 0) {
                    osmanDialogue(p, n, PrinceAliRescue.Osman.FIRST);
                } else
                    if (firstMenu == 1) {
                        osmanDialogue(p, n, PrinceAliRescue.Osman.SECOND);
                    } else
                        if (firstMenu == 2) {
                            osmanDialogue(p, n, PrinceAliRescue.Osman.FINAL);
                        } else
                            if (firstMenu == 3) {
                                osmanDialogue(p, n, PrinceAliRescue.Osman.BETTER_FIND);
                            }



                break;
            case PrinceAliRescue.Osman.SECOND :
                Functions.___npcTalk(p, n, "We need the key, or a copy made", "If you can get some soft clay, then you can copy the key", "If you can convince Lady Keli to show it to you for a moment", "She is very boastful. It should not be too hard", "Bring the imprint to me, with a bar of bronze.");
                final int choice = Functions.___showMenu(p, n, "What is first thing I must do?", "What exactly is needed second?", "And the final thing you need?", "Okay, I better go find some things");
                if (choice == 0) {
                    osmanDialogue(p, n, PrinceAliRescue.Osman.FIRST);
                } else
                    if (choice == 1) {
                        osmanDialogue(p, n, PrinceAliRescue.Osman.SECOND);
                    } else
                        if (choice == 2) {
                            osmanDialogue(p, n, PrinceAliRescue.Osman.FINAL);
                        } else
                            if (choice == 3) {
                                osmanDialogue(p, n, PrinceAliRescue.Osman.BETTER_FIND);
                            }



                break;
            case PrinceAliRescue.Osman.FINAL :
                Functions.___npcTalk(p, n, "You will need to stop the guard at the door", "Find out if he has any weaknesses, and use them");
                final int finalChoice = Functions.___showMenu(p, n, "What is first thing I must do?", "What is needed second?", "Okay, I better go find some things");
                if (finalChoice == 0) {
                    osmanDialogue(p, n, PrinceAliRescue.Osman.FIRST);
                } else
                    if (finalChoice == 1) {
                        osmanDialogue(p, n, PrinceAliRescue.Osman.SECOND);
                    } else
                        if (finalChoice == 2) {
                            osmanDialogue(p, n, PrinceAliRescue.Osman.BETTER_FIND);
                        }


                break;
            case PrinceAliRescue.Osman.BETTER_FIND :
                Functions.___npcTalk(p, n, "May good luck travel with you", "Don't forget to find Leela. It can't be done without her help");
                p.updateQuestStage(this, 2);
                break;
            case PrinceAliRescue.Osman.STILL_NEED :
                Functions.___npcTalk(p, n, "Let me check. You need:");
                if (!Functions.hasItem(p, ItemId.BRONZE_KEY.id())) {
                    Functions.___npcTalk(p, n, "A print of the key in soft clay, and a bronze bar");
                    Functions.___npcTalk(p, n, "Then collect the key from Leela");
                } else {
                    Functions.___npcTalk(p, n, "You have the key, good");
                }
                Functions.___npcTalk(p, n, !Functions.hasItem(p, ItemId.BLONDE_WIG.id()) ? "You need to make a Blonde Wig somehow. Leela may help" : "The wig you have got, well done");
                Functions.___npcTalk(p, n, !Functions.hasItem(p, ItemId.PINK_SKIRT.id()) ? "A skirt the same as Keli's," : "You have the skirt, good");
                Functions.___npcTalk(p, n, !Functions.hasItem(p, ItemId.PASTE.id()) ? "Something to colour the Princes skin lighter" : "You have the skin paint, well done", "I thought you would struggle to make that");
                Functions.___npcTalk(p, n, !Functions.hasItem(p, ItemId.ROPE.id()) ? "Rope to tie Keli up with" : "Yes, you have the rope.");
                Functions.___npcTalk(p, n, "You still need some way to stop the guard from interfering");
                Functions.___npcTalk(p, n, "Once you have everything, Go to Leela", "she must be ready to get the prince away");
                break;
        }
    }

    private void princeAliDialogue(final Player p, final Npc n, final int cID) {
        switch (p.getQuestStage(this)) {
            case 2 :
                Functions.___playerTalk(p, n, "Prince, I come to rescue you");
                Functions.___npcTalk(p, n, "That is very very kind of you, how do I get out?");
                Functions.___playerTalk(p, n, "With a disguise, I have removed the Lady Keli", "She is tied up, but will not stay tied up for long");
                if ((((!Functions.hasItem(p, ItemId.BLONDE_WIG.id())) || (!Functions.hasItem(p, ItemId.PINK_SKIRT.id()))) || (!Functions.hasItem(p, ItemId.PASTE.id()))) || (!Functions.hasItem(p, ItemId.BRONZE_KEY.id()))) {
                    // from behavior on osrs just returns, no evidence rsc had otherwise behavior
                    return;
                }
                Functions.___playerTalk(p, n, "Take this disguise, and this key");
                Functions.removeItem(p, ItemId.BLONDE_WIG.id(), 1);
                Functions.removeItem(p, ItemId.PINK_SKIRT.id(), 1);
                Functions.removeItem(p, ItemId.PASTE.id(), 1);
                Functions.removeItem(p, ItemId.BRONZE_KEY.id(), 1);
                Functions.___message(p, "You hand the disguise and the key to the prince");
                final Npc ladyAli = Functions.transform(n, NpcId.PRINCE_ALI_DISGUISE.id(), false);
                Functions.___npcTalk(p, ladyAli, "Thankyou my friend, I must leave you now", "My father will pay you well for this");
                Functions.___playerTalk(p, ladyAli, "Go to Leela, she is close to here");
                ladyAli.remove();
                Functions.___message(p, 1000, "The prince has escaped, well done!", "You are now a friend of Al kharid", "And may pass through the Al Kharid toll gate for free");
                p.updateQuestStage(this, 3);
                break;
            case 3 :
            case -1 :
                Functions.___npcTalk(p, n, "I owe you my life for that escape", "You cannot help me this time, they know who you are", "Go in peace, friend of Al Kharid");
                break;
        }
    }

    class Joe {
        static final int BEER = 6;

        static final int BETTERLEAVE = 3;

        static final int BOY = 2;

        static final int BUYWITH = 4;

        static final int CHILL = 0;

        static final int LIFE = 1;

        static final int MOREMONEY = 5;
    }

    class Keli {
        static final int BESURE = 9;

        static final int FAMOUS = 0;

        static final int GREAT = 10;

        static final int KATRINE = 1;

        static final int KEYPLEASE = 14;

        static final int KILLPEOPLE = 5;

        static final int NEVERHEARD = 7;

        static final int NOBELL = 12;

        static final int NOT_DISTURB = 2;

        static final int OFCOURSE = 13;

        static final int PLAN = 3;

        static final int SECRETS = 11;

        static final int SKILLFUL = 8;

        static final int TRAINED = 4;
    }

    class Leela {
        static final int ATTACK_HIM = 6;

        static final int BRIBE = 5;

        static final int DISGUISE = 0;

        static final int DRUNK = 4;

        static final int ESCAPE = 3;

        static final int GUARDS = 2;

        static final int HAVENT_SPOKE = 7;

        static final int KEYMADE = 1;
    }

    // NOTE: Lady dissapear when you tie her up, when you rescue prince he gets
    // switched to lady. after talking she dissapear again. Then she respawns
    // with joe the guard
    class Osman {
        static final int BETTER_FIND = 3;

        static final int FINAL = 2;

        static final int FIRST = 0;

        static final int SECOND = 1;

        static final int STILL_NEED = 4;
    }
}

