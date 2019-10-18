package com.openrsc.server.plugins.itemactions;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import java.util.concurrent.Callable;


public class CombinePotions implements InvUseOnItemListener , InvUseOnItemExecutiveListener {
    int[][] combinePotions = new int[][]{ new int[]{ ItemId.ONE_ATTACK_POTION.id(), ItemId.TWO_ATTACK_POTION.id(), ItemId.FULL_ATTACK_POTION.id() }, new int[]{ ItemId.ONE_STAT_RESTORATION_POTION.id(), ItemId.TWO_STAT_RESTORATION_POTION.id(), ItemId.FULL_STAT_RESTORATION_POTION.id() }, new int[]{ ItemId.ONE_DEFENSE_POTION.id(), ItemId.TWO_DEFENSE_POTION.id(), ItemId.FULL_DEFENSE_POTION.id() }, new int[]{ ItemId.ONE_RESTORE_PRAYER_POTION.id(), ItemId.TWO_RESTORE_PRAYER_POTION.id(), ItemId.FULL_RESTORE_PRAYER_POTION.id() }, new int[]{ ItemId.ONE_SUPER_ATTACK_POTION.id(), ItemId.TWO_SUPER_ATTACK_POTION.id(), ItemId.FULL_SUPER_ATTACK_POTION.id() }, new int[]{ ItemId.ONE_FISHING_POTION.id(), ItemId.TWO_FISHING_POTION.id(), ItemId.FULL_FISHING_POTION.id() }, new int[]{ ItemId.ONE_SUPER_STRENGTH_POTION.id(), ItemId.TWO_SUPER_STRENGTH_POTION.id(), ItemId.FULL_SUPER_STRENGTH_POTION.id() }, new int[]{ ItemId.ONE_SUPER_DEFENSE_POTION.id(), ItemId.TWO_SUPER_DEFENSE_POTION.id(), ItemId.FULL_SUPER_DEFENSE_POTION.id() }, new int[]{ ItemId.ONE_RANGING_POTION.id(), ItemId.TWO_RANGING_POTION.id(), ItemId.FULL_RANGING_POTION.id() }, new int[]{ ItemId.ONE_CURE_POISON_POTION.id(), ItemId.TWO_CURE_POISON_POTION.id(), ItemId.FULL_CURE_POISON_POTION.id() }, new int[]{ ItemId.ONE_POTION_OF_ZAMORAK.id(), ItemId.TWO_POTION_OF_ZAMORAK.id(), ItemId.FULL_POTION_OF_ZAMORAK.id() } };

    @Override
    public GameStateEvent onInvUseOnItem(Player p, Item item1, Item item2) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    // No Decanting without the config set to true!
                    if (!p.getWorld().getServer().getConfig().WANT_DECANTING) {
                        p.message("Nothing interesting happens");
                        return null;
                    }
                    /**
                     * Regular Strength Potions *
                     */
                    // 1 dose on 2 dose str = 3 dose
                    if (((item1.getID() == ItemId.ONE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.TWO_STRENGTH_POTION.id())) || ((item1.getID() == ItemId.TWO_STRENGTH_POTION.id()) && (item2.getID() == ItemId.ONE_STRENGTH_POTION.id()))) {
                        if ((p.getInventory().remove(new Item(ItemId.ONE_STRENGTH_POTION.id())) > (-1)) && (p.getInventory().remove(new Item(ItemId.TWO_STRENGTH_POTION.id())) > (-1))) {
                            Functions.addItem(p, ItemId.THREE_STRENGTH_POTION.id(), 1);
                            p.message((("You combine 2 doses of " + item1.getDef(p.getWorld()).getName().toLowerCase()) + " with 1 dose of ") + item2.getDef(p.getWorld()).getName().toLowerCase());
                            Functions.addItem(p, ItemId.EMPTY_VIAL.id(), 1);// give 1 empty vial.

                        }
                    } else// 1 dose on 3 dose = 4 dose

                        if (((item1.getID() == ItemId.ONE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.THREE_STRENGTH_POTION.id())) || ((item1.getID() == ItemId.THREE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.ONE_STRENGTH_POTION.id()))) {
                            if ((p.getInventory().remove(new Item(ItemId.ONE_STRENGTH_POTION.id())) > (-1)) && (p.getInventory().remove(new Item(ItemId.THREE_STRENGTH_POTION.id())) > (-1))) {
                                Functions.addItem(p, ItemId.FULL_STRENGTH_POTION.id(), 1);
                                p.message((("You combine 3 doses of " + item1.getDef(p.getWorld()).getName().toLowerCase()) + " with 1 dose of ") + item2.getDef(p.getWorld()).getName().toLowerCase());
                                Functions.addItem(p, ItemId.EMPTY_VIAL.id(), 1);// give 1 empty vial.

                            }
                        } else// 2 dose on 2 dose = 4 dose

                            if ((item1.getID() == ItemId.TWO_STRENGTH_POTION.id()) && (item2.getID() == ItemId.TWO_STRENGTH_POTION.id())) {
                                if ((p.getInventory().remove(new Item(ItemId.TWO_STRENGTH_POTION.id())) > (-1)) && (p.getInventory().remove(new Item(ItemId.TWO_STRENGTH_POTION.id())) > (-1))) {
                                    Functions.addItem(p, ItemId.FULL_STRENGTH_POTION.id(), 1);
                                    p.message("You combine two 2 doses of " + item1.getDef(p.getWorld()).getName().toLowerCase());
                                    Functions.addItem(p, ItemId.EMPTY_VIAL.id(), 1);// give 1 empty vial.

                                }
                            } else// 1 dose on 1 dose = 2 dose

                                if ((item1.getID() == ItemId.ONE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.ONE_STRENGTH_POTION.id())) {
                                    if ((p.getInventory().remove(new Item(ItemId.ONE_STRENGTH_POTION.id())) > (-1)) && (p.getInventory().remove(new Item(ItemId.ONE_STRENGTH_POTION.id())) > (-1))) {
                                        Functions.addItem(p, ItemId.TWO_STRENGTH_POTION.id(), 1);
                                        p.message((("You combine 1 dose of " + item1.getDef(p.getWorld()).getName().toLowerCase()) + " with 1 dose of ") + item2.getDef(p.getWorld()).getName().toLowerCase());
                                        Functions.addItem(p, ItemId.EMPTY_VIAL.id(), 1);// give 1 empty vial.

                                    }
                                } else// 3 dose on 3 dose = 6 dose (one 4 dose full pot, one 2 dose pot)

                                    if ((item1.getID() == ItemId.THREE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.THREE_STRENGTH_POTION.id())) {
                                        if ((p.getInventory().remove(new Item(ItemId.THREE_STRENGTH_POTION.id())) > (-1)) && (p.getInventory().remove(new Item(ItemId.THREE_STRENGTH_POTION.id())) > (-1))) {
                                            Functions.addItem(p, ItemId.FULL_STRENGTH_POTION.id(), 1);// 4 dose

                                            Functions.addItem(p, ItemId.TWO_STRENGTH_POTION.id(), 1);// 2 dose

                                            p.message("You combine two 3 doses of " + item1.getDef(p.getWorld()).getName().toLowerCase());
                                        }
                                    } else /**
                                     * Rest of the potions in the game *
                                     */
                                    {
                                        for (int i = 0; i < combinePotions.length; i++) {
                                            /**
                                             * 1 dose with 2 dose. *
                                             */
                                            if (((item1.getID() == combinePotions[i][0]) && (item2.getID() == combinePotions[i][1])) || ((item2.getID() == combinePotions[i][0]) && (item1.getID() == combinePotions[i][1]))) {
                                                if ((p.getInventory().remove(new Item(combinePotions[i][0])) > (-1)) && (p.getInventory().remove(new Item(combinePotions[i][1])) > (-1))) {
                                                    p.message((("You combine 2 doses of " + item1.getDef(p.getWorld()).getName().toLowerCase()) + " with 1 dose of ") + item2.getDef(p.getWorld()).getName().toLowerCase());
                                                    p.getInventory().add(new Item(combinePotions[i][2]));// 1 full pot

                                                    p.message("to a full 3 doses of " + item1.getDef(p.getWorld()).getName().toLowerCase());
                                                    Functions.addItem(p, ItemId.EMPTY_VIAL.id(), 1);// give 1 empty vial.

                                                    p.message("you get an empty vial over");
                                                    return null;
                                                }
                                            } else/**
                                             * 1 dose with 1 dose. *
                                             */

                                                if ((item1.getID() == combinePotions[i][0]) && (item2.getID() == combinePotions[i][0])) {
                                                    if ((p.getInventory().remove(new Item(combinePotions[i][0])) > (-1)) && (p.getInventory().remove(new Item(combinePotions[i][0])) > (-1))) {
                                                        p.message("You combine two 1 dose of " + item1.getDef(p.getWorld()).getName().toLowerCase());
                                                        p.getInventory().add(new Item(combinePotions[i][1]));// 2 dose pot

                                                        p.message("to 2 doses of " + item1.getDef(p.getWorld()).getName().toLowerCase());
                                                        Functions.addItem(p, ItemId.EMPTY_VIAL.id(), 1);// give 1 empty vial.

                                                        p.message("you get an empty vial over");
                                                        return null;
                                                    }
                                                } else/**
                                                 * 2 dose with 2 dose. *
                                                 */

                                                    if ((item1.getID() == combinePotions[i][1]) && (item2.getID() == combinePotions[i][1])) {
                                                        if ((p.getInventory().remove(new Item(combinePotions[i][1])) > (-1)) && (p.getInventory().remove(new Item(combinePotions[i][1])) > (-1))) {
                                                            p.message("You combine two 2 doses of " + item1.getDef(p.getWorld()).getName().toLowerCase());
                                                            p.getInventory().add(new Item(combinePotions[i][2]));// 1 full pot

                                                            p.getInventory().add(new Item(combinePotions[i][0]));// 1 dose pot

                                                            p.message((("to a full 3 doses of " + item1.getDef(p.getWorld()).getName().toLowerCase()) + " and 1 dose of ") + item1.getDef(p.getWorld()).getName().toLowerCase());
                                                            return null;
                                                        }
                                                    }


                                        }
                                    }




                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
        // 1 dose on 2 dose str = 3 dose
        if (((item1.getID() == ItemId.ONE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.TWO_STRENGTH_POTION.id())) || ((item1.getID() == ItemId.TWO_STRENGTH_POTION.id()) && (item2.getID() == ItemId.ONE_STRENGTH_POTION.id()))) {
            return true;
        }
        // 1 dose on 3 dose = 4 dose
        if (((item1.getID() == ItemId.ONE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.THREE_STRENGTH_POTION.id())) || ((item1.getID() == ItemId.THREE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.ONE_STRENGTH_POTION.id()))) {
            return true;
        }
        // 2 dose on 2 dose = 4 dose
        if ((item1.getID() == ItemId.TWO_STRENGTH_POTION.id()) && (item2.getID() == ItemId.TWO_STRENGTH_POTION.id())) {
            return true;
        }
        // 1 dose on 1 dose = 2 dose
        if ((item1.getID() == ItemId.ONE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.ONE_STRENGTH_POTION.id())) {
            return true;
        }
        if ((item1.getID() == ItemId.THREE_STRENGTH_POTION.id()) && (item2.getID() == ItemId.THREE_STRENGTH_POTION.id())) {
            return true;
        }
        for (int i = 0; i < combinePotions.length; i++) {
            if (((item1.getID() == combinePotions[i][0]) && (item2.getID() == combinePotions[i][1])) || ((item2.getID() == combinePotions[i][0]) && (item1.getID() == combinePotions[i][1]))) {
                return true;
            }
            if ((item1.getID() == combinePotions[i][1]) && (item2.getID() == combinePotions[i][1])) {
                return true;
            }
            if ((item1.getID() == combinePotions[i][0]) && (item2.getID() == combinePotions[i][0])) {
                return true;
            }
        }
        return false;
    }
}

