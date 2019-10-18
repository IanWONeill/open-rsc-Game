package com.openrsc.server.plugins.npcs.shilo;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class CartDriver implements ObjectActionListener , TalkToNpcListener , ObjectActionExecutiveListener , TalkToNpcExecutiveListener {
    public static final int TRAVEL_CART = 768;

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.CART_DRIVER_SHILO.id();
    }

    private void cartRide(Player p, Npc n) {
        Functions.___npcTalk(p, n, "I am offering a cart ride to Brimhaven if you're interested!", "It will cost 500 Gold");
        int menu = // do not send over
        Functions.___showMenu(p, n, false, "Yes, that sounds great!", "No thanks.");
        if (menu == 0) {
            Functions.___playerTalk(p, n, "Yes please, I'd like to go to Brimhaven!");
            if (Functions.hasItem(p, ItemId.COINS.id(), 500)) {
                Functions.___npcTalk(p, n, "Great!", "Just hop into the cart then and we'll go!");
                Functions.removeItem(p, ItemId.COINS.id(), 500);
                Functions.___message(p, 1000, "You Hop into the cart and the driver urges the horses on.");
                p.teleport(468, 662);
                Functions.___message(p, 1200, "You take a taxing journey through the jungle to Brimhaven.");
                Functions.___message(p, 1200, "You feel fatigued from the journey, but at least");
                Functions.___message(p, 1200, "you didn't have to walk all that distance.");
            } else {
                Functions.___npcTalk(p, n, "Sorry, but it looks as if you don't have enough money.", "Come back and see me when you have enough for the ride.");
            }
        } else
            if (menu == 1) {
                Functions.___playerTalk(p, n, "No thanks.");
                Functions.___npcTalk(p, n, "Ok Bwana, let me know if you change your mind.");
            }

    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.CART_DRIVER_SHILO.id()) {
                        Functions.___playerTalk(p, n, "Hello!");
                        Functions.___npcTalk(p, n, "Hello Bwana!");
                        cartRide(p, n);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == CartDriver.TRAVEL_CART;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == CartDriver.TRAVEL_CART) {
                        if (command.equalsIgnoreCase("Board")) {
                            p.message("This looks like a sturdy travelling cart.");
                            Npc driver = Functions.getNearestNpc(p, NpcId.CART_DRIVER_SHILO.id(), 10);
                            if (driver != null) {
                                driver.teleport(p.getX(), p.getY());
                                Functions.sleep(600);// 1 tick.

                                Functions.npcWalkFromPlayer(p, driver);
                                p.message("A nearby man walks over to you.");
                                cartRide(p, driver);
                            } else {
                                p.message("The cart driver is currently busy.");
                            }
                        } else
                            if (command.equalsIgnoreCase("Look")) {
                                p.message("A sturdy travelling cart built for long trips through jungle areas.");
                            }

                    }
                    return null;
                });
            }
        };
    }
}

