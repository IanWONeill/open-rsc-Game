package com.openrsc.server.plugins.misc;


import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


/**
 *
 *
 * @author Fate
 */
public class Necromancer implements PlayerAttackNpcListener , PlayerKilledNpcListener , PlayerMageNpcListener , TalkToNpcListener , PlayerAttackNpcExecutiveListener , PlayerKilledNpcExecutiveListener , PlayerMageNpcExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        return n.getID() == NpcId.NECROMANCER.id();
    }

    private void necromancerFightSpawnMethod(Player p, Npc necromancer) {
        if (necromancer.getID() == NpcId.NECROMANCER.id()) {
            Npc zombie = Functions.getNearestNpc(p, NpcId.ZOMBIE_INVOKED.id(), 10);
            if (((!p.getCache().hasKey("necroSpawn")) || (p.getCache().hasKey("necroSpawn") && (p.getCache().getInt("necroSpawn") < 7))) || ((p.getCache().hasKey("killedZomb") && (p.getCache().getInt("killedZomb") != 0)) && (zombie == null))) {
                Functions.___npcTalk(p, necromancer, "I summon the undead to smite you down");
                p.setBusyTimer(5);
                zombie = p.getWorld().registerNpc(new Npc(necromancer.getWorld(), NpcId.ZOMBIE_INVOKED.id(), necromancer.getX(), necromancer.getY()));
                zombie.setShouldRespawn(false);
                Functions.sleep(1600);
                if (!p.inCombat()) {
                    zombie.startCombat(p);
                }
                if (!p.getCache().hasKey("necroSpawn")) {
                    p.getCache().set("necroSpawn", 1);
                } else {
                    int spawn = p.getCache().getInt("necroSpawn");
                    if (spawn < 7) {
                        p.getCache().set("necroSpawn", spawn + 1);
                    }
                }
                if (!p.getCache().hasKey("killedZomb")) {
                    p.getCache().set("killedZomb", 7);
                }
            } else
                if ((((p.getCache().getInt("necroSpawn") > 6) && p.getCache().hasKey("necroSpawn")) && (zombie != null)) && (p.getCache().getInt("killedZomb") != 0)) {
                    Functions.___npcTalk(p, zombie, "Raargh");
                    p.setBusyTimer(5);
                    zombie.startCombat(p);
                } else
                    if ((p.getCache().getInt("killedZomb") == 0) && p.getCache().hasKey("killedZomb")) {
                        p.startCombat(necromancer);
                    }


        }
    }

    private void necromancerOnKilledMethod(Player p, Npc n) {
        if (n.getID() == NpcId.NECROMANCER.id()) {
            n.killedBy(p);
            p.getCache().remove("necroSpawn");
            p.getCache().remove("killedZomb");
            Npc newZombie = p.getWorld().registerNpc(new Npc(n.getWorld(), NpcId.ZOMBIE_INVOKED.id(), p.getX(), p.getY()));
            newZombie.setShouldRespawn(false);
            newZombie.setChasing(p);
        }
        if (n.getID() == NpcId.ZOMBIE_INVOKED.id()) {
            n.killedBy(p);
            if (p.getCache().hasKey("killedZomb") && (p.getCache().getInt("killedZomb") != 0)) {
                int delete = p.getCache().getInt("killedZomb");
                p.getCache().set("killedZomb", delete - 1);
            }
        }
    }

    @Override
    public GameStateEvent onPlayerAttackNpc(Player p, Npc necromancer) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    necromancerFightSpawnMethod(p, necromancer);
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    necromancerOnKilledMethod(p, n);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return (n.getID() == NpcId.NECROMANCER.id()) || (n.getID() == NpcId.ZOMBIE_INVOKED.id());
    }

    @Override
    public boolean blockPlayerMageNpc(Player p, Npc n) {
        return (n.getID() == NpcId.NECROMANCER.id()) || (n.getID() == NpcId.ZOMBIE_INVOKED.id());
    }

    @Override
    public GameStateEvent onPlayerMageNpc(Player p, Npc necromancer) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    necromancerFightSpawnMethod(p, necromancer);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.NECROMANCER.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    p.playerServerMessage(MessageType.QUEST, "Invrigar the necromancer is not interested in talking");
                    return null;
                });
            }
        };
    }
}

