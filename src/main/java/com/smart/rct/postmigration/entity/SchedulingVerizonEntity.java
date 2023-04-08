package com.smart.rct.postmigration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SCHEDULING_VERIZON")
public class SchedulingVerizonEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "FORECAST_START_DATE")
	private Date forecastStartDate;
	
	@Column(name = "COMP_DATE")
	private Date compDate;
	
	@Column(name = "MARKET")
	private String market;
	
	@Column(name = "ENB_ID")
	private String enbId;
	
	@Column(name = "ENB_NAME")
	private String enbName;
	
	@Column(name = "GROW_REQUEST")
	private String growRequest;
	
	@Column(name = "GROW_COMPLETED")
	private String growCompleted;
	
	@Column(name = "CIQ_PRESENT")
	private String ciqPresent;
	
	@Column(name = "ENV_COMPLETED")
	private String envCompleted;
	
	@Column(name = "STANDARD_NONSTANDARD")
	private String standardNonStandard;
	
	@Column(name = "CARRIERS")
	private String carriers;
	
	@Column(name = "UDA")
	private String uda;
	
	@Column(name = "SOFTWARE_LEVELS")
	private String softwareLevels;
	
	@Column(name = "FE_ARRIAVAL_TIME")
	private String feArrivalTime;
	
	@Column(name = "CI_START_TIME")
	private String ciStartTime;
	
	@Column(name = "DT_HANDOFF")
	private String dtHandoff;
	
	@Column(name = "CI_END_TIME")
	private String ciEndTime;
	
	@Column(name = "START_TIME")
	private String startTime;
	
	@Column(name = "END_TIME")
	private String endTime;
	
	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name = "CANC_ROLL_COMP")
	private String canRollComp;
	
	@Column(name = "TRAFFIC")
	private String traffic;
	
	@Column(name = "ALARM_PRESENT")
	private String alarmPresent;
	
	@Column(name = "CI_ENGINEER")
	private String ciEngineer;
	
	@Column(name = "FT")
	private String ft;
	
	@Column(name = "DT")
	private String dt;
	
	@Column(name = "NOTES")
	private String notes;
	
	@Column(name = "COLUMN1")
	private String column1;
	
	@Column(name = "TOTAL_LOOKUP")
	private String totalLookup;
	
	@Column(name = "RAN_ENGINEER")
	private String ranEngineer;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "REVISIT")
	private String revisit;
	
	@Column(name = "VLSM")
	private String vlsm;
	
	@Column(name = "COMMENTS",columnDefinition="LONGTEXT")
	private String comments;
	
	@Column(name = "ISSUE")
	private String issue;
	
	@Column(name = "CI")
	private String ci;
	
	@Column(name = "NON_CI")
	private String nonCi;
	
	@Column(name = "ALD")
	private String ald;
	
	@Column(name = "WEEK")
	private String week;
	
	@Column(name = "MONTH")
	private String month;
	
	@Column(name = "STATUS2")
	private String status2;
	
	@Column(name = "QUARTER")
	private String quarter;
	
	@Column(name = "YEAR")
	private String year;
	
	@Column(name = "RULE1")
	private String rule1;
	
	@Column(name = "RULE2")
	private String rule2;
	
	@Column(name = "DAY")
	private String day;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRule1() {
		return rule1;
	}

	public void setRule1(String rule1) {
		this.rule1 = rule1;
	}

	public String getRule2() {
		return rule2;
	}

	public void setRule2(String rule2) {
		this.rule2 = rule2;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Date getForecastStartDate() {
		return forecastStartDate;
	}

	public void setForecastStartDate(Date forecastStartDate) {
		this.forecastStartDate = forecastStartDate;
	}



	public Date getCompDate() {
		return compDate;
	}

	public void setCompDate(Date compDate) {
		this.compDate = compDate;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getEnbId() {
		return enbId;
	}

	public void setEnbId(String enbId) {
		this.enbId = enbId;
	}

	public String getEnbName() {
		return enbName;
	}

	public void setEnbName(String enbName) {
		this.enbName = enbName;
	}

	public String getGrowRequest() {
		return growRequest;
	}

	public void setGrowRequest(String growRequest) {
		this.growRequest = growRequest;
	}

	public String getGrowCompleted() {
		return growCompleted;
	}

	public void setGrowCompleted(String growCompleted) {
		this.growCompleted = growCompleted;
	}

	public String getCiqPresent() {
		return ciqPresent;
	}

	public void setCiqPresent(String ciqPresent) {
		this.ciqPresent = ciqPresent;
	}

	public String getEnvCompleted() {
		return envCompleted;
	}

	public void setEnvCompleted(String envCompleted) {
		this.envCompleted = envCompleted;
	}

	public String getStandardNonStandard() {
		return standardNonStandard;
	}

	public void setStandardNonStandard(String standardNonStandard) {
		this.standardNonStandard = standardNonStandard;
	}

	public String getCarriers() {
		return carriers;
	}

	public void setCarriers(String carriers) {
		this.carriers = carriers;
	}

	public String getUda() {
		return uda;
	}

	public void setUda(String uda) {
		this.uda = uda;
	}

	public String getSoftwareLevels() {
		return softwareLevels;
	}

	public void setSoftwareLevels(String softwareLevels) {
		this.softwareLevels = softwareLevels;
	}

	public String getFeArrivalTime() {
		return feArrivalTime;
	}

	public void setFeArrivalTime(String feArrivalTime) {
		this.feArrivalTime = feArrivalTime;
	}

	public String getCiStartTime() {
		return ciStartTime;
	}

	public void setCiStartTime(String ciStartTime) {
		this.ciStartTime = ciStartTime;
	}

	public String getDtHandoff() {
		return dtHandoff;
	}

	public void setDtHandoff(String dtHandoff) {
		this.dtHandoff = dtHandoff;
	}

	public String getCiEndTime() {
		return ciEndTime;
	}

	public void setCiEndTime(String ciEndTime) {
		this.ciEndTime = ciEndTime;
	}

	public String getCanRollComp() {
		return canRollComp;
	}

	public void setCanRollComp(String canRollComp) {
		this.canRollComp = canRollComp;
	}

	public String getTraffic() {
		return traffic;
	}

	public void setTraffic(String traffic) {
		this.traffic = traffic;
	}

	public String getAlarmPresent() {
		return alarmPresent;
	}

	public void setAlarmPresent(String alarmPresent) {
		this.alarmPresent = alarmPresent;
	}

	public String getCiEngineer() {
		return ciEngineer;
	}

	public void setCiEngineer(String ciEngineer) {
		this.ciEngineer = ciEngineer;
	}

	public String getFt() {
		return ft;
	}

	public void setFt(String ft) {
		this.ft = ft;
	}

	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getColumn1() {
		return column1;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public String getTotalLookup() {
		return totalLookup;
	}

	public void setTotalLookup(String totalLookup) {
		this.totalLookup = totalLookup;
	}

	public String getRanEngineer() {
		return ranEngineer;
	}

	public void setRanEngineer(String ranEngineer) {
		this.ranEngineer = ranEngineer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRevisit() {
		return revisit;
	}

	public void setRevisit(String revisit) {
		this.revisit = revisit;
	}

	public String getVlsm() {
		return vlsm;
	}

	public void setVlsm(String vlsm) {
		this.vlsm = vlsm;
	}


	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public String getNonCi() {
		return nonCi;
	}

	public void setNonCi(String nonCi) {
		this.nonCi = nonCi;
	}

	public String getAld() {
		return ald;
	}

	public void setAld(String ald) {
		this.ald = ald;
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

	public String getStatus2() {
		return status2;
	}

	public void setStatus2(String status2) {
		this.status2 = status2;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	

}
