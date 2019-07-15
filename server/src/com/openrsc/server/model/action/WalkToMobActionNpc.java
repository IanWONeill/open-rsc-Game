package com.openrsc.server.model.action;

import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;

public abstract class WalkToMobActionNpc extends WalkToActionNpc {

	protected Mob mob;
	protected Mob mob2;
	private int radius;

	public WalkToMobActionNpc(Mob owner, Mob mob, int radius) {
		super(owner, mob.getLocation());
		this.mob = mob;
		this.mob2 = owner;
		this.radius = radius;
		if (shouldExecute()) {
			execute();
			owner.setWalkToActionNpc(null);
			hasExecuted = true;
		}
	}

	public Mob getMob() {
		return mob;
	}

	@Override
	public boolean shouldExecute() {
		return !hasExecuted
				&& mob2.withinRange(mob, radius)
				&& PathValidation.checkAdjacent(mob2.getLocation(), mob.getLocation(), true);
	}
}