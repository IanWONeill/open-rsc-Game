package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.plugins.misc.Cannon;
import com.openrsc.server.util.rsc.DataConversions;


public class DwarfCannon implements QuestInterface , ObjectActionListener , PickupListener , TalkToNpcListener , WallObjectActionListener , ObjectActionExecutiveListener , PickupExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    private final Shop shop = new Shop(false, 3000, 100, 70, 2, new Item(ItemId.DWARF_CANNON_BASE.id(), 3), new Item(ItemId.DWARF_CANNON_STAND.id(), 3), new Item(ItemId.DWARF_CANNON_BARRELS.id(), 3), new Item(ItemId.DWARF_CANNON_FURNACE.id(), 3), new Item(ItemId.INSTRUCTION_MANUAL.id(), 7), new Item(ItemId.CANNON_AMMO_MOULD.id(), 7));

    @Override
    public int getQuestId() {
        return Quests.DWARF_CANNON;
    }

    @Override
    public String getQuestName() {
        return "Dwarf Cannon (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.DWARF_CANNON), true);
        p.message("@gre@You haved gained 1 quest point!");
        Functions.___message(p, "well done", "you have completed the dwarf cannon quest");
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return (n.getID() == NpcId.DWARF_COMMANDER.id()) || (n.getID() == NpcId.DWARF_CANNON_ENGINEER.id());
    }

    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.DWARF_CANNON_ENGINEER.id()) {
                        switch (p.getQuestStage(quest)) {
                            case 5 :
                                Functions.___playerTalk(p, n, "hello there");
                                Functions.___npcTalk(p, n, "can i help you?");
                                Functions.___playerTalk(p, n, "the Dwarf commander sent me, he's having trouble with his cannon");
                                Functions.___npcTalk(p, n, "of course, we forgot to send the ammo mould");
                                Functions.___playerTalk(p, n, "it fires a mould?");
                                Functions.___npcTalk(p, n, "don't be silly, the ammo's made by using a mould", "here, take these to him, the instructions explain everthing");
                                Functions.___playerTalk(p, n, "that's great, thanks");
                                Functions.___npcTalk(p, n, "thank you adventurer, the dwarf black guard will remember this");
                                Functions.___message(p, "the Cannon engineer gives you some notes and a mould");
                                Functions.addItem(p, ItemId.NULODIONS_NOTES.id(), 1);
                                Functions.addItem(p, ItemId.CANNON_AMMO_MOULD.id(), 1);
                                p.getCache().store("spoken_nulodion", true);
                                p.updateQuestStage(getQuestId(), 6);
                                break;
                            case 6 :
                                Functions.___playerTalk(p, n, "hello again");
                                if (!Functions.hasItem(p, ItemId.NULODIONS_NOTES.id())) {
                                    Functions.___playerTalk(p, n, "i've lost the notes");
                                    Functions.___npcTalk(p, n, "here take these");
                                    Functions.___message(p, "the Cannon engineer gives you some more notes");
                                    Functions.addItem(p, ItemId.NULODIONS_NOTES.id(), 1);
                                }
                                if (!Functions.hasItem(p, ItemId.CANNON_AMMO_MOULD.id())) {
                                    Functions.___playerTalk(p, n, "i've lost the cannon ball mould");
                                    Functions.___npcTalk(p, n, "deary me, you are trouble", "here take this one");
                                    Functions.___playerTalk(p, n, "the Cannon engineer gives you another mould");
                                    Functions.addItem(p, ItemId.CANNON_AMMO_MOULD.id(), 1);
                                }
                                Functions.___npcTalk(p, n, "so has the commander figured out how to work the cannon?");
                                Functions.___playerTalk(p, n, "not yet, but i'm sure he will");
                                Functions.___npcTalk(p, n, "if you can get those items to him it'll help");
                                break;
                            case -1 :
                                Functions.___playerTalk(p, n, "hello");
                                Functions.___npcTalk(p, n, "hello traveller, how's things?");
                                Functions.___playerTalk(p, n, "not bad thanks, yourself?");
                                Functions.___npcTalk(p, n, "i'm good, just working hard as usual");
                                int completeMenu = Functions.___showMenu(p, n, "i was hoping you might sell me a cannon?", "well, take care of yourself then", "i want to know more about the cannon?", "i've lost my cannon");
                                if (completeMenu == 0) {
                                    Functions.___npcTalk(p, n, "hmmm", "i shouldn't really, but as you helped us so much", "well, i could sort something out", "i'll warn you though, they don't come cheap");
                                    Functions.___playerTalk(p, n, "how much?");
                                    Functions.___npcTalk(p, n, "for the full set up.. 750 000 coins", "or i can sell you the seperate parts for 200 000 each");
                                    Functions.___playerTalk(p, n, "that's not cheap");
                                    int cannon = Functions.___showMenu(p, n, "ok, i'll take a cannon please", "can i look at the seperate parts please", "sorry, that's too much for me", "have you any ammo or instructions to sell?");
                                    if (cannon == 0) {
                                        Functions.___npcTalk(p, n, "ok then, but keep it quiet..");
                                        Functions.___npcTalk(p, n, "this thing's top secret");
                                        if ((((Functions.hasItem(p, ItemId.DWARF_CANNON_BASE.id()) || Functions.hasItem(p, ItemId.DWARF_CANNON_STAND.id())) || Functions.hasItem(p, ItemId.DWARF_CANNON_BARRELS.id())) || Functions.hasItem(p, ItemId.DWARF_CANNON_FURNACE.id())) || p.getCache().hasKey("has_cannon")) {
                                            Functions.___npcTalk(p, n, "wait a moment, our records show you already own some cannon equipment", "i'm afraid you can only have one set at a time");
                                            return null;
                                        }
                                        if (p.getInventory().countId(ItemId.COINS.id()) >= 750000) {
                                            Functions.___message(p, "you give the Cannon engineer 750 000 coins");
                                            p.getInventory().remove(ItemId.COINS.id(), 750000);
                                            Functions.___message(p, "he gives you the four parts that make the cannon");
                                            Functions.addItem(p, ItemId.DWARF_CANNON_BASE.id(), 1);
                                            Functions.addItem(p, ItemId.DWARF_CANNON_STAND.id(), 1);
                                            Functions.addItem(p, ItemId.DWARF_CANNON_BARRELS.id(), 1);
                                            Functions.addItem(p, ItemId.DWARF_CANNON_FURNACE.id(), 1);
                                            Functions.___message(p, "a ammo mould and an instruction manual");
                                            Functions.addItem(p, ItemId.CANNON_AMMO_MOULD.id(), 1);
                                            Functions.addItem(p, ItemId.INSTRUCTION_MANUAL.id(), 1);
                                            Functions.___npcTalk(p, n, "there you go, you be carefull with that thing");
                                            Functions.___playerTalk(p, n, "will do, take care mate");
                                            Functions.___npcTalk(p, n, "take care adventurer");
                                        } else {
                                            Functions.___playerTalk(p, n, "oops, i don't have enough money");
                                            Functions.___npcTalk(p, n, "sorry, i can't go any lower than that");
                                        }
                                    } else
                                        if (cannon == 1) {
                                            Functions.___npcTalk(p, n, "of course!");
                                            p.setAccessingShop(shop);
                                            ActionSender.showShop(p, shop);
                                        } else
                                            if (cannon == 2) {
                                                Functions.___npcTalk(p, n, "fair enough, it's too much for most of us");
                                            } else
                                                if (cannon == 3) {
                                                    Functions.___npcTalk(p, n, "yes, of course");
                                                    p.setAccessingShop(shop);
                                                    ActionSender.showShop(p, shop);
                                                }



                                } else
                                    if (completeMenu == 1) {
                                        // NOTHING
                                    } else
                                        if (completeMenu == 2) {
                                            Functions.___npcTalk(p, n, "there's only so much i can tell you adventurer", "we've been working on this little beauty for some time now");
                                            Functions.___playerTalk(p, n, "is it effective?");
                                            Functions.___npcTalk(p, n, "in short bursts it's very effective, the most destructive weapon to date", "the cannon automatically targets monsters close by", "you just have to make the ammo and let rip");
                                        } else
                                            if (completeMenu == 3) {
                                                if ((p.getCache().hasKey("cannon_stage") && p.getCache().hasKey("cannon_x")) && p.getCache().hasKey("cannon_y")) {
                                                    Functions.___npcTalk(p, n, "that's unfortunate...but don't worry, i can sort you out");
                                                    int cannonX = p.getCache().getInt("cannon_x");
                                                    int cannonY = p.getCache().getInt("cannon_y");
                                                    GameObject cannon = p.getWorld().getRegionManager().getRegion(cannonX, cannonY).getGameObject(cannonX, cannonY);
                                                    // does not exist or the object there is not a cannon.
                                                    if ((cannon == null) || (!DataConversions.inArray(Cannon.cannonObjectIDs, cannon.getID()))) {
                                                        Functions.___message(p, "the dwarf gives you a new cannon");
                                                        Functions.___npcTalk(p, n, "keep that quite or i'll be in real trouble");
                                                        Functions.___playerTalk(p, n, "thanks alot");
                                                        Functions.___npcTalk(p, n, "no worries");
                                                        int cannonStage = p.getCache().getInt("cannon_stage");
                                                        switch (cannonStage) {
                                                            case 1 :
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
                                                                break;
                                                            case 2 :
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id()));
                                                                break;
                                                            case 3 :
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id()));
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id()));
                                                                break;
                                                            case 4 :
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id()));
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id()));
                                                                p.getInventory().add(new Item(ItemId.DWARF_CANNON_FURNACE.id()));
                                                                break;
                                                        }
                                                        p.getCache().remove("cannon_stage");
                                                        p.getCache().remove("cannon_x");
                                                        p.getCache().remove("cannon_y");
                                                        p.getCache().remove("has_cannon");
                                                    } else {
                                                        Functions.___npcTalk(p, n, "oh dear, i'm only allowed to replace cannons...", "...that were stolen in action", "i'm sorry but you'll have to buy a new set");
                                                    }
                                                } else {
                                                    Functions.___npcTalk(p, n, "that's unfortunate...but don't worry, i can sort you out");
                                                    Functions.___npcTalk(p, n, "oh dear, i'm only allowed to replace cannons...", "...that were stolen in action", "i'm sorry but you'll have to buy a new set");
                                                }
                                            }
                                break;
                        }
                    } else
                        if (n.getID() == NpcId.DWARF_COMMANDER.id()) {
                            switch (p.getQuestStage(quest)) {
                                case 0 :
                                    Functions.___playerTalk(p, n, "hello");
                                    Functions.___npcTalk(p, n, "hello traveller, i'm pleased to see you", "we were hoping to find an extra pair of hands", "that's if you don't mind helping?");
                                    Functions.___playerTalk(p, n, "why, what's wrong?");
                                    Functions.___npcTalk(p, n, "as part of the dwarven black guard..", "...it is our duty to protect these mines", "but we just don't have the man power", "could you help?");
                                    int first = Functions.___showMenu(p, n, "i'm sorry, i'm too busy mining", "yeah, i'd love to help");
                                    if (first == 0) {
                                        Functions.___npcTalk(p, n, "ok then, we'll have find someone else");
                                    } else
                                        if (first == 1) {
                                            Functions.___npcTalk(p, n, "thankyou, we have no time to waste", "the goblins have been attacking from the forests to the south", "they manage to get through the broken railings", "could you please replace them with these new ones");
                                            Functions.___playerTalk(p, n, "sounds easy enough");
                                            Functions.___message(p, "the Dwarf commander gives you six railings");
                                            Functions.addItem(p, ItemId.RAILING_DWARF_CANNON.id(), 6);
                                            Functions.___npcTalk(p, n, "let me know once you've fixed the railings");
                                            Functions.___playerTalk(p, n, "ok , commander");
                                            p.updateQuestStage(getQuestId(), 1);
                                        }

                                    break;
                                case 1 :
                                    Functions.___playerTalk(p, n, "hello");
                                    Functions.___npcTalk(p, n, "hello again traveller", "how are you doing with those railings?");
                                    Functions.___playerTalk(p, n, "i'm getting there");
                                    if (((((p.getCache().hasKey("railone") && p.getCache().hasKey("railtwo")) && p.getCache().hasKey("railthree")) && p.getCache().hasKey("railfour")) && p.getCache().hasKey("railfive")) && p.getCache().hasKey("railsix")) {
                                        Functions.___npcTalk(p, n, "the goblins seemed to have stopped getting in", "i think you've done the job");
                                        Functions.___playerTalk(p, n, "good stuff");
                                        Functions.___npcTalk(p, n, "could you do me one more favour?", "i need you to go check up on a guard", "he should be in the black guard watch tower just to the south of here", "he should have reported in by now");
                                        Functions.___playerTalk(p, n, "ok, i'll see what i can find out");
                                        Functions.___npcTalk(p, n, "thanks traveller");
                                        p.updateQuestStage(getQuestId(), 2);
                                        // REMOVE AFTER DONE USING!!!
                                        p.getCache().remove("railone");
                                        p.getCache().remove("railtwo");
                                        p.getCache().remove("railthree");
                                        p.getCache().remove("railfour");
                                        p.getCache().remove("railfive");
                                        p.getCache().remove("railsix");
                                    } else {
                                        Functions.___npcTalk(p, n, "the goblins are still getting in", "so there must still be some broken railings");
                                        Functions.___playerTalk(p, n, "don't worry, i'll find them soon enough");
                                        if (!Functions.hasItem(p, ItemId.RAILING_DWARF_CANNON.id())) {
                                            Functions.___playerTalk(p, n, "but i'm out of railings");
                                            Functions.___npcTalk(p, n, "ok, we've got plenty");
                                            Functions.___message(p, "the Dwarf commander gives you another railing");
                                            Functions.addItem(p, ItemId.RAILING_DWARF_CANNON.id(), 1);
                                        }
                                    }
                                    break;
                                case 2 :
                                    Functions.___playerTalk(p, n, "hello");
                                    // this could be just by going up the ladder maybe?
                                    if (p.getCache().hasKey("grabed_dwarf_remains")) {
                                        Functions.___npcTalk(p, n, "have you been to the watch tower yet?");
                                        Functions.___playerTalk(p, n, "yes, i went up but there was no one");
                                        Functions.___npcTalk(p, n, "that's strange, gilob never leaves his post");
                                        if (Functions.hasItem(p, ItemId.DWARF_REMAINS.id())) {
                                            Functions.___playerTalk(p, n, "i may have some bad news for you commander");
                                            Functions.___message(p, "you show the Dwarf commander the remains");
                                            Functions.___npcTalk(p, n, "what's this?, oh no , it can't be!");
                                            Functions.___playerTalk(p, n, "i'm sorry, it looks like the goblins got him");
                                            Functions.___npcTalk(p, n, "noooo... those..those animals", "but where's gilobs son?, he was also there");
                                            Functions.___playerTalk(p, n, "the goblins must have taken him");
                                            Functions.___npcTalk(p, n, "please traveller, seek out the goblins base..", "...and return the lad to us", "they must sleep somewhere!");
                                            Functions.___playerTalk(p, n, "ok, i'll see if i can find their hide out");
                                            Functions.removeItem(p, ItemId.DWARF_REMAINS.id(), 1);
                                            p.updateQuestStage(getQuestId(), 3);
                                            p.getCache().remove("grabed_dwarf_remains");
                                        } else {
                                            Functions.___npcTalk(p, n, "his son was also with him, its too strange", "can you return and look for clues?");
                                            Functions.___playerTalk(p, n, "ok then");
                                        }
                                    } else {
                                        Functions.___npcTalk(p, n, "hello, any news from the watch man?");
                                        Functions.___playerTalk(p, n, "not yet");
                                        Functions.___npcTalk(p, n, "well, as quick as you can then");
                                    }
                                    break;
                                case 3 :
                                    if (p.getCache().hasKey("savedlollk")) {
                                        Functions.___playerTalk(p, n, "hello, has lollk returned yet?");
                                        Functions.___npcTalk(p, n, "he has, and i thank you from the bottom of my heart..", "...with out you he'd be goblin barbecue");
                                        Functions.___playerTalk(p, n, "always a pleasure to help");
                                        Functions.___npcTalk(p, n, "in that case i have one more favour to ask you", "as you've seen, our defences are too weak against those goblins", "the black guard have sent us a cannon to help the situation");
                                        Functions.___playerTalk(p, n, "sounds good");
                                        Functions.___npcTalk(p, n, "unfortunatly we're having trouble fixing the thing", "the cannon is stored in our shed", "if you could fix it, it would be a great help");
                                        int gobMenu = Functions.___showMenu(p, n, "ok, i'll see what i can do", "sorry, i've done enough for today");
                                        if (gobMenu == 0) {
                                            Functions.___npcTalk(p, n, "that's great,you'll need this");
                                            Functions.___message(p, "the Dwarf commander gives you a tool kit");
                                            Functions.addItem(p, ItemId.TOOL_KIT.id(), 1);
                                            Functions.___npcTalk(p, n, "let me know how you get on");
                                            p.updateQuestStage(getQuestId(), 4);
                                            p.getCache().remove("savedlollk");
                                        } else
                                            if (gobMenu == 1) {
                                                Functions.___npcTalk(p, n, " fair enough, take care traveller");
                                            }

                                        return null;
                                    }
                                    Functions.___playerTalk(p, n, "hello again");
                                    Functions.___npcTalk(p, n, "traveller have you managed to find the goblins base?");
                                    Functions.___playerTalk(p, n, "not yet i'm afraid, but i'll keep looking");
                                    break;
                                case 4 :
                                    if (p.getCache().hasKey("cannon_complete")) {
                                        Functions.___playerTalk(p, n, "hello again");
                                        Functions.___npcTalk(p, n, "hello there traveller, how's things?");
                                        Functions.___playerTalk(p, n, "well, i think i've done it, take a look");
                                        Functions.___npcTalk(p, n, "really!");
                                        Functions.___message(p, "the Dwarf commander pops into the shed to take a closer look");
                                        Functions.___npcTalk(p, n, "well i don't believe it, it seems to be in working order");
                                        Functions.___playerTalk(p, n, "not bad for an adventurer");
                                        Functions.___npcTalk(p, n, "not bad at all, your effort is appreciated my friend", "now, if i could only figure what the thing uses as ammo", "the black guard forgot to send instructions", "i know i said that was the last favour..but..");
                                        Functions.___playerTalk(p, n, "what now?");
                                        Functions.___npcTalk(p, n, "i can't leave this post, could you go to the black guard..", "..base and find out what this thing actually shoots?");
                                        int finale = Functions.___showMenu(p, n, "sorry, i've really done enough", "ok then, just for you");
                                        if (finale == 0) {
                                            Functions.___npcTalk(p, n, "fair enough");
                                        } else
                                            if (finale == 1) {
                                                Functions.___npcTalk(p, n, "you're a good adventurer, we were lucky to find you", "the base is located just south of the ice mountain", "you'll need to speak to the dwarf Cannon engineer", "he's the weapons development chief for the black guard", "so if anyone knows how to fire that thing, it'll be him");
                                                Functions.___playerTalk(p, n, "ok, i'll see what i can do");
                                                p.updateQuestStage(getQuestId(), 5);
                                                p.getCache().remove("cannon_complete");// REMOVE AFTER

                                                // USE!!!
                                            }

                                        return null;
                                    }
                                    Functions.___npcTalk(p, n, "how are doing in there bold adventurer?", "we've been trying our best with that thing", "but i just haven't got the patience");
                                    Functions.___playerTalk(p, n, "it's not an easy job, but i'm getting there");
                                    Functions.___npcTalk(p, n, "good stuff, let me know if you have any luck", "if we manage to get that thing working...", "those goblins will be know trouble at all");
                                    if (!Functions.hasItem(p, ItemId.TOOL_KIT.id())) {
                                        Functions.___playerTalk(p, n, "i'm afraid i lost the tool kit");
                                        Functions.___npcTalk(p, n, "that was silly, never mind, here you go");
                                        Functions.___message(p, "the Dwarf commander gives you another tool kit");
                                        Functions.addItem(p, ItemId.TOOL_KIT.id(), 1);
                                    }
                                    break;
                                case 5 :
                                case 6 :
                                    if ((p.getCache().hasKey("spoken_nulodion") && Functions.hasItem(p, ItemId.NULODIONS_NOTES.id())) && Functions.hasItem(p, ItemId.CANNON_AMMO_MOULD.id())) {
                                        Functions.___playerTalk(p, n, "hi");
                                        Functions.___npcTalk(p, n, "hello traveller, any word from the Cannon engineer?");
                                        Functions.___playerTalk(p, n, "yes, i have spoken to him", "he gave me these to give to you");
                                        Functions.___message(p, "you hand the Dwarf commander the mould and the notes");
                                        Functions.removeItem(p, ItemId.NULODIONS_NOTES.id(), 1);
                                        Functions.removeItem(p, ItemId.CANNON_AMMO_MOULD.id(), 1);
                                        Functions.___npcTalk(p, n, "aah, of course, we make the ammo", "this is great, now we will be able to defend ourselves", "i don't know how to thank you");
                                        Functions.___playerTalk(p, n, "you could give me a cannon");
                                        Functions.___npcTalk(p, n, "hah, you'd be lucky, those things are worth a fortune", "hmmm, now i think about it the Cannon engineer may be able to help", "he controls production of the cannons", "he won't be able to give you one", "but for the right price, i'm sure he'll sell one to you");
                                        Functions.___playerTalk(p, n, "hmmm, sounds interesting");
                                        Functions.___npcTalk(p, n, "take care of yourself traveller, and thanks again");
                                        Functions.___playerTalk(p, n, "you take care too");
                                        p.getCache().remove("spoken_nulodion");
                                        p.sendQuestComplete(Quests.DWARF_CANNON);
                                    } else
                                        if (p.getCache().hasKey("spoken_nulodion")) {
                                            Functions.___playerTalk(p, n, "hi");
                                            Functions.___npcTalk(p, n, "hello traveller, any word from the Cannon engineer?");
                                            Functions.___playerTalk(p, n, "yes, i have spoken to him", "he gave me some items to give you...", "but i seem to have lost something");
                                            Functions.___npcTalk(p, n, "if you could go back and get another, i'd appreciate it");
                                            Functions.___playerTalk(p, n, "ok then");
                                        } else {
                                            Functions.___playerTalk(p, n, "hi again");
                                            Functions.___npcTalk(p, n, "hello traveller", "any word from the Cannon engineer?");
                                            Functions.___playerTalk(p, n, "not yet");
                                            Functions.___npcTalk(p, n, "the black guard camp is just south of the ice mountain", "the quicker we can get some ammo for this thing..", ".. the quicker those goblins will leave us be");
                                            Functions.___playerTalk(p, n, "i'll get to it");
                                        }

                                    break;
                                case -1 :
                                    Functions.___playerTalk(p, n, "hello");
                                    Functions.___npcTalk(p, n, "well, hello there, how you doing?");
                                    Functions.___playerTalk(p, n, "not bad, yourself?");
                                    Functions.___npcTalk(p, n, "i'm great, the goblins can't get close with this cannon blasting at them");
                                    break;
                            }
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
        return (((((((obj.getID() == 181) || (obj.getID() == 182)) || (obj.getID() == 183)) || (obj.getID() == 184)) || (obj.getID() == 185)) || (obj.getID() == 186)) || (obj.getID() == 194)) || ((obj.getID() == 197) && (obj.getX() == 278));
    }

    private void rail(Player p, GameObject obj) {
        Functions.___message(p, "you search the railing", "one railing is broken and needs to be replaced");
        int railMenu = Functions.___showMenu(p, new String[]{ "try to replace railing", "leave it be" });
        if (railMenu == 0) {
            if (failToReplace()) {
                Functions.___message(p, "you attempt to replace the missing railing", "but you fail and cut yourself trying");
                p.damage(DataConversions.random(2, 3));
            } else {
                Functions.___message(p, "you attempt to replace the missing railing", "you replace the railing with no problems");
                p.getInventory().remove(ItemId.RAILING_DWARF_CANNON.id(), 1);
                if (obj.getID() == 181) {
                    p.getCache().store("railone", true);
                } else
                    if (obj.getID() == 182) {
                        p.getCache().store("railtwo", true);
                    } else
                        if (obj.getID() == 183) {
                            p.getCache().store("railthree", true);
                        } else
                            if (obj.getID() == 184) {
                                p.getCache().store("railfour", true);
                            } else
                                if (obj.getID() == 185) {
                                    p.getCache().store("railfive", true);
                                } else
                                    if (obj.getID() == 186) {
                                        p.getCache().store("railsix", true);
                                    }





            }
        } else
            if (railMenu == 1) {
                // NOTHING
            }

    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 193) {
                        Functions.___message(p, "you search the railing", "but find nothing of interest");
                    } else
                        if ((obj.getID() == 181) && (p.getQuestStage(getQuestId()) == 1)) {
                            if (p.getCache().hasKey("railone")) {
                                p.message("you have already fixed this railing");
                                return null;
                            }
                            rail(p, obj);
                        } else
                            if ((obj.getID() == 182) && (p.getQuestStage(getQuestId()) == 1)) {
                                if (p.getCache().hasKey("railtwo")) {
                                    p.message("you have already fixed this railing");
                                    return null;
                                }
                                rail(p, obj);
                            } else
                                if ((obj.getID() == 183) && (p.getQuestStage(getQuestId()) == 1)) {
                                    if (p.getCache().hasKey("railthree")) {
                                        p.message("you have already fixed this railing");
                                        return null;
                                    }
                                    rail(p, obj);
                                } else
                                    if ((obj.getID() == 184) && (p.getQuestStage(getQuestId()) == 1)) {
                                        if (p.getCache().hasKey("railfour")) {
                                            p.message("you have already fixed this railing");
                                            return null;
                                        }
                                        rail(p, obj);
                                    } else
                                        if ((obj.getID() == 185) && (p.getQuestStage(getQuestId()) == 1)) {
                                            if (p.getCache().hasKey("railfive")) {
                                                p.message("you have already fixed this railing");
                                                return null;
                                            }
                                            rail(p, obj);
                                        } else
                                            if ((obj.getID() == 186) && (p.getQuestStage(getQuestId()) == 1)) {
                                                if (p.getCache().hasKey("railsix")) {
                                                    p.message("you have already fixed this railing");
                                                    return null;
                                                }
                                                rail(p, obj);
                                            } else
                                                if (obj.getID() == 194) {
                                                    if (p.getQuestStage(getQuestId()) == 4) {
                                                        Functions.doDoor(obj, p);
                                                    } else {
                                                        p.message("the door is locked");
                                                    }
                                                } else
                                                    if ((obj.getID() == 197) && (obj.getX() == 278)) {
                                                        if (Functions.atQuestStages(p, getQuestId(), 5, 6, -1)) {
                                                            p.message("you go through the door");
                                                            Functions.doDoor(obj, p);
                                                        } else {
                                                            p.message("the door is locked");
                                                        }
                                                    }








                    return null;
                });
            }
        };
    }

    private boolean failToReplace() {
        return DataConversions.random(0, 100) > 75;
    }

    private boolean failToMultiCannon() {
        return DataConversions.random(0, 100) > 60;
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return ((((((obj.getID() == 982) && (obj.getY() == 523)) || ((obj.getID() == 981) || (obj.getID() == 985))) || (obj.getID() == 994)) || (obj.getID() == 983)) || (obj.getID() == 986)) || (obj.getID() == 987);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
        final QuestInterface quest = this;
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 981) {
                        player.message("you climb up the ladder");
                        if (player.getQuestStage(quest) == 0) {
                            player.message("but the trap door will not open");
                            return null;
                        }
                        player.teleport(616, 1435, false);
                    } else
                        if (obj.getID() == 985) {
                            player.message("you climb down the ladder");
                            player.teleport(616, 493, false);
                        } else
                            if ((obj.getID() == 982) && (obj.getY() == 523)) {
                                Functions.___message(player, "you cautiously enter the cave");
                                player.teleport(578, 3356, false);
                            } else
                                if (obj.getID() == 983) {
                                    Functions.___message(player, "you climb the mudpile");
                                    player.teleport(578, 521, false);
                                } else
                                    if (obj.getID() == 986) {
                                        Functions.___message(player, "you search the crate", "but it's empty");
                                    } else
                                        if (obj.getID() == 987) {
                                            // only allow at quest stage and before being rescued
                                            if ((player.getQuestStage(quest) == 3) && (!player.getCache().hasKey("savedlollk"))) {
                                                Functions.___message(player, "you search the crate", "inside you see a dwarf child tied up", "you untie the child");
                                                Npc lollk = Functions.spawnNpc(player.getWorld(), NpcId.LOLLK.id(), 619, 3314, 60000);
                                                lollk.face(player);
                                                player.face(lollk);
                                                Functions.___npcTalk(player, lollk, "thank the heavens, you saved me", "i thought i'd be goblin lunch for sure");
                                                Functions.___playerTalk(player, lollk, "are you ok?");
                                                Functions.___npcTalk(player, lollk, "i think so, i'd better run of home");
                                                Functions.___playerTalk(player, lollk, "that's right , you get going, i'll catch up");
                                                Functions.___npcTalk(player, lollk, "thanks again brave adventurer");
                                                Functions.___message(player, "the dwarf child runs off into the caverns");
                                                player.getCache().store("savedlollk", true);
                                                lollk.remove();
                                            } else {
                                                Functions.___message(player, "you search the crate", "but it's empty");
                                            }
                                        } else
                                            if (obj.getID() == 994) {
                                                if (player.getCache().hasKey("cannon_complete")) {
                                                    player.message("It's a strange dwarf contraption");
                                                    return null;
                                                }
                                                player.message("you inspect the multi cannon");
                                                if (((player.getCache().hasKey("pipe") && player.getCache().hasKey("barrel")) && player.getCache().hasKey("axle")) && player.getCache().hasKey("shaft")) {
                                                    player.message("the cannon seems to be in complete working order");
                                                    Functions.___message(player, "lawgof will be pleased");
                                                    player.getCache().store("cannon_complete", true);
                                                    player.getCache().remove("pipe");
                                                    player.getCache().remove("barrel");
                                                    player.getCache().remove("axle");
                                                    player.getCache().remove("shaft");
                                                    return null;
                                                }
                                                if (failToMultiCannon()) {
                                                    player.message("you try, but can't quite find the problem");
                                                    Functions.___message(player, "maybe you should inspect it again");
                                                } else {
                                                    player.message("you see that there are some damaged components");
                                                    Functions.___message(player, "a pipe, a gun barrel, an axle and a shaft seem to be damaged", "which part of the cannon will you attempt to fix?");
                                                    int cannonMenu = Functions.___showMenu(player, null, new String[]{ "Pipe", "Barrel", "Axle", "Shaft", "none" });
                                                    if (cannonMenu == 0) {
                                                        if (player.getCache().hasKey("pipe")) {
                                                            player.message("you've already fixed this part of the cannon");
                                                            return null;
                                                        }
                                                        Functions.___message(player, "you use your tool kit and attempt to fix the pipe");
                                                        Functions.showBubble(player, new Item(ItemId.TOOL_KIT.id()));
                                                        if (failToMultiCannon()) {
                                                            Functions.___message(player, "it's too hard, you fail to fix it", "maybe you should try again");
                                                        } else {
                                                            Functions.___message(player, "after some tinkering you manage to fix it");
                                                            player.getCache().store("pipe", true);
                                                        }
                                                    } else
                                                        if (cannonMenu == 1) {
                                                            if (player.getCache().hasKey("barrel")) {
                                                                player.message("you've already fixed this part of the cannon");
                                                                return null;
                                                            }
                                                            Functions.___message(player, "you use your tool kit and attempt to fix the barrel");
                                                            Functions.showBubble(player, new Item(ItemId.TOOL_KIT.id()));
                                                            if (failToMultiCannon()) {
                                                                Functions.___message(player, "it's too hard, you fail to fix it", "maybe you should try again");
                                                            } else {
                                                                Functions.___message(player, "after some tinkering you manage to fix it");
                                                                player.getCache().store("barrel", true);
                                                            }
                                                        } else
                                                            if (cannonMenu == 2) {
                                                                if (player.getCache().hasKey("axle")) {
                                                                    player.message("you've already fixed this part of the cannon");
                                                                    return null;
                                                                }
                                                                Functions.___message(player, "you use your tool kit and attempt to fix the axle");
                                                                Functions.showBubble(player, new Item(ItemId.TOOL_KIT.id()));
                                                                if (failToMultiCannon()) {
                                                                    Functions.___message(player, "it's too hard, you fail to fix it", "maybe you should try again");
                                                                } else {
                                                                    Functions.___message(player, "after some tinkering you manage to fix it");
                                                                    player.getCache().store("axle", true);
                                                                }
                                                            } else
                                                                if (cannonMenu == 3) {
                                                                    if (player.getCache().hasKey("shaft")) {
                                                                        player.message("you've already fixed this part of the cannon");
                                                                        return null;
                                                                    }
                                                                    Functions.___message(player, "you use your tool kit and attempt to fix the shaft");
                                                                    Functions.showBubble(player, new Item(ItemId.TOOL_KIT.id()));
                                                                    if (failToMultiCannon()) {
                                                                        Functions.___message(player, "it's too hard, you fail to fix it", "maybe you should try again");
                                                                    } else {
                                                                        Functions.___message(player, "after some tinkering you manage to fix it");
                                                                        player.getCache().store("shaft", true);
                                                                    }
                                                                } else
                                                                    if (cannonMenu == 4) {
                                                                        // nothing
                                                                    }




                                                }
                                            }






                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return i.getID() == ItemId.DWARF_REMAINS.id();
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ItemId.DWARF_REMAINS.id()) {
                        if (Functions.hasItem(p, ItemId.DWARF_REMAINS.id())) {
                            p.message("carrying one 'dwarfs remains' is bad enough");
                            return null;
                        }
                        if ((p.getQuestStage(quest) == 2) && (!p.getCache().hasKey("grabed_dwarf_remains"))) {
                            p.getCache().store("grabed_dwarf_remains", true);
                        }
                        p.getWorld().unregisterItem(i);
                        Functions.addItem(p, ItemId.DWARF_REMAINS.id(), 1);
                    }
                    return null;
                });
            }
        };
    }

    class RAILINGS {
        public static final int rail = 0;
    }
}

