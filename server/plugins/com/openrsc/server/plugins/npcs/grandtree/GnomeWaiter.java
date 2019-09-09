package com.openrsc.server.plugins.npcs.grandtree;

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

public final class GnomeWaiter implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 25, 1,
		new Item(ItemId.GNOME_WAITER_CHEESE_AND_TOMATO_BATTA.id(), 3), new Item(ItemId.GNOME_WAITER_TOAD_BATTA.id(), 3), new Item(ItemId.GNOME_WAITER_WORM_BATTA.id(), 3),
		new Item(ItemId.GNOME_WAITER_FRUIT_BATTA.id(), 3), new Item(ItemId.GNOME_WAITER_VEG_BATTA.id(), 3), new Item(ItemId.GNOME_WAITER_CHOCOLATE_BOMB.id(), 3),
		new Item(ItemId.GNOME_WAITER_VEGBALL.id(), 3), new Item(ItemId.GNOME_WAITER_WORM_HOLE.id(), 3), new Item(ItemId.GNOME_WAITER_TANGLED_TOADS_LEGS.id(), 3),
		new Item(ItemId.GNOME_WAITER_CHOC_CRUNCHIES.id(), 4), new Item(ItemId.GNOME_WAITER_WORM_CRUNCHIES.id(), 4), new Item(ItemId.GNOME_WAITER_TOAD_CRUNCHIES.id(), 4),
		new Item(ItemId.GNOME_WAITER_SPICE_CRUNCHIES.id(), 4));

	@Override
	public GameStateEvent onTalkToNpc(Player p, final Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					playerTalk(p, n, "hello");
					npcTalk(p, n, "good afternoon",
						"can i tempt you with our new menu?");

					int option = showMenu(p, n, "i'll take a look", "not really");
					switch (option) {
						case 0:
							npcTalk(p, n, "i hope you like what you see");
							p.setAccessingShop(shop);
							ActionSender.showShop(p, shop);
							break;

						case 1:
							npcTalk(p, n, "ok then, enjoy your stay");
							break;
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.GNOME_WAITER.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

}
