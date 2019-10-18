package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestNezikchened;
import com.openrsc.server.plugins.skills.Mining;
import com.openrsc.server.plugins.skills.Thieving;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import java.util.concurrent.Callable;


public class LegendsQuestGameObjects implements InvUseOnObjectListener , ObjectActionListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener {
    // Objects
    private static final int LEGENDS_CUPBOARD = 1149;

    private static final int GRAND_VIZIERS_DESK = 1177;

    private static final int TOTEM_POLE = 1169;

    private static final int ROCK = 1151;

    private static final int TALL_REEDS = 1163;

    private static final int SHALLOW_WATER = 582;

    private static final int CRATE = 1144;

    private static final int CRUDE_BED = 1162;

    private static final int CRUDE_DESK = 1032;

    private static final int TABLE = 1161;

    private static final int BOOKCASE = 931;

    private static final int CAVE_ENTRANCE_LEAVE_DUNGEON = 1158;

    private static final int CAVE_ENTRANCE_FROM_BOULDERS = 1159;

    private static final int CAVE_ANCIENT_WOODEN_DOORS = 1160;

    private static final int HEAVY_METAL_GATE = 1033;

    private static final int HALF_BURIED_REMAINS = 1168;

    private static final int CARVED_ROCK = 1037;

    private static final int WOODEN_BEAM = 1156;

    private static final int ROPE_UP = 1167;

    private static final int RED_EYE_ROCK = 1148;

    private static final int ANCIENT_LAVA_FURNACE = 1146;

    private static final int CAVERNOUS_OPENING = 1145;

    private static final int ECHNED_ZEKIN_ROCK = 1116;

    private static final int FERTILE_EARTH = 1113;

    private static final int[] SMASH_BOULDERS = new int[]{ 1117, 1184, 1185 };

    private static final int BABY_YOMMI_TREE = 1112;

    private static final int YOMMI_TREE = 1107;

    private static final int DEAD_YOMMI_TREE = 1141;

    private static final int GROWN_YOMMI_TREE = 1108;

    private static final int ROTTEN_YOMMI_TREE = 1172;

    private static final int CHOPPED_YOMMI_TREE = 1109;

    private static final int TRIMMED_YOMMI_TREE = 1110;

    private static final int CRAFTED_TOTEM_POLE = 1111;

    private final int[] REFILLABLE = new int[]{ 1188, 1266, 21, 140, 341, 465 };

    private final int[] REFILLED = new int[]{ 1189, 1267, 50, 141, 342, 464 };

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return (Functions.inArray(obj.getID(), LegendsQuestGameObjects.GRAND_VIZIERS_DESK, LegendsQuestGameObjects.LEGENDS_CUPBOARD, LegendsQuestGameObjects.TOTEM_POLE, LegendsQuestGameObjects.ROCK, LegendsQuestGameObjects.TALL_REEDS, LegendsQuestGameObjects.SHALLOW_WATER, LegendsQuestGameObjects.CAVE_ENTRANCE_LEAVE_DUNGEON, LegendsQuestGameObjects.CRATE, LegendsQuestGameObjects.TABLE, LegendsQuestGameObjects.BOOKCASE, LegendsQuestGameObjects.CAVE_ENTRANCE_FROM_BOULDERS, LegendsQuestGameObjects.CRUDE_DESK, LegendsQuestGameObjects.CAVE_ANCIENT_WOODEN_DOORS, LegendsQuestGameObjects.HEAVY_METAL_GATE, LegendsQuestGameObjects.HALF_BURIED_REMAINS, LegendsQuestGameObjects.CARVED_ROCK, LegendsQuestGameObjects.WOODEN_BEAM, LegendsQuestGameObjects.WOODEN_BEAM + 1, LegendsQuestGameObjects.ROPE_UP, LegendsQuestGameObjects.RED_EYE_ROCK, LegendsQuestGameObjects.ANCIENT_LAVA_FURNACE, LegendsQuestGameObjects.CAVERNOUS_OPENING, LegendsQuestGameObjects.ECHNED_ZEKIN_ROCK, LegendsQuestGameObjects.CRAFTED_TOTEM_POLE, LegendsQuestGameObjects.TOTEM_POLE + 1) || Functions.inArray(obj.getID(), LegendsQuestGameObjects.SMASH_BOULDERS)) || ((obj.getID() == LegendsQuestGameObjects.CRUDE_BED) && command.equalsIgnoreCase("search"));
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == LegendsQuestGameObjects.ECHNED_ZEKIN_ROCK) {
                        if (p.getQuestStage(Quests.LEGENDS_QUEST) == 8) {
                            Functions.___message(p, 1300, "The rock moves quite easily.");
                            p.message("And the spirit of Echned Zekin seems to have disapeared.");
                            Functions.replaceObjectDelayed(obj, 10000, LegendsQuestGameObjects.SHALLOW_WATER);
                            return null;
                        }
                        p.setBusy(true);
                        Npc echned = Functions.getNearestNpc(p, NpcId.ECHNED_ZEKIN.id(), 2);
                        if (echned == null) {
                            Functions.___message(p, 1300, "A thick, green mist seems to emanate from the water...", "It slowly congeals into the shape of a body...");
                            echned = Functions.spawnNpcWithRadius(p, NpcId.ECHNED_ZEKIN.id(), p.getX(), p.getY(), 0, 60000 * 3);
                            if (echned != null) {
                                p.setBusyTimer(5);
                                Functions.sleep(1300);
                                Functions.___message(p, echned, 1300, "Which slowly floats towards you.");
                                echned.initializeTalkScript(p);
                            }
                            return null;
                        }
                        if (echned != null) {
                            echned.initializeTalkScript(p);
                        }
                        p.setBusy(false);
                    } else
                        if (obj.getID() == LegendsQuestGameObjects.CAVERNOUS_OPENING) {
                            if (command.equalsIgnoreCase("enter")) {
                                if (p.getY() >= 3733) {
                                    p.message("You enter the dark cave...");
                                    p.teleport(395, 3725);
                                } else {
                                    if (p.getCache().hasKey("cavernous_opening")) {
                                        Functions.___message(p, 1300, "You walk carefully into the darkness of the cavern..");
                                        p.teleport(395, 3733);
                                    } else {
                                        Functions.___message(p, 1300, "You walk into an invisible barrier...");
                                        Functions.___message(p, 600, "Somekind of magical force will not allow you to pass into the cavern.");
                                    }
                                }
                            } else
                                if (command.equalsIgnoreCase("search")) {
                                    if (p.getCache().hasKey("cavernous_opening")) {
                                        Functions.___message(p, 1300, "You can see a glowing crystal shape in the wall.", "It looks like the Crystal is magical, ", "it allows access to the cavern.");
                                    } else {
                                        Functions.___message(p, 1300, "You see a heart shaped depression in the wall next to the cavern.", "And a message reads...", "@gre@All ye who stand 'ere the dragons teeth,");
                                        Functions.___message(p, 600, "@gre@Place your full true heart and proceed...");
                                    }
                                }

                        } else
                            if (obj.getID() == LegendsQuestGameObjects.ANCIENT_LAVA_FURNACE) {
                                if (command.equalsIgnoreCase("look")) {
                                    Functions.___message(p, 600, "This is an ancient looking furnace.");
                                } else
                                    if (command.equalsIgnoreCase("search")) {
                                        Functions.___message(p, 1300, "You search the lava furnace.", "You find a small compartment that you may be able to use.", "Strangely, it looks as if it is designed for a specific purpose...");
                                        Functions.___message(p, 600, "to fuse things together at very high temperatures...");
                                    }

                            } else
                                if (obj.getID() == LegendsQuestGameObjects.RED_EYE_ROCK) {
                                    Functions.___message(p, 600, "These rocks look somehow manufactured..");
                                } else
                                    if (obj.getID() == LegendsQuestGameObjects.ROPE_UP) {
                                        p.message("You climb the rope back out again.");
                                        p.teleport(471, 3707);
                                    } else
                                        if (obj.getID() == (LegendsQuestGameObjects.WOODEN_BEAM + 1)) {
                                            if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 9) || (p.getQuestStage(Quests.LEGENDS_QUEST) == (-1))) {
                                                Functions.___message(p, 1300, "The rope snaps as you're about to climb down it.", "Perhaps you need a new rope.");
                                                return null;
                                            }
                                            Functions.___message(p, 1300, "This rope climb looks pretty dangerous,", "Are you sure you want to go down?");
                                            int menu = Functions.___showMenu(p, "Yes,I'll go down the rope...", "No way do I want to go down there.");
                                            if (menu == 0) {
                                                Functions.___message(p, 1300, "You prepare to climb down the rope...");
                                                Functions.___playerTalk(p, null, "! Gulp !");
                                                Functions.sleep(1100);
                                                if (!p.getCache().hasKey("gujuo_potion")) {
                                                    Functions.___message(p, 1300, "...but a terrible fear grips you...");
                                                    p.message("And you can go no further.");
                                                } else {
                                                    int rnd = DataConversions.random(0, 4);
                                                    if (rnd == 0) {
                                                        Functions.___message(p, 1300, "but fear stabs at your heart...", "and you lose concentration,");
                                                        p.damage(DataConversions.random(10, 15));
                                                    } else {
                                                        Functions.___message(p, 1300, "And although fear stabs at your heart...", "You shimmey down the rope...");
                                                    }
                                                    p.teleport(426, 3707);
                                                }
                                            } else
                                                if (menu == 1) {
                                                    p.message("You decide not to go down the rope.");
                                                }

                                        } else
                                            if (obj.getID() == LegendsQuestGameObjects.WOODEN_BEAM) {
                                                p.message("You search the wooden beam...");
                                                if (p.getCache().hasKey("legends_wooden_beam")) {
                                                    p.message("You search the wooden beam and find the rope you attached.");
                                                    Functions.replaceObjectDelayed(obj, 5000, LegendsQuestGameObjects.WOODEN_BEAM + 1);
                                                } else {
                                                    Functions.___message(p, 1300, "You see nothing special about this...");
                                                    p.message("Perhaps if you had a rope, it might be more functional.");
                                                }
                                            } else
                                                if (obj.getID() == LegendsQuestGameObjects.CARVED_ROCK) {
                                                    Functions.___message(p, 1300, "You see a delicate inscription on the rock, it says,");
                                                    Functions.___message(p, 1900, "@gre@'Once there were crystals to make the pool shine,'");
                                                    Functions.___message(p, 0, "@gre@'Ordered in stature to retrieve what's mine.'");
                                                    String gem = "";
                                                    boolean attached = false;
                                                    // opal
                                                    if ((obj.getX() == 471) && (obj.getY() == 3722)) {
                                                        gem = "Opal";
                                                        attached = p.getCache().hasKey("legends_attach_1");
                                                    } else// emerald

                                                        if ((obj.getX() == 474) && (obj.getY() == 3730)) {
                                                            gem = "Emerald";
                                                            attached = p.getCache().hasKey("legends_attach_2");
                                                        } else// ruby

                                                            if ((obj.getX() == 471) && (obj.getY() == 3734)) {
                                                                gem = "Ruby";
                                                                attached = p.getCache().hasKey("legends_attach_3");
                                                            } else// diamond

                                                                if ((obj.getX() == 466) && (obj.getY() == 3739)) {
                                                                    gem = "Diamond";
                                                                    attached = p.getCache().hasKey("legends_attach_4");
                                                                } else// sapphire

                                                                    if ((obj.getX() == 460) && (obj.getY() == 3737)) {
                                                                        gem = "Sapphire";
                                                                        attached = p.getCache().hasKey("legends_attach_5");
                                                                    } else// red topaz

                                                                        if ((obj.getX() == 464) && (obj.getY() == 3730)) {
                                                                            gem = "Topaz";
                                                                            attached = p.getCache().hasKey("legends_attach_6");
                                                                        } else// jade

                                                                            if ((obj.getX() == 469) && (obj.getY() == 3728)) {
                                                                                gem = "Jade";
                                                                                attached = p.getCache().hasKey("legends_attach_7");
                                                                            }






                                                    if ((!gem.equals("")) && attached) {
                                                        Functions.___message(p, 1300, ("A barely visible " + gem) + " becomes clear again, spinning above the rock.", "And then fades again...");
                                                    }
                                                } else
                                                    if (obj.getID() == LegendsQuestGameObjects.HALF_BURIED_REMAINS) {
                                                        Functions.___message(p, "It looks as if some poor unfortunate soul died here.");
                                                    } else
                                                        if (obj.getID() == LegendsQuestGameObjects.HEAVY_METAL_GATE) {
                                                            if (command.equalsIgnoreCase("look")) {
                                                                Functions.___message(p, 1300, "This huge metal gate bars the way further...", "There is an intense and unpleasant feeling from this place.");
                                                                p.message("And you can see why, shadowy flying creatures seem to hover in the still dark air.");
                                                            } else
                                                                if (command.equalsIgnoreCase("push")) {
                                                                    Functions.___message(p, 1300, "You push the gates...they're very stiff...", "They won't budge with a normal push.", "Do you want to try to force them open with brute strength?");
                                                                    int menu = Functions.___showMenu(p, "Yes, I'm very strong, I'll force them open.", "No, I'm having second thoughts.");
                                                                    if (menu == 0) {
                                                                        if (Functions.getCurrentLevel(p, Skills.STRENGTH) < 50) {
                                                                            p.message("You need a Strength of at least 50 to affect these gates.");
                                                                            return null;
                                                                        }
                                                                        Functions.___message(p, 1300, "You ripple your muscles...preparing too exert yourself...");
                                                                        Functions.___playerTalk(p, null, "Hup!");
                                                                        Functions.___message(p, 1300, "You brace yourself against the doors...");
                                                                        Functions.___playerTalk(p, null, "Urghhhhh!");
                                                                        Functions.___message(p, 1300, "You start to force against the gate..");
                                                                        Functions.___playerTalk(p, null, "Arghhhhhhh!");
                                                                        Functions.___message(p, 1300, "You push and push,");
                                                                        Functions.___playerTalk(p, null, "Shhhhhhhshshehshsh");
                                                                        if (Formulae.failCalculation(p, Skills.STRENGTH, 50)) {
                                                                            Functions.___message(p, 1300, "You just manage to force the gates open slightly, ", "just enough to force yourself through.");
                                                                            Functions.replaceObjectDelayed(obj, 2000, 181);
                                                                            if (p.getY() <= 3717) {
                                                                                p.teleport(441, 3719);
                                                                            } else {
                                                                                p.teleport(441, 3717);
                                                                            }
                                                                        } else {
                                                                            Functions.___message(p, 1300, "but run out of steam before you're able to force the gates open.");
                                                                            p.message("The effort of trying to force the gates reduces your strength temporarily");
                                                                            p.getSkills().decrementLevel(Skills.STRENGTH);
                                                                        }
                                                                    } else
                                                                        if (menu == 1) {
                                                                            p.message("You decide against forcing the gates.");
                                                                        }

                                                                }

                                                        } else
                                                            if (Functions.inArray(obj.getID(), LegendsQuestGameObjects.SMASH_BOULDERS)) {
                                                                if (Functions.hasItem(p, Mining.getAxe(p))) {
                                                                    if (Functions.getCurrentLevel(p, Skills.MINING) < 52) {
                                                                        p.message("You need a mining ability of at least 52 to affect these boulders.");
                                                                        return null;
                                                                    }
                                                                    if (Formulae.failCalculation(p, Skills.MINING, 50)) {
                                                                        Functions.___message(p, 1300, "You take a good swing at the rock with your pick...");
                                                                        Functions.replaceObjectDelayed(obj, 2000, 1143);
                                                                        if ((obj.getID() == LegendsQuestGameObjects.SMASH_BOULDERS[0]) && (p.getY() <= 3704)) {
                                                                            p.teleport(441, 3707);
                                                                        } else
                                                                            if ((obj.getID() == LegendsQuestGameObjects.SMASH_BOULDERS[0]) && (p.getY() >= 3707)) {
                                                                                p.teleport(442, 3704);
                                                                            } else
                                                                                if ((obj.getID() == LegendsQuestGameObjects.SMASH_BOULDERS[1]) && (p.getY() <= 3708)) {
                                                                                    p.teleport(441, 3711);
                                                                                } else
                                                                                    if ((obj.getID() == LegendsQuestGameObjects.SMASH_BOULDERS[1]) && (p.getY() >= 3711)) {
                                                                                        p.teleport(441, 3708);
                                                                                    } else
                                                                                        if ((obj.getID() == LegendsQuestGameObjects.SMASH_BOULDERS[2]) && (p.getY() <= 3712)) {
                                                                                            p.teleport(441, 3715);
                                                                                        } else
                                                                                            if ((obj.getID() == LegendsQuestGameObjects.SMASH_BOULDERS[2]) && (p.getY() >= 3715)) {
                                                                                                p.teleport(441, 3712);
                                                                                            }





                                                                        Functions.___message(p, 1900, "...and smash it into smaller pieces.");
                                                                        p.message("Another large rock falls down replacing the one that you smashed.");
                                                                    } else {
                                                                        p.message("You fail to make a mark on the rocks.");
                                                                        p.message("You miss hit the rock and the vibration shakes your bones.");
                                                                        p.message("Your mining ability suffers...");
                                                                        p.getSkills().decrementLevel(Skills.MINING);
                                                                    }
                                                                } else {
                                                                    Functions.___message(p, "You'll need a pickaxe to smash your way through these boulders.");
                                                                }
                                                            } else
                                                                if (obj.getID() == LegendsQuestGameObjects.CAVE_ANCIENT_WOODEN_DOORS) {
                                                                    if (command.equalsIgnoreCase("open")) {
                                                                        if (p.getY() >= 3703) {
                                                                            Functions.___message(p, 1300, "You push the doors open and walk through.");
                                                                            Functions.replaceObjectDelayed(obj, 2000, 497);
                                                                            p.teleport(442, 3701);
                                                                            Functions.sleep(2000);
                                                                            p.message("The doors make a satisfying 'CLICK' sound as they close.");
                                                                        } else {
                                                                            Functions.___message(p, 1300, "You push on the doors...they're really shut..", "It looks as if they have a huge lock on it...");
                                                                            p.message("Although ancient, it looks very sophisticated...");
                                                                        }
                                                                    } else
                                                                        if (command.equalsIgnoreCase("pick lock")) {
                                                                            if (p.getY() >= 3703) {
                                                                                Functions.___message(p, 1300, "You see a lever which you pull on to open the door.");
                                                                                Functions.replaceObjectDelayed(obj, 2000, 497);
                                                                                p.teleport(442, 3701);
                                                                                Functions.___message(p, 1300, "You walk through the door.");
                                                                                p.message("The doors make a satisfying 'CLICK' sound as they close.");
                                                                            } else {
                                                                                if (Functions.getCurrentLevel(p, Skills.THIEVING) < 50) {
                                                                                    p.message("You need a thieving level of at least 50 to attempt this.");
                                                                                    return null;
                                                                                }
                                                                                if (Functions.hasItem(p, 714)) {
                                                                                    Functions.___message(p, 1300, "You attempt to pick the lock..");
                                                                                    p.message("It looks very sophisticated ...");
                                                                                    Functions.___playerTalk(p, null, "Hmmm, interesting...");
                                                                                    Functions.sleep(1300);
                                                                                    p.message("You carefully insert your lockpick into the lock.");
                                                                                    Functions.___playerTalk(p, null, "This will be a challenge...");
                                                                                    Functions.sleep(1300);
                                                                                    p.message("You feel for the pins and levers in the mechanism.");
                                                                                    Functions.___playerTalk(p, null, "Easy does it....");
                                                                                    Functions.sleep(1300);
                                                                                    if (Thieving.succeedPickLockThieving(p, 50)) {
                                                                                        Functions.___message(p, 1300, "@gre@'CLICK'");
                                                                                        Functions.___playerTalk(p, null, "Easy as pie...");
                                                                                        Functions.sleep(1300);
                                                                                        Functions.___message(p, 1300, "You tumble the lock mechanism and the door opens easily.");
                                                                                        p.incExp(Skills.THIEVING, 100, true);
                                                                                        Functions.replaceObjectDelayed(obj, 2000, 497);
                                                                                        p.teleport(441, 3703);
                                                                                    } else {
                                                                                        p.message("...but you don't manage to pick the lock.");
                                                                                    }
                                                                                } else {
                                                                                    Functions.___message(p, 1300, "The mechanism for this lock looks very sophisticated...");
                                                                                    p.message("you're unable to affect the lock without the proper tool..");
                                                                                }
                                                                            }
                                                                        }

                                                                } else
                                                                    if (obj.getID() == LegendsQuestGameObjects.CRUDE_DESK) {
                                                                        if (Functions.hasItem(p, ItemId.SHAMANS_TOME.id())) {
                                                                            Functions.___message(p, 1300, "You search the desk ...");
                                                                            p.message("...but find nothing.");
                                                                        } else {
                                                                            Functions.___message(p, 2500, "You search the desk ...");
                                                                            Functions.addItem(p, ItemId.SHAMANS_TOME.id(), 1);
                                                                            p.message("You find a book...it looks like an ancient tome...");
                                                                        }
                                                                    } else
                                                                        if (obj.getID() == LegendsQuestGameObjects.BOOKCASE) {
                                                                            Functions.___message(p, 1300, "You search the bookcase...", "And find a large gaping hole at the back.");
                                                                            p.message("Would you like to climb through the hole?");
                                                                            int menu = Functions.___showMenu(p, "Yes, I'll climb through the hole.", "No, I'll stay here.");
                                                                            if (menu == 0) {
                                                                                Functions.___message(p, 1300, "You climb through the hole in the wall..", "It's very narrow and you have to contort your body a lot.", "After some time, you  manage to wriggle out of a small cavern...");
                                                                                p.teleport(444, 3699);
                                                                            } else
                                                                                if (menu == 1) {
                                                                                    p.message("You decide to stay where you are.");
                                                                                }

                                                                        } else
                                                                            if (obj.getID() == LegendsQuestGameObjects.TABLE) {
                                                                                p.message("You start searching the table...");
                                                                                if (Functions.hasItem(p, ItemId.SCRAWLED_NOTES.id())) {
                                                                                    p.message("You cannot find anything else in here.");
                                                                                } else {
                                                                                    Functions.sleep(1300);
                                                                                    Functions.addItem(p, ItemId.SCRAWLED_NOTES.id(), 1);
                                                                                    Functions.___message(p, 1300, "You find a scrap of paper with nonesense written on it.");
                                                                                }
                                                                            } else
                                                                                if ((obj.getID() == LegendsQuestGameObjects.CRUDE_BED) && command.equalsIgnoreCase("search")) {
                                                                                    p.message("You search the flea infested rags..");
                                                                                    if (Functions.hasItem(p, ItemId.SCATCHED_NOTES.id())) {
                                                                                        p.message("You cannot find anything else in here.");
                                                                                    } else {
                                                                                        Functions.sleep(1300);
                                                                                        Functions.addItem(p, ItemId.SCATCHED_NOTES.id(), 1);
                                                                                        Functions.___message(p, 1300, "You find a scrap of paper with spidery writing on it.");
                                                                                    }
                                                                                } else
                                                                                    if (obj.getID() == LegendsQuestGameObjects.CRATE) {
                                                                                        p.message("You search the crate.");
                                                                                        if (Functions.hasItem(p, ItemId.SCRIBBLED_NOTES.id())) {
                                                                                            p.message("You cannot find anything else in here.");
                                                                                        } else {
                                                                                            Functions.sleep(1300);
                                                                                            Functions.addItem(p, ItemId.SCRIBBLED_NOTES.id(), 1);
                                                                                            Functions.___message(p, 1300, "After some time you find a scrumpled up piece of paper.");
                                                                                            p.message("It looks like rubbish...");
                                                                                        }
                                                                                    } else
                                                                                        if (obj.getID() == LegendsQuestGameObjects.CAVE_ENTRANCE_FROM_BOULDERS) {
                                                                                            Functions.___message(p, 1300, "You see a small cave entrance.", "Would you like to climb into it?");
                                                                                            int menu = Functions.___showMenu(p, "Yes, I'll climb into it.", "No, I'll stay where I am.");
                                                                                            if (menu == 0) {
                                                                                                p.message("You clamber into the small cave...");
                                                                                                p.teleport(452, 3702);
                                                                                            } else
                                                                                                if (menu == 1) {
                                                                                                    p.message("You decide against climbing into the small, uncomfortable looking tunnel.");
                                                                                                }

                                                                                        } else
                                                                                            if (obj.getID() == LegendsQuestGameObjects.CAVE_ENTRANCE_LEAVE_DUNGEON) {
                                                                                                Functions.___message(p, 1300, "You crawl back out from the cavern...");
                                                                                                p.teleport(452, 874);
                                                                                            } else
                                                                                                if (obj.getID() == LegendsQuestGameObjects.SHALLOW_WATER) {
                                                                                                    if (((p.getQuestStage(Quests.LEGENDS_QUEST) == 8) && (p.getY() >= 3723)) && (p.getY() <= 3740)) {
                                                                                                        p.message("A magical looking pool.");
                                                                                                        return null;
                                                                                                    }
                                                                                                    if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 5) || (p.getQuestStage(Quests.LEGENDS_QUEST) == (-1))) {
                                                                                                        p.message("A disgusting sess pit of filth and stench...");
                                                                                                        return null;
                                                                                                    }
                                                                                                    Functions.___message(p, 0, "A bubbling brook with effervescent water...");
                                                                                                } else
                                                                                                    if (obj.getID() == LegendsQuestGameObjects.TALL_REEDS) {
                                                                                                        Functions.___message(p, 1300, "These tall reeds look nice and long, ");
                                                                                                        Functions.___message(p, 1300, "with a long tube for a stem.");
                                                                                                        Functions.___message(p, 0, "They reach all the way down to the water.");
                                                                                                    } else
                                                                                                        if (obj.getID() == LegendsQuestGameObjects.ROCK) {
                                                                                                            if ((p.getCache().hasKey("legends_cavern") || (p.getQuestStage(Quests.LEGENDS_QUEST) >= 2)) || (p.getQuestStage(Quests.LEGENDS_QUEST) == (-1))) {
                                                                                                                if (p.getQuestStage(Quests.LEGENDS_QUEST) == 1) {
                                                                                                                    Functions.___message(p, 1200, "You see nothing significant...", "At first....");
                                                                                                                }
                                                                                                                Functions.___message(p, 1200, "You see that there is a small crevice that you may be able to crawl though.?", "Would you like to try to crawl through, it looks quite an enclosed area.");
                                                                                                                int menu = Functions.___showMenu(p, "Yes, I'll crawl through, I'm very athletic.", "No, I'm pretty scared of enclosed areas.");
                                                                                                                if (menu == 0) {
                                                                                                                    if (Functions.getCurrentLevel(p, Skills.AGILITY) < 50) {
                                                                                                                        p.message("You need an agility of 50 to even attempt this.");
                                                                                                                        p.setBusy(false);
                                                                                                                        return null;
                                                                                                                    }
                                                                                                                    Functions.___message(p, 1300, "You try to crawl through...", "You contort your body to fit the crevice.");
                                                                                                                    if (Formulae.failCalculation(p, Skills.AGILITY, 50)) {
                                                                                                                        Functions.___message(p, 1300, "You adroitely squeeze serpent like into the crevice.", "You find a small narrow tunnel that goes for some distance.", "After some time, you find a small cave opening...and walk through.");
                                                                                                                        p.teleport(461, 3700);
                                                                                                                        if (p.getCache().hasKey("legends_cavern")) {
                                                                                                                            p.getCache().remove("legends_cavern");
                                                                                                                            p.updateQuestStage(Quests.LEGENDS_QUEST, 2);
                                                                                                                        }
                                                                                                                    } else {
                                                                                                                        Functions.___message(p, 3200, "You get cramped into a tiny space and start to suffocate.", "You wriggle and wriggle but you cannot get out..");
                                                                                                                        Functions.___message(p, 1300, "Eventually you manage to break free.", "But you scrape yourself very badly as your force your way out.", "And you're totally exhausted from the experience.");
                                                                                                                        p.damage(5);
                                                                                                                    }
                                                                                                                } else
                                                                                                                    if (menu == 1) {
                                                                                                                        Functions.___message(p, 1200, "You decide against forcing yourself into the tiny crevice..", "And realise that you have much better things to do..", "Like visit Inn's and mine ore...");
                                                                                                                    }

                                                                                                            } else {
                                                                                                                p.message("You see nothing significant.");
                                                                                                            }
                                                                                                        } else
                                                                                                            if (obj.getID() == LegendsQuestGameObjects.TOTEM_POLE) {
                                                                                                                // BLACK
                                                                                                                if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 10) || (p.getQuestStage(Quests.LEGENDS_QUEST) == (-1))) {
                                                                                                                    Functions.replaceObjectDelayed(obj, 10000, 1170);
                                                                                                                    Functions.___message(p, 1300, "This totem pole is truly awe inspiring.", "It depicts powerful Karamja jungle animals.", "It is very well carved and brings a sense of power ", "and spiritual fullfilment to anyone who looks at it.");
                                                                                                                    return null;
                                                                                                                }
                                                                                                                if (p.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
                                                                                                                    replaceTotemPole(p, obj, false);
                                                                                                                    return null;
                                                                                                                }
                                                                                                                Functions.___message(p, 1300, "This totem pole looks very corrupted,", "there is a darkness about it that seems quite unnatural.", "You don't like to look at it for too long.");
                                                                                                            } else
                                                                                                                if (obj.getID() == (LegendsQuestGameObjects.TOTEM_POLE + 1)) {
                                                                                                                    // RED
                                                                                                                    if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 10) || (p.getQuestStage(Quests.LEGENDS_QUEST) == (-1))) {
                                                                                                                        Functions.___message(p, 1300, "This totem pole is truly awe inspiring.", "It depicts powerful Karamja jungle animals.", "It is very well carved and brings a sense of power ", "and spiritual fullfilment to anyone who looks at it.");
                                                                                                                        return null;
                                                                                                                    }
                                                                                                                    if (p.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
                                                                                                                        replaceTotemPole(p, obj, false);
                                                                                                                        return null;
                                                                                                                    }
                                                                                                                    Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.TOTEM_POLE, obj.getDirection(), obj.getType()));
                                                                                                                    Functions.___message(p, 1300, "This totem pole looks very corrupted,", "there is a darkness about it that seems quite unnatural.", "You don't like to look at it for too long.");
                                                                                                                } else
                                                                                                                    if (obj.getID() == LegendsQuestGameObjects.GRAND_VIZIERS_DESK) {
                                                                                                                        p.message("You rap loudly on the desk.");
                                                                                                                        Npc radimus = Functions.getNearestNpc(p, NpcId.SIR_RADIMUS_ERKLE_HOUSE.id(), 6);
                                                                                                                        if (radimus != null) {
                                                                                                                            radimus.teleport(517, 545);
                                                                                                                            Functions.npcWalkFromPlayer(p, radimus);
                                                                                                                            radimus.initializeTalkScript(p);
                                                                                                                        } else {
                                                                                                                            p.message("Sir Radimus Erkle is currently busy at the moment.");
                                                                                                                        }
                                                                                                                    } else
                                                                                                                        if (obj.getID() == LegendsQuestGameObjects.LEGENDS_CUPBOARD) {
                                                                                                                            if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 1) || (p.getQuestStage(Quests.LEGENDS_QUEST) == (-1))) {
                                                                                                                                if (Functions.hasItem(p, ItemId.MACHETTE.id())) {
                                                                                                                                    p.message("The cupboard is empty.");
                                                                                                                                } else {
                                                                                                                                    Functions.___message(p, 1200, "You open the cupboard and find a machette.", "You take it out and add it to your inventory.");
                                                                                                                                    Functions.addItem(p, ItemId.MACHETTE.id(), 1);
                                                                                                                                }
                                                                                                                            } else {
                                                                                                                                p.message("@gre@Sir Radimus Erkle: You're not authorised to open that cupboard.");
                                                                                                                            }
                                                                                                                        } else
                                                                                                                            if (obj.getID() == LegendsQuestGameObjects.CRAFTED_TOTEM_POLE) {
                                                                                                                                if (obj.getOwner().equals(p.getUsername())) {
                                                                                                                                    Functions.___message(p, 1300, "This totem pole looks very heavy...");
                                                                                                                                    Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.FERTILE_EARTH, obj.getDirection(), obj.getType()));
                                                                                                                                    Functions.addItem(p, ItemId.TOTEM_POLE.id(), 1);
                                                                                                                                    if (!p.getCache().hasKey("crafted_totem_pole")) {
                                                                                                                                        p.getCache().store("crafted_totem_pole", true);
                                                                                                                                    }
                                                                                                                                    p.message("Carrying this totem pole saps your strength...");
                                                                                                                                    p.getSkills().setLevel(Skills.STRENGTH, ((int) (p.getSkills().getLevel(Skills.STRENGTH) * 0.9)));
                                                                                                                                } else {
                                                                                                                                    p.message("This is not your totem pole to carry.");
                                                                                                                                }
                                                                                                                            }


























                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return (((((((((((((item.getID() == ItemId.MACHETTE.id()) && (obj.getID() == LegendsQuestGameObjects.TALL_REEDS)) || ((item.getID() == ItemId.CUT_REED_PLANT.id()) && (obj.getID() == LegendsQuestGameObjects.SHALLOW_WATER))) || ((item.getID() == ItemId.BLESSED_GOLDEN_BOWL.id()) && (obj.getID() == LegendsQuestGameObjects.SHALLOW_WATER))) || (obj.getID() == LegendsQuestGameObjects.CARVED_ROCK)) || ((obj.getID() == LegendsQuestGameObjects.WOODEN_BEAM) && (item.getID() == ItemId.ROPE.id()))) || (obj.getID() == LegendsQuestGameObjects.ANCIENT_LAVA_FURNACE)) || ((obj.getID() == LegendsQuestGameObjects.RED_EYE_ROCK) && (item.getID() == ItemId.A_RED_CRYSTAL.id()))) || ((obj.getID() == LegendsQuestGameObjects.CAVERNOUS_OPENING) && (item.getID() == ItemId.A_GLOWING_RED_CRYSTAL.id()))) || ((obj.getID() == LegendsQuestGameObjects.FERTILE_EARTH) && (item.getID() == ItemId.YOMMI_TREE_SEED.id()))) || ((obj.getID() == LegendsQuestGameObjects.FERTILE_EARTH) && (item.getID() == ItemId.GERMINATED_YOMMI_TREE_SEED.id()))) || ((obj.getID() == LegendsQuestGameObjects.YOMMI_TREE) && (item.getID() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()))) || (Functions.inArray(obj.getID(), LegendsQuestGameObjects.DEAD_YOMMI_TREE, LegendsQuestGameObjects.ROTTEN_YOMMI_TREE, LegendsQuestGameObjects.GROWN_YOMMI_TREE, LegendsQuestGameObjects.CHOPPED_YOMMI_TREE, LegendsQuestGameObjects.TRIMMED_YOMMI_TREE) && (item.getID() == ItemId.RUNE_AXE.id()))) || ((obj.getID() == LegendsQuestGameObjects.TOTEM_POLE) && (item.getID() == ItemId.TOTEM_POLE.id()));
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if ((obj.getID() == LegendsQuestGameObjects.TOTEM_POLE) && (item.getID() == ItemId.TOTEM_POLE.id())) {
                        if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 10) || (p.getQuestStage(Quests.LEGENDS_QUEST) == (-1))) {
                            Functions.___message(p, "You have already replaced the evil totem pole with your own.", "You feel a great sense of accomplishment");
                            return null;
                        }
                        if (p.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
                            replaceTotemPole(p, obj, true);
                            return null;
                        }
                        if (p.getQuestStage(Quests.LEGENDS_QUEST) == 8) {
                            if (p.getCache().hasKey("killed_viyeldi") && (!p.getCache().hasKey("viyeldi_companions"))) {
                                p.getCache().set("viyeldi_companions", 1);
                            }
                            Functions.___message(p, "You attempt to replace the evil totem pole.", "A black cloud emanates from the evil totem pole.");
                            p.message("It slowly forms into the dread demon Nezikchened...");
                            LegendsQuestNezikchened.demonFight(p);
                        }
                    } else
                        if ((obj.getID() == LegendsQuestGameObjects.TRIMMED_YOMMI_TREE) && (item.getID() == ItemId.RUNE_AXE.id())) {
                            if (obj.getOwner().equals(p.getUsername())) {
                                int objectX = obj.getX();
                                int objectY = obj.getY();
                                Functions.___message(p, 1300, "You craft a totem pole out of the Yommi tree.");
                                Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.CRAFTED_TOTEM_POLE, obj.getDirection(), obj.getType(), p.getUsername()));
                                obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 60000, "Legends Quest Craft Totem Pole") {
                                    public void action() {
                                        GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
                                        if ((whatObject != null) && (whatObject.getID() == LegendsQuestGameObjects.CRAFTED_TOTEM_POLE)) {
                                            obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.FERTILE_EARTH, obj.getDirection(), obj.getType()));
                                        }
                                    }
                                });
                            } else {
                                p.message("This is not your Yommi Tree.");
                            }
                        } else
                            if ((obj.getID() == LegendsQuestGameObjects.CHOPPED_YOMMI_TREE) && (item.getID() == ItemId.RUNE_AXE.id())) {
                                if (obj.getOwner().equals(p.getUsername())) {
                                    int objectX = obj.getX();
                                    int objectY = obj.getY();
                                    Functions.___message(p, 1300, "You professionally wield your Rune Axe...", "As you trim the branches from the Yommi tree.");
                                    Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.TRIMMED_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
                                    obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 60000, "Legend Quest Trim Yommi Tree") {
                                        public void action() {
                                            GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
                                            if ((whatObject != null) && (whatObject.getID() == LegendsQuestGameObjects.TRIMMED_YOMMI_TREE)) {
                                                obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.FERTILE_EARTH, obj.getDirection(), obj.getType()));
                                            }
                                        }
                                    });
                                } else {
                                    p.message("This is not your Yommi Tree.");
                                }
                            } else
                                if ((obj.getID() == LegendsQuestGameObjects.GROWN_YOMMI_TREE) && (item.getID() == ItemId.RUNE_AXE.id())) {
                                    if (obj.getOwner().equals(p.getUsername())) {
                                        int objectX = obj.getX();
                                        int objectY = obj.getY();
                                        Functions.___message(p, 1300, "You wield the Rune Axe and prepare to chop the Yommi tree.");
                                        Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.CHOPPED_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
                                        obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 60000, "Legend Quest Chop Yommi Tree") {
                                            public void action() {
                                                GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
                                                if ((whatObject != null) && (whatObject.getID() == LegendsQuestGameObjects.CHOPPED_YOMMI_TREE)) {
                                                    obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.FERTILE_EARTH, obj.getDirection(), obj.getType()));
                                                }
                                            }
                                        });
                                        Functions.___message(p, 1300, "You chop the Yommi tree down.", "Perhaps you should trim those branches ?");
                                    } else {
                                        p.message("This is not your Yommi Tree.");
                                    }
                                } else
                                    if (((obj.getID() == LegendsQuestGameObjects.DEAD_YOMMI_TREE) || (obj.getID() == LegendsQuestGameObjects.ROTTEN_YOMMI_TREE)) && (item.getID() == ItemId.RUNE_AXE.id())) {
                                        Functions.___message(p, 0, "You chop the dead Yommi Tree down.");
                                        Functions.___message(p, 1300, "You gain some logs..");
                                        Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.FERTILE_EARTH, obj.getDirection(), obj.getType()));
                                        Functions.addItem(p, ItemId.LOGS.id(), 1);
                                    } else
                                        if ((obj.getID() == LegendsQuestGameObjects.YOMMI_TREE) && (item.getID() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id())) {
                                            int objectX = obj.getX();
                                            int objectY = obj.getY();
                                            p.getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.BLESSED_GOLDEN_BOWL.id());
                                            Functions.displayTeleportBubble(p, obj.getX(), obj.getY(), true);
                                            Functions.___message(p, 1300, "You water the Yommi tree from the golden bowl...", "It grows at a remarkable rate.");
                                            Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.GROWN_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
                                            obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 15000, "Legend Quest Water Yommi Tree") {
                                                public void action() {
                                                    GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
                                                    if ((whatObject != null) && (whatObject.getID() == LegendsQuestGameObjects.GROWN_YOMMI_TREE)) {
                                                        obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.ROTTEN_YOMMI_TREE, obj.getDirection(), obj.getType()));
                                                        if (p.isLoggedIn()) {
                                                            p.message("The Yommi tree is past it's prime and dies .");
                                                        }
                                                        Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 60000);
                                                    }
                                                }
                                            });
                                            Functions.___message(p, 1300, "Soon the tree stops growing...", "It looks tall enough now to make a good totem pole.");
                                        } else
                                            if ((obj.getID() == LegendsQuestGameObjects.FERTILE_EARTH) && (item.getID() == ItemId.YOMMI_TREE_SEED.id())) {
                                                p.message("These seeds need to be germinated in pure water before they");
                                                p.message("can be planted in the fertile soil.");
                                            } else
                                                if ((obj.getID() == LegendsQuestGameObjects.FERTILE_EARTH) && (item.getID() == ItemId.GERMINATED_YOMMI_TREE_SEED.id())) {
                                                    if ((p.getQuestStage(Quests.LEGENDS_QUEST) != 8) || (!Functions.hasItem(p, ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()))) {
                                                        p.message("You'll need some sacred water to feed ");
                                                        p.message("the tree when it starts growing.");
                                                        return null;
                                                    }
                                                    if (!Functions.hasItem(p, ItemId.RUNE_AXE.id())) {
                                                        p.message("You'll need a very tough, very sharp axe to");
                                                        p.message("fell the tree once it is grown.");
                                                        return null;
                                                    }
                                                    if (Functions.getCurrentLevel(p, Skills.WOODCUT) < 50) {
                                                        p.message("You need an woodcut level of 50 to");
                                                        p.message("fell the tree once it is grown.");
                                                        return null;
                                                    }
                                                    if (Functions.getCurrentLevel(p, Skills.HERBLAW) < 45) {
                                                        p.message("You need a herblaw skill of at least 45 to complete this task.");
                                                        return null;
                                                    }
                                                    // 1112, 1107
                                                    // 1172
                                                    Functions.removeItem(p, ItemId.GERMINATED_YOMMI_TREE_SEED.id(), 1);
                                                    if (DataConversions.random(0, 1) != 1) {
                                                        int objectX = obj.getX();
                                                        int objectY = obj.getY();
                                                        Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.BABY_YOMMI_TREE, obj.getDirection(), obj.getType()));
                                                        Functions.___message(p, 1300, "You bury the Germinated Yommi tree seed in the fertile earth...", "You start to see something growing.");
                                                        Functions.replaceObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.YOMMI_TREE, obj.getDirection(), obj.getType()));
                                                        obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 15000, "Legends Quest Grow Yommi Tree") {
                                                            public void action() {
                                                                GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
                                                                if ((whatObject != null) && (whatObject.getID() == LegendsQuestGameObjects.YOMMI_TREE)) {
                                                                    obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), LegendsQuestGameObjects.DEAD_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
                                                                    if (p.isLoggedIn()) {
                                                                        p.message("The Sapling dies.");
                                                                    }
                                                                    Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 60000);
                                                                }
                                                            }
                                                        });
                                                        p.message("The plant grows at a remarkable rate.");
                                                        p.message("It looks as if the tree needs to be watered...");
                                                    } else {
                                                        p.message("You planted the seed incorrectly, it withers and dies.");
                                                    }
                                                } else
                                                    if ((obj.getID() == LegendsQuestGameObjects.CAVERNOUS_OPENING) && (item.getID() == ItemId.A_GLOWING_RED_CRYSTAL.id())) {
                                                        Functions.___message(p, 1300, "You carefully place the glowing heart shaped crystal into ", "the depression, it slots in perfectly and glows even brighter.", "You hear a snapping sound coming from in front of the cave.");
                                                        Functions.removeItem(p, item.getID(), 1);
                                                        if (!p.getCache().hasKey("cavernous_opening")) {
                                                            p.getCache().store("cavernous_opening", true);
                                                        }
                                                    } else
                                                        if ((obj.getID() == LegendsQuestGameObjects.RED_EYE_ROCK) && (item.getID() == ItemId.A_RED_CRYSTAL.id())) {
                                                            Functions.___message(p, 1300, "You carefully place the Dragon Crystal on the rock.", "The rocks seem to vibrate and hum and the crystal starts to glow.");
                                                            p.message("The vibration in the area diminishes, but the crystal continues to glow.");
                                                            p.getInventory().replace(ItemId.A_RED_CRYSTAL.id(), ItemId.A_GLOWING_RED_CRYSTAL.id());
                                                        } else
                                                            if (obj.getID() == LegendsQuestGameObjects.ANCIENT_LAVA_FURNACE) {
                                                                switch (ItemId.getById(item.getID())) {
                                                                    case A_CHUNK_OF_CRYSTAL :
                                                                    case A_LUMP_OF_CRYSTAL :
                                                                    case A_HUNK_OF_CRYSTAL :
                                                                        if (Functions.getCurrentLevel(p, Skills.CRAFTING) < 50) {
                                                                            // message possibly non kosher
                                                                            p.message("You need a crafting ability of at least 50 to perform this task.");
                                                                            return null;
                                                                        }
                                                                        if (!p.getCache().hasKey(item.getDef(p.getWorld()).getName().toLowerCase().replace(" ", "_"))) {
                                                                            p.getCache().store(item.getDef(p.getWorld()).getName().toLowerCase().replace(" ", "_"), true);
                                                                            Functions.removeItem(p, item.getID(), 1);
                                                                            Functions.___message(p, 1300, "You carefully place the piece of crystal into ", "a specially shaped compartment in the furnace.");
                                                                        }
                                                                        if ((p.getCache().hasKey("a_chunk_of_crystal") && p.getCache().hasKey("a_lump_of_crystal")) && p.getCache().hasKey("a_hunk_of_crystal")) {
                                                                            Functions.___message(p, 1300, "You place the final segment of the crystal together into the ", "strangely shaped compartment, all the pieces seem to fit...", "You use your crafting skill to control the furnace.", "The heat in the furnace slowly rises and soon fuses the parts together...", "As soon as the item cools, you pick it up...", "As the crystal touches your hands a voice inside of your head says..", "@gre@Voice in head: Bring life to the dragons eye.");
                                                                            p.getCache().remove("a_chunk_of_crystal");
                                                                            p.getCache().remove("a_lump_of_crystal");
                                                                            p.getCache().remove("a_hunk_of_crystal");
                                                                            Functions.addItem(p, ItemId.A_RED_CRYSTAL.id(), 1);
                                                                        } else {
                                                                            Functions.___message(p, 1300, "The compartment in the furnace isn't full yet.");
                                                                            Functions.___message(p, 600, "It looks like you need more pieces of crystal.");
                                                                        }
                                                                        break;
                                                                    default :
                                                                        p.message("Nothing interesting happens");
                                                                        break;
                                                                }
                                                            } else
                                                                if ((obj.getID() == LegendsQuestGameObjects.WOODEN_BEAM) && (item.getID() == ItemId.ROPE.id())) {
                                                                    p.message("You throw one end of the rope around the beam.");
                                                                    Functions.removeItem(p, ItemId.ROPE.id(), 1);
                                                                    Functions.replaceObjectDelayed(obj, 5000, LegendsQuestGameObjects.WOODEN_BEAM + 1);
                                                                    if (!p.getCache().hasKey("legends_wooden_beam")) {
                                                                        p.getCache().store("legends_wooden_beam", true);
                                                                    }
                                                                } else
                                                                    if (obj.getID() == LegendsQuestGameObjects.CARVED_ROCK) {
                                                                        switch (ItemId.getById(item.getID())) {
                                                                            case SAPPHIRE :
                                                                            case EMERALD :
                                                                            case RUBY :
                                                                            case DIAMOND :
                                                                            case OPAL :
                                                                            case JADE :
                                                                            case RED_TOPAZ :
                                                                                int attachmentMode = -1;
                                                                                boolean alreadyAttached = false;
                                                                                if (((item.getID() == ItemId.OPAL.id()) && (obj.getX() == 471)) && (obj.getY() == 3722)) {
                                                                                    // OPAL ROCK
                                                                                    attachmentMode = 1;
                                                                                } else
                                                                                    if (((item.getID() == ItemId.EMERALD.id()) && (obj.getX() == 474)) && (obj.getY() == 3730)) {
                                                                                        // EMERALD ROCK
                                                                                        attachmentMode = 2;
                                                                                    } else
                                                                                        if (((item.getID() == ItemId.RUBY.id()) && (obj.getX() == 471)) && (obj.getY() == 3734)) {
                                                                                            // RUBY ROCK
                                                                                            attachmentMode = 3;
                                                                                        } else
                                                                                            if (((item.getID() == ItemId.DIAMOND.id()) && (obj.getX() == 466)) && (obj.getY() == 3739)) {
                                                                                                // DIAMOND ROCK
                                                                                                attachmentMode = 4;
                                                                                            } else
                                                                                                if (((item.getID() == ItemId.SAPPHIRE.id()) && (obj.getX() == 460)) && (obj.getY() == 3737)) {
                                                                                                    // SAPPHIRE ROCK
                                                                                                    attachmentMode = 5;
                                                                                                } else
                                                                                                    if (((item.getID() == ItemId.RED_TOPAZ.id()) && (obj.getX() == 464)) && (obj.getY() == 3730)) {
                                                                                                        // RED TOPAZ ROCK
                                                                                                        attachmentMode = 6;
                                                                                                    } else
                                                                                                        if (((item.getID() == ItemId.JADE.id()) && (obj.getX() == 469)) && (obj.getY() == 3728)) {
                                                                                                            // JADE ROCK
                                                                                                            attachmentMode = 7;
                                                                                                        }






                                                                                if (p.getCache().hasKey("legends_attach_" + attachmentMode)) {
                                                                                    alreadyAttached = true;
                                                                                    attachmentMode = -1;
                                                                                }
                                                                                if (alreadyAttached) {
                                                                                    p.message(("You have already placed an " + item.getDef(p.getWorld()).getName()) + " above this rock.");
                                                                                    Functions.createGroundItemDelayedRemove(new GroundItem(p.getWorld(), item.getID(), obj.getX(), obj.getY(), 1, p), 5000);
                                                                                    Functions.___message(p, 1300, ("A barely visible " + item.getDef(p.getWorld()).getName()) + " becomes clear again, spinning above the rock.");
                                                                                    p.message("And then fades again...");
                                                                                } else {
                                                                                    if ((attachmentMode != (-1)) && (!Functions.hasItem(p, ItemId.BOOKING_OF_BINDING.id()))) {
                                                                                        Functions.removeItem(p, item.getID(), 1);
                                                                                        p.message("You carefully move the gem closer to the rock.");
                                                                                        p.message(("The " + item.getDef(p.getWorld()).getName()) + " glows and starts spinning as it hovers above the rock.");
                                                                                        Functions.createGroundItemDelayedRemove(new GroundItem(p.getWorld(), item.getID(), obj.getX(), obj.getY(), 1, p), 5000);
                                                                                        if (!p.getCache().hasKey("legends_attach_" + attachmentMode)) {
                                                                                            p.getCache().store("legends_attach_" + attachmentMode, true);
                                                                                        }
                                                                                        if ((((((p.getCache().hasKey("legends_attach_1") && p.getCache().hasKey("legends_attach_2")) && p.getCache().hasKey("legends_attach_3")) && p.getCache().hasKey("legends_attach_4")) && p.getCache().hasKey("legends_attach_5")) && p.getCache().hasKey("legends_attach_6")) && p.getCache().hasKey("legends_attach_7")) {
                                                                                            Functions.___message(p, 1300, "Suddenly all the crystals begin to glow very brightly.", "The room is lit up with the bright light...", "Soon, the light from all the crystals converges into a point.", "And you see a strange book appear where the light is focused.", "You pick the book up and place it in your inventory.", "All the crystals disapear...and the light fades...");
                                                                                            Functions.addItem(p, ItemId.BOOKING_OF_BINDING.id(), 1);
                                                                                            for (int i = 0; i < 8; i++) {
                                                                                                if (p.getCache().hasKey("legends_attach_" + i)) {
                                                                                                    p.getCache().remove("legends_attach_" + i);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        p.message("You carefully move the gem closer to the rock.");
                                                                                        p.message("but nothing happens...");
                                                                                    }
                                                                                }
                                                                                break;
                                                                            default :
                                                                                p.message("Nothing interesting happens");
                                                                                break;
                                                                        }
                                                                    } else
                                                                        if ((item.getID() == ItemId.MACHETTE.id()) && (obj.getID() == LegendsQuestGameObjects.TALL_REEDS)) {
                                                                            Functions.addItem(p, ItemId.CUT_REED_PLANT.id(), 1);
                                                                            Functions.___message(p, 1300, "You use your machette to cut down a tall reed.", "You cut it into a length of pipe.");
                                                                        } else
                                                                            if (item.getID() == ItemId.BLESSED_GOLDEN_BOWL.id()) {
                                                                                if (((p.getQuestStage(Quests.LEGENDS_QUEST) == 8) && (p.getY() >= 3723)) && (p.getY() <= 3740)) {
                                                                                    p.message("You fill the bowl up with water..");
                                                                                    p.getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id());
                                                                                    return null;
                                                                                }
                                                                                Functions.___message(p, 1300, "The water is awkward to get to...", "The gap to the water is too narrow.");
                                                                            } else
                                                                                if ((item.getID() == ItemId.CUT_REED_PLANT.id()) && (obj.getID() == LegendsQuestGameObjects.SHALLOW_WATER)) {
                                                                                    if (Functions.atQuestStages(p, Quests.LEGENDS_QUEST, 5, 6, 7)) {
                                                                                        Functions.___message(p, 1300, "It looks as if this pool has dried up...", "A thick black sludge has replaced the sparkling pure water...", "There is a disgusting stench of death that emanates from this area...", "Maybe Gujuo knows what's happened...");
                                                                                        if (p.getQuestStage(Quests.LEGENDS_QUEST) == 5) {
                                                                                            p.updateQuestStage(Quests.LEGENDS_QUEST, 6);
                                                                                        }
                                                                                        return null;
                                                                                    }
                                                                                    if (((p.getQuestStage(Quests.LEGENDS_QUEST) >= 9) || (p.getQuestStage(Quests.LEGENDS_QUEST) == (-1))) && (!p.getWorld().getServer().getConfig().LOOSE_SHALLOW_WATER_CHECK)) {
                                                                                        Functions.___message(p, 1300, "You use the cut reed plant to syphon some water from the pool.", "You take a refreshing drink from the pool.", "The cut reed is soaked through with water and is now all soggy.");
                                                                                        return null;
                                                                                    }
                                                                                    int emptyID = -1;
                                                                                    int refilledID = -1;
                                                                                    for (int i = 0; i < REFILLABLE.length; i++) {
                                                                                        if (Functions.hasItem(p, REFILLABLE[i])) {
                                                                                            emptyID = REFILLABLE[i];
                                                                                            refilledID = REFILLED[i];
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                    if (emptyID != ItemId.NOTHING.id()) {
                                                                                        Functions.___message(p, 1300, "You use the cut reed plant to syphon some water from the pool.");
                                                                                        if (emptyID == ItemId.GOLDEN_BOWL.id()) {
                                                                                            Functions.___message(p, 1300, "into your gold bowl.");
                                                                                            p.getInventory().replace(ItemId.GOLDEN_BOWL.id(), ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id());
                                                                                            Functions.___message(p, 1300, "The water doesn't seem to sparkle as much as it did in the pool.");
                                                                                        } else
                                                                                            if (emptyID == ItemId.BLESSED_GOLDEN_BOWL.id()) {
                                                                                                Functions.___message(p, 1300, "into your blessed gold bowl.");
                                                                                                p.getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id());
                                                                                                Functions.___message(p, 1300, "The water seems to bubble and sparkle as if alive.");
                                                                                            } else {
                                                                                                Functions.___message(p, 1300, ("You put some water in the " + p.getWorld().getServer().getEntityHandler().getItemDef(emptyID).getName().toLowerCase()) + ".");
                                                                                                p.getInventory().replace(emptyID, refilledID);
                                                                                            }

                                                                                        Functions.removeItem(p, ItemId.CUT_REED_PLANT.id(), 1);
                                                                                        Functions.___message(p, 0, "The cut reed is soaked through with water and is now all soggy.");
                                                                                    } else {
                                                                                        Functions.___message(p, 1300, "You start to syphon some water up the tube...");
                                                                                        Functions.___message(p, 0, "But you have nothing to put the water in.");
                                                                                    }
                                                                                }















                    return null;
                });
            }
        };
    }

    private void replaceTotemPole(Player p, GameObject obj, boolean calledGujuo) {
        if (Functions.hasItem(p, ItemId.TOTEM_POLE.id())) {
            if (p.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
                p.updateQuestStage(Quests.LEGENDS_QUEST, 10);
            }
            Functions.replaceObjectDelayed(obj, 10000, 1170);
            Functions.removeItem(p, ItemId.TOTEM_POLE.id(), 1);
            Functions.___message(p, "You remove the evil totem pole.", "And replace it with the one you carved yourself.", "As you do so, you feel a lightness in the air,");
            p.message("almost as if the Kharazi jungle were sighing.");
            p.message("Perhaps Gujuo would like to see the totem pole.");
            if (calledGujuo) {
                Npc gujuo = Functions.spawnNpc(obj.getWorld(), NpcId.GUJUO.id(), p.getX(), p.getY(), 60000 * 3);
                if (gujuo != null) {
                    gujuo.initializeTalkScript(p);
                }
            }
        } else {
            p.message("I shall replace it with the Totem pole");
        }
    }
}

