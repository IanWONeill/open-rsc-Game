package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;


import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.handlers.SpellHandler;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageObjectListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageObjectExecutiveListener;
import java.util.concurrent.Callable;


public class LegendsQuestDarkMetalGate implements ObjectActionListener , PlayerMageObjectListener , ObjectActionExecutiveListener , PlayerMageObjectExecutiveListener {
    public static final int DARK_METAL_GATE = 1165;

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == LegendsQuestDarkMetalGate.DARK_METAL_GATE;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == LegendsQuestDarkMetalGate.DARK_METAL_GATE) {
                        if (command.equalsIgnoreCase("open")) {
                            if (p.getY() <= 3715) {
                                Functions.___message(p, 1300, "You open the gates and walk through..");
                                p.teleport(474, 3720);
                                p.message("You magically appear in another area of the cave system.");
                                return null;
                            }
                            Functions.___message(p, 1300, "This gate is fused with rock, it doesn't seem possible to open it.", "But it does look slightly strange in some way.");
                        } else
                            if (command.equalsIgnoreCase("search")) {
                                Functions.___message(p, 3200, "It just looks like a normal gate...");
                                Functions.___message(p, 650, "At first...");
                                Functions.___message(p, 1300, "And then you notice that some of the bars of metal make up letters.", "After some time you manage to make sense of it...", "Would you like to read it?");
                                int menu = Functions.___showMenu(p, "Yes, I'll read it.", "No, I don't want to read that.", "Search further...");
                                if (menu == 0) {
                                    Functions.___message(p, 1300, "You attempt to read the message in the gate...");
                                    ActionSender.sendBox(p, "Gates of metal will not be kind, % %To those who care not for the way of mind. % %To all men of learning and supernatural powers, % %With book and rune spend the long dark hours. % %If passage further you would endure, % %Give me a taste of your power so pure. % %", true);
                                } else
                                    if (menu == 1) {
                                        p.message("You decide not to read the message.");
                                    } else
                                        if (menu == 2) {
                                            Functions.___message(p, 1300, "You scour the gate for any more clues...", "Something etched into the wall nearby catches your eye...", "It looks like a picture of four pillars or constructions.", "Over the first pillar is a picture of a cloud...", "Over the second pillar are some etched flickering flames...", "Over the third pillar is the carved image of a dew drop or a tear...", "Over the fourth pillar is the likeness of a ploughed field...", "All of these images are contained within a sphere.");
                                            Functions.___playerTalk(p, null, "Hmmm, I wonder what they could mean?");
                                        }


                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public GameStateEvent onPlayerMageObject(Player p, GameObject obj, SpellDef spell) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == LegendsQuestDarkMetalGate.DARK_METAL_GATE) {
                        switch (spell.getName()) {
                            case "Charge Fire Orb" :
                            case "Charge air Orb" :
                            case "Charge Water Orb" :
                            case "Charge earth Orb" :
                                if (!SpellHandler.checkAndRemoveRunes(p, spell)) {
                                    return null;
                                }
                                Functions.___message(p, 1300, "The orb shatters with the power of the magic.", "The spell works and the gates open.");
                                p.teleport(474, 3714);
                                Functions.___message(p, 5000, "You magically appear in a different part of the cave system.");
                                Functions.___message(p, 1300, "It seems that the gate was a test of magical ability.", "As soon as you enter this room, you are filled with dread.", "In the centre of the room is a large gaping hole.", "It goes down a long way...");
                                break;
                            default :
                                p.message("Nothing interesting happens");
                                break;
                        }
                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerMageObject(Player player, GameObject obj, SpellDef spell) {
        return obj.getID() == LegendsQuestDarkMetalGate.DARK_METAL_GATE;
    }
}

