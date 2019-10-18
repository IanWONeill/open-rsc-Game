package com.openrsc.server.plugins.quests.members.legendsquest.npcs;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class LegendsQuestSirRadimusErkle implements QuestInterface , InvUseOnNpcListener , TalkToNpcListener , InvUseOnNpcExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public int getQuestId() {
        return Quests.LEGENDS_QUEST;
    }

    @Override
    public String getQuestName() {
        return "Legend's Quest (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        p.message("@gre@Well done - you have completed the Legends Guild Quest!");
        Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.LEGENDS_QUEST), true);
        Functions.___message(p, "@gre@You haved gained 4 quest points!");
        /**
         * REMOVE QUEST CACHES *
         */
        String[] caches = new String[]{ "gujuo_potion", "JUNGLE_EAST", "JUNGLE_MIDDLE", "JUNGLE_WEST", "already_cast_holy_spell", "ran_from_2nd_nezi", "legends_choose_reward", "legends_reward_claimed", "ancient_wall_runes", "gave_glowing_dagger", "met_spirit", "cavernous_opening", "viyeldi_companions", "killed_viyeldi", "legends_wooden_beam", "rewarded_totem", "holy_water_neiz", "crafted_totem_pole" };
        for (String s : caches) {
            if (p.getCache().hasKey(s)) {
                p.getCache().remove(s);
            }
        }
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) || (n.getID() == NpcId.SIR_RADIMUS_ERKLE_GUILD.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) {
                        radimusInHouseDialogue(p, n, -1);
                    } else
                        if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_GUILD.id()) {
                            radimusInGuildDialogue(p, n, -1);
                        }

                    return null;
                });
            }
        };
    }

    private void radimusInGuildDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_GUILD.id()) {
            if (cID == (-1)) {
                switch (p.getQuestStage(Quests.LEGENDS_QUEST)) {
                    case 11 :
                        if (!p.getCache().hasKey("legends_choose_reward")) {
                            Functions.___npcTalk(p, n, "Welcome to the Legends Guild Main Hall.", "We have placed your Totem Pole as pride of place.", "All members of the Legends Guild will see it as they walk in.", "They will know that you were the person to bring it back.", "Congratulations, you're now a fully fledged member.", "I would like to to offer you some training.", "Which will increase your experience and abilities ", "In four areas.", "Would you like to train now?");
                            p.getCache().store("legends_choose_reward", true);
                        } else {
                            Functions.___npcTalk(p, n, "Hello again...", "Would you like to continue with your training?");
                        }
                        int menu = // do not send over
                        Functions.___showMenu(p, n, false, "Yes, I'll train now.", "No, I've got something else to do at the moment.");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, ("You can choose " + getRewardClaimCount(p)) + " areas to increase your abilities in.");
                            radimusInGuildDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_ONE);
                        } else
                            if (menu == 1) {
                                Functions.___playerTalk(p, n, "No, I've got something else to do at the moment.");
                                Functions.___npcTalk(p, n, ("Very well young " + (p.isMale() ? "man" : "lady")) + ".", "Return when you are able, but don't leave it too long.", "You'll benefit alot from this training.", "Now, do excuse me while, I have other things to attend to.", "Do feel free to explore the rest of the building.");
                            }

                        break;
                    case -1 :
                        Functions.___npcTalk(p, n, "Hello there! How are you enjoying the Legends Guild?");
                        Functions.___message(p, n, 1300, "Radimus looks busy...");
                        Functions.___npcTalk(p, n, "Excuse me a moment won't you.", "Do feel free to explore the rest of the building.");
                        break;
                }
            }
            switch (cID) {
                case LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_ONE :
                    int menu_one = Functions.___showMenu(p, "* Attack *", "* Defense * ", "* Strength * ", "--- Go to Skill Menu 2 ----");
                    if (menu_one == 0) {
                        skillReward(p, n, Skills.ATTACK);
                    } else
                        if (menu_one == 1) {
                            skillReward(p, n, Skills.DEFENSE);
                        } else
                            if (menu_one == 2) {
                                skillReward(p, n, Skills.STRENGTH);
                            } else
                                if (menu_one == 3) {
                                    radimusInGuildDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_TWO);
                                }



                    break;
                case LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_TWO :
                    int menu_two = Functions.___showMenu(p, "* Hits * ", "* Prayer * ", "* Magic *", "--- Go to Skill Menu 3  ----");
                    if (menu_two == 0) {
                        skillReward(p, n, Skills.HITS);
                    } else
                        if (menu_two == 1) {
                            skillReward(p, n, Skills.PRAYER);
                        } else
                            if (menu_two == 2) {
                                skillReward(p, n, Skills.MAGIC);
                            } else
                                if (menu_two == 3) {
                                    radimusInGuildDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_THREE);
                                }



                    break;
                case LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_THREE :
                    int menu_three = Functions.___showMenu(p, "* Woodcutting * ", "* Crafting * ", "* Smithing * ", "--- Go to Skill Menu 4 ----");
                    if (menu_three == 0) {
                        skillReward(p, n, Skills.WOODCUT);
                    } else
                        if (menu_three == 1) {
                            skillReward(p, n, Skills.CRAFTING);
                        } else
                            if (menu_three == 2) {
                                skillReward(p, n, Skills.SMITHING);
                            } else
                                if (menu_three == 3) {
                                    radimusInGuildDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_FOUR);
                                }



                    break;
                case LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_FOUR :
                    int menu_four = Functions.___showMenu(p, "* Herblaw *", "* Agility *", "* Thieving *", "--- Go to Skill Menu 1 ----");
                    if (menu_four == 0) {
                        skillReward(p, n, Skills.HERBLAW);
                    } else
                        if (menu_four == 1) {
                            skillReward(p, n, Skills.AGILITY);
                        } else
                            if (menu_four == 2) {
                                skillReward(p, n, Skills.THIEVING);
                            } else
                                if (menu_four == 3) {
                                    radimusInGuildDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_ONE);
                                }



                    break;
            }
        }
    }

    private int getRewardClaimCount(Player p) {
        int rewardCount = 4;
        if (p.getCache().hasKey("legends_reward_claimed")) {
            rewardCount = p.getCache().getInt("legends_reward_claimed");
        }
        return rewardCount;
    }

    private void updateRewardClaimCount(Player p) {
        if (!p.getCache().hasKey("legends_reward_claimed")) {
            p.getCache().set("legends_reward_claimed", 3);
        } else {
            int leftToClaim = p.getCache().getInt("legends_reward_claimed");
            p.getCache().set("legends_reward_claimed", leftToClaim - 1);
        }
    }

    private void skillReward(Player p, Npc n, int skill) {
        int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.LEGENDS_QUEST);
        questData[Quests.MAPIDX_SKILL] = skill;
        Functions.incQuestReward(p, questData, false);
        updateRewardClaimCount(p);
        p.message(("You receive some training and increase experience to your " + p.getWorld().getServer().getConstants().getSkills().getSkillName(skill)) + ".");
        if (getRewardClaimCount(p) == 0) {
            Functions.___npcTalk(p, n, "Right, that's all the training I can offer.! ", "Hope you're happy with your new skills.", "Excuse me now won't you ?", "Do feel free to explore the rest of the building.");
            p.sendQuestComplete(Quests.LEGENDS_QUEST);
        } else {
            Functions.___npcTalk(p, n, ("You can choose " + getRewardClaimCount(p)) + " areas to increase your abilities in.");
            radimusInGuildDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInGuild.SKILL_MENU_ONE);
        }
    }

    private void radimusInHouseDialogue(Player p, Npc n, int cID) {
        if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) {
            if (cID == (-1)) {
                switch (p.getQuestStage(Quests.LEGENDS_QUEST)) {
                    case 0 :
                        Functions.___npcTalk(p, n, ("Good day to you " + (p.isMale() ? "Sir" : "my Lady")) + " !", "No doubt you are keen to become a member of the Legends Guild ?");
                        int menu = // do not send over
                        Functions.___showMenu(p, n, false, "Yes actually, what's involved?", "Maybe some other time.", "Who are you?");
                        if (menu == 0) {
                            /* START LEGENDS QUEST */
                            Functions.___playerTalk(p, n, "Yes actually, what's involved ?");
                            radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.WHATS_INVOLVED);
                        } else
                            if (menu == 1) {
                                Functions.___playerTalk(p, n, "Maybe some other time.");
                                radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.MAYBE_SOME_OTHER_TIME);
                            } else
                                if (menu == 2) {
                                    Functions.___playerTalk(p, n, "Who are you?");
                                    radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.WHO_ARE_YOU);
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
                    case 9 :
                    case 10 :
                        Functions.___npcTalk(p, n, "Hello there, how is the quest going?");
                        if (Functions.hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || Functions.hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
                            radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_HAS_SCROLLS);
                        } else {
                            radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_NO_SCROLLS);
                        }
                        break;
                    case 11 :
                        Functions.___npcTalk(p, n, "Hello again, go through to the main Legends Guild Hall,", "I'll meet you in there. ", "And we can discuss your reward !");
                        break;
                    case -1 :
                        Functions.___npcTalk(p, n, "Hello there! How are you enjoying the Legends Guild?");
                        Functions.___message(p, n, 1300, "Radimus looks busy...");
                        Functions.___npcTalk(p, n, "Excuse me a moment won't you.", "Do feel free to explore the rest of the building.");
                        break;
                }
            }
            switch (cID) {
                case LegendsQuestSirRadimusErkle.RadimusInHouse.WHATS_INVOLVED :
                    Functions.___npcTalk(p, n, "Well, you need to complete a quest for us.", "You need to map an area called the Kharazi Jungle", "It is the unexplored southern part of Karamja Island.", "You also need to befriend a native from the Kharazi tribe", "in order to get a gift or token of friendship.", "We want to display it in the Legends Guild Main hall.", "Are you interested in this quest?");
                    int questMenu = Functions.___showMenu(p, n, "Yes, it sounds great!", "Not just at the moment.");
                    if (questMenu == 0) {
                        /* START LEGENDS QUEST */
                        Functions.___npcTalk(p, n, "Excellent!", "Ok, you'll need this starting map of the Kharazi Jungle.");
                        p.message("Grand Vizier Erkle gives you some notes and a map.");
                        Functions.addItem(p, ItemId.RADIMUS_SCROLLS.id(), 1);
                        Functions.___npcTalk(p, n, "Complete this map when you get to the Kharazi Jungle.", "It's towards the southern most part of Karamja.", "You'll need additional papyrus and charcoal to complete the map.", "There are three different sectors of the Kharazi jungle to map.");
                        Functions.___message(p, 1200, "Radimus shuffles around the back of his desk.");
                        Functions.___npcTalk(p, n, "It is likely to be very tough going.", "You'll need an axe and a machette to cut through ", "the dense Kharazi jungle,collect a machette from the ", "cupboard before you leave. Bring back some sort of token ", "which we can display in the Guild.", "And very good luck to you !");
                        p.updateQuestStage(Quests.LEGENDS_QUEST, 1);
                    } else
                        if (questMenu == 1) {
                            Functions.___npcTalk(p, n, "Very well, if you change your mind, please come back and see me.");
                        }

                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.MAYBE_SOME_OTHER_TIME :
                    Functions.___npcTalk(p, n, "Ok, as you wish...");
                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.WHO_ARE_YOU :
                    Functions.___npcTalk(p, n, "My name is Radimus Erkle, I am the Grand Vizier of the Legends Guild.", "Are you interested in becoming a member?");
                    int opt = // do not send over
                    Functions.___showMenu(p, n, false, "Yes actually, what's involved?", "Maybe some other time.");
                    if (opt == 0) {
                        Functions.___playerTalk(p, n, "Yes actually, what's involved ?");
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.WHATS_INVOLVED);
                    } else
                        if (opt == 1) {
                            Functions.___playerTalk(p, n, "Maybe some other time.");
                            radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.MAYBE_SOME_OTHER_TIME);
                        }

                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_HAS_SCROLLS :
                    int option = Functions.___showMenu(p, n, "It's Ok, but I have forgotten what to do.", "I need another machete.", "I've run out of Charcoal.", "I've run out of Papyrus.", "I've completed the quest.");
                    if (option == 0) {
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.FORGOTTEN_WHAT_TO_DO);
                    } else
                        if (option == 1) {
                            radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.ANOTHER_MACHETE);
                        } else
                            if (option == 2) {
                                radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.CHARCOAL);
                            } else
                                if (option == 3) {
                                    radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.PAPYRUS);
                                } else
                                    if (option == 4) {
                                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.IVE_COMPLETED_QUEST);
                                    }




                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_NO_SCROLLS :
                    int myMenu = // do not send over
                    Functions.___showMenu(p, n, false, "Terrible, I lost my map of the Kharazi Jungle.", "It's Ok, but I have forgotten what to do.", "Great, but I need another machete.", "I've run out of Charcoal.", "I've run out of Papyrus.");
                    if (myMenu == 0) {
                        Functions.___playerTalk(p, n, "Terrible, I lost my map of the Kharazi Jungle.");
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.LOST_KHARAZI_JUNGLE_MAP);
                    } else
                        if (myMenu == 1) {
                            Functions.___playerTalk(p, n, "It's Ok, but I have forgotten what to do.");
                            radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.FORGOTTEN_WHAT_TO_DO);
                        } else
                            if (myMenu == 2) {
                                Functions.___playerTalk(p, n, "I need another machete.");
                                radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.ANOTHER_MACHETE);
                            } else
                                if (myMenu == 3) {
                                    Functions.___playerTalk(p, n, "I've run out of Charcoal.");
                                    radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.CHARCOAL);
                                } else
                                    if (myMenu == 4) {
                                        Functions.___playerTalk(p, n, "I've run out of Papyrus.");
                                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.PAPYRUS);
                                    }




                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.FORGOTTEN_WHAT_TO_DO :
                    Functions.___npcTalk(p, n, "Tut! How forgetful!", "You need to find a way into the Kharazi jungle, ", "Then you need to explore and map that entire area.", "While you're there, you need to make contact with any jungle natives.", "Bring back a tribal gift from the natives", "so that we can display it in the Legends Guild.", "I hope that answers your question!");
                    if (Functions.hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || Functions.hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_HAS_SCROLLS);
                    } else {
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_NO_SCROLLS);
                    }
                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.ANOTHER_MACHETE :
                    Functions.___npcTalk(p, n, "Well, just get another one from the cupboard.");
                    if (Functions.hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || Functions.hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_HAS_SCROLLS);
                    } else {
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_NO_SCROLLS);
                    }
                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.CHARCOAL :
                    Functions.___npcTalk(p, n, "Well, get some more!", "Be proactive and get some more from somewhere.");
                    Functions.___message(p, 1200, "Sir Radimus mutters under his breath.");
                    Functions.___npcTalk(p, n, "It's hardly legendary if you fail a quest", "because you can't find some charcoal!");
                    if (Functions.hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || Functions.hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_HAS_SCROLLS);
                    } else {
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_NO_SCROLLS);
                    }
                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.PAPYRUS :
                    Functions.___npcTalk(p, n, "Well, get some more!", "Be proactive and try to find some!");
                    Functions.___message(p, 1200, "Sir Radimus mutters under his breath.");
                    Functions.___npcTalk(p, n, "It's hardly legendary if you fail a quest", "because you can't find some papyrus!");
                    if (Functions.hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || Functions.hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_HAS_SCROLLS);
                    } else {
                        radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_NO_SCROLLS);
                    }
                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.IVE_COMPLETED_QUEST :
                    Functions.___npcTalk(p, n, "Well, if you have, show me the gift the Kharazi people gave you !", "Becoming a legend is more than just fighting you know.", "It also requires some carefull diplomacy and problem solving.", "Also complete the map of Kharazi jungle", "and we will admit you to the Guild.");
                    radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_HAS_SCROLLS);
                    break;
                case LegendsQuestSirRadimusErkle.RadimusInHouse.LOST_KHARAZI_JUNGLE_MAP :
                    Functions.___npcTalk(p, n, "That is awful, well, luckily I have a copy here.", "But I need to charge you a copy fee of 30 gold pieces.");
                    if (Functions.hasItem(p, ItemId.COINS.id(), 30)) {
                        Functions.___npcTalk(p, n, "Do you agree to pay?");
                        int pay = Functions.___showMenu(p, n, "Yes, I'll pay for it.", "No, I won't pay for it.");
                        if (pay == 0) {
                            p.message("You hand over 30 gold coins.");
                            Functions.removeItem(p, ItemId.COINS.id(), 30);
                            Functions.addItem(p, ItemId.RADIMUS_SCROLLS.id(), 1);
                            Functions.___npcTalk(p, n, "Ok, please don't lose this one..");
                        } else
                            if (pay == 1) {
                                Functions.___npcTalk(p, n, "Well, that's your decision, of course... ", "but you won't be able to complete the quest without it.", "Excuse, me now won't you, I have other business to attend to.");
                                radimusInHouseDialogue(p, n, LegendsQuestSirRadimusErkle.RadimusInHouse.SAME_MENU_NO_SCROLLS);
                            }

                    } else {
                        Functions.___npcTalk(p, n, "It looks as if you don't have the funds for it at the moment.", "How irritating...");
                    }
                    break;
            }
        }
    }

    @Override
    public boolean blockInvUseOnNpc(Player p, Npc n, Item item) {
        return (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) && (((item.getID() == ItemId.RADIMUS_SCROLLS_COMPLETE.id()) || (item.getID() == ItemId.GILDED_TOTEM_POLE.id())) || (item.getID() == ItemId.TOTEM_POLE.id()));
    }

    @Override
    public GameStateEvent onInvUseOnNpc(Player p, Npc n, Item item) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) && (item.getID() == ItemId.GILDED_TOTEM_POLE.id())) {
                        if (p.getQuestStage(Quests.LEGENDS_QUEST) == 11) {
                            Functions.___npcTalk(p, n, "Go through to the main Legends Guild and I will join you.");
                            return null;
                        }
                        if (p.getQuestStage(Quests.LEGENDS_QUEST) == 10) {
                            Functions.___npcTalk(p, n, (p.isMale() ? "Sir" : "Madam") + ", this is truly amazing...");
                            if (!Functions.hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
                                Functions.___npcTalk(p, n, "However, I need you to complete the map of the ,", "Kharazi Jungle before your quest is complete.");
                            } else {
                                Functions.___message(p, n, 1300, "Radimus Erkle orders some guards to take the totem pole,", "into the main Legends Hall.");
                                Functions.removeItem(p, item.getID(), 1);
                                Functions.___npcTalk(p, n, "That will take pride of place in the Legends Guild ", "As a reminder of your quest to gain entry.", "And so that many other great adventurers can admire your bravery.", "Well, it seems that you have completed the tasks I set you.", "That map of the Kharazi jungle will be very helpful in future.", "Congratulations, welcome to the Legends Guild.", "Go through to the main Legends Guild building ", "and I will join you shortly.");
                                p.updateQuestStage(Quests.LEGENDS_QUEST, 11);
                            }
                        } else {
                            p.message("You have not completed this quest - submitting bug abuse.");
                        }
                    } else
                        if ((n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) && (item.getID() == ItemId.TOTEM_POLE.id())) {
                            Functions.___npcTalk(p, n, "Hmmm, well, it is very impressive.", "Especially since it looks very heavy...", "However, it lacks a certain authenticity,", "my guess is that you made it.", "But I'm not sure why.", "We would like to have a really nice display object", "to put on display in the Legends Guild main hall.", "Do you think you could get something more authentic ?");
                        } else
                            if ((n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) && (item.getID() == ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
                                Functions.___npcTalk(p, n, ("Well done " + (p.isMale() ? "Sir" : "Madam")) + ", very well done...", "However, you'll probably need it while you search", "for natives of the Kharazi tribe in the Kharazi jungle.", "Remember, we want a very special token of friendship from them.", "To place in the Legends Guild.", "I'll take the map off your hands once we get the ", "proof that you have met the natives.");
                            }


                    return null;
                });
            }
        };
    }

    class RadimusInHouse {
        static final int WHATS_INVOLVED = 0;

        static final int MAYBE_SOME_OTHER_TIME = 1;

        static final int WHO_ARE_YOU = 2;

        static final int SAME_MENU_HAS_SCROLLS = 3;

        static final int FORGOTTEN_WHAT_TO_DO = 4;

        static final int ANOTHER_MACHETE = 5;

        static final int CHARCOAL = 6;

        static final int PAPYRUS = 7;

        static final int IVE_COMPLETED_QUEST = 8;

        static final int SAME_MENU_NO_SCROLLS = 9;

        static final int LOST_KHARAZI_JUNGLE_MAP = 10;
    }

    class RadimusInGuild {
        static final int SKILL_MENU_ONE = 0;

        static final int SKILL_MENU_TWO = 1;

        static final int SKILL_MENU_THREE = 2;

        static final int SKILL_MENU_FOUR = 3;
    }
}

