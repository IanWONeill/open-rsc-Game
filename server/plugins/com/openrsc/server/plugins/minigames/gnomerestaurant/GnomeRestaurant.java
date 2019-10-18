package com.openrsc.server.plugins.minigames.gnomerestaurant;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class GnomeRestaurant implements MiniGameInterface , InvActionListener , TalkToNpcListener , DropExecutiveListener , InvActionExecutiveListener , TalkToNpcExecutiveListener {
    @Override
    public int getMiniGameId() {
        return Minigames.GNOME_RESTAURANT;
    }

    @Override
    public String getMiniGameName() {
        return "Gnome Restaurant (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        // mini-game complete handled already
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.ALUFT_GIANNE.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.ALUFT_GIANNE.id()) {
                        if (!p.getCache().hasKey("gnome_cooking")) {
                            Functions.___playerTalk(p, n, "hello");
                            Functions.___npcTalk(p, n, "well hello there,you hungry..", "you come to the right place", "eat green, eat gnome cruisine", "my waiter will be glad to take your order");
                            Functions.___playerTalk(p, n, "thanks");
                            Functions.___npcTalk(p, n, "on the other hand if you looking for some work", "i have a cook's position available");
                            int menu = Functions.___showMenu(p, n, "no thanks i'm no cook", "ok i'll give it a go");
                            if (menu == 0) {
                                Functions.___npcTalk(p, n, "in that case please, eat and enjoy");
                            } else
                                if (menu == 1) {
                                    Functions.___npcTalk(p, n, "well that's great", "of course i'll have to see what you're like first", "here, have a look at our menu");
                                    p.message("Aluft gives you a cook book");
                                    Functions.addItem(p, ItemId.GIANNE_COOK_BOOK.id(), 1);
                                    p.getCache().set("gnome_cooking", 1);
                                    Functions.___npcTalk(p, n, "when you've had a look come back...", "... and i'll let you prepare a few dishes");
                                    Functions.___playerTalk(p, n, "good stuff");
                                }

                        } else {
                            int stage = p.getCache().getInt("gnome_cooking");
                            switch (stage) {
                                case 1 :
                                    Functions.___playerTalk(p, n, "hi mr gianne");
                                    Functions.___npcTalk(p, n, "hello my good friend", "what did you think");
                                    Functions.___playerTalk(p, n, "I'm not too sure about toads legs");
                                    Functions.___npcTalk(p, n, "they're a gnome delicacy, you'll love them", "but we'll start with something simple", "can you make me a cheese and tomato gnome batta");
                                    Functions.___npcTalk(p, n, "here's what you need");
                                    Functions.___message(p, 1200, "aluft gives you one tomato, some cheese...");
                                    Functions.addItem(p, ItemId.TOMATO.id(), 1);
                                    Functions.addItem(p, ItemId.CHEESE.id(), 1);
                                    p.message("...some equa leaves and some plain dough");
                                    Functions.addItem(p, ItemId.EQUA_LEAVES.id(), 1);
                                    Functions.addItem(p, ItemId.GIANNE_DOUGH.id(), 1);
                                    p.getCache().set("gnome_cooking", 2);
                                    Functions.___playerTalk(p, n, "thanks");
                                    Functions.___npcTalk(p, n, "Let me know how you get on");
                                    break;
                                case 2 :
                                    Functions.___playerTalk(p, n, "Hi mr gianne");
                                    Functions.___npcTalk(p, n, "call me aluft");
                                    Functions.___playerTalk(p, n, "ok");
                                    Functions.___npcTalk(p, n, "so how did you get on?");
                                    if (Functions.hasItem(p, ItemId.CHEESE_AND_TOMATO_BATTA.id())) {
                                        Functions.___playerTalk(p, n, "no problem, it was easy");
                                        Functions.___message(p, 1900, "you give aluft the gnome batta");
                                        Functions.removeItem(p, ItemId.CHEESE_AND_TOMATO_BATTA.id(), 1);
                                        p.message("he takes a bite");
                                        Functions.___npcTalk(p, n, "not bad...not bad at all", "ok now for something a little harder", "try and make me a choc bomb.. they're my favorite", "here's what you need");
                                        Functions.___message(p, 1200, "aluft gives you four bars of chocolate");
                                        Functions.addItem(p, ItemId.CHOCOLATE_BAR.id(), 4);
                                        Functions.___message(p, 1200, "some equa leaves, some chocolate dust...");
                                        Functions.addItem(p, ItemId.EQUA_LEAVES.id(), 1);
                                        Functions.addItem(p, ItemId.CHOCOLATE_DUST.id(), 1);
                                        p.message("...some gianne dough and some cream");
                                        Functions.addItem(p, ItemId.GIANNE_DOUGH.id(), 1);
                                        Functions.addItem(p, ItemId.CREAM.id(), 2);
                                        Functions.___playerTalk(p, n, "ok aluft, i'll be back soon");
                                        Functions.___npcTalk(p, n, "good stuff");
                                        p.getCache().set("gnome_cooking", 3);
                                    } else {
                                        Functions.___playerTalk(p, n, "erm.. not quite done yet");
                                        Functions.___npcTalk(p, n, "ok, let me know when you are", "i need one cheese and tomato batta");
                                    }
                                    break;
                                case 3 :
                                    Functions.___playerTalk(p, n, "hi aluft");
                                    Functions.___npcTalk(p, n, "hello there, how did you get on");
                                    if (Functions.hasItem(p, ItemId.CHOCOLATE_BOMB.id())) {
                                        Functions.___playerTalk(p, n, "here you go");
                                        Functions.removeItem(p, ItemId.CHOCOLATE_BOMB.id(), 1);
                                        Functions.___message(p, 1200, "you give aluft the choc bomb");
                                        p.message("he takes a bite");
                                        Functions.___npcTalk(p, n, "yes, yes, yes, that's superb", "i'm really impressed");
                                        Functions.___playerTalk(p, n, "i'm glad");
                                        Functions.___npcTalk(p, n, "ok then, now can you make me a toad batta", "here's what you need");
                                        Functions.addItem(p, ItemId.GIANNE_DOUGH.id(), 1);
                                        Functions.addItem(p, ItemId.EQUA_LEAVES.id(), 1);
                                        Functions.addItem(p, ItemId.GNOME_SPICE.id(), 1);
                                        Functions.___message(p, 1900, "mr gianne gives you some dough, some equaleaves...");
                                        p.message("...and some gnome spice");
                                        Functions.___npcTalk(p, n, "i'm afraid all are toads legs are served fresh");
                                        Functions.___playerTalk(p, n, "nice!");
                                        Functions.___npcTalk(p, n, "so you'll need to go to the swamp on ground level", "and catch a toad", "let me know when the batta's ready");
                                        p.getCache().set("gnome_cooking", 4);
                                    } else {
                                        Functions.___playerTalk(p, n, "i haven't made it yet");
                                        Functions.___npcTalk(p, n, "just follow the instructions carefully", "i need one choc bomb");
                                    }
                                    break;
                                case 4 :
                                    Functions.___playerTalk(p, n, "hi mr gianne");
                                    Functions.___npcTalk(p, n, "aluft");
                                    Functions.___playerTalk(p, n, "sorry, aluft");
                                    Functions.___npcTalk(p, n, "so where's my toad batta?");
                                    if (Functions.hasItem(p, ItemId.TOAD_BATTA.id())) {
                                        Functions.___playerTalk(p, n, "here you go, easy");
                                        Functions.___message(p, 1900, "you give mr gianne the toad batta");
                                        Functions.removeItem(p, ItemId.TOAD_BATTA.id(), 1);
                                        p.message("he takes a bite");
                                        Functions.___npcTalk(p, n, "ooh, that's some good toad", "very nice", "let's see if you can make a worm hole");
                                        Functions.___playerTalk(p, n, "a wormhole?");
                                        Functions.___npcTalk(p, n, "yes, it's in the cooking guide i gave you", "you'll have to get the worms from the swamp", "but here's everything else you'll need", "let me know when your done");
                                        Functions.addItem(p, ItemId.GIANNE_DOUGH.id(), 1);
                                        Functions.addItem(p, ItemId.ONION.id(), 2);
                                        Functions.addItem(p, ItemId.EQUA_LEAVES.id(), 1);
                                        p.getCache().set("gnome_cooking", 5);
                                    } else {
                                        Functions.___playerTalk(p, n, "i'm not done yet");
                                        Functions.___npcTalk(p, n, "ok, quick as you can though");
                                        Functions.___playerTalk(p, n, "no problem");
                                    }
                                    break;
                                case 5 :
                                    Functions.___playerTalk(p, n, "hello again aluft");
                                    Functions.___npcTalk(p, n, "hello traveller, how did you do?");
                                    if (Functions.hasItem(p, ItemId.WORM_HOLE.id())) {
                                        Functions.___playerTalk(p, n, "here, see what you think");
                                        Functions.___message(p, 1900, "you give mr gianne the worm hole");
                                        Functions.removeItem(p, ItemId.WORM_HOLE.id(), 1);
                                        p.message("he takes a bite");
                                        Functions.___npcTalk(p, n, "hmm, that's actually really good", "how about you make me some toad crunchies for desert", "then i'll decide whether i can take you on");
                                        Functions.___playerTalk(p, n, "toad crunchies?");
                                        Functions.___npcTalk(p, n, "that's right, here's all you need", "except the toad");
                                        Functions.addItem(p, ItemId.GIANNE_DOUGH.id(), 1);
                                        Functions.addItem(p, ItemId.EQUA_LEAVES.id(), 1);
                                        p.message("mr gianne gives you some gianne dough and some equa leaves");
                                        Functions.___npcTalk(p, n, "let me know when your done");
                                        p.getCache().set("gnome_cooking", 6);
                                    } else {
                                        Functions.___playerTalk(p, n, "i'm not done yet");
                                        Functions.___npcTalk(p, n, "ok, quick as you can though", "i need one worm hole");
                                        Functions.___playerTalk(p, n, "no problem");
                                    }
                                    break;
                                case 6 :
                                    Functions.___playerTalk(p, n, "hi aluft\"");
                                    Functions.___npcTalk(p, n, "hello, how are you getting on?");
                                    if (Functions.hasItem(p, ItemId.TOAD_CRUNCHIES.id())) {
                                        Functions.___playerTalk(p, n, "here, try it");
                                        Functions.___message(p, 1900, "you give mr gianne the toad crunchie");
                                        Functions.removeItem(p, ItemId.TOAD_CRUNCHIES.id(), 1);
                                        p.message("he takes a bite");
                                        Functions.___npcTalk(p, n, "well for a human you certainly can cook", "i'd love to have you on the team", "if you ever want to make some money", "or want to improve your cooking skills just come and see me", "i'll tell you what meals i need, and if you can, you make them");
                                        Functions.___playerTalk(p, n, "what about ingredients?");
                                        Functions.___npcTalk(p, n, "well you know where to find toads and worms", "you can buy the rest from hudo glenfad the grocer", "i'll always pay you much more for the meal than you paid for the ingredients", "and it's a great way to improve your cooking skills");
                                        p.getCache().set("gnome_cooking", 7);// COMPLETED JOB!

                                    } else {
                                        Functions.___playerTalk(p, n, "no luck so for");
                                        Functions.___npcTalk(p, n, "ok then but don't take too long", "i need one toad crunchie");
                                    }
                                    break;
                                case 7 :
                                    /**
                                     * Completed and hired for job.
                                     */
                                    if (p.getCache().hasKey("gnome_restaurant_job")) {
                                        Functions.___playerTalk(p, n, "hi aluft");
                                        myCurrentJob(p, n);
                                    } else {
                                        Functions.___playerTalk(p, n, "hello again aluft");
                                        Functions.___npcTalk(p, n, "well hello there traveller", "have you come to help me out?");
                                        int menu = Functions.___showMenu(p, n, "sorry aluft, i'm too busy", "i would be glad to help");
                                        if (menu == 0) {
                                            Functions.___npcTalk(p, n, "no worries, let me know when you're free");
                                        } else
                                            if (menu == 1) {
                                                Functions.___npcTalk(p, n, "good stuff");
                                                randomizeJob(p, n);
                                            }

                                    }
                                    break;
                            }
                        }
                    }
                    return null;
                });
            }
        };
    }

    private void myCurrentJob(Player p, Npc n) {
        int job = p.getCache().getInt("gnome_restaurant_job");
        if (job == 0) {
            Functions.___npcTalk(p, n, "hello again, are the dishes ready?");
            if ((Functions.hasItem(p, ItemId.WORM_BATTA.id(), 2) && Functions.hasItem(p, ItemId.VEG_BATTA.id())) && Functions.hasItem(p, ItemId.TOAD_BATTA.id())) {
                Functions.___playerTalk(p, n, "all done, here you go");
                Functions.___message(p, 1900, "you give aluft two worm batta's a veg batta and a toad batta");
                p.incExp(Skills.COOKING, 425, true);
                Functions.removeItem(p, ItemId.WORM_BATTA.id(), 2);
                Functions.removeItem(p, ItemId.VEG_BATTA.id(), 1);
                Functions.removeItem(p, ItemId.TOAD_BATTA.id(), 1);
                Functions.___npcTalk(p, n, "they look great, well done", "here's your share of the profit");
                p.message("mr gianne gives you 45 gold coins");
                Functions.addItem(p, ItemId.COINS.id(), 45);
            } else {
                Functions.___playerTalk(p, n, "i'm not done yet");
                Functions.___npcTalk(p, n, "i need  two worm batta's, one toad batta", "...and one veg batta please", "be as quick as you can");
                return;
            }
        } else
            if (job == 1) {
                Functions.___npcTalk(p, n, "hello again, are the dishes ready?");
                if ((Functions.hasItem(p, ItemId.CHOCOLATE_BOMB.id()) && Functions.hasItem(p, ItemId.CHOC_CRUNCHIES.id(), 2)) && Functions.hasItem(p, ItemId.TOAD_CRUNCHIES.id(), 2)) {
                    Functions.___playerTalk(p, n, "here you go aluft");
                    Functions.___message(p, 1900, "you give aluft choc bomb, two choc crunchies and two toad crunchies");
                    Functions.removeItem(p, ItemId.CHOCOLATE_BOMB.id(), 1);
                    Functions.removeItem(p, ItemId.CHOC_CRUNCHIES.id(), 2);
                    Functions.removeItem(p, ItemId.TOAD_CRUNCHIES.id(), 2);
                    p.incExp(Skills.COOKING, 675, true);
                    Functions.___npcTalk(p, n, "they look great, well done", "here's your share of the profit");
                    p.message("mr gianne gives you 75 gold coins");
                    Functions.addItem(p, ItemId.COINS.id(), 75);
                } else {
                    Functions.___playerTalk(p, n, "i'm not done yet");
                    Functions.___npcTalk(p, n, "ok, i need a choc bomb, two choc crunchies and two toad crunchies", "don't take too long", "it's a full house tonight");
                    return;
                }
            } else
                if (job == 2) {
                    Functions.___npcTalk(p, n, "hello again traveller how did you do?");
                    if (Functions.hasItem(p, ItemId.CHOC_CRUNCHIES.id(), 2)) {
                        Functions.___playerTalk(p, n, "all done, here you go");
                        Functions.___message(p, 1900, "you give aluft the two choc crunchies");
                        Functions.removeItem(p, ItemId.CHOC_CRUNCHIES.id(), 2);
                        p.incExp(Skills.COOKING, 300, true);
                        Functions.___npcTalk(p, n, "they look great, well done", "here's your share of the profit");
                        p.message("mr gianne gives you 30 gold coins");
                        Functions.addItem(p, ItemId.COINS.id(), 30);
                    } else {
                        Functions.___playerTalk(p, n, "i'm not done yet");
                        Functions.___npcTalk(p, n, "i just need two choc crunchies", "should be easy");
                        return;
                    }
                } else
                    if (job == 3) {
                        Functions.___npcTalk(p, n, "hello again traveller how did you do?");
                        if (Functions.hasItem(p, ItemId.CHOCOLATE_BOMB.id()) && Functions.hasItem(p, ItemId.CHOC_CRUNCHIES.id(), 2)) {
                            Functions.___playerTalk(p, n, "here you go aluft");
                            Functions.___message(p, 1900, "you give aluft one choc bomb and two choc crunchies");
                            Functions.removeItem(p, ItemId.CHOCOLATE_BOMB.id(), 1);
                            Functions.removeItem(p, ItemId.CHOC_CRUNCHIES.id(), 2);
                            p.incExp(Skills.COOKING, 425, true);
                            Functions.___npcTalk(p, n, "they look great, well done", "here's your share of the profit");
                            p.message("mr gianne gives you 45 gold coins");
                            Functions.addItem(p, ItemId.COINS.id(), 45);
                        } else {
                            Functions.___playerTalk(p, n, "i'm not done yet");
                            Functions.___npcTalk(p, n, "i need one choc bomb and two choc crunchies please");
                            return;
                        }
                    } else
                        if (job == 4) {
                            Functions.___npcTalk(p, n, "hello again traveller how did you do?");
                            if (Functions.hasItem(p, ItemId.VEG_BATTA.id(), 2) && Functions.hasItem(p, ItemId.WORM_HOLE.id())) {
                                Functions.___playerTalk(p, n, "here you go aluft");
                                Functions.___message(p, 1900, "you give aluft two veg batta's and a worm hole");
                                Functions.removeItem(p, ItemId.VEG_BATTA.id(), 2);
                                Functions.removeItem(p, ItemId.WORM_HOLE.id(), 1);
                                p.incExp(Skills.COOKING, 425, true);
                                Functions.___npcTalk(p, n, "they look great, well done", "here's your share of the profit");
                                p.message("mr gianne gives you 45 gold coins");
                                Functions.addItem(p, ItemId.COINS.id(), 45);
                            } else {
                                Functions.___playerTalk(p, n, "i'm not done yet");
                                Functions.___npcTalk(p, n, "ok, i need two veg batta's and one worm hole", "ok, but try not to take too long", "it's a full house tonight");
                                return;
                            }
                        } else
                            if (job == 5) {
                                Functions.___npcTalk(p, n, "hello again, are the dishes ready?");
                                if ((Functions.hasItem(p, ItemId.VEGBALL.id()) && Functions.hasItem(p, ItemId.TANGLED_TOADS_LEGS.id())) && Functions.hasItem(p, ItemId.WORM_HOLE.id())) {
                                    Functions.___playerTalk(p, n, "all done, here you go");
                                    Functions.___message(p, 1900, "you give aluft one veg ball, one twisted toads legs and one worm hole");
                                    Functions.removeItem(p, ItemId.VEGBALL.id(), 1);
                                    Functions.removeItem(p, ItemId.TANGLED_TOADS_LEGS.id(), 1);
                                    Functions.removeItem(p, ItemId.WORM_HOLE.id(), 1);
                                    p.incExp(Skills.COOKING, 425, true);
                                    Functions.___npcTalk(p, n, "they look great, well done", "here's your share of the profit");
                                    p.message("mr gianne gives you 45 gold coins");
                                    Functions.addItem(p, ItemId.COINS.id(), 45);
                                } else {
                                    Functions.___playerTalk(p, n, "i'm not done yet");
                                    Functions.___npcTalk(p, n, "i need  one veg ball, one twisted toads legs...", "...and one worm hole please");
                                    return;
                                }
                            } else
                                if (job == 6) {
                                    Functions.___npcTalk(p, n, "hello again traveller how did you do?");
                                    if ((Functions.hasItem(p, ItemId.CHEESE_AND_TOMATO_BATTA.id()) && Functions.hasItem(p, ItemId.VEGBALL.id())) && Functions.hasItem(p, ItemId.WORM_CRUNCHIES.id(), 2)) {
                                        Functions.___message(p, 1900, "you give one cheese and tomato batta,one veg ball...", "...and two portions of worm crunchies");
                                        Functions.removeItem(p, ItemId.CHEESE_AND_TOMATO_BATTA.id(), 1);
                                        Functions.removeItem(p, ItemId.VEGBALL.id(), 1);
                                        Functions.removeItem(p, ItemId.WORM_CRUNCHIES.id(), 2);
                                        p.incExp(Skills.COOKING, 550, true);
                                        Functions.___npcTalk(p, n, "they look great, well done", "here's your share of the profit");
                                        p.message("mr gianne gives you 60 gold coins");
                                        Functions.addItem(p, ItemId.COINS.id(), 60);
                                    } else {
                                        Functions.___playerTalk(p, n, "i'm not done yet");
                                        Functions.___npcTalk(p, n, "i need one cheese and tomato batta,one veg ball...", "...and two portions of worm crunchies please");
                                        return;
                                    }
                                } else
                                    if (job == 7) {
                                        // intentional glitch on minigame, see https://youtu.be/jtc97eKmFWc?t=806
                                        Functions.___npcTalk(p, n, "hello again, are the dishes ready?");
                                        if (((Functions.hasItem(p, ItemId.SPICE_CRUNCHIES.id(), 2) && Functions.hasItem(p, ItemId.FRUIT_BATTA.id())) && Functions.hasItem(p, ItemId.CHOCOLATE_BOMB.id())) && Functions.hasItem(p, ItemId.VEGBALL.id())) {
                                            Functions.___playerTalk(p, n, "all done, here you go");
                                            Functions.___message(p, 1900, "you give aluft the tangled toads legs and two worm crunchies");
                                            Functions.removeItem(p, ItemId.SPICE_CRUNCHIES.id(), 2);
                                            Functions.removeItem(p, ItemId.FRUIT_BATTA.id(), 1);
                                            Functions.removeItem(p, ItemId.CHOCOLATE_BOMB.id(), 1);
                                            Functions.removeItem(p, ItemId.VEGBALL.id(), 1);
                                            p.incExp(Skills.COOKING, 425, true);
                                            Functions.___npcTalk(p, n, "they look great, well done", "here's your share of the profit");
                                            p.message("mr gianne gives you 45 gold coins");
                                            Functions.addItem(p, ItemId.COINS.id(), 45);
                                        } else {
                                            // dialogue recreated
                                            Functions.___playerTalk(p, n, "i'm not done yet");
                                            Functions.___npcTalk(p, n, "i need  two spice crunchies, one fruit batta...", "...a choc bomb and a veg ball please");
                                            return;
                                        }
                                    } else
                                        if (job == 8) {
                                            // recreated job from message on job 7
                                            // made it intentionally glitched
                                            Functions.___npcTalk(p, n, "hello again, are the dishes ready?");
                                            if (Functions.hasItem(p, ItemId.TANGLED_TOADS_LEGS.id()) && Functions.hasItem(p, ItemId.WORM_CRUNCHIES.id(), 2)) {
                                                Functions.___playerTalk(p, n, "all done, here you go");
                                                Functions.___message(p, 1900, "you give aluft one choc bomb and two choc crunchies");
                                                Functions.removeItem(p, ItemId.TANGLED_TOADS_LEGS.id(), 1);
                                                Functions.removeItem(p, ItemId.WORM_CRUNCHIES.id(), 2);
                                                p.incExp(Skills.COOKING, 425, true);
                                                Functions.___npcTalk(p, n, "they look great, well done", "here's your share of the profit");
                                                p.message("mr gianne gives you 45 gold coins");
                                                Functions.addItem(p, ItemId.COINS.id(), 45);
                                            } else {
                                                // dialogue recreated
                                                Functions.___playerTalk(p, n, "i'm not done yet");
                                                Functions.___npcTalk(p, n, "i need one tangled toads legs and two worm crunchies please");
                                                return;
                                            }
                                        }








        p.getCache().remove("gnome_restaurant_job");
        if (!p.getCache().hasKey("gnome_jobs_completed")) {
            p.getCache().set("gnome_jobs_completed", 1);
        } else {
            int completedJobs = p.getCache().getInt("gnome_jobs_completed");
            p.getCache().set("gnome_jobs_completed", completedJobs + 1);
        }
        Functions.___npcTalk(p, n, "can you stay and make another dish?");
        int menu = Functions.___showMenu(p, n, "sorry aluft, i'm too busy", "i would be glad to help");
        if (menu == 0) {
            Functions.___npcTalk(p, n, "no worries, let me know when you're free");
        } else
            if (menu == 1) {
                Functions.___npcTalk(p, n, "your a life saver");
                randomizeJob(p, n);
            }

    }

    private void randomizeJob(Player p, Npc n) {
        int randomize = DataConversions.random(0, 8);
        if (randomize == 0) {
            Functions.___npcTalk(p, n, "can you make me a two worm batta's, one toad batta...", "...and one veg batta please");
            Functions.___playerTalk(p, n, "ok then");
        } else
            if (randomize == 1) {
                Functions.___npcTalk(p, n, "ok, i need a choc bomb, two choc crunchies and two toad crunchies");
                Functions.___playerTalk(p, n, "no problem");
            } else
                if (randomize == 2) {
                    Functions.___npcTalk(p, n, "i just need two choc crunchies please");
                    Functions.___playerTalk(p, n, "no problem");
                } else
                    if (randomize == 3) {
                        Functions.___npcTalk(p, n, "i just need one choc bomb and two choc crunchies please");
                        Functions.___playerTalk(p, n, "no problem");
                    } else
                        if (randomize == 4) {
                            Functions.___npcTalk(p, n, "excellent, i need two veg batta's and one worm hole");
                            Functions.___playerTalk(p, n, "no problem");
                        } else
                            if (randomize == 5) {
                                Functions.___npcTalk(p, n, "can you make me a one veg ball, one twisted toads legs...", "...and one worm hole please");
                                Functions.___playerTalk(p, n, "ok then");
                            } else
                                if (randomize == 6) {
                                    Functions.___npcTalk(p, n, "i need one cheese and tomato batta,one veg ball...", "...and two portions of worm crunchies please");
                                    Functions.___playerTalk(p, n, "ok, i'll do my best");
                                } else
                                    if (randomize == 7) {
                                        Functions.___npcTalk(p, n, "can you make a two spice crunchies, one fruit batta...", "...a choc bomb and a veg ball please chef");
                                        Functions.___playerTalk(p, n, "i'll try");
                                    } else
                                        if (randomize == 8) {
                                            Functions.___npcTalk(p, n, "i just need one tangled toads legs and two worm crunchies please");
                                            Functions.___playerTalk(p, n, "ok, i'll do my best");
                                        }








        if (!p.getCache().hasKey("gnome_restaurant_job")) {
            p.getCache().set("gnome_restaurant_job", randomize);
        }
    }

    @Override
    public boolean blockInvAction(Item item, Player p, String command) {
        return item.getID() == ItemId.GIANNE_COOK_BOOK.id();
    }

    @Override
    public GameStateEvent onInvAction(Item item, Player p, String command) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (item.getID() == ItemId.GIANNE_COOK_BOOK.id()) {
                        p.message("you open aluft's cook book");
                        p.message("inside are various gnome dishes");
                        int menu = Functions.___showMenu(p, "gnomebattas", "gnomebakes", "gnomecrunchies");
                        if (menu == 0) {
                            int battaMenu = Functions.___showMenu(p, "cheese and tomato batta", "toad batta", "worm batta", "fruit batta", "veg batta");
                            if (battaMenu == 0) {
                                ActionSender.sendBox(p, "@yel@Cheese and tomato batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta, once removed place cheese and then tomato on top% %Place batta in oven once more untill cheese has melted, remove and top with equaleaves.", true);
                            } else
                                if (battaMenu == 1) {
                                    ActionSender.sendBox(p, "@yel@Toad batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta, mix some equa leaves with your toad's legs and then add some gnomespice% %Place the seasoned toads legs on the batta, add cheese and bake once more.", true);
                                } else
                                    if (battaMenu == 2) {
                                        ActionSender.sendBox(p, "@yel@Worm batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta, mix some gnomespice with a king worm% %Place the seasoned worm on the batta, add cheese and bake once more% %Remove from oven and finish with a sprinkle of equaleaves...yum.", true);
                                    } else
                                        if (battaMenu == 3) {
                                            ActionSender.sendBox(p, "@yel@Fruit batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta and remove from oven, then lay four sprigs of equa leaves on the batta and bake once more% %Add chunks of pineapple, orange and lime then finish with a sprinkle of gnomespice.", true);
                                        } else
                                            if (battaMenu == 4) {
                                                ActionSender.sendBox(p, "@yel@Veg Batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta then add an onion, two tomatos, one cabbage and some dwellberrys, next place the batta in the oven% %Add some cheese and place in the oven once more% %To finish add a sprinkle of equa leaves.", true);
                                            }




                        } else
                            if (menu == 1) {
                                int bakesMenu = Functions.___showMenu(p, "choc bomb", "veg ball", "wormhole", "tangled toads legs");
                                if (bakesMenu == 0) {
                                    ActionSender.sendBox(p, "@yel@Choc bomb% %Make some gnomebowl dough from the Gianne dough% %Bake the gnome bowl% %Add to the gnomebowl four bars of chocolate and one sprig of equaleaves% %Bake the gnome bowl in an oven% %Next add two portions of cream and finish with a sprinkle of chocolate dust.", true);
                                } else
                                    if (bakesMenu == 1) {
                                        ActionSender.sendBox(p, "@yel@Vegball% %Make some gnomebowl dough from the Gianne dough% %Bake the gnomebowl% %Add two onions,two potatoes and some gnome spice% %Bake the gnomebowl once more% %To finish sprinkle with equaleaves", true);
                                    } else
                                        if (bakesMenu == 2) {
                                            ActionSender.sendBox(p, "@yel@Worm hole% %Make some gnomebowl dough from the Gianne dough% %Bake the gnomebowl% %Add six king worms, two onions and some gnome spice% %Bake the gnomebowl once more% %To finish sprinkle with equaleaves", true);
                                        } else
                                            if (bakesMenu == 3) {
                                                ActionSender.sendBox(p, "@yel@Tangled toads legs% %Make some gnomebowl dough from the Gianne dough% %Bake the gnomebowl% %Add two portions of cheese, five pairs of toad's legs, two sprigs of equa leaves, some dwell berries and two sprinkle's of gnomespice% %Bake the gnomebowl once more", true);
                                            }



                            } else
                                if (menu == 2) {
                                    int crunchiesMenu = Functions.___showMenu(p, "choc crunchies", "worm crunchies", "toad crunchies", "spice crunchies");
                                    if (crunchiesMenu == 0) {
                                        ActionSender.sendBox(p, "@yel@choc crunchies% %Mix some gnome spice and two bars of chocolate with the Gianne dough% %Use dough to make gnomecrunchie dough% %Bake in oven% %Add of sprinkle of chocolate dust", true);
                                    } else
                                        if (crunchiesMenu == 1) {
                                            ActionSender.sendBox(p, "@yel@worm crunchies% %Mix some gnome spice, two king worms and some equa leaves with the Gianne dough% %Use dough to make gnomecrunchie dough% %Bake in oven% %Add of sprinkle of gnome spice", true);
                                        } else
                                            if (crunchiesMenu == 2) {
                                                ActionSender.sendBox(p, "@yel@toad crunchies% %Mix some gnome spice and two pair's of toads legs with the Gianne dough% %Use dough to make gnomecrunchie dough% %Bake in oven% %Add of sprinkle of equa leaves", true);
                                            } else
                                                if (crunchiesMenu == 3) {
                                                    ActionSender.sendBox(p, "@yel@spice crunchies% %Mix three sprinkles of gnomespice and two sprigs of equa leaves with Gianne dough% %Use dough to make gnomecrunchie dough% %Bake in oven% %Add of sprinkle of gnome spice", true);
                                                }



                                }


                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockDrop(Player p, Item i) {
        if (((i.getID() == ItemId.GNOMECRUNCHIE.id()) || (i.getID() == ItemId.GNOMEBOWL.id())) || (i.getID() == ItemId.GNOMEBATTA.id())) {
            Functions.resetGnomeCooking(p);
            return false;
        }
        return false;
    }
}

