package com.openrsc.server.plugins.quests.members.touristtrap;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.UnWieldListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class Tourist_Trap_Mechanism implements DropListener , InvUseOnItemListener , InvUseOnNpcListener , InvUseOnObjectListener , ObjectActionListener , PickupListener , TalkToNpcListener , UnWieldListener , DropExecutiveListener , InvUseOnItemExecutiveListener , InvUseOnNpcExecutiveListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener , PickupExecutiveListener , TalkToNpcExecutiveListener {
    private static int MINING_CAVE = 963;

    private static int MINING_CART = 976;

    private static int MINING_CAVE_BACK = 964;

    private static int TRACK = 974;

    private static int MINING_BARREL = 967;

    private static int LIFT_PLATFORM = 977;

    private static int LIFT_UP = 966;

    private static int MINING_CART_ABOVE = 1025;

    private static int DISTURBED_SAND1 = 944;

    private static int DISTURBED_SAND2 = 945;

    @Override
    public GameStateEvent onUnWield(Player player, Item item) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((((item.getID() == ItemId.SLAVES_ROBE_BOTTOM.id()) || (item.getID() == ItemId.SLAVES_ROBE_TOP.id())) && player.getLocation().inTouristTrapCave()) && (player.getQuestStage(Quests.TOURIST_TRAP) != (-1))) {
                        Npc n = Functions.getNearestNpc(player, NpcId.MERCENARY.id(), 5);
                        if (n != null) {
                            n.teleport(player.getX(), player.getY());
                            player.teleport(player.getX(), player.getY());
                            Functions.sleep(650);
                            Functions.___npcTalk(player, n, "Oi! What are you doing down here?", "You're no slave!");
                            n.startCombat(player);
                        } else {
                            player.teleport(player.getX(), player.getY());
                            Npc newNpc = Functions.spawnNpc(player.getWorld(), NpcId.MERCENARY.id(), player.getX(), player.getY(), 30000);
                            Functions.sleep(650);
                            Functions.___npcTalk(player, newNpc, "Oi! What are you doing down here?", "You're no slave!");
                            newNpc.startCombat(player);
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnNpc(Player p, Npc npc, Item item) {
        return ((((item.getID() == ItemId.TECHNICAL_PLANS.id()) && (npc.getID() == NpcId.BEDABIN_NOMAD_GUARD.id())) || ((item.getID() == ItemId.TECHNICAL_PLANS.id()) && (npc.getID() == NpcId.AL_SHABIM.id()))) || ((item.getID() == ItemId.TENTI_PINEAPPLE.id()) && (npc.getID() == NpcId.MERCENARY_ESCAPEGATES.id()))) || ((item.getID() == ItemId.MINING_BARREL.id()) && (npc.getID() == NpcId.ANA.id()));
    }

    @Override
    public GameStateEvent onInvUseOnNpc(Player p, Npc npc, Item item) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((item.getID() == ItemId.TECHNICAL_PLANS.id()) && (npc.getID() == NpcId.BEDABIN_NOMAD_GUARD.id())) {
                        if ((p.getQuestStage(Quests.TOURIST_TRAP) > 7) || (p.getQuestStage(Quests.TOURIST_TRAP) == (-1))) {
                            Functions.___npcTalk(p, npc, "Sorry, but you can't use the tent without permission.", "But thanks for all your help with the Bedabin people.", "And we'll take those plans off your hands as well!");
                        } else
                            if ((p.getQuestStage(Quests.TOURIST_TRAP) == 6) || (p.getQuestStage(Quests.TOURIST_TRAP) == 7)) {
                                Functions.___npcTalk(p, npc, "Ok, you can go in, Al Shabim has told me about you.");
                                p.teleport(171, 792);
                            } else
                                if (p.getQuestStage(Quests.TOURIST_TRAP) >= 0) {
                                    Functions.___npcTalk(p, npc, "Hmm, those plans look interesting.", "Go and show them to Al Shabim...", "I'm sure he'll be pleased to see them.");
                                }


                    } else
                        if ((item.getID() == ItemId.TECHNICAL_PLANS.id()) && (npc.getID() == NpcId.AL_SHABIM.id())) {
                            npc.initializeIndirectTalkScript(p);
                        } else
                            if ((item.getID() == ItemId.TENTI_PINEAPPLE.id()) && (npc.getID() == NpcId.MERCENARY_ESCAPEGATES.id())) {
                                Functions.removeItem(p, ItemId.TENTI_PINEAPPLE.id(), 1);
                                Functions.___npcTalk(p, npc, "Great! Just what I've been looking for!", "Mmmmmmm, delicious!!", "Oh, this is soo nice!", "Mmmmm, *SLURP*", "Yummmm....Oh yes, this is great.");
                                if (p.getQuestStage(Quests.TOURIST_TRAP) == 8) {
                                    p.updateQuestStage(Quests.TOURIST_TRAP, 9);
                                }
                            } else
                                if ((item.getID() == ItemId.MINING_BARREL.id()) && (npc.getID() == NpcId.ANA.id())) {
                                    if (p.getQuestStage(Quests.TOURIST_TRAP) == (-1)) {
                                        p.message("You have already completed this quest.");
                                        Functions.___npcTalk(p, npc, "I think you might have me confused with someone else.");
                                        return null;
                                    }
                                    if (!Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                        boolean isFirstTime = !p.getCache().hasKey("tried_ana_barrel");
                                        if ((p.getCache().hasKey("ana_lift") || p.getCache().hasKey("ana_cart")) || p.getCache().hasKey("ana_in_cart")) {
                                            Functions.___message(p, "Oh, here's Ana, the guards must have discovered her.", "And sent her back to the mines...");
                                        }
                                        if (isFirstTime) {
                                            Functions.___npcTalk(p, npc, "Hey, what do you think you're doing?", "Harumph!");
                                        } else {
                                            Functions.___npcTalk(p, npc, "Hey, what do you think you're doing?", "Leave me alone and let me get on with my work.", "Else we'll both be in trouble.", "Oh no, NOT AGAIN!", "Harumph!");
                                        }
                                        Functions.___playerTalk(p, npc, "Shush...It's for your own good!");
                                        Functions.___message(p, "You manage to squeeze Ana into the barrel,", "despite her many complaints.");
                                        p.getInventory().replace(ItemId.MINING_BARREL.id(), ItemId.ANA_IN_A_BARREL.id());
                                        if (npc != null) {
                                            npc.remove();
                                        }
                                        if (isFirstTime) {
                                            p.getCache().store("tried_ana_barrel", true);
                                        }
                                    } else {
                                        p.message("You already have Ana in a barrel, you can't get two in there!");
                                    }
                                }



                    return null;
                });
            }
        };
    }

    private void makeDartTip(Player p, GameObject obj) {
        if (obj.getID() == 1006) {
            if (!Functions.hasItem(p, ItemId.TECHNICAL_PLANS.id())) {
                Functions.___message(p, "This anvil is experimental...", "You need detailed plans of the item you want to make in order to use it.");
                return;
            }
            Functions.___message(p, "Do you want to follow the technical plans ?");
            int menu = Functions.___showMenu(p, "Yes. I'd like to try.", "No, not just yet.");
            if (menu == 0) {
                if (!Functions.hasItem(p, ItemId.HAMMER.id())) {
                    p.message("You need a hammer to work anything on the anvil.");
                    return;
                }
                if (Functions.getCurrentLevel(p, Skills.SMITHING) < 20) {
                    p.message("You need level 20 in smithing before you can attempt this.");
                    return;
                }
                Functions.___message(p, "You begin experimenting in forging the weapon...", "You follow the plans carefully.", "And after a long time of careful work.");
                Functions.removeItem(p, ItemId.BRONZE_BAR.id(), 1);
                if (succeedRate(p)) {
                    Functions.___message(p, "You finally manage to forge a sharp, pointed...", "... dart tip...");
                    if (!Functions.hasItem(p, ItemId.PROTOTYPE_DART_TIP.id())) {
                        Functions.addItem(p, ItemId.PROTOTYPE_DART_TIP.id(), 1);
                    }
                    Functions.___message(p, "You study the technical plans even more...", "You need to attach feathers to the tip to complete the weapon.");
                } else {
                    Functions.___message(p, "You waste the bronze bar through an unlucky accident.");
                }
            } else
                if (menu == 1) {
                    p.message("You decide not follow the technical plans.");
                }

        }
    }

    private void attachFeathersToPrototype(Player p, Item i, Item i2) {
        if (Functions.compareItemsIds(i, i2, ItemId.FEATHER.id(), ItemId.PROTOTYPE_DART_TIP.id())) {
            if (!Functions.hasItem(p, ItemId.FEATHER.id(), 10)) {
                p.message("You need at least ten feathers to make this item.");
                return;
            }
            if (Functions.getCurrentLevel(p, Skills.FLETCHING) < 10) {
                p.message("You need a fletching level of at least 10 to complete this.");
                return;
            }
            Functions.___message(p, "You try to attach feathers to the bronze dart tip.", "Following the plans is tricky, but you persevere.");
            Functions.removeItem(p, ItemId.FEATHER.id(), 10);
            if (succeedRate(p)) {
                Functions.___message(p, "You succesfully attach the feathers to the dart tip.");
                p.getInventory().replace(ItemId.PROTOTYPE_DART_TIP.id(), ItemId.PROTOTYPE_THROWING_DART.id());
                // kosher: dependent on fletching level!
                p.incExp(Skills.FLETCHING, Functions.getMaxLevel(p, Skills.FLETCHING) * 50, true);
            } else {
                Functions.___message(p, "An unlucky accident causes you to waste the feathers.", "But you feel that you're close to making this item though.");
            }
        }
    }

    private boolean succeedRate(Player p) {
        int random = DataConversions.getRandom().nextInt(5);
        if ((random == 4) || (random == 3)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return Functions.inArray(obj.getID(), Tourist_Trap_Mechanism.DISTURBED_SAND1, Tourist_Trap_Mechanism.DISTURBED_SAND2, 1006, Tourist_Trap_Mechanism.MINING_CAVE, Tourist_Trap_Mechanism.MINING_CAVE_BACK, Tourist_Trap_Mechanism.MINING_CART, Tourist_Trap_Mechanism.MINING_BARREL, Tourist_Trap_Mechanism.TRACK, Tourist_Trap_Mechanism.LIFT_PLATFORM, Tourist_Trap_Mechanism.LIFT_UP, Tourist_Trap_Mechanism.MINING_CART_ABOVE);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    // closest to irena
                    if (obj.getID() == Tourist_Trap_Mechanism.DISTURBED_SAND1) {
                        if (command.equals("look")) {
                            if (p.getQuestStage(Quests.TOURIST_TRAP) <= 0) {
                                Functions.___message(p, "You see some footsteps in the sand.");
                            } else {
                                Functions.___message(p, "This looks like some disturbed sand.", "footsteps seem to be heading of towards the south west.");
                            }
                        } else
                            if (command.equals("search")) {
                                if (p.getQuestStage(Quests.TOURIST_TRAP) <= 0) {
                                    Functions.___message(p, "You just see some footsteps in the sand.");
                                } else {
                                    Functions.___message(p, "You search the footsteps more closely.", "You can see that there are five sets of footprints.", "One set of footprints seems lighter than the others.", "The four other footsteps were made by heavier people with boots.");
                                }
                            }

                    } else// closest to camp

                        if (obj.getID() == Tourist_Trap_Mechanism.DISTURBED_SAND2) {
                            if (command.equals("look")) {
                                if (p.getQuestStage(Quests.TOURIST_TRAP) <= 0) {
                                    Functions.___message(p, "You just see some footsteps in the sand.");
                                } else {
                                    Functions.___message(p, "You find footsteps heading south.", "And this time evidence of a struggle...", "The footsteps head off due south.");
                                }
                            } else
                                if (command.equals("search")) {
                                    if (p.getQuestStage(Quests.TOURIST_TRAP) <= 0) {
                                        Functions.___message(p, "You just see some footsteps in the sand!");
                                    } else {
                                        Functions.___message(p, "You search the area thoroughly...", "You notice something colourful in the sand.", "You dig around and find a piece of red silk scarf.", "It looks as if Ana has been this way!");
                                    }
                                }

                        } else
                            if (obj.getID() == 1006) {
                                makeDartTip(p, obj);
                            } else
                                if (obj.getID() == Tourist_Trap_Mechanism.MINING_CAVE_BACK) {
                                    if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                        failCaveAnaInBarrel(p, null);
                                        return null;
                                    }
                                    Functions.___message(p, "You walk into the dark of the cavern...");
                                    p.message("And emerge in a different part of this huge underground complex.");
                                    p.teleport(84, 3640);
                                } else
                                    if (obj.getID() == Tourist_Trap_Mechanism.MINING_CAVE) {
                                        Npc n = Functions.getNearestNpc(p, NpcId.MERCENARY_ESCAPEGATES.id(), 10);
                                        if (((!p.getInventory().wielding(ItemId.SLAVES_ROBE_BOTTOM.id())) && (!p.getInventory().wielding(ItemId.SLAVES_ROBE_TOP.id()))) && (p.getQuestStage(Quests.TOURIST_TRAP) != (-1))) {
                                            p.message("This guard looks as if he's been down here a while.");
                                            Functions.___npcTalk(p, n, "Hey, you're no slave!");
                                            Functions.___npcTalk(p, n, "What are you doing down here?");
                                            n.setChasing(p);
                                            Functions.___message(p, "More guards rush to catch you.", "You are roughed up a bit by the guards as you're manhandlded to a cell.");
                                            Functions.___npcTalk(p, n, "Into the cell you go! I hope this teaches you a lesson.");
                                            p.teleport(89, 801);
                                            return null;
                                        }
                                        if ((p.getQuestStage(Quests.TOURIST_TRAP) >= 9) || (p.getQuestStage(Quests.TOURIST_TRAP) == (-1))) {
                                            Functions.___message(p, "You walk into the dark of the cavern...");
                                            p.message("And emerge in a different part of this huge underground complex.");
                                            p.teleport(76, 3640);
                                            return null;
                                        }
                                        p.message("Two guards block your way further into the caves");
                                        if (n != null) {
                                            Functions.___npcTalk(p, n, "Hey you, move away from there!");
                                        }
                                    } else
                                        if (obj.getID() == Tourist_Trap_Mechanism.MINING_CART) {
                                            if (command.equals("look")) {
                                                if ((obj.getX() == 62) && (obj.getY() == 3639)) {
                                                    p.message("This cart is being unloaded into this section of the mine.");
                                                    p.message("Before being sent back for another load.");
                                                } else {
                                                    p.message("This mine cart is being loaded up with new rocks and stone.");
                                                    p.message("It gets sent to a different section of the mine for unloading.");
                                                }
                                            } else
                                                if (command.equals("search")) {
                                                    p.message("You search the mine cart.");
                                                    if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                                        p.message("There isn't enough space for both you and Ana in the cart.");
                                                        return null;
                                                    }
                                                    p.message("There may be just enough space to squeeze yourself into the cart.");
                                                    p.message("Would you like to try?");
                                                    int menu = Functions.___showMenu(p, "Yes, of course.", "No Thanks, it looks pretty dangerous.");
                                                    if (menu == 0) {
                                                        if (succeedRate(p)) {
                                                            p.message("You succeed!");
                                                            if ((obj.getX() == 56) && (obj.getY() == 3631)) {
                                                                p.teleport(62, 3640);
                                                            } else
                                                                if ((obj.getX() == 62) && (obj.getY() == 3639)) {
                                                                    p.teleport(55, 3632);
                                                                }

                                                        } else {
                                                            p.message("You fail to fit yourself into the cart in time before it starts it's journey.");
                                                            p.message("You fall and hurt yourself.");
                                                            p.damage(2);
                                                        }
                                                    } else
                                                        if (menu == 1) {
                                                            p.message("You decide not to get into the dangerous looking mine cart.");
                                                        }

                                                }

                                        } else
                                            if (obj.getID() == Tourist_Trap_Mechanism.TRACK) {
                                                p.message("You see that this track is too dangerous to cross.");
                                                p.message("High speed carts are crossing the track most of the time.");
                                            } else
                                                if (obj.getID() == Tourist_Trap_Mechanism.MINING_BARREL) {
                                                    if (p.getCache().hasKey("ana_is_up")) {
                                                        if (Functions.hasItem(p, ItemId.MINING_BARREL.id())) {
                                                            p.message("You can only manage one of these at a time.");
                                                            return null;
                                                        }
                                                        Functions.___message(p, "You find the barrel with ana in it.", "@gre@Ana: Let me out of here, I feel sick!");
                                                        Functions.addItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                                                        p.getCache().remove("ana_is_up");
                                                        return null;
                                                    }
                                                    if (p.getCache().hasKey("ana_cart")) {
                                                        if (Functions.hasItem(p, ItemId.MINING_BARREL.id())) {
                                                            p.message("You can only manage one of these at a time.");
                                                            return null;
                                                        }
                                                        p.message("You search the barrels and find the one with Ana in it.");
                                                        p.message("@gre@Ana: Let me out!");
                                                        Functions.addItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                                                        p.getCache().remove("ana_cart");
                                                        return null;
                                                    }
                                                    if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                                        p.message("You cannot carry another barrel while you're carrying Ana.");
                                                        return null;
                                                    }
                                                    if (p.getCache().hasKey("ana_lift")) {
                                                        p.message("You search for Ana, but cannot find her.");
                                                    }
                                                    p.message("This barrel is quite big, but you may be able to carry one. ");
                                                    p.message("Would you like to take one?");
                                                    int menu = Functions.___showMenu(p, "Yeah, cool!", "No thanks.");
                                                    if (menu == 0) {
                                                        if (Functions.hasItem(p, ItemId.MINING_BARREL.id())) {
                                                            p.message("You can only manage one of these at a time.");
                                                        } else {
                                                            p.message("You take the barrel, it's not that heavy, just awkward.");
                                                            Functions.addItem(p, ItemId.MINING_BARREL.id(), 1);
                                                        }
                                                    } else
                                                        if (menu == 1) {
                                                            p.message("You decide not to take the barrel.");
                                                        }

                                                } else
                                                    if (obj.getID() == Tourist_Trap_Mechanism.LIFT_PLATFORM) {
                                                        Npc n = Functions.getNearestNpc(p, NpcId.MERCENARY_LIFTPLATFORM.id(), 5);
                                                        if (n != null) {
                                                            if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                                                anaToLift(p, n);
                                                                return null;
                                                            }
                                                            Functions.___npcTalk(p, n, "Hey there, what do you want?");
                                                            int menu = Functions.___showMenu(p, n, "What is this thing?", "Can I use this?");
                                                            if (menu == 0) {
                                                                repeatLiftDialogue(p, n, Tourist_Trap_Mechanism.RepeatLift.THING);
                                                            } else
                                                                if (menu == 1) {
                                                                    repeatLiftDialogue(p, n, Tourist_Trap_Mechanism.RepeatLift.USETHIS);
                                                                }

                                                        }
                                                    } else
                                                        if (obj.getID() == Tourist_Trap_Mechanism.LIFT_UP) {
                                                            p.message("You pull on the winch");
                                                            if (p.getCache().hasKey("ana_lift")) {
                                                                Functions.___message(p, "You see a barrel coming to the surface.", "Before too long you haul it onto the side.", "The barrel seems quite heavy and you hear a muffled sound coming from inside.");
                                                                p.message("@gre@Ana: Get me OUT OF HERE!");
                                                                p.getCache().remove("ana_lift");
                                                                if (!p.getCache().hasKey("ana_is_up")) {
                                                                    p.getCache().store("ana_is_up", true);
                                                                }
                                                            } else {
                                                                p.message("You pull on the winch and a heavy barrel filled with stone comes to the surface.");
                                                            }
                                                        } else
                                                            if (obj.getID() == Tourist_Trap_Mechanism.MINING_CART_ABOVE) {
                                                                p.message("You search the mine cart.");
                                                                if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                                                    Functions.___message(p, "There should be enough space for Ana (in the barrel) to go on here.");
                                                                }
                                                                if (p.getCache().hasKey("ana_in_cart")) {
                                                                    Functions.___message(p, "You can see the barrel with Ana in it on the cart already.");
                                                                }
                                                                Functions.___message(p, "There is space on the cart for you get on, would you like to try?");
                                                                int menu = Functions.___showMenu(p, "Yes, I'll get on.", "No, I've got other plans.", "Attract mine cart drivers attention.");
                                                                if (menu == 0) {
                                                                    p.message("You decide to climb onto the cart.");
                                                                    if (p.getCache().hasKey("ana_in_cart")) {
                                                                        Functions.___message(p, "You hear Ana starting to bang on the barrel for her to be let out.");
                                                                        Functions.___message(p, "@gre@Ana: Get me out of here, I'm suffocating!", "@gre@Ana: It smells like dwarven underwear in here!");
                                                                    }
                                                                    p.teleport(86, 808);
                                                                    if (p.getCache().hasKey("rescue")) {
                                                                        Functions.___message(p, "As soon as you get on the cart, it starts to move.", "Before too long you are past the gates.", "You jump off the cart taking Ana with you.");
                                                                        p.teleport(106, 806);
                                                                        p.getCache().remove("rescue");
                                                                        Functions.addItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                                                                    }
                                                                } else
                                                                    if (menu == 1) {
                                                                        p.message("You decide not to get onto the cart.");
                                                                    } else
                                                                        if (menu == 2) {
                                                                            Npc cartDriver = Functions.getNearestNpc(p, NpcId.MINING_CART_DRIVER.id(), 10);
                                                                            if (cartDriver != null) {
                                                                                Functions.___npcTalk(p, cartDriver, "Ahem.");
                                                                                if (p.getCache().hasKey("rescue")) {
                                                                                    Functions.___npcTalk(p, cartDriver, "Hurry up, get in the cart or I'll go without you!");
                                                                                    return null;
                                                                                }
                                                                                if (p.getCache().hasKey("ana_in_cart")) {
                                                                                    getOutWithAnaInCart(p, cartDriver, -1);
                                                                                    return null;
                                                                                }
                                                                                if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                                                                    Functions.___npcTalk(p, cartDriver, "What're you doing carrying that big barrel around?", "Put it in the back of the cart like all the others!");
                                                                                    return null;
                                                                                }
                                                                                p.message("The cart driver is busy loading the cart up ...");
                                                                            }
                                                                        }


                                                            }










                    return null;
                });
            }
        };
    }

    private void getOutWithAnaInCart(Player p, Npc n, int cID) {
        if (cID == (-1)) {
            Functions.___message(p, "The cart driver seems to be festidiously cleaning his cart.", "It doesn't look as if he wants to be disturbed.");
            int menu = // do not send over
            Functions.___showMenu(p, n, false, "Hello.", "Nice cart.", "Pssst...");
            if (menu == 0) {
                Functions.___playerTalk(p, n, "Hello");
                Functions.___npcTalk(p, n, "Can't you see I'm busy?", "Now get out of here!");
                int getGo = Functions.___showMenu(p, n, "Oh, ok, sorry.", "Nice cart.", "Pssst...");
                if (getGo == 0) {
                    getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.OKSORRY);
                } else
                    if (getGo == 1) {
                        getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.NICECART);
                    } else
                        if (getGo == 2) {
                            getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSST);
                        }


            } else
                if (menu == 1) {
                    Functions.___playerTalk(p, n, "Nice cart.");
                    getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.NICECART);
                } else
                    if (menu == 2) {
                        Functions.___playerTalk(p, n, "Pssst...");
                        getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSST);
                    }


        }
        switch (cID) {
            case Tourist_Trap_Mechanism.CartDriver.PSSST :
                Functions.___message(p, "The cart driver completely ignores you.");
                int pst = Functions.___showMenu(p, n, "Psssst...", "Psssssst...", "Pssssssssttt!!!");
                if (pst == 0) {
                    getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSST2);
                } else
                    if (pst == 1) {
                        getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSST3);
                    } else
                        if (pst == 2) {
                            getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSSTFINAL);
                        }


                break;
            case Tourist_Trap_Mechanism.CartDriver.PSSST2 :
                Functions.___message(p, "The driver completely ignores you.");
                int m = Functions.___showMenu(p, n, "Psssssst...", "Pssst...", "Pssssssssttt!!!");
                if (m == 0) {
                    getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSST3);
                } else
                    if (m == 1) {
                        getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSST);
                    } else
                        if (m == 2) {
                            getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSSTFINAL);
                        }


                break;
            case Tourist_Trap_Mechanism.CartDriver.PSSST3 :
                Functions.___message(p, "The driver completely ignores you.");
                int me = Functions.___showMenu(p, n, "Psssst...", "Pssst...", "Pssssssssttt!!!");
                if (me == 0) {
                    getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSST2);
                } else
                    if (me == 1) {
                        getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSST);
                    } else
                        if (me == 2) {
                            getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.PSSSTFINAL);
                        }


                break;
            case Tourist_Trap_Mechanism.CartDriver.PSSSTFINAL :
                Functions.___message(p, "The cart driver turns around quickly to face you.");
                Functions.___npcTalk(p, n, "What!", "Can't you see I'm busy?");
                int shh = Functions.___showMenu(p, n, "Oh, ok, sorry.", "Shhshhh!");
                if (shh == 0) {
                    getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.OKSORRY);
                } else
                    if (shh == 1) {
                        Functions.___npcTalk(p, n, "Shush yourself!");
                        p.message("The cart driver goes back to his work.");
                    }

                break;
            case Tourist_Trap_Mechanism.CartDriver.OKSORRY :
                Functions.___npcTalk(p, n, "Look just leave me alone!");
                p.message("The cart driver goes back to his work.");
                break;
            case Tourist_Trap_Mechanism.CartDriver.NICECART :
                Functions.___message(p, "The cart driver looks around at you and tries to weigh you up.");
                Functions.___npcTalk(p, n, "Hmmm.");
                Functions.___message(p, "He tuts to himself and starts checking the wheels.");
                Functions.___npcTalk(p, n, "Tut !");
                int tut = Functions.___showMenu(p, n, "I wonder if you could help me?", "One wagon wheel says to the other,'I'll see you around'.", "Can I help you at all?");
                if (tut == 0) {
                    getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.WONDERIF);
                } else
                    if (tut == 1) {
                        getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.WAGON);
                    } else
                        if (tut == 2) {
                            getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.HELPYOU);
                        }


                break;
            case Tourist_Trap_Mechanism.CartDriver.WAGON :
                Functions.___message(p, "The cart driver smirks a little.", "He starts checking the steering on the cart.");
                int menu = // do not send over
                Functions.___showMenu(p, n, false, "'One good turn deserves another'", "Can you get me the heck out of here please?");
                if (menu == 0) {
                    Functions.___playerTalk(p, n, "'One good turn deserves another.");
                    Functions.___message(p, "The cart driver smiles a bit and then turns to you.");
                    Functions.___npcTalk(p, n, "Are you trying to get me fired?");
                    int menu2 = Functions.___showMenu(p, n, "No", "Yes", "Fired...no, shot perhaps!");
                    if (menu2 == 0) {
                        Functions.___npcTalk(p, n, "It certainly sounds like it, now leave me alone.", "If you bug me again, I'm gonna call the guards.");
                        p.message("The cart driver goes back to his work.");
                    } else
                        if (menu2 == 1) {
                            Functions.___npcTalk(p, n, "And why would you want to do a crazy thing like that for?", "I ought to teach you a lesson!");
                            driverCallGuards(p, n);
                        } else
                            if (menu2 == 2) {
                                Functions.___npcTalk(p, n, "Ha ha ha! You're funny!");
                                Functions.___message(p, "The cart driver checks that the guards aren't watching him.");
                                Functions.___npcTalk(p, n, "What're you in fer?");
                                int menu3 = Functions.___showMenu(p, n, "Oh, I'm not supposed to be here at all actually.", "I'm in for murder, so you'd better get me out of here!", "In for a penny in for a pound.");
                                if (menu3 == 0) {
                                    Functions.___npcTalk(p, n, "Hmmm, interesting...let me guess.", "You're completely innocent...", "like all the other inmates in here.", "Ha ha ha!");
                                    p.message("The Cart driver goes back to his work.");
                                } else
                                    if (menu3 == 1) {
                                        Functions.___npcTalk(p, n, "Hmm, well, I wonder what the guards are gonna say about that!");
                                        driverCallGuards(p, n);
                                    } else
                                        if (menu3 == 2) {
                                            Functions.___message(p, "The cart driver laughs at your pun...");
                                            Functions.___npcTalk(p, n, "Ha ha ha, oh Stoppit!");
                                            Functions.___message(p, "The cart driver seems much happier now.");
                                            Functions.___npcTalk(p, n, "What can I do for you anyway?");
                                            int menu4 = // do not send over
                                            Functions.___showMenu(p, n, false, "Can you smuggle me out on your cart?", "Can you smuggle my friend Ana out on your cart?", "Well, you see, it's like this...");
                                            if (menu4 == 0) {
                                                Functions.___playerTalk(p, n, "Can you smuggle me out on your cart?");
                                                Functions.___message(p, "The cart driver points at a nearby guard.");
                                                Functions.___npcTalk(p, n, "Ask that man over there if it's OK and I'll consider it!", "Ha ha ha!");
                                                p.message("The cart driver goes back to his work, laughing to himself.");
                                            } else
                                                if (menu4 == 1) {
                                                    Functions.___playerTalk(p, n, "Can you smuggle my friend out on your cart?");
                                                    Functions.___npcTalk(p, n, "As long as your friend is a barrel full of rocks.", "I don't think it would be a problem at all!", "Ha ha ha!");
                                                    p.message("The cart driver goes back to his work, laughing to himself.");
                                                } else
                                                    if (menu4 == 2) {
                                                        Functions.___playerTalk(p, n, "Well, you see, it's like this...");
                                                        Functions.___npcTalk(p, n, "yeah!");
                                                        int menu5 = Functions.___showMenu(p, n, "Prison riot in ten minutes, get your cart out of here!", "There's ten gold in it for you if you leave now - no questions asked.");
                                                        if (menu5 == 0) {
                                                            p.message("The cart driver seems visibly shaken...");
                                                            Functions.___npcTalk(p, n, "Oh, right..yes...yess, Ok...");
                                                            Functions.___message(p, "The cart driver quickly starts preparing the cart.");
                                                            int menu6 = Functions.___showMenu(p, n, "Good luck!", "You can't leave me here, I'll get killed!");
                                                            if (menu6 == 0) {
                                                                Functions.___npcTalk(p, n, "Yeah, you too!");
                                                                Functions.___message(p, "The cart sets off at a hectic pace.", "The guards at the gate get suspiscious and search the cart.", "They find Ana in the Barrel and take her back into the mine.");
                                                                if (p.getCache().hasKey("ana_in_cart")) {
                                                                    p.getCache().remove("ana_in_cart");
                                                                }
                                                            } else
                                                                if (menu6 == 1) {
                                                                    Functions.___npcTalk(p, n, "Oh, right...ok, you'd better jump in the cart then!", "Quickly!");
                                                                    if (p.getCache().hasKey("ana_in_cart")) {
                                                                        p.getCache().remove("ana_in_cart");
                                                                        p.getCache().store("rescue", true);
                                                                    }
                                                                }

                                                        } else
                                                            if (menu5 == 1) {
                                                                Functions.___npcTalk(p, n, "If you're going to bribe me, at least make it worth my while.", "Now, let's say 100 Gold pieces should we?", "Ha ha ha!");
                                                                int menu6 = // do not send over
                                                                Functions.___showMenu(p, n, false, "A hundred it is!", "Forget it!");
                                                                if (menu6 == 0) {
                                                                    Functions.___playerTalk(p, n, "A hundred it is.");
                                                                    Functions.___npcTalk(p, n, "Great!");
                                                                    if (Functions.hasItem(p, ItemId.COINS.id(), 100)) {
                                                                        Functions.___npcTalk(p, n, "Ok, get in the back of the cart then!");
                                                                        Functions.removeItem(p, ItemId.COINS.id(), 100);
                                                                        if (p.getCache().hasKey("ana_in_cart")) {
                                                                            p.getCache().remove("ana_in_cart");
                                                                            p.getCache().store("rescue", true);
                                                                        }
                                                                    } else {
                                                                        Functions.___npcTalk(p, n, "You little cheat, trying to trick me!", "I'll show you!");
                                                                        driverCallGuards(p, n);
                                                                    }
                                                                } else
                                                                    if (menu6 == 1) {
                                                                        Functions.___playerTalk(p, n, "Forget it!");
                                                                        Functions.___npcTalk(p, n, "Ok, fair enough!", "But don't bother me anymore.");
                                                                        p.message("The cart driver goes back to work.");
                                                                    }

                                                            }

                                                    }


                                        }


                            }


                } else
                    if (menu == 1) {
                        Functions.___playerTalk(p, n, "Can you get me the heck out of here please?");
                        getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.HECKOUT);
                    }

                break;
            case Tourist_Trap_Mechanism.CartDriver.HELPYOU :
                Functions.___npcTalk(p, n, "I'm quite capable thanks...", "Now get lost before I call the guards.");
                int help = Functions.___showMenu(p, n, "Can you get me the heck out of here please?", "I could help, I know a lot about carts.");
                if (help == 0) {
                    getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.HECKOUT);
                } else
                    if (help == 1) {
                        Functions.___npcTalk(p, n, "Are you saying I don't know anything about carts?", "Why you cheeky little....");
                        Functions.___message(p, "The cart driver seems mortally offended...", "his temper explodes as he shouts the guards.");
                        driverCallGuards(p, n);
                    }

                break;
            case Tourist_Trap_Mechanism.CartDriver.WONDERIF :
                Functions.___npcTalk(p, n, "Sorry friend, I'm busy, go bug the guards,", "I'm sure they'll give ya the time of day.");
                Functions.___message(p, "The cart driver chuckles to himself.");
                int ok = Functions.___showMenu(p, n, "Can I help you at all?", "Can you get me the heck out of here please?");
                if (ok == 0) {
                    getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.HELPYOU);
                } else
                    if (ok == 1) {
                        getOutWithAnaInCart(p, n, Tourist_Trap_Mechanism.CartDriver.HECKOUT);
                    }

                break;
            case Tourist_Trap_Mechanism.CartDriver.HECKOUT :
                Functions.___npcTalk(p, n, "No way, and if you bug me again, I'm gonna call the guards.");
                Functions.___message(p, "The cart driver goes back to his work.");
                break;
        }
    }

    private void driverCallGuards(Player p, Npc n) {
        int succeed = DataConversions.random(0, 1);
        Functions.___npcTalk(p, n, "Guards! Guards!");
        if (succeed == 0) {
            Functions.___message(p, "Some guards notice you and come over.");
            Npc mercenary = Functions.getNearestNpc(p, NpcId.MERCENARY.id(), 15);
            if (mercenary != null) {
                mercenary = Functions.spawnNpc(p.getWorld(), NpcId.MERCENARY.id(), p.getX(), p.getY(), 60000);
                Functions.sleep(1000);
            }
            Functions.___npcTalk(p, mercenary, "Oi, what are you two doing?");
            mercenary.startCombat(p);
            Functions.___message(p, "The Guards search you!", "More guards rush to catch you.", "You are roughed up a bit by the guards as you're manhandlded to a cell.");
            Functions.___npcTalk(p, mercenary, "Into the cell you go! I hope this teaches you a lesson.");
            p.teleport(89, 801);
        } else {
            Functions.___message(p, "You quickly slope away and hide from the guards.");
        }
    }

    private void repeatLiftDialogue(Player p, Npc n, int cID) {
        switch (cID) {
            case Tourist_Trap_Mechanism.RepeatLift.THING :
                Functions.___npcTalk(p, n, "It is quite clearly a lift.", "Any fool can see that it's used to transport rock to the surface.");
                int opt = Functions.___showMenu(p, n, "Can I use this?", "Ok, thanks.");
                if (opt == 0) {
                    repeatLiftDialogue(p, n, Tourist_Trap_Mechanism.RepeatLift.USETHIS);
                }
                break;
            case Tourist_Trap_Mechanism.RepeatLift.USETHIS :
                Functions.___npcTalk(p, n, "Of course not, you'd be doing me out of a job.", "Anyway, you haven't got any barrels that need to go to the surface.", "Now, move along and get some work done before you get a good beating.");
                int options = // do not send over
                Functions.___showMenu(p, n, false, "What is this thing?,", "Ok, thanks.");
                if (options == 0) {
                    Functions.___playerTalk(p, n, "What is this thing?");
                    repeatLiftDialogue(p, n, Tourist_Trap_Mechanism.RepeatLift.THING);
                } else
                    if (options == 1) {
                        Functions.___playerTalk(p, n, "Ok, thanks.");
                    }

                break;
        }
    }

    private void failCaveAnaInBarrel(Player p, Npc n) {
        if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
            n = Functions.spawnNpc(p.getWorld(), NpcId.MERCENARY.id(), p.getX(), p.getY(), 60000);
            Functions.sleep(650);
            Functions.___npcTalk(p, n, "Hey, where d'ya think you're going with that Barrel?");
            p.message("A guard comes over and takes the barrel off you.");
            Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
            Functions.___npcTalk(p, n, "'Cor! This barrel is really heavy!", "Have you been mining lead?", "Har, har har!");
            Functions.___message(p, "@gre@Ana: How rude! Why I ought to teach you a lesson.");
            Functions.___npcTalk(p, n, "What was that!");
            p.message("The guards kick the barrel open.!");
            Npc ana = Functions.spawnNpc(p.getWorld(), NpcId.ANA.id(), p.getX(), p.getY(), 30000);
            Functions.sleep(650);
            Functions.___npcTalk(p, ana, "How dare you say that I'm as heavy as lead?");
            p.message("The guards drag Ana of and then throw you into a cell.");
            if (ana != null) {
                ana.remove();
            }
            Functions.___message(p, "@yel@Guards: Into the cell you go!", "@yel@I hope this teaches you a lesson.");
            if (n != null) {
                n.remove();
            }
            p.teleport(75, 3626);
        }
    }

    private void anaToLift(Player p, Npc n) {
        p.message("The guard notices the barrel (with Ana in it) that you're carrying.");
        Functions.___npcTalk(p, n, "Hey, that Barrel looks heavy, do you need a hand?");
        int menu = Functions.___showMenu(p, n, "Yes please.", "No thanks, I can manage.");
        if (menu == 0) {
            p.message("The guard comes over and helps you. He takes one end of the barrel.");
            Functions.___npcTalk(p, n, "Blimey! This is heavy!");
            Functions.___message(p, "@gre@Ana in a barrel: Why you cheeky....!", "The guard looks around suprised at Ana's outburst.");
            Functions.___npcTalk(p, n, "What was that?");
            Functions.___playerTalk(p, n, "Oh, it was nothing.");
            Functions.___npcTalk(p, n, "I could have sworn I heard something!");
            p.message("@gre@Ana in a barrel: Yes you did you ignaramus.");
            Functions.___npcTalk(p, n, "What was that you said?");
            int opt = Functions.___showMenu(p, n, "I said you were very gregarious!", "Oh, nothing.");
            if (opt == 0) {
                Functions.___message(p, "@gre@Ana in a barrel: You creep!");
                Functions.___npcTalk(p, n, "Oh, right, how very nice of you to say so.");
                p.message("The guard seems flattered.");
                Functions.___npcTalk(p, n, "Anyway, let's get this barrel up to the surface, plenty more work to you to do!");
                p.message("The guard places the barrel carefully on the lift platform.");
                Functions.___npcTalk(p, n, "Oh, there's no one operating the lift up top, hope this barrel isn't urgent?", "You'd better get back to work!");
                Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                if (!p.getCache().hasKey("ana_lift")) {
                    p.getCache().store("ana_lift", true);
                }
                // use cache again maybe?
            } else
                if (opt == 1) {
                    Functions.___npcTalk(p, n, "I heard you say something, now spit it out!");
                }

        } else
            if (menu == 1) {
                Functions.___npcTalk(p, n, "Ok, fair enough, I was only offering.");
            }

    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return ((((obj.getID() == 1006) && (item.getID() == ItemId.BRONZE_BAR.id())) || ((obj.getID() == Tourist_Trap_Mechanism.MINING_CART) && (item.getID() == ItemId.ANA_IN_A_BARREL.id()))) || ((obj.getID() == Tourist_Trap_Mechanism.LIFT_PLATFORM) && (item.getID() == ItemId.ANA_IN_A_BARREL.id()))) || ((obj.getID() == Tourist_Trap_Mechanism.MINING_CART_ABOVE) && (item.getID() == ItemId.ANA_IN_A_BARREL.id()));
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 1006) && (item.getID() == ItemId.BRONZE_BAR.id())) {
                        if (Functions.hasItem(p, ItemId.PROTOTYPE_DART_TIP.id())) {
                            p.message("You have already made the prototype dart tip.");
                            p.message("You don't need to make another one.");
                        } else
                            if (Functions.hasItem(p, ItemId.PROTOTYPE_THROWING_DART.id())) {
                                p.message("You have already made the prototype dart.");
                                p.message("You don't need to make another one.");
                            } else {
                                makeDartTip(p, obj);
                            }

                    } else
                        if ((obj.getID() == Tourist_Trap_Mechanism.MINING_CART) && (item.getID() == ItemId.ANA_IN_A_BARREL.id())) {
                            Functions.___message(p, "You carefully place Ana in the barrel into the mine cart.", "Soon the cart moves out of sight and then it returns.");
                            Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                            if (!p.getCache().hasKey("ana_cart")) {
                                p.getCache().store("ana_cart", true);
                            }
                        } else
                            if ((obj.getID() == Tourist_Trap_Mechanism.LIFT_PLATFORM) && (item.getID() == ItemId.ANA_IN_A_BARREL.id())) {
                                Npc n = Functions.getNearestNpc(p, NpcId.MERCENARY_LIFTPLATFORM.id(), 5);
                                if (n != null) {
                                    anaToLift(p, n);
                                }
                            } else
                                if ((obj.getID() == Tourist_Trap_Mechanism.MINING_CART_ABOVE) && (item.getID() == ItemId.ANA_IN_A_BARREL.id())) {
                                    Functions.___message(p, "You place Ana (In the barrel) carefully on the cart.", "This was the last barrel to go on the cart,", "but the cart driver doesn't seem to be in any rush to get going.", "And the desert heat will soon get to Ana.");
                                    Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                                    if (!p.getCache().hasKey("ana_in_cart")) {
                                        p.getCache().store("ana_in_cart", true);
                                    }
                                }



                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
        return Functions.compareItemsIds(item1, item2, ItemId.FEATHER.id(), ItemId.PROTOTYPE_DART_TIP.id());
    }

    @Override
    public GameStateEvent onInvUseOnItem(Player p, Item item1, Item item2) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.compareItemsIds(item1, item2, ItemId.FEATHER.id(), ItemId.PROTOTYPE_DART_TIP.id())) {
                        attachFeathersToPrototype(p, item1, item2);
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPickup(Player p, GroundItem item) {
        return item.getID() == ItemId.ANA_IN_A_BARREL.id();
    }

    @Override
    public GameStateEvent onPickup(Player player, GroundItem item) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (item.getID() == ItemId.ANA_IN_A_BARREL.id()) {
                        // non-kosher, unsure if item despawned when killed or gave dialogue on this condition
                        player.message("@gre@Ana: Don't think for one minute ...");
                        player.message("@gre@Ana: You can just come back and pick me up");
                        player.message("Ana goes out running away");
                        player.getWorld().unregisterItem(item);
                        return null;
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockDrop(Player p, Item i) {
        return i.getID() == ItemId.ANA_IN_A_BARREL.id();
    }

    @Override
    public GameStateEvent onDrop(Player p, Item i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ItemId.ANA_IN_A_BARREL.id()) {
                        if (p.getQuestStage(Quests.TOURIST_TRAP) == (-1)) {
                            Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                            return null;
                        }
                        p.message("Are you sure you want to drop this?");
                        int menu = Functions.___showMenu(p, "Yes, I'm sure.", "Erm, no I've had second thoughts.");
                        if (menu == 0) {
                            if (outsideCamp(p)) {
                                Functions.___message(p, "@gre@Ana: You can't drop me here!", "@gre@Ana: I'll die in the desert on my own!", "@gre@Ana: Take me back to the Shantay pass.");
                                return null;
                            }
                            int diffX = 0;
                            // inside mining prison cell
                            if (((p.getX() >= 72) && (p.getX() <= 77)) && ((p.getY() >= 3613) && (p.getY() <= 3631))) {
                                // mercenary does not get placed in jail if player is there
                                diffX = -8;
                            }
                            Functions.___message(p, "You drop the barrel to the floor and Ana gets out.");
                            Functions.removeItem(p, ItemId.ANA_IN_A_BARREL.id(), 1);
                            Npc Ana = Functions.spawnNpc(p.getWorld(), NpcId.ANA.id(), p.getX(), p.getY(), 20000);
                            Functions.sleep(650);
                            Functions.___npcTalk(p, Ana, "How dare you put me in that barrel you barbarian!");
                            Functions.___message(p, "Ana's outburst attracts the guards, they come running over.");
                            Npc guard = Functions.getNearestNpc(p, NpcId.MERCENARY.id(), 15);
                            if ((guard == null) || guard.inCombat()) {
                                guard = Functions.spawnNpc(p.getWorld(), NpcId.MERCENARY.id(), p.getX() + diffX, p.getY(), 30000);
                            }
                            Functions.sleep(650);
                            Functions.___npcTalk(p, guard, "Hey! What's going on here then?");
                            if (diffX == 0)
                                guard.startCombat(p);

                            Functions.___message(p, "The guards drag Ana away and then throw you into a cell.");
                            p.teleport(75, 3626);
                        } else
                            if (menu == 1) {
                                Functions.___message(p, "You think twice about dropping the barrel to the floor.");
                            }

                    }
                    return null;
                });
            }
        };
    }

    private boolean outsideCamp(Player p) {
        return ((p.getY() < 795) || ((p.getX() >= 92) && ((p.getY() >= 795) && (p.getY() <= 814)))) || ((p.getX() <= 78) && ((p.getY() >= 795) && (p.getY() <= 814)));
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.MINING_CART_DRIVER.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.MINING_CART_DRIVER.id()) {
                        if (n.getID() == NpcId.MINING_CART_DRIVER.id()) {
                            if (p.getQuestStage(Quests.TOURIST_TRAP) == (-1)) {
                                Functions.___npcTalk(p, n, "Don't trouble me, can't you see I'm busy?");
                                return null;
                            }
                            if (p.getCache().hasKey("rescue")) {
                                Functions.___npcTalk(p, n, "Hurry up, get in the cart or I'll go without you!");
                                return null;
                            }
                            if (p.getCache().hasKey("ana_in_cart")) {
                                getOutWithAnaInCart(p, n, -1);
                                return null;
                            }
                            if (Functions.hasItem(p, ItemId.ANA_IN_A_BARREL.id())) {
                                Functions.___npcTalk(p, n, "What're you doing carrying that big barrel around?", "Put it in the back of the cart like all the others!");
                                return null;
                            }
                            p.message("The cart driver is busy loading the cart up ...");
                        }
                    }
                    return null;
                });
            }
        };
    }

    class RepeatLift {
        static final int USETHIS = 0;

        static final int THING = 1;
    }

    class CartDriver {
        static final int PSSST = 0;

        static final int PSSST2 = 1;

        static final int PSSST3 = 2;

        static final int PSSSTFINAL = 3;

        static final int OKSORRY = 4;

        static final int NICECART = 5;

        static final int WAGON = 6;

        static final int HELPYOU = 7;

        static final int WONDERIF = 8;

        static final int HECKOUT = 9;
    }
}

