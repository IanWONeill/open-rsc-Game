package com.openrsc.server.plugins.npcs.brimhaven;


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
import java.util.concurrent.Callable;


public final class BrimHavenBartender implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.BARTENDER_BRIMHAVEN.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(p, n, "Yohoho me hearty what would you like to drink?");
                    String[] options;
                    if (p.getCache().hasKey("barcrawl") && (!p.getCache().hasKey("barfour"))) {
                        options = new String[]{ "Nothing thankyou", "A pint of Grog please", "A bottle of rum please", "I'm doing Alfred Grimhand's barcrawl" };
                    } else {
                        options = new String[]{ "Nothing thankyou", "A pint of Grog please", "A bottle of rum please" };
                    }
                    int firstMenu = Functions.___showMenu(p, n, options);
                    if (firstMenu == 0) {
                        // NOTHING
                    } else
                        if (firstMenu == 1) {
                            Functions.___npcTalk(p, n, "One grog coming right up", "That'll be 3 gold");
                            if (Functions.hasItem(p, ItemId.COINS.id(), 3)) {
                                p.message("You buy a pint of Grog");
                                p.getInventory().remove(ItemId.COINS.id(), 3);
                                Functions.addItem(p, ItemId.GROG.id(), 1);
                            } else {
                                Functions.___playerTalk(p, n, "Oh dear. I don't seem to have enough money");
                            }
                        } else
                            if (firstMenu == 2) {
                                Functions.___npcTalk(p, n, "That'll be 27 gold");
                                if (Functions.hasItem(p, ItemId.COINS.id(), 27)) {
                                    p.message("You buy a bottle of rum");
                                    p.getInventory().remove(ItemId.COINS.id(), 27);
                                    Functions.addItem(p, ItemId.KARAMJA_RUM.id(), 1);
                                } else {
                                    Functions.___playerTalk(p, n, "Oh dear. I don't seem to have enough money");
                                }
                            } else
                                if (firstMenu == 3) {
                                    Functions.___npcTalk(p, n, "Haha time to be breaking out the old supergrog", "That'll be 15 coins please");
                                    if (Functions.hasItem(p, ItemId.COINS.id(), 15)) {
                                        p.getInventory().remove(ItemId.COINS.id(), 15);
                                        Functions.___message(p, "The bartender serves you a glass of strange thick dark liquid", "You wince and drink it", "You stagger backwards");
                                        drinkAle(p);
                                        Functions.___message(p, "You think you see 2 bartenders signing 2 barcrawl cards");
                                        p.getCache().store("barfour", true);
                                    } else {
                                        Functions.___playerTalk(p, n, "I don't have 15 coins right now");
                                    }
                                }



                    return null;
                });
            }
        };
    }

    private void drinkAle(Player p) {
        int[] skillIDs = new int[]{ Skills.ATTACK, Skills.DEFENSE, Skills.PRAYER, Skills.COOKING, Skills.HERBLAW };
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
        reduction = (maxStat < 20) ? 5 : maxStat < 40 ? 6 : maxStat < 70 ? 7 : 8;
        currentStat = p.getSkills().getLevel(skillId);
        if (currentStat <= 8) {
            p.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
        } else {
            p.getSkills().setLevel(skillId, currentStat - reduction);
        }
    }
}

