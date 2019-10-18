package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnGroundItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnGroundItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class GertrudesCat implements QuestInterface , DropListener , InvUseOnGroundItemListener , InvUseOnItemListener , ObjectActionListener , PickupListener , TalkToNpcListener , WallObjectActionListener , DropExecutiveListener , InvUseOnGroundItemExecutiveListener , InvUseOnItemExecutiveListener , ObjectActionExecutiveListener , PickupExecutiveListener , TalkToNpcExecutiveListener , WallObjectActionExecutiveListener {
    @Override
    public int getQuestId() {
        return Quests.GERTRUDES_CAT;
    }

    @Override
    public String getQuestName() {
        return "Gertrude's Cat (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(final Player p) {
        Functions.incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.GERTRUDES_CAT), true);
        p.message("@gre@You haved gained 1 quest point!");
        p.message("well done, you have completed gertrudes cat quest");
    }

    @Override
    public boolean blockTalkToNpc(final Player p, final Npc n) {
        return DataConversions.inArray(new int[]{ NpcId.GERTRUDE.id(), NpcId.SHILOP.id(), NpcId.WILOUGH.id(), NpcId.KANEL.id(), NpcId.PHILOP.id() }, n.getID());
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        final QuestInterface quest = this;
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((n.getID() == NpcId.KANEL.id()) || (n.getID() == NpcId.PHILOP.id())) {
                        p.message("The boy's busy playing");
                    } else
                        if (n.getID() == NpcId.GERTRUDE.id()) {
                            switch (p.getQuestStage(quest)) {
                                case 0 :
                                    Functions.___playerTalk(p, n, "hello, are you ok?");
                                    Functions.___npcTalk(p, n, "do i look ok?...those kids drive me crazy", "...i'm sorry,  it's just, ive lost her");
                                    Functions.___playerTalk(p, n, "lost who?");
                                    Functions.___npcTalk(p, n, "fluffs, poor fluffs, she never hurt anyone");
                                    Functions.___playerTalk(p, n, "who's fluffs");
                                    Functions.___npcTalk(p, n, "my beloved feline friend fluffs", "she's been purring by my side for almost a decade", "please, could you go search for her...", "...while i look over the kids?");
                                    int first = Functions.___showMenu(p, n, "well, i suppose i could", "what's in it for me?", "sorry, i'm too busy to play pet rescue");
                                    if (first == 0) {
                                        Functions.___npcTalk(p, n, "really?, thank you so much", "i really have no idea where she could be", "i think my sons, shilop and Wilough, saw the cat last", "they'll be out in the market place");
                                        Functions.___playerTalk(p, n, "alright then, i'll see what i can do");
                                        p.updateQuestStage(getQuestId(), 1);
                                    } else
                                        if (first == 1) {
                                            Functions.___npcTalk(p, n, "i'm sorry, i'm too poor to pay you anything", "the best i could offer is a warm meal", "so, can you help?");
                                            int second = Functions.___showMenu(p, n, "well, i suppose i could", "sorry, i'm too busy to play pet rescue");
                                            if (second == 0) {
                                                Functions.___npcTalk(p, n, "really?, thank you so much", "i really have no idea where she could be", "i think my sons, shilop and Wilough, saw the cat last", "they'll be out in the market place");
                                                Functions.___playerTalk(p, n, "alright then, i'll see what i can do");
                                                p.updateQuestStage(getQuestId(), 1);
                                            } else
                                                if (second == 1) {
                                                    Functions.___npcTalk(p, n, " well, ok then, i'll have to find someone else");
                                                }

                                        } else
                                            if (first == 2) {
                                                Functions.___npcTalk(p, n, " well, ok then, i'll have to find someone else");
                                            }


                                    break;
                                case 1 :
                                    Functions.___playerTalk(p, n, "hello gertrude");
                                    Functions.___npcTalk(p, n, "have you seen my poor fluffs?");
                                    Functions.___playerTalk(p, n, "i'm afraid not");
                                    Functions.___npcTalk(p, n, "what about shilop?");
                                    Functions.___playerTalk(p, n, "no sign of him either");
                                    Functions.___npcTalk(p, n, "hmmm...strange, he should be at the market");
                                    break;
                                case 2 :
                                    if ((!p.getCache().hasKey("cat_milk")) && (!p.getCache().hasKey("cat_sardine"))) {
                                        Functions.___playerTalk(p, n, "hello gertrude");
                                        Functions.___npcTalk(p, n, "hello again, did you manage to find shilop?", "i can't keep an eye on him for the life of me");
                                        Functions.___playerTalk(p, n, "he does seem quite a handfull");
                                        Functions.___npcTalk(p, n, "you have no idea!.... did he help at all?");
                                        Functions.___playerTalk(p, n, "i think so, i'm just going to look now");
                                        Functions.___npcTalk(p, n, "thanks again adventurer");
                                    } else
                                        if (p.getCache().hasKey("cat_milk") && (!p.getCache().hasKey("cat_sardine"))) {
                                            Functions.___playerTalk(p, n, "hello again");
                                            Functions.___npcTalk(p, n, "hello, how's it going?, any luck?");
                                            Functions.___playerTalk(p, n, "yes, i've found fluffs");
                                            Functions.___npcTalk(p, n, "well well, you are clever, did you bring her back?");
                                            Functions.___playerTalk(p, n, "well, that's the thing, she refuses to leave");
                                            Functions.___npcTalk(p, n, "oh dear, oh dear, maybe she's just hungry", "she loves doogle sardines but i'm all out");
                                            Functions.___playerTalk(p, n, "doogle sardines?");
                                            Functions.___npcTalk(p, n, "yes, raw sardines seasoned with doogle leaves", "unfortunatly i've used all my doogle leaves", "but you may find some in the woods out back");
                                        } else
                                            if (p.getCache().hasKey("cat_sardine")) {
                                                Functions.___playerTalk(p, n, "hi");
                                                Functions.___npcTalk(p, n, "hey traveller, did fluffs eat the sardines?");
                                                Functions.___playerTalk(p, n, "yeah, she loved them, but she still won't leave");
                                                Functions.___npcTalk(p, n, "well that is strange, there must be a reason!");
                                            }


                                    break;
                                case 3 :
                                    Functions.___playerTalk(p, n, "hello gertrude", "fluffs ran off with her two kittens");
                                    Functions.___npcTalk(p, n, "you're back , thank you, thank you", "fluffs just came back, i think she was just upset...", "...as she couldn't find her kittens");
                                    Functions.___message(p, "gertrude gives you a hug");
                                    Functions.___npcTalk(p, n, "if you hadn't found her kittens they'd have died out there");
                                    Functions.___playerTalk(p, n, "that's ok, i like to do my bit");
                                    Functions.___npcTalk(p, n, "i don't know how to thank you", "I have no real material possessions..but i do have kittens", "..i can only really look after one");
                                    Functions.___playerTalk(p, n, "well, if it needs a home");
                                    Functions.___npcTalk(p, n, "i would sell it to my cousin in west ardounge..", "i hear there's a rat epidemic there..but it's too far", "here you go, look after her and thank you again");
                                    Functions.___message(p, "gertrude gives you a kitten...", "...and some food");
                                    Functions.addItem(p, ItemId.KITTEN.id(), 1);
                                    Functions.addItem(p, ItemId.CHOCOLATE_CAKE.id(), 1);
                                    Functions.addItem(p, ItemId.STEW.id(), 1);
                                    p.sendQuestComplete(Quests.GERTRUDES_CAT);
                                    break;
                                case -1 :
                                    Functions.___playerTalk(p, n, "hello again gertrude");
                                    Functions.___npcTalk(p, n, "well hello adventurer, how are you?");
                                    if (Functions.hasItem(p, ItemId.KITTEN.id()) || p.getBank().hasItemId(ItemId.KITTEN.id())) {
                                        Functions.___playerTalk(p, n, "pretty good thanks, yourself?");
                                        Functions.___npcTalk(p, n, "same old, running after shilob most of the time");
                                        Functions.___playerTalk(p, n, "never mind, i'm sure he'll calm down with age");
                                    } else {
                                        Functions.___playerTalk(p, n, "i'm ok, but i lost my kitten");
                                        Functions.___npcTalk(p, n, "that is a shame..as it goes fluffs just had more", "i'm selling them at 100 coins each...", "...it was shilop's idea");
                                        Functions.___playerTalk(p, n, "!");
                                        Functions.___npcTalk(p, n, "would you like one");
                                        int menu = Functions.___showMenu(p, n, "yes please", "no thanks, i've paid that boy enough already");
                                        if (menu == 0) {
                                            Functions.___npcTalk(p, n, "ok then, here you go");
                                            if (p.getInventory().countId(ItemId.COINS.id()) >= 100) {
                                                Functions.___playerTalk(p, n, "thanks");
                                                Functions.___message(p, "gertrude gives you another kitten");
                                                p.getInventory().remove(ItemId.COINS.id(), 100);
                                                p.getInventory().add(new Item(ItemId.KITTEN.id()));
                                            } else {
                                                Functions.___playerTalk(p, n, "oops, looks like i'm a bit short", "i'll have to come back later");
                                            }
                                        } else
                                            if (menu == 1) {
                                                // NOTHING
                                            }

                                    }
                                    break;
                            }
                        } else// shilop & wilough same dialogue

                            if ((n.getID() == NpcId.SHILOP.id()) || (n.getID() == NpcId.WILOUGH.id())) {
                                switch (p.getQuestStage(quest)) {
                                    case 0 :
                                        Functions.___playerTalk(p, n, "hello youngster");
                                        Functions.___npcTalk(p, n, "i don't talk to strange old people");
                                        break;
                                    case 1 :
                                        Functions.___playerTalk(p, n, "hello there, i've been looking for you");
                                        Functions.___npcTalk(p, n, "i didn't mean to take it!, i just forgot to pay");
                                        Functions.___playerTalk(p, n, "what?...i'm trying to help your mum find fluffs");
                                        Functions.___npcTalk(p, n, "ohh..., well, in that case i might be able to help", "fluffs followed me to my secret play area..", "i haven't seen him since");
                                        Functions.___playerTalk(p, n, "and where is this play area?");
                                        Functions.___npcTalk(p, n, "if i told you that, it wouldn't be a secret");
                                        int first = // do not send over
                                        Functions.___showMenu(p, n, false, "tell me sonny, or i will hurt you", "what will make you tell me?", "well never mind, fluffs' loss");
                                        if (first == 0) {
                                            Functions.___playerTalk(p, n, "tell me sonny, or i will hurt you");
                                            Functions.___npcTalk(p, n, "w..w..what? y..you wouldn't, a young lad like me", "i'd have you behind bars before nightfall");
                                            Functions.___message(p, "you decide it's best not to hurt the boy");
                                        } else
                                            if (first == 1) {
                                                Functions.___playerTalk(p, n, "what will make you tell me?");
                                                Functions.___npcTalk(p, n, "well...now you ask, i am a bit short on cash");
                                                Functions.___playerTalk(p, n, "how much?");
                                                Functions.___npcTalk(p, n, "100 coins should cover it");
                                                Functions.___playerTalk(p, n, "100 coins!, why should i pay you?");
                                                Functions.___npcTalk(p, n, "you shouldn't, but i won't help otherwise", "i never liked that cat any way, so what do you say?");
                                                int second = Functions.___showMenu(p, n, "i'm not paying you a penny", "ok then, i'll pay");
                                                if (second == 0) {
                                                    Functions.___npcTalk(p, n, "ok then, i find another way to make money");
                                                } else
                                                    if (second == 1) {
                                                        if (p.getInventory().countId(ItemId.COINS.id()) >= 100) {
                                                            Functions.___playerTalk(p, n, "there you go, now where did you see fluffs?");
                                                            Functions.___npcTalk(p, n, "i play at an abandoned lumber mill to the north..", "just beyond the jolly boar inn...", "i saw fluffs running around in there");
                                                            Functions.___playerTalk(p, n, "anything else?");
                                                            Functions.___npcTalk(p, n, "well, you'll have to find a broken fence to get in", "i'm sure you can manage that");
                                                            Functions.___message(p, "you give the lad 100 coins");
                                                            p.getInventory().remove(ItemId.COINS.id(), 100);
                                                            p.updateQuestStage(getQuestId(), 2);
                                                        } else {
                                                            Functions.___playerTalk(p, n, "but i'll have to get some money first");
                                                            Functions.___npcTalk(p, n, "i'll be waiting");
                                                        }
                                                    }

                                            } else
                                                if (first == 2) {
                                                    Functions.___playerTalk(p, n, "well, never mind, fluffs' loss");
                                                    Functions.___npcTalk(p, n, "i'm sure my mum will get over it");
                                                }


                                        break;
                                    case 2 :
                                    case 3 :
                                        Functions.___playerTalk(p, n, "where did you say you saw fluffs?");
                                        Functions.___npcTalk(p, n, "weren't you listerning?, i saw the flee bag...", "...in the old lumber mill just north east of here", "just walk past the jolly boar inn and you should find it");
                                        break;
                                    case -1 :
                                        Functions.___playerTalk(p, n, "hello again");
                                        Functions.___npcTalk(p, n, "you think you're tough do you?");
                                        Functions.___playerTalk(p, n, "pardon?");
                                        Functions.___npcTalk(p, n, "i can beat anyone up");
                                        Functions.___playerTalk(p, n, "really");
                                        Functions.___message(p, "the boy begins to jump around with his fists up", "you decide it's best not to kill him just yet");
                                        break;
                                }
                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
        return (obj.getID() == 199) && (obj.getY() == 438);
    }

    @Override
    public GameStateEvent onWallObjectAction(GameObject obj, Integer click, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == 199) && (obj.getY() == 438)) {
                        if ((p.getQuestStage(Quests.GERTRUDES_CAT) >= 2) || (p.getQuestStage(Quests.GERTRUDES_CAT) == (-1))) {
                            p.message("you find a crack in the fence");
                            p.message("you walk through");
                            if (p.getX() <= 50) {
                                p.teleport(51, 438, false);
                            } else {
                                p.teleport(50, 438, false);
                            }
                        } else {
                            p.message("you search the fence");
                            p.message("but can't see a way through");
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return (i.getID() == ItemId.GERTRUDES_CAT.id()) && (i.getY() == 2327);
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((i.getID() == ItemId.GERTRUDES_CAT.id()) && (i.getY() == 2327)) {
                        int damage = DataConversions.random(1, 3);
                        Functions.___message(p, "you attempt to pick up the cat");
                        p.message("but the cat scratches you");
                        p.damage(damage);
                        Functions.___playerTalk(p, null, "ouch");
                        if ((p.getQuestStage(Quests.GERTRUDES_CAT) >= 3) || (p.getQuestStage(Quests.GERTRUDES_CAT) == (-1))) {
                            return null;
                        }
                        if (p.getCache().hasKey("cat_sardine") && p.getCache().hasKey("cat_milk")) {
                            Functions.___message(p, "the cats seems afraid to leave", "she keeps meowing", "in the distance you hear kittens purring");
                        }
                        if (!p.getCache().hasKey("cat_milk")) {
                            p.message("the cats seems to be thirsty");
                        }
                        if (p.getCache().hasKey("cat_milk") && (!p.getCache().hasKey("cat_sardine"))) {
                            p.message("the cats seems to be hungry");
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnGroundItem(Item myItem, GroundItem item, Player player) {
        return (((myItem.getID() == ItemId.MILK.id()) || (myItem.getID() == ItemId.SEASONED_SARDINE.id())) || (myItem.getID() == ItemId.KITTENS.id())) && (item.getID() == ItemId.GERTRUDES_CAT.id());
    }

    @Override
    public GameStateEvent onInvUseOnGroundItem(Item myItem, GroundItem item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getQuestStage(getQuestId()) != 2) {
                        if ((myItem.getID() == ItemId.MILK.id()) && (item.getID() == ItemId.GERTRUDES_CAT.id())) {
                            p.message("the cat doesn't seem to be thirsty");
                        } else
                            if ((myItem.getID() == ItemId.SEASONED_SARDINE.id()) && (item.getID() == ItemId.GERTRUDES_CAT.id())) {
                                p.message("the cat doesn't seem to be hungry");
                            } else
                                if ((myItem.getID() == ItemId.KITTENS.id()) && (item.getID() == ItemId.GERTRUDES_CAT.id())) {
                                    p.message("the cat doesn't seem to be lonely");
                                }


                        return null;
                    }
                    if ((myItem.getID() == ItemId.MILK.id()) && (item.getID() == ItemId.GERTRUDES_CAT.id())) {
                        Functions.___message(p, "you give the cat some milk", "she really enjoys it", "but she now seems to be hungry");
                        p.getCache().store("cat_milk", true);
                        p.getInventory().remove(ItemId.MILK.id(), 1);
                    } else
                        if ((myItem.getID() == ItemId.SEASONED_SARDINE.id()) && (item.getID() == ItemId.GERTRUDES_CAT.id())) {
                            if (p.getCache().hasKey("cat_milk")) {
                                Functions.___message(p, "you give the cat the sardine", "the cat gobbles it up", "she still seems scared of leaving");
                                p.getCache().store("cat_sardine", true);
                                p.getInventory().remove(ItemId.SEASONED_SARDINE.id(), 1);
                            }
                        } else
                            if ((myItem.getID() == ItemId.KITTENS.id()) && (item.getID() == ItemId.GERTRUDES_CAT.id())) {
                                Functions.___message(p, "you place the kittens by their mother", "she purrs at you appreciatively", "and then runs off home with her kittens");
                                Functions.removeItem(p, ItemId.KITTENS.id(), 1);
                                p.updateQuestStage(getQuestId(), 3);
                                p.getCache().remove("cat_milk");
                                p.getCache().remove("cat_sardine");
                                p.getWorld().unregisterItem(item);
                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
        return Functions.compareItemsIds(item1, item2, ItemId.RAW_SARDINE.id(), ItemId.DOOGLE_LEAVES.id());
    }

    @Override
    public GameStateEvent onInvUseOnItem(Player p, Item item1, Item item2) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.compareItemsIds(item1, item2, ItemId.RAW_SARDINE.id(), ItemId.DOOGLE_LEAVES.id())) {
                        Functions.___message(p, "you rub the doogle leaves over the sardine");
                        p.getInventory().remove(ItemId.DOOGLE_LEAVES.id(), 1);
                        p.getInventory().replace(ItemId.RAW_SARDINE.id(), ItemId.SEASONED_SARDINE.id());
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return ((obj.getID() == 1039) || (obj.getID() == 1041)) || (obj.getID() == 1040);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == 1039) {
                        Functions.___message(p, "you search the crate...", "...but find nothing...");
                        if (((Functions.hasItem(p, ItemId.KITTENS.id()) || (!p.getCache().hasKey("cat_sardine"))) || (p.getQuestStage(getQuestId()) >= 3)) || (p.getQuestStage(getQuestId()) == (-1))) {
                            // nothing
                        } else {
                            Functions.___message(p, "...you hear a cat's purring close by");
                        }
                    } else
                        if (obj.getID() == 1041) {
                            Functions.___message(p, "you search the barrel...", "...but find nothing...");
                            if (((Functions.hasItem(p, ItemId.KITTENS.id()) || (!p.getCache().hasKey("cat_sardine"))) || (p.getQuestStage(getQuestId()) >= 3)) || (p.getQuestStage(getQuestId()) == (-1))) {
                                // nothing
                            } else {
                                Functions.___message(p, "...you hear a cat's purring close by");
                            }
                        } else
                            if (obj.getID() == 1040) {
                                Functions.___message(p, "you search the crate...");
                                if (((Functions.hasItem(p, ItemId.KITTENS.id()) || (!p.getCache().hasKey("cat_sardine"))) || (p.getQuestStage(getQuestId()) >= 3)) || (p.getQuestStage(getQuestId()) == (-1))) {
                                    Functions.___message(p, "you find nothing...");
                                } else {
                                    Functions.___message(p, "...and find two kittens");
                                    Functions.addItem(p, ItemId.KITTENS.id(), 1);
                                }
                            }


                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockDrop(Player p, Item i) {
        return i.getID() == ItemId.KITTENS.id();
    }

    @Override
    public GameStateEvent onDrop(Player p, Item i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (i.getID() == ItemId.KITTENS.id()) {
                        Functions.___message(p, "you drop the kittens", "they run back to the crate");
                        Functions.removeItem(p, ItemId.KITTENS.id(), 1);
                    }
                    return null;
                });
            }
        };
    }
}

