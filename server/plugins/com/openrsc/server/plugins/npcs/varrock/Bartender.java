package com.openrsc.server.plugins.npcs.varrock;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class Bartender implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.BARTENDER_VARROCK.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "What can I do yer for?");
                    String[] options = new String[]{  };
                    if (p.getCache().hasKey("barcrawl") && (!p.getCache().hasKey("bartwo"))) {
                        options = new String[]{ "A glass of your finest ale please", "Can you recommend anywhere an adventurer might make his fortune?", "Do you know where I can get some good equipment?", "I'm doing Alfred Grimhand's barcrawl" };
                    } else {
                        options = new String[]{ "A glass of your finest ale please", "Can you recommend anywhere an adventurer might make his fortune?", "Do you know where I can get some good equipment?" };
                    }
                    int reply = Functions.___showMenu(p, n, options);
                    if (reply == 0) {
                        Functions.___npcTalk(p, n, "No problemo", "That'll be 2 coins");
                        if (Functions.hasItem(p, ItemId.COINS.id(), 2)) {
                            p.message("You buy a pint of beer");
                            Functions.addItem(p, ItemId.BEER.id(), 1);
                            p.getInventory().remove(ItemId.COINS.id(), 2);
                        } else
                            Functions.___playerTalk(p, n, "Oh dear. I don't seem to have enough money");

                    } else
                        if (reply == 1) {
                            Functions.___npcTalk(p, n, "Ooh I don't know if I should be giving away information", "Makes the computer game too easy");
                            reply = // do not send over
                            Functions.___showMenu(p, n, false, "Oh ah well", "Computer game? What are you talking about?", "Just a small clue?");
                            if (reply == 0) {
                                Functions.___playerTalk(p, n, "Oh ah well");
                            } else
                                if (reply == 1) {
                                    Functions.___playerTalk(p, n, "Computer game?", "What are you talking about?");
                                    Functions.___npcTalk(p, n, "This world around us..", "is all a computer game..", "called Runescape");
                                    Functions.___playerTalk(p, n, "Nope, still don't understand what you are talking about", "What's a computer?");
                                    Functions.___npcTalk(p, n, "It's a sort of magic box thing,", "which can do all sorts of different things");
                                    Functions.___playerTalk(p, n, "I give up", "You're obviously completely mad!");
                                } else
                                    if (reply == 2) {
                                        Functions.___playerTalk(p, n, "Just a small clue?");
                                        Functions.___npcTalk(p, n, "Go and talk to the bartender at the Jolly Boar Inn", "He doesn't seem to mind giving away clues");
                                    }


                        } else
                            if (reply == 2) {
                                Functions.___npcTalk(p, n, "Well, there's the sword shop across the road,", "or there's also all sorts of shops up around the market");
                            } else
                                if (reply == 3) {
                                    Functions.___npcTalk(p, n, "Oh no not another of you guys", "These barbarian barcrawls cause too much damage to my bar", "You're going to have to pay 50 gold for the Uncle Humphrey's gutrot");
                                    if (Functions.hasItem(p, ItemId.COINS.id(), 50)) {
                                        p.getInventory().remove(ItemId.COINS.id(), 50);
                                        p.message("You buy some gutrot");
                                        Functions.sleep(800);
                                        p.message("You drink the gutrot");
                                        Functions.sleep(800);
                                        p.message("your insides feel terrible");
                                        drinkAle(p);
                                        p.damage(DataConversions.random(1, 3));
                                        Functions.sleep(800);
                                        p.message("The bartender signs your card");
                                        p.getCache().store("bartwo", true);
                                        Functions.___playerTalk(p, n, "Blearrgh");
                                    } else
                                        Functions.___playerTalk(p, n, "I don't have 50 coins");

                                }



                    return null;
                });
            }
        };
    }

    private void drinkAle(Player p) {
        int[] skillIDs = new int[]{ Skills.ATTACK, Skills.DEFENSE, Skills.STRENGTH, Skills.SMITHING };
        for (int i = 0; i < skillIDs.length; i++) {
            setAleEffect(p, skillIDs[i]);
        }
    }

    private void setAleEffect(Player p, int skillId) {
        int reduction;
        int currentStat;
        int maxStat;
        maxStat = p.getSkills().getMaxStat(skillId);
        // estimated
        reduction = (maxStat < 15) ? 5 : maxStat < 40 ? 6 : maxStat < 70 ? 7 : maxStat < 85 ? 8 : 9;
        currentStat = p.getSkills().getLevel(skillId);
        if (currentStat <= 9) {
            p.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
        } else {
            p.getSkills().setLevel(skillId, currentStat - reduction);
        }
    }
}

