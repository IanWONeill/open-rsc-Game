package com.openrsc.server.plugins.minigames.fishingtrawler;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import java.util.concurrent.Callable;


public class TrawlerCatch implements ObjectActionListener , ObjectActionExecutiveListener {
    private static final int TRAWLER_CATCH = 1106;

    private static final int[] JUNK_ITEMS = new int[]{ ItemId.OLD_BOOT.id(), ItemId.DAMAGED_ARMOUR_1.id(), ItemId.DAMAGED_ARMOUR_2.id(), ItemId.RUSTY_SWORD.id(), ItemId.BROKEN_ARROW.id(), ItemId.BUTTONS.id(), ItemId.BROKEN_STAFF.id(), ItemId.VASE.id(), ItemId.CERAMIC_REMAINS.id(), ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id()// Broken glass
    , ItemId.EDIBLE_SEAWEED.id(), ItemId.OYSTER.id() };

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == TrawlerCatch.TRAWLER_CATCH;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == TrawlerCatch.TRAWLER_CATCH) {
                        Functions.___message(p, 1900, "you search the smelly net");
                        Functions.showBubble(p, new Item(ItemId.NET.id()));
                        if (p.getCache().hasKey("fishing_trawler_reward")) {
                            p.message("you find...");
                            int fishCaught = p.getCache().getInt("fishing_trawler_reward");
                            boolean isFishRoll;
                            for (int fishGiven = 0; fishGiven < fishCaught; fishGiven++) {
                                isFishRoll = DataConversions.random(0, 1) == 1;
                                // roll for a fish
                                if (isFishRoll) {
                                    if (catchFish(81, p.getSkills().getLevel(Skills.FISHING))) {
                                        Functions.___message(p, 1200, "..a manta ray!");
                                        Functions.addItem(p, ItemId.RAW_MANTA_RAY.id(), 1);
                                        p.incExp(Skills.FISHING, 460, false);
                                    } else
                                        if (catchFish(79, p.getSkills().getLevel(Skills.FISHING))) {
                                            Functions.___message(p, 1200, "..a sea turtle!");
                                            Functions.addItem(p, ItemId.RAW_SEA_TURTLE.id(), 1);
                                            p.incExp(Skills.FISHING, 380, false);
                                        } else
                                            if (catchFish(76, p.getSkills().getLevel(Skills.FISHING))) {
                                                Functions.___message(p, 1200, "..a shark!");
                                                Functions.addItem(p, ItemId.RAW_SHARK.id(), 1);
                                                p.incExp(Skills.FISHING, 440, false);
                                            } else
                                                if (catchFish(50, p.getSkills().getLevel(Skills.FISHING))) {
                                                    Functions.___message(p, 1200, "..a sword fish");
                                                    Functions.addItem(p, ItemId.RAW_SWORDFISH.id(), 1);
                                                    p.incExp(Skills.FISHING, 400, false);
                                                } else
                                                    if (catchFish(40, p.getSkills().getLevel(Skills.FISHING))) {
                                                        Functions.___message(p, 1200, "..a lobster");
                                                        Functions.addItem(p, ItemId.RAW_LOBSTER.id(), 1);
                                                        p.incExp(Skills.FISHING, 360, false);
                                                    } else
                                                        if (catchFish(30, p.getSkills().getLevel(Skills.FISHING))) {
                                                            Functions.___message(p, 1200, "..some tuna");
                                                            Functions.addItem(p, ItemId.RAW_TUNA.id(), 1);
                                                            p.incExp(Skills.FISHING, 320, false);
                                                        } else
                                                            if (catchFish(15, p.getSkills().getLevel(Skills.FISHING))) {
                                                                Functions.___message(p, 1200, "..some anchovies");
                                                                Functions.addItem(p, ItemId.RAW_ANCHOVIES.id(), 1);
                                                                p.incExp(Skills.FISHING, 160, false);
                                                            } else
                                                                if (catchFish(5, p.getSkills().getLevel(Skills.FISHING))) {
                                                                    Functions.___message(p, 1200, "..a sardine");
                                                                    Functions.addItem(p, ItemId.RAW_SARDINE.id(), 1);
                                                                    p.incExp(Skills.FISHING, 80, false);
                                                                } else {
                                                                    Functions.___message(p, 1200, "..some shrimp");
                                                                    Functions.addItem(p, ItemId.RAW_SHRIMP.id(), 1);
                                                                    p.incExp(Skills.FISHING, 40, false);
                                                                }







                                } else {
                                    int randomJunkItem = TrawlerCatch.JUNK_ITEMS[DataConversions.random(0, TrawlerCatch.JUNK_ITEMS.length - 1)];
                                    if (randomJunkItem == ItemId.EDIBLE_SEAWEED.id()) {
                                        // Edible seaweed
                                        Functions.___message(p, 1200, "..some seaweed");
                                        Functions.addItem(p, ItemId.EDIBLE_SEAWEED.id(), 1);
                                        p.incExp(Skills.FISHING, 20, false);
                                    } else
                                        if (randomJunkItem == ItemId.OYSTER.id()) {
                                            // Oyster
                                            Functions.___message(p, 1200, "..an oyster!");
                                            Functions.addItem(p, ItemId.OYSTER.id(), 1);
                                            p.incExp(Skills.FISHING, 40, false);
                                        } else {
                                            // Broken glass, buttons, damaged armour, ceramic remains
                                            if (((((randomJunkItem == ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id()) || (randomJunkItem == ItemId.BUTTONS.id())) || (randomJunkItem == ItemId.DAMAGED_ARMOUR_1.id())) || (randomJunkItem == ItemId.DAMAGED_ARMOUR_2.id())) || (randomJunkItem == ItemId.CERAMIC_REMAINS.id())) {
                                                Functions.___message(p, 1200, "..some " + p.getWorld().getServer().getEntityHandler().getItemDef(randomJunkItem).getName());
                                            } else// Old boot

                                                if (randomJunkItem == ItemId.OLD_BOOT.id()) {
                                                    Functions.___message(p, 1200, "..an " + p.getWorld().getServer().getEntityHandler().getItemDef(randomJunkItem).getName());
                                                } else // broken arrow, broken staff, Rusty sword, vase
                                                {
                                                    Functions.___message(p, 1200, "..a " + p.getWorld().getServer().getEntityHandler().getItemDef(randomJunkItem).getName());
                                                }

                                            Functions.addItem(p, randomJunkItem, 1);
                                            p.incExp(Skills.FISHING, 5, false);
                                        }

                                }
                            }
                            p.getCache().remove("fishing_trawler_reward");
                            p.message("that's the lot");
                        } else {
                            p.message("the smelly net is empty");
                        }
                    }
                    return null;
                });
            }
        };
    }

    private boolean catchFish(int levelReq, int level) {
        return Formulae.calcGatheringSuccessful(levelReq, level);
    }
}

