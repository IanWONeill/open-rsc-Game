package com.openrsc.server.plugins.npcs.alkharid;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ShantayPassNpcs implements ShopInterface , ObjectActionListener , PickupListener , TalkToNpcListener , ObjectActionExecutiveListener , PickupExecutiveListener , TalkToNpcExecutiveListener {
    private static final Logger LOGGER = LogManager.getLogger(ShantayPassNpcs.class);

    private static int ASSISTANT = 720;

    private static int SHANTAY_DISCLAIMER = ItemId.A_FREE_SHANTAY_DISCLAIMER.id();

    private static int SHANTAY_STANDING_GUARD = 719;

    private static int SHANTAY_MOVING_GUARD = 717;

    private static int SHANTAY = 549;

    private static int BANK_CHEST = 942;

    private static int STONE_GATE = 916;

    private static int SHANTAY_PASS = ItemId.SHANTAY_DESERT_PASS.id();

    private final Shop shop = new Shop(false, 10000, 120, 70, 3, new Item(ItemId.JUG_OF_WATER.id(), 15), new Item(ItemId.BOWL_OF_WATER.id(), 15), new Item(ItemId.BUCKET_OF_WATER.id(), 15), new Item(ItemId.EMPTY_WATER_SKIN.id(), 15), new Item(ItemId.FULL_WATER_SKIN.id(), 15), new Item(ItemId.DESERT_ROBE.id(), 15), new Item(ItemId.DESERT_SHIRT.id(), 15), new Item(ItemId.DESERT_BOOTS.id(), 15), new Item(ItemId.TINDERBOX.id(), 5), new Item(ItemId.CHISEL.id(), 5), new Item(ItemId.HAMMER.id(), 5), new Item(ItemId.BRONZE_BAR.id(), 5), new Item(ItemId.FEATHER.id(), 200), new Item(ItemId.SHANTAY_DESERT_PASS.id(), 20), new Item(ItemId.KNIFE.id(), 20));

    private boolean inJail = false;

    @Override
    public GameStateEvent onTalkToNpc(final Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == ShantayPassNpcs.SHANTAY_STANDING_GUARD) {
                        Functions.___npcTalk(p, n, "Hello there!", "What can I do for you?");
                        int menu = Functions.___showMenu(p, n, "I'd like to go into the desert please.", "Nothing thanks.");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "Of course!");
                            if (!Functions.hasItem(p, ShantayPassNpcs.SHANTAY_PASS)) {
                                Functions.___npcTalk(p, n, "You'll need a Shantay pass to go through the gate into the desert.", "See Shantay, he'll sell you one for a very reasonable price.");
                            } else {
                                int menus;
                                if (!Functions.hasItem(p, ShantayPassNpcs.SHANTAY_DISCLAIMER)) {
                                    Functions.___message(p, "There is a large poster on the wall near the gateway. It reads..", "@gre@The Desert is a VERY Dangerous place...do not enter if you are scared of dying.", "@gre@Beware of high temperatures, sand storms, robbers, and slavers...", "@gre@No responsibility is taken by Shantay ", "@gre@If anything bad should happen to you in any circumstances whatsoever.", "That seems pretty scary! Are you sure you want to go through?");
                                    menus = Functions.___showMenu(p, "Yeah, that poster doesn't scare me!", "No, I'm having serious second thoughts now.");
                                } else {
                                    Functions.___message(p, "A poster on the wall says exactly the same as the disclaimer.", "Are you sure you want to go through?");
                                    menus = Functions.___showMenu(p, "Yeah, I'm not scared!", "No, I'm having serious second thoughts now.");
                                }
                                if (menus == 0) {
                                    final Npc npc = new Npc(n.getWorld(), 719, 63, 731);
                                    npc.setShouldRespawn(false);
                                    p.getWorld().registerNpc(npc);
                                    p.getWorld().getServer().getGameEventHandler().add(new SingleEvent(p.getWorld(), null, 60000, "Shantay Pass Talk Delay") {
                                        public void action() {
                                            Functions.___npcTalk(p, npc, "Right, time for dinner!");
                                            npc.remove();
                                        }
                                    });
                                    Functions.sleep(1000);
                                    Functions.___npcTalk(p, npc, "Can I see your Shantay Desert Pass please.");
                                    p.message("You hand over a Shantay Pass.");
                                    Functions.removeItem(p, ShantayPassNpcs.SHANTAY_PASS, 1);
                                    Functions.___playerTalk(p, npc, "Sure, here you go!");
                                    if (!Functions.hasItem(p, ShantayPassNpcs.SHANTAY_DISCLAIMER)) {
                                        Functions.___npcTalk(p, npc, "Here, have a disclaimer...", "It means that Shantay isn't responsible if you die in the desert.");
                                        p.message("The guard gives you a disclaimer.");
                                        Functions.addItem(p, ShantayPassNpcs.SHANTAY_DISCLAIMER, 1);
                                    }
                                    p.message("you go through the gate");
                                    p.teleport(62, 735);
                                } else
                                    if (menus == 1) {
                                        Functions.___message(p, "You decide that your visit to the desert can be postponed..");
                                        p.message("Perhaps indefinitely!");
                                    }

                            }
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "Ok then, have a nice day.");
                            }

                        return null;
                    }
                    if (n.getID() == ShantayPassNpcs.SHANTAY_MOVING_GUARD) {
                        Functions.___npcTalk(p, n, "Go talk to Shantay or one of his assistants.", "I'm on duty and I don't have time to talk to the likes of you!");
                        Functions.___message(p, "The guard seems quite bad tempered,", "probably from having to wear heavy armour in this intense heat.");
                        return null;
                    }
                    if (n.getID() == ShantayPassNpcs.SHANTAY) {
                        if (DataConversions.random(0, 25) == 0) {
                            // 1 in 25 chance to drop kebab recipe
                            GroundItem groundItem = new GroundItem(p.getWorld(), 1120, n.getX(), n.getY(), 1, p);
                            p.getWorld().registerItem(groundItem);
                        }
                        Functions.___npcTalk(p, n, "Hello Effendi, I am Shantay.");
                        if (!Functions.hasItem(p, ShantayPassNpcs.SHANTAY_DISCLAIMER)) {
                            Functions.___npcTalk(p, n, "I see you're new!", "Make sure you read the poster before going into the desert.");
                        }
                        if (p.getQuestStage(Quests.TOURIST_TRAP) == 0) {
                            Functions.___npcTalk(p, n, "There is a heartbroken Mother just past the gates and in the Desert.", "Her name is Irena and she mourns her lost Daughter. Such a shame.");
                        }
                    } else
                        if (n.getID() == ShantayPassNpcs.ASSISTANT) {
                            Functions.___npcTalk(p, n, "Hello Effendi, I am a Shantay Pass Assistant.");
                            if (!Functions.hasItem(p, ShantayPassNpcs.SHANTAY_DISCLAIMER)) {
                                Functions.___npcTalk(p, n, "I see you're new!", "Make sure you read the poster before going into the desert.");
                            }
                        }

                    int menu = Functions.___showMenu(p, n, "What is this place?", "Can I see what you have to sell please?", "I must be going.");
                    if (menu == 0) {
                        if (inJail) {
                            Functions.___npcTalk(p, n, "You should be in jail!", "Well, no doubt the authorities in Port Sarim know what they're doing.", "But if you get into any more trouble, you'll be stuck back in jail.");
                            inJail = false;
                            return null;
                        }
                        Functions.___npcTalk(p, n, "This is the pass of Shantay.", "Mr Shantay guards this area with his men.", "He is responsible for keeping this pass open and repaired.", "He and his men prevent outlaws from getting out of the desert.", "And he stops the inexperienced from a dry death in the sands.", "Which would you say you were?");
                        int menu2 = Functions.___showMenu(p, n, "I am definitely an outlaw, prepare to die!", "I am a little inexperienced.", "Er, neither, I'm an adventurer.");
                        if (menu2 == 0) {
                            Functions.___npcTalk(p, n, "Ha, very funny.....", "Guards arrest him!");
                            Functions.___message(p, "The guards arrest you and place you in the jail.");
                            Functions.___npcTalk(p, n, "You'll have to stay in there until you pay the fine of five gold pieces.", "Do you want to pay now?");
                            inJail = true;
                            int menu6 = Functions.___showMenu(p, n, "Yes, Ok.", "No thanks, you're not having my money.");
                            if (menu6 == 0) {
                                Functions.___npcTalk(p, n, "Good, I see that you have come to your senses.");
                                if (p.getInventory().countId(ItemId.COINS.id()) >= 5) {
                                    Functions.___message(p, "You hand over five gold pieces to Shantay.");
                                    Functions.___npcTalk(p, n, "Great Effendi, now please try to keep the peace.");
                                    Functions.___message(p, "The assistant unlocks the door to the cell.");
                                    Functions.removeItem(p, ItemId.COINS.id(), 5);
                                    inJail = false;
                                } else {
                                    Functions.___npcTalk(p, n, "You don't have that kind of cash on you I see.", "But perhaps you have some in your bank?", "You can transfer some money from your bank and pay the fine.", "or you will be sent to a maximum security prison in Port Sarim.", "Which is it going to be?");
                                    int menu8 = Functions.___showMenu(p, n, "I'll pay the fine.", "I'm not paying the fine!");
                                    if (menu8 == 0) {
                                        if (p.isIronMan(2)) {
                                            p.message("As an Ultimate Iron Man, you cannot use the bank.");
                                            return null;
                                        }
                                        Functions.___npcTalk(p, n, "Ok then..., you'll need access to your bank.");
                                        p.setAccessingBank(true);
                                        ActionSender.showBank(p);
                                        inJail = false;
                                    } else
                                        if (menu8 == 1) {
                                            Functions.___npcTalk(p, n, "You are to be transported to a maximum security prison in Port Sarim.", "I hope you've learnt an important lesson from this.");
                                            p.teleport(281, 665, false);
                                        }

                                }
                            } else
                                if (menu6 == 1) {
                                    Functions.___npcTalk(p, n, "You have a choice.", "You can either pay five gold pieces or...", "You can be transported to a maximum security prison in Port Sarim.", "Will you pay the five gold pieces?");
                                    int menu7 = Functions.___showMenu(p, n, "Yes, Ok.", "No, do your worst!");
                                    if (menu7 == 0) {
                                        Functions.___npcTalk(p, n, "Good, I see that you have come to your senses.");
                                        if (p.getInventory().countId(ItemId.COINS.id()) >= 5) {
                                            Functions.___message(p, "You hand over five gold pieces to Shantay.");
                                            Functions.___npcTalk(p, n, "Great Effendi, now please try to keep the peace.");
                                            Functions.___message(p, "The assistant unlocks the door to the cell.");
                                            Functions.removeItem(p, ItemId.COINS.id(), 5);
                                            inJail = false;
                                        } else {
                                            Functions.___npcTalk(p, n, "You don't have that kind of cash on you I see.", "But perhaps you have some in your bank?", "You can transfer some money from your bank and pay the fine.", "or you will be sent to a maximum security prison in Port Sarim.", "Which is it going to be?");
                                            int menu8 = Functions.___showMenu(p, n, "I'll pay the fine.", "I'm not paying the fine!");
                                            if (menu8 == 0) {
                                                if (p.isIronMan(2)) {
                                                    p.message("As an Ultimate Iron Man, you cannot use the bank.");
                                                    return null;
                                                }
                                                Functions.___npcTalk(p, n, "Ok then..., you'll need access to your bank.");
                                                p.setAccessingBank(true);
                                                ActionSender.showBank(p);
                                                inJail = false;
                                            } else
                                                if (menu8 == 1) {
                                                    Functions.___npcTalk(p, n, "You are to be transported to a maximum security prison in Port Sarim.", "I hope you've learnt an important lesson from this.");
                                                    p.teleport(281, 665, false);
                                                }

                                        }
                                    } else
                                        if (menu7 == 1) {
                                            Functions.___npcTalk(p, n, "You are to be transported to a maximum security prison in Port Sarim.", "I hope you've learnt an important lesson from this.");
                                            p.teleport(281, 665, false);
                                        }

                                }

                        } else
                            if (menu2 == 1) {
                                Functions.___npcTalk(p, n, "Can I recommend that you purchase a full waterskin and a knife!", "These items will no doubt save your life...", "A waterskin will keep water from evaporating in the desert.", "And a keen woodsman with a knife can extract the juice from a cactus.", "Before you go into the desert, it's advisable to wear desert clothes.", "It's very hot in the desert and you'll surely cook if you wear armour.", "To  keep the pass open and bandit free, we charge a small toll of five gold pieces.", "You can buy a desert pass from me, just ask me to open the shop.", "You can also use our free banking services by clicking on the chest.");
                                int menu5 = Functions.___showMenu(p, n, "Can I see what you have to sell please?", "I must be going.");
                                if (menu5 == 0) {
                                    Functions.___npcTalk(p, n, "Absolutely Effendi!");
                                    p.setAccessingShop(shop);
                                    ActionSender.showShop(p, shop);
                                } else
                                    if (menu5 == 1) {
                                        Functions.___npcTalk(p, n, " So long...");
                                    }

                            } else
                                if (menu2 == 2) {
                                    Functions.___npcTalk(p, n, "Great, I have just the thing for the desert adventurer.", "I sell desert clothes which will keep you cool in the heat of the desert.", "I also sell waterskins so that you won't die in the desert.", "A waterskin and a knife help you survive from the juice of a cactus.", "Use the chest to store your items, we'll take them to the bank.", "It's hot in the desert, you'll bake in all that armour.", "To keep the pass open we ask for 5 gold pieces.", "and we give you a Shantay Pass, just ask to see what I sell to buy one.");
                                    int menu3 = Functions.___showMenu(p, n, "Can I see what you have to sell please?", "I must be going.", "Why do I have to pay to go into the desert?");
                                    if (menu3 == 0) {
                                        Functions.___npcTalk(p, n, "Absolutely Effendi!");
                                        p.setAccessingShop(shop);
                                        ActionSender.showShop(p, shop);
                                    } else
                                        if (menu3 == 1) {
                                            Functions.___npcTalk(p, n, "So long...");
                                        } else
                                            if (menu3 == 2) {
                                                Functions.___message(p, "The Assistant opens his arms wide as if too embrace you.");
                                                Functions.___npcTalk(p, n, "Effendi, you insult me!", "We are not interested in making a profit from you!", "I merely seek to cover my expenses in keeping this pass open.", "There is repair work to carry out and also the mens wages to consider.", "For the paltry sum of 5 Gold pieces, I think we offer a great service.");
                                                int menu4 = Functions.___showMenu(p, n, "Can I see what you have to sell please?", "I must be going.");
                                                if (menu4 == 0) {
                                                } else
                                                    if (menu4 == 1) {
                                                        Functions.___npcTalk(p, n, " Absolutely Effendi!");
                                                        p.setAccessingShop(shop);
                                                        ActionSender.showShop(p, shop);
                                                    }

                                            }


                                }


                    } else
                        if (menu == 1) {
                            Functions.___npcTalk(p, n, "Absolutely Effendi!");
                            p.setAccessingShop(shop);
                            ActionSender.showShop(p, shop);
                        } else
                            if (menu == 2) {
                                Functions.___npcTalk(p, n, "So long...");
                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return (((n.getID() == ShantayPassNpcs.ASSISTANT) || (n.getID() == ShantayPassNpcs.SHANTAY)) || (n.getID() == ShantayPassNpcs.SHANTAY_MOVING_GUARD)) || (n.getID() == ShantayPassNpcs.SHANTAY_STANDING_GUARD);
    }

    @Override
    public Shop[] getShops(World world) {
        return new Shop[]{ shop };
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        if ((obj.getID() == ShantayPassNpcs.BANK_CHEST) || ((obj.getID() == ShantayPassNpcs.STONE_GATE) && (player.getY() < 735))) {
            return true;
        }
        return false;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == ShantayPassNpcs.BANK_CHEST) {
                        if (p.isIronMan(2)) {
                            p.message("As an Ultimate Iron Man, you cannot use the bank.");
                            return null;
                        }
                        if (p.getCache().hasKey("bank_pin") && (!p.getAttribute("bankpin", false))) {
                            String pin = Functions.___getBankPinInput(p);
                            if (pin == null) {
                                return null;
                            }
                            try {
                                PreparedStatement statement = p.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + p.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                                statement.setString(1, p.getUsername());
                                ResultSet result = statement.executeQuery();
                                if (result.next()) {
                                    pin = DataConversions.hashPassword(pin, result.getString("salt"));
                                }
                            } catch (SQLException e) {
                                ShantayPassNpcs.LOGGER.catching(e);
                            }
                            if (!p.getCache().getString("bank_pin").equals(pin)) {
                                ActionSender.sendBox(p, "Incorrect bank pin", false);
                                return null;
                            }
                            p.setAttribute("bankpin", true);
                            ActionSender.sendBox(p, "Bank pin correct", false);
                        }
                        if (!p.getCache().hasKey("shantay-chest")) {
                            Functions.___message(p, "This chest is used by Shantay and his men.", "They can put things in and out of storage for you.", "You open the bank.");
                            p.getCache().store("shantay-chest", true);
                        }
                        p.setAccessingBank(true);
                        ActionSender.showBank(p);
                    }
                    if ((obj.getID() == ShantayPassNpcs.STONE_GATE) && (p.getY() < 735)) {
                        if (command.equals("go through")) {
                            int menu;
                            if (!Functions.hasItem(p, ShantayPassNpcs.SHANTAY_DISCLAIMER)) {
                                Functions.___message(p, "There is a large poster on the wall near the gateway. It reads..", "@gre@The Desert is a VERY Dangerous place...do not enter if you are scared of dying.", "@gre@Beware of high temperatures, sand storms, robbers, and slavers...", "@gre@No responsibility is taken by Shantay ", "@gre@If anything bad should happen to you in any circumstances whatsoever.", "That seems pretty scary! Are you sure you want to go through?");
                                menu = Functions.___showMenu(p, "Yeah, that poster doesn't scare me!", "No, I'm having serious second thoughts now.");
                            } else {
                                Functions.___message(p, "A poster on the wall says exactly the same as the disclaimer.", "Are you sure you want to go through?");
                                menu = Functions.___showMenu(p, "Yeah, I'm not scared!", "No, I'm having serious second thoughts now.");
                            }
                            Npc shantayGuard = Functions.getNearestNpc(p, ShantayPassNpcs.SHANTAY_STANDING_GUARD, 5);
                            if (menu == 0) {
                                if (!Functions.hasItem(p, ShantayPassNpcs.SHANTAY_PASS)) {
                                    Functions.___message(p, "A guard stops you on your way out of the gate...");
                                    if (shantayGuard != null) {
                                        Functions.___npcTalk(p, shantayGuard, "You need a Shantay pass to get through this gate.", "See Shantay, he will sell you one for a very reasonable price.");
                                    } else {
                                        p.message("Shantay guard seem to be busy at the moment.");
                                    }
                                } else {
                                    if (shantayGuard != null) {
                                        Functions.___npcTalk(p, shantayGuard, "Can I see your Shantay Desert Pass please.");
                                        p.message("You hand over a Shantay Pass.");
                                        Functions.removeItem(p, ShantayPassNpcs.SHANTAY_PASS, 1);
                                        Functions.___playerTalk(p, shantayGuard, "Sure, here you go!");
                                        if (!Functions.hasItem(p, ShantayPassNpcs.SHANTAY_DISCLAIMER)) {
                                            Functions.___npcTalk(p, shantayGuard, "Here, have a disclaimer...", "It means that Shantay isn't responsible if you die in the desert.");
                                            p.message("The guard gives you a disclaimer.");
                                            Functions.addItem(p, ShantayPassNpcs.SHANTAY_DISCLAIMER, 1);
                                        }
                                        p.message("you go through the gate");
                                        p.teleport(62, 735);
                                    } else {
                                        p.message("Shantay guard seem to be busy at the moment.");
                                    }
                                }
                            } else
                                if (menu == 1) {
                                    Functions.___message(p, "You decide that your visit to the desert can be postponed..");
                                    p.message("Perhaps indefinitely!");
                                }

                        } else
                            if (command.equals("look")) {
                                Functions.___message(p, "You look at the huge Stone Gate.", "On the gate is a large poster, it reads.", "@gre@The Desert is a VERY Dangerous place...do not enter if you are scared of dying.", "@gre@Beware of high temperatures, sand storms, robbers, and slavers...", "@gre@No responsibility is taken by Shantay ", "@gre@If anything bad should happen to you in any circumstances whatsoever.", "Despite this warning lots of people seem to pass through the gate.");
                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return i.getID() == ShantayPassNpcs.SHANTAY_DISCLAIMER;
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ShantayPassNpcs.SHANTAY_DISCLAIMER) {
                        p.message("This looks very important indeed, would you like to read it now?");
                        Functions.addItem(p, ShantayPassNpcs.SHANTAY_DISCLAIMER, 1);
                        i.remove();
                        int menu = Functions.___showMenu(p, "Yes, I'll read it now!", "No thanks, it'll keep!");
                        if (menu == 0) {
                            ActionSender.sendBox(p, "@red@*** Shantay Disclaimer***% %@gre@The Desert is a VERY Dangerous place.% %@red@Do not enter if you're scared of dying.% %@gre@Beware of high temperatures, sand storms, and slavers% %@red@No responsibility is taken by Shantay% %@gre@If anything bad happens to you under any circumstances.", true);
                        } else
                            if (menu == 1) {
                                p.message("You decide not to read the disclaimer.");
                            }

                    }
                    return null;
                });
            }
        };
    }
}

