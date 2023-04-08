package com.smart.rct.postmigration.dto;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.postmigration.models.SchedulingSprintModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Component
public class SchedulingSRDto {
	final static Logger logger = LoggerFactory.getLogger(SchedulingSRDto.class);

	public SchedulingVerizonEntity getVerizonSchedulingEntity(SchedulingVerizonModel schedulingVerizonModel) {
		SchedulingVerizonEntity schedulingVerizonEntity = null;
		try{
			if (schedulingVerizonModel != null) {
				schedulingVerizonEntity = new SchedulingVerizonEntity();
				if (schedulingVerizonModel.getId() != null && schedulingVerizonModel.getId() != 0) {
					schedulingVerizonEntity.setId(Integer.valueOf(schedulingVerizonModel.getId()));
				}
				schedulingVerizonEntity.setCreationDate(new Date());
				schedulingVerizonEntity.setForecastStartDate((DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_)));;
				schedulingVerizonEntity.setCompDate((DateUtil.stringToDate(schedulingVerizonModel.getCompDate(), Constants.DD_MM_YYYY_)));
				schedulingVerizonEntity.setMarket(schedulingVerizonModel.getMarket());
				schedulingVerizonEntity.setEnbId(schedulingVerizonModel.getEnbId());
				schedulingVerizonEntity.setEnbName(schedulingVerizonModel.getEnbName());
				schedulingVerizonEntity.setGrowRequest(schedulingVerizonModel.getGrowRequest());
				schedulingVerizonEntity.setGrowCompleted(schedulingVerizonModel.getGrowCompleted());
				schedulingVerizonEntity.setCiqPresent(schedulingVerizonModel.getCiqPresent());
				schedulingVerizonEntity.setEnvCompleted(schedulingVerizonModel.getEnvCompleted());
				schedulingVerizonEntity.setStandardNonStandard(schedulingVerizonModel.getStandardNonStandard());
				schedulingVerizonEntity.setCarriers(schedulingVerizonModel.getCarriers());
				schedulingVerizonEntity.setUda(schedulingVerizonModel.getUda());
				schedulingVerizonEntity.setSoftwareLevels(schedulingVerizonModel.getSoftwareLevels());
				schedulingVerizonEntity.setFeArrivalTime(schedulingVerizonModel.getFeArrivalTime());
				schedulingVerizonEntity.setCiStartTime(schedulingVerizonModel.getCiStartTime());
				schedulingVerizonEntity.setStartTime(schedulingVerizonModel.getStartTime());
				schedulingVerizonEntity.setDtHandoff(schedulingVerizonModel.getDtHandoff());
				schedulingVerizonEntity.setCiEndTime(schedulingVerizonModel.getCiEndTime());
				schedulingVerizonEntity.setCanRollComp(schedulingVerizonModel.getCanRollComp());
				schedulingVerizonEntity.setTraffic(schedulingVerizonModel.getTraffic());
				schedulingVerizonEntity.setAlarmPresent(schedulingVerizonModel.getAlarmPresent());
				schedulingVerizonEntity.setCiEngineer(schedulingVerizonModel.getCiEngineer());
				schedulingVerizonEntity.setFt(schedulingVerizonModel.getFt());
				schedulingVerizonEntity.setDt(schedulingVerizonModel.getDt());
				schedulingVerizonEntity.setNotes(schedulingVerizonModel.getNotes());
				schedulingVerizonEntity.setTotalLookup(schedulingVerizonModel.getTotalLookup());
				schedulingVerizonEntity.setColumn1(schedulingVerizonModel.getColumn1());
				schedulingVerizonEntity.setRanEngineer(schedulingVerizonModel.getRanEngineer());
				schedulingVerizonEntity.setStatus(schedulingVerizonModel.getStatus());
				schedulingVerizonEntity.setRevisit(schedulingVerizonModel.getRevisit());
				schedulingVerizonEntity.setVlsm(schedulingVerizonModel.getVlsm());
				schedulingVerizonEntity.setEndTime(schedulingVerizonModel.getEndTime());
				schedulingVerizonEntity.setComments(schedulingVerizonModel.getComments());
				schedulingVerizonEntity.setIssue(schedulingVerizonModel.getIssue());
				schedulingVerizonEntity.setCi(schedulingVerizonModel.getCi());
				schedulingVerizonEntity.setNonCi(schedulingVerizonModel.getNonCi());
				schedulingVerizonEntity.setAld(schedulingVerizonModel.getAld());
				schedulingVerizonEntity.setWeek(schedulingVerizonModel.getWeek());
				schedulingVerizonEntity.setMonth(schedulingVerizonModel.getMonth());
				schedulingVerizonEntity.setStatus2(schedulingVerizonModel.getStatus2());
				schedulingVerizonEntity.setQuarter(schedulingVerizonModel.getQuarter());
				schedulingVerizonEntity.setYear(schedulingVerizonModel.getYear());
				schedulingVerizonEntity.setRule1(schedulingVerizonModel.getRule1());
				schedulingVerizonEntity.setRule2(schedulingVerizonModel.getRule2());
				schedulingVerizonEntity.setDay(schedulingVerizonModel.getDay());
				
			}
		}catch (Exception e) {
			logger.error("Excpetion getVerizonSchedulingEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingVerizonEntity;
	}

	public SchedulingSprintEntity getSprintSchedulingEntity(SchedulingSprintModel schedulingSprintModel) {
		SchedulingSprintEntity schedulingSprintEntity = null;
		try{
			if (schedulingSprintModel != null) {
				schedulingSprintEntity = new SchedulingSprintEntity();
				if (schedulingSprintModel.getId() != null && schedulingSprintModel.getId() != 0) {
					schedulingSprintEntity.setId(Integer.valueOf(schedulingSprintModel.getId()));
				}
				schedulingSprintEntity.setCreationDate(new Date());
				schedulingSprintEntity.setDay(schedulingSprintModel.getDay());
				schedulingSprintEntity.setWeek(schedulingSprintModel.getWeek());
				schedulingSprintEntity.setMonth(schedulingSprintModel.getMonth());
				schedulingSprintEntity.setQtr(schedulingSprintModel.getQtr());
				schedulingSprintEntity.setYear(schedulingSprintModel.getYear());
				schedulingSprintEntity.setType(schedulingSprintModel.getType());
				schedulingSprintEntity.setSiteRevisit(schedulingSprintModel.getSiteRevisit());
				schedulingSprintEntity.setGoldenCluster(schedulingSprintModel.getGoldenCluster());
				schedulingSprintEntity.setActualMigrationStartDate(DateUtil.stringToDate(schedulingSprintModel.getActualMigrationStartDate(), Constants.DD_MM_YYYY_));
				schedulingSprintEntity.setCompDate(DateUtil.stringToDate(schedulingSprintModel.getCompDate(),Constants.DD_MM_YYYY_));
				schedulingSprintEntity.setStartDate(DateUtil.stringToDate(schedulingSprintModel.getStartDate(),Constants.DD_MM_YYYY_));
				schedulingSprintEntity.setRegion(schedulingSprintModel.getRegion());
				schedulingSprintEntity.setMarket(schedulingSprintModel.getMarket());
				schedulingSprintEntity.setEnbId(schedulingSprintModel.getEnbId());
				schedulingSprintEntity.setCascade(schedulingSprintModel.getCascade());
				schedulingSprintEntity.setFiveG(schedulingSprintModel.getFiveG());
				schedulingSprintEntity.setTypeOne(schedulingSprintModel.getTypeOne());
				schedulingSprintEntity.setTvw(schedulingSprintModel.getTvw());
				schedulingSprintEntity.setCurrentSoftware(schedulingSprintModel.getCurrentSoftware());
				schedulingSprintEntity.setScriptsRan(schedulingSprintModel.getScriptsRan());
				schedulingSprintEntity.setDspImplemented(schedulingSprintModel.getDspImplemented());
				schedulingSprintEntity.setCiEngineerOne(schedulingSprintModel.getCiEngineerOne());
				schedulingSprintEntity.setCiStartTimeOne(schedulingSprintModel.getCiStartTimeOne());
				schedulingSprintEntity.setCiEndTimeOne(schedulingSprintModel.getCiEndTimeOne());
				schedulingSprintEntity.setFeRegion(schedulingSprintModel.getFeRegion());
				schedulingSprintEntity.setFeOne(schedulingSprintModel.getFeOne());
				schedulingSprintEntity.setFeContactInfoOne(schedulingSprintModel.getFeContactInfoOne());
				schedulingSprintEntity.setFeArrivalTimeOne(schedulingSprintModel.getFeArrivalTimeOne());
				schedulingSprintEntity.setCiEngineerTwo(schedulingSprintModel.getCiEngineerTwo());
				schedulingSprintEntity.setCiStartTimeTwo(schedulingSprintModel.getCiStartTimeTwo());
				schedulingSprintEntity.setCiEndTimeTwo(schedulingSprintModel.getCiEndTimeTwo());
				schedulingSprintEntity.setFeTwo(schedulingSprintModel.getFeTwo());
				schedulingSprintEntity.setFeContactInfoTwo(schedulingSprintModel.getFeContactInfoTwo());
				schedulingSprintEntity.setFeArrivalTimeTwo(schedulingSprintModel.getFeArrivalTimeTwo());
				schedulingSprintEntity.setGc(schedulingSprintModel.getGc());
				schedulingSprintEntity.setGcArrivalTime(schedulingSprintModel.getGcArrivalTime());
				schedulingSprintEntity.setPutTool(schedulingSprintModel.getPutTool());
				schedulingSprintEntity.setScriptErrors(schedulingSprintModel.getScriptErrors());
				schedulingSprintEntity.setReasonCode(schedulingSprintModel.getReasonCode());
				schedulingSprintEntity.setCiIssue(schedulingSprintModel.getCiIssue());
				schedulingSprintEntity.setNonCiIssue(schedulingSprintModel.getNonCiIssue());
				schedulingSprintEntity.setAlphaStartTime(schedulingSprintModel.getAlphaStartTime());
				schedulingSprintEntity.setAlphaEndTime(schedulingSprintModel.getAlphaEndTime());
				schedulingSprintEntity.setBetaStartTime(schedulingSprintModel.getBetaStartTime());
				schedulingSprintEntity.setBetaEndTime(schedulingSprintModel.getBetaEndTime());
				schedulingSprintEntity.setGammaStartTime(schedulingSprintModel.getGammaStartTime());
				schedulingSprintEntity.setGammaEndTime(schedulingSprintModel.getGammaEndTime());
				schedulingSprintEntity.setStatus(schedulingSprintModel.getStatus());
				schedulingSprintEntity.setEngineerOneNotes(schedulingSprintModel.getEngineerOneNotes());
				schedulingSprintEntity.setEngineerTwoNotes(schedulingSprintModel.getEngineerTwoNotes());
				schedulingSprintEntity.setCiEngineerNight(schedulingSprintModel.getCiEngineerNight());
				schedulingSprintEntity.setBridgeOne(schedulingSprintModel.getBridgeOne());
				schedulingSprintEntity.setFeNight(schedulingSprintModel.getFeNight());
				schedulingSprintEntity.setCiEngineerDay(schedulingSprintModel.getCiEngineerDay());
				schedulingSprintEntity.setBridge(schedulingSprintModel.getBridge());
				schedulingSprintEntity.setFeDay(schedulingSprintModel.getFeDay());
				schedulingSprintEntity.setNotes(schedulingSprintModel.getNotes());
		
			}
		}catch (Exception e) {
			logger.error("Excpetion getSprintSchedulingEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingSprintEntity;
	}

	public List<SchedulingSprintModel> getSprintSchedulingModel(
		List<SchedulingSprintEntity> objSchedulingSprintEntity) {
		List<SchedulingSprintModel> schedulingSprintModelList = null;
		try {
			if (objSchedulingSprintEntity != null) {
				schedulingSprintModelList = new ArrayList<SchedulingSprintModel>();
				SchedulingSprintModel schedulingSprintModel = null;

				for (SchedulingSprintEntity schedulingSprintEntity : objSchedulingSprintEntity) {
					schedulingSprintModel = getSchedulingSprintModelExcel(schedulingSprintEntity);
					schedulingSprintModelList.add(schedulingSprintModel);
				}
			}
		} catch (Exception e) {
			logger.error("Excpetion getSprintSchedulingModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingSprintModelList;
	}
	
	

	public List<SchedulingVerizonModel> getVerizonSchedulingModel(
			List<SchedulingVerizonEntity> objSchedulingVerizonEntity) {
		List<SchedulingVerizonModel> schedulingVerizonModelList = null;
		try {
			if (objSchedulingVerizonEntity != null) {
				schedulingVerizonModelList = new ArrayList<SchedulingVerizonModel>();
				SchedulingVerizonModel schedulingVerizonModel = null;

				for (SchedulingVerizonEntity schedulingVerizonEntity : objSchedulingVerizonEntity) {
					schedulingVerizonModel = getSchedulingVerizonModelExport(schedulingVerizonEntity);
					schedulingVerizonModelList.add(schedulingVerizonModel);
				}
			}																																																																																																																																		
		} catch (Exception e) {
			logger.error("Excpetion getSprintSchedulingModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingVerizonModelList;
	}
	
	
	public SchedulingSprintModel getSchedulingSprintModel(SchedulingSprintEntity schedulingSprintEntity) {
		SchedulingSprintModel schedulingSprintModel = null;
		try {
			if (schedulingSprintEntity != null) {
				schedulingSprintModel = new SchedulingSprintModel();
				if (schedulingSprintEntity.getId() != null && schedulingSprintEntity.getId() != 0) {
					schedulingSprintModel.setId(Integer.valueOf(schedulingSprintEntity.getId()));
				}
				schedulingSprintModel.setStartDate(CommonUtil.dateToString(schedulingSprintEntity.getStartDate(), Constants.YYYY_MM_DD));
				schedulingSprintModel.setCompDate(CommonUtil.dateToString(schedulingSprintEntity.getCompDate(), Constants.YYYY_MM_DD));
				schedulingSprintModel.setRegion(schedulingSprintEntity.getRegion());
				schedulingSprintModel.setMarket(schedulingSprintEntity.getMarket());
				schedulingSprintModel.setEnbId(schedulingSprintEntity.getEnbId());
				schedulingSprintModel.setCascade(schedulingSprintEntity.getCascade());
				schedulingSprintModel.setCurrentSoftware(schedulingSprintEntity.getCurrentSoftware());
				schedulingSprintModel.setScriptsRan(schedulingSprintEntity.getScriptErrors());
				schedulingSprintModel.setDspImplemented(schedulingSprintEntity.getDspImplemented());
				schedulingSprintModel.setCiEngineerOne(schedulingSprintEntity.getCiEngineerOne());
				schedulingSprintModel.setCiStartTimeOne(schedulingSprintEntity.getCiStartTimeOne());
				schedulingSprintModel.setCiEndTimeOne(schedulingSprintEntity.getCiEndTimeOne());
				schedulingSprintModel.setFeOne(schedulingSprintEntity.getFeOne());
				schedulingSprintModel.setFeContactInfoOne(schedulingSprintEntity.getFeContactInfoOne());
				schedulingSprintModel.setFeArrivalTimeOne(schedulingSprintEntity.getFeArrivalTimeOne());
				schedulingSprintModel.setCiEngineerTwo(schedulingSprintEntity.getCiEngineerTwo());
				schedulingSprintModel.setCiStartTimeTwo(schedulingSprintEntity.getCiStartTimeTwo());
				schedulingSprintModel.setCiEndTimeTwo(schedulingSprintEntity.getCiEndTimeTwo());
				schedulingSprintModel.setFeTwo(schedulingSprintEntity.getFeTwo());
				schedulingSprintModel.setFeContactInfoTwo(schedulingSprintEntity.getFeContactInfoTwo());
				schedulingSprintModel.setFeArrivalTimeTwo(schedulingSprintEntity.getFeArrivalTimeTwo());
				schedulingSprintModel.setGc(schedulingSprintEntity.getGc());
				schedulingSprintModel.setGcArrivalTime(schedulingSprintEntity.getGcArrivalTime());
				schedulingSprintModel.setPutTool(schedulingSprintEntity.getPutTool());
				schedulingSprintModel.setScriptErrors(schedulingSprintEntity.getScriptErrors());
				schedulingSprintModel.setCiIssue(schedulingSprintEntity.getCiIssue());
				schedulingSprintModel.setNonCiIssue(schedulingSprintEntity.getNonCiIssue());
				schedulingSprintModel.setCiEngineerNight(schedulingSprintEntity.getCiEngineerNight());
				schedulingSprintModel.setBridgeOne(schedulingSprintEntity.getBridgeOne());
				schedulingSprintModel.setFeRegion(schedulingSprintEntity.getFeRegion());
				schedulingSprintModel.setFeNight(schedulingSprintEntity.getFeNight());
				schedulingSprintModel.setCiEngineerDay(schedulingSprintEntity.getCiEngineerDay());
				schedulingSprintModel.setBridge(schedulingSprintEntity.getBridge());
				schedulingSprintModel.setFeDay(schedulingSprintEntity.getFeDay());
				schedulingSprintModel.setNotes(schedulingSprintEntity.getNotes());
				schedulingSprintModel.setStatus(schedulingSprintEntity.getStatus());
				schedulingSprintModel.setEngineerOneNotes(schedulingSprintEntity.getEngineerOneNotes());
				schedulingSprintModel.setEngineerTwoNotes(schedulingSprintEntity.getEngineerTwoNotes());
				schedulingSprintModel.setAlphaStartTime(schedulingSprintEntity.getAlphaStartTime());
				schedulingSprintModel.setAlphaEndTime(schedulingSprintEntity.getAlphaEndTime());
				schedulingSprintModel.setBetaStartTime(schedulingSprintEntity.getBetaStartTime());
				schedulingSprintModel.setBetaEndTime(schedulingSprintEntity.getBetaEndTime());
				schedulingSprintModel.setGammaStartTime(schedulingSprintEntity.getGammaStartTime());
				schedulingSprintModel.setGammaEndTime(schedulingSprintEntity.getGammaEndTime());
				schedulingSprintModel.setStatus(schedulingSprintEntity.getStatus());
				schedulingSprintModel.setEngineerOneNotes(schedulingSprintEntity.getEngineerOneNotes());
				schedulingSprintModel.setEngineerTwoNotes(schedulingSprintEntity.getEngineerTwoNotes());
				schedulingSprintModel.setBridge(schedulingSprintEntity.getBridge());
				schedulingSprintModel.setBridgeOne(schedulingSprintEntity.getBridgeOne());
				schedulingSprintModel.setNotes(schedulingSprintEntity.getNotes());
				schedulingSprintModel.setStatus(schedulingSprintEntity.getStatus());
				schedulingSprintModel.setType(schedulingSprintEntity.getType());
				schedulingSprintModel.setEodType(schedulingSprintEntity.getType());
				
				schedulingSprintModel.setCiEngineerThree(schedulingSprintEntity.getCiEngineerThree());
				schedulingSprintModel.setCiStartTimeThree(schedulingSprintEntity.getCiStartTimeThree());
				schedulingSprintModel.setCiEndTimeThree(schedulingSprintEntity.getCiEndTimeThree());
				schedulingSprintModel.setFeThree(schedulingSprintEntity.getFeThree());
				schedulingSprintModel.setFeContactInfoThree(schedulingSprintEntity.getFeContactInfoThree());
				schedulingSprintModel.setFeArrivalTimeThree(schedulingSprintEntity.getFeArrivalTimeThree());
				schedulingSprintModel.setScheduleDate(CommonUtil.dateToString(schedulingSprintEntity.getScheduleDate(), Constants.YYYY_MM_DD));
				schedulingSprintModel.setDtOrMw(schedulingSprintEntity.getDtOrMw());
				schedulingSprintModel.setTcName(schedulingSprintEntity.getTcName());
				schedulingSprintModel.setTcContactInfo(schedulingSprintEntity.getTcContactInfo());
				schedulingSprintModel.setResolution(schedulingSprintEntity.getResolution());
				schedulingSprintModel.setNvtfNoHarm(schedulingSprintEntity.getNvtfNoHarm());
				schedulingSprintModel.setEngineerThreeNotes(schedulingSprintEntity.getEngineerThreeNotes());
				
				schedulingSprintModel.setCircuitbreakerStart(schedulingSprintEntity.getCircuitbreakerStart());
				schedulingSprintModel.setCircuitbreakerEnd(schedulingSprintEntity.getCircuitbreakerEnd());
			}
		} catch (Exception e) {
			logger.error("Excpetion getSchedulingSprintModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingSprintModel;
	}
	
	
	
	public SchedulingSprintModel getSchedulingSprintModelExcel(SchedulingSprintEntity schedulingSprintEntity) {
		SchedulingSprintModel schedulingSprintModel = null;
		try {
			if (schedulingSprintEntity != null) {
				schedulingSprintModel = new SchedulingSprintModel();
				if (schedulingSprintEntity.getId() != null && schedulingSprintEntity.getId() != 0) {
					schedulingSprintModel.setId(Integer.valueOf(schedulingSprintEntity.getId()));
				}
				schedulingSprintModel.setStartDate(DateUtil.dateToString(schedulingSprintEntity.getStartDate(), Constants.DD_MM_YYYY_));
				schedulingSprintModel.setCompDate(DateUtil.dateToString(schedulingSprintEntity.getCompDate(), Constants.DD_MM_YYYY_));
				schedulingSprintModel.setRegion(schedulingSprintEntity.getRegion());
				schedulingSprintModel.setMarket(schedulingSprintEntity.getMarket());
				schedulingSprintModel.setEnbId(schedulingSprintEntity.getEnbId());
				schedulingSprintModel.setCascade(schedulingSprintEntity.getCascade());
				schedulingSprintModel.setCurrentSoftware(schedulingSprintEntity.getCurrentSoftware());
				schedulingSprintModel.setScriptsRan(schedulingSprintEntity.getScriptErrors());
				schedulingSprintModel.setDspImplemented(schedulingSprintEntity.getDspImplemented());
				schedulingSprintModel.setCiEngineerOne(schedulingSprintEntity.getCiEngineerOne());
				schedulingSprintModel.setCiStartTimeOne(schedulingSprintEntity.getCiStartTimeOne());
				schedulingSprintModel.setCiEndTimeOne(schedulingSprintEntity.getCiEndTimeOne());
				schedulingSprintModel.setFeOne(schedulingSprintEntity.getFeOne());
				schedulingSprintModel.setFeContactInfoOne(schedulingSprintEntity.getFeContactInfoOne());
				schedulingSprintModel.setFeArrivalTimeOne(schedulingSprintEntity.getFeArrivalTimeOne());
				schedulingSprintModel.setCiEngineerTwo(schedulingSprintEntity.getCiEngineerTwo());
				schedulingSprintModel.setCiStartTimeTwo(schedulingSprintEntity.getCiStartTimeTwo());
				schedulingSprintModel.setFeTwo(schedulingSprintEntity.getFeTwo());
				schedulingSprintModel.setFeContactInfoTwo(schedulingSprintEntity.getFeContactInfoTwo());
				schedulingSprintModel.setFeArrivalTimeTwo(schedulingSprintEntity.getFeArrivalTimeTwo());
				schedulingSprintModel.setGc(schedulingSprintEntity.getGc());
				schedulingSprintModel.setGcArrivalTime(schedulingSprintEntity.getGcArrivalTime());
				schedulingSprintModel.setPutTool(schedulingSprintEntity.getPutTool());
				schedulingSprintModel.setScriptErrors(schedulingSprintEntity.getScriptErrors());
				schedulingSprintModel.setCiIssue(schedulingSprintEntity.getCiIssue());
				schedulingSprintModel.setNonCiIssue(schedulingSprintEntity.getNonCiIssue());
				schedulingSprintModel.setCiEngineerNight(schedulingSprintEntity.getCiEngineerNight());
				schedulingSprintModel.setBridgeOne(schedulingSprintEntity.getBridgeOne());
				schedulingSprintModel.setFeRegion(schedulingSprintEntity.getFeRegion());
				schedulingSprintModel.setFeNight(schedulingSprintEntity.getFeNight());
				schedulingSprintModel.setCiEngineerDay(schedulingSprintEntity.getCiEngineerDay());
				schedulingSprintModel.setBridge(schedulingSprintEntity.getBridge());
				schedulingSprintModel.setFeDay(schedulingSprintEntity.getFeDay());
				schedulingSprintModel.setNotes(schedulingSprintEntity.getNotes());
				schedulingSprintModel.setStatus(schedulingSprintEntity.getStatus());
				schedulingSprintModel.setEngineerOneNotes(schedulingSprintEntity.getEngineerOneNotes());
				schedulingSprintModel.setEngineerTwoNotes(schedulingSprintEntity.getEngineerTwoNotes());
				
				schedulingSprintModel.setCiEndTimeTwo(schedulingSprintEntity.getCiEndTimeTwo());
				
				schedulingSprintModel.setCiEngineerThree(schedulingSprintEntity.getCiEngineerThree());
				schedulingSprintModel.setCiStartTimeThree(schedulingSprintEntity.getCiStartTimeThree());
				schedulingSprintModel.setCiEndTimeThree(schedulingSprintEntity.getCiEndTimeThree());
				schedulingSprintModel.setFeThree(schedulingSprintEntity.getFeThree());
				schedulingSprintModel.setFeContactInfoThree(schedulingSprintEntity.getFeContactInfoThree());
				schedulingSprintModel.setFeArrivalTimeThree(schedulingSprintEntity.getFeArrivalTimeThree());
				schedulingSprintModel.setScheduleDate(CommonUtil.dateToString(schedulingSprintEntity.getScheduleDate(), Constants.YYYY_MM_DD));
				schedulingSprintModel.setDtOrMw(schedulingSprintEntity.getDtOrMw());
				schedulingSprintModel.setTcName(schedulingSprintEntity.getTcName());
				schedulingSprintModel.setTcContactInfo(schedulingSprintEntity.getTcContactInfo());
				schedulingSprintModel.setResolution(schedulingSprintEntity.getResolution());
				schedulingSprintModel.setNvtfNoHarm(schedulingSprintEntity.getNvtfNoHarm());
				schedulingSprintModel.setEngineerThreeNotes(schedulingSprintEntity.getEngineerThreeNotes());

				schedulingSprintModel.setCircuitbreakerStart(schedulingSprintEntity.getCircuitbreakerStart());
				schedulingSprintModel.setCircuitbreakerEnd(schedulingSprintEntity.getCircuitbreakerEnd());
			}
		} catch (Exception e) {
			logger.error("Excpetion getSchedulingSprintModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingSprintModel;
	}
	
	public SchedulingVerizonModel getSchedulingVerizonModel(SchedulingVerizonEntity schedulingVerizonEntity) {
		SchedulingVerizonModel schedulingVerizonModel = null;
		try {
			if (schedulingVerizonEntity != null) {
				schedulingVerizonModel = new SchedulingVerizonModel();
				if (schedulingVerizonEntity.getId() != null && schedulingVerizonEntity.getId() != 0) {
					schedulingVerizonModel.setId(Integer.valueOf(schedulingVerizonEntity.getId()));
				}
				schedulingVerizonModel.setForecastStartDate(CommonUtil.dateToString(schedulingVerizonEntity.getForecastStartDate(), Constants.YYYY_MM_DD));;
				schedulingVerizonModel.setCompDate(DateUtil.dateToString(schedulingVerizonEntity.getCompDate(), Constants.YYYY_MM_DD));
				schedulingVerizonModel.setMarket(schedulingVerizonEntity.getMarket());
				schedulingVerizonModel.setEnbId(schedulingVerizonEntity.getEnbId());
				schedulingVerizonModel.setEnbName(schedulingVerizonEntity.getEnbName());
				schedulingVerizonModel.setGrowRequest(schedulingVerizonEntity.getGrowRequest());
				schedulingVerizonModel.setGrowCompleted(schedulingVerizonEntity.getGrowCompleted());
				schedulingVerizonModel.setCiqPresent(schedulingVerizonEntity.getCiqPresent());
				schedulingVerizonModel.setEnvCompleted(schedulingVerizonEntity.getEnvCompleted());
				schedulingVerizonModel.setStandardNonStandard(schedulingVerizonEntity.getStandardNonStandard());
				schedulingVerizonModel.setCarriers(schedulingVerizonEntity.getCarriers());
				schedulingVerizonModel.setUda(schedulingVerizonEntity.getUda());
				schedulingVerizonModel.setSoftwareLevels(schedulingVerizonEntity.getSoftwareLevels());
				schedulingVerizonModel.setFeArrivalTime(schedulingVerizonEntity.getFeArrivalTime());
				schedulingVerizonModel.setCiStartTime(schedulingVerizonEntity.getCiStartTime());
				schedulingVerizonModel.setRanEngineer(schedulingVerizonEntity.getRanEngineer());
				schedulingVerizonModel.setDtHandoff(schedulingVerizonEntity.getDtHandoff());
				schedulingVerizonModel.setCiEndTime(schedulingVerizonEntity.getCiEndTime());
				schedulingVerizonModel.setCanRollComp(schedulingVerizonEntity.getCanRollComp());
				schedulingVerizonModel.setTraffic(schedulingVerizonEntity.getTraffic());
				schedulingVerizonModel.setAlarmPresent(schedulingVerizonEntity.getAlarmPresent());
				schedulingVerizonModel.setCiEngineer(schedulingVerizonEntity.getCiEngineer());
				schedulingVerizonModel.setFt(schedulingVerizonEntity.getFt());
				schedulingVerizonModel.setDt(schedulingVerizonEntity.getDt());
				schedulingVerizonModel.setNotes(schedulingVerizonEntity.getNotes());
				schedulingVerizonModel.setTotalLookup(schedulingVerizonEntity.getTotalLookup());
				schedulingVerizonModel.setRanEngineer(schedulingVerizonEntity.getRanEngineer());
				schedulingVerizonModel.setCiEngineer(schedulingVerizonEntity.getCiEngineer());
				schedulingVerizonModel.setStatus(schedulingVerizonEntity.getStatus());
				schedulingVerizonModel.setRevisit(schedulingVerizonEntity.getRevisit());
				schedulingVerizonModel.setVlsm(schedulingVerizonEntity.getVlsm());
				schedulingVerizonModel.setComments(schedulingVerizonEntity.getComments());
				schedulingVerizonModel.setIssue(schedulingVerizonEntity.getIssue());
				schedulingVerizonModel.setCi(schedulingVerizonEntity.getCi());
				schedulingVerizonModel.setNonCi(schedulingVerizonEntity.getNonCi());
				schedulingVerizonModel.setAld(schedulingVerizonEntity.getAld());
				schedulingVerizonModel.setWeek(schedulingVerizonEntity.getWeek());
				schedulingVerizonModel.setMonth(schedulingVerizonEntity.getMonth());
				schedulingVerizonModel.setStatus2(schedulingVerizonEntity.getStatus2());
				schedulingVerizonModel.setQuarter(schedulingVerizonEntity.getQuarter());
				schedulingVerizonModel.setYear(schedulingVerizonEntity.getYear());
				schedulingVerizonModel.setEndTime(schedulingVerizonEntity.getEndTime());
				schedulingVerizonModel.setStartTime(schedulingVerizonEntity.getStartTime());
			}
		} catch (Exception e) {
			logger.error("Excpetion getSchedulingVerizonModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingVerizonModel;
	}

	
	public SchedulingVerizonModel getSchedulingVerizonModelExport(SchedulingVerizonEntity schedulingVerizonEntity) {
		SchedulingVerizonModel schedulingVerizonModel = null;
		try {
			if (schedulingVerizonEntity != null) {
				schedulingVerizonModel = new SchedulingVerizonModel();
				if (schedulingVerizonEntity.getId() != null && schedulingVerizonEntity.getId() != 0) {
					schedulingVerizonModel.setId(Integer.valueOf(schedulingVerizonEntity.getId()));
				}
				schedulingVerizonModel.setForecastStartDate(DateUtil.dateToString(schedulingVerizonEntity.getForecastStartDate(), Constants.DD_MM_YYYY_));
				schedulingVerizonModel.setCompDate(DateUtil.dateToString(schedulingVerizonEntity.getCompDate(), Constants.DD_MM_YYYY_));
				schedulingVerizonModel.setMarket(schedulingVerizonEntity.getMarket());
				schedulingVerizonModel.setEnbId(schedulingVerizonEntity.getEnbId());
				schedulingVerizonModel.setEnbName(schedulingVerizonEntity.getEnbName());
				schedulingVerizonModel.setGrowRequest(schedulingVerizonEntity.getGrowRequest());
				schedulingVerizonModel.setGrowCompleted(schedulingVerizonEntity.getGrowCompleted());
				schedulingVerizonModel.setCiqPresent(schedulingVerizonEntity.getCiqPresent());
				schedulingVerizonModel.setEnvCompleted(schedulingVerizonEntity.getEnvCompleted());
				schedulingVerizonModel.setStandardNonStandard(schedulingVerizonEntity.getStandardNonStandard());
				schedulingVerizonModel.setCarriers(schedulingVerizonEntity.getCarriers());
				schedulingVerizonModel.setUda(schedulingVerizonEntity.getUda());
				schedulingVerizonModel.setSoftwareLevels(schedulingVerizonEntity.getSoftwareLevels());
				schedulingVerizonModel.setFeArrivalTime(schedulingVerizonEntity.getFeArrivalTime());
				schedulingVerizonModel.setCiStartTime(schedulingVerizonEntity.getCiStartTime());
				schedulingVerizonModel.setRanEngineer(schedulingVerizonEntity.getRanEngineer());
				schedulingVerizonModel.setDtHandoff(schedulingVerizonEntity.getDtHandoff());
				schedulingVerizonModel.setCiEndTime(schedulingVerizonEntity.getCiEndTime());
				schedulingVerizonModel.setCanRollComp(schedulingVerizonEntity.getCanRollComp());
				schedulingVerizonModel.setTraffic(schedulingVerizonEntity.getTraffic());
				schedulingVerizonModel.setAlarmPresent(schedulingVerizonEntity.getAlarmPresent());
				schedulingVerizonModel.setCiEngineer(schedulingVerizonEntity.getCiEngineer());
				schedulingVerizonModel.setFt(schedulingVerizonEntity.getFt());
				schedulingVerizonModel.setDt(schedulingVerizonEntity.getDt());
				schedulingVerizonModel.setNotes(schedulingVerizonEntity.getNotes());
				schedulingVerizonModel.setTotalLookup(schedulingVerizonEntity.getTotalLookup());
				schedulingVerizonModel.setRanEngineer(schedulingVerizonEntity.getRanEngineer());
				schedulingVerizonModel.setCiEngineer(schedulingVerizonEntity.getCiEngineer());
				schedulingVerizonModel.setStatus(schedulingVerizonEntity.getStatus());
				schedulingVerizonModel.setRevisit(schedulingVerizonEntity.getRevisit());
				schedulingVerizonModel.setVlsm(schedulingVerizonEntity.getVlsm());
				schedulingVerizonModel.setComments(schedulingVerizonEntity.getComments());
				schedulingVerizonModel.setIssue(schedulingVerizonEntity.getIssue());
				schedulingVerizonModel.setCi(schedulingVerizonEntity.getCi());
				schedulingVerizonModel.setNonCi(schedulingVerizonEntity.getNonCi());
				schedulingVerizonModel.setAld(schedulingVerizonEntity.getAld());
				schedulingVerizonModel.setWeek(schedulingVerizonEntity.getWeek());
				schedulingVerizonModel.setMonth(schedulingVerizonEntity.getMonth());
				schedulingVerizonModel.setStatus2(schedulingVerizonEntity.getStatus2());
				schedulingVerizonModel.setQuarter(schedulingVerizonEntity.getQuarter());
				schedulingVerizonModel.setYear(schedulingVerizonEntity.getYear());
				schedulingVerizonModel.setEndTime(schedulingVerizonEntity.getEndTime());
				schedulingVerizonModel.setStartTime(schedulingVerizonEntity.getStartTime());
			}
		} catch (Exception e) {
			logger.error("Excpetion getSchedulingVerizonModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingVerizonModel;
	}
	public SchedulingVerizonEntity getVerizonOverallReportsEntity(SchedulingVerizonModel schedulingVerizonModel) {
		SchedulingVerizonEntity schedulingVerizonEntity = null;
		try{
			if (schedulingVerizonModel != null) {
				schedulingVerizonEntity = new SchedulingVerizonEntity();
				if (schedulingVerizonModel.getId() != null && schedulingVerizonModel.getId() != 0) {
					schedulingVerizonEntity.setId(Integer.valueOf(schedulingVerizonModel.getId()));
				}
				schedulingVerizonEntity.setCreationDate(new Date());
				schedulingVerizonEntity.setTotalLookup(schedulingVerizonModel.getTotalLookup());
				schedulingVerizonEntity.setForecastStartDate((DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_)));;
				schedulingVerizonEntity.setCompDate((DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_)));;
				schedulingVerizonEntity.setMarket(schedulingVerizonModel.getMarket());
				schedulingVerizonEntity.setEnbId(schedulingVerizonModel.getEnbId());
				schedulingVerizonEntity.setEnbName(schedulingVerizonModel.getEnbName());
				schedulingVerizonEntity.setRanEngineer(schedulingVerizonModel.getRanEngineer());
				schedulingVerizonEntity.setGrowRequest(schedulingVerizonModel.getGrowRequest());
				schedulingVerizonEntity.setGrowCompleted(schedulingVerizonModel.getGrowCompleted());
				schedulingVerizonEntity.setEnvCompleted(schedulingVerizonModel.getEnvCompleted());
				schedulingVerizonEntity.setStandardNonStandard(schedulingVerizonModel.getStandardNonStandard());
				schedulingVerizonEntity.setCarriers(schedulingVerizonModel.getCarriers());
				schedulingVerizonEntity.setUda(schedulingVerizonModel.getUda());
				schedulingVerizonEntity.setSoftwareLevels(schedulingVerizonModel.getSoftwareLevels());
				schedulingVerizonEntity.setFeArrivalTime(schedulingVerizonModel.getFeArrivalTime());
				schedulingVerizonEntity.setDtHandoff(schedulingVerizonModel.getDtHandoff());
				schedulingVerizonEntity.setCanRollComp(schedulingVerizonModel.getCanRollComp());
				schedulingVerizonEntity.setTraffic(schedulingVerizonModel.getTraffic());
				schedulingVerizonEntity.setAlarmPresent(schedulingVerizonModel.getAlarmPresent());
				schedulingVerizonEntity.setCiEngineer(schedulingVerizonModel.getCiEngineer());
				schedulingVerizonEntity.setFt(schedulingVerizonModel.getFt());
				schedulingVerizonEntity.setDt(schedulingVerizonModel.getDt());
				schedulingVerizonEntity.setCiqPresent(schedulingVerizonModel.getCiqPresent());
				schedulingVerizonEntity.setStatus(schedulingVerizonModel.getStatus());
				schedulingVerizonEntity.setRevisit(schedulingVerizonModel.getRevisit());
				schedulingVerizonEntity.setVlsm(schedulingVerizonModel.getVlsm());
				schedulingVerizonEntity.setComments(schedulingVerizonModel.getComments());
				schedulingVerizonEntity.setIssue(schedulingVerizonModel.getIssue());
				schedulingVerizonEntity.setCi(schedulingVerizonModel.getCi());
				schedulingVerizonEntity.setNonCi(schedulingVerizonModel.getNonCi());
				schedulingVerizonEntity.setCiStartTime(schedulingVerizonModel.getCiStartTime());
				schedulingVerizonEntity.setCiEndTime(schedulingVerizonModel.getCiEndTime());
				schedulingVerizonEntity.setStartTime(schedulingVerizonModel.getStartTime());
				schedulingVerizonEntity.setEndTime(schedulingVerizonModel.getEndTime());
				schedulingVerizonEntity.setAld(schedulingVerizonModel.getAld());
				schedulingVerizonEntity.setWeek(schedulingVerizonModel.getWeek());
				schedulingVerizonEntity.setMonth(schedulingVerizonModel.getMonth());
				schedulingVerizonEntity.setStatus2(schedulingVerizonModel.getStatus2());
				schedulingVerizonEntity.setQuarter(schedulingVerizonModel.getQuarter());
				schedulingVerizonEntity.setYear(schedulingVerizonModel.getYear());
				schedulingVerizonEntity.setRule1(schedulingVerizonModel.getRule1());
				schedulingVerizonEntity.setRule2(schedulingVerizonModel.getRule2());
				schedulingVerizonEntity.setDay(schedulingVerizonModel.getDay());
				schedulingVerizonEntity.setNotes(schedulingVerizonModel.getNotes());
			}
		}catch (Exception e) {
			logger.error("Excpetion getVerizonOverallReportsEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingVerizonEntity;
	}

	public SchedulingSprintEntity getSprintOverallReportsEntity(SchedulingSprintModel schedulingSprintModel) {
		SchedulingSprintEntity schedulingSprintEntity = null;
		try{
			if (schedulingSprintModel != null) {
				schedulingSprintEntity = new SchedulingSprintEntity();
				if (schedulingSprintModel.getId() != null && schedulingSprintModel.getId() != 0) {
					schedulingSprintEntity.setId(Integer.valueOf(schedulingSprintModel.getId()));
				}
				schedulingSprintEntity.setCreationDate(new Date());
				schedulingSprintEntity.setDay(schedulingSprintModel.getDay());
				schedulingSprintEntity.setWeek(schedulingSprintModel.getWeek());
				schedulingSprintEntity.setMonth(schedulingSprintModel.getMonth());
				schedulingSprintEntity.setQtr(schedulingSprintModel.getQtr());
				schedulingSprintEntity.setYear(schedulingSprintModel.getYear());
				schedulingSprintEntity.setType(schedulingSprintModel.getType());
				schedulingSprintEntity.setSiteRevisit(schedulingSprintModel.getSiteRevisit());
				schedulingSprintEntity.setGoldenCluster(schedulingSprintModel.getGoldenCluster());
				schedulingSprintEntity.setActualMigrationStartDate(DateUtil.stringToDate(schedulingSprintModel.getActualMigrationStartDate(), Constants.DD_MM_YYYY_));
				schedulingSprintEntity.setCompDate(DateUtil.stringToDate(schedulingSprintModel.getCompDate(),Constants.DD_MM_YYYY_));
				schedulingSprintEntity.setStartDate(DateUtil.stringToDate(schedulingSprintModel.getStartDate(),Constants.DD_MM_YYYY_));
				schedulingSprintEntity.setRegion(schedulingSprintModel.getRegion());
				schedulingSprintEntity.setMarket(schedulingSprintModel.getMarket());
				schedulingSprintEntity.setEnbId(schedulingSprintModel.getEnbId());
				schedulingSprintEntity.setCascade(schedulingSprintModel.getCascade());
				schedulingSprintEntity.setFiveG(schedulingSprintModel.getFiveG());
				schedulingSprintEntity.setTypeOne(schedulingSprintModel.getTypeOne());
				schedulingSprintEntity.setTvw(schedulingSprintModel.getTvw());
				schedulingSprintEntity.setCurrentSoftware(schedulingSprintModel.getCurrentSoftware());
				schedulingSprintEntity.setScriptsRan(schedulingSprintModel.getScriptsRan());
				schedulingSprintEntity.setDspImplemented(schedulingSprintModel.getDspImplemented());
				schedulingSprintEntity.setCiEngineerOne(schedulingSprintModel.getCiEngineerOne());
				schedulingSprintEntity.setCiStartTimeOne(schedulingSprintModel.getCiStartTimeOne());
				schedulingSprintEntity.setCiEndTimeOne(schedulingSprintModel.getCiEndTimeOne());
				schedulingSprintEntity.setFeRegion(schedulingSprintModel.getFeRegion());
				schedulingSprintEntity.setFeOne(schedulingSprintModel.getFeOne());
				schedulingSprintEntity.setFeContactInfoOne(schedulingSprintModel.getFeContactInfoOne());
				schedulingSprintEntity.setFeArrivalTimeOne(schedulingSprintModel.getFeArrivalTimeOne());
				schedulingSprintEntity.setCiEngineerTwo(schedulingSprintModel.getCiEngineerTwo());
				schedulingSprintEntity.setCiStartTimeTwo(schedulingSprintModel.getCiStartTimeTwo());
				schedulingSprintEntity.setCiEndTimeTwo(schedulingSprintModel.getCiEndTimeTwo());
				schedulingSprintEntity.setFeTwo(schedulingSprintModel.getFeTwo());
				schedulingSprintEntity.setFeContactInfoTwo(schedulingSprintModel.getFeContactInfoTwo());
				schedulingSprintEntity.setFeArrivalTimeTwo(schedulingSprintModel.getFeArrivalTimeTwo());
				schedulingSprintEntity.setGc(schedulingSprintModel.getGc());
				schedulingSprintEntity.setGcArrivalTime(schedulingSprintModel.getGcArrivalTime());
				schedulingSprintEntity.setPutTool(schedulingSprintModel.getPutTool());
				schedulingSprintEntity.setScriptErrors(schedulingSprintModel.getScriptErrors());
				schedulingSprintEntity.setReasonCode(schedulingSprintModel.getReasonCode());
				schedulingSprintEntity.setCiIssue(schedulingSprintModel.getCiIssue());
				schedulingSprintEntity.setNonCiIssue(schedulingSprintModel.getNonCiIssue());
				schedulingSprintEntity.setAlphaStartTime(schedulingSprintModel.getAlphaStartTime());
				schedulingSprintEntity.setAlphaEndTime(schedulingSprintModel.getAlphaEndTime());
				schedulingSprintEntity.setBetaStartTime(schedulingSprintModel.getBetaStartTime());
				schedulingSprintEntity.setBetaEndTime(schedulingSprintModel.getBetaEndTime());
				schedulingSprintEntity.setGammaStartTime(schedulingSprintModel.getGammaStartTime());
				schedulingSprintEntity.setGammaEndTime(schedulingSprintModel.getGammaEndTime());
				schedulingSprintEntity.setStatus(schedulingSprintModel.getStatus());
				schedulingSprintEntity.setEngineerOneNotes(schedulingSprintModel.getEngineerOneNotes());
				schedulingSprintEntity.setEngineerTwoNotes(schedulingSprintModel.getEngineerTwoNotes());
				schedulingSprintEntity.setBridge(schedulingSprintModel.getBridge());
				schedulingSprintEntity.setBridgeOne(schedulingSprintModel.getBridgeOne());
				schedulingSprintEntity.setNotes(schedulingSprintModel.getNotes());
				schedulingSprintEntity.setStatus(schedulingSprintModel.getStatus());
				
				schedulingSprintEntity.setCiEngineerThree(schedulingSprintModel.getCiEngineerThree());
				schedulingSprintEntity.setCiStartTimeThree(schedulingSprintModel.getCiStartTimeThree());
				schedulingSprintEntity.setCiEndTimeThree(schedulingSprintModel.getCiEndTimeThree());
				schedulingSprintEntity.setFeThree(schedulingSprintModel.getFeThree());
				schedulingSprintEntity.setFeContactInfoThree(schedulingSprintModel.getFeContactInfoThree());
				schedulingSprintEntity.setFeArrivalTimeThree(schedulingSprintModel.getFeArrivalTimeThree());
				schedulingSprintEntity.setScheduleDate(DateUtil.stringToDate(schedulingSprintModel.getScheduleDate(),Constants.DD_MM_YYYY_));
				schedulingSprintEntity.setDtOrMw(schedulingSprintModel.getDtOrMw());
				schedulingSprintEntity.setTcName(schedulingSprintModel.getTcName());
				schedulingSprintEntity.setTcContactInfo(schedulingSprintModel.getTcContactInfo());
				schedulingSprintEntity.setResolution(schedulingSprintModel.getResolution());
				schedulingSprintEntity.setNvtfNoHarm(schedulingSprintModel.getNvtfNoHarm());
				schedulingSprintEntity.setEngineerThreeNotes(schedulingSprintModel.getEngineerThreeNotes());
				
				schedulingSprintEntity.setCircuitbreakerStart(schedulingSprintModel.getCircuitbreakerStart());
				schedulingSprintEntity.setCircuitbreakerEnd(schedulingSprintModel.getCircuitbreakerEnd());
			}
		}catch (Exception e) {
			logger.error("Excpetion getSprintOverallReportsEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingSprintEntity;
	}

	public List<SchedulingVerizonModel> getVerizonOverallModel(
			List<SchedulingVerizonEntity> objSchedulingVerizonEntity) {
		List<SchedulingVerizonModel> schedulingVerizonModelList = null;
		try {
			if (objSchedulingVerizonEntity != null) {
				schedulingVerizonModelList = new ArrayList<SchedulingVerizonModel>();
				SchedulingVerizonModel schedulingVerizonModel = null;

				for (SchedulingVerizonEntity schedulingVerizonEntity : objSchedulingVerizonEntity) {
					schedulingVerizonModel = getOverallVerizonModelexport(schedulingVerizonEntity);
					schedulingVerizonModelList.add(schedulingVerizonModel);
				}
			}
		} catch (Exception e) {
			logger.error("Excpetion getSprintSchedulingModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingVerizonModelList;

}

	public SchedulingVerizonModel getOverallVerizonModel(SchedulingVerizonEntity schedulingVerizonEntity) {
		SchedulingVerizonModel schedulingVerizonModel = null;
		try {
			if (schedulingVerizonEntity != null) {
				schedulingVerizonModel = new SchedulingVerizonModel();
				if (schedulingVerizonEntity.getId() != null && schedulingVerizonEntity.getId() != 0) {
					schedulingVerizonModel.setId(Integer.valueOf(schedulingVerizonEntity.getId()));
				}
				schedulingVerizonModel.setForecastStartDate(CommonUtil.dateToString(schedulingVerizonEntity.getForecastStartDate(), Constants.YYYY_MM_DD));;
				schedulingVerizonModel.setCompDate(DateUtil.dateToString(schedulingVerizonEntity.getCompDate(), Constants.YYYY_MM_DD));
				schedulingVerizonModel.setMarket(schedulingVerizonEntity.getMarket());
				schedulingVerizonModel.setEnbId(schedulingVerizonEntity.getEnbId());
				schedulingVerizonModel.setEnbName(schedulingVerizonEntity.getEnbName());
				schedulingVerizonModel.setGrowRequest(schedulingVerizonEntity.getGrowRequest());
				schedulingVerizonModel.setGrowCompleted(schedulingVerizonEntity.getGrowCompleted());
				schedulingVerizonModel.setCiqPresent(schedulingVerizonEntity.getCiqPresent());
				schedulingVerizonModel.setEnvCompleted(schedulingVerizonEntity.getEnvCompleted());
				schedulingVerizonModel.setStandardNonStandard(schedulingVerizonEntity.getStandardNonStandard());
				schedulingVerizonModel.setCarriers(schedulingVerizonEntity.getCarriers());
				schedulingVerizonModel.setUda(schedulingVerizonEntity.getUda());
				schedulingVerizonModel.setSoftwareLevels(schedulingVerizonEntity.getSoftwareLevels());
				schedulingVerizonModel.setFeArrivalTime(schedulingVerizonEntity.getFeArrivalTime());
				schedulingVerizonModel.setCiStartTime(schedulingVerizonEntity.getCiStartTime());
				schedulingVerizonModel.setRanEngineer(schedulingVerizonEntity.getRanEngineer());
				schedulingVerizonModel.setDtHandoff(schedulingVerizonEntity.getDtHandoff());
				schedulingVerizonModel.setCiEndTime(schedulingVerizonEntity.getCiEndTime());
				schedulingVerizonModel.setCanRollComp(schedulingVerizonEntity.getCanRollComp());
				schedulingVerizonModel.setTraffic(schedulingVerizonEntity.getTraffic());
				schedulingVerizonModel.setAlarmPresent(schedulingVerizonEntity.getAlarmPresent());
				schedulingVerizonModel.setCiEngineer(schedulingVerizonEntity.getCiEngineer());
				schedulingVerizonModel.setFt(schedulingVerizonEntity.getFt());
				schedulingVerizonModel.setDt(schedulingVerizonEntity.getDt());
				schedulingVerizonModel.setNotes(schedulingVerizonEntity.getNotes());
				schedulingVerizonModel.setTotalLookup(schedulingVerizonEntity.getTotalLookup());
				schedulingVerizonModel.setRanEngineer(schedulingVerizonEntity.getRanEngineer());
				schedulingVerizonModel.setCiEngineer(schedulingVerizonEntity.getCiEngineer());
				schedulingVerizonModel.setStatus(schedulingVerizonEntity.getStatus());
				schedulingVerizonModel.setRevisit(schedulingVerizonEntity.getRevisit());
				schedulingVerizonModel.setVlsm(schedulingVerizonEntity.getVlsm());
				schedulingVerizonModel.setComments(schedulingVerizonEntity.getComments());
				schedulingVerizonModel.setIssue(schedulingVerizonEntity.getIssue());
				schedulingVerizonModel.setCi(schedulingVerizonEntity.getCi());
				schedulingVerizonModel.setNonCi(schedulingVerizonEntity.getNonCi());
				schedulingVerizonModel.setAld(schedulingVerizonEntity.getAld());
				schedulingVerizonModel.setRule1(schedulingVerizonEntity.getRule1());
				schedulingVerizonModel.setRule2(schedulingVerizonEntity.getRule2());
				schedulingVerizonModel.setWeek(schedulingVerizonEntity.getWeek());
				schedulingVerizonModel.setMonth(schedulingVerizonEntity.getMonth());
				schedulingVerizonModel.setStatus2(schedulingVerizonEntity.getStatus2());
				schedulingVerizonModel.setQuarter(schedulingVerizonEntity.getQuarter());
				schedulingVerizonModel.setYear(schedulingVerizonEntity.getYear());
				schedulingVerizonModel.setEndTime(schedulingVerizonEntity.getEndTime());
				schedulingVerizonModel.setStartTime(schedulingVerizonEntity.getStartTime());
				schedulingVerizonModel.setDay(schedulingVerizonEntity.getDay());
				
			}
		}catch (Exception e) {
			logger.error("Excpetion getSprintOverallReportsEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingVerizonModel;
	}
	
	
	public SchedulingVerizonModel getOverallVerizonModelexport(SchedulingVerizonEntity schedulingVerizonEntity) {
		SchedulingVerizonModel schedulingVerizonModel = null;
		try {
			if (schedulingVerizonEntity != null) {
				schedulingVerizonModel = new SchedulingVerizonModel();
				if (schedulingVerizonEntity.getId() != null && schedulingVerizonEntity.getId() != 0) {
					schedulingVerizonModel.setId(Integer.valueOf(schedulingVerizonEntity.getId()));
				}
				schedulingVerizonModel.setTotalLookup(schedulingVerizonEntity.getTotalLookup());
				schedulingVerizonModel.setForecastStartDate(DateUtil.dateToString(schedulingVerizonEntity.getForecastStartDate(), Constants.DD_MM_YYYY_));;
				schedulingVerizonModel.setMarket(schedulingVerizonEntity.getMarket());
				schedulingVerizonModel.setEnbId(schedulingVerizonEntity.getEnbId());
				schedulingVerizonModel.setEnbName(schedulingVerizonEntity.getEnbName());
				schedulingVerizonModel.setRanEngineer(schedulingVerizonEntity.getRanEngineer());
				schedulingVerizonModel.setGrowRequest(schedulingVerizonEntity.getGrowRequest());
				schedulingVerizonModel.setGrowCompleted(schedulingVerizonEntity.getGrowCompleted());
				schedulingVerizonModel.setCiqPresent(schedulingVerizonEntity.getCiqPresent());
				schedulingVerizonModel.setStatus(schedulingVerizonEntity.getStatus());
				schedulingVerizonModel.setRevisit(schedulingVerizonEntity.getRevisit());
				schedulingVerizonModel.setVlsm(schedulingVerizonEntity.getVlsm());
				schedulingVerizonModel.setComments(schedulingVerizonEntity.getComments());
				schedulingVerizonModel.setIssue(schedulingVerizonEntity.getIssue());
				schedulingVerizonModel.setCi(schedulingVerizonEntity.getCi());
				schedulingVerizonModel.setNonCi(schedulingVerizonEntity.getNonCi());
				schedulingVerizonModel.setAld(schedulingVerizonEntity.getAld());
				schedulingVerizonModel.setWeek(schedulingVerizonEntity.getWeek());
				schedulingVerizonModel.setMonth(schedulingVerizonEntity.getMonth());
				schedulingVerizonModel.setStatus2(schedulingVerizonEntity.getStatus2());
				schedulingVerizonModel.setQuarter(schedulingVerizonEntity.getQuarter());
				schedulingVerizonModel.setYear(schedulingVerizonEntity.getYear());
				schedulingVerizonModel.setEndTime(schedulingVerizonEntity.getEndTime());
				schedulingVerizonModel.setStartTime(schedulingVerizonEntity.getStartTime());
			}
		}catch (Exception e) {
			logger.error("Excpetion getSprintOverallReportsEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingVerizonModel;
	}

	public SchedulingVerizonEntity getVerizonEodEntity(SchedulingVerizonModel schedulingVerizonModel) {
		// TODO Auto-generated method stub
		SchedulingVerizonEntity schedulingVerizonEntity = null;
		try{
			if (schedulingVerizonModel != null) {
				schedulingVerizonEntity = new SchedulingVerizonEntity();
				if (schedulingVerizonModel.getId() != null && schedulingVerizonModel.getId() != 0) {
					schedulingVerizonEntity.setId(Integer.valueOf(schedulingVerizonModel.getId()));
				}
				schedulingVerizonEntity.setCreationDate(new Date());
				schedulingVerizonEntity.setForecastStartDate((DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_)));;
				schedulingVerizonEntity.setCompDate((DateUtil.stringToDate(schedulingVerizonModel.getCompDate(), Constants.DD_MM_YYYY_)));
				schedulingVerizonEntity.setMarket(schedulingVerizonModel.getMarket());
				schedulingVerizonEntity.setEnbId(schedulingVerizonModel.getEnbId());
				schedulingVerizonEntity.setEnbName(schedulingVerizonModel.getEnbName());
				schedulingVerizonEntity.setGrowRequest(schedulingVerizonModel.getGrowRequest());
				schedulingVerizonEntity.setGrowCompleted(schedulingVerizonModel.getGrowCompleted());
				schedulingVerizonEntity.setCiqPresent(schedulingVerizonModel.getCiqPresent());
				schedulingVerizonEntity.setEnvCompleted(schedulingVerizonModel.getEnvCompleted());
				schedulingVerizonEntity.setStandardNonStandard(schedulingVerizonModel.getStandardNonStandard());
				schedulingVerizonEntity.setCarriers(schedulingVerizonModel.getCarriers());
				schedulingVerizonEntity.setUda(schedulingVerizonModel.getUda());
				schedulingVerizonEntity.setSoftwareLevels(schedulingVerizonModel.getSoftwareLevels());
				schedulingVerizonEntity.setFeArrivalTime(schedulingVerizonModel.getFeArrivalTime());
				schedulingVerizonEntity.setCiStartTime(schedulingVerizonModel.getCiStartTime());
				schedulingVerizonEntity.setStartTime(schedulingVerizonModel.getStartTime());
				schedulingVerizonEntity.setDtHandoff(schedulingVerizonModel.getDtHandoff());
				schedulingVerizonEntity.setCiEndTime(schedulingVerizonModel.getCiEndTime());
				schedulingVerizonEntity.setCanRollComp(schedulingVerizonModel.getCanRollComp());
				schedulingVerizonEntity.setTraffic(schedulingVerizonModel.getTraffic());
				schedulingVerizonEntity.setAlarmPresent(schedulingVerizonModel.getAlarmPresent());
				schedulingVerizonEntity.setCiEngineer(schedulingVerizonModel.getCiEngineer());
				schedulingVerizonEntity.setFt(schedulingVerizonModel.getFt());
				schedulingVerizonEntity.setDt(schedulingVerizonModel.getDt());
				schedulingVerizonEntity.setNotes(schedulingVerizonModel.getNotes());
				schedulingVerizonEntity.setTotalLookup(schedulingVerizonModel.getTotalLookup());
				schedulingVerizonEntity.setColumn1(schedulingVerizonModel.getColumn1());
				schedulingVerizonEntity.setRanEngineer(schedulingVerizonModel.getRanEngineer());
				schedulingVerizonEntity.setStatus(schedulingVerizonModel.getStatus());
				schedulingVerizonEntity.setRevisit(schedulingVerizonModel.getRevisit());
				schedulingVerizonEntity.setVlsm(schedulingVerizonModel.getVlsm());
				schedulingVerizonEntity.setEndTime(schedulingVerizonModel.getEndTime());
				schedulingVerizonEntity.setComments(schedulingVerizonModel.getComments());
				schedulingVerizonEntity.setIssue(schedulingVerizonModel.getIssue());
				schedulingVerizonEntity.setCi(schedulingVerizonModel.getCi());
				schedulingVerizonEntity.setNonCi(schedulingVerizonModel.getNonCi());
				schedulingVerizonEntity.setAld(schedulingVerizonModel.getAld());
				schedulingVerizonEntity.setWeek(schedulingVerizonModel.getWeek());
				schedulingVerizonEntity.setMonth(schedulingVerizonModel.getMonth());
				schedulingVerizonEntity.setStatus2(schedulingVerizonModel.getStatus2());
				schedulingVerizonEntity.setQuarter(schedulingVerizonModel.getQuarter());
				schedulingVerizonEntity.setYear(schedulingVerizonModel.getYear());
				schedulingVerizonEntity.setRule1(schedulingVerizonModel.getRule1());
				schedulingVerizonEntity.setRule2(schedulingVerizonModel.getRule2());
				schedulingVerizonEntity.setDay(schedulingVerizonModel.getDay());
	}
	}catch (Exception e) {
			logger.error("Excpetion getVerizonEodEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
	}
		return schedulingVerizonEntity;
	}

	public SchedulingSprintEntity getSprintEodEntity(SchedulingSprintModel schedulingSprintModel) {
		SchedulingSprintEntity schedulingSprintEntity = null;
		try{
			if (schedulingSprintModel != null) {
				schedulingSprintEntity = new SchedulingSprintEntity();
				if (schedulingSprintModel.getId() != null && schedulingSprintModel.getId() != 0) {
					schedulingSprintEntity.setId(Integer.valueOf(schedulingSprintModel.getId()));
				}
		schedulingSprintEntity.setCreationDate(new Date());
		schedulingSprintEntity.setActualMigrationStartDate(DateUtil.stringToDate(schedulingSprintModel.getActualMigrationStartDate(), Constants.DD_MM_YYYY_));
		schedulingSprintEntity.setCompDate(DateUtil.stringToDate(schedulingSprintModel.getCompDate(), Constants.DD_MM_YYYY_));
		schedulingSprintEntity.setRegion(schedulingSprintModel.getRegion());
		schedulingSprintEntity.setMarket(schedulingSprintModel.getMarket());
		schedulingSprintEntity.setEnbId(schedulingSprintModel.getEnbId());
		schedulingSprintEntity.setCascade(schedulingSprintModel.getCascade());
		schedulingSprintEntity.setTypeOne(schedulingSprintModel.getTypeOne());
		schedulingSprintEntity.setCurrentSoftware(schedulingSprintModel.getCurrentSoftware());
		schedulingSprintEntity.setScriptsRan(schedulingSprintModel.getScriptsRan());
		schedulingSprintEntity.setDspImplemented(schedulingSprintModel.getDspImplemented());
		schedulingSprintEntity.setPutTool(schedulingSprintModel.getPutTool());
		schedulingSprintEntity.setScriptErrors(schedulingSprintModel.getScriptErrors());
		schedulingSprintEntity.setDay(schedulingSprintModel.getDay());
		schedulingSprintEntity.setWeek(schedulingSprintModel.getWeek());
		schedulingSprintEntity.setMonth(schedulingSprintModel.getMonth());
		schedulingSprintEntity.setQtr(schedulingSprintModel.getQtr());
		schedulingSprintEntity.setYear(schedulingSprintModel.getYear());
		schedulingSprintEntity.setType(schedulingSprintModel.getType());
		schedulingSprintEntity.setSiteRevisit(schedulingSprintModel.getSiteRevisit());
		schedulingSprintEntity.setGoldenCluster(schedulingSprintModel.getGoldenCluster());
		schedulingSprintEntity.setStartDate(DateUtil.stringToDate(schedulingSprintModel.getStartDate(),Constants.DD_MM_YYYY_));
		schedulingSprintEntity.setFiveG(schedulingSprintModel.getFiveG());
		schedulingSprintEntity.setTvw(schedulingSprintModel.getTvw());
		schedulingSprintEntity.setCiEngineerOne(schedulingSprintModel.getCiEngineerOne());
		schedulingSprintEntity.setCiStartTimeOne(schedulingSprintModel.getCiStartTimeOne());
		schedulingSprintEntity.setCiEndTimeOne(schedulingSprintModel.getCiEndTimeOne());
		schedulingSprintEntity.setFeRegion(schedulingSprintModel.getFeRegion());
		schedulingSprintEntity.setFeOne(schedulingSprintModel.getFeOne());
		schedulingSprintEntity.setFeContactInfoOne(schedulingSprintModel.getFeContactInfoOne());
		schedulingSprintEntity.setFeArrivalTimeOne(schedulingSprintModel.getFeArrivalTimeOne());
		schedulingSprintEntity.setCiEngineerTwo(schedulingSprintModel.getCiEngineerTwo());
		schedulingSprintEntity.setCiStartTimeTwo(schedulingSprintModel.getCiStartTimeTwo());
		schedulingSprintEntity.setCiEndTimeTwo(schedulingSprintModel.getCiEndTimeTwo());
		schedulingSprintEntity.setFeTwo(schedulingSprintModel.getFeTwo());
		schedulingSprintEntity.setFeContactInfoTwo(schedulingSprintModel.getFeContactInfoTwo());
		schedulingSprintEntity.setFeArrivalTimeTwo(schedulingSprintModel.getFeArrivalTimeTwo());
		schedulingSprintEntity.setGc(schedulingSprintModel.getGc());
		schedulingSprintEntity.setGcArrivalTime(schedulingSprintModel.getGcArrivalTime());
		schedulingSprintEntity.setReasonCode(schedulingSprintModel.getReasonCode());
		schedulingSprintEntity.setCiIssue(schedulingSprintModel.getCiIssue());
		schedulingSprintEntity.setNonCiIssue(schedulingSprintModel.getNonCiIssue());
		schedulingSprintEntity.setAlphaStartTime(schedulingSprintModel.getAlphaStartTime());
		schedulingSprintEntity.setAlphaEndTime(schedulingSprintModel.getAlphaEndTime());
		schedulingSprintEntity.setBetaStartTime(schedulingSprintModel.getBetaStartTime());
		schedulingSprintEntity.setBetaEndTime(schedulingSprintModel.getBetaEndTime());
		schedulingSprintEntity.setGammaStartTime(schedulingSprintModel.getGammaStartTime());
		schedulingSprintEntity.setGammaEndTime(schedulingSprintModel.getGammaEndTime());
		schedulingSprintEntity.setStatus(schedulingSprintModel.getStatus());
		schedulingSprintEntity.setEngineerOneNotes(schedulingSprintModel.getEngineerOneNotes());
		schedulingSprintEntity.setEngineerTwoNotes(schedulingSprintModel.getEngineerTwoNotes());
		
		schedulingSprintEntity.setCiEngineerThree(schedulingSprintModel.getCiEngineerThree());
		schedulingSprintEntity.setCiStartTimeThree(schedulingSprintModel.getCiStartTimeThree());
		schedulingSprintEntity.setCiEndTimeThree(schedulingSprintModel.getCiEndTimeThree());
		schedulingSprintEntity.setFeThree(schedulingSprintModel.getFeThree());
		schedulingSprintEntity.setFeContactInfoThree(schedulingSprintModel.getFeContactInfoThree());
		schedulingSprintEntity.setFeArrivalTimeThree(schedulingSprintModel.getFeArrivalTimeThree());
		schedulingSprintEntity.setScheduleDate(DateUtil.stringToDate(schedulingSprintModel.getScheduleDate(),Constants.DD_MM_YYYY_));
		schedulingSprintEntity.setDtOrMw(schedulingSprintModel.getDtOrMw());
		schedulingSprintEntity.setTcName(schedulingSprintModel.getTcName());
		schedulingSprintEntity.setTcContactInfo(schedulingSprintModel.getTcContactInfo());
		schedulingSprintEntity.setResolution(schedulingSprintModel.getResolution());
		schedulingSprintEntity.setNvtfNoHarm(schedulingSprintModel.getNvtfNoHarm());
		schedulingSprintEntity.setEngineerThreeNotes(schedulingSprintModel.getEngineerThreeNotes());
		
		schedulingSprintEntity.setCircuitbreakerStart(schedulingSprintModel.getCircuitbreakerStart());
		schedulingSprintEntity.setCircuitbreakerEnd(schedulingSprintModel.getCircuitbreakerEnd());
			}
		}catch (Exception e) {
			logger.error("Excpetion getSprintEodEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingSprintEntity;
	}

	public List<SchedulingVerizonModel> getVerizonEodModel(List<SchedulingVerizonEntity> objSchedulingVerizonEntity) {
			List<SchedulingVerizonModel> eodVerizonModelList = null;
			try {
				if (objSchedulingVerizonEntity != null) {
					eodVerizonModelList = new ArrayList<SchedulingVerizonModel>();
					SchedulingVerizonModel schedulingVerizonModel = null;

					for (SchedulingVerizonEntity schedulingVerizonEntity : objSchedulingVerizonEntity) {
						schedulingVerizonModel = getEodVerizonModel(schedulingVerizonEntity);
						eodVerizonModelList.add(schedulingVerizonModel);
					}
				}																																																																																																																																		
			} catch (Exception e) {
				logger.error("Excpetion getSprintSchedulingModel : " + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			}
			return eodVerizonModelList;
	}

	private SchedulingVerizonModel getEodVerizonModel(SchedulingVerizonEntity schedulingVerizonEntity) {
		SchedulingVerizonModel schedulingVerizonModel = null;
		try {
			if (schedulingVerizonEntity != null) {
				schedulingVerizonModel = new SchedulingVerizonModel();
				if (schedulingVerizonEntity.getId() != null && schedulingVerizonEntity.getId() != 0) {
					schedulingVerizonModel.setId(Integer.valueOf(schedulingVerizonEntity.getId()));
				}
				schedulingVerizonModel.setTotalLookup(schedulingVerizonEntity.getTotalLookup());
				schedulingVerizonModel.setForecastStartDate(CommonUtil.dateToString(schedulingVerizonEntity.getForecastStartDate(), Constants.YYYY_MM_DD));;
				schedulingVerizonModel.setMarket(schedulingVerizonEntity.getMarket());
				schedulingVerizonModel.setEnbId(schedulingVerizonEntity.getEnbId());
				schedulingVerizonModel.setEnbName(schedulingVerizonEntity.getEnbName());
				schedulingVerizonModel.setRanEngineer(schedulingVerizonEntity.getRanEngineer());
				schedulingVerizonModel.setGrowRequest(schedulingVerizonEntity.getGrowRequest());
				schedulingVerizonModel.setGrowCompleted(schedulingVerizonEntity.getGrowCompleted());
				schedulingVerizonModel.setCiqPresent(schedulingVerizonEntity.getCiqPresent());
				schedulingVerizonModel.setRevisit(schedulingVerizonEntity.getRevisit());
				schedulingVerizonModel.setVlsm(schedulingVerizonEntity.getVlsm());
				schedulingVerizonModel.setComments(schedulingVerizonEntity.getComments());
				schedulingVerizonModel.setIssue(schedulingVerizonEntity.getIssue());
				schedulingVerizonModel.setCi(schedulingVerizonEntity.getCi());
				schedulingVerizonModel.setNonCi(schedulingVerizonEntity.getNonCi());
				schedulingVerizonModel.setCiEndTime(schedulingVerizonEntity.getCiEndTime());
				schedulingVerizonModel.setCiStartTime(schedulingVerizonEntity.getCiStartTime());
				schedulingVerizonModel.setNotes(schedulingVerizonEntity.getNotes());
			}
		} catch (Exception e) {
			logger.error("Excpetion getSchedulingVerizonModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingVerizonModel;
				
	}

	public List<SchedulingSprintModel> getSprintEodModel(List<SchedulingSprintEntity> objSchedulingSprintEntity) {
			List<SchedulingSprintModel> schedulingSprintModelList = null;
			try {
				if (objSchedulingSprintEntity != null) {
					schedulingSprintModelList = new ArrayList<SchedulingSprintModel>();
					SchedulingSprintModel schedulingSprintModel = null;

					for (SchedulingSprintEntity schedulingSprintEntity : objSchedulingSprintEntity) {
						schedulingSprintModel = getEodSprintModel(schedulingSprintEntity);
						schedulingSprintModelList.add(schedulingSprintModel);
					}
				}
			} catch (Exception e) {
				logger.error("Excpetion getSprintSchedulingModel : " + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			}
			return schedulingSprintModelList;
	}

	private SchedulingSprintModel getEodSprintModel(SchedulingSprintEntity schedulingSprintEntity) {
		SchedulingSprintModel schedulingSprintModel = null;
		try {
			if (schedulingSprintEntity != null) {
				schedulingSprintModel = new SchedulingSprintModel();
				if (schedulingSprintEntity.getId() != null && schedulingSprintEntity.getId() != 0) {
					schedulingSprintModel.setId(Integer.valueOf(schedulingSprintEntity.getId()));
				}
				schedulingSprintModel.setActualMigrationStartDate(CommonUtil.dateToString(schedulingSprintEntity.getActualMigrationStartDate(), Constants.YYYY_MM_DD));
				schedulingSprintModel.setCompDate(CommonUtil.dateToString(schedulingSprintEntity.getCompDate(), Constants.YYYY_MM_DD));
				schedulingSprintModel.setRegion(schedulingSprintEntity.getRegion());
				schedulingSprintModel.setMarket(schedulingSprintEntity.getMarket());
				schedulingSprintModel.setEnbId(schedulingSprintEntity.getEnbId());
				schedulingSprintModel.setCascade(schedulingSprintEntity.getCascade());
				schedulingSprintModel.setTypeOne(schedulingSprintEntity.getTypeOne());
				schedulingSprintModel.setCurrentSoftware(schedulingSprintEntity.getCurrentSoftware());
				schedulingSprintModel.setScriptsRan(schedulingSprintEntity.getScriptsRan());
				schedulingSprintModel.setDspImplemented(schedulingSprintEntity.getDspImplemented());
				schedulingSprintModel.setPutTool(schedulingSprintEntity.getPutTool());
				schedulingSprintModel.setScriptErrors(schedulingSprintEntity.getScriptErrors());
			}
		} catch (Exception e) {
			logger.error("Excpetion getSchedulingVerizonModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return schedulingSprintModel;
	}
}
