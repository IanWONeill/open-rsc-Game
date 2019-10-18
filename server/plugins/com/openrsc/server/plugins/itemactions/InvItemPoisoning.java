package com.openrsc.server.plugins.itemactions;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import java.util.concurrent.Callable;


public class InvItemPoisoning implements InvUseOnItemListener , InvUseOnItemExecutiveListener {
    @Override
    public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
        return (item1.getID() == ItemId.WEAPON_POISON.id()) || (item2.getID() == ItemId.WEAPON_POISON.id());
    }

    @Override
    public GameStateEvent onInvUseOnItem(Player player, Item item1, Item item2) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (item1.getID() == ItemId.WEAPON_POISON.id()) {
                        applyPoison(player, item2);
                    } else
                        if (item2.getID() == ItemId.WEAPON_POISON.id()) {
                            applyPoison(player, item1);
                        }

                    return null;
                });
            }
        };
    }

    private void applyPoison(Player player, Item item) {
        int makeAmount = 1;
        int maxAmount;
        String rawItemName = item.getDef(player.getWorld()).getName().toLowerCase();
        String procItemName;
        if (item.getDef(player.getWorld()).isStackable()) {
            // 6 darts or 5 bolts/arrows
            maxAmount = (rawItemName.contains("dart")) ? 6 : 5;
            makeAmount = (Functions.hasItem(player, item.getID(), maxAmount)) ? maxAmount : player.getInventory().countId(item.getID());
            procItemName = "some ";
            if (rawItemName.contains("dart")) {
                procItemName += "darts";
            } else
                if (rawItemName.contains("bolt")) {
                    procItemName += "bolts";
                } else
                    if (rawItemName.contains("arrow")) {
                        procItemName += "arrows";
                    } else {
                        procItemName += rawItemName + (!rawItemName.endsWith("s") ? "s" : "");
                    }


            procItemName += "!";
        } else {
            procItemName = ("a " + rawItemName) + ".";
        }
        Item poisonedItem = getPoisonedItem(player.getWorld(), item.getDef(player.getWorld()).getName());
        if (poisonedItem != null) {
            if (Functions.removeItem(player, ItemId.WEAPON_POISON.id(), 1) && Functions.removeItem(player, item.getID(), makeAmount)) {
                player.message("You poison " + procItemName);
                Functions.addItem(player, poisonedItem.getID(), makeAmount);
            }
        } else {
            player.message("Nothing interesting happens");
        }
    }

    private Item getPoisonedItem(World world, String name) {
        String poisonedVersion = "Poisoned " + name;
        String poisonedVersion2 = "Poison " + name;
        for (int i = 0; i < world.getServer().getEntityHandler().items.length; i++) {
            ItemDefinition def = world.getServer().getEntityHandler().getItemDef(i);
            if (def.getName().equalsIgnoreCase(poisonedVersion) || def.getName().equalsIgnoreCase(poisonedVersion2)) {
                return new Item(i);
            }
        }
        return null;
    }
}

