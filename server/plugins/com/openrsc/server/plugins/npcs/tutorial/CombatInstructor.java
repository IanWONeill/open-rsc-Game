package com.openrsc.server.plugins.npcs.tutorial;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class CombatInstructor implements PlayerKilledNpcListener , TalkToNpcListener , PlayerAttackNpcExecutiveListener , PlayerKilledNpcExecutiveListener , PlayerMageNpcExecutiveListener , TalkToNpcExecutiveListener {
    /**
     *
     *
     * @author Davve
    Tutorial island combat instructor
    Level-7 rat not the regular rat!!!!!!!
    YOUTUBE: NO XP GIVEN IN ANY COMBAT STAT BY KILLING THE RAT
     */
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((((!Functions.hasItem(p, ItemId.WOODEN_SHIELD.id(), 1)) && (!Functions.hasItem(p, ItemId.BRONZE_LONG_SWORD.id(), 1))) && p.getCache().hasKey("tutorial")) && (p.getCache().getInt("tutorial") == 15)) {
                        Functions.___npcTalk(p, n, "Aha a new recruit", "I'm here to teach you the basics of fighting", "First of all you need weapons");
                        Functions.addItem(p, ItemId.WOODEN_SHIELD.id(), 1);// Add wooden shield to the players inventory

                        Functions.addItem(p, ItemId.BRONZE_LONG_SWORD.id(), 1);// Add bronze long sword to the players inventory

                        Functions.___message(p, "The instructor gives you a sword and shield");
                        Functions.___npcTalk(p, n, "look after these well", "These items will now have appeared in your inventory", "You can access them by selecting the bag icon in the menu bar", "which can be found in the top right hand corner of the screen", "To wield your weapon and shield left click on them within your inventory", "their box will go red to show you are wearing them");
                        p.message("When you have done this speak to the combat instructor again");
                        p.getCache().set("tutorial", 16);
                    } else
                        if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") == 16)) {
                            if (((!p.getInventory().hasItemId(ItemId.WOODEN_SHIELD.id())) || p.getInventory().wielding(ItemId.WOODEN_SHIELD.id())) && ((!p.getInventory().hasItemId(ItemId.BRONZE_LONG_SWORD.id())) || p.getInventory().wielding(ItemId.BRONZE_LONG_SWORD.id()))) {
                                Functions.___npcTalk(p, n, "Today we're going to be killing giant rats");
                                Npc rat = Functions.getNearestNpc(p, NpcId.RAT_TUTORIAL.id(), 10);
                                if (rat != null) {
                                    Functions.___npcTalk(p, rat, "squeek");
                                }
                                Functions.___npcTalk(p, n, "move your mouse over a rat you will see it is level 7", "You will see that it's level is written in green", "If it is green this means you have a strong chance of killing it", "creatures with their name in red should probably be avoided", "As this indicates they are tougher than you", "left click on the rat to attack it");
                            } else {
                                Functions.___npcTalk(p, n, "You need to wield your equipment", "You can access it by selecting the bag icon", "which can be found in the top right hand corner of the screen", "To wield your weapon and shield left click on them", "their boxs will go red to show you are wearing them");
                                p.message("When you have done this speak to the combat instructor again");
                            }
                        } else
                            if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") >= 20)) {
                                Functions.___npcTalk(p, n, "Well done you're a born fighter", "As you kill things", "Your combat experience will go up", "this expereince will slowly cause you to get tougher", "eventually you will be able to take on stronger enemies", "Such as those found in dungeons", "Now contine to the building to the northeast");
                                if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") < 25))
                                    p.getCache().set("tutorial", 25);

                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.COMBAT_INSTRUCTOR.id();
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return n.getID() == NpcId.RAT_TUTORIAL.id();
    }

    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        if ((!p.getCache().hasKey("tutorial")) || (!p.getLocation().aroundTutorialRatZone()))
            return false;

        if (n.getID() == NpcId.CHICKEN.id())
            return false;

        if (n.getID() == NpcId.RAT_TUTORIAL.id()) {
            if (p.getCache().getInt("tutorial") == 16)
                return false;

        }
        if (p.getCache().getInt("tutorial") < 16)
            Functions.___message(p, "Speak to the combat instructor before killing rats");
        else
            Functions.___message(p, "That's enough rat killing for now");

        return true;
    }

    @Override
    public boolean blockPlayerMageNpc(Player p, Npc n) {
        return blockPlayerAttackNpc(p, n);
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.RAT_TUTORIAL.id()) {
                        n.resetCombatEvent();
                        n.remove();
                        // GIVE NO XP ACCORDING TO YOUTUBE VIDEOS FOR COMBAT SINCE IT WAS HEAVILY ABUSED IN REAL RSC TO TRAIN ON THOSE RATS.
                        if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") == 16)) {
                            Functions.___message(p, "Well done you've killed the rat", "Now speak to the combat instructor again");
                            p.getCache().set("tutorial", 20);
                        }
                    }
                    return null;
                });
            }
        };
    }
}

