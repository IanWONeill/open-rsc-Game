package com.openrsc.server.plugins.itemactions;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnPlayerListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnPlayerExecutiveListener;
import java.util.concurrent.Callable;


public class IronManInvUseOnPlayer implements InvUseOnPlayerListener , InvUseOnPlayerExecutiveListener {
    @Override
    public boolean blockInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
        if ((item.getID() == ItemId.BROKEN_SHIELD_ARRAV_1.id()) || (item.getID() == ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
            return true;
        }
        if (item.getID() == ItemId.PHOENIX_GANG_WEAPON_KEY.id()) {
            return true;
        }
        if (item.getID() == ItemId.CERTIFICATE.id()) {
            return true;
        }
        if (item.getID() == ItemId.CANDLESTICK.id()) {
            return true;
        }
        if (item.getID() == ItemId.MISCELLANEOUS_KEY.id()) {
            return true;
        }
        return false;
    }

    @Override
    public GameStateEvent onInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((((((item.getID() == ItemId.MISCELLANEOUS_KEY.id()) || (item.getID() == ItemId.CANDLESTICK.id())) || (item.getID() == ItemId.CERTIFICATE.id())) || (item.getID() == ItemId.BROKEN_SHIELD_ARRAV_1.id())) || (item.getID() == ItemId.BROKEN_SHIELD_ARRAV_2.id())) || (item.getID() == ItemId.PHOENIX_GANG_WEAPON_KEY.id())) {
                        if (otherPlayer.isBusy() || player.isBusy()) {
                            return null;
                        }
                        if (otherPlayer.getInventory().full()) {
                            player.message("Other player doesn't have enough inventory space to receive the object");
                            return null;
                        }
                        player.resetPath();
                        otherPlayer.resetPath();
                        Functions.removeItem(player, item.getID(), 1);
                        Functions.addItem(otherPlayer, item.getID(), 1);
                        Functions.___message(player, 0, (("You give the " + item.getDef(player.getWorld()).getName()) + " to ") + otherPlayer.getUsername());
                        Functions.___message(otherPlayer, 0, (player.getUsername() + " has given you a ") + item.getDef(player.getWorld()).getName());
                    }
                    return null;
                });
            }
        };
    }
}

