package com.openrsc.server.plugins.misc;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class LadyOfTheWaves implements ObjectActionListener , ObjectActionExecutiveListener {
    private static final int SHIP_LADY_OF_THE_WAVES_FRONT = 780;

    private static final int SHIP_LADY_OF_THE_WAVES_BACK = 781;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return (obj.getID() == LadyOfTheWaves.SHIP_LADY_OF_THE_WAVES_FRONT) || (obj.getID() == LadyOfTheWaves.SHIP_LADY_OF_THE_WAVES_BACK);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == LadyOfTheWaves.SHIP_LADY_OF_THE_WAVES_FRONT) || (obj.getID() == LadyOfTheWaves.SHIP_LADY_OF_THE_WAVES_BACK)) {
                        p.message("This ship looks like it might take you somewhere.");
                        p.message("The captain shouts down,");
                        p.message("@yel@Captain: Where would you like to go?");
                        int menu = Functions.___showMenu(p, "Khazard Port", "Port Sarim", "No where thanks!");
                        if (menu == 0) {
                            sail(p, menu);
                        } else
                            if (menu == 1) {
                                sail(p, menu);
                            } else
                                if (menu == 2) {
                                    Functions.___playerTalk(p, null, "No where thanks!");
                                    p.message("@yel@Captain: Ok, come back if you change your mind.");
                                }


                    }
                    return null;
                });
            }
        };
    }

    private void sail(Player p, int option) {
        p.setBusy(true);
        if (Functions.hasItem(p, ItemId.SHIP_TICKET.id())) {
            Functions.removeItem(p, ItemId.SHIP_TICKET.id(), 1);
            Functions.___message(p, 1200, "@yel@Captain: Thanks for the ticket, let's set sail!");
            Functions.___message(p, 1200, "You board the ship and it sails off.");
            if (option == 0) {
                p.teleport(545, 703);
                p.message("Before you know it, you're in Khazard Port.");
                p.setBusy(false);
            } else
                if (option == 1) {
                    p.teleport(269, 640);
                    p.message("Before you know it, you're in Port Sarim.");
                    p.setBusy(false);
                }

        } else {
            Functions.___message(p, 1200, "The captain shakes his head.");
            Functions.___message(p, 1200, "@yel@Captain: Sorry Bwana, but you need a ticket!");
            Functions.___message(p, 1200, "@yel@Captain: You can get one in Shilo Village ");
            Functions.___message(p, 1200, "@yel@Captain: Just above the fishing shop. ");
            p.setBusy(false);
        }
    }
}

