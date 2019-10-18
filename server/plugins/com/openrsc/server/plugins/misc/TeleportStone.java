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
import java.util.concurrent.Callable;


public class TeleportStone implements InvActionListener , InvActionExecutiveListener {
    // Pretty sure this item doesn't even exist.
    private final int TELEPORT_STONE = 2107;

    @Override
    public boolean blockInvAction(Item item, Player p, String command) {
        return item.getID() == TELEPORT_STONE;
    }

    @Override
    public GameStateEvent onInvAction(Item item, Player p, String command) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (item.getID() == TELEPORT_STONE) {
                        Functions.___message(p, "the stone starts shaking...");
                        p.message("a magical portal opens up, where would you like to go?");
                        String[] teleLoc = new String[]{ "Lumbridge", "Draynor", "Falador", "Edgeville", "Varrock", "Alkharid", "Karamja", "Yanille", "Ardougne", "Catherby", "Seers", "Gnome Stronghold", "Stay here" };
                        int menu = Functions.___showMenu(p, teleLoc);
                        // if (p.getLocation().inWilderness() && System.currentTimeMillis() - p.getCombatTimer() < 10000) {
                        // p.message("You need to stay out of combat for 10 seconds before using a teleport.");
                        // return;
                        // }
                        if ((((p.getLocation().wildernessLevel() >= 30) || p.getLocation().isInFisherKingRealm()) || p.getLocation().isInsideGrandTreeGround()) || (p.getLocation().inModRoom() && (!p.isAdmin()))) {
                            p.message("A mysterious force blocks your teleport!");
                            p.message("You can't use this teleport after level 30 wilderness");
                            return null;
                        }
                        if (p.getInventory().countId(ItemId.ANA_IN_A_BARREL.id()) > 0) {
                            Functions.___message(p, "You can't teleport while holding Ana,", "It's just too difficult to concentrate.");
                            return null;
                        }
                        if (p.getInventory().hasItemId(ItemId.PLAGUE_SAMPLE.id())) {
                            p.message("the plague sample is too delicate...");
                            p.message("it disintegrates in the crossing");
                            while (p.getInventory().countId(ItemId.PLAGUE_SAMPLE.id()) > 0) {
                                p.getInventory().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
                            } 
                        }
                        switch (menu) {
                            case -1 :
                                // stop them.
                                return null;
                            case 0 :
                                // lumb
                                p.teleport(125, 648);
                                break;
                            case 1 :
                                // dray
                                p.teleport(214, 632);
                                break;
                            case 2 :
                                // falla
                                p.teleport(304, 542);
                                break;
                            case 3 :
                                // edge
                                p.teleport(223, 447);
                                break;
                            case 4 :
                                // varrock
                                p.teleport(122, 509);
                                break;
                            case 5 :
                                // alkharid
                                p.teleport(85, 691);
                                break;
                            case 6 :
                                // Karamja
                                p.teleport(372, 706);
                                break;
                            case 7 :
                                // Yanille
                                p.teleport(583, 747);
                                break;
                            case 8 :
                                // Ardougne
                                p.teleport(557, 606);
                                break;
                            case 9 :
                                // Catherby
                                p.teleport(442, 503);
                                break;
                            case 10 :
                                // Seers
                                p.teleport(493, 456);
                                break;
                            case 11 :
                                // Gnome Stronghold
                                p.teleport(703, 481);
                                break;
                            case 12 :
                                return null;
                        }
                        Functions.removeItem(p, TELEPORT_STONE, 1);
                        Functions.sleep(650);
                        p.message("You landed in " + teleLoc[menu]);
                    }
                    return null;
                });
            }
        };
    }
}

