package com.openrsc.server.plugins.quests.members.touristtrap;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.IndirectTalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.IndirectTalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TouristTrap implements QuestInterface , IndirectTalkToNpcListener , InvUseOnNpcListener , NpcCommandListener , ObjectActionListener , PlayerAttackNpcListener , PlayerKilledNpcListener , PlayerMageNpcListener , PlayerRangeNpcListener , TalkToNpcListener , WallObjectActionListener , IndirectTalkToNpcExecutiveListener , InvUseOnNpcExecutiveListener , NpcCommandExecutiveListener , ObjectActionExecutiveListener , PlayerAttackNpcExecutiveListener , PlayerKilledNpcExecutiveListener , PlayerMageNpcExecutiveListener , PlayerRangeNpcExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    private static final Logger LOGGER = LogManager.getLogger(TouristTrap.class);

    /**
     * Player isWielding
     */
    private static final int[] restricted = new int[]{ 0, 1, 3, 4, 5, 6, 7, 9 };

    public static final int[] allow = new int[]{ ItemId.DESERT_ROBE.id(), ItemId.DESERT_SHIRT.id(), ItemId.METAL_KEY.id(), ItemId.SLAVES_ROBE_BOTTOM.id(), ItemId.SLAVES_ROBE_TOP.id() };

    /**
     * Quest Objects
     */
    private static int STONE_GATE = 916;

    private static int IRON_GATE = 932;

    private static int JAIL_DOOR = 177;

    private static int WINDOW = 178;

    private static int ROCK_1 = 953;

    private static int WOODEN_DOORS = 958;

    private static int DESK = ItemId.SLAVES_ROBE_TOP.id();

    private static int BOOKCASE = 1004;

    private static int CAPTAINS_CHEST = 1005;

    /**
     * Quest WallObjects
     */
    private static int TENT_DOOR_1 = 198;

    private static int TENT_DOOR_2 = 196;

    private static int CAVE_JAIL_DOOR = 180;

    private static int STURDY_IRON_GATE = 200;

    private ArrayList<Integer> wieldPos = new ArrayList<>();

    private ArrayList<Integer> allowed = new ArrayList<>();

    @Override
    public int getQuestId() {
        return Quests.TOURIST_TRAP;
    }

    @Override
    public String getQuestName() {
        return "Tourist trap (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        p.message("");
        Functions.sleep(650);
        p.message("@yel@                          !!!  Well Done !!!   ");
        Functions.sleep(650);
        p.message("");
        Functions.sleep(650);
        p.message("@gre@***********************************************************");
        Functions.sleep(650);
        p.message("@gre@*** You have completed the 'Tourist Trap' Quest ! ***");
        Functions.sleep(650);
        p.message("@gre@***********************************************************");
        Functions.sleep(650);
        Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.TOURIST_TRAP), true);
        p.message("@gre@You haved gained 2 quest points!");
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return Functions.inArray(n.getID(), NpcId.IRENA.id(), NpcId.MERCENARY.id(), NpcId.MERCENARY_CAPTAIN.id(), NpcId.MERCENARY_ESCAPEGATES.id(), NpcId.CAPTAIN_SIAD.id(), NpcId.MINING_SLAVE.id(), NpcId.BEDABIN_NOMAD.id(), NpcId.BEDABIN_NOMAD_GUARD.id(), NpcId.AL_SHABIM.id(), NpcId.MERCENARY_LIFTPLATFORM.id(), NpcId.ANA.id());
    }

    @Override
    public boolean blockIndirectTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.AL_SHABIM.id();
    }

    private void irenaDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.IRENA.id()) {
            if (cID == (-1)) {
                switch (p.getQuestStage(this)) {
                    case 0 :
                        Functions.___message(p, "Irena seems to be very upset and cries as you start to approach her.");
                        Functions.___npcTalk(p, n, "Boo hoo, oh dear, my only daughter....");
                        int menu = Functions.___showMenu(p, n, "What's the matter?", "Cheer up, it might never happen.");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "Oh dear...my daughter, Ana, has gone missing in the desert.", "I fear that she is lost, or perhaps...*sob* even worse.");
                            int matterMenu = Functions.___showMenu(p, n, "When did she go into the desert?", "What did she go into the desert for?", "Is there a reward if I get her back?");
                            if (matterMenu == 0) {
                                irenaDialogue(p, n, TouristTrap.Irene.WHENDIDSHEGO);
                            } else
                                if (matterMenu == 1) {
                                    irenaDialogue(p, n, TouristTrap.Irene.WHATDIDSHEGO);
                                } else
                                    if (matterMenu == 2) {
                                        irenaDialogue(p, n, TouristTrap.Irene.REWARD);
                                    }


                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "It may already have happened you thoughtless oaf!", "My daughter, Ana, could be dead or dying in the desert!!!");
                                int newMenu = Functions.___showMenu(p, n, "When did she go into the desert?", "What did she go into the desert for?", "Is there a reward if I get her back?");
                                if (newMenu == 0) {
                                    irenaDialogue(p, n, TouristTrap.Irene.WHENDIDSHEGO);
                                } else
                                    if (newMenu == 1) {
                                        irenaDialogue(p, n, TouristTrap.Irene.WHATDIDSHEGO);
                                    } else
                                        if (newMenu == 2) {
                                            irenaDialogue(p, n, TouristTrap.Irene.REWARD);
                                        }


                            }

                        break;
                    case 1 :
                    case 2 :
                    case 3 :
                    case 4 :
                    case 5 :
                    case 6 :
                    case 7 :
                    case 8 :
                        Functions.___npcTalk(p, n, "Please bring my daughter back to me.", "She is most likely lost in the Desert somewhere.", "I miss her so much....", "Wahhhhh!", "*Sob*");
                        break;
                    case 9 :
                        if (!Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                            Functions.___npcTalk(p, n, "Please bring my daughter back to me.", "She is most likely lost in the Desert somewhere.", "I miss her so much....", "Wahhhhh!", "*Sob*");
                        } else {
                            Functions.___npcTalk(p, n, "Hey, great you've found Ana!");
                            Functions.___message(p, "You show Irena the barrel with Ana in it.");
                            Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                            p.updateQuestStage(this, 10);
                            Npc Ana = Functions.spawnNpc(p.getWorld(), NpcId.ANA.id(), p.getX(), p.getY(), 60000);
                            Ana.teleport(p.getX(), p.getY() + 1);
                            if (Ana != null) {
                                Functions.sleep(650);
                                p.message("@gre@Ana: Hey great, there's my Mum!");
                                Functions.___npcTalk(p, Ana, "Great! Thanks for getting me out of that mine!", "And that barrel wasn't too bad anyway!", "Pop by again sometime, I'm sure we'll have a barrel of laughs!", "Oh! I nearly forgot, here's a key I found in the tunnels.", "It might be of some use to you, not sure what it opens.");
                                Functions.addItem(p, ItemId.WROUGHT_IRON_KEY.id(), 1);
                                Functions.___message(p, "Ana spots Irena and waves...");
                                Functions.___npcTalk(p, Ana, "Hi Mum!", "Sorry, I have to go now!");
                                Ana.remove();
                            }
                            Functions.___npcTalk(p, n, "Hi Ana!");
                            rewardMenu(p, n, true);
                            p.getCache().remove("tried_ana_barrel");
                        }
                        break;
                    case 10 :
                        if (p.getCache().hasKey("advanced1")) {
                            lastRewardMenu(p, n, true);
                        } else {
                            rewardMenu(p, n, true);
                        }
                        break;
                    case -1 :
                        p.message("Irena seems happy now that her daugher has returned home.");
                        Functions.___npcTalk(p, n, "Thanks so much for returning my daughter to me.", "I expect that she will go on another trip soon though.", "She is the adventurous type...a bit like yourself really!", "Ok, see you around then!");
                        p.message("Irena goes back to work.");
                        break;
                }
            }
            switch (cID) {
                case TouristTrap.Irene.WHENDIDSHEGO :
                    Functions.___npcTalk(p, n, "*Sob*", "She went in there just a few days ago, ", "She said she would be back yesterday.", "And she's not...");
                    int menu = Functions.___showMenu(p, n, "What did she go into the desert for?", "Is there a reward if I get her back?", "I'll look for your daughter.");
                    if (menu == 0) {
                        irenaDialogue(p, n, TouristTrap.Irene.WHATDIDSHEGO);
                    } else
                        if (menu == 1) {
                            irenaDialogue(p, n, TouristTrap.Irene.REWARD);
                        } else
                            if (menu == 2) {
                                irenaDialogue(p, n, TouristTrap.Irene.LOOKFORDAUGHTER);
                            }


                    break;
                case TouristTrap.Irene.WHATDIDSHEGO :
                    Functions.___npcTalk(p, n, "She was just travelling, a tourist you might say.", "*Sob* She said she would be safe and now she could be..");
                    p.message("Irena's bottom lip trembles a little.");
                    Functions.___npcTalk(p, n, "*Whhhhhaaaaa*");
                    p.message("Irena cries her heart out in front of you.");
                    int menuWhat = Functions.___showMenu(p, n, "When did she go into the desert?", "Is there a reward if I get her back?", "I'll look for your daughter.");
                    if (menuWhat == 0) {
                        irenaDialogue(p, n, TouristTrap.Irene.WHENDIDSHEGO);
                    } else
                        if (menuWhat == 1) {
                            irenaDialogue(p, n, TouristTrap.Irene.REWARD);
                        } else
                            if (menuWhat == 2) {
                                irenaDialogue(p, n, TouristTrap.Irene.LOOKFORDAUGHTER);
                            }


                    break;
                case TouristTrap.Irene.REWARD :
                    Functions.___npcTalk(p, n, "Well, yes, you'll have my gratitude young " + (p.isMale() ? "man." : "lady."), "And I'm sure that Ana will also be very pleased!", "And I may see if I can get a small reward together...", "But I cannot promise anything.", "So does that mean that you'll look for her then?");
                    int rewardMenu = Functions.___showMenu(p, n, "Oh, Ok, I'll get your daughter back for you.", "No, sorry, I'm just too busy!");
                    if (rewardMenu == 0) {
                        irenaDialogue(p, n, TouristTrap.Irene.GETBACKDAUGHTER);
                    } else
                        if (rewardMenu == 1) {
                            Functions.___npcTalk(p, n, "Oh really, can't I persuade you in anyway?");
                        }

                    break;
                case TouristTrap.Irene.LOOKFORDAUGHTER :
                    Functions.___npcTalk(p, n, "That would be very good of you.", "You would have the gratitude of a very loving mother.", "Are you sure you want to take on that responsibility?");
                    int lookMenu = Functions.___showMenu(p, n, "Oh, Ok, I'll get your daughter back for you.", "No, sorry, I'm just too busy!");
                    if (lookMenu == 0) {
                        irenaDialogue(p, n, TouristTrap.Irene.GETBACKDAUGHTER);
                    } else
                        if (lookMenu == 1) {
                            Functions.___npcTalk(p, n, "Oh really, can't I persuade you in anyway?");
                        }

                    break;
                case TouristTrap.Irene.GETBACKDAUGHTER :
                    Functions.___npcTalk(p, n, "That would be great!", "That's really very nice of you!", "She was wearing a red silk scarf when she left.");
                    p.updateQuestStage(this, 1);
                    break;
            }
        }
    }

    private void lastRewardMenu(Player p, Npc n, boolean showIrenaDialogue) {
        if (showIrenaDialogue) {
            Functions.___npcTalk(p, n, "Thank you very much for returning my daughter to me.", "I'm really very grateful...", "I would like to reward you for your bravery and daring.", "I can offer you increased knowledge in one of the following areas.");
        }
        int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.TOURIST_TRAP);
        int lastRewardMenu = // do not send over
        Functions.___showMenu(p, n, false, "Fletching.", "Agility.", "Smithing.", "Thieving");
        if (lastRewardMenu == 0) {
            questData[Quests.MAPIDX_SKILL] = Skills.FLETCHING;
            Functions.incQuestReward(p, questData, false);
            Functions.___message(p, "You advance your stat in Fletching.");
            p.sendQuestComplete(Quests.TOURIST_TRAP);
            if (p.getCache().hasKey("advanced1")) {
                p.getCache().remove("advanced1");
            }
        } else
            if (lastRewardMenu == 1) {
                questData[Quests.MAPIDX_SKILL] = Skills.AGILITY;
                Functions.incQuestReward(p, questData, false);
                Functions.___message(p, "You advance your stat in Agility.");
                p.sendQuestComplete(Quests.TOURIST_TRAP);
                if (p.getCache().hasKey("advanced1")) {
                    p.getCache().remove("advanced1");
                }
            } else
                if (lastRewardMenu == 2) {
                    questData[Quests.MAPIDX_SKILL] = Skills.SMITHING;
                    Functions.incQuestReward(p, questData, false);
                    Functions.___message(p, "You advance your stat in Smithing.");
                    p.sendQuestComplete(Quests.TOURIST_TRAP);
                    if (p.getCache().hasKey("advanced1")) {
                        p.getCache().remove("advanced1");
                    }
                } else
                    if (lastRewardMenu == 3) {
                        questData[Quests.MAPIDX_SKILL] = Skills.THIEVING;
                        Functions.incQuestReward(p, questData, false);
                        Functions.___message(p, "You advance your stat in Thieving.");
                        p.sendQuestComplete(Quests.TOURIST_TRAP);
                        if (p.getCache().hasKey("advanced1")) {
                            p.getCache().remove("advanced1");
                        }
                    }



    }

    private void rewardMenu(Player p, Npc n, boolean showIrenaDialogue) {
        Functions.___npcTalk(p, n, "Thank you very much for returning my daughter to me.", "I'm really very grateful...", "I would like to reward you for your bravery and daring.", "I can offer you increased knowledge in two of the following areas.");
        int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.TOURIST_TRAP);
        int rewardMenu = // do not send over
        Functions.___showMenu(p, n, false, "Fletching.", "Agility.", "Smithing.", "Thieving");
        if (rewardMenu == 0) {
            questData[Quests.MAPIDX_SKILL] = Skills.FLETCHING;
            Functions.incQuestReward(p, questData, false);
            Functions.___message(p, "You advance your stat in Fletching.", "Ok, now choose your second skil.");
            if (!p.getCache().hasKey("advanced1")) {
                p.getCache().store("advanced1", true);
            }
            lastRewardMenu(p, n, false);
        } else
            if (rewardMenu == 1) {
                questData[Quests.MAPIDX_SKILL] = Skills.AGILITY;
                Functions.incQuestReward(p, questData, false);
                Functions.___message(p, "You advance your stat in Agility.", "Ok, now choose your second skil.");
                if (!p.getCache().hasKey("advanced1")) {
                    p.getCache().store("advanced1", true);
                }
                lastRewardMenu(p, n, false);
            } else
                if (rewardMenu == 2) {
                    questData[Quests.MAPIDX_SKILL] = Skills.SMITHING;
                    Functions.incQuestReward(p, questData, false);
                    Functions.___message(p, "You advance your stat in Smithing.", "Ok, now choose your second skil.");
                    if (!p.getCache().hasKey("advanced1")) {
                        p.getCache().store("advanced1", true);
                    }
                    lastRewardMenu(p, n, false);
                } else
                    if (rewardMenu == 3) {
                        questData[Quests.MAPIDX_SKILL] = Skills.THIEVING;
                        Functions.incQuestReward(p, questData, false);
                        Functions.___message(p, "You advance your stat in Thieving.", "Ok, now choose your second skil.");
                        if (!p.getCache().hasKey("advanced1")) {
                            p.getCache().store("advanced1", true);
                        }
                        lastRewardMenu(p, n, false);
                    }



    }

    private void mercenaryDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.MERCENARY.id()) {
            if (cID == (-1)) {
                switch (p.getQuestStage(this)) {
                    case 0 :
                        if (Functions.hasItem(p, ItemId.METAL_KEY.id())) {
                            Functions.___npcTalk(p, n, "Move along now...we've had enough of your sort!");
                            return;
                        }
                        Functions.___npcTalk(p, n, "Yeah, what do you want?");
                        int menu = Functions.___showMenu(p, n, "What is this place?", "What are you guarding?");
                        if (menu == 0) {
                            mercenaryDialogue(p, n, TouristTrap.Mercenary.PLACE_START);
                        } else
                            if (menu == 1) {
                                mercenaryDialogue(p, n, TouristTrap.Mercenary.GUARDING_FIRST);
                            }

                        break;
                    case 1 :
                    case 2 :
                    case 3 :
                    case 4 :
                    case 5 :
                        if (Functions.hasItem(p, ItemId.METAL_KEY.id()) || p.getLocation().inTouristTrapCave()) {
                            Functions.___npcTalk(p, n, "Move along now...we've had enough of your sort!");
                            return;
                        }
                        if (((p.getQuestStage(this) == 1) && p.getCache().hasKey("first_kill_captn")) && p.getCache().getBoolean("first_kill_captn")) {
                            // dialogue only on stage 1
                            // talking after captain is killed -> special dialogue bet and sets false flag
                            boolean completed = false;
                            if (!p.getCache().hasKey("mercenary_bet")) {
                                Functions.___npcTalk(p, n, "Well, you've killed our Captain.", "I guess you've proved yourself in combat.", "However, you've left a horrible mess now.", "And it's gonna cost you for us to clean it up.", "Let's say 20 gold and we won't have to get rough with you?");
                                int opts = // do not send over
                                Functions.___showMenu(p, n, false, "Yeah, ok, I'll give you 20 gold.", "I'll give you 15, that's all you're gettin'", "You can whistle for you money, I'll take you all on.");
                                if (opts == 0) {
                                    Functions.___playerTalk(p, n, "Yeah, ok, I'll give you 20 gold.");
                                    if (Functions.hasItem(p, ItemId.COINS.id(), 20)) {
                                        Functions.removeItem(p, ItemId.COINS.id(), 20);
                                        Functions.___npcTalk(p, n, "Good! Seeya, we have some cleaning to do.");
                                        completed = true;
                                    } else {
                                        Functions.___npcTalk(p, n, "You don't have the gold and now we're gonna teach you a lesson.");
                                        Functions.___message(p, "The Guards search you!");
                                        mercenaryDialogue(p, n, TouristTrap.Mercenary.LEAVE_DESERT);
                                        completed = true;
                                    }
                                } else
                                    if (opts == 1) {
                                        Functions.___playerTalk(p, n, "I'll give you 15, that's all you're gettin'");
                                        if (Functions.hasItem(p, ItemId.COINS.id(), 15)) {
                                            Functions.removeItem(p, ItemId.COINS.id(), 15);
                                            Functions.___npcTalk(p, n, "Ok, we'll take fifteen, you push a hard bargain!");
                                            completed = true;
                                        } else {
                                            Functions.___npcTalk(p, n, "You don't have the gold and now we're gonna teach you a lesson.");
                                            Functions.___message(p, "The Guards search you!");
                                            mercenaryDialogue(p, n, TouristTrap.Mercenary.LEAVE_DESERT);
                                            completed = true;
                                        }
                                    } else
                                        if (opts == 2) {
                                            Functions.___playerTalk(p, n, "You can whistle for your money, I'll take you all on.");
                                            Functions.___npcTalk(p, n, "Ok, that's it, we're gonna teach you a lesson.");
                                            Functions.___message(p, "The Guards search you!");
                                            mercenaryDialogue(p, n, TouristTrap.Mercenary.LEAVE_DESERT);
                                            completed = true;
                                        }


                            } else {
                                Functions.___playerTalk(p, n, "Hey, I've come to collect my bet!");
                                Functions.___npcTalk(p, n, "Well, I guess congratulations are in order.");
                                Functions.___playerTalk(p, n, "Thanks!");
                                Functions.___npcTalk(p, n, "And we'll only charge the paltry sum of..erm...");
                                Functions.___message(p, "The guards starts to do some mental calculations...", "You can see his brow furrow and he starts to sweat profusely");
                                switch (p.getCache().getInt("mercenary_bet")) {
                                    case 5 :
                                        Functions.___npcTalk(p, n, "Five gold for cleaning up the mess.", "You have won 1 Gold piece!");
                                        Functions.addItem(p, ItemId.COINS.id(), 1);
                                        break;
                                    case 10 :
                                        Functions.___npcTalk(p, n, "10 gold for cleaning up the mess.", "You have won 2 Gold pieces!");
                                        Functions.addItem(p, ItemId.COINS.id(), 2);
                                        break;
                                    case 15 :
                                        Functions.___npcTalk(p, n, "15 gold for cleaning up the mess.", "You have won 4 Gold pieces!");
                                        Functions.addItem(p, ItemId.COINS.id(), 4);
                                        break;
                                    case 20 :
                                        Functions.___npcTalk(p, n, "20 gold for cleaning up the mess.", "You have won 10 Gold pieces!");
                                        Functions.addItem(p, ItemId.COINS.id(), 10);
                                        break;
                                }
                                Functions.___npcTalk(p, n, "Well done..!", "Ha, ha, ha ha!");
                                p.message("The guards walk off chuckling to themselves.");
                                completed = true;
                            }
                            if (completed) {
                                p.getCache().store("first_kill_captn", false);
                            }
                            return;
                        }
                        Functions.___npcTalk(p, n, "Yeah, what do you want?");
                        int option = Functions.___showMenu(p, n, "What is this place?", "What are you guarding?", "I'm looking for a woman called Ana, have you seen her?");
                        if (option == 0) {
                            mercenaryDialogue(p, n, TouristTrap.Mercenary.PLACE_START);
                        } else
                            if (option == 1) {
                                mercenaryDialogue(p, n, TouristTrap.Mercenary.GUARDING_FIRST);
                            } else
                                if (option == 2) {
                                    mercenaryDialogue(p, n, TouristTrap.Mercenary.ANA_FIRST);
                                }


                        break;
                    case 6 :
                    case 7 :
                    case 8 :
                    case 9 :
                    case 10 :
                        Functions.___npcTalk(p, n, "Move along now...we've had enough of your sort!");
                        break;
                    case -1 :
                        Functions.___npcTalk(p, n, "What're you looking at?");
                        break;
                }
            }
            switch (cID) {
                case TouristTrap.Mercenary.THROW_PLAYER :
                    Functions.___npcTalk(p, n, "Don't try to fool me, you don't have five gold coins!", "Before you try to bribe someone, make sure you have the money effendi!");
                    mercenaryDialogue(p, n, TouristTrap.Mercenary.LEAVE_DESERT);
                    break;
                case TouristTrap.Mercenary.LEAVE_DESERT :
                    Functions.___npcTalk(p, n, "Guards, guards!");
                    n.setChasing(p);
                    Functions.___message(p, "Nearby guards quickly grab you and rough you up a bit.");
                    Functions.___npcTalk(p, n, "Let's see how good you are with desert survival techniques!");
                    Functions.___message(p, "You're bundled into the back of a cart and blindfolded...");
                    Functions.___message(p, "Sometime later you wake up in the desert.");
                    if (Functions.hasItem(p, ItemId.BOWL_OF_WATER.id())) {
                        Functions.___npcTalk(p, n, "You won't be needing that water any more!");
                        Functions.___message(p, "The guards throw your water away...");
                        Functions.removeItem(p, ItemId.BOWL_OF_WATER.id(), 1);
                    }
                    p.teleport(121, 803);
                    Functions.___message(p, "The guards move off in the cart leaving you stranded in the desert.");
                    break;
                case TouristTrap.Mercenary.PLACE_START :
                    Functions.___npcTalk(p, n, "It's none of your business now get lost.");
                    int menu = Functions.___showMenu(p, n, "Perhaps five gold coins will make it my business?", "Ok, thanks.");
                    if (menu == 0) {
                        Functions.___npcTalk(p, n, "It certainly will!");
                        if (Functions.hasItem(p, ItemId.COINS.id(), 5)) {
                            p.message("The guard takes the five gold coins.");
                            Functions.removeItem(p, ItemId.COINS.id(), 5);
                            Functions.___npcTalk(p, n, "Now then, what did you want to know?");
                            int secondMenu = Functions.___showMenu(p, n, "What is this place?", "What are you guarding?");
                            if (secondMenu == 0) {
                                mercenaryDialogue(p, n, TouristTrap.Mercenary.PLACE_SECOND);
                            } else
                                if (secondMenu == 1) {
                                    mercenaryDialogue(p, n, TouristTrap.Mercenary.GUARDING_SECOND);
                                }

                        } else {
                            mercenaryDialogue(p, n, TouristTrap.Mercenary.THROW_PLAYER);
                        }
                    } else
                        if (menu == 1) {
                            Functions.___npcTalk(p, n, "Yeah, whatever!");
                        }

                    break;
                case TouristTrap.Mercenary.PLACE_SECOND :
                    Functions.___npcTalk(p, n, "It's just a mining camp. Prisoners are sent here from Al Kharid.", "They serve out their sentence by mining.", "Most prisoners will end their days here, surrounded by desert.");
                    Functions.___playerTalk(p, n, "So you could almost say that they got their... 'just desserts'");
                    Functions.___npcTalk(p, n, "You could say that...");
                    Functions.___message(p, "There is an awkward pause");
                    Functions.___npcTalk(p, n, "But it wouldn't be very funny.");
                    Functions.___message(p, "There is another awkward pause.");
                    Functions.___playerTalk(p, n, "When they talk about the silence of the desert,", "this must be what they mean.");
                    p.message("The guard starts losing interest in the conversation.");
                    int options = Functions.___showMenu(p, n, "Can I take a look around the place?", "Ok thanks.");
                    if (options == 0) {
                        Functions.___npcTalk(p, n, "Not really. The Captain won't let you in the compound.", "He's the only one who has the key to the gate.", "And if you talk to him, he'll probably just order us to kill you.", "Unless...");
                        int newMenu = Functions.___showMenu(p, n, "Does the Captain order you to kill a lot of people?", "Unless what?");
                        if (newMenu == 0) {
                            mercenaryDialogue(p, n, TouristTrap.Mercenary.ORDER_KILL_PEOPLE);
                        } else
                            if (newMenu == 1) {
                                Functions.___npcTalk(p, n, "Unless he has a use for you.", "He's been trying to track down a someone called 'Al Zaba Bhasim'.", "You could offer to catch him and that might put you in his good books?");
                                int tenthMenu = Functions.___showMenu(p, n, "Where would I find this Al Zaba Bhasim?", "Ok thanks.");
                                if (tenthMenu == 0) {
                                    Functions.___npcTalk(p, n, "Well, he could be anywhere, he's a nomadic desert dweller.", "However, he is frequently to be found to the west in the ", "hospitality of the tenti's.");
                                    int eleventhMenu = // do not send over
                                    Functions.___showMenu(p, n, false, "The Tenti's, who are they?", "Ok thanks.");
                                    if (eleventhMenu == 0) {
                                        Functions.___playerTalk(p, n, "The Tenti's, who are they?");
                                        Functions.___npcTalk(p, n, "Well, we're not really sure what they're proper name is.", "But they live in tents so we call them the 'Tenti's'.");
                                        int twelftMenu = // do not send over
                                        Functions.___showMenu(p, n, false, "Ok thanks.", "Is Al Zaba Bhasim very tough?");
                                        if (twelftMenu == 0) {
                                            Functions.___playerTalk(p, n, "Ok, thanks.");
                                            Functions.___npcTalk(p, n, "Yeah, whatever!");
                                        } else
                                            if (twelftMenu == 1) {
                                                Functions.___playerTalk(p, n, "Is Al Zaba Bhasim very tough?");
                                                Functions.___npcTalk(p, n, "Well, I'm not sure, but by all accounts, he is a slippery fellow.", "The Captain has been trying to capture him for years.", "A bit of a waste of time if you ask me.", "Anyway, I have to get going, I do have work to do.");
                                                p.message("The guard walks off.");
                                            }

                                    } else
                                        if (eleventhMenu == 1) {
                                            Functions.___playerTalk(p, n, "Ok, thanks.");
                                            Functions.___npcTalk(p, n, "Yeah, whatever!");
                                        }

                                } else
                                    if (tenthMenu == 1) {
                                        Functions.___npcTalk(p, n, "Yeah, whatever!");
                                    }

                            }

                    } else
                        if (options == 1) {
                            Functions.___npcTalk(p, n, "Yeah, whatever!");
                        }

                    break;
                case TouristTrap.Mercenary.ORDER_KILL_PEOPLE :
                    p.message("The guard snorts.");
                    Functions.___npcTalk(p, n, "*Snort*", "Just about anyone who talks to him.", "Unless he has a use for you, he'll probably just order us to kill you.", "And it's such a horrible job cleaning up the mess afterwards.");
                    int sixthMenu = Functions.___showMenu(p, n, "Not to mention the senseless waste of human life.", "Ok thanks.");
                    if (sixthMenu == 0) {
                        Functions.___npcTalk(p, n, "Heh?");
                        Functions.___message(p, "The guard looks at you with a confused stare...");
                        int seventhMenu = // do not send over
                        Functions.___showMenu(p, n, false, "It doesn't sound as if you respect your Captain much.", "Ok thanks.");
                        if (seventhMenu == 0) {
                            Functions.___playerTalk(p, n, "It doesn't sound is if you respect your Captain much.");
                            Functions.___npcTalk(p, n, "Well, to be honest.");
                            Functions.___message(p, "The guard looks around conspiratorially.");
                            Functions.___npcTalk(p, n, "We think he's not exactly as brave as he makes out.", "But we have to follow his orders.", "If someone called him a coward, ", "or managed to trick him into a one-on-one duel.", "Many of us bet that he'll be slaughtered in double quick time.", "And all the men agreed that they wouldn't intervene.");
                            int eightMenu = // do not send over
                            Functions.___showMenu(p, n, false, "Can I have a bet on that?", "Ok Thanks.");
                            if (eightMenu == 0) {
                                Functions.___playerTalk(p, n, "Can I have a bet on that?");
                                Functions.___npcTalk(p, n, "Well, if you think you stand a chance, sure.", "But remember, if he gives us an order, we have to obey.");
                                int ninthMenu = Functions.___showMenu(p, n, "I'll bet 5 gold that I win.", "I'll bet 10 gold that I win.", "I'll bet 15 gold that I win.", "I'll bet 20 gold that I win.", "Ok, thanks.");
                                if ((ninthMenu >= 0) && (ninthMenu <= 3)) {
                                    int betAmount = 5;
                                    int recvAmount = 6;
                                    if (ninthMenu == 0) {
                                        betAmount = 5;
                                        recvAmount = 6;
                                    } else
                                        if (ninthMenu == 1) {
                                            betAmount = 10;
                                            recvAmount = 12;
                                        } else
                                            if (ninthMenu == 2) {
                                                betAmount = 15;
                                                recvAmount = 19;
                                            } else
                                                if (ninthMenu == 3) {
                                                    betAmount = 20;
                                                    recvAmount = 30;
                                                }



                                    if (Functions.hasItem(p, ItemId.COINS.id(), betAmount)) {
                                        Functions.___npcTalk(p, n, "Great, I'll take that bet.");
                                        p.message(("You hand over " + betAmount) + " gold coins.");
                                        Functions.removeItem(p, ItemId.COINS.id(), betAmount);
                                        Functions.___npcTalk(p, n, ("Ok, if you win, you'll get " + recvAmount) + "gold back.");
                                        p.getCache().set("mercenary_bet", betAmount);
                                    }
                                    Functions.___npcTalk(p, n, "Anyway, I have to get going, I do have work to do.");
                                    p.message("The guard walks off.");
                                } else
                                    if (ninthMenu == 4) {
                                        Functions.___npcTalk(p, n, "Yeah, whatever!");
                                    }

                            } else
                                if (eightMenu == 1) {
                                    Functions.___playerTalk(p, n, "Ok, thanks.");
                                    Functions.___npcTalk(p, n, "Yeah, whatever!");
                                }

                        } else
                            if (seventhMenu == 1) {
                                Functions.___playerTalk(p, n, "Ok, thanks.");
                                Functions.___npcTalk(p, n, "Yeah, whatever!");
                            }

                    } else
                        if (sixthMenu == 1) {
                            Functions.___npcTalk(p, n, "Yeah, whatever!");
                        }

                    break;
                case TouristTrap.Mercenary.GUARDING_FIRST :
                    Functions.___npcTalk(p, n, "Get lost before I chop off your head!");
                    int chopMenu = // do not send over
                    Functions.___showMenu(p, n, false, "Ok thanks.", "Perhaps these five gold coins will sweeten your mood?");
                    if (chopMenu == 0) {
                        Functions.___playerTalk(p, n, "Ok, thanks.");
                        Functions.___npcTalk(p, n, "Yeah, whatever!");
                    } else
                        if (chopMenu == 1) {
                            Functions.___playerTalk(p, n, "Perhaps these five gold coins will sweeten your mood?");
                            if (Functions.hasItem(p, ItemId.COINS.id(), 5)) {
                                Functions.___npcTalk(p, n, "Well, it certainly will help...");
                                p.message("The guard takes the five gold coins.");
                                Functions.removeItem(p, ItemId.COINS.id(), 5);
                                Functions.___npcTalk(p, n, "Now then, what did you want to know?");
                                int knowMenu = Functions.___showMenu(p, n, "What is this place?", "What are you guarding?", "I'm looking for a woman called Ana, have you seen her?");
                                if (knowMenu == 0) {
                                    mercenaryDialogue(p, n, TouristTrap.Mercenary.PLACE_SECOND);
                                } else
                                    if (knowMenu == 1) {
                                        mercenaryDialogue(p, n, TouristTrap.Mercenary.GUARDING_SECOND);
                                    } else
                                        if (knowMenu == 2) {
                                            mercenaryDialogue(p, n, TouristTrap.Mercenary.ANA_SECOND);
                                        }


                            } else {
                                mercenaryDialogue(p, n, TouristTrap.Mercenary.THROW_PLAYER);
                            }
                        }

                    break;
                case TouristTrap.Mercenary.GUARDING_SECOND :
                    Functions.___npcTalk(p, n, "Well, if you have to know, we're making sure that no prisoners get out.");
                    Functions.___message(p, "The guard gives you a disaproving look.");
                    Functions.___npcTalk(p, n, "And to make sure that unauthorised people don't get in.");
                    Functions.___message(p, "The guard looks around nervously.");
                    Functions.___npcTalk(p, n, "You'd better go now before the Captain orders us to kill you.");
                    int gmenu = // do not send over
                    Functions.___showMenu(p, n, false, "Does the Captain order you to kill a lot of people?", "Ok Thanks.");
                    if (gmenu == 0) {
                        Functions.___playerTalk(p, n, "Does the Captain order you to kill a lot of people?");
                        mercenaryDialogue(p, n, TouristTrap.Mercenary.ORDER_KILL_PEOPLE);
                    } else
                        if (gmenu == 2) {
                            Functions.___playerTalk(p, n, "Ok, thanks.");
                            Functions.___npcTalk(p, n, "Yeah, whatever!");
                        }

                    break;
                case TouristTrap.Mercenary.ANA_FIRST :
                    Functions.___npcTalk(p, n, "No, now get lost!");
                    int altMenu = Functions.___showMenu(p, n, "Perhaps five gold coins will help you remember?", "Ok, thanks.");
                    if (altMenu == 0) {
                        Functions.___npcTalk(p, n, "Hmm, it might help!");
                        if (Functions.hasItem(p, ItemId.COINS.id(), 5)) {
                            p.message("The guards takes the five gold coins.");
                            Functions.removeItem(p, ItemId.COINS.id(), 5);
                            Functions.___npcTalk(p, n, "Now then, what did you want to know?");
                            int anaMenu = Functions.___showMenu(p, n, "I'm looking for a woman called Ana, have you seen her?", "What is this place?", "What are you guarding?");
                            if (anaMenu == 0) {
                                mercenaryDialogue(p, n, TouristTrap.Mercenary.ANA_SECOND);
                            } else
                                if (anaMenu == 1) {
                                    mercenaryDialogue(p, n, TouristTrap.Mercenary.PLACE_SECOND);
                                } else
                                    if (anaMenu == 2) {
                                        mercenaryDialogue(p, n, TouristTrap.Mercenary.GUARDING_SECOND);
                                    }


                        } else {
                            mercenaryDialogue(p, n, TouristTrap.Mercenary.THROW_PLAYER);
                        }
                    } else
                        if (altMenu == 1) {
                            Functions.___npcTalk(p, n, "Yeah, whatever!");
                        }

                    break;
                case TouristTrap.Mercenary.ANA_SECOND :
                    Functions.___npcTalk(p, n, "Hmm, well, we get a lot of people in here.", "But not many women though...", "Saw one come in last week....", "But I don't know if it's the woman you're looking for?");
                    int lastMenu = Functions.___showMenu(p, n, "What is this place?", "What are you guarding?");
                    if (lastMenu == 0) {
                        mercenaryDialogue(p, n, TouristTrap.Mercenary.PLACE_SECOND);
                    } else
                        if (lastMenu == 1) {
                            mercenaryDialogue(p, n, TouristTrap.Mercenary.GUARDING_SECOND);
                        }

                    break;
            }
        }
    }

    private void mercenaryCaptainDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.MERCENARY_CAPTAIN.id()) {
            if (cID == (-1)) {
                switch (p.getQuestStage(this)) {
                    case 0 :
                    case 1 :
                    case 2 :
                    case 3 :
                    case 4 :
                    case 5 :
                    case 6 :
                    case 7 :
                    case 8 :
                    case 9 :
                    case 10 :
                    case -1 :
                        if (Functions.hasItem(p, ItemId.METAL_KEY.id())) {
                            Functions.___npcTalk(p, n, "Move along now...we've had enough of your sort!");
                            return;
                        }
                        p.message("You approach the Mercenary Captain.");
                        int menu = Functions.___showMenu(p, n, "Hello.", "You there!", "Hey ugly!");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "Be off Effendi, you are not wanted around here.");
                            int be = Functions.___showMenu(p, n, "That's rude, I ought to teach you some manners.", "I 'll offer you something in return for your time.");
                            if (be == 0) {
                                Functions.___npcTalk(p, n, "Oh yes! How might you do that?", "You seem little more than a gutter dweller.", "How could you teach me manners?");
                                int manners = // do not send over
                                Functions.___showMenu(p, n, false, "With my right fist and a good deal of force.", "Err, sorry, I thought I was talking to someone else.");
                                if (manners == 0) {
                                    Functions.___playerTalk(p, n, "With my good right arm and a good deal of force.");
                                    Functions.___npcTalk(p, n, "Oh yes, ready your weapon then!", "I'm sure you won't mind if my men join in?", "Har, har, har!", "Guards, kill this gutter dwelling slime.");
                                    captainWantToThrowPlayer(p, n);
                                } else
                                    if (manners == 1) {
                                        Functions.___playerTalk(p, n, "Err, sorry, I thought I was talking to someone else.");
                                        Functions.___npcTalk(p, n, "Well, Effendi, you do need to be carefull of what you say to people.", "Or they may take it the wrong way.", "Thankfully, I'm very understanding.", "I'll just let me guards deal with you.", "Guards, teach this desert weed some manners.");
                                        captainWantToThrowPlayer(p, n);
                                    }

                            } else
                                if (be == 1) {
                                    Functions.___npcTalk(p, n, "Hmmm, oh yes, what might that be?");
                                    int menus = Functions.___showMenu(p, n, "I have some gold.", "There must be something that I can do for you?");
                                    if (menus == 0) {
                                        Functions.___npcTalk(p, n, "Ha, ha, ha! You come to a mining camp and offer us gold!", "Thanks effendi, but we have all the gold that we'll ever need.", "Now be off with you,", "before we reduce you to a bloody mess on the sand.");
                                        int option = Functions.___showMenu(p, n, "There must be something that I can do for you?", "You don't scare me!");
                                        if (option == 0) {
                                            mercenaryCaptainDialogue(p, n, TouristTrap.MercenaryCaptain.MUSTBESOMETHINGICANDO);
                                        } else
                                            if (option == 1) {
                                                mercenaryCaptainDialogue(p, n, TouristTrap.MercenaryCaptain.DONTSCAREME);
                                            }

                                    } else
                                        if (menus == 1) {
                                            mercenaryCaptainDialogue(p, n, TouristTrap.MercenaryCaptain.MUSTBESOMETHINGICANDO);
                                        }

                                }

                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "How dare you talk to me like that!", "Explain your business quickly...", "or my guards will slay you where you stand.");
                                p.message("Some guards close in around you.");
                                int thirdMenu = Functions.___showMenu(p, n, "I'm lost, can you help me?", "What are you guarding?");
                                if (thirdMenu == 0) {
                                    Functions.___message(p, "The captain smiles broadly and with a sickening voice says.");
                                    Functions.___npcTalk(p, n, "We are not a charity effendi,", "Be off with you before I have your head removed from your body.");
                                    int lostMenu = Functions.___showMenu(p, n, "What are you guarding?", "You don't scare me!");
                                    if (lostMenu == 0) {
                                        mercenaryCaptainDialogue(p, n, TouristTrap.MercenaryCaptain.GUARDING);
                                    } else
                                        if (lostMenu == 1) {
                                            mercenaryCaptainDialogue(p, n, TouristTrap.MercenaryCaptain.DONTSCAREME);
                                        }

                                } else
                                    if (thirdMenu == 1) {
                                        mercenaryCaptainDialogue(p, n, TouristTrap.MercenaryCaptain.GUARDING);
                                    }

                            } else
                                if (menu == 2) {
                                    Functions.___npcTalk(p, n, "I will not tolerate such insults..", "Guards, kill " + (p.isMale() ? "him." : "her."));
                                    Functions.___message(p, "The captain marches away in disgust leaving his guards to tackle you.");
                                    captainWantToThrowPlayer(p, n);
                                }


                        break;
                }
                // 102, 775
            }
            switch (cID) {
                case TouristTrap.MercenaryCaptain.GUARDING :
                    Functions.___npcTalk(p, n, "Effendi...", "For just one second, imagine that it's none of your business!", "Also imagine having your limbs pulled from your body one at a time.", "Now, what was the question again?");
                    int fourthMenu = Functions.___showMenu(p, n, "Do you have sand in your ears, I said, 'What are you guarding?'", "You don't scare me!");
                    if (fourthMenu == 0) {
                        Functions.___npcTalk(p, n, "Why....you ignorant, rude and eternally damned infidel,");
                        p.message("The captain seems very agitated with what you just said.");
                        Functions.___npcTalk(p, n, "Guards, kill this infidel!");
                        captainWantToThrowPlayer(p, n);
                    } else
                        if (fourthMenu == 1) {
                            mercenaryCaptainDialogue(p, n, TouristTrap.MercenaryCaptain.DONTSCAREME);
                        }

                    break;
                case TouristTrap.MercenaryCaptain.DONTSCAREME :
                    Functions.___npcTalk(p, n, "Well, perhaps I can try a little harder.", "Guards, kill this infidel.");
                    captainWantToThrowPlayer(p, n);
                    break;
                case TouristTrap.MercenaryCaptain.MUSTBESOMETHINGICANDO :
                    p.message("The Captain ponders a moment and then looks at you critically.");
                    Functions.___npcTalk(p, n, "You could bring me the head of Al Zaba Bhasim.", "He is the leader of the notorius desert bandits, they plague us daily.", "You should find them west of here.", "You should have no problem in finishing them all off.", "Do this for me and maybe I will consider helping you.");
                    int doThis = Functions.___showMenu(p, n, "Consider it done.", "I don't think I can do that.");
                    if (doThis == 0) {
                        Functions.___npcTalk(p, n, "Good...run along then.", "You stand around flapping your tongue chatting like an insane camel.");
                    } else
                        if (doThis == 1) {
                            Functions.___npcTalk(p, n, "Hmm, well yes, I did consider that you might not be right for the job.", "Be off with you then before I turn my men loose on you.");
                            int no = // do not send over
                            Functions.___showMenu(p, n, false, "I guess you can't fight your own battles then?", "Ok, I'll move on.");
                            if (no == 0) {
                                Functions.___playerTalk(p, n, "I guess you can't fight your own battles then?");
                                p.message("The men around you fall silent and the Captain silently fumes.");
                                Functions.sleep(1600);
                                p.message("All eyes turn to the Captain...");
                                Functions.___npcTalk(p, n, "Very well, if you're challenging me, let's get on with it!");
                                p.message("The guards gather around to watch the fight.");
                                n.setChasing(p);
                            } else
                                if (no == 1) {
                                    Functions.___playerTalk(p, n, "Ok, I'll be moving along then.");
                                    Functions.___npcTalk(p, n, "Effendi, I think you'll find that is the ", "wisest decision you have made today.");
                                }

                        }

                    break;
            }
        }
    }

    private void slaveDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.MINING_SLAVE.id()) {
            if (cID == (-1)) {
                switch (p.getQuestStage(this)) {
                    case 0 :
                    case 1 :
                        Functions.___npcTalk(p, n, "You look like a new 'recruit'.", "How long have you been here?");
                        int menu = Functions.___showMenu(p, n, "I've just arrived.", "Oh, I've been here ages.");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "Yeah, it looks like it as well.");
                            slaveDialogue(p, n, TouristTrap.Slave.NEWRECRUIT);
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "That's funny, I haven't seen you around here before.", "You're clothes look too clean for you to have been here ages.");
                                int secondMenu = Functions.___showMenu(p, n, "Ok, you caught me out.", "The guards allow me to clean my clothes.");
                                if (secondMenu == 0) {
                                    Functions.___npcTalk(p, n, "Ah ha! I knew it! A new recruit then?");
                                    slaveDialogue(p, n, TouristTrap.Slave.NEWRECRUIT);
                                } else
                                    if (secondMenu == 1) {
                                        Functions.___npcTalk(p, n, "Oh, a special relationship with the guards heh?", "How very nice of them.", "Maybe you could persuade them to let me out of here?");
                                        Functions.___message(p, "The slave swaggers of with a sarcastic smirk on his face.");
                                    }

                            }

                        break;
                    case 2 :
                        Functions.___npcTalk(p, n, "Hello again, are you ready to unlock my chains?");
                        int opt = Functions.___showMenu(p, n, "Yeah, Ok, let's give it a go.", "I need to do some other things first.");
                        if (opt == 0) {
                            slaveDialogue(p, n, TouristTrap.Slave.GIVEITAGO);
                        } else
                            if (opt == 1) {
                                Functions.___npcTalk(p, n, "Ok, fair enough, let me know when you want to give it another go.");
                            }

                        break;
                    case 3 :
                        Functions.___npcTalk(p, n, "Do you have the Desert Clothes yet?");
                        necessaryStuffSlave(p, n);
                        break;
                    case 4 :
                    case 5 :
                    case 6 :
                    case 7 :
                    case 8 :
                    case 9 :
                    case 10 :
                    case -1 :
                        if (p.getLocation().inTouristTrapCave()) {
                            Functions.___npcTalk(p, n, "Can't you see I'm busy?");
                            if (((!p.getInventory().wielding(ItemId.SLAVES_ROBE_BOTTOM.id())) && (!p.getInventory().wielding(ItemId.SLAVES_ROBE_TOP.id()))) && (p.getQuestStage(this) != (-1))) {
                                p.message("A guard notices you and starts running after you.");
                                Npc npcN = Functions.getNearestNpc(p, NpcId.MERCENARY.id(), 10);
                                if (npcN == null) {
                                    npcN = Functions.spawnNpc(p.getWorld(), NpcId.MERCENARY.id(), p.getX(), p.getY(), 60000);
                                    Functions.sleep(1000);
                                }
                                Functions.___npcTalk(p, npcN, "Hey! You're no slave!");
                                npcN.startCombat(p);
                                Functions.___message(p, "The Guards search you!");
                                if (Functions.hasItem(p, ItemId.CELL_DOOR_KEY.id())) {
                                    Functions.removeItem(p, ItemId.CELL_DOOR_KEY.id(), 1);
                                }
                                Functions.___message(p, "Some guards rush to help their comrade.", "You are roughed up a bit by the guards as you're manhandlded into a cell.");
                                Functions.___npcTalk(p, npcN, "Into the cell you go! I hope this teaches you a lesson.");
                                if (p.getQuestStage(this) >= 9) {
                                    p.teleport(74, 3626);
                                } else {
                                    p.teleport(89, 801);
                                }
                            }
                        } else
                            if (Functions.hasItem(p, ItemId.SLAVES_ROBE_BOTTOM.id()) && Functions.hasItem(p, ItemId.SLAVES_ROBE_TOP.id())) {
                                Functions.___npcTalk(p, n, "Not much to do here but mine all day long.");
                            } else {
                                Functions.___npcTalk(p, n, "Oh bother, I was caught by the guards again...", "Listen, if you can get me some Desert Clothes,", "I'll trade you for my slaves clothes again..", "Do you want to trade?");
                                int trade = Functions.___showMenu(p, n, "Yes, I'll trade.", "No thanks...");
                                if (trade == 0) {
                                    necessaryStuffSlave(p, n);
                                } else
                                    if (trade == 1) {
                                        Functions.___npcTalk(p, n, "Ok, fair enough, let me know if you change your mind though.");
                                    }

                            }

                        break;
                }
            }
            switch (cID) {
                case TouristTrap.Slave.NEWRECRUIT :
                    Functions.___npcTalk(p, n, "It's a shame that I won't be around long enough to get to know you.", "I'm making a break for it today.", "I have a plan to get out of here!", "It's amazing in it's sophistication.");
                    int thirdMenu = Functions.___showMenu(p, n, "What are those big wooden doors in the corner of the compound?", "Oh yes, that sounds interesting.");
                    if (thirdMenu == 0) {
                        Functions.___npcTalk(p, n, "They lead to an underground mine,", "but you really don't want to go down there.", "I've only seen slaves and guards go down there,", "I never see the slaves come back up.", "At least up here you have a nice view and a bit of sun.");
                        Functions.___message(p, "The slave smiles at you happily and then goes back to his work.");
                    } else
                        if (thirdMenu == 1) {
                            Functions.___npcTalk(p, n, "Yes, it is actually.", "I have all the details figured out except for one.");
                            int four = // do not send over
                            Functions.___showMenu(p, n, false, "What's that then?", "Oh, that's a shame.");
                            if (four == 0) {
                                Functions.___playerTalk(p, n, "What's that then?");
                                Functions.___message(p, "The slave shakes his arms and the chains rattle loudly.");
                                Functions.___npcTalk(p, n, "These bracelets, I can't seem to get them off.", "If I could get them off, I'd be able to climb my way", "out of here.");
                                int five = Functions.___showMenu(p, n, "I can try to undo them for you.", "That's ridiculous, you're talking rubbish.");
                                if (five == 0) {
                                    slaveDialogue(p, n, TouristTrap.Slave.UNDOTHEM);
                                } else
                                    if (five == 1) {
                                        Functions.___npcTalk(p, n, "No, it's true, I can make a break for it", "If I can just get these bracelets off.");
                                        int six = Functions.___showMenu(p, n, "Good luck!", "I can try to undo them for you.");
                                        if (six == 0) {
                                            Functions.___npcTalk(p, n, "Thanks...same to you.");
                                        } else
                                            if (six == 1) {
                                                slaveDialogue(p, n, TouristTrap.Slave.UNDOTHEM);
                                            }

                                    }

                            } else
                                if (four == 1) {
                                    Functions.___playerTalk(p, n, "Oh, that's a shame...", "Still, 'worse things happen at sea right?'");
                                    Functions.___npcTalk(p, n, "You've obviously never worked as a slave", "...in a mining camp...", "...in the middle of the desert");
                                    Functions.___playerTalk(p, n, "Well I suppose I'd better be getting on my way now...");
                                    p.message("The slave nods in agreement and goes back to work.");
                                }

                        }

                    break;
                case TouristTrap.Slave.UNDOTHEM :
                    Functions.___npcTalk(p, n, "Really, that would be great...");
                    Functions.___message(p, "The slave looks at you strangely.");
                    Functions.___npcTalk(p, n, "Hang on a minute...I suppose you want something for doing this?", "The last time I did a trade in this place,", "I nearly lost the shirt from my back!");
                    int trade = // do not send over
                    Functions.___showMenu(p, n, false, "It's funny you should say that...", "That sounds awful.");
                    if (trade == 0) {
                        Functions.___playerTalk(p, n, "It's funny you should say that actually.");
                        Functions.___message(p, "The slave looks at you blankly.");
                        Functions.___npcTalk(p, n, "Yeah, go on!");
                        Functions.___playerTalk(p, n, "If I can get the chains off, you have to give me something, ok?");
                        Functions.___npcTalk(p, n, "Sure, what do you want?");
                        Functions.___playerTalk(p, n, "I want your clothes!", "I can dress like a slave and gain access to the mine area to scout it out.");
                        Functions.___npcTalk(p, n, "Blimey! You're either incredibly brave or incredibly stupid.", "But what would I wear if you take my clothes?", "Get me some nice desert clothes and I'll think about it?", "Do you still want to try and undo the locks for me?");
                        p.updateQuestStage(this, 2);
                        p.getCache().remove("first_kill_captn");
                        p.getCache().remove("mercenary_bet");
                        int go = Functions.___showMenu(p, n, "Yeah, Ok, let's give it a go.", "I need to do some other things first.");
                        if (go == 0) {
                            slaveDialogue(p, n, TouristTrap.Slave.GIVEITAGO);
                        } else
                            if (go == 1) {
                                Functions.___npcTalk(p, n, "Ok, fair enough, let me know when you want to give it another go.");
                            }

                    } else
                        if (trade == 1) {
                            Functions.___playerTalk(p, n, "That sounds awful.");
                            Functions.___npcTalk(p, n, "Yeah, bunch of no hopers, tried to rob me blind.", "But I guess that's what you get when you deal with convicts.");
                        }

                    break;
                case TouristTrap.Slave.GIVEITAGO :
                    Functions.___npcTalk(p, n, "Great!");
                    Functions.___message(p, "You use some nearby bits of wood and wire to try and pick the lock.");
                    int attempt1 = DataConversions.random(0, 1);
                    // failed attempt 1
                    if (attempt1 == 0) {
                        Functions.___message(p, "You fail!", "You didn't manage to pick the lock this time, would you like another go?");
                        int anotherGo = Functions.___showMenu(p, "Yeah, I'll give it another go.", "I'll try something different instead.");
                        if (anotherGo == 0) {
                            Functions.___message(p, "You use some nearby bits of wood and wire to try and pick the lock.");
                            int attempt2 = DataConversions.random(0, 1);
                            // failed attempt 2
                            if (attempt2 == 0) {
                                Functions.___message(p, "You fail!");
                                Npc mercenary = Functions.getNearestNpc(p, NpcId.MERCENARY.id(), 15);
                                if (mercenary != null) {
                                    Functions.___message(p, "A nearby guard spots you!");
                                    Functions.___npcTalk(p, n, "Oh oh!");
                                    Functions.___npcTalk(p, mercenary, "Oi, what are you two doing?");
                                    mercenary.setChasing(p);
                                    Functions.___message(p, "The Guards search you!", "More guards rush to catch you.", "You are roughed up a bit by the guards as you're manhandlded to a cell.");
                                    Functions.___npcTalk(p, mercenary, "Into the cell you go! I hope this teaches you a lesson.");
                                    p.teleport(89, 801);
                                }
                            } else {
                                succeedFreeSlave(p, n);
                            }
                        } else
                            if (anotherGo == 1) {
                                Functions.___message(p, "You decide to try something else.");
                                Functions.___npcTalk(p, n, "Are you givin in already?");
                                Functions.___playerTalk(p, n, "I just want to try something else.");
                                Functions.___npcTalk(p, n, "Ok, if you want to try again, let me know.");
                            }

                    } else {
                        succeedFreeSlave(p, n);
                    }
                    break;
            }
        }
    }

    private void succeedFreeSlave(Player p, Npc n) {
        Functions.___message(p, "You hear a satisfying 'click' as you tumble the lock mechanism.");
        Functions.___npcTalk(p, n, "Great! You did it!");
        necessaryStuffSlave(p, n);
    }

    private void necessaryStuffSlave(Player p, Npc n) {
        if ((Functions.hasItem(p, ItemId.DESERT_SHIRT.id()) && Functions.hasItem(p, ItemId.DESERT_ROBE.id())) && Functions.hasItem(p, ItemId.DESERT_BOOTS.id())) {
            Functions.___npcTalk(p, n, "Great! You have the Desert Clothes!");
            Functions.___message(p, "The slave starts getting undressed right in front of you.");
            Functions.___npcTalk(p, n, "Ok, here's the clothes, I won't need them anymore.");
            Functions.___message(p, "The slave gives you his dirty, flea infested robe.", "The slave gives you his muddy, sweat soaked shirt.");
            p.getInventory().replace(ItemId.DESERT_ROBE.id(), ItemId.SLAVES_ROBE_BOTTOM.id());
            p.getInventory().replace(ItemId.DESERT_SHIRT.id(), ItemId.SLAVES_ROBE_TOP.id());
            Functions.removeItem(p, ItemId.DESERT_BOOTS.id(), 1);
            Npc newSlave = Functions.transform(n, NpcId.ESCAPING_MINING_SLAVE.id(), true);
            Functions.sleep(1000);
            delayedReturnSlave(p, newSlave);
            Functions.___npcTalk(p, newSlave, "Right, I'm off! Good luck!");
            Functions.___playerTalk(p, newSlave, "Yeah, good luck to you too!");
            if ((p.getQuestStage(this) == 2) || (p.getQuestStage(this) == 3))
                p.updateQuestStage(this, 4);

            return;
        }
        if (((!Functions.hasItem(p, ItemId.DESERT_SHIRT.id())) && (!Functions.hasItem(p, ItemId.DESERT_ROBE.id()))) && (!Functions.hasItem(p, ItemId.DESERT_BOOTS.id()))) {
            Functions.___npcTalk(p, n, "I need a desert shirt, robe and boots if you want these clothes off me.");
        } else
            if (((!Functions.hasItem(p, ItemId.DESERT_SHIRT.id())) && (!Functions.hasItem(p, ItemId.DESERT_ROBE.id()))) && Functions.hasItem(p, ItemId.DESERT_BOOTS.id())) {
                Functions.___npcTalk(p, n, "I need desert robe and shirt if you want these clothes off me.");
            } else
                if (((!Functions.hasItem(p, ItemId.DESERT_SHIRT.id())) && Functions.hasItem(p, ItemId.DESERT_ROBE.id())) && (!Functions.hasItem(p, ItemId.DESERT_BOOTS.id()))) {
                    Functions.___npcTalk(p, n, "I need a desert shirt and boots if you want these clothes off me.");
                } else
                    if ((Functions.hasItem(p, ItemId.DESERT_SHIRT.id()) && (!Functions.hasItem(p, ItemId.DESERT_ROBE.id()))) && (!Functions.hasItem(p, ItemId.DESERT_BOOTS.id()))) {
                        Functions.___npcTalk(p, n, "I need desert robe and boots if you want these clothes off me.");
                    } else
                        if (((!Functions.hasItem(p, ItemId.DESERT_SHIRT.id())) && Functions.hasItem(p, ItemId.DESERT_ROBE.id())) && Functions.hasItem(p, ItemId.DESERT_BOOTS.id())) {
                            Functions.___npcTalk(p, n, "I need a desert shirt if you want these clothes off me.");
                        } else
                            if ((Functions.hasItem(p, ItemId.DESERT_SHIRT.id()) && (!Functions.hasItem(p, ItemId.DESERT_ROBE.id()))) && Functions.hasItem(p, ItemId.DESERT_BOOTS.id())) {
                                Functions.___npcTalk(p, n, "I need desert robe if you want these clothes off me.");
                            } else
                                if ((Functions.hasItem(p, ItemId.DESERT_SHIRT.id()) && Functions.hasItem(p, ItemId.DESERT_ROBE.id())) && (!Functions.hasItem(p, ItemId.DESERT_BOOTS.id()))) {
                                    Functions.___npcTalk(p, n, "I need desert boots if you want these clothes off me.");
                                }






        if (p.getQuestStage(this) == 2)
            p.updateQuestStage(this, 3);

    }

    private void mercenaryInsideDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.MERCENARY_ESCAPEGATES.id()) {
            if (cID == (-1)) {
                if (p.getLocation().inTouristTrapCave()) {
                    if ((!p.getInventory().wielding(ItemId.SLAVES_ROBE_BOTTOM.id())) && (!p.getInventory().wielding(ItemId.SLAVES_ROBE_TOP.id()))) {
                        p.message("This guard looks as if he's been down here a while.");
                        Functions.___npcTalk(p, n, "Hey, you're no slave!", "What are you doing down here?");
                        n.setChasing(p);
                        if (p.getQuestStage(this) != (-1)) {
                            Functions.___message(p, "More guards rush to catch you.", "You are roughed up a bit by the guards as you're manhandlded to a cell.");
                            Functions.___npcTalk(p, n, "Into the cell you go! I hope this teaches you a lesson.");
                            p.teleport(89, 801);
                        }
                        return;
                    }
                    if ((p.getQuestStage(this) >= 9) || (p.getQuestStage(this) == (-1))) {
                        p.message("This guard looks as if he's been down here a while.");
                        Functions.___npcTalk(p, n, "That pineapple was just delicious, many thanks.", "I don't suppose you could get me another?");
                        p.message("The guard looks at you pleadingly.");
                        return;
                    }
                    p.message("This guard looks as if he's been down here a while.");
                    Functions.___npcTalk(p, n, "Yeah, what do you want?");
                    int mama = Functions.___showMenu(p, n, "Er nothing really.", "I'd like to mine in a different area.");
                    if (mama == 0) {
                        Functions.___npcTalk(p, n, "Ok...so move along and get on with your work.");
                    } else
                        if (mama == 1) {
                            Functions.___npcTalk(p, n, "Oh, so you want to work in another area of the mine heh?");
                            Functions.___message(p, "The guard seems quite pleased with his rhetorical question.");
                            Functions.___npcTalk(p, n, "Well, I can understand that, a change is as good as a rest they say.");
                            int menu = Functions.___showMenu(p, n, "Huh, fat chance of a rest for me.", "Yes sir, you're quite right sir.");
                            if (menu == 0) {
                                Functions.___npcTalk(p, n, "You miserable whelp!", "Get back to work!");
                                p.damage(2);
                                p.message("The guard cuffs you around head.");
                            } else
                                if (menu == 1) {
                                    Functions.___npcTalk(p, n, "Of course I'm right...", "And what goes around comes around as they say.", "And it's been absolutely ages since I've had anything different to eat.", "What I wouldn't give for some ripe and juicy pineapple for a change.", "And those Tenti's have the best pineapple in this entire area.");
                                    p.message("The guard winks at you.");
                                    Functions.___npcTalk(p, n, "I'm sure you get my meaning...");
                                    int pus = Functions.___showMenu(p, n, "How am I going to get some pineapples around here?", "Yes sir, we understand each other perfectly.", "What are the 'Tenti's'?");
                                    if (pus == 0) {
                                        mercenaryInsideDialogue(p, n, TouristTrap.MercenaryInside.PINEAPPLES);
                                    } else
                                        if (pus == 1) {
                                            mercenaryInsideDialogue(p, n, TouristTrap.MercenaryInside.UNDERSTAND);
                                        } else
                                            if (pus == 2) {
                                                Functions.___npcTalk(p, n, "Well, you really don't come from around here do you?", "The tenti's are what we call the nomadic people west of here.", "They live in tents, so we call them the tenti's", "They have great pineapples!", "I'm sure you get my meaning...");
                                                int pus2 = Functions.___showMenu(p, n, "How am I going to get some pineapples around here?", "Yes sir, we understand each other perfectly.");
                                                if (pus2 == 0) {
                                                    mercenaryInsideDialogue(p, n, TouristTrap.MercenaryInside.PINEAPPLES);
                                                } else
                                                    if (pus2 == 1) {
                                                        mercenaryInsideDialogue(p, n, TouristTrap.MercenaryInside.UNDERSTAND);
                                                    }

                                            }


                                }

                        }

                    return;
                }
                p.message("This guard looks as if he's been in the sun for a while.");
                Functions.___npcTalk(p, n, "Move along now...");
            }
            switch (cID) {
                case TouristTrap.MercenaryInside.PINEAPPLES :
                    if (p.getQuestStage(this) == 4) {
                        p.updateQuestStage(this, 5);
                    }
                    Functions.___npcTalk(p, n, "Well, that's not my problem is it?", "Also, I know that you slaves trade your items down here.", "I'm sure that if you're resourceful enough, you'll come up with the goods.", "Now, get along and do some work, before we're both in for it.");
                    break;
                case TouristTrap.MercenaryInside.UNDERSTAND :
                    if (p.getQuestStage(this) == 4) {
                        p.updateQuestStage(this, 5);
                    }
                    Functions.___npcTalk(p, n, "Ok, good then.");
                    p.message("The guard moves back to his post and winks at you knowingly.");
                    break;
            }
        }
    }

    private void bedabinNomadDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.BEDABIN_NOMAD.id()) {
            if (cID == (-1)) {
                Functions.___npcTalk(p, n, "Hello Effendi!", "How can I help you?");
                int menu = // do not send over
                Functions.___showMenu(p, n, false, "What is this place?", "Where is the Shantay Pass?", "Buy a jug of water - 5 Gold Pieces.", "Buy a full waterskin - 20 Gold Pieces.", "Buy a bucket of water - 20 Gold Pieces.");
                if (menu == 0) {
                    Functions.___playerTalk(p, n, "What is this place?");
                    bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.PLACE);
                } else
                    if (menu == 1) {
                        Functions.___playerTalk(p, n, "Where is the Shantay Pass.");
                        bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.SHANTAYPASS);
                    } else
                        if (menu == 2) {
                            Functions.___playerTalk(p, n, "Buy a jug of water - 5 Gold Pieces.");
                            bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.JUGOFWATER);
                        } else
                            if (menu == 3) {
                                Functions.___playerTalk(p, n, "Buy a full waterskin - 25 Gold Pieces.");
                                bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.FULLWATERSKIN);
                            } else
                                if (menu == 4) {
                                    Functions.___playerTalk(p, n, "Buy a bucket of water - 20 Gold Pieces.");
                                    bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.BUCKETOFWATER);
                                }




            }
            switch (cID) {
                case TouristTrap.BedabinNomad.BUCKETOFWATER :
                    if (Functions.hasItem(p, ItemId.COINS.id(), 20)) {
                        Functions.___message(p, "You hand over 20 gold pieces.");
                        Functions.removeItem(p, ItemId.COINS.id(), 20);
                        Functions.___npcTalk(p, n, "Very well Effendi!");
                        Functions.___message(p, "You recieve a bucket of water.");
                        Functions.addItem(p, ItemId.BUCKET_OF_WATER.id(), 1);
                    } else {
                        Functions.___message(p, "Sorry Effendi, you don't seem to have the money.");
                    }
                    Functions.___npcTalk(p, n, "How can I help you?");
                    int newMenu = Functions.___showMenu(p, n, "What is this place?", "Where is the Shantay Pass.", "Buy a jug of water - 5 Gold Pieces.", "Buy a full waterskin - 25 Gold Pieces.", "Buy a bucket of water - 20 Gold Pieces.");
                    if (newMenu == 0) {
                        bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.PLACE);
                    } else
                        if (newMenu == 1) {
                            bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.SHANTAYPASS);
                        } else
                            if (newMenu == 2) {
                                bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.JUGOFWATER);
                            } else
                                if (newMenu == 3) {
                                    bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.FULLWATERSKIN);
                                } else
                                    if (newMenu == 4) {
                                        bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.BUCKETOFWATER);
                                    }




                    break;
                case TouristTrap.BedabinNomad.FULLWATERSKIN :
                    if (Functions.hasItem(p, ItemId.COINS.id(), 25)) {
                        Functions.___message(p, "You hand over 25 gold pieces.");
                        Functions.removeItem(p, ItemId.COINS.id(), 25);
                        Functions.___npcTalk(p, n, "Very well Effendi!");
                        Functions.___message(p, "You recieve a full waterskin.");
                        Functions.addItem(p, ItemId.FULL_WATER_SKIN.id(), 1);
                    } else {
                        Functions.___message(p, "Sorry Effendi, you don't seem to have the money.");
                    }
                    Functions.___npcTalk(p, n, "How can I help you?");
                    int option = Functions.___showMenu(p, n, "What is this place?", "Where is the Shantay Pass.", "Buy a jug of water - 5 Gold Pieces.", "Buy a full waterskin - 25 Gold Pieces.", "Buy a bucket of water - 20 Gold Pieces.");
                    if (option == 0) {
                        bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.PLACE);
                    } else
                        if (option == 1) {
                            bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.SHANTAYPASS);
                        } else
                            if (option == 2) {
                                bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.JUGOFWATER);
                            } else
                                if (option == 3) {
                                    bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.FULLWATERSKIN);
                                } else
                                    if (option == 4) {
                                        bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.BUCKETOFWATER);
                                    }




                    break;
                case TouristTrap.BedabinNomad.JUGOFWATER :
                    if (Functions.hasItem(p, ItemId.COINS.id(), 5)) {
                        Functions.___message(p, "You hand over 5 gold pieces.");
                        Functions.removeItem(p, ItemId.COINS.id(), 5);
                        Functions.___npcTalk(p, n, "Very well Effendi!");
                        Functions.___message(p, "You recieve a jug full or water.");
                        Functions.addItem(p, ItemId.JUG_OF_WATER.id(), 1);
                    } else {
                        Functions.___message(p, "Sorry Effendi, you don't seem to have the money.");
                    }
                    Functions.___npcTalk(p, n, "How can I help you?");
                    int optiony = Functions.___showMenu(p, n, "What is this place?", "Where is the Shantay Pass.", "Buy a jug of water - 5 Gold Pieces.", "Buy a full waterskin - 25 Gold Pieces.", "Buy a bucket of water - 20 Gold Pieces.");
                    if (optiony == 0) {
                        bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.PLACE);
                    } else
                        if (optiony == 1) {
                            bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.SHANTAYPASS);
                        } else
                            if (optiony == 2) {
                                bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.JUGOFWATER);
                            } else
                                if (optiony == 3) {
                                    bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.FULLWATERSKIN);
                                } else
                                    if (optiony == 4) {
                                        bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.BUCKETOFWATER);
                                    }




                    break;
                case TouristTrap.BedabinNomad.PLACE :
                    Functions.___npcTalk(p, n, "This is the camp of the Bedabin.", "Talk to our leader, Al Shabim, he'll be happy to chat.");
                    p.message("We can sell you very reasonably priced water...");
                    Functions.___npcTalk(p, n, "How can I help you?");
                    int opt = Functions.___showMenu(p, n, "Where is the Shantay Pass.", "Buy a jug of water - 5 Gold Pieces.", "Buy a full waterskin - 25 Gold Pieces.", "Buy a bucket of water - 20 Gold Pieces.");
                    if (opt == 0) {
                        bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.SHANTAYPASS);
                    } else
                        if (opt == 1) {
                            bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.JUGOFWATER);
                        } else
                            if (opt == 2) {
                                bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.FULLWATERSKIN);
                            } else
                                if (opt == 3) {
                                    bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.BUCKETOFWATER);
                                }



                    break;
                case TouristTrap.BedabinNomad.SHANTAYPASS :
                    Functions.___npcTalk(p, n, "It is North East of here effendi, across the trackless desert.", "It will be a thirsty trip, can I interest you in a drink?", "How can I help you?");
                    int options = Functions.___showMenu(p, n, "Buy a jug of water - 5 Gold Pieces.", "What is this place?", "Buy a full waterskin - 25 Gold Pieces.", "Buy a bucket of water - 20 Gold Pieces.");
                    if (options == 0) {
                        bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.JUGOFWATER);
                    } else
                        if (options == 1) {
                            bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.PLACE);
                        } else
                            if (options == 2) {
                                bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.FULLWATERSKIN);
                            } else
                                if (options == 3) {
                                    bedabinNomadDialogue(p, n, TouristTrap.BedabinNomad.BUCKETOFWATER);
                                }



                    break;
            }
        }
    }

    private void alShabimDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.AL_SHABIM.id()) {
            if (cID == (-1)) {
                switch (p.getQuestStage(this)) {
                    case 0 :
                    case 1 :
                    case 2 :
                    case 3 :
                    case 4 :
                        Functions.___npcTalk(p, n, "Hello Effendi!", "I am Al Shabim, greetings on behalf of the Bedabin nomads.");
                        int menu = Functions.___showMenu(p, n, "What is this place?", "Goodbye!");
                        if (menu == 0) {
                            alShabimDialogue(p, n, TouristTrap.AlShabim.WHATISTHISPLACE);
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "Very well, good day Effendi!");
                            }

                        break;
                    case 5 :
                        Functions.___npcTalk(p, n, "Hello Effendi!", "I am Al Shabim, greetings on behalf of the Bedabin nomads.");
                        int option = Functions.___showMenu(p, n, "I am looking for a pineapple.", "What is this place?");
                        if (option == 0) {
                            Functions.___npcTalk(p, n, "Oh yes, well that is interesting.", "Our sweet pineapples are renowned throughout the whole of Kharid !", "And I'll give you one if you do me a favour?");
                            Functions.___playerTalk(p, n, "Yes ?");
                            Functions.___npcTalk(p, n, "Captain Siad at the mining camp is holding some secret information.", "It is very important to us and we would like you to get it for us.", "It gives details of an interesting, yet ancient weapon.", "We would gladly share this information with you.", "All you have to do is gain access to his private room upstairs.", "We have a key for the chest that contains this information.", "Are you interested in our deal?");
                            int opt = Functions.___showMenu(p, n, "Yes, I'm interested.", "Not at the moment.");
                            if (opt == 0) {
                                Functions.___npcTalk(p, n, "That's great Effendi!", "Here is a copy of the key that should give you access to the chest.", "Bring us back the plans inside the chest, they should be sealed.", "All haste to you Effendi!");
                                Functions.addItem(p, ItemId.BEDOBIN_COPY_KEY.id(), 1);
                                p.updateQuestStage(this, 6);
                            } else
                                if (opt == 1) {
                                    Functions.___npcTalk(p, n, "Very well Effendi!");
                                }

                        } else
                            if (option == 1) {
                                alShabimDialogue(p, n, TouristTrap.AlShabim.WHATISTHISPLACE);
                            }

                        break;
                    case 6 :
                    case 7 :
                        Functions.___npcTalk(p, n, "Hello Effendi!");
                        if (Functions.hasItem(p, ItemId.PROTOTYPE_THROWING_DART.id())) {
                            alShabimDialogue(p, n, TouristTrap.AlShabim.MADE_WEAPON);
                        } else
                            if (Functions.hasItem(p, ItemId.TECHNICAL_PLANS.id()) && (!Functions.hasItem(p, ItemId.PROTOTYPE_THROWING_DART.id()))) {
                                alShabimDialogue(p, n, TouristTrap.AlShabim.HAVE_PLANS);
                            } else
                                if (Functions.hasItem(p, ItemId.BEDOBIN_COPY_KEY.id()) && (!Functions.hasItem(p, ItemId.TECHNICAL_PLANS.id()))) {
                                    Functions.___npcTalk(p, n, "How are things going Effendi?");
                                    int dede = Functions.___showMenu(p, n, "Very well thanks!", "Not so good actually!", "What is this place?", "Goodbye!");
                                    if (dede == 0) {
                                        Functions.___npcTalk(p, n, "Well, hurry along and get those plans for me.");
                                    } else
                                        if (dede == 1) {
                                            Functions.___npcTalk(p, n, "Well, first you need to get those plans from Captain Siad.");
                                        } else
                                            if (dede == 2) {
                                                alShabimDialogue(p, n, TouristTrap.AlShabim.WHATISTHISPLACE);
                                            } else
                                                if (dede == 3) {
                                                    Functions.___npcTalk(p, n, "Very well, good day Effendi!");
                                                }



                                } else {
                                    int kaka = Functions.___showMenu(p, n, "I've lost the key!", "What is this place?", "Goodbye!");
                                    if (kaka == 0) {
                                        Functions.___npcTalk(p, n, "How very careless of you!", "Here is another key, don't lose it this time !");
                                        p.message("Al Shabim gives you another key.");
                                        Functions.addItem(p, ItemId.BEDOBIN_COPY_KEY.id(), 1);
                                    } else
                                        if (kaka == 1) {
                                            alShabimDialogue(p, n, TouristTrap.AlShabim.WHATISTHISPLACE);
                                        } else
                                            if (kaka == 2) {
                                                Functions.___npcTalk(p, n, "Very well, good day Effendi!");
                                            }


                                }


                        break;
                    case 8 :
                    case 9 :
                    case 10 :
                    case -1 :
                        if (Functions.hasItem(p, ItemId.PROTOTYPE_THROWING_DART.id())) {
                            Functions.___npcTalk(p, n, "Hello Effendi!", "Wonderful, I see you have made the new weapon!", "Where did you get this from Effendi!", "I'll have to confiscate this for your own safety!");
                            Functions.removeItem(p, ItemId.PROTOTYPE_THROWING_DART.id(), 1);
                            return;
                        }
                        if (Functions.hasItem(p, ItemId.TECHNICAL_PLANS.id())) {
                            Functions.___npcTalk(p, n, "Hello Effendi!");
                            alShabimDialogue(p, n, TouristTrap.AlShabim.HAVE_PLANS);
                            return;
                        }
                        if (p.getQuestStage(this) == 8) {
                            Functions.___npcTalk(p, n, "Hello Effendi!", "Many thanks with your help previously Effendi!");
                            if (Functions.hasItem(p, ItemId.TENTI_PINEAPPLE.id())) {
                                int mopt = Functions.___showMenu(p, n, "What is this place?", "Goodbye!");
                                if (mopt == 0) {
                                    alShabimDialogue(p, n, TouristTrap.AlShabim.WHATISTHISPLACE);
                                } else
                                    if (mopt == 1) {
                                        Functions.___npcTalk(p, n, "Very well, good day Effendi!");
                                    }

                            } else {
                                int mopt = Functions.___showMenu(p, n, "I am looking for a pineapple.", "What is this place?");
                                if (mopt == 0) {
                                    Functions.___npcTalk(p, n, "Here is another pineapple, try not to lose this one.");
                                    p.message("Al Shabim gives you another pineapple.");
                                    Functions.addItem(p, ItemId.TENTI_PINEAPPLE.id(), 1);
                                } else
                                    if (mopt == 1) {
                                        alShabimDialogue(p, n, TouristTrap.AlShabim.WHATISTHISPLACE);
                                    }

                            }
                        } else {
                            Functions.___npcTalk(p, n, "Hello Effendi!", "Many thanks with your help previously Effendi!", "I am Al Shabim, greetings on behalf of the Bedabin nomads.");
                            int mopt = Functions.___showMenu(p, n, "What is this place?", "Goodbye!");
                            if (mopt == 0) {
                                alShabimDialogue(p, n, TouristTrap.AlShabim.WHATISTHISPLACE);
                            } else
                                if (mopt == 1) {
                                    Functions.___npcTalk(p, n, "Very well, good day Effendi!");
                                }

                        }
                        break;
                }
            }
            switch (cID) {
                case TouristTrap.AlShabim.WHATISTHISPLACE :
                    Functions.___npcTalk(p, n, "This is the home of the Bedabin, ", "We're a peaceful tribe of desert dwellers.", "Some idiots call us 'Tenti's', a childish name borne of ignorance.", "We're renowned for surviving in the harshest desert climate.", "We also grow the 'Bedabin ambrosia.'...", "A pineapple of such delicious sumptiousness that it defies description.", "Take a look around our camp if you like!");
                    int menu = Functions.___showMenu(p, n, "Ok Thanks!", "What is there to do around here?");
                    if (menu == 0) {
                        Functions.___npcTalk(p, n, "Good day Effendi!");
                    } else
                        if (menu == 1) {
                            Functions.___npcTalk(p, n, "Well, we are all very busy most of the time tending to the pineapples.", "They are grown in a secret location.", "To stop thieves from raiding our most precious prize.");
                        }

                    break;
                case TouristTrap.AlShabim.HAVE_PLANS :
                    Functions.___npcTalk(p, n, "Aha! I see you have the plans.", "This is great!", "However, these plans do indeed look very technical", "My people have further need of your skills.", "If you can help us to manufacture this item,", "we will share it's secret with you.", "Does this deal interest you effendi?");
                    int tati = Functions.___showMenu(p, n, "Yes, I'm very interested.", "No, sorry.");
                    if (tati == 0) {
                        if (Functions.hasItem(p, ItemId.BRONZE_BAR.id()) && Functions.hasItem(p, ItemId.FEATHER.id(), 10)) {
                            Functions.___npcTalk(p, n, "Aha! I see you have the items we need!", "Are you still willing to help make the weapon?");
                            int make = Functions.___showMenu(p, n, "Yes, I'm kind of curious.", "No,sorry.");
                            if (make == 0) {
                                Functions.___npcTalk(p, n, "Ok Effendi, you need to follow the plans.", "You will need some special tools for this...", "There is a forge in the other tent.", "You have my permision to use it, but show the plans to the guard.", "You have the plans and the all the items needed, ", "You should be able to complete the item on your own.", "Please bring me the item when it is finished.");
                                if (p.getQuestStage(this) == 6) {
                                    p.updateQuestStage(this, 7);
                                }
                            } else
                                if (make == 1) {
                                    Functions.___npcTalk(p, n, "As you wish effendi!", "Come back if you change your mind!");
                                }

                        } else {
                            Functions.___npcTalk(p, n, "Great, we need the following items.", "A bar of pure bronze and 10 feathers.", "Bring them to me and we'll continue to make the item.");
                        }
                    } else
                        if (tati == 1) {
                            Functions.___npcTalk(p, n, "As you wish effendi!", "Come back if you change your mind!");
                        }

                    break;
                case TouristTrap.AlShabim.MADE_WEAPON :
                    Functions.___npcTalk(p, n, "Wonderful, I see you have made the new weapon!");
                    Functions.___message(p, "You show Al Shabim the prototype dart.");
                    Functions.removeItem(p, ItemId.PROTOTYPE_THROWING_DART.id(), 1);
                    Functions.___npcTalk(p, n, "This is truly fantastic Effendi!");
                    if (Functions.hasItem(p, ItemId.TECHNICAL_PLANS.id())) {
                        Functions.___npcTalk(p, n, "We will take the technical plans for the weapon as well.");
                        Functions.removeItem(p, ItemId.TECHNICAL_PLANS.id(), 1);
                        Functions.___message(p, "You hand over the technical plans for the weapon.");
                    }
                    Functions.___npcTalk(p, n, "We are forever grateful for this gift.", "My advisors have discovered some secrets which we will share with you.");
                    Functions.___message(p, "Al Shabim's advisors show you some advanced techniques for making the new weapon.");
                    Functions.___npcTalk(p, n, "Oh, and here is your pineapple!");
                    Functions.addItem(p, ItemId.TENTI_PINEAPPLE.id(), 1);
                    Functions.___npcTalk(p, n, "Please accept this selection of six bronze throwing darts", "as a token of our appreciation.");
                    Functions.addItem(p, ItemId.BRONZE_THROWING_DART.id(), 6);
                    if (Functions.hasItem(p, ItemId.BEDOBIN_COPY_KEY.id())) {
                        Functions.___npcTalk(p, n, "I'll take that key off your hands as well effendi!");
                        Functions.removeItem(p, ItemId.BEDOBIN_COPY_KEY.id(), 1);
                        Functions.___npcTalk(p, n, "Many thanks!");
                    }
                    p.message("");
                    p.message("********************************************************************");
                    p.message("*** You can now make a new weapon type: Throwing dart. ***");
                    p.message("********************************************************************");
                    p.updateQuestStage(this, 8);// >= 8 or -1 for throwing darts.

                    break;
            }
        }
    }

    private void captainSiadDialogue(Player p, Npc n, int cID, GameObject obj) {
        // USED FOR CHEST AND TALK-TO
        if (n.getID() == NpcId.CAPTAIN_SIAD.id()) {
            if (cID == (-1)) {
                switch (p.getQuestStage(this)) {
                    case 0 :
                    case 1 :
                    case 2 :
                    case 3 :
                    case 4 :
                    case 5 :
                    case 6 :
                    case 7 :
                    case 8 :
                    case 9 :
                    case 10 :
                    case -1 :
                        if (obj != null) {
                            Functions.___message(p, "The captains spots you before you manage to open the chest...");
                        } else {
                            Functions.___message(p, "The captain looks up from his work as you address him.");
                        }
                        if ((Functions.hasItem(p, ItemId.TECHNICAL_PLANS.id()) || (p.getQuestStage(this) >= 8)) || (p.getQuestStage(this) == (-1))) {
                            Functions.___npcTalk(p, n, "I don't have time to talk to you.", "Move along please!");
                            return;
                        }
                        Functions.___npcTalk(p, n, "What are you doing in here?");
                        int menu = Functions.___showMenu(p, n, "I wanted to have a chat?", "What's it got to do with you?", "Prepare to die!", "All the slaves have broken free!", "Fire!Fire!");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "You don't belong in here, get out!");
                            int m = Functions.___showMenu(p, n, "But I just need two minutes of your time?", "Prepare to die!", "All the slaves have broken free!", "Fire!Fire!", "You seem to have a lot of books!");
                            if (m == 0) {
                                captainSiadDialogue(p, n, TouristTrap.Siad.TWOMINUTES, null);
                            } else
                                if (m == 1) {
                                    captainSiadDialogue(p, n, TouristTrap.Siad.PREPARETODIE, null);
                                } else
                                    if (m == 2) {
                                        captainSiadDialogue(p, n, TouristTrap.Siad.SLAVESBROKENFREE, null);
                                    } else
                                        if (m == 3) {
                                            captainSiadDialogue(p, n, TouristTrap.Siad.FIREFIRE, null);
                                        } else
                                            if (m == 4) {
                                                captainSiadDialogue(p, n, TouristTrap.Siad.BOOKS, null);
                                            }




                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "This happens to be my office.", "Now explain yourself before I run you through!");
                                int keke = Functions.___showMenu(p, n, "The guard downstairs said you were lonely.", "I need to service your chest.");
                                if (keke == 0) {
                                    captainSiadDialogue(p, n, TouristTrap.Siad.LONELY, null);
                                } else
                                    if (keke == 1) {
                                        captainSiadDialogue(p, n, TouristTrap.Siad.SERVICE, null);
                                    }

                            } else
                                if (menu == 2) {
                                    captainSiadDialogue(p, n, TouristTrap.Siad.PREPARETODIE, null);
                                } else
                                    if (menu == 3) {
                                        captainSiadDialogue(p, n, TouristTrap.Siad.SLAVESBROKENFREE, null);
                                    } else
                                        if (menu == 4) {
                                            captainSiadDialogue(p, n, TouristTrap.Siad.FIREFIRE, null);
                                        }




                        break;
                }
            }
            switch (cID) {
                case TouristTrap.Siad.PREPARETODIE :
                    Functions.___npcTalk(p, n, "I'll teach you a lesson!", "Guards! Guards!");
                    captainSiadDialogue(p, n, TouristTrap.Siad.PUNISHED, null);
                    break;
                case TouristTrap.Siad.PUNISHED :
                    Functions.___message(p, "The Guards search you!");
                    if (Functions.hasItem(p, ItemId.METAL_KEY.id())) {
                        p.message("The guards find the main gate key and remove it!");
                        Functions.removeItem(p, ItemId.METAL_KEY.id(), 1);
                    }
                    Functions.___message(p, "Some guards rush to help the captain.", "You are roughed up a bit by the guards as you're manhandlded into a cell.");
                    p.damage(7);
                    Functions.___message(p, "@yel@Guards: Into the cell you go! I hope this teaches you a lesson.");
                    p.teleport(89, 801);
                    break;
                case TouristTrap.Siad.TWOMINUTES :
                    Functions.___npcTalk(p, n, "Well, ok, but very quickly.", "I am a very busy person you know!");
                    int menu = Functions.___showMenu(p, n, "Well, er...erm, I err....", "Oh my, a dragon just flew straight past your window!");
                    if (menu == 0) {
                        captainSiadDialogue(p, n, TouristTrap.Siad.ERM, null);
                    } else
                        if (menu == 1) {
                            captainSiadDialogue(p, n, TouristTrap.Siad.DRAGON, null);
                        }

                    break;
                case TouristTrap.Siad.SLAVESBROKENFREE :
                    if (!succeedRate(p)) {
                        Functions.___npcTalk(p, n, "Don't talk rubbish, the warning siren isn't sounding.", "Now state your business before I have you thrown out.");
                        int gay = Functions.___showMenu(p, n, "The guard downstairs said you were lonely.", "I need to service your chest.");
                        if (gay == 0) {
                            captainSiadDialogue(p, n, TouristTrap.Siad.LONELY, null);
                        } else
                            if (gay == 1) {
                                captainSiadDialogue(p, n, TouristTrap.Siad.SERVICE, null);
                            }

                    } else {
                        Functions.___message(p, "The captain seems distracted with what you just said.", "The captain looks out of the window to see if there are any prisoners escaping.");
                        if (!p.getCache().hasKey("tourist_chest")) {
                            p.getCache().store("tourist_chest", true);// if don't have the key, remove the cache.

                        }
                    }
                    break;
                case TouristTrap.Siad.ERM :
                    Functions.___npcTalk(p, n, "Come on, spit it out!", "Right that's it!", "Guards!");
                    captainSiadDialogue(p, n, TouristTrap.Siad.PUNISHED, null);
                    break;
                case TouristTrap.Siad.SERVICE :
                    Functions.___npcTalk(p, n, "You need to what?");
                    Functions.___playerTalk(p, n, "I need to service your chest?");
                    Functions.___npcTalk(p, n, "There's nothing wrong with the chest, it's fine, now get out!");
                    int fire = Functions.___showMenu(p, n, "I'm here to take your plans, hand them over now or I'll kill you!", "Fire!Fire!");
                    if (fire == 0) {
                        captainSiadDialogue(p, n, TouristTrap.Siad.PLANS, null);
                    } else
                        if (fire == 1) {
                            captainSiadDialogue(p, n, TouristTrap.Siad.FIREFIRE, null);
                        }

                    break;
                case TouristTrap.Siad.DRAGON :
                    if (!succeedRate(p)) {
                        Functions.___npcTalk(p, n, "Really! Where?", "I don't see any dragons young man?", "Now, please get out of my office, I have work to do.");
                        p.message("The Captain goes back to his work.");
                    } else {
                        captainSiadDialogue(p, n, TouristTrap.Siad.SUCCEED, null);
                    }
                    break;
                case TouristTrap.Siad.LONELY :
                    Functions.___message(p, "The captain gives you a puzzled look.");
                    Functions.___npcTalk(p, n, "Well, I most certainly am not lonely!", "I'm an incredibly busy man you know!", "Now, get to the point, what do you want?");
                    int opt = Functions.___showMenu(p, n, "Well, er...erm, I err....", "I need to service your chest.");
                    if (opt == 0) {
                        captainSiadDialogue(p, n, TouristTrap.Siad.ERM, null);
                    } else
                        if (opt == 1) {
                            captainSiadDialogue(p, n, TouristTrap.Siad.SERVICE, null);
                        }

                    break;
                case TouristTrap.Siad.PLANS :
                    Functions.___npcTalk(p, n, "Don't be silly!", "I'm going to teach you a lesson!", "Guards! Guards!");
                    captainSiadDialogue(p, n, TouristTrap.Siad.PUNISHED, null);
                    break;
                case TouristTrap.Siad.SUCCEED :
                    Functions.___message(p, "The captain seems distracted with what you just said.", "The captain looks out of the window for the dragon.");
                    if (!p.getCache().hasKey("tourist_chest")) {
                        p.getCache().store("tourist_chest", true);// if don't have the key, remove the cache.

                    }
                    break;
                case TouristTrap.Siad.FIREFIRE :
                    if (!succeedRate(p)) {
                        Functions.___npcTalk(p, n, "Where's the fire?", "I don't see any fire?");
                        int fireMenu = Functions.___showMenu(p, n, "It's down in the lower mines, sound the alarm!", "Oh yes,  you're right, they must have put it out!");
                        if (fireMenu == 0) {
                            Functions.___npcTalk(p, n, "You go and sound the alarm, I can't see anything wrong with the mine.", "Have you seen the fire yourself?");
                            int variableF = Functions.___showMenu(p, n, "Yes actually!", "Er, no, one of the slaves told me.");
                            if (variableF == 0) {
                                Functions.___npcTalk(p, n, "Well, why didn't you raise the alarm?");
                                int variableG = Functions.___showMenu(p, n, "I don't know where the alarm is.", "I was so concerned for your safety that I rushed to save you.");
                                if (variableG == 0) {
                                    Functions.___npcTalk(p, n, "That's the most ridiculous thing I've heard.", "Who are you? Where do you come from?", "It doesn't matter...");
                                    Functions.___message(p, "The Captain shouts the guards...");
                                    Functions.___npcTalk(p, n, "Guards!", "Show this person out!");
                                    captainSiadDialogue(p, n, TouristTrap.Siad.PUNISHED, null);
                                } else
                                    if (variableG == 1) {
                                        Functions.___npcTalk(p, n, "Well, that's very good of you.", "But as you can see, I am very fine and well thanks!", "Now, please leave so that I can get back to my work.");
                                        p.message("The Captain goes back to his desk.");
                                    }

                            } else
                                if (variableF == 1) {
                                    Functions.___npcTalk(p, n, "Well...you can't believe them, they're all a bunch of convicts.", "Anyway, it doesn't look as if there is a fire down there.", "So I'm going to get on with my work.", "Please remove yourself from my office.");
                                    p.message("The Captain goes back to his desk and starts studying.");
                                }

                        } else
                            if (fireMenu == 1) {
                                Functions.___npcTalk(p, n, "Good, now perhaps you can leave me in peace?", "After all I do have some work to do.");
                                int er = Functions.___showMenu(p, n, "Er, yes Ok then.", "Well, er...erm, I err....");
                                if (er == 0) {
                                    Functions.___npcTalk(p, n, "Good!", "Please remove yourself from my office.");
                                    p.message("The Captain goes back to his desk and starts studying.");
                                } else
                                    if (er == 1) {
                                        captainSiadDialogue(p, n, TouristTrap.Siad.ERM, null);
                                    }

                            }

                    } else {
                        Functions.___message(p, "The captain seems distracted with what you just said.", "The captain looks out of the window to see if is a fire.");
                        if (!p.getCache().hasKey("tourist_chest")) {
                            p.getCache().store("tourist_chest", true);// if don't have the key, remove the cache.

                        }
                    }
                    break;
                case TouristTrap.Siad.BOOKS :
                    Functions.___npcTalk(p, n, "Yes, I do. Now please get to the point?");
                    int books = 0;
                    if (p.getCache().hasKey("sailing")) {
                        books = Functions.___showMenu(p, n, "How long have you been interested in books?", "I could get you some books!", "So, you're interested in sailing?");
                    } else {
                        books = Functions.___showMenu(p, n, "How long have you been interested in books?", "I could get you some books!");
                    }
                    if (books == 0) {
                        Functions.___npcTalk(p, n, "Long enough to know when someone is stalling!", "Ok, that's it, get out!", "Guards!");
                        captainSiadDialogue(p, n, TouristTrap.Siad.PUNISHED, null);
                    } else
                        if (books == 1) {
                            Functions.___npcTalk(p, n, "Oh, really!", "Sorry, not interested!", "GUARDS!");
                            captainSiadDialogue(p, n, TouristTrap.Siad.PUNISHED, null);
                        } else
                            if (books == 2) {
                                p.message("The captain's interest seems to perk up.");
                                Functions.___npcTalk(p, n, "Well, yes actually...", "It's been a passion of mine for some years...");
                                int sail = Functions.___showMenu(p, n, "I could tell by the cut of your jib.", "Not much sailing to be done around here though?");
                                if (sail == 0) {
                                    Functions.___npcTalk(p, n, "Oh yes? Really?");
                                    p.message("The Captain looks flattered.");
                                    Functions.___npcTalk(p, n, "Well, you know, I was quite the catch in my day you know!");
                                    Functions.___message(p, "The captain starts rambling on about his days as a salty sea dog.", "He looks quite distracted...");
                                    if (!p.getCache().hasKey("tourist_chest")) {
                                        p.getCache().store("tourist_chest", true);// if don't have the key, remove the cache.

                                    }
                                } else
                                    if (sail == 1) {
                                        p.message("The captain frowns slightly...");
                                        Functions.___npcTalk(p, n, "Well of course there isn't, we're surrounded by desert.", "Now, why are you here exactly?");
                                        int again = Functions.___showMenu(p, n, "Oh my, a dragon just flew straight past your window!", "Well, er...erm, I err....");
                                        if (again == 0) {
                                            captainSiadDialogue(p, n, TouristTrap.Siad.DRAGON, null);
                                        } else
                                            if (again == 1) {
                                                captainSiadDialogue(p, n, TouristTrap.Siad.ERM, null);
                                            }

                                    }

                            }


                    break;
            }
        }
    }

    private void anaDialogue(Player p, Npc n, int cID) {
        if (cID == (-1)) {
            if (p.getQuestStage(this) == (-1)) {
                p.message("This slave does not appear interested in talking to you.");
                return;
            }
            if (((!p.getInventory().wielding(ItemId.SLAVES_ROBE_BOTTOM.id())) && (!p.getInventory().wielding(ItemId.SLAVES_ROBE_TOP.id()))) && (p.getQuestStage(this) != (-1))) {
                p.message("A guard notices you and starts running after you.");
                Npc npcN = Functions.getNearestNpc(p, NpcId.MERCENARY.id(), 10);
                if (npcN == null) {
                    npcN = Functions.spawnNpc(p.getWorld(), NpcId.MERCENARY.id(), p.getX(), p.getY(), 60000);
                    Functions.sleep(1000);
                }
                Functions.___npcTalk(p, npcN, "Hey! You're no slave!");
                npcN.startCombat(p);
                Functions.___message(p, "The Guards search you!");
                if (Functions.hasItem(p, ItemId.CELL_DOOR_KEY.id())) {
                    Functions.removeItem(p, ItemId.CELL_DOOR_KEY.id(), 1);
                }
                Functions.___message(p, "Some guards rush to help their comrade.", "You are roughed up a bit by the guards as you're manhandlded into a cell.");
                Functions.___npcTalk(p, npcN, "Into the cell you go! I hope this teaches you a lesson.");
                p.teleport(75, 3625);
                return;
            }
            Functions.___playerTalk(p, n, "Hello!");
            Functions.___npcTalk(p, n, "Hello there, I don't think I've seen you before.");
            int menu = Functions.___showMenu(p, n, "No, I'm new here!", "What's your name.");
            if (menu == 0) {
                Functions.___npcTalk(p, n, "I thought so you know!", "How do you like the hospitality down here?", "Not exactly Al Kharid Inn style is it?", "Well, I guess I'd better get back to work.", "Don't want to get into trouble with the guards again.");
                int ooo = Functions.___showMenu(p, n, "Do you get into trouble with guards often?", "I want to try and get you out of here.");
                if (ooo == 0) {
                    Functions.___npcTalk(p, n, "No, not really, because I'm usually working very hard.", "Come to think of it, I'd better get back to work.");
                    int often = Functions.___showMenu(p, n, "Do you enjoy it down here?", "Ok, see ya!");
                    if (often == 0) {
                        Functions.___npcTalk(p, n, "Of course not!", "I just don't have much choice about it a the moment.");
                        int enjoy = Functions.___showMenu(p, n, "I want to try and get you out of here.", "Do you have any ideas about how we can get out of here?");
                        if (enjoy == 0) {
                            anaDialogue(p, n, TouristTrap.Ana.TRYGETYOUOUTOFHERE);
                        } else
                            if (enjoy == 1) {
                                Functions.___npcTalk(p, n, "Hmmm, not really, I would have tried them already if I did.", "The guards seem to live in the compound.", "How did you get in there anyway?");
                                int mmm = Functions.___showMenu(p, n, "I managed to sneak past the guards.", "Huh, these guards are rubbish, it was easy to sneak past them!");
                                if (mmm == 0) {
                                    anaDialogue(p, n, TouristTrap.Ana.SNEAKEDPAST);
                                } else
                                    if (mmm == 1) {
                                        anaDialogue(p, n, TouristTrap.Ana.GUARDSRUBBISH);
                                    }

                            }

                    } else
                        if (often == 1) {
                            Functions.___npcTalk(p, n, "Goodbye and good luck!");
                        }

                } else
                    if (ooo == 1) {
                        anaDialogue(p, n, TouristTrap.Ana.TRYGETYOUOUTOFHERE);
                    }

            } else
                if (menu == 1) {
                    Functions.___npcTalk(p, n, "My name? Oh, how sweet, my name is Ana,", "I come from Al Kharid, thought the desert might be interesting.", "What a surprise I got!");
                    int opt = // do not send over
                    Functions.___showMenu(p, n, false, "What kind of suprise did you get?", "Do you want to go back to Al Kharid?");
                    if (opt == 0) {
                        Functions.___playerTalk(p, n, "What kind of surpise did you get?");
                        Functions.___npcTalk(p, n, "Well, I was just touring the desert looking for the nomad tribe to west.", "And I was set upon by these armoured men.", "I think that the guards think I am an escaped prisoner.", "They didn't understand that I was exploring the desert as an adventurer.");
                    } else
                        if (opt == 1) {
                            Functions.___playerTalk(p, n, "Do you want to go back to Al Kharid?");
                            Functions.___npcTalk(p, n, "Sure, I miss my Mum, her name is Irena and she is probably waiting for me.", "how do you propose we get out of here though?", "I'm sure you've noticed the many square jawed guards around here.", "You look like you can handle yourself, ", "but I have my doubts that you can take them all on!");
                        }

                }

        }
        switch (cID) {
            case TouristTrap.Ana.TRYGETYOUOUTOFHERE :
                Functions.___npcTalk(p, n, "Wow! You're brave. How do you propose we do that?", "In case you hadn't noticed, this place is quite well guarded.");
                int menu = Functions.___showMenu(p, n, "We could try to sneak out.", "Have you got any suggestions?");
                if (menu == 0) {
                    Functions.___npcTalk(p, n, "That doesn't sound very likely. How did you get in here anway?", "Did you deliberately hand yourself over to the guards?", "Ha, ha ha ha! Sorry, just kidding.");
                    int last = Functions.___showMenu(p, n, "I managed to sneak past the guards.", "Huh, these guards are rubbish, it was easy to sneak past them!");
                    if (last == 0) {
                        anaDialogue(p, n, TouristTrap.Ana.SNEAKEDPAST);
                    } else
                        if (last == 1) {
                            anaDialogue(p, n, TouristTrap.Ana.GUARDSRUBBISH);
                        }

                } else
                    if (menu == 1) {
                        Functions.___npcTalk(p, n, "Hmmm, let me think...", "Hmmm.", "No, sorry...", "The only thing that gets out of here is the rock that we mine.", "Not even the dead get a decent funeral.", "Bodies are just thrown down dissused mine holes.", "It's very disrespectful...");
                        int gah = Functions.___showMenu(p, n, "Ok, I'll check around for another way to try and get out.", "How does the rock get out?");
                        if (gah == 0) {
                            Functions.___npcTalk(p, n, "Good luck!");
                        } else
                            if (gah == 1) {
                                Functions.___npcTalk(p, n, "Well, in this section we mine it, ", "Then someone else scoops it into a barrel. ", "The barrels are loaded onto a mine cart.", "Then they're desposited near the surface lift.", "I have no idea where they go from there.", "But that's not going to help us, is it?");
                                int kaka = Functions.___showMenu(p, n, "Maybe? I'll come back to you when I have a plan.", "Where would I get one of those barrels from?");
                                if (kaka == 0) {
                                    Functions.___npcTalk(p, n, "Ok, well, I'm not going anywhere!");
                                    p.message("Ana nods at a nearby guard!");
                                    Functions.___npcTalk(p, n, "Unless he feels generous enough to let me go!");
                                    p.message("The guard ignores the comment.");
                                    Functions.___npcTalk(p, n, "Oh well, I'd better get back to work, you take care!");
                                } else
                                    if (kaka == 1) {
                                        Functions.___npcTalk(p, n, "Well, you would get one from around by the lift area.", "But why would you want one of those?");
                                        int tjatja = Functions.___showMenu(p, n, "Er no reason! Just wondering.", "You could hide in one of those barrels and I could try to sneak you out!");
                                        if (tjatja == 0) {
                                            Functions.___npcTalk(p, n, "Hmmm, just don't get any funny ideas...", "I am not going to get into one of those barrels!", "Ok, have you got that?");
                                            anaDialogue(p, n, TouristTrap.Ana.GOTTHAT);
                                        } else
                                            if (tjatja == 1) {
                                                Functions.___npcTalk(p, n, "There is no way that you are getting me into a barrel.", "No WAY! DO you understand?");
                                                anaDialogue(p, n, TouristTrap.Ana.GOTTHAT);
                                            }

                                    }

                            }

                    }

                break;
            case TouristTrap.Ana.GOTTHAT :
                int gotit = Functions.___showMenu(p, n, "Ok, yep, I've got that.", "Well, we'll see, it might be the only way.");
                if (gotit == 0) {
                    Functions.___npcTalk(p, n, "Good, just make sure you keep it in mind.", "Anyway, I have to get back to work.", "The guards will come along soon and give us some trouble else.");
                } else
                    if (gotit == 1) {
                        Functions.___npcTalk(p, n, "No, there has to be a better way!", "Anyway, I have to get back to work.", "The guards will come along soon and give us some trouble else.");
                    }

                break;
            case TouristTrap.Ana.SNEAKEDPAST :
                Functions.___npcTalk(p, n, "Hmm, impressive, but can you so easily sneak out again?", "How did you manage to get through the gate?");
                int gosh = // do not send over
                Functions.___showMenu(p, n, false, "I have a key", "It's a trade secret!");
                if (gosh == 0) {
                    Functions.___playerTalk(p, n, "I used a key.");
                    Npc guard = Functions.spawnNpc(p.getWorld(), NpcId.MERCENARY.id(), p.getX(), p.getY(), 60000);
                    if (guard != null) {
                        Functions.___npcTalk(p, guard, "I heard that! So you used a key did you?! ");
                        if (Functions.hasItem(p, ItemId.METAL_KEY.id())) {
                            Functions.___npcTalk(p, guard, "Right, we'll have that key off you!");
                            Functions.removeItem(p, ItemId.METAL_KEY.id(), 1);
                        }
                        Functions.___npcTalk(p, guard, "Guards! Guards!");
                        guard.startCombat(p);
                        Functions.___npcTalk(p, n, "Oopps! See ya!");
                        Functions.___message(p, "Some guards rush to help their comrade.", "You are roughed up a bit by the guards as you're manhandlded into a cell.");
                        Functions.___npcTalk(p, guard, "Into the cell you go! I hope this teaches you a lesson.");
                        p.teleport(75, 3625);
                    }
                } else
                    if (gosh == 1) {
                        Functions.___playerTalk(p, n, "It's a trade secret!");
                        Functions.___npcTalk(p, n, "Oh, right, well, I guess you know what you're doing.", "Anyway, I have to get back to work.", "The guards will come along soon and give us some trouble else.");
                    }

                break;
            case TouristTrap.Ana.GUARDSRUBBISH :
                Npc guard = Functions.spawnNpc(p.getWorld(), NpcId.MERCENARY.id(), p.getX(), p.getY(), 60000);
                if (guard != null) {
                    Functions.___npcTalk(p, guard, "I heard that! So you managed to sneak in did you!", "Guards! Guards!");
                    guard.startCombat(p);
                    Functions.___npcTalk(p, n, "Oopps! See ya!");
                    Functions.___message(p, "The Guards search you!", "Some guards rush to help their comrade.", "You are roughed up a bit by the guards as you're manhandlded into a cell.");
                    Functions.___npcTalk(p, guard, "Into the cell you go! I hope this teaches you a lesson.");
                    p.teleport(75, 3625);
                }
                break;
        }
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.IRENA.id()) {
                        irenaDialogue(p, n, -1);
                    } else
                        if (n.getID() == NpcId.MERCENARY.id()) {
                            mercenaryDialogue(p, n, -1);
                        } else
                            if (n.getID() == NpcId.MERCENARY_CAPTAIN.id()) {
                                mercenaryCaptainDialogue(p, n, -1);
                            } else
                                if (n.getID() == NpcId.MERCENARY_ESCAPEGATES.id()) {
                                    mercenaryInsideDialogue(p, n, -1);
                                } else
                                    if (n.getID() == NpcId.MINING_SLAVE.id()) {
                                        slaveDialogue(p, n, -1);
                                    } else
                                        if (n.getID() == NpcId.BEDABIN_NOMAD.id()) {
                                            bedabinNomadDialogue(p, n, -1);
                                        } else
                                            if (n.getID() == NpcId.BEDABIN_NOMAD_GUARD.id()) {
                                                switch (p.getQuestStage(quest)) {
                                                    case 8 :
                                                    case 9 :
                                                    case 10 :
                                                    case -1 :
                                                        Functions.___npcTalk(p, n, "Sorry, but you can't use the tent without permission.", "But thanks for your help to the Bedabin people.");
                                                        if (Functions.hasItem(p, ItemId.TECHNICAL_PLANS.id())) {
                                                            Functions.___npcTalk(p, n, "And we'll take those plans off your hands as well!");
                                                            Functions.removeItem(p, ItemId.TECHNICAL_PLANS.id(), 1);
                                                        }
                                                        break;
                                                    default :
                                                        Functions.___npcTalk(p, n, "Sorry, this is a private tent, no one is allowed in.", "Orders of Al Shabim...");
                                                        break;
                                                }
                                            } else
                                                if (n.getID() == NpcId.AL_SHABIM.id()) {
                                                    alShabimDialogue(p, n, -1);
                                                } else
                                                    if (n.getID() == NpcId.CAPTAIN_SIAD.id()) {
                                                        captainSiadDialogue(p, n, -1, null);
                                                    } else
                                                        if (n.getID() == NpcId.MERCENARY_LIFTPLATFORM.id()) {
                                                            if (p.getQuestStage(quest) == (-1)) {
                                                                Functions.___npcTalk(p, n, "Move along please, don't want any trouble today!");
                                                                return null;
                                                            }
                                                            Functions.___npcTalk(p, n, "Yes, what do you want?");
                                                            int menu = Functions.___showMenu(p, n, "Nothing thanks - sorry for disturbing you.", "Your head on a stick.");
                                                            if (menu == 0) {
                                                                Functions.___npcTalk(p, n, "Well...I guess that's Ok, get on your way though.");
                                                            } else
                                                                if (menu == 1) {
                                                                    Functions.___npcTalk(p, n, "Why you ungrateful whelp...I'll teach you some manners.");
                                                                    Functions.___message(p, "The guard shouts for help.");
                                                                    n.startCombat(p);
                                                                    Functions.___message(p, "Other guards start arriving.");
                                                                    Functions.___npcTalk(p, n, ("Get " + (p.isMale() ? "him" : "her")) + " men!");
                                                                    p.message("The guards rough you up a bit and then drag you to a cell.");
                                                                    p.teleport(76, 3625);
                                                                }

                                                        } else
                                                            if (n.getID() == NpcId.ANA.id()) {
                                                                anaDialogue(p, n, -1);
                                                            }










                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onIndirectTalkToNpc(Player p, final Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.AL_SHABIM.id()) {
                        if ((p.getQuestStage(quest) == 6) || (p.getQuestStage(quest) == 7)) {
                            alShabimDialogue(p, n, TouristTrap.AlShabim.HAVE_PLANS);
                        } else
                            if ((p.getQuestStage(quest) > 7) || (p.getQuestStage(quest) == (-1))) {
                                Functions.___message(p, "Al Shabim takes the technical plans off you.");
                                Functions.___npcTalk(p, n, "Thanks for the technical plans Effendi!", "We've been lost without them!");
                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return Functions.inArray(obj.getID(), TouristTrap.IRON_GATE, TouristTrap.ROCK_1, TouristTrap.WOODEN_DOORS, TouristTrap.DESK, TouristTrap.BOOKCASE, TouristTrap.CAPTAINS_CHEST) || ((obj.getID() == TouristTrap.STONE_GATE) && (p.getY() >= 735));
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == TouristTrap.STONE_GATE) && (p.getY() >= 735)) {
                        if (command.equals("go through")) {
                            if (!Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                p.message("you go through the gate");
                                p.teleport(62, 732);
                            } else {
                                if (p.getQuestStage(quest) == 9) {
                                    Functions.___message(p, "Ana looks out of the barrel...");
                                    Functions.___message(p, "@gre@Ana: Hey great, we're at the Shantay Pass!");
                                    Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                                    p.updateQuestStage(quest, 10);
                                    Npc Ana = Functions.spawnNpc(p.getWorld(), NpcId.ANA.id(), p.getX(), p.getY(), 60000);
                                    Ana.teleport(p.getX(), p.getY() + 1);
                                    if (Ana != null) {
                                        Functions.sleep(650);
                                        Functions.___npcTalk(p, Ana, "Great! Thanks for getting me out of that mine!", "And that barrel wasn't too bad anyway!", "Pop by again sometime, I'm sure we'll have a barrel of laughs!", "Oh! I nearly forgot, here's a key I found in the tunnels.", "It might be of some use to you, not sure what it opens.");
                                        Functions.addItem(p, ItemId.WROUGHT_IRON_KEY.id(), 1);
                                        Functions.___message(p, "Ana spots Irena and waves...");
                                        Functions.___npcTalk(p, Ana, "Hi Mum!", "Sorry, I have to go now!");
                                        Ana.remove();
                                    }
                                    Npc Irena = Functions.getNearestNpc(p, NpcId.IRENA.id(), 15);
                                    Functions.___npcTalk(p, Irena, "Hi Ana!");
                                    rewardMenu(p, Irena, true);
                                    p.getCache().remove("tried_ana_barrel");
                                } else // should not have an ana in barrel in other stages
                                {
                                    Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                                }
                            }
                        } else
                            if (command.equals("look")) {
                                Functions.___message(p, "You look at the huge Stone Gate.", "On the gate is a large poster, it reads.", "@gre@The Desert is a VERY Dangerous place...do not enter if you are scared of dying.", "@gre@Beware of high temperatures, sand storms, robbers, and slavers...", "@gre@No responsibility is taken by Shantay ", "@gre@If anything bad should happen to you in any circumstances whatsoever.", "Despite this warning lots of people seem to pass through the gate.");
                            }

                    } else
                        if (obj.getID() == TouristTrap.IRON_GATE) {
                            if (command.equals("open")) {
                                if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                    failEscapeAnaInBarrel(p, null);
                                    return null;
                                } else
                                    if (!Functions.hasItem(p, ItemId.METAL_KEY.id())) {
                                        p.message("This gate is locked, you'll need a key to open it.");
                                    } else {
                                        Functions.___message(p, "You use the metal key to unlock the gates.", "You manage to sneak past the guards!.");
                                        Functions.___doGate(p, obj);
                                        p.message("The gate swings open.");
                                        Functions.sleep(1000);
                                        p.message("The gates close behind you.");
                                        Npc n = Functions.getNearestNpc(p, NpcId.MERCENARY_ESCAPEGATES.id(), 15);
                                        if (n != null) {
                                            if (p.getQuestStage(quest) == (-1)) {
                                                // no dialogue after quest on just opening gates
                                                // todo change the coords going in and going out.
                                            } else {
                                                TouristTrap.Armed armedVal = playerArmed(p);
                                                if (armedVal != TouristTrap.Armed.NONE) {
                                                    switch (armedVal) {
                                                        case WEAPON :
                                                            Functions.___npcTalk(p, n, "Oi You with the weapon, what are you doing?");
                                                            break;
                                                        case ARMOUR :
                                                            Functions.___npcTalk(p, n, "Oi You with the armour on, what are you doing?");
                                                            break;
                                                        case BOTH :
                                                        default :
                                                            Functions.___npcTalk(p, n, "Oi You with the weapon and armour, what are you doing?");
                                                            break;
                                                    }
                                                    Functions.___npcTalk(p, n, "You don't belong in here!");
                                                    p.message("More guards come to arrest you.");
                                                    n.startCombat(p);
                                                    Functions.___npcTalk(p, n, "Right, you're going in the cell!");
                                                    Functions.___message(p, "You're outnumbered by all the guards.", "They man-handle you into a cell.");
                                                    p.teleport(89, 801);
                                                }
                                            }
                                        }
                                    }

                            } else
                                if (command.equals("search")) {
                                    Functions.___message(p, "You search the gate.", "Inside the compound you can see that there are lots of slaves mining away.", "They all seem to be dressed in dirty disgusting desert rags.", "And equiped only with a mining pick.", "Each slave is chained to a rock where they seemingly mine all day long.", "Guards patrol the area extensively.", "But you might be able to sneak past them if you try to blend in.");
                                }

                        } else
                            if (obj.getID() == TouristTrap.ROCK_1) {
                                p.message("You start climbing the rocky elevation.");
                                if (!succeedRate(p)) {
                                    p.message("You slip a little and tumble the rest of the way down the slope.");
                                    p.damage(7);
                                }
                                p.teleport(93, 799);
                            } else
                                if (obj.getID() == TouristTrap.WOODEN_DOORS) {
                                    if (command.equals("open")) {
                                        Functions.___message(p, "You push the door.");
                                        Functions.___playerTalk(p, null, "Ugh!");
                                        if (p.getInventory().wielding(ItemId.SLAVES_ROBE_BOTTOM.id()) && p.getInventory().wielding(ItemId.SLAVES_ROBE_TOP.id())) {
                                            Functions.___message(p, "The door opens with some effort ");
                                            if ((obj.getX() == 81) && (obj.getY() == 3633)) {
                                                p.teleport(82, 802);
                                                return null;
                                            }
                                            p.teleport(82, 3630);
                                            Functions.___message(p, "The huge doors open into a dark, dank and smelly tunnel.", "The associated smells of a hundred sweaty miners greets your nostrils.", "And your ears ring with the 'CLANG CLANG CLANG' as metal hits rock.");
                                        } else {
                                            Npc n = Functions.spawnNpc(p.getWorld(), NpcId.DRAFT_MERCENARY_GUARD.id(), p.getX(), p.getY(), 60000);
                                            Functions.sleep(1000);
                                            Functions.___npcTalk(p, n, "Oi You!");
                                            Functions.___message(p, "A guard notices you and approaches...");
                                            n.startCombat(p);
                                            Functions.___npcTalk(p, n, "Hey, you're no slave, where do you think you're going!");
                                            Functions.___npcTalk(p, n, "Guards, guards!");
                                            if (p.getQuestStage(quest) == (-1)) {
                                                p.message("No other guards come to the rescue.");
                                                return null;
                                            }
                                            Functions.___message(p, "The Guards search you!");
                                            if (Functions.hasItem(p, ItemId.METAL_KEY.id())) {
                                                p.message("The guards find the main gate key and remove it!");
                                                Functions.removeItem(p, ItemId.METAL_KEY.id(), 1);
                                            }
                                            Functions.___message(p, "More guards rush to catch you.", "You are roughed up a bit by the guards as you're manhandlded to a cell.");
                                            if (n != null) {
                                                Functions.___npcTalk(p, n, "Into the cell you go! I hope this teaches you a lesson.");
                                            }
                                            p.teleport(89, 801);
                                        }
                                    } else
                                        if (command.equals("watch")) {
                                            if ((obj.getX() == 81) && (obj.getY() == 3633)) {
                                                p.message("Nothing much seems to happen.");
                                            } else {
                                                Functions.___message(p, "You watch the doors for some time.", "You notice that only slaves seem to go down there.", "You might be able to sneak down if you pass as a slave.");
                                            }
                                        }

                                } else
                                    if (obj.getID() == TouristTrap.DESK) {
                                        Functions.___message(p, "You search the captains desk while he's not looking.");
                                        if ((Functions.hasItem(p, ItemId.CELL_DOOR_KEY.id()) && Functions.hasItem(p, ItemId.METAL_KEY.id())) && ((p.getQuestStage(quest) >= 0) && (p.getQuestStage(quest) <= 9) ? true : Functions.hasItem(p, ItemId.WROUGHT_IRON_KEY.id()))) {
                                            Functions.___message(p, "...but you find nothing of interest.");
                                            return null;
                                        }
                                        if (!Functions.hasItem(p, ItemId.CELL_DOOR_KEY.id())) {
                                            Functions.___message(p, "You find a cell door key.");
                                            Functions.addItem(p, ItemId.CELL_DOOR_KEY.id(), 1);
                                        }
                                        if (!Functions.hasItem(p, ItemId.METAL_KEY.id())) {
                                            Functions.___message(p, "You find a large metalic key.");
                                            Functions.addItem(p, ItemId.METAL_KEY.id(), 1);
                                        }
                                        // only after player has past to stage of wrought iron key
                                        if ((!((p.getQuestStage(quest) >= 0) && (p.getQuestStage(quest) <= 9))) && (!Functions.hasItem(p, ItemId.WROUGHT_IRON_KEY.id()))) {
                                            Functions.___message(p, "You find a large wrought iron key.");
                                            Functions.addItem(p, ItemId.WROUGHT_IRON_KEY.id(), 1);
                                        }
                                    } else
                                        if (obj.getID() == TouristTrap.BOOKCASE) {
                                            if (command.equals("search")) {
                                                p.message("You notice several books on the subject of Sailing.");
                                                if (!p.getCache().hasKey("sailing")) {
                                                    p.getCache().store("sailing", true);
                                                }
                                            } else
                                                if (command.equals("look")) {
                                                    p.message("The captain seems to collect lots of books!");
                                                }

                                        } else
                                            if (obj.getID() == TouristTrap.CAPTAINS_CHEST) {
                                                if (p.getCache().hasKey("tourist_chest") || (p.getQuestStage(quest) == (-1))) {
                                                    if (Functions.hasItem(p, ItemId.BEDOBIN_COPY_KEY.id())) {
                                                        if (!Functions.hasItem(p, ItemId.TECHNICAL_PLANS.id())) {
                                                            Functions.___message(p, "While the Captain's distracted, you quickly unlock the chest.", "You use the Bedobin Copy Key to open the chest.", "You open the chest and take out the plans.");
                                                            Functions.addItem(p, ItemId.TECHNICAL_PLANS.id(), 1);
                                                        } else {
                                                            p.message("The chest is empty.");
                                                        }
                                                        if (p.getCache().hasKey("sailing")) {
                                                            p.getCache().remove("sailing");
                                                        }
                                                        if (p.getCache().hasKey("tourist_chest")) {
                                                            p.getCache().remove("tourist_chest");
                                                        }
                                                    } else {
                                                        if (p.getCache().hasKey("sailing")) {
                                                            p.getCache().remove("sailing");
                                                        }
                                                        if (p.getCache().hasKey("tourist_chest")) {
                                                            p.getCache().remove("tourist_chest");
                                                        }
                                                        p.message("This chest needs a key!");
                                                    }
                                                } else {
                                                    Npc n = Functions.getNearestNpc(p, NpcId.CAPTAIN_SIAD.id(), 5);
                                                    if (n == null) {
                                                        n = Functions.spawnNpc(p.getWorld(), NpcId.CAPTAIN_SIAD.id(), p.getX(), p.getY(), 60000);
                                                        n.teleport(86, 1745);
                                                        Functions.sleep(1000);
                                                    }
                                                    captainSiadDialogue(p, n, -1, obj);
                                                }
                                            }






                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockNpcCommand(Npc n, String command, Player p) {
        return (n.getID() == NpcId.MERCENARY_CAPTAIN.id()) && command.equalsIgnoreCase("watch");
    }

    @Override
    public GameStateEvent onNpcCommand(Npc n, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((n.getID() == NpcId.MERCENARY_CAPTAIN.id()) && command.equalsIgnoreCase("watch")) {
                        Functions.___message(p, "You watch the Mercenary Captain for some time.", "He has a large metal key attached to his belt.", "You notice that he usually gets his men to do his dirty work.");
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return n.getID() == NpcId.MERCENARY_CAPTAIN.id();
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.MERCENARY_CAPTAIN.id()) {
                        p.message("You kill the captain!");
                        if ((p.getQuestStage(quest) == 1) && (!p.getCache().hasKey("first_kill_captn"))) {
                            p.getCache().store("first_kill_captn", true);
                        }
                        n.killedBy(p);
                        if (!Functions.hasItem(p, ItemId.METAL_KEY.id())) {
                            Functions.addItem(p, ItemId.METAL_KEY.id(), 1);
                            Functions.___message(p, "The mercenary captain drops a metal key on the floor.", "You quickly grab the key and add it to your inventory.");
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        return ((n.getID() == NpcId.MERCENARY.id()) || (n.getID() == NpcId.MERCENARY_ESCAPEGATES.id())) || ((n.getID() == NpcId.MERCENARY_CAPTAIN.id()) && (p.getInventory().countId(ItemId.METAL_KEY.id()) < 1));
    }

    @Override
    public GameStateEvent onPlayerAttackNpc(Player p, Npc affectedmob) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    tryToAttackMercenarys(p, affectedmob);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerRangeNpc(Player p, Npc n) {
        return ((n.getID() == NpcId.MERCENARY.id()) || (n.getID() == NpcId.MERCENARY_ESCAPEGATES.id())) || ((n.getID() == NpcId.MERCENARY_CAPTAIN.id()) && (p.getInventory().countId(ItemId.METAL_KEY.id()) < 1));
    }

    @Override
    public GameStateEvent onPlayerRangeNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    tryToAttackMercenarys(p, n);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerMageNpc(Player p, Npc n) {
        return ((n.getID() == NpcId.MERCENARY.id()) || (n.getID() == NpcId.MERCENARY_ESCAPEGATES.id())) || ((n.getID() == NpcId.MERCENARY_CAPTAIN.id()) && (p.getInventory().countId(ItemId.METAL_KEY.id()) < 1));
    }

    @Override
    public GameStateEvent onPlayerMageNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    tryToAttackMercenarys(p, n);
                    return null;
                });
            }
        };
    }

    private void tryToAttackMercenarys(Player p, Npc affectedmob) {
        if (((affectedmob.getID() == NpcId.MERCENARY_CAPTAIN.id()) || (affectedmob.getID() == NpcId.MERCENARY.id())) || (affectedmob.getID() == NpcId.MERCENARY_ESCAPEGATES.id())) {
            p.message("This guard looks fearsome and very aggressive.");
            p.message("Are you sure you want to attack him?");
            int menu = Functions.___showMenu(p, "Yes, I want to attack him.", "Nope, I've changed my mind.");
            if (menu == 0) {
                p.message("You decide to attack the guard.");
                Functions.___npcTalk(p, affectedmob, "Guards! Guards!");
                if (affectedmob.getID() == NpcId.MERCENARY_CAPTAIN.id()) {
                    affectedmob = Functions.getNearestNpc(p, NpcId.MERCENARY.id(), 10);
                }
                affectedmob.startCombat(p);
                if (affectedmob.getID() == NpcId.MERCENARY_ESCAPEGATES.id()) {
                    p.message("More guards rush to catch you.");
                    Functions.___message(p, "You are roughed up a bit by the guards as you're manhandlded to a cell.");
                    Functions.___npcTalk(p, affectedmob, "Into the cell you go! I hope this teaches you a lesson.");
                    p.teleport(89, 801);
                } else {
                    Functions.___npcTalk(p, affectedmob, "Guards, guards!");
                    Functions.___message(p, "Nearby guards quickly grab you and rough you up a bit.");
                    Functions.___npcTalk(p, affectedmob, "Let's see how good you are with desert survival techniques!");
                    Functions.___message(p, "You're bundled into the back of a cart and blindfolded...", "Sometime later you wake up in the desert.");
                    if (Functions.hasItem(p, ItemId.BOWL_OF_WATER.id())) {
                        Functions.___npcTalk(p, affectedmob, "You won't be needing that water any more!");
                        Functions.___message(p, "The guards throw your water away...");
                        Functions.removeItem(p, ItemId.BOWL_OF_WATER.id(), 1);
                    }
                    Functions.___message(p, "The guards move off in the cart leaving you stranded in the desert.");
                    p.teleport(121, 743);
                }
            } else
                if (menu == 1) {
                    p.message("You decide not to attack the guard.");
                }

        }
    }

    private void captainWantToThrowPlayer(Player p, Npc n) {
        n = Functions.getNearestNpc(p, NpcId.MERCENARY.id(), 10);
        if (n != null) {
            int punishment = DataConversions.random(0, 3);
            if (punishment == 0) {
                p.message("A guard approaches you and pretends to start hiting you.");
                Functions.___npcTalk(p, n, "Take that you infidel!");
                p.message("The guard leans closer to you and says in a low voice.");
                Functions.___npcTalk(p, n, "We're sick of having to kill every lunatic that comes along", "and insults the captain, it makes such a mess.", "Thankfully, he's a bit decrepid so he doesn't notice", "so please, buzz off and don't come here again.");
            } else
                if (punishment == 1) {
                    p.message("The guard approaches you again kicks you slightly.");
                    Functions.___playerTalk(p, n, "Ow!");
                    Functions.___npcTalk(p, n, "Take that you mad child of a dog!");
                    p.message("The guard leans closer to you and says in a low voice.");
                    Functions.___npcTalk(p, n, "What are you doing here again?", "Didn't I tell you to get out of here!", "Now get lost, properly this time!", "Or we may be forced to see his orders through properly.");
                } else
                    if (punishment == 2) {
                        p.message("A guard approaches you and looks very angry, he slaps you across the face.");
                        Functions.___npcTalk(p, n, "Prepare to die effendi!");
                        p.message("The guard leans close and whispers");
                        Functions.___npcTalk(p, n, "Are you mad effendi!", "This is your last chance.", "Leave now and never come back.", "Or I'll introduce you to my friend.");
                        p.message("The guard half draws his fearsome looking scimitar.");
                        Functions.___npcTalk(p, n, "And we'll be pleased to clean the mess up after you've been dispatched.");
                    } else {
                        p.message("An angry guard approaches you and whips out his sword.");
                        Functions.___npcTalk(p, n, "Ok, that does it!", "You're in serious trouble now!", ("Ok men, we need to teach this " + (p.isMale() ? "man" : "woman")) + " a thing or two", "about desert survival techniques.");
                        Functions.___message(p, "The guards grab you and beat you up.");
                        p.damage(7);
                        Functions.___message(p, "You're grabed and manhandled onto a cart.", "Sometime later you're dumped in the middle of the desert.", "The guards move off in the cart leaving you stranded in the desert.");
                        int random = DataConversions.getRandom().nextInt(2);
                        if (random == 0) {
                            p.teleport(102, 775);
                        } else
                            if (random == 1) {
                                p.teleport(135, 775);
                            }

                    }


        }
    }

    private void failEscapeAnaInBarrel(Player p, Npc n) {
        if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
            n = Functions.spawnNpc(p.getWorld(), NpcId.MERCENARY.id(), p.getX(), p.getY(), 60000);
            Functions.sleep(650);
            Functions.___npcTalk(p, n, "Hey, where d'ya think you're going with that barrel?", "You should know that they go out on the cart!", "We'd better check this out!");
            p.message("The guards prize the lid off the barrel.");
            Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
            Functions.___npcTalk(p, n, "Blimey! It's a jail break!", "They're making a break for it!");
            Npc ana = Functions.spawnNpc(p.getWorld(), NpcId.ANA.id(), p.getX(), p.getY(), 30000);
            Functions.sleep(650);
            Functions.___npcTalk(p, ana, "I could have told you we wouldn't get away with it!", "Now look at the mess you've caused!");
            p.message("The guards grab Ana and drag her away.");
            if (ana != null) {
                ana.remove();
            }
            Functions.___message(p, "@gre@Ana: Hey, watch it with the hands buster.", "@gre@Ana: These are the upper market slaves clothes doncha know!");
            Functions.___npcTalk(p, n, "Right, we'd better teach you a lesson as well!");
            Functions.___message(p, "The guards rough you up a bit.");
            Functions.___npcTalk(p, n, ("Right lads, stuff " + (p.isMale() ? "him" : "her")) + " in the mining cell!", "Specially for our most honoured guests.");
            p.message("The guards drag you away to a cell.");
            Functions.___message(p, "@yel@Guards: There you go, we hope you 'dig' you're stay here.", "@yel@Guards: Har! Har! Har!");
            if (n != null) {
                n.remove();
            }
            p.teleport(75, 3626);
        }
    }

    private void failWindowAnaInBarrel(Player p, Npc n) {
        if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
            Functions.___message(p, "You focus all of your strength on the bar. Your muscles ripple!", "You manage to bend the bars on the window .", "You'll never get Ana in the Barrel through the window.", "The barrel is just too big.");
            Functions.___message(p, "@gre@Ana: Don't think for one minute ...", "@gre@Ana: you're gonna get me through that window!");
        }
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
        return (((((((obj.getID() == TouristTrap.JAIL_DOOR) && (obj.getX() == 88)) && (obj.getY() == 801)) || (((obj.getID() == TouristTrap.WINDOW) && ((obj.getX() == 90) || (obj.getX() == 89))) && (obj.getY() == 802))) || (obj.getID() == TouristTrap.TENT_DOOR_1)) || (obj.getID() == TouristTrap.TENT_DOOR_2)) || (obj.getID() == TouristTrap.CAVE_JAIL_DOOR)) || (obj.getID() == TouristTrap.STURDY_IRON_GATE);
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((obj.getID() == TouristTrap.WINDOW) && ((obj.getX() == 90) || (obj.getX() == 89))) && (obj.getY() == 802)) {
                        Functions.___message(p, "You search the window.", "After some time you find that one of the bars looks weak,  ", "you may be able to bend one of the bars. ", "Would you like to try ?");
                        int menu = Functions.___showMenu(p, "Yes, I'll bend the bar.", "No, I'd better stay here.");
                        if (menu == 0) {
                            attemptBendBar(p);
                        } else
                            if (menu == 1) {
                                Functions.___message(p, "You decide to stay in the cell.", "Maybe they'll let you out soon?");
                            }

                    } else
                        if (((obj.getID() == TouristTrap.JAIL_DOOR) && (obj.getX() == 88)) && (obj.getY() == 801)) {
                            if (Functions.hasItem(p, ItemId.CELL_DOOR_KEY.id())) {
                                p.message("You unlock the door and walk through.");
                                Functions.doDoor(obj, p);
                            } else {
                                Functions.___message(p, "You need a key to unlock this door,", "And you don't seem to have one that fits.");
                            }
                        } else
                            if (obj.getID() == TouristTrap.TENT_DOOR_1) {
                                if (p.getY() <= 793) {
                                    p.teleport(171, 795);
                                } else {
                                    Npc n = Functions.getNearestNpc(p, NpcId.BEDABIN_NOMAD_GUARD.id(), 5);
                                    if (n == null) {
                                        n = Functions.spawnNpc(p.getWorld(), NpcId.BEDABIN_NOMAD_GUARD.id(), p.getX(), p.getY(), 60000);
                                        Functions.sleep(650);
                                    }
                                    n.teleport(170, 794);
                                    switch (p.getQuestStage(quest)) {
                                        case 8 :
                                        case 9 :
                                        case 10 :
                                        case -1 :
                                            Functions.___npcTalk(p, n, "Sorry, but you can't use the tent without permission.", "But thanks for your help to the Bedabin people.");
                                            if (Functions.hasItem(p, ItemId.TECHNICAL_PLANS.id())) {
                                                Functions.___npcTalk(p, n, "And we'll take those plans off your hands as well!");
                                                Functions.removeItem(p, ItemId.TECHNICAL_PLANS.id(), 1);
                                            }
                                            break;
                                        default :
                                            Functions.___npcTalk(p, n, "Sorry, this is a private tent, no one is allowed in.", "Orders of Al Shabim...");
                                            break;
                                    }
                                }
                            } else
                                if (obj.getID() == TouristTrap.TENT_DOOR_2) {
                                    /* if(p.getY() >= 805) {
                                    p.teleport(169, 804);
                                    } else {
                                    p.teleport(171, 806);
                                    }
                                     */
                                    Functions.doTentDoor(obj, p);
                                } else
                                    if (obj.getID() == TouristTrap.CAVE_JAIL_DOOR) {
                                        Npc n = Functions.getNearestNpc(p, NpcId.MERCENARY_JAILDOOR.id(), 5);
                                        if (n != null) {
                                            if (p.getX() >= 72) {
                                                if (!Functions.hasItem(p, ItemId.ROCKS.id(), 15)) {
                                                    Functions.___npcTalk(p, n, "Hey, move away from the gate.", "If you wanna get out, you're gonna have to mine for it.", "You're gonna have to bring me 15 loads of rocks - in one go!", "And then I'll let you out.", "You can go back and work with the other slaves then!");
                                                } else {
                                                    Functions.___playerTalk(p, n, "Hey, I have your rocks here, let me out.");
                                                    Functions.removeItem(p, ItemId.ROCKS.id(), 15);
                                                    Functions.___npcTalk(p, n, "Ok, ok, come on out.");
                                                    p.teleport(71, 3626);
                                                    p.message("The guard unlocks the gate and lets you out.");
                                                    p.teleport(69, 3625);
                                                }
                                            } else {
                                                Functions.___npcTalk(p, n, "Hey, move away from that gate!");
                                            }
                                        }
                                    } else
                                        if (obj.getID() == TouristTrap.STURDY_IRON_GATE) {
                                            if (p.getY() >= 3617) {
                                                if (Functions.hasItem(p, ItemId.WROUGHT_IRON_KEY.id())) {
                                                    p.message("You use the wrought iron key to unlock the gate.");
                                                    p.teleport(p.getX(), p.getY() - 1);
                                                } else {
                                                    Functions.___message(p, "You need a key to unlock this door,", "And you don't seem to have one that fits.");
                                                }
                                            } else {
                                                p.message("You push the gate open and walk through.");
                                                p.teleport(p.getX(), p.getY() + 1);
                                            }
                                        }





                    return null;
                });
            }
        };
    }

    private void attemptBendBar(Player p) {
        Functions.___message(p, "You focus all of your strength on the bar. Your muscles ripple!");
        if (p.getX() <= 89) {
            int attempt = DataConversions.random(0, 1);
            if (attempt == 0) {
                // fail-cell
                Functions.___message(p, "You find it hard to bend the bar, perhaps you should try again?");
                int stay = Functions.___showMenu(p, "Yes, I'll try to bend the bar again.", "No, I'm going to give up.");
                if (stay == 0) {
                    attemptBendBar(p);
                } else
                    if (stay == 1) {
                        Functions.___message(p, "You decide to stay in the cell.", "Maybe they'll let you out soon?");
                    }

            } else {
                // success-cell
                if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                    failWindowAnaInBarrel(p, null);
                } else {
                    Functions.___message(p, "You manage to bend the bar and climb out of the window.");
                    p.incExp(Skills.STRENGTH, 40, true);
                    p.teleport(90, 802);
                    p.message("You land near some rough rocks, which you may be able to climb.");
                }
            }
        } else {
            int attempt = DataConversions.random(0, 1);
            if (attempt == 0) {
                // fail-hill
                Functions.___message(p, "You find it hard to bend the bar, perhaps you should try again?");
                int stay = Functions.___showMenu(p, "Yes, I'll try to bend the bar again.", "No, I'm going to give up.");
                if (stay == 0) {
                    attemptBendBar(p);
                } else
                    if (stay == 1) {
                        Functions.___message(p, "You decide to stay in the cell.", "Maybe they'll let you out soon?");
                    }

            } else {
                // success-hill
                if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                    // from the hill outside the window (fail-safe)
                    failWindowAnaInBarrel(p, null);
                } else {
                    Functions.___message(p, "You manage to bend the bar !");
                    p.incExp(Skills.STRENGTH, 40, true);
                    p.teleport(89, 802);
                    p.message("You climb back inside the cell.");
                }
            }
        }
    }

    private boolean succeedRate(Player p) {
        int random = DataConversions.getRandom().nextInt(5);
        if ((random == 4) || (random == 3)) {
            return false;
        } else {
            return true;
        }
    }

    private TouristTrap.Armed playerArmed(Player p) {
        for (int robes : TouristTrap.allow) {
            allowed.add(robes);
        }
        for (int pos : TouristTrap.restricted) {
            wieldPos.add(pos);
        }
        boolean hasArmour = false;
        boolean hasWeapon = false;
        int wieldpos;
        if (p.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
            Item item;
            for (int i = 0; i < Equipment.slots; i++) {
                item = p.getEquipment().get(i);
                if (item == null)
                    continue;

                if ((item.getDef(p.getWorld()).getWieldPosition() > 5) && allowed.contains(item.getID()))
                    continue;

                if (wieldPos.contains(item.getDef(p.getWorld()).getWieldPosition())) {
                    if (item.getDef(p.getWorld()).getWieldPosition() == 3) {
                        if (item.getDef(p.getWorld()).getName().toLowerCase().contains("shield"))
                            hasArmour = true;
                        else
                            hasWeapon = true;

                    } else
                        if (item.getDef(p.getWorld()).getWieldPosition() == 4) {
                            hasWeapon = true;
                        } else {
                            hasArmour = true;
                        }

                }
            }
        } else {
            for (Item item : p.getInventory().getItems()) {
                if ((item.isWielded() && (item.getDef(p.getWorld()).getWieldPosition() > 5)) && allowed.contains(item.getID())) {
                    continue;
                }
                wieldpos = item.getDef(p.getWorld()).getWieldPosition();
                if (item.isWielded() && wieldPos.contains(wieldpos)) {
                    if (wieldpos == 3) {
                        if (item.getDef(p.getWorld()).getName().toLowerCase().contains("shield")) {
                            hasArmour = true;
                        } else {
                            hasWeapon = true;
                        }
                    } else
                        if (wieldpos == 4) {
                            hasWeapon = true;
                        } else {
                            hasArmour = true;
                        }

                }
            }
        }
        if (hasWeapon && hasArmour)
            return TouristTrap.Armed.BOTH;
        else
            if (hasWeapon)
                return TouristTrap.Armed.WEAPON;
            else
                if (hasArmour)
                    return TouristTrap.Armed.ARMOUR;
                else
                    return TouristTrap.Armed.NONE;



    }

    private void delayedReturnSlave(Player p, Npc n) {
        try {
            p.getWorld().getServer().getGameEventHandler().add(new SingleEvent(p.getWorld(), null, 30000, "Tourist Trap Delayed Return Slave", true) {
                @Override
                public void action() {
                    Functions.transform(n, NpcId.MINING_SLAVE.id(), true);
                }
            });
        } catch (Exception e) {
            TouristTrap.LOGGER.catching(e);
        }
    }

    @Override
    public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
        return (item.getID() == ItemId.PROTOTYPE_THROWING_DART.id()) && (npc.getID() == NpcId.AL_SHABIM.id());
    }

    @Override
    public GameStateEvent onInvUseOnNpc(Player player, Npc npc, Item item) {
        final QuestInterface quest = this;
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((npc.getID() == NpcId.AL_SHABIM.id()) && Functions.hasItem(player, ItemId.PROTOTYPE_THROWING_DART.id())) {
                        if (player.getQuestStage(quest) == 7) {
                            alShabimDialogue(player, npc, TouristTrap.AlShabim.MADE_WEAPON);
                        } else
                            if ((player.getQuestStage(quest) > 7) || (player.getQuestStage(quest) == (-1))) {
                                Functions.___npcTalk(player, npc, "Where did you get this from Effendi!", "I'll have to confiscate this for your own safety!");
                                Functions.removeItem(player, ItemId.PROTOTYPE_THROWING_DART.id(), 1);
                            }

                    }
                    return null;
                });
            }
        };
    }

    enum Armed {

        NONE,
        ARMOUR,
        WEAPON,
        BOTH;}

    class Irene {
        static final int WHENDIDSHEGO = 0;

        static final int WHATDIDSHEGO = 1;

        static final int REWARD = 2;

        static final int LOOKFORDAUGHTER = 3;

        static final int GETBACKDAUGHTER = 4;
    }

    class Mercenary {
        static final int THROW_PLAYER = 0;

        static final int PLACE_START = 1;

        static final int PLACE_SECOND = 2;

        static final int ORDER_KILL_PEOPLE = 3;

        static final int GUARDING_FIRST = 4;

        static final int GUARDING_SECOND = 5;

        static final int ANA_FIRST = 6;

        static final int ANA_SECOND = 7;

        static final int LEAVE_DESERT = 8;
    }

    class MercenaryCaptain {
        static final int GUARDING = 0;

        static final int DONTSCAREME = 1;

        static final int MUSTBESOMETHINGICANDO = 2;
    }

    class Slave {
        static final int NEWRECRUIT = 0;

        static final int UNDOTHEM = 1;

        static final int GIVEITAGO = 2;
    }

    class MercenaryInside {
        static final int PINEAPPLES = 0;

        static final int UNDERSTAND = 1;
    }

    class BedabinNomad {
        static final int JUGOFWATER = 0;

        static final int FULLWATERSKIN = 1;

        static final int BUCKETOFWATER = 2;

        static final int SHANTAYPASS = 3;

        static final int PLACE = 4;
    }

    class AlShabim {
        static final int WHATISTHISPLACE = 0;

        static final int HAVE_PLANS = 1;

        static final int MADE_WEAPON = 2;
    }

    class Siad {
        static final int PREPARETODIE = 0;

        static final int SLAVESBROKENFREE = 1;

        static final int FIREFIRE = 2;

        static final int TWOMINUTES = 3;

        static final int ERM = 4;

        static final int SERVICE = 5;

        static final int DRAGON = 6;

        static final int LONELY = 7;

        static final int PLANS = 8;

        static final int SUCCEED = 9;

        static final int BOOKS = 10;

        static final int PUNISHED = 11;
    }

    class Ana {
        static final int TRYGETYOUOUTOFHERE = 0;

        static final int GOTTHAT = 1;

        static final int SNEAKEDPAST = 2;

        static final int GUARDSRUBBISH = 3;
    }
}

