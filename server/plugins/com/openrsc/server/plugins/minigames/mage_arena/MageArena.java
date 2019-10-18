package com.openrsc.server.plugins.minigames.mage_arena;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.event.rsc.impl.ObjectRemover;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerDeathExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import java.util.Optional;
import java.util.concurrent.Callable;


public class MageArena implements MiniGameInterface , ObjectActionListener , PickupListener , PlayerKilledNpcListener , TalkToNpcListener , ObjectActionExecutiveListener , PickupExecutiveListener , PlayerAttackNpcExecutiveListener , PlayerDeathExecutiveListener , PlayerKilledNpcExecutiveListener , PlayerMageNpcExecutiveListener , TalkToNpcExecutiveListener {
    public static final int SARADOMIN_STONE = 1152;

    public static final int GUTHIX_STONE = 1153;

    public static final int ZAMORAK_STONE = 1154;

    @Override
    public int getMiniGameId() {
        return Minigames.MAGE_ARENA;
    }

    @Override
    public String getMiniGameName() {
        return "Mage Arena (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player p) {
        // mini-quest complete handled already
    }

    @Override
    public GameStateEvent onTalkToNpc(final Player p, final Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.getMaxLevel(p, Skills.MAGIC) < 60) {
                        // TODO: Enter the arena game.
                        Functions.___playerTalk(p, n, "hello there", "what is this place?");
                        Functions.___npcTalk(p, n, "do not waste my time with trivial questions!", "i am the great kolodion, master of battle magic", "i have an arena to run");
                        Functions.___playerTalk(p, n, "can i enter?");
                        Functions.___npcTalk(p, n, "hah, a wizard of your level..don't be absurd");
                    } else
                        if (p.getCache().hasKey("mage_arena")) {
                            int stage = p.getCache().getInt("mage_arena");
                            /* Started but failed. */
                            if (stage == 1) {
                                Functions.___playerTalk(p, n, "hi");
                                Functions.___npcTalk(p, n, "you return young conjurer..", "..you obviously have a taste for the darkside of magic", "let us continue with the battle...now");
                                if (cantGo(p)) {
                                    Functions.___message(p, "You cannot enter the arena...", "...while carrying weapons or armour");
                                    return null;
                                }
                                Functions.movePlayer(p, 229, 130);
                                Functions.setCurrentLevel(p, 0, 0);
                                Functions.setCurrentLevel(p, 2, 0);
                                spawnKolodion(p, p.getCache().getInt("kolodion_stage"));
                                startKolodionEvent(p);
                            } else
                                if (stage >= 2) {
                                    Functions.___playerTalk(p, n, "hello kolodion");
                                    Functions.___npcTalk(p, n, "hey there, how are you?, enjoying the bloodshed?");
                                    Functions.___playerTalk(p, n, "it's not bad, i've seen worse");
                                    int menu = Functions.___showMenu(p, n, "i think i've had enough for now", "how can i use my new spells outside of the arena?");
                                    if (menu == 0) {
                                        Functions.___npcTalk(p, n, "shame , you're a good battle mage", "hope to see you soon");
                                    } else
                                        if (menu == 1) {
                                            Functions.___npcTalk(p, n, "experience my friend, experience", "once you've used the spell enough times in the arena...", "...you'll be able to use them in the rest of runescape");
                                            Functions.___playerTalk(p, n, "good stuff");
                                            Functions.___npcTalk(p, n, "not so good for the citizens, they won't stand a chance");
                                            Functions.___playerTalk(p, n, "how am i doing so far?");
                                            if (p.getCache().hasKey("Saradomin strike_casts") && (p.getCache().getInt("Saradomin strike_casts") >= 100)) {
                                                Functions.___npcTalk(p, n, "you're fully trained to use the strike spell anywhere");
                                            } else {
                                                Functions.___npcTalk(p, n, "you still need to train with the strike spell...", "...inside the arena before you can use it outside");
                                            }
                                            if (p.getCache().hasKey("Claws of Guthix_casts") && (p.getCache().getInt("Claws of Guthix_casts") >= 100)) {
                                                Functions.___npcTalk(p, n, "you're fully trained to use the claw spell anywhere");
                                            } else {
                                                Functions.___npcTalk(p, n, "you still need to train with the claw spell...", "...inside the arena before you can use it outside");
                                            }
                                            if (p.getCache().hasKey("Flames of Zamorak_casts") && (p.getCache().getInt("Flames of Zamorak_casts") >= 100)) {
                                                Functions.___npcTalk(p, n, "you're fully trained to use the flame spell anywhere");
                                            } else {
                                                Functions.___npcTalk(p, n, "you still need to train with the flame spell...", "...inside the arena before you can use it outside");
                                            }
                                        }

                                }

                        } else {
                            Functions.___playerTalk(p, n, "hello there", "what is this place?");
                            Functions.___npcTalk(p, n, "i am the great kolodion, master of battle magic ...", "... and this is my battle arena", "top wizards travel from all over to fight here");
                            int choice = Functions.___showMenu(p, n, "can i fight here?", "what's the point of that?", "that's barbaric");
                            if (choice == 0) {
                                canifight(p, n);
                            } else
                                if (choice == 1) {
                                    whatsthepoint(p, n);
                                } else
                                    if (choice == 2) {
                                        barbaric(p, n);
                                    }


                        }

                    return null;
                });
            }
        };
    }

    public void canifight(Player p, Npc n) {
        Functions.___npcTalk(p, n, "my arena is open to any high level wizard", "but this is no game traveller, wizards fall in this arena..", "..never to rise again, the strongest of mage's have been destroyed", "but if you're sure you want in?");
        int choice = Functions.___showMenu(p, n, "yes indeedy", "no, i don't");
        if (choice == 0) {
            joinfight(p, n);
        } else
            if (choice == 1) {
                Functions.___npcTalk(p, n, "your loss");
            }

    }

    public void whatsthepoint(Player p, Npc n) {
        Functions.___npcTalk(p, n, "we learn how to use our magic to it fullest...", "..,how to channel forces of the cosmos into our world..", "..,but mainly I just like blasting people into dust");
        int choice = Functions.___showMenu(p, n, "can i fight here?", "that's barbaric");
        if (choice == 0) {
            canifight(p, n);
        } else
            if (choice == 1) {
                barbaric(p, n);
            }

    }

    public void barbaric(Player p, Npc n) {
        Functions.___npcTalk(p, n, "nope, it's magic, but I know what you mean", "so do you want to join us?");
        int choice = Functions.___showMenu(p, n, "yes indeedy", "no, i don't");
        if (choice == 0) {
            joinfight(p, n);
        } else
            if (choice == 1) {
                Functions.___npcTalk(p, n, "your loss");
            }

    }

    public void joinfight(Player p, Npc n) {
        Functions.___npcTalk(p, n, "good..good, you have a healthy sense of competition", "remember traveller in my arena hand to hand combat is useless", "your strength will diminish as you enter the arena", "but the spells you can learn are amongst the most powerful in runescape", "before i can accept you in, we must duel", "you may not take armour or weapons into the arena");
        if (cantGo(p)) {
            Functions.___message(p, "You cannot enter the arena...", "...while carrying weapons or armour");
        } else {
            int choice = Functions.___showMenu(p, n, "ok let's fight", "no thanks");
            if (choice == 0) {
                Functions.___npcTalk(p, n, "I must check that you're up to scratch");
                Functions.___playerTalk(p, n, "you don't need to worry about that");
                Functions.___npcTalk(p, n, "not just any magician can enter traveller", "only the most powerful, the most feared", "before you use the power of this arena", "you must prove yourself against me", "now!");
                if (!p.getCache().hasKey("mage_arena")) {
                    p.getCache().set("mage_arena", 1);
                }
                Functions.movePlayer(p, 229, 130);
                Functions.setCurrentLevel(p, Skills.ATTACK, 0);
                Functions.setCurrentLevel(p, Skills.STRENGTH, 0);
                startKolodionEvent(p);
                spawnKolodion(p, NpcId.KOLODION_HUMAN.id());
                Functions.sleep(650);
            } else
                if (choice == 1) {
                    Functions.___npcTalk(p, n, "your loss");
                }

        }
    }

    public void learnSpellEvent(Player p) {
        DelayedEvent mageArena = p.getAttribute("mageArenaEvent", null);
        DelayedEvent mageArenaEvent = new DelayedEvent(p.getWorld(), p, 1900, "Mage Arena Learn Spell Event") {
            @Override
            public void run() {
                /* Player logged out. */
                if ((!getOwner().isLoggedIn()) || getOwner().isRemoved()) {
                    stop();
                    return;
                }
                if (!getOwner().getLocation().inMageArena()) {
                    stop();
                    return;
                }
                if (getOwner().inCombat()) {
                    return;
                }
                if ((getOwner().getSkills().getLevel(Skills.ATTACK) > 0) || (getOwner().getSkills().getLevel(Skills.STRENGTH) > 0)) {
                    getOwner().getSkills().setLevel(Skills.ATTACK, 0);
                    getOwner().getSkills().setLevel(Skills.STRENGTH, 0);
                }
                Npc Guthix = Functions.getNearestNpc(p, NpcId.BATTLE_MAGE_GUTHIX.id(), 2);
                Npc Zamorak = Functions.getNearestNpc(p, NpcId.BATTLE_MAGE_ZAMAROK.id(), 2);
                Npc Saradomin = Functions.getNearestNpc(p, NpcId.BATTLE_MAGE_SARADOMIN.id(), 2);
                String[] randomMessage = new String[]{ "@yel@zamorak mage: feel the wrath of zamarok", "@yel@Saradomin mage: feel the wrath of Saradomin", "@yel@guthix mage: feel the wrath of guthix" };
                if ((Guthix != null) && Guthix.withinRange(getOwner(), 1)) {
                    godSpellObject(getOwner(), 33);
                    p.message(randomMessage[2]);
                    if (Functions.getCurrentLevel(getOwner(), Skills.HITS) < 20) {
                        getOwner().damage(2);
                    } else {
                        getOwner().damage(Functions.getCurrentLevel(getOwner(), Skills.HITS) / 10);
                    }
                } else
                    if ((Zamorak != null) && Zamorak.withinRange(getOwner(), 1)) {
                        godSpellObject(getOwner(), 35);
                        p.message(randomMessage[0]);
                        if (Functions.getCurrentLevel(getOwner(), Skills.HITS) < 20) {
                            getOwner().damage(2);
                        } else {
                            getOwner().damage(Functions.getCurrentLevel(getOwner(), Skills.HITS) / 10);
                        }
                    } else
                        if ((Saradomin != null) && Saradomin.withinRange(getOwner(), 1)) {
                            godSpellObject(getOwner(), 34);
                            p.message(randomMessage[1]);
                            if (Functions.getCurrentLevel(getOwner(), Skills.HITS) < 20) {
                                getOwner().damage(2);
                            } else {
                                getOwner().damage(Functions.getCurrentLevel(getOwner(), Skills.HITS) / 10);
                            }
                        }


            }
        };
        if (mageArena != null) {
            if (mageArena.shouldRemove()) {
                p.setAttribute("mageArenaEvent", mageArenaEvent);
                p.getWorld().getServer().getGameEventHandler().add(mageArenaEvent);
            }
        } else {
            p.setAttribute("mageArenaEvent", mageArenaEvent);
            p.getWorld().getServer().getGameEventHandler().add(mageArenaEvent);
        }
    }

    private void startKolodionEvent(Player p) {
        DelayedEvent kolE = p.getAttribute("kolodionEvent", null);
        DelayedEvent kolodionEvent = new DelayedEvent(p.getWorld(), p, 650, "Mage Arena Kolodion Event") {
            @Override
            public void run() {
                Npc npc = getOwner().getAttribute("spawned_kolodion");
                if (npc == null) {
                    return;
                }
                /* Player logged out. */
                if ((!getOwner().isLoggedIn()) || getOwner().isRemoved()) {
                    npc.remove();
                    stop();
                    return;
                }
                /* Npc has been removed from the world. */
                if (!p.getWorld().hasNpc(npc)) {
                    stop();
                    return;
                }
                /* Player has left the area */
                if (!npc.withinRange(getOwner())) {
                    npc.remove();
                    stop();
                    return;
                }
                if (getOwner().inCombat()) {
                    return;
                }
                if (!npc.withinRange(getOwner(), 8)) {
                    return;
                }
                if (Functions.random(0, 5) != 2) {
                    return;
                }
                int spell_type = Functions.random(0, 2);
                switch (spell_type) {
                    case 0 :
                        godSpellObject(getOwner(), 33);
                        break;
                    case 1 :
                        godSpellObject(getOwner(), 34);
                        break;
                    case 2 :
                        godSpellObject(getOwner(), 35);
                        break;
                }
                String[] randomMessage = new String[]{ "roooaar", "die you foolish mortal", "feel the power of the elements", "the bigger the better", "aaarrgghhh" };
                Functions.npcYell(getOwner(), npc, randomMessage[Functions.random(0, randomMessage.length - 1)]);
                getOwner().damage(Functions.random(3, 13));
            }
        };
        if (kolE != null) {
            if (kolE.shouldRemove()) {
                p.setAttribute("kolodionEvent", kolodionEvent);
                p.getWorld().getServer().getGameEventHandler().add(kolodionEvent);
            }
        } else {
            p.setAttribute("kolodionEvent", kolodionEvent);
            p.getWorld().getServer().getGameEventHandler().add(kolodionEvent);
        }
    }

    public void spawnKolodion(Player player, int id) {
        player.setAttribute("spawned_kolodion", Functions.spawnNpc(id, 227, 130, 300000, player));
        player.getCache().set("kolodion_stage", id);
        player.message("kolodion blasts you " + (id == NpcId.KOLODION_HUMAN.id() ? "with his staff" : "again"));
        player.damage(Functions.random(7, 15));
        startKolodionEvent(player);
        ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
    }

    private boolean cantGo(Player p) {
        for (Item item : p.getInventory().getItems()) {
            String name = item.getDef(p.getWorld()).getName().toLowerCase();
            if (((((((((((name.contains("dagger") || name.contains("scimitar")) || name.contains("bow")) || name.contains("mail")) || (((name.contains("sword") && (!name.equalsIgnoreCase("Swordfish"))) && (!name.equalsIgnoreCase("Burnt Swordfish"))) && (!name.equalsIgnoreCase("Raw Swordfish")))) || name.contains("mace")) || name.contains("helmet")) || name.contains("axe")) || name.contains("arrow")) || name.contains("bow")) || name.contains("spear")) || name.contains("battlestaff")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.KOLODION.id();
    }

    @Override
    public boolean blockPlayerKilledNpc(Player p, Npc n) {
        return Functions.inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(), NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id());
    }

    @Override
    public GameStateEvent onPlayerKilledNpc(Player p, Npc n) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (Functions.inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(), NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
                        n.remove();
                        if (n.getID() == NpcId.KOLODION_HUMAN.id()) {
                            Functions.___message(p, "kolodion slumps to the floor..");
                            Functions.___message(p, "..his body begins to grow and he changes form", "He becomes an intimidating ogre");
                            spawnKolodion(p, NpcId.KOLODION_OGRE.id());
                        } else
                            if (n.getID() == NpcId.KOLODION_OGRE.id()) {
                                Functions.___message(p, "kolodion slumps to the floor once more..", "..but again his body begins to grow and he changes form", "He becomes an enormous spider");
                                spawnKolodion(p, NpcId.KOLODION_SPIDER.id());
                            } else
                                if (n.getID() == NpcId.KOLODION_SPIDER.id()) {
                                    Functions.___message(p, "kolodion again slumps to the floor..", "..but again his body begins to grow as he changes form", "He becomes an ethereal being");
                                    spawnKolodion(p, NpcId.KOLODION_SOULESS.id());
                                } else
                                    if (n.getID() == NpcId.KOLODION_SOULESS.id()) {
                                        Functions.___message(p, "kolodion again slumps to the floor..motionless", "..but again his body begins to grow as he changes form", "...larger this time", "He becomes a vicious demon");
                                        spawnKolodion(p, NpcId.KOLODION_DEMON.id());
                                    } else
                                        if (n.getID() == NpcId.KOLODION_DEMON.id()) {
                                            Functions.___message(p, "kolodion again slumps to the floor..motionless", "..he slowly rises to his feet in his true form");
                                            Functions.___message(p, "@yel@Kolodion: \"well done young adventurer\"", "@yel@Kolodion: \"you truly are a worthy battle mage\"");
                                            p.message("kolodion teleports you to his cave");
                                            p.teleport(446, 3370);
                                            Npc kolodion = Functions.getNearestNpc(p, NpcId.KOLODION.id(), 5);
                                            if (kolodion == null) {
                                                p.message("kolodion is currently busy");
                                                return null;
                                            }
                                            Functions.___playerTalk(p, kolodion, "what now kolodion? how can i learn some of those spells?");
                                            Functions.___npcTalk(p, kolodion, "these spells are gifts from the gods", "first you must choose which god...", "...you will represent in the mage arena");
                                            Functions.___playerTalk(p, kolodion, "cool");
                                            Functions.___npcTalk(p, kolodion, "step into the magic pool, it will carry you to the chamber");
                                            Functions.___playerTalk(p, kolodion, "the chamber?");
                                            Functions.___npcTalk(p, kolodion, "there you must decide your loyalty");
                                            Functions.___playerTalk(p, kolodion, "ok kolodion , thanks for the battle");
                                            Functions.___npcTalk(p, kolodion, "remember young mage, you must use the spells...", "...many times in the arena before you can use them outside");
                                            Functions.___playerTalk(p, kolodion, "no problem");
                                            p.getCache().set("mage_arena", 2);
                                            p.getCache().remove("kolodion_stage");
                                        }




                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockPlayerMageNpc(final Player p, final Npc n) {
        if (Functions.inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(), NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
            if (!n.getAttribute("spawnedFor", null).equals(p)) {
                p.message("that mage is busy.");
                return true;
            }
        }
        return false;
    }

    public void godSpellObject(Mob affectedMob, int spell) {
        switch (spell) {
            case 33 :
                GameObject guthix = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1142, 0, 0);
                affectedMob.getWorld().registerGameObject(guthix);
                affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), guthix, 2));
                break;
            case 34 :
                GameObject sara = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1031, 0, 0);
                affectedMob.getWorld().registerGameObject(sara);
                affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), sara, 2));
                break;
            case 35 :
                GameObject zammy = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1036, 0, 0);
                affectedMob.getWorld().registerGameObject(zammy);
                affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), zammy, 2));
                break;
            case 47 :
                GameObject charge = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1147, 0, 0);
                affectedMob.getWorld().registerGameObject(charge);
                affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), charge, 2));
                break;
        }
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player player) {
        return (((((obj.getID() == 1019) || (obj.getID() == 1020)) || (obj.getID() == 1027)) || (obj.getID() == MageArena.SARADOMIN_STONE)) || (obj.getID() == MageArena.GUTHIX_STONE)) || (obj.getID() == MageArena.ZAMORAK_STONE);
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    boolean firstTimeEnchant = false;
                    if ((obj.getID() == 1019) || (obj.getID() == 1020)) {
                        player.message("you open the gate ...");
                        player.message("... and walk through");
                        Functions.___doGate(player, obj);
                        if (player.getCache().hasKey("mage_arena") && (player.getCache().getInt("mage_arena") == 4)) {
                            learnSpellEvent(player);
                        }
                    } else
                        if (obj.getID() == 1027) {
                            if (player.getY() >= 120) {
                                player.message("you pass through the mystical barrier");
                                Functions.movePlayer(player, 228, 118);
                                Npc kolodion = player.getAttribute("spawned_kolodion", null);
                                if (kolodion != null) {
                                    kolodion.remove();
                                }
                            } else {
                                if (player.getCache().hasKey("mage_arena") && (player.getCache().getInt("mage_arena") >= 4)) {
                                    Functions.___message(player, "the barrier is checking your person for weapons");
                                    if (!cantGo(player)) {
                                        Functions.movePlayer(player, 228, 120);
                                    } else {
                                        Functions.___message(player, "You cannot enter the arena...", "...while carrying weapons or armour");
                                    }
                                } else {
                                    player.message("you cannot enter without the permission of kolodion");
                                }
                            }
                        } else
                            if (obj.getID() == MageArena.SARADOMIN_STONE) {
                                if (player.getCache().hasKey("mage_arena") && (player.getCache().getInt("mage_arena") >= 3)) {
                                    Functions.___message(player, "you kneel and chant to saradomin");
                                    if (!alreadyHasCape(player)) {
                                        Functions.___message(player, "you feel a rush of energy charge through your veins", "...and a cape appears before you");
                                        Functions.addItem(player, ItemId.SARADOMIN_CAPE.id(), 1);
                                    } else {
                                        Functions.___message(player, "but there is no response");
                                    }
                                } else// first time

                                    if (player.getCache().hasKey("mage_arena") && (player.getCache().getInt("mage_arena") == 2)) {
                                        Functions.___message(player, "you kneel and begin to chant to saradomin", "you feel a rush of energy charge through your veins");
                                        ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
                                        Functions.addItem(player, ItemId.SARADOMIN_CAPE.id(), 1);
                                        player.getCache().set("mage_arena", 3);
                                        firstTimeEnchant = true;
                                    }

                            } else
                                if (obj.getID() == MageArena.GUTHIX_STONE) {
                                    if (player.getCache().hasKey("mage_arena") && (player.getCache().getInt("mage_arena") >= 3)) {
                                        Functions.___message(player, "you kneel and chant to guthix");
                                        if (!alreadyHasCape(player)) {
                                            Functions.___message(player, "you feel a rush of energy charge through your veins", "...and a cape appears before you");
                                            Functions.addItem(player, ItemId.GUTHIX_CAPE.id(), 1);
                                        } else {
                                            Functions.___message(player, "but there is no response");
                                        }
                                    } else// first time

                                        if (player.getCache().hasKey("mage_arena") && (player.getCache().getInt("mage_arena") == 2)) {
                                            Functions.___message(player, "you kneel and begin to chant to guthix", "you feel a rush of energy charge through your veins");
                                            ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
                                            Functions.addItem(player, ItemId.GUTHIX_CAPE.id(), 1);
                                            player.getCache().set("mage_arena", 3);
                                            firstTimeEnchant = true;
                                        }

                                } else
                                    if (obj.getID() == MageArena.ZAMORAK_STONE) {
                                        if (player.getCache().hasKey("mage_arena") && (player.getCache().getInt("mage_arena") >= 3)) {
                                            Functions.___message(player, "you kneel and chant to zamorak");
                                            if (!alreadyHasCape(player)) {
                                                Functions.___message(player, "you feel a rush of energy charge through your veins", "...and a cape appears before you");
                                                Functions.addItem(player, ItemId.ZAMORAK_CAPE.id(), 1);
                                            } else {
                                                Functions.___message(player, "but there is no response");
                                            }
                                        } else// first time

                                            if (player.getCache().hasKey("mage_arena") && (player.getCache().getInt("mage_arena") == 2)) {
                                                Functions.___message(player, "you kneel and begin to chant to zamorak", "you feel a rush of energy charge through your veins");
                                                ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
                                                Functions.addItem(player, ItemId.ZAMORAK_CAPE.id(), 1);
                                                player.getCache().set("mage_arena", 3);
                                                firstTimeEnchant = true;
                                            }

                                    }




                    if (firstTimeEnchant) {
                        player.sendMiniGameComplete(getMiniGameId(), Optional.empty());
                    }
                    return null;
                });
            }
        };
    }

    private boolean alreadyHasCape(Player player) {
        for (Item item : player.getInventory().getItems()) {
            if (((item.getID() == ItemId.ZAMORAK_CAPE.id()) || (item.getID() == ItemId.SARADOMIN_CAPE.id())) || (item.getID() == ItemId.GUTHIX_CAPE.id())) {
                return true;
            }
        }
        for (Item item : player.getBank().getItems()) {
            if (((item.getID() == ItemId.ZAMORAK_CAPE.id()) || (item.getID() == ItemId.SARADOMIN_CAPE.id())) || (item.getID() == ItemId.GUTHIX_CAPE.id())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean blockPlayerAttackNpc(Player p, Npc n) {
        if (Functions.inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(), NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
            if (!n.getAttribute("spawnedFor", null).equals(p)) {
                p.message("that mage is busy.");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean blockPlayerDeath(Player p) {
        if (p.getAttribute("spawned_kolodion", null) != null) {
            p.setAttribute("spawned_kolodion", null);
        }
        return false;
    }

    @Override
    public boolean blockPickup(Player p, GroundItem i) {
        return ((i.getID() == ItemId.ZAMORAK_CAPE.id()) || (i.getID() == ItemId.SARADOMIN_CAPE.id())) || (i.getID() == ItemId.GUTHIX_CAPE.id());
    }

    @Override
    public GameStateEvent onPickup(Player p, GroundItem i) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (((i.getID() == ItemId.ZAMORAK_CAPE.id()) || (i.getID() == ItemId.SARADOMIN_CAPE.id())) || (i.getID() == ItemId.GUTHIX_CAPE.id())) {
                        if (alreadyHasCape(p)) {
                            p.message("you may only possess one sacred cape at a time");
                        } else {
                            Item Item = new Item(i.getID(), i.getAmount());
                            p.getWorld().unregisterItem(i);
                            p.playSound("takeobject");
                            p.getInventory().add(Item);
                        }
                    }
                    return null;
                });
            }
        };
    }
}

