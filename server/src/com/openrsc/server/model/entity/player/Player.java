package com.openrsc.server.model.entity.player;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.achievement.Achievement;
import com.openrsc.server.content.clan.Clan;
import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler;
import com.openrsc.server.content.party.Party;
import com.openrsc.server.content.party.PartyInvite;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.event.rsc.impl.*;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.model.*;
import com.openrsc.server.model.action.WalkToAction;
import com.openrsc.server.model.container.Bank;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.net.rsc.PacketHandlerLookup;
import com.openrsc.server.net.rsc.handlers.ItemDropHandler;
import com.openrsc.server.net.rsc.handlers.Ping;
import com.openrsc.server.net.rsc.handlers.WalkRequest;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.sql.query.logs.GenericLog;
import com.openrsc.server.sql.query.logs.LiveFeedLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.plugins.Functions.sleep;

/**
 * A single player.
 */
public final class Player extends Mob {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	public final int MAX_FATIGUE = 75000;
	public final String MEMBER_MESSAGE = "This feature is only available for members only";
	/**
	 * Players cache is used to store various objects into database
	 */
	private final Cache cache = new Cache();
	private final Map<Integer, Integer> killCache = new HashMap<>();

	private boolean killCacheUpdated = false;
	/**
	 * Received packets from this player yet to be processed.
	 */
	private final LinkedHashMap<Integer, Packet> incomingPackets = new LinkedHashMap<Integer, Packet>();
	private final Object incomingPacketLock = new Object();
	/**
	 * Outgoing packets from this player yet to be processed.
	 */
	private final ArrayList<Packet> outgoingPackets = new ArrayList<Packet>();
	private final Object outgoingPacketsLock = new Object();
	private final Map<Integer, Integer> questStages = new ConcurrentHashMap<>();
	private int IRON_MAN_MODE = 0;
	private int IRON_MAN_RESTRICTION = 1;
	private int IRON_MAN_HC_DEATH = 0;
	public int lastMineTry = -1;
	public int click = -1;
	/**
	 * Added by Zerratar: Correct sleepword we are looking for! Case SenSitIvE
	 */
	private String correctSleepword = "";
	/**
	 * The last menu reply this player gave in a quest
	 */
	public long lastCast = System.currentTimeMillis();
	private FireCannonEvent cannonEvent = null;
	private long consumeTimer = 0;
	private long lastSaveTime = System.currentTimeMillis();
	private int appearanceID;
	private HashMap<Long, Integer> knownPlayersAppearanceIDs = new HashMap<Long, Integer>();
	private long lastCommand;
	private LinkedHashSet<Player> localPlayers = new LinkedHashSet<Player>();
	private LinkedHashSet<Npc> localNpcs = new LinkedHashSet<Npc>();
	private LinkedHashSet<GameObject> localObjects = new LinkedHashSet<GameObject>();
	private LinkedHashSet<GameObject> localWallObjects = new LinkedHashSet<GameObject>();
	private LinkedHashSet<GroundItem> localGroundItems = new LinkedHashSet<GroundItem>();
	private ArrayDeque<Point> locationsToClear = new ArrayDeque<Point>();
	/**
	 * Prayers
	 */
	private Prayers prayers;
	/**
	 * Bank for banked items
	 */
	private Bank bank;
	private BatchEvent batchEvent = null;
	/**
	 * Controls if were allowed to accept appearance updates
	 */
	private boolean changingAppearance = false;
	/**
	 * Combat style: 0 - all, 1 - str, 2 - att, 3 - def
	 */
	private int combatStyle = 0;
	private String currentIP = "0.0.0.0";
	/**
	 * Unix time when the player logged in
	 */
	private long currentLogin = 0;
	/**
	 * DelayedEvent responsible for handling prayer drains
	 */
	private PrayerDrainEvent prayerDrainEvent;
	/**
	 * The drain rate of the prayers currently enabled
	 */
	private int drainRate = 0;
	/**
	 * Amount of fatigue - 0 to 75000
	 */
	private int fatigue = 0, sleepStateFatigue = 0;
	/**
	 * The main accounts group is
	 */
	private int groupID = Group.DEFAULT_GROUP;
	/**
	 * Is the player accessing their bank?
	 */
	private boolean inBank = false;
	private int incorrectSleepTries = 0;
	/**
	 * The npc we are currently interacting with
	 */
	private Npc interactingNpc = null;
	/**
	 * Atomic reference to the inventory, multiple threads use this instance and
	 * it is never changed during session.
	 */
	private AtomicReference<Inventory> inventory = new AtomicReference<Inventory>();

	private AtomicReference<Equipment> equipment = new AtomicReference<Equipment>();

	/**
	 * Channel
	 */
	private Channel channel;
	/**
	 * Time of antidote protection from poison
	 */
	private long lastAntidote = 0;
	/**
	 * Stores the last IP address used
	 */
	private String lastIP = "0.0.0.0";
	/**
	 * Unix time when the player last logged in
	 */
	private long lastLogin = 0;
	/**
	 * Unix time when the player last requested a change in recovery questions
	 */
	private long lastRecoveryChangeRequest = 0;
	/**
	 * Last time a 'ping' was received
	 */
	private long lastPing = System.currentTimeMillis();
	/**
	 * Time last report was sent, used to throttle reports
	 */
	private long lastReport = 0;
	/**
	 * The time of the last spell cast, used as a throttle
	 */
	private long lastSpellCast = 0;
	/**
	 * Time of last trade/duel request
	 */
	private long lastTradeDuelRequest = 0;
	/**
	 * Whether the player is currently logged in
	 */
	private boolean loggedIn = false;
	/**
	 * Is the character male?
	 */
	private boolean maleGender;
	/**
	 * The current active menu
	 */
	private Menu menu;
	/**
	 * A handler for any menu we are currently in
	 */
	private MenuOptionListener menuHandler = null;
	/**
	 * The ID of the owning account
	 */
	private int owner = 1;
	/**
	 * The player's password
	 */
	private String password;
	private int questionOption;
	/**
	 * Total quest points
	 */
	private int questPoints = 0;
	/**
	 * Ranging event
	 */
	private RangeEvent rangeEvent;
	private ThrowingEvent throwingEvent;
	/**
	 * If the player is reconnecting after connection loss
	 */
	private boolean reconnecting = false;
	/**
	 * Is a trade/duel update required?
	 */
	private boolean requiresOfferUpdate = false;
	/**
	 * The shop (if any) the player is currently accessing
	 */
	private Shop shop = null;
	/**
	 * DelayedEvent used for removing players skull after 20mins
	 */
	private DelayedEvent skullEvent = null;
	private DelayedEvent chargeEvent = null;
	private DelayedEvent sleepEvent;
	private boolean sleeping = false;
	/**
	 * Player sleep word
	 */
	private String sleepword;
	/**
	 * The current status of the player
	 */
	private Action status = Action.IDLE;
	/**
	 * If the player has been sending suspicious packets
	 */
	private boolean suspiciousPlayer;
	/**
	 * The player's username
	 */
	private String username;
	/**
	 * The player's username hash
	 */
	private long usernameHash;
	/**
	 * The items being worn by the player
	 */
	private int[] wornItems = new int[12];
	/**
	 * Time when the player logged in, used to calculate the total play time.
	 */
	private long sessionStart;
	private PlayerSettings playerSettings;
	private Social social;
	private Duel duel;
	private DelayedEvent unregisterEvent;

	/**
	 * Restricts P2P stuff in F2P wilderness.
	 */
	/*public void unwieldMembersItems() {
		if (!getServer().getConfig().MEMBER_WORLD) {
			boolean found = false;
			for (Item i : getInventory().getItems()) {

				if (i.isWielded() && i.getDef().isMembersOnly()) {
					getInventory().unwieldItem(i, true);
					found = true;
				}
				if (i.getID() == 2109 && i.isWielded()) {
					getInventory().unwieldItem(i, true);
				}
			}
			if (found) {
				message("Members objects can not be wielded on this world.");

				ActionSender.sendInventory(this);
				ActionSender.sendEquipmentStats(this);
			}
			for (int i = 0; i < 3; i++) {
				int min = skills.getLevel(i);
				int max = skills.getMaxStat(i);
				int baseStat = min > max ? max : min;
				int newStat = baseStat + DataConversions.roundUp((max / 100D) * 10) + 2;
				if (min > newStat || (min > max && (i == 1 || i == 0))) {
					skills.setLevel(i, max);
				}
			}
		}
	}*/
	private int bankSize = 192; //Maximum bank items allowed
	private Queue<PrivateMessage> privateMessageQueue = new LinkedList<PrivateMessage>();
	private long lastSave = System.currentTimeMillis();
	private int actionsMouseStill = 0;
	private long lastMouseMoved = 0;
	private Map<Integer, Integer> achievements = new ConcurrentHashMap<>();
	// activity indicator for kitten to cat growth
	// 100 trigger up a Kitten to cat event
	// 1 walked step is +1 activity, 1 5-min warn to move is +25 activity (saved each 30 secs => 2.5 per save)
	// so everything is multiplied by 2 to avoid decimals
	private final int KITTEN_ACTIVITY_THRESHOLD = 50;
	private int activity = 0;

	/**
	 * KILLS N DEATHS
	 **/
	private int kills = 0;
	private int kills2 = 0;
	private int deaths = 0;
	private int npcDeaths = 0;
	private WalkToAction walkToAction;
	private Trade trade;
	private int databaseID;
	private Clan clan;
	private Party party;
	private ClanInvite activeClanInvitation;
	private PartyInvite activePartyInvitation;

	/**
	 * Constructs a new Player instance from LoginRequest
	 *
	 * @param request
	 */
	public Player(World world, LoginRequest request) {
		super(world);

		password = request.getPassword();
		usernameHash = DataConversions.usernameToHash(request.getUsername());
		username = DataConversions.hashToUsername(usernameHash);
		sessionStart = System.currentTimeMillis();

		channel = request.getChannel();

		currentIP = ((InetSocketAddress) request.getChannel().remoteAddress()).getAddress().getHostAddress();
		currentLogin = System.currentTimeMillis();

		setBusy(true);

		trade = new Trade(this);
		duel = new Duel(this);
		playerSettings = new PlayerSettings(this);
		social = new Social(this);
		prayers = new Prayers(this);
	}

	public int getIronMan() {
		return IRON_MAN_MODE;
	}

	public void setIronMan(int i) {
		this.IRON_MAN_MODE = i;
	}

	public int getIronManRestriction() {
		return IRON_MAN_RESTRICTION;
	}

	public void setIronManRestriction(int i) {
		this.IRON_MAN_RESTRICTION = i;
	}

	public int getHCIronmanDeath() {
		return IRON_MAN_HC_DEATH;
	}

	public void setHCIronmanDeath(int i) {
		this.IRON_MAN_HC_DEATH = i;
	}

	private void updateHCIronman(int int1) {
		this.IRON_MAN_MODE = int1;
		this.IRON_MAN_HC_DEATH = int1;
	}

	public boolean isIronMan(int mode) {
		if (mode == 1 && getIronMan() == 1) {
			return true;
		} else if (mode == 2 && getIronMan() == 2) {
			return true;
		} else if (mode == 3 && getIronMan() == 3) {
			return true;
		}
		return false;
	}

	public void resetCannonEvent() {
		if (cannonEvent != null) {
			cannonEvent.stop();
		}
		cannonEvent = null;
	}

	public boolean isCannonEventActive() {
		return cannonEvent != null;
	}

	public void setCannonEvent(FireCannonEvent event) {
		cannonEvent = event;
	}

	public boolean cantConsume() {
		return consumeTimer - System.currentTimeMillis() > 0;
	}

	public void setConsumeTimer(long l) {
		consumeTimer = System.currentTimeMillis() + (l * getWorld().getServer().getConfig().GAME_TICK);
	}

	public long getLastSaveTime() {
		return lastSaveTime;
	}

	public void setLastSaveTime(long save) {
		lastSaveTime = save;
	}

	private int getAppearanceID() {
		return appearanceID;
	}

	public void incAppearanceID() {
		appearanceID++;
	}

	public long getLastCommand() {
		return lastCommand;
	}

	public void setLastCommand(long newTime) {
		this.lastCommand = newTime;
	}

	public boolean requiresAppearanceUpdateFor(Player p) {
		for (Entry<Long, Integer> entry : knownPlayersAppearanceIDs.entrySet()) {
			if (entry.getKey() == p.getUsernameHash()) {
				if (entry.getValue() != p.getAppearanceID()) {
					knownPlayersAppearanceIDs.put(p.getUsernameHash(), p.getAppearanceID());
					return true;
				} else {
					return false;
				}
			}
		}
		knownPlayersAppearanceIDs.put(p.getUsernameHash(), p.getAppearanceID());
		return true;
	}

	public boolean requiresAppearanceUpdateForPeek(Player p) {
		for (Entry<Long, Integer> entry : knownPlayersAppearanceIDs.entrySet()) {
			if (entry.getKey() == p.getUsernameHash()) {
				if (entry.getValue() != p.getAppearanceID()) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public HashMap<Long, Integer> getKnownPlayerAppearanceIDs() {
		return knownPlayersAppearanceIDs;
	}

	public void write(Packet o) {
		if (channel != null && channel.isOpen() && isLoggedIn()) {
			synchronized (outgoingPacketsLock) {
				outgoingPackets.add(o);
			}
		}
	}

	public LinkedHashSet<Npc> getLocalNpcs() {
		return localNpcs;
	}

	public LinkedHashSet<GameObject> getLocalWallObjects() {
		return localWallObjects;
	}

	public LinkedHashSet<GameObject> getLocalGameObjects() {
		return localObjects;
	}

	public LinkedHashSet<Player> getLocalPlayers() {
		return localPlayers;
	}

	public LinkedHashSet<GroundItem> getLocalGroundItems() {
		return localGroundItems;
	}

	public ArrayDeque<Point> getLocationsToClear() {
		return locationsToClear;
	}

	public boolean accessingBank() {
		return inBank;
	}

	//private int unreadMessages, teleportStones;

	public boolean accessingShop() {
		return shop != null;
	}

	public PrivateMessage getNextPrivateMessage() {
		return privateMessageQueue.poll();
	}

	public void addSkull(int timeLeft) {
		if (skullEvent == null) {
			skullEvent = new DelayedEvent(getWorld(), this, timeLeft, "Player Add Skull") {

				@Override
				public void run() {
					removeSkull();
					if (getWorld().getServer().getConfig().WANT_PARTIES) {
						if(getParty() != null){
							getParty().sendParty();
						}
					}
				}
			};
			getWorld().getServer().getGameEventHandler().add(skullEvent);
			getUpdateFlags().setAppearanceChanged(true);
		}
		if (getWorld().getServer().getConfig().WANT_PARTIES) {
			if(getParty() != null){
				getParty().sendParty();
			}
		}
	}

	private void removeCharge() {
		if (chargeEvent == null) {
			return;
		}
		chargeEvent.stop();
		chargeEvent = null;
	}

	public void addCharge(int timeLeft) {
		if (chargeEvent == null) {
			chargeEvent = new DelayedEvent(getWorld(), this, timeLeft, "Charge Spell Removal") {
				// 6 minutes taken from RS2.
				// the charge spell in RSC seem to be bugged, but 10 minutes most of the times.
				// sometimes you are charged for 1 hour lol.
				@Override
				public void run() {
					removeCharge();
					getOwner().message("@red@Your magic charge fades");
				}
			};
			getWorld().getServer().getGameEventHandler().add(chargeEvent);
		}
	}

	public void close() {
		getChannel().close();
	}

	public boolean canLogout() {
		return !isBusy() && System.currentTimeMillis() - getCombatTimer() > 10000
			&& System.currentTimeMillis() - getAttribute("last_shot", (long) 0) > 10000;
	}

	public boolean canReport() {
		return System.currentTimeMillis() - lastReport > 60000;
	}

	public boolean castTimer() {
		return System.currentTimeMillis() - lastSpellCast > (getWorld().getServer().getConfig().GAME_TICK * 2);
	}

	public void checkAndInterruptBatchEvent() {
		if (batchEvent != null) {
			batchEvent.interrupt();
			batchEvent = null;
		}
	}

	public boolean checkAttack(Mob mob, boolean missile) {
		if (mob.isPlayer()) {
			Player victim = (Player) mob;
			if (getParty() != null && ((Player) mob).getParty() != null && getParty() == ((Player) mob).getParty()) {
				message("You can't attack your party members");
					return false;
			}
			if ((inCombat() && getDuel().isDuelActive()) && (victim.inCombat() && victim.getDuel().isDuelActive())) {
				Player opponent = (Player) getOpponent();
				if (opponent != null && victim.equals(opponent)) {
					return true;
				}
			}
			if (!missile) {
				if (System.currentTimeMillis() - mob.getCombatTimer() < (mob.getCombatState() == CombatState.RUNNING
					|| mob.getCombatState() == CombatState.WAITING ? 3000 : 500)) {
					return false;
				}
			}

			int myWildLvl = getLocation().wildernessLevel();
			int victimWildLvl = victim.getLocation().wildernessLevel();
			if (myWildLvl < 1 || victimWildLvl < 1) {
				message("You can't attack other players here. Move to the wilderness");
				return false;
			}
			int combDiff = Math.abs(getCombatLevel() - victim.getCombatLevel());
			if (combDiff > myWildLvl) {
				message("You can only attack players within " + (myWildLvl) + " levels of your own here");
				message("Move further into the wilderness for less restrictions");
				return false;
			}
			if (combDiff > victimWildLvl) {
				message("You can only attack players within " + (victimWildLvl) + " levels of your own here");
				message("Move further into the wilderness for less restrictions");
				return false;
			}

			if (victim.isInvulnerable(mob) || victim.isInvisible(mob)) {
				message("You are not allowed to attack that person");
				return false;
			}
			return true;
		} else if (mob.isNpc()) {
			Npc victim = (Npc) mob;
			if (!victim.getDef().isAttackable()) {
				setSuspiciousPlayer(true);
				return false;
			}
			return true;
		}
		return true;
	}

	@Override
	public void resetCombatEvent() {
		if (inCombat()) {
			super.resetCombatEvent();
		}
	}

	public int combatStyleToIndex() {
		if (getCombatStyle() == 1) {
			return 2;
		}
		if (getCombatStyle() == 2) {
			return 0;
		}
		if (getCombatStyle() == 3) {
			return 1;
		}
		return -1;
	}

	/**
	 * Unregisters this player instance from the server
	 *
	 * @param force  - if false wait until combat is over
	 * @param reason - reason why the player was unregistered.
	 */
	public void unregister(boolean force, final String reason) {
		if (unregistering) {
			return;
		}
		if (force || canLogout()) {
			updateTotalPlayed();
			getCache().store("last_spell_cast", lastSpellCast);
			LOGGER.info("Requesting unregistration for " + getUsername() + ": " + reason);
			unregistering = true;
		} else {
			if (unregisterEvent != null) {
				return;
			}
			final long startDestroy = System.currentTimeMillis();
			unregisterEvent = new DelayedEvent(getWorld(), this, 500, "Unregister Player") {
				@Override
				public void run() {
					if (getOwner().canLogout() || (!(getOwner().inCombat() && getOwner().getDuel().isDuelActive())
						&& System.currentTimeMillis() - startDestroy > 60000)) {
						getOwner().unregister(true, reason);
						running = false;
					}
					else {
						running = false;
					}
				}
			};
			getWorld().getServer().getGameEventHandler().add(unregisterEvent);
		}
	}

	public void updateTotalPlayed() {
		if (cache.hasKey("total_played")) {
			long oldTotal = cache.getLong("total_played");
			long sessionLength = oldTotal + (System.currentTimeMillis() - sessionStart);
			cache.store("total_played", sessionLength);
		} else {
			cache.store("total_played", System.currentTimeMillis() - sessionStart);
		}
		sessionStart = System.currentTimeMillis();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Player) {
			Player p = (Player) o;
			return usernameHash == p.getUsernameHash();
		}
		return false;
	}

	public void checkEquipment2() {
		for (int slot = 0; slot < Equipment.slots; slot++) {
			Item item = getEquipment().get(slot);
			if (item == null)
				continue;
			int requiredLevel = item.getDef(getWorld()).getRequiredLevel();
			int requiredSkillIndex = item.getDef(getWorld()).getRequiredSkillIndex();
			String itemLower = item.getDef(getWorld()).getName().toLowerCase();
			Optional<Integer> optionalLevel = Optional.empty();
			Optional<Integer> optionalSkillIndex = Optional.empty();
			boolean unWield = false;
			boolean bypass = !getWorld().getServer().getConfig().STRICT_CHECK_ALL &&
				(itemLower.startsWith("poisoned") &&
					((itemLower.endsWith("throwing dart") && !getWorld().getServer().getConfig().STRICT_PDART_CHECK) ||
						(itemLower.endsWith("throwing knife") && !getWorld().getServer().getConfig().STRICT_PKNIFE_CHECK) ||
						(itemLower.endsWith("spear") && !getWorld().getServer().getConfig().STRICT_PSPEAR_CHECK))
				);
			if (itemLower.endsWith("spear") || itemLower.endsWith("throwing knife")) {
				optionalLevel = Optional.of(requiredLevel <= 10 ? requiredLevel : requiredLevel + 5);
				optionalSkillIndex = Optional.of(com.openrsc.server.constants.Skills.AGILITY);
			}
			//staff of iban (usable)
			if (item.getID() == ItemId.STAFF_OF_IBAN.id()) {
				optionalLevel = Optional.of(requiredLevel);
				optionalSkillIndex = Optional.of(com.openrsc.server.constants.Skills.AGILITY);
			}
			//battlestaves (incl. enchanted version)
			if (itemLower.contains("battlestaff")) {
				optionalLevel = Optional.of(requiredLevel);
				optionalSkillIndex = Optional.of(com.openrsc.server.constants.Skills.AGILITY);
			}

			if (getSkills().getMaxStat(requiredSkillIndex) < requiredLevel) {
				if (!bypass) {
					message("You are not a high enough level to use this item");
					message("You need to have a " + getWorld().getServer().getConstants().getSkills().getSkillName(requiredSkillIndex) + " level of " + requiredLevel);
					unWield = true;
				}
			}
			if (optionalSkillIndex.isPresent() && getSkills().getMaxStat(optionalSkillIndex.get()) < optionalLevel.get()) {
				if (!bypass) {
					message("You are not a high enough level to use this item");
					message("You need to have a " + getWorld().getServer().getConstants().getSkills().getSkillName(optionalSkillIndex.get()) + " level of " + optionalLevel.get());
					unWield = true;
				}
			}

			if (unWield) {
				getInventory().unwieldItem(item, false);
				//check to make sure their item was actually unequipped.
				//it might not have if they have a full inventory.
				if (getEquipment().get(slot) != null) {
					ItemDropHandler doit = new ItemDropHandler();
					if (item.getDef(getWorld()).isStackable())
						doit.dropStackable(this, item, item.getAmount(), false);
					else
						doit.dropUnstackable(this, item, 1, false);
				}
			}

		}
		ActionSender.sendEquipmentStats(this);
	}

	public void checkEquipment() {
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			checkEquipment2();
			return;
		}
		ListIterator<Item> iterator = getInventory().iterator();
		for (int slot = 0; iterator.hasNext(); slot++) {
			Item item = iterator.next();
			if (item.isWielded()) {
				int requiredLevel = item.getDef(getWorld()).getRequiredLevel();
				int requiredSkillIndex = item.getDef(getWorld()).getRequiredSkillIndex();
				String itemLower = item.getDef(getWorld()).getName().toLowerCase();
				Optional<Integer> optionalLevel = Optional.empty();
				Optional<Integer> optionalSkillIndex = Optional.empty();
				boolean unWield = false;
				boolean bypass = !getWorld().getServer().getConfig().STRICT_CHECK_ALL &&
					(itemLower.startsWith("poisoned") &&
						((itemLower.endsWith("throwing dart") && !getWorld().getServer().getConfig().STRICT_PDART_CHECK) ||
							(itemLower.endsWith("throwing knife") && !getWorld().getServer().getConfig().STRICT_PKNIFE_CHECK) ||
							(itemLower.endsWith("spear") && !getWorld().getServer().getConfig().STRICT_PSPEAR_CHECK))
					);
				if (itemLower.endsWith("spear") || itemLower.endsWith("throwing knife")) {
					optionalLevel = Optional.of(requiredLevel <= 10 ? requiredLevel : requiredLevel + 5);
					optionalSkillIndex = Optional.of(com.openrsc.server.constants.Skills.ATTACK);
				}
				//staff of iban (usable)
				if (item.getID() == ItemId.STAFF_OF_IBAN.id()) {
					optionalLevel = Optional.of(requiredLevel);
					optionalSkillIndex = Optional.of(com.openrsc.server.constants.Skills.ATTACK);
				}
				//battlestaves (incl. enchanted version)
				if (itemLower.contains("battlestaff")) {
					optionalLevel = Optional.of(requiredLevel);
					optionalSkillIndex = Optional.of(com.openrsc.server.constants.Skills.ATTACK);
				}

				if (getSkills().getMaxStat(requiredSkillIndex) < requiredLevel) {
					if (!bypass) {
						message("You are not a high enough level to use this item");
						message("You need to have a " + getWorld().getServer().getConstants().getSkills().getSkillName(requiredSkillIndex) + " level of " + requiredLevel);
						unWield = true;
					}
				}
				if (optionalSkillIndex.isPresent() && getSkills().getMaxStat(optionalSkillIndex.get()) < optionalLevel.get()) {
					if (!bypass) {
						message("You are not a high enough level to use this item");
						message("You need to have a " + getWorld().getServer().getConstants().getSkills().getSkillName(optionalSkillIndex.get()) + " level of " + optionalLevel.get());
						unWield = true;
					}
				}

				if (unWield) {
					item.setWielded(false);
					updateWornItems(item.getDef(getWorld()).getWieldPosition(),
						getSettings().getAppearance().getSprite(item.getDef(getWorld()).getWieldPosition()),
						item.getDef(getWorld()).getWearableId(), false);
					ActionSender.sendInventoryUpdateItem(this, slot);
				}
			}
		}
		ActionSender.sendEquipmentStats(this);
	}

	public int getBankSize() {
		return bankSize;
	}

	public void setBankSize(int size) {
		this.bankSize = size;
	}

	public int getFreeBankSlots() {
		return bankSize - getBank().size();
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank b) {
		bank = b;
	}

	public Cache getCache() {
		return cache;
	}

	public Map<Integer, Integer> getKillCache() {
		return killCache;
	}

	public boolean getKillCacheUpdated() {
		return killCacheUpdated;
	}

	public void setKillCacheUpdated(boolean value) {
		killCacheUpdated = value;
	}

	public long getCastTimer() {
		return lastSpellCast;
	}

	public int getClick() {
		return click;
	}

	public void setClick(int click) {
		this.click = click;
	}

	@Override
	public int getCombatStyle() {
		return combatStyle;
	}

	public void setCombatStyle(int style) {
		combatStyle = style;
	}

	public String getCorrectSleepword() {
		return correctSleepword;
	}

	public void setCorrectSleepword(String correctSleepword) {
		this.correctSleepword = correctSleepword;
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public void setCurrentIP(String currentIP) {
		this.currentIP = currentIP;
	}

	public long getCurrentLogin() {
		return currentLogin;
	}

	public void setCurrentLogin(long currentLogin) {
		this.currentLogin = currentLogin;
	}

	public int getDaysSinceLastLogin() {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - lastLogin) / 86400);
	}

	public void setLastRecoveryChangeRequest(long l) {
		lastRecoveryChangeRequest = l;
	}

	public int getDaysSinceLastRecoveryChangeRequest() {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - lastRecoveryChangeRequest) / 86400);
	}

	public PrayerDrainEvent getDrainer() {
		return prayerDrainEvent;
	}

	public int getDrainRate() {
		return drainRate;
	}

	public void setDrainRate(int rate) {
		drainRate = rate;
	}

	public int getFatigue() {
		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			return fatigue;
		} else {
			return 0;
		}
	}

	public void setFatigue(int fatigue) {
		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			this.fatigue = fatigue;
			ActionSender.sendFatigue(this);
		} else {
			this.fatigue = 0;
		}
	}

	public int getIncorrectSleepTimes() {
		return incorrectSleepTries;
	}

	public Npc getInteractingNpc() {
		return interactingNpc;
	}

	public void setInteractingNpc(Npc interactingNpc) {
		this.interactingNpc = interactingNpc;
	}

	public Inventory getInventory() {
		return inventory.get();
	}

	public Equipment getEquipment() {
		return equipment.get();
	}


	public void setInventory(Inventory i) {
		inventory.set(i);
	}

	public void setEquipment(Equipment e) {
		equipment.set(e);
	}

	public String getLastIP() {
		return lastIP;
	}

	public void setLastIP(String ip) {
		lastIP = ip;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(long l) {
		lastLogin = l;
	}

	public long getLastPing() {
		return lastPing;
	}

	public int getMagicPoints() {
		int points = 1;
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			points = getEquipment().getMagic();
		} else {
			for (Item item : getInventory().getItems()) {
				if (item.isWielded()) {
					points += item.getDef(getWorld()).getMagicBonus();
				}
			}
		}
		return points < 1 ? 1 : points;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public MenuOptionListener getMenuHandler() {
		return menuHandler;
	}

	public void setMenuHandler(MenuOptionListener menuHandler) {
		menuHandler.setOwner(this);
		this.menuHandler = menuHandler;
	}

	public int getMinutesMuteLeft() {
		long now = System.currentTimeMillis();
		return (int) ((getMuteExpires() - now) / 60000);
	}

	public long getMuteExpires() {
		if (getCache().hasKey("mute_expires"))
			return getCache().getLong("mute_expires");
		else
			return 0;
	}

	public void setMuteExpires(long l) {
		getCache().store("mute_expires", l);
		getCache().store("global_mute", l);
	}

	public int getOption() {
		return questionOption;
	}

	public void setOption(int option) {
		this.questionOption = option;
	}

	public int getOwner() {
		return owner;
	}

	public String getPassword() {
		return password;
	}

	public int getPrayerPoints() {
		int points = 1;
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			points = getEquipment().getPrayer();
		} else {
			for (Item item : getInventory().getItems()) {
				if (item.isWielded()) {
					points += item.getDef(getWorld()).getPrayerBonus();
				}
			}
		}

		return points < 1 ? 1 : points;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	public int calculateQuestPoints() {
		int qps = 0;
		for (Map.Entry<Integer, int[]> quest : getWorld().getServer().getConstants().getQuests().questData.entrySet()) {
			Integer q = quest.getKey();
			int[] data = quest.getValue();
			if (this.getQuestStage(q) < 0) {
				qps += data[0];
			}
		}
		this.setQuestPoints(qps);
		return qps;
	}

	public int getQuestStage(int id) {
		if (getQuestStages().containsKey(id)) {
			return getQuestStages().get(id);
		}
		return 0;
	}

	public int getQuestStage(QuestInterface q) {
		if (getQuestStages().containsKey(q.getQuestId())) {
			return getQuestStages().get(q.getQuestId());
		}
		return 0;
	}

	public int getRangeEquip() {
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item item;
			for (int i = 0; i < Equipment.slots; i++) {
				item = getEquipment().get(i);
				if (item != null && (DataConversions.inArray(Formulae.bowIDs, item.getID())
					|| DataConversions.inArray(Formulae.xbowIDs, item.getID()))) {
					return item.getID();
				}
			}
		} else {
			for (Item item : getInventory().getItems()) {
				if (item.isWielded() && (DataConversions.inArray(Formulae.bowIDs, item.getID())
					|| DataConversions.inArray(Formulae.xbowIDs, item.getID()))) {
					return item.getID();
				}
			}
		}
		return -1;
	}

	public int getThrowingEquip() {
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item item;
			for (int i = 0; i < Equipment.slots; i++) {
				item = getEquipment().get(i);
				if (item != null && DataConversions.inArray(Formulae.throwingIDs, item.getID())) {
					return item.getID();
				}
			}
		} else {
			for (Item item : getInventory().getItems()) {
				if (item.isWielded() && (DataConversions.inArray(Formulae.throwingIDs, getEquippedWeaponID()) && item.getDef(getWorld()).getWieldPosition() == 4)) {
					return item.getID();
				}
			}
		}

		return -1;
	}

	public RangeEvent getRangeEvent() {
		return rangeEvent;
	}

	public void setRangeEvent(RangeEvent event) {
		if (rangeEvent != null) {
			rangeEvent.stop();
		}
		rangeEvent = event;
		setStatus(Action.RANGING_MOB);
		getWorld().getServer().getGameEventHandler().add(rangeEvent);
	}

	public ThrowingEvent getThrowingEvent() {
		return throwingEvent;
	}

	public void setThrowingEvent(ThrowingEvent event) {
		if (throwingEvent != null) {
			throwingEvent.stop();
		}
		throwingEvent = event;
		setStatus(Action.RANGING_MOB);
		getWorld().getServer().getGameEventHandler().add(throwingEvent);
	}

	public String getStaffName() {
		return Group.getStaffPrefix(getWorld(), getGroupID()) + getUsername();
	}

	public Channel getChannel() {
		return channel;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public DelayedEvent getSkullEvent() {
		return skullEvent;
	}

	public void setSkullEvent(DelayedEvent skullEvent) {
		this.skullEvent = skullEvent;
	}

	public DelayedEvent getChargeEvent() {
		return chargeEvent;
	}

	public void setChargeEvent(DelayedEvent chargeEvent) {
		this.chargeEvent = chargeEvent;
	}

	public long getSkullTime() {
		if (isSkulled() && getSkullType() == 1) {
			return skullEvent.timeTillNextRun();
		}
		return 0;
	}

	public long getChargeTime() {
		if (isCharged()) {
			return chargeEvent.timeTillNextRun();
		}
		return 0;
	}

	public String getSleepword() {
		return sleepword;
	}

	public void setSleepword(String sleepword) {
		this.sleepword = sleepword;
	}

	public int getSpellWait() {
		return DataConversions.roundUp((1600 - (System.currentTimeMillis() - lastSpellCast)) / 1000D);
	}

	public Action getStatus() {
		return status;
	}

	public void setStatus(Action a) {
		status = a;
	}

	public String getUsername() {

		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getUsernameHash() {
		if (getAttribute("fakeuser", null) != null) {
			return DataConversions.usernameToHash((String) getAttribute("fakeuser", null));
		}
		return usernameHash;
	}

	public void setUsernameHash(long usernameHash) {
		this.usernameHash = usernameHash;
	}

	@Override
	public int getArmourPoints() {
		int points = 1;
		if (!getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			for (Item item : getInventory().getItems()) {
				if (item.isWielded()) {
					points += item.getDef(getWorld()).getArmourBonus();
				}
			}
		} else {
			points = getEquipment().getArmour();
		}

		return points < 1 ? 1 : points;
	}

	@Override
	public int getWeaponAimPoints() {
		int points = 1;
		if (!getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			for (Item item : getInventory().getItems()) {
				if (item.isWielded()) {
					points += item.getDef(getWorld()).getWeaponAimBonus();
				}
			}
		} else {
			points = this.getEquipment().getWeaponAim();
		}


		return points < 1 ? 1 : points;
	}

	@Override
	public int getWeaponPowerPoints() {
		int points = 1;
		if (!getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			for (Item item : getInventory().getItems()) {
				if (item.isWielded()) {
					points += item.getDef(getWorld()).getWeaponPowerBonus();
				}
			}
		} else {
			points = this.getEquipment().getWeaponPower();
		}
		return points < 1 ? 1 : points;
	}

	public int[] getWornItems() {
		return wornItems;
	}

	public void setWornItems(int[] worn) {
		wornItems = worn;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public void handleWakeup() {
		fatigue = sleepStateFatigue;
		ActionSender.sendFatigue(this);
	}

	public void incQuestExp(int i, int amount) {
		skills.addExperience(i, amount);
	}

	private double getExperienceRate(int skill) {
		double multiplier = 1.0;
		/*
		  Skilling Experience Rate
		 */
		if (skill >= 4 && skill <= getWorld().getServer().getConstants().getSkills().getSkillsCount() - 1) {
			multiplier = getWorld().getServer().getConfig().SKILLING_EXP_RATE;
			if (getLocation().inWilderness() && !getLocation().inBounds(220, 108, 225, 111)) {
				multiplier += getWorld().getServer().getConfig().WILDERNESS_BOOST;
				if (isSkulled()) {
					multiplier += getWorld().getServer().getConfig().SKULL_BOOST;
				}
			}
		}
		/*
		  Combat Experience Rate
		 */
		else if (skill >= 0 && skill <= 3) { // Attack, Strength, Defense & HP bonus.
			multiplier = getWorld().getServer().getConfig().COMBAT_EXP_RATE;
			if (getLocation().inWilderness()) {
				multiplier += getWorld().getServer().getConfig().WILDERNESS_BOOST;
				if (isSkulled()) {
					multiplier += getWorld().getServer().getConfig().SKULL_BOOST;
				}
			}
		}

		/*
		  Double Experience
		 */
		if (getWorld().getServer().getConfig().IS_DOUBLE_EXP) {
			multiplier *= 2;
		}

		/*
		  Experience Elixir
		 */
		if (getCache().hasKey("elixir_time")) {
			if (getElixir() <= 0) {
				getCache().remove("elixir_time");
				ActionSender.sendElixirTimer(this, 0);
			} else {
				multiplier += 1;
			}
		}

		return multiplier;
	}

	public void incExp(int skill, int skillXP, boolean useFatigue) {
		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (isExperienceFrozen()) {
				ActionSender.sendMessage(this, "You can not gain experience right now!");
				return;
			}
		} else if (isExperienceFrozen()) {
			return;
		}

		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (useFatigue) {
				if (fatigue >= this.MAX_FATIGUE) {
					ActionSender.sendMessage(this, "@gre@You are too tired to gain experience, get some rest!");
					return;
				}
				//if (fatigue >= 69750) {
				//	ActionSender.sendMessage(this, "@gre@You start to feel tired, maybe you should rest soon.");
				//}
				if (skill >= 3 && useFatigue) {
					fatigue += skillXP * 4;
					if (fatigue > this.MAX_FATIGUE) {
						fatigue = this.MAX_FATIGUE;
					}
					ActionSender.sendFatigue(this);
				}
			}
		}

		if (getLocation().onTutorialIsland()) {
			if (skills.getExperience(skill) + skillXP > 200) {
				if (skill != 3) {
					skills.setExperience(skill, 200);
				} else {
					skills.setExperience(skill, 1200);
				}
			}
		}
		skillXP *= getExperienceRate(skill);
		if(this.getParty() != null){
			PartyPlayer partyLeader = this.getParty().getLeader();
			if(partyLeader.getShareExp() > 0){
				if(skill > 6){
					for (PartyPlayer p : this.getParty().getPlayers()) {
						if(p.getPlayerReference().getFatigue() < p.getPlayerReference().MAX_FATIGUE){
							if(p.getPlayerReference().getUsername() != this.getUsername()){
								p.getPlayerReference().setFatigue(p.getPlayerReference().getFatigue() + skillXP * 4);
								ActionSender.sendFatigue(this);
							}
							p.getPlayerReference().getSkills().addExperience(skill, (int) skillXP / p.getPartyMembersNotTired());
						}
					}
				} else {
					skills.addExperience(skill, (int) skillXP);
				}
			} else {
				skills.addExperience(skill, (int) skillXP);
			}
		} else {
			skills.addExperience(skill, (int) skillXP);
		}
		// ActionSender.sendExperience(this, skill);
	}

	public void incQuestPoints(int amount) {
		setQuestPoints(getQuestPoints() + amount);
	}

	public void incrementSleepTries() {
		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			incorrectSleepTries++;
		}
	}

	private void incrementActivity(int amount) {
		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			activity += amount;
			if (activity >= KITTEN_ACTIVITY_THRESHOLD) {
				activity -= KITTEN_ACTIVITY_THRESHOLD;
				getWorld().getServer().getPluginHandler().blockDefaultAction("CatGrowth", new Object[]{this});
			}
		}
	}

	/*
	 * Called on periodic saves
	 */
	public void timeIncrementActivity() {
		incrementActivity(5);
	}

	/*
	 * Called when walking a single step
	 */
	public void stepIncrementActivity() {
		incrementActivity(2);
	}

	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int id) {
		getUpdateFlags().setAppearanceChanged(true);
		groupID = id;
	}

	public boolean isOwner() {
		return groupID == Group.OWNER;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public boolean isAdmin() {
		return groupID == Group.ADMIN || isOwner();
	}

	public boolean isSuperMod() {
		return groupID == Group.SUPER_MOD || isAdmin();
	}

	public boolean isMod() {
		return groupID == Group.MOD || isSuperMod();
	}

	public boolean isDev() {
		return groupID == Group.DEV || isAdmin();
	}

	public boolean isEvent() {
		return groupID == Group.EVENT || isMod() || isDev();
	}

	public boolean isStaff() {
		return isEvent();
	}

	public boolean isChangingAppearance() {
		return changingAppearance;
	}

	public void setChangingAppearance(boolean b) {
		changingAppearance = b;
	}

	public boolean isAntidoteProtected() {
		return System.currentTimeMillis() - lastAntidote < 90000;
	}

	public boolean isInBank() {
		return inBank;
	}

	public void setInBank(boolean inBank) {
		this.inBank = inBank;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			currentLogin = System.currentTimeMillis();
			if (getCache().hasKey("poisoned")) {
				startPoisonEvent();
				PoisonEvent poisonEvent = getAttribute("poisonEvent", null);
				poisonEvent.setPoisonPower(getCache().getInt("poisoned"));
			}
			prayerDrainEvent = new PrayerDrainEvent(getWorld(), this, Integer.MAX_VALUE);
			getWorld().getServer().getGameEventHandler().add(prayerDrainEvent);
			getWorld().getServer().getGameEventHandler().add(statRestorationEvent);
		}
		this.loggedIn = loggedIn;
	}

	public boolean isMale() {
		return maleGender;
	}

	public void setMale(boolean male) {
		maleGender = male;
	}

	public boolean isMaleGender() {
		return maleGender;
	}

	public boolean isMuted() {
		if (getMuteExpires() == 0)
			return false;
		if (getMuteExpires() == -1)
			return true;

		return getMuteExpires() - System.currentTimeMillis() > 0;
	}

	public boolean isRanging() {
		return rangeEvent != null || throwingEvent != null;
	}

	public boolean isReconnecting() {
		return reconnecting;
	}

	public void setReconnecting(boolean reconnecting) {
		this.reconnecting = reconnecting;
	}

	public boolean isRequiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public void setRequiresOfferUpdate(boolean b) {
		requiresOfferUpdate = b;
	}

	public boolean isSkulled() {
		return skullEvent != null;
	}

	public boolean isCharged() {
		return chargeEvent != null;
	}

	public int getSkullType() {
		int type = 0;
		if (isSkulled()) {
			type = 1;
		}
		return type;
	}

	public boolean isSleeping() {
		return sleeping;
	}

	public void setSleeping(boolean isSleeping) {
		this.sleeping = isSleeping;
	}

	public boolean isSuspiciousPlayer() {
		return suspiciousPlayer;
	}

	public void setSuspiciousPlayer(boolean suspicious) {
		suspiciousPlayer = suspicious;
	}

	@Override
	public void killedBy(Mob mob) {
		if (!loggedIn) {
			return;
		}

		ActionSender.sendSound(this, "death");
		ActionSender.sendDied(this);

		if (getAttribute("projectile", null) != null) {
			ProjectileEvent projectileEvent = getAttribute("projectile");
			projectileEvent.setCanceled(true);
		}
		getSettings().getAttackedBy().clear();

		getCache().store("last_death", System.currentTimeMillis());

		Player player = mob instanceof Player ? (Player) mob : null;
		boolean stake = getDuel().isDuelActive() || (player != null && player.getDuel().isDuelActive());

		if (player != null) {
			player.message("You have defeated " + getUsername() + "!");
			ActionSender.sendSound(player, "victory");
			if (player.getLocation().inWilderness()) {
				int id = -1;
				if (player.getKillType() == 0) {
					id = player.getEquippedWeaponID();
					if (id == -1 || id == 59 || id == 60)
						id = 16;
				} else if (player.getKillType() == 1) {
					id = -1;
				} else if (player.getKillType() == 2) {
					id = -2;
				}
				getWorld().sendKilledUpdate(this.getUsernameHash(), player.getUsernameHash(), id);
				player.incKills();
				this.incDeaths();
				getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(player, "has PKed <strong>" + this.getUsername() + "</strong>"));
			} else if (stake) {
				getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(player,
					"has just won a stake against <strong>" + this.getUsername() + "</strong>"));
			}
		}
		if (stake) {
			getDuel().dropOnDeath();
		} else {
			if (!isStaff())
				getInventory().dropOnDeath(mob);
		}
		if (isIronMan(3)) {
			updateHCIronman(1);
			ActionSender.sendIronManMode(this);
			getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(this, "has died and lost the HC Iron Man Rank!"));
		}
		removeSkull(); // destroy
		resetCombatEvent();
		this.setLastOpponent(null);
		getWorld().registerItem(new GroundItem(getWorld(), ItemId.BONES.id(), getX(), getY(), 1, player));
		if ((!getCache().hasKey("death_location_x") && !getCache().hasKey("death_location_y"))) {
			setLocation(Point.location(122, 647), true);
		} else {
			setLocation(Point.location(getCache().getInt("death_location_x"), getCache().getInt("death_location_y")), true);
		}
		ActionSender.sendWorldInfo(this);
		ActionSender.sendEquipmentStats(this);
		ActionSender.sendInventory(this);

		resetPath();
		if (getWorld().getServer().getConfig().WANT_PARTIES) {
			if(this.getParty() != null){
				this.getParty().sendParty();
			}
		}
		this.cure();
		prayers.resetPrayers();
		skills.normalize();
		if (getWorld().getServer().getConfig().WANT_PARTIES) {
			if(getParty() != null){
				this.getParty().sendParty();
			}
		}

		//getUpdateFlags().setHpUpdate(new HpUpdate(this, 0));
		getUpdateFlags().reset();
		//getUpdateFlags().setAppearanceChanged(true);
	}

	private int getEquippedWeaponID() {
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item i = getEquipment().get(4);
			if (i != null)
				return i.getID();
		} else {
			for (Item i : getInventory().getItems()) {
				if (i.isWielded() && (i.getDef(getWorld()).getWieldPosition() == 4))
					return i.getID();
			}
		}
		return -1;
	}

	public boolean loggedIn() {
		return loggedIn;
	}

	public void message(String string) {
		// resetMenuHandler();
		// setOption(-1);
		ActionSender.sendMessage(this, string);
	}

	public void playerServerMessage(MessageType type, String string) {
		ActionSender.sendPlayerServerMessage(this, type, string);
	}

	public void walkThenTeleport(int x1, int y1, int x2, int y2, boolean bubble) {
		walk(x1, y1);
		while (!getWalkingQueue().finished()) {
			sleep(1);
		}
		teleport(x2, y2, bubble);
	}

	public void teleport(int x, int y) {
		teleport(x, y, false);
	}

	public void addPrivateMessage(PrivateMessage privateMessage) {
		if (getPrivateMessageQueue().size() < 2) {
			getPrivateMessageQueue().add(privateMessage);
		}
	}

	public void addToPacketQueue(Packet e) {
		ping();
		if (incomingPackets.size() <= getWorld().getServer().getConfig().PACKET_LIMIT) {
			synchronized (incomingPacketLock) {
				if (!incomingPackets.containsKey(e.getID()))
					incomingPackets.put(e.getID(), e);
			}
		}
	}

	public void ping() {
		lastPing = System.currentTimeMillis();
	}

	public void playSound(String sound) {
		ActionSender.sendSound(this, sound);
	}

	public void checkForMouseMovement(boolean movedMouse) {
		if (!movedMouse) {
			actionsMouseStill++;

			float minutesFlagged = (float) (System.currentTimeMillis() - lastMouseMoved) / (float) 60000;
			if (actionsMouseStill >= 30 && minutesFlagged >= 1) {
				String string = "Check " + getUsername() + "! " + actionsMouseStill
					+ " actions with mouse still. Mouse was last moved " + String.format("%.02f", minutesFlagged)
					+ " mins ago";

				for (Player p : getWorld().getPlayers()) {
					if (p.isMod()) {
						p.message("@red@Server@whi@: " + string);
					}
				}
				setSuspiciousPlayer(true);
			}
		} else {
			actionsMouseStill = 0;
			lastMouseMoved = System.currentTimeMillis();
		}
	}

	public void process() {
		if (System.currentTimeMillis() - lastSave >= 300000) {
			save();
			lastSave = System.currentTimeMillis();
		}
	}

	public void processIncomingPackets() {
		if (!channel.isOpen() && !channel.isWritable()) {
			return;
		}
		synchronized (incomingPacketLock) {
			for (Map.Entry<Integer, Packet> p : incomingPackets.entrySet()) {
				PacketHandler ph = PacketHandlerLookup.get(p.getValue().getID());
				if (ph != null && p.getValue().getBuffer().readableBytes() >= 0) {
					try {
						if (!(ph instanceof Ping) && !(ph instanceof WalkRequest))
							LOGGER.info("Handling Packet (CLASS: " + ph + "): "
								+ this.username + " (ID: " + this.owner + ")");
						ph.handlePacket(p.getValue(), this);
					} catch (Exception e) {
						LOGGER.catching(e);
						unregister(false, "Malformed packet!");
					}
				}
			}
			incomingPackets.clear();
		}
	}

	public void processOutgoingPackets() {
		// Unsure if we want to clear right now. Probably OK not to since the player should be cleaned up when the channel is no longer open.
		/*if(!channel.isOpen() || !isLoggedIn()) {
			outgoingPackets.clear();
		}*/

		if (!channel.isOpen() || !isLoggedIn() || !channel.isActive() || !channel.isWritable()) {
			return;
		}
		synchronized (outgoingPacketsLock) {
			try {
				for (Packet outgoing : outgoingPackets) {
					channel.writeAndFlush(outgoing);
				}
			} catch (Exception e) {
				LOGGER.catching(e);
			}
			// channel.flush();

			outgoingPackets.clear();
		}
	}

	public void removeSkull() {
		if (skullEvent == null) {
			return;
		}
		skullEvent.stop();
		skullEvent = null;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public boolean requiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public void resetAll() {
		checkAndInterruptBatchEvent();
		resetAllExceptTradeOrDuel(true);
		getTrade().resetAll();
		getDuel().resetAll();
	}

	public void resetAllExceptBank() {
		resetAllExceptTradeOrDuel(false);
		getTrade().resetAll();
		getDuel().resetAll();
	}

	public void resetAllExceptDueling() {
		resetAllExceptTradeOrDuel(true);
		getTrade().resetAll();
	}

	private void resetAllExceptTradeOrDuel(boolean resetBank) {
		resetCannonEvent();
		setAttribute("bank_pin_entered", "cancel");
		setWalkToAction(null);
		if (getMenu() != null) {
			menu = null;
		}
		if (getMenuHandler() != null) {
			resetMenuHandler();
		}
		if (accessingBank() && resetBank) {
			resetBank();
		}
		if (accessingShop()) {
			resetShop();
		}
		if (isFollowing()) {
			resetFollowing();
		}
		if (isRanging()) {
			resetRange();
		}
		setInteractingNpc(null);
		setStatus(Action.IDLE);
	}

	public void resetAllExceptTrading() {
		resetAllExceptTradeOrDuel(true);
		getDuel().resetAll();
	}

	public void resetBank() {
		setAccessingBank(false);
		ActionSender.hideBank(this);
	}

	public void resetMenuHandler() {
		menu = null;
		menuHandler = null;
		ActionSender.hideMenu(this);
	}

	public void resetRange() {
		if (rangeEvent != null) {
			rangeEvent.stop();
			rangeEvent = null;
		}
		if (throwingEvent != null) {
			throwingEvent.stop();
			throwingEvent = null;
		}
		setStatus(Action.IDLE);
	}

	public void resetShop() {
		if (shop != null) {
			shop.removePlayer(this);
			shop = null;
			ActionSender.hideShop(this);
		}
	}

	public void resetSleepTries() {
		incorrectSleepTries = 0;
	}

	public void save() {
		getWorld().getServer().getPlayerDataProcessor().addSaveRequest(this);
	}

	public void logout() {
		ActionSender.sendLogoutRequestConfirm(this);
		setLoggedIn(false);

		FishingTrawler trawlerInstance = getWorld().getFishingTrawler(this);

		resetAll();

		Mob opponent = getOpponent();
		if (opponent != null) {
			resetCombatEvent();
		}
		this.setLastOpponent(null);
		if (trawlerInstance != null && trawlerInstance.getPlayers().contains(this)) {
			trawlerInstance.disconnectPlayer(this, true);
		}
		if (getLocation().inMageArena()) {
			teleport(228, 109);
		}
		if (getParty() != null) {
			getParty().removePlayer(this.getUsername());
		}
		// store kitten growth progress
		getCache().set("kitten_events", getAttribute("kitten_events", 0));
		getCache().set("kitten_hunger", getAttribute("kitten_hunger", 0));
		getCache().set("kitten_loneliness", getAttribute("kitten_loneliness", 0));
		// any gnome ball progress
		getCache().set("gnomeball_goals", getSyncAttribute("gnomeball_goals", 0));
		getCache().set("gnomeball_npc", getSyncAttribute("gnomeball_npc", 0));

		save();

		/** IP Tracking in wilderness removal */
		/*if(player.getLocation().inWilderness())
		{
			wildernessIPTracker.remove(player.getCurrentIP());
		}*/

		for (Player other : getWorld().getPlayers()) {
			other.getSocial().alertOfLogout(this);
		}

		getWorld().getClanManager().checkAndUnattachFromClan(this);
		getWorld().getPartyManager().checkAndUnattachFromParty(this);

		getWorld().getServer().getPlayerDataProcessor().addRemoveRequest(this);
	}

	public void sendMemberErrorMessage() {
		message(MEMBER_MESSAGE);
	}

	public void sendQuestComplete(int questId) { // REMEMBER THIS
		if (getQuestStage(questId) != -1) {
			getWorld().getQuest(questId).handleReward(this);
			updateQuestStage(questId, -1);
			ActionSender.sendStats(this);
			getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(this,
				"just completed <strong><font color=#00FF00>" + getWorld().getQuest(questId).getQuestName()
					+ "</font></strong> quest! They now have <strong><font color=#E1E100>" + this.getQuestPoints()
					+ "</font></strong> quest points"));
		}
	}

	public void sendMiniGameComplete(int miniGameId, Optional<String> message) {
		getWorld().getMiniGame(miniGameId).handleReward(this);
		getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(this, "just completed <strong><font color=#00FF00>" + getWorld().getMiniGame(miniGameId).getMiniGameName()
			+ "</font></strong> minigame! " + (message.isPresent() ? message.get() : "")));
	}

	public void setAccessingBank(boolean b) {
		inBank = b;
	}

	public void setAccessingShop(Shop shop) {
		this.shop = shop;
		if (shop != null) {
			shop.addPlayer(this);
		}
	}

	public void setBatchEvent(BatchEvent batchEvent) {
		if (batchEvent != null && batchEvent.getOwner() instanceof Player) {
			Player player = (Player)batchEvent.getOwner();
			player.checkAndInterruptBatchEvent();
			this.batchEvent = batchEvent;
			getWorld().getServer().getGameEventHandler().add(batchEvent);
		} else {
			this.batchEvent = null;
		}
	}

	public void setCastTimer(long timer) {
		lastSpellCast = timer;
	}

	public void setCastTimer() {
		lastSpellCast = System.currentTimeMillis();
	}

	public void setAntidoteProtection() {
		lastAntidote = System.currentTimeMillis();
	}

	public void setLastReport() {
		lastReport = System.currentTimeMillis();
	}

	public void setLastReport(long lastReport) {
		this.lastReport = lastReport;
	}

	public void setQuestStage(int q, int stage) {
		getQuestStages().put(q, stage);
	}

	public void updateQuestStage(int q, int stage) {
		getQuestStages().put(q, stage);
		ActionSender.sendQuestInfo(this, q, stage);
	}

	public void updateQuestStage(QuestInterface q, int stage) {
		getQuestStages().put(q.getQuestId(), stage);
		ActionSender.sendQuestInfo(this, q.getQuestId(), stage);
	}

	private Map<Integer, Integer> getAchievements() {
		return achievements;
	}

	public void setAchievementStatus(int achid, int status) {
		getAchievements().put(achid, status);

		getWorld().getServer().getAchievementSystem().achievementListGUI(this, achid, status);
	}

	public void updateAchievementStatus(Achievement ach, int status) {
		getAchievements().put(ach.getId(), status);

		getWorld().getServer().getAchievementSystem().achievementListGUI(this, ach.getId(), status);
	}

	public int getAchievementStatus(int id) {
		if (getAchievements().containsKey(id)) {
			return getAchievements().get(id);
		}
		return 0;
	}

	public void setSkulledOn(Player player) {
		player.getSettings().addAttackedBy(this);
		if (System.currentTimeMillis() - getSettings().lastAttackedBy(player) > 1200000) {
			addSkull(1200000);
		}
		player.getUpdateFlags().setAppearanceChanged(true);
	}

	public void setSpellFail() {
		lastSpellCast = System.currentTimeMillis() + 20000;
	}

	public void startSleepEvent(final boolean bed) {
		sleepEvent = new DelayedEvent(getWorld(), this, 600, "Start Sleep Event") {
			@Override
			public void run() {
				if (getOwner().isRemoved() || sleepStateFatigue == 0 || !sleeping) {
					running = false;
					return;
				}

				if (bed) {
					getOwner().sleepStateFatigue -= 16500;
				} else {
					getOwner().sleepStateFatigue -= 4125;
				}

				if (getOwner().sleepStateFatigue < 0) {
					getOwner().sleepStateFatigue = 0;
				}
				ActionSender.sendSleepFatigue(getOwner(), getOwner().sleepStateFatigue);
			}
		};
		sleepStateFatigue = fatigue;
		ActionSender.sendSleepFatigue(this, sleepStateFatigue);
		getWorld().getServer().getGameEventHandler().add(sleepEvent);
	}

	public void teleport(int x, int y, boolean bubble) {
		if (bubble && getWorld().getServer().getPluginHandler().blockDefaultAction("Teleport", new Object[]{this})) {
			return;
		}
		if (inCombat()) {
			this.setLastOpponent(null);
			combatEvent.resetCombat();
		}

		if (bubble) {
			for (Player p : getViewArea().getPlayersInView()) {
				if (!isInvisible(p)) {
					ActionSender.sendTeleBubble(p, getX(), getY(), false);
				}
			}
			ActionSender.sendTeleBubble(this, getX(), getY(), false);
		}

		setLocation(Point.location(x, y), true);
		resetPath();
		ActionSender.sendWorldInfo(this);
	}

	@Override
	public void setLocation(Point p, boolean teleported) {
		if(!teleported) {
			if (getSkullType() == 2)
				getUpdateFlags().setAppearanceChanged(true);
			else if (getSkullType() == 0)
				getUpdateFlags().setAppearanceChanged(true);
		}
		else {
			setTeleporting(true);
			getUpdateFlags().setAppearanceChanged(true);
		}

		super.setLocation(p, teleported);

	}

	public void produceUnderAttack() {
		getWorld().produceUnderAttack(this);
	}

	public boolean checkUnderAttack() {
		return getWorld().checkUnderAttack(this);
	}

	public void releaseUnderAttack() {
		getWorld().releaseUnderAttack(this);
	}

	@Override
	public String toString() {
		return "[Player:" + username + "]";
	}

	public boolean tradeDuelThrottling() {
		long now = System.currentTimeMillis();
		if (now - lastTradeDuelRequest > 1000) {
			lastTradeDuelRequest = now;
			return false;
		}
		return true;
	}

	public void updateWornItems(int indexPosition, int appearanceId) {
		this.updateWornItems(indexPosition, appearanceId, 0, false);
	}

	public void updateWornItems(int indexPosition, int appearanceId, int wearableId, boolean isEquipped) {
		// metal skirts (ideally all !full pants should update pants appearance to minishorts when that anim exists)
		if (getWorld().getServer().getConfig().WANT_CUSTOM_SPRITES && wearableId == 640) {
			if (isEquipped) wornItems[2] = 0;
			else wornItems[2] = 3;
		}
		//Don't need to show arrows or rings
		if (indexPosition <= 11) {
			wornItems[indexPosition] = appearanceId;
			getUpdateFlags().setAppearanceChanged(true);
		}
	}

	private Queue<PrivateMessage> getPrivateMessageQueue() {
		return privateMessageQueue;
	}

	public Map<Integer, Integer> getQuestStages() {
		return questStages;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int i) {
		this.kills = i;
	}

	public int getDeaths() {
		return deaths;
	}

	public int getNpcDeaths() {
		return npcDeaths;
	}

	public int getKills2() {
		return kills2;
	}

	public void setDeaths(int i) {
		this.deaths = i;
	}

	public void setNpcDeaths(int i) {
		this.npcDeaths = i;
	}

	public void setKills2(int i) {
		this.kills2 = i;
		ActionSender.sendKills2(this);
	}

	private void incDeaths() {
		deaths++;
	}

	private void incNpcDeaths() {
		npcDeaths++;
	}

	private void incKills() {
		kills++;
	}

	public void incKills2() {
		kills2++;
	}

	public void addKill(boolean add) {
		if (!add) {
			kills++;
		}
	}

	public WalkToAction getWalkToAction() {
		return walkToAction;
	}

	public void setWalkToAction(WalkToAction action) {
		this.walkToAction = action;
	}

	public int getElixir() {
		if (getCache().hasKey("elixir_time")) {
			int now = (int) (System.currentTimeMillis() / 1000);
			int time = ((int) getCache().getLong("elixir_time") - now);
			return (time < 0) ? 0 : time;
		}
		return 0;
	}

	public void addElixir(int seconds) {
		long elixirTime = seconds;
		long now = System.currentTimeMillis() / 1000;
		long experience = (now + elixirTime);
		getCache().store("elixir_time", experience);
	}

	public void removeElixir() {
		if (getCache().hasKey("elixir_time"))
			getCache().remove("elixir_time");

		ActionSender.sendElixirTimer(this, 0);
	}

	public int getGlobalBlock() {
		if (getCache().hasKey("setting_block_global")) {
			return getCache().getInt("setting_block_global");
		}
		return 1;
	}

	public Boolean getVolumeToRotate() {
		if (getCache().hasKey("setting_volume_rotate")) {
			return getCache().getBoolean("setting_volume_rotate");
		}
		return false;
	}

	public Boolean getSwipeToRotate() {
		if (getCache().hasKey("setting_swipe_rotate")) {
			return getCache().getBoolean("setting_swipe_rotate");
		}
		return true;
	}

	public Boolean getSwipeToScroll() {
		if (getCache().hasKey("setting_swipe_scroll")) {
			return getCache().getBoolean("setting_swipe_scroll");
		}
		return true;
	}

	public Boolean getSwipeToZoom() {
		if (getCache().hasKey("setting_swipe_zoom")) {
			return getCache().getBoolean("setting_swipe_zoom");
		}
		return true;
	}

	public Boolean getBatchProgressBar() {
		if (getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			if (getCache().hasKey("setting_batch_progressbar")) {
				return getCache().getBoolean("setting_batch_progressbar");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getExperienceDrops() {
		if (getWorld().getServer().getConfig().EXPERIENCE_DROPS_TOGGLE) {
			if (getCache().hasKey("setting_experience_drops")) {
				return getCache().getBoolean("setting_experience_drops");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideFog() {
		if (getWorld().getServer().getConfig().FOG_TOGGLE) {
			if (getCache().hasKey("setting_showfog")) {
				return getCache().getBoolean("setting_showfog");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getAutoMessageSwitch() {
		if (getWorld().getServer().getConfig().AUTO_MESSAGE_SWITCH_TOGGLE) {
			if (getCache().hasKey("setting_auto_messageswitch")) {
				return getCache().getBoolean("setting_auto_messageswitch");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideSideMenu() {
		if (getWorld().getServer().getConfig().SIDE_MENU_TOGGLE) {
			if (getCache().hasKey("setting_side_menu")) {
				return getCache().getBoolean("setting_side_menu");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideKillFeed() {
		if (getWorld().getServer().getConfig().WANT_KILL_FEED) {
			if (getCache().hasKey("setting_kill_feed")) {
				return getCache().getBoolean("setting_kill_feed");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideInventoryCount() {
		if (getWorld().getServer().getConfig().INVENTORY_COUNT_TOGGLE) {
			if (getCache().hasKey("setting_inventory_count")) {
				return getCache().getBoolean("setting_inventory_count");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideNameTag() {
		if (getWorld().getServer().getConfig().SHOW_FLOATING_NAMETAGS) {
			if (getCache().hasKey("setting_floating_nametags")) {
				return getCache().getBoolean("setting_floating_nametags");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideRoofs() {
		if (getWorld().getServer().getConfig().SHOW_ROOF_TOGGLE) {
			if (getCache().hasKey("setting_showroof")) {
				return getCache().getBoolean("setting_showroof");
			}
			return true;
		} else {
			return false;
		}
	}

	public int getGroundItemsToggle() {
		if (getWorld().getServer().getConfig().GROUND_ITEM_TOGGLE) {
			if (getCache().hasKey("setting_ground_items")) {
				return getCache().getInt("setting_ground_items");
			}
			return 0;
		} else {
			return 0;
		}
	}

	public int getFightModeSelectorToggle() {
		if (getWorld().getServer().getConfig().FIGHTMODE_SELECTOR_TOGGLE) {
			if (getCache().hasKey("setting_fightmode_selector")) {
				return getCache().getInt("setting_fightmode_selector");
			}
			return 1;
		} else {
			return 1;
		}
	}

	public int getExperienceCounterToggle() {
		if (getWorld().getServer().getConfig().EXPERIENCE_COUNTER_TOGGLE) {
			if (getCache().hasKey("setting_experience_counter")) {
				return getCache().getInt("setting_experience_counter");
			}
			return 1;
		} else {
			return 0;
		}
	}

	public int getLongPressDelay() {
		if (getCache().hasKey("setting_press_delay")) {
			return getCache().getInt("setting_press_delay");
		}
		return 5;
	}

	public int getLastZoom() {
		if (getCache().hasKey("setting_last_zoom")) {
			return getCache().getInt("setting_last_zoom");
		}
		return 125;
	}

	public int getFontSize() {
		if (getCache().hasKey("setting_font_size")) {
			return getCache().getInt("setting_font_size");
		}
		return 3;
	}

	public Boolean getHoldAndChoose() {
		if (getCache().hasKey("setting_hold_choose")) {
			return getCache().getBoolean("setting_hold_choose");
		}
		return true;
	}

	public boolean getClanInviteSetting() {
		if (getWorld().getServer().getConfig().WANT_CLANS) {
			if (getCache().hasKey("p_block_invites")) {
				return getCache().getBoolean("p_block_invites");
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean getPartyInviteSetting() {
		//if (getServer().getConfig().WANT_PARTIES) {
			if (getCache().hasKey("party_block_invites")) {
				return getCache().getBoolean("party_block_invites");
			}
			return false;
		//}
	}

	public boolean getShowNPCKC() {
		if (getCache().hasKey("show_npc_kc")) {
			return getCache().getBoolean("show_npc_kc");
		}
		return false;
	}

	public boolean getAndroidInvToggle() {
		if (getCache().hasKey("android_inv_toggle")) {
			return getCache().getBoolean("android_inv_toggle");
		}
		return false;
	}
	public boolean getPartyLootSetting() {
		return getPartyInviteSetting();
	}

	public boolean isPlayer() {
		return true;
	}

	public boolean isNpc() {
		return false;
	}

	public PlayerSettings getSettings() {
		return playerSettings;
	}

	public Social getSocial() {
		return social;
	}

	public Prayers getPrayers() {
		return prayers;
	}

	public int getIcon() {
		if (getWorld().getServer().getConfig().WANT_CUSTOM_RANK_DISPLAY) {
			if (isAdmin())
				return 0x0100FF00;

			if (isMod())
				return 0x010000FF;

			if (isDev())
				return 0x01FF0000;

			if (isEvent())
				return 0x014D33BD;

			return 0;
		} else {
			if (isAdmin())
				return 0x02FFFFFF;

			if (isMod())
				return 0x03FFFFFF;

			return 0;
		}
	}

	public Trade getTrade() {
		return trade;
	}

	public Duel getDuel() {
		return duel;
	}
	/*public int getUnreadMessages() {
		return unreadMessages + 1;
	}*/
	/*public void setUnreadMessages(int unreadMessages) {
		this.unreadMessages = unreadMessages;
	}*/
	/*public int getTeleportStones() {
		return teleportStones;
	}*/
	/*public void setTeleportStones(int stones) {
		this.teleportStones = stones;
	}*/

	public int getDatabaseID() {
		return databaseID;
	}

	public void setDatabaseID(int i) {
		this.databaseID = i;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public PartyInvite getActivePartyInvite() {
		return activePartyInvitation;
	}

	public void setActivePartyInvite(PartyInvite inv) {
		activePartyInvitation = inv;
	}

	public Clan getClan() {
		return clan;
	}

	public void setClan(Clan clan) {
		this.clan = clan;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public ClanInvite getActiveClanInvite() {
		return activeClanInvitation;
	}

	public void setActiveClanInvite(ClanInvite inv) {
		activeClanInvitation = inv;
	}

	public long secondsUntillPool() {
		return (90 - ((System.currentTimeMillis() - (getCache().hasKey("last_death") ? getCache().getLong("last_death") : 0)) / 1000));
	}

	public boolean canUsePool() {
		return System.currentTimeMillis() - (getCache().hasKey("last_death") ? getCache().getLong("last_death") : 0) > 90000;
	}

	public void addNpcKill(Npc n, boolean sendUpdate) {
		int kills = 1;
		if (getKillCache().containsKey(n.getID())) {
			kills = getKillCache().get(n.getID()) + 1;

		}
		getKillCache().put(n.getID(), kills);
		setKillCacheUpdated(true);
		if (sendUpdate) {
			message("Your " + n.getDef().getName() + " kill count is: @red@" + kills + "@whi@.");
		}
	}

	public boolean toggleCacheInvisible() {
		return setCacheInvisible(!cacheIsInvisible());
	}

	public boolean isInvisible(Mob m) {
		return stateIsInvisible() && m.rankCheckInvisible(this);
	}

	private boolean cacheIsInvisible() {
		if (!getCache().hasKey("invisible"))
			return false;

		return getCache().getBoolean("invisible");
	}

	public boolean stateIsInvisible() {
		return super.stateIsInvisible() || cacheIsInvisible();
	}

	public boolean setCacheInvisible(boolean invisible) {
		getUpdateFlags().setAppearanceChanged(true);
		this.getCache().store("invisible", invisible);
		return invisible;
	}

	public boolean isInvulnerable(Mob m) {
		return stateIsInvulnerable() && m.rankCheckInvulnerable(this);
	}

	private boolean cacheIsInvulnerable() {
		if (!getCache().hasKey("invulnerable"))
			return false;

		return getCache().getBoolean("invulnerable");
	}

	public boolean stateIsInvulnerable() {
		return super.stateIsInvulnerable() || cacheIsInvulnerable();
	}

	public boolean setCacheInvulnerable(boolean invulnerable) {
		getUpdateFlags().setAppearanceChanged(true);
		this.getCache().store("invulnerable", invulnerable);
		return invulnerable;
	}

	public boolean toggleCacheInvulnerable() {
		return setCacheInvulnerable(!cacheIsInvulnerable());
	}

	public boolean isExperienceFrozen() {
		if (!getCache().hasKey("freezexp"))
			return false;

		return getCache().getBoolean("freezexp");
	}

	public boolean setFreezeXp(boolean freezeXp) {
		this.getCache().store("freezexp", freezeXp);
		return freezeXp;
	}

	public boolean toggleFreezeXp() {
		return setFreezeXp(!isExperienceFrozen());
	}

	public Point summon(Point summonLocation) {
		Point originalLocation = getLocation();
		setSummonReturnPoint();
		teleport(summonLocation.getX(), summonLocation.getY(), true);
		return originalLocation;
	}

	public Point summon(Player summonTo) {
		return summon(summonTo.getLocation());
	}

	public void setSummonReturnPoint() {
		if (wasSummoned())
			return;

		getCache().set("return_x", getX());
		getCache().set("return_y", getY());
		getCache().store("was_summoned", true);
	}

	private void resetSummonReturnPoint() {
		getCache().remove("return_x");
		getCache().remove("return_y");
		getCache().remove("was_summoned");
	}

	private int getSummonReturnX() {
		if (!getCache().hasKey("return_x"))
			return -1;

		return getCache().getInt("return_x");
	}

	private int getSummonReturnY() {
		if (!getCache().hasKey("return_y"))
			return -1;

		return getCache().getInt("return_y");
	}

	public Point returnFromSummon() {
		if (!wasSummoned())
			return null;

		Point originalLocation = getLocation();
		teleport(getSummonReturnX(), getSummonReturnY(), true);
		resetSummonReturnPoint();
		return originalLocation;
	}

	public void setSummoned(boolean wasSummoned) {
		getCache().store("was_summoned", wasSummoned);
	}

	public boolean wasSummoned() {
		if (!getCache().hasKey("was_summoned"))
			return false;

		return getCache().getBoolean("was_summoned");
	}

	public Point jail() {
		Point originalLocation = getLocation();
		setJailReturnPoint();
		teleport(75, 1641, true);
		return originalLocation;
	}

	private void setJailReturnPoint() {
		if (isJailed())
			return;

		getCache().set("jail_return_x", getX());
		getCache().set("jail_return_y", getY());
		getCache().store("is_jailed", true);
	}

	private void resetJailReturnPoint() {
		getCache().remove("jail_return_x");
		getCache().remove("jail_return_y");
		getCache().remove("is_jailed");
	}

	private int getJailReturnX() {
		if (!getCache().hasKey("jail_return_x"))
			return -1;

		return getCache().getInt("jail_return_x");
	}

	private int getJailReturnY() {
		if (!getCache().hasKey("jail_return_y"))
			return -1;

		return getCache().getInt("jail_return_y");
	}

	public Point releaseFromJail() {
		if (!isJailed())
			return null;

		Point originalLocation = getLocation();
		teleport(getJailReturnX(), getJailReturnY(), true);
		resetJailReturnPoint();
		return originalLocation;
	}

	public void setJailed(boolean isJailed) {
		getCache().store("is_jailed", isJailed);
	}

	public boolean isJailed() {
		if (!getCache().hasKey("is_jailed"))
			return false;

		return getCache().getBoolean("is_jailed");
	}

	public boolean groundItemTake(GroundItem item) {
		Item itemFinal = new Item(item.getID(), item.getAmount());
		if (item.getOwnerUsernameHash() == 0 || item.getAttribute("npcdrop", false)) {
			itemFinal.setAttribute("npcdrop", true);
		}

		if (!this.getInventory().canHold(itemFinal)) {
			return false;
		}

		getWorld().unregisterItem(item);
		this.playSound("takeobject");
		this.getInventory().add(itemFinal);
		getWorld().getServer().getGameLogger().addQuery(new GenericLog(this.getWorld(), this.getUsername() + " picked up " + item.getDef().getName() + " x"
			+ item.getAmount() + " at " + this.getLocation().toString()));

		return true;
	}

	public boolean checkRingOfLife(Mob hitter) {
		if (this.isPlayer() && Functions.isWielding(this, ItemId.RING_OF_LIFE.id())
			&& (!this.getLocation().inWilderness()
			|| (this.getLocation().inWilderness() && this.getLocation().wildernessLevel() <= Constants.GLORY_TELEPORT_LIMIT))) {
			if (((float) this.getSkills().getLevel(3)) / ((float) this.getSkills().getMaxStat(3)) <= 0.1f) {
				this.resetCombatEvent();
				this.resetRange();
				this.resetAll();
				hitter.resetCombatEvent();
				hitter.resetRange();
				if (hitter.isPlayer()) {
					((Player) hitter).resetAll();
				}
				this.teleport(122, 647, false);
				this.message("Your ring of Life shines brightly");
				this.getInventory().shatter(ItemId.RING_OF_LIFE.id());
				return true;
			}
		}
		return false;
	}

	public boolean shouldBreakMenu(Npc npc) {
		if (npc != null) {
			if (npc.isRemoved()) {
				this.resetMenuHandler();
				this.setOption(-1);
				this.setBusy(false);
				return true;
			}
			npc.setBusy(true);
		}

		if (this.checkUnderAttack())
		{
			this.releaseUnderAttack();
			if (npc != null) {
				npc.setBusy(false);
			}
			return true;
		}
		return false;
	}
}
