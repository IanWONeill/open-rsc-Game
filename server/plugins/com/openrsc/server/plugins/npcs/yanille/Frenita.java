package com.openrsc.server.plugins.npcs.yanille;

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

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

public final class Frenita implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 55, 1,
		new Item(ItemId.PIE_DISH.id(), 5), new Item(ItemId.COOKING_APPLE.id(), 2), new Item(ItemId.CAKE_TIN.id(), 2),
		new Item(ItemId.BOWL.id(), 2), new Item(ItemId.POTATO.id(), 5), new Item(ItemId.TINDERBOX.id(), 4),
		new Item(ItemId.JUG.id(), 1), new Item(ItemId.POT.id(), 8), new Item(ItemId.CHOCOLATE_BAR.id(), 2),
		new Item(ItemId.POT_OF_FLOUR.id(), 8));

	@Override
	public GameStateEvent onTalkToNpc(Player p, final Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					npcTalk(p, n, "Would you like to buy some cooking equipment");

					int option = showMenu(p, n, "Yes please", "No thankyou");
					switch (option) {

						case 0:
							p.setAccessingShop(shop);
							ActionSender.showShop(p, shop);
							break;
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.FRENITA.id();
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
