package com.openrsc.server.plugins.minigames.gnomerestaurant;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;
import java.util.concurrent.Callable;


public class GnomeCooking implements InvActionListener , InvUseOnObjectListener , InvActionExecutiveListener , InvUseOnObjectExecutiveListener {
    private boolean canCook(Item item, GameObject object) {
        for (GnomeCooking.GnomeCook c : GnomeCooking.GnomeCook.values()) {
            if ((item.getID() == c.uncookedID) && Functions.inArray(object.getID(), 119)) {
                return true;
            }
        }
        return false;
    }

    private void handleGnomeCooking(final Item item, Player p, final GameObject object) {
        GnomeCooking.GnomeCook gc = null;
        for (GnomeCooking.GnomeCook c : GnomeCooking.GnomeCook.values()) {
            if ((item.getID() == c.uncookedID) && Functions.inArray(object.getID(), 119)) {
                gc = c;
            }
        }
        // NOTE: THERE ARE NO REQUIREMENT TO COOK THE DOUGH ONLY TO MOULD IT.
        p.setBusy(true);
        Functions.showBubble(p, item);
        p.playSound("cooking");
        if (p.getInventory().remove(item) > (-1)) {
            Functions.___message(p, 3000, gc.messages[0]);
            if (!burnFood(p, gc.requiredLevel, p.getSkills().getLevel(Skills.COOKING))) {
                if (Functions.inArray(item.getID(), ItemId.GNOMEBATTA_DOUGH.id(), ItemId.GNOMEBOWL_DOUGH.id(), ItemId.GNOMECRUNCHIE_DOUGH.id())) {
                    p.message(gc.messages[1]);
                    p.incExp(Skills.COOKING, gc.experience, true);
                } else
                    if (gc.cookedID == ItemId.GNOMEBATTA.id()) {
                        // stop tomato cheese batta when doing veg batta.
                        if ((p.getCache().hasKey("cheese_on_batta") && p.getCache().hasKey("tomato_on_batta")) && (!p.getCache().hasKey("onion_on_batta"))) {
                            p.getCache().store("tomato_cheese_batta", true);
                            p.message(gc.messages[1]);
                            p.incExp(Skills.COOKING, gc.experience, true);
                        } else
                            if (((p.getCache().hasKey("cheese_on_batta") && p.getCache().hasKey("aqua_toad_legs")) && p.getCache().hasKey("gnomespice_toad_legs")) && p.getCache().hasKey("toadlegs_on_batta")) {
                                // Makes toad batta
                                p.message(gc.messages[1]);
                                p.incExp(Skills.COOKING, gc.experience, true);
                                Functions.addItem(p, ItemId.TOAD_BATTA.id(), 1);
                                Functions.resetGnomeCooking(p);
                                return;
                            } else
                                if ((p.getCache().hasKey("gnomespice_on_worm") && p.getCache().hasKey("worm_on_batta")) && p.getCache().hasKey("cheese_on_batta")) {
                                    // makes worm batta
                                    p.getCache().store("worm_batta", true);
                                    p.message(gc.messages[1]);
                                } else
                                    if ((((p.getCache().hasKey("onion_on_batta") && p.getCache().hasKey("tomato_on_batta")) && (p.getCache().getInt("tomato_on_batta") >= 2)) && p.getCache().hasKey("cabbage_on_batta")) && p.getCache().hasKey("dwell_on_batta")) {
                                        // makes veg batta
                                        if (!p.getCache().hasKey("cheese_on_batta")) {
                                            p.getCache().store("veg_batta_no_cheese", true);
                                        } else {
                                            p.getCache().store("veg_batta_with_cheese", true);
                                        }
                                        p.message(gc.messages[1]);
                                    } else
                                        if (p.getCache().hasKey("leaves_on_batta") && (p.getCache().getInt("leaves_on_batta") >= 4)) {
                                            // makes fruit batta
                                            p.getCache().store("batta_cooked_leaves", true);
                                            p.message(gc.messages[1]);
                                        }




                    } else
                        if (gc.cookedID == ItemId.GNOMEBOWL.id()) {
                            p.message(gc.messages[1]);
                            if ((p.getCache().hasKey("chocolate_on_bowl") && (p.getCache().getInt("chocolate_on_bowl") >= 4)) && p.getCache().hasKey("leaves_on_bowl")) {
                                p.getCache().store("chocolate_bomb", true);
                            } else
                                if ((((p.getCache().hasKey("kingworms_on_bowl") && (p.getCache().getInt("kingworms_on_bowl") >= 6)) && p.getCache().hasKey("onions_on_bowl")) && (p.getCache().getInt("onions_on_bowl") >= 2)) && p.getCache().hasKey("gnomespice_on_bowl")) {
                                    p.getCache().store("wormhole", true);
                                    p.incExp(Skills.COOKING, gc.experience, true);
                                } else
                                    if ((((p.getCache().hasKey("onions_on_bowl") && (p.getCache().getInt("onions_on_bowl") >= 2)) && p.getCache().hasKey("potato_on_bowl")) && (p.getCache().getInt("potato_on_bowl") >= 2)) && p.getCache().hasKey("gnomespice_on_bowl")) {
                                        p.getCache().store("vegball", true);
                                        p.incExp(Skills.COOKING, gc.experience, true);
                                    } else
                                        if ((((((((p.getCache().hasKey("cheese_on_bowl") && (p.getCache().getInt("cheese_on_bowl") >= 2)) && p.getCache().hasKey("toadlegs_on_bowl")) && (p.getCache().getInt("toadlegs_on_bowl") >= 5)) && p.getCache().hasKey("leaves_on_bowl")) && (p.getCache().getInt("leaves_on_bowl") >= 2)) && p.getCache().hasKey("dwell_on_bowl")) && p.getCache().hasKey("gnomespice_on_bowl")) && (p.getCache().getInt("gnomespice_on_bowl") >= 2)) {
                                            // tangled toads legs
                                            p.incExp(Skills.COOKING, gc.experience, true);
                                            Functions.addItem(p, ItemId.TANGLED_TOADS_LEGS.id(), 1);
                                            Functions.resetGnomeCooking(p);
                                            return;
                                        }



                        } else
                            if (gc.cookedID == ItemId.GNOMECRUNCHIE.id()) {
                                p.message(gc.messages[1]);
                                if (!p.getCache().hasKey("gnome_crunchie_cooked")) {
                                    p.getCache().store("gnome_crunchie_cooked", true);
                                }
                            } else {
                                p.message(gc.messages[1]);
                                p.incExp(Skills.COOKING, gc.experience, true);
                            }



                Functions.addItem(p, gc.cookedID, 1);
            } else {
                p.getInventory().add(new Item(gc.burntID));
                p.message(gc.messages[2]);
            }
        }
        p.setBusy(false);
    }

    private boolean mouldDough(Item item, Player p) {
        if (((((Functions.hasItem(p, ItemId.GNOMEBATTA_DOUGH.id()) || Functions.hasItem(p, ItemId.GNOMEBOWL_DOUGH.id())) || Functions.hasItem(p, ItemId.GNOMECRUNCHIE_DOUGH.id())) || Functions.hasItem(p, ItemId.GNOMEBATTA.id())) || Functions.hasItem(p, ItemId.GNOMEBOWL.id())) || Functions.hasItem(p, ItemId.GNOMECRUNCHIE.id())) {
            Functions.___message(p, "you need to finish, eat or drop the unfinished dish you hold");
            p.message("before you can make another - giannes rules");
            return false;
        }
        p.message("which shape would you like to mould");
        int menu = Functions.___showMenu(p, "gnomebatta", "gnomebowl", "gnomecrunchie");
        if (menu != (-1)) {
            p.setOption(-1);
            p.setBusy(true);
            if (menu == 0) {
                if (p.getSkills().getLevel(Skills.COOKING) < 25) {
                    p.message("you need a cooking level of 25 to mould dough batta's");
                    p.setBusy(false);
                    return false;
                }
                Functions.showBubble(p, item);
                Functions.___message(p, 3000, "you attempt to mould the dough into a gnomebatta");
                p.message("You manage to make some gnome batta dough");
                p.getInventory().replace(item.getID(), ItemId.GNOMEBATTA_DOUGH.id());
            } else
                if (menu == 1) {
                    if (p.getSkills().getLevel(Skills.COOKING) < 30) {
                        p.message("you need a cooking level of 30 to mould dough bowls");
                        p.setBusy(false);
                        return false;
                    }
                    Functions.showBubble(p, item);
                    Functions.___message(p, 3000, "you attempt to mould the dough into a gnome bowl");
                    p.message("You manage to make some gnome bowl dough");
                    p.getInventory().replace(item.getID(), ItemId.GNOMEBOWL_DOUGH.id());
                } else
                    if (menu == 2) {
                        if (p.getSkills().getLevel(Skills.COOKING) < 15) {
                            p.message("you need a cooking level of 15 to mould crunchies");
                            p.setBusy(false);
                            return false;
                        }
                        Functions.showBubble(p, item);
                        Functions.___message(p, 3000, "you attempt to mould the dough into gnome crunchies");
                        p.message("You manage to make some gnome crunchies dough");
                        p.getInventory().replace(item.getID(), ItemId.GNOMECRUNCHIE_DOUGH.id());
                        if (!p.getCache().hasKey("gnomecrunchie_dough")) {
                            p.getCache().store("gnomecrunchie_dough", true);
                        }
                    }


            p.incExp(Skills.COOKING, 100, true);
            p.setBusy(false);
        }
        return true;
    }

    @Override
    public GameStateEvent onInvAction(Item item, Player p, String command) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (item.getID() == ItemId.GIANNE_DOUGH.id()) {
                        mouldDough(item, p);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvAction(Item item, Player p, String command) {
        return item.getID() == ItemId.GIANNE_DOUGH.id();
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return canCook(item, obj);
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    handleGnomeCooking(item, p, obj);
                    return null;
                });
            }
        };
    }

    private boolean burnFood(Player p, int reqLvl, int myCookingLvl) {
        return Formulae.burnFood(p, ItemId.GIANNE_DOUGH.id(), myCookingLvl);
    }

    enum GnomeCook {

        GNOME_BATTA_DOUGH(ItemId.GNOMEBATTA_DOUGH.id(), ItemId.GNOMEBATTA.id(), ItemId.BURNT_GNOMEBATTA.id(), 120, 1, "You cook the gnome batta in the oven...", "You remove the gnome batta from the oven", "You accidentally burn the gnome batta"),
        GNOME_BOWL_DOUGH(ItemId.GNOMEBOWL_DOUGH.id(), ItemId.GNOMEBOWL.id(), ItemId.BURNT_GNOMEBOWL.id(), 120, 1, "You cook the gnome bowl in the oven...", "You remove the gnome bowl from the oven", "You accidentally burn the gnome bbowl"),
        GNOME_CRUNCHIE_DOUGH(ItemId.GNOMECRUNCHIE_DOUGH.id(), ItemId.GNOMECRUNCHIE.id(), ItemId.BURNT_GNOMECRUNCHIE.id(), 120, 1, "You cook the gnome crunchie in the oven...", "You remove the gnome crunchie from the oven", "You accidentally burn the gnome crunchie"),
        GNOME_BATTA_ALREADY_COOKED(ItemId.GNOMEBATTA.id(), ItemId.GNOMEBATTA.id(), ItemId.BURNT_GNOMEBATTA.id(), 120, 1, "You cook the gnome batta in the oven...", "You remove the gnome batta from the oven", "You accidentally burn the gnome batta"),
        GNOME_BOWL_ALREADY_COOKED(ItemId.GNOMEBOWL.id(), ItemId.GNOMEBOWL.id(), ItemId.BURNT_GNOMEBOWL.id(), 120, 1, "You cook the gnome bowl in the oven...", "You remove the gnome bowl from the oven", "You accidentally burn the gnome bbowl");
        private int uncookedID;

        private int cookedID;

        private int burntID;

        private int experience;

        private int requiredLevel;

        private String[] messages;

        GnomeCook(int uncookedID, int cookedID, int burntID, int experience, int reqlevel, String... cookingMessages) {
            this.uncookedID = uncookedID;
            this.cookedID = cookedID;
            this.burntID = burntID;
            this.experience = experience;
            this.requiredLevel = reqlevel;
            this.messages = cookingMessages;
        }
    }
}

