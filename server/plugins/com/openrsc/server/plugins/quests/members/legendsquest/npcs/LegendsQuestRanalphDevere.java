package com.openrsc.server.plugins.quests.members.legendsquest.npcs;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerNpcRunListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerNpcRunExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;
import java.util.concurrent.Callable;


public class LegendsQuestRanalphDevere implements PlayerAttackNpcListener , PlayerKilledNpcListener , PlayerMageNpcListener , PlayerNpcRunListener , PlayerRangeNpcListener , PlayerAttackNpcExecutiveListener , PlayerKilledNpcExecutiveListener , PlayerMageNpcExecutiveListener , PlayerNpcRunExecutiveListener , PlayerRangeNpcExecutiveListener {
    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        return ((n.getID() == NpcId.RANALPH_DEVERE.id()) && (!Functions.hasItem(p, ItemId.A_HUNK_OF_CRYSTAL.id()))) && (!p.getCache().hasKey("cavernous_opening"));
    }

    @Override
    public GameStateEvent onPlayerAttackNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((n.getID() == NpcId.RANALPH_DEVERE.id()) && (!Functions.hasItem(p, ItemId.A_HUNK_OF_CRYSTAL.id()))) && (!p.getCache().hasKey("cavernous_opening"))) {
                        attackMessage(p, n);
                    }
                    return null;
                });
            }
        };
    }

    private void attackMessage(Player p, Npc n) {
        if (((n.getID() == NpcId.RANALPH_DEVERE.id()) && (!Functions.hasItem(p, ItemId.A_HUNK_OF_CRYSTAL.id()))) && (!p.getCache().hasKey("cavernous_opening"))) {
            Functions.___npcTalk(p, n, "Upon my honour, I will defend till the end...");
            n.setChasing(p);
            Functions.___npcTalk(p, n, "May your aim be true and the best of us win...");
        }
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return ((n.getID() == NpcId.RANALPH_DEVERE.id()) && (!p.getCache().hasKey("cavernous_opening"))) || (((n.getID() == NpcId.RANALPH_DEVERE.id()) && (p.getQuestStage(Quests.LEGENDS_QUEST) == 8)) && p.getCache().hasKey("viyeldi_companions"));
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((n.getID() == NpcId.RANALPH_DEVERE.id()) && (p.getQuestStage(Quests.LEGENDS_QUEST) == 8)) && p.getCache().hasKey("viyeldi_companions")) {
                        n.remove();
                        if (p.getCache().hasKey("viyeldi_companions") && (p.getCache().getInt("viyeldi_companions") == 3)) {
                            p.getCache().set("viyeldi_companions", 4);
                        }
                        Functions.___message(p, 1300, "A nerve tingling scream echoes around you as you slay the dead Hero.", "@yel@Ranalph Devere: Ahhhggggh", "@yel@Ranalph Devere:Forever must I live in this torment till this beast is slain...");
                        Functions.sleep(650);
                        LegendsQuestNezikchened.demonFight(p);
                    }
                    if ((n.getID() == NpcId.RANALPH_DEVERE.id()) && (!p.getCache().hasKey("cavernous_opening"))) {
                        if ((Functions.hasItem(p, ItemId.A_HUNK_OF_CRYSTAL.id()) || Functions.hasItem(p, ItemId.A_RED_CRYSTAL.id())) || Functions.hasItem(p, ItemId.A_GLOWING_RED_CRYSTAL.id())) {
                            Functions.___npcTalk(p, n, "A fearsome foe you are, and bettered me once have you done already.");
                            p.message("Your opponent is retreating");
                            n.remove();
                        } else {
                            Functions.___npcTalk(p, n, "You have proved yourself of the honour..");
                            p.resetCombatEvent();
                            n.resetCombatEvent();
                            p.message("Your opponent is retreating");
                            Functions.___npcTalk(p, n, "");
                            n.remove();
                            Functions.___message(p, 1300, "A piece of crystal forms in midair and falls to the floor.", "You place the crystal in your inventory.");
                            Functions.addItem(p, ItemId.A_HUNK_OF_CRYSTAL.id(), 1);
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerMageNpc(Player p, Npc n) {
        return ((n.getID() == NpcId.RANALPH_DEVERE.id()) && (!Functions.hasItem(p, ItemId.A_HUNK_OF_CRYSTAL.id()))) && (!p.getCache().hasKey("cavernous_opening"));
    }

    @Override
    public GameStateEvent onPlayerMageNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((n.getID() == NpcId.RANALPH_DEVERE.id()) && (!Functions.hasItem(p, ItemId.A_HUNK_OF_CRYSTAL.id()))) && (!p.getCache().hasKey("cavernous_opening"))) {
                        attackMessage(p, n);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerRangeNpc(Player p, Npc n) {
        return ((n.getID() == NpcId.RANALPH_DEVERE.id()) && (!Functions.hasItem(p, ItemId.A_HUNK_OF_CRYSTAL.id()))) && (!p.getCache().hasKey("cavernous_opening"));
    }

    @Override
    public GameStateEvent onPlayerRangeNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((n.getID() == NpcId.RANALPH_DEVERE.id()) && (!Functions.hasItem(p, ItemId.A_HUNK_OF_CRYSTAL.id()))) {
                        attackMessage(p, n);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerNpcRun(Player p, Npc n) {
        return ((n.getID() == NpcId.RANALPH_DEVERE.id()) && (p.getQuestStage(Quests.LEGENDS_QUEST) == 8)) && p.getCache().hasKey("viyeldi_companions");
    }

    @Override
    public GameStateEvent onPlayerNpcRun(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((n.getID() == NpcId.RANALPH_DEVERE.id()) && (p.getQuestStage(Quests.LEGENDS_QUEST) == 8)) && p.getCache().hasKey("viyeldi_companions")) {
                        n.remove();
                        Functions.___message(p, 1300, "As you try to make your escape,", "the Viyeldi fighter is recalled by the demon...", "@yel@Nezikchened : Ha, ha ha!", "@yel@Nezikchened : Run then fetid worm...and never touch my totem again...");
                    }
                    return null;
                });
            }
        };
    }
}

