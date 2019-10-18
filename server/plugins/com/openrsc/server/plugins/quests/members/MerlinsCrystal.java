package com.openrsc.server.plugins.quests.members;


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
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


/**
 *
 *
 * @author n0m
 * @author complete dialogues & fixed functions - Davve
 * @author start of holy grail quest - davve
 */
public class MerlinsCrystal implements QuestInterface , DropListener , InvUseOnObjectListener , ObjectActionListener , PlayerKilledNpcListener , TalkToNpcListener , WallObjectActionListener , DropExecutiveListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener , PlayerKilledNpcExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    @Override
    public int getQuestId() {
        return Quests.MERLINS_CRYSTAL;
    }

    @Override
    public String getQuestName() {
        return "Merlin's crystal (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player player) {
        player.getCache().remove("magic_words");
        player.message("Well done you have completed the Merlin's crystal quest");
        Functions.incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.MERLINS_CRYSTAL), true);
        player.message("You haved gained 6 quest points!");
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return ((((obj.getID() == 292) || (obj.getID() == 293)) || (obj.getID() == 291)) || (((obj.getID() == 296) && (obj.getY() == 366)) && command.equalsIgnoreCase("search"))) || (obj.getID() == 295);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 292) || (obj.getID() == 293)) {
                        Npc arhein = Functions.getNearestNpc(p, NpcId.ARHEIN.id(), 10);
                        if ((p.getQuestStage(quest) >= 0) && (p.getQuestStage(quest) < 2)) {
                            p.playerServerMessage(MessageType.QUEST, "I have no reason to do that");
                        } else
                            if (arhein != null) {
                                Functions.___npcTalk(p, arhein, "Oi get away from there!");
                            } else {
                                p.teleport(456, 3352, false);
                                p.message("You hide away in the ship");
                                Functions.sleep(1200);
                                p.message("The ship starts to move");
                                Functions.sleep(3000);
                                p.message("You are out at sea");
                                Functions.sleep(3000);
                                p.message("The ship comes to a stop");
                                p.teleport(456, 520, false);
                                Functions.___message(p, "You sneak out of the ship");
                            }

                    } else
                        if (obj.getID() == 291) {
                            p.message("there are buckets in this crate");
                            Functions.sleep(800);
                            p.message("would you like a bucket?");
                            int opt = Functions.___showMenu(p, "Yes", "No");
                            if (opt == 0) {
                                p.message("you take a bucket.");
                                Functions.addItem(p, ItemId.BUCKET.id(), 1);
                            }
                        } else
                            if (obj.getID() == 296) {
                                Functions.___message(p, "You find a small inscription at the bottom of the altar", "It reads Snarthon Candtrick Termanto");
                                if (!p.getCache().hasKey("magic_words")) {
                                    p.getCache().store("magic_words", true);
                                }
                            } else
                                if (obj.getID() == 295) {
                                    p.teleport(p.getX(), p.getY() + 944);
                                    p.message("You climb up the ladder");
                                    if ((p.getQuestStage(quest) != 3) && (!p.getCache().hasKey("lady_test"))) {
                                        return null;
                                    }
                                    Functions.sleep(600);
                                    Npc lady = Functions.getNearestNpc(p, NpcId.LADY_UPSTAIRS.id(), 5);
                                    if (lady == null) {
                                        lady = Functions.spawnNpc(NpcId.LADY_UPSTAIRS.id(), p.getX() - 1, p.getY() - 1, 60000, p);
                                    }
                                    Functions.sleep(600);
                                    if (lady != null) {
                                        Functions.___playerTalk(p, lady, "Hello I am here, can I have Excalibur yet?");
                                        Functions.___npcTalk(p, lady, "I don't think you are worthy enough", "Come back when you are a better person");
                                    }
                                }



                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return (n.getID() == NpcId.SIR_MORDRED.id()) && (p.getQuestStage(this) == 2);
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getCombatEvent() != null) {
                        n.getCombatEvent().resetCombat();
                    }
                    n.getSkills().setLevel(Skills.HITS, 5);
                    Npc leFaye = Functions.spawnNpc(p.getWorld(), NpcId.MORGAN_LE_FAYE.id(), 461, 2407, 60000);
                    Functions.sleep(500);
                    Functions.___npcTalk(p, leFaye, "Please spare my son");
                    int option = Functions.___showMenu(p, n, "Tell me how to untrap Merlin and I might", "No he deserves to die", "OK then");
                    if (option == 0) {
                        p.updateQuestStage(quest, 3);
                        Functions.___npcTalk(p, leFaye, "You have guessed correctly that I'm responsible for that");
                        Functions.___npcTalk(p, leFaye, "I suppose I can live with that fool Merlin being loose");
                        Functions.___npcTalk(p, leFaye, "for the sake of my son");
                        Functions.___npcTalk(p, leFaye, "Setting him free won't be easy though");
                        Functions.___npcTalk(p, leFaye, "You will need to find a pentagram as close to the crystal as you can find");
                        Functions.___npcTalk(p, leFaye, "You will need to drop some bats bones in the pentagram");
                        Functions.___npcTalk(p, leFaye, "while holding a black candle");
                        Functions.___npcTalk(p, leFaye, "This will summon the demon Thrantax");
                        Functions.___npcTalk(p, leFaye, "You will need to bind him with magic words");
                        Functions.___npcTalk(p, leFaye, "Then you will need the sword Excalibur with which the spell was bound");
                        Functions.___npcTalk(p, leFaye, "Shatter the crystal with Excalibur");
                        int sub_opt = Functions.___showMenu(p, leFaye, "So where can I find Excalibur?", "OK I will do all that", "What are the magic words?");
                        if (sub_opt == 0) {
                            Functions.___npcTalk(p, leFaye, "The lady of the lake has it");
                            Functions.___npcTalk(p, leFaye, "I don't know if she will give it you though");
                            Functions.___npcTalk(p, leFaye, "She can be rather temperamental");
                            int sub_opt2 = // do not send over
                            Functions.___showMenu(p, leFaye, false, "OK I will go do all that", "What are the magic words?");
                            if (sub_opt2 == 0) {
                                Functions.___playerTalk(p, leFaye, "OK I will do all that");
                                p.message("Morgan Le Faye vanishes");
                            } else
                                if (sub_opt2 == 1) {
                                    Functions.___playerTalk(p, leFaye, "What are the magic words?");
                                    Functions.___npcTalk(p, leFaye, "You will find the magic words at the base of one of the chaos altars");
                                    Functions.___npcTalk(p, leFaye, "Which chaos altar I cannot remember");
                                }

                        } else
                            if (sub_opt == 1) {
                                p.message("Morgan Le Faye vanishes");
                            } else
                                if (sub_opt == 2) {
                                    Functions.___npcTalk(p, leFaye, "You will find the magic words at the base of one of the chaos altars");
                                    Functions.___npcTalk(p, leFaye, "Which chaos altar I cannot remember");
                                }


                    } else
                        if (option == 1) {
                            p.message("You kill Mordred");
                            n.killedBy(p);
                        } else
                            if (option == 2) {
                                p.message("Morgan Le Faye vanishes");
                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
        return (obj.getID() == 294) || ((obj.getID() == 287) && (item.getID() == ItemId.EXCALIBUR.id()));
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 294) {
                        if (item.getID() == ItemId.INSECT_REPELLANT.id()) {
                            Functions.___message(p, "you squirt insect repellant on the beehive", "You see bees leaving the hive");
                            if (!p.getCache().hasKey("squirt")) {
                                p.getCache().store("squirt", true);
                            }
                        } else
                            if (item.getID() == ItemId.BUCKET.id()) {
                                Functions.___message(p, "You try to get some wax from the beehive");
                                if (p.getCache().hasKey("squirt")) {
                                    Functions.___message(p, "You get some wax from the hive", "The bees fly back to the hive as the repellant wears off");
                                    Functions.removeItem(p, ItemId.BUCKET.id(), 1);
                                    Functions.addItem(p, ItemId.WAX_BUCKET.id(), 1);
                                    p.getCache().remove("squirt");
                                } else {
                                    p.message("Suddenly bees fly out of the hive and sting you");
                                    p.damage(2);
                                }
                            }

                    } else
                        if ((obj.getID() == 287) && (item.getID() == ItemId.EXCALIBUR.id())) {
                            if (p.getQuestStage(quest) == 4) {
                                Functions.___message(p, "The crystal shatters");
                                p.getWorld().unregisterGameObject(obj);
                                p.getWorld().delayedSpawnObject(obj.getLoc(), 30000);
                                Npc merlin = Functions.getNearestNpc(p, NpcId.MERLIN_CRYSTAL.id(), 5);
                                Functions.___npcTalk(p, merlin, "Thankyou thankyou", "It's not fun being trapped in a giant crystal", "Go speak to King Arthur, I'm sure he'll reward you");
                                p.message("You have set Merlin free now talk to king arthur");
                                p.updateQuestStage(quest, 5);
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
        return (obj.getX() == 277) && (obj.getY() == 632);
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getX() == 277) && (obj.getY() == 632)) {
                        if ((p.getQuestStage(quest) != 3) && (!p.getCache().hasKey("lady_test"))) {
                            Functions.doDoor(obj, p);
                            return null;
                        } else {
                            Npc beggar = Functions.getNearestNpc(p, NpcId.BEGGAR.id(), 5);
                            if (beggar == null) {
                                beggar = Functions.spawnNpc(NpcId.BEGGAR.id(), 276, 631, 60000, p);
                            }
                            Functions.sleep(600);
                            if (beggar != null) {
                                Functions.___npcTalk(p, beggar, "Please sir, me and my family are starving", "Could you possibly give me a loaf of bread?");
                                int opt = Functions.___showMenu(p, beggar, "Yes certainly", "No I don't have any bread with me");
                                if (opt == 0) {
                                    if (!p.getInventory().hasItemId(ItemId.BREAD.id())) {
                                        Functions.___playerTalk(p, beggar, "Except that I don't have any bread at the moment");
                                        Functions.___npcTalk(p, beggar, "Well if you get some you know where to come");
                                        Functions.doDoor(obj, p);
                                    } else
                                        if (p.getInventory().hasItemId(ItemId.BREAD.id())) {
                                            Functions.___message(p, "You give the bread to the beggar");
                                            Functions.removeItem(p, ItemId.BREAD.id(), 1);
                                            Functions.___npcTalk(p, beggar, "Thankyou very much");
                                            if (p.getCache().hasKey("lady_test")) {
                                                p.message("The beggar has turned into the lady of the lake!");
                                                Npc lady = Functions.transform(beggar, NpcId.LADY_GROUND.id(), false);
                                                Functions.___npcTalk(p, lady, "Well done you have passed my test", "Here is Excalibur, guard it well");
                                                Functions.addItem(p, ItemId.EXCALIBUR.id(), 1);
                                                if (p.getCache().hasKey("lady_test")) {
                                                    p.getCache().remove("lady_test");
                                                }
                                                lady.remove();
                                            }
                                        }

                                } else
                                    if (opt == 1) {
                                        Functions.___npcTalk(p, beggar, "Well if you get some you know where to come");
                                        Functions.doDoor(obj, p);
                                        beggar.remove();
                                    }

                            }
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockDrop(Player p, Item i) {
        return ((((p.getX() == 448) && (p.getY() == 435)) && (i.getID() == ItemId.BAT_BONES.id())) && p.getCache().hasKey("magic_words")) && Functions.hasItem(p, ItemId.LIT_BLACK_CANDLE.id());
    }

    @Override
    public GameStateEvent onDrop(Player p, Item i) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    p.getInventory().remove(i);
                    Npc n = Functions.spawnNpc(p.getWorld(), NpcId.THRANTAX.id(), p.getX(), p.getY(), 300000);
                    n.displayNpcTeleportBubble(n.getX(), n.getY());
                    p.message("Suddenly a demon appears");
                    Functions.___playerTalk(p, null, "Now what were those magic words?");
                    int opt = // do not send over
                    Functions.___showMenu(p, n, false, "Snarthtrick Candanto Termon", "Snarthon Candtrick Termanto", "Snarthanto Candon Termtrick");
                    if (opt == 1) {
                        Functions.___playerTalk(p, n, "Snarthon Candtrick Termanto");
                        Functions.___npcTalk(p, n, "rarrrrgh", "You have me in your control", "What do you wish of me?", "So that I may return to the nether regions");
                        Functions.___playerTalk(p, n, "I wish to free Merlin from his giant crystal");
                        Functions.___npcTalk(p, n, "rarrrrgh", "It is done, you can now shatter Merlins crystal with Excalibur");
                        n.remove();
                        p.updateQuestStage(quest, 4);
                        return null;
                    }
                    if (opt == 0) {
                        Functions.___playerTalk(p, n, "Snarthtrick Candato Termon");
                    } else
                        if (opt == 2) {
                            Functions.___playerTalk(p, n, "Snarthanto Candon Termtrick");
                        }

                    n.getUpdateFlags().setChatMessage(new ChatMessage(n, "rarrrrgh", p));
                    n.startCombat(p);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return (((n.getID() == NpcId.KING_ARTHUR.id()) && (!p.getLocation().inVarrock())) || (n.getID() == NpcId.SIR_GAWAIN.id())) || (n.getID() == NpcId.SIR_LANCELOT.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((n.getID() == NpcId.KING_ARTHUR.id()) && (!p.getLocation().inVarrock())) {
                        switch (p.getQuestStage(Quests.THE_HOLY_GRAIL)) {
                            case 1 :
                            case 2 :
                            case 3 :
                            case 5 :
                                Functions.___npcTalk(p, n, "How goes thy quest?");
                                if (Functions.hasItem(p, ItemId.HOLY_GRAIL.id())) {
                                    Functions.___playerTalk(p, n, "I have retrieved the grail");
                                    Functions.___npcTalk(p, n, "wow incredible you truly are a splendid knight");
                                    Functions.removeItem(p, ItemId.HOLY_GRAIL.id(), 1);
                                    p.sendQuestComplete(Quests.THE_HOLY_GRAIL);
                                } else {
                                    Functions.___playerTalk(p, n, "I am making progress", "But I have not recovered the grail yet");
                                    Functions.___npcTalk(p, n, "Well the grail is very elusive", "It may take some perserverance");
                                    if (p.getQuestStage(Quests.THE_HOLY_GRAIL) == 1) {
                                        Functions.___npcTalk(p, n, "As I said before speak to Merlin", "in the workshop by the library");
                                    }
                                }
                                return null;
                            case 4 :
                                Functions.___playerTalk(p, n, "Hello, do you have a knight named Sir Percival?");
                                Functions.___npcTalk(p, n, "Ah yes I remember, young percival", "He rode off on a quest a couple of months ago", "We are getting a bit worried, he's not back yet", "He was going to try and recover the golden boots of Arkaneeses");
                                Functions.___playerTalk(p, n, "Any idea which way that would be?");
                                Functions.___npcTalk(p, n, "Not exactly", "We discovered, some magic golden feathers", "They are said to point the way to the boots", "they certainly point somewhere", "just blowing gently on them", "Will make them show the way to go");
                                if (!Functions.hasItem(p, ItemId.MAGIC_GOLDEN_FEATHER.id())) {
                                    p.message("King arthur gives you a feather");
                                    Functions.addItem(p, ItemId.MAGIC_GOLDEN_FEATHER.id(), 1);
                                }
                                return null;
                            case -1 :
                                Functions.___npcTalk(p, n, "Thankyou for retrieving the grail", "You shall be long remembered", "As one of the greatest heros", "Amongst the knights of the round table");
                                return null;
                        }
                        /**
                         * KING ARTHUR MERLINS CRYSTAL*
                         */
                        switch (p.getQuestStage(quest)) {
                            case 0 :
                            case 1 :
                            case 2 :
                            case 3 :
                            case 4 :
                                Functions.___npcTalk(p, n, "Welcome to the court of King Arthur");
                                Functions.___npcTalk(p, n, "I am King Arthur");
                                int option = // do not send over
                                Functions.___showMenu(p, n, false, "I want to become a knight of the round table", "So what are you doing in Runescape?", "Thankyou very much");
                                if (option == 0) {
                                    Functions.___playerTalk(p, n, "I want to become a knight of the round table");
                                    Functions.___npcTalk(p, n, "Well I think you need to go on a quest to prove yourself worthy", "My knights like a good quest", "Unfortunately our current quest is to rescue Merlin", "Back in England he got himself trapped in some sort of magical Crystal", "We've moved him from the cave we found him in", "He's upstairs in his tower");
                                    Functions.___playerTalk(p, n, "I will see what I can do then");
                                    Functions.___npcTalk(p, n, "Talk to my knights if you need any help");
                                    if (p.getQuestStage(quest) == 0) {
                                        p.updateQuestStage(Quests.MERLINS_CRYSTAL, 1);
                                    }
                                } else
                                    if (option == 1) {
                                        Functions.___playerTalk(p, n, "So what are you doing in Runescape");
                                        Functions.___npcTalk(p, n, "Well legend says we will return to Britain in it's time of greatest need");
                                        Functions.___npcTalk(p, n, "But that's not for quite a while");
                                        Functions.___npcTalk(p, n, "So we've moved the whole outfit here for now");
                                        Functions.___npcTalk(p, n, "We're passing the time in Runescape");
                                    } else
                                        if (option == 2) {
                                            Functions.___playerTalk(p, n, "thankyou very much");
                                        }


                                break;
                            case 5 :
                                Functions.___playerTalk(p, n, "I have freed Merlin from his crystal");
                                Functions.___npcTalk(p, n, "Ah a good job well done", "I knight thee", "You are now a knight of the round table");
                                p.sendQuestComplete(Quests.MERLINS_CRYSTAL);
                                break;
                            case -1 :
                                Functions.___playerTalk(p, n, "Now i am a knight of the round table", "Do you have anymore quests for me?");
                                Functions.___npcTalk(p, n, "Aha, I'm glad you are here", "I am sending out various knights on an important quest", "I was wondering if you too would like to take up this quest?");
                                int q = Functions.___showMenu(p, n, "Tell me of this quest", "I am weary of questing for the time being");
                                if (q == 0) {
                                    /**
                                     * ***************************
                                     */
                                    /**
                                     * START OF THE HOLY GRAIL QUEST*
                                     */
                                    /**
                                     * ***************************
                                     */
                                    Functions.___npcTalk(p, n, "Well we recently found out", "The holy grail has passed into the runescape world", "This is most fortuitous", "None of my knights ever did return with it last time", "Now we have the opportunity to give it another go", "Maybe this time we will have more luck");
                                    int startHoly = Functions.___showMenu(p, n, "I'd enjoy trying that", "I may come back and try that later");
                                    if (startHoly == 0) {
                                        Functions.___npcTalk(p, n, "Go speak to Merlin", "He may be able to give a better clue as to where it is", "Now you have freed him from the crystal", "He has set up his workshop in the room next to the library");
                                        p.updateQuestStage(Quests.THE_HOLY_GRAIL, 1);
                                    } else
                                        if (startHoly == 1) {
                                            Functions.___npcTalk(p, n, "Be sure that you come speak to me soon then");
                                        }

                                } else
                                    if (q == 1) {
                                        Functions.___npcTalk(p, n, "Maybe later then");
                                        Functions.___playerTalk(p, n, "Maybe so");
                                    }

                                break;
                        }
                    } else
                        if (n.getID() == NpcId.SIR_GAWAIN.id()) {
                            if (p.getCache().hasKey("talked_to_gawain")) {
                                Functions.___npcTalk(p, n, "Good day to you sir");
                                int option = Functions.___showMenu(p, n, "Any idea how to get into Morgan Le Faye's stronghold?", "Hello again");
                                if (option == 0) {
                                    Functions.___npcTalk(p, n, "No you've got me stumped there");
                                }
                                return null;
                            }
                            switch (p.getQuestStage(quest)) {
                                case 0 :
                                    Functions.___npcTalk(p, n, "Good day to you sir");
                                    int opt = // do not send over
                                    Functions.___showMenu(p, n, false, "Good day", "Know you of any quests Sir knight?");
                                    if (opt == 0) {
                                        Functions.___playerTalk(p, n, "good day");
                                    } else
                                        if (opt == 1) {
                                            Functions.___playerTalk(p, n, "Know you of any quests sir knight?");
                                            Functions.___npcTalk(p, n, "The king is the man to talk to if you want a quest");
                                        }

                                    break;
                                case 1 :
                                    Functions.___npcTalk(p, n, "Good day to you sir");
                                    int option = // do not send over
                                    Functions.___showMenu(p, n, false, "Good day", "Any ideas on how to get Merlin out that crystal?", "Do you know how Merlin got trapped");
                                    if (option == 0) {
                                        Functions.___playerTalk(p, n, "good day");
                                    } else
                                        if (option == 1) {
                                            Functions.___playerTalk(p, n, "Any ideas on how to get Merlin out that crystal?");
                                            Functions.___npcTalk(p, n, "I'm a little stumped myself", "We've tried opening it with anything and everything");
                                        } else
                                            if (option == 2) {
                                                Functions.___playerTalk(p, n, "Do you know how Merlin got trapped?");
                                                Functions.___npcTalk(p, n, "I would guess this is the work of the evil Morgan Le Faye");
                                                Functions.___playerTalk(p, n, "And where can I find her?");
                                                Functions.___npcTalk(p, n, "She lives in her stronghold to the south of here");
                                                Functions.___npcTalk(p, n, "Guarded by some renegade knights led by Sir Mordred");
                                                p.getCache().store("talked_to_gawain", true);
                                                int sub_option = Functions.___showMenu(p, n, "Any idea how to get into Morgan Le Faye's stronghold?", "Thankyou for the information");
                                                if (sub_option == 0) {
                                                    Functions.___npcTalk(p, n, "No you've got me stumped there");
                                                }
                                            }


                                    break;
                                case 2 :
                                    Functions.___npcTalk(p, n, "Good day to you sir");
                                    int op = // do not send over
                                    Functions.___showMenu(p, n, false, "Good day", "Know you of any quests Sir knight?");
                                    if (op == 0) {
                                        Functions.___playerTalk(p, n, "good day");
                                    } else
                                        if (op == 1) {
                                            Functions.___playerTalk(p, n, "Know you of any quests sir knight?");
                                            Functions.___npcTalk(p, n, "The king is the man to talk to if you want a quest");
                                        }

                                    break;
                                case -1 :
                                    Functions.___npcTalk(p, n, "Good day to you sir");
                                    int ope = // do not send over
                                    Functions.___showMenu(p, n, false, "Good day", "Know you of any quests Sir knight?");
                                    if (ope == 0) {
                                        Functions.___playerTalk(p, n, "good day");
                                    } else
                                        if (ope == 1) {
                                            Functions.___playerTalk(p, n, "Know you of any quests sir knight?");
                                            Functions.___npcTalk(p, n, "I think you've done the main quest we were on right now");
                                        }

                                    break;
                            }
                        } else
                            if (n.getID() == NpcId.SIR_LANCELOT.id()) {
                                switch (p.getQuestStage(quest)) {
                                    case 0 :
                                    case 1 :
                                        Functions.___npcTalk(p, n, "Greetings I am Sir Lancelot the greatest knight in the land", "What do you want?");
                                        if (p.getCache().hasKey("talked_to_gawain")) {
                                            int opt = Functions.___showMenu(p, n, "I want to get Merlin out of the crystal", "You're a little full of yourself aren't you?", "Any ideas on how to get into Morgan Le Faye's stronghold?");
                                            if (opt == 0) {
                                                Functions.___npcTalk(p, n, "Well the knights of the round table can't manage it", "I can't see how a commoner like you could succeed where we have failed");
                                            } else
                                                if (opt == 1) {
                                                    Functions.___npcTalk(p, n, "I have every right to be proud of myself", "My prowess in battle is world renowned");
                                                } else
                                                    if (opt == 2) {
                                                        Functions.___npcTalk(p, n, "That stronghold is built in a strong defensive position", "It's on a big rock sticking out into the sea", "There are two ways in that I know of, the large heavy front doors", "And the sea entrance, only penetrable by boat", "They take all their deliveries by boat");
                                                        p.updateQuestStage(Quests.MERLINS_CRYSTAL, 2);
                                                        p.getCache().remove("talked_to_gawain");
                                                    }


                                        } else {
                                            int opt = Functions.___showMenu(p, n, "I want to get Merlin out of the crystal", "You're a little full of yourself aren't you?");
                                            if (opt == 0) {
                                                Functions.___npcTalk(p, n, "Well the knights of the round table can't manage it", "I can't see how a commoner like you could succeed where we have failed");
                                            } else
                                                if (opt == 1) {
                                                    Functions.___npcTalk(p, n, "I have every right to be proud of myself", "My prowess in battle is world renowned");
                                                }

                                        }
                                        break;
                                    case 2 :
                                    case -1 :
                                        Functions.___npcTalk(p, n, "Greetings I am Sir Lancelot the greatest knight in the land", "What do you want?");
                                        int opt = Functions.___showMenu(p, n, "You're a little full of yourself aren't you?", "I seek a quest");
                                        if (opt == 0) {
                                            Functions.___npcTalk(p, n, "I have every right to be proud of myself", "My prowess in battle is world renowned");
                                        } else
                                            if (opt == 1) {
                                                Functions.___npcTalk(p, n, "Leave questing to the profesionals", "Such as myself");
                                            }

                                        break;
                                }
                            }


                    return null;
                });
            }
        };
    }
}

