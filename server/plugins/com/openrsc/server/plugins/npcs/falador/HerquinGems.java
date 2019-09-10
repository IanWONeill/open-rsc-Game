package com.openrsc.server.plugins.npcs.falador;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
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

import static com.openrsc.server.plugins.Functions.*;

public final class HerquinGems implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 60000 * 10, 100, 70, 3, new Item(ItemId.UNCUT_SAPPHIRE.id(),
		1), new Item(ItemId.UNCUT_EMERALD.id(), 0), new Item(ItemId.UNCUT_RUBY.id(), 0), new Item(ItemId.UNCUT_DIAMOND.id(), 0),
		new Item(ItemId.SAPPHIRE.id(), 1), new Item(ItemId.EMERALD.id(), 0), new Item(ItemId.RUBY.id(), 0),
		new Item(ItemId.DIAMOND.id(), 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.HERQUIN.id();
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
					int option = showMenu(p, n, false, //do not send over
						"Do you wish to trade?", "Sorry i don't want to talk to you actually");
					if (option == 0) {
						playerTalk(p, n, "Do you wish to trade?");
						npcTalk(p, n, "Why yes this a jewel shop after all");
						p.setAccessingShop(shop);
						ActionSender.showShop(p, shop);
					} else if (option == 1) {
						playerTalk(p, n, "Sorry I don't want to talk to you actually");
					}

					return null;
				});
			}
		};
	}
}
