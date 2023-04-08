package com.smart.rct.common.serviceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.AutoFecthTriggerEntity;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.OvAutomationModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.repository.OvScheduledTaskRepository;
import com.smart.rct.common.repositoryImpl.OvScheduledTaskRepositoryImpl;
import com.smart.rct.configuration.DailyOvScheduleConfig;
import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.util.DateUtil;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.OvGeneralEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.common.models.OvAutomationModel;
import com.smart.rct.common.models.OvInteractionModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.FetchScheduledService;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.constants.Constants;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.repository.UserDetailsRepository;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.migration.controller.RunTestController;
import com.smart.rct.migration.controller.WorkFlowManagementController;
import com.smart.rct.migration.service.WorkFlowManagementService;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.usermanagement.controller.LoginActionController;


public class FetchScheduledServiceImpl {
	
final static Logger logger = LoggerFactory.getLogger(FetchScheduledServiceImpl.class);

@Autowired
FetchProcessRepository fetchProcessRepository;

@Autowired
FetchProcessService fetchProcessService;

@Autowired
OvScheduledTaskService ovScheduledTaskService;

@Autowired
DailyOvScheduleConfig dailyOvScheduleConfig;

@Autowired
CustomerService customerService;

@Autowired
OvScheduledTaskRepository ovScheduledTaskRepository;

@Autowired
WorkFlowManagementController workFlowManagementController;


@EventListener(ApplicationReadyEvent.class)
public void scheduling4GUsmLive() {
	AtomicBoolean statusFetch = new AtomicBoolean();
	String ovAutomation = null;
	String ovOverallInteraction = null;
	List<String> scheduleCase = new ArrayList<>();
	List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
	configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
	for (ProgramTemplateModel template : configDetailModelList) {
		if (template.getLabel().equals("OV AUTOMATION"))
			ovAutomation = template.getValue();
		if (template.getLabel().equals("OV OVERALL INTERACTION"))
			ovOverallInteraction = template.getValue();
	}
	if (ovAutomation != null && ovAutomation.equals("OFF") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
		CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.USM_LIVE_4G);
		ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
		programTemplateModel.setProgramDetailsEntity(programmeEntity);
		programTemplateModel.setConfigType("s&r"); 
		programTemplateModel.setLabel(Constants.NE_GROW_SCHEDULE);
		 
		OvScheduledEntity ovDetailsEntity = ovScheduledTaskRepository.get4gOvDetails(Constants.USM_LIVE_4G);
		String fetchDate = ovDetailsEntity.getFetchDate();
		
		
		
		ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
				.getFetchTimeProgaramTemplate(programTemplateModel);
		if (!ObjectUtils.isEmpty(programTemplateEntity)
				&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {
			//String schedule = programTemplateEntity.getValue();
			String scheduledDateTime = programTemplateEntity.getValue();
			
			List<String> scheduleDateTimeFormat = Arrays.stream(scheduledDateTime.split(",|\\|")).collect(Collectors.toList());
			String time = scheduleDateTimeFormat.get(scheduleDateTimeFormat.size()-1);
			ovDetailsEntity.setNeGrowScheduledTime(time);
		
			scheduleDateTimeFormat.remove(scheduleDateTimeFormat.size()-1);
			List<String> dateTimeValues = scheduleDateTimeFormat.stream().map(date -> (date+ " " + time)).collect(Collectors.toList());
			for(String dateTime :dateTimeValues) {
			DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm");
			LocalDateTime start = LocalDateTime.parse(fetchDate ,formater);
			LocalDateTime stop = LocalDateTime.parse(dateTime ,formater);
			boolean isBefore = start.isBefore(stop);
			

			if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {
				
				if(isBefore==true)
				{
					JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto NE_Scheduling",
								"Uploaded through Fetch Automation Functionality", Constants.USM_LIVE_4G,"");
						if (statusOfOv.containsKey("statusCode")
								&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
							dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
						}
					
					
				}
				
				
				
			}}

		}		
	
	}}
}
