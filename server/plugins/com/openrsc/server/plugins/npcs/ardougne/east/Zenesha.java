package com.openrsc.server.plugins.npcs.ardougne.east;

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

public class Zenesha implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(ItemId.BRONZE_PLATE_MAIL_TOP.id(), 3), new Item(ItemId.IRON_PLATE_MAIL_TOP.id(), 1), new Item(ItemId.STEEL_PLATE_MAIL_TOP.id(), 1), new Item(ItemId.BLACK_PLATE_MAIL_TOP.id(), 1), new Item(ItemId.MITHRIL_PLATE_MAIL_TOP.id(), 1));

	@Override
	public GameStateEvent onTalkToNpc(Player p, Npc n) {
		return new GameStateEvent(p.getWorld(), p, 0, getClass().getSimpleName() + " " + getClass().getEnclosingMethod().getName()) {
			public void init() {
				addState(0, () -> {
					npcTalk(p, n, "hello I sell plate mail tops");
					int menu = showMenu(p, n, false, "I'm not intersted", "I may be intersted");
					if (menu == 0) {
						playerTalk(p, n, "I'm not interested");
					} else if (menu == 1) {
						playerTalk(p, n, "I may be interested");
						npcTalk(p, n, "Look at these fine samples then");
						p.setAccessingShop(shop);
						ActionSender.showShop(p, shop);
					}

					return null;
				});
			}
		};
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.ZENESHA.id();
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
