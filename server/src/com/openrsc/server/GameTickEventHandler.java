package com.openrsc.server;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.GameNotifyEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameTickEventHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final ConcurrentHashMap<String, GameTickEvent> events = new ConcurrentHashMap<String, GameTickEvent>();
	private final ConcurrentHashMap<String, GameTickEvent> eventsToAdd = new ConcurrentHashMap<String, GameTickEvent>();

	private final HashMap<String, Integer> eventsCounts = new HashMap<String, Integer>();
	private final HashMap<String, Long> eventsDurations = new HashMap<String, Long>();

	private final Server server;

	public GameTickEventHandler(Server server) {
		this.server = server;
	}

	public void add(GameTickEvent event) {
		String className = String.valueOf(event.getClass());
		if (event.getOwner() == null) { // Server events, no owner.
			eventsToAdd.merge(className + UUID.randomUUID(), event, (oldEvent, newEvent) -> newEvent);
		} else {
			if (event.getOwner().isPlayer())
				eventsToAdd.merge(className + event.getOwner().getUUID() + "p", event, (oldEvent, newEvent) -> newEvent);
			else
				eventsToAdd.merge(className + event.getOwner().getUUID() + "n", event, (oldEvent, newEvent) -> newEvent);
		}
	}

	public void add(DelayedEvent event) {
		String className = String.valueOf(event.getClass());
		UUID uuid = UUID.randomUUID();
		if (event.isUniqueEvent() || !event.hasOwner()) {
			eventsToAdd.putIfAbsent(className + uuid, event);
		} else {
			if (event.getOwner().isPlayer())
				eventsToAdd.putIfAbsent(className + event.getOwner().getUUID() + "p", event);
			else
				eventsToAdd.putIfAbsent(className + event.getOwner().getUUID() + "n", event);
		}
	}

	public boolean contains(GameTickEvent event) {
		if (event.getOwner() != null)
			return events.containsKey(String.valueOf(event.getOwner().getID()));
		return false;
	}

	private void processEvents() {
		if (eventsToAdd.size() > 0) {
			for (Iterator<Map.Entry<String, GameTickEvent>> iter = eventsToAdd.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry<String, GameTickEvent> e = iter.next();
				events.merge(e.getKey(), e.getValue(), (oldEvent, newEvent) -> newEvent);
				iter.remove();
			}
		}

		// Sort the Events Hashmap such that the following execution order is preserved: GameStateEvent -> GameNotifyEvent -> Everything else
		List list = new LinkedList(events.entrySet());
		Collections.sort(list, (Object o1, Object o2) -> {
			if(o1 instanceof GameStateEvent && !(o2 instanceof GameStateEvent)) {
				return -1;
			}
			else if (!(o1 instanceof GameStateEvent) && o2 instanceof GameStateEvent){
				 return 1;
			}
			else {
				if(o1 instanceof GameNotifyEvent && !(o2 instanceof GameNotifyEvent)) {
					return -1;
				}
				else if (!(o1 instanceof GameNotifyEvent) && o2 instanceof GameNotifyEvent){
					return 1;
				}
				else {
					return 0;
				}
			}
		});
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		events.clear();
		events.putAll(sortedHashMap);

		for (Iterator<Map.Entry<String, GameTickEvent>> it = events.entrySet().iterator(); it.hasNext(); ) {
			GameTickEvent event = it.next().getValue();
			if (event == null || event.getOwner() != null && event.getOwner().isUnregistering()) {
				it.remove();
				continue;
			}
			try {
				event.countdown();
				if (event.shouldRun()) {
					event.doRun();
					event.resetCountdown();
				}
			} catch (Exception e) {
				LOGGER.catching(e);
				event.stop();
			}

			if (!eventsCounts.containsKey(event.getDescriptor())) {
				eventsCounts.put(event.getDescriptor(), 1);
			} else {
				eventsCounts.put(event.getDescriptor(), eventsCounts.get(event.getDescriptor()) + 1);
			}

			if (!eventsDurations.containsKey(event.getDescriptor())) {
				eventsDurations.put(event.getDescriptor(), event.getLastEventDuration());
			} else {
				eventsDurations.put(event.getDescriptor(), eventsDurations.get(event.getDescriptor()) + event.getLastEventDuration());
			}

			if (event.shouldRemove()) {
				it.remove();
			}
		}
	}

	public long runGameEvents() {
		final long eventsStart = System.currentTimeMillis();

		eventsCounts.clear();
		eventsDurations.clear();

		processEvents();

		final long eventsEnd = System.currentTimeMillis();
		return eventsEnd - eventsStart;
	}

	public final String buildProfilingDebugInformation(boolean forInGame) {
		int countAllEvents = 0;
		long durationAllEvents = 0;
		String newLine = forInGame ? "%" : "\r\n";

		final HashMap<String, Integer> eventsCounts = getEventsCounts();
		final HashMap<String, Long> eventsDurations = getEventsDurations();

		// Calculate Totals
		for (Map.Entry<String, Integer> eventEntry : eventsCounts.entrySet()) {
			countAllEvents += eventEntry.getValue();
		}
		for (Map.Entry<String, Long> eventEntry : eventsDurations.entrySet()) {
			durationAllEvents += eventEntry.getValue();
		}

		// Sort the Events Hashmap
		List list = new LinkedList(eventsDurations.entrySet());
		Collections.sort(list, (Object o1, Object o2) -> {
			int o1EventCount = eventsCounts.get(((Map.Entry) (o1)).getKey());
			int o2EventCount = eventsCounts.get(((Map.Entry) (o2)).getKey());
			long o1EventDuration = eventsDurations.get(((Map.Entry) (o1)).getKey());
			long o2EventDuration = eventsDurations.get(((Map.Entry) (o2)).getKey());

			if(o1EventDuration == o2EventDuration) {
				return o1EventCount < o2EventCount ? 1 : -1;
			} else {
				return o1EventDuration < o2EventDuration ? 1 : -1;
			}
		});
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		eventsDurations.clear();
		eventsDurations.putAll(sortedHashMap);

		int i = 0;
		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, Long> entry : eventsDurations.entrySet()) {
			if (forInGame && i >= 17) // Only display first 17 elements of the hashmap
				break;

			String name = entry.getKey();
			Long duration = entry.getValue();
			Integer count = eventsCounts.get(entry.getKey());
			s.append(name).append(" : ").append(duration).append("ms").append(" : ").append(count).append(newLine);
			++i;
		}

		String returnString = (
			"Tick: " + getServer().getConfig().GAME_TICK + "ms, Server: " + getServer().getLastTickDuration() + "ms " + getServer().getLastIncomingPacketsDuration() + "ms " + getServer().getLastEventsDuration() + "ms " + getServer().getLastGameStateDuration() + "ms " + getServer().getLastOutgoingPacketsDuration() + "ms" + newLine +
				"Game Updater: " + getServer().getGameUpdater().getLastProcessPlayersDuration() + "ms " + getServer().getGameUpdater().getLastProcessNpcsDuration() + "ms " + getServer().getGameUpdater().getLastProcessMessageQueuesDuration() + "ms " + getServer().getGameUpdater().getLastUpdateClientsDuration() + "ms " + getServer().getGameUpdater().getLastDoCleanupDuration() + "ms " + getServer().getGameUpdater().getLastExecuteWalkToActionsDuration() + "ms " + newLine +
				"Events: " + countAllEvents + ", NPCs: " + getServer().getWorld().getNpcs().size() + ", Players: " + getServer().getWorld().getPlayers().size() + ", Shops: " + getServer().getWorld().getShops().size() + newLine +
				/*"Player Atk Map: " + getWorld().getPlayersUnderAttack().size() + ", NPC Atk Map: " + getWorld().getNpcsUnderAttack().size() + ", Quests: " + getWorld().getQuests().size() + ", Mini Games: " + getWorld().getMiniGames().size() + newLine +*/
				s
		);

		return returnString.substring(0, returnString.length() > 1999 ? 1999 : returnString.length()); // Limit to 2000 characters for Discord.
	}

	public HashMap<String, GameTickEvent> getEvents() {
		return new LinkedHashMap<String, GameTickEvent>(events);
	}

	public void remove(GameTickEvent event) {
		events.remove(event);
	}

	public void removePlayersEvents(Player player) {
		try {
			Iterator<Map.Entry<String, GameTickEvent>> iterator = events.entrySet().iterator();
			while (iterator.hasNext()) {
				GameTickEvent event = iterator.next().getValue();
				if (event.belongsTo(player)) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public HashMap<String, Integer> getEventsCounts() {
		return new LinkedHashMap<String, Integer>(eventsCounts);
	}

	public HashMap<String, Long> getEventsDurations() {
		return new LinkedHashMap<String, Long>(eventsDurations);
	}

	public final Server getServer() {
		return server;
	}
}
