package com.smart.rct.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.smart.rct.common.controller.GeneralConfigController;
import com.smart.rct.premigration.controller.GenerateCsvController;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.service.GenerateCsvService;
import com.smart.rct.premigration.serviceImpl.GenerateCsvServiceImpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.json.simple.JSONObject;
/*
@Component
public class TaskSchedulerConfig {
	
	
	TaskScheduler taskSchedular = new ConcurrentTaskScheduler();
	
	@Autowired
	GeneralConfigController configController;
	
	@Autowired	
	GenerateCsvController generateCsvController;
	
	@Autowired
	GenerateCsvService objGenerateCsvService;
	
	@Autowired
	GenerateCsvServiceImpl generateCsvServiceImpl;
	
	
    public void setCrontabTiming(String startTime) {
    	    	
    	DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime dateTime = LocalDateTime.parse(startTime ,formater);
    	
    	//CronTrigger cronTrigger = new CronTrigger("0" + timeArray[0] +"" + timeArray[1] + "0"+ dateArray[1]+""+dateArray[2]);
    	Runnable runnable = () -> {
    		this.triggerNegrow();
    	};   	
    	this.taskSchedular.schedule(runnable,dateTime.toInstant(OffsetDateTime.now().getOffset()) );
    	
    }

    public void triggerNegrow() {
    	JSONObject ciqDetails =null;
    	ciqDetails = generateCsvController.generateFile(ciqDetails);
    	String programName = ciqDetails.get("programName").toString();
    	
    	if(ciqDetails.get("programName").toString().equals("VZN-5G-DSS") ) {
    		
    		generateCsvServiceImpl.csvFileGeneration.
    	}
    		
    	
    }
        
    

}*/