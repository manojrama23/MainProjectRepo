package com.smart.rct.common.models;

public class TroubleshootTimelineDetailsModel {
	private String timelineId;
	private String inTimelineEditMode;
	private String timeLine;
	private String siteDate;
	private String siteTime;
	private String remarks;
	private String siteTimeMin;
	public String getSiteTimeMin() {
		return siteTimeMin;
	}
	public void setSiteTimeMin(String siteTimeMin) {
		this.siteTimeMin = siteTimeMin;
	}
	public String getSiteTimeHr() {
		return siteTimeHr;
	}
	public void setSiteTimeHr(String siteTimeHr) {
		this.siteTimeHr = siteTimeHr;
	}
	private String siteTimeHr;
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
