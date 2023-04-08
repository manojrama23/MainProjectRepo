package com.smart.rct.premigration.models;

public class DashBoardModel {
	private String activeUsersCount;
	private String totalMemory;
	private String freeMemory;
	private String usedMemory;
	private String freeMemoryPercentage;
	private String usedMemoryPercentage;

	private String diskTotalSpace;
	private String diskFreeSpace;
	private String usedDiskSpace;
	private String freeDiskSpacePercentage;
	private String usedDiskSpacePercentage;
	private String scheduledPercentage;
	private String cancelledPercentage;

	public String getActiveUsersCount() {
		return activeUsersCount;
	}

	public void setActiveUsersCount(String activeUsersCount) {
		this.activeUsersCount = activeUsersCount;
	}

	public String getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(String totalMemory) {
		this.totalMemory = totalMemory;
	}

	public String getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}

	public String getDiskTotalSpace() {
		return diskTotalSpace;
	}

	public void setDiskTotalSpace(String diskTotalSpace) {
		this.diskTotalSpace = diskTotalSpace;
	}

	public String getDiskFreeSpace() {
		return diskFreeSpace;
	}

	public void setDiskFreeSpace(String diskFreeSpace) {
		this.diskFreeSpace = diskFreeSpace;
	}

	public String getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(String usedMemory) {
		this.usedMemory = usedMemory;
	}

	public String getFreeMemoryPercentage() {
		return freeMemoryPercentage;
	}

	public void setFreeMemoryPercentage(String freeMemoryPercentage) {
		this.freeMemoryPercentage = freeMemoryPercentage;
	}

	public String getUsedMemoryPercentage() {
		return usedMemoryPercentage;
	}

	public void setUsedMemoryPercentage(String usedMemoryPercentage) {
		this.usedMemoryPercentage = usedMemoryPercentage;
	}

	public String getUsedDiskSpace() {
		return usedDiskSpace;
	}

	public void setUsedDiskSpace(String usedDiskSpace) {
		this.usedDiskSpace = usedDiskSpace;
	}

	public String getFreeDiskSpacePercentage() {
		return freeDiskSpacePercentage;
	}

	public void setFreeDiskSpacePercentage(String freeDiskSpacePercentage) {
		this.freeDiskSpacePercentage = freeDiskSpacePercentage;
	}

	public String getUsedDiskSpacePercentage() {
		return usedDiskSpacePercentage;
	}

	public void setUsedDiskSpacePercentage(String usedDiskSpacePercentage) {
		this.usedDiskSpacePercentage = usedDiskSpacePercentage;
	}

	public String getScheduledPercentage() {
		return scheduledPercentage;
	}

	public void setScheduledPercentage(String scheduledPercentage) {
		this.scheduledPercentage = scheduledPercentage;
	}

	public String getCancelledPercentage() {
		return cancelledPercentage;
	}

	public void setCancelledPercentage(String cancelledPercentage) {
		this.cancelledPercentage = cancelledPercentage;
	}

}
