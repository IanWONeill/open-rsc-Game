package com.openrsc.server.plugins.npcs.portsarim;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import java.util.concurrent.Callable;


public class Bartender implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.BARTENDER_PORTSARIM.id();
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Menu defaultMenu = new Menu();
                    defaultMenu.addOption(new Option("Could i buy a beer please?") {
                        @Override
                        public void action() {
                            Functions.___npcTalk(p, n, "Sure that will be 2 gold coins please");
                            if (Functions.hasItem(p, ItemId.COINS.id(), 2)) {
                                Functions.___playerTalk(p, n, "Ok here you go thanks");
                                p.getInventory().remove(ItemId.COINS.id(), 2);
                                p.message("you buy a pint of beer");
                                Functions.addItem(p, ItemId.BEER.id(), 1);
                            } else {
                                p.message("You dont have enough coins for the beer");
                            }
                        }
                    });
                    if (p.getQuestStage(Quests.GOBLIN_DIPLOMACY) == 0) {
                        defaultMenu.addOption(new Option("Not very busy in here today is it?") {
                            @Override
                            public void action() {
                                Functions.___npcTalk(p, n, "No it was earlier", "There was a guy in here saying the goblins up by the mountain are arguing again", "Of all things about the colour of their armour", "Knowing the goblins, it could easily turn into a full blown war", "Which wouldn't be good", "Goblin wars make such a mess of the countryside");
                                Functions.___playerTalk(p, n, "Well if I have time I'll see if I can go and knock some sense into them");
                                p.updateQuestStage(Quests.GOBLIN_DIPLOMACY, 1);// remember

                                // quest
                                // starts
                                // here.
                            }
                        });
                    } else
                        if ((p.getQuestStage(Quests.GOBLIN_DIPLOMACY) >= 1) || (p.getQuestStage(Quests.GOBLIN_DIPLOMACY) == (-1))) {
                            // TODO
                            defaultMenu.addOption(new Option("Have you heard any more rumours in here?") {
                                @Override
                                public void action() {
                                    Functions.___npcTalk(p, n, "No it hasn't been very busy lately");
                                }
                            });
                        }

                    if (p.getCache().hasKey("barcrawl") && (!p.getCache().hasKey("barsix"))) {
                        defaultMenu.addOption(new Option("I'm doing Alfred Grimhand's barcrawl") {
                            @Override
                            public void action() {
                                Functions.___npcTalk(p, n, "Are you sure you look a bit skinny for that");
                                Functions.___playerTalk(p, n, "Just give me whatever drink I need to drink here");
                                Functions.___npcTalk(p, n, "Ok one black skull ale coming up, 8 coins please");
                                if (Functions.hasItem(p, ItemId.COINS.id(), 8)) {
                                    p.getInventory().remove(ItemId.COINS.id(), 8);
                                    Functions.___message(p, "You buy a black skull ale", "You drink your black skull ale", "Your vision blurs", "The bartender signs your card");
                                    p.getCache().store("barsix", true);
                                    Functions.___playerTalk(p, n, "hiccup", "hiccup");
                                } else {
                                    Functions.___playerTalk(p, n, "I don't have 8 coins with me");
                                }
                            }
                        });
                    }
                    defaultMenu.showMenu(p);
                    return null;
                });
            }
        };
    }
}

