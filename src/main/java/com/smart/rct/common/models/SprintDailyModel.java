package com.smart.rct.common.models;

public class SprintDailyModel {
	private long attempted;
	private long migratedCount;
	private long inProgress;
	private long Cancelled;
	public long getMigratedCount() {
		return migratedCount;
	}
	public void setMigratedCount(long migratedCount) {
		this.migratedCount = migratedCount;
	}
	public long getInProgress() {
		return inProgress;
	}
	public void setInProgress(long inProgress) {
		this.inProgress = inProgress;
	}
	public long getCancelled() {
		return Cancelled;
	}
	public void setCancelled(long cancelled) {
		Cancelled = cancelled;
	}
	public long getAttempted() {
		return attempted;
	}
	public void setAttempted(long attempted) {
		this.attempted = attempted;
	}
	
	
	
}
