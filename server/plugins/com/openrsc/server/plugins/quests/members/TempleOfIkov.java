package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class TempleOfIkov implements QuestInterface , InvUseOnObjectListener , ObjectActionListener , PickupListener , PlayerAttackNpcListener , PlayerKilledNpcListener , PlayerMageNpcListener , PlayerRangeNpcListener , TalkToNpcListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener , PickupExecutiveListener , PlayerAttackNpcExecutiveListener , PlayerKilledNpcExecutiveListener , PlayerMageNpcExecutiveListener , PlayerRangeNpcExecutiveListener , TalkToNpcExecutiveListener {
    /**
     * Quest Objects
     */
    private static int STAIR_DOWN = 370;

    private static int STAIR_UP = 369;

    private static int LEVER = 361;

    private static int LEVER_BRACKET = 367;

    private static int COMPLETE_LEVER = 368;

    @Override
    public int getQuestId() {
        return Quests.TEMPLE_OF_IKOV;
    }

    @Override
    public String getQuestName() {
        return "Temple of Ikov (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        p.getCache().remove("openSpiderDoor");
        p.getCache().remove("completeLever");
        p.getCache().remove("killedLesarkus");
        int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.TEMPLE_OF_IKOV);
        // keep order kosher
        int[] skillIDs = new int[]{ Skills.RANGED, Skills.FLETCHING };
        for (int i = 0; i < skillIDs.length; i++) {
            questData[Quests.MAPIDX_SKILL] = skillIDs[i];
            Functions.incQuestReward(p, questData, i == (skillIDs.length - 1));
        }
        p.message("@gre@You haved gained 1 quest point!");
        p.message("Well done you have completed the temple of Ikov quest");
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return DataConversions.inArray(new int[]{ NpcId.LUCIEN.id(), NpcId.WINELDA.id(), NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id(), NpcId.GUARDIAN_OF_ARMADYL_MALE.id(), NpcId.LUCIEN_EDGE.id() }, n.getID());
    }

    private void lucienDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.LUCIEN.id()) {
            if (cID == (-1)) {
                switch (p.getQuestStage(this)) {
                    case 0 :
                        Functions.___npcTalk(p, n, "I come here seeking a hero who can help me");
                        int menu = Functions.___showMenu(p, n, "I am a hero", "Yep lots of heroes about here");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "I need someone who can enter the tunnels under the deserted temple of Ikov", "Near Hemenster, to the north of here", "Kill the fire warrior of Lesarkus", "And retrieve the staff of Armardyl");
                            int newMenu = // do not send over
                            Functions.___showMenu(p, n, false, "Why can't you do it yourself?", "That sounds like fun", "That sounds too dangerous for me", "How much will you pay me?");
                            if (newMenu == 0) {
                                Functions.___playerTalk(p, n, "Why can't you do that yourself?");
                                Functions.___npcTalk(p, n, "The guardians of the staff of Armardyl fear me", "They know my kind is powerful", "So they have set up magical wards against are race");
                                int newMenu2 = // do not send over
                                Functions.___showMenu(p, n, false, "How much will you pay me?", "That sounds like fun", "Who are your kind?", "That sounds too dangerous for me");
                                if (newMenu2 == 0) {
                                    Functions.___playerTalk(p, n, "How much will you pay me");
                                    lucienDialogue(p, n, TempleOfIkov.Lucien.PAYME);
                                } else
                                    if (newMenu2 == 1) {
                                        Functions.___playerTalk(p, n, "That sounds like fun");
                                        lucienDialogue(p, n, TempleOfIkov.Lucien.SOUNDSFUN);
                                    } else
                                        if (newMenu2 == 2) {
                                            Functions.___playerTalk(p, n, "Who are your kind?");
                                            Functions.___npcTalk(p, n, "An ancient and powerful race", "Back in the second age we held great influence in this world", "There are few of us left now");
                                        } else
                                            if (newMenu2 == 3) {
                                                Functions.___playerTalk(p, n, "That sounds too dangerous for me");
                                                Functions.___npcTalk(p, n, "Fortune favours the bold");
                                            }



                            } else
                                if (newMenu == 1) {
                                    Functions.___playerTalk(p, n, "That sounds like fun");
                                    lucienDialogue(p, n, TempleOfIkov.Lucien.SOUNDSFUN);
                                } else
                                    if (newMenu == 2) {
                                        Functions.___playerTalk(p, n, "That sounds too dangerous for me");
                                        Functions.___npcTalk(p, n, "Fortune favours the bold");
                                    } else
                                        if (newMenu == 3) {
                                            Functions.___playerTalk(p, n, "How much will you pay me");
                                            lucienDialogue(p, n, TempleOfIkov.Lucien.PAYME);
                                        }



                        }
                        break;
                    case 1 :
                    case 2 :
                    case -1 :
                    case -2 :
                        Functions.___npcTalk(p, n, "I thought I told you not to meet me here again");
                        if (Functions.hasItem(p, ItemId.PENDANT_OF_LUCIEN.id())) {
                            Functions.___playerTalk(p, n, "Yes you did, sorry");
                        } else {
                            int lostAmuletMenu = Functions.___showMenu(p, n, "I lost that pendant you gave me", "Yes you did sorry");
                            if (lostAmuletMenu == 0) {
                                Functions.___npcTalk(p, n, "Hmm", "Imbecile");
                                p.message("Lucien gives you another pendant");
                                Functions.addItem(p, ItemId.PENDANT_OF_LUCIEN.id(), 1);
                            }
                        }
                        break;
                }
            }
            switch (cID) {
                case TempleOfIkov.Lucien.SOUNDSFUN :
                    Functions.___npcTalk(p, n, "Well it's not that easy", "The fire warrior can only be killed with a weapon of ice", "And there are many other traps and hazards in those tunnels");
                    Functions.___playerTalk(p, n, "Well I am brave I shall give it a go");
                    Functions.___npcTalk(p, n, "Take this pendant you will need it to get through the chamber of fear");
                    Functions.addItem(p, ItemId.PENDANT_OF_LUCIEN.id(), 1);
                    Functions.___npcTalk(p, n, "It is not safe for me to linger here much longer", "When you have done meet me in the forest north of Varrock", "I have a small holding up there");
                    p.updateQuestStage(this, 1);
                    break;
                case TempleOfIkov.Lucien.PAYME :
                    Functions.___npcTalk(p, n, "Ah the mercenary type I see");
                    Functions.___playerTalk(p, n, "It's a living");
                    Functions.___npcTalk(p, n, "I shall adequately reward you", "With both money and power");
                    Functions.___playerTalk(p, n, "Sounds rather too vague for me");
                    break;
            }
        }
    }

    private void wineldaDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.WINELDA.id()) {
            if (cID == (-1)) {
                if (Functions.hasItem(p, ItemId.LIMPWURT_ROOT.id(), 20)) {
                    Functions.___playerTalk(p, n, "I have the 20 limpwurt roots, now transport me please");
                    Functions.___npcTalk(p, n, "Oh marverlous", "Brace yourself then");
                    p.getInventory().remove(ItemId.LIMPWURT_ROOT.id(), 20);
                    p.teleport(557, 3290);
                    Functions.sleep(650);
                    ActionSender.sendTeleBubble(p, p.getX(), p.getY(), false);
                } else {
                    Functions.___npcTalk(p, n, "Hehe in a bit of a pickle are we?", "Want to be getting over the nasty lava stream do we?");
                    int menu = Functions.___showMenu(p, n, "Not really, no", "Yes we do", "Yes I do");
                    if (menu == 0) {
                        Functions.___npcTalk(p, n, "Hehe ye'll come back later", "They always come back later");
                    } else
                        if (menu == 1) {
                            wineldaDialogue(p, n, TempleOfIkov.Winelda.YES);
                        } else
                            if (menu == 2) {
                                wineldaDialogue(p, n, TempleOfIkov.Winelda.YES);
                            }


                }
            }
            switch (cID) {
                case TempleOfIkov.Winelda.YES :
                    Functions.___npcTalk(p, n, "Well keep it under your helmet", "But I'm knowing some useful magic tricks", "I could get you over there easy as that");
                    Functions.___playerTalk(p, n, "Okay get me over there");
                    Functions.___npcTalk(p, n, "Okay brace yourself", "Actually no no", "Why should I do it for free", "Bring me a bite to eat and I'll be a touch more helpful", "How about some nice tasty limpwurt roots to chew on", "Yes yes that's good, bring me 20 limpwurt roots and over you go");
                    break;
            }
        }
    }

    private void guardianDialogue(Player p, Npc n, int cID) {
        if ((n.getID() == NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id()) || (n.getID() == NpcId.GUARDIAN_OF_ARMADYL_MALE.id())) {
            if (cID == (-1)) {
                switch (p.getQuestStage(this)) {
                    case 1 :
                        if (p.getInventory().wielding(ItemId.PENDANT_OF_LUCIEN.id()) && (!Functions.hasItem(p, ItemId.STAFF_OF_ARMADYL.id()))) {
                            Functions.___npcTalk(p, n, "Ahh tis a foul agent of Lucien", "Get ye from our master's house");
                            if (n != null) {
                                n.startCombat(p);
                            }
                            return;
                        }
                        if (Functions.hasItem(p, ItemId.STAFF_OF_ARMADYL.id())) {
                            Functions.___npcTalk(p, n, "Stop", "You cannot take the staff of Armadyl");
                            n.setChasing(p);
                            return;
                        }
                        Functions.___npcTalk(p, n, "Thou dost venture deep in the tunnels", "It has been many a year since someone has passed thus far");
                        int menu = Functions.___showMenu(p, n, "I seek the staff of Armadyl", "Out of my way fool", "Who are you?");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "We guard that here", "As did our fathers", "And our father's fathers", "Why dost thou seeketh it?");
                            int seekMenu = // do not send over
                            Functions.___showMenu(p, n, false, "A guy named Lucien is paying me", "Just give it to me", "I am a collector of rare and powerful artifacts");
                            if (seekMenu == 0) {
                                Functions.___playerTalk(p, n, "A guy named Lucien is paying me");
                                guardianDialogue(p, n, TempleOfIkov.Guardian.WORKINGFORLUCIEN);
                            } else
                                if (seekMenu == 1) {
                                    Functions.___playerTalk(p, n, "Just give it to me");
                                    Functions.___npcTalk(p, n, "The staff is a sacred object", "Not to be given away to anyone who asks");
                                } else
                                    if (seekMenu == 2) {
                                        Functions.___playerTalk(p, n, "I am a collector of rare and powerful objects");
                                        Functions.___npcTalk(p, n, "The staff is not yours to collect");
                                    }


                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "I may be a fool, but I will not step aside");
                                int foolMenu = Functions.___showMenu(p, n, "Why not?", "Then I must strike you down", "Then I guess I will turn back");
                                if (foolMenu == 0) {
                                    Functions.___npcTalk(p, n, "Only members of our order are allowed further");
                                } else
                                    if (foolMenu == 1) {
                                        n.startCombat(p);
                                    }

                            } else
                                if (menu == 2) {
                                    Functions.___npcTalk(p, n, "I am a guardian of Armadyl", "We have kept this place safe and holy", "For many generations", "Many evil souls would like to get their hands on what lies here", "Especially the Mahjarrat");
                                    int whoMenu = Functions.___showMenu(p, n, "What is an Armadyl?", "Who are the Mahjarrat?", "Wow you must be old");
                                    if (whoMenu == 0) {
                                        Functions.___npcTalk(p, n, "Armadyl is our God", "We are his servants", "Who have the honour to stay here", "And guard his artifacts", "Till he needs them to smite his enemies");
                                        int bla = // do not send over
                                        Functions.___showMenu(p, n, false, "Ok that's nice to know", "Someone told me there were only three gods");
                                        if (bla == 0) {
                                            Functions.___playerTalk(p, n, "I am a collector of rare and powerful objects");
                                            Functions.___npcTalk(p, n, "The staff is not yours to collect");
                                        } else
                                            if (bla == 1) {
                                                Functions.___playerTalk(p, n, "Someone told me there were only three gods", "Saradomin, Zamorak and Guthix");
                                                Functions.___npcTalk(p, n, "Was that someone a Saradominist?", "I hear Saradominism is the principle doctrine", "Out in the world currently", "They only Acknowledge those three gods", "They are wrong", "Depending on what you define as a god", "We are aware of at least twenty");
                                            }

                                    } else
                                        if (whoMenu == 1) {
                                            Functions.___npcTalk(p, n, "Ancient powerful beings", "They are very evil", "They were said to once dominate this plane of existance", "Zamorak was said to once have been of their stock", "They are few in number and have less power these days", "Some still have presence in this world in their liche forms", "Mahjarrat such as Lucien and Azzanadra would become extremely powerful", "If they got their hands on the staff of Armadyl");
                                            int maj = // do not send over
                                            Functions.___showMenu(p, n, false, "Did you say Lucien?", "You had better guard it well then");
                                            if (maj == 0) {
                                                Functions.___playerTalk(p, n, "Did you say Lucien?", "He's the one who sent me to fetch the staff");
                                                guardianDialogue(p, n, TempleOfIkov.Guardian.WORKINGFORLUCIEN);
                                            } else
                                                if (maj == 1) {
                                                    Functions.___playerTalk(p, n, "You had better guard it well them");
                                                    Functions.___npcTalk(p, n, "Don't fret, for we shall");
                                                }

                                        } else
                                            if (whoMenu == 2) {
                                                Functions.___npcTalk(p, n, "No no, I have not guarded here for all those generations", "Many generations of my family have though");
                                            }


                                }


                        break;
                    case 2 :
                        Functions.___npcTalk(p, n, "Any luck against Lucien?");
                        if (!Functions.hasItem(p, ItemId.PENDANT_OF_ARMADYL.id())) {
                            int option = Functions.___showMenu(p, n, "Not yet", "No I've lost the pendant you gave me");
                            if (option == 0) {
                                Functions.___npcTalk(p, n, "Well good luck on your quest");
                            } else
                                if (option == 1) {
                                    Functions.___npcTalk(p, n, "Thou art a careless buffoon", "Have another one");
                                    p.message("The guardian gives you a pendant");
                                    Functions.addItem(p, ItemId.PENDANT_OF_ARMADYL.id(), 1);
                                }

                        } else {
                            Functions.___playerTalk(p, n, "Not yet");
                            Functions.___npcTalk(p, n, "Well good luck on your quest");
                        }
                        break;
                    case -1 :
                        Functions.___playerTalk(p, n, "I have defeated Lucien");
                        Functions.___npcTalk(p, n, "Well done", "We can only hope that will keep him quiet for a while");
                        break;
                    case -2 :
                        Functions.___npcTalk(p, n, "Get away from here", "Thou evil agent of Lucien");
                        break;
                }
            }
            switch (cID) {
                case TempleOfIkov.Guardian.WORKINGFORLUCIEN :
                    Functions.___npcTalk(p, n, "Thou art working for him?", "Thy fool", "Quick you must be cleansed to save your soul");
                    int menu = // do not send over
                    Functions.___showMenu(p, n, false, "How dare you call me a fool?", "Erm I think I'll be leaving now", "Yes I could do with a bath");
                    if (menu == 0) {
                        Functions.___playerTalk(p, n, "How dare you call me a fool", "I will work for who I please");
                        Functions.___npcTalk(p, n, "This one is too far gone", "He must be cut down to stop the spread of the blight");
                        n.startCombat(p);
                    } else
                        if (menu == 1) {
                            Functions.___playerTalk(p, n, "Erm, I think I'll be leaving now");
                            Functions.___npcTalk(p, n, "We cannot allow an agent of Lucien to roam free");
                            n.startCombat(p);
                        } else
                            if (menu == 2) {
                                Functions.___playerTalk(p, n, "Yes I could do with a bath");
                                p.message("The guardian splashes holy water over you");
                                Functions.___npcTalk(p, n, "That should do the trick", "Now you say that Lucien sent you to retrieve the staff", "He must not get a hold of it", "He would become too powerful with the staff", "Hast thou heard of the undead necromancer?", "Who raised an undead army against Varrock a few years past", "That was Lucien", "If thou knowest where to find him maybe you can help us against him");
                                int lastMenu = Functions.___showMenu(p, n, "Ok I will help", "No I shan't turn against my employer", "I need time to consider this");
                                if (lastMenu == 0) {
                                    Functions.___npcTalk(p, n, "So you know where he lurks?");
                                    Functions.___playerTalk(p, n, "Yes");
                                    Functions.___npcTalk(p, n, "He must be growing in power again if he is after the staff", "If you can defeat him, it may weaken him for a time", "You will need to use this pendant to even be able to attack him");
                                    p.message("The guardian gives you a pendant");
                                    Functions.addItem(p, ItemId.PENDANT_OF_ARMADYL.id(), 1);
                                    p.updateQuestStage(this, 2);
                                } else
                                    if (lastMenu == 1) {
                                        Functions.___npcTalk(p, n, "This one is too far gone", "He must be cut down to stop the spread of the blight");
                                        n.startCombat(p);
                                    } else
                                        if (lastMenu == 2) {
                                            Functions.___npcTalk(p, n, "Come back when you have made your choice");
                                        }


                            }


                    break;
            }
        }
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.LUCIEN_EDGE.id()) {
                        if ((p.getQuestStage(quest) == (-1)) || (p.getQuestStage(quest) == (-2))) {
                            p.message("You have already completed this quest");
                            return null;
                        }
                        Functions.___npcTalk(p, n, "Have you got the staff of Armadyl yet?");
                        if (Functions.hasItem(p, ItemId.STAFF_OF_ARMADYL.id())) {
                            int menu = Functions.___showMenu(p, n, "Yes here it is", "No not yet");
                            if (menu == 0) {
                                Functions.___message(p, "You give the staff to Lucien");
                                Functions.removeItem(p, ItemId.STAFF_OF_ARMADYL.id(), 1);
                                Functions.___npcTalk(p, n, "Muhahahaha", "Already I can feel the power of this staff running through my limbs", "Soon I shall be exceedingly powerful", "I suppose you would like a reward now", "I shall grant you much power");
                                p.message("A glow eminates from Lucien's helmet");
                                p.sendQuestComplete(Quests.TEMPLE_OF_IKOV);
                                p.updateQuestStage(quest, -2);
                                Functions.___npcTalk(p, n, "I must be away now to make preparations for my conquest", "Muhahahaha");
                                n.remove();
                            }
                        } else {
                            Functions.___playerTalk(p, n, "No not yet");
                        }
                    } else
                        if (n.getID() == NpcId.LUCIEN.id()) {
                            lucienDialogue(p, n, -1);
                        } else
                            if (n.getID() == NpcId.WINELDA.id()) {
                                wineldaDialogue(p, n, -1);
                            } else
                                if ((n.getID() == NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id()) || (n.getID() == NpcId.GUARDIAN_OF_ARMADYL_MALE.id())) {
                                    guardianDialogue(p, n, -1);
                                }



                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return ((obj.getID() == TempleOfIkov.STAIR_DOWN) || (obj.getID() == TempleOfIkov.STAIR_UP)) || ((obj.getID() == TempleOfIkov.LEVER) || (obj.getID() == TempleOfIkov.COMPLETE_LEVER));
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == TempleOfIkov.STAIR_DOWN) {
                        if ((Functions.hasItem(p, ItemId.LIT_CANDLE.id()) || Functions.hasItem(p, ItemId.LIT_BLACK_CANDLE.id())) || Functions.hasItem(p, ItemId.LIT_TORCH.id())) {
                            p.message("Your flame lights up the room");
                            p.teleport(537, 3372);
                        } else {
                            p.message("You cannot see any further into the room");
                            p.teleport(537, 3394);
                            Functions.sleep(1600);
                            p.message("It is too dark");
                        }
                    } else
                        if (obj.getID() == TempleOfIkov.STAIR_UP) {
                            p.teleport(536, 3338);
                        } else
                            if (obj.getID() == TempleOfIkov.LEVER) {
                                if (command.equals("pull")) {
                                    if (!p.getCache().hasKey("ikovLever")) {
                                        p.message("You have activated a trap on the lever");
                                        p.damage(DataConversions.roundUp(p.getSkills().getLevel(Skills.HITS) / 5));
                                    } else {
                                        Functions.___message(p, "You pull the lever", "You hear a clunk", "The trap on the lever resets");
                                        if (p.getCache().hasKey("ikovLever")) {
                                            p.getCache().remove("ikovLever");
                                        }
                                        if ((!p.getCache().hasKey("openSpiderDoor")) && ((p.getQuestStage(quest) != (-1)) || (p.getQuestStage(quest) != (-2)))) {
                                            p.getCache().store("openSpiderDoor", true);
                                        }
                                    }
                                } else
                                    if (command.equals("searchfortraps")) {
                                        p.message("You search the lever for traps");
                                        if (Functions.getCurrentLevel(p, Skills.THIEVING) < 42) {
                                            p.message("You have not high thieving enough to disable this trap");
                                            return null;
                                        }
                                        Functions.___message(p, "You find a trap on the lever", "You disable the trap");
                                        if (!p.getCache().hasKey("ikovLever")) {
                                            p.getCache().store("ikovLever", true);
                                        }
                                    }

                            } else
                                if (obj.getID() == TempleOfIkov.COMPLETE_LEVER) {
                                    Functions.___message(p, "You pull the lever", "You hear the door next to you make a clunking noise");
                                    if ((!p.getCache().hasKey("completeLever")) && ((p.getQuestStage(quest) != (-1)) || (p.getQuestStage(quest) != (-2)))) {
                                        p.getCache().store("completeLever", true);
                                    }
                                }



                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        if (i.getID() == ItemId.ICE_ARROWS.id()) {
            return true;
        }
        if (i.getID() == ItemId.STAFF_OF_ARMADYL.id()) {
            Npc guardian = Functions.getMultipleNpcsInArea(p, 5, NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id(), NpcId.GUARDIAN_OF_ARMADYL_MALE.id());
            if (guardian == null)
                return false;
            else
                return true;

        }
        return false;
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ItemId.ICE_ARROWS.id()) {
                        if (((i.getX() == 560) && (i.getY() == 3352)) || ((i.getX() == 563) && (i.getY() == 3354))) {
                            Functions.addItem(p, ItemId.ICE_ARROWS.id(), 1);
                            p.teleport(538, 3348);
                            Functions.sleep(650);
                            ActionSender.sendTeleBubble(p, p.getX(), p.getY(), false);
                            Functions.sleep(1000);
                            p.message("Suddenly your surroundings change");
                        } else {
                            Functions.___message(p, "You can only take ice arrows from the cave of ice spiders", "In the temple of Ikov");
                        }
                    } else
                        if (i.getID() == ItemId.STAFF_OF_ARMADYL.id()) {
                            if (((p.getQuestStage(quest) == 2) || (p.getQuestStage(quest) == (-1))) || (p.getQuestStage(quest) == (-2))) {
                                p.message("I shouldn't steal this");
                                return null;
                            }
                            if (Functions.hasItem(p, ItemId.STAFF_OF_ARMADYL.id())) {
                                p.message("I already have one of those");
                                return null;
                            }
                            Npc n = Functions.getMultipleNpcsInArea(p, 5, NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id(), NpcId.GUARDIAN_OF_ARMADYL_MALE.id());
                            if (n != null) {
                                Functions.___npcTalk(p, n, "That is not thine to take");
                                n.setChasing(p);
                                return null;
                            }
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return (item.getID() == ItemId.LEVER.id()) && (obj.getID() == TempleOfIkov.LEVER_BRACKET);
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((item.getID() == ItemId.LEVER.id()) && (obj.getID() == TempleOfIkov.LEVER_BRACKET)) {
                        p.message("You fit the lever into the bracket");
                        Functions.removeItem(p, ItemId.LEVER.id(), 1);
                        p.getWorld().replaceGameObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), TempleOfIkov.COMPLETE_LEVER, obj.getDirection(), obj.getType()));
                        p.getWorld().delayedSpawnObject(obj.getLoc(), 15000);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerMageNpc(Player p, Npc n) {
        return n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id();
    }

    @Override
    public GameStateEvent onPlayerMageNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) {
                        if ((p.getCache().hasKey("killedLesarkus") || (p.getQuestStage(quest) == (-1))) || (p.getQuestStage(quest) == (-2))) {
                            p.message("You have already killed the fire warrior");
                            return null;
                        }
                        p.message("You need to kill the fire warrior with ice arrows");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return (n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) || (n.getID() == NpcId.LUCIEN_EDGE.id());
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) {
                        n.killedBy(p);
                        if (!p.getCache().hasKey("killedLesarkus")) {
                            p.getCache().store("killedLesarkus", true);
                        }
                    } else
                        if (n.getID() == NpcId.LUCIEN_EDGE.id()) {
                            if ((p.getQuestStage(quest) == (-1)) || (p.getQuestStage(quest) == (-2))) {
                                p.message("You have already completed this quest");
                                n.getSkills().setLevel(Skills.HITS, n.getSkills().getMaxStat(Skills.HITS));
                                return null;
                            }
                            n.getSkills().setLevel(Skills.HITS, n.getSkills().getMaxStat(Skills.HITS));
                            Functions.___npcTalk(p, n, "You may have defeated me for now", "But I will be back");
                            p.sendQuestComplete(Quests.TEMPLE_OF_IKOV);
                            n.displayNpcTeleportBubble(n.getX(), n.getY());
                            n.remove();
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        return n.getID() == NpcId.LUCIEN_EDGE.id();
    }

    @Override
    public GameStateEvent onPlayerAttackNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.LUCIEN_EDGE.id()) {
                        if ((p.getQuestStage(quest) == (-1)) || (p.getQuestStage(quest) == (-2))) {
                            p.message("You have already completed this quest");
                        } else {
                            if (p.getInventory().wielding(ItemId.PENDANT_OF_ARMADYL.id())) {
                                p.startCombat(n);
                            } else {
                                Functions.___npcTalk(p, n, "I'm sure you don't want to attack me really", "I am your friend");
                                Functions.___message(p, "You decide you don't want to attack Lucien really", "He is your friend");
                            }
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerRangeNpc(Player p, Npc n) {
        if ((n.getID() == NpcId.LUCIEN_EDGE.id()) && (((p.getQuestStage(Quests.TEMPLE_OF_IKOV) == (-1)) || (p.getQuestStage(Quests.TEMPLE_OF_IKOV) == (-2))) || (!p.getInventory().wielding(ItemId.PENDANT_OF_ARMADYL.id())))) {
            return true;
        }
        if ((n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) && ((p.getCache().hasKey("killedLesarkus") || (p.getQuestStage(Quests.TEMPLE_OF_IKOV) == (-1))) || (p.getQuestStage(Quests.TEMPLE_OF_IKOV) == (-2)))) {
            return true;
        } else {
            if ((n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) && ((p.getInventory().hasItemId(ItemId.ICE_ARROWS.id()) && hasGoodBow(p)) || p.getCache().hasKey("shot_ice"))) {
                p.getCache().store("shot_ice", true);
                return false;
            }
        }
        if ((n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) && (!p.getCache().hasKey("shot_ice"))) {
            return true;
        }
        return false;
    }

    public boolean hasGoodBow(Player p) {
        int[] allowedBowsIce = new int[]{ ItemId.YEW_LONGBOW.id(), ItemId.YEW_SHORTBOW.id(), ItemId.MAGIC_LONGBOW.id(), ItemId.MAGIC_SHORTBOW.id() };
        boolean hasGoodBow = false;
        for (int bow : allowedBowsIce) {
            hasGoodBow |= p.getInventory().hasItemId(bow) && p.getInventory().wielding(bow);
        }
        return hasGoodBow;
    }

    @Override
    public GameStateEvent onPlayerRangeNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.LUCIEN_EDGE.id()) {
                        if ((p.getQuestStage(Quests.TEMPLE_OF_IKOV) == (-1)) || (p.getQuestStage(Quests.TEMPLE_OF_IKOV) == (-2))) {
                            p.message("You have already completed this quest");
                            return null;
                        }
                        if (!p.getInventory().wielding(ItemId.PENDANT_OF_ARMADYL.id())) {
                            Functions.___npcTalk(p, n, "I'm sure you don't want to attack me really", "I am your friend");
                            Functions.___message(p, "You decide you don't want to attack Lucien really", "He is your friend");
                            return null;
                        }
                    } else
                        if (n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) {
                            if ((p.getCache().hasKey("killedLesarkus") || (p.getQuestStage(Quests.TEMPLE_OF_IKOV) == (-1))) || (p.getQuestStage(Quests.TEMPLE_OF_IKOV) == (-2))) {
                                p.message("You have already killed the fire warrior");
                                return null;
                            }
                            if (!p.getCache().hasKey("shot_ice")) {
                                p.message("You need to kill the fire warrior with ice arrows");
                                return null;
                            }
                        }

                    return null;
                });
            }
        };
    }

    class Lucien {
        public static final int SOUNDSFUN = 0;

        public static final int PAYME = 1;
    }

    class Winelda {
        public static final int YES = 0;
    }

    class Guardian {
        public static final int WORKINGFORLUCIEN = 0;
    }
}

