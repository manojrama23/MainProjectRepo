package com.smart.rct.common.models;

public class TimeLineDetailsModel {
	private String timelineId;
	private String inTimelineEditMode;
	private String timeLine;
	private String siteDate;
	private String siteTime;
	private String remarks;
	public String getTimeLine() {
		return timeLine;
	}
	public void setTimeLine(String timeLine) {
		this.timeLine = timeLine;
	}
	
	public String getSiteDate() {
		return siteDate;
	}
	public void setSiteDate(String siteDate) {
		this.siteDate = siteDate;
	}
	public String getSiteTime() {
		return siteTime;
	}
	public void setSiteTime(String siteTime) {
		this.siteTime = siteTime;
	}
	public String getTimelineId() {
		return timelineId;
	}
	public void setTimelineId(String timelineId) {
		this.timelineId = timelineId;
	}
	public String getInTimelineEditMode() {
		return inTimelineEditMode;
	}
	public void setInTimelineEditMode(String inTimelineEditMode) {
		this.inTimelineEditMode = inTimelineEditMode;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	

}
