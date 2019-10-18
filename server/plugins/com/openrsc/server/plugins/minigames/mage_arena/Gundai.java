package com.openrsc.server.plugins.minigames.mage_arena;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.util.rsc.DataConversions;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Gundai implements NpcCommandListener , TalkToNpcListener , NpcCommandExecutiveListener , TalkToNpcExecutiveListener {
    private static final Logger LOGGER = LogManager.getLogger(Gundai.class);

    @Override
    public GameStateEvent onTalkToNpc(final Player player, final Npc n) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___playerTalk(player, n, "hello, what are you doing out here?");
                    Functions.___npcTalk(player, n, "why i'm a banker, the only one around these dangerous parts");
                    Menu defaultMenu = new Menu();
                    defaultMenu.addOption(new Option("cool, I'd like to access my bank account please") {
                        @Override
                        public void action() {
                            player.setAccessingBank(true);
                            if (player.getWorld().getServer().getConfig().WANT_BANK_PINS) {
                                if (player.getCache().hasKey("bank_pin") && (!player.getAttribute("bankpin", false))) {
                                    String pin = Functions.___getBankPinInput(player);
                                    if (pin == null) {
                                        return;
                                    }
                                    try {
                                        PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                                        statement.setString(1, player.getUsername());
                                        ResultSet result = statement.executeQuery();
                                        if (result.next()) {
                                            pin = DataConversions.hashPassword(pin, result.getString("salt"));
                                        }
                                    } catch (SQLException e) {
                                        Gundai.LOGGER.catching(e);
                                    }
                                    if (!player.getCache().getString("bank_pin").equals(pin)) {
                                        ActionSender.sendBox(player, "Incorrect bank pin", false);
                                        return;
                                    }
                                    player.setAttribute("bankpin", true);
                                    ActionSender.sendBox(player, "Bank pin correct", false);
                                }
                            }
                            Functions.___npcTalk(player, n, "no problem");
                            ActionSender.showBank(player);
                        }
                    });
                    if (player.getWorld().getServer().getConfig().WANT_BANK_PINS) {
                        defaultMenu.addOption(new Option("I'd like to talk about bank pin") {
                            @Override
                            public void action() {
                                int menu = Functions.___showMenu(player, "Set a bank pin", "Change bank pin", "Delete bank pin");
                                if (menu == 0) {
                                    if (!player.getCache().hasKey("bank_pin")) {
                                        String bankPin = Functions.___getBankPinInput(player);
                                        if (bankPin == null) {
                                            return;
                                        }
                                        try {
                                            PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                                            statement.setString(1, player.getUsername());
                                            ResultSet result = statement.executeQuery();
                                            if (result.next()) {
                                                bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
                                                player.getCache().store("bank_pin", bankPin);
                                                // ActionSender.sendBox(p, "Your new bank pin is " + bankPin, false);
                                            }
                                        } catch (SQLException e) {
                                            Gundai.LOGGER.catching(e);
                                        }
                                    } else {
                                        ActionSender.sendBox(player, "You already have a bank pin", false);
                                    }
                                } else
                                    if (menu == 1) {
                                        if (player.getCache().hasKey("bank_pin")) {
                                            String bankPin = Functions.___getBankPinInput(player);
                                            if (bankPin == null) {
                                                return;
                                            }
                                            try {
                                                PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                                                statement.setString(1, player.getUsername());
                                                ResultSet result = statement.executeQuery();
                                                if (result.next()) {
                                                    bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
                                                }
                                            } catch (SQLException e) {
                                                Gundai.LOGGER.catching(e);
                                            }
                                            if (!player.getCache().getString("bank_pin").equals(bankPin)) {
                                                ActionSender.sendBox(player, "Incorrect bank pin", false);
                                                return;
                                            }
                                            String changeTo = Functions.___getBankPinInput(player);
                                            try {
                                                PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                                                statement.setString(1, player.getUsername());
                                                ResultSet result = statement.executeQuery();
                                                if (result.next()) {
                                                    changeTo = DataConversions.hashPassword(changeTo, result.getString("salt"));
                                                }
                                            } catch (SQLException e) {
                                                Gundai.LOGGER.catching(e);
                                            }
                                            player.getCache().store("bank_pin", changeTo);
                                            // ActionSender.sendBox(p, "Your new bank pin is " + bankPin, false);
                                        } else {
                                            player.message("You don't have a bank pin");
                                        }
                                    } else
                                        if (menu == 2) {
                                            if (player.getCache().hasKey("bank_pin")) {
                                                String bankPin = Functions.___getBankPinInput(player);
                                                if (bankPin == null) {
                                                    return;
                                                }
                                                if (!player.getCache().getString("bank_pin").equals(bankPin)) {
                                                    ActionSender.sendBox(player, "Incorrect bank pin", false);
                                                    return;
                                                }
                                                player.getCache().remove("bank_pin");
                                                ActionSender.sendBox(player, "Your bank pin is removed", false);
                                            } else {
                                                player.message("You don't have a bank pin");
                                            }
                                        }


                            }
                        });
                    }
                    if (player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
                        defaultMenu.addOption(new Option("I'd like to collect my items from auction") {
                            @Override
                            public void action() {
                                if (player.getWorld().getServer().getConfig().WANT_BANK_PINS) {
                                    if (player.getCache().hasKey("bank_pin") && (!player.getAttribute("bankpin", false))) {
                                        String pin = Functions.___getBankPinInput(player);
                                        if (pin == null) {
                                            return;
                                        }
                                        try {
                                            PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                                            statement.setString(1, player.getUsername());
                                            ResultSet result = statement.executeQuery();
                                            if (result.next()) {
                                                pin = DataConversions.hashPassword(pin, result.getString("salt"));
                                            }
                                        } catch (SQLException e) {
                                            Gundai.LOGGER.catching(e);
                                        }
                                        if (!player.getCache().getString("bank_pin").equals(pin)) {
                                            ActionSender.sendBox(player, "Incorrect bank pin", false);
                                            return;
                                        }
                                        player.setAttribute("bankpin", true);
                                        ActionSender.sendBox(player, "Bank pin correct", false);
                                    }
                                }
                                player.getWorld().getMarket().addPlayerCollectItemsTask(player);
                            }
                        });
                    }
                    defaultMenu.addOption(new Option("Well, now i know") {
                        @Override
                        public void action() {
                            Functions.___npcTalk(player, n, "knowledge is power my friend");
                        }
                    });
                    defaultMenu.showMenu(player);
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockTalkToNpc(Player p, Npc n) {
        return n.getID() == NpcId.GUNDAI.id();
    }

    @Override
    public GameStateEvent onNpcCommand(Npc n, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == NpcId.GUNDAI.id()) {
                        if (command.equalsIgnoreCase("Bank")) {
                            quickFeature(n, p, false);
                        } else
                            if (p.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS && command.equalsIgnoreCase("Collect")) {
                                quickFeature(n, p, true);
                            }

                    }
                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockNpcCommand(Npc n, String command, Player p) {
        if ((n.getID() == NpcId.GUNDAI.id()) && command.equalsIgnoreCase("Bank")) {
            return true;
        }
        if (((n.getID() == NpcId.GUNDAI.id()) && p.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) && command.equalsIgnoreCase("Collect")) {
            return true;
        }
        return false;
    }

    private void quickFeature(Npc npc, Player player, boolean auction) {
        if (player.getWorld().getServer().getConfig().WANT_BANK_PINS) {
            if (player.getCache().hasKey("bank_pin") && (!player.getAttribute("bankpin", false))) {
                String pin = Functions.___getBankPinInput(player);
                if (pin == null) {
                    return;
                }
                try {
                    PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                    statement.setString(1, player.getUsername());
                    ResultSet result = statement.executeQuery();
                    if (result.next()) {
                        pin = DataConversions.hashPassword(pin, result.getString("salt"));
                    }
                } catch (SQLException e) {
                    Gundai.LOGGER.catching(e);
                }
                if (!player.getCache().getString("bank_pin").equals(pin)) {
                    ActionSender.sendBox(player, "Incorrect bank pin", false);
                    return;
                }
                player.setAttribute("bankpin", true);
                ActionSender.sendBox(player, "Bank pin correct", false);
            }
        }
        if (player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS && auction) {
            player.getWorld().getMarket().addPlayerCollectItemsTask(player);
        } else {
            player.setAccessingBank(true);
            ActionSender.showBank(player);
        }
    }
}

