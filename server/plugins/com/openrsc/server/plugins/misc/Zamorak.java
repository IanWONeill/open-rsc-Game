package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;

import static com.openrsc.server.plugins.Functions.*;

/**
 * @author n0m, Fate
 */
public class Zamorak implements TalkToNpcListener, TalkToNpcExecutiveListener, PickupListener, PickupExecutiveListener, PlayerAttackNpcExecutiveListener, PlayerAttackNpcListener, PlayerRangeNpcExecutiveListener, PlayerRangeNpcListener, PlayerMageNpcExecutiveListener, PlayerMageNpcListener {

	@Override
	public GameStateEvent onPickup(Player owner, GroundItem item) {
		return new GameStateEvent(owner.getWorld(), owner, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (item.getID() == ItemId.WINE_OF_ZAMORAK.id() && item.getX() == 333 && item.getY() == 434) {
						Npc zam = getMultipleNpcsInArea(owner, 7, NpcId.MONK_OF_ZAMORAK.id(), NpcId.MONK_OF_ZAMORAK_MACE.id());
						if (zam != null && !zam.inCombat()) {
							owner.face(zam);
							zam.face(owner);
							applyCurse(owner, zam);
						}
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == ItemId.WINE_OF_ZAMORAK.id()) {
			Npc zam = getMultipleNpcsInArea(p, 7, NpcId.MONK_OF_ZAMORAK.id(), NpcId.MONK_OF_ZAMORAK_MACE.id());
			return zam != null && !zam.inCombat();
		}
		return false;
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id();
	}

	@Override
	public GameStateEvent onPlayerAttackNpc(Player p, Npc zamorak) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (zamorak.getID() == NpcId.MONK_OF_ZAMORAK.id() || zamorak.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id()) {
						applyCurse(p, zamorak);
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		return n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id();
	}

	@Override
	public GameStateEvent onPlayerMageNpc(Player p, Npc zamorak) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (zamorak.getID() == NpcId.MONK_OF_ZAMORAK.id() || zamorak.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id()) {
						applyCurse(p, zamorak);
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id();
	}

	@Override
	public GameStateEvent onPlayerRangeNpc(Player p, Npc zamorak) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (zamorak.getID() == NpcId.MONK_OF_ZAMORAK.id() || zamorak.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id()) {
						applyCurse(p, zamorak);
					}

					return null;
				});
			}
		};
	}

	private void applyCurse(Player owner, Npc zam) {
		owner.setBusy(true);
		zam.getUpdateFlags().setChatMessage(new ChatMessage(zam, "A curse be upon you", owner));
		sleep(2200);
		owner.message("You feel slightly weakened");
		int dmg = (int) Math.ceil(((owner.getSkills().getMaxStat(Skills.HITS) + 20) * 0.05));
		owner.damage(dmg);
		int[] stats = {Skills.ATTACK, Skills.DEFENSE, Skills.STRENGTH};
		for(int affectedStat : stats) {
			/* How much to lower the stat */
			int lowerBy = (int) Math.ceil(((owner.getSkills().getMaxStat(affectedStat) + 20) * 0.05));
			/* New current level */
			final int newStat = Math.max(0, owner.getSkills().getLevel(affectedStat) - lowerBy);
			owner.getSkills().setLevel(affectedStat, newStat);
		}
		sleep(500);
		zam.setChasing(owner);
		owner.setBusy(false);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id();
	}

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					if (n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id()) {
						if (n.getID() == NpcId.MONK_OF_ZAMORAK.id()) {
							npcTalk(p, n, "Save your speech for the altar");
						} else {
							npcTalk(p, n, "Who are you to dare speak to the servants of Zamorak ?");
						}
						n.setChasing(p);
					}

					return null;
				});
			}
		};
	}
}
