package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public final class AuburysRunes implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 70, 2, new Item(ItemId.FIRE_RUNE.id(),
		50), new Item(ItemId.WATER_RUNE.id(), 50), new Item(ItemId.AIR_RUNE.id(), 50), new Item(ItemId.EARTH_RUNE.id(),
		50), new Item(ItemId.MIND_RUNE.id(), 50), new Item(ItemId.BODY_RUNE.id(), 50));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.AUBURY.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					ArrayList<String> menu = new ArrayList<>();
					menu.add("Yes please");
					menu.add("Oh it's a rune shop. No thank you, then.");

					if (p.getWorld().getServer().getConfig().WANT_RUNECRAFTING)
						if (p.getQuestStage(Quests.RUNE_MYSTERIES) == 2)
							menu.add("I've been sent here with a package for you.");
						else if (p.getQuestStage(Quests.RUNE_MYSTERIES) == 3)
							menu.add("Rune mysteries");
						else if (p.getQuestStage(Quests.RUNE_MYSTERIES) == -1)
							menu.add("Teleport to rune essence");

					npcTalk(p, n, "Do you want to buy some runes?");

					int opt = showMenu(p, n, false, menu.toArray(new String[menu.size()]));

					if (opt == 0) {
						playerTalk(p, n, "Yes Please");
						p.setAccessingShop(shop);
						ActionSender.showShop(p, shop);
					}
					else if (opt == 1) {
						playerTalk(p, n, "Oh it's a rune shop. No thank you, then");
						npcTalk(p, n,
							"Well if you find someone who does want runes,",
							"send them my way");
					}
					else if (opt == 2) {
						com.openrsc.server.plugins.quests.members.RuneMysteries.auburyDialog(p,n);
					}

					return null;
				});
			}
		};
	}
}
