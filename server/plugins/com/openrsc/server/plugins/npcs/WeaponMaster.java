package com.openrsc.server.plugins.npcs;


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
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.quests.free.ShieldOfArrav;
import java.util.concurrent.Callable;


public class WeaponMaster implements PickupListener , PlayerAttackNpcListener , TalkToNpcListener , PickupExecutiveListener , PlayerAttackNpcExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.WEAPONSMASTER.id();
    }

    @Override
    public GameStateEvent onPlayerAttackNpc(Player p, Npc affectedmob) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (ShieldOfArrav.isPhoenixGang(p)) {
                        Functions.___playerTalk(p, affectedmob, "Nope, I'm not going to attack a fellow gang member");
                        return null;
                    } else {
                        p.startCombat(affectedmob);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        return n.getID() == NpcId.WEAPONSMASTER.id();
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return (((i.getX() == 107) || (i.getX() == 105)) && (i.getY() == 1476)) && (i.getID() == ItemId.PHOENIX_CROSSBOW.id());
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((((i.getX() == 107) || (i.getX() == 105)) && (i.getY() == 1476)) && (i.getID() == ItemId.PHOENIX_CROSSBOW.id())) {
                        Npc weaponMaster = Functions.getNearestNpc(p, NpcId.WEAPONSMASTER.id(), 20);
                        if (weaponMaster == null) {
                            p.getWorld().unregisterItem(i);
                            Functions.addItem(p, ItemId.PHOENIX_CROSSBOW.id(), 1);
                            if (p.getCache().hasKey("arrav_mission") && ((p.getCache().getInt("arrav_mission") & 1) == ShieldOfArrav.BLACKARM_MISSION)) {
                                p.getCache().set("arrav_gang", ShieldOfArrav.BLACK_ARM);
                                p.updateQuestStage(Quests.SHIELD_OF_ARRAV, 4);
                                p.getCache().remove("arrav_mission");
                                p.getCache().remove("spoken_tramp");
                            }
                        } else
                            if ((!p.getCache().hasKey("arrav_gang")) || ShieldOfArrav.isBlackArmGang(p)) {
                                Functions.___npcTalk(p, weaponMaster, "Hey thief!");
                                weaponMaster.setChasing(p);
                            } else
                                if (ShieldOfArrav.isPhoenixGang(p)) {
                                    Functions.___npcTalk(p, weaponMaster, "Hey, that's Straven's", "He won't like you messing with that");
                                }


                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((!p.getCache().hasKey("arrav_gang")) || ShieldOfArrav.isBlackArmGang(p)) {
                        Functions.___playerTalk(p, n, "Hello");
                        Functions.___npcTalk(p, n, "Hey I don't know you", "You're not meant to be here");
                        n.setChasing(p);
                    } else
                        if (ShieldOfArrav.isPhoenixGang(p)) {
                            Functions.___npcTalk(p, n, "Hello Fellow phoenix", "What are you after?");
                            int menu = Functions.___showMenu(p, n, "I'm after a weapon or two", "I'm looking for treasure");
                            if (menu == 0) {
                                Functions.___npcTalk(p, n, "Sure have a look around");
                            } else
                                if (menu == 1) {
                                    Functions.___npcTalk(p, n, "We've not got any up here", "Go mug someone somewhere", "If you want some treasure");
                                }

                        }

                    return null;
                });
            }
        };
    }
}

