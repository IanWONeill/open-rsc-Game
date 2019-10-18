package com.openrsc.server.plugins.npcs.yanille;


import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class SidneySmith implements InvUseOnNpcListener , TalkToNpcListener , InvUseOnNpcExecutiveListener , TalkToNpcExecutiveListener {
    public static final int SIDNEY_SMITH = 778;

    /**
     * ITEM UNCERTED
     */
    public static final int PRAYER_RESTORE_POT = 483;

    public static final int SUPER_ATTACK_POT = 486;

    public static final int SUPER_STRENGTH_POT = 492;

    public static final int SUPER_DEFENSE_POT = 495;

    public static final int DRAGON_BONES = 814;

    public static final int LIMPWURT_ROOT = 220;

    /**
     * ITEM CERTED
     */
    public static final int PRAYER_CERT = 1272;

    public static final int SUPER_ATTACK_CERT = 1273;

    public static final int SUPER_DEFENSE_CERT = 1274;

    public static final int SUPER_STRENGTH_CERT = 1275;

    public static final int DRAGON_BONES_CERT = 1270;

    public static final int LIMPWURT_ROOT_CERT = 1271;

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == SidneySmith.SIDNEY_SMITH;
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == SidneySmith.SIDNEY_SMITH) {
                        sidneyCert(p, n, -1);
                    }
                    return null;
                });
            }
        };
    }

    private void sidneyCert(Player p, Npc n, int cID) {
        if (cID == (-1)) {
            Functions.___npcTalk(p, n, "Hello, I'm Sidney Smith, the certification Clerk.", "How can I help you ?");
            int menu = Functions.___showMenu(p, n, "I'd like to certificate some goods please.", "I'd like to change some certificates for goods please.", "What is certification ?", "Which goods do you certificate ?");
            if (menu == 0) {
                sidneyCert(p, n, SidneySmith.Sidney.GOODS_TO_CERTIFICATE);
            } else
                if (menu == 1) {
                    sidneyCert(p, n, SidneySmith.Sidney.CERTIFICATE_TO_GOODS);
                } else
                    if (menu == 2) {
                        sidneyCert(p, n, SidneySmith.Sidney.WHAT_IS_CERTIFICATION);
                    } else
                        if (menu == 3) {
                            sidneyCert(p, n, SidneySmith.Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE);
                        }



        }
        switch (cID) {
            case SidneySmith.Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE :
                Functions.___npcTalk(p, n, "Well, I can certificate the following items.", "Prayer Restore Potion,", "Super Attack Potion,", "Super Defense Potion,", "Super Strength Potion,", "Dragon Bones,", "and Limpwurt Root.");
                int SUB_MENU_ONE = Functions.___showMenu(p, n, "How many items do you need to make a certificate.", "I'd like to certificate some goods please.", "I'd like to change some certificates for goods please.", "Ok, thanks.");
                if (SUB_MENU_ONE == 0) {
                    sidneyCert(p, n, SidneySmith.Sidney.HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE);
                } else
                    if (SUB_MENU_ONE == 1) {
                        sidneyCert(p, n, SidneySmith.Sidney.GOODS_TO_CERTIFICATE);
                    } else
                        if (SUB_MENU_ONE == 2) {
                            sidneyCert(p, n, SidneySmith.Sidney.CERTIFICATE_TO_GOODS);
                        }


                break;
            case SidneySmith.Sidney.WHAT_IS_CERTIFICATION :
                Functions.___npcTalk(p, n, "It's quite easy really..", "You swap some goods for certificates which are easier to store.", "I specialise in certificating very rare items.", "The kinds of items only Legendary Runescape citizens will own.");
                int SUB_MENU_TWO = Functions.___showMenu(p, n, "I'd like to certificate some goods please.", "I'd like to change some certificates for goods please.", "Ok thanks.");
                if (SUB_MENU_TWO == 0) {
                    sidneyCert(p, n, SidneySmith.Sidney.GOODS_TO_CERTIFICATE);
                } else
                    if (SUB_MENU_TWO == 1) {
                        sidneyCert(p, n, SidneySmith.Sidney.CERTIFICATE_TO_GOODS);
                    }

                break;
            case SidneySmith.Sidney.HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE :
                Functions.___npcTalk(p, n, "Well, you need at the least five items to make a certificate.", "We'll turn any five items into one certificate.", "It makes storage and transportation much easier.");
                int SUB_MENU_THREE = Functions.___showMenu(p, n, "Which goods do you certificate?", "Ok, thanks.");
                if (SUB_MENU_THREE == 0) {
                    sidneyCert(p, n, SidneySmith.Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE);
                }
                break;
            case SidneySmith.Sidney.CERTIFICATE_TO_GOODS :
                if ((((((!Functions.hasItem(p, SidneySmith.PRAYER_CERT)) && (!Functions.hasItem(p, SidneySmith.SUPER_ATTACK_CERT))) && (!Functions.hasItem(p, SidneySmith.SUPER_DEFENSE_CERT))) && (!Functions.hasItem(p, SidneySmith.SUPER_STRENGTH_CERT))) && (!Functions.hasItem(p, SidneySmith.DRAGON_BONES_CERT))) && (!Functions.hasItem(p, SidneySmith.LIMPWURT_ROOT_CERT))) {
                    Functions.___npcTalk(p, n, "Sorry, but you don't have any certificates that I can change.", "I can only change the following certificates into goods.", "Dragon Bone Certificates,", "Limpwurt Root Certificates,", "Prayer Potion Certificates,", "Super Attack Potion Certificates,", "Super Defense Potion Certificates,", "and Super Strength Potion Certificates.");
                } else {
                    Functions.___npcTalk(p, n, "Ok then, which certificates would you like to change?");
                    certMenuOne(p, n);
                }
                break;
            case SidneySmith.Sidney.GOODS_TO_CERTIFICATE :
                if ((((((!Functions.hasItem(p, SidneySmith.PRAYER_RESTORE_POT, 5)) && (!Functions.hasItem(p, SidneySmith.SUPER_ATTACK_POT, 5))) && (!Functions.hasItem(p, SidneySmith.SUPER_DEFENSE_POT, 5))) && (!Functions.hasItem(p, SidneySmith.SUPER_STRENGTH_POT, 5))) && (!Functions.hasItem(p, SidneySmith.DRAGON_BONES, 5))) && (!Functions.hasItem(p, SidneySmith.LIMPWURT_ROOT, 5))) {
                    Functions.___npcTalk(p, n, "Sorry, but you either don't have enough items for me to certificate.", "or you don't have the right type of items for me to certificate.");
                    int SUB_MENU_FOUR = Functions.___showMenu(p, n, "Which goods do you certificate?", "How many items do you need to make a certificate.");
                    if (SUB_MENU_FOUR == 0) {
                        sidneyCert(p, n, SidneySmith.Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE);
                    } else
                        if (SUB_MENU_FOUR == 1) {
                            sidneyCert(p, n, SidneySmith.Sidney.HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE);
                        }

                } else {
                    Functions.___npcTalk(p, n, "Which goods would you like to certificate?");
                    goodsMenuOne(p, n);
                }
                break;
        }
    }

    private void goodsMenuOne(Player p, Npc n) {
        int goods = Functions.___showMenu(p, "* Prayer Restore Potion * ", "* Super Attack Potion *", "* Super Defense Potion *", "* Super Strength Potion *", "-*- Menu 2 -*-");
        if (goods == 0) {
            if (Functions.hasItem(p, SidneySmith.PRAYER_RESTORE_POT, 5)) {
                calculateExchangeMenu(p, n, false, new Item(SidneySmith.PRAYER_RESTORE_POT), new Item(SidneySmith.PRAYER_CERT));
            } else {
                Functions.___npcTalk(p, n, "You don't have any Prayer potions to certificate.", "Which goods would you like to certificate?");
                goodsMenuOne(p, n);
            }
        } else
            if (goods == 1) {
                if (Functions.hasItem(p, SidneySmith.SUPER_ATTACK_POT, 5)) {
                    calculateExchangeMenu(p, n, false, new Item(SidneySmith.SUPER_ATTACK_POT), new Item(SidneySmith.SUPER_ATTACK_CERT));
                } else {
                    Functions.___npcTalk(p, n, "You don't have enough Super Attack potions to certificate.");
                    Functions.___playerTalk(p, n, "Ok thanks.");
                }
            } else
                if (goods == 2) {
                    if (Functions.hasItem(p, SidneySmith.SUPER_DEFENSE_POT, 5)) {
                        calculateExchangeMenu(p, n, false, new Item(SidneySmith.SUPER_DEFENSE_POT), new Item(SidneySmith.SUPER_DEFENSE_CERT));
                    } else {
                        Functions.___npcTalk(p, n, "You don't have any Super Defense potions to certificate.", "Which goods would you like to certificate?");
                        goodsMenuOne(p, n);
                    }
                } else
                    if (goods == 3) {
                        if (Functions.hasItem(p, SidneySmith.SUPER_STRENGTH_POT, 5)) {
                            calculateExchangeMenu(p, n, false, new Item(SidneySmith.SUPER_STRENGTH_POT), new Item(SidneySmith.SUPER_STRENGTH_CERT));
                        } else {
                            Functions.___npcTalk(p, n, "You don't have any Super Strength potions to certificate.", "Which goods would you like to certificate?");
                            goodsMenuOne(p, n);
                        }
                    } else
                        if (goods == 4) {
                            goodsMenuTwo(p, n);
                        }




    }

    private void goodsMenuTwo(Player p, Npc n) {
        int goods = Functions.___showMenu(p, "* Dragon Bones *", "* Limpwurt Root *", "-*- Menu 1 -*-");
        if (goods == 0) {
            if (Functions.hasItem(p, SidneySmith.DRAGON_BONES, 5)) {
                calculateExchangeMenu(p, n, false, new Item(SidneySmith.DRAGON_BONES), new Item(SidneySmith.DRAGON_BONES_CERT));
            } else {
                Functions.___npcTalk(p, n, "You don't have any Dragon Bones to certificate.", "Which goods would you like to certificate?");
                goodsMenuOne(p, n);
            }
        } else
            if (goods == 1) {
                if (Functions.hasItem(p, SidneySmith.LIMPWURT_ROOT, 5)) {
                    calculateExchangeMenu(p, n, false, new Item(SidneySmith.LIMPWURT_ROOT), new Item(SidneySmith.LIMPWURT_ROOT_CERT));
                } else {
                    Functions.___npcTalk(p, n, "You don't have any Limpwurt Roots to certificate.", "Which goods would you like to certificate?");
                    goodsMenuOne(p, n);
                }
            } else
                if (goods == 2) {
                    goodsMenuOne(p, n);
                }


    }

    private void certMenuOne(Player p, Npc n) {
        int certs = Functions.___showMenu(p, "* Restore Prayer Potion Certificates * ", "* Super Attack Potion Certificates *", "* Super Defense Potion Certificates *", "* Super Strength Potion Certificates *", "-*- Menu 2 -*-");
        if (certs == 0) {
            if (Functions.hasItem(p, SidneySmith.PRAYER_CERT)) {
                calculateExchangeMenu(p, n, true, new Item(SidneySmith.PRAYER_RESTORE_POT), new Item(SidneySmith.PRAYER_CERT));
            } else {
                Functions.___npcTalk(p, n, "Sorry, but you don't have any ", "Prayer Restore Potion Certificates to change.");
            }
        } else
            if (certs == 1) {
                if (Functions.hasItem(p, SidneySmith.SUPER_ATTACK_CERT)) {
                    calculateExchangeMenu(p, n, true, new Item(SidneySmith.SUPER_ATTACK_POT), new Item(SidneySmith.SUPER_ATTACK_CERT));
                } else {
                    Functions.___npcTalk(p, n, "Sorry, but you don't have any ", "Super attack Potion Certificates to change.");
                }
            } else
                if (certs == 2) {
                    if (Functions.hasItem(p, SidneySmith.SUPER_DEFENSE_CERT)) {
                        calculateExchangeMenu(p, n, true, new Item(SidneySmith.SUPER_DEFENSE_POT), new Item(SidneySmith.SUPER_DEFENSE_CERT));
                    } else {
                        Functions.___npcTalk(p, n, "Sorry, but you don't have any ", "Super Defense Potion Certificates to change.");
                    }
                } else
                    if (certs == 3) {
                        if (Functions.hasItem(p, SidneySmith.SUPER_STRENGTH_CERT)) {
                            calculateExchangeMenu(p, n, true, new Item(SidneySmith.SUPER_STRENGTH_POT), new Item(SidneySmith.SUPER_STRENGTH_CERT));
                        } else {
                            Functions.___npcTalk(p, n, "Sorry, but you don't have any ", "Super Strength Potion Certificates to change.");
                        }
                    } else
                        if (certs == 4) {
                            certMenuTwo(p, n);
                        }




    }

    private void certMenuTwo(Player p, Npc n) {
        int menu = Functions.___showMenu(p, "* Dragon Bones Certificates *", "* Limpwurt Root Certificates *", "-*- Menu 1 -*-");
        if (menu == 0) {
            if (Functions.hasItem(p, SidneySmith.DRAGON_BONES_CERT)) {
                calculateExchangeMenu(p, n, true, new Item(SidneySmith.DRAGON_BONES), new Item(SidneySmith.DRAGON_BONES_CERT));
            } else {
                Functions.___npcTalk(p, n, "Sorry, but you don't have any ", "Dragon Bone Certificates to change.");
            }
        } else
            if (menu == 1) {
                if (Functions.hasItem(p, SidneySmith.LIMPWURT_ROOT_CERT)) {
                    calculateExchangeMenu(p, n, true, new Item(SidneySmith.LIMPWURT_ROOT), new Item(SidneySmith.LIMPWURT_ROOT_CERT));
                } else {
                    Functions.___npcTalk(p, n, "Sorry, but you don't have any ", "Limpwurt Root Certificates to change.");
                }
            } else
                if (menu == 2) {
                    certMenuOne(p, n);
                }


    }

    private void calculateExchangeMenu(Player p, Npc n, boolean useCertificate, Item i, Item certificate) {
        int count = p.getInventory().countId(useCertificate ? certificate.getID() : i.getID());
        int mainMenu = -1;
        if (useCertificate) {
            Functions.___npcTalk(p, n, ("How many " + i.getDef(p.getWorld()).getName()) + " certificates do you want to change?");
            if (count == 1) {
                int firstMenu = Functions.___showMenu(p, "None thanks.", "One Certificate please");
                if (firstMenu != (-1)) {
                    if (firstMenu == 0) {
                        Functions.___npcTalk(p, n, "Ok, suit yourself.");
                        return;
                    } else
                        if (firstMenu == 1) {
                            mainMenu = 0;
                        }

                }
            } else
                if (count == 2) {
                    mainMenu = Functions.___showMenu(p, "One Certificate please", "Two Certificates Please");
                } else
                    if (count == 3) {
                        mainMenu = Functions.___showMenu(p, "One Certificate please", "Two Certificates Please", "Three Certificates Please.");
                    } else
                        if (count == 4) {
                            mainMenu = Functions.___showMenu(p, "One Certificate please", "Two Certificates Please", "Three Certificates Please.", "Four Certificates Please");
                        } else
                            if (count >= 5) {
                                mainMenu = Functions.___showMenu(p, "One Certificate please", "Two Certificates Please", "Three Certificates Please.", "Four Certificates Please", "Five Certificates Please.");
                            }




        } else {
            Functions.___npcTalk(p, n, ("How many " + i.getDef(p.getWorld()).getName()) + " would you like to certificate?");
            if ((count >= 5) && (count < 10)) {
                int firstMenu = Functions.___showMenu(p, "None", "Five");
                if (firstMenu != (-1)) {
                    if (firstMenu == 0) {
                        p.message("You decide not to change any items.");
                        return;
                    } else
                        if (firstMenu == 1) {
                            mainMenu = 0;
                        }

                }
            } else
                if ((count >= 10) && (count < 15)) {
                    mainMenu = Functions.___showMenu(p, "Five", "Ten");
                } else
                    if ((count >= 15) && (count < 20)) {
                        mainMenu = Functions.___showMenu(p, "Five", "Ten", "Fifteen");
                    } else
                        if ((count >= 20) && (count < 25)) {
                            mainMenu = Functions.___showMenu(p, "Five", "Ten", "Fifteen", "Twenty");
                        } else
                            if (count >= 25) {
                                mainMenu = Functions.___showMenu(p, "Five", "Ten", "Fifteen", "Twenty", "Twenty Five");
                            } else {
                                Functions.___npcTalk(p, n, ("Sorry, but you don't have enough " + i.getDef(p.getWorld()).getName()) + ".", "You need at least five to make a certificate.");
                                return;
                            }




        }
        if (mainMenu != (-1)) {
            if (useCertificate) {
                Functions.___npcTalk(p, n, ("Ok, that's your " + i.getDef(p.getWorld()).getName()) + " certificates done.");
                mainMenu += 1;
                int itemAmount = mainMenu * 5;
                if (p.getInventory().remove(certificate.getID(), mainMenu) > (-1)) {
                    for (int x = 0; x < itemAmount; x++) {
                        p.getInventory().add(new Item(i.getID(), 1));
                    }
                }
                Functions.___playerTalk(p, n, "Ok thanks.");
            } else {
                Functions.___npcTalk(p, n, ("Ok, that's your " + i.getDef(p.getWorld()).getName()) + " certificated.");
                mainMenu += 1;
                int itemAmount = mainMenu * 5;
                for (int x = 0; x < itemAmount; x++) {
                    p.getInventory().remove(i.getID(), 1);
                }
                p.getInventory().add(new Item(certificate.getID(), mainMenu));
                Functions.___playerTalk(p, n, "Ok thanks.");
            }
        }
    }

    @Override
    public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
        return (npc.getID() == SidneySmith.SIDNEY_SMITH) && Functions.inArray(item.getID(), SidneySmith.PRAYER_RESTORE_POT, SidneySmith.SUPER_ATTACK_POT, SidneySmith.SUPER_STRENGTH_POT, SidneySmith.SUPER_DEFENSE_POT, SidneySmith.DRAGON_BONES, SidneySmith.LIMPWURT_ROOT, SidneySmith.PRAYER_CERT, SidneySmith.SUPER_ATTACK_CERT, SidneySmith.SUPER_DEFENSE_CERT, SidneySmith.SUPER_STRENGTH_CERT, SidneySmith.DRAGON_BONES_CERT, SidneySmith.LIMPWURT_ROOT_CERT);
    }

    @Override
    public GameStateEvent onInvUseOnNpc(Player player, Npc npc, Item item) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((npc.getID() == SidneySmith.SIDNEY_SMITH) && Functions.inArray(item.getID(), SidneySmith.PRAYER_RESTORE_POT, SidneySmith.SUPER_ATTACK_POT, SidneySmith.SUPER_STRENGTH_POT, SidneySmith.SUPER_DEFENSE_POT, SidneySmith.DRAGON_BONES, SidneySmith.LIMPWURT_ROOT, SidneySmith.PRAYER_CERT, SidneySmith.SUPER_ATTACK_CERT, SidneySmith.SUPER_DEFENSE_CERT, SidneySmith.SUPER_STRENGTH_CERT, SidneySmith.DRAGON_BONES_CERT, SidneySmith.LIMPWURT_ROOT_CERT)) {
                        switch (item.getID()) {
                            case SidneySmith.PRAYER_RESTORE_POT :
                                calculateExchangeMenu(player, npc, false, item, new Item(SidneySmith.PRAYER_CERT));
                                break;
                            case SidneySmith.SUPER_ATTACK_POT :
                                calculateExchangeMenu(player, npc, false, item, new Item(SidneySmith.SUPER_ATTACK_CERT));
                                break;
                            case SidneySmith.SUPER_STRENGTH_POT :
                                calculateExchangeMenu(player, npc, false, item, new Item(SidneySmith.SUPER_STRENGTH_CERT));
                                break;
                            case SidneySmith.SUPER_DEFENSE_POT :
                                calculateExchangeMenu(player, npc, false, item, new Item(SidneySmith.SUPER_DEFENSE_CERT));
                                break;
                            case SidneySmith.DRAGON_BONES :
                                calculateExchangeMenu(player, npc, false, item, new Item(SidneySmith.DRAGON_BONES_CERT));
                                break;
                            case SidneySmith.LIMPWURT_ROOT :
                                calculateExchangeMenu(player, npc, false, item, new Item(SidneySmith.LIMPWURT_ROOT_CERT));
                                break;
                            case SidneySmith.PRAYER_CERT :
                                calculateExchangeMenu(player, npc, true, new Item(SidneySmith.PRAYER_RESTORE_POT), item);
                                break;
                            case SidneySmith.SUPER_ATTACK_CERT :
                                calculateExchangeMenu(player, npc, true, new Item(SidneySmith.SUPER_ATTACK_POT), item);
                                break;
                            case SidneySmith.SUPER_STRENGTH_CERT :
                                calculateExchangeMenu(player, npc, true, new Item(SidneySmith.SUPER_STRENGTH_POT), item);
                                break;
                            case SidneySmith.SUPER_DEFENSE_CERT :
                                calculateExchangeMenu(player, npc, true, new Item(SidneySmith.SUPER_DEFENSE_POT), item);
                                break;
                            case SidneySmith.DRAGON_BONES_CERT :
                                calculateExchangeMenu(player, npc, true, new Item(SidneySmith.DRAGON_BONES), item);
                                break;
                            case SidneySmith.LIMPWURT_ROOT_CERT :
                                calculateExchangeMenu(player, npc, true, new Item(SidneySmith.LIMPWURT_ROOT), item);
                                break;
                            default :
                                player.message("Nothing interesting happens");
                                break;
                        }
                    }
                    return null;
                });
            }
        };
    }

    class Sidney {
        public static final int CERTIFICATE_TO_GOODS = 0;

        public static final int GOODS_TO_CERTIFICATE = 1;

        public static final int WHAT_IS_CERTIFICATION = 2;

        public static final int WHICH_GOODS_DO_YOU_CERTIFICATE = 3;

        public static final int HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE = 4;
    }
}

