package com.openrsc.server.plugins.npcs.tutorial;


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


public class MagicInstructor implements TalkToNpcListener , TalkToNpcExecutiveListener {
    /**
     *
     *
     * @author Davve
    Tutorial island magic instructor
     */
    @Override
    public GameStateEvent onTalkToNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") == 70)) {
                        Functions.___npcTalk(p, n, "there's good magic potential in this one", "Yes definitely something I can work with");
                        int menu = Functions.___showMenu(p, n, "Hmm are you talking about me?", "teach me some magic");
                        if (menu == 0) {
                            Functions.___npcTalk(p, n, "Yes that is the one of which I speak");
                            optionDialogue(p, n);
                        } else
                            if (menu == 1) {
                                Functions.___npcTalk(p, n, "Teacher, yes I am one of them");
                                optionDialogue(p, n);
                            }

                    } else
                        if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") == 75)) {
                            Functions.___playerTalk(p, n, "I don't have the runes to cast wind strike");
                            Functions.___npcTalk(p, n, "How do you expect to do magic without runes?", "Ok I shall have to provide you with runes");
                            p.message("The instructor gives you some runes");
                            Functions.addItem(p, ItemId.AIR_RUNE.id(), 12);
                            Functions.addItem(p, ItemId.MIND_RUNE.id(), 8);
                            Functions.addItem(p, ItemId.WATER_RUNE.id(), 3);
                            Functions.addItem(p, ItemId.EARTH_RUNE.id(), 2);
                            Functions.addItem(p, ItemId.BODY_RUNE.id(), 1);
                            Functions.___npcTalk(p, n, "Ok look at your spell list now", "You will see you have the runes for the spell", "And it shows up yellow in your list");
                            p.getCache().set("tutorial", 76);
                        } else
                            if (p.getCache().hasKey("tutorial") && (p.getCache().getInt("tutorial") == 76)) {
                                Npc chicken = Functions.getNearestNpc(p, NpcId.CHICKEN.id(), 10);
                                if (chicken != null) {
                                    Functions.___npcTalk(p, chicken, "cluck");
                                }
                                Functions.___npcTalk(p, n, "Aha a chicken", "An Ideal wind strike target", "ok click on the wind strike spell in your spell list", "then click on the chicken to chose it as a target");
                                p.getCache().set("tutorial", 77);
                            } else {
                                Functions.___npcTalk(p, n, "Well done", "As you get a higher magic level", "You will be able to cast all sorts of interesting spells", "Now go through the next door");
                                if (p.getCache().getInt("tutorial") < 80)
                                    p.getCache().set("tutorial", 80);

                            }


                    return null;
                });
            }
        };
    }

    private void optionDialogue(Player p, Npc n) {
        Functions.___npcTalk(p, n, "Ok move your mouse over the book icon on the menu bar", "this is your magic menu", "You will see at level 1 magic you can only cast wind strike", "move your mouse over the wind strike text", "If you look at the bottom of the magic window", "You will see more information about the spell", "runes required for the spell have two numbers over them", "The first number is how many runes you have", "The second is how many runes the spell requires", "Speak to me again when you have checked this");
        p.getCache().set("tutorial", 75);
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.MAGIC_INSTRUCTOR.id();
    }
}

