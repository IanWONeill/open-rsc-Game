package com.openrsc.server.plugins.npcs.varrock;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
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
import com.openrsc.server.util.rsc.DataConversions;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Auctioneers implements NpcCommandListener , TalkToNpcListener , NpcCommandExecutiveListener , TalkToNpcExecutiveListener {
    private static final Logger LOGGER = LogManager.getLogger(Auctioneers.class);

    public static int AUCTIONEER = NpcId.AUCTIONEER.id();

    public static int AUCTION_CLERK = NpcId.AUCTION_CLERK.id();

    @Override
    public boolean blockTalkToNpc(final Player player, final Npc npc) {
        if (npc.getID() == Auctioneers.AUCTIONEER) {
            return true;
        }
        if (npc.getID() == Auctioneers.AUCTION_CLERK) {
            return true;
        }
        return false;
    }

    @Override
    public GameStateEvent onTalkToNpc(Player player, final Npc npc) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    Functions.___npcTalk(player, npc, "Hello");
                    int menu;
                    if (npc.getID() == Auctioneers.AUCTION_CLERK) {
                        menu = Functions.___showMenu(player, npc, "I'd like to browse the auction house", "Can you teleport me to Varrock Centre");
                    } else {
                        menu = Functions.___showMenu(player, npc, "I'd like to browse the auction house");
                    }
                    if (menu == 0) {
                        if ((player.isIronMan(1) || player.isIronMan(2)) || player.isIronMan(3)) {
                            player.message("As an Iron Man, you cannot use the Auction.");
                            return null;
                        }
                        if (player.getWorld().getServer().getConfig().WANT_BANK_PINS) {
                            if (player.getCache().hasKey("bank_pin") && (!player.getAttribute("bankpin", false))) {
                                String pin = Functions.___getBankPinInput(player);
                                if (pin == null) {
                                    return null;
                                }
                                try {
                                    PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                                    statement.setString(1, player.getUsername());
                                    ResultSet result = statement.executeQuery();
                                    if (result.next()) {
                                        pin = DataConversions.hashPassword(pin, result.getString("salt"));
                                    }
                                } catch (SQLException e) {
                                    Auctioneers.LOGGER.catching(e);
                                }
                                if (!player.getCache().getString("bank_pin").equals(pin)) {
                                    ActionSender.sendBox(player, "Incorrect bank pin", false);
                                    return null;
                                }
                                player.setAttribute("bankpin", true);
                                ActionSender.sendBox(player, "Bank pin correct", false);
                            }
                        }
                        Functions.___npcTalk(player, npc, "Certainly " + (player.isMale() ? "Sir" : "Miss"));
                        player.setAttribute("auctionhouse", true);
                        ActionSender.sendOpenAuctionHouse(player);
                    } else
                        if (menu == 1) {
                            Functions.___npcTalk(player, npc, "Yes of course " + (player.isMale() ? "Sir" : "Miss"), "the costs is 1,000 coins");
                            int tMenu = Functions.___showMenu(player, npc, "Teleport me", "I'll stay here");
                            if (tMenu == 0) {
                                if (Functions.hasItem(player, ItemId.COINS.id(), 1000)) {
                                    Functions.removeItem(player, ItemId.COINS.id(), 1000);
                                    player.teleport(133, 508);
                                } else {
                                    player.message("You don't seem to have enough coins");
                                }
                            }
                        }

                    return null;
                });
            }
        };
    }

    @Override
    public boolean blockNpcCommand(Npc n, String command, Player p) {
        if ((n.getID() == Auctioneers.AUCTIONEER) && command.equalsIgnoreCase("Auction")) {
            return true;
        }
        if ((n.getID() == Auctioneers.AUCTION_CLERK) && (command.equalsIgnoreCase("Teleport") || command.equalsIgnoreCase("Auction"))) {
            return true;
        }
        return false;
    }

    @Override
    public GameStateEvent onNpcCommand(Npc n, String command, Player p) {
        return new GameStateEvent(p.getWorld(), p, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (n.getID() == Auctioneers.AUCTIONEER) {
                        if (command.equalsIgnoreCase("Auction")) {
                            if ((p.isIronMan(1) || p.isIronMan(2)) || p.isIronMan(3)) {
                                p.message("As an Iron Man, you cannot use the Auction.");
                                return null;
                            }
                            if (p.getWorld().getServer().getConfig().WANT_BANK_PINS) {
                                if (p.getCache().hasKey("bank_pin") && (!p.getAttribute("bankpin", false))) {
                                    String pin = Functions.___getBankPinInput(p);
                                    if (pin == null) {
                                        return null;
                                    }
                                    try {
                                        PreparedStatement statement = p.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + p.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                                        statement.setString(1, p.getUsername());
                                        ResultSet result = statement.executeQuery();
                                        if (result.next()) {
                                            pin = DataConversions.hashPassword(pin, result.getString("salt"));
                                        }
                                    } catch (SQLException e) {
                                        Auctioneers.LOGGER.catching(e);
                                    }
                                    if (!p.getCache().getString("bank_pin").equals(pin)) {
                                        ActionSender.sendBox(p, "Incorrect bank pin", false);
                                        return null;
                                    }
                                    p.setAttribute("bankpin", true);
                                    ActionSender.sendBox(p, "Bank pin correct", false);
                                }
                            }
                            p.message(("Welcome to the auction house " + (p.isMale() ? "Sir" : "Miss")) + "!");
                            p.setAttribute("auctionhouse", true);
                            ActionSender.sendOpenAuctionHouse(p);
                        }
                    } else
                        if (n.getID() == Auctioneers.AUCTION_CLERK) {
                            if (command.equalsIgnoreCase("Auction")) {
                                if ((p.isIronMan(1) || p.isIronMan(2)) || p.isIronMan(3)) {
                                    p.message("As an Iron Man, you cannot use the Auction.");
                                    return null;
                                }
                                if (p.getWorld().getServer().getConfig().WANT_BANK_PINS) {
                                    if (p.getCache().hasKey("bank_pin") && (!p.getAttribute("bankpin", false))) {
                                        String pin = Functions.___getBankPinInput(p);
                                        if (pin == null) {
                                            return null;
                                        }
                                        try {
                                            PreparedStatement statement = p.getWorld().getServer().getDatabaseConnection().prepareStatement(("SELECT salt FROM " + p.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX) + "players WHERE `username`=?");
                                            statement.setString(1, p.getUsername());
                                            ResultSet result = statement.executeQuery();
                                            if (result.next()) {
                                                pin = DataConversions.hashPassword(pin, result.getString("salt"));
                                            }
                                        } catch (SQLException e) {
                                            Auctioneers.LOGGER.catching(e);
                                        }
                                        if (!p.getCache().getString("bank_pin").equals(pin)) {
                                            ActionSender.sendBox(p, "Incorrect bank pin", false);
                                            return null;
                                        }
                                        p.setAttribute("bankpin", true);
                                        ActionSender.sendBox(p, "Bank pin correct", false);
                                    }
                                }
                                p.message(("Welcome to the auction house " + (p.isMale() ? "Sir" : "Miss")) + "!");
                                p.setAttribute("auctionhouse", true);
                                ActionSender.sendOpenAuctionHouse(p);
                            } else
                                if (command.equalsIgnoreCase("Teleport")) {
                                    n.face(p);
                                    p.face(n);
                                    Functions.___message(p, n, 1300, "Would you like to be teleport to Varrock centre for 1000 gold?");
                                    int yesOrNo = Functions.___showMenu(p, "Yes please!", "No thanks.");
                                    if (yesOrNo == 0) {
                                        if (Functions.hasItem(p, ItemId.COINS.id(), 1000)) {
                                            Functions.removeItem(p, ItemId.COINS.id(), 1000);
                                            p.teleport(133, 508);
                                            p.message("You have been teleported to the Varrock Centre");
                                        } else {
                                            p.message("You don't seem to have enough coins");
                                        }
                                    } else
                                        if (yesOrNo == 1) {
                                            p.message("You decide to stay where you are located.");
                                        }

                                }

                        }

                    return null;
                });
            }
        };
    }
}

