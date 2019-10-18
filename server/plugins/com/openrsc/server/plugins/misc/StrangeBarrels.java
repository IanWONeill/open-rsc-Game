package com.openrsc.server.plugins.misc;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import java.util.concurrent.Callable;


public class StrangeBarrels implements ObjectActionListener , ObjectActionExecutiveListener {
    /**
     *
     *
     * @author Davve
    What I have discovered from barrels is the food, potions, misc, weapon, runes, certificates and monsters.
    There are 8 ways that the barrel behave - more comment below.
    The barrel is smashed and then after 40 seconds it adds on a new randomized coord in the cave area.
     */
    private static final int STRANGE_BARREL = 1178;

    private static final int[] FOOD = new int[]{ ItemId.BREAD.id(), ItemId.SPINACH_ROLL.id(), ItemId.SLICE_OF_CAKE.id(), ItemId.HALF_A_MEAT_PIE.id(), ItemId.CHEESE.id(), ItemId.HALF_A_REDBERRY_PIE.id(), ItemId.HALF_AN_APPLE_PIE.id(), ItemId.FRESH_PINEAPPLE.id() };

    private static final int[] POTION = new int[]{ ItemId.ONE_DEFENSE_POTION.id(), ItemId.ONE_STRENGTH_POTION.id(), ItemId.ONE_RESTORE_PRAYER_POTION.id(), ItemId.ONE_ATTACK_POTION.id() };

    private static final int[] OTHER = new int[]{ ItemId.ROPE.id(), ItemId.ROCKS.id(), ItemId.SHIP_TICKET.id(), ItemId.UNIDENTIFIED_SNAKE_WEED.id(), ItemId.COINS.id(), ItemId.BOW_STRING.id(), ItemId.BRONZE_PICKAXE.id(), ItemId.CASKET.id(), ItemId.GOLD_BAR.id(), ItemId.LOGS.id(), ItemId.PARAMAYA_REST_TICKET.id(), ItemId.STEEL_PICKAXE.id(), ItemId.TINDERBOX.id(), ItemId.LIT_TORCH.id() };

    private static final int[] WEAPON = new int[]{ ItemId.BRONZE_THROWING_DART.id(), ItemId.BRONZE_THROWING_KNIFE.id(), ItemId.RUNE_THROWING_KNIFE.id(), ItemId.MITHRIL_THROWING_KNIFE.id(), ItemId.STEEL_THROWING_DART.id(), ItemId.IRON_THROWING_DART.id(), ItemId.MITHRIL_THROWING_DART.id(), ItemId.IRON_THROWING_KNIFE.id(), ItemId.STEEL_THROWING_KNIFE.id(), ItemId.ADAMANTITE_THROWING_KNIFE.id(), ItemId.ADAMANTITE_THROWING_DART.id() };

    private static final int[] RUNES = new int[]{ ItemId.EARTH_RUNE.id(), ItemId.WATER_RUNE.id(), ItemId.AIR_RUNE.id(), ItemId.FIRE_RUNE.id() };

    private static final int[] CERTIFICATE = new int[]{ ItemId.COAL_CERTIFICATE.id(), ItemId.MITHRIL_ORE_CERTIFICATE.id(), ItemId.RAW_BASS_CERTIFICATE.id(), ItemId.RAW_LOBSTER_CERTIFICATE.id(), ItemId.SWORDFISH_CERTIFICATE.id(), ItemId.RAW_SHARK_CERTIFICATE.id(), ItemId.SHARK_CERTIFICATE.id(), ItemId.WILLOW_LOGS_CERTIFICATE.id(), ItemId.YEW_LOGS_CERTIFICATE.id() };

    // TODO CHECK IDS ON AMBIGUOUS NPCS
    private static final int[] MONSTER = new int[]{ NpcId.CHAOS_DWARF.id(), NpcId.DARK_WARRIOR.id(), NpcId.DARKWIZARD_LVL13.id(), NpcId.DEADLY_RED_SPIDER.id(), NpcId.DEATH_WING.id(), NpcId.GIANT.id(), NpcId.GIANT_BAT.id(), NpcId.MUGGER.id(), NpcId.GIANT_SPIDER_LVL8.id(), NpcId.ZOMBIE_LVL24_GEN.id(), NpcId.SKELETON_LVL25.id(), NpcId.SKELETON_LVL21.id(), NpcId.RAT_LVL13.id(), NpcId.HOBGOBLIN_LVL32.id(), NpcId.MOSS_GIANT.id(), NpcId.BLACK_KNIGHT.id(), NpcId.SKELETON_LVL31.id(), NpcId.RAT_LVL8.id(), NpcId.SCORPION.id() };

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == StrangeBarrels.STRANGE_BARREL;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == StrangeBarrels.STRANGE_BARREL) {
                        p.setBusyTimer(1);
                        int action = DataConversions.random(0, 4);
                        if (action != 0) {
                            p.message("You smash the barrel open.");
                            Functions.removeObject(obj);
                            p.getWorld().getServer().getGameEventHandler().add(new SingleEvent(p.getWorld(), null, 40000, "Smash Strange Barrel") {
                                // 40 seconds
                                public void action() {
                                    int newObjectX = DataConversions.random(467, 476);
                                    int newObjectY = DataConversions.random(3699, 3714);
                                    if (p.getWorld().getRegionManager().getRegion(Point.location(newObjectX, newObjectY)).getGameObject(Point.location(newObjectX, newObjectY)) != null) {
                                        Functions.registerObject(new GameObject(obj.getWorld(), obj.getLoc()));
                                    } else {
                                        Functions.registerObject(new GameObject(obj.getWorld(), Point.location(newObjectX, newObjectY), 1178, 0, 0));
                                    }
                                }
                            });
                            /* Out comes a NPC only. */
                            if (action == 1) {
                                spawnMonster(p, obj.getX(), obj.getY());
                            } else/* Out comes an item only. */

                                if (action == 2) {
                                    spawnItem(p, obj.getX(), obj.getY());
                                } else/* Out comes both a NPC and an ITEM. */

                                    if (action == 3) {
                                        spawnItem(p, obj.getX(), obj.getY());
                                        spawnMonster(p, obj.getX(), obj.getY());
                                    } else/* Smash the barrel and get randomly hit from 0-14 damage. */

                                        if (action == 4) {
                                            p.message("The barrel explodes...");
                                            p.message("...you take some damage...");
                                            Functions.displayTeleportBubble(p, obj.getX(), obj.getY(), true);
                                            p.damage(DataConversions.random(0, 14));
                                        }



                        } else {
                            /* Smash the barrel open but nothing happens. */
                            if (DataConversions.random(0, 1) != 1) {
                                p.message("You smash the barrel open.");
                                Functions.removeObject(obj);
                                Functions.delayedSpawnObject(obj.getWorld(), obj.getLoc(), 40000);// 40 seconds

                            } else {
                                if (DataConversions.random(0, 1) != 0) {
                                    p.message("You were unable to smash this barrel open.");
                                    Functions.___message(p, 1300, "You hit the barrel at the wrong angle.", "You're heavily jarred from the vibrations of the blow.");
                                    int reduceAttack = DataConversions.random(1, 3);
                                    p.message(("Your attack is reduced by " + reduceAttack) + ".");
                                    p.getSkills().setLevel(Skills.ATTACK, p.getSkills().getLevel(Skills.ATTACK) - reduceAttack);
                                } else {
                                    p.message("You were unable to smash this barrel open.");
                                }
                            }
                        }
                    }
                    return null;
                });
            }
        };
    }

    private void spawnMonster(Player p, int x, int y) {
        int randomizeMonster = DataConversions.random(0, StrangeBarrels.MONSTER.length - 1);
        int selectedMonster = StrangeBarrels.MONSTER[randomizeMonster];
        Npc monster = Functions.spawnNpc(p.getWorld(), selectedMonster, x, y, 60000 * 3);// 3 minutes

        Functions.sleep(600);
        if (monster != null) {
            monster.startCombat(p);
        }
    }

    private void spawnItem(Player p, int x, int y) {
        int randomizeReward = DataConversions.random(0, 100);
        int[] selectItemArray = StrangeBarrels.OTHER;
        if ((randomizeReward >= 0) && (randomizeReward <= 14)) {
            // 15%
            selectItemArray = StrangeBarrels.FOOD;
        } else
            if ((randomizeReward >= 15) && (randomizeReward <= 29)) {
                // 15%
                selectItemArray = StrangeBarrels.POTION;
            } else
                if ((randomizeReward >= 30) && (randomizeReward <= 44)) {
                    // 15%
                    selectItemArray = StrangeBarrels.RUNES;
                } else
                    if ((randomizeReward >= 45) && (randomizeReward <= 59)) {
                        // 15%
                        selectItemArray = StrangeBarrels.CERTIFICATE;
                    } else
                        if ((randomizeReward >= 60) && (randomizeReward <= 89)) {
                            // 30%
                            selectItemArray = StrangeBarrels.OTHER;
                        } else
                            if ((randomizeReward >= 90) && (randomizeReward <= 100)) {
                                // 11%
                                selectItemArray = StrangeBarrels.WEAPON;
                            }





        int randomizeItem = DataConversions.random(0, selectItemArray.length - 1);
        int selectedItem = selectItemArray[randomizeItem];
        if (selectedItem == 10) {
            Functions.createGroundItem(selectedItem, 100, x, y, p);
        } else {
            Functions.createGroundItem(selectedItem, 1, x, y, p);
        }
    }
}

