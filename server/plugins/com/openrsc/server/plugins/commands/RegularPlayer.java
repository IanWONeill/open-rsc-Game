package com.openrsc.server.plugins.commands;


import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.content.party.PartyRank;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.plugins.quests.free.ShieldOfArrav;
import com.openrsc.server.sql.query.logs.ChatLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class RegularPlayer implements CommandListener {
    private static final Logger LOGGER = LogManager.getLogger(RegularPlayer.class);

    public static String messagePrefix = null;

    public static String badSyntaxPrefix = null;

    public GameStateEvent onCommand(String cmd, String[] args, Player player) {
        if (isCommandAllowed(player, cmd)) {
            if (RegularPlayer.messagePrefix == null) {
                RegularPlayer.messagePrefix = player.getWorld().getServer().getConfig().MESSAGE_PREFIX;
            }
            if (RegularPlayer.badSyntaxPrefix == null) {
                RegularPlayer.badSyntaxPrefix = player.getWorld().getServer().getConfig().BAD_SYNTAX_PREFIX;
            }
            return handleCommand(cmd, args, player);
        }
        return null;
    }

    public boolean isCommandAllowed(Player player, String cmd) {
        return player.getWorld().getServer().getConfig().PLAYER_COMMANDS || player.isMod();
    }

    public GameStateEvent handleCommand(String cmd, String[] args, Player player) {
        return new GameStateEvent(player.getWorld(), player, 0, (getClass().getSimpleName() + " ") + Thread.currentThread().getStackTrace()[1].getMethodName()) {
            public void init() {
                addState(0, () -> {
                    if (cmd.equalsIgnoreCase("gang")) {
                        if (ShieldOfArrav.isBlackArmGang(player)) {
                            player.message(RegularPlayer.messagePrefix + "You are a member of the Black Arm Gang");
                        } else
                            if (ShieldOfArrav.isPhoenixGang(player)) {
                                player.message(RegularPlayer.messagePrefix + "You are a member of the Phoenix Gang");
                            } else {
                                player.message(RegularPlayer.messagePrefix + "You are not in a gang - you need to start the shield of arrav quest");
                            }

                    } else
                        if (cmd.equalsIgnoreCase("wilderness")) {
                            int TOTAL_PLAYERS_IN_WILDERNESS = 0;
                            int PLAYERS_IN_F2P_WILD = 0;
                            int PLAYERS_IN_P2P_WILD = 0;
                            int EDGE_DUNGEON = 0;
                            for (Player p : player.getWorld().getPlayers()) {
                                if (p.getLocation().inWilderness()) {
                                    TOTAL_PLAYERS_IN_WILDERNESS++;
                                }
                                if (p.getLocation().inFreeWild() && (!p.getLocation().inBounds(195, 3206, 234, 3258))) {
                                    PLAYERS_IN_F2P_WILD++;
                                }
                                if ((p.getLocation().wildernessLevel() >= 48) && (p.getLocation().wildernessLevel() <= 56)) {
                                    PLAYERS_IN_P2P_WILD++;
                                }
                                if (p.getLocation().inBounds(195, 3206, 234, 3258)) {
                                    EDGE_DUNGEON++;
                                }
                            }
                            ActionSender.sendBox(player, (((((((((((((((((("There are currently @red@" + TOTAL_PLAYERS_IN_WILDERNESS) + " @whi@player") + (TOTAL_PLAYERS_IN_WILDERNESS == 1 ? "" : "s")) + " in wilderness % %") + "F2P wilderness(Wild Lvl. 1-48) : @dre@") + PLAYERS_IN_F2P_WILD) + "@whi@ player") + (PLAYERS_IN_F2P_WILD == 1 ? "" : "s")) + " %") + "P2P wilderness(Wild Lvl. 48-56) : @dre@") + PLAYERS_IN_P2P_WILD) + "@whi@ player") + (PLAYERS_IN_P2P_WILD == 1 ? "" : "s")) + " %") + "Edge dungeon wilderness(Wild Lvl. 1-9) : @dre@") + EDGE_DUNGEON) + "@whi@ player") + (EDGE_DUNGEON == 1 ? "" : "s")) + " %", false);
                        } else
                            if (cmd.equalsIgnoreCase("c") && player.getWorld().getServer().getConfig().WANT_CLANS) {
                                if (player.getClan() == null) {
                                    player.message(RegularPlayer.messagePrefix + "You are not in a clan.");
                                    return null;
                                }
                                String message = "";
                                for (String arg : args) {
                                    message = (message + arg) + " ";
                                }
                                player.getClan().messageChat(player, (("@cya@" + player.getStaffName()) + ":@whi@ ") + message);
                            } else
                                if (cmd.equalsIgnoreCase("clanaccept") && player.getWorld().getServer().getConfig().WANT_CLANS) {
                                    if (player.getActiveClanInvite() == null) {
                                        player.message(RegularPlayer.messagePrefix + "You have not been invited to a clan.");
                                        return null;
                                    }
                                    player.getActiveClanInvite().accept();
                                    player.message((RegularPlayer.messagePrefix + "You have joined clan ") + player.getClan().getClanName());
                                } else
                                    if (cmd.equalsIgnoreCase("partyaccept")) {
                                        if (player.getActivePartyInvite() == null) {
                                            // player.message(messagePrefix + "You have not been invited to a party.");
                                            return null;
                                        }
                                        player.getActivePartyInvite().accept();
                                        player.message(RegularPlayer.messagePrefix + "You have joined the party");
                                    } else
                                        if (cmd.equalsIgnoreCase("claninvite") && player.getWorld().getServer().getConfig().WANT_CLANS) {
                                            if (args.length < 1) {
                                                player.message((RegularPlayer.badSyntaxPrefix + cmd.toUpperCase()) + " [name]");
                                                return null;
                                            }
                                            long invitePlayer = DataConversions.usernameToHash(args[0]);
                                            Player invited = player.getWorld().getPlayer(invitePlayer);
                                            if (!player.getClan().isAllowed(1, player)) {
                                                player.message((RegularPlayer.messagePrefix + "You are not allowed to invite into clan ") + player.getClan().getClanName());
                                                return null;
                                            }
                                            if (invited == null) {
                                                player.message(RegularPlayer.messagePrefix + "Invalid name or player is not online");
                                                return null;
                                            }
                                            ClanInvite.createClanInvite(player, invited);
                                            player.message(((RegularPlayer.messagePrefix + invited.getUsername()) + " has been invited into clan ") + player.getClan().getClanName());
                                        } else
                                            if (cmd.equalsIgnoreCase("clankick") && player.getWorld().getServer().getConfig().WANT_CLANS) {
                                                if (args.length < 1) {
                                                    player.message((RegularPlayer.badSyntaxPrefix + cmd.toUpperCase()) + " [name]");
                                                    return null;
                                                }
                                                if (player.getClan() == null) {
                                                    player.message(RegularPlayer.messagePrefix + "You are not in a clan.");
                                                    return null;
                                                }
                                                String playerToKick = args[0].replace("_", " ");
                                                long kickedHash = DataConversions.usernameToHash(args[0]);
                                                Player kicked = player.getWorld().getPlayer(kickedHash);
                                                if (!player.getClan().isAllowed(3, player)) {
                                                    player.message(RegularPlayer.messagePrefix + "You are not allowed to kick that player.");
                                                    return null;
                                                }
                                                if (player.getClan().getLeader().getUsername().equals(playerToKick)) {
                                                    player.message(RegularPlayer.messagePrefix + "You can't kick the leader.");
                                                    return null;
                                                }
                                                player.getClan().removePlayer(playerToKick);
                                                player.message(((RegularPlayer.messagePrefix + playerToKick) + " has been kicked from clan ") + player.getClan().getClanName());
                                                if (kicked != null)
                                                    kicked.message((RegularPlayer.messagePrefix + "You have been kicked from clan ") + player.getClan().getClanName());

                                            } else
                                                if (cmd.equalsIgnoreCase("gameinfo")) {
                                                    player.updateTotalPlayed();
                                                    long timePlayed = player.getCache().getLong("total_played");
                                                    ActionSender.sendBox(player, ((((("@lre@Player Information: %" + (" %" + "@gre@Coordinates:@whi@ ")) + player.getLocation().toString()) + " %") + "@gre@Total Time Played:@whi@ ") + DataConversions.getDateFromMsec(timePlayed)) + " %", true);
                                                } else
                                                    if (cmd.equalsIgnoreCase("event")) {
                                                        if (!player.getWorld().EVENT) {
                                                            player.message(RegularPlayer.messagePrefix + "There is no event running at the moment");
                                                            return null;
                                                        }
                                                        if (player.getLocation().inWilderness()) {
                                                            player.message(RegularPlayer.messagePrefix + "Please move out of wilderness first");
                                                            return null;
                                                        } else
                                                            if (player.isJailed()) {
                                                                player.message(RegularPlayer.messagePrefix + "You can't participate in events while you are jailed.");
                                                                return null;
                                                            }

                                                        if ((player.getCombatLevel() > player.getWorld().EVENT_COMBAT_MAX) || (player.getCombatLevel() < player.getWorld().EVENT_COMBAT_MIN)) {
                                                            player.message((((RegularPlayer.messagePrefix + "This event is only for combat level range: ") + player.getWorld().EVENT_COMBAT_MIN) + " - ") + player.getWorld().EVENT_COMBAT_MAX);
                                                            return null;
                                                        }
                                                        player.teleport(player.getWorld().EVENT_X, player.getWorld().EVENT_Y);
                                                    } else
                                                        if (cmd.equalsIgnoreCase("g") || cmd.equalsIgnoreCase("pk")) {
                                                            if (!player.getWorld().getServer().getConfig().WANT_GLOBAL_CHAT)
                                                                return null;

                                                            if (player.isMuted()) {
                                                                player.message(RegularPlayer.messagePrefix + "You are muted, you cannot send messages");
                                                                return null;
                                                            }
                                                            if ((player.getCache().hasKey("global_mute") && (((player.getCache().getLong("global_mute") - System.currentTimeMillis()) > 0) || (player.getCache().getLong("global_mute") == (-1)))) && cmd.equals("g")) {
                                                                long globalMuteDelay = player.getCache().getLong("global_mute");
                                                                player.message(((RegularPlayer.messagePrefix + "You are ") + (globalMuteDelay == (-1) ? "permanently muted" : ("temporary muted for " + ((int) (((player.getCache().getLong("global_mute") - System.currentTimeMillis()) / 1000) / 60))) + " minutes")) + " from the ::g chat.");
                                                                return null;
                                                            }
                                                            long sayDelay = 0;
                                                            if (player.getCache().hasKey("say_delay")) {
                                                                sayDelay = player.getCache().getLong("say_delay");
                                                            }
                                                            long waitTime = 15000;
                                                            if (player.isMod()) {
                                                                waitTime = 0;
                                                            }
                                                            if ((System.currentTimeMillis() - sayDelay) < waitTime) {
                                                                player.message(((RegularPlayer.messagePrefix + "You can only use this command every ") + (waitTime / 1000)) + " seconds");
                                                                return null;
                                                            }
                                                            if (player.getLocation().onTutorialIsland() && (!player.isMod())) {
                                                                return null;
                                                            }
                                                            player.getCache().store("say_delay", System.currentTimeMillis());
                                                            StringBuilder newStr = new StringBuilder();
                                                            for (String arg : args) {
                                                                newStr.append(arg).append(" ");
                                                            }
                                                            newStr = new StringBuilder(newStr.toString().replace('~', ' '));
                                                            newStr = new StringBuilder(newStr.toString().replace('@', ' '));
                                                            String channelPrefix = (cmd.equals("g")) ? "@gr2@[General] " : "@or1@[PKing] ";
                                                            int channel = (cmd.equalsIgnoreCase("g")) ? 1 : 2;
                                                            for (Player p : player.getWorld().getPlayers()) {
                                                                if (p.getSocial().isIgnoring(player.getUsernameHash()))
                                                                    continue;

                                                                if ((p.getGlobalBlock() == 3) && (channel == 2)) {
                                                                    continue;
                                                                }
                                                                if ((p.getGlobalBlock() == 4) && (channel == 1)) {
                                                                    continue;
                                                                }
                                                                if (p.getGlobalBlock() != 2) {
                                                                    String header = "";
                                                                    ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, ((((((channelPrefix + "@whi@") + (player.getClan() != null ? ("@cla@<" + player.getClan().getClanTag()) + "> @whi@" : "")) + header) + player.getStaffName()) + ": ") + (channel == 1 ? "@gr2@" : "@or1@")) + newStr, player.getIcon());
                                                                }
                                                            }
                                                            if (cmd.equalsIgnoreCase("g")) {
                                                                player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(Global) " + newStr));
                                                                player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
                                                            } else {
                                                                player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(PKing) " + newStr));
                                                                player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(PKing) " + newStr));
                                                            }
                                                        } else
                                                            if (cmd.equalsIgnoreCase("p")) {
                                                                if (player.isMuted()) {
                                                                    player.message(RegularPlayer.messagePrefix + "You are muted, you cannot send messages");
                                                                    return null;
                                                                }
                                                                if ((player.getCache().hasKey("global_mute") && (((player.getCache().getLong("global_mute") - System.currentTimeMillis()) > 0) || (player.getCache().getLong("global_mute") == (-1)))) && cmd.equals("g")) {
                                                                    long globalMuteDelay = player.getCache().getLong("global_mute");
                                                                    player.message(((RegularPlayer.messagePrefix + "You are ") + (globalMuteDelay == (-1) ? "permanently muted" : ("temporary muted for " + ((int) (((player.getCache().getLong("global_mute") - System.currentTimeMillis()) / 1000) / 60))) + " minutes")) + " from the ::g chat.");
                                                                    return null;
                                                                }
                                                                long sayDelay = 0;
                                                                if (player.getCache().hasKey("say_delay")) {
                                                                    sayDelay = player.getCache().getLong("say_delay");
                                                                }
                                                                long waitTime = 1200;
                                                                if (player.isMod()) {
                                                                    waitTime = 0;
                                                                }
                                                                if ((System.currentTimeMillis() - sayDelay) < waitTime) {
                                                                    player.message(((RegularPlayer.messagePrefix + "You can only use this command every ") + (waitTime / 1000)) + " seconds");
                                                                    return null;
                                                                }
                                                                if (player.getLocation().onTutorialIsland() && (!player.isMod())) {
                                                                    return null;
                                                                }
                                                                if (player.getParty() == null) {
                                                                    return null;
                                                                }
                                                                player.getCache().store("say_delay", System.currentTimeMillis());
                                                                StringBuilder newStr = new StringBuilder();
                                                                for (String arg : args) {
                                                                    newStr.append(arg).append(" ");
                                                                }
                                                                newStr = new StringBuilder(newStr.toString().replace('~', ' '));
                                                                newStr = new StringBuilder(newStr.toString().replace('@', ' '));
                                                                String channelPrefix = "@whi@[@or1@Party@whi@] ";
                                                                int channel = (cmd.equalsIgnoreCase("p")) ? 1 : 2;
                                                                for (Player p : player.getWorld().getPlayers()) {
                                                                    if (p.getSocial().isIgnoring(player.getUsernameHash()))
                                                                        continue;

                                                                    if (p.getParty() == player.getParty()) {
                                                                        ActionSender.sendMessage(p, player, 1, MessageType.CLAN_CHAT, (((channelPrefix + "") + player.getUsername()) + ": @or1@") + newStr, player.getIcon());
                                                                    }
                                                                }
                                                                if (cmd.equalsIgnoreCase("g")) {
                                                                    player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(Global) " + newStr));
                                                                    player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
                                                                } else {
                                                                    player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(PKing) " + newStr));
                                                                    player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(PKing) " + newStr));
                                                                }
                                                            } else
                                                                if (cmd.equalsIgnoreCase("online")) {
                                                                    int players = ((int) (player.getWorld().getPlayers().size()));
                                                                    for (Player p : player.getWorld().getPlayers()) {
                                                                        if (p.isMod() && p.getSettings().getPrivacySetting(1)) {
                                                                            players--;
                                                                        }
                                                                    }
                                                                    player.message((RegularPlayer.messagePrefix + "Players Online: ") + players);
                                                                } else
                                                                    if (cmd.equalsIgnoreCase("uniqueonline")) {
                                                                        ArrayList<String> IP_ADDRESSES = new ArrayList<>();
                                                                        for (Player p : player.getWorld().getPlayers()) {
                                                                            if (!IP_ADDRESSES.contains(p.getCurrentIP()))
                                                                                IP_ADDRESSES.add(p.getCurrentIP());

                                                                        }
                                                                        player.message(((RegularPlayer.messagePrefix + "There are ") + IP_ADDRESSES.size()) + " unique players online");
                                                                    } else
                                                                        if (cmd.equalsIgnoreCase("leaveparty")) {
                                                                            player.getParty().removePlayer(player.getUsername());
                                                                        } else
                                                                            if (cmd.equalsIgnoreCase("shareloot")) {
                                                                                if (player.getParty().getPlayer(player.getUsername()).getRank().equals(PartyRank.LEADER)) {
                                                                                    for (PartyPlayer m : player.getParty().getPlayers()) {
                                                                                        if (m.getShareLoot() > 0) {
                                                                                            m.setShareLoot(0);
                                                                                            m.getPlayerReference().message("@whi@[@blu@Party@whi@] - @whi@Loot Sharing has been @red@Disabled");
                                                                                            ActionSender.sendParty(m.getPlayerReference());
                                                                                        } else {
                                                                                            m.setShareLoot(1);
                                                                                            ActionSender.sendParty(m.getPlayerReference());
                                                                                            m.getPlayerReference().message("@whi@[@blu@Party@whi@] - @whi@Loot Sharing has been @gre@Enabled");
                                                                                        }
                                                                                    }
                                                                                }
                                                                            } else
                                                                                if (cmd.equalsIgnoreCase("shareexp")) {
                                                                                    if (player.getParty().getPlayer(player.getUsername()).getRank().equals(PartyRank.LEADER)) {
                                                                                        for (PartyPlayer m : player.getParty().getPlayers()) {
                                                                                            if (m.getShareExp() > 0) {
                                                                                                m.setShareExp(0);
                                                                                                m.getPlayerReference().message("@whi@[@blu@Party@whi@] - @whi@Exp Sharing has been @red@Disabled");
                                                                                                ActionSender.sendParty(m.getPlayerReference());
                                                                                            } else {
                                                                                                m.setShareExp(1);
                                                                                                ActionSender.sendParty(m.getPlayerReference());
                                                                                                m.getPlayerReference().message("@whi@[@blu@Party@whi@] - @whi@Exp Sharing has been @gre@Enabled");
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                } else
                                                                                    if (cmd.equals("onlinelist")) {
                                                                                        // modern onlinelist display using ActionSender.SendOnlineList()
                                                                                        ActionSender.sendOnlineList(player);
                                                                                        /* } else if (cmd.equalsIgnoreCase("onlinelist")) { // this is the old onlinelist display using ActionSender.sendBox()
                                                                                        int players = player.getWorld().getPlayers().size();
                                                                                        for (Player p : player.getWorld().getPlayers()) {
                                                                                        if (p.isMod() && p.getSettings().getPrivacySetting(1)) {
                                                                                        players--;
                                                                                        }
                                                                                        }
                                                                                        StringBuilder boxTextPlayerNames = new StringBuilder();
                                                                                        for (Player p : player.getWorld().getPlayers()) {
                                                                                        boxTextPlayerNames
                                                                                        .append(Group.getNameSprite(p.getGroupID()) + Group.getNameColour(p.getGroupID())) // displays group color for player username
                                                                                        .append(p.getUsername()) // displays player username
                                                                                        .append(player.getCombatLevel() > p.getCombatLevel() ? " @whi@(@gre@" : "") // less than combat level is green
                                                                                        .append(player.getCombatLevel() == p.getCombatLevel() ? " @whi@(@whi@" : "") // equal to combat level is white
                                                                                        .append(player.getCombatLevel() < p.getCombatLevel() ? " @whi@(@yel@" : "") // greater than combat level is yellow
                                                                                        .append("level-" + p.getCombatLevel() + "@whi@)") // displays the player's combat level
                                                                                        .append(player.isDev() ? (p.getLocation()) : "") // states player coordinates for staff to see
                                                                                        .append(players > 1 ? ("  ") : ""); // adds a double space between player names if there are more than one online
                                                                                        }
                                                                                        ActionSender.sendBox(player, "" + "@yel@Online Players: %" + boxTextPlayerNames, true);
                                                                                         */
                                                                                    } else
                                                                                        if (cmd.equalsIgnoreCase("groups") || cmd.equalsIgnoreCase("ranks")) {
                                                                                            ArrayList<String> groups = new ArrayList<>();
                                                                                            for (Map.Entry<Integer, String> entry : Group.GROUP_NAMES.entrySet()) {
                                                                                                groups.add((Group.getStaffPrefix(player.getWorld(), entry.getKey()) + entry.getValue()) + (player.isDev() ? (" (" + entry.getKey()) + ")" : ""));
                                                                                            }
                                                                                            ActionSender.sendBox(player, "@whi@Server Groups:%" + StringUtils.join(groups, "%"), true);
                                                                                        } else
                                                                                            if ((cmd.equalsIgnoreCase("time") || cmd.equalsIgnoreCase("date")) || cmd.equalsIgnoreCase("datetime")) {
                                                                                                player.message((RegularPlayer.messagePrefix + " the current time/date is:@gre@ ") + new Date().toString());
                                                                                            } else
                                                                                                if (player.getWorld().getServer().getConfig().NPC_KILL_LIST && cmd.equalsIgnoreCase("kills")) {
                                                                                                    StringBuilder kills = new StringBuilder(("NPC Kill List for " + player.getUsername()) + " % %");
                                                                                                    // PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(
                                                                                                    // "SELECT * FROM `" + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "npckills` WHERE playerID = ? ORDER BY killCount DESC LIMIT 16");
                                                                                                    // statement.setInt(1, player.getDatabaseID());
                                                                                                    // ResultSet result = statement.executeQuery();
                                                                                                    for (Map.Entry<Integer, Integer> entry : player.getKillCache().entrySet()) {
                                                                                                        kills.append("NPC: ").append(player.getWorld().getServer().getEntityHandler().getNpcDef(entry.getKey()).getName()).append(" - Kill Count: ").append(entry.getValue()).append("%");
                                                                                                    }
                                                                                                    ActionSender.sendBox(player, kills.toString(), true);
                                                                                                } else
                                                                                                    if (cmd.equalsIgnoreCase("commands")) {
                                                                                                        ActionSender.sendBox(player, "" + (((((((((((((((("@yel@Commands available: %" + "Type :: before you enter your command, see the list below. % %") + "@whi@::gameinfo - shows player and server information %") + "@whi@::online - shows players currently online %") + "@whi@::uniqueonline - shows number of unique IPs logged in %") + "@whi@::onlinelist - shows players currently online in a list %") + "@whi@::g <message> - to talk in @gr1@general @whi@global chat channel %") + "@whi@::p <message> - to talk in @or1@pking @whi@global chat channel %") + "@whi@::c <message> - talk in clan chat %") + "@whi@::claninvite <name> - invite player to clan %") + "@whi@::clankick <name> - kick player from clan %") + "@whi@::clanaccept - accept clan invitation %") + "@whi@::gang - shows if you are 'Pheonix' or 'Black arm' gang %") + "@whi@::groups - shows available ranks on the server %") + "@whi@::wilderness - shows the wilderness activity %") + "@whi@::time - shows the current server time %") + "@whi@::event - to enter an ongoing server event %"), true);
                                                                                                    }




















                    return null;
                });
            }
        };
    }
}

