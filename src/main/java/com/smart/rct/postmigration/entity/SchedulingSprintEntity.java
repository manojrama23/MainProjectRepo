package com.smart.rct.postmigration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SCHEDULING_SPRINT")
public class SchedulingSprintEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "REGION")
	private String region;
	
	@Column(name = "MARKET")
	private String market;
	
	@Column(name = "CI_ENGINEER_NIGHT")
	private String ciEngineerNight;
	
	@Column(name = "BRIDGE_ONE")
	private String bridgeOne;
	
	@Column(name = "FE_REGION")
	private String feRegion;
	
	@Column(name = "FE_NIGHT")
	private String feNight;
	
	@Column(name = "CI_ENGINEER_DAY")
	private String ciEngineerDay;
	
	@Column(name = "BRIDGE")
	private String bridge;
	
	@Column(name = "FE_DAY")
	private String feDay;
	
	@Column(name = "NOTES")
	private String notes;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "START_DATE")
	private Date startDate;
	
	@Column(name = "CASCADE_ID")
	private String cascade;
	
	@Column(name = "DAY")
	private String day;
	
	@Column(name = "WEEK")
	private String week;
	
	@Column(name = "MONTH")
	private String month;
	
	@Column(name = "QTR")
	private String qtr;
	
	@Column(name = "YEAR")
	private String year;
	
	@Column(name = "TYPE")
	private String type;
	
	@Column(name = "SITE_REVISIT")
	private String siteRevisit;
	
	@Column(name = "GOLDEN_CLUSTER")
	private String goldenCluster;
	
	@Column(name = "ACTUAL_MIGRATION_START_DATE")
	private Date actualMigrationStartDate;
	
	@Column(name = "COMP_DATE")
	private Date compDate;
	
	@Column(name = "ENB_ID")
	private String enbId;
	
	@Column(name = "5G")
	private String fiveG;
	
	@Column(name = "TYPE_ONE")
	private String typeOne;
	
	@Column(name = "TVW")
	private String tvw;
	
	@Column(name = "CURRENT_SOFTWARE")
	private String currentSoftware;
	
	@Column(name = "SCRIPTS_RAN")
	private String scriptsRan;

	@Column(name = "DSP_IMPLEMENTED")
	private String dspImplemented;

	@Column(name = "CI_ENGINEER_ONE")
	private String ciEngineerOne;

	@Column(name = "CI_START_TIME_ONE")
	private String ciStartTimeOne;

	@Column(name = "CI_END_TIME_ONE")
	private String ciEndTimeOne;

	@Column(name = "FE_ONE")
	private String feOne;

	@Column(name = "FE_CONTACT_INFO_ONE")
	private String feContactInfoOne;

	@Column(name = "FE_ARRIVAL_TIME_ONE")
	private String feArrivalTimeOne;
	
	@Column(name = "CI_ENGINEER_TWO")
	private String ciEngineerTwo;

	@Column(name = "CI_START_TIME_TWO")
	private String ciStartTimeTwo;

	@Column(name = "CI_END_TIME_TWO")
	private String ciEndTimeTwo;

	@Column(name = "FE_TWO")
	private String feTwo;

	@Column(name = "FE_CONTACT_INFO_TWO")
	private String feContactInfoTwo;

	@Column(name = "FE_ARRIVAL_TIME_TWO")
	private String feArrivalTimeTwo;
	
	@Column(name = "CI_ENGINEER_THREE")
	private String ciEngineerThree;

	@Column(name = "CI_START_TIME_THREE")
	private String ciStartTimeThree;

	@Column(name = "CI_END_TIME_THREE")
	private String ciEndTimeThree;

	@Column(name = "FE_THREE")
	private String feThree;

	@Column(name = "FE_CONTACT_INFO_THREE")
	private String feContactInfoThree;

	@Column(name = "FE_ARRIVAL_TIME_THREE")
	private String feArrivalTimeThree;
	
	@Column(name = "GC")
	private String gc;

	@Column(name = "GC_ARRIVAL_TIME")
	private String gcArrivalTime;

	@Column(name = "PUT_TOOL")
	private String putTool;

	@Column(name = "SCRIPT_ERRORS")
	private String scriptErrors;

	@Column(name = "REASON_CODE")
	private String reasonCode;

	@Column(name = "CI_ISSUE")
	private String ciIssue;

	@Column(name = "NON_CI_ISSUE")
	private String nonCiIssue;

	@Column(name = "ENGINEER_ONE_NOTES",columnDefinition="LONGTEXT")
	private String engineerOneNotes;

	@Column(name = "ENGINEER_TWO_NOTES",columnDefinition="LONGTEXT")
	private String engineerTwoNotes;
	
	@Column(name = "ENGINEER_THREE_NOTES",columnDefinition="LONGTEXT")
	private String engineerThreeNotes;
	
	@Column(name = "CIRCUIT_BREAKER_START")
	private String circuitbreakerStart;
	
	@Column(name = "CIRCUIT_BREAKER_END")
	private String circuitbreakerEnd;
	
	@Column(name = "ALPHA_START_TIME")
	private String alphaStartTime;
	
	@Column(name = "SCHEDULE_TIME")
	private Date scheduleDate;
	
	@Column(name = "DT_OR_MW")
	private String dtOrMw;
	
	@Column(name = "TC_NAME")
	private String tcName;
	
	@Column(name = "TC_CONTACT_INFO")
	private String tcContactInfo;
	
	@Column(name = "RESOLUTION")
	private String resolution;
	
	@Column(name = "NVTF_NO_HARM")
	private String nvtfNoHarm;
	
	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getAlphaStartTime() {
		return alphaStartTime;
	}

	public void setAlphaStartTime(String alphaStartTime) {
		this.alphaStartTime = alphaStartTime;
	}

	public String getAlphaEndTime() {
		return alphaEndTime;
	}

	public void setAlphaEndTime(String alphaEndTime) {
		this.alphaEndTime = alphaEndTime;
	}

	public String getBetaStartTime() {
		return betaStartTime;
	}

	public void setBetaStartTime(String betaStartTime) {
		this.betaStartTime = betaStartTime;
	}

	public String getBetaEndTime() {
		return betaEndTime;
	}

	public void setBetaEndTime(String betaEndTime) {
		this.betaEndTime = betaEndTime;
	}

	public String getGammaStartTime() {
		return gammaStartTime;
	}

	public void setGammaStartTime(String gammaStartTime) {
		this.gammaStartTime = gammaStartTime;
	}

	public String getGammaEndTime() {
		return gammaEndTime;
	}

	public void setGammaEndTime(String gammaEndTime) {
		this.gammaEndTime = gammaEndTime;
	}

	@Column(name = "ALPHA_END_TIME")
	private String alphaEndTime;
	
	@Column(name = "BETA_START_TIME")
	private String betaStartTime;
	
	@Column(name = "BETA_END_TIME")
	private String betaEndTime;
	
	@Column(name = "GAMMA_START_TIME")
	private String gammaStartTime;
	
	@Column(name = "GAMMA_END_TIME")
	private String gammaEndTime;

	public Integer getId() {
		return id;
	}

	public String getCircuitbreakerStart() {
		return circuitbreakerStart;
	}

	public void setCircuitbreakerStart(String circuitbreakerStart) {
		this.circuitbreakerStart = circuitbreakerStart;
	}

	public String getCircuitbreakerEnd() {
		return circuitbreakerEnd;
	}

	public void setCircuitbreakerEnd(String circuitbreakerEnd) {
		this.circuitbreakerEnd = circuitbreakerEnd;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getCiEngineerNight() {
		return ciEngineerNight;
	}

	public void setCiEngineerNight(String ciEngineerNight) {
		this.ciEngineerNight = ciEngineerNight;
	}


	public String getBridgeOne() {
		return bridgeOne;
	}

	public void setBridgeOne(String bridgeOne) {
		this.bridgeOne = bridgeOne;
	}

	public String getFeRegion() {
		return feRegion;
	}

	public void setFeRegion(String feRegion) {
		this.feRegion = feRegion;
	}

	public String getFeNight() {
		return feNight;
	}

	public void setFeNight(String feNight) {
		this.feNight = feNight;
	}

	public String getCiEngineerDay() {
		return ciEngineerDay;
	}

	public void setCiEngineerDay(String ciEngineerDay) {
		this.ciEngineerDay = ciEngineerDay;
	}

	public String getBridge() {
		return bridge;
	}

	public void setBridge(String bridge) {
		this.bridge = bridge;
	}

	public String getFeDay() {
		return feDay;
	}

	public void setFeDay(String feDay) {
		this.feDay = feDay;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getCascade() {
		return cascade;
	}

	public void setCascade(String cascade) {
		this.cascade = cascade;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getQtr() {
		return qtr;
	}

	public void setQtr(String qtr) {
		this.qtr = qtr;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSiteRevisit() {
		return siteRevisit;
	}

	public void setSiteRevisit(String siteRevisit) {
		this.siteRevisit = siteRevisit;
	}

	public String getGoldenCluster() {
		return goldenCluster;
	}

	public void setGoldenCluster(String goldenCluster) {
		this.goldenCluster = goldenCluster;
	}



	public Date getActualMigrationStartDate() {
		return actualMigrationStartDate;
	}

	public void setActualMigrationStartDate(Date actualMigrationStartDate) {
		this.actualMigrationStartDate = actualMigrationStartDate;
	}

	public Date getCompDate() {
		return compDate;
	}

	public void setCompDate(Date compDate) {
		this.compDate = compDate;
	}

	public String getEnbId() {
		return enbId;
	}

	public void setEnbId(String enbId) {
		this.enbId = enbId;
	}

	public String getFiveG() {
		return fiveG;
	}

	public void setFiveG(String fiveG) {
		this.fiveG = fiveG;
	}

	public String getTypeOne() {
		return typeOne;
	}

	public void setTypeOne(String typeOne) {
		this.typeOne = typeOne;
	}

	public String getTvw() {
		return tvw;
	}

	public void setTvw(String tvw) {
		this.tvw = tvw;
	}

	public String getCurrentSoftware() {
		return currentSoftware;
	}

	public void setCurrentSoftware(String currentSoftware) {
		this.currentSoftware = currentSoftware;
	}

	public String getScriptsRan() {
		return scriptsRan;
	}

	public void setScriptsRan(String scriptsRan) {
		this.scriptsRan = scriptsRan;
	}

	public String getDspImplemented() {
		return dspImplemented;
	}

	public void setDspImplemented(String dspImplemented) {
		this.dspImplemented = dspImplemented;
	}

	public String getCiEngineerOne() {
		return ciEngineerOne;
	}

	public void setCiEngineerOne(String ciEngineerOne) {
		this.ciEngineerOne = ciEngineerOne;
	}

	public String getCiStartTimeOne() {
		return ciStartTimeOne;
	}

	public void setCiStartTimeOne(String ciStartTimeOne) {
		this.ciStartTimeOne = ciStartTimeOne;
	}

	public String getCiEndTimeOne() {
		return ciEndTimeOne;
	}

	public void setCiEndTimeOne(String ciEndTimeOne) {
		this.ciEndTimeOne = ciEndTimeOne;
	}

	public String getFeOne() {
		return feOne;
	}

	public void setFeOne(String feOne) {
		this.feOne = feOne;
	}

	public String getFeContactInfoOne() {
		return feContactInfoOne;
	}

	public void setFeContactInfoOne(String feContactInfoOne) {
		this.feContactInfoOne = feContactInfoOne;
	}

	public String getFeArrivalTimeOne() {
		return feArrivalTimeOne;
	}

	public void setFeArrivalTimeOne(String feArrivalTimeOne) {
		this.feArrivalTimeOne = feArrivalTimeOne;
	}

	public String getCiEngineerTwo() {
		return ciEngineerTwo;
	}

	public void setCiEngineerTwo(String ciEngineerTwo) {
		this.ciEngineerTwo = ciEngineerTwo;
	}

	public String getCiStartTimeTwo() {
		return ciStartTimeTwo;
	}

	public void setCiStartTimeTwo(String ciStartTimeTwo) {
		this.ciStartTimeTwo = ciStartTimeTwo;
	}

	public String getCiEndTimeTwo() {
		return ciEndTimeTwo;
	}

	public void setCiEndTimeTwo(String ciEndTimeTwo) {
		this.ciEndTimeTwo = ciEndTimeTwo;
	}

	public String getFeTwo() {
		return feTwo;
	}

	public void setFeTwo(String feTwo) {
		this.feTwo = feTwo;
	}

	public String getFeContactInfoTwo() {
		return feContactInfoTwo;
	}

	public void setFeContactInfoTwo(String feContactInfoTwo) {
		this.feContactInfoTwo = feContactInfoTwo;
	}

	public String getFeArrivalTimeTwo() {
		return feArrivalTimeTwo;
	}

	public void setFeArrivalTimeTwo(String feArrivalTimeTwo) {
		this.feArrivalTimeTwo = feArrivalTimeTwo;
	}

	public String getGc() {
		return gc;
	}

	public void setGc(String gc) {
		this.gc = gc;
	}

	public String getGcArrivalTime() {
		return gcArrivalTime;
	}

	public void setGcArrivalTime(String gcArrivalTime) {
		this.gcArrivalTime = gcArrivalTime;
	}

	public String getPutTool() {
		return putTool;
	}

	public void setPutTool(String putTool) {
		this.putTool = putTool;
	}

	public String getScriptErrors() {
		return scriptErrors;
	}

	public void setScriptErrors(String scriptErrors) {
		this.scriptErrors = scriptErrors;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getCiIssue() {
		return ciIssue;
	}

	public void setCiIssue(String ciIssue) {
		this.ciIssue = ciIssue;
	}

	public String getNonCiIssue() {
		return nonCiIssue;
	}

	public void setNonCiIssue(String nonCiIssue) {
		this.nonCiIssue = nonCiIssue;
	}

	public String getEngineerOneNotes() {
		return engineerOneNotes;
	}

	public void setEngineerOneNotes(String engineerOneNotes) {
		this.engineerOneNotes = engineerOneNotes;
	}

	public String getEngineerTwoNotes() {
		return engineerTwoNotes;
	}

	public void setEngineerTwoNotes(String engineerTwoNotes) {
		this.engineerTwoNotes = engineerTwoNotes;
	}

	public String getCiEngineerThree() {
		return ciEngineerThree;
	}

	public void setCiEngineerThree(String ciEngineerThree) {
		this.ciEngineerThree = ciEngineerThree;
	}

	public String getCiStartTimeThree() {
		return ciStartTimeThree;
	}

	public void setCiStartTimeThree(String ciStartTimeThree) {
		this.ciStartTimeThree = ciStartTimeThree;
	}

	public String getCiEndTimeThree() {
		return ciEndTimeThree;
	}

	public void setCiEndTimeThree(String ciEndTimeThree) {
		this.ciEndTimeThree = ciEndTimeThree;
	}

	public String getFeThree() {
		return feThree;
	}

	public void setFeThree(String feThree) {
		this.feThree = feThree;
	}

	public String getFeContactInfoThree() {
		return feContactInfoThree;
	}

	public void setFeContactInfoThree(String feContactInfoThree) {
		this.feContactInfoThree = feContactInfoThree;
	}

	public String getFeArrivalTimeThree() {
		return feArrivalTimeThree;
	}

	public void setFeArrivalTimeThree(String feArrivalTimeThree) {
		this.feArrivalTimeThree = feArrivalTimeThree;
	}

	public String getEngineerThreeNotes() {
		return engineerThreeNotes;
	}

	public void setEngineerThreeNotes(String engineerThreeNotes) {
		this.engineerThreeNotes = engineerThreeNotes;
	}

	public String getDtOrMw() {
		return dtOrMw;
	}

	public void setDtOrMw(String dtOrMw) {
		this.dtOrMw = dtOrMw;
	}

	public String getTcName() {
		return tcName;
	}

	public void setTcName(String tcName) {
		this.tcName = tcName;
	}

	public String getTcContactInfo() {
		return tcContactInfo;
	}

	public void setTcContactInfo(String tcContactInfo) {
		this.tcContactInfo = tcContactInfo;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getNvtfNoHarm() {
		return nvtfNoHarm;
	}

	public void setNvtfNoHarm(String nvtfNoHarm) {
		this.nvtfNoHarm = nvtfNoHarm;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	
	
}
