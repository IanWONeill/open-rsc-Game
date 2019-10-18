package com.openrsc.server.plugins.skills;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


public final class Pick implements ObjectActionListener , ObjectActionExecutiveListener {
    @Override
    public boolean blockObjectAction(final GameObject obj, final String command, final Player player) {
        return command.equals("pick") || (obj.getID() == 313);
    }

    private void handleCropPickup(final Player player, int objID, String pickMessage) {
        int delaytime = player.getWorld().getServer().getConfig().GAME_TICK;
        if (delaytime == 600)
            delaytime = 300;
        // cabbage
        else// openrsc

            if (delaytime == 420)
                delaytime = 370;

        // cabbage

        player.setBatchEvent(new BatchEvent(player.getWorld(), player, delaytime, "Pick Vegetal", 30, true) {
            @Override
            public void action() {
                getOwner().playerServerMessage(MessageType.QUEST, pickMessage);
                Functions.addItem(getOwner(), objID, 1);
                getOwner().playSound("potato");
                if (getOwner().getInventory().full())
                    interrupt();

            }
        });
    }

    @Override
    public GameStateEvent onObjectAction(final GameObject object, final String command, final Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    switch (object.getID()) {
                        case 72 :
                            // Wheat
                            handleCropPickup(player, ItemId.GRAIN.id(), "You get some grain");
                            break;
                        case 191 :
                            // Potatos
                            handleCropPickup(player, ItemId.POTATO.id(), "You pick a potato");
                            break;
                        case 313 :
                            // Flax
                            handleCropPickup(player, ItemId.FLAX.id(), "You uproot a flax plant");
                            break;
                        default :
                            player.message("Nothing interesting happens");
                            break;
                    }
                    return null;
                });
            }
        };
    }
}

