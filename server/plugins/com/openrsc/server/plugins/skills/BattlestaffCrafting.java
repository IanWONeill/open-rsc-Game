package com.openrsc.server.plugins.skills;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;
import java.util.concurrent.Callable;


public class BattlestaffCrafting implements InvUseOnItemListener , InvUseOnItemExecutiveListener {
    private boolean canCraft(Item itemOne, Item itemTwo) {
        for (BattlestaffCrafting.Battlestaff c : BattlestaffCrafting.Battlestaff.values()) {
            if (c.isValid(itemOne.getID(), itemTwo.getID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GameStateEvent onInvUseOnItem(Player p, Item item1, Item item2) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    BattlestaffCrafting.Battlestaff combine = null;
                    for (BattlestaffCrafting.Battlestaff c : BattlestaffCrafting.Battlestaff.values()) {
                        if (c.isValid(item1.getID(), item2.getID())) {
                            combine = c;
                        }
                    }
                    if (p.getSkills().getLevel(Skills.CRAFTING) < combine.requiredLevel) {
                        p.playerServerMessage(MessageType.QUEST, (("You need a crafting level of " + combine.requiredLevel) + " to make ") + resultItemString(combine));
                        return null;
                    }
                    if (Functions.removeItem(p, combine.itemID, 1) && Functions.removeItem(p, combine.itemIDOther, 1)) {
                        if (combine.messages.length > 1)
                            Functions.___message(p, combine.messages[0]);
                        else
                            p.message(combine.messages[0]);

                        Functions.addItem(p, combine.resultItem, 1);
                        p.incExp(Skills.CRAFTING, combine.experience, true);
                        if (combine.messages.length > 1)
                            p.message(combine.messages[1]);

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
        return canCraft(item1, item2);
    }

    private String resultItemString(BattlestaffCrafting.Battlestaff combinedItem) {
        String name;
        switch (combinedItem) {
            case WATER_BATTLESTAFF :
                name = "a water battlestaff";
                break;
            case EARTH_BATTLESTAFF :
                // kosher: didn't say "an earth"
                name = "a earth battlestaff";
                break;
            case FIRE_BATTLESTAFF :
                name = "a fire battlestaff";
                break;
            case AIR_BATTLESTAFF :
                name = "an air battlestaff";
                break;
            default :
                // unimplemented battlestaff or not known
                name = "this";
        }
        return name;
    }

    enum Battlestaff {

        WATER_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.WATER_ORB.id(), ItemId.BATTLESTAFF_OF_WATER.id(), 400, 54, ""),
        EARTH_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.EARTH_ORB.id(), ItemId.BATTLESTAFF_OF_EARTH.id(), 450, 58, ""),
        FIRE_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.FIRE_ORB.id(), ItemId.BATTLESTAFF_OF_FIRE.id(), 500, 62, ""),
        AIR_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.AIR_ORB.id(), ItemId.BATTLESTAFF_OF_AIR.id(), 550, 66, "");
        private int itemID;

        private int itemIDOther;

        private int resultItem;

        private int experience;

        private int requiredLevel;

        private String[] messages;

        Battlestaff(int itemOne, int itemTwo, int resultItem, int experience, int level, String... messages) {
            this.itemID = itemOne;
            this.itemIDOther = itemTwo;
            this.resultItem = resultItem;
            this.experience = experience;
            this.requiredLevel = level;
            this.messages = messages;
        }

        public boolean isValid(int i, int is) {
            return ((itemID == i) && (itemIDOther == is)) || ((itemIDOther == i) && (itemID == is));
        }
    }
}

