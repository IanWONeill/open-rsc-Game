package com.openrsc.server.plugins.npcs.ardougne.west;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.concurrent.Callable;


public class Civillians implements TalkToNpcListener , TalkToNpcExecutiveListener {
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    boolean hasCat = p.getInventory().hasItemId(ItemId.CAT.id());
                    boolean hasKitten = p.getInventory().hasItemId(ItemId.KITTEN.id());
                    boolean hasKardiasCat = p.getInventory().hasItemId(ItemId.KARDIA_CAT.id());
                    boolean hasGertrudesCat = p.getInventory().hasItemId(ItemId.GERTRUDES_CAT.id());
                    boolean hasFluffsKittens = p.getInventory().hasItemId(ItemId.KITTENS.id());
                    boolean hasAnyCat = (((hasCat || hasKitten) || hasKardiasCat) || hasGertrudesCat) || (hasFluffsKittens && p.getWorld().getServer().getConfig().WANT_SHOW_KITTENS_CIVILLIAN);
                    switch (NpcId.getById(n.getID())) {
                        case CIVILLIAN_APRON :
                            Functions.___playerTalk(p, n, "hi");
                            Functions.___npcTalk(p, n, "good day to you traveller");
                            Functions.___playerTalk(p, n, "what are you up to?");
                            Functions.___npcTalk(p, n, "chasing mice as usual...", "...it's all i seem to do");
                            Functions.___playerTalk(p, n, "you must waste alot of time");
                            Functions.___npcTalk(p, n, "yep, but what can you do?", "it's not like there's many cats around here");
                            if (!hasAnyCat) {
                                Functions.___playerTalk(p, n, "no you're right, you don't see many around");
                            } else {
                                if (hasCat)
                                    civilianWantCatDialogue(p, n);
                                else
                                    if (hasKitten)
                                        civilianShowKittenDialogue(p, n);
                                    else
                                        if (hasKardiasCat)
                                            civilianShowKardiasCatDialogue(p, n);
                                        else
                                            if (hasGertrudesCat)
                                                civilianShowGertrudesCatDialogue(p, n);
                                            else
                                                if (hasFluffsKittens && p.getWorld().getServer().getConfig().WANT_SHOW_KITTENS_CIVILLIAN)
                                                    civilianShowFluffsKittensDialogue(p, n);





                            }
                            break;
                        case CIVILLIAN_ATTACKABLE :
                            Functions.___playerTalk(p, n, "hello there");
                            Functions.___npcTalk(p, n, "oh hello, i'm sorry, i'm a bit worn out");
                            Functions.___playerTalk(p, n, "busy day?");
                            Functions.___npcTalk(p, n, "oh, it's those bleeding mice, they're everywhere", "what i really need is a cat, but they're hard to come by nowadays");
                            if (!hasAnyCat) {
                                Functions.___playerTalk(p, n, "no, you're right, you don't see many around");
                            } else {
                                if (hasCat)
                                    civilianWantCatDialogue(p, n);
                                else
                                    if (hasKitten)
                                        civilianShowKittenDialogue(p, n);
                                    else
                                        if (hasKardiasCat)
                                            civilianShowKardiasCatDialogue(p, n);
                                        else
                                            if (hasGertrudesCat)
                                                civilianShowGertrudesCatDialogue(p, n);
                                            else
                                                if (hasFluffsKittens && p.getWorld().getServer().getConfig().WANT_SHOW_KITTENS_CIVILLIAN)
                                                    civilianShowFluffsKittensDialogue(p, n);





                            }
                            break;
                        case CIVILLIAN_PICKPOCKET :
                            Functions.___playerTalk(p, n, "hello");
                            Functions.___npcTalk(p, n, "i'm a bit busy to talk, sorry");
                            Functions.___playerTalk(p, n, "what are you doing?");
                            Functions.___npcTalk(p, n, "i need to kill these blasted mice", "they're all over the place, i need a cat");
                            if (!hasAnyCat) {
                                Functions.___playerTalk(p, n, "no you're right, you don't see many around");
                            } else {
                                if (hasCat)
                                    civilianWantCatDialogue(p, n);
                                else
                                    if (hasKitten)
                                        civilianShowKittenDialogue(p, n);
                                    else
                                        if (hasKardiasCat)
                                            civilianShowKardiasCatDialogue(p, n);
                                        else
                                            if (hasGertrudesCat)
                                                civilianShowGertrudesCatDialogue(p, n);
                                            else
                                                if (hasFluffsKittens && p.getWorld().getServer().getConfig().WANT_SHOW_KITTENS_CIVILLIAN)
                                                    civilianShowFluffsKittensDialogue(p, n);





                            }
                            break;
                        default :
                            break;
                    }
                    return null;
                });
            }
        };
    }

    private void civilianWantCatDialogue(Player p, Npc n) {
        int menu = Functions.___showMenu(p, n, "i have a cat that i could sell", "nope, they're not easy to get hold of");
        if (menu == 0) {
            Functions.___npcTalk(p, n, "you don't say, can i see it");
            p.message("you reveal the cat in your satchel");
            Functions.___npcTalk(p, n, "hmmm, not bad, not bad at all", "looks like it's a lively one");
            Functions.___playerTalk(p, n, "erm ...kind of!");
            Functions.___npcTalk(p, n, "i don't have much in the way of money...", "but i do have these...");
            p.message("the peasent shows you a sack of death runes");
            Functions.___npcTalk(p, n, "the dwarfs bring them from the mine for us", "tell you what, i'll give you 25 death runes for the cat");
            int sub_menu = Functions.___showMenu(p, n, "nope, i'm not parting for that", "ok then, you've got a deal");
            if (sub_menu == 0) {
                Functions.___npcTalk(p, n, "well, i'm not giving you anymore");
            } else
                if (sub_menu == 1) {
                    p.message("you hand over the cat");
                    Functions.removeItem(p, ItemId.CAT.id(), 1);
                    p.message("you are given 25 death runes");
                    Functions.addItem(p, ItemId.DEATH_RUNE.id(), 25);
                    Functions.___npcTalk(p, n, "great, thanks for that");
                    Functions.___playerTalk(p, n, "that's ok, take care");
                }

        } else
            if (menu == 1) {
                // nothing
            }

    }

    private void civilianShowKittenDialogue(Player p, Npc n) {
        int menu = Functions.___showMenu(p, n, "i have a kitten that i could sell", "nope, they're not easy to get hold of");
        if (menu == 0) {
            Functions.___npcTalk(p, n, "really, lets have a look");
            p.message("you reveal the kitten in your satchel");
            Functions.___npcTalk(p, n, "hah, that little thing won't catch any mice", "i need a fully grown cat");
        } else
            if (menu == 1) {
                // nothing
            }

    }

    private void civilianShowKardiasCatDialogue(Player p, Npc n) {
        Functions.___playerTalk(p, n, "i have a cat..look");
        Functions.___npcTalk(p, n, "hmmm..doesn't look like it's seen daylight in years", "that's not going to catch any mice");
    }

    // no known method to obtain gertrudes cat
    private void civilianShowGertrudesCatDialogue(Player p, Npc n) {
        Functions.___playerTalk(p, n, "i have a cat..look");
        Functions.___npcTalk(p, n, "hmmm..doesn't look like it belongs to you", "i cannot buy it");
    }

    // very likely did not trigger something, it does not appear to trigger dialogue in OSRS
    // and kardias cat is wikified to have dialogue in OSRS
    private void civilianShowFluffsKittensDialogue(Player p, Npc n) {
        Functions.___playerTalk(p, n, "i have some kittens..look");
        Functions.___npcTalk(p, n, "hmmm..doesn't look like they are happy", "better return them where they were");
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return Functions.inArray(n.getID(), NpcId.CIVILLIAN_APRON.id(), NpcId.CIVILLIAN_ATTACKABLE.id(), NpcId.CIVILLIAN_PICKPOCKET.id());
    }
}

