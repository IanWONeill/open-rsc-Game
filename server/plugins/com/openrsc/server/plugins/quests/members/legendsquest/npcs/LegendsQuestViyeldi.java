package com.openrsc.server.plugins.quests.members.legendsquest.npcs;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class LegendsQuestViyeldi implements PickupListener , PlayerAttackNpcListener , PlayerMageNpcListener , PlayerRangeNpcListener , TalkToNpcListener , PickupExecutiveListener , PlayerAttackNpcExecutiveListener , PlayerMageNpcExecutiveListener , PlayerRangeNpcExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.VIYELDI.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.VIYELDI.id()) {
                        switch (p.getQuestStage(Quests.LEGENDS_QUEST)) {
                            case 7 :
                                Functions.___message(p, n, 1300, "The headless, spirit of Viyeldi animates and walks towards you.");
                                if (!p.getCache().hasKey("killed_viyeldi")) {
                                    Functions.___message(p, n, 1300, "And starts talking to you in a shrill, excited voice...");
                                    Functions.___npcTalk(p, n, "Beware adventurer, lest thee loses they head in search of source.", "Bravery has thee been tested and not found wanting..");
                                    Functions.___message(p, n, 1300, "The spirit wavers slightly and then stands proud...");
                                    Functions.___npcTalk(p, n, "But perilous danger waits for thee,", "Tojalon, Senay and Devere makes three,", "None hold malice but will test your might,", "Pray that you do not lose this fight,", "If however, you win this day,", "Take heart that see the source you may,", "Through dragons eye will you gain new heart,", "To see the source and then depart.");
                                } else {
                                    p.message("Viyeldi falls silent...");
                                    Functions.sleep(7000);
                                    p.message("...and the clothes slump to the floor.");
                                    if (n != null)
                                        n.remove();

                                }
                                break;
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return ((i.getID() == ItemId.A_BLUE_WIZARDS_HAT.id()) && (i.getX() == 426)) && (i.getY() == 3708);
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((i.getID() == ItemId.A_BLUE_WIZARDS_HAT.id()) && (i.getX() == 426)) && (i.getY() == 3708)) {
                        p.teleport(i.getX(), i.getY());
                        Functions.___message(p, 1300, "Your hand passes through the hat as if it wasn't there.");
                        if (p.getQuestStage(Quests.LEGENDS_QUEST) >= 8) {
                            return null;
                        }
                        p.teleport(i.getX(), i.getY() - 1);
                        Functions.___message(p, 1300, "Instantly the clothes begin to animate and then walk towards you.");
                        Npc n = Functions.getNearestNpc(p, NpcId.VIYELDI.id(), 3);
                        if (n == null)
                            n = Functions.spawnNpc(p.getWorld(), NpcId.VIYELDI.id(), i.getX(), i.getY(), 60000);

                        if (n != null) {
                            n.initializeTalkScript(p);
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        return n.getID() == NpcId.VIYELDI.id();
    }

    @Override
    public GameStateEvent onPlayerAttackNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.VIYELDI.id()) {
                        attackViyeldi(p, n);
                    }
                    return null;
                });
            }
        };
    }

    private void attackViyeldi(Player p, Npc n) {
        if (n.getID() == NpcId.VIYELDI.id()) {
            if (!p.getInventory().wielding(ItemId.DARK_DAGGER.id())) {
                Functions.___message(p, n, 1300, "Your attack passes straight through Viyeldi.");
                Functions.___npcTalk(p, n, "Take challenge with me is useless for I am impervious to your attack", "Take your fight to someone else, and maybe then get back on track.");
            } else {
                p.getInventory().replace(ItemId.DARK_DAGGER.id(), ItemId.GLOWING_DARK_DAGGER.id());
                Functions.___message(p, n, 1300, "You thrust the Dark Dagger at Viyeldi...");
                Functions.___npcTalk(p, n, "So, you have fallen for the foul one's trick...");
                Functions.___message(p, n, 1300, "You hit Viyeldi squarely with the Dagger .");
                Functions.___npcTalk(p, n, "AhhhhhhhhHH! The Pain!");
                Functions.___message(p, n, 1300, "You see a flash as something travels from Viyeldi into the dagger.");
                Functions.___message(p, n, 0, "The dagger seems to glow as Viyeldi crumpels to the floor.");
                if (n != null) {
                    n.remove();
                }
                if (!p.getCache().hasKey("killed_viyeldi")) {
                    p.getCache().store("killed_viyeldi", true);
                }
            }
        }
    }

    @Override
    public boolean blockPlayerMageNpc(Player p, Npc n) {
        return n.getID() == NpcId.VIYELDI.id();
    }

    @Override
    public GameStateEvent onPlayerMageNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.VIYELDI.id()) {
                        attackViyeldi(p, n);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerRangeNpc(Player p, Npc n) {
        return n.getID() == NpcId.VIYELDI.id();
    }

    @Override
    public GameStateEvent onPlayerRangeNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.VIYELDI.id()) {
                        attackViyeldi(p, n);
                    }
                    return null;
                });
            }
        };
    }
}

