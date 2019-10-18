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
import com.openrsc.server.model.world.World;
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


public class FightArena implements QuestInterface , InvUseOnObjectListener , ObjectActionListener , PlayerKilledNpcListener , TalkToNpcListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener , PlayerKilledNpcExecutiveListener , TalkToNpcExecutiveListener {
    private static final int GUARDS_CUPBOARD_OPEN = 382;

    private static final int GUARDS_CUPBOARD_CLOSED = 381;

    @Override
    public int getQuestId() {
        return Quests.FIGHT_ARENA;
    }

    @Override
    public String getQuestName() {
        return "Fight Arena (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        Functions.___message(p, "you have completed the fight arena quest", "Lady Servil gives you 1000 gold coins", "you gain two quest points");
        Functions.addItem(p, ItemId.COINS.id(), 1000);
        p.message("@gre@You haved gained 2 quest points!");
        int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.FIGHT_ARENA);
        // keep order kosher
        int[] skillIDs = new int[]{ Skills.ATTACK, Skills.THIEVING };
        for (int i = 0; i < skillIDs.length; i++) {
            questData[Quests.MAPIDX_SKILL] = skillIDs[i];
            Functions.incQuestReward(p, questData, i == (skillIDs.length - 1));
        }
        p.getCache().remove("freed_servil");
        p.getCache().remove("killed_ogre");
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return DataConversions.inArray(new int[]{ NpcId.LADY_SERVIL.id(), NpcId.LOCAL.id(), NpcId.GUARD_KHAZARD_BRIBABLE.id(), NpcId.GUARD_KHAZARD_BYPRISONER.id(), NpcId.GUARD_KHAZARD_MACE.id(), NpcId.JEREMY_SERVIL.id(), NpcId.HENGRAD.id() }, n.getID());
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return DataConversions.inArray(new int[]{ NpcId.KHAZARD_SCORPION.id(), NpcId.KHAZARD_OGRE.id(), NpcId.BOUNCER.id(), NpcId.GENERAL_KHAZARD.id() }, n.getID());
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    n.killedBy(p);
                    n.remove();
                    if (n.getID() == NpcId.KHAZARD_OGRE.id()) {
                        if (!p.getCache().hasKey("killed_ogre")) {
                            p.getCache().store("killed_ogre", true);
                        }
                        p.message("You kill the ogre");
                        Functions.___message(p, "Jeremy's father survives");
                        Npc justin = Functions.getNearestNpc(p, NpcId.JUSTIN_SERVIL.id(), 15);
                        if (justin != null) {
                            Functions.___npcTalk(p, justin, "You saved my life and my son's", "I am eternally in your debt brave traveller");
                        }
                        Functions.spawnNpc(p.getWorld(), NpcId.GENERAL_KHAZARD.id(), 613, 708, 60000);
                        Functions.sleep(1000);
                        Npc general = Functions.getNearestNpc(p, NpcId.GENERAL_KHAZARD.id(), 8);
                        if (general != null) {
                            Functions.___npcTalk(p, general, "Haha, well done, well done that was rather entertaining", "I'm the great General Khazard", "And the two men you just saved are my property");
                            Functions.___playerTalk(p, general, "They belong to no one");
                            Functions.___npcTalk(p, general, "I suppose we could find some arrangement", "for their freedom... hmmmm");
                            Functions.___playerTalk(p, general, "What do you mean?");
                            Functions.___npcTalk(p, general, "I'll let them go but you must stay and fight for me", "You'll make me double the gold if you manage to last a few fights", "Guards! take him away!");
                            p.message("Khazard's men have locked you in a cell");
                            p.teleport(609, 715, false);
                        }
                    } else
                        if (n.getID() == NpcId.KHAZARD_SCORPION.id()) {
                            p.message("You defeat the scorpion");
                            Functions.spawnNpc(p.getWorld(), NpcId.GENERAL_KHAZARD.id(), 613, 708, 30000);
                            Functions.sleep(1000);
                            Npc generalAgain = Functions.getNearestNpc(p, NpcId.GENERAL_KHAZARD.id(), 15);
                            if (generalAgain != null) {
                                Functions.___npcTalk(p, generalAgain, "Not bad, not bad at all", "I think you need a tougher challenge", "Time for my puppy", "Guards, guards bring on bouncer");
                            }
                            Functions.___message(p, "From above you hear a voice...", "Ladies and gentlemen!", "Todays second round");
                            Functions.spawnNpc(p.getWorld(), NpcId.BOUNCER.id(), 613, 708, 240000);
                            p.message("between the Outsider and bouncer");
                            Npc bouncer = p.getWorld().getNpcById(NpcId.BOUNCER.id());
                            if (bouncer != null) {
                                bouncer.setChasing(p);
                            }
                        } else
                            if (n.getID() == NpcId.BOUNCER.id()) {
                                p.message("You defeat bouncer");
                                Functions.spawnNpc(p.getWorld(), NpcId.GENERAL_KHAZARD.id(), 613, 708, 60000 * 2);
                                Functions.sleep(1000);
                                Npc generalAgainAgain = Functions.getNearestNpc(p, NpcId.GENERAL_KHAZARD.id(), 15);
                                if (generalAgainAgain != null) {
                                    Functions.___npcTalk(p, generalAgainAgain, "nooooo! bouncer, how dare you?", "you've taken the life of my only friend!");
                                    p.message("Khazard looks very angry");
                                    Functions.___npcTalk(p, generalAgainAgain, "now you'll suffer traveller, prepare to meet your maker");
                                    Functions.___message(p, "No, he doesn't look happy at all", "You might want to run for it", "Go back to lady servil to claim your reward");
                                    generalAgainAgain.setChasing(p);
                                }
                                p.updateQuestStage(getQuestId(), 3);
                            } else
                                if (n.getID() == NpcId.GENERAL_KHAZARD.id()) {
                                    p.message("You kill general khazard");
                                    p.message("but he shall return");
                                }



                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.HENGRAD.id()) {
                        if ((p.getQuestStage(getQuestId()) == 2) && p.getCache().hasKey("killed_ogre")) {
                            Functions.___playerTalk(p, n, "Are you ok stranger?");
                            Functions.___npcTalk(p, n, "I'm fine thanks, my name's Hengrad", "So khazard got his hands on you too?");
                            Functions.___playerTalk(p, n, "I'm afraid so");
                            Functions.___npcTalk(p, n, "If you're lucky you may last as long as me");
                            Functions.___playerTalk(p, n, "How long have you been here?");
                            Functions.___npcTalk(p, n, "I've been in khazard's prisons ever since i can remember", "I was a child when his men kidnapped me", "My whole life has been spent killing and fighting", "All in the hope that one day I'll escape");
                            Functions.___playerTalk(p, n, "Don't give up");
                            Functions.___npcTalk(p, n, "Thanks friend..wait..sshh,the guard is coming", "He'll be taking one of us to the arena");
                            Functions.___message(p, "A guard approaches the cell");
                            Functions.___npcTalk(p, n, "Looks like it's you,good luck friend");
                            Functions.___message(p, "The guard leads you to the arena", "For your battle");
                            p.teleport(609, 705, false);
                            Functions.___message(p, "From above you hear a voice...", "Ladies and gentlemen!", "Todays first fight between the outsider", "And everyone's favorite scorpion has begun");
                            Functions.spawnNpc(p.getWorld(), NpcId.KHAZARD_SCORPION.id(), 613, 708, 120000);
                            Npc scorp = p.getWorld().getNpcById(NpcId.KHAZARD_SCORPION.id());
                            if (scorp != null) {
                                scorp.setChasing(p);
                            }
                        }
                    } else
                        if (n.getID() == NpcId.JEREMY_SERVIL.id()) {
                            if ((p.getQuestStage(getQuestId()) >= 3) || (p.getQuestStage(getQuestId()) == (-1))) {
                                p.message("You need to kill the creatures in the arena");
                                return null;
                            }
                            if ((p.getQuestStage(getQuestId()) == 2) && p.getCache().hasKey("freed_servil")) {
                                Functions.___playerTalk(p, n, "Jeremy where's your father?");
                                Functions.___npcTalk(p, n, "Quick, help him! that beast will kill him", "He can't fight! he's too old!");
                                Functions.___message(p, "You see Jeremy's father Justin", "Trying to escape an ogre");
                                Functions.___npcTalk(p, n, "Please help him!");
                                Functions.spawnNpc(p.getWorld(), NpcId.KHAZARD_OGRE.id(), 613, 708, 60000 * 2);
                            }
                        } else
                            if (n.getID() == NpcId.GUARD_KHAZARD_MACE.id()) {
                                if ((p.getQuestStage(getQuestId()) == 3) || (p.getQuestStage(getQuestId()) == (-1))) {
                                    Functions.___playerTalk(p, n, "hello");
                                    Functions.___npcTalk(p, n, "you're the outsider who killed bouncer", "die traitor!");
                                    n.setChasing(p);
                                    return null;
                                }
                                Functions.___playerTalk(p, n, "hello");
                                if (p.getInventory().wielding(ItemId.KHAZARD_HELMET.id()) && p.getInventory().wielding(ItemId.KHAZARD_CHAINMAIL.id())) {
                                    Functions.___npcTalk(p, n, "can i help you stranger?", "oh.. you're a guard as well", "that's ok then", "we don't like outsiders around here");
                                } else {
                                    Functions.___npcTalk(p, n, "i don't know you stranger", "get of our land");
                                    n.setChasing(p);
                                }
                            } else
                                if (n.getID() == NpcId.GUARD_KHAZARD_BYPRISONER.id()) {
                                    if (p.getQuestStage(getQuestId()) >= 2) {
                                        if (p.getInventory().wielding(ItemId.KHAZARD_HELMET.id()) && p.getInventory().wielding(ItemId.KHAZARD_CHAINMAIL.id())) {
                                            Functions.___playerTalk(p, n, "hello");
                                            Functions.___npcTalk(p, n, "hello, hope you're keeping busy?");
                                            Functions.___playerTalk(p, n, "of course");
                                        } else {
                                            Functions.___npcTalk(p, n, "this area is restricted, leave now", "OUT and don't come back!");
                                            Functions.___message(p, "the guard has thrown you out");
                                            p.teleport(602, 717, false);
                                        }
                                        return null;
                                    }
                                    Functions.___playerTalk(p, n, "long live General Khazard");
                                    Functions.___npcTalk(p, n, "erm.. yes.. soldier", "i take it you're new");
                                    Functions.___playerTalk(p, n, "you could say that");
                                    Functions.___npcTalk(p, n, "Khazard died two hundred years ago", "however his dark spirit remains", "in the form of the undead maniac...General Khazard", "remember he is your master, always watching", "you got that, newbie?");
                                    Functions.___playerTalk(p, n, "undead, maniac, master, got it - loud and clear");
                                } else
                                    if (n.getID() == NpcId.GUARD_KHAZARD_BRIBABLE.id()) {
                                        if ((p.getQuestStage(getQuestId()) == 3) || (p.getQuestStage(getQuestId()) == (-1))) {
                                            if (p.getInventory().wielding(ItemId.KHAZARD_HELMET.id()) && p.getInventory().wielding(ItemId.KHAZARD_CHAINMAIL.id())) {
                                                Functions.___playerTalk(p, n, "hello");
                                                Functions.___npcTalk(p, n, "less chat and more work", "i can't stand lazy guards");
                                            } else {
                                                Functions.___npcTalk(p, n, "this area is restricted, leave now", "OUT and don't come back!");
                                                Functions.___message(p, "the guard has thrown you out");
                                                p.teleport(621, 698, false);
                                            }
                                            return null;
                                        }
                                        if (p.getCache().hasKey("guard_sleeping") || p.getCache().hasKey("freed_servil")) {
                                            Functions.___npcTalk(p, n, "please, let me rest");
                                            return null;
                                        }
                                        if (p.getQuestStage(getQuestId()) == 2) {
                                            Functions.___playerTalk(p, n, "hello again");
                                            Functions.___npcTalk(p, n, "bored, bored, bored", "you would think the slaves would be more entertaining", "selfish.. the lot of 'em");
                                            if (Functions.hasItem(p, ItemId.KHALI_BREW.id())) {
                                                Functions.___playerTalk(p, n, "do you still fancy a drink?");
                                                Functions.___npcTalk(p, n, "I really shouldn't... ok then, just the one", "this stuff looks good");
                                                Functions.removeItem(p, ItemId.KHALI_BREW.id(), 1);
                                                Functions.___message(p, "the guard takes a mouthful of drink");
                                                Functions.___npcTalk(p, n, "blimey this stuff is pretty good", "it's not too strong is it?");
                                                Functions.___playerTalk(p, n, "no, not at all, you'll be fine");
                                                Functions.___message(p, "the guard finishes the bottle");
                                                Functions.___npcTalk(p, n, "that is some gooood stuff", "yeah... woooh... yeah");
                                                Functions.___message(p, "the guard seems quite typsy");
                                                Functions.___playerTalk(p, n, "are you alright?");
                                                Functions.___npcTalk(p, n, "yeesshh, ooohh, 'hiccup'", "maybe i should relax for a while....");
                                                Functions.___playerTalk(p, n, "good idea, i'll look after the prisoners");
                                                Functions.___npcTalk(p, n, "ok then, here, 'hiccup',", "take these keys", "any trouble you give 'em a good beating");
                                                Functions.___playerTalk(p, n, "no problem, i'll keep them in line");
                                                Functions.___npcTalk(p, n, "zzzzz zzzzz zzzzz");
                                                Functions.___message(p, "the guard is asleep");
                                                p.getCache().store("guard_sleeping", true);
                                                Functions.addItem(p, ItemId.KHAZARD_CELL_KEYS.id(), 1);
                                            }
                                            return null;
                                        }
                                        Functions.___playerTalk(p, n, "long live General Khazard");
                                        Functions.___npcTalk(p, n, "erm.. yes.. quite right", "have you come to laugh at the fight slaves?", "i used to really enjoy it", "but after a while they become quite boring", "now i just want a decent drink", "mind you, too much khali brew and i'll fall asleep");
                                    } else
                                        if (n.getID() == NpcId.LOCAL.id()) {
                                            if (p.getQuestStage(getQuestId()) == (-1)) {
                                                Functions.___playerTalk(p, n, "hello");
                                                Functions.___npcTalk(p, n, "please, i haven't done anything");
                                                Functions.___playerTalk(p, n, "what?");
                                                Functions.___npcTalk(p, n, "i love General Khazard, please believe me");
                                                return null;
                                            }
                                            if (p.getQuestStage(getQuestId()) == 3) {
                                                Functions.___playerTalk(p, n, "hello");
                                                Functions.___npcTalk(p, n, "hello stranger", "Khazard's got some great fights lined up this week", "i can't wait");
                                                return null;
                                            }
                                            Functions.___playerTalk(p, n, "hello");
                                            Functions.___npcTalk(p, n, "are you enjoying the arena?", "i heard the servil family are fighting soon", "should be very entertaining");
                                        } else
                                            if (n.getID() == NpcId.LADY_SERVIL.id()) {
                                                switch (p.getQuestStage(quest)) {
                                                    case 0 :
                                                        Functions.___playerTalk(p, n, "hi there, looks like you're in some trouble");
                                                        Functions.___npcTalk(p, n, "oh, i wish this broken cart was my only problem");
                                                        Functions.___npcTalk(p, n, "sob.. i've got to find my family.. sob");
                                                        int first = Functions.___showMenu(p, n, "I hope you can, good luck", "can i help you?");
                                                        if (first == 0) {
                                                            Functions.___npcTalk(p, n, " sob..sob");
                                                        } else
                                                            if (first == 1) {
                                                                Functions.___npcTalk(p, n, "sob.. would you? please?", "i'm Lady Servil, my husband's Sir Servil", "we were travelling north with my son", "when we were ambushed by general Khazard's men");
                                                                Functions.___playerTalk(p, n, "general Khazard? i haven't heard of him");
                                                                Functions.___npcTalk(p, n, "he's been after me ever since i", "declined his hand in marriage", "now he's kidnapped my husband and son", "to fight slaves in his", "battle arena, to the south of here", "i hate to think what he'll do to them", "he's a sick, twisted man");
                                                                Functions.___playerTalk(p, n, "I'll try my best to return your family");
                                                                Functions.___npcTalk(p, n, "please do, i'm a wealthy woman", "and can reward you handsomely", "i'll be waiting for you here");
                                                                p.updateQuestStage(getQuestId(), 1);
                                                            }

                                                        break;
                                                    case 1 :
                                                    case 2 :
                                                        Functions.___playerTalk(p, n, "hello Lady Servil");
                                                        Functions.___npcTalk(p, n, "Brave traveller, please..bring back my family");
                                                        break;
                                                    case 3 :
                                                        Functions.___playerTalk(p, n, "Lady Servil");
                                                        Functions.___npcTalk(p, n, "you're alive, i thought Khazard's men took you", "My son and husband are safe and recovering at home", "without you they would certainly be dead", "I am truly grateful for your service", "all i can offer in return is material wealth", "please take these coins and enjoy");
                                                        p.sendQuestComplete(Quests.FIGHT_ARENA);
                                                        break;
                                                    case -1 :
                                                        Functions.___playerTalk(p, n, "Hello lady Servil");
                                                        Functions.___npcTalk(p, n, "oh hello my dear", "my husband and son are resting", "while i wait for the cart fixer");
                                                        Functions.___playerTalk(p, n, "hope he's not too long");
                                                        Functions.___npcTalk(p, n, "thanks again for everything");
                                                        break;
                                                }
                                            }






                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return ((((obj.getID() == FightArena.GUARDS_CUPBOARD_OPEN) || (obj.getID() == FightArena.GUARDS_CUPBOARD_CLOSED)) && ((obj.getY() == 683) || (obj.getY() == 1623))) || ((obj.getID() == 371) && ((obj.getY() == 700) || (obj.getY() == 707)))) || ((obj.getID() == 371) && (obj.getY() == 716));
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((obj.getID() == FightArena.GUARDS_CUPBOARD_OPEN) || (obj.getID() == FightArena.GUARDS_CUPBOARD_CLOSED)) && ((obj.getY() == 683) || (obj.getY() == 1623))) {
                        if (command.equalsIgnoreCase("open")) {
                            Functions.openCupboard(obj, p, FightArena.GUARDS_CUPBOARD_OPEN);
                        } else
                            if (command.equalsIgnoreCase("close")) {
                                Functions.closeCupboard(obj, p, FightArena.GUARDS_CUPBOARD_CLOSED);
                            } else {
                                if (((!Functions.hasItem(p, ItemId.KHAZARD_CHAINMAIL.id())) && (!Functions.hasItem(p, ItemId.KHAZARD_HELMET.id()))) && (p.getQuestStage(getQuestId()) >= 1)) {
                                    p.message("You search the cupboard...");
                                    p.message("You find a khazard helmet");
                                    p.message("You find a khazard chainmail");
                                    Functions.addItem(p, ItemId.KHAZARD_CHAINMAIL.id(), 1);
                                    Functions.addItem(p, ItemId.KHAZARD_HELMET.id(), 1);
                                } else {
                                    p.message("You search the cupboard, but find nothing");
                                }
                            }

                    } else
                        if ((obj.getID() == 371) && ((obj.getY() == 700) || (obj.getY() == 707))) {
                            Npc joe = Functions.getNearestNpc(p, NpcId.FIGHTSLAVE_JOE.id(), 5);
                            if (joe != null) {
                                Functions.___playerTalk(p, joe, "are you ok?");
                                Functions.___npcTalk(p, joe, "spare me your fake pity", "I spit on Khazard's grave and all who do his bidding");
                            }
                            Npc kelvin = Functions.getNearestNpc(p, NpcId.FIGHTSLAVE_KELVIN.id(), 5);
                            if (kelvin != null) {
                                Functions.___playerTalk(p, kelvin, "hello there");
                                Functions.___npcTalk(p, kelvin, "get away, get away", "one day i'll have my revenge", "and i'll have all your heads!");
                            }
                        } else
                            if ((obj.getID() == 371) && (obj.getY() == 716)) {
                                if ((p.getCache().hasKey("freed_servil") || (p.getQuestStage(getQuestId()) == 3)) || (p.getQuestStage(getQuestId()) == (-1))) {
                                    p.message("You have already freed jeremy");
                                    return null;
                                }
                                Npc servil = Functions.getNearestNpc(p, NpcId.JEREMY_SERVIL.id(), 5);
                                Npc guard = Functions.getNearestNpc(p, NpcId.GUARD_KHAZARD_BYPRISONER.id(), 5);
                                if ((servil != null) && (guard != null)) {
                                    if (p.getCache().hasKey("guard_sleeping") && Functions.hasItem(p, ItemId.KHAZARD_CELL_KEYS.id())) {
                                        Functions.___playerTalk(p, servil, "Jeremy, look, I have the cell keys");
                                        Functions.___npcTalk(p, servil, "Wow! Please help me");
                                        Functions.___playerTalk(p, servil, "ok, keep quiet");
                                        Functions.___npcTalk(p, servil, "Set me free then we can find dad");
                                        Functions.___message(p, "You use your key to open the cell door", "The gate swings open");
                                        p.playSound("opendoor");
                                        p.getWorld().replaceGameObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), 181, obj.getDirection(), obj.getType()));
                                        p.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
                                        servil.teleport(605, 718);
                                        Functions.___playerTalk(p, servil, "There you go, now we need to find your father");
                                        Functions.___npcTalk(p, servil, "I overheard a guard talking", "I think they've taken him to the arena");
                                        Functions.___playerTalk(p, servil, "OK we'd better hurry");
                                        Functions.___npcTalk(p, servil, " I'll run ahead");
                                        servil.remove();
                                        p.getCache().store("freed_servil", true);
                                        p.getCache().remove("guard_sleeping");
                                        Functions.___npcTalk(p, guard, "What are you doing?", "It's an imposter!");
                                        Functions.sleep(1000);
                                        guard.setChasing(p);
                                        return null;
                                    }
                                    Functions.___npcTalk(p, servil, "I'm Jeremy Servil", "Please sir, don't hurt me");
                                    Functions.___playerTalk(p, servil, "I'm here to help", "Where do they keep the keys?");
                                    Functions.___npcTalk(p, servil, "The guard keeps them.. always");
                                    p.updateQuestStage(getQuestId(), 2);
                                }
                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
        return ((obj.getID() == 371) && (obj.getY() == 716)) && (item.getID() == ItemId.KHAZARD_CELL_KEYS.id());
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((obj.getID() == 371) && (obj.getY() == 716)) && (item.getID() == ItemId.KHAZARD_CELL_KEYS.id())) {
                        p.message("To unlock the gate, left click on it");
                    }
                    return null;
                });
            }
        };
    }
}

