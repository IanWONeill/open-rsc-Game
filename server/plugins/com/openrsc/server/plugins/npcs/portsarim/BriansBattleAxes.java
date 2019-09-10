package com.openrsc.server.plugins.npcs.portsarim;

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

public final class BriansBattleAxes implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 15000, 100, 55, 1, new Item(ItemId.BRONZE_BATTLE_AXE.id(),
		4), new Item(ItemId.IRON_BATTLE_AXE.id(), 3), new Item(ItemId.STEEL_BATTLE_AXE.id(), 2), new Item(ItemId.BLACK_BATTLE_AXE.id(), 1),
		new Item(ItemId.MITHRIL_BATTLE_AXE.id(), 1), new Item(ItemId.ADAMANTITE_BATTLE_AXE.id(), 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.BRIAN.id();
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
					npcTalk(p, n, "ello");
					int option = showMenu(p, n, false, //do not send over
						"So are you selling something?", "ello");
					switch (option) {
						case 0:
							npcTalk(p, n, "Yep take a look at these great axes");
							p.setAccessingShop(shop);
							ActionSender.showShop(p, shop);
							break;
						case 1:
							playerTalk(p, n, "Ello");
							break;
					}

					return null;
				});
			}
		};
	}
}
