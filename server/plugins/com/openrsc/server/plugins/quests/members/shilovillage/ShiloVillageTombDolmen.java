package com.openrsc.server.plugins.quests.members.shilovillage;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import java.util.concurrent.Callable;


public class ShiloVillageTombDolmen implements QuestInterface , InvUseOnObjectListener , ObjectActionListener , InvUseOnObjectExecutiveListener , ObjectActionExecutiveListener {
    private static final int TOMB_DOLMEN = 689;

    @Override
    public int getQuestId() {
        return Quests.SHILO_VILLAGE;
    }

    @Override
    public String getQuestName() {
        return "Shilo village (members)";
    }

    @Override
    public boolean isMembers() {
        return true;
    }

    @Override
    public void handleReward(Player player) {
        /* REMOVE CACHES AFTER QUEST COMPLETE THAT NO LONGER IS USED */
        if (player.getCache().hasKey("obtained_shilo_info")) {
            player.getCache().remove("obtained_shilo_info");
        }
        if (player.getCache().hasKey("coins_shilo_cave")) {
            player.getCache().remove("coins_shilo_cave");
        }
        if (player.getCache().hasKey("can_chisel_bone")) {
            player.getCache().remove("can_chisel_bone");
        }
        if (player.getCache().hasKey("tomb_door_shilo")) {
            player.getCache().remove("tomb_door_shilo");
        }
        if (player.getCache().hasKey("SV_DIG_LIT")) {
            player.getCache().remove("SV_DIG_LIT");
        }
        if (player.getCache().hasKey("SV_DIG_ROPE")) {
            player.getCache().remove("SV_DIG_ROPE");
        }
        if (player.getCache().hasKey("SV_DIG_BUMP")) {
            player.getCache().remove("SV_DIG_BUMP");
        }
        if (player.getCache().hasKey("dolmen_zombie")) {
            player.getCache().remove("dolmen_zombie");
        }
        if (player.getCache().hasKey("dolmen_skeleton")) {
            player.getCache().remove("dolmen_skeleton");
        }
        if (player.getCache().hasKey("dolmen_ghost")) {
            player.getCache().remove("dolmen_ghost");
        }
        player.message("Well Done!");
        player.message("You have completed the Shilo Village Quest.");
        player.message("You gain some experience in crafting.");
        player.message("@gre@You haved gained 2 quest points!");
        Functions.incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.SHILO_VILLAGE), true);
    }

    @Override
    public boolean blockObjectAction(GameObject obj, String command, Player p) {
        return obj.getID() == ShiloVillageTombDolmen.TOMB_DOLMEN;
    }

    @Override
    public GameStateEvent onObjectAction(GameObject obj, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == ShiloVillageTombDolmen.TOMB_DOLMEN) {
                        if (p.getQuestStage(Quests.SHILO_VILLAGE) == (-1)) {
                            p.message("You find nothing on the Dolmen.");
                            return null;
                        }
                        if (command.equalsIgnoreCase("Look")) {
                            Functions.___message(p, "The Dolmen is intricately decorated with the family", "symbol of two crossed palm trees .");
                            if (p.getQuestStage(Quests.SHILO_VILLAGE) == 8) {
                                p.message("There is nothing on the Dolmen.");
                                return null;
                            }
                            if (Functions.hasItem(p, ItemId.SWORD_POMMEL.id()) && Functions.hasItem(p, ItemId.LOCATING_CRYSTAL.id())) {
                                p.message("There is nothing on the Dolmen.");
                            } else
                                if (Functions.hasItem(p, ItemId.SWORD_POMMEL.id()) || Functions.hasItem(p, ItemId.LOCATING_CRYSTAL.id())) {
                                    p.message("You can see an item on the Dolmen");
                                } else {
                                    p.message("You can see that there are some items on the Dolmen.");
                                }

                        } else
                            if (command.equalsIgnoreCase("Search")) {
                                Functions.___message(p, "The Dolmen is intricately decorated with the symbol of");
                                Functions.___message(p, "two crossed palm trees. It might be the family crest?");
                                if (Functions.hasItem(p, ItemId.SWORD_POMMEL.id()) && Functions.hasItem(p, ItemId.LOCATING_CRYSTAL.id())) {
                                    Functions.___message(p, "There is nothing on the Dolmen.");
                                } else
                                    if (Functions.hasItem(p, ItemId.SWORD_POMMEL.id()) || Functions.hasItem(p, ItemId.LOCATING_CRYSTAL.id())) {
                                        Functions.___message(p, "You can see an item on the Dolmen");
                                    } else {
                                        Functions.___message(p, "You can see that there are some items on the Dolmen.");
                                    }

                                if ((!Functions.hasItem(p, ItemId.SWORD_POMMEL.id())) && (!Functions.hasItem(p, ItemId.BONE_BEADS.id()))) {
                                    // SWORD POMMEL
                                    Functions.___message(p, "You find a rusty sword with an ivory pommel.");
                                    p.message("You take the pommel and place it into your inventory.");
                                    Functions.addItem(p, ItemId.SWORD_POMMEL.id(), 1);
                                    Functions.sleep(500);
                                }
                                if (!Functions.hasItem(p, ItemId.LOCATING_CRYSTAL.id())) {
                                    // crystal
                                    Functions.___message(p, "You find a Crystal Sphere ");
                                    Functions.addItem(p, ItemId.LOCATING_CRYSTAL.id(), 1);
                                }
                                Functions.___message(p, "You find some writing on the dolmen,");
                                if ((!Functions.hasItem(p, ItemId.BERVIRIUS_TOMB_NOTES.id())) && p.getCache().hasKey("dropped_writing")) {
                                    Functions.___message(p, "You would need some Papyrus and Charcoal");
                                    p.message("to take more notes from this Dolmen!");
                                } else
                                    if ((!Functions.hasItem(p, ItemId.BERVIRIUS_TOMB_NOTES.id())) && (!p.getCache().hasKey("dropped_writing"))) {
                                        Functions.___message(p, "you grab some nearby scraps of delicate paper together ", "and copy the text as best you can and collect");
                                        p.message("them together as a scroll");
                                        Functions.addItem(p, ItemId.BERVIRIUS_TOMB_NOTES.id(), 1);
                                    }

                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
        return obj.getID() == ShiloVillageTombDolmen.TOMB_DOLMEN;
    }

    @Override
    public GameStateEvent onInvUseOnObject(GameObject obj, Item item, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (obj.getID() == ShiloVillageTombDolmen.TOMB_DOLMEN) {
                        switch (ItemId.getById(item.getID())) {
                            case PAPYRUS :
                                if (Functions.hasItem(p, ItemId.BERVIRIUS_TOMB_NOTES.id())) {
                                    p.message("You already have Bervirius Tomb Notes in your inventory.");
                                } else {
                                    Functions.___message(p, "You try to take some new notes on the delicate papyrus.");
                                    if (!Functions.hasItem(p, ItemId.A_LUMP_OF_CHARCOAL.id())) {
                                        p.message("You need some charcoal to make notes.");
                                        return null;
                                    }
                                    Functions.___message(p, "You use the charcoal and the Papyrus to make some new notes.");
                                    p.message("You collect the notes together as a scroll.");
                                    Functions.removeItem(p, ItemId.PAPYRUS.id(), 1);
                                    Functions.removeItem(p, ItemId.A_LUMP_OF_CHARCOAL.id(), 1);
                                    Functions.addItem(p, ItemId.BERVIRIUS_TOMB_NOTES.id(), 1);
                                }
                                break;
                            case RASHILIYA_CORPSE :
                                // COMPLETE QUEST - RASHILIYIA CORPSE
                                if (p.getQuestStage(Quests.SHILO_VILLAGE) == 8) {
                                    p.setBusy(true);
                                    p.message("You carefully place Rashiliyia's remains on the Dolmen.");
                                    Functions.sleep(1200);
                                    p.message("You feel a strange vibration in the air.");
                                    Npc rash = Functions.spawnNpc(p.getWorld(), NpcId.RASHILIYIA.id(), p.getX(), p.getY(), 60000);
                                    if (rash != null) {
                                        rash.teleport(rash.getX() + 1, rash.getY());
                                        Functions.___npcTalk(p, rash, "You have my gratitude for releasing my spirit.", "I have suffered a vengeful and evil existence.", "I was tricked by Zamorak. He returned my son to me as an undead Creature.", "My hatred and bitterness corrupted me.", "I tried too destroy all life...now I am released.", "And am grateful to contemplate eternal rest...");
                                        Functions.___message(p, "Without warning the spirit of Rashiliyia disapears.");
                                        rash.remove();
                                    }
                                    Functions.removeItem(p, ItemId.RASHILIYA_CORPSE.id(), 1);
                                    p.sendQuestComplete(Quests.SHILO_VILLAGE);
                                    p.setBusy(false);
                                }
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
}

