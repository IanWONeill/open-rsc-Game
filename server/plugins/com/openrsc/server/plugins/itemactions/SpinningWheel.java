package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.showBubble;

public class SpinningWheel implements InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return obj.getID() == 121;
	}

	@Override
	public GameStateEvent onInvUseOnObject(GameObject obj, final Item item, Player player) {
		return new GameStateEvent(player.getWorld(), player, 0, getClass().getSimpleName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName()) {
			public void init() {
				addState(0, () -> {
					int produceID = -1;
					int requiredLevel = -1;
					int experience = -1;
					String verb, consumedItem, producedItem;

					if (item.getID() == ItemId.WOOL.id()) {
						produceID = ItemId.BALL_OF_WOOL.id();
						requiredLevel = 1;
						experience = 10;
						verb = "spin";
						consumedItem = "sheeps wool";
						producedItem = "nice ball of wool";
					}

					else if (item.getID() == ItemId.FLAX.id()) {
						produceID = ItemId.BOW_STRING.id();
						requiredLevel = 10;
						experience = 60;
						verb = "make";
						consumedItem = "flax";
						producedItem = "bow string";
					}

					else {
						player.message("Nothing interesting happens");
						return null;
					}

					final int produce = produceID;
					final int requirement = requiredLevel;
					final int exp = experience;
					if (produce == -1 || requirement == -1 || exp == -1) {
						return null;
					}
					player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Spinning Wheel Batch", Formulae
						.getRepeatTimes(player, Skills.CRAFTING), false) {

						@Override
						public void action() {
							if (getOwner().getSkills().getLevel(Skills.CRAFTING) < requirement) {
								message(getOwner(), "You need to have a crafting of level "
									+ requirement + " or higher to make a "
									+ new Item(produce).getDef(getWorld()).getName().toLowerCase());
								interrupt();
								return;
							}
							if (getOwner().getInventory().remove(item.getID(), 1) > -1) {
								showBubble(getOwner(), item);
								getOwner().playSound("mechanical");
								getOwner().message("You " + verb + " the "
									+ consumedItem + " into a " + producedItem);
								getOwner().getInventory().add(new Item(produce, 1));
								getOwner().incExp(Skills.CRAFTING, exp, true);
							} else {
								interrupt();
							}
						}
					});

					return null;
				});
			}
		};
	}
}
