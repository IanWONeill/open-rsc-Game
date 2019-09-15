package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GameNotifyEvent extends GameStateEvent {

	private GameStateEvent parentEvent;
	private final Map<String, Object> inObjects = new ConcurrentHashMap<>();
	private final Map<String, Object> outObjects = new ConcurrentHashMap<>();
	private int returnState;
	private int returnDelay;
	private boolean triggered = false;

	public GameNotifyEvent(World world, Mob owner, int ticks, String descriptor) {
		super(world, owner, ticks, descriptor);
	}

	@Override
	public void stop() {
		super.stop();
		trigger();
	}

	public void setParentEvent(GameStateEvent event) {
		this.parentEvent = event;
	}

	public void trigger() {
		if(!isTriggered()) {
			triggered = true;
			restoreParent();
			onTriggered();
		}
	}

	public void onTriggered() {}

	private void restoreParent() {
		getParentEvent().setState(getReturnState());
		getParentEvent().setDelayTicks(getReturnDelay());
	}

	public boolean isTriggered() { return triggered; }

	public void addObjectOut(String name, Object item) {
		outObjects.put(name, item);
	}

	public void addObjectIn(String name, Object item) {
		inObjects.put(name, item);
	}

	public Object getObjectOut(String name) {
		return outObjects.get(name);
	}

	public Object getObjectIn(String name) {
		return inObjects.get(name);
	}

	public int getReturnState() {
		return returnState;
	}

	public void setReturnState(int returnState) {
		this.returnState = returnState;
	}

	public int getReturnDelay() {
		return returnDelay;
	}

	public void setReturnDelay(int returnDelay) {
		this.returnDelay = returnDelay;
	}

	public GameStateEvent getParentEvent() {
		return parentEvent;
	}
}
