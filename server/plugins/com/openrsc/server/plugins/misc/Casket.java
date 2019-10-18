package com.openrsc.server.plugins.misc;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


public class Casket implements InvActionListener , InvActionExecutiveListener {
    @Override
    public boolean blockInvAction(Item item, Player p, String command) {
        return item.getID() == ItemId.CASKET.id();
    }

    @Override
    public GameStateEvent onInvAction(Item item, Player p, String command) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (item.getID() == ItemId.CASKET.id()) {
                        getPlayerOwner().setBusy(true);
                        getPlayerOwner().message("you open the casket");
                        return invokeNextState(3);
                    }
                    return null;
                });
                addState(1, () -> {
                    int randomChanceOpen = DataConversions.random(0, 1081);
                    getPlayerOwner().playerServerMessage(MessageType.QUEST, "you find some treasure inside!");
                    Functions.removeItem(getPlayerOwner(), ItemId.CASKET.id(), 1);
                    getPlayerOwner().setBusy(false);
                    // Coins, 54.11% chance
                    if (randomChanceOpen <= 585) {
                        // Randomly gives different coin amounts
                        int randomChanceCoin = DataConversions.random(0, 6);
                        if (randomChanceCoin == 0) {
                            Functions.addItem(getPlayerOwner(), ItemId.COINS.id(), 10);
                        } else
                            if (randomChanceCoin == 1) {
                                Functions.addItem(getPlayerOwner(), ItemId.COINS.id(), 20);
                            } else
                                if (randomChanceCoin == 2) {
                                    Functions.addItem(getPlayerOwner(), ItemId.COINS.id(), 40);
                                } else
                                    if (randomChanceCoin == 3) {
                                        Functions.addItem(getPlayerOwner(), ItemId.COINS.id(), 80);
                                    } else
                                        if (randomChanceCoin == 4) {
                                            Functions.addItem(getPlayerOwner(), ItemId.COINS.id(), 160);
                                        } else
                                            if (randomChanceCoin == 5) {
                                                Functions.addItem(getPlayerOwner(), ItemId.COINS.id(), 320);
                                            } else {
                                                Functions.addItem(getPlayerOwner(), ItemId.COINS.id(), 640);
                                            }





                    } else
                        if (randomChanceOpen <= 859) {
                            // Uncut sapphire, 25.34% chance
                            Functions.addItem(getPlayerOwner(), ItemId.UNCUT_SAPPHIRE.id(), 1);
                        } else
                            if (randomChanceOpen <= 990) {
                                // Uncut emerald, 12.11% chance
                                Functions.addItem(getPlayerOwner(), ItemId.UNCUT_EMERALD.id(), 1);
                            } else
                                if (randomChanceOpen <= 1047) {
                                    // Uncut ruby, 5.27% chance
                                    Functions.addItem(getPlayerOwner(), ItemId.UNCUT_RUBY.id(), 1);
                                } else
                                    if (randomChanceOpen <= 1064) {
                                        // Uncut diamond, 1.57% chance
                                        Functions.addItem(getPlayerOwner(), ItemId.UNCUT_DIAMOND.id(), 1);
                                    } else {
                                        // Tooth halves, 1.56% chance
                                        // Randomly give one part or the other
                                        int randomChanceKey = DataConversions.random(0, 1);
                                        if (randomChanceKey == 0) {
                                            Functions.addItem(getPlayerOwner(), ItemId.TOOTH_KEY_HALF.id(), 1);
                                        } else {
                                            Functions.addItem(getPlayerOwner(), ItemId.LOOP_KEY_HALF.id(), 1);
                                        }
                                    }




                    return null;
                });
            }
        };
    }
}

