package com.openrsc.server.plugins.minigames.kittencare;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.listeners.action.CatGrowthListener;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.executive.CatGrowthExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class KittenToCat implements MiniGameInterface , CatGrowthListener , DropListener , InvActionListener , InvUseOnItemListener , InvUseOnNpcListener , CatGrowthExecutiveListener , DropExecutiveListener , InvActionExecutiveListener , InvUseOnItemExecutiveListener , InvUseOnNpcExecutiveListener {
    protected static final int BASE_FACTOR = 16;

    @Override
    public int getMiniGameId() {
        return Minigames.KITTEN_CARE;
    }

    @Override
    public String getMiniGameName() {
        return "Kitten Care (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        // mini-quest complete handled already
    }

    @Override
    public boolean blockDrop(Player p, Item i) {
        return i.getID() == ItemId.KITTEN.id();
    }

    @Override
    public GameStateEvent onDrop(Player p, Item i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ItemId.KITTEN.id()) {
                        Functions.removeItem(p, ItemId.KITTEN.id(), 1);
                        Functions.___message(p, 1200, "you drop the kitten");
                        Functions.___message(p, 0, "it's upset and runs away");
                    }
                    KittenState state = new KittenState();
                    state.saveState(p);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvAction(Item item, Player p, String command) {
        return item.getID() == ItemId.KITTEN.id();
    }

    @Override
    public GameStateEvent onInvAction(Item item, Player p, String command) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (item.getID() == ItemId.KITTEN.id()) {
                        Functions.___message(p, "you softly stroke the kitten", "@yel@kitten:..purr..purr..");
                        Functions.___message(p, 600, "the kitten appreciates the attention");
                        reduceKittensLoneliness(p);
                    }
                    return null;
                });
            }
        };
    }

    public void entertainCat(Item item, Player p, boolean isGrown) {
        if (item.getID() == ItemId.BALL_OF_WOOL.id()) {
            if (!isGrown) {
                Functions.___message(p, "your kitten plays around with the ball of wool", "it seems to love pouncing on it");
                reduceKittensLoneliness(p);
            } else {
                Functions.___message(p, "your cat plays around with the wool", "it seems to be enjoying itself");
            }
        }
    }

    public void feedCat(Item item, Player p, boolean isGrown) {
        boolean feeded = false;
        switch (ItemId.getById(item.getID())) {
            case MILK :
                p.getInventory().replace(item.getID(), ItemId.BUCKET.id());
                if (!isGrown) {
                    Functions.___message(p, "you give the kitten the milk", "the kitten quickly laps it up then licks his paws");
                } else {
                    Functions.___message(p, "you give the cat the milk", "the kitten quickly laps it up then licks his paws");
                }
                feeded = true;
                break;
            case RAW_SHRIMP :
            case RAW_SARDINE :
            case SEASONED_SARDINE :
            case SARDINE :
            case RAW_HERRING :
            case RAW_ANCHOVIES :
            case RAW_TROUT :
            case TROUT :
            case RAW_SALMON :
            case RAW_TUNA :
            case TUNA :
                Functions.removeItem(p, item.getID(), 1);
                if (!isGrown) {
                    Functions.___message(p, "you give the kitten the " + item.getDef(p.getWorld()).getName(), "the kitten quickly eats it up then licks his paws");
                } else {
                    Functions.___message(p, "you give the cat the " + item.getDef(p.getWorld()).getName(), "it quickly eat's them up and licks its paws");
                }
                feeded = true;
                break;
            default :
                break;
        }
        if (feeded && (!isGrown))
            reduceKittensHunger(p);

    }

    private void reduceKittensLoneliness(Player p) {
        KittenState state = new KittenState();
        state.loadState(p);
        int loneliness = state.getLoneliness();
        if (loneliness >= KittenToCat.BASE_FACTOR) {
            state.setLoneliness(loneliness - KittenToCat.BASE_FACTOR);
            state.saveState(p);
        }
    }

    private void reduceKittensHunger(Player p) {
        KittenState state = new KittenState();
        state.loadState(p);
        int hunger = state.getHunger();
        if (hunger >= KittenToCat.BASE_FACTOR) {
            state.setHunger(hunger - KittenToCat.BASE_FACTOR);
            state.saveState(p);
        }
    }

    @Override
    public boolean blockCatGrowth(Player p) {
        return p.getInventory().hasItemId(ItemId.KITTEN.id());
    }

    @Override
    public GameStateEvent onCatGrowth(Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getInventory().hasItemId(ItemId.KITTEN.id())) {
                        // no events in memory, check in cache
                        KittenState state = new KittenState();
                        state.loadState(p);
                        int kittenHunger = state.getHunger();
                        int kittenLoneliness = state.getLoneliness();
                        int kittenEvents = state.getEvents();
                        int changeHunger = DataConversions.random(4, 6);
                        int changeLoneliness = DataConversions.random(4, 6);
                        // trigger only if the gauges have passed to the next tenth digit
                        boolean tHunger = (((kittenHunger + changeHunger) / KittenToCat.BASE_FACTOR) - (kittenHunger / KittenToCat.BASE_FACTOR)) > 0;
                        boolean tLoneliness = (((kittenLoneliness + changeLoneliness) / KittenToCat.BASE_FACTOR) - (kittenLoneliness / KittenToCat.BASE_FACTOR)) > 0;
                        kittenHunger += changeHunger;
                        kittenLoneliness += changeLoneliness;
                        List<String> messages = new ArrayList<String>();
                        // hungry and lonely
                        if (tHunger && tLoneliness) {
                            messages = KittenMessageSolver.messagesCombined(kittenHunger, kittenLoneliness);
                            kittenEvents++;
                        } else// just hungry

                            if (tHunger) {
                                messages = KittenMessageSolver.messagesHunger(kittenHunger);
                                kittenEvents++;
                            } else// just lonely

                                if (tLoneliness) {
                                    messages = KittenMessageSolver.messagesLoneliness(kittenLoneliness);
                                    kittenEvents++;
                                }


                        for (String message : messages) {
                            p.message(message);
                        }
                        // kitten runs off - reset counters
                        if ((kittenHunger >= (4 * KittenToCat.BASE_FACTOR)) || (kittenLoneliness >= (4 * KittenToCat.BASE_FACTOR))) {
                            p.getInventory().remove(ItemId.KITTEN.id(), 1);
                            kittenEvents = kittenHunger = kittenLoneliness = 0;
                        } else// kitten grows to cat - replace and reset counters

                            if (kittenEvents >= 32) {
                                p.getInventory().replace(ItemId.KITTEN.id(), ItemId.CAT.id());
                                kittenEvents = kittenHunger = kittenLoneliness = 0;
                                Functions.___message(p, 1200, "you're kitten has grown into a healthy cat", "it can hunt for its self now");
                            }

                        state.setEvents(kittenEvents);
                        state.setHunger(kittenHunger);
                        state.setLoneliness(kittenLoneliness);
                        state.saveState(p);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
        return isFoodOnCat(item1, item2) || isBallWoolOnCat(item1, item2);
    }

    @Override
    public GameStateEvent onInvUseOnItem(Player p, Item item1, Item item2) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (isFoodOnCat(item1, item2) || isBallWoolOnCat(item1, item2)) {
                        boolean isGrownCat = (item1.getID() != ItemId.KITTEN.id()) && (item2.getID() != ItemId.KITTEN.id());
                        Item item;
                        if (isGrownCat) {
                            item = (item1.getID() == ItemId.CAT.id()) ? item2 : item1;
                        } else {
                            item = (item1.getID() == ItemId.KITTEN.id()) ? item2 : item1;
                        }
                        if (isBallWoolOnCat(item1, item2)) {
                            entertainCat(item, p, isGrownCat);
                        } else
                            if (isFoodOnCat(item1, item2)) {
                                feedCat(item, p, isGrownCat);
                            }

                    }
                    return null;
                });
            }
        };
    }

    private boolean isBallWoolOnCat(Item item1, Item item2) {
        return Functions.compareItemsIds(item1, item2, ItemId.KITTEN.id(), ItemId.BALL_OF_WOOL.id()) || Functions.compareItemsIds(item1, item2, ItemId.CAT.id(), ItemId.BALL_OF_WOOL.id());
    }

    private boolean isFoodOnCat(Item item1, Item item2) {
        return (((item2.getID() == ItemId.KITTEN.id()) || (item2.getID() == ItemId.CAT.id())) && Functions.inArray(item1.getID(), ItemId.MILK.id(), ItemId.RAW_SHRIMP.id(), ItemId.RAW_SARDINE.id(), ItemId.SEASONED_SARDINE.id(), ItemId.SARDINE.id(), ItemId.RAW_HERRING.id(), ItemId.RAW_ANCHOVIES.id(), ItemId.RAW_TROUT.id(), ItemId.TROUT.id(), ItemId.RAW_SALMON.id(), ItemId.RAW_TUNA.id(), ItemId.TUNA.id())) || (((item1.getID() == ItemId.KITTEN.id()) || (item1.getID() == ItemId.CAT.id())) && Functions.inArray(item2.getID(), ItemId.MILK.id(), ItemId.RAW_SHRIMP.id(), ItemId.RAW_SARDINE.id(), ItemId.SEASONED_SARDINE.id(), ItemId.SARDINE.id(), ItemId.RAW_HERRING.id(), ItemId.RAW_ANCHOVIES.id(), ItemId.RAW_TROUT.id(), ItemId.TROUT.id(), ItemId.RAW_SALMON.id(), ItemId.RAW_TUNA.id(), ItemId.TUNA.id()));
    }

    @Override
    public boolean blockInvUseOnNpc(Player p, Npc n, Item item) {
        // only small rats
        return ((item.getID() == ItemId.KITTEN.id()) || (item.getID() == ItemId.CAT.id())) && (n.getID() == NpcId.RAT_WITCHES_POTION.id());
    }

    @Override
    public GameStateEvent onInvUseOnNpc(Player p, Npc n, Item item) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((item.getID() == ItemId.KITTEN.id()) && (n.getID() == NpcId.RAT_WITCHES_POTION.id())) {
                        p.message("it pounces on the rat...");
                        if (DataConversions.random(0, 9) == 0) {
                            n.face(p);
                            Functions.sleep(600);
                            n.remove();
                            p.setBusyTimer(2);
                            Functions.sleep(1200);
                            // possibly non kosher
                            Functions.___message(p, 1800, "...and quickly gobbles it up", "it returns to your satchel licking it's paws");
                            reduceKittensLoneliness(p);
                        }
                    } else
                        if ((item.getID() == ItemId.CAT.id()) && (n.getID() == NpcId.RAT_WITCHES_POTION.id())) {
                            p.message("the cat pounces on the rat...");
                            n.face(p);
                            Functions.sleep(600);
                            n.remove();
                            p.setBusyTimer(2);
                            Functions.sleep(1200);
                            Functions.___message(p, 1800, "...and quickly gobbles it up", "it returns to your satchel licking it's paws");
                        }

                    return null;
                });
            }
        };
    }
}

